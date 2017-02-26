package ru.restserver.api

case class ServiceException(message: String) extends RestServerException(message)
