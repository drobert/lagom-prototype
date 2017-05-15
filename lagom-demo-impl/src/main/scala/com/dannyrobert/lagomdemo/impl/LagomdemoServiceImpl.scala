package com.dannyrobert.lagomdemo.impl

import java.util.concurrent.atomic.AtomicInteger

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.dannyrobert.lagomdemo.api.{FooResponse, LagomdemoService}

import scala.concurrent.Future

/**
  * Implementation of the LagomdemoService.
  */
class LagomdemoServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends LagomdemoService {
  private final val counter = new AtomicInteger(0)

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the lagom-demo entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomdemoEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id, None))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the lagom-demo entity for the given ID.
    val ref = persistentEntityRegistry.refFor[LagomdemoEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }

  override def other(): ServiceCall[NotUsed, FooResponse] = ServiceCall(_ => Future.successful(FooResponse(counter.incrementAndGet())))
}
