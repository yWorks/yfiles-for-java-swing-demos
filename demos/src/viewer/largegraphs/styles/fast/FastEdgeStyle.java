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
package viewer.largegraphs.styles.fast;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import viewer.largegraphs.styles.levelofdetail.LevelOfDetailLabelStyle;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.Arrays;

/**
 * A faster edge style.
 * <p>
 * This style offers three main optimizations compared to the default {@link com.yworks.yfiles.graph.styles.PolylineEdgeStyle}:
 * <ul>
 * <li>The edge is not clipped at node boundaries.</li>
 * <li>Bends are not drawn below a configurable zoom level.</li>
 * <li>Edges are hidden completely if they are shorter than a given number of pixels on screen.</li>
 * </ul>
 * </p>
 * <p>
 * When an edge has labels, bends should not be hidden as long as the edge labels are visible. This is because edge
 * labels are attached to the conceptual edge path, which includes bends. If bends are not drawn, edge labels may look
 * out of place or even far away from the actually displayed edge path. Using {@link LevelOfDetailLabelStyle} a suitable
 * zoom level at which to display labels can easily be configured.
 * </p>
 */
public class FastEdgeStyle extends AbstractEdgeStyle {

  /**
   * Initializes a new instance of the {@link FastEdgeStyle} class with default settings.
   * <p>
   * By default bends are not drawn below a zoom level of 50&nbsp;% and edges shorter than 10 pixels are hidden.
   * </p>
   */
  public FastEdgeStyle() {
    drawBendsThreshold = 0.5;
    minimumEdgeLength = 10;
  }

  private double drawBendsThreshold;

  /**
   * Gets the minimum zoom level at which bends are drawn.
   * <p>
   * Below this zoom level the edge is only drawn as a single line between its source and target ports.
   * </p>
   */
  public double getDrawBendsThreshold() {
    return drawBendsThreshold;
  }

  /**
   * Sets the minimum zoom level at which bends are drawn.
   * <p>
   * Below this zoom level the edge is only drawn as a single line between its source and target ports.
   * </p>
   */
  public void setDrawBendsThreshold(double drawBendsThreshold) {
    this.drawBendsThreshold = drawBendsThreshold;
  }

  private double minimumEdgeLength;

  /**
   * Gets the minimum length (in pixels on screen) where edges will still be drawn.
   * <p>
   * All edges where the distance between source and target port is shorter than this will not be displayed.
   * </p>
   */
  public double getMinimumEdgeLength() {
    return minimumEdgeLength;
  }

  /**
   * Sets the minimum length (in pixels on screen) where edges will still be drawn.
   * <p>
   * All edges where the distance between source and target port is shorter than this will not be displayed.
   * </p>
   */
  public void setMinimumEdgeLength(double minimumEdgeLength) {
    this.minimumEdgeLength = minimumEdgeLength;
  }


  public FastEdgeStyle(double drawBendsThreshold, double minimumEdgeLength) {
    this.drawBendsThreshold = drawBendsThreshold;
    this.minimumEdgeLength = minimumEdgeLength;
  }

  @Override
  protected IVisual createVisual(IRenderContext context, IEdge edge) {
    PointD source = edge.getSourcePort().getLocation();
    PointD target = edge.getTargetPort().getLocation();
    double zoom = context.getZoom();
    if (!shouldDrawEdge(source, target, zoom)) {
      return null;
    }

    boolean drawBends = shouldDrawBends(zoom);
    return new EdgeVisual(source, target, drawBends, getBendLocations(edge));
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, IEdge edge) {
    EdgeVisual edgeVisual = oldVisual instanceof EdgeVisual ? ((EdgeVisual) oldVisual) : null;
    if (edgeVisual == null) {
      return createVisual(context, edge);
    }

    PointD source = edge.getSourcePort().getLocation();
    PointD target = edge.getTargetPort().getLocation();
    double zoom = context.getZoom();

    if (!shouldDrawEdge(source, target, zoom)) {
      return null;
    }

    boolean drawBends = shouldDrawBends(zoom);
    PointD[] bendLocations = getBendLocations(edge);

    // Did anything change at all? If not, we can just re-use the old visual
    if (source.equals(edgeVisual.source) &&
        target.equals(edgeVisual.target) &&
        drawBends == edgeVisual.drawBends &&
        Arrays.equals(bendLocations, edgeVisual.bendLocations)) {
      return oldVisual;
    }

    // Otherwise re-create the EdgeVisual
    return new EdgeVisual(source, target, drawBends, bendLocations);
  }

  @Override
  protected RectD getBounds(ICanvasContext context, IEdge edge) {
    double zoom = context.getZoom();
    if (zoom >= getDrawBendsThreshold()) {
      return super.getBounds(context, edge);
    }
    PointD source = edge.getSourcePort().getLocation();
    PointD target = edge.getTargetPort().getLocation();
    if (shouldDrawEdge(source, target, zoom)) {
      return new RectD(source, target);
    }
    return RectD.EMPTY;
  }



  /**
   * Determines whether the edge should be drawn at all, taking into account the value of the {@link
   * #getMinimumEdgeLength()} property.
   *
   * @param source The source port location.
   * @param target The target port location.
   * @param zoom   The current zoom level.
   * @return <code>true</code>, if the edge should be drawn, <code>false</code> otherwise.
   */
  private boolean shouldDrawEdge(PointD source, PointD target, double zoom) {
    double dx = (source.getX() - target.getX()) * zoom;
    double dy = (source.getY() - target.getY()) * zoom;

    // Minor optimization: Avoid square root
    double distSquared = dx * dx + dy * dy;
    return distSquared >= getMinimumEdgeLength() * getMinimumEdgeLength();
  }

  /**
   * Determines whether bends should be drawn, according to the value of the {@link #getDrawBendsThreshold()}
   * property.
   *
   * @param zoom The current zoom level.
   * @return <code><true/code>, if bends should be drawn, <code>false</code> if not.
   */
  private boolean shouldDrawBends(double zoom) {
    return zoom >= getDrawBendsThreshold();
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


  /**
   * Helper structure to keep information about the edge.
   */
  private static class EdgeVisual implements IVisual {

    private static final BasicStroke STROKE = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
    private static final Line2D.Double line = new Line2D.Double();

    /**
     * A list of bend locations in the edge.
     */
    public PointD[] bendLocations;

    /**
     * A flag determining whether bends should be drawn or not.
     */
    public boolean drawBends;

    /**
     * The source port location.
     */
    public PointD source;

    /**
     * The target port location.
     */
    public PointD target;

    /**
     * Initializes a new instance of the EdgeInfo structure, using the given source and target port locations, whether
     * to draw bends or not and the given list of bend locations.
     *
     * @param source        The source port location.
     * @param target        The target port location.
     * @param drawBends     A flag determining whether bends should be drawn or not.
     * @param bendLocations A list of bend locations.
     */
    public EdgeVisual(PointD source, PointD target, boolean drawBends, PointD[] bendLocations) {
      this.source = source;
      this.target = target;
      this.drawBends = drawBends;
      this.bendLocations = bendLocations;
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      g.setPaint(Colors.BLACK);
      g.setStroke(STROKE);

      PointD last = source;
      if (drawBends) {
        for (PointD bend : bendLocations) {
          line.setLine(last.getX(), last.getY(), bend.getX(), bend.getY());
          g.draw(line);
          last = bend;
        }
      }
      line.setLine(last.getX(), last.getY(), target.getX(), target.getY());
      g.draw(line);
    }
  }
}