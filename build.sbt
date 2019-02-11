val commonSettings = Seq(
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    conflictManager := ConflictManager.strict,
    scalacOptions ++= Seq(
        "-deprecation",
        "-feature",
        "-unchecked",
        "-language:implicitConversions",
        "-Xlint",
        "-Xfatal-warnings",
        "-Ywarn-numeric-widen",
        "-Ywarn-unused",
        "-Ywarn-unused-import",
        "-Ywarn-value-discard",
    ),
)

val Json4sVersion   = "3.6.1"

val json4s = Seq(
    "org.json4s" %% "json4s-jackson" % Json4sVersion,
    "org.json4s" %% "json4s-ext"     % Json4sVersion,
)

val database = Seq(
          "org.scalikejdbc" %% "scalikejdbc" % "3.3.2",
          "mysql" % "mysql-connector-java" % "8.0.15"
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
      name := "scalike-jdbc-mysql-sample",
      libraryDependencies ++= (database ++ json4s)
  )
