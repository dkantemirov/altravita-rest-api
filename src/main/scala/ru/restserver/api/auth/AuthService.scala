package ru.restserver.api.auth

import scala.concurrent.Future

trait AuthService {
  @throws[AuthenticationException]("if credentials not verified")
  def signIn(login: String, password: String): Future[Session]

  @throws[AuthenticationException]("if invalid session")
  def signOut(session: Session): Future[Unit]

  def signUp(newUser: User): Future[Session]

  def authenticate(token: String): Future[Option[Session]]

  def createSession(user: User): Future[Session]
}
