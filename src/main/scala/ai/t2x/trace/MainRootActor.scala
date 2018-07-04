package ai.t2x.trace

import akka.actor.{Actor, ActorRef, CoordinatedShutdown}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MainRootActor(mainContext: MainContext) extends Actor with StrictLogging{

  override def preStart(): Unit = {
    mainContext.registerNamedActor(TraceContext.rootActorName, self)
  }

  override def postStop(): Unit = {
    mainContext.unregisterNamedActor(TraceContext.rootActorName)

    CoordinatedShutdown(context.system).shutdownReason() match {
      case Some(reason) =>
        logger.error(s"shutdown reason = $reason")
        super.postStop()

      case _ =>
        logger.error(s"no shutdown reason")
        Future {
          System.exit(1)
        }
    }
  }

  override def receive: Receive = {
    case AddChildActor(name, childRef) => {
      mainContext.registerNamedActor(name, childRef)
    }

    case DeleteChildActor(name) => {
      mainContext.unregisterNamedActor(name)
    }
  }
}

case class AddChildActor(name: String, actorRef: ActorRef)
case class DeleteChildActor(name: String)
