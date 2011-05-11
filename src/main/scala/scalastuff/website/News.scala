package scalastuff.website

import java.util.Date
import xml.NodeSeq

case class Project
case class News(date : Date, project : Option[Project], html : NodeSeq)
case class Blog(title : String, date : Date, author : String, html : NodeSeq)