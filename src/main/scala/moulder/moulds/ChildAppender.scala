package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class ChildAppender(private val content: Value[List[Node]]) extends Moulder {
  override def process(element: Element): List[Node] = {
    for (nodes <- content(); node <- nodes) {
      element.appendChild(node)
    }
    List(element)
  }
}
