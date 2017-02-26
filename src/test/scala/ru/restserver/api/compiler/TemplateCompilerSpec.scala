package ru.restserver.api.compiler

import akka.event.LoggingAdapter
import org.scalatest.{ AsyncWordSpecLike, Matchers }
import ru.restserver.api.report.Template
import ru.restserver.api.report.compile.{ CompiledTemplate, TemplateCompiler }
import ru.restserver.utils.{ StubbedJasperReportContext, Boot }

import scala.concurrent.{ ExecutionContext, Future }

class TemplateCompilerSpec extends StubbedJasperReportContext()(Boot.system, Boot.ec, Boot.timeout)
    with AsyncWordSpecLike
    with Matchers {
  override implicit val ec: ExecutionContext = Boot.ec
  implicit val log: LoggingAdapter = Boot.log
  val template: Template = Boot.template

  "A report compiler" when {
    "the template isn't cached" should {
      "compile template and put into cache" in {
        for {
          compiled <- compileTemplate(template)
          a <- isCompiled(template)
          b <- allPrecompiled
        } yield {
          a shouldBe true
          b should contain(compiled)
        }
      }
    }
    "the template is cached" should {
      "retrieve compiled template from cache" in {
        val f1 = compileTemplate(template)
        val f2 = compileTemplate(template)
        val f3 = compileTemplate(template)
        def f4 = isCompiled(template)
        def f5 = allPrecompiled
        for {
          r1 <- f1
          r2 <- f2
          r3 <- f3
          r4 <- f4
          r5 <- f5
        } yield {
          r1.generatedDateTime shouldBe r2.generatedDateTime
          r2.generatedDateTime shouldBe r3.generatedDateTime
          r4 shouldBe true
          r5 should contain(r1)
        }
      }
    }
  }

  def compileTemplate(template: Template): Future[CompiledTemplate] = {
    TemplateCompiler(templateCompiler, template)
  }

  def isCompiled(template: Template): Future[Boolean] = {
    val client = new TemplateCompiler(templateCompiler)
    for {
      isCompiled <- client.isCompiled(template)
      closed <- client.close
    } yield isCompiled
  }

  def allPrecompiled: Future[Set[CompiledTemplate]] = {
    val client = new TemplateCompiler(templateCompiler)
    for {
      precompiled <- client.allPrecompiled
      closed <- client.close
    } yield precompiled
  }
}