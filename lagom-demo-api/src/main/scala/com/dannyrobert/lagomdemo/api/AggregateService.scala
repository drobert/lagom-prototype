package com.dannyrobert.lagomdemo.api

import java.time.LocalDate

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api._
import com.lightbend.lagom.scaladsl.api.deser.PathParamSerializer
import com.dannyrobert.lagomdemo.model._
import play.api.libs.json._

object AggregateService {
  import Aggregate._

  implicit val localDateMapKeyFormat: Format[Map[LocalDate, Map[Sku, Total]]] = new Format[Map[LocalDate, Map[Sku, Total]]] {
    override def reads(json: JsValue): JsResult[Map[LocalDate, Map[Sku, Total]]] =
      Reads.mapReads[Map[Sku, Total]].map{_.collect{case (day, results) => LocalDate.parse(day) -> results}}.reads(json)

    override def writes(dmap: Map[LocalDate, Map[Sku, Total]]): JsValue = {
      val stringKeys = dmap.map { case (day, skutotal) => day.toString -> skutotal }
      Writes.mapWrites[Map[Sku, Total]].writes(stringKeys)
    }
  }
}

trait AggregateService extends Service {
  import AggregateService._

  implicit val posParamSer: PathParamSerializer[PointOfSale] =
    PathParamSerializer.required[PointOfSale]("pos")(PointOfSale.apply)(_.pos)

  def record(): ServiceCall[Aggregate, Done]

  def fetchDays(profileId: Int, pos: PointOfSale, days: Int):
    ServiceCall[NotUsed, Map[LocalDate, Map[Sku, Total]]]

  def fetchAll(profileId: Int, pos: PointOfSale):
    ServiceCall[NotUsed, Map[LocalDate, Map[Sku, Total]]]

  override def descriptor: Descriptor = {
    import Service._
    named("aggregates").withCalls(
      pathCall("/agg", record _),
      pathCall("/agg/:profileId/:pos", fetchAll _),
      pathCall("/agg/:profileId/:pos/:days", fetchDays _)
    )
    .withAutoAcl(true)
  }
}


