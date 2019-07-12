/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package databinding.graphbuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Provides utility methods for parsing and processing XML data.
 */
class XmlUtils {
  private XmlUtils() {
  }


  /**
   * Parses the content of the given resource as an XML document.
   */
  static Document parse( URL resource ) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      try (InputStream is = resource.openStream()) {
        Document document = db.parse(is);
        return document;
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Returns the direct child elements with the given name.
   */
  static Iterable<Element> getChildrenByTagName( Element parent, String name ) {
    return new DescendantsIterable(parent, name, true);
  }

  /**
   * Returns all descendant elements with the given name.
   */
  static Iterable<Element> getDescendantsByTagName( Element parent, String name ) {
    return new DescendantsIterable(parent, name, false);
  }


  /**
   * Adapts low-level element traversal idoms to {@link Iterable}.
   */
  private static final class DescendantsIterable implements Iterable<Element> {
    final Element parent;
    final String name;
    final boolean childrenOnly;

    DescendantsIterable( Element parent, String name, boolean childrenOnly ) {
      this.parent = parent;
      this.name = name;
      this.childrenOnly = childrenOnly;
    }

    @Override
    public Iterator<Element> iterator() {
      if (childrenOnly) {
        ArrayList<Element> children = new ArrayList<>();
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
          if (Node.ELEMENT_NODE == child.getNodeType() &&
              name.equals(child.getNodeName())) {
            children.add((Element) child);
          }
        }
        return children.iterator();
      } else {
        return new NodeListIterator(parent.getElementsByTagName(name));
      }
    }
  }

  /**
   * Adapts the index-based {@link NodeList} iteration idom to {@link Iterator}. 
   */
  private static class NodeListIterator implements Iterator<Element> {
    final NodeList elements;
    int idx;

    NodeListIterator( NodeList elements ) {
      this.elements = elements;
    }

    @Override
    public boolean hasNext() {
      return idx < elements.getLength();
    }

    @Override
    public Element next() {
      if (hasNext()) {
        return (Element) elements.item(idx++);
      } else {
        throw new NoSuchElementException();
      }
    }
  }
}
