/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package layout.hierarchiclayout;

import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.Pen;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Path2D;

/**
 * A {@link IVisual} that draws a constraint indicator.
 */
class ConstraintIndicatorVisual implements IVisual {
  private Path2D path;
  private Pen pen;
  private Paint fill;

  /**
   * Initializes a new <code>ConstraintIndicatorVisual</code> instance.
   * @param path the outline of the constraint indicator
   * @param pen  the pen to draw the constraint indicator
   * @param fill the paint to fill the constraint indicator
   */
  public ConstraintIndicatorVisual(Path2D path, Pen pen, Paint fill) {
    this.path = path;
    this.pen = pen;
    this.fill = fill;
  }

  /**
   * Returns the outline of the constraint indicator.
   * @return the outline of the constraint indicator.
   */
  public Path2D getPath() {
    return path;
  }

  @Override
  public void paint(IRenderContext context, Graphics2D gfx) {

    final Paint oldPaint = gfx.getPaint();
    final Stroke oldStroke = gfx.getStroke();

    try {
      if (fill != null) {
        gfx.setPaint(fill);
        gfx.fill(path);
      }
      if (pen != null) {
        pen.commit(gfx);
        gfx.draw(path);
      }
    } finally {
      gfx.setPaint(oldPaint);
      gfx.setStroke(oldStroke);
    }
  }
}
