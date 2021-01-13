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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.FreeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.SimpleEdge;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.SimplePort;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VisualGroup;

/**
 * An {@link ILabelStyle} implementation combining an text label, an icon and a connecting line between the icon and the
 * label owner.
 */
class ConnectedIconLabelStyle extends AbstractLabelStyle {

  private ILabelModelParameter textPlacement;

  public final ILabelModelParameter getTextPlacement() {
    return this.textPlacement;
  }

  public final void setTextPlacement( ILabelModelParameter value ) {
    this.textPlacement = value;
  }

  private IPortLocationModelParameter labelConnectorLocation;

  public final IPortLocationModelParameter getLabelConnectorLocation() {
    return this.labelConnectorLocation;
  }

  public final void setLabelConnectorLocation( IPortLocationModelParameter value ) {
    this.labelConnectorLocation = value;
  }

  private IPortLocationModelParameter nodeConnectorLocation;

  public final IPortLocationModelParameter getNodeConnectorLocation() {
    return this.nodeConnectorLocation;
  }

  public final void setNodeConnectorLocation( IPortLocationModelParameter value ) {
    this.nodeConnectorLocation = value;
  }

  private SizeD iconSize = new SizeD();

  public final SizeD getIconSize() {
    return this.iconSize;
  }

  public final void setIconSize( SizeD value ) {
    this.iconSize = value;
  }

  private INodeStyle iconStyle;

  public final INodeStyle getIconStyle() {
    return this.iconStyle;
  }

  public final void setIconStyle( INodeStyle value ) {
    this.iconStyle = value;
  }

  private ILabelStyle textStyle;

  public final ILabelStyle getTextStyle() {
    return this.textStyle;
  }

  public final void setTextStyle( ILabelStyle value ) {
    this.textStyle = value;
  }

  private IEdgeStyle connectorStyle;

  public final IEdgeStyle getConnectorStyle() {
    return this.connectorStyle;
  }

  public final void setConnectorStyle( IEdgeStyle value ) {
    this.connectorStyle = value;
  }



  private static final SimpleNode LABEL_AS_NODE;

  private static final SimpleLabel DUMMY_TEXT_LABEL;

  private static final SimpleEdge DUMMY_EDGE;

  private static final SimpleNode DUMMY_FOR_LABEL_OWNER;


  @Override
  protected IVisual createVisual( IRenderContext context, ILabel label ) {

    configure(label);
    VisualGroup container = new VisualGroup();

    IVisual iconVisual = null;
    if (getIconStyle() != null) {
      iconVisual = getIconStyle().getRenderer().getVisualCreator(LABEL_AS_NODE, LABEL_AS_NODE.getStyle()).createVisual(context);
    }
    container.add(iconVisual != null ? iconVisual : new VisualGroup());

    IVisual textVisual = null;
    if (getTextStyle() != null && getTextPlacement() != null) {
      textVisual = getTextStyle().getRenderer().getVisualCreator(DUMMY_TEXT_LABEL, DUMMY_TEXT_LABEL.getStyle()).createVisual(context);
    }
    container.add(textVisual != null ? textVisual : new VisualGroup());

    IVisual connectorVisual = null;
    if (getConnectorStyle() != null) {
      connectorVisual = DUMMY_EDGE.getStyle().getRenderer().getVisualCreator(DUMMY_EDGE, DUMMY_EDGE.getStyle()).createVisual(context);
    }
    container.add(connectorVisual != null ? connectorVisual : new VisualGroup());

    return container;
  }

  @Override
  protected SizeD getPreferredSize( ILabel label ) {
    if (SizeD.notEquals(getIconSize(), SizeD.ZERO)) {
      return getIconSize();
    } else {
      return label.getPreferredSize();
    }
  }

  @Override
  protected IVisual updateVisual( IRenderContext context, IVisual oldVisual, ILabel label ) {

    configure(label);

    VisualGroup container = (oldVisual instanceof VisualGroup) ? (VisualGroup)oldVisual : null;
    IVisual oldIconVisual = container.getChildren().get(0);
    IVisual newIconVisual = null;
    if (getIconStyle() != null) {
      newIconVisual = getIconStyle().getRenderer().getVisualCreator(LABEL_AS_NODE, LABEL_AS_NODE.getStyle()).updateVisual(context, oldIconVisual);
    }
    if (oldIconVisual != newIconVisual) {
      container.getChildren().set(0, newIconVisual != null ? newIconVisual : new VisualGroup());
    }

    IVisual oldTextVisual = container.getChildren().get(1);
    IVisual newTextVisual = null;
    if (getTextStyle() != null && getTextPlacement() != null) {
      newTextVisual = getTextStyle().getRenderer().getVisualCreator(DUMMY_TEXT_LABEL, DUMMY_TEXT_LABEL.getStyle()).updateVisual(context, oldTextVisual);
    }
    if (oldTextVisual != newTextVisual) {
      container.getChildren().set(1, newTextVisual != null ? newTextVisual : new VisualGroup());
    }

    IVisual oldConnectorVisual = container.getChildren().get(2);
    IVisual newConnectorVisual = null;
    if (getConnectorStyle() != null) {
      newConnectorVisual = DUMMY_EDGE.getStyle().getRenderer().getVisualCreator(DUMMY_EDGE, DUMMY_EDGE.getStyle()).updateVisual(context, oldConnectorVisual);
    }
    if (oldConnectorVisual != newConnectorVisual) {
      container.getChildren().set(2, newConnectorVisual != null ? newConnectorVisual : new VisualGroup());
    }

    return container;
  }

