package org.scalastuff.scalapages

import java.io.IOException
import java.net.URI

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
}