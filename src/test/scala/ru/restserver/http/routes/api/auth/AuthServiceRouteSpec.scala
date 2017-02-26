package ru.restserver.http.routes.api.auth

import ru.restserver.http.DefaultExceptionHandler
import ru.restserver.http.routes.api.auth.json._
import ru.restserver.utils.ServiceRouteSpec
import ru.restserver.api.Configuration
import ru.restserver.api.auth.{ DriverManager, UnknownUserOrPasswordIncorrectException }
import ru.restserver.services.auth.AuthService
import spray.http.{ HttpEntity, MediaTypes }
import spray.http._
import spray.routing.Route

class AuthServiceRouteSpec extends ServiceRouteSpec {
  import StatusCodes._

  class Context extends DefaultExceptionHandler {
    val driverManager: DriverManager = stub[DriverManager]
    val authService = new AuthService(driverManager)
    val authServiceRouter = new AuthServiceRoute(authService)
    val route: Route = handleExceptions(exceptionHandler) {
      authServiceRouter.route
    }
    val login = "user"
    val password = "user"
  }
  class SuccessfulVerificationContext extends Context {
    driverManager.verifyCredentials _ when (*, *, *) returns true
  }
  class UnsuccessfulVerificationContext extends Context {
    driverManager.verifyCredentials _ when (*, *, *) throws new UnknownUserOrPasswordIncorrectException
  }

  "Auth service route" when {
    "successful verification of credentials" should produce {
      "new session" in new SuccessfulVerificationContext {
        signIn(JsonLoginPassword(login, password), route) {
          import JsonProtocol._
          response.status shouldBe OK
          responseAs[JsonSession]
        }
      }
      "true when user is signed out" in new SuccessfulVerificationContext {
        signIn(JsonLoginPassword(login, password), route) {
          import JsonProtocol._
          response.status shouldBe OK
          val token = responseAs[JsonSession].token
          signOut(token, route) {
            response.status shouldBe OK
          }
        }
      }
    }
    "unsuccessful verification of credentials" should produce {
      "UnknownUserOrPasswordIncorrectException with Unauthorized status" in new UnsuccessfulVerificationContext {
        signIn(JsonLoginPassword(login, password), route) {
          import JsonProtocol._
          response.status shouldBe Unauthorized
          responseAs[UnknownUserOrPasswordIncorrectException]
        }
      }
    }
  }

  private def signIn(loginPassword: JsonLoginPassword, route: Route)(action: => Unit) = {
    def serialized = {
      import JsonProtocol._
      serialize(loginPassword)
    }
    val requestEntity = HttpEntity(MediaTypes.`application/json`, serialized)
    Post("/auth/sign-in/", requestEntity) ~> route ~> check(action)
  }

  private def signOut(token: String, route: Route)(action: => Unit) = {
    Post("/auth/sign-out/") ~> addHeader(Configuration.auth.tokenAuthenticator.header, token) ~> route ~> check(action)
  }
}