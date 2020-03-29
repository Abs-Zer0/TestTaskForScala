name := """database"""
organization := "test.task.database"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-slick" % "5.0.0",
    "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
    "com.typesafe.slick" %% "slick" % "3.3.2",
    "org.slf4j" % "slf4j-nop" % "1.7.26",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
    "com.h2database" % "h2" % "1.4.197"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "test.task.database.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "test.task.database.binders._"
