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
package toolkit;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasPrintable;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;

/**
 * A JPanel that previews the current output of a given CanvasPrintable. The preview consists of a grid of rectangles representing the papers that
 * would be printed with a given PageFormat.
 */
public class PagePanel extends JPanel implements Scrollable {

  // Some useful constants, for simplicity
  private static final int DESKTOP_INSETS = 10;
  private static final int DROP_SHADOW_OFFSET = 5;
  private static final double PRINT_TO_SCREEN_DPI = 96d / 72d;

  /**
   * The PrintInfo from the CanvasPrintable which tells the PagePanel how many columns and rows there are to paint.
   */
  private CanvasPrintable.PrintInfo printInfo;

  /**
   * The zoom level of the panel.
   */
  private double zoom;

  /**
   * The PageFormat in which the pages will be printed.
   */
  private PageFormat pageFormat;

  /**
   * The printable that will do the actual printing. Used to fill the content of the paper previews in the panel.
   */
  private CanvasPrintable printable;

  /**
   * Creates a new PagePanel and initializes the PrintInfo, minimumSize of the component and the zoom using the given values.
   * @param printable the printable that will provide the contents of the printing.
   * @param pageFormat the PageFormat to use for the papers to print.
   */
  public PagePanel(CanvasPrintable printable, PageFormat pageFormat) {
    this.printable = printable;
    this.pageFormat = pageFormat;
    // determine the number of columns and rows there are to paint
    updatePrintInfo();
    // calculate and set the minimum size of the component (the size where all papers have zero width and height)
    setMinimumSize(new Dimension(printInfo.getColumnCount() * 2 * DESKTOP_INSETS + DROP_SHADOW_OFFSET,
        printInfo.getRowCount() * 2 * DESKTOP_INSETS + DROP_SHADOW_OFFSET));
    // init the zoom to some reasonable amount
    setZoom(0.5);
  }

  /**
   * Calculates the zoom that would be necessary to fit the current contents of the preview into the given width
   * and sets the zoom for this panel accordingly. The minimum value set is 0.05.
   */
  public void zoomToFitWidth(int width) {
    double zoom = getWidthRatio(width);
    setZoom(Math.max(0.05d, zoom));
  }

  /**
   * Calculates the zoom that would be necessary to fit the current contents of the preview into the given width or height, whichever is smaller.
   * The minimum returned value is 0.05.
   */
  public void zoomToFit(int width, int height) {
    double widthRatio = getWidthRatio(width);
    double heightRatio = getHeightRatio(height);
    setZoom(Math.max(0.05d, Math.min(widthRatio, heightRatio)));
  }

  /**
   * Calculates the ratio of the given width minus the insets to the total width.
   */
  private double getWidthRatio(int width) {
    double totalInsetsWidth = printInfo.getColumnCount() * 2 * DESKTOP_INSETS;
    double totalPaperWidth = printInfo.getColumnCount() * pageFormat.getWidth() * PRINT_TO_SCREEN_DPI;
    return (width - totalInsetsWidth) / totalPaperWidth;
  }

  /**
   * Calculates the ratio of the given height minus the insets to the total height.
   */
  private double getHeightRatio(int height) {
    double totalInsetsHeight = printInfo.getRowCount() * 2 * DESKTOP_INSETS;
    double totalPaperHeight = printInfo.getRowCount() * pageFormat.getHeight() * PRINT_TO_SCREEN_DPI;
    return (height - totalInsetsHeight) / totalPaperHeight;
  }

  /**
   * Force a recalculation and revalidate / repaint.
   */
  public void adjust() {
    double completePaperFrameWidth = 2 * DESKTOP_INSETS + (pageFormat.getWidth() * zoom * PRINT_TO_SCREEN_DPI);
    double completePaperFrameHeight = 2 * DESKTOP_INSETS + pageFormat.getHeight() * zoom * PRINT_TO_SCREEN_DPI;
    setPreferredSize(new Dimension((int) Math.rint(printInfo.getColumnCount() * completePaperFrameWidth),
        (int) Math.rint(printInfo.getRowCount() * completePaperFrameHeight)));
    revalidate();
    repaint();
  }

  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    Graphics2D g2d = (Graphics2D) graphics.create();
    // find out if the page format is landscape or portrait
    Paper paper = this.pageFormat.getPaper();
    boolean isPortrait = this.pageFormat.getOrientation() == PageFormat.PORTRAIT;
    // the bounds for one paper
    RectD paperBounds = new RectD(0, 0, isPortrait ? paper.getWidth() : paper.getHeight(), isPortrait ? paper.getHeight() : paper.getWidth());
    // include the insets that are set for the JComponent
    Insets componentInsets = super.getInsets();
    // the real zoom
    double zoom = PRINT_TO_SCREEN_DPI * this.zoom;
    // an awt shape instance that will be used for the drawing.
    Rectangle2D.Double paperRect = new Rectangle2D.Double(0,0,0,0);

