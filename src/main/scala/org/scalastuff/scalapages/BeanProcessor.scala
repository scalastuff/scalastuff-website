package org.scalastuff.scalapages

import scala.xml.{Elem, Node, NodeSeq, Text, XML}
import io.Codec
import org.scalastuff.scalabeans.{BeanDescriptor,PropertyDescriptor}
import org.scalastuff.scalabeans.Preamble._
import collection.mutable
import Preamble._
import org.scalastuff.scalabeans.types.{IterableType,AnyValType}

object RootBean {
	def apply[T <: AnyRef](prefix : String, getInstance : => T)(implicit manifest : Manifest[T]) =
	  new RootBean(prefix, descriptorOf(manifest), () => getInstance)
	  
  def apply[T <: AnyRef](prefix : String)(implicit manifest : Manifest[T]) = {
    val desc = descriptorOf(manifest)
    new RootBean(prefix, desc, () => desc.newInstance().asInstanceOf[T]) 
  }
}

/**
 * Root beans need an explicit descriptor.
 */
case class RootBean private (prefix : String, desc : BeanDescriptor, getInstance : () => AnyRef)

/**
 * This context var should be filled with root beans upon start. 
 * Root beans are the starting points for bean navigation
 */
object RootBeans extends ContextVar[Seq[RootBean]](Seq())

/**
 * Maps prefixes to indexes. 
 * Not used while producing.
 */
private object PrefixIndexes extends ContextVar[mutable.Map[String, Int]](mutable.Map())

private class ProcessingState {
  var nextBeanIndex : Int = 0
  var nextSeqIndex : Int = 0
  var prefixMap = Map[String,BoundProcessor]()
}

private object ProcessingState extends ContextVar[ProcessingState](new ProcessingState)

object BeanProcessor {
  val NS = "http://scalapages.scalastuff.org/beans"
}

trait BeanProcessor extends Processor {
  
  var beans : Array[AnyRef]
  
	override def process(recurse : NodeSeq => Context => Seq[Producer])(implicit context : Context) = {
    
    // match root
    case root : RootNode =>
      
      // initialize processing state
      ProcessingState.nextBeanIndex = 0
      ProcessingState.nextSeqIndex = 0
      
      // set root bean processors 
      val rootProcessors = RootBeans.get.map(new RootProcessor(_))
      ProcessingState.prefixMap = Map(rootProcessors.map(root => (root.rootBean.prefix, root)):_*)
      
      // recurse
      val children = recurse(root.child)(context)
      
      // process root processors
      val childrenWithRoots = (rootProcessors :\ children)(_.process(_))
      
      // initialize
      Seq(new InitializeBeansProducer(ProcessingState.nextBeanIndex, ProcessingState.nextSeqIndex, childrenWithRoots))
    	
    // elements are matched that have an unbound or special namespace
		case e : Elem if e.prefix != null && (e.namespace == "" || e.namespace == BeanProcessor.NS) =>
		  
		  // find the processor bound to this prefix
		  BoundProcessors.get.get(e.prefix) match {
		    case Some(processor) => processor.invoke(e, recurse)
		    case None => Throw("Prefix not bound in %s.%s".format(e.prefix, e.label))
		  }
	}
}

trait BoundProcessor {
	def invoke(elem : Elem, recurse : NodeSeq => Context => Seq[Producer])(implicit context : Context) : Seq[Producer] = Throw("Internal error")
  def process(children : Seq[Producer])(implicit context : Context) : Seq[Producer]
}

private object BoundProcessors extends ContextVar[Map[String, BoundProcessor]](null)


private class AnyValProcessor(elem : Elem, beanIndex : Int, property : PropertyDescriptor) extends BoundProcessor {
  	override def process(children : Seq[Producer])(implicit context : Context) = {
  	  new AnyValProducer(beanIndex, property) +: children
  	}
}

private abstract class BoundBeanProcessor(desc : BeanDescriptor) extends BoundProcessor {

  // only assign an index when this bean is actually referred to
	var beanIndexOption : Option[Int] = None
	
	// assign an index now
  def beanIndex(implicit context : Context) = beanIndexOption match {
	  case Some(index) => index
	  case None =>
	    beanIndexOption = Some(ProcessingState.nextBeanIndex)
	    ProcessingState.nextBeanIndex += ProcessingState.nextBeanIndex + 1
	    beanIndexOption.get
  }
	  
