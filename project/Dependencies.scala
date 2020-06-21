import sbt._

object Dependencies {
  val database: Seq[ModuleID] = Seq(
    "org.scalikejdbc" % "scalikejdbc" % "3.3.2",
    "mysql" % "mysql-connector-java" % "8.0.15"
  )

  val logger: Seq[ModuleID] = Seq(
    "org.slf4j" % "slf4j-log4j12"    % "1.7.25"
  )
}
