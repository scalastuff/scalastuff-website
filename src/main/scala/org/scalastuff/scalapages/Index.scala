package org.scalastuff.scalapages

import com.weiglewilczek.slf4s.Logging
import java.io.{IOException,InputStream}
import java.net.URI
import xml.{Elem,Node,NodeSeq,XML}
import Preamble._

/**
 * Uses TemplateBaseURI.
 */
class Index(file : String = "", path : List[String] = Nil, val inheritedProcessors : Seq[Processor] = Seq())(implicit var context : Context) extends Logging {
		
	val indexFile = (if (file.endsWith("/")) file else file + "/") + "index.xml"
	
	context = CurrentTemplateURI.setRelative(indexFile) 
	
	// load the index file
	val xml : Elem = CurrentTemplateURI.loadXML
	
	// read parameters
	def params(parent : Node) = parent.child.collect {
		case e: Elem if e.label == "param" => { val c : NodeSeq = e.child; e.get("@name") -> c }
	}.toMap
		 
	// read decorator elements 
	def decorators(parent : Node, inheritedOnly : Boolean = false) = parent.child.collect {
		case e : Elem if e.label == "decorator" && (!inheritedOnly || e ?? "@inherit") => 
			new Decorator(e get "@file", params(e))
	}
	
	// read processor elements 
	def processors(parent : Node, inheritedOnly : Boolean = false) = parent.child.collect {
		case e : Elem if e.label == "processor" && (!inheritedOnly || e ?? "@inherit")  => 
			AvailableProcessors.find(_.name == Some(e get "@class")).
				getOrElse(Throw("Processor '" + e.get("@class") + "' not found"))
	}

	// find sub-indexes
	val subIndexes = 
		xml.child.collect {
			case e : Elem if e.label == "index" =>
				new Index(e get "@file", path ++ e.get("@path").split("/").filter(_.nonEmpty), inheritedProcessors ++ decorators(xml, true) ++ processors(xml, true))
		}
	
	val decorators : Seq[Decorator] = decorators(xml)
	val processors : Seq[Processor] = processors(xml)

	// find pages
	val pages0 = 
		xml.child.collect {
			case e : Elem if e.label == "page" => 
				new FileBasedPage(
					// @path is optional here
						e get "@file",
					path ++ (e \ "@path").text.split("/").filter(_.nonEmpty),
					decorators ++ decorators(e) ++ processors ++ processors(e) ++ inheritedProcessors ++ DefaultProcessors)
		}

  // add a static index page if there is no page without a path attr
	val pages = if (pages0.exists(_.path == path)) pages0
		else pages0 :+ new IndexPage(pages0, path)
	
	// contains all pages of this and all sub-indexes, recursively
	val pageExtent : Seq[Page] = pages ++ subIndexes.flatMap(_.pageExtent)
}

class IndexPage(pages : Seq[Page], val path : List[String]) extends Page {
	val title = path.lastOption.getOrElse("index")
	val html = <ul>{ 
			for (page <- pages) yield <li><a href={page.href}>{page.title}</a></li>	
		}</ul>
	val processors = Seq()
}
