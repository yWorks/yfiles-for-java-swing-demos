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
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;

import java.awt.BasicStroke;
import java.awt.Paint;

/**
 * An {@link INodeStyle} implementation representing an Group Node according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class GroupNodeStyle implements INodeStyle {
  private final GroupNodeStyleRenderer renderer = new GroupNodeStyleRenderer();

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

  public final GroupNodeStyle clone() {
    GroupNodeStyle groupNodeStyle = new GroupNodeStyle();
    groupNodeStyle.setInsets(getInsets());
    groupNodeStyle.setBackground(getBackground());
    groupNodeStyle.setOutline(getOutline());
    return (GroupNodeStyle)groupNodeStyle;
  }

  public final INodeStyleRenderer getRenderer() {
    return renderer;
  }

  /**
   * Gets the background color of the group.
   * @return The Background.
   * @see #setBackground(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "GroupDefaultBackground", classValue = BpmnConstants.class)
  public final Paint getBackground() {
    return renderer.shapeNodeStyle.getPaint();
  }

  /**
   * Sets the background color of the group.
   * @param value The Background to set.
   * @see #getBackground()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "GroupDefaultBackground", classValue = BpmnConstants.class)
  public final void setBackground( Paint value ) {
    if (renderer.shapeNodeStyle.getPaint() != value) {
      renderer.shapeNodeStyle.setPaint(value);
    }
  }

  /**
   * Gets the outline color of the group.
   * @return The Outline.
   * @see #setOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "GroupDefaultOutline", classValue = BpmnConstants.class)
  public final Paint getOutline() {
    return renderer.shapeNodeStyle.getPen().getPaint();
  }

  /**
   * Sets the outline color of the group.
   * @param value The Outline to set.
   * @see #getOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "GroupDefaultOutline", classValue = BpmnConstants.class)
  public final void setOutline( Paint value ) {
    if (renderer.shapeNodeStyle.getPen().getPaint() != value) {
      renderer.shapeNodeStyle.setPen(getPen(value));
    }
  }

  private static Pen getPen( Paint outline ) {
    Pen pen = new Pen();
    pen.setDashStyle(DashStyle.getDashDot());
    pen.setEndCap(BasicStroke.CAP_ROUND);
    pen.setPaint(outline);
    return (Pen)pen;
  }

  /**
   * An {@link INodeStyleRenderer} implementation used by {@link GroupNodeStyle}.
   */
  static class GroupNodeStyleRenderer implements INodeStyleRenderer, ILookup {
    final ShapeNodeStyle shapeNodeStyle;

    private INode lastNode;

    private GroupNodeStyle lastStyle;

    public GroupNodeStyleRenderer() {
      final ShapeNodeStyleRenderer renderer = new ShapeNodeStyleRenderer();
      renderer.setRoundRectArcRadius(BpmnConstants.GROUP_NODE_CORNER_RADIUS);
      final ShapeNodeStyle shapeNodeStyle = new ShapeNodeStyle(renderer);
      shapeNodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
      shapeNodeStyle.setPaint(BpmnConstants.GROUP_DEFAULT_BACKGROUND);
      shapeNodeStyle.setPen(getPen(BpmnConstants.GROUP_DEFAULT_OUTLINE));
      this.shapeNodeStyle = shapeNodeStyle;
    }

    public final IVisualCreator getVisualCreator( INode item, INodeStyle style ) {
      return shapeNodeStyle.getRenderer().getVisualCreator(item, shapeNodeStyle);
    }

    public final IBoundsProvider getBoundsProvider( INode item, INodeStyle style ) {
      return shapeNodeStyle.getRenderer().getBoundsProvider(item, shapeNodeStyle);
    }

    public final IVisibilityTestable getVisibilityTestable( INode item, INodeStyle style ) {
      return shapeNodeStyle.getRenderer().getVisibilityTestable(item, shapeNodeStyle);
    }

    public final IHitTestable getHitTestable( INode item, INodeStyle style ) {
      IShapeGeometry geometry = shapeNodeStyle.getRenderer().getShapeGeometry(item, shapeNodeStyle);
      GeneralPath outline = geometry.getOutline();
      return new PathHitTestable(outline);
    }

    public final IMarqueeTestable getMarqueeTestable( INode item, INodeStyle style ) {
      return shapeNodeStyle.getRenderer().getMarqueeTestable(item, shapeNodeStyle);
    }

    public final ILookup getContext( INode item, INodeStyle style ) {
      lastNode = item;
      lastStyle = (style instanceof GroupNodeStyle) ? (GroupNodeStyle)style : null;
      return this;
    }

    public final IShapeGeometry getShapeGeometry( INode node, INodeStyle style ) {
      return shapeNodeStyle.getRenderer().getShapeGeometry(node, shapeNodeStyle);
    }

    @Obfuscation(stripAfterObfuscation = false, exclude = true)
    public final <TLookup> TLookup lookup( Class<TLookup> type ) {
      if (type == INodeInsetsProvider.class && lastStyle != null) {
        return (TLookup)new GroupInsetsProvider(lastStyle);
      }
      ILookup lookup = shapeNodeStyle.getRenderer().getContext(lastNode, shapeNodeStyle);
      return lookup != null ? lookup.lookup(type) : null;
    }

    /**
     * Uses the style insets extended by the size of the participant bands.
     */
    private static final class GroupInsetsProvider implements INodeInsetsProvider {
      private final GroupNodeStyle style;

      GroupInsetsProvider( GroupNodeStyle style ) {
        this.style = style;
      }

      public final InsetsD getInsets( INode node ) {
        return style.getInsets();
      }

    }

    private static class PathHitTestable implements IHitTestable {
      private final GeneralPath path;

      public PathHitTestable( GeneralPath path ) {
        this.path = path;
      }

      public final boolean isHit( IInputModeContext context, PointD location ) {
        return path.pathContains(location, context.getHitTestRadius());
      }
    }
  }

}
