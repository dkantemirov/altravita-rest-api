package ru.restserver.services.report

import akka.actor.{ ActorRef, ActorSystem }
import akka.util.Timeout
import akka.event.LoggingAdapter
import net.sf.jasperreports.engine._
import ru.restserver.api.{ report => api }
import ru.restserver.api.report._
import ru.restserver.api.report.compile.{ CompiledTemplate, TemplateCompiler }
import ru.restserver.api.DatabaseService
import ru.restserver.database.DatabaseConnection

import scala.collection.mutable
import scala.concurrent.Future

class ReportService(val databaseService: DatabaseService, val compilerActor: ActorRef, val filler: TemplateFiller = new TemplateFiller)(implicit system: ActorSystem, timeout: Timeout, log: LoggingAdapter) extends api.ReportService {
  import system.dispatcher
  override def generate(report: Report): Future[GeneratedReport] = {
    val compile: Future[CompiledTemplate] = TemplateCompiler(compilerActor, report.template)
    val createParameters: Future[Map[String, String]] = Future {
      val format = report.format.getOrElse(Format.Default)
      val parameters = mutable.Map[String, String]()
      for (p <- report.parameters) parameters += p.name -> p.value
      val d = (format, parameters.toMap)
      d._2
    }
    val retrieveConnection: Future[DatabaseConnection] = databaseService.retrieveConnection
    def fill(template: CompiledTemplate, parameters: Map[String, String], databaseService: DatabaseService, connection: DatabaseConnection): Future[JasperPrint] = Future {
      try filler(template, parameters, connection) finally databaseService.closeConnection(connection)
    }
    def export(format: Format, jp: JasperPrint): Future[Array[Byte]] = Future {
      format match {
        case _ => JasperExportManager exportReportToPdf jp
      }
    }
    def generate(format: Format, output: Array[Byte]): Future[GeneratedReport] = Future {
      val generated = GeneratedReport(format, output)
      log.info(s"Report [template[id:${report.template.id}, fileName:${report.template.fileName}]] generated.")
      generated
    }
    for {
      compiled <- compile
      parameters <- createParameters
      connection <- retrieveConnection
      filled <- fill(compiled, parameters, databaseService, connection)
      exported <- export(Format.Default, filled)
      generated <- generate(Format.Default, exported)
    } yield generated
  }

  override def info(template: Template): Future[TemplateInfo] = TemplateCompiler(compilerActor, template) map { compiled =>
    TemplateInfo(template, compiled.parametersInfo)
  }

  override def allInfo: Future[Set[TemplateInfo]] = Future.sequence(Template.all.map(info))

  override def list: Future[Map[Int, String]] = Future(Template.all.map(template => template.id -> template.name).toMap)
}