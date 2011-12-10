package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Replacer(private val content: Value[List[Node]]) extends Moulder {
  override def process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = {
    content.bind(elementAndData)
    content().map(_.map(_ -> elementAndData._2)).getOrElse(Nil)
  }
}
