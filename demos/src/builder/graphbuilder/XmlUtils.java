/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for Java (Swing) functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for Java (Swing) version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for Java (Swing) powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for Java (Swing)
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
package builder.graphbuilder;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides utility methods for parsing and processing XML data.
 */
class XmlUtils {
  private XmlUtils() {
  }


  /**
   * Parses the content of the given resource as an XML document.
   */
  static Document parse(URL resource) {
    try (InputStream is = resource.openStream()) {
      return toolkit.XmlUtils.parse(is);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Returns the direct child elements with the given name.
   */
  static Iterable<Element> getChildrenByTagName(Element parent, String name) {
    return new DescendantsIterable(parent, name, true, element -> true);
  }

  /**
   * Returns all descendant elements with the given name.
   */
  static Iterable<Element> getDescendantsByTagName(Element parent, String name) {
    return new DescendantsIterable(parent, name, false, element -> true);
  }

  /**
   * Returns all descendant elements with the given name.
   */
  static Iterable<Element> getDescendantsByTagName(Element parent, String name, Predicate<Element> predicate) {
    return new DescendantsIterable(parent, name, false, predicate);
  }


  /**
   * Adapts low-level element traversal idoms to {@link Iterable}.
   */
  private static final class DescendantsIterable implements Iterable<Element> {
    final Element parent;
    final String name;
    final boolean childrenOnly;
    final Predicate<Element> predicate;

    DescendantsIterable(
      Element parent, String name, boolean childrenOnly, Predicate<Element> predicate
    ) {
      this.parent = parent;
      this.name = name;
      this.childrenOnly = childrenOnly;
      this.predicate = predicate;
    }

    @Override
    public Iterator<Element> iterator() {
      if (childrenOnly) {
        ArrayList<Element> children = new ArrayList<>();
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
          if (Node.ELEMENT_NODE == child.getNodeType() &&
              name.equals(child.getNodeName()) &&
              predicate.test((Element) child)) {
            children.add((Element) child);
          }
        }
        return children.iterator();
      } else {
        ArrayList<Element> elements = new ArrayList<>();
        NodeList elementsByTagName = parent.getElementsByTagName(name);
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
          Element element = (Element) elementsByTagName.item(i);
          if (predicate.test(element)) {
            elements.add(element);
          }
        }
        return elements.iterator();
      }
    }
  }
}
