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

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/**
 * Draws nodes in a circular-sector style.
 */
class DemoNodeStyle extends AbstractNodeStyle {
  private final Color color;

  /**
   * Initializes a new {@link DemoNodeStyle} instance with the color
   * {@link Colors#DARK_ORANGE}.
   */
  DemoNodeStyle() {
    this(Colors.DARK_ORANGE);
  }

  /**
   * Initializes a new {@link DemoNodeStyle} instance with the given color.
   */
  DemoNodeStyle( Color color ) {
    this.color = color;
  }

  /**
   * Creates the visual for a node.
   */
  @Override
  protected IVisual createVisual( IRenderContext context, INode node ) {
    return new ShapeVisual(getVisualShape(node), Pen.getWhite(), color);
  }

  /**
   * Determines whether the visual representation of the node has been hit at the given location.
   */
  @Override
  protected boolean isHit( IInputModeContext context, PointD location, INode node ) {
    Shape shape = getVisualShape(node);
    return shape.contains(location.getX(), location.getY());
  }

  /**
   * Calculates the shape of the node as a general path.
   */
  private static Shape getVisualShape( INode node ) {
    IRectangle nl = node.getLayout();
    DemoNodeStyleData tag =
            node.getTag() instanceof DemoNodeStyleData
            ? (DemoNodeStyleData) node.getTag() : null;
    if (tag != null && tag.id != null && tag.center != null) {
      double x = nl.getX() + nl.getWidth() * 0.5;
      double y = nl.getY() + nl.getHeight() * 0.5;

      // determine the circle's center
      PointD center = tag.center;
      // calculate the circle's radius
      double dx = x - center.getX();
      double dy = y - center.getY();
      double radius = Math.sqrt(dx * dx + dy * dy);

      // calculate the node's angle
      double angle = (Math.atan2(dy, dx) * 180) / Math.PI;
      if (angle < 0) {
        angle += 360;
      }
      // calculate how much space there exists for each node
      double offset = (360.0 - tag.nodes.size()) / tag.nodes.size();
      double startAngleInDegrees = angle - (offset * 0.5);
      double endAngleInDegrees = startAngleInDegrees + offset;
      double startAngle = (Math.PI * startAngleInDegrees) / 180;
      double endAngle = (Math.PI * endAngleInDegrees) / 180;

      double innerRadius = radius;
      double outerRadius = innerRadius + 25;
      PointD p1 = calculatePointOnCircle(center, outerRadius, startAngle);
      PointD p2 = calculatePointOnCircle(center, innerRadius, startAngle);
      PointD p3 = calculatePointOnCircle(center, innerRadius, endAngle);
      PointD p4 = calculatePointOnCircle(center, outerRadius, endAngle);

      Path2D.Double path = new Path2D.Double();
      //  start   c1     c2      end
      // p1 \------|------|------/ p4
      //     \    c4     c3     /
      // p2   \----|------|----/ p3
      path.moveTo(p1.x, p1.y);
      offset = ((Math.PI * offset) / 180) * 0.25;
      PointD c1 = calculatePointOnCircle(center, outerRadius + 1, startAngle + offset);
      PointD c2 = calculatePointOnCircle(center, outerRadius + 1, endAngle - offset);
      path.curveTo(c1.x, c1.y, c2.x, c2.y, p4.x, p4.y);
      path.lineTo(p3.x, p3.y);

      PointD c3 = calculatePointOnCircle(center, innerRadius + 1, endAngle - offset);
      PointD c4 = calculatePointOnCircle(center, innerRadius + 1, startAngle + offset);
      path.curveTo(c3.x, c3.y, c4.x, c4.y, p2.x, p2.y);
      path.lineTo(p1.x, p1.y);
      path.closePath();
      return path;
    } else {
      return new Ellipse2D.Double(nl.getX(), nl.getY(), nl.getWidth(), nl.getHeight());
    }
  }

  /**
   * Calculates the coordinates of the point on the circle at the given angle.
   * @param center The center of the circle
   * @param radius The radius of the circle
   * @param angle The angle in radians
   * @return The coordinates of the point on the circle at the given angle
   */
  private static PointD calculatePointOnCircle( PointD center, double radius, double angle ) {
    return new PointD(
            center.x + (radius * Math.cos(angle)),
            center.y + (radius * Math.sin(angle)));
  }
}
