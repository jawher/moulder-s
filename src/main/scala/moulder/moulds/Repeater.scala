package moulder.moulds

import helpers.MouldersApplier
import org.jsoup.nodes._
import moulder._

case class Repeater[A](private val items: Value[List[A]], private val mould: (A, Int) => List[Moulder]) extends Moulder {

  override def process(element: Element): List[Node] = {
    items() match {
      case Some(data) => handleData(element, data)
      case None => Nil
    }
  }

  //  private def handleData(element: Element,  data: List[A]): List[Node] = {
  //    data.zip(0 until data.length).flatMap((item: A, index: Int) => {
  //      val elementCopy = List(copy(element))
  //      MouldersApplier.applyMoulders(mould(item, index), elementCopy)
  //    })
  //  }

  private def handleData(element: Element, data: List[A]): List[Node] = {
    for ((item, index) <- data.zip(0 until data.length);
         elementCopy = List(copy(element));
         produced <- MouldersApplier.applyMoulders(mould(item, index), elementCopy)
    ) yield produced
  }

  private def copy(e: Element) = {
    import scala.collection.JavaConversions._

    val res = e.ownerDocument().createElement(e.tagName())
    e.attributes().foreach(a => res.attr(a.getKey(), a.getValue()))
    res.html(e.html())
    res
  }
}
