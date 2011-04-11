package scalastuff.webtoolkit

import java.net.URI

object Preamble {
	implicit def uriOps(uri : URI) = new UriOps(uri)
}