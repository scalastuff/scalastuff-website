import sbt._

class ScalastuffWebsiteProject(info:ProjectInfo) extends DefaultWebProject(info) {

//  val javaNetRepo = "Java.net Repository for Maven" at "http://download.java.net/maven/2"
    val maven2Repo = "Maven repo" at "http://repo2.maven.org/maven2"
    
  val ufj = "net.databinder" %% "unfiltered-jetty" % "0.3.0" withSources() intransitive()
  val uff = "net.databinder" %% "unfiltered-filter" % "0.3.0" withSources() 
  val slf4s = "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.4" withSources
  val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.6.1" withSources
  val httpclient = "org.apache.httpcomponents" % "httpclient" % "4.1.1" withSources
  val commonsio = "commons-io" % "commons-io" % "2.0.1" withSources
  val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.3.1.v20110307" % "test"
}