package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Texter(private val text: Value[Any]) extends Moulder {
  override def process(element: Element): List[Node] = {
    text().foreach((t: Any) => element.text(t + ""))
    List(element)
  }
}
