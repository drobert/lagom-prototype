package com.dannyrobert.lagomdemo.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

/**
  * The lagom-demo service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the LagomdemoService.
  */
trait LagomdemoService extends Service {

  /**
    * Example: curl http://localhost:9000/api/hello/Alice
    */
  def hello(id: String): ServiceCall[NotUsed, String]

  /**
    * Example: curl -H "Content-Type: application/json" -X POST -d '{"message":
    * "Hi"}' http://localhost:9000/api/hello/Alice
    */
  def useGreeting(id: String): ServiceCall[GreetingMessage, Done]

  def other(): ServiceCall[NotUsed, FooResponse]

  override final def descriptor = {
    import Service._
    // @formatter:off
    named("lagom-demo").withCalls(
      pathCall("/api/hello/:id", hello _),
      pathCall("/api/hello/:id", useGreeting _),
      call(other)
    ).withAutoAcl(true)
    // @formatter:on
  }
}

/**
  * The greeting message class.
  */
case class GreetingMessage(message: String)

object GreetingMessage {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val format: Format[GreetingMessage] = Json.format[GreetingMessage]
}

case class FooResponse(message: Int) extends AnyVal
object FooResponse {
  implicit val format: Format[FooResponse] = Json.format[FooResponse]
}
