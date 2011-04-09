package scalastuff.website.scalabeans

import scalastuff.website._
import xml.NodeSeq

object ScalaBeansPage extends Page with ProjectSidebar {
  
  val sitemap = Map[List[String],Page] (
    ("projects" :: "scalabeans" :: Nil) -> ScalaBeansPage,
    ("projects" :: "scalabeans" :: "scalatypes" :: Nil) -> ScalaTypesPage,
    ("projects" :: "scalabeans" :: "usage" :: Nil) -> UsagePage)
  
  def content2(implicit context: TemplateContext) =  
    <div>
    <h1>ScalaBeans</h1>
<pre class="brush: scala">
  val s = "hi there again"
  for ((a,b) - myColl) {
    
  }
</pre>
	</div>
}
