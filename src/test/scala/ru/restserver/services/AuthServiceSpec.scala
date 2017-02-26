package ru.restserver.services

import org.scalamock.scalatest.MockFactory
import ru.restserver.api.auth._
import ru.restserver.http.routes.api.auth.AuthServiceRoute
import ru.restserver.services.auth.AuthService
import ru.restserver.utils.ServiceSpec
import ru.restserver.utils.Boot

class AuthServiceSpec extends ServiceSpec {
  "Auth service" when {
    "signIn()" should produce {
      "new session" in {
        import SuccessfulVerificationContext._
        authService.signIn(login, password).map { session =>
          val token = session.token
          authService.sessions.containsKey(token) shouldBe true
          token should not contain "-"
        }
      }
      "UnknownUserOrPasswordIncorrectException" in {
        import UnsuccessfulVerificationContext._
        recoverToSucceededIf[UnknownUserOrPasswordIncorrectException] {
          authService.signIn(login, password)
        }
      }
    }
    "createToken()" should produce {
      "token" in {
        import DefaultContext._
        authService.createSession(User(1, login, password)).map { session =>
          val token = session.token
          token should not contain "-"
          authService.sessions.containsKey(token) shouldBe true
        }
      }
    }
    "authenticate()" should produce {
      "token" in {
        import DefaultContext._
        for {
          session <- authService.signIn(login, password)
          authenticated <- authService.authenticate(session.token)
        } yield authenticated should not be empty
      }
      "None" in {
        import DefaultContext._
        authService.authenticate("token").map(_ shouldBe empty)
      }
    }
    "signOut()" should {
      "not produce any exceptions" in {
        import DefaultContext._
        for {
          session <- authService.signIn(login, password)
          authenticated <- authService.authenticate(session.token)
          _ <- authService.signOut(session)
        } yield {
          val token = session.token
          authenticated should not be empty
          authService.sessions.containsKey(token) shouldBe false
        }
      }
      "produce AuthenticationException" in {
        import DefaultContext._
        recoverToSucceededIf[AuthenticationException] {
          authService.signOut(Session(1, User(2, login, password)))
        }
      }
    }
  }
}

private trait Context extends MockFactory {
  import Boot._
  val login = "user"
  val password = "user"
  val driverManager = stub[DriverManager]
  val authService = new AuthService(driverManager)
  val authServiceRouter = new AuthServiceRoute(authService)
}
private object SuccessfulVerificationContext extends Context {
  driverManager.verifyCredentials _ when(*, *, *) returns true
}
private object UnsuccessfulVerificationContext extends Context {
  driverManager.verifyCredentials _ when(*, *, *) throws new UnknownUserOrPasswordIncorrectException
}
private object DefaultContext extends Context