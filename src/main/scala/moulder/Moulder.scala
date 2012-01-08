package moulder

import org.jsoup.nodes._

trait Moulder {
  def process(element: Element): List[Node]
}

