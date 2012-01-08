package moulder

import org.jsoup.nodes._

trait Value[+A] extends Function0[Option[A]] {
  def bind(elementAndData: (Element, Option[Any])): Unit = {}
}

object Value {
  def apply[A](a: A) = new Value[A]() {
    def apply() = Some(a)
  }
}
