package org.scalastuff.scalapages

import xml.{NodeSeq,MetaData,Text,XML}

object ScaladocProcessor extends Processor {
	override def preProcess(recurse : NodeSeq => Context => NodeSeq)(implicit context : Context) = {
	  case <scaladoc:content/> => 
	  	val content = io.Source.fromInputStream(getClass.getResourceAsStream(CurrentPage.path.mkString("/","/","")))
	  	for (line <- content.getLines) println("CONTENT: " + line)
	  	val xml = XML.load(content.reader)
//	    val scaladoc = XML.load(getClass.getResourceAsStream(CurrentPage.path.mkString("/","/","")))
	    <h1>Current page ": {xml}</h1>
	}

}