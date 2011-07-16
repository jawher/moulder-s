package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Repeater[A](private val items: Value[List[A]]) extends Moulder {
  override def process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = {
    items.bind(elementAndData)
    items() match {
      case Some(data) => data.map((i: A) => (u.copy(elementAndData._1), Some(i)))
      case None => Nil
    }
  }
}
