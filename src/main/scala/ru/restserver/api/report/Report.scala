package ru.restserver.api.report

case class Report(template: Template, var format: Option[Format], parameters: Set[Parameter])