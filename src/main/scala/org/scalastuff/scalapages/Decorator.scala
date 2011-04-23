package org.scalastuff.scalapages

import xml.{Elem, Node, NodeSeq, XML}
import Preamble._

class Decorator(val file : String, params : Map[String,NodeSeq])(implicit context : Context) extends Processor {
	
  override def preProcess(recurse: NodeSeq => Context => NodeSeq)(implicit context: Context) = { 
		case r : RootNode => 
  		preProcess2(r.child, recurse)(CurrentTemplateURI.setRelative(file)) 
  }
  
	private def preProcess2(content : NodeSeq, recurse: NodeSeq => Context => NodeSeq)(implicit context: Context) = {
		val xml : NodeSeq = CurrentTemplateURI.loadXML
		recurse(xml copy {
			case e : Elem if e.prefix == "template" && e.label == "content" =>
				content
		})(context)
	}
}  