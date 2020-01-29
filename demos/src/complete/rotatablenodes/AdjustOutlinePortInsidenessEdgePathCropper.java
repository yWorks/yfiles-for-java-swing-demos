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
package complete.rotatablenodes;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.DefaultEdgePathCropper;
import com.yworks.yfiles.graph.styles.IShapeGeometry;


/**
 * Crops adjacent edges at the nodes rotated bounds for internal ports.
 */
public class AdjustOutlinePortInsidenessEdgePathCropper extends DefaultEdgePathCropper {

  private static final double FACTOR = 1.001d;
  /**
   * Checks whether or not the given location is inside the nodes rotated shape.
   */
  @Override
  protected boolean isInside(PointD location, INode node, IShapeGeometry nodeShapeGeometry, IEdge edge) {
    if (nodeShapeGeometry != null) {
      return  getScaledOutline(node, nodeShapeGeometry).areaContains(location);
    }
    return super.isInside(location, node, nodeShapeGeometry, edge);
  }


  /**
   * Returns the intersection point of the segment between the outer and inner and the node's rotated shape.
   * If there is no intersection point, the result is null.
   */
  @Override
  protected PointD getIntersection(INode node, IShapeGeometry nodeShapeGeometry, IEdge edge, PointD inner, PointD outer) {
    if (nodeShapeGeometry != null){
      double a = getScaledOutline(node, nodeShapeGeometry).findLineIntersection(inner, outer);
      if (a < Double.POSITIVE_INFINITY) {
        double returnValueX = inner.getX() + (outer.getX() - inner.getX()) * a;
        double returnValueY = inner.getY() + (outer.getY() - inner.getY()) * a;
        return new PointD(returnValueX, returnValueY);
      }
      return null;
    }
    return super.getIntersection(node, nodeShapeGeometry, edge, inner, outer);
  }

  /**
   * Returns a slightly enlarged outline of the shape to ensure that ports that lie exactly on the shape's
   * outline are always considered inside.
   */
  private GeneralPath getScaledOutline(INode node, IShapeGeometry nodeShapeGeometry) {
    GeneralPath outline = nodeShapeGeometry.getOutline();
    PointD center = node.getLayout().getCenter();
    Matrix2D matrix = new Matrix2D();

    matrix.translate(new PointD(-center.getX() * (FACTOR - 1), -center.getY() * (FACTOR - 1)));
    matrix.scale(FACTOR, FACTOR);
    outline.transform(matrix);
    return outline;
  }
}
