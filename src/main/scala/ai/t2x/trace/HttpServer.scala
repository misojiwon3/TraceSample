package ai.t2x.trace

import ai.t2x.trace.Controller.AccountController
import ai.t2x.trace.swagger.SwaggerDocService
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class HttpServer(implicit system: ActorSystem, materializer: ActorMaterializer, context: TraceContext) extends StrictLogging {
  private var bindingFuture: Future[ServerBinding] = _
  private var routes: Route = _

  def start: Unit = {
    import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

    routes = cors() (swaggerResourceRoute ~ SwaggerDocService.routes ~ AccountController(system, context).route)

    logger.info("----------------------------")
    logger.info(s"host=${TraceConfig.host}")
    logger.info(s"port=${TraceConfig.port}")
    logger.info("----------------------------")

    bindingFuture = Http().bindAndHandle(routes, TraceConfig.host, TraceConfig.port)
    bindingFuture.onComplete {
      case Success(b: ServerBinding) => logger.info(s"Succeed to start Http Server. Listening on: ${b.localAddress}")
      case Failure(e: Throwable) => logger.error(s"Failed to start Http Server. error: ${e.getMessage}")
    }
  }

  private def swaggerResourceRoute: Route =
    get {
      path("swagger") {
        getFromResource("swagger-ui/index.html")
      } ~ getFromResourceDirectory("swagger-ui")
    }
}

object HttpServer {
  def apply(implicit system: ActorSystem, materializer: ActorMaterializer, context: TraceContext): HttpServer = new HttpServer()
}
