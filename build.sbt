
scalaVersion := "2.10.5"

name := "sss-ancillary"

version := "0.9.3"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "joda-time" % "joda-time" % "2.8.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

