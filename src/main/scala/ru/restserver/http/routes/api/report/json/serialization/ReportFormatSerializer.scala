package ru.restserver.http.routes.api.report.json.serialization

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString
import ru.restserver.api.report.Format

case object ReportFormatSerializer extends CustomSerializer[Format](_ => (
  {
    case JString(s) => Format(s)
  },
  {
    case value: Format => JString(value.toString)
  }
))
