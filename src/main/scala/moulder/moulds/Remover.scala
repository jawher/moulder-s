package moulder.moulds

import org.jsoup.nodes._
import moulder._

case class Remover(private val remove: Value[Boolean]) extends Moulder {
  import Values._
  def this() = this(Value(true))

  override def process(elementAndData: (Element, Option[Any])): List[(Node, Option[Any])] = {
    remove.bind(elementAndData)
    remove() match {
      case Some(true) | None => List()
      case _ => List(elementAndData)
    }
  }
}
