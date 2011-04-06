package scalastuff.website

import xml.NodeSeq

object ScalaBeansPage extends Page {
  def content(implicit context: TemplateContext) = 
    <div>
    <h1>ScalaBeans</h1>
	<pre class="brush: scala">
	  val s = "hi there"
	  </pre>
	</div>
}
