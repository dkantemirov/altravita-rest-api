package ru.restserver.http.routes.api.report.json

import ru.restserver.api.report.ParameterInfo

case class JsonTemplateInfo(template: Int, name: String, fileName: Option[String] = None, parametersInfo: Option[Set[ParameterInfo]] = None)