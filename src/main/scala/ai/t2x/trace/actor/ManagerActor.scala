package ai.t2x.trace.actor

import akka.pattern.ask
import ai.t2x.trace.TraceContext
import ai.t2x.trace.common.{CtrlToManager, ManagerToHandler}
import akka.actor.{ActorRef, Props}
import ai.t2x.trace.common.Functions._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class ManagerActor(ctx: TraceContext) extends AbstractActor {

  override protected val serviceName: String = "Manager"

  private val handlerActor: ActorRef = ctx.actorSystem.actorOf(Props(new HandlerActor(ctx)))

  override def receive: Receive = {
    case m: CtrlToManager =>
      logger.info(s"${this.getClass.getSimpleName} received ${m.getClass.getSimpleName} message.")
      Thread.sleep(10L)

      val origin = sender

      val tracer = initTracer(serviceName)
      val span = startChildSpan(tracer, "manageRequest", m.span.context())

      val result = handlerActor ? ManagerToHandler("send to handler actor", span) // manager span 전달

      result.onComplete {
        case Success(r) =>
          Thread.sleep(10L)

          span.finish()
          logSpanFinished("manageRequest")
          tracer.close()
          logTracerClosed(serviceName)
          origin ! r
        case Failure(e) =>
          origin ! e.getMessage
      }
  }
}

object ManagerActor {
  def apply(mainContext: TraceContext): ManagerActor = new ManagerActor(mainContext)
}