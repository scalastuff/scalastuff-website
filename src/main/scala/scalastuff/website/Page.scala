package scalastuff.website

import com.weiglewilczek.slf4s.Logging
import xml.NodeSeq
import scalastuff.webtoolkit.{Menu,Trail}

object Page {
  val menu = List(
    HomePage -> "Home",
    ProjectsPage -> "Projects",
    ToolsPage -> "Tools")
}

abstract class Page extends Logging {
  
  def title : String = getClass.getSimpleName match {
    case s if s.endsWith("Page$") => s.dropRight(5)
    case s if s.endsWith("$") => s.dropRight(1)
    case s => s
  }
  
  def xml(implicit context: TemplateContext) = {
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
      <body class={title}>
        <div class="main-panel">
      	  <div class="header2">
      		<div>
      		<img src="/resources/ScalastuffLogo.png"/>
      		<span>Stuff that matters</span>
      		</div>
          </div>
          <div class="main-menu">
          {Menu[Page](Website.sitemap, _.title, this, depth=1, flat=true)}
          </div>
          <div class="trail">{Trail[Page](Website.sitemap, _.title, this, <span class="sep">&#8594;</span>)}</div>
          <div class="page-content">
          	{ content }
          </div>
        </div>
      </body>
    </html>
  }

  lazy val href = Website.sitemap.find(_._2 == this).getOrElse(Nil -> HomePage)._1.mkString("/", "/", "")
  
  /**
   * O
   * @param context
   * @return
   */
  def content(implicit context: TemplateContext): NodeSeq
}

abstract class ProjectPage extends Page {
  
  lazy val sideMenu = Menu[Page](Website.sitemap, _.title, this, Some(ProjectsPage))
  
	def content(implicit context: TemplateContext) = 
	  <div><div class="page-content2">{projectContent}</div><div class="sidebar">
  		<h1>Navigation</h1>
		<div class="sidebar-menu">{sideMenu}</div>
		<p>saklf jasf wioae fakwjefh kljh lk</p><p>saklf jasf wioae fakwjefh kljh lk</p><p>saklf jasf wioae fakwjefh kljh lk</p></div></div>
	
	def projectContent(implicit context: TemplateContext) : NodeSeq
}