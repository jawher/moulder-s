package moulder.values

import moulder.Value

case class SeqValue[A](private val values: List[A]) extends Value[A] {
  private val it = values.iterator
  def apply() = if (it.hasNext) Some(it.next) else None

  def cycle(): Value[A] = new Value[A] {
    private var s = values.iterator

    def apply() = if (s.hasNext)
      Some(s.next)
    else {
      if (values.iterator.hasNext) {
        s = values.iterator; Some(s.next)
      } else
        None
    }
  }
}
