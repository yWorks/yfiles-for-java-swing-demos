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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyleRenderer;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.VoidLabelStyle;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import java.awt.Paint;
import java.util.Objects;

/**
 * A label style for message labels of nodes using a {@link ChoreographyNodeStyle}.
 * <p>
 * To place labels with this style, {@link ChoreographyLabelModel#NORTH_MESSAGE} or {@link ChoreographyLabelModel#SOUTH_MESSAGE}
 * are recommended.
 * </p>
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class ChoreographyMessageLabelStyle implements ILabelStyle, Cloneable {
  private static final ChoreographyMessageLabelStyleRenderer RENDERER = new ChoreographyMessageLabelStyleRenderer();

  private static final BpmnEdgeStyle CONNECTOR_STYLE;

  private static final DefaultLabelStyle TEXT_STYLE = new DefaultLabelStyle();

  public static final ILabelModelParameter DEFAULT_TEXT_PLACEMENT;

  static {
    final BpmnEdgeStyle connectorStyle = new BpmnEdgeStyle();
    connectorStyle.setType(EdgeType.ASSOCIATION);
    CONNECTOR_STYLE = connectorStyle;
    final ExteriorLabelModel textPlacement = new ExteriorLabelModel();
    textPlacement.setInsets(new InsetsD(5));
    DEFAULT_TEXT_PLACEMENT = textPlacement.createParameter(ExteriorLabelModel.Position.WEST);
  }

  private final BpmnNodeStyle messageStyle;

  private final ConnectedIconLabelStyle delegateStyle;

  /**
   * Gets where the text is placed relative to the message icon.
   * <p>
   * The label model parameter has to support {@link INode}s.
   * </p>
   * @return The TextPlacement.
   * @see #setTextPlacement(ILabelModelParameter)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyMessageLabelStyle.DefaultTextPlacement", classValue = BpmnDefaultValueSerializerHolder.class)
  public final ILabelModelParameter getTextPlacement() {
    return delegateStyle != null ? delegateStyle.getTextPlacement() : null;
  }

  /**
   * Sets where the text is placed relative to the message icon.
   * <p>
   * The label model parameter has to support {@link INode}s.
   * </p>
   * @param value The TextPlacement to set.
   * @see #getTextPlacement()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ChoreographyMessageLabelStyle.DefaultTextPlacement", classValue = BpmnDefaultValueSerializerHolder.class)
  public final void setTextPlacement( ILabelModelParameter value ) {
    if (delegateStyle != null) {
      delegateStyle.setTextPlacement(value);
    }
  }

  /**
   * Creates a new instance.
   */
  public ChoreographyMessageLabelStyle() {
    final BpmnNodeStyle messageStyle = new BpmnNodeStyle();
    messageStyle.setMinimumSize(BpmnConstants.MESSAGE_SIZE);
    this.messageStyle = messageStyle;

    ConnectedIconLabelStyle connectedIconLabelStyle = new ConnectedIconLabelStyle();
    connectedIconLabelStyle.setIconSize(BpmnConstants.MESSAGE_SIZE);
    connectedIconLabelStyle.setIconStyle(this.messageStyle);
    connectedIconLabelStyle.setTextStyle(TEXT_STYLE);
    connectedIconLabelStyle.setConnectorStyle(CONNECTOR_STYLE);
    connectedIconLabelStyle.setLabelConnectorLocation(FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED);
    connectedIconLabelStyle.setNodeConnectorLocation(FreeNodePortLocationModel.NODE_TOP_ANCHORED);
    delegateStyle = connectedIconLabelStyle;

    setTextPlacement(DEFAULT_TEXT_PLACEMENT);
  }

  public final ChoreographyMessageLabelStyle clone() {
    try {
      return (ChoreographyMessageLabelStyle)super.clone();
    }catch (CloneNotSupportedException exception) {
      throw new RuntimeException("Class doesn't implement java.lang.Cloneable");
    }
  }

  public final ILabelStyleRenderer getRenderer() {
    return RENDERER;
  }

  /**
   * An {@link ILabelStyleRenderer} implementation used by {@link ChoreographyMessageLabelStyle}.
   */
  static class ChoreographyMessageLabelStyleRenderer implements ILabelStyleRenderer, IVisualCreator {
    private ILabel item;

    private ILabelStyle style;

    private boolean north;

    private Paint messageColor;

    private Pen messageOutline;

    private ILabelStyle getCurrentStyle( ILabel item, ILabelStyle style ) {

      if (!(style instanceof ChoreographyMessageLabelStyle)) {
        return VoidLabelStyle.INSTANCE;
      }
      ChoreographyMessageLabelStyle labelStyle = (ChoreographyMessageLabelStyle)style;

      north = true;
      messageColor = BpmnConstants.DEFAULT_INITIATING_MESSAGE_COLOR;
      messageOutline = null;
      ILabelOwner owner = item.getOwner();
      INode node = (owner instanceof INode) ? (INode)owner : null;
      if (node != null) {
        north = item.getLayout().getCenter().y < node.getLayout().getCenter().y;
        INodeStyle s = node.getStyle();
        ChoreographyNodeStyle nodeStyle = (s instanceof ChoreographyNodeStyle) ? (ChoreographyNodeStyle)s : null;
        if (nodeStyle != null) {
          boolean responseMessage = nodeStyle.isInitiatingAtTop() ^ north;
          messageColor = responseMessage ? nodeStyle.getResponseColor() : nodeStyle.getInitiatingColor();
          messageOutline = nodeStyle.messagePen;
        }
      }
      messageOutline = messageOutline != null ? messageOutline : (Pen)new Pen(BpmnConstants.DEFAULT_MESSAGE_OUTLINE, 1);

      ConnectedIconLabelStyle delegateStyle = labelStyle.delegateStyle;
      delegateStyle.setIconStyle(labelStyle.messageStyle);
      labelStyle.messageStyle.setIcon(IconFactory.createMessage(messageOutline, messageColor, false));
      delegateStyle.setLabelConnectorLocation(north ? FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED : FreeNodePortLocationModel.NODE_TOP_ANCHORED);
      delegateStyle.setNodeConnectorLocation(north ? FreeNodePortLocationModel.NODE_TOP_ANCHORED : FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED);
      return delegateStyle;
    }

    public final IVisualCreator getVisualCreator( ILabel item, ILabelStyle style ) {
      this.item = item;
      this.style = style;
      return this;
    }

    public final IBoundsProvider getBoundsProvider( ILabel item, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      return delegateStyle.getRenderer().getBoundsProvider(item, delegateStyle);
    }

    public final IVisibilityTestable getVisibilityTestable( ILabel item, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      return delegateStyle.getRenderer().getVisibilityTestable(item, delegateStyle);
    }

    public final IHitTestable getHitTestable( ILabel item, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      return delegateStyle.getRenderer().getHitTestable(item, delegateStyle);
    }

    public final IMarqueeTestable getMarqueeTestable( ILabel item, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      return delegateStyle.getRenderer().getMarqueeTestable(item, delegateStyle);
    }

    public final ILookup getContext( ILabel item, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      return delegateStyle.getRenderer().getContext(item, delegateStyle);
    }

    public final SizeD getPreferredSize( ILabel label, ILabelStyle style ) {
      ILabelStyle delegateStyle = getCurrentStyle(label, style);
      return delegateStyle.getRenderer().getPreferredSize(label, delegateStyle);
    }

    public final IVisual createVisual( IRenderContext context ) {
      MyVisual container = new MyVisual(getTextPlacement(), north, messageColor, messageOutline);
      ILabelStyle delegateStyle = getCurrentStyle(item, style);
      container.add(delegateStyle.getRenderer().getVisualCreator(item, delegateStyle).createVisual(context));
      return container;
    }

    public final IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
      MyVisual container = (oldVisual instanceof MyVisual) ? (MyVisual)oldVisual : null;
      ILabelStyle delegateStyle = getCurrentStyle(item, style);

      if(container == null) {
        return createVisual(context);
      }
      ILabelModelParameter textPlacement = container.getTextPlacement();
      if (!(textPlacement == getTextPlacement() && container.isNorth() == north && container.messageColor == messageColor && container.messageOutline == messageOutline) || container.getChildren().size() != 1) {
        return createVisual(context);
      }
      IVisual oldDelegateVisual = container.getChildren().get(0);
      IVisual newDelegateVisual = delegateStyle.getRenderer().getVisualCreator(item, delegateStyle).updateVisual(context, oldDelegateVisual);
      if (oldDelegateVisual != newDelegateVisual) {
        container.getChildren().set(0, newDelegateVisual);
      }
      container.setNorth(north);
      container.setTextPlacement(textPlacement);
      container.setMessageColor(messageColor);
      container.setMessageOutline(messageOutline);
      return container;
    }

    private ILabelModelParameter getTextPlacement() {
      return ((ChoreographyMessageLabelStyle)style).getTextPlacement();
    }

    static class MyVisual extends VisualGroup {

      public MyVisual(ILabelModelParameter textPlacement, boolean north, Paint messageColor, Pen messageOutline) {
        this.textPlacement = textPlacement;
        this.north = north;
        this.messageColor = messageColor;
        this.messageOutline = messageOutline;
      }

      private ILabelModelParameter textPlacement;

      private final ILabelModelParameter getTextPlacement() {
        return this.textPlacement;
      }

      public final void setTextPlacement( ILabelModelParameter value ) {
        this.textPlacement = value;
      }

      private boolean north;

      private final boolean isNorth() {
        return this.north;
      }

      public final void setNorth( boolean value ) {
        this.north = value;
      }

      private Paint messageColor;

      private final Paint getMessageColor() {
        return this.messageColor;
      }

      public final void setMessageColor( Paint value ) {
        this.messageColor = value;
      }

      private Pen messageOutline;

      private final Pen getMessageOutline() {
        return this.messageOutline;
      }

      public final void setMessageOutline( Pen value ) {
        this.messageOutline = value;
      }

      @Override
      public boolean equals( Object obj ) {
        MyVisual other = (obj instanceof MyVisual) ? (MyVisual)obj : null;
        if (other == null) {
          return false;
        }
        return getTextPlacement() == other.getTextPlacement() && isNorth() == other.isNorth() && Objects.equals(getMessageColor(), other.getMessageColor()) && Objects.equals(getMessageOutline(), other.getMessageOutline());
      }

    }

  }

}
