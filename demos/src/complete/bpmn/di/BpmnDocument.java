/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.6.
 **
 ** Copyright (c) 2000-2023 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class holding the information of a BPMN {@link Document}.
 */
class BpmnDocument {
  private Map<String, BpmnElement> elements;

  /**
   * Mapping of all IDs of BPMN elements to these elements.
   * @return The Elements.
   */
  final Map<String, BpmnElement> getElements() {
    return this.elements;
  }

  /**
   * Mapping of all IDs of BPMN elements to these elements.
   * @param value The Elements to set.
   */
  final void setElements( Map<String, BpmnElement> value ) {
    this.elements = value;
  }

  private List<BpmnDiagram> diagrams;

  /**
   * List of all diagrams, that are parsed from this document.
   * @return The Diagrams.
   */
  final List<BpmnDiagram> getDiagrams() {
    return this.diagrams;
  }

  /**
   * List of all diagrams, that are parsed from this document.
   * @param value The Diagrams to set.
   */
  final void setDiagrams( List<BpmnDiagram> value ) {
    this.diagrams = value;
  }

  private List<BpmnDiagram> topLevelDiagrams;

  /**
   * List of diagrams representing a "process", "choreography" or "collaboration".
   * @return The TopLevelDiagrams.
   */
  final List<BpmnDiagram> getTopLevelDiagrams() {
    return this.topLevelDiagrams;
  }

  /**
   * List of diagrams representing a "process", "choreography" or "collaboration".
   * @param value The TopLevelDiagrams to set.
   */
  final void setTopLevelDiagrams( List<BpmnDiagram> value ) {
    this.topLevelDiagrams = value;
  }

  private Map<BpmnElement, BpmnDiagram> elementToDiagram;

  /**
   * Mapping from a BPMN element to the diagram representing it.
   * @return The ElementToDiagram.
   */
  final Map<BpmnElement, BpmnDiagram> getElementToDiagram() {
    return this.elementToDiagram;
  }

  /**
   * Mapping from a BPMN element to the diagram representing it.
   * @param value The ElementToDiagram to set.
   */
  private final void setElementToDiagram( Map<BpmnElement, BpmnDiagram> value ) {
    this.elementToDiagram = value;
  }

  private List<String> messages;

  /**
   * Collection of all warnings during program execution.
   * @return The Messages.
   */
  final List<String> getMessages() {
    return this.messages;
  }

  /**
   * Collection of all warning during program execution.
   * @param value The Messages to set.
   */
  final void setMessages( List<String> value ) {
    this.messages = value;
  }

  /**
   * Creates a new instance for the BPMN {@link Document}.
   * @param doc The BPMN document to parse.
   */
  public BpmnDocument( Document doc ) {
    setElements(new HashMap<>());
    setDiagrams(new ArrayList<>());
    setTopLevelDiagrams(new ArrayList<>());
    setElementToDiagram(new HashMap<>());
    setMessages(new ArrayList<>());

    // parse the XML file
    ArrayList<BpmnElement> callingElements = new ArrayList<>();
    recursiveElements(doc.getDocumentElement(), null, callingElements);

    // collect all elements that are linked to from a plane
    for (BpmnDiagram diagram : getDiagrams()) {
      try {
        BpmnElement planeElement = diagram.getPlane().getElement();
        getElementToDiagram().put(planeElement, diagram);
      }catch (IllegalArgumentException e) {
        getMessages().add("Tried to add a diagram with the already existing id: " + diagram.getId());
      }
    }

    // collect all diagrams where the plane corresponds to a Top Level BpmnElement (Process/Choreography/Collaboration)
    for (BpmnDiagram diagram : getDiagrams()) {
      BpmnDiagram parent = null;
      BpmnElement element = diagram.getPlane().getElement();
      String elementId = element.getId();
      String elementName = element.getName();
      if (BpmnDiConstants.PROCESS_ELEMENT.compareTo(elementName) == 0 || BpmnDiConstants.CHOREOGRAPHY_ELEMENT.compareTo(elementName) == 0 || BpmnDiConstants.COLLABORATION_ELEMENT.compareTo(elementName) == 0) {
        getTopLevelDiagrams().add(diagram);
        for (BpmnElement callingElement : callingElements) {
          if (elementId == null
              ? null == callingElement.getCalledElement()
              : elementId.equals(callingElement.getCalledElement())) {
            parent = callingElement.getNearestAncestor(getElementToDiagram());
            if (parent != null) {
              parent.addChild(diagram, callingElement);
            }
          }
        }
        for (BpmnElement child : diagram.getPlane().getElement().getChildren()) {
          collectChildDiagrams(child, diagram);
        }
      }
    }
  }

