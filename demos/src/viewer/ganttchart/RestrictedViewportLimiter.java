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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ViewportLimiter;

/**
 * Prevents scrolling above or below the vertical bounds of the task lanes.
 */
public class RestrictedViewportLimiter extends ViewportLimiter {
  private final GraphComponent taskComponent;

  /**
   * Initializes a new {@code RestrictedViewportLimiter} instances.
   */
  public RestrictedViewportLimiter( GraphComponent taskComponent ) {
    this.taskComponent = taskComponent;
  }

  /**
   * Limits the viewport to the area which contains task nodes.
   * @param canvas The canvas control on which the viewport should be applied.
   * @param suggestedViewport The suggested viewport.
   */
  @Override
  public RectD limitViewport( CanvasComponent canvas, RectD suggestedViewport ) {
    double topY = taskComponent.getContentRect().getY();
    double bottomY = taskComponent.getContentRect().getMaxY();

    double oldY = suggestedViewport.getY();
    double newY = Math.max(topY, Math.min(bottomY - suggestedViewport.getHeight(), oldY));

    return new RectD(suggestedViewport.getX(), newY, suggestedViewport.getWidth(), suggestedViewport.getHeight());
  }
}
