package ru.restserver.api.worker

import akka.actor.{ Actor, ActorLogging, ActorRef }
import ru.restserver.api.ActorLifecycleHooks

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

abstract class WorkerActor[InputData: ClassTag, OutputData](implicit ec: ExecutionContext) extends Actor
    with ActorLogging
    with ActorLifecycleHooks {
  import WorkerActor._
  var processed: Option[OutputData] = None

  override def receive: Receive = {
    case Work(recipient, data: InputData) =>
      recipient ! {
        processed match {
          case Some(p) => p
          case None =>
            val p = process(data)
            processed = Option(p)
            context.parent ! WorkDone(p)
            p
        }
      }
    case IsDone => sender ! processed.nonEmpty
    case Processed => sender ! processed
  }

  def process(data: InputData): OutputData
}

object WorkerActor {
  case class Work[A: ClassTag](recipient: ActorRef, data: A)
  case class WorkDone[A](result: A)
  case object IsDone
  case object Processed
}