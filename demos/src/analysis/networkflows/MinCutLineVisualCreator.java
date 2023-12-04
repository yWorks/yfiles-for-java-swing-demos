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
package analysis.networkflows;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

/**
 * A {@link IVisualCreator} to visualize the minimum cut line of a max flow / min cut diagram.
 */
class MinCutLineVisualCreator implements IVisualCreator {
  private RectD bounds;
  private boolean visible;

  MinCutLineVisualCreator() {
    this.bounds = RectD.EMPTY;
    this.visible = false;
  }

  /**
   * Creates a visual that displays the min-cut line.
   */
  @Override
  public IVisual createVisual(IRenderContext context) {
    MinCutLineVisual visual = new MinCutLineVisual();
    visual.update(bounds, visible);
    return visual;
  }


  @Override
  public IVisual updateVisual(IRenderContext context, IVisual oldVisual) {
    if (oldVisual instanceof MinCutLineVisual) {
      MinCutLineVisual visual = (MinCutLineVisual) oldVisual;
      visual.update(bounds, visible);
      return visual;
    } else {
      return createVisual(context);
    }
  }

  /**
   * Gets the bounds of the min-cut line.
   */
  RectD getBounds() {
    return bounds;
  }

  /**
   * Set the bounds of the min-cut line.
   */
  void setBounds(RectD bounds) {
    this.bounds = bounds;
  }

  /**
   * Gets whether the min-cut line is visible.
   */
  boolean isVisible() {
    return visible;
  }

  /**
   * Sets whether the min-cut line is visible.
   */
  void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Custom {@link IVisual} implementation used to display the MinCutLine
   */
  private static class MinCutLineVisual implements IVisual {
    private static final String TEXT = "MIN CUT";

    private final RoundRectangle2D line;
    private boolean visible;

    MinCutLineVisual() {
      line = new RoundRectangle2D.Double(0, 0, 0, 0, 0, 0);
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      if (this.visible) {
        Paint oldPaint = g.getPaint();
        Stroke oldStroke = g.getStroke();
        AffineTransform oldTransform = g.getTransform();

        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(TEXT);
        final int asc = fm.getMaxAscent();
        final int th = asc + fm.getMaxDescent();

        g.setPaint(Colors.GOLD);
        g.fill(line);
        g.translate(line.getCenterX(), line.getCenterY());
        g.rotate(Math.PI * 0.5);
        g.translate(-tw * 0.5, -th * 0.5 + asc);
        g.setPaint(Colors.DARK_ORANGE);
        g.drawString(TEXT, 0, 0);

        g.setTransform(oldTransform);
        g.setStroke(oldStroke);
        g.setPaint(oldPaint);
      }
    }

    void update(final RectD bounds, final boolean visible) {
      if (bounds == null) {
        throw new IllegalArgumentException("Bounds may not be null.");
      }

      if (bounds.getWidth() > 0 && bounds.getHeight() > 0) {
        this.visible = visible;
        double r = Math.min(bounds.getWidth(), bounds.getHeight());
        this.line.setRoundRect(
            bounds.getX(),
            bounds.getY(),
            bounds.getWidth(),
            bounds.getHeight(), r, r);
      } else {
        this.visible = false;
      }
    }
  }
}
