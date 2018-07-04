package ai.t2x.trace

import ai.t2x.trace.actor._
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging

object Main extends App with StrictLogging {
  logger.info("Starting...")

  implicit val system: ActorSystem = ActorSystem("TRACE")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val traceMainContext: TraceContext = new TraceContext(system)


  traceMainContext.registerNamedActor(TraceContext.managerActor, system.actorOf(Props(new ManagerActor(traceMainContext))))
  traceMainContext.registerNamedActor(TraceContext.handlerActor, system.actorOf(Props(new HandlerActor(traceMainContext))))
  traceMainContext.registerNamedActor(TraceContext.daoActor, system.actorOf(Props(new DaoActor(traceMainContext))))
  traceMainContext.registerNamedActor(TraceContext.accountActor, system.actorOf(Props(new AccountActor(traceMainContext))))
  traceMainContext.registerNamedActor(TraceContext.authenticationActor, system.actorOf(Props(new AuthenticationActor(traceMainContext))))
  traceMainContext.registerNamedActor(TraceContext.rdbActor, system.actorOf(Props(new RdbActor(traceMainContext))))


  val httpServer = HttpServer(system, materializer, traceMainContext)
  httpServer.start
}
