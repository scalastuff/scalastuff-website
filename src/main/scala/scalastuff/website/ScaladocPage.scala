package scalastuff.website

import java.io.InputStream
import java.net.URI
import org.scalastuff.scalapages.{Page,Context,CurrentPage,PageServer,TemplateBaseURI,Processor,CurrentTemplateURI}
import org.scalastuff.scalapages.Preamble._

object ProxyCache {

}

class ScaladocPage(is : InputStream, val path: List[String], val processors : Seq[Processor])(implicit var context : Context) extends Page {
  context = CurrentTemplateURI.setRelative("classpath:/" + path.mkString("/"))
  val rawXml = 
    <template:include file="Scalastuff.xml">
  		{CurrentTemplateURI.loadXML}
  	</template:include> 
  val html = Processor.preProcess(rawXml, processors)
  val title = "Scalastuff.org"
}