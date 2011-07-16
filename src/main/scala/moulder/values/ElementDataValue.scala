package moulder.values

import org.jsoup.nodes._
import moulder.Value

case class ElementDataValue[A]() extends Value[A] {
  private var value: Option[A] = None

  override def bind(elementAndData: (Element, Option[Any])) = {
    value = elementAndData._2 match {
      case v @ Some(x: A) => Some(x.asInstanceOf[A])
      case _ => None
    }
  }

  def apply() = value
}
