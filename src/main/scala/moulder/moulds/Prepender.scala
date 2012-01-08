package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Prepender(private val content: Value[List[Node]]) extends Moulder {

  override def process(element: Element): List[Node] = {
    content().getOrElse(Nil) ::: List(element)
  }
}
