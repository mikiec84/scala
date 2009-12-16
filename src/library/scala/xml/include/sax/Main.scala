/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2002-2010, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id$

package scala.xml
package include.sax

import scala.xml.include._
import scala.util.control.Exception.{ catching, ignoring }
import org.xml.sax.{ SAXException, SAXParseException, EntityResolver, XMLReader }
import org.xml.sax.helpers.XMLReaderFactory

object Main {
  private val xercesClass = "org.apache.xerces.parsers.SAXParser"
  private val namespacePrefixes = "http://xml.org/sax/features/namespace-prefixes"
  private val lexicalHandler = "http://xml.org/sax/properties/lexical-handler"

  /**
  * The driver method for xinc
  * Output is written to System.out via Conolse
  * </p>
  *
  * @param args  contains the URLs and/or filenames
  *              of the documents to be procesed.
  */
  def main(args: Array[String]) {
    def saxe[T](body: => T) = catching[T](classOf[SAXException]) opt body
    def error(msg: String) = System.err.println(msg)

    val parser: XMLReader =
      saxe[XMLReader](XMLReaderFactory.createXMLReader()) getOrElse (
        saxe[XMLReader](XMLReaderFactory.createXMLReader(xercesClass)) getOrElse (
          return error("Could not find an XML parser")
        )
      )

    // Need better namespace handling
    try parser.setFeature(namespacePrefixes, true)
    catch { case e: SAXException => return System.err.println(e) }

    if (args.isEmpty)
      return

    def dashR = args.size >= 2 && args(0) == "-r"
    val args2 = if (dashR) args drop 2 else args
    val resolver: Option[EntityResolver] =
      if (dashR) None
      else catching(classOf[Exception]) opt {
          val r = Class.forName(args(1)).newInstance().asInstanceOf[EntityResolver]
          parser setEntityResolver r
          r
        } orElse (return error("Could not load requested EntityResolver"))

    for (arg <- args2) {
      try {
        val includer = new XIncludeFilter()
        includer setParent parser
        val s = new XIncluder(System.out, "UTF-8")
        includer setContentHandler s

        resolver map (includer setEntityResolver _)
        // SAXException here means will not support comments
        ignoring(classOf[SAXException]) {
          includer.setProperty(lexicalHandler, s)
          s setFilter includer
        }
        includer parse arg
      }
      catch {
        case e: SAXParseException =>
          error(e.toString)
          error("Problem in %s at line %d".format(e.getSystemId, e.getLineNumber))
        case e: SAXException =>
          error(e.toString)
      }
    }
  }
}
