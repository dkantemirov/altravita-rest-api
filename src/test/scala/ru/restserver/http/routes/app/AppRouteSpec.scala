package ru.restserver.http.routes.app

import ru.restserver.http.DefaultExceptionHandler
import ru.restserver.utils.ServiceRouteSpec
import spray.http._
import spray.routing.Route

class AppRouteSpec extends ServiceRouteSpec {
  import StatusCodes._

  class Context extends DefaultExceptionHandler {
    val appRouter = new AppRoute
    val route: Route = handleExceptions(exceptionHandler) {
      appRouter.route
    }
  }

  "App route" when {
    "the resource exists" should produce {
      "index.html in redirected request" in new Context {
        redirectToIndexFile(route) {
          status shouldBe MovedPermanently
          mediaType shouldBe MediaTypes.`text/html`
          responseAs[String] === """The request, and all future requests should be repeated using <a href="/app/">this URI</a>."""
        }
      }
      "index.html" in new Context {
        indexFile(route) {
          status shouldBe OK
          mediaType shouldBe MediaTypes.`text/html`
          body.toString() should include ("<html>")
        }
      }
      "main.js" in new Context {
        mainFile(route) {
          status shouldBe OK
          mediaType shouldBe MediaTypes.`application/javascript`
          responseAs[String]
        }
      }
    }
  }

  private def redirectToIndexFile(route: Route)(action: => Unit) = {
    Get("/app") ~> route ~> check(action)
  }
  private def indexFile(route: Route)(action: => Unit) = {
    Get("/app/") ~> route ~> check(action)
  }
  private def mainFile(route: Route)(action: => Unit) = {
    Get("/app/main.js") ~> route ~> check(action)
  }
}