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
package complete.isometric;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.RectD;

/**
 * This class provides methods to handle a solid figure in both view and layout space.
 */
class IsometricTransformationSupport {
  // Matrix to transform points from the layout space into the view space.
  static final double M_TO_VIEW_11 = Math.sqrt(3) * 0.5;
  static final double M_TO_VIEW_12 = M_TO_VIEW_11;
  static final double M_TO_VIEW_21 = -0.5;
  static final double M_TO_VIEW_22 = 0.5;

  // Matrix to transform points from the view space into the layout space.
  static final double M_TO_LAYOUT_11 = 1 / Math.sqrt(3);
  static final double M_TO_LAYOUT_12 = -1;
  static final double M_TO_LAYOUT_21 = M_TO_LAYOUT_11;
  static final double M_TO_LAYOUT_22 = -M_TO_LAYOUT_12;

  // Indices for the corners of the bounding box.
  // lower left
  static final int C0_X = 0;
  static final int C0_Y = 1;

  // lower front
  static final int C1_X = 2;
  static final int C1_Y = 3;

  // lower right
  static final int C2_X = 4;
  static final int C2_Y = 5;

  // lower back
  static final int C3_X = 6;
  static final int C3_Y = 7;

  // upper left
  static final int C4_X = 8;
  static final int C4_Y = 9;

  // upper front
  static final int C5_X = 10;
  static final int C5_Y = 11;

  // upper right
  static final int C6_X = 12;
  static final int C6_Y = 13;

  // upper back
  static final int C7_X = 14;
  static final int C7_Y = 15;


  private IsometricTransformationSupport() {
  }

  /**
   * Calculates the bounds of the solid figure in the view space.
   * @param geometry the data to construct the 3D-figure
   * @return the calculated bounds
   */
  static RectD calculateViewBounds( IsometricGeometry geometry ) {
    return calculateViewBounds(geometry, calculateCorners(geometry));
  }

  /**
   * Calculates the bounds of the solid figure in the view space.
   * @param geometry the data to construct the 3D-figure
   * @param corners the corners of the projection of the bounds of solid figure into the view space
   * @return the calculated bounds
   */
  static RectD calculateViewBounds( IsometricGeometry geometry, double[] corners ) {
    double minX = corners[C0_X];
    double minY = corners[C0_Y];
    double maxX = corners[C0_X];
    double maxY = corners[C0_Y];
    for (int i = 2; i < corners.length; i += 2) {
      minX = Math.min(minX, corners[i]);
      minY = Math.min(minY, corners[i + 1]);
      maxX = Math.max(maxX, corners[i]);
      maxY = Math.max(maxY, corners[i + 1]);
    }
    return new RectD(minX, minY, maxX - minX, maxY - minY);
  }

  /**
   * Calculates the corners of the projection of the bounds of solid figure into the view space.
   * @return corners the calculated corners.
   */
  static double[] calculateCorners( IPoint location, IsometricGeometry geometry ) {
    final double[] corners = calculateCorners(geometry);
    moveTo(location.getX(), location.getY(), corners);
    return corners;
  }

  /**
   * Calculates the corners of the projection of the bounds of solid figure into the view space.
   * @return corners the calculated corners.
   */
  static double[] calculateCorners( IsometricGeometry geometry ) {
    double[] corners = new double[16];
    corners[C0_X] = 0;
    corners[C0_Y] = 0;

    corners[C1_X] = toViewX(geometry.getWidth(), 0);
    corners[C1_Y] = toViewY(geometry.getWidth(), 0);

    corners[C2_X] = toViewX(geometry.getWidth(), geometry.getDepth());
    corners[C2_Y] = toViewY(geometry.getWidth(), geometry.getDepth());

    corners[C3_X] = toViewX(0, geometry.getDepth());
    corners[C3_Y] = toViewY(0, geometry.getDepth());

    for (int i = 0; i < 8; i += 2) {
      corners[i + 8] = corners[i];
      corners[i + 9] = corners[i + 1] - geometry.getHeight();
    }
    return corners;
  }

  /**
   * Transforms the given point from the layout space into the view space.
   * @param layoutX x-coordinate in layout space
   * @param layoutY y-coordinate in layout space
   * @return x-coordinate in view space
   */
  static double toViewX( double layoutX, double layoutY ) {
    return (M_TO_VIEW_11 * layoutX) + (M_TO_VIEW_12 * layoutY);
  }

  /**
   * Transforms the given point from the layout space into the view space.
   * @param layoutX x-coordinate in layout space
   * @param layoutY y-coordinate in layout space
   * @return y-coordinate in view space
   */
  static double toViewY( double layoutX, double layoutY ) {
    return (M_TO_VIEW_21 * layoutX) + (M_TO_VIEW_22 * layoutY);
  }

  /**
   * Transforms the given point from the view space into the layout space.
   * @param viewX x-coordinate in view space
   * @param viewY y-coordinate in view space
   * @return x-coordinate in layout space
   */
  static double toLayoutX( double viewX, double viewY ) {
    return (M_TO_LAYOUT_11 * viewX) + (M_TO_LAYOUT_12 * viewY);
  }

  /**
   * Transforms the given point from the view space into the layout space.
   * @param viewX x-coordinate in view space
   * @param viewY y-coordinate in view space
   * @return y-coordinate in layout space
   */
  static double toLayoutY( double viewX, double viewY ) {
    return (M_TO_LAYOUT_21 * viewX) + (M_TO_LAYOUT_22 * viewY);
  }

  /**
   * Translates the given corner to the given location, so that the upper left location of the bounds of the given
   * corners is on the given location.
   * @param x x-coordinate of the location where the corners should be moved to
   * @param y y-coordinate of the location where the corners should be moved to
   * @param corners corners to be moved
   */
  static void moveTo( double x, double y, double[] corners ) {
    // Calculate the upper left location of the bounds of the given corners.
    double minX = corners[C0_X];
    double minY = corners[C0_Y];
    for (int i = 2; i < corners.length; i += 2) {
      minX = Math.min(minX, corners[i]);
      minY = Math.min(minY, corners[i + 1]);
    }

    // Move the corners to the given location.
    double dx = x - minX;
    double dy = y - minY;
    for (int i = 0; i < corners.length; i += 2) {
      corners[i] += dx;
      corners[i + 1] += dy;
    }
  }
}
