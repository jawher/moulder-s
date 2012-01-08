package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Nop() extends Moulder {
  override def process(element: Element): List[Node] = {
    List(element)
  }
}
