package org.scalastuff.scalapages

import java.net.URI
import xml.{Node,NodeSeq,Elem}

object Preamble {
	implicit def uriOps(uri : URI) = new UriOps(uri)
	implicit def elemOps(elem : Elem) = new ElemOps(elem)
	implicit def nodeOps(node : Node) = new NodeOps(node)
	implicit def nodeSeqOps(xml : NodeSeq) = new NodeSeqOps(xml)
	implicit def stringOps(s : String) = new StringOps(s)
}