package org.scalastuff.scalapages

object StringOps {
  implicit def stringOps(s : String) = new StringOps(s)
}

class StringOps(s : String) {

	def toCamelCase : String = toCamelCase(false)
	
  def toCamelCase(capitalizeFirst : Boolean) = {
    val result = s.split("-").filter(_.nonEmpty)
    if (result.nonEmpty) {
      if (capitalizeFirst) Character.toUpperCase(result(0)(0)) + result(0).substring(1) else result(0) +
      result.drop(1).map(s => Character.toUpperCase(s(0)) + s.substring(1)).mkString
    }
    else
      ""
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