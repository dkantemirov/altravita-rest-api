package ru.restserver.api.report

sealed abstract case class Template(id: Int, name: String, fileName: String)

object Template {
  object PatientsByNamePattern extends Template(5000, "Поиск пациентов по фамилии", "PatientsByNamePattern")
  object Report2 extends Template(5001, "Отчет 2", "Report2")
  object Report3 extends Template(5002, "Отчет 3", "Report3")

  def apply(id: Int): Template = {
    id match {
      case PatientsByNamePattern.id => PatientsByNamePattern
      case Report2.id => Report2
      case Report3.id => Report3
      case _ => throw new UnknownTemplateException(id)
    }
  }

  def all: Set[Template] = Set(
    PatientsByNamePattern,
    Report2,
    Report3
  )
}