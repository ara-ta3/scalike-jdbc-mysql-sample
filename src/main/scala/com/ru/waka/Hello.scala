package com.ru.waka

import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZonedDateTime}
import java.util.TimeZone

import org.json4s.ext.JavaTimeSerializers
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Formats, MappingException}
import scalikejdbc.{
  Commons2ConnectionPool,
  ConnectionPool,
  ConnectionPoolFactory,
  ConnectionPoolFactoryRepository,
  ConnectionPoolSettings,
  DBConnectionAttributes,
  DBSession,
  NamedDB,
  SQL,
  TimeZoneSettings
}

import scala.util.control.Exception._

object HelloConnectionPoolFactory extends ConnectionPoolFactory {
  override def apply(url: String,
                     user: String,
                     password: String,
                     settings: ConnectionPoolSettings): ConnectionPool =
    new HelloConnectionPool(url, user, password, settings)
}

class HelloConnectionPool(
    override val url: String,
    override val user: String,
    password: String,
    override val settings: ConnectionPoolSettings = ConnectionPoolSettings()
) extends Commons2ConnectionPool(url, user, password, settings) {
  override def connectionAttributes: DBConnectionAttributes = {
    val timeZoneSettings =
      Option(settings.timeZone).fold(TimeZoneSettings(conversionEnabled = false)) { timeZone =>
        TimeZoneSettings(
          conversionEnabled = false,
          java.util.TimeZone.getTimeZone(timeZone)
        )
      }
    DBConnectionAttributes(driverName = Option(settings.driverName),
                           timeZoneSettings = timeZoneSettings)
  }
}

object Hello {
  private val defaultTimeZoneSymbol = 'testDB1

  private val jstTimeZoneSymbol = 'testDB2

  Class.forName("com.mysql.cj.jdbc.Driver")

  def pool(symbol: Symbol, timeZone: Option[String] = None): Symbol = {
    ConnectionPool.add(
      symbol,
      "jdbc:mysql://127.0.0.1/test?characterEncoding=UTF-8",
      "root",
      "root",
      settings = ConnectionPoolSettings(
        connectionPoolFactoryName = "hello",
        timeZone = timeZone.orNull
      )
    )
    symbol
  }

