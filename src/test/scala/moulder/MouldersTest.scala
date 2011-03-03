package moulder

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

    "apply its registered moulder result" in { 
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

    val a = Appender(content)

    val processed = a.process(nd, mu)
    
    "call bind then apply on its value" in { 
      there was one(content).bind(nd) then one(content).apply()
    }

    "append its content after its processed element" in { 
      assertXMLEqual(new StringReader("<body><outer>test</outer><e a='v'>c</e>text</body>"), new StringReader(
	html(processed)))
    }
  }

  "Prepender" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val content = mock[Value[List[Node]]]
    content.apply() returns Some(parse("<e a='v'>c</e>text"))

    val a = Prepender(content)

    val processed = a.process(nd, mu)
    
    "call bind then apply on its value" in { 
      there was one(content).bind(nd) then one(content).apply()
    }

    "prepend its content before its processed element" in { 
      assertXMLEqual(new StringReader("<body><e a='v'>c</e>text<outer>test</outer></body>"), new StringReader(
	html(processed)))
    }
  }

  "ChildAppender" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test<b a='v'>t</b>s</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val content = mock[Value[List[Node]]]
    content.apply() returns Some(parse("<e a='v'>c</e>text"))

    val a = ChildAppender(content)

    val processed = a.process(nd, mu)
    
    "call bind then apply on its value" in { 
      there was one(content).bind(nd) then one(content).apply()
    }

    "append its content to its parent's children" in { 
      assertXMLEqual(new StringReader("<body><outer>test<b a='v'>t</b>s<e a='v'>c</e>text</outer></body>"), new StringReader(
	html(processed)))
    }
  }

  "ChildPrepender" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test<b a='v'>t</b>s</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val content = mock[Value[List[Node]]]
    content.apply() returns Some(parse("<e a='v'>c</e>text"))

    val a = ChildPrepender(content)

    val processed = a.process(nd, mu)
    
    "call bind then apply on its value" in { 
      there was one(content).bind(nd) then one(content).apply()
    }

    "prepend its content to its parent's children" in { 
      assertXMLEqual(new StringReader("<body><outer><e a='v'>c</e>texttest<b a='v'>t</b>s</outer></body>"), new StringReader(
	html(processed)))
    }
  }

  "Remover" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test<b a='v'>t</b>s</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    ", given a value that returns true, " in { 
      val remove = mock[Value[Boolean]]
      remove.apply() returns Some(true)

      val a = Remover(remove)

      val processed = a.process(nd, mu)
      "call bind then apply on its value" in { 
        there was one(remove).bind(nd) then one(remove).apply()
      }

      "remove its element" in { 
        assertXMLEqual(new StringReader("<body></body>"), new StringReader(
	  html(processed)))
      }
    }

    ", given a value that returns false, " in { 
      val remove = mock[Value[Boolean]]
      remove.apply() returns Some(false)

      val a = Remover(remove)

      val processed = a.process(nd, mu)
      "call bind then apply on its value" in { 
        there was one(remove).bind(nd) then one(remove).apply()
      }

      "keep its element" in { 
        assertXMLEqual(new StringReader("<body><outer>test<b a='v'>t</b>s</outer></body>"), new StringReader(
	  html(processed)))
      }
    }
  }

  "Replacer" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    ", given a value that returns something, " in { 
      val content = mock[Value[List[Node]]]
      content.apply() returns Some(parse("<e a='v'>c</e>text"))

      val a = Replacer(content)

      val processed = a.process(nd, mu)
    
      "call bind then apply on its value" in { 
        there was one(content).bind(nd) then one(content).apply()
      }
      
      "replace its element with its content" in { 
        assertXMLEqual(new StringReader("<body><e a='v'>c</e>text</body>"), new StringReader(
	  html(processed)))
      }
    }

    ", given a value that returns nothing, " in { 
      val content = mock[Value[List[Node]]]
      content.apply() returns None
      
      val a = Replacer(content)

      val processed = a.process(nd, mu)
    
      "call bind then apply on its value" in { 
        there was one(content).bind(nd) then one(content).apply()
      }
      
      "remove its element" in { 
        assertXMLEqual(new StringReader("<body></body>"), new StringReader(
	  html(processed)))
      }
    }
  }

  "Repeater" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer a='v'>test</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val list = 1 :: 3 :: 4 :: 7 :: Nil
    val items = mock[Value[List[Int]]]    
    items.apply() returns Some(list)

    val a = Repeater(items)

    val processed = a.process(nd, mu)
    
    "call bind then apply on its value" in { 
      there was one(items).bind(nd) then one(items).apply()
    }

    "repeat its elements for every item" in { 
      assertXMLEqual(new StringReader("<body><outer a='v'>test</outer><outer a='v'>test</outer><outer a='v'>test</outer><outer a='v'>test</outer></body>"), new StringReader(
	html(processed)))
    }

    "associate the corresponding item to every produced element" in {
      val it = list.iterator
      processed.forall(it.hasNext && Some(it.next) == _._2)
    }
  }

  "AttrModifier" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer a='v'>test</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))   

    ", given a value that returns something, " in { 
      val attr = mock[Value[String]]
      attr.apply() returns Some("b") 

      val value = mock[Value[String]]
      value.apply() returns Some("u")

      val a = AttrModifier(attr, value)

      val processed = a.process(nd, mu)
      "call bind then apply on its attr and value" in { 
        there was one(attr).bind(nd) then one(attr).apply()
        there was one(value).bind(nd) then one(value).apply()
      }

      "add the specified attribute to its element" in { 
        assertXMLEqual(new StringReader("<body><outer a='v' b='u'>test</outer></body>"), new StringReader(
	  html(processed)))
      }
    }

    ", given a value that returns nothing, " in { 
      val attr = mock[Value[String]]
      attr.apply() returns Some("a")
      
      val value = mock[Value[String]]
      value.apply() returns None

      val a = AttrModifier(attr, value)

      val processed = a.process(nd, mu)
      "call bind then apply on its attr and value" in { 
        there was one(attr).bind(nd) then one(attr).apply()
        there was one(value).bind(nd) then one(value).apply()
      }

      "remove the specified attribute from its element" in { 
        assertXMLEqual(new StringReader("<body><outer>test</outer></body>"), new StringReader(
	  html(processed)))
      }
    }
  }

  "Texter" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test<b a='v'>t</b>s</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val text = mock[Value[String]]
    text.apply() returns Some("text")

    val a = Texter(text)

    val processed = a.process(nd, mu)
    
    "call bind then apply on its value" in { 
      there was one(text).bind(nd) then one(text).apply()
    }

    "set its element content with its value's text" in { 
      assertXMLEqual(new StringReader("<body><outer>text</outer></body>"), new StringReader(
	html(processed)))
    }
  }

  "Nop" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test<b a='v'>t</b>s</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))

    val a = Nop()

    val processed = a.process(nd, mu)
    
    "do nothing, really" in { 
      assertXMLEqual(new StringReader("<body><outer>test<b a='v'>t</b>s</outer></body>"), new StringReader(
	html(processed)))
    }
  }

  
  "If" should {
    XMLUnit.setIgnoreWhitespace(true);
    val document = Jsoup.parseBodyFragment("<html><body><outer>test</outer></body></html>")
    val mu = new MoulderUtils(document)

    val element = document.getElementsByTag("outer").first()
    val nd = (element, Some("data"))
    
    val thenMoulder = mock[Moulder]
    val thenResult = (parseNode("<then>then</then>"), Some("then")) :: Nil
    thenMoulder.process(nd, mu) returns thenResult

    val elseMoulder = mock[Moulder]
    val elseResult = (parseNode("<else></else>"), Some("else")) :: Nil
    elseMoulder.process(nd, mu) returns elseResult

    ", given a value that returns true, " in { 
      val condition = mock[Value[Boolean]]
      condition.apply() returns Some(true)

      val a = If(condition, thenMoulder, elseMoulder)

      val processed = a.process(nd, mu)
    
      "call bind then apply on its value" in { 
        there was one(condition).bind(nd) then one(condition).apply()
      }
      
      "call the thenMoulder with the correct args" in { 
        there was one(thenMoulder).process(nd, mu)
      }

      "return the thenMoulder result" in { 
        processed must_== thenResult
      }
    }

    ", given a value that returns false, " in { 
      val condition = mock[Value[Boolean]]
      condition.apply() returns Some(false)

      val a = If(condition, thenMoulder, elseMoulder)

      val processed = a.process(nd, mu)
    
      "call bind then apply on its value" in { 
        there was one(condition).bind(nd) then one(condition).apply()
      }
      
      "call the elseMoulder with the correct args" in { 
        there was one(elseMoulder).process(nd, mu)
      }

      "return the elseMoulder result" in { 
        processed must_== elseResult
      }
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
