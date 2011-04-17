package scalastuff.webtoolkit

import scala.xml.{Elem, Node, NodeSeq, Text, XML}

object XmlOps {
	def copy(node : Node)(fn : PartialFunction[Node, NodeSeq]) : NodeSeq = {
		if (fn.isDefinedAt(node)) fn(node)
		else node match {
			case e : Elem => e.copy(child = e.child.flatMap(copy(_)(fn)))
			case n => n
		}
	}
}

class NodeOps(node : Node) {
	def copy(fn : PartialFunction[Node, NodeSeq]) : NodeSeq = XmlOps.copy(node)(fn)
}
	
class ElemOps(elem : Elem) {
	
	def get(attr : String)(implicit context : Context) = (elem \ attr).text match {
		case "" => Throw("Missing '" + attr + "' in element '" + elem.label)
		case s => s
	}
	
	def getOrElse(attr : String, defaultValue : => String)(implicit context : Context) = (elem \ attr).text match {
		case "" => defaultValue
		case s => s
	}
	
	def getOrElse(attr : String, defaultValue : => Int)(implicit context : Context) : Int = (elem \ attr).text match {
		case "" => defaultValue
		case s => s.toInt
	}
	
	def ??(s : String)(implicit context : Context) = (elem \ s).text == "true"
}

class NodeSeqOps(xml : NodeSeq) {
	def copy(fn : PartialFunction[Node, NodeSeq]) = xml.flatMap(XmlOps.copy(_)(fn))
}
