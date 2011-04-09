package scalastuff.website

import scalastuff.webtoolkit.Menu
import xml.NodeSeq

trait ProjectSidebar extends Page {

  lazy val sidebarMenu = Menu[Page](Website.sitemap, _.title, this, Some(ProjectsPage))
  
  def content(implicit context: TemplateContext) = 
    <table class="layout-with-sidebar"><tr>
	  <td class="content2">{content2}</td>
  	  <td class="sidebar">
  		  <h1>Navigation</h1>
		  <div class="sidebar-menu">{sidebarMenu}</div>
  	  </td>
	</tr></table>
  
  def content2(implicit context: TemplateContext) : NodeSeq 
}

object ProjectsPage extends Page with ProjectSidebar {

  def content2(implicit context: TemplateContext) = 
    <div>
	  <h1>Projects</h1>
	  <h3>ScalaBeans</h3>
	  Go to <a href={scalabeans.ScalaBeansPage.href}>ScalaBeans</a> page
	</div>
}

