package ru.restserver.http.routes.app

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.util.Timeout
import spray.http.StatusCodes
import spray.routing.{ Directives, Route }

class AppRoute(implicit system: ActorSystem, timeout: Timeout, log: LoggingAdapter) extends Directives {
  import StatusCodes._
  val route: Route = pathPrefix("app") {
    pathSingleSlash {
      getFromResource("public/index.html")
    } ~
      pathEnd {
        requestUri { uri =>
          redirect(s"./app/", MovedPermanently)
        }
      } ~
      compressResponse() {
        getFromResourceDirectory("public")
      }
  }
}