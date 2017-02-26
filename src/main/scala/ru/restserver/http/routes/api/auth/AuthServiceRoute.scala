package ru.restserver.http.routes.api.auth

import akka.event.LoggingAdapter
import ru.restserver.api.auth.{ AuthService, User }
import ru.restserver.http.SecurityDirectives
import ru.restserver.http.routes.api.auth.json.{ JsonLoginPassword, JsonProtocol }
import spray.http.StatusCodes
import spray.routing._

import scala.concurrent.ExecutionContext

class AuthServiceRoute(val authService: AuthService)(implicit val ec: ExecutionContext, log: LoggingAdapter) extends Directives with SecurityDirectives {
  import StatusCodes._
  val route: Route = pathPrefix("auth") {
    (pathPrefix("sign-in") & pathEndOrSingleSlash) {
      post {
        import JsonProtocol._
        entity(as[JsonLoginPassword]) { loginPassword =>
          complete {
            authService.signIn(loginPassword.login, loginPassword.password) map toJsonSession
          }
        }
      }
    } ~
      (pathPrefix("sign-up") & pathEndOrSingleSlash) {
        post {
          import JsonProtocol._
          entity(as[User]) { user =>
            complete {
              import JsonProtocol._
              Created -> authService.signUp(user)
            }
          }
        }
      } ~
      (pathPrefix("sign-out") & pathEndOrSingleSlash) {
        post {
          auth { implicit session =>
            import scala.util.{ Failure, Success }
            onComplete(authService.signOut(session)) {
              case Success(_) => complete(OK)
              case Failure(_) => complete(InternalServerError)
            }
          }
        }
      }
  }
}
