package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Appender(private val content: Value[List[Node]]) extends Moulder {
  override def process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = {
    content.bind(elementAndData)
    List(elementAndData) ::: (content() match {
      case Some(nodes: List[Node]) => nodes.map((_, elementAndData._2))
      case None => Nil
    })
  }
}
