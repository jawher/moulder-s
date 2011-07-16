package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Texter(private val text: Value[Any]) extends Moulder {
  override def process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = {
    text.bind(elementAndData)
    text().foreach((t: Any) => elementAndData._1.text(t + ""))
    List(elementAndData)
  }
}
