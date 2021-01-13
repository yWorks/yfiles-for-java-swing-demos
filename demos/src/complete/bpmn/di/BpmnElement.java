/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.ITable;
import complete.bpmn.view.LoopCharacteristic;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for Bpmn Element objects.
 */
class BpmnElement {
  private List<BpmnElement> children;

  /**
   * List of all children of this element.
   * @return The Children.
   * @see #setChildren(List)
   */
  public final List<BpmnElement> getChildren() {
    return this.children;
  }

  /**
   * List of all children of this element.
   * @param value The Children to set.
   * @see #getChildren()
   */
  public final void setChildren( List<BpmnElement> value ) {
    this.children = value;
  }

  private List<Node> foreignChildren;

  /**
   * List of all {@link Node} that were children of this element but have a different namespace then {@link BpmnNamespaceManager#NS_BPMN Bpmn}
   * or {@link BpmnNamespaceManager#NS_BPMN_DI BpmnDi}.
   * @return The ForeignChildren.
   * @see #setForeignChildren(List)
   */
  public final List<Node> getForeignChildren() {
    return this.foreignChildren;
  }

  /**
   * List of all {@link Node} that were children of this element but have a different namespace then {@link BpmnNamespaceManager#NS_BPMN Bpmn}
   * or {@link BpmnNamespaceManager#NS_BPMN_DI BpmnDi}.
   * @param value The ForeignChildren to set.
   * @see #getForeignChildren()
   */
  public final void setForeignChildren( List<Node> value ) {
    this.foreignChildren = value;
  }

  private String id;

  /**
   * Id of this element.
   * @return The Id.
   * @see #setId(String)
   */
  public final String getId() {
    return this.id;
  }

  /**
   * Id of this element.
   * @param value The Id to set.
   * @see #getId()
   */
  public final void setId( String value ) {
    this.id = value;
  }

  private Map<String, String> attributes;

  /**
   * List of all attributes, that do not have a Property.
   * @return The Attributes.
   * @see #setAttributes(Map)
   */
  public final Map<String, String> getAttributes() {
    return this.attributes;
  }

  /**
   * List of all attributes, that do not have a Property.
   * @param value The Attributes to set.
   * @see #getAttributes()
   */
  public final void setAttributes( Map<String, String> value ) {
    this.attributes = value;
  }

  private int topParticipants;

  /**
   * Number of TopParticipants, if this is a choreography Node.
   * @return The TopParticipants.
   * @see #setTopParticipants(int)
   */
  public final int getTopParticipants() {
    return this.topParticipants;
  }

  /**
   * Number of TopParticipants, if this is a choreography Node.
   * @param value The TopParticipants to set.
   * @see #getTopParticipants()
   */
  public final void setTopParticipants( int value ) {
    this.topParticipants = value;
  }

  private int bottomParticipants;

  /**
   * Number of BottomParticipants, if this is a choreography Node.
   * @return The BottomParticipants.
   * @see #setBottomParticipants(int)
   */
  public final int getBottomParticipants() {
    return this.bottomParticipants;
  }

  /**
   * Number of BottomParticipants, if this is a choreography Node.
   * @param value The BottomParticipants to set.
   * @see #getBottomParticipants()
   */
  public final void setBottomParticipants( int value ) {
    this.bottomParticipants = value;
  }

  private BpmnElement parent;

  /**
   * The parent BpmnElement.
   * @return The Parent.
   * @see #setParent(BpmnElement)
   */
  public final BpmnElement getParent() {
    return this.parent;
  }

  /**
   * The parent BpmnElement.
   * @param value The Parent to set.
   * @see #getParent()
   */
  public final void setParent( BpmnElement value ) {
    this.parent = value;
  }

  private INode node;

  /**
   * The corresponding INode, if this element is a BpmnShape.
   * @return The Node.
   * @see #setNode(INode)
   */
  public final INode getNode() {
    return this.node;
  }

  /**
   * The corresponding INode, if this element is a BpmnShape.
   * @param value The Node to set.
   * @see #getNode()
   */
  public final void setNode( INode value ) {
    this.node = value;
  }

  private ITable table;

  /**
   * The corresponding table, if this element is part of a pool.
   * @return The Table.
   * @see #setTable(ITable)
   */
  public final ITable getTable() {
    return this.table;
  }

  /**
   * The corresponding table, if this element is part of a pool.
   * @param value The Table to set.
   * @see #getTable()
   */
  public final void setTable( ITable value ) {
    this.table = value;
  }

  private String label;

  /**
   * The label text of this element.
   * @return The Label.
   * @see #setLabel(String)
   */
  public final String getLabel() {
    return this.label;
  }

  /**
   * The label text of this element.
   * @param value The Label to set.
   * @see #getLabel()
   */
  public final void setLabel( String value ) {
    this.label = value;
  }

  private String name;

  /**
   * The name of the element type.
   * @return The Name.
   * @see #setName(String)
   */
  public final String getName() {
    return this.name;
  }

