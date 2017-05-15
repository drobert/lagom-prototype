package com.dannyrobert.lagomdemostream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import com.dannyrobert.lagomdemostream.api.LagomdemoStreamService
import com.dannyrobert.lagomdemo.api.LagomdemoService
import com.softwaremill.macwire._

class LagomdemoStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new LagomdemoStreamApplication(context) {
      override def serviceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new LagomdemoStreamApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[LagomdemoStreamService]
  )
}

abstract class LagomdemoStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[LagomdemoStreamService](wire[LagomdemoStreamServiceImpl])

  // Bind the LagomdemoService client
  lazy val lagomdemoService = serviceClient.implement[LagomdemoService]
}
