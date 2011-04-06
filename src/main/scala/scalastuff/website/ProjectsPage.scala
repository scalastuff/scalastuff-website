package scalastuff.website

object ProjectsPage extends Page {
  def content(implicit context: TemplateContext) =
    <div>
	  <h1>Projects</h1>
	  <h3>ScalaBeans</h3>
	  Go to <a href={ScalaBeansPage.href}>ScalaBeans</a> page
	</div>
}

