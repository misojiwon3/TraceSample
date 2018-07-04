package ai.t2x.trace.actor

import akka.pattern.ask
import ai.t2x.trace.TraceContext
import ai.t2x.trace.common.{CreateAccountAPI, CreateAccountReq, ForwardCreateAuthenticationReq, ReadAccountReq}
import ai.t2x.trace.common.Functions._
import akka.actor.{ActorRef, Props}

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class AccountActor(ctx: TraceContext) extends AbstractActor {

  override protected val serviceName: String = "Account"

  private val rdbActor: ActorRef = ctx.actorSystem.actorOf(Props(new RdbActor(ctx)))
  private val authenticationActor: ActorRef = ctx.actorSystem.actorOf(Props(new AuthenticationActor(ctx)))

  override def receive: Receive = {
    case m: CreateAccountAPI =>
      logger.info(s"${this.getClass.getSimpleName} received ${m.getClass.getSimpleName} message.")

      val origin = sender

      val tracer = initTracer(serviceName)
      val readAccountSpan = startChildSpan(tracer, "RequestReadAccount", m.span.context())

      val readAccountResult = rdbActor ? ReadAccountReq("request check account", readAccountSpan) // account span 전달

      readAccountResult.onComplete {
        case Success(_) => // read account 성공 하면
          Thread.sleep(5L)
          readAccountSpan.finish() // read account span 종료
          logSpanFinished("ReadAccountSpan")

          val createAccountSpan = startChildSpan(tracer, "RequestCreateAccount", m.span.context())
          val createAccountResult = rdbActor ? CreateAccountReq("request create account", createAccountSpan) // account span 전달

          createAccountResult.onComplete {
            case Success(_) => // create account 성공 하면
              Thread.sleep(5L)
              createAccountSpan.finish() // create account span 종료
              logSpanFinished("RequestCreateAccount")

              val forwardCreateAuthenticationSpan = startChildSpan(tracer, "ForwardRequestCreateAuthentication", m.span.context())
              val forwardCreateAuthenticationResult = authenticationActor ? ForwardCreateAuthenticationReq("request to forward create authentication", forwardCreateAuthenticationSpan) // authentication span 전달

              forwardCreateAuthenticationResult.onComplete {
                case Success(r) => // create authentication 성공 하면
                  Thread.sleep(5L)
                  forwardCreateAuthenticationSpan.finish() // create authentication span 종료
                  logSpanFinished("ForwardRequestCreateAuthentication")
                  tracer.close()
                  logTracerClosed(serviceName)

                  origin ! r // 초기 sender 로 응답 보냄

                case Failure(e) =>
                  tracer.close()
                  logTracerClosed(serviceName)
                  origin ! e.getMessage
              }
            case Failure(e) =>
              origin ! e.getMessage
          }
        case Failure(e) =>
          origin ! e.getMessage
      }
  }
}

object AccountActor {
  def apply(mainContext: TraceContext): AccountActor = new AccountActor(mainContext)
}