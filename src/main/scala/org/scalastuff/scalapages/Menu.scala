package org.scalastuff.scalapages

import xml.{ Elem, NodeSeq, Text, MetaData }
import Preamble._

object Menu {

  def apply(pages: Seq[Page], currentPage: Page, items : Seq[(List[String], NodeSeq)], attributes : MetaData, depth: Int = 2, flat: Boolean = false, expandAll: Boolean = false): NodeSeq = {

    def menu(items : Seq[(List[String], NodeSeq)], level: Int, flat2: Boolean = false): NodeSeq = {
      val currentPath = currentPage.path
      val elts = for {
        (path,title) <- items
        page = pages.find(_.path == path)
        isParent = currentPath startsWith path
        children = pages.filter(p => p.path.startsWith(path) && p.path.size == path.size + 1).map(mkItem).toSeq
        submenu = if ((isParent || expandAll) && level < depth) menu(children, level + 1, flat) else NodeSeq.Empty 
        classes = ("level" + level :: title.text :: Nil) ++
          (if (page == Some(currentPage) || (isParent && submenu.isEmpty)) List("selected") else Nil) ++
          (if (isParent && children.nonEmpty) List("open") else Nil)
      } yield <li><a href={ path.mkString("/", "/", "") }>{
        title ++ submenu
      }</a></li> % (if (classes.isEmpty) xml.Null else xml.Attribute(None, "class", Text(classes.mkString(" ")), xml.Null))
      if (flat2) elts.toSeq
      else <ul>{ elts }</ul> % attributes
    }
    menu(items, 0)
  }
  
  def mkItem(page : Page) : (List[String], NodeSeq) = (page.path, Text(page.title))
}


object MenuProcessor extends Processor {
	override val name = Some("menu")
	override def process(recurse : NodeSeq => Context => Seq[Producer])(implicit context : Context) = {
		case e : Elem if e.label == "menu" => 
			val items : Seq[(List[String],NodeSeq)] = 
			  for (item <- e \\ "item") 
			  yield (mkPath(item.getOrElse("@href", "")), item \*)
			val parentItems : Seq[(List[String],NodeSeq)] = 
				(e \ "@root").text match {
				  case "" => Seq()
				  case s => 
				  	val path = mkPath(s)
				  	IndexedPages.get.filter(_.path == path).map(Menu.mkItem).toSeq
			  }
			
			Seq(Producer(Menu(IndexedPages.get, CurrentPage.get, items ++ parentItems, e.attributes, depth = e.getOrElse("@depth", 0), flat = e ?? "@flat").toString))
	}
	
	def mkPath(s : String) = s.split("/").filter(_.nonEmpty).toList
}
