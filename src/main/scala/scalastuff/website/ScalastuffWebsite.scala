package scalastuff.website

import org.scalastuff.scalapages.{Context,TemplateBaseURI}
import org.scalastuff.scalapages.http.{Request, Stream, ScalaPagesFilter,CacheControl,LastModified,ETag,HtmlContent}
import java.net.URI
import javax.servlet.FilterConfig


class ScalastuffWebsite extends ScalaPagesFilter {

  override def init = context ++= TemplateBaseURI -> TemplateBaseURI.resolve("scalastuff/website/")
  
  def Caching = 
    CacheControl("max-age=3600") +
  	LastModified(new java.util.Date) +
  	ETag(hashCode.toString) 

  def handle = { 
    case Request("resources" :: resource) => Caching + Stream(getClass, resource.mkString("resources/","/",""))
    case Request("scaladoc2" :: resource) => 
  		if (resource.mkString("/").endsWith(".html")) HtmlContent + Caching + Stream(getClass, resource.mkString("/","/",""))
  		else Caching + Stream(getClass, resource.mkString("/","/",""))
    case Request("favicon.ico" :: Nil) => Caching + Stream(getClass, "resources/ScalastuffIcon.png")
    case Request(path) => HtmlContent + Stream(pageServer.renderPage(path, _))
  }
}