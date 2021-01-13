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
package style.zoominvariantlabelstyle;

import com.yworks.yfiles.graph.ILabel;

/**
 * Stops the label from getting smaller in view coordinates if the zoom is
 * less than the style's minimum zoom value.
 */
public class ZoomInvariantBelowThresholdLabelStyle extends AbstractZoomInvariantLabelStyle {
  private double minZoom;

  /**
   * Initializes a new {@code ZoomInvariantBelowThresholdLabelStyle} instance
   * with {@code minZoom} set to {@code 1.0}.
   */
  public ZoomInvariantBelowThresholdLabelStyle() {
    minZoom = 1;
  }

  /**
   * Stops the label from getting smaller in view coordinates if the zoom is
   * less than the style's minimum zoom value.
   * @param label the current label which will be styled
   * @param zoom the current zoom factor
   */
  @Override
  protected double getScaleForZoom( ILabel label, double zoom ) {
    double min = getMinZoom();
    if (zoom > min) {
      return 1;
    }
    return min / zoom;
  }

  /**
   * Gets the minimum zoom value for this style.
   * This style will scale labels only down to this zoom value.
   */
  public double getMinZoom() {
    return minZoom;
  }

  /**
   * Gets the minimum zoom value for this style.
   * This style will scale labels only down to this zoom value.
   */
  public void setMinZoom( double value ) {
    this.minZoom = value;
  }
}
