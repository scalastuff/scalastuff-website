package scalastuff.website

import tools._
import scalastuff.webtoolkit.{Menu,Trail,Page,PageDecorator,PageRequest}

object ToolsPage extends ScalastuffPage {
	
	val path = "tools" :: Nil;

	def html(implicit request : PageRequest) =
    <div>
	  <h1>Tools</h1>
	  <h3>Unfiltered</h3>
	  Go to <a href={UnfilteredPage.href}>Unfiltered</a> page
	</div>
}

