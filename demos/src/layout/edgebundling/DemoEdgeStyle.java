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
package layout.edgebundling;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.AbstractEdgeStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws edges with cubic bezier curves.
 * Also, edges are drawn with gradient colors that start at the source node of
 * the edge and end at the target node of the edge.
 */
class DemoEdgeStyle extends AbstractEdgeStyle {
  private final float pathThickness;
  private final Color start;
  private final Color end;

  /**
   * Initializes a new {@link DemoEdgeStyle} instance with thickness {@code 2}
   * and colors {@link Colors#DARK_BLUE} and {@link Colors#SKY_BLUE}.
   */
  DemoEdgeStyle() {
    this(2, Colors.DARK_BLUE, Colors.SKY_BLUE);
  }

  /**
   * Initializes a new {@link DemoEdgeStyle} instance with the given
   * thickness and colors.
   */
  DemoEdgeStyle( float pathThickness, Color start, Color end ) {
    this.pathThickness = pathThickness;
    this.start = start;
    this.end = end;
  }

  @Override
  protected IVisual createVisual( IRenderContext context, IEdge edge ) {
    boolean selected = isSelected(context, edge);

    BezierPathVisual visual = new BezierPathVisual();
    visual.controlPoints = calculatePoints(edge);
    visual.selected = selected;
    visual.pathThickness = pathThickness;
    visual.start = start;
    visual.end = end;
    visual.gradientColors = generateColors(start, end);
    visual.selectionColors = generateColors(Color.RED, Colors.GOLD);
    return visual;
  }

  @Override
  protected IVisual updateVisual( IRenderContext context, IVisual oldVisual, IEdge edge ) {
    if (oldVisual instanceof BezierPathVisual) {
      BezierPathVisual bpv = (BezierPathVisual) oldVisual;
      if (!start.equals(bpv.start) || !end.equals(bpv.end)) {
        bpv.start = start;
        bpv.end = end;
        bpv.gradientColors = generateColors(start, end);
        bpv.selectionColors = generateColors(Color.RED, Colors.GOLD);
      }
      bpv.controlPoints = calculatePoints(edge);
      bpv.selected = isSelected(context, edge);
      bpv.pathThickness = pathThickness;
      return bpv;
    }
    return createVisual(context, edge);
  }

  /**
   * Creates a {@link com.yworks.yfiles.geometry.GeneralPath} from the edge's bends.
   */
  protected GeneralPath getPath( IEdge edge) {
    // Create a general path from the locations of the ports and the bends of the edge.
    GeneralPath path = new GeneralPath();
    if (edge.getBends().size() > 1) {
      List<PointD> controlPoints = calculateControlPoints(edge);
      path.moveTo(edge.getSourcePort().getLocation());
      for (int i = 0; i < controlPoints.size() - 3; i += 3) {
        path.cubicTo(controlPoints.get(i + 1), controlPoints.get(i + 2), controlPoints.get(i + 3));
      }
    } else {
      path.moveTo(edge.getSourcePort().getLocation());
      for (IBend bend : edge.getBends()) {
        path.lineTo(bend.getLocation().getX(), bend.getLocation().getY());
      }
      path.lineTo(edge.getTargetPort().getLocation());
    }
    return path;
  }

  private static List<PointD> calculatePoints( final IEdge edge ) {
    IListEnumerable<IBend> bends = edge.getBends();
    if (bends.size() > 1) {
      return calculateControlPoints(edge);
    } else {
      ArrayList<PointD> points = new ArrayList<>();
      points.add(edge.getSourcePort().getLocation());
      for (IBend bend : bends) {
        points.add(new PointD(bend.getLocation()));
      }
      points.add(edge.getTargetPort().getLocation());
      return points;
    }
  }

