package ru.restserver.services

import akka.event.LoggingAdapter
import ru.restserver.api
import ru.restserver.database.{ DatabaseConnection, DatabaseConnectionPool }

import scala.concurrent.{ ExecutionContext, Future }

class DatabaseService extends api.DatabaseService {
  def retrieveConnection(implicit ec: ExecutionContext, log: LoggingAdapter): Future[DatabaseConnection] = DatabaseConnectionPool.retrieve(ec, log)
  def closeConnection(connection: DatabaseConnection)(implicit ec: ExecutionContext, log: LoggingAdapter): Future[Boolean] = DatabaseConnectionPool.close(connection)
}
