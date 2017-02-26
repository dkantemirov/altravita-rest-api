package ru.restserver.http

import ru.restserver.api.Configuration
import ru.restserver.api.auth.{ AuthService, Session, TokenAuthenticator }
import spray.routing.{ Directives, _ }

import scala.concurrent.ExecutionContext

trait SecurityDirectives {
  implicit val ec: ExecutionContext
  val authService: AuthService
  val authenticator = TokenAuthenticator[Session](
    Configuration.auth.tokenAuthenticator.header,
    Configuration.auth.tokenAuthenticator.queryStringParameter
  )(authService.authenticate)

  def auth: Directive1[Session] = Directives.authenticate(authenticator)
}
