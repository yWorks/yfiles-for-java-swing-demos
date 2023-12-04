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

import com.yworks.yfiles.geometry.RectD;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Class for BPMNShape objects.
 */
class BpmnShape {
  private String choreographyActivityShape;

  /**
   * The string id of the choreographyActivityShape if this shape is depicting a participant band.
   * @return The ChoreographyActivityShape.
   * @see #setChoreographyActivityShape(String)
   */
  public final String getChoreographyActivityShape() {
    return this.choreographyActivityShape;
  }

  /**
   * The string id of the choreographyActivityShape if this shape is depicting a participant band.
   * @param value The ChoreographyActivityShape to set.
   * @see #getChoreographyActivityShape()
   */
  public final void setChoreographyActivityShape( String value ) {
    this.choreographyActivityShape = value;
  }

  private BpmnElement element;

  /**
   * The {@link BpmnElement} this shape refers to.
   * @return The Element.
   * @see #setElement(BpmnElement)
   */
  public final BpmnElement getElement() {
    return this.element;
  }

  /**
   * The {@link BpmnElement} this shape refers to.
   * @param value The Element to set.
   * @see #getElement()
   */
  public final void setElement( BpmnElement value ) {
    this.element = value;
  }

  private String isExpanded;

  /**
   * String id of the expansion state of this shape.
   * @return The IsExpanded.
   * @see #setIsExpanded(String)
   */
  public final String getIsExpanded() {
    return this.isExpanded;
  }

  /**
   * String id of the expansion state of this shape.
   * @param value The IsExpanded to set.
   * @see #getIsExpanded()
   */
  public final void setIsExpanded( String value ) {
    this.isExpanded = value;
  }

  private boolean horizontal;

  /**
   * Indicates the orientation if this is a pool or lane.
   * @return The orientation.
   * @see #setHorizontal(boolean)
   */
  public final boolean isHorizontal() {
    return this.horizontal;
  }

  /**
   * Indicates the orientation if this is a pool or lane.
   * @param value The orientation to set.
   * @see #isHorizontal()
   */
  public final void setHorizontal( boolean value ) {
    this.horizontal = value;
  }

  private boolean markerVisible;

  /**
   * Whether a marker should be depicted on the shape for exclusive Gateways.
   * @return The MarkerVisible.
   * @see #setMarkerVisible(boolean)
   */
  public final boolean isMarkerVisible() {
    return this.markerVisible;
  }

  /**
   * Whether a marker should be depicted on the shape for exclusive Gateways.
   * @param value The MarkerVisible to set.
   * @see #isMarkerVisible()
   */
  public final void setMarkerVisible( boolean value ) {
    this.markerVisible = value;
  }

  private boolean messageVisible;

  /**
   * Whether a message envelope should be depicted connected to the shape for participant bands.
   * @return The MessageVisible.
   * @see #setMessageVisible(boolean)
   */
  public final boolean isMessageVisible() {
    return this.messageVisible;
  }

  /**
   * Whether a message envelope should be depicted connected to the shape for participant bands.
   * @param value The MessageVisible to set.
   * @see #isMessageVisible()
   */
  public final void setMessageVisible( boolean value ) {
    this.messageVisible = value;
  }

  private boolean hasLabel;

  /**
   * Whether this shape has a label.
   * @return The HasLabel.
   * @see #setHasLabel(boolean)
   */
  public final boolean isHasLabel() {
    return this.hasLabel;
  }

  /**
   * Whether this shape has a label.
   * @param value The HasLabel to set.
   * @see #isHasLabel()
   */
  public final void setHasLabel( boolean value ) {
    this.hasLabel = value;
  }

  private RectD labelBounds = new RectD();

  /**
   * Bounds for the shapes label, if it has one.
   * @return The LabelBounds.
   * @see #setLabelBounds(RectD)
   */
  public final RectD getLabelBounds() {
    return this.labelBounds;
  }

  /**
   * Bounds for the shapes label, if it has one.
   * @param value The LabelBounds to set.
   * @see #getLabelBounds()
   */
  public final void setLabelBounds( RectD value ) {
    this.labelBounds = value;
  }

  private ParticipantBandKind partBandKind = ParticipantBandKind.BOTTOM_INITIATING;