    try {
      // draw each paper separately
      for (int row = 0; row < this.printInfo.getRowCount(); row++) {
        for (int column = 0; column < this.printInfo.getColumnCount(); column++) {

          // find out the location of the paper in the grid, taking into account insets, spacing and so on
          double insetWidthSoFar = column * 2 * DESKTOP_INSETS + DESKTOP_INSETS;
          double insetHeightSoFar = row * 2 * DESKTOP_INSETS + DESKTOP_INSETS;
          double paperWidthSoFar = column * (paperBounds.getWidth() * zoom);
          double paperHeightSoFar = row * (paperBounds.getHeight() * zoom);

          double deltaX = componentInsets.left + paperWidthSoFar + insetWidthSoFar;
          double deltaY = componentInsets.top + paperHeightSoFar + insetHeightSoFar;

          paperBounds = new RectD(deltaX, deltaY, paperBounds.getWidth(), paperBounds.getHeight());

          // first draw the frame before drawing the content
          drawPaperFrame(g2d, paperBounds, zoom, paperRect);

          double transX = paperRect.x;
          double transY = paperRect.y;

          // try draw the content of the paper using the CanvasPrintable on top of the paper frame we just created.
          int result = drawPaperContent(g2d, paperBounds, zoom, row, column, transX, transY, paperRect);

          if (result == Printable.NO_SUCH_PAGE) {
            // draws a placeholder in case the page requested to be drawn doesn't exist
            // actually doesn't happy when the rows and columns are not manipulated since the panel always calculates the exact amount of papers
            // included anyways for demonstrational purposes
            drawEmptyPaper(g2d, paperBounds, zoom, paperRect);
          } else {
            // else everything was alright and we can just draw the border of the imageable area and move on
            paperRect.setFrame(
                this.pageFormat.getImageableX() * zoom + transX,
                this.pageFormat.getImageableY() * zoom + transY,
                this.pageFormat.getImageableWidth() * zoom,
                this.pageFormat.getImageableHeight() * zoom);
            drawPrintableAreaBorder(g2d, paperRect);
          }
        }
      }
    } finally {
      g2d.dispose();
    }
  }

  /**
   * Draws a "paper", which means a rectangle with white fill paint, black border and a drop shadow.
   * The content of the paper which will be on the print output is drawn later on top of it.
   * @param g2d the context to draw onto.
   * @param bounds the bounds in the grid of the paper to draw.
   * @param zoom the current onscreen zoom of the panel.
   * @param paperRect the shape to use for drawing.
   */
  private void drawPaperFrame(Graphics2D g2d, RectD bounds, double zoom, Rectangle2D.Double paperRect) {
    // draw drop shadow
    paperRect.x = bounds.getX() + DROP_SHADOW_OFFSET;
    paperRect.y = bounds.getY() + DROP_SHADOW_OFFSET;
    paperRect.width = bounds.getWidth() * zoom;
    paperRect.height = bounds.getHeight() * zoom;

    g2d.setColor(this.getBackground().darker());
    g2d.fill(paperRect);

    // draw paper
    paperRect.x = bounds.getX();
    paperRect.y = bounds.getY();

    g2d.setColor(Color.white);
    g2d.fill(paperRect);
    g2d.setColor(Color.black);
    g2d.draw(paperRect);
  }

  /**
   * Draws the actual content of a paper that would be later printed on it.
   * @param g2d the context to draw onto.
   * @param bounds the bounds in the grid of the paper to draw.
   * @param zoom the current onscreen zoom of the panel.
   * @param row the index of the row of the paper to draw.
   * @param column the index of the column of the paper to draw.
   * @param transX the x offset of the content in paper coordinates.
   * @param transY the y offset of the content in paper coordinates.
   * @param paperRect the shape to use for drawing.
   */
  private int drawPaperContent(Graphics2D g2d, RectD bounds, double zoom, int row, int column,
                               double transX, double transY, Rectangle2D.Double paperRect) {
    int result;
    try {
      AffineTransform oldTrans = g2d.getTransform();
      Shape oldClip = g2d.getClip();
      g2d.translate(transX, transY);
      g2d.scale(zoom, zoom); // zoom!
      Shape prevClip = g2d.getClip();

      paperRect.setFrame(
          this.pageFormat.getImageableX(),
          this.pageFormat.getImageableY(),
          this.pageFormat.getImageableWidth(),
          this.pageFormat.getImageableHeight());

      Rectangle paperContents = new Rectangle(0, 0, (int) Math.ceil(bounds.getWidth()), (int) Math.ceil(bounds.getHeight()));
      if (prevClip.intersects(paperContents)) {
        g2d.clip(paperRect);
        result = this.printable.print(g2d, this.pageFormat, column + this.printInfo.getColumnCount() * row);
      } else {
        result = Printable.PAGE_EXISTS;
      }
      g2d.setTransform(oldTrans);
      g2d.setClip(oldClip);
    } catch (Exception pe) {
      result = Printable.NO_SUCH_PAGE;
      pe.printStackTrace();
    }
    return result;
  }

  /**
   * Draws the content for an "empty" paper, which means a paper for which the result of the printing was Printable.NO_SUCH_PAGE.
   * The way this panel and printable is build and maintained this shouldn't happen in this demo, but for completeness sake it is
   * included.
   * This draws a big red "X" across the paper on a gray background.
   * @param g2d the context to draw onto.
   * @param bounds the bounds in the grid of the paper to draw.
   * @param zoom the current onscreen zoom of the panel.
   * @param paperRect the shape to use for drawing.
   */
  private void drawEmptyPaper(Graphics2D g2d, RectD bounds, double zoom, Rectangle2D.Double paperRect) {

    paperRect.x = bounds.getX();
    paperRect.y = bounds.getY();
    paperRect.width = bounds.getWidth() * zoom;
    paperRect.height = bounds.getHeight() * zoom;

    // fill the rectangular paper area
    g2d.setColor(Color.lightGray);
    g2d.fill(paperRect);

    // draw a red "X" across the paper
    Line2D.Double line = new Line2D.Double(paperRect.x, paperRect.y, paperRect.x + paperRect.width, paperRect.y + paperRect.height);
    g2d.setColor(Color.red);
    g2d.draw(line);
    line.x1 = line.x2;
    line.x2 = paperRect.x;
    g2d.draw(line);

    // draw the border
    g2d.setColor(Color.darkGray);
    g2d.draw(paperRect);
  }

  /**
   * Draws the given rect in a light gray color. This is used to draw the border for the imageable area of the paper.
   * @param g2d the context to draw onto.
   * @param paperRect the shape to use for drawing.
   */
  private void drawPrintableAreaBorder(final Graphics2D g2d, final Rectangle2D.Double paperRect) {
    g2d.setColor(Color.lightGray);
    g2d.draw(paperRect);
  }

  /**
   * Updates the number of columns and rows using the CanvasPrintable.
   */
  public void updatePrintInfo(){
    this.printInfo = this.printable.createPrintInfo(getPageFormat());
  }

  /**
   * Returns the current set PageFormat.
   */
  public PageFormat getPageFormat() {
    return pageFormat;
  }

  /**
   * Sets the PageFormat to use for the papers in the preview.
   */
  public void setPageFormat(PageFormat pageFormat) {
    this.pageFormat = pageFormat;
  }

  /**
   * Gets the zoom. The default value is <code>0.5</code>.
   */
  public double getZoom() {
    return zoom;
  }

  /**
   * Sets the zoom. Triggers a call to adjust() to make the changes visible.
   * The default value is <code>0.5</code>.
   */
  public void setZoom(double zoom) {
    if (this.zoom != zoom) {
      if (zoom < 0.05d) {
        zoom = 0.05d;
      }
      this.zoom = zoom;

      adjust();
    }
  }

  /*
    Scrollable specific methods that we implement trivially.
   */

  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  public int getScrollableBlockIncrement(Rectangle rectangle, int param, int param2) {
    return 10;
  }

  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  public int getScrollableUnitIncrement(Rectangle rectangle, int param, int param2) {
    return 20;
  }
}
