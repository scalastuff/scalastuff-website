package scalastuff.website

import com.weiglewilczek.slf4s.Logging
import xml.NodeSeq
import scalastuff.webtoolkit.{Menu,Trail,Page,PageDecorator,PageRequest}

abstract class ScalastuffPage extends Page with Logging {
  val title : String = getClass.getSimpleName match {
    case s if s.endsWith("Page$") => s.dropRight(5)
    case s if s.endsWith("$") => s.dropRight(1)
    case s => s
  }
  lazy val defaultDecorators : Seq[PageDecorator] = Seq(new ScalastuffDecorator(this))
  override def decorators = defaultDecorators
}

class ScalastuffDecorator(val page : Page) extends PageDecorator {

	lazy val menu = Menu(Website.pages, page, depth=1, flat=true)
	lazy val trail = Trail(Website.sitemap, page, <span class="sep">&#8594;</span>)
	
	def decorate(xml : NodeSeq)(implicit request : PageRequest) : NodeSeq = 
    <html>
      <head>
        <title>ScalaStuff</title>
	  	<link href="/resources/syntaxhighlighter_3.0.83/styles/shCore.css" rel="stylesheet" type="text/css" />
	  	<link href="/resources/syntaxhighlighter_3.0.83/styles/shThemeScalastuff.css" rel="stylesheet" type="text/css" />
	  	<link href="/resources/Scalastuff.css" rel="stylesheet" type="text/css" />
	  	<script type="text/javascript" src="/resources/syntaxhighlighter_3.0.83/scripts/shCore.js"></script>
	  	<script type="text/javascript" src="/resources/syntaxhighlighter_3.0.83/scripts/shBrushJava.js"></script>
	  	<script type="text/javascript" src="/resources/syntaxhighlighter_3.0.83/scripts/shBrushScala.js"></script>
	  <script type="text/javascript" src="/resources/syntaxhighlighter_3.0.83/scripts/shBrushXml.js"></script>
	  	<script type="text/javascript">SyntaxHighlighter.all()</script> 
      </head>
      <body class={request.page.title}>
        <div class="main-panel">
      	  <div class="header2">
      		<div>
      		<img src="/resources/ScalastuffLogo.png"/>
      		<span>Stuff that matters</span>
      		</div>
          </div>
          <div class="main-menu">{menu}</div>
          <div class="trail">{trail}</div>
          <div class="page-content">{xml}</div>
        </div>
      </body>
    </html>
}

abstract class ProjectPage extends ScalastuffPage {
	override val decorators = super.decorators :+ new ProjectPageDecorator(this)
}

class ProjectPageDecorator(page : Page) extends ScalastuffDecorator(page) {

	lazy val sidebarMenu = Menu(Website.pages, page, Some(ProjectsPage))
  
	override def decorate(xml : NodeSeq)(implicit request : PageRequest) : NodeSeq =
		super.decorate(
    <table class="layout-with-sidebar"><tr>
	  <td class="content2">{xml}</td>
  	  <td class="sidebar">
  		  <h1>Navigation</h1>
		  <div class="sidebar-menu">{sidebarMenu}</div>
  	  </td>
	</tr></table>)
}

