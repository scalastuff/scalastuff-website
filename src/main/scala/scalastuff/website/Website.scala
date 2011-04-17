package scalastuff.website

import com.weiglewilczek.slf4s.Logging
import java.io.{File,InputStream, OutputStream}
import java.net.URI
import javax.servlet.{FilterConfig,ServletRequest,ServletResponse,FilterChain}
import javax.servlet.http.{HttpServletRequest}
import org.apache.commons.io.IOUtils
import org.apache.http.impl.cookie.DateUtils
import unfiltered.filter._
import unfiltered.request._
import unfiltered.response._
import scalastuff.webtoolkit.{Page,Context,CurrentPage,PageServer,TemplateBaseURI}
import xml.NodeSeq
import scala.collection.mutable

class Website extends Plan with Logging {
   
  private var _pageServer : PageServer = null
  def pageServer = _pageServer
  
  def Caching =
    CacheControl("max-age=3600") ~>
  	LastModified(DateUtils.formatDate(new java.util.Date)) ~>
  	ETag(Server.hashCode.toString)

  def stream(resource : String) = new ResponseStreamer {
    def stream(os: OutputStream) = IOUtils.copy(getClass.getResourceAsStream("resources/" + resource), os)
  }
  	
  override def init(config : FilterConfig) = {
    super.init(config)
    logger info "ScalaStuff website started"
    try {
    	implicit var context = new Context
    	context ++= (TemplateBaseURI -> new URI("classpath:/scalastuff/website/"))
    	_pageServer = new PageServer()
    } catch {
    	case e => 
    		logger error e.getMessage
    		logger error (e.getMessage, e)
    }
  }
  

  override def doFilter(request : ServletRequest, response : ServletResponse, chain : FilterChain) {
  	val start = System.nanoTime
		super.doFilter(request, response, chain)
  	val end = System.nanoTime
  	val requestString = request match {
  		case r : HttpServletRequest => r.getRequestURI
  		case r => r.getProtocol
  	}
  	
  	logger.info("Handling request %s (%.2f ms) ".format(requestString, (end-start) / 1000000.0))
  }
  
  def intent = {
    case Path(Seg("resources" :: resource)) => Caching ~> stream(resource.mkString("/"))
    case Path(Seg("favicon.ico" :: Nil)) => Caching ~> stream("ScalastuffIcon.png")
    case Path(Seg(path)) if pageServer != null => HtmlContent ~> new ResponseStreamer {
    	def stream(os : OutputStream) = pageServer.renderPage(path, os)
    }
  }
}
