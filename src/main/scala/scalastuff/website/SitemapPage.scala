package scalastuff.website

import scalastuff.webtoolkit.Menu

object SitemapPage extends Page {
  
  lazy val menu = Menu[Page](Website.sitemap, _.title, this, depth=99, expandAll=true)
  
  def content(implicit context: TemplateContext) =
    <div>
	  <h1>Sitemap</h1>
	  <div class="sitemap-menu">
	   {menu}
  		</div>
	</div>
}

