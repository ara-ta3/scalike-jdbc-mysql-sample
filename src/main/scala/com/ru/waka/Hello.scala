package com.ru.waka

import java.time.LocalDateTime

import scala.util.control.Exception._
import scalikejdbc._

object Hello {
  private val connectionSymbol = 'testDB

  lazy val connection: Symbol = {
    ConnectionPool.add(
      connectionSymbol,
      "jdbc:mysql://127.0.0.1/test?characterEncoding=UTF-8",
      "root",
      "root"
    )
    connectionSymbol
  }

  Class.forName("com.mysql.cj.jdbc.Driver")

  val repository = new HelloRepository()

  def main(args: Array[String]): Unit = {
    val time = LocalDateTime.now().toString
    NamedDB(connection) localTx { implicit session =>
      (for {
        _   <- createTable()
        _   <- repository.put(time)
        rs  <- repository.fetch()
        rss <- repository.fetchAsRecord()
      } yield (rs, rss)) match {
        case Right(rs) =>
          session.connection.commit()
          println(rs)
        case Left(th) =>
          session.connection.rollback()
          println(th)
      }
    }
  }

  def createTable()(implicit session: DBSession): Either[Throwable, Boolean] =
    catching(classOf[Throwable]) either {
      SQL(
        """
        |CREATE TABLE IF NOT EXISTS foo (id int unsigned not null auto_increment primary key,hello varchar(100))
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

  def fetch()(implicit session: DBSession): Either[Throwable, Seq[Map[String, Any]]] =
    catching(classOf[Throwable]) either
      SQL(
        """
          |SELECT hello FROM foo;
        """.stripMargin
      ).map(_.toMap()).list().apply()

  def fetchAsRecord()(implicit session: DBSession): Either[Throwable, Seq[Record]] =
    catching(classOf[Throwable]) either {
      val r = Record.syntax("r")
      val sql = withSQL {
        select(r.result(sqls"count(*) as count").column("count"))
          .from(Record as r)
      }
      println(sql.statement)
      sql.map(Record(r)).list().apply()
    }
}
