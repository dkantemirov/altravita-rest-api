package ru.restserver.http

import ru.restserver.api.ServiceException
import ru.restserver.api.auth.AuthenticationException
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import spray.routing.{ Directives, ExceptionHandler }
import spray.util.LoggingContext

trait DefaultExceptionHandler extends Directives {
  implicit def exceptionHandler(implicit log: LoggingContext): ExceptionHandler = {
    ExceptionHandler {
      case e: AuthenticationException =>
        requestUri { uri =>
          respondWithMediaType(`application/json`) {
            complete {
              import JsonProtocol._
              Unauthorized -> e
            }
          }
        }
      case e: ServiceException =>
        requestUri { uri =>
          respondWithMediaType(`application/json`) {
            complete {
              import JsonProtocol._
              InternalServerError -> e
            }
          }
        }
    }
  }
}