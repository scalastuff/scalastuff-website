package org.scalastuff.scalapages

import xml.{Elem,NodeSeq}

import Preamble._

object ConsolidateHead extends Processor {
	override def preProcess(recurse : NodeSeq => Context => NodeSeq)(implicit context : Context) = {
		case e : Elem if e.prefix == "" && e.label == "html" =>
			var head = NodeSeq.Empty
			val xml = recurse(e.child)(context) copy {
				case <head>{h @ _*}</head> => 
					head ++= h
					NodeSeq.Empty
			}
			<html><head>{head}</head>{xml}</html>
	}
}