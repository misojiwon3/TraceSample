package ai.t2x.trace.actor

import akka.actor.Actor
import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration._
import scala.language.postfixOps

trait AbstractActor extends Actor with StrictLogging {

  protected implicit val timeout: Timeout = 20 seconds

  protected val serviceName: String

}
