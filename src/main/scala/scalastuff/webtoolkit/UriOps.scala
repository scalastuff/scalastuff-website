package scalastuff.webtoolkit

import java.io.IOException
import java.net.URI

class UriOps(uri : URI) {
	def openStream = uri.getScheme match {
		case "classpath" => getClass.getClassLoader.getResourceAsStream(uri.getPath.substring(1)) match {
			case null => throw new IOException("Resource not found on classpath: " + uri.getPath)
			case s => s
		}
		case _ => uri.toURL.openStream
	}
}