package ai.t2x.trace

import akka.actor.{ActorRef, ActorSystem}

trait MainContext {

  val actorSystem: ActorSystem
  val rootActor: ActorRef

  def registerNamedActor(name: String, actorRef: ActorRef): Unit

  def unregisterNamedActor(name: String): Option[ActorRef]

  def namedActor(name: String): Option[ActorRef]

  def instanceOf[T](clazz: Class[T]): T

}
