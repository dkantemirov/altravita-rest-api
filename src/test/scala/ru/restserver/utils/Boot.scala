package ru.restserver.utils

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.util.Timeout
import ru.restserver.api.auth.{DriverManager, Session}
import ru.restserver.api.report.Template
import ru.restserver.services.DatabaseService
import ru.restserver.services.auth.AuthService
import ru.restserver.services.report.ReportService

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

object Boot extends StubbedJasperReportContextLike {
  implicit val system: ActorSystem = {
    val system = ActorSystem("rest")
    system.log.info("Actor system started.")
    system
  }
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val log: LoggingAdapter = system.log
  implicit val timeout: Timeout = Timeout(5 seconds)
  lazy val driverManager: DriverManager = stub[DriverManager]
  lazy val authService: AuthService = {
    driverManager.verifyCredentials _ when (*, *, *) returns true
    new AuthService(driverManager)
  }
  lazy val databaseService = new DatabaseService
  lazy val reportService = new ReportService(databaseService, templateCompiler, filler)
  lazy val templateId: Int = Template.PatientsByNamePattern.id
  lazy val template = Template(templateId)
  implicit lazy val session: Session = Await.result(authService.signIn("login", "password"), 1 second)
}