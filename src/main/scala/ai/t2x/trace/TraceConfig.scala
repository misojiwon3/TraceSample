package ai.t2x.trace

import com.typesafe.config.{Config, ConfigFactory}

object TraceConfig {
  lazy val config: Config = ConfigFactory.load()

  lazy val host: String = config.getString("trace.http.host")
  lazy val port: Int = config.getInt("trace.http.port")
}