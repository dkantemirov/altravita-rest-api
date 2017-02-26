package ru.restserver.api.report.jasperreport

import net.sf.jasperreports.engine.{ JRParameter, JasperReport }

class JasperReportCompiledTemplate(val reportOpt: Option[JasperReport] = None) {
  def this(report: JasperReport) = this(Option(report))
  def report: JasperReport = reportOpt.get
  def getParameters: Set[JasperReportParameter] = report.getParameters.toSet.map { p: JRParameter =>
    new JasperReportParameter(Option(p))
  }
}