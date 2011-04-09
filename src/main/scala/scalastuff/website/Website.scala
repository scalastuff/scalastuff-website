package scalastuff.website

import com.weiglewilczek.slf4s.Logging
import java.io.{InputStream, OutputStream}
import org.apache.commons.io.IOUtils
import org.apache.http.impl.cookie.DateUtils
import unfiltered.filter._
import unfiltered.request._
import unfiltered.response._

object Website {
 
  val sitemap = Map[List[String],Page] (
    (Nil) -> HomePage,
    ("projects" :: Nil) -> ProjectsPage,
    ("tools" :: Nil) -> ToolsPage,
    ("tools" :: "unfiltered" :: Nil) -> tools.UnfilteredPage,
    ("sitemap" :: Nil) -> SitemapPage) ++ scalabeans.ScalaBeansPage.sitemap
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

  def intent = {
    case Path(Seg("resources" :: resource)) => Caching ~> stream(resource.mkString("/"))
    case Path(Seg("favicon.ico" :: Nil)) => Caching ~> stream("ScalastuffIcon.png")
    case Path(Seg(path)) => Html(Website.sitemap.getOrElse(path, NotFoundPage).xml(new TemplateContext(path)))
  }
}

case class TemplateContext(path : List[String])
 
