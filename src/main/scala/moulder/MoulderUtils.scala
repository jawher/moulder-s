package moulder

import org.jsoup.Jsoup
import org.jsoup.nodes._
import scala.collection.JavaConversions._

case class MoulderUtils(private val doc: Document) {
  def e(name: String) = doc.createElement(name);

  def copy(e: Element) = {
    val res = doc.createElement(e.tagName());
    e.attributes().foreach(a => res.attr(a.getKey(), a.getValue()))
    res.html(e.html())
    res
  }
}