  /**
   * Collects all {@link BpmnDiagram} where the plane corresponds to a {@link BpmnElement} in {@code diagram}.
   * @param bpmnElement The element to check.
   * @param diagram The diagram to collect the child diagrams for.
   */
  private void collectChildDiagrams( BpmnElement bpmnElement, BpmnDiagram diagram ) {
    BpmnDiagram currentDiagram = diagram;

    if (getElementToDiagram().containsKey(bpmnElement)) {
      BpmnDiagram childDiagram = getElementToDiagram().get(bpmnElement);
      diagram.addChild(childDiagram, bpmnElement);
      currentDiagram = childDiagram;
    }
    for (BpmnElement child : bpmnElement.getChildren()) {
      collectChildDiagrams(child, currentDiagram);
    }
    if (bpmnElement.getProcess() != null && getElements().containsKey(bpmnElement.getProcess())) {
      BpmnElement process = getElements().get(bpmnElement.getProcess());
      collectChildDiagrams(process, currentDiagram);
    }
  }

  /**
   * Traverses depth-first through the bpmn XML file, collecting and linking all elements.
   * @param xNode The XML node to start with
   * @param parent The parent {@link BpmnElement}
   * @param callingElements A list to add all {@link BpmnElement} with a valid 'CalledElement' or 'Process' property.
   */
  private void recursiveElements( Element xNode, BpmnElement parent, Collection<BpmnElement> callingElements ) {
    BpmnElement element = new BpmnElement(xNode);
    if (element.getCalledElement() != null) {
      callingElements.add(element);
    } else if (element.getProcess() != null) {
      callingElements.add(element);
    }

    // Only xml nodes with an id can be bpmn elements
    if (element.getId() != null) {
      try {
        getElements().put(element.getId(), element);
        // some tools prefix the ids with a namespace prefix but don't use this namespace to reference it
        // so if the id contains a prefix, it is mapped with and without it
        String prefix = xNode.getPrefix();
        if (prefix != null) {
          getElements().put(prefix + ":" + element.getId(), element);
        }
      }catch (IllegalArgumentException e) {
        getMessages().add("Error while trying to add second Element with the same id: " + element.getId());
      }
    }

    // Double-link bpmn element to the given parent element
    if (parent != null) {
      parent.addChild(element);
    }
    element.setParent(parent);

    // Call all xml children
    for (Element xChild : BpmnNamespaceManager.children(xNode)) {
      String nameSpace = xChild.getNamespaceURI();
      String localName = BpmnNamespaceManager.getLocalName(xChild);
      if (nameSpace.equals(BpmnNamespaceManager.NS_BPMN)) {

        // Add all bpmn elements to the dictionary
        recursiveElements(xChild, element, callingElements);
      } else if (nameSpace.equals(BpmnNamespaceManager.NS_BPMN_DI)) {
        // Parse a diagram as whole
        if (BpmnDiConstants.BPMN_DIAGRAM_ELEMENT.compareTo(localName) == 0) {
          BpmnDiagram diagram = buildDiagram(xChild);
          if (diagram.getPlane() != null) {
            getDiagrams().add(diagram);
          } else {
            getMessages().add("The plane for diagram + " + diagram.getId() + " was not correctly parsed.");
          }
        }
      } else {
        element.getForeignChildren().add(xChild);
      }
    }
  }

