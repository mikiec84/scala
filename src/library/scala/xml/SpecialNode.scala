/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2009, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

// $Id$


package scala.xml

import collection.immutable.{List, Nil, ::}
import collection.mutable.StringBuilder

/** <p>
 *    <code>SpecialNode</code> is a special XML node which
 *    represents either text (PCDATA), a comment, a PI, or an entity ref.
 *  </p>
 *  <p>
 *    SpecialNodes also play the role of XMLEvents for pull-parsing.
 *  </p>
 *
 *  @author Burak Emir
 */
abstract class SpecialNode extends Node with pull.XMLEvent
{
  /** always empty */
  final override def attributes = Null

  /** always Node.EmptyNamespace */
  final override def namespace = null

  /** always empty */
  final def child = Nil

  /** append string representation to the given stringbuffer */
  def buildString(sb: StringBuilder): StringBuilder
}
