package org.scalastuff.scalapages.http

import org.scalastuff.scalapages.{PageServer,TemplateBaseURI,Context}
import grizzled.slf4j.Logging
import javax.servlet.{Filter,FilterConfig,ServletRequest,ServletResponse,FilterChain,ServletException}
import javax.servlet.http.{HttpServletRequest,HttpServletResponse}
import java.io.{File,InputStream,OutputStream}
import java.net.URI

abstract class ScalaPagesFilter extends Filter with Logging {
  
  var pageServer : PageServer = null
  implicit var context : Context = new Context
  
  override def init(config : FilterConfig) {
    try {
      val realClasspath = config.getServletContext.getRealPath("WEB-INF/classes")
      if (realClasspath != null && new File(realClasspath).isDirectory) {
      	context ++= TemplateBaseURI -> new File(realClasspath).toURI
      } else {
      	context ++= TemplateBaseURI -> new URI("classpath:/")
      }
      logger.info("Template base URI: " + TemplateBaseURI.get)
      
      init
    	pageServer = new PageServer()(context)
    } catch {
    	case e => 
    		logger error e.getMessage
    		logger trace (e.getMessage, e) 
    }
  }
  override def doFilter(request : ServletRequest, response : ServletResponse, chain : FilterChain) {
  	val start = System.nanoTime

  	val requestString = request match {
  	case r : HttpServletRequest => r.getRequestURI
  	case r => r.getProtocol
  	}
  	
  	try {
	  	request match {
	  	  case request : HttpServletRequest =>
	  	  val path = request.getServletPath.split("/").toList.filter(_.nonEmpty)
	  	  val req = new Request(path)
	  	  val pf = handle
	  	  if (pf.isDefinedAt(req))  {
	  	    pf(req).respond(response.asInstanceOf[HttpServletResponse])
	  	  } else {
	  	  	chain.doFilter(request, response)
	  	  }
	  	}
		  val end = System.nanoTime
	  	logger.info("Handled request %s (%.2f ms) ".format(requestString, (end-start) / 1000000.0))
  	} catch {
  	  case e => 
  	    logger.error("Error handling request %s: %s".format(requestString, e.getMessage))
    		logger.trace("Error handling request %s",e)
  	}
  }
  def destroy {}
	
  def init = {}
  def handle : PartialFunction[Request, Response]
}

case class Request(path : List[String])

trait Response {
  def respond(response : HttpServletResponse)
  def + (response : Response) = new CompoundResponse(Vector(this, response))
}

class HeaderResponse(name : String, value : String) extends Response {
	def respond(response : HttpServletResponse) = response.addHeader(name, value)
}

class CompoundResponse(val responses : Vector[Response]) extends Response {
	def respond(response : HttpServletResponse) = responses.foreach (_.respond(response))
  override def + (response : Response) = new CompoundResponse(responses :+ response)
}

class OutputStreamResponse(f : OutputStream => Unit) extends Response {
	def respond(response : HttpServletResponse) = {
		val os = response.getOutputStream 
		try {
		  f(os)
		} finally {
		  os.close
		}
	}
}

object NullResponse extends Response {
	def respond(response : HttpServletResponse) = {}
}

object Stream {
  def apply(f : OutputStream => Unit) = new OutputStreamResponse(f)
  
  def apply(is : InputStream) = new OutputStreamResponse(copy(is, _))
  
  def apply(c : Class[_], resource : String) = new OutputStreamResponse(copy(c, resource, _))
  
  def copy(c : Class[_], resource : String, os : OutputStream) {
    try { 
	  	copy(c.getResourceAsStream(resource), os)
	  } catch {
	    case e => throw new Exception("Resource not found on classpath: " + resource) 
	  }
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



