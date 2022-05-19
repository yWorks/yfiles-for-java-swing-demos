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
package complete.bpmn.di;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class for BPMNEdge objects.
 */
class BpmnEdge {
  private static final SimpleLabel CALCULATE_SIZE_LABEL = new SimpleLabel(new SimpleNode(), "", FreeNodeLabelModel.INSTANCE.createDefaultParameter());

  private static final DefaultLabelStyle CALCULATE_SIZE_LABEL_STYLE = new DefaultLabelStyle();

  /**
   * Calculate the preferred size for {@code text} using a {@link DefaultLabelStyle}.
   * @param text The text to measure.
   * @return The preferred Size of the given text.
   */
  private static SizeD calculatePreferredSize( String text ) {
    CALCULATE_SIZE_LABEL.setText(text);
    return CALCULATE_SIZE_LABEL_STYLE.getRenderer().getPreferredSize(CALCULATE_SIZE_LABEL, CALCULATE_SIZE_LABEL_STYLE);
  }

  private BpmnElement element;

  /**
   * The {@link BpmnElement} this edge references to.
   * @return The Element.
   */
  public final BpmnElement getElement() {
    return this.element;
  }

  /**
   * The {@link BpmnElement} this edge references to.
   * @param value The Element to set.
   * @see #getElement()
   */
  private final void setElement( BpmnElement value ) {
    this.element = value;
  }

  private boolean hasLabel;

  /**
   * True, if this edge has a label.
   * @return The HasLabel.
   * @see #setHasLabel(boolean)
   */
  public final boolean isHasLabel() {
    return this.hasLabel;
  }

  /**
   * True, if this edge has a label.
   * @param value The HasLabel to set.
   * @see #isHasLabel()
   */
  public final void setHasLabel( boolean value ) {
    this.hasLabel = value;
  }

  private RectD labelBounds = new RectD();

  /**
   * The label bounds of this edge.
   * @return The LabelBounds.
   * @see #setLabelBounds(RectD)
   */
  public final RectD getLabelBounds() {
    return this.labelBounds;
  }

  /**
   * The label bounds of this edge.
   * @param value The LabelBounds to set.
   * @see #getLabelBounds()
   */
  public final void setLabelBounds( RectD value ) {
    this.labelBounds = value;
  }

  private MessageVisibleKind messageVisibleK = MessageVisibleKind.UNSPECIFIED;

  /**
   * Visibility of a message envelope on this edge.
   * @return The MessageVisibleK.
   */
  public final MessageVisibleKind getMessageVisibleK() {
    return this.messageVisibleK;
  }

  /**
   * Visibility of a message envelope on this edge.
   * @param value The MessageVisibleK to set.
   * @see #getMessageVisibleK()
   */
  private final void setMessageVisibleK( MessageVisibleKind value ) {
    this.messageVisibleK = value;
  }

  private BpmnElement source;

  /**
   * The source element of this edge.
   * @return The Source.
   */
  public final BpmnElement getSource() {
    return this.source;
  }

  /**
   * The source element of this edge.
   * @param value The Source to set.
   * @see #getSource()
   */
  private final void setSource( BpmnElement value ) {
    this.source = value;
  }

  private BpmnElement target;

  /**
   * The target element of this edge.
   * @return The Target.
   */
  public final BpmnElement getTarget() {
    return this.target;
  }

  /**
   * The target element of this edge.
   * @param value The Target to set.
   * @see #getTarget()
   */
  private final void setTarget( BpmnElement value ) {
    this.target = value;
  }

  private List<PointD> waypoints;

  /**
   * List of all waypoints (ports and bends).
   * @return The Waypoints.
   */
  public final List<PointD> getWaypoints() {
    return this.waypoints;
  }

  /**
   * List of all waypoints (ports and bends).
   * @param value The Waypoints to set.
   * @see #getWaypoints()
   */
  private final void setWaypoints( List<PointD> value ) {
    this.waypoints = value;
  }

  private String id;

  /**
   * The id of this edge.
   * @return The Id.
   */
  public final String getId() {
    return this.id;
  }

  /**
   * The id of this edge.
   * @param value The Id to set.
   * @see #getId()
   */
  private final void setId( String value ) {
    this.id = value;
  }

  private String labelStyle;

  /**
   * The custom style of this label.
   * @return The LabelStyle.
   * @see #setLabelStyle(String)
   */
  public final String getLabelStyle() {
    return this.labelStyle;
  }

  /**
   * The custom style of this label.
   * @param value The LabelStyle to set.
   * @see #getLabelStyle()
   */
  public final void setLabelStyle( String value ) {
    this.labelStyle = value;
  }

