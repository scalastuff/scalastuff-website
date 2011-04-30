package org.scalastuff.scalapages

object StringOps {
  implicit def stringOps(s : String) = new StringOps(s)
}

class StringOps(s : String) {

	def toCamelCase : String = toCamelCase(false)
	
  def toCamelCase(capitalizeFirst : Boolean) = {
    val result = s.split("-").filter(_.nonEmpty).map(s => Character.toUpperCase(s(0)) + s.substring(1)).mkString
    if (capitalizeFirst && result.nonEmpty && Character.isUpperCase(result(0))) 
      Character.toUpperCase(result(0)) + result.substring(1)
    else
      result
  }
	
	def splitWords : List[String] = {
	  val builder = List.newBuilder[String]
	  var pos = 0
	  var start = 0
	  var lastWasCap = true 
	  while (pos < s.size) {
	    val char = s(pos)
	    if ((!char.isLetterOrDigit) ||
	        (!lastWasCap && char.isUpper) ||
	        lastWasCap && char.isUpper && pos + 1 < s.size && s(pos + 1).isLower) {
	      if (pos > start) builder += s.substring(start, pos)
	    	start = if (char.isLetterOrDigit) pos else pos + 1
	    }
	    pos += 1
	    lastWasCap = char.isUpper
	  }
	   if (pos > start) builder += s.substring(start, pos)
	  builder.result
	}
}