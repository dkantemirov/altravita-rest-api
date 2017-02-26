package ru.restserver.http.routes.api.report.json

import ru.restserver.api.report.Format

case class JsonGeneratedReport(format: Format, data: String)