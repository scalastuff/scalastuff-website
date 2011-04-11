package scalastuff.webtoolkit

import xml.{Elem, NodeSeq}

case class PageRequest(page : Page)

trait Page {
	val title : String
	val path : List[String]
	def html(implicit request : PageRequest) : NodeSeq
	def decorators = Seq[PageDecorator]()
	
	def href = path.mkString("/","/","")
}

trait PageDecorator {
	val page : Page
	def decorate(xml : NodeSeq)(implicit request : PageRequest) : NodeSeq
	def postProcess(elem : Elem)(implicit request : PageRequest) : Elem = elem
}



