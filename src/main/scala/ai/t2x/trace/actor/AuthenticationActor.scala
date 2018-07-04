package ai.t2x.trace.actor

import akka.pattern.ask
import ai.t2x.trace.TraceContext
import ai.t2x.trace.common.{CreateAuthenticationReq, ForwardCreateAuthenticationReq}
import akka.actor.{ActorRef, Props}
import ai.t2x.trace.common.Functions._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class AuthenticationActor(ctx: TraceContext) extends AbstractActor {

  override protected val serviceName: String = "Authentication"

  private val rdbActor: ActorRef = ctx.actorSystem.actorOf(Props(new RdbActor(ctx)))

  override def receive: Receive = {
    case m: ForwardCreateAuthenticationReq =>
      logger.info(s"${this.getClass.getSimpleName} received ${m.getClass.getSimpleName} message.")

      val origin = sender

      Thread.sleep(10L)

      val tracer = initTracer(serviceName)
      val createAuthenticationSpan = startChildSpan(tracer, "RequestCreateAuthentication", m.span.context())

      val result = rdbActor ? CreateAuthenticationReq("RequestCreateAuthentication", createAuthenticationSpan)

      result.onComplete {
        case Success(r) =>
          Thread.sleep(15L) // create authentication 작업 중
          createAuthenticationSpan.finish()
          logSpanFinished("RequestCreateAuthentication")
          tracer.close()
          logTracerClosed(serviceName)
          origin ! r
        case Failure(e) =>
          tracer.close()
          logTracerClosed(serviceName)
          origin ! e.getMessage
      }

  }
}

object AuthenticationActor {
  def apply(mainContext: TraceContext): AuthenticationActor = new AuthenticationActor(mainContext)
}