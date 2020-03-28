name := """MainTask"""
organization := "test.task"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "test.task.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "test.task.binders._"
