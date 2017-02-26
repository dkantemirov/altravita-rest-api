package ru.restserver.database

import javax.naming.InitialContext
import javax.sql.DataSource

import akka.event.LoggingAdapter
import ru.restserver.api.Configuration

import scala.concurrent.{ ExecutionContext, Future }

object DatabaseConnectionPool {
  def retrieve(implicit executor: ExecutionContext, log: LoggingAdapter): Future[DatabaseConnection] = {
    Future {
      val ctx = new InitialContext
      try {
        val ds = ctx.lookup(Configuration.servletContainer.jndiPool).asInstanceOf[DataSource]
        val connection = ds.getConnection
        log.info("Retrieved new database connection from pool.")
        new DatabaseConnection(Option(connection))
      } catch {
        case e: Throwable =>
          log.error("Exception when retrieve new database connection.")
          new DatabaseConnection(None)
      }
    }
  }
  def close(connection: DatabaseConnection)(implicit executor: ExecutionContext, log: LoggingAdapter): Future[Boolean] = Future {
    var closed = false
    connection.wrapped.foreach(connection => {
      connection.close()
      log.info("Database connection closed.")
      closed = true
    })
    closed
  }
}
