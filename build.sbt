scalaVersion := "2.11.4"

//scalaVersion := "2.10.4"

name := "sss-ancillary"

version := "0.9"

EclipseKeys.withSource := true

scalacOptions ++= Seq("-deprecation", "-feature")

val scalaVer = "2.11.4"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVer

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"


