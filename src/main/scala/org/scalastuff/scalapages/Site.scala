package org.scalastuff.scalapages

import grizzled.slf4j.Logging
import java.io.{File,IOException,InputStream}
import java.net.URI
import xml.{Elem,Node,NodeSeq,XML}
import Preamble._

/**
 * Uses TemplateBaseURI.
 */
class Site(parent : Option[Site] = None, file : String = "", val path : List[String] = Nil, val inheritedProcessors : Seq[Processor] = Seq())(implicit var context : Context) extends Logging {
		
  def root : Site = parent match {
    case Some(site) => site.root
    case None => this
  }
  
	val siteFile = (if (file.endsWith("/")) file else file + "/") + "site.xml"
	context = CurrentTemplateURI.setRelative(siteFile) 
	val siteUri = CurrentTemplateURI.resolve(".").ensureEndSlash
	
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
		  Processor.load(e get "@class")
	}
	
	// find sub-sites
	val subSites = 
		xml.child.collect {
			case e : Elem if e.label == "site" =>
			  val nestedFile = e get "@file"
			  val nestedPath = e.getOrElse("@path", new URI(nestedFile).resolve(".").toString).split("/").filter(_.nonEmpty)
				new Site(Some(this), nestedFile, path ++ nestedPath, inheritedProcessors ++ decorators(xml, true) ++ processors(xml, true))
		}
	
	val decorators : Seq[Decorator] = decorators(xml)
	val processors : Seq[Processor] = processors(xml)

	// default page
	def virtualPage(path : List[String])(implicit context : Context) = 
		xml.child.collect {
			case e : Elem if e.label == "virtual-page" && path.mkString("/").matches(e getOrElse("@pattern", ".*")) => 
				mkPage(e get "@file", path, decorators ++ decorators(e) ++ processors ++ processors(e) ++ inheritedProcessors ++ DefaultProcessors)
		}.headOption


	// declared pages
	val pages0 = 
		xml.child.collect {
			case e : Elem if e.label == "page" => 
				mkPage(
					// @path is optional here
						e get "@file",
					path ++ (e \ "@path").text.split("/").filter(_.nonEmpty),
					decorators ++ decorators(e) ++ processors ++ processors(e) ++ inheritedProcessors ++ DefaultProcessors)
		}

	// scanned pages
	val pages1 : Seq[Page] = (xml \\ "scan").text match {
	  case "" => Seq()
	  case pattern =>  
	    val stopFiles = subSites.map(s => new File(s.siteUri)).toSet
	    def scan(file : File, p : List[String]) : Seq[Page] = {
	      if (file.isDirectory && !stopFiles.contains(file)) {
	        file.listFiles.flatMap(scan(_, p :+ file.getName))
	      } else if (file.getName.matches(pattern)) Seq(
	          mkPage(file.toURI.toString, path ++ p,
					decorators ++ processors ++ inheritedProcessors ++ DefaultProcessors))
	        else Seq()
	    }
	    scan(new File(siteUri), Nil)
	}	

  // add a static index page if there is no page without a path attr
//	val pages2 = if (pages0.exists(_.path == path)) Seq() else Seq(new SiteIndexPage("index.html", this, pages0 ++ pages1, path))

	val pages = pages0 ++ pages1 //++ pages2
	
	// contains all pages of this and all sub-indexes, recursively
	val pageExtent : Seq[Page] = pages ++ subSites.flatMap(_.pageExtent)
	
	def loadPage(path : List[String]) : Option[Page] = {
		val file = path.drop(this.path.size).mkString("/")
		if (path.startsWith(this.path)) 
		  subSites.flatMap(_.loadPage(path)).headOption match {
		  	case Some(page) => Some(page)
		  	case None => try {
		  	  // try a scanned page
		  	  // TODO check pattern
		  	  Some(mkPage(file, path, decorators ++ processors ++ inheritedProcessors ++ DefaultProcessors)) 
		  	} catch {
		  	  case e => virtualPage(path.drop(this.path.size)) match {
		  	    case Some(page) => Some(page)
		  	    case None => throw e
		  	  }
		  	}
		}
		else None
	}
	
	def mkString(prefix : String = "\n  ") : String = 
	  prefix + "Site: " + path.mkString("/","/","") + "(" + pages.size + " pages) " + subSites.map(_.mkString(prefix + "  ")).mkString("")
	  
	private def mkPage(file : String, path : List[String], processors : Seq[Processor])(implicit context : Context) : Page = {
	   val context2 = CurrentTemplateURI.setRelative(file)
	   new Page(this, path, CurrentTemplateURI.loadXML(context2), processors,  context2)
	}
}
//
//class SiteIndexPage(val file : String, val site : Site, pages : Seq[Page], val path : List[String]) extends Page {
//	val title = path.lastOption.getOrElse("index")
//	val xml = <ul>{ 
//			for (page <- pages) yield <li><a href={page.href}>{page.title}</a></li>	
//		}</ul>
//	val processors = Seq()
//}

//class VirtualPage(val site : Site, val file : String, val processors : Seq[Processor])(implicit var context : Context) extends Page {
//	context = CurrentTemplateURI.setRelative(file)
//	context ++= (CurrentPage -> this) 
//	val rawXml = CurrentTemplateURI.loadXML
//	val xml = Processor.preProcess(rawXml, processors)
//	val title = (xml \\ "h1").text
//	
//	def process : Seq[Producer] = Processor.process(xml, processors)
//}