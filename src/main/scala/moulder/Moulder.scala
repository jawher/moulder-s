package moulder

import org.jsoup.nodes._

trait Moulder {
  def process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])]
}

