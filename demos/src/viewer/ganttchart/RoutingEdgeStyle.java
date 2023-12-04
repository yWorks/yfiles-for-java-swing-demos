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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.Tangent;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.IEdgeStyleRenderer;
import com.yworks.yfiles.graph.styles.PathBasedEdgeStyleRenderer;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Draws an edge in a pre-determined orthogonal fashion.
 * All existing bends of the edge are ignored.
 */
public class RoutingEdgeStyle implements IEdgeStyle {
  private int outSegmentLength;
  private int inSegmentLength;
  private int middleSegmentOffset;
  private int smoothing;
  private Pen pen;
  private IArrow tgtArrow;
  private IArrow srcArrow;

  /**
   * Initializes a new {@code RoutingEdgeStyle} instance.
   */
  public RoutingEdgeStyle() {
    this(20, 10);
  }

  /**
   * Initializes a new {@code RoutingEdgeStyle} instance.
   * @param outSegmentLength The length of the horizontal segment that connects to the source node.
   * @param inSegmentLength The length of the horizontal segment that connects to the target node.
   */
  public RoutingEdgeStyle( int outSegmentLength, int inSegmentLength ) {
    this(outSegmentLength, inSegmentLength, new Color(100, 100, 100), 2);
  }

  /**
   * Initializes a new {@code RoutingEdgeStyle} instance.
   * @param outSegmentLength The length of the horizontal segment that connects to the source node.
   * @param inSegmentLength The length of the horizontal segment that connects to the target node.
   */
  public RoutingEdgeStyle( int outSegmentLength, int inSegmentLength, Color fill, int thickness) {
    this.inSegmentLength = inSegmentLength;
    this.outSegmentLength = outSegmentLength;
    this.middleSegmentOffset = 32;
    this.smoothing = 10;
    this.pen = new Pen(fill, thickness);
    this.srcArrow = Arrow.NONE;
    this.tgtArrow = new Arrow(ArrowType.TRIANGLE, fill);
  }

  /**
   * Gets the length of the horizontal segment that connects to the source node.
   */
  public int getOutSegmentLength() {
    return outSegmentLength;
  }

  /**
   * Sets the length of the horizontal segment that connects to the source node.
   */
  public void setOutSegmentLength( int outSegmentLength ) {
    this.outSegmentLength = outSegmentLength;
  }

  /**
   * Gets the length of the horizontal segment that connects to the target node.
   */
  public int getInSegmentLength() {
    return inSegmentLength;
  }

  /**
   * Sets the length of the horizontal segment that connects to the target node.
   */

  public void setInSegmentLength( int inSegmentLength ) {
    this.inSegmentLength = inSegmentLength;
  }

  /**
   * Gets the vertical distance between the source port and the horizontal middle segment.
   * This only has an effect when the source location is right of the target location.
   */

  public int getMiddleSegmentOffset() {
    return middleSegmentOffset;
  }

  /**
   * Sets the vertical distance between the source port and the horizontal middle segment.
   * This only has an effect when the source location is right of the target location.
   */
  public void setMiddleSegmentOffset( int middleSegmentOffset ) {
    this.middleSegmentOffset = middleSegmentOffset;
  }

  /**
   * Gets the amount of corner rounding.
   */

  public int getSmoothing() {
    return smoothing;
  }

  /**
   * Sets the amount of corner rounding.
   */
  public void setSmoothing( int smoothing ) {
    this.smoothing = smoothing;
  }

  /**
   * Gets the source arrow.
   */
  public IArrow getSrcArrow() {
    return srcArrow;
  }

  /**
   * Sets the source arrow.
   */
  public void setSrcArrow( IArrow srcArrow ) {
    this.srcArrow = srcArrow;
  }

  /**
   * Gets the target arrow.
   */
  public IArrow getTgtArrow() {
    return tgtArrow;
  }

  /**
   * Sets the target arrow.
   */
  public void setTgtArrow( IArrow tgtArrow ) {
    this.tgtArrow = tgtArrow;
  }

