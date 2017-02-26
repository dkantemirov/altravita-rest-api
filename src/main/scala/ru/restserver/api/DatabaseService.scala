package ru.restserver.api

import akka.event.LoggingAdapter
import ru.restserver.database.DatabaseConnection

import scala.concurrent.{ ExecutionContext, Future }

trait DatabaseService {
  def retrieveConnection(implicit ec: ExecutionContext, log: LoggingAdapter): Future[DatabaseConnection]
  def closeConnection(connection: DatabaseConnection)(implicit ec: ExecutionContext, log: LoggingAdapter): Future[Boolean]
}
