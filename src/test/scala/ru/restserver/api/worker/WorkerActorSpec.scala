package ru.restserver.api.worker

import akka.actor.{ActorRef, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import org.scalatest.{Matchers, WordSpecLike}
import ru.restserver.utils.Boot

import scala.concurrent.ExecutionContext

class WorkerActorSpec extends TestKit(Boot.system)
    with WordSpecLike
    with ImplicitSender
    with Matchers
    with DefaultTimeout {
  import WorkerActor._
  implicit val executor: ExecutionContext = system.dispatcher
  val worker: ActorRef = system.actorOf(Props(new WorkerActorImpl), "workeractorimpl")
  val data = "the:work:is:done"

  "A worker actor" when {
    "the work is not done" should {
      "send processed to recipient" in {
        worker ! Work(self, data)
        expectMsg("the-work-is-done")
        worker ! IsDone
        expectMsg(true)
      }
    }
    "the work is done" should {
      "return processed" in {
        worker ! Processed
        expectMsg(Option("the-work-is-done"))
      }
    }
  }
}

class WorkerActorImpl(implicit executor: ExecutionContext) extends WorkerActor[String, String] {
  override def process(data: String): String = {
    data.replaceAll(":", "-")
  }
}