package org.scalastuff.scalapages

import xml.{Elem,Node,NodeSeq,Text}
import io.Codec
import Preamble._

object XmlProcessor extends Processor {

	override def process(recurse : NodeSeq => Context => Seq[Producer])(implicit context : Context) : PartialFunction[Node,Seq[Producer]] = {
		case e : Elem =>
			(if (e.label == "html") Seq(Producer("<!DOCTYPE html>")) else Seq()) ++
		  Seq(Producer("<" + (if (e.prefix != null) e.prefix + ":" else "") + e.label + e.attributes + ">")) ++ 
		  recurse(e.child)(context) ++
		  Seq(Producer("</" + (if (e.prefix != null) e.prefix + ":" else "") + e.label + ">"))
		case t : Text => 
		  Seq(Producer(t.text))
	}

}
