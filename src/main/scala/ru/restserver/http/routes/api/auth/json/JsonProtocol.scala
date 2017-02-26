package ru.restserver.http.routes.api.auth.json

import ru.restserver.api.auth.Session
import ru.restserver.http.DefaultJsonProtocol

object JsonProtocol extends DefaultJsonProtocol {
  override implicit def json4sFormats = super.json4sFormats

  def toJsonSession(session: Session): JsonSession = JsonSession(session.user.id, session.token)
}
