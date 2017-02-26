package ru.restserver.database.asyncpool

import com.typesafe.config.{ ConfigFactory, ConfigException }

trait Configuration {
  implicit class WrappedConfig(config: com.typesafe.config.Config) {
    def get(key: String, orElse: => String) =
      try {
        config.getString(key)
      } catch {
        case ce: ConfigException => orElse
      }
  }

  protected lazy val configuration =
    try {
      ConfigFactory.load
    } catch {
      case ce: ConfigException => throw new AsyncPoolException("Configuration failed to load", ce)
    }

  private val configRoot = "asyncpool"

  def poolConfig(name: String) =
    try {
      configuration.getConfig(s"$configRoot.$name")
    } catch {
      case ce: ConfigException =>
        throw new AsyncPoolException(s"Missing configuration of pool $name", ce)
    }
}
