package jawher.moulder

import scala.collection.JavaConversions._

import org.specs._
import org.specs.runner._
import org.specs.mock.Mockito
import org.mockito.Matchers._
import org.mockito.ArgumentCaptor

import org.custommonkey.xmlunit.XMLUnit
import org.custommonkey.xmlunit.XMLAssert._

import org.jsoup.Jsoup
import org.jsoup.nodes._

import java.io.StringReader

class MoulderSpecTest extends JUnit4(MouldersSpec)

object MouldersSpec extends Specification with Mockito {
  import V._
  import M._

  "SubMoulder" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer a='v'><a>test</a></outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val subElement = document.getElementsByTag("a").first()
    val subNd = (subElement, Some("data"))

    val moulder = mock[Moulder]
    val edc = ArgumentCaptor.forClass(classOf[(Element, Option[Any])])
    moulder.process(edc.capture(), any[MoulderUtils]) returns  List((parseNode("<b>text</b>"), None), (parseNode("text"), None))
  
    val sm = new SubMoulder().register("a", List(moulder))

    val processed = sm.process(nd, mu)
    
    "call its registered moulder with the correct params" in { 
      subNd must_== edc.getValue()
    }

    "correctly applies its registered moulder result" in { 
      assertXMLEqual(new StringReader("<body><outer a='v'><b>text</b>text</outer></body>"), new StringReader(
	html(processed)))
    }
  }

  "Appender" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val content = mock[Value[List[Node]]]
    content.apply() returns Some(parse("<e a='v'>c</e>text"))

    val a = new Appender(content)

    val processed = a.process(nd, mu)
    
    "call bind then apply on its value" in { 
      there was one(content).bind(nd) then one(content).apply()
    }

    "correctly applies its registered moulder result" in { 
      assertXMLEqual(new StringReader("<body><outer>test</outer><e a='v'>c</e>text</body>"), new StringReader(
	html(processed)))
    }
  }

  private def parse(s: String) : List[Node] = {
    val d = Jsoup.parseBodyFragment(s)
    new JListWrapper(d.body().childNodes()).toList
  }

  def parseNode(s: String) : Node = {
    val d = Jsoup.parseBodyFragment(s)
    d.body().childNode(0)
  }

  def html(nodes: List[(Node, Any)]): String = {
    nodes.map(_._1).foldLeft("<body>")((s, n) =>s + n.outerHtml) + "</body>"
  }
}
