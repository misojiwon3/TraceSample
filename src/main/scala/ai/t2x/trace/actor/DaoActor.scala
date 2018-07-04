package ai.t2x.trace.actor

import ai.t2x.trace.TraceContext
import ai.t2x.trace.common.HandlerToDao
import ai.t2x.trace.common.Functions._
import akka.actor.Props

class DaoActor(ctx: TraceContext) extends AbstractActor {

  override protected val serviceName: String = "DAO"

  override def receive: Receive = {
    case m: HandlerToDao =>
      logger.info(s"${this.getClass.getSimpleName} received ${m.getClass.getSimpleName} message.")
      Thread.sleep(10L)

      val origin = sender

      val tracer = initTracer(serviceName)
      val span = startChildSpan(tracer, "daoRequest", m.span.context())

      Thread.sleep(10L)

      span.finish() // timestamp 를 넣을때 ui가 왜 깨지는지 확인
      logSpanFinished("daoRequest")
      tracer.close()
      logTracerClosed(serviceName)
      origin ! "response message"
  }
}

object DaoActor {
  val name = "dao"

  def props(mainContext: TraceContext) = Props(new DaoActor(mainContext))
}