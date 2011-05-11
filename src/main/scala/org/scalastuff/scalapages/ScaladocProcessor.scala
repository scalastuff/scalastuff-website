package org.scalastuff.scalapages

import xml.{NodeSeq,MetaData,Text,XML}

object ScaladocProcessor extends Processor {
	override def preProcess(recurse : NodeSeq => Context => NodeSeq)(implicit context : Context) = {
	  case <scaladoc:content/> => 
	  	val content = io.Source.fromInputStream(getClass.getResourceAsStream(CurrentPage.path.mkString("/","/","")))
	  	var docType : Option[String] = None
	  	val builder = new StringBuilder
	  	for (line <- content.getLines) {
	  	  println("CONTENT: " + line)
	  	  if (line.startsWith("<?xml")) None
	  	  else if (line.startsWith("<!DOCTYPE")) docType = Some(line)
	  	  else builder.append(line).append("\n")
	  	}
	  	println("CONTENT: " + builder.toString)
	  	val xml = XML.loadString(builder.toString)
//	    val scaladoc = XML.load(getClass.getResourceAsStream(CurrentPage.path.mkString("/","/","")))
	    <h1>Current page ": {xml}</h1>
	}

}