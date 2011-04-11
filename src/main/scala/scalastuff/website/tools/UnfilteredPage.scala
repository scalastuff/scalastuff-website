package scalastuff.website.tools

import scalastuff.website._
import scalastuff.webtoolkit.{Menu,Trail,Page,PageDecorator,PageRequest}

object UnfilteredPage extends ScalastuffPage {
	
	val path = "tools" :: "unfiltered" :: Nil
	
  def html(implicit request : PageRequest) =
    <div>
	  <h1>Unfiltered</h1>
	  <h2>Description</h2>
	  <p>This guy gets it. I totally agree with his argument of toolkits over <a href="http://unfiltered.lessis.me/#9">frameworks</a>.</p> 

<p>There are some nice quotes, like
<cite>xml may be good for some things.
a maintainable configuration is not one of them.</cite>
Absolutely, but I don't think configuration is SBT is particularely nice. You configuration is scattered throughout 
project, project/build and project/plugins, there are scala files and property files, and these are interleaved with a 
lot of generated files.</p>  
	  <p>Go to <a href="http://unfiltered.lessis.me">ScalaBeans</a> page</p>
	</div>
}