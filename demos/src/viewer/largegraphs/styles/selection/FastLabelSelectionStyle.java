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
package viewer.largegraphs.styles.selection;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import viewer.largegraphs.LargeGraphsDemo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * Label style that is used as a zoom-invariant selection decorator.
 * <p>
 * This style essentially displays a rotated rectangle and scales its stroke thickness and brush by 1&nbsp;/&nbsp;zoom level.
 * This means that positioning considerations can still be done in world coordinates and the path doesn't require a
 * series of transformations to end up where it should be.
 * </p>
 * <p>
 * This style caches the scaled stroke color to avoid creating a new color for every invocation of
 * {@link #updateVisual(IRenderContext, IVisual, ILabel)}.
 * </p>
 * @see LargeGraphsDemo#setSelectionDecorators
 */
public class FastLabelSelectionStyle extends AbstractLabelStyle {

  // region Properties

  private Color fill;

  /**
   * Gets the color used to fill the selection rectangle.
   */
  public Color getFill() {
    return fill;
  }

  /**
   * Sets the color used to fill the selection rectangle.
   */
  public void setFill(Color fill) {
    this.fill = fill;
  }

  private Pen pen;

  /**
   * Gets the pen used to draw the rectangle outline.
   */
  public Pen getPen() {
    return pen;
  }

  /**
   * Sets the pen used to draw the rectangle outline.
   */
  public void setPen(Pen pen) {
    this.pen = pen;
  }

  // endregion

  /**
   * Initializes a new instance with the given fill and pen.
   * @param fill The color used to fill the selection rectangle.
   * @param pen The pen used to draw the rectangle outline.
   */
  public FastLabelSelectionStyle(Color fill, Pen pen) {
    this.fill = fill;
    this.pen = pen;
  }

  // region Style

  @Override
  protected IVisual createVisual(IRenderContext context, ILabel label) {
    double scale = 1 / context.getZoom();
    IOrientedRectangle layout = getSelectionBounds(label, scale);

    LabelVisual labelVisual = new LabelVisual(layout, getFill(), getPen());
    labelVisual.updatePen(scale);
    return labelVisual;
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, ILabel label) {
    if (!(oldVisual instanceof LabelVisual)) {
      return createVisual(context, label);
    }

    double scale = 1 / context.getZoom();
    IOrientedRectangle layout = getSelectionBounds(label, scale);
    LabelVisual labelVisual = (LabelVisual) oldVisual;

    if (!layout.equals(labelVisual.layout)) {
      labelVisual = new LabelVisual(layout, getFill(), getPen());
    }
    labelVisual.updatePen(1 / context.getZoom());
    return labelVisual;
  }

  @Override
  protected SizeD getPreferredSize(ILabel label) {
    return label.getLayout().toSizeD();
  }

  /**
   * Returns an {@link IOrientedRectangle} representing the selection rectangle around the label.
   * @param label The label.
   * @param scale The scale. This is 1 / zoom level.
   * @return The selection rectangle, enlarged by the scaled pen thickness.
   */
  private IOrientedRectangle getSelectionBounds(ILabel label, double scale) {
    IOrientedRectangle layout = label.getLayout();
    // Normally I'd say scale / 2 would be correct here, but scale / 4 reproduces the default label selection exactly.
    double amount = getPen().getThickness() * scale / 4;
    PointD up = layout.getUp();
    PointD anchor = layout.getAnchorLocation();
    PointD right = new PointD(-up.getY(), up.getX());
    double width = layout.getWidth();
    double height = layout.getHeight();

    double newAnchorX = anchor.getX() - up.getX() * amount - right.getX() * amount;
    double newAnchorY = anchor.getY() - up.getY() * amount - right.getY() * amount;
    double newWidth = width + amount * 2;
    double newHeight = height + amount * 2;

    return new OrientedRectangle(newAnchorX, newAnchorY, newWidth, newHeight, up.getX(), up.getY());
  }

  // endregion


  /**
   * Custom label visualization rendering in world coordinates
   */
  private static class LabelVisual implements IVisual {
    private static final Path2D.Double path = new Path2D.Double();

    private IOrientedRectangle layout;

    private Color fill;
    private Pen pen;
    private Pen scaledPen;

    public LabelVisual(IOrientedRectangle layout, Color fill, Pen pen) {
      this.layout = layout;
      this.fill = fill;
      this.pen = pen;
      if (pen != null) {
        this.scaledPen = pen.cloneCurrentValue();
      }
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      PointD up = layout.getUp();
      PointD anchor = layout.getAnchorLocation();
      PointD right = new PointD(-up.getY(), up.getX());

      path.reset();
      path.moveTo(anchor.getX(), anchor.getY());
      path.lineTo(anchor.getX() + right.getX() * layout.getWidth(), anchor.getY() + right.getY() * layout.getWidth());
      path.lineTo(
          anchor.getX() + right.getX() * layout.getWidth() + up.getX() * layout.getHeight(),
          anchor.getY() + right.getY() * layout.getWidth() + up.getY() * layout.getHeight());
      path.lineTo(anchor.getX() + up.getX() * layout.getHeight(), anchor.getY() + up.getY() * layout.getHeight());
      path.closePath();

      // Fill -- only drawn when a brush was set
      if (fill != null) {
        g.setBackground(fill);
        g.fill(path);
      }
      if (scaledPen != null) {
        scaledPen.commit(g);
        g.draw(path);
      }
    }


    /**
     * Re-creates the scaled stroke brush if necessary and sets it on the rectangle.
     * @param scale The scale. This is 1&nbsp;/&nbsp;zoom level.
     */
    private void updatePen(double scale) {
      if (pen != null) {
        double strokeWidth = pen.getThickness() * scale;
        scaledPen.setThickness(strokeWidth);
      }
    }
  }
}
