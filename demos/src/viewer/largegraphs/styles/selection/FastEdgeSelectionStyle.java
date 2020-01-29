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
package viewer.largegraphs.styles.selection;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

/**
 * Edge style that is used as a zoom-invariant selection decorator.
 * <p>
 * This style essentially displays a path along the edge and scales its stroke thickness and brush by 1 / zoom level.
 * This means that positioning considerations can still be done in world coordinates and the path doesn't require a
 * series of transformations to end up where it should be. The brush is scaled because the default selection
 * decoration uses a pixel checkerboard pattern which would otherwise be scaled with the zoom level.
 * </p>
 * <p>
 * This style caches the scaled stroke color to avoid creating a new color for every invocation of
 * {@link #updateVisual(IRenderContext, IVisual, IEdge)}.
 * </p>
 */
public class FastEdgeSelectionStyle extends AbstractEdgeStyle {

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

  /**
   * Initializes a new instance with the given pen.
   * @param pen The pen used to draw the path.
   */
  public FastEdgeSelectionStyle(Pen pen) {
    this.pen = pen;
  }

  // region Style

  @Override
  protected IVisual createVisual(IRenderContext context, IEdge edge) {
    double scale = 1 / context.getZoom();
    PointD n1 = edge.getSourcePort().getLocation();
    PointD n2 = edge.getTargetPort().getLocation();

    EdgeVisual edgeVisual = new EdgeVisual(n1, n2, getBendLocations(edge), getPen());
    edgeVisual.updatePen(scale);
    return edgeVisual;
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, IEdge edge) {
    if (!(oldVisual instanceof EdgeVisual)) {
      return createVisual(context, edge);
    }

    PointD source = edge.getSourcePort().getLocation();
    PointD target = edge.getTargetPort().getLocation();
    PointD[] bendLocations = getBendLocations(edge);

    EdgeVisual edgeVisual = (EdgeVisual) oldVisual;

    if (!source.equals(edgeVisual.source) ||
        !target.equals(edgeVisual.target) ||
        !Arrays.equals(bendLocations, edgeVisual.bendLocations)) {
      edgeVisual = new EdgeVisual(source, target, bendLocations, getPen());
    }
    edgeVisual.updatePen(1 / context.getZoom());

    return edgeVisual;
  }

  /**
   * Gets a list of bend locations from an edge.
   *
   * @param edge The edge.
   * @return A list of the edge's bend locations, or an empty list if there are no bends.
   */
  private static PointD[] getBendLocations(IEdge edge) {
    IListEnumerable<IBend> bends = edge.getBends();
    int count = bends.size();
    PointD[] points = new PointD[count];
    for (int i = 0; i < count; i++) {
      points[i] = bends.getItem(i).getLocation().toPointD();
    }
    return points;
  }

  // endregion

  /**
   * Helper structure to keep information about the edge.
   */
  private static class EdgeVisual implements IVisual {
    private static Path2D.Double path = new Path2D.Double();

    /**
     * A list of bend locations in the edge.
     */
    public PointD[] bendLocations;

    /**
     * The source port location.
     */
    public PointD source;

    /**
     * The target port location.
     */
    public PointD target;

    private Pen pen;

    private Pen scaledPen;

    /**
     * Initializes a new instance of the EdgeInfo structure, using the given source and target port locations
     * and the given list of bend locations.
     *  @param source        The source port location.
     * @param target        The target port location.
     * @param bendLocations A list of bend locations.
     * @param pen
     */
    public EdgeVisual(PointD source, PointD target, PointD[] bendLocations, Pen pen) {
      this.source = source;
      this.target = target;
      this.bendLocations = bendLocations;
      this.pen = pen;
      if (pen != null) {
        scaledPen = pen.cloneCurrentValue();
      }
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      if (scaledPen == null) {
        return;
      }
      scaledPen.commit(g);

      path.reset();
      path.moveTo(source.getX(), source.getY());
      for (PointD bend : bendLocations) {
        path.lineTo(bend.getX(), bend.getY());
      }
      path.lineTo(target.getX(), target.getY());
      g.draw(path);
    }


    /**
     * Re-creates the scaled stroke brush if necessary and sets it on the rectangle.
     * @param scale The scale. This is 1 / zoom level.
     */
    private void updatePen(double scale) {
      if (pen != null) {
        double strokeWidth = pen.getThickness() * scale;
        scaledPen.setThickness(strokeWidth);
      }
    }

  }
}
