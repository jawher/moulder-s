package moulder

import org.jsoup.Jsoup
import org.jsoup.nodes._
import moulder.moulds._

object Moulds {
  import Values._

  def sub() = SubMoulder()

  def append(content: Value[List[Node]]) = Appender(content)

  def prepend(content: Value[List[Node]]) = Prepender(content)

  def appendChildren(content: Value[List[Node]]) = ChildAppender(content)

  def prependChildren(content: Value[List[Node]]) = ChildPrepender(content)

  def remove() = Remover(true)
  def remove(remove: Value[Boolean]) = Remover(remove)

  def replace(content: Value[List[Node]]) = Replacer(content)

  def repeat[A](items: Value[List[A]], m: (A, Int) => List[Moulder]) = Repeater(items, m)


  def repeat[A](items: Value[List[A]]) = Repeater(items, (item: A, index: Int) => Nil)

  def repeatN(n: Value[Int]) = Repeater(Value.of(n().map(0 until _).map(_.toList)), (item: Int, index: Int) => Nil)

  def repeat[A](items: Value[List[A]], moulds: List[Moulder]) = Repeater(items, (item: A, index: Int) => moulds)

  def attr(attr: Value[String], value: Value[String]) = AttrModifier(attr, value)

  def text(text: Value[Any]) = Texter(text)

  def nop() = Nop()

}
