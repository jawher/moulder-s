package moulder.values

import org.jsoup.nodes._
import moulder.Value

case class ElementDataValue[A]() extends Value[A] {
  private var value: Option[A] = None

  override def bind(elementAndData: (Element, Option[Any])) = {
    value = elementAndData._2.flatMap(x => try {
      Some(x.asInstanceOf[A])
    } catch {
      case e => None
    })
  }

  def apply() = value
}
