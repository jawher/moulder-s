package moulder

import scala.collection.JavaConversions._

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.mockito.Matchers._
import org.mockito.ArgumentCaptor

import org.custommonkey.xmlunit.XMLUnit
import org.custommonkey.xmlunit.XMLAssert._

import org.jsoup.Jsoup
import org.jsoup.nodes._

import java.io.StringReader

import moulds._
import values._
import Values._
import Moulds._

class MouldersSpec extends Specification with Mockito {

  "SubMoulder" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer a='v'><a>test</a></outer></body></html>")

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val subElement = document.getElementsByTag("a").first()
    val subNd = (subElement, Some("data"))

    val moulder = mock[Moulder]
    val edc = ArgumentCaptor.forClass(classOf[(Element, Option[Any])])
    moulder.process(edc.capture()) returns List((parseNode("<b>text</b>"), None), (parseNode("text"), None))

    val sm = new SubMoulder().register("a", List(moulder))

    val processed = sm.process(nd)

    "call its registered moulder with the correct params" in {
      subNd must_== edc.getValue()
    }

    "apply its registered moulder result" in {
      XMLUnit.compareXML(new StringReader("<body><outer a='v'><b>text</b>text</outer></body>"), new StringReader(
        html(processed))).identical() must beTrue
    }
  }

  "Appender" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test</outer></body></html>")

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val content = mock[Value[List[Node]]]
    content.apply() returns Some(parse("<e a='v'>c</e>text"))

    val a = Appender(content)

    val processed = a.process(nd)

    "call bind then apply on its value" in {
      there was one(content).bind(nd) then one(content).apply()
    }

    "append its content after its processed element" in {
      XMLUnit.compareXML(new StringReader("<body><outer>test</outer><e a='v'>c</e>text</body>"), new StringReader(
        html(processed))).identical() must beTrue
    }
  }

  "Prepender" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test</outer></body></html>")

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val content = mock[Value[List[Node]]]
    content.apply() returns Some(parse("<e a='v'>c</e>text"))

    val a = Prepender(content)

    val processed = a.process(nd)

    "call bind then apply on its value" in {
      there was one(content).bind(nd) then one(content).apply()
    }

    "prepend its content before its processed element" in {
      XMLUnit.compareXML(new StringReader("<body><e a='v'>c</e>text<outer>test</outer></body>"), new StringReader(
        html(processed))).identical() must beTrue
    }
  }

  "ChildAppender" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test<b a='v'>t</b>s</outer></body></html>")

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val content = mock[Value[List[Node]]]
    content.apply() returns Some(parse("<e a='v'>c</e>text"))

    val a = ChildAppender(content)

    val processed = a.process(nd)

    "call bind then apply on its value" in {
      there was one(content).bind(nd) then one(content).apply()
    }

    "append its content to its parent's children" in {
      XMLUnit.compareXML(new StringReader("<body><outer>test<b a='v'>t</b>s<e a='v'>c</e>text</outer></body>"), new StringReader(
        html(processed))).identical() must beTrue
    }
  }

  "ChildPrepender" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test<b a='v'>t</b>s</outer></body></html>")

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val content = mock[Value[List[Node]]]
    content.apply() returns Some(parse("<e a='v'>c</e>text"))

    val a = ChildPrepender(content)

    val processed = a.process(nd)

    "call bind then apply on its value" in {
      there was one(content).bind(nd) then one(content).apply()
    }

    "prepend its content to its parent's children" in {
      XMLUnit.compareXML(new StringReader("<body><outer><e a='v'>c</e>texttest<b a='v'>t</b>s</outer></body>"), new StringReader(
        html(processed))).identical() must beTrue
    }
  }

  "Remover" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test<b a='v'>t</b>s</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    "Given a value that returns true" in {
      val remove = mock[Value[Boolean]]
      remove.apply() returns Some(true)

      val a = Remover(remove)

      val processed = a.process(nd)
      "call bind then apply on its value" in {
        there was one(remove).bind(nd) then one(remove).apply()
      }

      "remove its element" in {
        XMLUnit.compareXML(new StringReader("<body></body>"), new StringReader(
          html(processed))).identical() must beTrue
      }
    }

    "Given a value that returns false" in {
      val remove = mock[Value[Boolean]]
      remove.apply() returns Some(false)

      val a = Remover(remove)

      val processed = a.process(nd)
      "call bind then apply on its value" in {
        there was one(remove).bind(nd) then one(remove).apply()
      }

      "keep its element" in {
        XMLUnit.compareXML(new StringReader("<body><outer>test<b a='v'>t</b>s</outer></body>"), new StringReader(
          html(processed))).identical() must beTrue
      }
    }
  }

  "Replacer" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test</outer></body></html>")

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    "Given a value that returns something" in {
      val content = mock[Value[List[Node]]]
      content.apply() returns Some(parse("<e a='v'>c</e>text"))

      val a = Replacer(content)

      val processed = a.process(nd)

      "call bind then apply on its value" in {
        there was one(content).bind(nd) then one(content).apply()
      }

      "replace its element with its content" in {
        XMLUnit.compareXML(new StringReader("<body><e a='v'>c</e>text</body>"), new StringReader(
          html(processed))).identical() must beTrue
      }
    }

    "Given a value that returns nothing" in {
      val content = mock[Value[List[Node]]]
      content.apply() returns None

      val a = Replacer(content)

      val processed = a.process(nd)

      "call bind then apply on its value" in {
        there was one(content).bind(nd) then one(content).apply()
      }

      "remove its element" in {
        XMLUnit.compareXML(new StringReader("<body></body>"), new StringReader(
          html(processed))).identical() must beTrue
      }
    }
  }

  "Repeater" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer a='v'>test</outer></body></html>")

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val list = 1 :: 3 :: 4 :: 7 :: Nil
    val items = mock[Value[List[Int]]]
    items.apply() returns Some(list)

    val a = Repeater(items)

    val processed = a.process(nd)

    "call bind then apply on its value" in {
      there was one(items).bind(nd) then one(items).apply()
    }

    "repeat its elements for every item" in {
      XMLUnit.compareXML(new StringReader("<body><outer a='v'>test</outer><outer a='v'>test</outer><outer a='v'>test</outer><outer a='v'>test</outer></body>"), new StringReader(
        html(processed))).identical() must beTrue
    }

    "associate the corresponding item to every produced element" in {
      val it = list.iterator
      processed.forall(it.hasNext && Some(it.next) == _._2)
    }
  }

  "AttrModifier" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer a='v'>test</outer></body></html>")

    val element = document.getElementsByTag("outer").first()

    "Given a value that returns something" in {
      val attr = mock[Value[String]]
      attr.apply() returns Some("b")

      val value = mock[Value[String]]
      value.apply() returns Some("u")

      val a = AttrModifier(attr, value)

      val nd = (element.clone(), Some("data"))

      val processed = a.process(nd)
  
      "call bind then apply on its attr and value" in {
        there was one(attr).bind(nd) then one(attr).apply()
        there was one(value).bind(nd) then one(value).apply()
      }

      "add the specified attribute to its element" in {
        XMLUnit.compareXML(new StringReader("<body><outer a='v' b='u'>test</outer></body>"), new StringReader(
          html(processed))).identical() must beTrue
      }
    }

    "Given a value that returns nothing" in {
      val attr = mock[Value[String]]
      attr.apply() returns Some("a")

      val value = mock[Value[String]]
      value.apply() returns None

      val a = AttrModifier(attr, value)

      val nd = (element.clone(), Some("data"))

      val processed = a.process(nd)
      "call bind then apply on its attr and value" in {
        there was one(attr).bind(nd) then one(attr).apply()
        there was one(value).bind(nd) then one(value).apply()
      }

      "remove the specified attribute from its element" in {
        XMLUnit.compareXML(new StringReader("<body><outer>test</outer></body>"), new StringReader(
          html(processed))).identical() must beTrue
      }
    }
  }

  "Texter" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test<b a='v'>t</b>s</outer></body></html>")

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val text = mock[Value[String]]
    text.apply() returns Some("text")

    val a = Texter(text)

    val processed = a.process(nd)

    "call bind then apply on its value" in {
      there was one(text).bind(nd) then one(text).apply()
    }

    "set its element content with its value's text" in {
      XMLUnit.compareXML(new StringReader("<body><outer>text</outer></body>"), new StringReader(
        html(processed))).identical() must beTrue
    }
  }

  "Nop" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test<b a='v'>t</b>s</outer></body></html>")

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val a = Nop()

    val processed = a.process(nd)

    "do nothing, really" in {
      XMLUnit.compareXML(new StringReader("<body><outer>test<b a='v'>t</b>s</outer></body>"), new StringReader(
        html(processed))).identical() must beTrue
    }
  }

  private def parse(s: String): List[Node] = {
    val d = Jsoup.parseBodyFragment(s)
    new JListWrapper(d.body().childNodes()).toList
  }

  def parseNode(s: String): Node = {
    val d = Jsoup.parseBodyFragment(s)
    d.body().childNode(0)
  }

  def html(nodes: List[(Node, Any)]): String = {
    nodes.map(_._1).foldLeft("<body>")((s, n) => s + n.outerHtml) + "</body>"
  }
}
