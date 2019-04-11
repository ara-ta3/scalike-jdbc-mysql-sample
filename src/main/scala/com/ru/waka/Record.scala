package com.ru.waka

import scalikejdbc._

case class Record(
    hello: String,
    count: Int,
)

object Record extends MySQLSyntaxSupport[Record] {
  override val tableName = "foo"

  def apply(
      r: SyntaxProvider[Record]
  )(rs: WrappedResultSet): Record =
    apply(r.resultName)(rs)

  def apply(
      r: ResultName[Record]
  )(rs: WrappedResultSet): Record =
    Record(
      hello = "hello",
      count = rs.int(r.count),
    )
}
