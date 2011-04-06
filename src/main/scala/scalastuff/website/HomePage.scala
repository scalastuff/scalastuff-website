package scalastuff.website

import xml.NodeSeq
import xml.NodeSeq.Empty

object HomePage extends Page {
  def content(implicit context: TemplateContext) = 
    <h1>Home</h1>

}