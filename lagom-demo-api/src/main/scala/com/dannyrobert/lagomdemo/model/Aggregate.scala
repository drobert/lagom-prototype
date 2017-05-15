package com.dannyrobert.lagomdemo.model

import java.time.LocalDate

case class Key(profileId: Int, pos: PointOfSale)
object Key {
  import play.api.libs.json._

  implicit val format = Json.format[Key]
}

case class Total(val category: String, val total: BigDecimal)
object Total {
  import play.api.libs.json._
  implicit val format = Json.format[Total]
}

case class Aggregate(
  key: Key,
  day: LocalDate,
  totals: Map[Sku, Total]
)

object Aggregate {
  import play.api.libs.json._


  implicit val skuTotalReadsFormat = new Format[Map[Sku, Total]] {
    override def reads(json: JsValue): JsResult[Map[Sku, Total]] =
      Reads.mapReads[Total].map{_.collect{case (key, total) => Sku(key) -> total} }.reads(json)

    override def writes(st: Map[Sku, Total]): JsValue = {
      val stringKeys = st.map{ case (sku, total) => sku.value -> total }
      Writes.mapWrites[Total].writes(stringKeys)
    }
  }

  implicit val format = Json.format[Aggregate]
}

