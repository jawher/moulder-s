package moulder

import scala.collection.JavaConversions._

import moulder.values._
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.mockito.Matchers._
import org.mockito.ArgumentCaptor

import org.custommonkey.xmlunit.XMLUnit
import org.custommonkey.xmlunit.XMLAssert._

import org.jsoup.Jsoup
import org.jsoup.nodes._

import java.io.StringReader


class MoulderUseCases extends Specification {
  import Values._
  import Moulds._
  "A complex use case" in {
    val document = Jsoup.parse("<html><body><h1>[...]</h1></body></html>")
    val s = MoulderShop()
    s.register("h1",
      repeat("Spring" :: "Summer" :: "Autumn" :: "Winter" :: Nil)
      :: attr("class", SeqValue("even" :: "odd" :: Nil).cycle)
      :: text(elemData())
      :: append(html(transform(elemData[String](), (c: String) => "<p>" + c + "</p>")))
      :: Nil)
    s.process(document)
    println(document)
    success
  }

  "Usage for the blog" in {
    object TaskType extends Enumeration {
      type TaskType = Value
      val BUG, ENHANCEMENT, NEW_FEATURE = Value
    }

    object TaskStatus extends Enumeration {
      type TaskStatus = Value
      val OPEN, CLOSED = Value
    }

    import TaskType._
    import TaskStatus._
    case class Task(val title: String, val description: String, val typ: TaskType, val status: TaskStatus, val urgent: Boolean)

    val tasks = Task("Fix the bug", "bug", BUG, OPEN, true) ::
      Task("Fix the bug", "bug", ENHANCEMENT, CLOSED, false) ::
      Task("Fix the bug", "bug", NEW_FEATURE, OPEN, false) ::
      Nil

    val m = MoulderShop()

    m.register("#tasks li", repeat(tasks),
      attr("class", transform(elemData[Task](), (t: Task) => if (t.status == CLOSED) "closed" else "")),
      sub().register("span", remove(transform(elemData[Task](), (t: Task) => !t.urgent))),
      sub().register("img", attr("src", transform(elemData[Task](), (t: Task) => "/images/" + (t.typ match {
        case BUG => "circle_red.png"
        case ENHANCEMENT => "circle_green.png"
        case _ => "circle_blue.png"
      })))),
      sub().register("h2", text(transform(elemData[Task](), (t: Task) => t.title))),
      sub().register("p", text(transform(elemData[Task](), (t: Task) => t.description))))

    val doc = m.process(classOf[MoulderUseCases].getResourceAsStream("tasks.html"));

    println(doc)
    success
  }
}
