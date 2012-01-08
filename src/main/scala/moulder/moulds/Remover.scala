package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Remover(private val remove: Value[Boolean]) extends Moulder {
  import Values._
  def this() = this(Value(true))

  override def process(element: Element): List[Node] = {
    remove() match {
      case Some(true) | None => List()
      case _ => List(element)
    }
  }
}
