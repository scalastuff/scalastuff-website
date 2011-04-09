package scalastuff.website.scalabeans

import scalastuff.website._

object UsagePage extends Page with ProjectSidebar {
  
  def content2(implicit context: TemplateContext) = 
    <div>
    <h1>Usage</h1>
	  Add the following line to your SBT project:

<pre class="brush: scala">
  val scalabeans = "org.scalastuff" %% "scalabeans" % "0.1"
</pre>
	</div>
}
