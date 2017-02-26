package ru.restserver.services

import ru.restserver.api.report.{Format, Report, Template}
import ru.restserver.utils.ServiceSpec

class ReportServiceSpec extends ServiceSpec {
  "Report service" when {
    "generate()" should produce {
      "generated report" in {
        reportService.generate(Report(Template.PatientsByNamePattern, None, Set.empty)).map { generated =>
          generated.data should not be empty
          generated.format shouldBe Format.Default
        }
      }
    }
    "info()" should produce {
      "template information" in {
        reportService.info(template).map { info =>
          info.parametersInfo map { i =>
            i.name should not be empty
            i.dataType should not be empty
          }
          info.template shouldBe template
        }
      }
    }
  }
}