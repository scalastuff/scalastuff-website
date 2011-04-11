package scalastuff.website.scalabeans

import scalastuff.website._
import xml.NodeSeq
import scalastuff.webtoolkit.{PageRequest}


object ScalaBeansPage extends ScalastuffPage {
  
	val path = "projects" :: "scalabeans" :: Nil;
  
  def html(implicit request: PageRequest) =  
    <div>
    <h1>ScalaBeans</h1>
    <p>ScalaBeans is a reflection toolkit for Scala.</p>

    <p>Features:</p>

    <ul>
      <li>Provides consistent view of beans, suitable for persistence, serialization and GUI frameworks</li>
      <li>Recognizes vals, vars, Scala getters and setters, constructor parameters (including their default values)</li>
      <li>Provides high-performance property getters, setters, bean constructors</li>
      <li>Type information is fully preserved (including type parameters) and can be pattern-matched</li>
      <li>Collection builders can be obtained from collection type</li>
      <li>Integration with <a href="http://code.google.com/p/protostuff/">protostuff</a></li>
    </ul>

    <p>Roadmap:</p>

    <ul>
      <li>BeanDescriptor views</li>
      <li>Cloning</li>
      <li>Arrays</li>
      <li>Polymorphism</li>
      <li>Object graph support for protobuf</li>
      <li>Mapping beans to .proto schema</li>
      <li>Java types (including coercion)</li>
    </ul>
	</div>
}
