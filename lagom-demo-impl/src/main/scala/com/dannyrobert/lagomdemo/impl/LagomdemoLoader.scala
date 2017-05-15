package com.dannyrobert.lagomdemo.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.dannyrobert.lagomdemo.api.{AggregateService, LagomdemoService}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.softwaremill.macwire._

import scala.collection.immutable

class LagomdemoLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomdemoApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomdemoApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[LagomdemoService],
    readDescriptor[AggregateService]
  )
}

abstract class LagomdemoApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  //override lazy val lagomServer = serverFor[LagomdemoService](wire[LagomdemoServiceImpl])
  override lazy val lagomServer = serverFor[AggregateService](wire[AggregateServiceImpl])

  // Register the JSON serializer registry
  //override lazy val jsonSerializerRegistry = LagomdemoSerializerRegistry
  override lazy val jsonSerializerRegistry = LagomdemoSerializerRegistry ++ AggregateSerializerRegistry

  // Register the lagom-demo persistent entity
  persistentEntityRegistry.register(wire[LagomdemoEntity])
}