  /**
   * Constructs a new edge instance.
   * @param xEdge The XML element which represents this edge
   * @param elements Dictionary of all bpmn elements from this file parsing
   */
  public BpmnEdge( Element xEdge, Map<String, BpmnElement> elements ) {
    setWaypoints(new ArrayList<PointD>());
    setHasLabel(false);
    setLabelBounds(new RectD(0, 0, 0, 0));
    setSource(null);
    setTarget(null);
    setMessageVisibleK(MessageVisibleKind.UNSPECIFIED);

    // Get id
    setId(BpmnNamespaceManager.getAttributeValue(xEdge, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.ID_ATTRIBUTE));
    // Get and link element
    if (BpmnNamespaceManager.getAttributeValue(xEdge, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.BPMN_ELEMENT_ATTRIBUTE) != null) {
      setElement(elements.get(BpmnNamespaceManager.getAttributeValue(xEdge, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.BPMN_ELEMENT_ATTRIBUTE)));
    }

    // If there is no element, skip
    if (getElement() == null) {
      return;
    }

    // Source and target elements can be specified as attribute of the element
    // or as children of the element (in data associations).
    String sourceRef;
    String targetRef;

    NamedNodeMap attrs = xEdge.getAttributes();

    // Getting source element id
    String sourceVar = getElement().getSource();
    if (sourceVar != null) {
      sourceRef = sourceVar;
    } else {
      sourceRef = getElement().loadSourceFromChild();
    }

    // Getting and linking source element
    setSource(elements.get(sourceRef));



    // Getting target element id
    String targetVar = getElement().getTarget();
    if (targetVar != null) {
      targetRef = targetVar;
    } else {
      targetRef = getElement().loadTargetFromChild();
    }

    // Getting and linking target element
    setTarget(elements.get(targetRef));

    String kind = BpmnNamespaceManager.getAttributeValue(xEdge, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.MESSAGE_VISIBLE_KIND_ATTRIBUTE);
    if (kind == null) {
      kind = "";
    }
    switch (kind) {
      case "non_initiating":
        setMessageVisibleK(MessageVisibleKind.NON_INITIATING);
        break;
      case "initiating":
        setMessageVisibleK(MessageVisibleKind.INITIATING);
        break;
      default:
        setMessageVisibleK(MessageVisibleKind.UNSPECIFIED);
        break;
    }
  }

  /**
   * Add a label and its bounds to the edge.
   * @param xBounds The XML element of the label bounds
   */
  public final void addLabel( Element xBounds ) {
    setHasLabel(true);
    if (xBounds == null) return;

    // If there are bounds, set standard values, first.
    double labelX = 0;
    double labelY = 0;
    double labelWidth = 100;
    double labelHeight = 20;

    String attr = BpmnNamespaceManager.getAttributeValue(xBounds, BpmnNamespaceManager.NS_DC, BpmnDiConstants.X_ATTRIBUTE);
    if (attr != null) {
      labelX = Double.parseDouble(attr);
    }

    attr = BpmnNamespaceManager.getAttributeValue(xBounds, BpmnNamespaceManager.NS_DC, BpmnDiConstants.Y_ATTRIBUTE);
    if (attr != null) {
      labelY = Double.parseDouble(attr);
    }

    attr = BpmnNamespaceManager.getAttributeValue(xBounds, BpmnNamespaceManager.NS_DC, BpmnDiConstants.HEIGHT_ATTRIBUTE);
    if (attr != null) {
      labelHeight = Double.parseDouble(attr);
    }

    attr = BpmnNamespaceManager.getAttributeValue(xBounds, BpmnNamespaceManager.NS_DC, BpmnDiConstants.WIDTH_ATTRIBUTE);
    if (attr != null) {
      labelWidth = Double.parseDouble(attr);
    }

    // In case, the label sizes were set to 0
    if (labelWidth < 1 || labelHeight < 1) {
      String text = getElement().getLabel();
      SizeD preferredSize = calculatePreferredSize(text);

      labelWidth = preferredSize.width;
      labelHeight = preferredSize.height;
    }

    setLabelBounds(new RectD(labelX, labelY, labelWidth, labelHeight));
  }

  /**
   * Adds a waypoint to the edge.
   * @param xWaypoint The waypoint to add
   */
  public final void addWayPoint( Element xWaypoint ) {
    double x = 0;
    double y = 0;

    String attr = BpmnNamespaceManager.getAttributeValue(xWaypoint, BpmnNamespaceManager.NS_DI, BpmnDiConstants.X_ATTRIBUTE);
    if (attr != null) {
      x = Double.parseDouble(attr);
    }
    attr = BpmnNamespaceManager.getAttributeValue(xWaypoint, BpmnNamespaceManager.NS_DI, BpmnDiConstants.Y_ATTRIBUTE);
    if (attr != null) {
      y = Double.parseDouble(attr);
    }

    PointD tuple = new PointD(x, y);
    getWaypoints().add(tuple);
  }

  /**
   * Returns true, if the edge has width and height attributes set.
   * @return True, if the label has size, false if not
   */
  public final boolean hasLabelSize() {
    return getLabelBounds().width > 0 && getLabelBounds().height > 0;
  }

  /**
   * Returns true, if the top left point of the bounds is not 0/0 (standard case).
   * @return True, if the label has a given position, false if it is 0/0
   */
  public final boolean hasLabelPosition() {
    return getLabelBounds().x > 0 && getLabelBounds().y > 0;
  }

  /**
   * Returns the value of the given attribute of the linked BpmnElement, or null.
   * @param attribute Id (name) of the attribute to get
   * @return value of the attribute or null
   */
  public final String getAttribute( String attribute ) {
    return getElement().getValue(attribute);
  }

}
