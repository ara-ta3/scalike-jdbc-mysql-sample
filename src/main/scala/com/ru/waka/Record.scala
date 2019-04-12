package com.ru.waka

import scalikejdbc._

case class Record(
    id: Int,
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
      id = rs.int(r.id),
      hello = rs.string(r.hello),
      count = rs.int(r.count)
    )

}

case class BarRecord(
    id: Int,
    fooId: Int,
)

object BarRecord extends MySQLSyntaxSupport[BarRecord] {
  override val tableName = "bar"

  def apply(
      r: SyntaxProvider[BarRecord]
  )(rs: WrappedResultSet): BarRecord =
    apply(r.resultName)(rs)

  def apply(
      r: ResultName[BarRecord]
  )(rs: WrappedResultSet): BarRecord =
    BarRecord(
      id = rs.int(r.id),
      fooId = rs.int(r.fooId)
    )
}
