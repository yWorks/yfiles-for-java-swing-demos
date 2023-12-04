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
package viewer.levelofdetail;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Visualizes different pieces of information about employees depending on
 * the graph control's current zoom factor.
 */
public class LevelOfDetailNodeStyle extends AbstractNodeStyle {

  /**
   * The minimum zoom factor to display all employee information.
   */
  private static final double DETAIL_THRESHOLD = 0.7;
  /**
   * The minimum zoom factor to display some but not all employee information.
   */
  private static final double INTERMEDIATE_THRESHOLD = 0.4;

  /**
   * Creates the IVisual for the given graph node and its associated
   * employee instance.
   */
  @Override
  protected IVisual createVisual( IRenderContext context, INode node ) {
    double zoom = context.getZoom();
    if (zoom > DETAIL_THRESHOLD) {
      DetailVisual visual = new DetailVisual();
      visual.update(node);
      return visual;
    } else if (zoom > INTERMEDIATE_THRESHOLD) {
      IntermediateVisual visual = new IntermediateVisual();
      visual.update(node);
      return visual;
    } else {
      OverviewVisual visual = new OverviewVisual();
      visual.update(node);
      return visual;
    }
  }

  /**
   * Updates the IVisual for the given graph node and its associated
   * employee instance.
   */
  @Override
  protected IVisual updateVisual( IRenderContext context, IVisual oldVisual, INode node ) {
    double zoom = context.getZoom();
    if (zoom > DETAIL_THRESHOLD) {
      if (oldVisual instanceof DetailVisual) {
        ((DetailVisual) oldVisual).update(node);
        return oldVisual;
      }
    } else if (zoom > INTERMEDIATE_THRESHOLD) {
      if (oldVisual instanceof IntermediateVisual) {
        ((IntermediateVisual) oldVisual).update(node);
        return oldVisual;
      }
    } else {
      if (oldVisual instanceof OverviewVisual) {
        ((OverviewVisual) oldVisual).update(node);
        return oldVisual;
      }
    }
    return createVisual(context, node);
  }

  /**
   * Base class for displaying employee information.
   */
  private abstract static class AbstractVisual implements IVisual {
    final Font nameFont;
    final Font dataFont;
    final int nameAsc;
    final int nameFontHeight;
    final int dataAsc;
    final int dataFontHeight;

    double x;
    double y;
    double width;
    double height;

    AbstractVisual( int nameFontSize, int dataFontSize ) {
      this.nameFont = newFont(nameFontSize);
      this.dataFont = newFont(dataFontSize);

      // calculate ascent and maximum font height
      Graphics2D tmp = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY).createGraphics();
      tmp.setFont(nameFont);
      FontMetrics fontMetrics = tmp.getFontMetrics();
      this.nameAsc = fontMetrics.getMaxAscent();
      this.nameFontHeight = this.nameAsc + fontMetrics.getMaxDescent() + fontMetrics.getLeading();
      tmp.setFont(dataFont);
      fontMetrics = tmp.getFontMetrics();
      this.dataAsc = fontMetrics.getMaxAscent();
      this.dataFontHeight = this.dataAsc + fontMetrics.getMaxDescent() + fontMetrics.getLeading();
    }

    /**
     * Updates geometry and displayed information for the given node and its
     * associated employee.
     */
    void update( INode node ) {
      IRectangle nl = node.getLayout();
      x = nl.getX();
      y = nl.getY();
      width = nl.getWidth();
      height = nl.getHeight();

      updateEmployeeData((Employee) node.getTag());
    }

    /**
     * Updates the displayed information for the given employee.
     */
    abstract void updateEmployeeData( Employee employee );

    /**
     * Updates the Graphics
     */
    @Override
    public void paint( IRenderContext context, Graphics2D graphics ) {
      Graphics2D newGraphics = (Graphics2D) graphics.create();
      newGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

      paintBorder(newGraphics);
      paintContent(newGraphics);
      newGraphics.dispose();
    }

