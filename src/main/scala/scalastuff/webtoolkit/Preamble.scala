package scalastuff.webtoolkit

import java.net.URI
import xml.{Elem,Node,NodeSeq}

object Preamble {
	implicit def uriOps(uri : URI) = new UriOps(uri)
	implicit def elemOps(elem : Elem) = new ElemOps(elem)
	implicit def nodeOps(node : Node) = new NodeOps(node)
	implicit def nodeSeqOps(xml : NodeSeq) = new NodeSeqOps(xml)
}