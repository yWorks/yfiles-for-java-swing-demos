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

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class for BPMNPlane objects.
 */
class BpmnPlane {
  private BpmnElement element;

  /**
   * The {@link BpmnElement} this plane refers to.
   * @return The Element.
   * @see #setElement(BpmnElement)
   */
  public final BpmnElement getElement() {
    return this.element;
  }

  /**
   * The {@link BpmnElement} this plane refers to.
   * @param value The Element to set.
   * @see #getElement()
   */
  public final void setElement( BpmnElement value ) {
    this.element = value;
  }

  private List<BpmnEdge> listOfEdges;

  /**
   * List of all {@link BpmnEdge}s in this plane.
   * @return The ListOfEdges.
   * @see #setListOfEdges(List)
   */
  public final List<BpmnEdge> getListOfEdges() {
    return this.listOfEdges;
  }

  /**
   * List of all {@link BpmnEdge}s in this plane.
   * @param value The ListOfEdges to set.
   * @see #getListOfEdges()
   */
  public final void setListOfEdges( List<BpmnEdge> value ) {
    this.listOfEdges = value;
  }

  private List<BpmnShape> listOfShapes;

  /**
   * List of all {@link BpmnShape}s in this plane.
   * @return The ListOfShapes.
   * @see #setListOfShapes(List)
   */
  public final List<BpmnShape> getListOfShapes() {
    return this.listOfShapes;
  }

  /**
   * List of all {@link BpmnShape}s in this plane.
   * @param value The ListOfShapes to set.
   * @see #getListOfShapes()
   */
  public final void setListOfShapes( List<BpmnShape> value ) {
    this.listOfShapes = value;
  }

  /**
   * Constructs a new plane instance.
   * @param xNode The XML element which represents this plane
   * @param elements Dictionary of all bpmn elements from this file parsing
   */
  public BpmnPlane( Element xNode, Map<String, BpmnElement> elements ) {
    setListOfEdges(new ArrayList<>());
    setListOfShapes(new ArrayList<>());

    // A BPMNPlane only has one bpmnElement and no further attributes
    setElement(elements.get(BpmnNamespaceManager.getAttributeValue(xNode, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.BPMN_ELEMENT_ATTRIBUTE)));
  }

  /**
   * Adds a new {@link BpmnEdge} to this planes list of edges.
   * @param edge Edge to add
   */
  public final void addEdge( BpmnEdge edge ) {
    getListOfEdges().add(edge);
  }

  /**
   * Adds a new {@link BpmnShape} to this planes list of shapes.
   * @param shape Shape to add
   */
  public final void addShape( BpmnShape shape ) {
    getListOfShapes().add(shape);
  }

  /**
   * Returns the {@link BpmnShape} with the given id.
   * @param id Id
   * @return {@link BpmnShape} with the given id, or null if no {@link BpmnShape} with this id exists
   */
  public final BpmnShape getShape( String id ) {
    for (BpmnShape shape : getListOfShapes()) {
      if (id.equals(shape.getId())) {
        return shape;
      }
    }
    return null;
  }
}
