import sbt._

class MoulderProject(info: ProjectInfo) extends DefaultProject(info)
{
  val scalaTools = "Scala tools" at "http://scala-tools.org/repo-releases"

  val junit = "junit" % "junit" % "4.8.2"
  val specs = "org.scala-tools.testing" % "specs_2.8.1" % "1.6.7.2"
  val moquito = "org.mockito" % "mockito-all" % "1.8.4"
  val xmlunit = "xmlunit" % "xmlunit" % "1.3"

  val jsoup = "org.jsoup" % "jsoup" % "1.5.2"
}
