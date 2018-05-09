package com.ru.waka

import scalikejdbc.{DBSession, NamedDB, SQL}

import scala.util.control.Exception._

object Hello {
  def main(args: Array[String]): Unit = {

  }
}

class HelloRepository(connectionName: Symbol) {
  def put(hello: String): Either[Throwable, Unit] = catching(classOf[Throwable]) either {
    val q =
      """
        |INSERT INTO hoge (foo) VALUES (?);
      """.stripMargin
    NamedDB(connectionName) localTx { implicit session: DBSession =>
      SQL(q).bind()


    }

  }
}