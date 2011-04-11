package scalastuff.website

import scalastuff.webtoolkit.PageRequest

class NotFoundPage(val path : List[String]) extends ScalastuffPage  {
	
  def html(implicit request : PageRequest) = {
    logger warn "Page not found: " + path.mkString("/", "/", "") 
    <div>
	  <h3>Page not found...</h3>
	  Go to the <a href="/">home</a> page.
	</div>
  }
}