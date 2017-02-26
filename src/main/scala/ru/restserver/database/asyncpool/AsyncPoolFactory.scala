package ru.restserver.database.asyncpool

import scala.concurrent.duration.Duration

import akka.actor.ActorSystem
import akka.util.Timeout

class AsyncPoolFactory(implicit system: ActorSystem) extends Configuration {
  protected def newPool[T](
    name: String,
    size: Int,
    defaultTimeout: Timeout,
    maxNrOfRetries: Int,
    retryRange: Duration,
    objectFactory: PoolableObjectFactory[T]
  ) =
    new AsyncPool(
      name = name,
      size = size,
      defaultTimeout = defaultTimeout,
      maxNrOfRetries = maxNrOfRetries,
      retryRange = retryRange,
      objectFactory = objectFactory
    )

  //  protected def newConfiguredPool[T](name: String)(factory: com.typesafe.config.Config => PoolableObjectFactory[T]) = {
  //    val config = poolConfig(name)
  //    val size = config.get("size", throw new AsyncPoolException(s"Unable to create pool $name, due to missing or invalid size configuration")).toInt
  //    val defaultTimeout = Timeout(Duration(config.get("defaultTimeout", throw new AsyncPoolException(s"Unable to create pool $name, due to missing or invalid defaultTimeout configuration"))).toMillis)
  //    val maxNrOfRetries = config.get("maxNrOfRetries", throw new AsyncPoolException(s"Unable to create pool $name, due to missing or invalid maxNrOfRetries configuration")).toInt
  //    val retryRange = Duration(config.get("retryRange", throw new AsyncPoolException(s"Unable to create pool $name, due to missing or invalid retryRange configuration")))
  //
  //    newPool(
  //      name = name,
  //      size = size,
  //      defaultTimeout = defaultTimeout,
  //      maxNrOfRetries = maxNrOfRetries,
  //      retryRange = retryRange,
  //      objectFactory = factory(config))
  //  }
}