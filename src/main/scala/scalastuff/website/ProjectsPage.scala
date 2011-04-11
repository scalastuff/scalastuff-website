package scalastuff.website

import scalastuff.webtoolkit.{Menu,Trail,Page,PageDecorator,PageRequest}
import xml.NodeSeq

object ProjectsPage extends ScalastuffPage {

	val path = "projects" :: Nil;

  def html(implicit request : PageRequest) = 
    <div>
	  <h1>Projects</h1>
	  <h3>ScalaBeans</h3>
	  Go to <a href={scalabeans.ScalaBeansPage.href}>ScalaBeans</a> page
	</div>
}

