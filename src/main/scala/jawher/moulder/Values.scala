package jawher.moulder

import org.jsoup.Jsoup
import org.jsoup.nodes._
import scala.collection.JavaConversions._

object V { 
  implicit def fn2value[A](f: Function0[Option[A]]) = new Value[A] { 
    def apply() = f()
  }

  implicit def any2value[A](v: A) = new Value[A] { 
    def apply() = Some(v)
  }

  def apply[A](v: => A) = new Value[A] { 
    def apply() = Some(v)
  }

  val none = new Value[Nothing] { 
    def apply() = None
  }

  case class Values[A](private val values: List[A]) extends Value[A] { 
    private val it = values.iterator
    def apply() = if (it.hasNext) Some(it.next) else None

    def cycle() : Value[A] = if(values.isEmpty) none else new Value[A] { 
      private var s = values.iterator
      
      def apply() = if(s.hasNext) Some(s.next) else { s=values.iterator; Some(s.next)}
    }
  }

  case class ElementDataValue[A] extends Value[A] { 
    private var value: Option[A] = None

    override def bind(elementAndData: (Element, Option[Any])) = { 
      value= elementAndData._2 match { 
        case v @ Some(x: A) => Some(x.asInstanceOf[A])
        case _ => None
      }
    }

    def apply() = value
  }

  def eData[A]() = ElementDataValue[A]()

  case class ValueTransformer[A, B](private val delegate: Value[A], private val f: A=>B) extends Value[B] { 

    override def bind(elementAndData: (Element, Option[Any])) = { 
      delegate.bind(elementAndData)
    }

    def apply() = delegate().map(f)
  }
  
  def tr[A, B](delegate: Value[A], f: A=>B) = ValueTransformer(delegate, f)

  case class HtmlValue(private val delegate: Value[String]) extends Value[List[Node]] { 

    override def bind(elementAndData: (Element, Option[Any])) = { 
      delegate.bind(elementAndData)
    }

    def apply() = delegate().map((h: String) => new JListWrapper(Jsoup.parseBodyFragment(h).body().childNodes()).toList)
  }

  def h(delegate: Value[String]) = HtmlValue(delegate)

}
