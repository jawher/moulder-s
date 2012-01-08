package moulder

import org.jsoup.nodes._

trait Moulder {
  def process(elementAndData: (Element, Option[Any])): List[(Node, Option[Any])]
}

