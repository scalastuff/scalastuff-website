package org.scalastuff.scalapages

import grizzled.slf4j.Logging
import collection.mutable
import java.util.concurrent.ConcurrentHashMap
import java.io.OutputStream

import java.net.URI
import io.Codec
import xml.{Elem,NodeSeq}
import collection.JavaConversions._	

/**
 * Page that is currently being processed.
 */
object CurrentPage extends ContextVar[Page]

object CurrentPath extends ContextVar[List[String]](Nil)

object DefaultProcessors extends ContextVar[Seq[Processor]](MenuProcessor :: TrailProcessor :: XmlProcessor :: StdProcessor :: TemplateProcessor :: ConsolidateHead :: Nil)

object AvailableProcessors extends ContextVar[Seq[Processor]](XmlProcessor :: MenuProcessor :: TrailProcessor :: Nil)

object OutputCodec extends ContextVar[Codec](Codec.UTF8)

class PageServer(implicit var context : Context) extends Logging {
  
	private val staticPageMap = mutable.Map[List[String], Seq[Producer]]()
	private val dynamicPageMap = new java.util.concurrent.ConcurrentHashMap[List[String], Seq[Producer]]
				
	val processors = DefaultProcessors.get
	val codec = OutputCodec.get
	
	val site = try {
		// (note: uses TemplateBaseURI)
		// read main site and sub sites
		val site = new Site
		
		// pre-process known pages
		val preProcessedPages =
		  for (page <- site.pageExtent) 
		  	yield (page, preProcess(page.path, page))

		// process them
		for ((page, xml) <- preProcessedPages)
			staticPageMap.put(page.path, process(page.path, page, xml))	
		  
		// log
//			logger.info(site.pageExtent.map(p => p.path.mkString("/","/",": " + p.title)).mkString("Page server started:\n  ", "\n  ", ""))
		logger info site.mkString()
		site
	} catch {
		case e => 
		logger.error(e.getMessage)
		throw e
	}
	
	/**
	 * Serve page at path.
	 */
	def renderPage(path : List[String], os : OutputStream) {
	  
	  // first try the static map
		val producers = staticPageMap.getOrElse(path, 
		    
		    // try dynamically loaded pages
				dynamicPageMap.getOrElseUpdate(path, 
				    
				    // load for first time
				    site.loadPage(path) match {
						  case Some(page) => process(path, page, preProcess(path, page))
						  case None => Throw("Page not found: " + path.mkString("/"))
						}))
				
		// produce output
		producers.foreach(_ produce os)
	}
	
	private def preProcess(path : List[String], page : Page) : NodeSeq =  
	  Processor.preProcess(page.xml, page.processors)(page.context ++ (CurrentPath -> path, CurrentPage -> page))

	private def process(path : List[String], page : Page, xml : NodeSeq) : Seq[Producer] = 
  	Processor.process(xml, page.processors)(page.context ++ (CurrentPath -> path, CurrentPage -> page))

}
