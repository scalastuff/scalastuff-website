import sbt._
import de.element34.sbteclipsify._

class ScalastuffWebsiteProject(info:ProjectInfo) extends DefaultWebProject(info) {//with Eclipsify {

//  val javaNetRepo = "Java.net Repository for Maven" at "http://download.java.net/maven/2"
//    val maven2Repo = "Maven repo" at "http://repo2.maven.org/maven2"
    val mavenLocal = "Local Maven Repository" at "file://"+Path.userHome.toString.replace("\\", "/")+"/.m2/repository"
  val protostuffRepo = "Protostuff Repository" at "http://protostuff.googlecode.com/svn/repos/maven2/"
     
     
//  val ufj = "net.databinder" %% "unfiltered-jetty" % "0.3.2" withSources() intransitive()
//  val uff = "net.databinder" %% "unfiltered-filter" % "0.3.2" withSources() 
  val slf4s = "org.clapper" % "grizzled-slf4j_2.8.1" % "0.4" 
  val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.6.1" withSources
  val httpclient = "org.apache.httpcomponents" % "httpclient" % "4.1.1" withSources
  val commonsio = "commons-io" % "commons-io" % "2.0.1" withSources
  val servlet = "javax.servlet" % "servlet-api" % "2.5"
  val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.3.1.v20110307" % "test"
  //val jetty7 = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.RC0" % "test"
  val junit = "junit" % "junit" % "4.8" % "test" withSources()

  val scalabeans = "org.scalastuff" %% "scalabeans" % "0.1-SNAPSHOT"
  
  override def packageAction = super.packageAction dependsOn(docAction)
  
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc, packageDocs)
  
  override def managedStyle = ManagedStyle.Maven
  //lazy val touchContextAction = task { print("Touching context..."); None }
 // override def publishAction = super.compileAction dependsOn(touchContextAction)
  lazy val publishTo = Resolver.sftp("Scalastuff Maven Repo", "www.scalastuff.org", "/opt/jetty-7.3.0/webapps")
}