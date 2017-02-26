package ru.restserver.api.report.jasperreport

import java.io.InputStream

import net.sf.jasperreports.engine.JasperCompileManager

class JasperReportTemplateCompiler {
  def compile(stream: InputStream): JasperReportCompiledTemplate = {
    new JasperReportCompiledTemplate(JasperCompileManager compileReport stream)
  }
}
