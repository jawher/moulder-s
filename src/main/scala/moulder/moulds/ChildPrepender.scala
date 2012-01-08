package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class ChildPrepender(private val content: Value[List[Node]]) extends Moulder {

  override def process(elementAndData: (Element, Option[Any])): List[(Node, Option[Any])] = {
    content.bind(elementAndData)
    content().map(_.reverse.foreach(elementAndData._1.prependChild(_)))
    List(elementAndData)
  }
}
