package ru.restserver.http.routes.api.report

import akka.event.LoggingAdapter
import ru.restserver.api.auth.AuthService
import ru.restserver.api.report.Template
import ru.restserver.http.SecurityDirectives
import ru.restserver.http.routes.api.report.json.{ JsonProtocol, JsonReport }
import ru.restserver.services.report.ReportService
import spray.routing.{ Directives, Route }

import scala.concurrent.ExecutionContext

class ReportServiceRoute(val authService: AuthService, reportService: ReportService)(implicit val ec: ExecutionContext, log: LoggingAdapter) extends Directives with SecurityDirectives {
  val route: Route = pathPrefix("report") {
    post {
      auth { implicit session =>
        import JsonProtocol._
        entity(as[JsonReport]) { jsonReport =>
          complete {
            reportService.generate(jsonReport.report) map toJsonGeneratedReport
          }
        }
      }
    } ~
      (pathPrefix("info" / IntNumber) & pathEndOrSingleSlash) { templateId =>
        get {
          auth { implicit session =>
            complete {
              import JsonProtocol._
              reportService.info(Template(templateId)) map toJsonTemplateInfo
            }
          }
        }
      } ~
      (pathPrefix("all-info") & pathEndOrSingleSlash) {
        get {
          auth { implicit session =>
            complete {
              import JsonProtocol._
              reportService.allInfo.map(_.map(toJsonTemplateInfo))
            }
          }
        }
      } ~
      (pathPrefix("list") & pathEndOrSingleSlash) {
        get {
          auth { implicit session =>
            complete {
              import JsonProtocol._
              reportService.list.map { list =>
                list.toSet.map { p: (Int, String) =>
                  toJsonTemplateInfo(p._1, p._2)
                }
              }
            }
          }
        }
      }
  }
}