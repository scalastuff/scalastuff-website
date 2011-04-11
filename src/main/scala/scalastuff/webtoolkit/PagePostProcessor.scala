package scalastuff.webtoolkit

import xml.NodeSeq

class PagePostProcessor(val page : Page) extends PageDecorator {
	override def decorate(xml : NodeSeq)(implicit request : PageRequest) : NodeSeq = {
		xml map {  
			_ match {
			 	case <dummy>{content}</dummy> => content 
				case s => s 
			}
		}
	}
}