  /**
   * Creates a {@link BpmnDiagram}.
   * @param xNode The XML node to start with
   * @return The parsed {@link BpmnDiagram}
   */
  private BpmnDiagram buildDiagram( Element xNode ) {
    BpmnDiagram diagram = new BpmnDiagram(xNode);

    BpmnPlane bpmnPlane = buildPlane(BpmnNamespaceManager.getElement(xNode, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.BPMN_PLANE_ELEMENT));
    if (bpmnPlane != null) {
      diagram.addPlane(bpmnPlane);
    }

    for (Element xChild : BpmnNamespaceManager.getElements(xNode, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.BPMN_LABEL_STYLE_ELEMENT)) {
      BpmnLabelStyle style = new BpmnLabelStyle(xChild);
      diagram.addStyle(style);
    }

    // Setting a default LabelStyle for all labels that do not have their own style.
    diagram.setDefaultStyle(BpmnLabelStyle.newDefaultInstance());

    return diagram;
  }

  /**
   * Parses all bpmn shapes and bpmn edges and their associations and attributes from one {@link BpmnPlane}.
   * @param xNode The XML node to start with
   */
  private BpmnPlane buildPlane( Element xNode ) {
    BpmnPlane plane = new BpmnPlane(xNode, getElements());
    if (plane.getElement() == null) {
      return null;
    }

    // All Shapes
    for (Element xChild : BpmnNamespaceManager.getElements(xNode, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.BPMN_SHAPE_ELEMENT)) {
      BpmnShape shape = new BpmnShape(xChild, getElements());
      if (shape.getElement() != null) {
        plane.addShape(shape);
      } else {
        getMessages().add("Error in parsing shape " + (shape.getId()) + ", could not find corresponding BPMNElement.");
        continue;
      }

      // Shapes usually define their bounds
      shape.addBounds(BpmnNamespaceManager.getElement(xChild, BpmnNamespaceManager.NS_DC, BpmnDiConstants.BOUNDS_ELEMENT));

      // Shapes can have a BPMNLabel as child
      Element bpmnLabel = BpmnNamespaceManager.getElement(xChild, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.BPMN_LABEL_ELEMENT);
      if (bpmnLabel != null) {
        // Label bounds
        Element bounds = BpmnNamespaceManager.getElement(bpmnLabel, BpmnNamespaceManager.NS_DC, BpmnDiConstants.BOUNDS_ELEMENT);
        shape.addLabel(bounds);
        // BpmnLabelStyle
        shape.setLabelStyle(BpmnNamespaceManager.getAttributeValue(bpmnLabel, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.LABEL_STYLE_ATTRIBUTE));
      }
    }

    for (Element xChild : BpmnNamespaceManager.getElements(xNode, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.BPMN_EDGE_ELEMENT)) {
      BpmnEdge edge = new BpmnEdge(xChild, getElements());
      if (edge.getElement() != null) {
        plane.addEdge(edge);
      } else {
        getMessages().add("Error in parsing edge " + (edge.getId()) + ", could not find corresponding BPMNElement.");
        continue;
      }

      // Edges define 2 or more Waypoints
      for (Element waypoint : BpmnNamespaceManager.getElements(xChild, BpmnNamespaceManager.NS_DI, BpmnDiConstants.WAYPOINT_ELEMENT)) {
        edge.addWayPoint(waypoint);
      }

      // Edges can have a BPMNLabel as child
      Element bpmnLabel = BpmnNamespaceManager.getElement(xChild, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.BPMN_LABEL_ELEMENT);
      if (bpmnLabel != null) {
        // Label bounds
        Element bounds = BpmnNamespaceManager.getElement(bpmnLabel, BpmnNamespaceManager.NS_DC, BpmnDiConstants.BOUNDS_ELEMENT);
        edge.addLabel(bounds);
        // BpmnLabelStyle
        edge.setLabelStyle(BpmnNamespaceManager.getAttributeValue(bpmnLabel, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.LABEL_STYLE_ATTRIBUTE));
      }
    }
    return plane;
  }

}
