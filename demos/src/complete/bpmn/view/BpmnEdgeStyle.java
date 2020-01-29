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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IArrowOwner;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.IEdgeStyleRenderer;
import com.yworks.yfiles.graph.styles.IPathGeometry;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyleRenderer;
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

/**
 * An {@link IEdgeStyle} implementation representing a connection according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class BpmnEdgeStyle implements IEdgeStyle, IArrowOwner {

  private static final IconArrow DEFAULT_TARGET_ARROW;

  private static final IconArrow DEFAULT_SOURCE_ARROW;

  private static final IconArrow ASSOCIATION_ARROW;

  private static final IconArrow CONDITIONAL_SOURCE_ARROW;

  private static final IconArrow MESSAGE_TARGET_ARROW;

  private static final IconArrow MESSAGE_SOURCE_ARROW;



  private PolylineEdgeStyle delegateStyle;

  private EdgeType type = EdgeType.SEQUENCE_FLOW;

  private double smoothingLength = 20;

  private IEdgeStyleRenderer renderer;

  private IArrow sourceArrow;

  private IArrow targetArrow;

  private Pen pen;

  private Pen doubleLineCenterPen;

  /**
   * Gets the edge type of this style.
   * @return The Type.
   * @see #setType(EdgeType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeType.class, stringValue = "SEQUENCE_FLOW")
  public final EdgeType getType() {
    return type;
  }

  /**
   * Sets the edge type of this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeType.class, stringValue = "SEQUENCE_FLOW")
  public final void setType( EdgeType value ) {
    type = value;
    switch (value) {
      case CONDITIONAL_FLOW:
        pen = BpmnConstants.Pens.BPMN_EDGE_STYLE;
        sourceArrow = CONDITIONAL_SOURCE_ARROW;
        targetArrow = DEFAULT_TARGET_ARROW;
        break;
      case ASSOCIATION:
        pen = BpmnConstants.Pens.ASSOCIATION_EDGE_STYLE;
        sourceArrow = IArrow.NONE;
        targetArrow = IArrow.NONE;
        break;
      case DIRECTED_ASSOCIATION:
        pen = BpmnConstants.Pens.ASSOCIATION_EDGE_STYLE;
        sourceArrow = IArrow.NONE;
        targetArrow = ASSOCIATION_ARROW;
        break;
      case BIDIRECTED_ASSOCIATION:
        pen = BpmnConstants.Pens.ASSOCIATION_EDGE_STYLE;
        sourceArrow = ASSOCIATION_ARROW;
        targetArrow = ASSOCIATION_ARROW;
        break;
      case MESSAGE_FLOW:
        pen = BpmnConstants.Pens.MESSAGE_EDGE_STYLE;
        sourceArrow = MESSAGE_SOURCE_ARROW;
        targetArrow = MESSAGE_TARGET_ARROW;
        break;
      case DEFAULT_FLOW:
        pen = BpmnConstants.Pens.BPMN_EDGE_STYLE;
        sourceArrow = DEFAULT_SOURCE_ARROW;
        targetArrow = DEFAULT_TARGET_ARROW;
        break;
      case CONVERSATION:
        pen = BpmnConstants.Pens.CONVERSATION_DOUBLE_LINE;
        sourceArrow = IArrow.NONE;
        targetArrow = IArrow.NONE;
        break;
      case SEQUENCE_FLOW:
      default:
        pen = BpmnConstants.Pens.BPMN_EDGE_STYLE;
        sourceArrow = IArrow.NONE;
        targetArrow = DEFAULT_TARGET_ARROW;
        break;
    }
    updateDelegate();
  }


  /**
   * Creates a new instance using {@link EdgeType#SEQUENCE_FLOW}.
   */
  public BpmnEdgeStyle() {
    renderer = new BpmnEdgeStyleRenderer();
    doubleLineCenterPen = BpmnConstants.Pens.CONVERSATION_CENTER_LINE;
    delegateStyle = new PolylineEdgeStyle();
    setType(EdgeType.SEQUENCE_FLOW);
  }

  // clone constructor
  private BpmnEdgeStyle( BpmnEdgeStyle other ) {
    renderer = other.renderer;
    doubleLineCenterPen = other.doubleLineCenterPen;
    delegateStyle = new PolylineEdgeStyle();
    smoothingLength = other.smoothingLength;
    // setting the type updates all read-only properties
    setType(other.getType());
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final BpmnEdgeStyle clone() {
    return (BpmnEdgeStyle)new BpmnEdgeStyle(this);
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IEdgeStyleRenderer getRenderer() {
    return renderer;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IArrow getSourceArrow() {
    return sourceArrow;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IArrow getTargetArrow() {
    return targetArrow;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final Pen getPen() {
    return pen;
  }

  /**
   * Gets the {@link #getPen() Pen} for the center line of a {@link EdgeType#CONVERSATION}.
   * @return The DoubleLineCenterPen.
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  final Pen getDoubleLineCenterPen() {
    return doubleLineCenterPen;
  }

  /**
   * Gets the smoothing length used for creating smooth bends.
   * <p>
   * A value of {@code 0.0d} will disable smoothing.
   * </p>
   * @return The SmoothingLength.
   * @see #setSmoothingLength(double)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(doubleValue = 20.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final double getSmoothingLength() {
    return smoothingLength;
  }

  /**
   * Sets the smoothing length used for creating smooth bends.
   * <p>
   * A value of {@code 0.0d} will disable smoothing.
   * </p>
   * @param value The SmoothingLength to set.
   * @see #getSmoothingLength()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(doubleValue = 20.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  public final void setSmoothingLength( double value ) {
    smoothingLength = value;
    updateDelegate();
  }

  private void updateDelegate() {
    if (delegateStyle != null) {
      delegateStyle.setPen(getPen());
      delegateStyle.setSourceArrow(getSourceArrow());
      delegateStyle.setTargetArrow(getTargetArrow());
      delegateStyle.setSmoothingLength(getSmoothingLength());
    }
  }


  /**
   * Renderer class used for the {@link BpmnEdgeStyle}.
   */
  private static class BpmnEdgeStyleRenderer implements IEdgeStyleRenderer, IVisualCreator {
    private static final PolylineEdgeStyleRenderer delegateRenderer = new PolylineEdgeStyleRenderer();

    private BpmnEdgeStyle style;

    private IEdge edge;

    public final IBoundsProvider getBoundsProvider( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getBoundsProvider(edge, this.style.delegateStyle);
    }

    public final IPathGeometry getPathGeometry( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getPathGeometry(edge, this.style.delegateStyle);
    }

    public final IVisualCreator getVisualCreator( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      this.edge = edge;
      delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle);
      return this;
    }

    public final IVisibilityTestable getVisibilityTestable( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getVisibilityTestable(edge, this.style.delegateStyle);
    }

    public final IHitTestable getHitTestable( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getHitTestable(edge, this.style.delegateStyle);
    }

    public final IMarqueeTestable getMarqueeTestable( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getMarqueeTestable(edge, this.style.delegateStyle);
    }

    public final ILookup getContext( IEdge edge, IEdgeStyle style ) {
      this.style = (BpmnEdgeStyle)style;
      return delegateRenderer.getContext(edge, this.style.delegateStyle);
    }

    public IVisual createVisual( IRenderContext context ) {
      EdgeTypeVisualGroup container = new EdgeTypeVisualGroup();
      if (style.getType() != EdgeType.CONVERSATION) {
        container.add(delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).createVisual(context));
      } else {
        container.add(delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).createVisual(context));
        style.delegateStyle.setPen(style.getDoubleLineCenterPen());
        container.add(delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).createVisual(context));
        style.delegateStyle.setPen(style.getPen());
      }
      container.edgeType = style.getType();
      return container;
    }

    public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
      EdgeTypeVisualGroup container = (oldVisual instanceof EdgeTypeVisualGroup) ? (EdgeTypeVisualGroup)oldVisual : null;
      if (container == null) {
        return createVisual(context);
      }
      EdgeType cachedType = container.edgeType;
      if (cachedType != style.getType() && (cachedType == EdgeType.CONVERSATION || style.getType() == EdgeType.CONVERSATION)) {
        return createVisual(context);
      }
      if (style.getType() != EdgeType.CONVERSATION) {
        IVisual firstChild = container.getChildren().get(0);
        IVisual newFirstChild = delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).updateVisual(context, firstChild);
        if (firstChild != newFirstChild) {
          container.getChildren().remove(firstChild);
          container.getChildren().add(0, newFirstChild);
        }
      } else {
        IVisual firstPath = container.getChildren().get(0);
        IVisual newFirstPath = delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).updateVisual(context, firstPath);
        if (firstPath != newFirstPath) {
          container.getChildren().remove(firstPath);
          container.getChildren().add(0, newFirstPath);
        }

        style.delegateStyle.setPen(style.getDoubleLineCenterPen());
        IVisual secondPath = container.getChildren().get(1);
        IVisual newSecondPath = delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).updateVisual(context, secondPath);
        if (secondPath != newSecondPath) {
          container.getChildren().remove(secondPath);
          container.getChildren().add(1, newSecondPath);
        }
        style.delegateStyle.setPen(style.getPen());
      }
      return container;
    }

  }

  static {
    IconArrow iconArrow = new IconArrow(IconFactory.createArrowIcon(ArrowType.DEFAULT_TARGET));
    iconArrow.setBounds(new SizeD(8, 6));
    iconArrow.setCropLength(0);
    iconArrow.setLength(8);
    DEFAULT_TARGET_ARROW = iconArrow;
    IconArrow iconArrow2 = new IconArrow(IconFactory.createArrowIcon(ArrowType.DEFAULT_SOURCE));
    iconArrow2.setBounds(new SizeD(8, 6));
    iconArrow2.setCropLength(0);
    iconArrow2.setLength(0);
    DEFAULT_SOURCE_ARROW = iconArrow2;
    IconArrow iconArrow3 = new IconArrow(IconFactory.createArrowIcon(ArrowType.ASSOCIATION));
    iconArrow3.setBounds(new SizeD(8, 6));
    iconArrow3.setCropLength(0);
    iconArrow3.setLength(0);
    ASSOCIATION_ARROW = iconArrow3;
    IconArrow iconArrow4 = new IconArrow(IconFactory.createArrowIcon(ArrowType.CONDITIONAL_SOURCE));
    iconArrow4.setBounds(new SizeD(16, 8));
    iconArrow4.setCropLength(0);
    iconArrow4.setLength(16);
    CONDITIONAL_SOURCE_ARROW = iconArrow4;
    IconArrow iconArrow5 = new IconArrow(IconFactory.createArrowIcon(ArrowType.MESSAGE_TARGET));
    iconArrow5.setBounds(new SizeD(8, 6));
    iconArrow5.setCropLength(0);
    iconArrow5.setLength(8);
    MESSAGE_TARGET_ARROW = iconArrow5;
    IconArrow iconArrow6 = new IconArrow(IconFactory.createArrowIcon(ArrowType.MESSAGE_SOURCE));
    iconArrow6.setBounds(new SizeD(6, 6));
    iconArrow6.setCropLength(0);
    iconArrow6.setLength(6);
    MESSAGE_SOURCE_ARROW = iconArrow6;
  }

  static class EdgeTypeVisualGroup extends VisualGroup {
    EdgeType edgeType;
  }

}