  protected final void configure( ILabel item ) {
    LABEL_AS_NODE.setStyle(getIconStyle());
    RectD bounds = item.getLayout().getBounds();
    LABEL_AS_NODE.setLayout(bounds);
    ILabelOwner owner = item.getOwner();
    if (owner instanceof INode) {
      INode nodeOwner = (INode)owner;
      DUMMY_FOR_LABEL_OWNER.setStyle(nodeOwner.getStyle());
      DUMMY_FOR_LABEL_OWNER.setLayout(nodeOwner.getLayout());
    }

    DUMMY_TEXT_LABEL.setStyle(getTextStyle());
    DUMMY_TEXT_LABEL.setLayoutParameter(getTextPlacement());
    DUMMY_TEXT_LABEL.setText(item.getText());
    DUMMY_TEXT_LABEL.setPreferredSize(DUMMY_TEXT_LABEL.getStyle().getRenderer().getPreferredSize(DUMMY_TEXT_LABEL, DUMMY_TEXT_LABEL.getStyle()));
    textBounds = getTextPlacement().getModel().getGeometry(DUMMY_TEXT_LABEL, getTextPlacement());

    boundingBox = RectD.add(bounds, textBounds.getBounds());

    // Set source port to the port of the node using a dummy node that is located at the origin.
    ((SimplePort)DUMMY_EDGE.getSourcePort()).setLocationParameter(getLabelConnectorLocation());
    ((SimplePort)DUMMY_EDGE.getTargetPort()).setLocationParameter(getNodeConnectorLocation());
    DUMMY_EDGE.setStyle(getConnectorStyle());
  }

  private IOrientedRectangle textBounds;

  private RectD boundingBox = new RectD();

  @Override
  protected boolean isHit( IInputModeContext context, PointD location, ILabel label ) {
    configure(label);
    return label.getLayout().contains(location, context.getHitTestRadius()) || textBounds.contains(location, context.getHitTestRadius()) || DUMMY_EDGE.getStyle().getRenderer().getHitTestable(DUMMY_EDGE, DUMMY_EDGE.getStyle()).isHit(context, location);
  }

  @Override
  protected boolean isInBox( IInputModeContext context, RectD rectangle, ILabel label ) {
    configure(label);
    return rectangle.intersects(boundingBox.getEnlarged(context.getHitTestRadius()));
  }

  @Override
  protected RectD getBounds( ICanvasContext context, ILabel label ) {
    return RectD.add(boundingBox, DUMMY_EDGE.getStyle().getRenderer().getBoundsProvider(DUMMY_EDGE, DUMMY_EDGE.getStyle()).getBounds(context));
  }

  @Override
  protected boolean isVisible( ICanvasContext context, RectD rectangle, ILabel label ) {
    ILabelOwner owner = label.getOwner();
    // We're computing a (very generous) bounding box here because relying on GetBounds does not work.
    // The visibility test does not call Configure, which means we don't have the dummy edge set up yet.
    INode ownerNode = (owner instanceof INode) ? (INode)owner : null;
    if (ownerNode != null) {
      return rectangle.intersects(RectD.add(boundingBox, ownerNode.getLayout().toRectD()));
    }
    return rectangle.intersects(boundingBox);
  }

  static {
    LABEL_AS_NODE = new SimpleNode();
    SimpleLabel simpleLabel = new SimpleLabel(LABEL_AS_NODE, "", FreeLabelModel.INSTANCE.createDefaultParameter());
    simpleLabel.setStyle(new DefaultLabelStyle());
    DUMMY_TEXT_LABEL = simpleLabel;

    DUMMY_FOR_LABEL_OWNER = new SimpleNode();
    SimpleEdge simpleEdge = new SimpleEdge(new SimplePort(LABEL_AS_NODE, FreeNodePortLocationModel.NODE_CENTER_ANCHORED), new SimplePort(DUMMY_FOR_LABEL_OWNER, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));
    BpmnEdgeStyle bpmnEdgeStyle = new BpmnEdgeStyle();
    bpmnEdgeStyle.setType(EdgeType.ASSOCIATION);
    simpleEdge.setStyle(bpmnEdgeStyle);
    DUMMY_EDGE = simpleEdge;
  }

}
