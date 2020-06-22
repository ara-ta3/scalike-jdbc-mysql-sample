import sbt._

object Dependencies {
  val scalikejdbc: ModuleID = "org.scalikejdbc" % "scalikejdbc" % "3.3.2"
  val mysql: ModuleID = "mysql" % "mysql-connector-java" % "8.0.15"

  val logger: ModuleID = "org.slf4j" % "slf4j-log4j12"    % "1.7.25"
}
