package scalastuff.webtoolkit

import xml.{NodeSeq,Text}

object Menu {
  
  def pathOf[Page](sitemap : Map[List[String],Page], page : Page) = sitemap.find(_._2 == page).map(_._1).getOrElse(Nil) 
  
  def apply[Page](sitemap : Map[List[String],Page], label : Page => String, currentPage : Page, root : Option[Page] = None, depth : Int = 2, flat : Boolean = false, expandAll : Boolean = false) : NodeSeq = {
	
	def menu(paths : Seq[List[String]], level : Int, flat2 : Boolean = false) : NodeSeq = { 
      val currentPath = pathOf(sitemap, currentPage)
	  val elts = for {
	    path <- paths
	    page = sitemap(path)
	    title = label(page)
	    isParent = currentPath startsWith path
	    children = sitemap.filter(p => p._1.startsWith(path) && p._1.size == path.size + 1).map(_._1).toSeq 
	    classes = ("level" + level :: title :: Nil) ++
	        (if (page == currentPage) List("selected") else Nil) ++
	    	(if (isParent && children.nonEmpty) List("open") else Nil)
	  } yield 
	  	<li><a href={path.mkString("/","/","")}>{
		  Text(title) ++
		  (if ((isParent || expandAll) && level < depth) menu(children, level + 1, flat) else NodeSeq.Empty)
	    }</a></li> % (if (classes.isEmpty) xml.Null else xml.Attribute(None, "class", Text(classes.mkString(" ")), xml.Null))
	  if (flat2) elts
	  else <ul>{elts}</ul>
  	}
    menu(Seq(root.flatMap(r => sitemap.find(_._2 == r).map(_._1)).getOrElse(List[String]())), 0)
  }
}

object Trail {
  def apply[Page](sitemap : Map[List[String],Page], label : Page => String, currentPage : Page, sep : NodeSeq) : NodeSeq = {
    
    def elt(path : List[String], first : Boolean) : NodeSeq =
     (if (path.nonEmpty) elt(path.dropRight(1), false) else NodeSeq.Empty) ++
	 <li><a href={path.mkString("/","/","")}>{label(sitemap(path))}</a>
	 </li> % (if (first) xml.Attribute(None, "class", Text("first"), xml.Null) else xml.Null) ++ 
	 (if (!first) sep else NodeSeq.Empty) 
	 
    val path = Menu.pathOf(sitemap, currentPage)
    <ul>{elt(path, true)}</ul>
  }
}