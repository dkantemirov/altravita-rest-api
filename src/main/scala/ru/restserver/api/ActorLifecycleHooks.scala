package ru.restserver.api

import akka.actor.{ Actor, ActorLogging }

trait ActorLifecycleHooks { self: Actor with ActorLogging =>
  override def preStart(): Unit = log.info("Created.")
  override def postStop(): Unit = log.info("Terminated.")
}
