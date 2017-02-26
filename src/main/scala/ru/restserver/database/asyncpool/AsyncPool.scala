package ru.restserver.database.asyncpool

import scala.util.{ Failure, Success, Try }
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import akka.actor.{ Actor, ActorSystem, OneForOneStrategy, Props }
import akka.actor.SupervisorStrategy._
import akka.routing.RoundRobinPool
import akka.pattern.ask
import akka.util.Timeout

class AsyncPool[T](
    val name: String,
    val size: Int,
    val defaultTimeout: Timeout,
    val maxNrOfRetries: Int,
    val retryRange: Duration,
    val objectFactory: PoolableObjectFactory[T]
)(implicit actorSystem: ActorSystem) {
  private val supervisor =
    OneForOneStrategy(
      maxNrOfRetries = maxNrOfRetries,
      withinTimeRange = retryRange
    ) {

      case _: Throwable => Restart
    }

  private val router =
    actorSystem.actorOf(
      props =
        AsyncPoolWorkerActor.props(objectFactory)
          .withRouter(RoundRobinPool(
            nrOfInstances = size,
            supervisorStrategy = supervisor
          )),
      name = name
    )

  import actorSystem.dispatcher

  def execute[U](work: T => U)(implicit timeout: Timeout = defaultTimeout): Future[U] =
    ask(router, Work[T, U](work)).map {
      case Success(res: U) => res
      case Failure(t) => throw new AsyncPoolException("AsyncPools execution error", t)
    }

}

private class AsyncPoolWorkerActor[T](objectFactory: PoolableObjectFactory[T]) extends Actor {
  private val pooledObject = objectFactory.apply

  def receive = {
    case Work(work: Function1[T, _]) =>
      objectFactory.check(pooledObject)
      sender ! Try(work(pooledObject))
      objectFactory.postCheck(pooledObject)
  }
}

private object AsyncPoolWorkerActor {
  def props[T](objectFactory: PoolableObjectFactory[T]): Props = Props(new AsyncPoolWorkerActor[T](objectFactory))
}

