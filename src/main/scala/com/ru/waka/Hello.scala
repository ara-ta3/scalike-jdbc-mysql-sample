package com.ru.waka

import java.time.LocalDateTime

import scala.util.control.Exception._
import scalikejdbc._

object Hello {
  private val connectionSymbol = 'testDB

  lazy val connection: Symbol = {
    ConnectionPool.add(
      connectionSymbol,
      "jdbc:mysql://127.0.0.1:3307/test?characterEncoding=UTF-8",
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
        _       <- createTable()
        id      <- repository.put(time)
        _       <- repository.put2(id)
        _       <- repository.fetch()
        records <- repository.fetchAsRecord()
      } yield records) match {
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
    for {
      _ <- catching(classOf[Throwable]) either {
        SQL(
          """
        |CREATE TABLE IF NOT EXISTS foo (
        |  id int unsigned not null auto_increment primary key,
        |  hello varchar(100)
        |)
      """.stripMargin
        ).execute().apply()
      }
      _ <- catching(classOf[Throwable]) either {
        SQL(
          """
        |CREATE TABLE IF NOT EXISTS bar (
        |  id int unsigned not null auto_increment primary key,
        |  foo_id int unsigned not null,
        |  foreign key (foo_id) references foo(id) on delete cascade
        |)
      """.stripMargin
        ).execute().apply()
      }
    } yield true

}

class HelloRepository() {
  def put(hello: String)(implicit session: DBSession): Either[Throwable, Int] =
    catching(classOf[Throwable]) either
      SQL(
        """
          |INSERT INTO foo (hello) VALUES (?);
        """.stripMargin
      ).bind(hello).updateAndReturnGeneratedKey().apply().toInt

  def put2(id: Int)(implicit session: DBSession): Either[Throwable, Int] =
    catching(classOf[Throwable]) either
      SQL(
        """
          |INSERT INTO bar (foo_id) VALUES (?);
        """.stripMargin
      ).bind(id).executeUpdate().apply()

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
      val b = BarRecord.syntax("b")
      val sql = withSQL {
        select(
          sqls"${r.id} as ${r.resultName.id}, ${r.hello} as ${r.resultName.hello}, ${sqls
            .count(r.id)} as ${r.resultName.count}"
        ).from(Record as r)
          .leftJoin(BarRecord as b)
          .on(b.fooId, r.id)
          .groupBy(r.id)
      }
      println(sql.statement)
      sql.map(Record(r)).list().apply()
    }
}
