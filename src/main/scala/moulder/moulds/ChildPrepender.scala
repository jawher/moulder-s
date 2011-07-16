package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class ChildPrepender(private val content: Value[List[Node]]) extends Moulder {
  override def process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = {
    content.bind(elementAndData)
    (content() match {
      case Some(nodes: List[Node]) => nodes
      case None => Nil
    }).reverse.foreach(elementAndData._1.prependChild(_))
    List(elementAndData)
  }
}
