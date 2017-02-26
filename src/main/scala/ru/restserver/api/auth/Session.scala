package ru.restserver.api.auth

import java.util.UUID

case class Session(id: Long, user: User, token: String = UUID.randomUUID().toString.replaceAll("-", ""))
