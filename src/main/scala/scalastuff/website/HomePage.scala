package scalastuff.website

import xml.NodeSeq.Empty
import scalastuff.webtoolkit.PageRequest

object HomePage extends ScalastuffPage {
	
	val path = Nil
	
	def html(implicit request : PageRequest) =
    <div style="position:relative;height: 400px;">
	  <div style="top: 10%; left: 10%">
	  <h3>Stuff we Made</h3>
	  <p>This is the stuff we made ourselves</p>
	  </div>
	  <div style="position:absolute;top: 50%; left: 60%">
	  <h3>Stuff we Made</h3>
	  <p>This is the stuff we made ourselves</p>
	  </div>
	  <div style="position:absolute;top: 60%; left: 20%">
	  <h3>Scala trends</h3>
	  <p></p>
	  <p>Read <a href="http://scala-boss.heroku.com/#1">How to convince your boss?</a></p>
	  </div>
	</div>
}