  /**
   * Gets the Pen used to draw the edge.
   */
  public Pen getPen() {
    return pen;
  }

  /**
   * Sets the Pen used to draw the edge.
   */
  public void setPen( Pen pen ) {
    this.pen = pen;
  }

  /**
   * @return {@link RoutingEdgeStyleRenderer}
   */
  @Override
  public IEdgeStyleRenderer getRenderer() {
    return new RoutingEdgeStyleRenderer();
  }

  /**
   * @return {@link RoutingEdgeStyleRenderer}
   */
  @Override
  public Object clone() {
    return new RoutingEdgeStyle(outSegmentLength, inSegmentLength, null, -1);
  }



  private static class RoutingEdgeStyleRenderer extends PathBasedEdgeStyleRenderer<RoutingEdgeStyle> {
    /**
     * Initializes a new {@code RoutingEdgeStyleRenderer} instance.
     */
    RoutingEdgeStyleRenderer() {
      super(RoutingEdgeStyle.class);
    }

    /**
     * Calculates the points that define the edge path.
     * @return A list of points that define the edge path.
     */
    private List<PointD> getEdgePoints( IEdge edge ){
      PointD srcLocation = edge.getSourcePort().getLocation();
      PointD tgtLocation = edge.getTargetPort().getLocation();

      ArrayList<PointD> points = new ArrayList<PointD>();
      points.add(srcLocation);

      // the source location with the x-offset
      double srcX = srcLocation.getX() + this.getStyle().getOutSegmentLength();
      // the target location with the x-offset
      double tgtX = tgtLocation.getX() - this.getStyle().getInSegmentLength();

      // check if source and target are not exactly in the same row - in this case we just draw a straight line
      if (srcX > tgtX) {
        // source is right of target
        // get the y-coordinate of the vertical middle segment
        double middleSegmentY = srcLocation.getY() <= tgtLocation.getY()
          ? srcLocation.getY() + getStyle().getMiddleSegmentOffset()
          : srcLocation.getY() - getStyle().getMiddleSegmentOffset();
        points.add(new PointD(srcX, srcLocation.getY()));
        points.add(new PointD(srcX, middleSegmentY));
        points.add(new PointD(tgtX, middleSegmentY));
        points.add(new PointD(tgtX, tgtLocation.getY()));
      } else {
        if (srcLocation.getY() != tgtLocation.getY()) {
          // source is left of target
          points.add(new PointD(srcX, srcLocation.getY()));
          points.add(new PointD(srcX, tgtLocation.getY()));
        }
      }
      points.add(tgtLocation);
      return points;
    }

    /**
     * Constructs the orthogonal edge path.
     */
    @Override
    protected GeneralPath createPath() {
      // create a new GeneralPath with the edge points
      GeneralPath generalPath = new GeneralPath();
      Iterator<PointD> points = getEdgePoints(getEdge()).iterator();
      if (points.hasNext()) {
        generalPath.moveTo(points.next());
      }
      while (points.hasNext()) {
        generalPath.lineTo(points.next());
      }
      return generalPath;
    }

    /**
     * Get the tangent on this path at the given ratio.
     */
    @Override
    public Tangent getTangent( double ratio ) {
      return getPath().getTangent(ratio);
    }

    /**
     * Gets the tangent on this path instance at the segment and segment ratio.
     */
    @Override
    public Tangent getTangent( int segmentIndex, double ratio ) {
      return getPath().getTangent(segmentIndex, ratio);
    }

    /**
     * Get the segment count which is the number of edge points -1.
     */
    @Override
    public int getSegmentCount() {
      return getEdgePoints(getEdge()).size() - 1;
    }

    /**
     * Get the target arrow.
     */
    @Override
    protected IArrow getTargetArrow() {
      return getStyle().getTgtArrow();
    }

    /**
     * Get the source arrow.
     */
    @Override
    protected IArrow getSourceArrow() {
      return getStyle().getSrcArrow();
    }

    /**
     * Get the pen used by style.
     */
    @Override
    protected Pen getPen() {
      return getStyle().getPen();
    }
  }
}