  case class Request(time: ZonedDateTime)
  def main(args: Array[String]): Unit = {
    ConnectionPoolFactoryRepository.add("hello", HelloConnectionPoolFactory)
    val repository = new HelloRepository()
    val timeJson =
      """
        |{"time": "2019-02-11T12:00:00+09:00"}
      """.stripMargin
    val defaultTimeZonePool = pool(defaultTimeZoneSymbol, Some("UTC"))
    val jstTimeZonePool     = pool(jstTimeZoneSymbol, Some("Asia/Tokyo"))

    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"))
    NamedDB(defaultTimeZonePool) localTx { implicit session =>
      (for {
        zonedTime <- parseJsonToZonedDateTime(timeJson)
        _         <- dropTable()
        _         <- createTable()
        _ = println(zonedTime)
        _ = println(zonedTime.toLocalDateTime)
        _ = println(zonedTime.toLocalDateTime.toString)
        _  <- repository.put(zonedTime)
        _  <- repository.put(zonedTime.toLocalDateTime)
        _  <- repository.put(zonedTime.toLocalDateTime.toString)
        rs <- repository.fetch()
      } yield rs) match {
        case Right(_) =>
          session.connection.commit()
        case Left(th) =>
          session.connection.rollback()
          println(th)
      }
    }

    NamedDB(jstTimeZonePool) localTx { implicit session =>
      (for {
        zonedTime <- parseJsonToZonedDateTime(timeJson)
        _         <- createTable()
        _ = println(zonedTime)
        _ = println(zonedTime.toLocalDateTime)
        _ = println(zonedTime.toLocalDateTime.toString)
        _  <- repository.put(zonedTime)
        _  <- repository.put(zonedTime.toLocalDateTime)
        _  <- repository.put(zonedTime.toLocalDateTime.toString)
        rs <- repository.fetch()
      } yield rs) match {
        case Right(_) =>
          session.connection.commit()
        case Left(th) =>
          session.connection.rollback()
          println(th)
      }

    }
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Tokyo"))

    NamedDB(defaultTimeZonePool) localTx { implicit session =>
      (for {
        zonedTime <- parseJsonToZonedDateTime(timeJson)
        _         <- createTable()
        _ = println(zonedTime)
        _ = println(zonedTime.toLocalDateTime)
        _ = println(zonedTime.toLocalDateTime.toString)
        _  <- repository.put(zonedTime)
        _  <- repository.put(zonedTime.toLocalDateTime)
        _  <- repository.put(zonedTime.toLocalDateTime.toString)
        rs <- repository.fetch()
      } yield rs) match {
        case Right(_) =>
          session.connection.commit()
        case Left(th) =>
          session.connection.rollback()
          println(th)
      }

    }
    NamedDB(jstTimeZonePool) localTx { implicit session =>
      (for {
        zonedTime <- parseJsonToZonedDateTime(timeJson)
        _         <- createTable()
        _ = println(zonedTime)
        _ = println(zonedTime.toLocalDateTime)
        _ = println(zonedTime.toLocalDateTime.toString)
        _  <- repository.put(zonedTime)
        _  <- repository.put(zonedTime.toLocalDateTime)
        _  <- repository.put(zonedTime.toLocalDateTime.toString)
        rs <- repository.fetch()
      } yield rs) match {
        case Right(rs) =>
          session.connection.commit()
          println(rs)
        case Left(th) =>
          session.connection.rollback()
          println(th)
      }
    }
  }

  private implicit val jsonFormats: Formats = new DefaultFormats {
    override def dateFormatter: SimpleDateFormat =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX") {
        setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"))
      }
    override val strictOptionParsing: Boolean = true
  } ++ JavaTimeSerializers.all

  private def parseJsonToZonedDateTime(timeJson: String): Either[Throwable, ZonedDateTime] =
    (catching(classOf[MappingException]) either parse(timeJson).extract[Request]).map(_.time)

  private def createTable()(implicit session: DBSession): Either[Throwable, Boolean] =
    catching(classOf[Throwable]) either {
      SQL(
        """
        |CREATE TABLE IF NOT EXISTS foo (hello DateTime)
      """.stripMargin
      ).execute().apply()
    }

  private def dropTable()(implicit session: DBSession): Either[Throwable, Boolean] =
    catching(classOf[Throwable]) either {
      SQL(
        """
        |DROP TABLE IF EXISTS foo
      """.stripMargin
      ).execute().apply()
    }
}

class HelloRepository() {
  def put(hello: String)(implicit session: DBSession): Either[Throwable, Int] =
    catching(classOf[Throwable]) either
      SQL(
        """
          |INSERT INTO foo (hello) VALUES (?);
        """.stripMargin
      ).bind(hello).executeUpdate().apply()

  def put(hello: LocalDateTime)(implicit session: DBSession): Either[Throwable, Int] =
    catching(classOf[Throwable]) either
      SQL(
        """
          |INSERT INTO foo (hello) VALUES (?);
        """.stripMargin
      ).bind(hello).executeUpdate().apply()

  def put(hello: ZonedDateTime)(implicit session: DBSession): Either[Throwable, Int] =
    catching(classOf[Throwable]) either
      SQL(
        """
          |INSERT INTO foo (hello) VALUES (?);
        """.stripMargin
      ).bind(hello).executeUpdate().apply()

  def fetch()(implicit session: DBSession): Either[Throwable, Seq[Map[String, Any]]] =
    catching(classOf[Throwable]) either {
      SQL(
        """
          |SELECT hello FROM foo;
        """.stripMargin
      ).map(_.toMap()).list().apply()
    }

}
