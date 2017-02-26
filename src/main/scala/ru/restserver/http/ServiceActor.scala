package ru.restserver.http

import akka.actor.ActorLogging
import spray.http.MediaTypes._
import spray.routing.HttpServiceActor
import spray.routing.Route

class ServiceActor(apiRoute: Route, appRoute: Route) extends HttpServiceActor with ActorLogging with DefaultExceptionHandler with CORSSupport {
  val route: Route = cors {
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>RESTful Web Services are working...</h1>
                <h2><a href="app">Web application</a></h2>
              </body>
            </html>
          }
        }
      }
    } ~ pathPrefix("api") {
      apiRoute
    } ~ appRoute
  }

  override def receive: Receive = runRoute(route)

  override def preStart(): Unit = log.info("Service actor started.")

  override def postStop(): Unit = log.info("Service actor stopped.")
}