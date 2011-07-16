package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class AttrModifier(private val attr: Value[String], private val value: Value[String]) extends Moulder {
  override def process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = {
    attr.bind(elementAndData)
    attr() match {
      case Some(a) => {
        value.bind(elementAndData)
        value() match {
          case Some(v) => println("@a="+elementAndData._1.attr("a"));elementAndData._1.attr(a, v);println("2@a="+elementAndData._1.attr("a")); List(elementAndData)
          case None => elementAndData._1.removeAttr(a); List(elementAndData)
        }
      }
      case None => List(elementAndData)
    }
  }
}
