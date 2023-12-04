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
package deploy.obfuscation;

import com.yworks.yfiles.geometry.GeomUtilities;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Visualizes the content rectangle of a yFiles for Java (Swing)
 * {@link com.yworks.yfiles.view.CanvasComponent}.
 * <p>
 * This class solely exists for the purpose of demonstrating that obfuscated
 * classes still work as intended.
 * </p>
 * @author Thomas Behr
 */
public class BackgroundRectangle implements IVisualCreator {
  private final Paint color = toolkit.Themes.PALETTE12.getBackgroundPaint();
  /**
   * Creates a blue rectangle that visualizes the content rectangle
   * of the canvas component associated to the given render context.
   */
  @Override
  public IVisual createVisual( IRenderContext ctx ) {
    RectD r = ctx.getCanvasComponent().getContentRect();
    return new ShapeVisual(new Rectangle2D.Double(r.x, r.y, r.width, r.height), Pen.getBlack(), color);
  }

  /**
   * Updates the geometry of the rectangle that visualizes the content rectangle
   * of the canvas component associated to the given render context.
   */
  @Override
  public IVisual updateVisual( IRenderContext ctx, IVisual oldVisual ) {
    if (oldVisual instanceof ShapeVisual) {
      Shape shape = ((ShapeVisual) oldVisual).getShape();
      if (shape instanceof Rectangle2D) {
        RectD r = ctx.getCanvasComponent().getContentRect();
        ((Rectangle2D) shape).setFrame(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        return oldVisual;
      }
    }
    return createVisual(ctx);
  }
}
