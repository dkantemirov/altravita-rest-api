package ru.restserver

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.event.{ Logging, LoggingAdapter }
import akka.util.Timeout
import ru.restserver.api.report.compile.TemplateCompilerActor
import ru.restserver.http.ServiceActor
import ru.restserver.http.routes.app.AppRoute
import ru.restserver.http.routes.api.auth.AuthServiceRoute
import ru.restserver.http.routes.api.report.ReportServiceRoute
import ru.restserver.services.DatabaseService
import ru.restserver.services.auth.AuthService
import ru.restserver.services.report.ReportService
import spray.routing._
import spray.servlet.WebBoot

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

class Boot extends WebBoot with Directives {
  implicit val system: ActorSystem = ActorSystem("rest")
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val log: LoggingAdapter = Logging(system, classOf[ServiceActor])
  implicit val timeout = Timeout(5 seconds)
  val templateCompiler: ActorRef = system.actorOf(TemplateCompilerActor.props(), TemplateCompilerActor.name)
  val databaseService = new DatabaseService
  val authService = new AuthService
  val authServiceRouter = new AuthServiceRoute(authService)
  val reportService = new ReportService(databaseService, templateCompiler)
  val reportServiceRouter = new ReportServiceRoute(authService, reportService)
  val appRouter = new AppRoute
  val apiRoute: Route = {
    authServiceRouter.route ~
      reportServiceRouter.route
  }
  val appRoute: Route = appRouter.route
  implicit val serviceActor: ActorRef = system.actorOf(Props(new ServiceActor(apiRoute, appRoute)), "service")
  implicit val sender: ActorRef = serviceActor

  system.registerOnTermination {
    // put additional cleanup code here
    system.log.info("Application shut down.")
  }
}