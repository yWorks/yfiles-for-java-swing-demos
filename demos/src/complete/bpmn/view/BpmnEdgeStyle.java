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
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.util.Objects;

/**
 * An {@link IEdgeStyle} implementation representing a connection according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class BpmnEdgeStyle implements IEdgeStyle, IArrowOwner {
  private final PolylineEdgeStyle delegateStyle;

  private EdgeType type = EdgeType.SEQUENCE_FLOW;

  private IEdgeStyleRenderer renderer = new BpmnEdgeStyleRenderer();

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
    updatePen(getColor());
    updateArrow(value);
  }

  /**
   * Gets the stroke color of the edge.
   * @return The Color.
   * @see #setColor(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "EdgeDefaultColor", classValue = BpmnConstants.class)
  public final Paint getColor() {
    return delegateStyle.getPen().getPaint();
  }

  /**
   * Sets the stroke color of the edge.
   * @param value The Color to set.
   * @see #getColor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "EdgeDefaultColor", classValue = BpmnConstants.class)
  public final void setColor( Paint value ) {
    if (!Objects.equals(delegateStyle.getPen().getPaint(), value)) {
      updatePen(value);
      updateArrow(getType());
    }
  }

  private Pen innerPen;

  /**
   * Gets the inner stroke color of the edge when {@link #getType() Type} is {@link EdgeType#CONVERSATION}.
   * @return The InnerColor.
   * @see #setInnerColor(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "EdgeDefaultInnerColor", classValue = BpmnConstants.class)
  public final Paint getInnerColor() {
    return innerPen.getPaint();
  }

  /**
   * Sets the inner stroke color of the edge when {@link #getType() Type} is {@link EdgeType#CONVERSATION}.
   * @param value The InnerColor to set.
   * @see #getInnerColor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "EdgeDefaultInnerColor", classValue = BpmnConstants.class)
  public final void setInnerColor( Paint value ) {
    if (innerPen == null || !Objects.equals(innerPen.getPaint(), value)) {
      Pen pen = new Pen(value, 1);
      pen.setLineJoin(BasicStroke.JOIN_ROUND);
      innerPen = (Pen)pen;
    }
  }

  /**
   * Creates a new instance using {@link EdgeType#SEQUENCE_FLOW}.
   */
  public BpmnEdgeStyle() {
    PolylineEdgeStyle polylineEdgeStyle = new PolylineEdgeStyle();
    polylineEdgeStyle.setSmoothingLength(20);
    delegateStyle = polylineEdgeStyle;
    // Setting the type also initializes the pen and arrows correctly
    setType(EdgeType.SEQUENCE_FLOW);
    setColor(BpmnConstants.EDGE_DEFAULT_COLOR);
    setInnerColor(BpmnConstants.EDGE_DEFAULT_INNER_COLOR);
  }

  // clone constructor
  private BpmnEdgeStyle( BpmnEdgeStyle other ) {
    renderer = other.renderer;
    innerPen = other.innerPen;
    // We need to clone the wrapped style since our properties just delegate there
    delegateStyle = (PolylineEdgeStyle)other.delegateStyle.clone();
    // setting the type updates all read-only properties
    setType(other.getType());
    innerPen = other.innerPen;
  }

  public final BpmnEdgeStyle clone() {
    return (BpmnEdgeStyle)new BpmnEdgeStyle(this);
  }

  public final IEdgeStyleRenderer getRenderer() {
    return renderer;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IArrow getSourceArrow() {
    return delegateStyle.getSourceArrow();
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final IArrow getTargetArrow() {
    return delegateStyle.getTargetArrow();
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
    return delegateStyle.getSmoothingLength();
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
    delegateStyle.setSmoothingLength(value);
  }

  private void updatePen( Paint paint ) {
    Pen result;
    switch (getType()) {
      case CONDITIONAL_FLOW:
      case DEFAULT_FLOW:
      case SEQUENCE_FLOW:
      default:
        result = new Pen(paint, 1);
        break;
      case ASSOCIATION:
      case DIRECTED_ASSOCIATION:
      case BIDIRECTED_ASSOCIATION:
        result = new Pen();
        result.setPaint(paint);
        result.setDashStyle(DashStyle.getDot());
        result.setEndCap(BasicStroke.CAP_ROUND);
        break;
      case MESSAGE_FLOW:
        result = new Pen();
        result.setPaint(paint);
        result.setDashStyle(DashStyle.getDash());
        break;
      case CONVERSATION:
        result = new Pen();
        result.setPaint(paint);
        result.setThickness(3);
        result.setLineJoin(BasicStroke.JOIN_ROUND);
        break;
    }
    delegateStyle.setPen((Pen)result);
  }

  private void updateArrow( EdgeType type ) {
    switch (type) {
      case CONDITIONAL_FLOW:
        IconArrow iconArrow = new IconArrow(IconFactory.createArrowIcon(ArrowType.CONDITIONAL_SOURCE, getColor()));
        iconArrow.setBounds(new SizeD(16, 8));
        iconArrow.setCropLength(0);
        iconArrow.setLength(16);
        delegateStyle.setSourceArrow(iconArrow);
        IconArrow iconArrow2 = new IconArrow(IconFactory.createArrowIcon(ArrowType.DEFAULT_TARGET, getColor()));
        iconArrow2.setBounds(new SizeD(8, 6));
        iconArrow2.setCropLength(0);
        iconArrow2.setLength(8);
        delegateStyle.setTargetArrow(iconArrow2);
        break;
      case ASSOCIATION:
        delegateStyle.setSourceArrow(IArrow.NONE);
        delegateStyle.setTargetArrow(IArrow.NONE);
        break;
      case DIRECTED_ASSOCIATION:
        delegateStyle.setSourceArrow(IArrow.NONE);
        IconArrow iconArrow3 = new IconArrow(IconFactory.createArrowIcon(ArrowType.ASSOCIATION, getColor()));
        iconArrow3.setBounds(new SizeD(8, 6));
        iconArrow3.setCropLength(0);
        iconArrow3.setLength(0);
        delegateStyle.setTargetArrow(iconArrow3);
        break;
      case BIDIRECTED_ASSOCIATION:
        IconArrow iconArrow4 = new IconArrow(IconFactory.createArrowIcon(ArrowType.ASSOCIATION, getColor()));
        iconArrow4.setBounds(new SizeD(8, 6));
        iconArrow4.setCropLength(0);
        iconArrow4.setLength(0);
        delegateStyle.setSourceArrow(iconArrow4);
        IconArrow iconArrow5 = new IconArrow(IconFactory.createArrowIcon(ArrowType.ASSOCIATION, getColor()));
        iconArrow5.setBounds(new SizeD(8, 6));
        iconArrow5.setCropLength(0);
        iconArrow5.setLength(0);
        delegateStyle.setTargetArrow(iconArrow5);
        break;
      case MESSAGE_FLOW:
        IconArrow iconArrow6 = new IconArrow(IconFactory.createArrowIcon(ArrowType.MESSAGE_SOURCE, getColor()));
        iconArrow6.setBounds(new SizeD(6, 6));
        iconArrow6.setCropLength(0);
        iconArrow6.setLength(6);
        delegateStyle.setSourceArrow(iconArrow6);
        IconArrow iconArrow7 = new IconArrow(IconFactory.createArrowIcon(ArrowType.MESSAGE_TARGET, getColor()));
        iconArrow7.setBounds(new SizeD(8, 6));
        iconArrow7.setCropLength(0);
        iconArrow7.setLength(8);
        delegateStyle.setTargetArrow(iconArrow7);
        break;
      case DEFAULT_FLOW:
        IconArrow iconArrow8 = new IconArrow(IconFactory.createArrowIcon(ArrowType.DEFAULT_SOURCE, getColor()));
        iconArrow8.setBounds(new SizeD(8, 6));
        iconArrow8.setCropLength(0);
        iconArrow8.setLength(0);
        delegateStyle.setSourceArrow(iconArrow8);
        IconArrow iconArrow9 = new IconArrow(IconFactory.createArrowIcon(ArrowType.DEFAULT_TARGET, getColor()));
        iconArrow9.setBounds(new SizeD(8, 6));
        iconArrow9.setCropLength(0);
        iconArrow9.setLength(8);
        delegateStyle.setTargetArrow(iconArrow9);
        break;
      case CONVERSATION:
        delegateStyle.setSourceArrow(IArrow.NONE);
        delegateStyle.setTargetArrow(IArrow.NONE);
        break;
      case SEQUENCE_FLOW:
      default:
        delegateStyle.setSourceArrow(IArrow.NONE);
        IconArrow iconArrow10 = new IconArrow(IconFactory.createArrowIcon(ArrowType.DEFAULT_TARGET, getColor()));
        iconArrow10.setBounds(new SizeD(8, 6));
        iconArrow10.setCropLength(0);
        iconArrow10.setLength(8);
        delegateStyle.setTargetArrow(iconArrow10);
        break;
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
        Pen oldPen = style.delegateStyle.getPen();
        style.delegateStyle.setPen(style.innerPen);
        container.add(delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).createVisual(context));
        style.delegateStyle.setPen(oldPen);
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
          container.getChildren().set(0, firstChild);
        }
      } else {
        IVisual firstPath = container.getChildren().get(0);
        IVisual newFirstPath = delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).updateVisual(context, firstPath);
        if (firstPath != newFirstPath) {
          container.getChildren().set(0, firstPath);
        }

        Pen oldPen = style.delegateStyle.getPen();
        style.delegateStyle.setPen(style.innerPen);
        IVisual secondPath = container.getChildren().get(1);
        IVisual newSecondPath = delegateRenderer.getVisualCreator(this.edge, this.style.delegateStyle).updateVisual(context, secondPath);
        if (secondPath != newSecondPath) {
          container.getChildren().set(1, secondPath);
        }
        style.delegateStyle.setPen(oldPen);
      }
      return container;
    }

  }

  static class EdgeTypeVisualGroup extends VisualGroup {
    EdgeType edgeType;
  }

}
