package ru.restserver.api

import com.typesafe.config.ConfigFactory

object Configuration {
  private lazy val configuration = ConfigFactory.load()
  object servletContainer {
    private lazy val servletContainer = configuration.getConfig("servlet-container")
    lazy val jndiPool = servletContainer.getString("jndi-pool")
  }
  object auth {
    private lazy val auth = configuration.getConfig("auth")
    object db {
      private lazy val db = auth.getConfig("db")
      lazy val serverHost = db.getString("server-name")
      lazy val serverInstance = db.getString("server-instance")
      lazy val url = db.getString("url")
    }
    object tokenAuthenticator {
      private lazy val tokenAuthenticator = auth.getConfig("token-authenticator")
      lazy val header = tokenAuthenticator.getString("header")
      lazy val queryStringParameter = tokenAuthenticator.getString("query-string-parameter")
      lazy val integrationToken = tokenAuthenticator.getString("integration-token")
    }
  }
}
