package scalastuff.website

import com.weiglewilczek.slf4s.Logging
import java.io.{File,InputStream, OutputStream}
import java.net.URI
import javax.servlet.{FilterConfig,ServletRequest,ServletResponse,FilterChain}
import javax.servlet.http.{HttpServletRequest,HttpServletResponse}
import org.apache.commons.io.IOUtils
import org.apache.http.impl.cookie.DateUtils
import unfiltered.filter._
import unfiltered.request._
import unfiltered.response._
import org.scalastuff.scalapages.{Page,Context,CurrentPage,PageServer,TemplateBaseURI}
import xml.NodeSeq
import scala.collection.mutable
import org.scalastuff.scalapages.Preamble._

class Website extends Plan with Logging {
   
  private var _pageServer : PageServer = null
  def pageServer = _pageServer
  
  def Caching = 
    CacheControl("max-age=3600") ~>
  	LastModified(DateUtils.formatDate(new java.util.Date)) ~>
  	ETag(Server.hashCode.toString) 

  override def init(config : FilterConfig) = {
    super.init(config)
    logger info "ScalaStuff website started"
    try {
    	implicit var context = new Context
    	context ++ (TemplateBaseURI ->  new URI("classpath:/scalastuff/website/"))
    	_pageServer = new PageServer()(context)
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
  	case Path(Seg("resources" :: resource)) => Caching ~> Stream2(getClass.getResourceAsStream(resource.mkString("resources/","/","")))
    case Path(Seg("scaladoc" :: resource)) => 
  		if (resource.mkString("/").endsWith(".html")) HtmlContent ~> Caching ~> Stream2(getClass.getResourceAsStream(resource.mkString("/","/","")))
  		else Caching ~> Stream2(getClass.getResourceAsStream(resource.mkString("/","/","")))
    case Path(Seg("scaladoc2" :: resource)) => 
  		if (resource.mkString("/").endsWith(".html")) HtmlContent ~> Caching ~> new ResponseStreamer {
	    	def stream(os : OutputStream) = pageServer.renderPage(resource, context => new ScaladocPage(getClass.getResourceAsStream(resource.mkString("/","/","")), resource, pageServer.processors)(context), os)
	    }
  		else Caching ~> Stream2(getClass.getResourceAsStream(resource.mkString("/","/","")))
    case Path(Seg("favicon.ico" :: Nil)) => Caching ~> Stream2(getClass.getResourceAsStream("ScalastuffIcon.png"))
    case Path(Seg(path)) if pageServer != null => HtmlContent ~> Stream2(pageServer.renderPage(path, _))
    }
}

object Stream2 {
  def apply(f : OutputStream => Unit) = new ResponseStreamer {
    def stream(os : OutputStream) = f(os)
  }
  
  def apply(is : InputStream) = new ResponseStreamer {
    def stream(os : OutputStream) = copy(is, os)
  }
  
  def copy(is : InputStream, os : OutputStream) { 
  	try {
  	  val b = new Array[Byte](4096)  
  	  var read : Int = 0;
  	  do {
  	  	os.write(b, 0, read);  
  	  	read = is.read(b)
  	  } while (read != -1)
  	} finally {
  	  is.close
  	  os.close
  	}
  }
}

