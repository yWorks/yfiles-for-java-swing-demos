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
package viewer.largegraphs.styles.levelofdetail;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.Tangent;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Graphics2D;

/**
 * Level-of-detail style for edges that delegates to different {@link IEdgeStyle}s depending on the zoom level.
 */
public class LevelOfDetailEdgeStyle extends AbstractEdgeStyle {

  // the style container
  private LevelOfDetailStyleContainer<IEdgeStyle> styles = new LevelOfDetailStyleContainer<>();

  /**
   * Gets the style container.
   * <p>
   * Styles have to be added in ascending order of zoom level.
   * </p>
   */
  public LevelOfDetailStyleContainer<IEdgeStyle> getStyles() {
    return styles;
  }

  @Override
  protected IVisual createVisual(IRenderContext context, IEdge edge) {
    IEdgeStyle style = styles.getStyle(context.getZoom());

    IVisual visual = style.getRenderer().getVisualCreator(edge, style).createVisual(context);
    return new EdgeVisual(visual, context.getZoom());
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, IEdge edge) {
    EdgeVisual edgeVisual = oldVisual instanceof EdgeVisual ? (EdgeVisual) oldVisual : null;
    if (edgeVisual == null) {
      return createVisual(context, edge);
    }

    IVisual v = edgeVisual.delegate;
    double oldZoom = edgeVisual.zoom;
    if (styles.hasSameStyle(context.getZoom(), oldZoom)) {
      IEdgeStyle style = styles.getStyle(oldZoom);
      v = style.getRenderer().getVisualCreator(edge, style).updateVisual(context, v);
    } else {
      IEdgeStyle style = styles.getStyle(context.getZoom());
      v = style.getRenderer().getVisualCreator(edge, style).createVisual(context);
    }
    edgeVisual.delegate = v;
    edgeVisual.zoom = context.getZoom();
    return edgeVisual;
  }

  // region Delegating Methods

  @Override
  protected RectD getBounds(ICanvasContext context, IEdge edge) {
    IEdgeStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getBoundsProvider(edge, style).getBounds(context);
  }

  @Override
  protected boolean isVisible(ICanvasContext context, RectD rectangle, IEdge edge) {
    IEdgeStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getVisibilityTestable(edge, style).isVisible(context, rectangle);
  }

  @Override
  protected boolean isHit(IInputModeContext context, PointD location, IEdge edge) {
    IEdgeStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getHitTestable(edge, style).isHit(context, location);
  }

  @Override
  protected boolean isInBox(IInputModeContext context, RectD rectangle, IEdge edge) {
    IEdgeStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getMarqueeTestable(edge, style).isInBox(context, rectangle);
  }

  @Override
  protected Tangent getTangent(IEdge edge, double ratio) {
    IEdgeStyle mostDetailedStyle = styles.getStyle(Double.POSITIVE_INFINITY);
    return mostDetailedStyle.getRenderer().getPathGeometry(edge, mostDetailedStyle).getTangent(ratio);
  }

  @Override
  protected Tangent getTangent(IEdge edge, int segmentIndex, double ratio) {
    IEdgeStyle mostDetailedStyle = styles.getStyle(Double.POSITIVE_INFINITY);
    return mostDetailedStyle.getRenderer().getPathGeometry(edge, mostDetailedStyle).getTangent(segmentIndex, ratio);
  }

  @Override
  protected GeneralPath getPath(IEdge edge) {
    IEdgeStyle mostDetailedStyle = styles.getStyle(Double.POSITIVE_INFINITY);
    return mostDetailedStyle.getRenderer().getPathGeometry(edge, mostDetailedStyle).getPath();
  }

  @Override
  protected int getSegmentCount(IEdge edge) {
    IEdgeStyle mostDetailedStyle = styles.getStyle(Double.POSITIVE_INFINITY);
    return mostDetailedStyle.getRenderer().getPathGeometry(edge, mostDetailedStyle).getSegmentCount();
  }

  private static class EdgeVisual implements IVisual {
    private IVisual delegate;
    private double zoom;

    public EdgeVisual(IVisual delegate, double zoom) {
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


  // endregion
}
