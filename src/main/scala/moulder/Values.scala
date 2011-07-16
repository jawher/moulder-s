package moulder

import org.jsoup.Jsoup
import org.jsoup.nodes._
import scala.collection.JavaConversions._
import moulder.values._

object Values {
  implicit def fn2value[A](f: Function0[Option[A]]) = new Value[A] {
    def apply() = f()
  }

  implicit def any2value[A](v: A) = new Value[A] {
    def apply() = Some(v)
  }

  def apply[A](v: => A) = new Value[A] {
    def apply() = Some(v)
  }

  def elemData[A]() = ElementDataValue[A]()

  def transform[A, B](delegate: Value[A], f: A => B) = ValueTransformer(delegate, f)

  def html(delegate: Value[String]) = HtmlValue(delegate)

}
