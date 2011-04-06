package scalastuff.website

import com.weiglewilczek.slf4s.Logging
import xml.NodeSeq

object Page {
  val menu = List(
    HomePage -> "Home",
    ProjectsPage -> "Projects",
    ToolsPage -> "Tools")
}

abstract class Page extends Logging {
  def xml(implicit context: TemplateContext) = {
    <html>
      <head>
        <title>ScalaStuff</title>
	  	<link href="/resources/syntaxhighlighter_3.0.83/styles/shCore.css" rel="stylesheet" type="text/css" />
	  	<link href="/resources/syntaxhighlighter_3.0.83/styles/shThemeDefault.css" rel="stylesheet" type="text/css" />
	  	<link href="/resources/Scalastuff.css" rel="stylesheet" type="text/css" />
	  	<script type="text/javascript" src="/resources/syntaxhighlighter_3.0.83/scripts/shCore.js"></script>
	  	<script type="text/javascript" src="/resources/syntaxhighlighter_3.0.83/scripts/shBrushJava.js"></script>
	  	<script type="text/javascript" src="/resources/syntaxhighlighter_3.0.83/scripts/shBrushScala.js"></script>
	  	<script type="text/javascript">SyntaxHighlighter.all()</script> 
      </head>
      <body>
        <img src="/resources/ScalastuffLogo.png"/>
        { menuXml }
        { content }
      </body>
    </html>
  }

  private lazy val menuXml = <ul>{ 
    for ((path, page) <- Website.sitemap; (page2, title) <- Page.menu if page==page2) 
      yield <li class={ if (page == this) "selected" else "" }><a href={path.mkString("/", "/", "")}>{title}</a></li>
  }</ul>

  lazy val href = Website.sitemap.find(_._2 == this).getOrElse(Nil -> HomePage)._1.mkString("/", "/", "")
  
  /**
   * O
   * @param context
   * @return
   */
  def content(implicit context: TemplateContext): NodeSeq
}