package ai.t2x.trace.common

import akka.http.scaladsl.server.Route

/**
  * 2018. 4. 13. - Created by Cho, Hee-Seung
  */
trait RouteInterface {
  def route: Route
}
