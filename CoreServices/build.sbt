name := """CoreServices"""

version := "1.0"

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "0.8.1",
  "com.typesafe.akka" %% "akka-cluster" % "2.4.16",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.16",
  "org.scalatest" %% "scalatest" % "3.0.0",
  "org.scalacheck" %% "scalacheck" % "1.13.4"
)