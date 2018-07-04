package ai.t2x.trace.common

import akka.util.Timeout
import com.typesafe.config.Config

import scala.concurrent.duration._

/**
  * 2018. 4. 16. - Created by Cho, Hee-Seung
  */
trait RequestTimeout {
  def requestTimeout(config: Config): Timeout = {
    val t = config.getLong("trace.http.timeout")
    val d = Duration(t, "millisecond")
    FiniteDuration(d.length, d.unit)
  }
}
