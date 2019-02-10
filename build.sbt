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

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
      name := "scalike-jdbc-mysql-sample",
      libraryDependencies ++= Seq(
          "org.scalikejdbc" %% "scalikejdbc" % "3.3.2",
          "mysql" % "mysql-connector-java" % "6.0.6"
      )
  )
