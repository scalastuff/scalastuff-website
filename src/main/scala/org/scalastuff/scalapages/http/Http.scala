package org.scalastuff.scalapages.http

import java.text.SimpleDateFormat
import java.util.{Locale, TimeZone}
import javax.servlet.http.{HttpServletRequest,HttpServletResponse}

object Header {
  val RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz"
  
 	private val dateFormatter = new SimpleDateFormat(RFC1123, Locale.US);
  dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"))
  
  def format(date : java.util.Date) = dateFormatter.format(date)
}

class Header(name : String) {
  def apply(value : String) = new HeaderResponse(name, value)
  def apply(date : java.util.Date) = new HeaderResponse(name, Header.format(date))
}
 
// http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.10

object AcceptRanges extends Header("Accept-Ranges")
object Age extends Header("Age")
object Allow extends Header("Allow")
object CacheControl extends Header("Cache-Control")
object Connection extends Header("Connection")
object ContentEncoding extends Header("Content-Encoding")
object ContentLanguage extends Header("Content-Language")
object ContentLength extends Header("Content-Length")
object ContentLocation extends Header("Content-Location")
object ContentMD5 extends Header("Content-MD5")
object ContentRange extends Header("Content-Range")
object Date extends Header("Date")
object ETag extends Header("ETag")
object Expires extends Header("Expires")
object LastModified extends Header("Last-Modified")
object Location extends Header("Location")
object Pragma extends Header("Pragma")
object ProxyAuthenticate extends Header("Proxy-Authenticate")
object RetryAfter extends Header("Retry-After")
object Server extends Header("Server")
object Trailer extends Header("Trailer")
object TransferEncoding extends Header("Transfer-Encoding")
object Vary extends Header("Vary")
object Warning extends Header("Warning")
object WWWAuthenticate extends Header("WWW-Authenticate")

class ContentType(contentType : String) extends Response {
  def respond(response : HttpServletResponse) = response.setContentType(contentType)
}

class CharContentType(contentType : String, charset : String = "utf=8") extends 
	ContentType(contentType + "; charset=" + charset) 

object CssContent extends CharContentType("text/css")
object HtmlContent extends CharContentType("text/html")
object JsContent extends CharContentType("text/javascript")
object CsvContent extends CharContentType("text/csv")
object TextXmlContent extends CharContentType("text/xml")
object PlainTextContent extends CharContentType("text/plain")
object JsonContent extends CharContentType("application/json")
object ApplicationXmlContent extends CharContentType("application/xml")
object FormEncodedContent extends ContentType("application/x-www-form-urlencoded")
