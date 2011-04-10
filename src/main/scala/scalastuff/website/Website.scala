package scalastuff.website

import com.weiglewilczek.slf4s.Logging
import java.io.{File,InputStream, OutputStream}
import javax.servlet.FilterConfig
import org.apache.commons.io.IOUtils
import org.apache.http.impl.cookie.DateUtils
import unfiltered.filter._
import unfiltered.request._
import unfiltered.response._

object Website {
 
  var pagesDir : String = ""

  lazy val sitemap = Map[List[String],Page] (
    (Nil) -> HomePage,
    ("projects" :: Nil) -> ProjectsPage,
    ("tools" :: Nil) -> ToolsPage,
    ("tools" :: "unfiltered" :: Nil) -> tools.UnfilteredPage,
    ("sitemap" :: Nil) -> SitemapPage) ++ 
    scalabeans.ScalaBeansPage.sitemap ++
    Map(new File(pagesDir).listFiles.flatMap(scanPages(Nil, _)):_*)

  private def scanPages(path : List[String], file : File) : Seq[(List[String], Page)] = file match {
	case f if f.getName.endsWith(".html") => println("path: " + (path :+ f.getName.dropRight(5))); Seq((path :+ f.getName.dropRight(5), new HtmlResourcePage(f)))
	case d if d.isDirectory => d.listFiles.flatMap(f => scanPages(path :+ d.getName, f))
	case _ => Seq()
  }
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
	Website.pagesDir = config.getServletContext.getRealPath("WEB-INF/classes/scalastuff/website/pages/")
	println(Website.sitemap)
    super.init(config)
  }

  def intent = {
    case Path(Seg("resources" :: resource)) => Caching ~> stream(resource.mkString("/"))
    case Path(Seg("favicon.ico" :: Nil)) => Caching ~> stream("ScalastuffIcon.png")
    case Path(Seg(path)) => Html(Website.sitemap.getOrElse(path, NotFoundPage).xml(new TemplateContext(path)))
  }
}

case class TemplateContext(path : List[String])
 
