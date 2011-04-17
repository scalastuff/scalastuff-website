package scalastuff.webtoolkit

import Preamble._

// TODO distinguish unset
// TODO evalutate default fun only once
class Context(var vars : Array[Any] = new Array[Any](20)) {
	def get[T](index : Int, defaultValue : => T) : T = 
		if (index >= vars.size) defaultValue
		else vars(index) match {
			case null => defaultValue
			case value => value.asInstanceOf[T]
		}	
		
	def ++(assignments : ContextVarAssignment[_]*) : Context = {
		val change = assignments.exists(a => a._1.index >= vars.size || vars(a._1.index) != a._2)
		if (change) {
			val maxIndex = assignments.map(_._1.index).reduceLeft(_ max _)
			val vars2 = Array.concat(vars, new Array[Any](if (maxIndex >= vars.size) vars.size + 20 else 0))
			assignments.foreach(a => vars2(a._1.index) = a._2)
			new Context(vars2)
		} else this
	}
}


object ContextVarAssignment {
	implicit def toAssignment[T](tpl : (ContextVar[T],T)) = new ContextVarAssignment(tpl._1, tpl._2)
}
class ContextVarAssignment[T](contextVar : ContextVar[T], value : T) extends Tuple2[ContextVar[T],T](contextVar, value)

object ContextVar {
	private var nextIndex = -1
	private def reserveIndex = { nextIndex += 1; nextIndex }
	implicit def toValue[T](contextVar : ContextVar[T])(implicit context : Context) : T = contextVar.get(context)
}

class ContextVar[T](defaultValue : => T) {
	val index = ContextVar.reserveIndex
	def get(implicit context : Context) : T = context.get(index, defaultValue)
}
