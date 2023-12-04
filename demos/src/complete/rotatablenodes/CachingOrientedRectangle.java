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
package complete.rotatablenodes;


import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;

/**
 * An oriented rectangle that specifies the location, size and rotation angle of a rotated node.
 * <p>
 * This class is used mainly for performance reasons. It provides cached values. In principle, it would be enough
 * to just store the rotation angle but following to that, we would have to recalculate all the properties of this
 * class very often.
 * </p>
 */
public class CachingOrientedRectangle implements IOrientedRectangle {
  private static final double FULL_CIRCLE = 2 * Math.PI;

  private final OrientedRectangle cachedOrientedRect;
  private RectD cachedLayout;
  private double angle;
  private PointD upVector;

  /**
   * Initializes a new instance with an empty layout.
   */
  CachingOrientedRectangle() {
    this(RectD.EMPTY);
  }

  /**
   * Initializes a new instance with the given layout.
   */
  private CachingOrientedRectangle(RectD layout) {
    this.upVector = new PointD(0, -1);
    angle = 0.0d;
    this.cachedLayout = layout;
    cachedOrientedRect = new OrientedRectangle(this.cachedLayout);
  }


  /**
   * Gets the rotation angle in radians.
   */
  public double getAngle() {
    return this.angle;
  }

  /**
   * Sets the rotation angle in radians.
   */
  public void setAngle(double value) {
    this.angle = normalizeAngle(value);
    cachedOrientedRect.setAngle(value);
    cachedOrientedRect.setCenter(cachedLayout.getCenter());
    upVector = cachedOrientedRect.getUp();
  }

  /**
   * Returns the width of the rectangle.
   */
  public double getWidth() {
    return cachedLayout.getWidth();
  }

  /**
   * Returns the height of the rectangle.
   */
  public double getHeight() {
    return cachedLayout.getHeight();
  }

  /**
   * Returns the x-coordinate of the rectangle's anchor point.
   */
  public double getAnchorX() {
    return cachedOrientedRect.getAnchorX();
  }

  /**
   * Returns the y-coordinate of the rectangle's anchor point.
   */
  public double getAnchorY() {
    return cachedOrientedRect.getAnchorY();
  }

  /**
   * Returns the x-coordinate of the rectangle's up vector.
   */
  public double getUpX() {
    return cachedOrientedRect.getUpX();
  }

  /**
   * Returns the y-coordinate of the rectangles's up vector.
   */
  public double getUpY() {
    return cachedOrientedRect.getUpY();
  }

  /**
   * Returns the rectangle's up vector.
   */
  public PointD getUpVector() {
    return upVector;
  }

  /**
   * Sets the rectangle's uo vector.
   */
  public void setUpVector(PointD value) {
    this.upVector = value;
    cachedOrientedRect.setUpVector(value.getX(), value.getY());
    cachedOrientedRect.setCenter(cachedLayout.getCenter());
    angle = cachedOrientedRect.getAngle();
  }

  /**
   * Updates the layout in the cache.
   */
  void updateCache(RectD layout) {
    if (layout.equals(cachedLayout) && upVector.equals(cachedOrientedRect.getUp())) {
      return;
    }

    cachedLayout = layout;
    cachedOrientedRect.setUpVector(upVector.getX(), upVector.getY());
    cachedOrientedRect.setWidth(getWidth());
    cachedOrientedRect.setHeight(getHeight());
    cachedOrientedRect.setCenter(cachedLayout.getCenter());
  }

  /**
   * Normalizes the angle to 0 - 2*PI.
   */
  static double normalizeAngle(double angle) {
    angle %= FULL_CIRCLE;
    if (angle < 0) {
      angle += FULL_CIRCLE;
    }
    return angle;
  }
}

