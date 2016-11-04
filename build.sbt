
publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

//sonatypeProfileName := "com.mcsherrylabs"

scalaVersion := "2.11.8"

name := "sss-ancillary"

version := "1.1-SNAPSHOT"

parallelExecution in Test := false

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "joda-time" % "joda-time" % "2.8.2"

// https://mvnrepository.com/artifact/org.eclipse.jetty.aggregate/jetty-all-server
libraryDependencies += "org.eclipse.jetty.aggregate" % "jetty-all-server" % "8.2.0.v20160908"


libraryDependencies += "us.monoid.web" % "resty" % "0.3.2"

libraryDependencies += "org.scalatra" % "scalatra_2.11" % "2.4.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % Test

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
