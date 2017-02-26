package ru.restserver.utils

import akka.actor.{ActorRef, ActorSystem}
import akka.event.LoggingAdapter
import akka.util.Timeout
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import ru.restserver.api.auth.Session
import ru.restserver.api.report.{ParameterInfo, TemplateFiller}
import ru.restserver.services.DatabaseService
import ru.restserver.services.auth.AuthService
import spray.testkit.ScalatestRouteTest

abstract class ServiceRouteSpec extends WordSpec with Matchers with MockFactory with ScalatestRouteTest {
  implicit val log: LoggingAdapter = Boot.log
  implicit val timeout: Timeout = Boot.timeout
  lazy val authService: AuthService = Boot.authService
  lazy val databaseService: DatabaseService = Boot.databaseService
  lazy val templateCompiler: ActorRef = Boot.templateCompiler
  lazy val filler: TemplateFiller = Boot.filler
  lazy val parameterInfo: ParameterInfo = Boot.parameterInfo
  lazy val filteredParameterInfo: ParameterInfo = Boot.filteredParameterInfo
  lazy val templateId: Int = Boot.templateId
  implicit lazy val session: Session = Boot.session

  override def createActorSystem(): ActorSystem = Boot.system
  override def cleanUp(): Unit = {
    log.info(s"Test Suite [$getClass] complited.")
  }

  def produce: AfterWord = afterWord("produce")
}