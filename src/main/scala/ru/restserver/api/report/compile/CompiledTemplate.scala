package ru.restserver.api.report.compile

import java.time.LocalDateTime

import ru.restserver.api.report.ParameterInfo
import ru.restserver.api.report.jasperreport.{ JasperReportCompiledTemplate, JasperReportParameter }

class CompiledTemplate(val templateId: Long, val report: JasperReportCompiledTemplate) {
  val generatedDateTime = LocalDateTime.now()
  def parametersInfo: Set[ParameterInfo] = {
    def filterParameter(p: JasperReportParameter): Boolean = {
      val jrParameters = Seq(
        "REPORT_FILE_RESOLVER",
        "JASPER_REPORTS_CONTEXT",
        "SORT_FIELDS",
        "IS_IGNORE_PAGINATION",
        "REPORT_TIME_ZONE",
        "REPORT_MAX_COUNT",
        "REPORT_CONNECTION",
        "REPORT_SCRIPTLET",
        "FILTER",
        "REPORT_URL_HANDLER_FACTORY",
        "REPORT_FORMAT_FACTORY",
        "REPORT_CONTEXT",
        "REPORT_VIRTUALIZER",
        "JASPER_REPORT",
        "REPORT_DATA_SOURCE",
        "REPORT_RESOURCE_BUNDLE",
        "REPORT_CLASS_LOADER",
        "REPORT_PARAMETERS_MAP",
        "REPORT_TEMPLATES",
        "REPORT_LOCALE"
      )
      val contains = jrParameters contains p.name
      !contains
    }
    report.getParameters
      .filter(filterParameter)
      .map(p => ParameterInfo(p.name, p.description, "String", p.defaultValue))
  }
}