    /**
     * Paints the employee data.
     */
    abstract void paintContent(Graphics2D graphics );

    /**
     * Paints the border of the rectangle.
     */
    void paintBorder( Graphics2D graphics ) {
      graphics.setColor(Color.BLACK);
      graphics.setStroke(new BasicStroke(3));
      graphics.draw(new Rectangle2D.Double(x, y, width, height));
    }

    /**
     * Paints the specified text at the given position.
     * @param x the x-coordinate of the upper left corner of the text bounds.
     * @param y the y-coordinate of the upper left corner of the text bounds.
     * This differs from {@link Graphics2D#drawString(String, int, int)}
     * which uses the baseline for positioning.
     */
    int paintText( Graphics2D graphics, String text, double x, double y, boolean headline ) {
      AffineTransform oldTransform = graphics.getTransform();
      graphics.translate(x, y);

      graphics.setFont(headline ? nameFont : dataFont);
      graphics.setColor(headline ? Color.BLUE : Color.BLACK);

      int asc = headline ? nameAsc : dataAsc;
      graphics.drawString(text, 0, asc);

      graphics.setTransform(oldTransform);

      int textOffset = 3;
      return textOffset + (headline ? nameFontHeight : dataFontHeight);
    }

    /**
     * Creates a new {@link Font} instance with the specified size.
     */
    static Font newFont( int size ) {
      return new Font("Dialog", Font.PLAIN, size);
    }
  }

  /**
   * Displays the most pieces of information for high-detail rendering.
   */
  private static final class DetailVisual extends AbstractVisual {
    /** The name of the employee. */
    private String name;
    /** The position of the employee. */
    private String position;
    /** The eMail address of the employee. */
    private String mail;
    /** The phone number of the employee. */
    private String phone;
    /** The fax number of the employee. */
    private String fax;

    DetailVisual() {
      super(16, 10);
    }

    /**
     * Updates the employee's name, position, mail address, phone and fax
     * numbers.
     */
    @Override
    void updateEmployeeData( Employee employee ) {
      name = employee.getName();
      position = employee.getPosition();
      mail = employee.getMail();
      phone = employee.getPhone();
      fax = employee.getFax();
    }

    /**
     * Displays the employee's name, position, mail address, phone and fax
     * numbers.
     */
    @Override
    void paintContent( Graphics2D graphics ) {
      double x = this.x + 10;
      double y = this.y + 2;

      y += paintText(graphics, name, x, y, true);
      y += paintText(graphics, position, x, y, false);
      y += 8;
      y += paintText(graphics, mail, x, y, false);
      paintText(graphics, phone, x, y, false);
      paintText(graphics, fax, x + 65, y, false);
    }
  }

  /**
   * Displays some but not all information for intermediate detail rendering.
   */
  private static final class IntermediateVisual extends AbstractVisual {
    /** The name of the employee. */
    private String name;
    /** The position of the employee. */
    private String position;

    IntermediateVisual() {
      super(26, 15);
    }

    /**
     * Updates the employee's name and position.
     */
    @Override
    void updateEmployeeData( Employee employee ) {
      name = employee.getName();
      position = employee.getPosition();
    }

    /**
     * Displays the employee's name and position.
     */
    @Override
    void paintContent( Graphics2D graphics ) {
      double x = this.x + 10;
      double y = this.y + 2;

      y += paintText(graphics, name, x, y, true);
      paintText(graphics, position, x, y, false);
    }
  }

  /**
   * Displays only the employee name for low-detail rendering.
   */
  private static final class OverviewVisual extends AbstractVisual {
    /** The name of the employee. */
    private String name;

    OverviewVisual() {
      super(35, 35);
    }

    /**
     * Updates the employee's name.
     */
    @Override
    void updateEmployeeData( Employee employee ) {
      name = employee.getName();
    }

    /**
     * Displays the employee's name.
     */
    @Override
    void paintContent( Graphics2D graphics ) {
      paintText(graphics, name, x + 10, y + 20, true);
    }
  }
}
