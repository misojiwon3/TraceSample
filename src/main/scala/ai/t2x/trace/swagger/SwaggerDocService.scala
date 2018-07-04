package ai.t2x.trace.swagger

import ai.t2x.trace.TraceConfig
import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.model.Info
import io.swagger.models.ExternalDocs
import io.swagger.models.auth.BasicAuthDefinition

/**
  * 2018. 4. 16. - Created by Cho, Hee-Seung
  */
object SwaggerDocService extends SwaggerHttpService {
  override val apiClasses = Set(
  )
  override val host = s"${TraceConfig.host}:${TraceConfig.port}"
  override val info = Info(version = "1.0")
  override val externalDocs = Some(new ExternalDocs("Core Docs", "http://acme.com/docs"))
  override val securitySchemeDefinitions = Map("basicAuth" -> new BasicAuthDefinition())
  override val unwantedDefinitions = Seq("Function1", "Function1RequestContextFutureRouteResult")
}
