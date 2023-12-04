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
package complete.isometric;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;
import complete.isometric.model.NodeData;

import java.awt.Color;
import java.awt.geom.AffineTransform;

/**
 * A node style that visualizes the node as block in an isometric fashion.
 */
public class IsometricNodeStyle extends AbstractNodeStyle {

  /**
   * Calculates a vector in world coordinates whose transformation by the projection results
   * in the vector (0, -1).
   *
   * @param projection The projection to consider.
   * @return The vector in world coordinates that gets transformed to the vector (0, -1).
   */
  protected static PointD calculateHeightVector(AffineTransform projection) {
    Matrix2D matrix2D = Matrix2D.fromTransform(projection);
    matrix2D.clone();
    matrix2D.invert();
    return matrix2D.transform(new PointD(0d, -1d));
  }

  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    IsometricNodeVisual isometricNodeVisual = new IsometricNodeVisual();
    isometricNodeVisual.update(context, node);
    return isometricNodeVisual;
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    IsometricNodeVisual visual = (oldVisual instanceof IsometricNodeVisual) ? (IsometricNodeVisual) oldVisual : null;
    if (visual != null) {
      return visual.update(context, node);
    } else {
      return createVisual(context, node);
    }
  }

  /**
   * The visual that renders the node as an isometric block.
   */
  private static class IsometricNodeVisual extends VisualGroup {

    private RectD layout;
    private double height;
    private Color color;
    private AffineTransform projection;

    private GeneralPath topFacePath;
    private GeneralPath leftFacePath;
    private GeneralPath rightFacePath;

    private Color topColor;
    private Color leftColor;
    private Color rightColor;
    private Pen pen;

    public IsometricNodeVisual update(IRenderContext context, INode node) {
      NodeData nodeData = (NodeData) node.getTag();
      if (nodeData == null) {
        return null;
      }

      if (node.getLayout().toRectD() != layout
              || nodeData.getGeometry().getHeight() != height
              || nodeData.getColor() != color
              || nodeData.getPen() != pen
              || context.getProjection() != projection) {


        layout = node.getLayout().toRectD();
        height = nodeData.getGeometry().getHeight();
        color = nodeData.getColor();
        pen = nodeData.getPen();
        projection = context.getProjection();

        double[] corners = calculateCorners(context.getProjection(), layout.getX(), layout.getY(), layout.getWidth(), layout.getHeight(), height);
        final Color color = nodeData.getColor();
        if (color != null) {
          this.topColor = color;
          if (height > 0) {
            this.leftColor = color.darker();
            this.rightColor = leftColor.darker();
          }
        }

        if (height == 0) {
          leftFacePath = null;
          rightFacePath = null;
        } else if (height > 0) {
          // check which of the left, right, back and front faces are visible using the current projection
          PointD upVector = calculateHeightVector(projection);
          boolean useLeft = upVector.getX() > 0;
          boolean useBack = upVector.getY() > 0;

          leftFacePath = useLeft ? getLeftFacePath(corners) : getRightFacePath(corners);
          rightFacePath = useBack ? getBackFacePath(corners) : getFrontFacePath(corners);
        }
        topFacePath = getTopFacePath(corners);
      }
      this.getChildren().clear();
      configure();
      return this;
    }

    private void configure() {
      if (leftFacePath != null) {
        add(new ShapeVisual(leftFacePath.createPath(new Matrix2D()), pen, leftColor));
      }
      if (rightFacePath != null) {
        add(new ShapeVisual(rightFacePath.createPath(new Matrix2D()), pen, rightColor));
      }
      if (topFacePath != null) {
        add(new ShapeVisual(topFacePath.createPath(new Matrix2D()), pen, topColor));
      }
    }
  }

  /**
   * Creates a {@link GeneralPath} that describes the face on the top of the block.
   *
   * @param corners The coordinates of the corners of the block.
   * @return A {@link GeneralPath} that describes the face on the top of the block.
   */
  static GeneralPath getTopFacePath(double[] corners) {
    GeneralPath path = new GeneralPath();
    path.moveTo(
        corners[UP_TOP_LEFT_X],
        corners[UP_TOP_LEFT_Y]
    );
    path.lineTo(
        corners[UP_TOP_RIGHT_X],
        corners[UP_TOP_RIGHT_Y]
    );
    path.lineTo(
        corners[UP_BOTTOM_RIGHT_X],
        corners[UP_BOTTOM_RIGHT_Y]
    );
    path.lineTo(
        corners[UP_BOTTOM_LEFT_X],
        corners[UP_BOTTOM_LEFT_Y]
    );
    path.close();
    return path;
  }

  /**
   * Creates a {@link GeneralPath} that describes the face on the left side of the block.
   *
   * @param corners The coordinates of the corners of the block.
   * @return A {@link GeneralPath} that describes the face on the left side of the block.
   */
  static GeneralPath getLeftFacePath(double[] corners) {
    GeneralPath path = new GeneralPath();
    path.moveTo(
        corners[LOW_TOP_LEFT_X],
        corners[LOW_TOP_LEFT_Y]
    );
    path.lineTo(
        corners[UP_TOP_LEFT_X],
        corners[UP_TOP_LEFT_Y]
    );
    path.lineTo(
        corners[UP_BOTTOM_LEFT_X],
        corners[UP_BOTTOM_LEFT_Y]
    );
    path.lineTo(
        corners[LOW_BOTTOM_LEFT_X],
        corners[LOW_BOTTOM_LEFT_Y]
    );
    path.close();
    return path;
  }


  /**
   * Creates a {@link GeneralPath} that describes the face on the right side of the block.
   *
   * @param corners The coordinates of the corners of the block.
   * @return A {@link GeneralPath} that describes the face on the right side of the block.
   */
  static GeneralPath getRightFacePath(double[] corners) {
    GeneralPath path = new GeneralPath();
    path.moveTo(
        corners[LOW_TOP_RIGHT_X],
        corners[LOW_TOP_RIGHT_Y]
    );
    path.lineTo(
        corners[UP_TOP_RIGHT_X],
        corners[UP_TOP_RIGHT_Y]
    );
    path.lineTo(
        corners[UP_BOTTOM_RIGHT_X],
        corners[UP_BOTTOM_RIGHT_Y]
    );
    path.lineTo(
        corners[LOW_BOTTOM_RIGHT_X],
        corners[LOW_BOTTOM_RIGHT_Y]
    );
    path.close();
    return path;
  }


  /**
   * Creates a {@link GeneralPath} that describes the face on the front side of the block.
   *
   * @param corners The coordinates of the corners of the block.
   * @return A {@link GeneralPath} that describes the face on the front side of the block.
   */
  static GeneralPath getFrontFacePath(double[] corners) {
    GeneralPath path = new GeneralPath();
    path.moveTo(
        corners[LOW_BOTTOM_LEFT_X],
        corners[LOW_BOTTOM_LEFT_Y]
    );
    path.lineTo(
        corners[UP_BOTTOM_LEFT_X],
        corners[UP_BOTTOM_LEFT_Y]
    );
    path.lineTo(
        corners[UP_BOTTOM_RIGHT_X],
        corners[UP_BOTTOM_RIGHT_Y]
    );
    path.lineTo(
        corners[LOW_BOTTOM_RIGHT_X],
        corners[LOW_BOTTOM_RIGHT_Y]
    );
    path.close();
    return path;
  }


  /**
   * Creates a {@link GeneralPath} that describes the face on the back side of the block.
   *
   * @param corners The coordinates of the corners of the block.
   * @return A {@link GeneralPath} that describes the face on the back side of the block.
   */
  static GeneralPath getBackFacePath(double[] corners) {
    GeneralPath path = new GeneralPath();
    path.moveTo(
        corners[LOW_TOP_LEFT_X],
        corners[LOW_TOP_LEFT_Y]
    );
    path.lineTo(
        corners[UP_TOP_LEFT_X],
        corners[UP_TOP_LEFT_Y]
    );
    path.lineTo(
        corners[UP_TOP_RIGHT_X],
        corners[UP_TOP_RIGHT_Y]
    );
    path.lineTo(
        corners[LOW_TOP_RIGHT_X],
        corners[LOW_TOP_RIGHT_Y]
    );
    path.close();
    return path;
  }

  // Indices for the corners of the bounding box.
  private static final int LOW_TOP_LEFT_X = 0;
  private static final int LOW_TOP_LEFT_Y = 1;
  private static final int LOW_TOP_RIGHT_X = 2;
  private static final int LOW_TOP_RIGHT_Y = 3;
  private static final int LOW_BOTTOM_RIGHT_X = 4;
  private static final int LOW_BOTTOM_RIGHT_Y = 5;
  private static final int LOW_BOTTOM_LEFT_X = 6;
  private static final int LOW_BOTTOM_LEFT_Y = 7;
  private static final int UP_TOP_LEFT_X = 8;
  private static final int UP_TOP_LEFT_Y = 9;
  private static final int UP_TOP_RIGHT_X = 10;
  private static final int UP_TOP_RIGHT_Y = 11;
  private static final int UP_BOTTOM_RIGHT_X = 12;
  private static final int UP_BOTTOM_RIGHT_Y = 13;
  private static final int UP_BOTTOM_LEFT_X = 14;
  private static final int UP_BOTTOM_LEFT_Y = 15;

  private static double[] calculateCorners(AffineTransform projection, double x, double y, double width, double depth, double height) {
    PointD tmpVector = calculateHeightVector(projection);
    PointD heightVector = new PointD(height * tmpVector.getX(), height * tmpVector.getY());

    double[] corners = new double[16];
    corners[LOW_TOP_LEFT_X] = x;
    corners[LOW_TOP_LEFT_Y] = y;

    corners[LOW_TOP_RIGHT_X] = x + width;
    corners[LOW_TOP_RIGHT_Y] = y;

    corners[LOW_BOTTOM_RIGHT_X] = x + width;
    corners[LOW_BOTTOM_RIGHT_Y] = y + depth;

    corners[LOW_BOTTOM_LEFT_X] = x;
    corners[LOW_BOTTOM_LEFT_Y] = y + depth;

    for (int i = 0; i < 8; i += 2) {
      corners[i + 8] = corners[i] + heightVector.getX();
      corners[i + 9] = corners[i + 1] + heightVector.getY();
    }
    return corners;
  }
}
