package org.scalastuff.scalapages

import xml.{NodeSeq,MetaData,Text}

object Trail {
  def apply(pages : Seq[Page], currentPage : Page, attributes : MetaData, sep : NodeSeq) : NodeSeq = {
    
    def elt(path : List[String], first : Boolean) : NodeSeq =
     (if (path.nonEmpty) elt(path.dropRight(1), false) else NodeSeq.Empty) ++
	 <li><a href={path.mkString("/","/","")}>{pages.find(_.path == path).get.title}</a>
	 </li> % (if (first) xml.Attribute(None, "class", Text("first"), xml.Null) else xml.Null) ++ 
	 (if (!first) sep else NodeSeq.Empty) 
	 
    <ul>{elt(currentPage.path, true)}</ul> % attributes
  }
}


object TrailProcessor extends Processor {
	override val name = Some("trail")
	override def process(recurse : NodeSeq => Context => Seq[Producer])(implicit context : Context) = {
		case e @ <trail>{_*}</trail> =>
			Seq(Producer(Trail(IndexedPages.get, CurrentPage.get, e.attributes, e.child).toString))
	}
}