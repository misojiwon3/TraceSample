/**
  * 2018. 4. 13. - Created by Kwon, Yeong Eon
  */


import sbt._

object Dependencies {

  object Versions {
    val scala = "2.12.5"
    val akka = "2.5.11"
    val akka_http = "10.1.1"
  }

  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % Versions.akka force(),
    "com.typesafe.akka" %% "akka-remote" % Versions.akka force(),
    "com.typesafe.akka" %% "akka-http" % Versions.akka_http,
    "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akka_http,
  "com.typesafe.play" %% "play-logback" % "2.6.11"
  )

  val jaeger = Seq(
    "io.jaegertracing" % "jaeger-core" % "0.29.0"
  )

  val swagger = Seq(
    "io.swagger" % "swagger-jaxrs" % "1.5.18",
    "com.github.swagger-akka-http" %% "swagger-akka-http" % "0.14.0"
  )

  val jwt = Seq(
    "com.pauldijou" %% "jwt-core" % "0.16.0"
  )

  val cors = Seq(
    "ch.megard" %% "akka-http-cors" % "0.3.0"
  )

  val slick = Seq(
    "com.typesafe.slick" %% "slick" % "3.2.3",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3"
  )

  val mariadb = Seq(
    // https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
    "org.mariadb.jdbc" % "mariadb-java-client" % "2.2.5"
  )

  val logging = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3" ,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
  )

  val spray = Seq(
    "io.spray" %%  "spray-json" % "1.3.3"
  )

  val mqtt = Seq(
    "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.2.0"
  )

  val t2xlib = Seq(
    "ai.t2x" % "lib-common_2.12" % "1.0.0-SNAPSHOT",
    "ai.t2x" % "lib-verinfo_2.12" % "1.0.0-SNAPSHOT"
  )
}
