package scalastuff.webtoolkit

import xml.{Elem,Node,NodeSeq,Text}
import io.Codec
import Preamble._

object XmlProcessor extends Processor {

	override def process(recurse : NodeSeq => Seq[Producer])(implicit context : Context) : PartialFunction[Node,Seq[Producer]] = {
		case e : Elem => 
		  Seq(Producer("<" + (if (e.prefix != null) e.prefix + ":" else "") + e.label + e.attributes.mkString + ">")) ++ 
		  recurse(e.child) ++
		  Seq(Producer("</" + (if (e.prefix != null) e.prefix + ":" else "") + e.label + ">"))
		case t : Text => 
		  Seq(Producer(t.text))
	}

}
