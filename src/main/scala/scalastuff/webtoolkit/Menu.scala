package scalastuff.webtoolkit

import xml.{ Elem, NodeSeq, Text, MetaData }
import Preamble._

object Menu {

  def apply(pages: Seq[Page], currentPage: Page, root: Option[Page] = None, attributes : MetaData, depth: Int = 2, flat: Boolean = false, expandAll: Boolean = false): NodeSeq = {

    def menu(paths: Seq[List[String]], level: Int, flat2: Boolean = false): NodeSeq = {
      val currentPath = currentPage.path
      val elts = for {
        path <- paths
        page = pages.find(_.path == path).get
        isParent = currentPath startsWith path
        children = pages.filter(p => p.path.startsWith(path) && p.path.size == path.size + 1).map(_.path).toSeq
        submenu = if ((isParent || expandAll) && level < depth) menu(children, level + 1, flat) else NodeSeq.Empty 
        classes = ("level" + level :: page.title :: Nil) ++
          (if (page == currentPage || (isParent && submenu.isEmpty)) List("selected") else Nil) ++
          (if (isParent && children.nonEmpty) List("open") else Nil)
      } yield <li><a href={ path.mkString("/", "/", "") }>{
        Text(page.title) ++ submenu
      }</a></li> % (if (classes.isEmpty) xml.Null else xml.Attribute(None, "class", Text(classes.mkString(" ")), xml.Null))
      if (flat2) elts
      else <ul>{ elts }</ul> % attributes
    }
    menu(Seq(root.map(r => r.path).getOrElse(List[String]())), 0)
  }
}


object MenuProcessor extends Processor {
	override val name = Some("menu")
	override def process(recurse : NodeSeq => Seq[Producer])(implicit context : Context) = {
		case e : Elem if e.label == "menu" =>
			val root = e.getOrElse("@root", "").split("/").filter(_.nonEmpty).toList 
			Seq(Producer(Menu(IndexedPages, CurrentPage, IndexedPages.find(_.path == root), e.attributes, depth = e.getOrElse("@depth", 1), flat = e ?? "@flat").toString))
	}
}
