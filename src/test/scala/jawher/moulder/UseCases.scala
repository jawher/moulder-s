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

class MoulderUseCasesTest extends JUnit4(MoulderUseCases)

object MoulderUseCases extends Specification {
  import V._
  import M._
  "A complex use case" in {
    val document = Jsoup.parse("<html><body><h1>[...]</h1></body></html>")
    val s = MoulderShop()
    s.register("h1", 
               repeat("Spring" :: "Summer" :: "Autumn" :: "Winter" :: Nil)
               :: attr("class", Values("even" :: "odd" :: Nil).cycle) 
               :: text(eData()) 
               :: append(h(tr(eData[String](), (c:String)=>"<p>"+ c +"</p>"))) 
               :: Nil)
    s.process(document)
    println(document)
  }
}