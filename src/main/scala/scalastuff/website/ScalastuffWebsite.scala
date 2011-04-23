package scalastuff.website

import org.scalastuff.scalapages.{Context,TemplateBaseURI}
import org.scalastuff.scalapages.http.{Request, Stream, ScalaPagesFilter,CacheControl,LastModified,ETag,HtmlContent}
import java.net.URI

class ScalastuffWebsite extends ScalaPagesFilter {

  override def init = context ++= TemplateBaseURI -> new URI("classpath:/scalastuff/website/")
  
  def Caching = 
    CacheControl("max-age=3600") +
  	LastModified(new java.util.Date) +
  	ETag(hashCode.toString) 

  def handle = { 
    case Request("resources" :: resource) => Caching + Stream(getClass.getResourceAsStream(resource.mkString("resources/","/","")))
    case Request("scaladoc" :: resource) => 
  		if (resource.mkString("/").endsWith(".html")) HtmlContent + Caching + Stream(getClass.getResourceAsStream(resource.mkString("/","/","")))
  		else Caching + Stream(getClass.getResourceAsStream(resource.mkString("/","/","")))
    case Request("favicon.ico" :: Nil) => Caching + Stream(getClass.getClassLoader.getResourceAsStream("ScalastuffIcon.png"))
    case Request(path) => HtmlContent + Stream(pageServer.renderPage(path, _))
  }
}