package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class AttrModifier(private val attr: Value[String], private val value: Value[String]) extends Moulder {
  override def process(elementAndData: (Element, Option[Any])): List[(Node, Option[Any])] = {
    attr.bind(elementAndData)
    attr() match {
      case Some(a) => {
        value.bind(elementAndData)
        value() match {
          case Some(v) => elementAndData._1.attr(a, v); List(elementAndData)
          case None => elementAndData._1.removeAttr(a); List(elementAndData)
        }
      }
      case None => List(elementAndData)
    }
  }
}
