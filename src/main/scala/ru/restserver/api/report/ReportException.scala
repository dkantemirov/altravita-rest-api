package ru.restserver.api.report

import ru.restserver.api.ServiceException

class ReportException(message: String = "Ошибка в модуле генерации отчетов.") extends ServiceException(message)
class UnknownTemplateException(message: String) extends ReportException(message) {
  def this(templateId: Int) = this(s"Шаблон отчета $templateId не существует.")
}

