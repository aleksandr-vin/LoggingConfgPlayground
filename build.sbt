name := "LoggingConfgPlayground"

version := "0.1-SNAPSHOT"

scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.5" % "test",
  "org.scalatestplus" %% "scalacheck-1-15" % "3.2.5.0" % "test",
  "org.slf4j" % "slf4j-api" % "1.7.30",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
)

javacOptions += "-Xlint:deprecation"

scalacOptions += "-deprecation"

