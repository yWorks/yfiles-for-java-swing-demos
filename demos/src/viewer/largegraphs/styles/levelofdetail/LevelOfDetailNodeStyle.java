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
package viewer.largegraphs.styles.levelofdetail;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Graphics2D;

/**
 * Level-of-detail style for nodes that delegates to different {@link INodeStyle}s depending on the zoom level.
 */
public class LevelOfDetailNodeStyle extends AbstractNodeStyle {

  // the style container
  private LevelOfDetailStyleContainer<INodeStyle> styles = new LevelOfDetailStyleContainer<>();

  /**
   * Gets the style container.
   * <p>
   *   Styles have to be added in ascending order of zoom level.
   * </p>
   */
  public LevelOfDetailStyleContainer<INodeStyle> getStyles() {
    return styles;
  }

  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    INodeStyle style = styles.getStyle(context.getZoom());
    IVisual visual = style.getRenderer().getVisualCreator(node, style).createVisual(context);
    return new NodeVisual(visual, context.getZoom());
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    NodeVisual nodeVisual = oldVisual instanceof NodeVisual ? (NodeVisual) oldVisual : null;
    if (nodeVisual == null) {
      return createVisual(context, node);
    }
    IVisual v = nodeVisual.delegate;
    double oldZoom = nodeVisual.zoom;
    if (styles.hasSameStyle(context.getZoom(), oldZoom)) {
      INodeStyle style = styles.getStyle(oldZoom);
      v = style.getRenderer().getVisualCreator(node, style).updateVisual(context, v);
    } else {
      INodeStyle style = styles.getStyle(context.getZoom());
      v = style.getRenderer().getVisualCreator(node, style).createVisual(context);
    }
    nodeVisual.delegate = v;
    nodeVisual.zoom = context.getZoom();
    return v;
  }

  // region Delegate methods

  @Override
  protected RectD getBounds(ICanvasContext context, INode node) {
    INodeStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getBoundsProvider(node, style).getBounds(context);
  }

  @Override
  protected boolean isVisible(ICanvasContext context, RectD rectangle, INode node) {
    INodeStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getVisibilityTestable(node, style).isVisible(context, rectangle);
  }

  @Override
  protected boolean isHit(IInputModeContext context, PointD location, INode node) {
    INodeStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getHitTestable(node, style).isHit(context, location);
  }

  @Override
  protected boolean isInBox(IInputModeContext context, RectD rectangle, INode node) {
    INodeStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getMarqueeTestable(node, style).isInBox(context, rectangle);
  }


  @Override
  protected PointD getIntersection(INode node, PointD inner, PointD outer) {
    INodeStyle mostDetailedStyle = styles.getStyle(Double.POSITIVE_INFINITY);
    return mostDetailedStyle.getRenderer().getShapeGeometry(node, mostDetailedStyle).getIntersection(inner, outer);
  }

  @Override
  protected boolean isInside(INode node, PointD location) {
    INodeStyle mostDetailedStyle = styles.getStyle(Double.POSITIVE_INFINITY);
    return mostDetailedStyle.getRenderer().getShapeGeometry(node, mostDetailedStyle).isInside(location);
  }

  @Override
  protected GeneralPath getOutline(INode node) {
    INodeStyle mostDetailedStyle = styles.getStyle(Double.POSITIVE_INFINITY);
    return mostDetailedStyle.getRenderer().getShapeGeometry(node, mostDetailedStyle).getOutline();
  }

  // endregion

  private static class NodeVisual implements IVisual {
    private IVisual delegate;
    private double zoom;

    public NodeVisual(IVisual delegate, double zoom) {
      this.delegate = delegate;
      this.zoom = zoom;
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      if (delegate != null) {
        delegate.paint(context, g);
      }
    }
  }
}
