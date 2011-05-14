package scalastuff.website

import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.scalastuff.util.Loan._
import xml.{ XML, Node, Elem, UnprefixedAttribute }
import org.xml.sax.InputSource

object GoogleWikiImport extends Application {
  def loadHtml(source: InputSource) = {
    val parserFactory = new org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
    val parser = parserFactory.newSAXParser()
    val adapter = new scala.xml.parsing.NoBindingFactoryAdapter
    adapter.loadXML(source, parser)
  }

  def transform(n: Node): Node = n match {
    case <pre>{ code }</pre> =>
      <pre class="brush: scala">{ code }</pre>
    case <tt>{ code }</tt> =>
      <code>{ code }</code>
    case a: Elem if a.label == "a" && ((a \ "@href" text) startsWith "/p/scalabeans/wiki/") =>
      <a href={ (a \ "@href" text) stripPrefix "/p/scalabeans/wiki/"}>{ a.text }</a>
    case e: Elem =>
      e.copy(child = e.child map transform)
    case _ => n
  }

  def importWiki(page: String) {
    val source = new InputSource("http://code.google.com/p/scalabeans/wiki/" + page + "?show=content")

    val pageXML = loadHtml(source)
    for (wikiXML <- pageXML \\ "div" if (wikiXML \ "@id" text) == "wikimaincol") {
      val scalastuffDoc = transform(wikiXML)
      XML.save("src/main/resources/scalastuff/website/projects/scalabeans/" + page + "2.html", scalastuffDoc, "UTF-8")
      println(page + " imported")
    }
  }
  
  val wikiPages = "BeanIntrospection" :: "Serialization" :: "LoanDesignPattern" :: Nil
  wikiPages foreach importWiki
}
