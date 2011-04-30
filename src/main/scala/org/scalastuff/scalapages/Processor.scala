package org.scalastuff.scalapages

import xml.{ Elem, Node, NodeSeq, Text }
import io.Codec
import org.scalastuff.scalabeans.Preamble._
import Preamble._

case class RootNode(child : Node*) extends Node {
	val label = ""
}

object Processor {
  
  def load(className : String)(implicit context: Context) = {
    try {
      val desc = descriptorOf(Class.forName(className))
      val cc = Class.forName(desc.beanType.erasure.getName + "$")
      println("MODULE:"+cc.getField("MODULE$").get(cc))
      desc.companion match {
        case Some(processor : Processor) => processor
        case None => desc.newInstance().asInstanceOf[Processor]
        case c => println("NONOL: " + c) 
        null
      }
    } catch {
      case e => Throw("Couldn't load processor %s".format(className), e)
    }
  }
  
	def preProcess(xml: NodeSeq, processors : Seq[Processor])(implicit context: Context) : NodeSeq = {
	  processors foreach (_.initialize)
		val xml2 = (processors :\ xml)(_ preProcess _)
		preProcess2(RootNode(xml2 :_*), processors)
	}
	
  private def preProcess2(xml: NodeSeq, processors : Seq[Processor])(implicit context: Context) : NodeSeq = {
    xml flatMap { node =>
      val recurse: NodeSeq => Context => NodeSeq = xml => context => xml.flatMap(preProcess2(_, processors)(context))
      val pfs = processors.map(_.preProcess(recurse)(context))
      pfs find (_.isDefinedAt(node)) match {
        case Some(pf) => pf(node)
        case None => node match {
        	case r: RootNode => RootNode(recurse(r.child)(context):_*)
	        case e: Elem => e.copy(child = recurse(e.child)(context))
	        case n => n
	      }
      }
    }
  }
  
  def process(xml: NodeSeq, processors : Seq[Processor])(implicit context: Context) : Seq[Producer] = {
    xml flatMap { node =>
      val recurse: NodeSeq => Context => Seq[Producer] = xml => context => xml.flatMap(process(_, processors)(context))
      val pfs = processors.map(_.process(recurse)(context))
      pfs find (_.isDefinedAt(node)) match {
        case Some(pf) => pf(node)
        case None => recurse(node.child)(context)
      }
    }
  }
}

trait Processor {
  def name: Option[String] = None

  def initialize {}
  
  def preProcess(xml: NodeSeq)(implicit context: Context): NodeSeq = xml

  def preProcess(recurse: NodeSeq => Context => NodeSeq)(implicit context: Context): PartialFunction[Node, NodeSeq] = Map()

  def process(recurse: NodeSeq => Context => Seq[Producer])(implicit context: Context): PartialFunction[Node, Seq[Producer]] = Map()
}

class CompoundProcessor(processors : Processor*) extends Processor {
  override def initialize = processors foreach (_ initialize)
  override def preProcess(xml: NodeSeq)(implicit context: Context) = (processors :\ xml)(_ preProcess _)
  override def preProcess(recurse: NodeSeq => Context => NodeSeq)(implicit context: Context) = new PartialFunction[Node, NodeSeq] {
    def isDefinedAt(node : Node) = processors.map(_.preProcess(recurse)(context)) exists (_.isDefinedAt(node))
    def apply(node : Node) = processors.map(_.preProcess(recurse)(context)) find (_.isDefinedAt(node)) match {
        case Some(pf) => pf(node)
        case None => Throw("Internal error: Processor no longer found for " + node)
      }
  }
  
  override def process(recurse: NodeSeq => Context => Seq[Producer])(implicit context: Context) = new PartialFunction[Node, Seq[Producer]] {
    def isDefinedAt(node : Node) = processors.map(_.process(recurse)(context)) exists (_.isDefinedAt(node))
    def apply(node : Node) = processors.map(_.process(recurse)(context)) find (_.isDefinedAt(node)) match {
        case Some(pf) => pf(node)
        case None => Throw("Internal error: Processor no longer found for " + node)
      }
  }
}