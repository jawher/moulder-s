package jawher.moulder

import org.jsoup.Jsoup
import org.jsoup.nodes._
import scala.collection.JavaConversions._

object M { 
  case class SubMoulder extends Moulder { 
    private var cfg = List[(String, List[Moulder])]()

    def register(selector: String, moulders: List[Moulder]) = { 
      cfg = (selector, moulders)::cfg
      this
    }

    private def applyMoulder(m: Moulder, nodesAndData: List[(Node, Option[Any])], u: MoulderUtils): List[(Node, Option[Any])] = { 
      nodesAndData.flatMap(_ match { 
        case ed: (Element, Option[Any]) => m.process(ed, u)
        case nd: (Node, Option[Any]) => List(nd)
      })
    }

    private def applyMoulders(ms: List[Moulder], nodesAndData: List[(Node, Option[Any])], u: MoulderUtils): List[(Node, Option[Any])] = {
      if(ms.isEmpty)
        nodesAndData
      else
        applyMoulders(ms.tail, applyMoulder(ms.head, nodesAndData, u), u)
    }

    private def replace(e: Element, nodes: List[Node]) = { 
      nodes.foreach(n => e.before(n.outerHtml))
      e.remove
    }

    override def process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      cfg.foreach(sm => {
        val elements=elementAndData._1.select(sm._1)
        elements.foreach(e => replace(e, applyMoulders(sm._2, List((e, elementAndData._2)), u).map(_._1)))
      })
      List(elementAndData)
    }
  }

  def sub() = SubMoulder()

  case class Appender(private val content: Value[List[Node]]) extends Moulder { 
    override def  process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      content.bind(elementAndData)
      List(elementAndData) ::: (content() match { 
        case Some(nodes: List[Node])=> nodes.map((_, elementAndData._2))
        case None => Nil
      })
    }
  }

  def append(content: Value[List[Node]]) = Appender(content)

  case class Prepender(private val content: Value[List[Node]]) extends Moulder { 
    override def  process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      content.bind(elementAndData)
      (content() match { 
        case Some(nodes: List[Node]) => nodes.map((_, elementAndData._2))
        case None => Nil
      }) ::: List(elementAndData)
    }
  }

  def prepend(content: Value[List[Node]]) = Prepender(content)

  case class ChildAppender(private val content: Value[List[Node]]) extends Moulder { 
    override def  process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      content.bind(elementAndData)
      content().foreach(_.foreach(elementAndData._1.appendChild(_)))
      List(elementAndData)
    }
  }

  def appendChildren(content: Value[List[Node]]) = ChildAppender(content)

  case class ChildPrepender(private val content: Value[List[Node]]) extends Moulder { 
    override def  process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      content.bind(elementAndData)
      (content() match { 
        case Some(nodes: List[Node])=> nodes
        case None => Nil
      }).reverse.foreach(elementAndData._1.prependChild(_))
      List(elementAndData)
    }
  }

  def prependChildren(content: Value[List[Node]]) = ChildPrepender(content)

  case class Remover(private val remove: Value[Boolean]) extends Moulder {
    def this() = this(V(true))
    
    override def process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      remove.bind(elementAndData)
      remove() match { 
        case Some(true) | None => List()
        case _ => List(elementAndData)
      }
    }
  }

  def remove() = Remover(V(true))
  def remove(remove: Value[Boolean]) = Remover(remove)

  case class Repeater[A](private val items: Value[List[A]]) extends Moulder { 
    override def  process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      items.bind(elementAndData)
      items() match { 
        case Some(data) => data.map((i: A)=> (u.copy(elementAndData._1), Some(i)))
        case None => Nil
      }
    }
  }

  case class Replacer(private val content: Value[List[Node]]) extends Moulder { 
    override def  process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      content.bind(elementAndData)
      content() match { 
        case Some(nodes: List[Node]) => nodes.map((_, elementAndData._2))
        case None => Nil
      }
    }
  }

  def replace(content: Value[List[Node]]) = Replacer(content)


  def repeat[A](items: Value[List[A]]) = Repeater(items)

  case class AttrModifier(private val attr: Value[String], private val value: Value[String]) extends Moulder { 
    override def  process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      attr.bind(elementAndData)
      attr() match { 
        case Some(a)=> { 
          value.bind(elementAndData)
          value() match { 
            case Some(v) => elementAndData._1.attr(a, v); List(elementAndData)
            case None => elementAndData._1.removeAttr(a); List(elementAndData)
          }
        }
        case None => List(elementAndData)
      }
    }
  }

  def attr(attr: Value[String], value: Value[String]) = AttrModifier(attr, value)

  case class Texter(private val text: Value[String]) extends Moulder { 
    override def  process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      text.bind(elementAndData)
      text().foreach(elementAndData._1.text(_))
      List(elementAndData)
    }
  }

  def text(text: Value[String]) = Texter(text)

  case class Nop extends Moulder { 
    override def  process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      List(elementAndData)
    }
  }

  def nop() = Nop()

  case class If(private val condition: Value[Boolean], 
                private val thenMoulder: Moulder,
                private val elseMoulder: Moulder) extends Moulder { 
    override def  process(elementAndData: (Element, Option[Any]), u: MoulderUtils): List[(Node, Option[Any])] = { 
      condition.bind(elementAndData)
      condition() match { 
        case Some(true) => thenMoulder.process(elementAndData, u)
        case _ => elseMoulder.process(elementAndData, u)
      }
    }
  }

  def ifm(condition: Value[Boolean], theMoulder: Moulder, elseMoulder: Moulder) = If(condition, theMoulder, elseMoulder)

}
