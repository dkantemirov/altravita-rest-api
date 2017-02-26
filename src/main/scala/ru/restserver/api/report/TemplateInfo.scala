package ru.restserver.api.report

case class TemplateInfo(template: Template, parametersInfo: Set[ParameterInfo]) {
  def apply(id: Int, parametersInfo: Set[ParameterInfo]): TemplateInfo = {
    TemplateInfo(Template(id), parametersInfo)
  }
}
