package ai.t2x.trace.common

import io.opentracing.Span

trait SpanMsg {
  def span: Span
}

case class CtrlToManager(message: String, override val span: Span) extends SpanMsg

case class ManagerToHandler(message: String, override val span: Span) extends SpanMsg

case class HandlerToDao(message: String, override val span: Span) extends SpanMsg


case class CreateAccountAPI(message: String, override val span: Span) extends SpanMsg

case class ReadAccountReq(message: String, override val span: Span) extends SpanMsg

case class CreateAccountReq(message: String, override val span: Span) extends SpanMsg

case class ForwardCreateAuthenticationReq(message: String, override val span: Span) extends SpanMsg

case class CreateAuthenticationReq(message: String, override val span: Span) extends SpanMsg


case class Response(message: String)