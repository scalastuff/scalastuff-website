package scalastuff.website

object NotFoundPage extends Page {
  def content(implicit context : TemplateContext) = {
    logger warn "Page not found: " + context.path.mkString("/", "/", "") 
    <div>
	  <h3>Page not found...</h3>
	  Go to the <a href="/">home</a> page.
	</div>
  }
}