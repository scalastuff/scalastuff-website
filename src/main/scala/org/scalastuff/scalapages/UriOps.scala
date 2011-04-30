package org.scalastuff.scalapages

import java.io.{File, IOException}
import java.net.URI

object UriOps {
  implicit def uriOps(uri : URI) = new UriOps(uri)
}

class UriOps(uri : URI) {
	
	/**
	 * Implement #openStream for URI's and support classpath:// scheme at the same time.
	 */
	def openStream = uri.getScheme match {
		case "classpath" => getClass.getClassLoader.getResourceAsStream(uri.getPath.substring(1)) match {
			case null => throw new IOException("Resource not found: " + uri)
			case s => s
		}
		case _ => uri.toURL.openStream
	}
	
	def ensureEndSlash = 
	  if (uri.getPath.endsWith("/")) uri
	  else new URI(uri.getScheme, uri.getUserInfo, uri.getHost, uri.getPort, uri.getPath + "/", uri.getQuery, uri.getFragment)
	
	def list : Seq[URI] =  try {
  	val uri = ensureEndSlash
  	new File(uri).list.map(uri.resolve(_))
  } catch {
    case e => Seq()
  }
}