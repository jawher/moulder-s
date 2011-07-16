name := "moulder-s"

organization := "moulder"

scalaVersion := "2.9.0-1"

version := "1.0"

libraryDependencies ++= Seq(
	"org.jsoup" % "jsoup" % "1.6.1",
	"xmlunit" % "xmlunit" % "1.1",
	"org.specs2" %% "specs2" % "1.5" % "test",
	"org.specs2" %% "specs2-scalaz-core" % "6.0.RC2" % "test"
)

resolvers += ScalaToolsSnapshots
