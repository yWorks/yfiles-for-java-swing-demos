/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.IAnimation;

import java.time.Duration;

/**
 * An animation that zooms in and out again.
 * <p>
 * Half the animation duration is spent zooming in from the initial zoom level to a given target zoom level. The
 * other half of the animation duration is spent zooming out again.
 * </p>
 * @see IAnimation
 */
public class ZoomInAndBackAnimation implements IAnimation {

  private final CanvasComponent canvas;
  private final double targetZoom;
  private final Duration duration;

  // Binary logarithm of the initial zoom level.
  private double initialZoomLog = 0;
  private final double targetZoomLog;

  // The zoom level difference between the initial and the target zoom level.
  private double delta = 0;


  public ZoomInAndBackAnimation(CanvasComponent canvas, double targetZoom, Duration duration) {
    this.canvas = canvas;
    this.targetZoom = targetZoom;
    this.duration = duration;
    targetZoomLog = Math.log(targetZoom) / Math.log(2);
  }

  @Override
  public void initialize() {
    this.initialZoomLog = Math.log(this.canvas.getZoom()) / Math.log(2);
    this.delta = this.targetZoomLog - this.initialZoomLog;
  }

  @Override
  public void animate(double time) {
    double newZoom = time < 0.5
        ? this.initialZoomLog + (this.delta * (time * 2))
        : this.targetZoomLog - (this.delta * ((time - 0.5) * 2));
    this.canvas.setZoom(Math.pow(2, newZoom));
  }

  @Override
  public void cleanUp() {
  }

  @Override
  public Duration getPreferredDuration() {
    return duration;
  }
}
