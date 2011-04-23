package org.scalastuff.scalapages

import java.io.OutputStream
import org.scalastuff.scalabeans.PropertyDescriptor

case class SeqState(seq : Iterable[_ <: AnyRef], iterator : Iterator[_ <: AnyRef], var index : Int, var first : Boolean)

class InitializeBeansProducer(beanCount : Int, seqCount : Int, children : Seq[Producer]) extends Producer {
	def produce(os : OutputStream)(implicit context : Context) {
	  val newContext = context.copy(beans = new Array[AnyRef](beanCount), beanSeqStates = new Array[SeqState](seqCount))
	  children foreach (_.produce(os)(newContext))
	}
}

class AnyValProducer(beanIndex : Int, property : PropertyDescriptor) extends Producer {
	def produce(os : OutputStream)(implicit context : Context) {
	  val value = property.get(context.beans(beanIndex)).toString
	  os.write(Producer.arrayOf(value))
	}
}

class RootBeanProducer(targetBeanIndex : Int, rootBean : RootBean) extends Producer {
	def produce(os : OutputStream)(implicit context : Context) {
		context.beans(targetBeanIndex) = rootBean.getInstance()
	}
}

class InvokeBeanProducer(beanIndex : Int, targetBeanIndex : Int, property : PropertyDescriptor) extends Producer {
  def produce(os : OutputStream)(implicit context : Context) {
    val bean = context.beans(beanIndex)
    context.beans(targetBeanIndex) = property.get(bean)
  }
}

/**
 * Bean invocation of a sequence-property (extending Iterable)
 */
class InvokeSeqProducer(beanIndex : Int, targetSeqIndex : Int, property : PropertyDescriptor, children : Seq[Producer]) extends Producer {
	def produce(os : OutputStream)(implicit context : Context) {
		val bean = context.beans(beanIndex)
		val seq : Iterable[_ <: AnyRef] = property.get(bean)
		if (seq.nonEmpty) {
			val seqState = SeqState(seq, seq.iterator, -1, false)
			context.beanSeqStates(targetSeqIndex) = seqState
			children.foreach(_.produce(os))
		}
	}
}

class SeqRepeatProducer(seqIndex : Int, targetBeanIndex : Int, children : Seq[Producer]) extends Producer {
	def produce(os : OutputStream)(implicit context : Context) {
		val seqState = context.beanSeqStates(seqIndex)
		seqState.first = true
		while (seqState.iterator.hasNext) {
		  val obj = seqState.iterator.next
	    seqState.index += 1
	    context.beans(targetBeanIndex) = obj
	    children.foreach(_.produce(os))
	    seqState.first = false
		}
	}
}

class SeqFirstProducer(seqIndex : Int, children : Seq[Producer]) extends Producer {
	def produce(os : OutputStream)(implicit context : Context) {
		val seqState = context.beanSeqStates(seqIndex)
		if (seqState.first) 
		  children foreach (_.produce(os))
	}  
}

class SeqLastProducer(seqIndex : Int, children : Seq[Producer]) extends Producer {
	def produce(os : OutputStream)(implicit context : Context) {
		val seqState = context.beanSeqStates(seqIndex)
		if (!seqState.iterator.hasNext) 
			children foreach (_.produce(os))
	}  
}

class SeqIndexProducer(seqIndex : Int, children : Seq[Producer]) extends Producer {
	def produce(os : OutputStream)(implicit context : Context) {
		val seqState = context.beanSeqStates(seqIndex)
		os.write(Producer.arrayOf(seqState.index.toString)) 
	}  
}