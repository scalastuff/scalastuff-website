package scalastuff.website

import com.weiglewilczek.slf4s.Logging
import java.io.{File,InputStream, OutputStream}
import java.net.URI
import javax.servlet.FilterConfig
import org.apache.commons.io.IOUtils
import org.apache.http.impl.cookie.DateUtils
import unfiltered.filter._
import unfiltered.request._
import unfiltered.response._
import scalastuff.webtoolkit.{Page,PageDecorator,StaticPage,PageRequest}
import xml.NodeSeq
import scala.collection.mutable

object Website {
 
	val decoratorMap = Map[String, Map[String,NodeSeq] => Page => PageDecorator](
			"ScalastuffDecorator" -> (pars => page => new ScalastuffDecorator(page)),
			"ProjectPageDecorator" -> (pars => page => new ProjectPageDecorator(page))
	)

	val dynamicPages =   	
		HomePage :: 
  	ProjectsPage :: 
	  ToolsPage :: 
	  tools.UnfilteredPage :: 
	  SitemapPage :: Nil

	
  val pages : Seq[Page] = 
  	dynamicPages ++  
  	StaticPage.load(Nil, new URI("classpath:/scalastuff/website/"), decoratorMap)  
	  	
  val sitemap = Map(pages.reverse.map(page => page.path -> page):_*) 
}

class Website extends Plan with Logging {
   
  logger info "ScalaStuff website started"
  
  def Caching =
    CacheControl("max-age=3600") ~>
  	LastModified(DateUtils.formatDate(new java.util.Date)) ~>
  	ETag(Server.hashCode.toString)

  def stream(resource : String) = new ResponseStreamer {
    def stream(os: OutputStream) = IOUtils.copy(getClass.getResourceAsStream("resources/" + resource), os)
  }
  	
  override def init(config : FilterConfig) = {
    super.init(config)
    println(Website.sitemap)
  }

  def intent = {
    case Path(Seg("resources" :: resource)) => Caching ~> stream(resource.mkString("/"))
    case Path(Seg("favicon.ico" :: Nil)) => Caching ~> stream("ScalastuffIcon.png")
    case Path(Seg(path)) =>
    	val page = Website.sitemap.getOrElse(path, new NotFoundPage(path))
    	val request = new PageRequest(page) 
    	val html = (page.decorators :\ page.html(request))(_.decorate(_)(request))
    	Html(html)
  }
}
