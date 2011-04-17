package scalastuff.webtoolkit

import com.weiglewilczek.slf4s.Logging
import collection.mutable
import java.util.concurrent.ConcurrentHashMap
import java.io.OutputStream
import java.net.URI
import io.Codec
import collection.JavaConversions._	

/**
 * Page that is currently being processed.
 */
object CurrentPage extends ContextVar[Page](null)

/**
 * Pages that are known upon startup. Usually, the ones
 * referred to by index.xml files.
 */
object IndexedPages extends ContextVar[Seq[Page]](Nil)

object DefaultProcessors extends ContextVar[Seq[Processor]](XmlProcessor :: StdProcessor :: TemplateProcessor :: ConsolidateHead :: TrailProcessor :: Nil)

object AvailableProcessors extends ContextVar[Seq[Processor]](XmlProcessor :: MenuProcessor :: TrailProcessor :: Nil)

object OutputCodec extends ContextVar[Codec](Codec.UTF8)

class PageServer(implicit var context : Context) extends Logging {
		
	private val pageMap : mutable.Map[List[String], Seq[Producer]] = new java.util.concurrent.ConcurrentHashMap[List[String], Seq[Producer]]
				
	val processors = DefaultProcessors.get
	val codec = OutputCodec.get
	
	try {
		/**
		 * Main index for site
		 */
		// (note: uses TemplateBaseURI)
		val index = new Index
		
		// store indexed pages in context
		context ++= (IndexedPages -> index.pageExtent)
		
		// pre-produce indexed pages
		index.pageExtent.foreach { page =>
			context ++= (CurrentPage -> page)
			pageMap.put(page.path, processPage(page))	
		}
		logger.info(index.pageExtent.map(p => p.path.mkString("/","/",": " + p.title)).mkString("Page server started:\n  ", "\n  ", ""))
	} catch {
		case e => 
		e.printStackTrace
		logger.error(e.getMessage)
	}
	
	/**
	 * Serve page at path.
	 */
	def renderPage(path : List[String], os : OutputStream) {
		val producers = pageMap.getOrElseUpdate(path, Seq())
		producers.foreach(_ produce os)
	}
	
	private def processPage(page : Page) : Seq[Producer] = {
		Processor.process(page.html, page.processors ++ processors)(context)
	}
}