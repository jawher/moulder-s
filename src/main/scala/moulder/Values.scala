package moulder

import moulder.values._

object Values {
  implicit def fn2value[A](f: Function0[Option[A]]) = new Value[A] {
    def apply() = f()
  }

  implicit def any2value[A](v: A) = new Value[A] {
    def apply() = Some(v)
  }

  def transform[A, B](delegate: Value[A], f: A => B) = ValueTransformer(delegate, f)

  def html(delegate: Value[String]) = HtmlValue(delegate)

}
