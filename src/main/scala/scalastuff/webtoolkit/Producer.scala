package scalastuff.webtoolkit

import java.io.OutputStream
import xml.{Elem,Text}
import io.Codec

trait Producer {
	def produce(os : OutputStream)(implicit context : Context)
}

object Producer {
	def apply(s : String)(implicit context : Context) = new ByteArrayProducer(arrayOf(s))
	def apply(bytes : Array[Byte]) = new ByteArrayProducer(bytes) 
	
	def arrayOf(chars : CharSequence)(implicit context : Context) = {
		val charBuf = java.nio.CharBuffer.wrap(chars)
		val byteBuf = OutputCodec.encoder.encode(charBuf)
		val bytes = new Array[Byte](byteBuf.remaining())
		byteBuf get bytes
		bytes
	}
}

class ByteArrayProducer(bytes : Array[Byte]) extends Producer {
	def produce(os : OutputStream)(implicit context : Context) {
		os.write(bytes)
	}
}

