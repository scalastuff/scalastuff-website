package scalastuff.webtoolkit

import scala.xml.{Elem, Node, NodeSeq, Text, XML}
import io.Codec

class Bean

object CurrentBeans extends ContextVar[Map[String,Bean]](Map())

trait BeanProcessor extends Processor {
	override def process(recurse : NodeSeq => Seq[Producer])(implicit context : Context) = {
		case e : Elem if e.prefix != null && CurrentBeans.contains(e.prefix) => Seq()
	}
}
