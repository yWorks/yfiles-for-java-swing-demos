/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.bpmn.di;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Provides convenience methods to search for specific XElements and XAttributes and test results for the relevant BPMN
 * Namespaces.
 */
class BpmnNamespaceManager {

  public static final String NS_BPMN = "http://www.omg.org/spec/BPMN/20100524/MODEL";
  public static final String NS_BPMN_DI = "http://www.omg.org/spec/BPMN/20100524/DI";
  public static final String NS_DI = "http://www.omg.org/spec/DD/20100524/DI";
  public static final String NS_DC = "http://www.omg.org/spec/DD/20100524/DC";


  /**
   * Returns all Attributes of the Element that belong to the given namespace.
   * @param xElement The Element
   * @param nameSpace The namespace
   * @return The list with all items left in the namespaces.
   */
  static Iterable<Attr> attributesInNamespace( Element xElement, String nameSpace ) {
    NamedNodeMap attrs = xElement.getAttributes();
    int n = attrs.getLength();
    List<Attr> result = new ArrayList<>(n);
    for (int i = 0; i < n; ++i) {
      Attr attr = (Attr) attrs.item(i);
      String ns = attr.getNamespaceURI();
      if (ns == null || ns.equals(nameSpace)) {
        result.add(attr);
      }
    }
    return result;
  }

  /**
   * Returns the value of the given attribute in the given XElement.
   * @param xElement The xElement
   * @param nameSpace The namespace
   * @param attributeName The local name of the attribute
   */
  static String getAttributeValue( Element xElement, String nameSpace, String attributeName ) {
    NamedNodeMap attrs = xElement.getAttributes();
    Attr attr = (Attr) attrs.getNamedItem(attributeName);
    if (attr != null) {
      String ns = attr.getNamespaceURI();
      if (ns == null || ns.equals(nameSpace)) {
        return attr.getValue();
      }
    }
    return null;
  }

  /**
   * Returns the child XML element with the given namespace and local name.
   * @param xElement The element
   * @param nameSpace The namespace
   * @param localName The local name
   */
  static Element getElement( Element xElement, String nameSpace, String localName ) {
    for (Node child = xElement.getFirstChild(); child != null; child = child.getNextSibling()) {
      if (Node.ELEMENT_NODE == child.getNodeType() &&
          localName.equals(getLocalName((Element) child))) {
        String ns = child.getNamespaceURI();
        if (ns != null && ns.equals(nameSpace)) {
          return (Element) child;
        }
      }
    }
    return null;
  }

  /**
   * Returns the child XML element with the given namespace and local name.
   * @param xElement The element
   * @param nameSpace The namespace
   * @param localName The local name
   */
  static Iterable<Element> getElements( Element xElement, String nameSpace, String localName ) {
    List<Element> result = new ArrayList<>();
    for (Node child = xElement.getFirstChild(); child != null; child = child.getNextSibling()) {
      if (Node.ELEMENT_NODE == child.getNodeType() &&
          localName.equals(getLocalName((Element) child))) {
        String ns = child.getNamespaceURI();
        if (ns != null && ns.equals(nameSpace)) {
          result.add((Element) child);
        }
      }
    }
    return result;
  }

  static String getLocalName( Element element ) {
    if (element.getNamespaceURI() == null) {
      String name = element.getTagName();
      int idx = name.lastIndexOf(':');
      return idx > -1 ? name.substring(idx + 1) : name;
    } else {
      return element.getLocalName();
    }
  }

  static Iterable<Element> children( final Element xElement ) {
    List<Element> result = new ArrayList<>();
    for (Node child = xElement.getFirstChild(); child != null; child = child.getNextSibling()) {
      if (Node.ELEMENT_NODE == child.getNodeType()) {
        result.add((Element) child);
      }
    }
    return result;
  }
}
