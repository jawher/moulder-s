package moulder.values

import moulder.Value

case class ValueTransformer[A, B](private val delegate: Value[A], private val f: A => B) extends Value[B] {

  def apply() = delegate().map(f)
}
