package ru.restserver.api.report

import java.util

import akka.event.LoggingAdapter
import net.sf.jasperreports.engine.{ JasperFillManager, JasperPrint }
import ru.restserver.api.report.compile.CompiledTemplate
import ru.restserver.database.DatabaseConnection

class TemplateFiller {
  def apply(compiled: CompiledTemplate, parameters: Map[String, String], connection: DatabaseConnection)(implicit log: LoggingAdapter): JasperPrint = {
    val c = connection.wrapped.orNull
    val fillParameters = new util.HashMap[String, AnyRef]()
    parameters.map(p => fillParameters.put(p._1, p._2))
    JasperFillManager.fillReport(compiled.report.reportOpt.get, fillParameters, c)
  }
}
