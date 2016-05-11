
scalaVersion := "2.11.7"

name := "sss-ancillary"

version := "0.9.8"

parallelExecution in Test := false

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "joda-time" % "joda-time" % "2.8.2"

libraryDependencies += "org.eclipse.jetty.aggregate" % "jetty-all-server" % "8.1.19.v20160209"

libraryDependencies += "us.monoid.web" % "resty" % "0.3.2"

libraryDependencies += "org.scalatra" % "scalatra_2.11" % "2.4.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % Test

