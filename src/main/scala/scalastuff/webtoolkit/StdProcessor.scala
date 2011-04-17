package scalastuff.webtoolkit

import xml.{Elem,Node,Text}
import xml.{Elem,NodeSeq,Text}
import io.Codec

object StdProcessor extends Processor {
	override def preProcess(phase : Int, recurse : NodeSeq => Context => NodeSeq)(implicit context : Context) = {
		case <dummy>{content @ _*}</dummy> => recurse(content)(context)
	}
}