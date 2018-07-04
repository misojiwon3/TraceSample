package ai.t2x.trace.actor

import akka.pattern.ask
import ai.t2x.trace.TraceContext
import ai.t2x.trace.common.{HandlerToDao, ManagerToHandler}
import ai.t2x.trace.common.Functions._
import akka.actor.{ActorRef, Props}

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class HandlerActor(ctx: TraceContext) extends AbstractActor {

  override protected val serviceName: String = "Handler"

  private val daoActor: ActorRef = ctx.actorSystem.actorOf(Props(new DaoActor(ctx)))

  override def receive: Receive = {
    case m: ManagerToHandler =>
      logger.info(s"${this.getClass.getSimpleName} received ${m.getClass.getSimpleName} message.")
      Thread.sleep(10L)

      val origin = sender

      val tracer = initTracer(serviceName)
      val span = startChildSpan(tracer, "handleRequest", m.span.context())

      val result = daoActor ? HandlerToDao("handle", span) // handler span 전달

      result.onComplete {
        case Success(r) =>
          Thread.sleep(10L)

          span.finish()
          logSpanFinished("handleRequest")
          tracer.close()
          logTracerClosed(serviceName)
          origin ! r
        case Failure(e) =>
          origin ! e.getMessage
      }
  }
}

object HandlerActor {
  def apply(mainContext: TraceContext): HandlerActor = new HandlerActor(mainContext)
//  val name = "handler"
//  def props(main: Main) = Props(new HandlerActor(main))
}