  /**
   * Calculates the bezier control points for the given edge.
   */
  private static List<PointD> calculateControlPoints( IEdge edge ) {
    ArrayList<PointD> controlPoints = new ArrayList<>();
    // add the source port
    controlPoints.add(edge.getSourcePort().getLocation());
    // add all edge bends
    for (IBend bend : edge.getBends()) {
      controlPoints.add(new PointD(bend.getLocation()));
    }
    // add the target port
    controlPoints.add(edge.getTargetPort().getLocation());

    // check if the control points can create piecewise bezier curves, if not duplicate the target port
    if (controlPoints.size() % 3 == 0) {
      controlPoints.add(edge.getTargetPort().getLocation());
    } else if (controlPoints.size() % 3 == 2) {
      controlPoints.add(edge.getTargetPort().getLocation());
      controlPoints.add(edge.getTargetPort().getLocation());
    }
    return controlPoints;
  }

  /**
   * Generates gradient colors between the two given colors.
   */
  private static Color[] generateColors( Color startColor, Color endColor ) {
    int gradient = 25;
    Color[] colors = new Color[gradient];
    int stepCount = gradient - 1;

    for (int i = 0; i < gradient; ++i) {
      int r = ((endColor.getRed()   * (stepCount - i)) + (startColor.getRed()   * i)) / stepCount;
      int g = ((endColor.getGreen() * (stepCount - i)) + (startColor.getGreen() * i)) / stepCount;
      int b = ((endColor.getBlue()  * (stepCount - i)) + (startColor.getBlue()  * i)) / stepCount;
      int a = ((endColor.getAlpha() * (stepCount - i)) + (startColor.getAlpha() * i)) / stepCount;
      colors[i] = new Color(r, g, b, a);
    }
    return colors;
  }

  /**
   * Determines if the given edge is selected in the given context.
   */
  private static boolean isSelected( IRenderContext context, IEdge edge ) {
    CanvasComponent component = context.getCanvasComponent();
    if (component instanceof GraphComponent) {
      IGraphSelection selection = ((GraphComponent) component).getSelection();
      return selection.isSelected(edge);
    }
    return false;
  }


  /**
   * Renders bazier paths with multiple colors.
   */
  private static final class BezierPathVisual implements IVisual {
    Color start;
    Color end;
    List<PointD> controlPoints;
    float pathThickness;
    boolean selected;

    Color[] gradientColors;
    Color[] selectionColors;

    @Override
    public void paint( IRenderContext context, Graphics2D g ) {
      Color oldColor = g.getColor();
      Stroke oldStroke = g.getStroke();
      g.setStroke(selected ? newStroke(pathThickness) : newStroke(5));
      Color[] colors = selected ? selectionColors : gradientColors;

      List<PointD> points = controlPoints;
      if (points.size() > 3) {
        // for each pairwise bezier curve create a path that will have a different gradient fill color
        Path2D path = new Path2D.Double();
        PointD lastPoint = points.get(0);
        for (int i = 1, n = points.size(); i < n; i += 3) {
          PointD p1 = points.get(i);
          PointD p2 = points.get(i + 1);
          PointD p3 = points.get(i + 2);

          path.reset();
          path.moveTo(lastPoint.getX(), lastPoint.getY());
          path.curveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());

          int colorIndex = (i * (colors.length - 1)) / (n - 1);
          g.setColor(colors[colorIndex]);
          g.draw(path);

          lastPoint = p3;
        }
      } else {
        // we want to have gradient colors starting with light blue and ending with dark blue
        // (or starting with red and ending with gold if the edge is selected) 
        // split the path in lines and assign each line a different color from the gradient

        // if the edge is straight-line, split in two parts such that one is
        // light blue (red) and one is dark blue (gold)
        PointD first = points.get(0);
        PointD last = points.get(points.size() - 1);
        PointD midPoint = points.size() > 2 ? points.get(1)
                : new PointD((first.x + last.x) * 0.5, (first.y + last.y) * 0.5);
        
        Line2D.Double line = new Line2D.Double();
        line.setLine(first.x, first.y, midPoint.x, midPoint.y);
        g.setColor(colors[0]);
        g.draw(line);
        line.setLine(midPoint.x, midPoint.y, last.x, last.y);
        g.setColor(colors[colors.length - 1]);
        g.draw(line);
      }

      g.setStroke(oldStroke);
      g.setColor(oldColor);
    }

    private static BasicStroke newStroke(final float pathThickness) {
      return new BasicStroke(pathThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    }

  }
}