  /**
   * The name of the element type.
   * @param value The Name to set.
   * @see #getName()
   */
  public final void setName( String value ) {
    this.name = value;
  }

  private String process;

  /**
   * The reference to a process, if this element is a subprocess.
   * @return The Process.
   * @see #setProcess(String)
   */
  public final String getProcess() {
    return this.process;
  }

  /**
   * The reference to a process, if this element is a subprocess.
   * @param value The Process to set.
   * @see #getProcess()
   */
  public final void setProcess( String value ) {
    this.process = value;
  }

  private String source;

  /**
   * The source element, if this element is an edge.
   * @return The Source.
   * @see #setSource(String)
   */
  public final String getSource() {
    return this.source;
  }

  /**
   * The source element, if this element is an edge.
   * @param value The Source to set.
   * @see #getSource()
   */
  public final void setSource( String value ) {
    this.source = value;
  }

  private String target;

  /**
   * The target element, if this element is an edge.
   * @return The Target.
   * @see #setTarget(String)
   */
  public final String getTarget() {
    return this.target;
  }

  /**
   * The target element, if this element is an edge.
   * @param value The Target to set.
   * @see #getTarget()
   */
  public final void setTarget( String value ) {
    this.target = value;
  }

  private String value;

  /**
   * The value (string text between XML tags) of this Element.
   * @return The Value.
   * @see #setValue(String)
   */
  public final String getValue() {
    return this.value;
  }

  /**
   * The value (string text between XML tags) of this Element.
   * @param value The Value to set.
   * @see #getValue()
   */
  public final void setValue( String value ) {
    this.value = value;
  }

  private String calledElement;

  /**
   * The element called by this element, if it is a calling element.
   * @return The CalledElement.
   */
  public final String getCalledElement() {
    return this.calledElement;
  }

  /**
   * The element called by this element, if it is a calling element.
   * @param value The CalledElement to set.
   * @see #getCalledElement()
   */
  private final void setCalledElement( String value ) {
    this.calledElement = value;
  }

  private IPort port;

  /**
   * The corresponding IPort if this element is a BoundaryEvent.
   * @return The Port.
   * @see #setPort(IPort)
   */
  public final IPort getPort() {
    return this.port;
  }

  /**
   * The corresponding IPort if this element is a BoundaryEvent.
   * @param value The Port to set.
   * @see #getPort()
   */
  public final void setPort( IPort value ) {
    this.port = value;
  }

  private IEdge edge;

  /**
   * The corresponding IEdge, if this element is an Edge.
   * @return The Edge.
   * @see #setEdge(IEdge)
   */
  public final IEdge getEdge() {
    return this.edge;
  }

  /**
   * The corresponding IEdge, if this element is an Edge.
   * @param value The Edge to set.
   * @see #getEdge()
   */
  public final void setEdge( IEdge value ) {
    this.edge = value;
  }

  /**
   * Class for BPMNElement objects.
   * @param xNode The XML Node to turn into a BpmnElement
   */
  public BpmnElement( Element xNode ) {
    setChildren(new ArrayList<>());
    setForeignChildren(new ArrayList<>());
    setAttributes(new HashMap<>());

    //Initialize blank Label
    setLabel("");

    // Parsing all Attributes
    for (Attr attribute : BpmnNamespaceManager.attributesInNamespace(xNode, BpmnNamespaceManager.NS_BPMN)) {
      String localName = attribute.getName();
      switch (localName) {
        case BpmnDiConstants.ID_ATTRIBUTE:
          setId(attribute.getValue());
          break;
        case BpmnDiConstants.NAME_ATTRIBUTE:
          setLabel(attribute.getValue());
          break;
        case BpmnDiConstants.SOURCE_REF_ATTRIBUTE:
          setSource(attribute.getValue());
          break;
        case BpmnDiConstants.TARGET_REF_ATTRIBUTE:
          setTarget(attribute.getValue());
          break;
        case BpmnDiConstants.PROCESS_REF_ATTRIBUTE:
          setProcess(attribute.getValue());
          break;
        case BpmnDiConstants.CALLED_ELEMENT_ATTRIBUTE:
        case BpmnDiConstants.CALLED_CHOREOGRAPHY_REF_ATTRIBUTE:
          setCalledElement(attribute.getValue());
          break;
        default:
          getAttributes().put(localName, attribute.getValue());
          break;
      }
    }

    setValue(xNode.getTextContent());
    setName(xNode.getLocalName());
    switch (getName()) {
      case BpmnDiConstants.GROUP_ELEMENT:
        setLabel(BpmnNamespaceManager.getAttributeValue(xNode, BpmnNamespaceManager.NS_BPMN, BpmnDiConstants.CATEGORY_VALUE_REF_ATTRIBUTE));
        break;
      case BpmnDiConstants.TEXT_ANNOTATION_ELEMENT:
        Element element = BpmnNamespaceManager.getElement(xNode, BpmnNamespaceManager.NS_BPMN, BpmnDiConstants.TEXT_ELEMENT);
        if (element != null) {
          setLabel(element.getTextContent());
        }
        break;
    }
  }

