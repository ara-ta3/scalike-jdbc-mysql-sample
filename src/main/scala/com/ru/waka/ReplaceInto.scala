package com.ru.waka

import scalikejdbc._
import scala.util.control.Exception.catching

object ReplaceInto {
  def main(args: Array[String]): Unit = {
    val data1 = Seq(
      Seq(1, 2)
    )
    val data2 = Seq(
      Seq(1, 2),
      Seq(2, 3)
    )
    val connection = Hello.connection
    val col        = UniqTestRecord.column
    val s          = sql"""
              REPLACE INTO ${UniqTestRecord.table}
              (${col.id}, ${col.otherId})
              VALUES (?, ?)
        """
    NamedDB(connection) localTx { implicit session =>
      catching(classOf[Throwable]) either {
        s.batch(data1: _*).apply()
        s.batch(data2: _*).apply()

        val u = UniqTestRecord.syntax
        val rs = withSQL(select.from(UniqTestRecord as u))
          .map(rs => (rs.long(u.resultName.id), rs.long(u.resultName.otherId)))
          .list()
          .apply()
        println(rs)
        // List((1,2), (2,3))
      }
    }

    ()
  }
}

case class UniqTestRecord(
    id: Long,
    otherId: Long
)

object UniqTestRecord extends MySQLSyntaxSupport[UniqTestRecord] {
  override val tableName = "uniq_test"
}
