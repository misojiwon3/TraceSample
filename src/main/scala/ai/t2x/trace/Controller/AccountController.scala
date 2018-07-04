package ai.t2x.trace.Controller

import akka.pattern.ask
import ai.t2x.trace.actor.ManagerActor
import ai.t2x.trace.{TraceConfig, TraceContext}
import ai.t2x.trace.common.{CreateAccountAPI, CtrlToManager, RequestTimeout, RouteInterface}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.server.{Directives, Route}
import akka.util.Timeout
import com.google.common.collect.ImmutableMap
import com.typesafe.scalalogging.StrictLogging
import ai.t2x.trace.common.Functions._

import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class AccountController(implicit system: ActorSystem, context: TraceContext) extends RouteInterface with RequestTimeout with Directives with StrictLogging {

  private val managerActor: ActorRef = system.actorOf(Props(new ManagerActor(context)))

  implicit val timeout: Timeout = requestTimeout(TraceConfig.config)

  override def route: Route =
    getAccount ~ createAccount

  def getAccount: Route = {
    path("account") {
      get {
        val tracer = initTracer("Controller")
        val span = tracer.buildSpan("GET /account").start() // GET /account span 시작
        println(span)
        println(span)
        println(span)
        println(span)
        println(span)

//        span.setTag("http.url", request.host + request.path)
        span.setTag("http.url", "글자")
//        span.log(ImmutableMap.of("API", s"${request.host} ${request.path} request"))
        span.log(ImmutableMap.of("API", s"localhost /account request"))


        onComplete(managerActor ? CtrlToManager("send to manager actor", span)) {
          case Success(r) =>
            println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            span.setTag("http.status_code", "200")
            span.finish()
            logSpanFinished("GET /account")
            tracer.close()
            logTracerClosed("Controller")
            complete("success")
          case Failure(e) =>
            tracer.close()
            logTracerClosed("Controller")
            complete(e.getMessage)
        }
      }
    }
  }

  def createAccount: Route = {
    path("account") {
      post {
        val tracer = initTracer("Controller")
        val span = tracer.buildSpan("POST /account").start() // POST /account span 시작

        case class SampleClass(any: String)

        entity(as[String]) { _ =>
          onComplete(managerActor ? CreateAccountAPI("Create Account API", span)) {
            case Success(r) => // account 등록 과정 모두 성공 하면
              span.setTag("http.status_code", "200")
              span.finish() // account 등록 span 종료
              logSpanFinished("POST /account")
              tracer.close()
              logTracerClosed("Controller")
              complete("success")

            case Failure(e) =>
              tracer.close()
              logTracerClosed("Controller")
              complete(e.getMessage)
          }
        }
      }
    }
  }
}

object AccountController {
  def apply(implicit system: ActorSystem, context: TraceContext): AccountController = new AccountController()
}
