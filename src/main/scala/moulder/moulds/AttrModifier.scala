package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class AttrModifier(private val attr: Value[String], private val value: Value[String]) extends Moulder {
  override def process(element: Element): List[Node] = {
    attr() match {
      case Some(a) => {
        value() match {
          case Some(v) => element.attr(a, v); List(element)
          case None => element.removeAttr(a); List(element)
        }
      }
      case None => List(element)
    }
  }
}
