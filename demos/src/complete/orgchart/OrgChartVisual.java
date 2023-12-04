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
package complete.orgchart;

import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * A visual that acts as common superclass for the various level-of-detail-visuals.
 * This visual paints a rectangular shape that fit the INode's layout according to
 * the state of the INode in the GraphComponent. A node is either normal, hovered or focused.
 * The colors of the rectangular shape change according to the state of the node.
 *
 * This class also implements the paint method of the {@link IVisual} interface.
 * The method paints the before mentioned rectangular shape for the employee and then delegates
 * to the abstract method #paintContent to paint the interior of the rectangle.
 */
public abstract class OrgChartVisual implements IVisual {

  // constants for the different colors of the rectangular shape

  // colors for the "normal" mode - not focused, not hovered.
  private static final Color NORMAL_COLOR_1 = Color.decode("#CCFFFF");
  private static final Color NORMAL_COLOR_2 = Color.decode("#249AE7");
  private static final GradientPaint NORMAL_GRADIENT = new GradientPaint(0, 0, NORMAL_COLOR_1, 0, 100, NORMAL_COLOR_2);
  private static final Pen NORMAL_PEN = new Pen(NORMAL_COLOR_2, 3);

  // colors for the hovered mode - when a node is hovered by the mouse and is not focused.
  private static final Color HOVER_COLOR_1 = Color.decode("#FFFFFF");
  private static final Color HOVER_COLOR_2 = Color.decode("#249AE7");
  private static final GradientPaint HOVER_GRADIENT = new GradientPaint(0, 0, HOVER_COLOR_1, 0, 100, HOVER_COLOR_2);
  private static final Pen HOVER_PEN = new Pen(HOVER_COLOR_2, 3);
  // colors for the focused mode - when a node is the current item of the GraphComponent (which it becomes when it is focused in most of the cases)
  private static final Color FOCUSED_COLOR_1 = Color.decode("#FFFFFF");

  private static final Color FOCUSED_COLOR_2 = Color.decode("#FFA500");
  private static final GradientPaint FOCUSED_GRADIENT = new GradientPaint(0, 0, FOCUSED_COLOR_1, 0, 100, FOCUSED_COLOR_2);
  private static final Pen FOCUSED_PEN = new Pen(FOCUSED_COLOR_2, 3);

  /**
   * The employee that is associated with the node that this visual is created for.
   */
  private Employee employee;

  /**
   * The layout of the node that this visual is created for. the layout determines the boundaries of the rectangular shape that this visual paints.
   */
  private IRectangle layout;

  /**
   * The java rectangle shape that is painted for the background of the visual.
   */
  private RoundRectangle2D rect;

  // indicators for the state of the node that this visual paints
  private boolean isFocused;
  private boolean isHovered;

  protected OrgChartVisual(Employee employee, IRectangle layout, boolean isFocused, boolean isHovered) {
    this.employee = employee;
    this.layout = layout;
    this.isFocused = isFocused;
    this.isHovered = isHovered;
    this.rect = new RoundRectangle2D.Double(0, 0, layout.getWidth(), layout.getHeight(), 16, 16);
  }

  @Override
  public void paint(IRenderContext context, Graphics2D g) {
    final AffineTransform oldTransform = g.getTransform();
    final Paint oldPaint = g.getPaint();
    final Font oldFont = g.getFont();
    final Stroke oldStroke = g.getStroke();
    try {
      g.translate(layout.getX(), layout.getY());

      rect.setRoundRect(0, 0, layout.getWidth(), layout.getHeight(), 16, 16);

      // paint the visual in the correct colors dependent on the state. note that the focused state "wins" over the hovered state.
      if (isHovered) {
        paintRect(g, HOVER_GRADIENT, HOVER_PEN);
      }
      if (isFocused){
        paintRect(g, FOCUSED_GRADIENT, FOCUSED_PEN);
      }
      if (!isHovered && !isFocused){
        paintRect(g, NORMAL_GRADIENT, NORMAL_PEN);
      }

      // delegate to the subclass to paint the content of the visual
      paintContent(context, g);

    } finally {
      g.setTransform(oldTransform);
      g.setPaint(oldPaint);
      g.setFont(oldFont);
      g.setStroke(oldStroke);
    }
  }

  /**
   * Paints the content of the rectangular shape that this visual paints.
   */
  protected abstract void paintContent(IRenderContext context, Graphics2D g);

  /**
   * Uses the given paint and pen to draw the rectangle of this visual with a border.
   * @param fill the color to use for the interior of the rectangle
   * @param pen the pen that defines the color and stroke to use for the border of the rectangle
   */
  private void paintRect(Graphics2D g, Paint fill, Pen pen) {
    g.setPaint(fill);
    g.fill(rect);
    pen.commit(g);
    g.draw(rect);
  }

  /**
   * A static instance of {@link java.awt.font.FontRenderContext} to calculate the bounds of text that subclasses probably draw.
   */
  private static final FontRenderContext FRC = new FontRenderContext(new AffineTransform(), false, false);

  /**
   * Calculates the bounds of the given string when drawn with the given font. This method is used by subclasses to position text
   * to be drawn.
   * @param s the text to measure.
   * @param font the font that the given text would be drawn in.
   * @return a rectangle that defines the bounds of the text when drawn with the given font.
   */
  protected Rectangle2D getLineBounds(String s, Font font){
    return font.getStringBounds(s, FRC);
  }

  /**
   * Returns the employee that this visual is assigned to.
   */
  public Employee getEmployee() {
    return employee;
  }

  /**
   * Returns the layout for the rectangle.
   */
  public IRectangle getLayout() {
    return layout;
  }

  /**
   * Sets the layout for the rectangle.
   */
  public void setLayout(IRectangle layout) {
    this.layout = layout;
  }

  /**
   * Sets the indicator for the focused state of the node this visual is made for.
   */
  public void setFocused(final boolean isFocused) {
    this.isFocused = isFocused;
  }

  /**
   * Sets the indicator for the hovered state of the node this visual is made for.
   */
  public void setHovered(final boolean isHovered) {
    this.isHovered = isHovered;
  }
}
