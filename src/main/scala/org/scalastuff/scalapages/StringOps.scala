package org.scalastuff.scalapages

class StringOps(s : String) {

	def toCamelCase : String = toCamelCase(false)
	
  def toCamelCase(capitalizeFirst : Boolean) = {
    val result = s.split("-").filter(_.nonEmpty).map(s => Character.toUpperCase(s(0)) + s.substring(1)).mkString
    if (capitalizeFirst && result.nonEmpty && Character.isUpperCase(result(0))) 
      Character.toUpperCase(result(0)) + result.substring(1)
    else
      result
  }
}