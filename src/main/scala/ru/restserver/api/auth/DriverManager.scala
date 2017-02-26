package ru.restserver.api.auth

import java.sql.{ Connection, SQLException }

import akka.event.LoggingAdapter

class DriverManager(implicit log: LoggingAdapter) {
  import DriverManager._
  def getConnection(url: String, user: String, password: String): Connection = {
    try java.sql.DriverManager.getConnection(url, user, password) catch {
      case e: SQLException if e.getErrorCode == unknownUserOrPasswordCode =>
        throw new UnknownUserOrPasswordIncorrectException
      case e: SQLException if passwordExpiredCodes contains e.getErrorCode =>
        throw new PasswordHasExpiredException
      case e: SQLException =>
        log.error(e, s"Exception when connecting database [url=$url, user:$user, errorCode=${e.getErrorCode}, SQLSTATE=${e.getSQLState}].")
        throw new AuthenticationException
    }
  }
  def verifyCredentials(url: String, user: String, password: String): Boolean = {
    getConnection(url, user, password).close()
    true
  }
}

object DriverManager {
  final val unknownUserOrPasswordCode = 18456
  final val passwordExpiredCodes = List(18487, 18488)
}
