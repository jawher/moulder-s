package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Repeater[A](private val items: Value[List[A]]) extends Moulder {
  override def process(element: Element): List[Node] = {
    items() match {
      case Some(data) => data.map((i: A) => copy(element))
      case None => Nil
    }
  }

  private def copy(e: Element) = {
    import scala.collection.JavaConversions._

    val res = e.ownerDocument().createElement(e.tagName())
    e.attributes().foreach(a => res.attr(a.getKey(), a.getValue()))
    res.html(e.html())
    res
  }
}
