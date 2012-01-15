package moulder.moulds.helpers

import moulder.Moulder
import org.jsoup.nodes.{Element, Node}

object MouldersApplier {
  private def applyMoulder(m: Moulder, nodes: List[Node]): List[Node] = {
    nodes.flatMap(_ match {
      case ed: Element => m.process(ed)
      case nd: Node => List(nd)
    })
  }

  def applyMoulders(ms: List[Moulder], nodes: List[Node]): List[Node] = {
    if (ms.isEmpty)
      nodes
    else
      applyMoulders(ms.tail, applyMoulder(ms.head, nodes))
  }

}