  /**
   * Determines the kind of the participant band, if this participant should be depicted as participant band instead of
   * being depicted as lane.
   * @return The PartBandKind.
   * @see #setPartBandKind(ParticipantBandKind)
   */
  public final ParticipantBandKind getPartBandKind() {
    return this.partBandKind;
  }

  /**
   * Determines the kind of the participant band, if this participant should be depicted as participant band instead of
   * being depicted as lane.
   * @param value The PartBandKind to set.
   * @see #getPartBandKind()
   */
  public final void setPartBandKind( ParticipantBandKind value ) {
    this.partBandKind = value;
  }

  private double height;

  /**
   * Height of the shape.
   * @return The Height.
   * @see #setHeight(double)
   */
  public final double getHeight() {
    return this.height;
  }

  /**
   * Height of the shape.
   * @param value The Height to set.
   * @see #getHeight()
   */
  public final void setHeight( double value ) {
    this.height = value;
  }

  private double width;

  /**
   * Width of the shape.
   * @return The Width.
   * @see #setWidth(double)
   */
  public final double getWidth() {
    return this.width;
  }

  /**
   * Width of the shape.
   * @param value The Width to set.
   * @see #getWidth()
   */
  public final void setWidth( double value ) {
    this.width = value;
  }

  private double x;

  /**
   * X position of the upper left corner of this shape.
   * @return The X.
   * @see #setX(double)
   */
  public final double getX() {
    return this.x;
  }

  /**
   * X position of the upper left corner of this shape.
   * @param value The X to set.
   * @see #getX()
   */
  public final void setX( double value ) {
    this.x = value;
  }

  private double y;

  /**
   * Y position of the upper left corner of this shape.
   * @return The Y.
   * @see #setY(double)
   */
  public final double getY() {
    return this.y;
  }

  /**
   * Y position of the upper left corner of this shape.
   * @param value The Y to set.
   * @see #getY()
   */
  public final void setY( double value ) {
    this.y = value;
  }

  private String id;

  /**
   * Id of this shape.
   * @return The Id.
   * @see #setId(String)
   */
  public final String getId() {
    return this.id;
  }

  /**
   * Id of this shape.
   * @param value The Id to set.
   * @see #getId()
   */
  public final void setId( String value ) {
    this.id = value;
  }

  private String labelStyle;

  /**
   * Custom {@link complete.bpmn.di.BpmnLabelStyle} for the label of this shape.
   * @return The LabelStyle.
   * @see #setLabelStyle(String)
   */
  public final String getLabelStyle() {
    return this.labelStyle;
  }

  /**
   * Custom {@link complete.bpmn.di.BpmnLabelStyle} for the label of this shape.
   * @param value The LabelStyle to set.
   * @see #getLabelStyle()
   */
  public final void setLabelStyle( String value ) {
    this.labelStyle = value;
  }

