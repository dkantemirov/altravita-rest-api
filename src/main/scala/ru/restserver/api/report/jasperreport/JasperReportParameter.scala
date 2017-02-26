package ru.restserver.api.report.jasperreport

import net.sf.jasperreports.engine.JRParameter

class JasperReportParameter(val parameterOpt: Option[JRParameter] = None) {
  lazy val parameter = parameterOpt.get
  def name: String = parameter.getName
  def description: Option[String] = Option(parameter.getDescription)
  def defaultValue: Option[String] = Option(parameter.getDefaultValueExpression).map(_.getText)
}
