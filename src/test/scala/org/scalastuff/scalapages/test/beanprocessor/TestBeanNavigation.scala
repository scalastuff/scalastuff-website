package org.scalastuff.scalapages.test.beanprocessor

import org.scalastuff.scalapages._
import org.junit.{Assert, Test}
import java.net.URI 

class TestBeanNavigation {

  object PersonServer {
    val allPersons = List {
      Person("Alexander Dvorkovvy", List(Address("Maarssen")))
      Person("Ruud Diterwich", List(Address("Hilversum")))
    }
  }
  
  case class Person (
    var name : String = "",
    var addresses : Seq[Address] = Nil
  )
  
  case class Address (
    var city : String = ""
  )
  
  @Test
  def testBean {
  	val rootBean = RootBean("server", PersonServer)
    implicit val context = Context(
        RootBeans -> Seq(rootBean), 
        TemplateBaseURI -> new URI("classpath:/org/scalastuff/scalapages/test/beanprocessor/")) 
    val server = new PageServer
  }
  
}