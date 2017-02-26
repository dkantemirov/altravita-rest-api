package ru.restserver.http.routes.api.report.json

import ru.restserver.api.report._

case class JsonReport(template: Int, format: Option[Format], parameters: Set[Parameter]) {
  def report = Report(Template(template), format, parameters)
}