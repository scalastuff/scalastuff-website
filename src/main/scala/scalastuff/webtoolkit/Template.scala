package scalastuff.webtoolkit

import com.weiglewilczek.slf4s.Logging
import Preamble._
import java.io.{IOException, InputStream}
import java.net.URI
import scala.io.Codec
import scala.xml.{Elem, Node, NodeSeq, Text, XML}

object TemplateBaseURI extends ContextVar[URI](new URI("classpath:/"))

object CurrentTemplateURIs extends ContextVar[List[URI]](Nil) 
 
object CurrentTemplateURI extends ContextVar[URI](new URI("")) {
	def setRelative[U](file : String)(implicit context : Context) = {
		val uri = file match {
			case s if s startsWith "/" => TemplateBaseURI.resolve(s.substring(1))
			case s => TemplateBaseURI.resolve(get).resolve(s)
		}
		if (CurrentTemplateURIs.contains(uri)) Throw("Cyclic file: " + getRelative)
		else context ++ (this -> uri, CurrentTemplateURIs -> (uri :: CurrentTemplateURIs))
	}
	def getRelative(implicit context : Context) = TemplateBaseURI.relativize(get)
	def loadXML(implicit context : Context) : NodeSeq = 
		try XML.load(get.openStream) catch { case e => Throw(e) }
}

object TemplateProcessor extends Processor {
	override val name = Some("templates")
	override def preProcess(phase : Int, recurse: NodeSeq => Context => NodeSeq)(implicit context : Context) = { 
		case e : Elem if e.prefix == "template" && e.label == "include" =>
			// call method with new context
			preProcess2(e.child, recurse)(CurrentTemplateURI.setRelative(e get "@file"))
	}

	def preProcess2(content : NodeSeq, recurse: NodeSeq => Context => NodeSeq)(implicit context : Context) = {
		
	  // filter out named sections, e.g. <template:header-section>...<template:header-section>
		val (contentNodes,namedSections) = content.partition(node => node.prefix != "template" || node.label == "content")
		
		// make map of sections, default section get name "content
		val sections = Map("content" -> contentNodes) ++ Map(namedSections.map(n => (n.label, n.child)):_*)
		
		// replace sections in this template
		val xml = recurse(CurrentTemplateURI.loadXML)(context)

		var availableSections = Set[String]()
		val xml2 = xml copy {
			case e : Elem if e.prefix == "template" && e.label == "include" => e 
			case e : Elem if e.prefix == "template" =>
				availableSections += e.label
				sections.get(e.label) match {
					case Some(xml) => recurse(xml)(context)
					case None => NodeSeq.Empty 
				}
		}
		
		namedSections.foreach(section => if (!availableSections.contains(section.label)) Throw("Section '" + section.label + "' not defined by template '" + CurrentTemplateURI.getRelative + "'\n  available sections: " + availableSections.mkString(", ")))
		xml2
	}
}
