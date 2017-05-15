package com.dannyrobert.lagomdemostream.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.dannyrobert.lagomdemostream.api.LagomdemoStreamService
import com.dannyrobert.lagomdemo.api.LagomdemoService

import scala.concurrent.Future

/**
  * Implementation of the LagomdemoStreamService.
  */
class LagomdemoStreamServiceImpl(lagomdemoService: LagomdemoService) extends LagomdemoStreamService {
  def stream = ServiceCall { hellos =>
    Future.successful(hellos.mapAsync(8)(lagomdemoService.hello(_).invoke()))
  }
}