  /**
   * Constructs a new shape instance.
   * @param xShape The XML element which represents this shape
   * @param elements Dictionary of all bpmn elements from this file parsing
   */
  public BpmnShape( Element xShape, Map<String, BpmnElement> elements ) {
    setHasLabel(false);
    setLabelBounds(new RectD(0, 0, 0, 0));
    setX(0);
    setY(0);
    setHeight(30);
    setWidth(30);

    // Get and Link the corresponding element
    setElement(elements.get(BpmnNamespaceManager.getAttributeValue(xShape, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.BPMN_ELEMENT_ATTRIBUTE)));

    // If there is no element, skip
    if (getElement() == null) {
      return;
    }

    // Get the id
    setId(BpmnNamespaceManager.getAttributeValue(xShape, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.ID_ATTRIBUTE));

    // Get all additional Attributes
    setHorizontal(Boolean.parseBoolean(BpmnNamespaceManager.getAttributeValue(xShape, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.IS_HORIZONTAL_ATTRIBUTE)));
    setIsExpanded(BpmnNamespaceManager.getAttributeValue(xShape, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.IS_EXPANDED_ATTRIBUTE));
    setMarkerVisible(Boolean.parseBoolean(BpmnNamespaceManager.getAttributeValue(xShape, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.IS_MARKER_VISIBLE_ATTRIBUTE)));
    setMessageVisible(Boolean.parseBoolean(BpmnNamespaceManager.getAttributeValue(xShape, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.IS_MESSAGE_VISIBLE_ATTRIBUTE)));
    setChoreographyActivityShape(BpmnNamespaceManager.getAttributeValue(xShape, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.CHOREOGRAPHY_ACTIVITY_SHAPE_ATTRIBUTE));

    String kind = BpmnNamespaceManager.getAttributeValue(xShape, BpmnNamespaceManager.NS_BPMN_DI, BpmnDiConstants.PARTICIPANT_BAND_KIND_ATTRIBUTE);
    if (kind == null) {
      kind = "";
    }
    switch (kind) {
      case "top_non_initiating":
        setPartBandKind(ParticipantBandKind.TOP_NON_INITIATING);
        break;
      case "top_initiating":
        setPartBandKind(ParticipantBandKind.TOP_INITIATING);
        break;
      case "middle_non_initiating":
        setPartBandKind(ParticipantBandKind.MIDDLE_NON_INITIATING);
        break;
      case "middle_initiating":
        setPartBandKind(ParticipantBandKind.MIDDLE_INITIATING);
        break;
      case "bottom_non_initiating":
        setPartBandKind(ParticipantBandKind.BOTTOM_NON_INITIATING);
        break;
      case "bottom_initiating":
        setPartBandKind(ParticipantBandKind.BOTTOM_INITIATING);
        break;
    }
  }

  /**
   * Adds the bound of this shape.
   * @param xBounds XML element representing the bounds
   */
  public final void addBounds( Element xBounds ) {

    String attr = BpmnNamespaceManager.getAttributeValue(xBounds, BpmnNamespaceManager.NS_DC, BpmnDiConstants.X_ATTRIBUTE);
    if (attr != null) {
      setX(Double.parseDouble(attr));
    }

    attr = BpmnNamespaceManager.getAttributeValue(xBounds, BpmnNamespaceManager.NS_DC, BpmnDiConstants.Y_ATTRIBUTE);
    if (attr != null) {
      setY(Double.parseDouble(attr));
    }

    attr = BpmnNamespaceManager.getAttributeValue(xBounds, BpmnNamespaceManager.NS_DC, BpmnDiConstants.HEIGHT_ATTRIBUTE);
    if (attr != null) {
      setHeight(Double.parseDouble(attr));
    }

    attr = BpmnNamespaceManager.getAttributeValue(xBounds, BpmnNamespaceManager.NS_DC, BpmnDiConstants.WIDTH_ATTRIBUTE);
    if (attr != null) {
      setWidth(Double.parseDouble(attr));
    }

    //Check for size 0
    if (getHeight() == 0) {
      setHeight(30);
    }

    if (getWidth() == 0) {
      setWidth(30);
    }
  }

  /**
   * Adds the label bounds for this shapes label.
   * @param xBounds The XML element representing this labels bounds
   */
  public final void addLabel( Element xBounds ) {
    setHasLabel(true);
    if (xBounds == null) return;

    // If there are bounds, set standard values, first.
    double labelX = 0;
    double labelY = 0;
    double labelWidth = 0;
    double labelHeight = 0;

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
    if (labelWidth < 1) {
      labelWidth = 100;
    }
    if (labelHeight < 1) {
      labelHeight = 20;
    }

    setLabelBounds(new RectD(labelX, labelY, labelWidth, labelHeight));
  }

  /**
   * Returns the value of the given attribute of the linked {@link BpmnElement}, or null.
   * @param attribute The attribute to receive
   */
  public final String getAttribute( String attribute ) {
    return getElement().getValue(attribute);
  }

  /**
   * Whether the shape has width and height attributes set.
   * @return True, if the label has size, false if not
   */
  public final boolean hasLabelSize() {
    return getLabelBounds().width > 0 && getLabelBounds().height > 0;
  }

  /**
   * Whether the top left point of the bounds is not 0/0 (standard case).
   * @return True, if the label position is 0/0
   */
  public final boolean hasLabelPosition() {
    return getLabelBounds().x > 0 && getLabelBounds().y > 0;
  }
}
