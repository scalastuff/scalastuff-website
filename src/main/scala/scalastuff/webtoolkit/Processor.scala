package scalastuff.webtoolkit

import xml.{ Elem, Node, NodeSeq, Text }
import io.Codec
import Preamble._

case class RootNode(child : Node*) extends Node {
	val label = ""
}

object Processor {
	def preProcess(xml: NodeSeq, processors : Seq[Processor])(implicit context: Context) : NodeSeq = {
		val xml2 = (processors :\ xml)(_ preProcess _)
		preProcess2(RootNode(xml2 :_*), 1, processors)
	}
	
  private def preProcess2(xml: NodeSeq, phase : Int, processors : Seq[Processor])(implicit context: Context) : NodeSeq = {
    xml flatMap { node =>
      val recurse: NodeSeq => Context => NodeSeq = xml => context => xml.flatMap(preProcess2(_, phase, processors)(context))
      val pfs = processors.map(_.preProcess(phase, recurse)(context))
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
      val recurse: NodeSeq => Seq[Producer] = _.flatMap(process(_, processors))
      val pfs = processors.map(_.process(recurse)(context))
      pfs find (_.isDefinedAt(node)) match {
        case Some(pf) => pf(node)
        case None => recurse(node.child)
      }
    }
  }
}

trait Processor {
  def name: Option[String] = None

  def preProcess(xml: NodeSeq)(implicit context: Context): NodeSeq = xml

  def preProcess(phase : Int, recurse: NodeSeq => Context => NodeSeq)(implicit context: Context): PartialFunction[Node, NodeSeq] = Map()

  def process(recurse: NodeSeq => Seq[Producer])(implicit context: Context): PartialFunction[Node, Seq[Producer]] = Map()
}