  /**
   * Adds a child to the current BpmnElement.
   * @param child The child to be added
   */
  public final void addChild( BpmnElement child ) {
    getChildren().add(child);
  }

  /**
   * Returns true, if a child with the given name exists.
   * @param name The name
   */
  public final boolean hasChild( String name ) {
    for (BpmnElement child : getChildren()) {
      if (name.equals(child.getName())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the Value of an Attribute of a given child.
   * @param name The name
   * @param attribute The Attribute
   */
  public final String getChildAttribute( String name, String attribute ) {
    String result = null;
    for (BpmnElement child : getChildren()) {
      if (name.equals(child.getName())) {
        result = child.getAttributes().get(attribute);
      }
    }
    return result;
  }

  /**
   * Returns the first child with the given name.
   * @param name The name
   */
  public final BpmnElement getChild( String name ) {
    for (BpmnElement child : getChildren()) {
      if (name.equals(child.getName())) {
        return child;
      }
    }
    return null;
  }

  /**
   * Returns all children with the given name.
   * @param name The name
   */
  public final Iterable<BpmnElement> getChildren( String name ) {
    List<BpmnElement> retChildren = new ArrayList<>();
    for (BpmnElement child : getChildren()) {
      if (name.equals(child.getName())) {
        retChildren.add(child);
      }
    }
    return retChildren;
  }

  /**
   * Retrieves the sourceRef string of the current element.
   * @return The sourceRef string
   */
  public final String loadSourceFromChild() {
    for (BpmnElement child : getChildren()) {
      if (BpmnDiConstants.SOURCE_REF_ELEMENT.equals(child.getName())) {
        return child.getValue();
      }
    }
    return "";
  }

  /**
   * Retrieves the targetRef string of the current element.
   * @return The targetRef string
   */
  public final String loadTargetFromChild() {
    for (BpmnElement child : getChildren()) {
      if (BpmnDiConstants.TARGET_REF_ELEMENT.equals(child.getName())) {
        return child.getValue();
      }
    }
    return "";
  }

  /**
   * Sets the INode of all dataInput and dataOutput hidden children to the given node.
   * @param node The node
   */
  public final void setINodeInputOutput( INode node ) {
    for (BpmnElement child : getChildren()) {
      String name = child.getName();
      if (BpmnDiConstants.IO_SPECIFICATION_ELEMENT.compareTo(name) == 0) {
        for (BpmnElement childChild : child.getChildren()) {
          String childName = childChild.getName();
          if (BpmnDiConstants.DATA_OUTPUT_ELEMENT.compareTo(childName) == 0 || BpmnDiConstants.DATA_INPUT_ELEMENT.compareTo(childName) == 0) {
            childChild.setNode(node);
          }
        }
      }
      if (BpmnDiConstants.DATA_INPUT_ELEMENT.compareTo(name) == 0) {
        child.setNode(node);
      }
      if (BpmnDiConstants.DATA_OUTPUT_ELEMENT.compareTo(name) == 0) {
        child.setNode(node);
      }
      if (BpmnDiConstants.PROPERTY_ELEMENT.compareTo(name) == 0) {
        child.setNode(node);
      }
    }
  }

  /**
   * Returns the Loop Characteristics of this Element.
   */
  public final LoopCharacteristic getLoopCharacteristics() {
    if (hasChild(BpmnDiConstants.MULTI_INSTANCE_LOOP_CHARACTERISTICS_ELEMENT)) {
      if ("true".equals(getChildAttribute(BpmnDiConstants.MULTI_INSTANCE_LOOP_CHARACTERISTICS_ELEMENT, BpmnDiConstants.IS_SEQUENTIAL_ATTRIBUTE))) {
        return LoopCharacteristic.SEQUENTIAL;
      }

      return LoopCharacteristic.PARALLEL;
    }

    if (hasChild(BpmnDiConstants.STANDARD_LOOP_CHARACTERISTICS_ELEMENT)) {
      return LoopCharacteristic.LOOP;
    }

    return LoopCharacteristic.NONE;
  }

  /**
   * Returns the value of the given attribute, or null.
   * @param attribute The attribute
   * @return The value, or null
   */
  public final String getValue( String attribute ) {
    return getAttributes().get(attribute);
  }

  /**
   * Returns the nearest Ancestor in a given List of Bpmn Elements, or null, if there is no ancestor,
   * which means there is something wrong.
   * @param planeElements A list of BpmnElements
   */
  public final BpmnDiagram getNearestAncestor( Map<BpmnElement, BpmnDiagram> planeElements ) {
    BpmnElement parent = getParent();
    while (parent != null) {
      if (planeElements.containsKey(parent)) {
        return planeElements.get(parent);
      }
      parent = parent.getParent();
    }
    return null;
  }
}
