package ai.t2x.trace

import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.scalalogging.StrictLogging

import scala.collection.mutable

class TraceContext(system: ActorSystem) extends MainContext with StrictLogging {

  private val namedActorTable = mutable.Map[String, ActorRef]()

  override val actorSystem: ActorSystem = system
  override val rootActor: ActorRef = system.actorOf(Props(new MainRootActor(this)), TraceContext.rootActorName)

  override def registerNamedActor(name: String, actorRef: ActorRef): Unit = {
    logger.debug(s"Registered named actor: $name")
    namedActorTable.put(name, actorRef)
  }

  override def unregisterNamedActor(name: String): Option[ActorRef] = {
    logger.debug(s"Unregistered named actor: $name")
    namedActorTable.remove(name)
  }

  override def namedActor(name: String): Option[ActorRef] = {
    if (namedActorTable.contains(name))
      namedActorTable.get(name)
    else {
      logger.debug(s"actor[$name] is not in table")

      None
    }
  }

  override def instanceOf[T](clazz: Class[T]): T = ???
}

object TraceContext {
  val rootActorName = "root"

  val managerActor = "manager"
  val handlerActor = "handler"
  val daoActor = "dao"

  val accountActor = "account"
  val authenticationActor = "authentication"
  val rdbActor = "rdb"
}