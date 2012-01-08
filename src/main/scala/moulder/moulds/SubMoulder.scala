package moulder.moulds

import org.jsoup.nodes._
import moulder._
import scala.collection.JavaConversions._

case class SubMoulder() extends Moulder {
  private var cfg = List[(String, List[Moulder])]()

  def register(selector: String, moulders: List[Moulder]) = {
    cfg = (selector, moulders) :: cfg
    this
  }

  def register(selector: String, moulders: Moulder*) = {
    cfg = (selector, moulders.toList) :: cfg
    this
  }

  private def applyMoulder(m: Moulder, nodesAndData: List[(Node, Option[Any])]): List[(Node, Option[Any])] = {
    nodesAndData.flatMap(_ match {
      case ed: (Element, Option[Any]) => m.process(ed)
      case nd: (Node, Option[Any]) => List(nd)
    })
  }

  private def applyMoulders(ms: List[Moulder], nodesAndData: List[(Node, Option[Any])]): List[(Node, Option[Any])] = {
    if (ms.isEmpty)
      nodesAndData
    else
      applyMoulders(ms.tail, applyMoulder(ms.head, nodesAndData))
  }

  private def replace(e: Element, nodes: List[Node]) = {
    nodes.foreach(n => e.before(n.outerHtml))
    e.remove
  }

  override def process(elementAndData: (Element, Option[Any])): List[(Node, Option[Any])] = {
    cfg.foreach(sm => {
      val elements = elementAndData._1.select(sm._1)
      elements.foreach(e => replace(e, applyMoulders(sm._2, List((e, elementAndData._2))).map(_._1)))
    })
    List(elementAndData)
  }
}
