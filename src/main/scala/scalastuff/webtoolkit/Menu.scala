package scalastuff.webtoolkit

import xml.{NodeSeq,Text}

object Menu {
  
  def pathOf(sitemap : Map[List[String],Page], page : Page) = sitemap.find(_._2 == page).map(_._1).getOrElse(Nil) 
  
  def apply(pages : Seq[Page], currentPage : Page, root : Option[Page] = None, depth : Int = 2, flat : Boolean = false, expandAll : Boolean = false) : NodeSeq = {
	
	def menu(paths : Seq[List[String]], level : Int, flat2 : Boolean = false) : NodeSeq = { 
      val currentPath = currentPage.path
	  val elts = for {
	    path <- paths
	    page = pages.find(_.path == path).get
	    isParent = currentPath startsWith path
	    children = pages.filter(p => p.path.startsWith(path) && p.path.size == path.size + 1).map(_.path).toSeq
	    classes = ("level" + level :: page.title :: Nil) ++
	        (if (page == currentPage) List("selected") else Nil) ++
	    	(if (isParent && children.nonEmpty) List("open") else Nil)
	  } yield 
	  	<li><a href={path.mkString("/","/","")}>{
	    println(path + " CHILDREN: " + children)
		  Text(page.title) ++
		  (if ((isParent || expandAll) && level < depth) menu(children, level + 1, flat) else NodeSeq.Empty)
	    }</a></li> % (if (classes.isEmpty) xml.Null else xml.Attribute(None, "class", Text(classes.mkString(" ")), xml.Null))
	  if (flat2) elts
	  else <ul>{elts}</ul>
  	}
    menu(Seq(root.map(r => r.path).getOrElse(List[String]())), 0)
  }
}