	override def invoke(elem : Elem, recurse : NodeSeq => Context => Seq[Producer])(implicit context : Context) = {

	  // make property name
	  val name = elem.label.toCamelCase
	  
	  // get property desc
	  val property = desc.property(name) match {
	    case Some(property) => property
	    case None => Throw("Property %s.%s not found in %s:%s".format(desc.toString, name, elem.prefix, elem.label))
	  }
	  
	  // save old prefix map
	  val oldPrefixMap = ProcessingState.prefixMap
	  
	  // was it a sequence property?
	  val processor = property.scalaType match {
	    case IterableType(t) => 
	      val prefix = elem getOrElse("@prefix", elem.label)
	      val seqPrefix = elem getOrElse("@seq-prefix", "seq")
	      val repeatProcessor = new SeqRepeatProcessor(beanIndex, descriptorOf(property.scalaType))
	      val seqProcessor = new SeqProcessor(elem, seqPrefix, repeatProcessor, beanIndex, property)
	      ProcessingState.prefixMap += (prefix -> repeatProcessor, seqPrefix -> seqProcessor)
	      seqProcessor
	    case AnyValType() => new AnyValProcessor(elem, beanIndex, property)
//	    case _ if elem has "@prefix" =>
	  }
	  
	  // recurse
	  val children = recurse(elem.child)(context)
	  
	  // restore old prefix map
	  ProcessingState.prefixMap = oldPrefixMap
	  
	  // apply new processor
	  processor.process(children)
	}
}

private class InvokeBeanProcessor(sourceIndex : Int, property : PropertyDescriptor) extends BoundBeanProcessor(descriptorOf(property.scalaType)) {
	override def process(children : Seq[Producer])(implicit context : Context) = {
	  beanIndexOption match {
	    case Some(index) => new InvokeBeanProducer(sourceIndex, index, property) +: children
	    case None => children
	  }
	}  
}

private class SeqProcessor(parent : Elem, defaultSeqPrefix : String, implicitRepeat : SeqRepeatProcessor, sourceBeanIndex : Int, property : PropertyDescriptor) extends BoundProcessor {

  // only assign an index when this bean is actually referred to
	var seqIndexOption : Option[Int] = None
	
	var explicitRepeat : Option[SeqRepeatProcessor] = None

  // assign an index now
  def seqIndex(implicit context : Context) = seqIndexOption match {
	  case Some(index) => index
	  case None =>
	    seqIndexOption = Some(ProcessingState.nextSeqIndex)
	    ProcessingState.nextSeqIndex += ProcessingState.nextSeqIndex + 1
	    seqIndexOption.get
  }
	  

	override def invoke(elem : Elem, recurse : NodeSeq => Context => Seq[Producer])(implicit context : Context) = {
	
	  elem.label match {
	    case "first" => Seq(new SeqFirstProducer(seqIndex, recurse(elem.child)(context)))
	    case "last" => Seq(new SeqLastProducer(seqIndex, recurse(elem.child)(context)))
	    case "index" => Seq(new SeqIndexProducer(seqIndex, recurse(elem.child)(context)))
	    case "repeat" =>
		    
		    if (explicitRepeat.isDefined) Throw("Multiple repeat sections in %s:%s".format(parent.prefix, parent.label))
		    explicitRepeat = Some(new SeqRepeatProcessor(seqIndex, descriptorOf(property.scalaType)))
	      
			  // save old prefix map
			  val oldPrefixMap = ProcessingState.prefixMap
			  
			  // add processor to map
			  val prefix = elem getOrElse ("@prefix", defaultSeqPrefix)
			  ProcessingState.prefixMap += (prefix -> explicitRepeat.get)
			  
			  // recurse children
	      val children = recurse(elem.child)(context)
	      
    	  // restore old prefix map
	      ProcessingState.prefixMap = oldPrefixMap
	      
	      // process actual repeat 
	      explicitRepeat.get.process(children)
	  }
	}
	
	override def process(children : Seq[Producer])(implicit context : Context) = {
	  
	  if (implicitRepeat.beanIndexOption.isDefined && explicitRepeat.isDefined) Throw("Element referred to outside repeat section in %s:%s".format(parent.prefix, parent.label))

	  // either seq-operation are used, or bean-invocations in implicit or explicit repeat 
	  if (seqIndexOption.isDefined || implicitRepeat.beanIndexOption.isDefined || (explicitRepeat.isDefined && explicitRepeat.get.beanIndexOption.isDefined)) {
	    val children2 = explicitRepeat match {
	      case Some(proc) => proc.process(children)
	      case None => children
	    }
	    Seq(new InvokeSeqProducer(sourceBeanIndex, seqIndex, property, children2))
	  } else  
	    children
	}  
}

private class SeqRepeatProcessor(seqIndex : Int, desc : BeanDescriptor) extends BoundBeanProcessor(desc) {
	override def process(children : Seq[Producer])(implicit context : Context) = {
		beanIndexOption match {
		case Some(index) =>
		  Seq(new SeqRepeatProducer(seqIndex, beanIndex, children))
		case None => children
		}
	}  
}

private class RootProcessor(val rootBean : RootBean) extends BoundBeanProcessor(rootBean.desc) {
	override def process(children : Seq[Producer])(implicit context : Context) = {
	  beanIndexOption match {
	    case Some(index) => new RootBeanProducer(index, rootBean) +: children
	    case None => children
	  }
	}  
}

