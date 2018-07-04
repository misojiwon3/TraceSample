package ai.t2x.trace.actor

import ai.t2x.trace.TraceContext
import ai.t2x.trace.common.{CreateAccountReq, CreateAuthenticationReq, ReadAccountReq}
import ai.t2x.trace.common.Functions._
import com.google.common.collect.ImmutableMap

class RdbActor(ctx: TraceContext) extends AbstractActor {
  override protected val serviceName: String = "RDB"

  override def receive: Receive = {
    case m: ReadAccountReq =>
      logger.info(s"${this.getClass.getSimpleName} received ${m.getClass.getSimpleName} message.")

      Thread.sleep(10L)

      val tracer = initTracer(serviceName)
      val span = startChildSpan(tracer, "ReadAccount", m.span.context())

      val s = System.currentTimeMillis()
      Thread.sleep(10L) // read account 작업 중
    val e = System.currentTimeMillis()

      span.setTag("sql.query", "SELECT * FROM account WHERE id = 33;")
      span.log(ImmutableMap.of("query.time", (e - s).toString + "ms"))
      span.finish()
      logSpanFinished("ReadAccount")
      tracer.close()
      logTracerClosed(serviceName)
      sender ! "read account"

    case m: CreateAccountReq =>
      logger.info(s"${this.getClass.getSimpleName} received ${m.getClass.getSimpleName} message.")

      Thread.sleep(10L)

      val tracer = initTracer(serviceName)
      val span = startChildSpan(tracer, "CreateAccount", m.span.context())

      val s = System.currentTimeMillis()
      Thread.sleep(15L) // create account 작업 중
    val e = System.currentTimeMillis()

      span.setTag("sql.query", "INSERT INTO account (id, username) VALUES (1, 'myname');")
      span.log(ImmutableMap.of("query.time", (e - s).toString + "ms"))
      span.finish()
      logSpanFinished("CreateAccount")
      tracer.close()
      logTracerClosed(serviceName)
      sender ! "create account"

    case m: CreateAuthenticationReq =>
      logger.info(s"${this.getClass.getSimpleName} received ${m.getClass.getSimpleName} message.")

      Thread.sleep(10L)

      val tracer = initTracer(serviceName)
      val span = startChildSpan(tracer, "CreateAuthentication", m.span.context())

      val s = System.currentTimeMillis()
      Thread.sleep(15L) // create authentication 작업 중
    val e = System.currentTimeMillis()

      span.setTag("sql.query", "INSERT INTO authentication (id, authname) VALUES (2, 'myauth');")
      span.log(ImmutableMap.of("query.time", (e - s).toString + "ms"))
      span.finish()
      logSpanFinished("CreateAuthentication")
      tracer.close()
      logTracerClosed(serviceName)
      sender ! "create authentication"
  }
}

object RdbActor {
  def apply(mainContext: TraceContext): RdbActor = new RdbActor(mainContext)
}