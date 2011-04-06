package scalastuff.website

import tools._

object ToolsPage extends Page {
  def content(implicit context: TemplateContext) =
    <div>
	  <h1>Tools</h1>
	  <h3>Unfiltered</h3>
	  Go to <a href={UnfilteredPage.href}>Unfiltered</a> page
	</div>
}

