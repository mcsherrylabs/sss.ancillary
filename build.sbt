
publishMavenStyle := true

updateOptions := updateOptions.value.withGigahorse(false)

/*publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}*/

publishArtifact in Test := false

//sonatypeProfileName := "com.mcsherrylabs"

scalaVersion := "2.13.3"

javacOptions := Seq("-source", "11", "-target", "11")

name := "sss-ancillary"

version := "1.17-SNAPSHOT"

//crossScalaVersions := Seq(scalaVersion.toString())

parallelExecution in Test := false

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies += "com.typesafe" % "config" % "1.4.0"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "joda-time" % "joda-time" % "2.9.9"

// https://mvnrepository.com/artifact/org.eclipse.jetty.aggregate/jetty-all

libraryDependencies += "org.eclipse.jetty.aggregate" % "jetty-all" % "9.4.29.v20200521" pomOnly()

libraryDependencies += "us.monoid.web" % "resty" % "0.3.2"

libraryDependencies += "org.scalatra" %% "scalatra" % "2.7.0" % Test

libraryDependencies += "com.google.guava" % "guava" % "20.0"

// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.2" % Test


pomExtra := (
  <url>https://github.com/mcsherrylabs/sss.ancillary</url>
  <licenses>
    <license>
      <name>GPL3</name>
      <url>https://www.gnu.org/licenses/gpl-3.0.en.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:mcsherrylabs/sss.ancillary.git</url>
    <connection>scm:git:git@github.com:mcsherrylabs/sss.ancillary.git</connection>
  </scm>
  <developers>
    <developer>
      <id>mcsherrylabs</id>
      <name>Alan McSherry</name>
      <url>http://mcsherrylabs.com</url>
    </developer>
  </developers>)
