package org.scalastuff.scalapages

object Throw  {
	
	def apply(message : String)(implicit context : Context) : Nothing = {
		throw new Exception(message + trail)
	}
	
	def apply(e : Throwable)(implicit context : Context) : Nothing = {
		throw new Exception(e.getMessage + trail, e)
	}
	def apply(message : String, e : Throwable)(implicit context : Context) : Nothing = {
		throw new Exception(message + trail, e)
	}
	
	def trail(implicit context : Context) = CurrentTemplateURIs.map("\n  while reading file " + _).mkString
}