package ru.restserver.http.routes.api.report.json

import java.util.Base64

import org.json4s.Formats
import ru.restserver.api.report.{ GeneratedReport, ParameterInfo, TemplateInfo }
import ru.restserver.http.DefaultJsonProtocol
import ru.restserver.http.routes.api.report.json.serialization._

object JsonProtocol extends DefaultJsonProtocol {
  override implicit def json4sFormats: Formats =
    super.json4sFormats +
      ReportFormatSerializer

  def toJsonGeneratedReport(r: GeneratedReport): JsonGeneratedReport = {
    JsonGeneratedReport(
      r.format,
      Base64.getEncoder.encodeToString(r.data)
    )
  }

  def toJsonTemplateInfo(i: TemplateInfo): JsonTemplateInfo = {
    JsonTemplateInfo(
      i.template.id,
      i.template.name,
      Option(i.template.fileName),
      i.parametersInfo.toSeq match {
        case Seq() => None
        case s: Seq[ParameterInfo] => Option(i.parametersInfo)
      }
    )
  }

  def toJsonTemplateInfo(templateId: Int, name: String): JsonTemplateInfo = JsonTemplateInfo(templateId, name)
}
