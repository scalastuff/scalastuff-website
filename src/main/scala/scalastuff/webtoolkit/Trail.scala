package scalastuff.webtoolkit

import xml.{NodeSeq,Text}

object Trail {
  def apply(sitemap : Map[List[String],Page], currentPage : Page, sep : NodeSeq) : NodeSeq = {
    
    def elt(path : List[String], first : Boolean) : NodeSeq =
     (if (path.nonEmpty) elt(path.dropRight(1), false) else NodeSeq.Empty) ++
	 <li><a href={path.mkString("/","/","")}>{sitemap(path).title}</a>
	 </li> % (if (first) xml.Attribute(None, "class", Text("first"), xml.Null) else xml.Null) ++ 
	 (if (!first) sep else NodeSeq.Empty) 
	 
    val path = Menu.pathOf(sitemap, currentPage)
    <ul>{elt(path, true)}</ul>
  }
}