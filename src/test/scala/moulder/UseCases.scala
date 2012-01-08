package moulder


import moulder.values._
import org.specs2.mutable.Specification


import org.jsoup.Jsoup

import java.lang.String


class MoulderUseCases extends Specification {
  import Values._
  import Moulds._
  "A complex use case" in {
    val document = Jsoup.parse("<html><body><h1>[...]</h1></body></html>")
    val s = MoulderShop()
    val items = "Spring" :: "Summer" :: "Autumn" :: "Winter" :: Nil
    s.register("h1",
      repeat(items)
      :: attr("class", SeqValue("even" :: "odd" :: Nil).cycle)
      :: text(SeqValue(items))
      :: append(html(transform(SeqValue(items), (c: String) => "<p>" + c + "</p>")))
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
      Task("Enhance the thing", "enhance", ENHANCEMENT, CLOSED, false) ::
      Task("Add dat", "add", NEW_FEATURE, OPEN, false) ::
      Nil

    val m = MoulderShop()

    m.register("#tasks li", repeat(tasks),
      attr("class", transform(SeqValue(tasks), (t: Task) => if (t.status == CLOSED) "closed" else "")),
      sub().register("span", remove(transform(SeqValue(tasks), (t: Task) => !t.urgent))),
      sub().register("img", attr("src", transform(SeqValue(tasks), (t: Task) => "/images/" + (t.typ match {
        case BUG => "circle_red.png"
        case ENHANCEMENT => "circle_green.png"
        case _ => "circle_blue.png"
      })))),
      sub().register("h2", text(transform(SeqValue(tasks), (t: Task) => t.title))),
      sub().register("p", text(transform(SeqValue(tasks), (t: Task) => t.description))))

    val doc = m.process(classOf[MoulderUseCases].getResourceAsStream("tasks.html"));

    println(doc)
    success
  }
}
