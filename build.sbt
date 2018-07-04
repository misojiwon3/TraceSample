import com.typesafe.sbt.packager.MappingsHelper.directory
import com.typesafe.sbt.SbtNativePackager.autoImport.NativePackagerHelper._
import sbt.Keys.name

name := "trace"

version := "trace-1.0"

scalaVersion := "2.12.2"

scalacOptions ++= Seq("-feature" /*, "-deprecation" */)

//unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

publishArtifact in (Compile, packageSrc) := false

// resolving libraries stored on nexus
resolvers ++= Seq(
  "Nexus: maven-snapshots" at "http://192.168.7.150:8081/repository/maven-snapshots",
  "Nexus: maven-releases" at "http://192.168.7.150:8081/repository/maven-releases"
)

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

lazy val trace = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "trace",
    libraryDependencies ++= Dependencies.logging
      ++ Dependencies.akka
      ++ Dependencies.swagger
      ++ Dependencies.cors
      ++ Dependencies.jaeger
      ++ Dependencies.spray
  )
  .settings(
    publish := (), // no publish
    publishLocal := (),
    publishArtifact := false
  )
  .settings(
    mappings in Universal ++= directory("bin"),
    mappings in Universal ++= directory("conf").filter { case (file, _) =>
      file.getName match {
        case _ => false
      }
    },
    packageName in Universal := s"trace-${(version in ThisBuild).value}",
    executableScriptName := "trace",
    bashScriptConfigLocation := Some("conf/process.ini")
  )
  .settings(
    mainClass in Compile := Some("ai.t2x.trace.Main"),
    javaOptions in run ++= Seq(
      "-Dconfig.file=./conf/application.conf",
      "-Dlogback.configurationFile=./conf/logback.xml"
    ),
    fork in run := true
  )
  .enablePlugins(DockerPlugin)
  .enablePlugins(AshScriptPlugin)
  .settings(
    mappings in Docker += file("conf/application.conf") -> "/opt/trace/conf/application.conf",
    mappings in Docker += file("conf/process.ini") -> "/opt/trace/conf/process.ini",
    packageName in Docker := "trace",
    dockerBaseImage := "openjdk:jre-alpine",
    defaultLinuxInstallLocation in Docker := "/opt/trace",
    bashScriptConfigLocation := Some("/opt/trace/conf/process.ini"),
    dockerEntrypoint := Seq("/opt/trace/bin/trace"),
    dockerRepository := Some("192.168.7.150:8083"),
    dockerExposedPorts := Seq(9005)
  )
