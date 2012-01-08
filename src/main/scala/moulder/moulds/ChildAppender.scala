package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class ChildAppender(private val content: Value[List[Node]]) extends Moulder {
  override def process(elementAndData: (Element, Option[Any])): List[(Node, Option[Any])] = {
    content.bind(elementAndData)
    content().foreach(_.foreach(elementAndData._1.appendChild(_)))
    List(elementAndData)
  }
}
