package com.dannyrobert.lagomdemo.model

sealed abstract class PointOfSale(val pos: String)

case object Amazon extends PointOfSale("Amazon")
case object Walmart extends PointOfSale("Walmart")

object PointOfSale {
  import play.api.libs.json._

  def apply(pos: String): PointOfSale = pos match {
    case Amazon.pos => Amazon
    case Walmart.pos => Walmart
  }

  implicit val format: Format[PointOfSale] = new Format[PointOfSale] {
    override def reads(json: JsValue) = json match {
      case JsString(Amazon.pos) => JsSuccess(Amazon)
      case JsString(Walmart.pos) => JsSuccess(Walmart)
      case x => JsError(x.toString())
    }

    override def writes(pos: PointOfSale) = JsString(pos.pos)
  }
}