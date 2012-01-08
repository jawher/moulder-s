package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Appender(private val content: Value[List[Node]]) extends Moulder {

  override def process(element: Element): List[Node] = {
    (List(element):List[Node]) ::: content().getOrElse(Nil)
  }
}
