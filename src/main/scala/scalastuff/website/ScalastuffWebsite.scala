package scalastuff.website

import org.scalastuff.scalapages.{Context,TemplateBaseURI,RootBean,RootBeans}
import org.scalastuff.scalapages.http.{Request, Stream, ScalaPagesFilter,CacheControl,LastModified,ETag,HtmlContent}
import org.scalastuff.scalabeans.Preamble._
import java.net.URI
import javax.servlet.FilterConfig


object Scalastuff {
	val format = new java.text.SimpleDateFormat("dd-MM-yyyy")
	val news = 
	  News(format.parse("10-5-2011"), None, <p>ScalaStuff website released</p>) :: 
  	News(format.parse("9-5-2011"), None, <p>ScalaBeans 0.1 released</p>) :: Nil
  	
  val blogs = 
    Blog("Scala Types", format.parse("2-4-2011"), "Ruud Diterwich", <a href="http://scalastuff.wordpress.com/2011/04/02/scala-types/">Scala Types</a>) ::
    Blog("Scala Enums", format.parse("1-4-2011"), "Ruud Diterwich", <a href="http://scalastuff.wordpress.com/2011/04/02/scala-enums/">Scala Enums</a>) :: Nil
}

class ScalastuffWebsite extends ScalaPagesFilter {

  override def init = context ++= (
  		TemplateBaseURI -> TemplateBaseURI.resolve("scalastuff/website/"),
  		RootBeans -> Seq(RootBean("scalastuff", Scalastuff)))
  
  def Caching = 
    CacheControl("max-age=3600") +
  	LastModified(new java.util.Date) +
  	ETag(hashCode.toString) 

  def handle = { 
    case Request("resources" :: resource) => Caching + Stream(getClass, resource.mkString("resources/","/",""))
    case Request("scaladoc" :: resource) => 
  		if (resource.mkString("/").endsWith(".html")) HtmlContent + Caching + Stream(getClass, resource.mkString("/","/",""))
  		else Caching + Stream(getClass, resource.mkString("/","/",""))
    case Request("favicon.ico" :: Nil) => Caching + Stream(getClass, "resources/ScalastuffIcon.png")
    case Request(path) => HtmlContent + Stream(pageServer.renderPage(path, _))
  }
}