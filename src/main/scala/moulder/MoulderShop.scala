package moulder

import org.jsoup.Jsoup
import org.jsoup.nodes._
import scala.collection.JavaConversions._
import moulder.moulds.SubMoulder

case class MoulderShop() {
  private val sm = SubMoulder()

  def register(selector: String, moulders: List[Moulder]) = sm.register(selector, moulders)
  def register(selector: String, moulders: Moulder*) = sm.register(selector, moulders: _*)

  def process(document: Document) = sm.process(document)

  def process(stream: java.io.InputStream) = {
    val doc = Jsoup.parse(stream, null, "#")
    sm.process(doc)
  }
}
