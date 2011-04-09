package scalastuff.website.scalabeans

import scalastuff.website._

object ScalaTypesPage extends Page with ProjectSidebar {
  
  def content2(implicit context: TemplateContext) = 
    <div>
    <h1>ScalaTypes</h1>
<pre class="brush: scala">
  for ((a,b) - myColl) {
    
  }
</pre>
	</div>
}
