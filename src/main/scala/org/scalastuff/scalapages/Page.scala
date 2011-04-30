package org.scalastuff.scalapages

import xml.{Elem, Node, NodeSeq, XML}
import java.io.OutputStream
import java.net.URI
import Preamble._

class Page(val site : Site, val path : List[String], val xml : NodeSeq, val processors : Seq[Processor], val context : Context) 

/**
 * Page that is read from a file.
 */
//class FileBasedPage(val site : Site, val file : String, val path : List[String], val processors : Seq[Processor])(implicit var context : Context) extends Page {
//	context = CurrentTemplateURI.setRelative(file)
//	context ++= (CurrentPage -> this) 
//	val rawXml = CurrentTemplateURI.loadXML
//	val xml = Processor.preProcess(rawXml, processors)
//	val title = (xml \\ "h1").text
//	
//	def process : Seq[Producer] = Processor.process(xml, processors)
//}
//
