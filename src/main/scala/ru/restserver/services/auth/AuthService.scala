package ru.restserver.services.auth

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

import akka.event.LoggingAdapter
import ru.restserver.api.{ Configuration, auth => api }
import ru.restserver.api.auth._

import scala.concurrent.{ ExecutionContext, Future }

class AuthService(val driverManager: DriverManager)(implicit ec: ExecutionContext, log: LoggingAdapter) extends api.AuthService {
  private val currentUserId = new AtomicLong(1000)
  val currentSessionId = new AtomicLong(1000)
  val sessions = new ConcurrentHashMap[String, Session]()
  sessions.put(Configuration.auth.tokenAuthenticator.integrationToken, Session(
    currentSessionId.incrementAndGet(),
    User(currentUserId.incrementAndGet(), "", ""),
    Configuration.auth.tokenAuthenticator.integrationToken
  ))

  def this()(implicit ec: ExecutionContext, log: LoggingAdapter) = this(new DriverManager)

  override def signIn(login: String, password: String): Future[Session] = {
    def getUser: Future[User] = Future(User(currentUserId.incrementAndGet(), login, password))
    def verifyCredentials: Future[Boolean] = Future(driverManager.verifyCredentials(Configuration.auth.db.url, login, password))
    for {
      verified <- verifyCredentials
      user <- getUser
      session <- createSession(user)
    } yield {
      log.info(s"User [userId:${session.user.id}, sessionId:${session.id}] signed in.")
      session
    }
  }

  override def signUp(newUser: User): Future[Session] = ???

  override def authenticate(token: String): Future[Option[Session]] = Future {
    if (sessions.containsKey(token)) Option(sessions.get(token))
    else None
  }

  override def createSession(user: User): Future[Session] = Future {
    val session = Session(currentSessionId.incrementAndGet(), user)
    sessions.put(session.token, session)
    session
  }

  override def signOut(session: Session): Future[Unit] = Future {
    val token = session.token
    if (sessions.containsKey(token)) {
      sessions.remove(token)
      log.info(s"User [userId:${session.user.id}, sessionId:${session.id}] signed out.")
    } else throw new AuthenticationException
  }
}