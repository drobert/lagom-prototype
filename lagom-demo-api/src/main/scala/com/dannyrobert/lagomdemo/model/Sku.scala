package com.dannyrobert.lagomdemo.model

case class Sku(value: String) extends AnyVal
object Sku {
  import play.api.libs.json._
  implicit val skuWrite = Writes[Sku] { sku => JsString(sku.value) }
  implicit val skuReads = Reads.of[String].map(Sku.apply)
}
