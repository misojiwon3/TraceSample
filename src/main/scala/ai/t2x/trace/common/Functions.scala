package ai.t2x.trace.common

import com.typesafe.scalalogging.StrictLogging
import io.jaegertracing.{Configuration, Tracer}
import io.jaegertracing.samplers.ConstSampler
import io.opentracing.{Span, SpanContext}

object Functions extends StrictLogging {

  def initTracer(serviceName: String): Tracer = {
    logTracerGenerated(serviceName)
    new Configuration(serviceName)
      .withReporter(
        Configuration.ReporterConfiguration.fromEnv()
          .withLogSpans(true)
          .withFlushInterval(1000)
          .withMaxQueueSize(10000)
          .withSender(
            Configuration.SenderConfiguration.fromEnv() // fromEnv() 기능 보기. 이게 이상한것 같다
              .withAgentHost("127.0.0.1") // localhost 로 하니깐 에러난다 (TUDPTransport cannot connect)
              .withAgentPort(5775)
          )
      )
      .withSampler(
        Configuration.SamplerConfiguration.fromEnv() // 각 명령 기능 파악 필요
          .withType(ConstSampler.TYPE)
          .withParam(1)
      )
      .getTracerBuilder.build()
  }

  def startChildSpan(tracer: Tracer, spanName: String, parentSpanContext: SpanContext): Span = {
    logSpanStarted(spanName)
    tracer.buildSpan(spanName) // .withStartTimestamp() 에 System.currentTimeMillis() 을 넣으면 그림이 이상해짐
      .asChildOf(parentSpanContext)
      .start()
  }

  def logTracerGenerated(serviceName: String): Unit =
    logger.info(s"[$serviceName] Tracer is generated.")

  def logTracerClosed(serviceName: String): Unit =
    logger.info(s"[$serviceName] Tracer is closed.")

  def logSpanStarted(spanName: String): Unit =
    logger.info(s"[$spanName] Span is started.")

  def logSpanFinished(spanName: String): Unit =
    logger.info(s"[$spanName] Span is finished.")
}
