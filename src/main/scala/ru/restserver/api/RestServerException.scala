package ru.restserver.api

class RestServerException(message: String, cause: Throwable) extends RuntimeException(message, cause) with Serializable {
  def this(msg: String) = this(msg, null)
}

class ConfigurationException(message: String, cause: Throwable) extends RestServerException(message, cause) {
  def this(msg: String) = this(msg, null)
}