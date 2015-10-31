name := """jackson-pointer"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" %  "jackson-databind" % "2.4.4",
  "org.scalatest"              %% "scalatest"        % "2.1.7" % "test"
)
