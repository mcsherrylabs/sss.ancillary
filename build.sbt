
publishMavenStyle := true

updateOptions := updateOptions.value.withGigahorse(false)

organization := "com.mcsherrylabs"

val JettyVer = "10.0.11"

/*publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}*/


publishTo := {
  val nexus = "https://nexus.mcsherrylabs.com/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "repository/snapshots")
  else
    Some("releases"  at nexus + "repository/releases")
}

credentials += sys.env.get("NEXUS_USER").map(userName => Credentials(
  "Sonatype Nexus Repository Manager",
  "nexus.mcsherrylabs.com",
  userName,
  sys.env.getOrElse("NEXUS_PASS", ""))
).getOrElse(
  Credentials(Path.userHome / ".ivy2" / ".credentials")
)

Test / publishArtifact := false

//sonatypeProfileName := "com.mcsherrylabs"

scalaVersion := "2.13.3"

javacOptions := Seq("-source", "11", "-target", "11")

name := "sss-ancillary"

version := "1.24"

//crossScalaVersions := Seq(scalaVersion.toString())

Test / parallelExecution  := false

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies += "com.typesafe" % "config" % "1.4.2"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

libraryDependencies += "ch.qos.logback" % "logback-core" % "1.2.11"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.3.0-alpha16"

libraryDependencies += "org.eclipse.jetty" % "jetty-server" % JettyVer

libraryDependencies += "org.eclipse.jetty.websocket" % "websocket-jetty-server" % JettyVer

libraryDependencies += "us.monoid.web" % "resty" % "0.3.2"

libraryDependencies += "org.scalatra" %% "scalatra" % "2.8.2" % Test

libraryDependencies += "com.google.guava" % "guava" % "31.1-jre"

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
