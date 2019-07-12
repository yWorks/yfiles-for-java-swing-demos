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

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyleRenderer;
import com.yworks.yfiles.graph.styles.IShapeGeometry;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyleRenderer;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisualCreator;

/**
 * An {@link INodeStyle} implementation representing an Group Node according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class GroupNodeStyle implements INodeStyle, Cloneable {

  private static final INodeStyle SHAPE_NODE_STYLE;

  private static final INodeStyleRenderer RENDERER;


  private InsetsD insets = new InsetsD(15);

  /**
   * Gets the insets for the node.
   * <p>
   * These insets are returned via an {@link INodeInsetsProvider} if such an instance is queried through the
   * {@link INodeStyleRenderer#getContext(INode, INodeStyle) context lookup}.
   * </p>
   * @return The Insets.
   * @see INodeInsetsProvider
   * @see #setInsets(InsetsD)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "15", classValue = InsetsD.class)
  public final InsetsD getInsets() {
    return insets;
  }

  /**
   * Sets the insets for the node.
   * <p>
   * These insets are returned via an {@link INodeInsetsProvider} if such an instance is queried through the
   * {@link INodeStyleRenderer#getContext(INode, INodeStyle) context lookup}.
   * </p>
   * @param value The Insets to set.
   * @see INodeInsetsProvider
   * @see #getInsets()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "15", classValue = InsetsD.class)
  public final void setInsets( InsetsD value ) {
    insets = value;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final GroupNodeStyle clone() {
    try {
      return (GroupNodeStyle)super.clone();
    }catch (CloneNotSupportedException exception) {
      throw new RuntimeException("Class doesn't implement java.lang.Cloneable");
    }
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final INodeStyleRenderer getRenderer() {
    return RENDERER;
  }

  /**
   * An {@link INodeStyleRenderer} implementation used by {@link GroupNodeStyle}.
   */
  static class GroupNodeStyleRenderer implements INodeStyleRenderer, ILookup, IHitTestable {
    private INode lastNode;

    private GroupNodeStyle lastStyle;

    private GeneralPath lastOutline;

    public final IVisualCreator getVisualCreator( INode item, INodeStyle style ) {
      return SHAPE_NODE_STYLE.getRenderer().getVisualCreator(item, SHAPE_NODE_STYLE);
    }

    public final IBoundsProvider getBoundsProvider( INode item, INodeStyle style ) {
      return SHAPE_NODE_STYLE.getRenderer().getBoundsProvider(item, SHAPE_NODE_STYLE);
    }

    public final IVisibilityTestable getVisibilityTestable( INode item, INodeStyle style ) {
      return SHAPE_NODE_STYLE.getRenderer().getVisibilityTestable(item, SHAPE_NODE_STYLE);
    }

    public final IHitTestable getHitTestable( INode item, INodeStyle style ) {
      IShapeGeometry geometry = SHAPE_NODE_STYLE.getRenderer().getShapeGeometry(item, SHAPE_NODE_STYLE);
      lastOutline = geometry.getOutline();
      return this;
    }

    public final boolean isHit( IInputModeContext context, PointD location ) {
      return lastOutline.pathContains(location, context.getHitTestRadius());
    }

    public final IMarqueeTestable getMarqueeTestable( INode item, INodeStyle style ) {
      return SHAPE_NODE_STYLE.getRenderer().getMarqueeTestable(item, SHAPE_NODE_STYLE);
    }

    public final ILookup getContext( INode item, INodeStyle style ) {
      lastNode = item;
      lastStyle = (style instanceof GroupNodeStyle) ? (GroupNodeStyle)style : null;
      return this;
    }

    public final IShapeGeometry getShapeGeometry( INode node, INodeStyle style ) {
      return SHAPE_NODE_STYLE.getRenderer().getShapeGeometry(node, SHAPE_NODE_STYLE);
    }

    @Obfuscation(stripAfterObfuscation = false, exclude = true)
    public final <TLookup> TLookup lookup( Class<TLookup> type ) {
      if (type == INodeInsetsProvider.class && lastStyle != null) {
        return (TLookup)new GroupInsetsProvider(lastStyle);
      }
      ILookup lookup = SHAPE_NODE_STYLE.getRenderer().getContext(lastNode, SHAPE_NODE_STYLE);
      return lookup != null ? lookup.lookup(type) : null;
    }

    /**
     * Uses the style insets extended by the size of the participant bands.
     */
    private static class GroupInsetsProvider implements INodeInsetsProvider {
      private final GroupNodeStyle style;

      GroupInsetsProvider( GroupNodeStyle style ) {
        this.style = style;
      }

      public final InsetsD getInsets( INode node ) {
        return style.getInsets();
      }

    }

  }

  static {
    ShapeNodeStyle shapeNodeStyle = new ShapeNodeStyle();
    shapeNodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    shapeNodeStyle.setPaint(BpmnConstants.Paints.GROUP_NODE);
    shapeNodeStyle.setPen(BpmnConstants.Pens.GROUP_NODE);
    SHAPE_NODE_STYLE = shapeNodeStyle;
    ((ShapeNodeStyleRenderer)SHAPE_NODE_STYLE.getRenderer()).setRoundRectArcRadius(BpmnConstants.GROUP_NODE_CORNER_RADIUS);
    RENDERER = new GroupNodeStyleRenderer();
  }

}
