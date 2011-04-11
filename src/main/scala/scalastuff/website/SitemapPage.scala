package scalastuff.website

import scalastuff.webtoolkit.Menu
import scalastuff.webtoolkit.PageRequest

object SitemapPage extends ScalastuffPage {

	val path = "sitemap" :: Nil;
  
  lazy val menu = Menu(Website.pages, this, depth=99, expandAll=true)
  
  def html(implicit request : PageRequest) =
    <div>
	  <h1>Sitemap</h1>
	  <div class="sitemap-menu">
	   {menu}
  		</div>
	</div>
}

