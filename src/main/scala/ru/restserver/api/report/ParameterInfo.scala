package ru.restserver.api.report

case class ParameterInfo(name: String, description: Option[String], dataType: String, defaultValue: Option[String])
