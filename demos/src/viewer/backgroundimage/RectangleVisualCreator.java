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
package viewer.backgroundimage;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

/**
 * Creates visuals that visualize the content rectangle of a
 * {@link com.yworks.yfiles.view.CanvasComponent}.
 */
public class RectangleVisualCreator implements IVisualCreator {

  /**
   * Creates the visual for the background.
   * @param context The context that describes where the visual will be used
   * @return {@link com.yworks.yfiles.view.IVisual} The visual for the background
   */
  @Override
  public IVisual createVisual( IRenderContext context ) {
    return newBackgroundVisual(getBackgroundRectangle(context));
  }

  /**
   * Updates the visual for the background.
   * @param context The context that describes where the visual will be used
   * @param oldVisual The old visual
   * @return {@link com.yworks.yfiles.view.IVisual} The visual for the background
   */
  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    RectD area = getBackgroundRectangle(context);

    if (oldVisual instanceof MyRectangleVisual) {
      ((MyRectangleVisual) oldVisual).update(area);
      return oldVisual;
    } else {
      return newBackgroundVisual(area);
    }
  }

  /**
   * Returns the area which contains all elements in the graph.
   */
  private static RectD getBackgroundRectangle( IRenderContext context ) {
    return context.getCanvasComponent().getContentRect().getEnlarged(new InsetsD(20));
  }

  /**
   * Creates a visual for the given rectangular area.
   */
  private static IVisual newBackgroundVisual( RectD area ) {
    MyRectangleVisual visual = new MyRectangleVisual();
    visual.update(area);
    return visual;
  }


  /**
   * Holds and paints a rectangular area.
   */
  private static class MyRectangleVisual implements IVisual {
    private final Rectangle2D rect;

    MyRectangleVisual() {
      rect = new Rectangle2D.Double();
    }

    /**
     * Updates the visuals bounds to the given rectangular area.
     */
    void update( RectD bounds ) {
      rect.setFrame(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }

    /**
     * Paints the rectangle according to the saved values.
     */
    @Override
    public void paint( IRenderContext context, Graphics2D graphics ) {
      Paint oldPaint = graphics.getPaint();
      graphics.setPaint(new Color(102, 153, 204));
      graphics.fill(rect);
      graphics.setPaint(oldPaint);
    }
  }
}
