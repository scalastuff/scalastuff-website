package scalastuff.webtoolkit

import xml.{Elem, Node, NodeSeq, XML}
import java.io.OutputStream
import java.net.URI
import Preamble._

trait Page {
	val html : NodeSeq
	val title : String
	val path : List[String]
	def href = path.mkString("/","/","")
	val processors : Seq[Processor]
}

/**
 * Page that is read from a file.
 */
class FileBasedPage(val file : String, val path : List[String], val processors : Seq[Processor])(implicit var context : Context) extends Page {
	context = CurrentTemplateURI.setRelative(file)
	val rawXml = CurrentTemplateURI.loadXML
	val html = Processor.preProcess(rawXml, processors)
	val title = (html \\ "h1").text
}

