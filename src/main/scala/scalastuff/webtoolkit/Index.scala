package scalastuff.webtoolkit

import com.weiglewilczek.slf4s.Logging
import java.io.{IOException,InputStream}
import java.net.URI
import xml.{Elem,Node,NodeSeq,XML}
import Preamble._

object StaticPage extends Logging {
	
	def load(uri : URI) : Elem = try {
		XML.load(uri.openStream)
	} catch {
		case e : IOException => 
		logger warn "Couldn't find resource: " + uri
		<dummy/>
	}
	
	def load(path : List[String], uri : URI, decoratorFactory : Map[String, Map[String,NodeSeq] => Page => PageDecorator]) : Seq[Page] = {
		val xml = load(uri.resolve("index.xml"))
		
		def decorator(node : Node) : Option[Page => PageDecorator] = node match {
			case d : Elem if d.label == "decorator" =>
				val pars = Map(d.child.collect {case e: Elem => val c:NodeSeq = e.child; e.label -> c}:_*)
				println("PARS:" + pars)
				val kind = (d \ "@kind").text
				val file = (d \ "@file").text
				if (kind != "") decoratorFactory.get(kind) match {
					case Some(d) => Some(d(pars))
					case None =>
						logger warn "Decorator not found: " + kind
						None
				} else if (file != "") {
					val decoratorXml = load(uri.resolve(file))
					Some((page : Page) => new StaticDecorator(page, pars, decoratorXml))
				}
				else {
					logger error "Should specify either 'kind' or 'file' attribute in <decorator>"
					None
				}
			case _ => None
		}

		val decorators2 = xml.child
		val decorators = xml.child.flatMap(decorator(_))

		val pageEntries = for (node <- xml.child) yield node match {
			case p @ <page/> => Some((p \ "@path").text -> (p \ "@file").text -> p \\ "decorator")
			case _ => None
		}
		var indexPageFound = false
		val pageSeqs = pageEntries.flatten map { t =>
		  val (file, isHtml) = t._1._2 match {
		  	case s if s.endsWith(".html") => (s, true)
		  	case s if s.endsWith("/") => (s, false)
		  	case s => (s + "/", false)
		  }
		  val pathExt = if (t._1._1 != "") t._1._1.split("/").toList else {indexPageFound = true; Nil}
		  val pageDecorators = decorators ++ t._2.flatMap(decorator(_))
			if (isHtml) Seq(new StaticPage(decorators, path ++ pathExt, load(uri.resolve(file))))
			else load(path ++ pathExt, uri.resolve(file), decoratorFactory)
		}
		val pages = pageSeqs.flatten
		if (indexPageFound) pages
		else new StaticIndexPage(pages, decorators, path, uri) +: pages
	}
}

class StaticIndexPage(pages : Seq[Page], decoratorCtors : Seq[Page => PageDecorator], val path : List[String], uri : URI) extends Page {
	val xml : NodeSeq = 
		<ul>{ 
			for (page <- pages) yield <li><a href={page.href}>{page.title}</a></li>	
		}</ul>
	val title = path.lastOption.getOrElse("index")
	def html(implicit request : PageRequest) = xml
	override lazy val decorators : Seq[PageDecorator] = decoratorCtors.map(_(this))
}

class StaticPage(decoratorCtors : Seq[Page => PageDecorator], val path : List[String], val xml : NodeSeq) extends Page {
	val title = (xml \\ "h1").text
	def html(implicit request : PageRequest) = xml
	override lazy val decorators : Seq[PageDecorator] = decoratorCtors.map(_(this))
}

class StaticDecorator(val page : Page, pars : Map[String,NodeSeq], decoratorXml : NodeSeq) extends PageDecorator {
	def decorate(xml : NodeSeq)(implicit request : PageRequest) : NodeSeq = {
		
		def process(nodes : NodeSeq) : Seq[NodeSeq] = for (node <- nodes) yield node match {
			case <content/> => xml
			case elem : Elem => elem.copy(child = process(elem.child).flatten)
			case node => node
		}
		process(decoratorXml).flatten
	}
}