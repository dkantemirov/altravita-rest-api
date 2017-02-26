package ru.restserver.api.report.compile

import java.io.InputStream
import java.util.UUID

import akka.pattern.gracefulStop
import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import ru.restserver.api.report.jasperreport.{ JasperReportCompiledTemplate, JasperReportTemplateCompiler }
import ru.restserver.api.report.Template
import ru.restserver.api.worker.WorkerActor
import ru.restserver.api.worker.WorkerActor.WorkDone
import ru.restserver.api.ActorLifecycleHooks

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class TemplateCompilerActor(compiler: JasperReportTemplateCompiler = new JasperReportTemplateCompiler)(implicit ec: ExecutionContext) extends Actor
    with ActorLogging
    with ActorLifecycleHooks {
  import TemplateCompilerActor._
  lazy val cache: mutable.HashMap[Long, CompiledTemplate] = mutable.HashMap()
  lazy val workers: mutable.HashMap[Long, ActorRef] = mutable.HashMap()

  override def receive: Receive = {
    case Compile(template) =>
      isCompiled(template) match {
        case true => sender() ! precompiled(template)
        case _ => compile(sender(), template)
      }
    case WorkDone(compiled: CompiledTemplate) =>
      cache.put(compiled.templateId, compiled)
      gracefulStop(sender(), 5 seconds)
    case IsCompiled(template) => sender() ! isCompiled(template)
    case AllPrecompiled => sender() ! allPrecompiled
  }

  def isCompiled(template: Template): Boolean = cache contains template.id

  def notCompiled(template: Template): Boolean = !isCompiled(template)

  def precompiled(template: Template): CompiledTemplate = {
    val templateId = template.id
    if (isCompiled(template)) {
      val p: CompiledTemplate = cache(templateId)
      log.info(s"Retrieved pre-compiled template [id:$templateId][${p.generatedDateTime}] from cache.")
      p
    } else throw new TemplateCompilerException(s"Template [id:$templateId] hasn't been compiled yet.")
  }

  def allPrecompiled: Set[CompiledTemplate] = {
    val cached: Set[(Long, CompiledTemplate)] = cache.toSet
    cached.map(c => c._2)
  }

  def compile(client: ActorRef, template: Template): Unit = {
    val templateId = template.id
    workers contains templateId match {
      case true => workers(templateId) ! WorkerActor.Work(client, template)
      case _ =>
        val worker = {
          val id = UUID.randomUUID()
          context.actorOf(TemplateCompilerWorkerActor.props(compiler), s"worker-$id")
        }
        workers.put(templateId, worker)
        worker ! WorkerActor.Work(client, template)
    }
  }
}

object TemplateCompilerActor {
  final val folder = "reports"
  final val name = "templatecompiler"

  case class Compile(template: Template)
  case class IsCompiled(template: Template)
  case object AllPrecompiled

  def props(compiler: Option[JasperReportTemplateCompiler] = None)(implicit ec: ExecutionContext): Props = {
    compiler match {
      case Some(c) => Props(new TemplateCompilerActor(c))
      case None => Props(new TemplateCompilerActor)
    }
  }
}

private class TemplateCompilerWorkerActor(compiler: JasperReportTemplateCompiler = new JasperReportTemplateCompiler)(implicit ec: ExecutionContext) extends WorkerActor[Template, CompiledTemplate] {
  import TemplateCompilerActor._
  override def process(template: Template): CompiledTemplate = {
    val id = template.id
    val fileName = template.fileName
    val path = s"/$folder/$fileName.jrxml"
    def compile(path: String): JasperReportCompiledTemplate = {
      val stream: InputStream = getClass getResourceAsStream path
      val compiled = compiler compile stream
      stream.close()
      compiled
    }
    val compiled = new CompiledTemplate(id, compile(path))
    log.info(s"Template [id:$id, path:$path][${compiled.generatedDateTime}] compiled.")
    compiled
  }
}

private object TemplateCompilerWorkerActor {
  def props(compiler: JasperReportTemplateCompiler)(implicit ec: ExecutionContext): Props = Props(new TemplateCompilerWorkerActor(compiler))
}

