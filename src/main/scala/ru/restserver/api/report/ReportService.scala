package ru.restserver.api.report

import scala.concurrent.Future

trait ReportService {
  def generate(report: Report): Future[GeneratedReport]
  def info(template: Template): Future[TemplateInfo]
  def allInfo: Future[Set[TemplateInfo]]
  def list: Future[Map[Int, String]]
}
