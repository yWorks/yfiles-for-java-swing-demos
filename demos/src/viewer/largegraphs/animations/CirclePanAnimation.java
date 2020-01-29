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
package viewer.largegraphs.animations;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.IAnimation;

import java.time.Duration;

/**
 * An animation that pans the viewport in a circular motion.
 * <p>
 * The animation pans the viewport in a circle with a diameter of half the viewport's width.
 * </p>
 * @see com.yworks.yfiles.view.IAnimation
 */
public class CirclePanAnimation implements IAnimation {

  private final CanvasComponent canvas;
  private final int revolutions;
  private final Duration preferredDuration;

  // The rotation angle during the last frame. This is needed for correct interaction with a simultaneous zoom animation.
  double lastAngle = 0;

  // The circle radius during the last frame. This is needed for correct interaction with a simultaneous zoom animation.
  double lastRadius = 0;

  /**
   * Initializes a new instance of the {@link CirclePanAnimation} class with the given number of revolutions and
   * animation time.
   */
  public CirclePanAnimation(CanvasComponent canvas, int revolutions, Duration preferredDuration) {
    this.canvas = canvas;
    this.revolutions = revolutions;
    this.preferredDuration = preferredDuration;
  }

  @Override
  public void initialize() {
    this.lastAngle = 0;
    this.lastRadius = this.canvas.getViewport().getWidth() / 4;
  }

  @Override
  public void animate(double time) {
    // The circle radius depends on the viewport size to be zoom-invariant
    double radius = this.canvas.getViewport().getWidth() / 4;
    double totalAngle = 2 * Math.PI * this.revolutions;
    double currentAngle = totalAngle * time;

    // Undo the last frame's movement first
    PointD undo = new PointD(
        Math.cos(this.lastAngle) * this.lastRadius,
        Math.sin(this.lastAngle) * this.lastRadius);

    // Then apply the current one. This is needed to play well with a simultaneous zoom animation.
    PointD p1 = new PointD(this.canvas.getViewPoint().getX() - undo.x, this.canvas.getViewPoint().getY() - undo.y);
    PointD p2 = new PointD(Math.cos(currentAngle) * radius, Math.sin(currentAngle) * radius);
    this.canvas.setViewPoint(PointD.add(p1, p2));
    this.lastRadius = radius;
    this.lastAngle = currentAngle;
  }

  @Override
  public void cleanUp() {
  }

  @Override
  public Duration getPreferredDuration() {
    return preferredDuration;
  }
}
