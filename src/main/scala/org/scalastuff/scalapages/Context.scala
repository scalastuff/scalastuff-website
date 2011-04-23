package org.scalastuff.scalapages

import Preamble._

object Context {
	type Assignment[T] = (ContextVar[T], T)
	def apply(assignments : Context.Assignment[_]*) = new Context ++ (assignments:_*)
}

case class Context(
    private var vars : Array[Any] = new Array[Any](20), 
    val beans : Array[AnyRef] = new Array[AnyRef](0),
    val beanSeqStates : Array[SeqState] = new Array[SeqState](0)) {
	def isSet[T](index : Int) : Boolean = index < vars.size && vars(index) != null 
	def get[T](index : Int, defaultValue : => T) : T = {
		if (index >= vars.size) vars = Array.concat(vars, new Array[Any](index + 20))
		vars(index) match {
			case null => 
			  if (defaultValue != null) vars(index) = defaultValue
			  vars(index).asInstanceOf[T]
			case value => value.asInstanceOf[T]
		}	
	}
		
//  def set[T](index : Int, value : T) = {
//    if (index >= vars.size || vars(index) != value) {
//    	Array.concat(vars, new Array[Any](if (index >= vars.size) index + 20 else 0))
//    	varsCloned = true
//    	vars(index) = value
//    }
//  }
//  def setAll(assignments : Assignment[_]*) : Context = {
//  	val change = assignments.exists(a => a._1.index >= vars.size || vars(a._1.index) != a._2)
//  	if (change) {
//  		val maxIndex = assignments.map(_._1.index).reduceLeft(_ max _)
//  		val vars2 = Array.concat(vars, new Array[Any](if (maxIndex >= vars.size) vars.size + 20 else 0))
//  		assignments.foreach(a => vars2(a._1.index) = a._2)
//  		new Context(vars2)
//  	} else this
//  }
	def ++(assignments : Context.Assignment[_]*) : Context = {
		val change = assignments.exists(a => a._1.index >= vars.size || vars(a._1.index) != a._2)
		if (change) {
			val maxIndex = assignments.map(_._1.index).reduceLeft(_ max _)
			val vars2 = Array.concat(vars, new Array[Any](if (maxIndex >= vars.size) maxIndex + 20 else 0))
			assignments.foreach(a => vars2(a._1.index) = a._2)
			new Context(vars2, beans, beanSeqStates)
		} else this
	}
}

object ContextWrapper {
	implicit def toContext(wrapper : ContextWrapper) = wrapper.context
}

case class ContextWrapper(var context : Context) {
}

object ContextVar {
	private var nextIndex = -1
	private def reserveIndex = { nextIndex += 1; nextIndex }
	implicit def toValue[T](contextVar : ContextVar[T])(implicit context : Context) : T = contextVar.get(context)
}

class ContextVar[T](defaultValue : => T = null) {
	val index = ContextVar.reserveIndex
	def isSet(implicit context : Context) : Boolean = context.isSet(index)
	def get(implicit context : Context) : T = context.get(index, defaultValue)
}
