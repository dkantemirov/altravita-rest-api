package ru.restserver.database.models

import ru.restserver.api.auth.User

import scala.concurrent.Future

class UsersModel {
  def getByLogin(login: String): Future[User] = {
    ???
  }
}
