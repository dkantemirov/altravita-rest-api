package ru.restserver.utils

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.event.LoggingAdapter
import akka.util.Timeout
import net.sf.jasperreports.engine._
import org.scalamock.scalatest.MockFactory
import ru.restserver.api.report.compile.{CompiledTemplate, TemplateCompilerActor}
import ru.restserver.api.report.jasperreport.{JasperReportCompiledTemplate, JasperReportParameter, JasperReportTemplateCompiler}
import ru.restserver.api.report.{ParameterInfo, TemplateFiller}
import ru.restserver.database.DatabaseConnection

import scala.concurrent.ExecutionContext

abstract class StubbedJasperReportContext(implicit val system: ActorSystem, val ec: ExecutionContext, val timeout: Timeout) extends StubbedJasperReportContextLike

trait StubbedJasperReportContextLike extends MockFactory {
  implicit val system: ActorSystem
  implicit val ec: ExecutionContext
  implicit val timeout: Timeout
  lazy val parameterInfo = ParameterInfo("NamePattern", Option("Поле для фильтрации пациентов по фамилии"), "String", Option(""""K""""))
  lazy val filteredParameterInfo = ParameterInfo("REPORT_LOCALE", Option("Report locale"), "String", Option(""""RU""""))
  lazy val jrCompiler = stubbedJRCompiler
  lazy val templateCompiler = {
    val id = UUID.randomUUID()
    system.actorOf(TemplateCompilerActor.props(Option(jrCompiler)), s"templatecompiler-$id")
  }
  lazy val filler: TemplateFiller = stubbedFiller

  def stubbedJRCompiler: JasperReportTemplateCompiler = {
    val jrCompiler = stub[JasperReportTemplateCompiler]
    val jrCompiledTemplate = stub[JasperReportCompiledTemplate]
    val jrParameter1 = stub[JasperReportParameter]
    val jrParameter2 = stub[JasperReportParameter]
    def getParameters: Set[JasperReportParameter] = Set(jrParameter1, jrParameter2)
    jrCompiler.compile _ when * returns jrCompiledTemplate
    jrParameter1.name _ when () returns parameterInfo.name
    jrParameter1.description _ when () returns parameterInfo.description
    jrParameter1.defaultValue _ when () returns parameterInfo.defaultValue
    jrParameter2.name _ when () returns filteredParameterInfo.name
    jrParameter1.description _ when () returns filteredParameterInfo.description
    jrParameter1.defaultValue _ when () returns filteredParameterInfo.defaultValue
    jrCompiledTemplate.getParameters _ when () returns getParameters
    jrCompiler
  }

  def stubbedFiller: TemplateFiller = {
    val filler = stub[TemplateFiller]
    (filler.apply(_: CompiledTemplate, _: Map[String, String], _: DatabaseConnection)(_: LoggingAdapter)) when (*, *, *, *) returns new JasperPrint
    filler
  }
}