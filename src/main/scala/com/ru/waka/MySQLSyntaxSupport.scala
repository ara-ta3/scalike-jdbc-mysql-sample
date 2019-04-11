package com.ru.waka

import scalikejdbc._

class MySQLSyntaxSupport[T] extends SQLSyntaxSupport[T] {
  override def connectionPoolName: Symbol = Hello.connection
}

