package moulder.values

import org.jsoup.nodes._
import moulder.Value

case class ValueTransformer[A, B](private val delegate: Value[A], private val f: A => B) extends Value[B] {

  override def bind(elementAndData: (Element, Option[Any])) = {
    delegate.bind(elementAndData)
  }

  def apply() = delegate().map(f)
}
