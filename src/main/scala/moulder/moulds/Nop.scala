package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Nop() extends Moulder {
  override def process(elementAndData: (Element, Option[Any])): List[(Node, Option[Any])] = {
    List(elementAndData)
  }
}
