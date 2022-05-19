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
package viewer.largegraphs.styles.selection;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * Node style that is used as a zoom-invariant selection decorator.
 * <p>
 * This style essentially displays a rectangle and scales its stroke thickness and brush by 1&nbsp;/&nbsp;zoom level.
 * This means that positioning considerations can still be done in world coordinates and the path doesn't require a
 * series of transformations to end up where it should be. The brush is scaled because the default selection
 * decoration uses a pixel checkerboard pattern which would otherwise be scaled with the zoom level.
 * </p>
 * <p>
 * This style caches the scaled stroke color to avoid creating a new color for every invocation of
 * {@link #updateVisual(IRenderContext, IVisual, INode)}.
 * </p>
 */
public class FastNodeSelectionStyle extends AbstractNodeStyle {

  // region Properties

  private Color fill;

  /**
   * Gets the color used to fill the selection rectangle.
   */
  public Color getFill() {
    return fill;
  }

  /**
   * Sets the color used to fill the selection rectangle.
   */
  public void setFill(Color fill) {
    this.fill = fill;
  }

  private Pen pen;

  /**
   * Gets the pen used to draw the rectangle outline.
   */
  public Pen getPen() {
    return pen;
  }

  /**
   * Sets the pen used to draw the rectangle outline.
   */
  public void setPen(Pen pen) {
    this.pen = pen;
  }

  // endregion

  /**
   * Initializes a new instance with the given fill and pen.
   * @param fill The color used to fill the selection rectangle.
   * @param pen The pen used to draw the rectangle outline.
   */
  public FastNodeSelectionStyle(Color fill, Pen pen) {
    this.fill = fill;
    this.pen = pen;
  }

  // region Style

  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    double scale = 1 / context.getZoom();
    RectD layout = getSelectionBounds(node, scale);

    NodeVisual nodeVisual = new NodeVisual(layout, getFill(), getPen());
    nodeVisual.updatePen(scale);
    return nodeVisual;
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (!(oldVisual instanceof NodeVisual)) {
      return createVisual(context, node);
    }
    double scale = 1 / context.getZoom();
    RectD layout = getSelectionBounds(node, scale);

    NodeVisual nodeVisual = (NodeVisual) oldVisual;

    if (!layout.equals(nodeVisual.layout)) {
      nodeVisual = new NodeVisual(layout, getFill(), getPen());
    }
    nodeVisual.updatePen(1 / context.getZoom());
    return nodeVisual;
  }

  /**
   * Returns the size and position of the selection rectangle around a node.
   * @param node The node.
   * @param scale The scale. This is 1&nbsp;/&nbsp;zoom level.
   * @return The selection rectangle layout, enlarged by the scaled stroke thickness.
   */
  private RectD getSelectionBounds(INode node, double scale) {
    RectD layout = node.getLayout().toRectD().getEnlarged(getPen().getThickness() * scale);
    return layout;
  }

  // endregion

  /**
   * Helper structure to keep information about the node.
   */
  private static class NodeVisual implements IVisual {
    private Rectangle2D.Double rect2D = new Rectangle2D.Double();

    private RectD layout;

    private Color fill;

    private Pen pen;
    private Pen scaledPen;

    public NodeVisual(RectD layout, Color fill, Pen pen) {
      this.layout = layout;
      this.fill = fill;
      this.pen = pen;
      if (pen != null) {
        scaledPen = pen.cloneCurrentValue();
      }
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      rect2D.setRect(layout.getX(), layout.getY(), layout.getWidth(), layout.getHeight());
      if (fill != null) {
        g.setPaint(fill);
        g.fill(rect2D);
      }
      if (scaledPen != null) {
        scaledPen.commit(g);
        g.draw(rect2D);
      }
    }


    /**
     * Updates the scaled pen.
     * @param scale The scale. This is 1&nbsp;/&nbsp;zoom level.
     */
    private void updatePen(double scale) {
      if (pen != null) {
        double strokeWidth = pen.getThickness() * scale;
        scaledPen.setThickness(strokeWidth);
      }
    }
  }
}
