package scalastuff.webtoolkit

import xml.{Elem,Node,Text}
import xml.{Elem,NodeSeq,Text}
import Preamble._

object ConsolidateHead extends Processor {
	override def preProcess(phase : Int, recurse : NodeSeq => Context => NodeSeq)(implicit context : Context) = {
		case e : Elem if e.label == "html" =>
			var head = NodeSeq.Empty
			val xml = recurse(e.child)(context) copy {
				case <head>{h @ _*}</head> => 
					head ++= h
					NodeSeq.Empty
			}
			<html><head>{head}{xml}</head></html>
	}
}