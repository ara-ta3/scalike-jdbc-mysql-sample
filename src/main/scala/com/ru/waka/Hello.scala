package com.ru.waka

import java.time.LocalDateTime

import scalikejdbc.{ConnectionPool, DBSession, NamedDB, SQL}

import scala.util.control.Exception._

object Hello {
  private val connectionSymbol = 'testDB

  Class.forName("com.mysql.cj.jdbc.Driver")

  ConnectionPool.add(connectionSymbol, "jdbc:mysql://localhost/test?characterEncoding=UTF-8", "root", "")

  val repository = new HelloRepository(connectionSymbol)

  def main(args: Array[String]): Unit = {
    val time = LocalDateTime.now().toString
    NamedDB(connectionSymbol) localTx {implicit  session =>
      (
        for {
          _ <- createTable()
          _ <- repository.put(time)
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

  def createTable()(implicit session: DBSession): Either[Throwable, Boolean] = catching(classOf[Throwable]) either {
    SQL(
      """
        |CREATE TABLE IF NOT EXISTS foo (hello varchar(100))
      """.stripMargin
    ).execute().apply()
  }
}

class HelloRepository(connectionName: Symbol) {
  def put(hello: String)(implicit session: DBSession): Either[Throwable, Int] =
    catching(classOf[Throwable]) either
      SQL(
        """
          |INSERT INTO foo (hello) VALUES (?);
        """.stripMargin
      ).bind(hello).executeUpdate().apply()

  def fetch() (implicit session: DBSession): Either[Throwable, Seq[Map[String, Any]]] =
    catching(classOf[Throwable]) either
      SQL(
        """
          |SELECT hello FROM foo;
        """.stripMargin
      ).map(_.toMap()).list().apply()
}