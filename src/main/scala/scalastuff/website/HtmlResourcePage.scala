package scalastuff.website

import java.io.File
import xml.XML

object HtmlResources {

  def sitemap(basepath : String) = scanPages(new File(basepath), Seq()) 
	
  def scanPages(file : File, path : Seq[String]) : Seq[(Seq[String], Page)] = file match {
	case f if f.getName.endsWith(".html") => Seq((path :+ f.getName, new HtmlResourcePage(f)))
	case d if d.isDirectory => d.listFiles.flatMap(scanPages(_, path :+ d.getName))
  }
}

class HtmlResourcePage(file : File) extends Page {

	val page = XML.loadFile(file)
	
	def content(implicit context : TemplateContext) = page
	
	override val title = (page \\ "h1").text
	
}