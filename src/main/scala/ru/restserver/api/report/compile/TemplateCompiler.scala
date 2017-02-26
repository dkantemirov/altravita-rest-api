package ru.restserver.api.report.compile

import java.util.UUID

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props }
import akka.pattern.ask
import akka.event.LoggingAdapter
import akka.util.Timeout
import ru.restserver.api.report.Template
import ru.restserver.api.ActorLifecycleHooks

import scala.concurrent.Future

class TemplateCompiler(val compilerActor: ActorRef)(implicit system: ActorSystem, timeout: Timeout, log: LoggingAdapter) {
  import TemplateCompilerActor._
  import system.dispatcher
  implicit val sender = {
    val id = UUID.randomUUID()
    system.actorOf(TemplateCompilerClientActor.props, s"templatecompilerclient-$id")
  }
  def isCompiled(template: Template): Future[Boolean] = (compilerActor ? IsCompiled(template)).mapTo[Boolean]
  def allPrecompiled: Future[Set[CompiledTemplate]] = (compilerActor ? AllPrecompiled).mapTo[Set[CompiledTemplate]]
  def compile(template: Template): Future[CompiledTemplate] = (compilerActor ? Compile(template)).mapTo[CompiledTemplate]
  def compileAndClose(template: Template): Future[CompiledTemplate] = {
    for {
      compiled <- compile(template)
      closed <- close
    } yield compiled
  }
  def close: Future[Boolean] = Future {
    sender ! PoisonPill
    true
  }
}

object TemplateCompiler {
  def apply(compilerActor: ActorRef, template: Template)(implicit system: ActorSystem, timeout: Timeout, log: LoggingAdapter): Future[CompiledTemplate] = {
    val client = new TemplateCompiler(compilerActor)
    client.compileAndClose(template)
  }
}

private class TemplateCompilerClientActor extends Actor
    with ActorLogging
    with ActorLifecycleHooks {
  override def receive: Receive = {
    case _ =>
  }
}

private object TemplateCompilerClientActor {
  def props: Props = Props[TemplateCompilerClientActor]
}
