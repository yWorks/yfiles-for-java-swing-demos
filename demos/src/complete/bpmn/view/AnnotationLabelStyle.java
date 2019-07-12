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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyleRenderer;
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
import com.yworks.yfiles.view.VisualGroup;

/**
 * A label style for annotations according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class AnnotationLabelStyle implements ILabelStyle, Cloneable {

  private static final AnnotationLabelStyleRenderer RENDERER = new AnnotationLabelStyleRenderer();

  private static final BpmnEdgeStyle CONNECTOR_STYLE;

  private static final DefaultLabelStyle TEXT_STYLE;

  private static final AnnotationNodeStyle LEFT_ANNOTATION_STYLE;

  private static final AnnotationNodeStyle RIGHT_ANNOTATION_STYLE;

  static final AnnotationNodeStyle getLeftAnnotationStyle() {
    return LEFT_ANNOTATION_STYLE;
  }

  static final AnnotationNodeStyle getRightAnnotationStyle() {
    return RIGHT_ANNOTATION_STYLE;
  }



  private double insets = 5.0;

  /**
   * Gets the insets around the text.
   * @return The Insets.
   * @see #setInsets(double)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(doubleValue = 5.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final double getInsets() {
    return insets;
  }

  /**
   * Sets the insets around the text.
   * @param value The Insets to set.
   * @see #getInsets()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(doubleValue = 5.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final void setInsets( double value ) {
    insets = value;
  }

  private ConnectedIconLabelStyle delegateStyle;

  final ConnectedIconLabelStyle getDelegateStyle() {
    return delegateStyle;
  }


  /**
   * Creates a new instance.
   */
  public AnnotationLabelStyle() {
    ConnectedIconLabelStyle connectedIconLabelStyle = new ConnectedIconLabelStyle();
    connectedIconLabelStyle.setIconStyle(getLeftAnnotationStyle());
    connectedIconLabelStyle.setTextStyle(TEXT_STYLE);
    connectedIconLabelStyle.setTextPlacement(InteriorLabelModel.CENTER);
    connectedIconLabelStyle.setConnectorStyle(CONNECTOR_STYLE);
    connectedIconLabelStyle.setLabelConnectorLocation(FreeNodePortLocationModel.NODE_LEFT_ANCHORED);
    connectedIconLabelStyle.setNodeConnectorLocation(FreeNodePortLocationModel.NODE_CENTER_ANCHORED);
    delegateStyle = connectedIconLabelStyle;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final AnnotationLabelStyle clone() {
    try {
      return (AnnotationLabelStyle)super.clone();
    }catch (CloneNotSupportedException exception) {
      throw new RuntimeException("Class doesn't implement java.lang.Cloneable");
    }
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelStyleRenderer getRenderer() {
    return RENDERER;
  }


  /**
   * An {@link ILabelStyleRenderer} implementation used by {@link AnnotationLabelStyle}.
   */
  private static class AnnotationLabelStyleRenderer implements ILabelStyleRenderer, IVisualCreator {
    private ILabel label;

    private ILabelStyle labelStyle;

    private boolean left;

    private double insets;

    private ILabelStyle getCurrentStyle( ILabel item, ILabelStyle style ) {
      AnnotationLabelStyle annotationLabelStyle = (style instanceof AnnotationLabelStyle) ? (AnnotationLabelStyle)style : null;
      ILabelOwner owner = item.getOwner();
      INode nodeOwner = (owner instanceof INode) ? (INode)owner : null;
      if (annotationLabelStyle == null || nodeOwner == null) {
        return VoidLabelStyle.INSTANCE;
      }

      left = item.getLayout().getCenter().x > nodeOwner.getLayout().getCenter().x;
      insets = annotationLabelStyle.getInsets();

      ConnectedIconLabelStyle delegateStyle = annotationLabelStyle.getDelegateStyle();
      delegateStyle.setIconStyle(left ? getLeftAnnotationStyle() : getRightAnnotationStyle());
      delegateStyle.setLabelConnectorLocation(left ? FreeNodePortLocationModel.NODE_LEFT_ANCHORED : FreeNodePortLocationModel.NODE_RIGHT_ANCHORED);
      return delegateStyle;
    }

    public final IVisualCreator getVisualCreator( ILabel item, ILabelStyle style ) {
      label = item;
      labelStyle = style;
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
      SizeD preferredTextSize = TEXT_STYLE.getRenderer().getPreferredSize(label, TEXT_STYLE);
      double insets = ((AnnotationLabelStyle)style).getInsets();
      return new SizeD(2 * insets + preferredTextSize.width, 2 * insets + preferredTextSize.height);
    }

    public final IVisual createVisual( IRenderContext context ) {
      MyVisual container = new MyVisual(left, insets);
      ILabelStyle delegateStyle = getCurrentStyle(label, labelStyle);
      container.add(delegateStyle.getRenderer().getVisualCreator(label, delegateStyle).createVisual(context));
      return container;
    }

    public final IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
      MyVisual container = (oldVisual instanceof MyVisual) ? (MyVisual)oldVisual : null;
      if(container == null) {
        return createVisual(context);
      }
      ILabelStyle delegateStyle = getCurrentStyle(label, labelStyle);
      if (container.isLeft() != left || container.getInsets() != insets || container.getChildren().size() != 1) {
        return createVisual(context);
      }
      IVisual oldDelegateVisual = container.getChildren().get(0);
      IVisual newDelegateVisual = delegateStyle.getRenderer().getVisualCreator(label, delegateStyle).updateVisual(context, oldDelegateVisual);
      if (oldDelegateVisual != newDelegateVisual) {
        container.getChildren().set(0, newDelegateVisual);
      }
      container.setInsets(insets);
      container.setLeft(left);
      return container;
    }

    private static class MyVisual extends VisualGroup {
      private boolean left;

      private final boolean isLeft() {
        return this.left;
      }

      public final void setLeft( boolean value ) {
        this.left = value;
      }

      private double insets;

      private final double getInsets() {
        return this.insets;
      }

      public final void setInsets( double value ) {
        this.insets = value;
      }

      public MyVisual(boolean left, double insets) {
        this.left = left;
        this.insets = insets;
      }

    }

  }

  static {
    AnnotationNodeStyle annotationNodeStyle = new AnnotationNodeStyle();
    annotationNodeStyle.setLeft(true);
    LEFT_ANNOTATION_STYLE = annotationNodeStyle;
    AnnotationNodeStyle annotationNodeStyle2 = new AnnotationNodeStyle();
    annotationNodeStyle2.setLeft(false);
    RIGHT_ANNOTATION_STYLE = annotationNodeStyle2;
    BpmnEdgeStyle bpmnEdgeStyle = new BpmnEdgeStyle();
    bpmnEdgeStyle.setType(EdgeType.ASSOCIATION);
    CONNECTOR_STYLE = bpmnEdgeStyle;
    TEXT_STYLE = new DefaultLabelStyle();
  }

}
