package ru.restserver.http.routes.api.report

import ru.restserver.api.Configuration
import ru.restserver.api.auth.Session
import ru.restserver.api.report._
import ru.restserver.http.DefaultExceptionHandler
import ru.restserver.http.routes.api.report.json._
import ru.restserver.services.report.ReportService
import ru.restserver.utils.ServiceRouteSpec
import spray.http._
import spray.routing.Route

class ReportServiceRouteSpec extends ServiceRouteSpec {
  import StatusCodes._

  class Context extends DefaultExceptionHandler {
    val reportService = new ReportService(databaseService, templateCompiler, filler)
    val reportServiceRouter = new ReportServiceRoute(authService, reportService)
    val route: Route = handleExceptions(exceptionHandler) {
      reportServiceRouter.route
    }
  }

  "A report service route" when {
    "the template exists" should produce {
      "generated report" in new Context {
        generate(JsonReport(templateId, Option(Format.Pdf), Set(Parameter("par", "val"))), route) {
          import JsonProtocol._
          response.status shouldBe OK
          responseAs[JsonGeneratedReport].format shouldBe Format("pdf")
          responseAs[JsonGeneratedReport].data should not be empty
        }
      }
      "template information" in new Context {
        info(templateId, route) {
          import JsonProtocol._
          response.status shouldBe OK
          val info = responseAs[JsonTemplateInfo]
          val parametersInfo = info.parametersInfo.get
          info.template shouldBe templateId
          parametersInfo should contain(parameterInfo)
          parametersInfo should not contain filteredParameterInfo
        }
      }
    }
    "the template does not exist" should produce {
      "UnknownTemplateException with InternalServerError status" in new Context {
        generate(JsonReport(0, Option(Format.Pdf), Set(Parameter("par", "val"))), route) {
          import JsonProtocol._
          response.status shouldBe InternalServerError
          responseAs[UnknownTemplateException].message should not be empty
        }
      }
    }
    "unknown report format" should produce {
      "generated report with default format" in new Context {
        generate(JsonReport(templateId, Option(Format("Unknown")), Set.empty), route) {
          import JsonProtocol._
          response.status shouldBe OK
          responseAs[JsonGeneratedReport].format shouldBe Format.Default
          responseAs[JsonGeneratedReport].data should not be empty
        }
      }
    }
    "templates exist" should produce {
      "all templates information" in new Context {
        allInfo(route) {
          import JsonProtocol._
          response.status shouldBe OK
          val allInfo = responseAs[Set[JsonTemplateInfo]]
          allInfo should not be empty
        }
      }
      "list of available templates" in new Context {
        list(route) {
          import JsonProtocol._
          response.status shouldBe OK
          val list = responseAs[Set[JsonTemplateInfo]]
          list should not be empty
          list foreach { info =>
            info.fileName shouldBe empty
            info.parametersInfo shouldBe empty
          }
        }
      }
    }
  }

  private def generate(report: JsonReport, route: Route)(action: => Unit)(implicit session: Session) = {
    def serialized = {
      import JsonProtocol._
      serialize(report)
    }
    val requestEntity = HttpEntity(MediaTypes.`application/json`, serialized)
    Post("/report/", requestEntity) ~> addHeader(Configuration.auth.tokenAuthenticator.header, session.token) ~> route ~> check(action)
  }

  private def info(templateId: Int, route: Route)(action: => Unit)(implicit session: Session) = {
    Get(s"/report/info/$templateId") ~> addHeader(Configuration.auth.tokenAuthenticator.header, session.token) ~> route ~> check(action)
  }

  private def allInfo(route: Route)(action: => Unit)(implicit session: Session) = {
    Get("/report/all-info/") ~> addHeader(Configuration.auth.tokenAuthenticator.header, session.token) ~> route ~> check(action)
  }

  private def list(route: Route)(action: => Unit)(implicit session: Session) = {
    Get("/report/list/") ~> addHeader(Configuration.auth.tokenAuthenticator.header, session.token) ~> route ~> check(action)
  }
}