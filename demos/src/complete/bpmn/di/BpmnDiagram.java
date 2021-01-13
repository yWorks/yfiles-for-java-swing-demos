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

import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for BPMNDiagram Objects.
 */
class BpmnDiagram {
  private String id;

  /**
   * Id of this diagram.
   * @return The Id.
   */
  public final String getId() {
    return this.id;
  }

  /**
   * Id of this diagram.
   * @param value The Id to set.
   * @see #getId()
   */
  private final void setId( String value ) {
    this.id = value;
  }

  private BpmnPlane plane;

  /**
   * BPMNPlane of this diagram.
   * @return The Plane.
   * @see #setPlane(BpmnPlane)
   */
  public final BpmnPlane getPlane() {
    return this.plane;
  }

  /**
   * BPMNPlane of this diagram.
   * @param value The Plane to set.
   * @see #getPlane()
   */
  public final void setPlane( BpmnPlane value ) {
    this.plane = value;
  }

  private Map<BpmnDiagram, BpmnElement> children;

  /**
   * List of all child diagrams this diagram contains.
   * @return The Children.
   */
  public final Map<BpmnDiagram, BpmnElement> getChildren() {
    return this.children;
  }

  /**
   * List of all child diagrams this diagram contains.
   * @param value The Children to set.
   * @see #getChildren()
   */
  private final void setChildren( Map<BpmnDiagram, BpmnElement> value ) {
    this.children = value;
  }

  private Map<String, BpmnLabelStyle> styles;

  /**
   * All BPMNLabelStyle instances of this diagram.
   * @return The Styles.
   */
  private Map<String, BpmnLabelStyle> getStyles() {
    return this.styles;
  }

  /**
   * All BPMNLabelStyle instances of this diagram.
   * @param value The Styles to set.
   */
  private void setStyles( Map<String, BpmnLabelStyle> value ) {
    this.styles = value;
  }

  private String name;

  /**
   * The name of this diagram.
   * @return The Name.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * The name of this diagram.
   * @param value The Name to set.
   * @see #getName()
   */
  private final void setName( String value ) {
    this.name = value;
  }

  // These parameters are currently unused. They are part of the BPMN Syntax and might be used in the future.
  private String documentation;

  private String getDocumentation() {
    return this.documentation;
  }

  private void setDocumentation( String value ) {
    this.documentation = value;
  }

  private String resolution;

  private String getResolution() {
    return this.resolution;
  }

  private void setResolution( String value ) {
    this.resolution = value;
  }

  /**
   * Constructs a new diagram instance.
   * @param xNode The XML node which is the root for this diagram instance
   */
  public BpmnDiagram( Element xNode ) {
    setPlane(null);
    setChildren(new HashMap<>());
    setStyles(new HashMap<>());
    setId("");
    setDocumentation("");
    setResolution("");

    // Name Diagram "Unnamed", if it has no name (for choosing, if file contains multiple diagrams)
    String name = BpmnNamespaceManager.getAttributeValue(xNode, BpmnNamespaceManager.NS_BPMN, BpmnDiConstants.NAME_ATTRIBUTE);
    setName(null == name || "".equals(name) ? "Unnamed Diagram" : name);

    // Get id, if it exists
    setId(BpmnNamespaceManager.getAttributeValue(xNode, BpmnNamespaceManager.NS_BPMN, BpmnDiConstants.ID_ATTRIBUTE));

    // Get documentation, if it exists
    setDocumentation(BpmnNamespaceManager.getAttributeValue(xNode, BpmnNamespaceManager.NS_BPMN, BpmnDiConstants.DOCUMENTATION_ATTRIBUTE));

    // Get resolution, if it exists
    setResolution(BpmnNamespaceManager.getAttributeValue(xNode, BpmnNamespaceManager.NS_BPMN, BpmnDiConstants.RESOLUTION_ATTRIBUTE));
  }

  private DefaultLabelStyle defaultStyle;

  /**
   * The default label style for this diagram instance.
   * @return The DefaultStyle.
   * @see #setDefaultStyle(DefaultLabelStyle)
   */
  public final DefaultLabelStyle getDefaultStyle() {
    return this.defaultStyle;
  }

  /**
   * The default label style for this diagram instance.
   * @param value The DefaultStyle to set.
   * @see #getDefaultStyle()
   */
  public final void setDefaultStyle( DefaultLabelStyle value ) {
    this.defaultStyle = value;
  }

  /**
   * Adds a plane to this diagram. Only happens once, but is caught elsewhere
   */
  public final void addPlane( BpmnPlane plane ) {
    setPlane(plane);
  }

  /**
   * Adds a LabelStyle to the collection of styles in this diagram.
   * @param style The {@link BpmnLabelStyle} to add
   */
  public final void addStyle( BpmnLabelStyle style ) {
    getStyles().put(style.getId(), style);
  }

  /**
   * Returns the given Style, or the default style, in case it does nor exist.
   * @param style The id (name) of the style to get
   */
  public final DefaultLabelStyle getStyle( String style ) {
    if (style == null) {
      return getDefaultStyle().clone();
    }
    BpmnLabelStyle labelStyle = getStyles().get(style);
    if (labelStyle != null) {
      return labelStyle.getStyle().clone();
    }
    return getDefaultStyle().clone();
  }

  /**
   * Adds a child diagram to this diagram.
   * @param diagram The child diagram
   * @param localRoot The local root element
   */
  public final void addChild( BpmnDiagram diagram, BpmnElement localRoot ) {
    getChildren().put(diagram, localRoot);
  }

  /**
   * @return The name of the Diagram
   */
  @Override
  public String toString() {
    return getName();
  }

}
