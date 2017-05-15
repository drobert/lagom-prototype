package com.dannyrobert.lagomdemo.impl

import java.time.{LocalDate, Period}

import scala.concurrent.{ExecutionContext, Future}
import akka.{Done, NotUsed}
import com.dannyrobert.lagomdemo.api.AggregateService
import com.dannyrobert.lagomdemo.model._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable.Seq

class AggregateServiceImpl(implicit ec: ExecutionContext) extends AggregateService {

  // TODO: DI
  val db: Storage = new MapStorage

  override def record(): ServiceCall[Aggregate, Done] = ServiceCall { agg =>
    Future.successful(db.store(agg)).map(_ => Done)
  }

  override def fetchDays(profileId: Int, pos: PointOfSale, days: Int): ServiceCall[NotUsed, Map[LocalDate, Map[Sku, Total]]] = ServiceCall { _ =>
    Future.successful(db.fetchDateRange(Key(profileId, pos), LocalDate.now.minusDays(days), days))
  }

  override def fetchAll(profileId: Int, pos: PointOfSale): ServiceCall[NotUsed, Map[LocalDate, Map[Sku, Total]]] = ServiceCall { _ =>
    Future.successful(db.fetchAll(Key(profileId, pos)))
  }

}

object AggregateSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[PointOfSale],
    JsonSerializer[Sku],
    JsonSerializer[Total],
    JsonSerializer[Key],
    JsonSerializer[Aggregate]
  )
}

sealed trait AggregateCommand[R] extends ReplyType[R]
case class RecordedEntities(val i: Int) extends AggregateCommand[Done]

trait Storage {
  def store(agg: Aggregate): Int

  def fetchAll(key: Key): Map[LocalDate, Map[Sku, Total]]

  def fetchDateRange(key: Key, startDate: LocalDate, days: Int): Map[LocalDate, Map[Sku, Total]]
}

class MapStorage extends Storage {
  import scala.collection.concurrent

  private val store: concurrent.Map[Key, concurrent.Map[LocalDate, Map[Sku, Total]]] = concurrent.TrieMap()

  override def store(agg: Aggregate): Int = {
    store.putIfAbsent(agg.key, concurrent.TrieMap(agg.day -> agg.totals)) match {
      case Some(_) => 1 // first post!
      case None => {
        store(agg.key).putIfAbsent(agg.day, agg.totals) match {
          case Some(_) => 1
          case _ => 0
        }
      }
    }
  }

  override def fetchAll(key: Key): Map[LocalDate, Map[Sku, Total]] = store.get(key).map(_.toMap).getOrElse(Map())

  override def fetchDateRange(key: Key, startDate: LocalDate, days: Int): Map[LocalDate, Map[Sku, Total]] =
    fetchAll(key).filterKeys(Period.between(startDate, _).getDays < days).mapValues(_.toMap)
}
