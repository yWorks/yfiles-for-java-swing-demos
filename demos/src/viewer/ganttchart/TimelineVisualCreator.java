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
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

/**
 * A custom {@link IVisualCreator} implementation to draw the timeline
 * at the top of the demo.
 */
public class TimelineVisualCreator implements IVisualCreator {
  private final GanttDataUtil data;

  /**
   * Initializes a new {@code TimelineVisualCreator} instance for the given
   * project schedule.
   */
  public TimelineVisualCreator( GanttDataUtil data ) {
    this.data = data;
  }

  /**
   * Creates the time line visualization.
   */
  @Override
  public IVisual createVisual( IRenderContext context ) {
    return new TimeLineVisual(data);
  }

  /**
   * Updates the time line visualization.
   */
  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    if (oldVisual instanceof TimeLineVisual) {
      return oldVisual;
    } else {
      return createVisual(context);
    }
  }



  /**
   * Renders a time line of months and days.
   */
  private static class TimeLineVisual implements IVisual {
    private static final Color EVEN_COLOR = new Color(155, 195, 255);
    private static final Color ODD_COLOR = new Color(105, 145, 255);

    private final GanttDataUtil data;

    private final Pen whitePen;

    TimeLineVisual( GanttDataUtil data ) {
      this.data = data;
      whitePen = new Pen(Color.WHITE, 2);
    }

    @Override
    public void paint( IRenderContext context, Graphics2D g ) {
      CanvasComponent component = context.getCanvasComponent();

      RectD viewport = component.getViewport();
      double x = viewport.getX();
      double width = viewport.getWidth();

      // find the first day of the latest month to "start outside the viewport"
      LocalDate beginDate = data.getDate(x - 100).toLocalDate();
      LocalDate firstDayOfMonth = beginDate.with(TemporalAdjusters.firstDayOfMonth());
      int beginX = data.getX(firstDayOfMonth.atTime(LocalTime.MIN));

      // find the last day of the earliest month to "end outside the viewport"
      LocalDate endDate = data.getDate(x + width + 100).toLocalDate();
      LocalDate lastDayOfMonth = endDate.with(TemporalAdjusters.lastDayOfMonth());
      int endX = data.getX(lastDayOfMonth.atTime(LocalTime.MIN));

      // paint the time line
      Paint oldPaint = g.getPaint();
      Stroke oldStroke = g.getStroke();

      Rectangle2D.Double rect = new Rectangle2D.Double();
      drawMonths(g, beginX, endX, firstDayOfMonth, rect);
      drawDays(g, beginX, endX, firstDayOfMonth, rect);

      g.setStroke(oldStroke);
      g.setPaint(oldPaint);
    }

    /**
     * Draws the months in the time line.
     */
    private void drawMonths(
      Graphics2D g, int beginX, int endX, LocalDate date, Rectangle2D rect
    ) {
      LocalDate currentDate = date;

      int y = 5;
      int width = 0;
      int height = 30;
      boolean odd = currentDate.getMonthValue() % 2 != 0;

      for (int x = beginX; x < endX; x += width) {
        int monthDays = currentDate.lengthOfMonth();
        width = GanttDataUtil.DAY_WIDTH * monthDays;

        // draw the background for the current month
        rect.setRect(x, y, width, height);
        g.setColor(odd ? ODD_COLOR : EVEN_COLOR);
        g.fill(rect);
        whitePen.commit(g);
        g.draw(rect);

        // draw the month and year string in the horizontal and vertical center
        paintText(g, formatMonth(currentDate), x, y, width, height);

        currentDate = currentDate.plusMonths(1);
        odd = !odd;
      }
    }

    /**
     * Draws the days in the timeline.
     */
    private void drawDays(
      Graphics2D g, int beginX, int endX, LocalDate date, Rectangle2D rect
    ) {
      LocalDate currentDate = date;

      int y = 35;
      int width = GanttDataUtil.DAY_WIDTH;
      int height = 30;
      boolean odd = currentDate.getDayOfMonth() % 2 != 0;

      for (int x = beginX; x < endX; x += width) {
        // draw the background for the current day
        rect.setRect(x, y, width, height);
        g.setColor(odd ? ODD_COLOR : EVEN_COLOR);
        g.fill(rect);
        whitePen.commit(g);
        g.draw(rect);

        // draw the day string of the date (horizontal and vertical center of the box)
        paintText(g, formatDay(currentDate), x, y, width, height);

        currentDate = currentDate.plusDays(1);
        odd = !odd;
      }
    }

    private static void paintText(
      Graphics2D g, String text, int x, int y, int width, int height
    ) {
      AffineTransform oldTransform = g.getTransform();
      FontMetrics fm = g.getFontMetrics();
      int asc = fm.getAscent();
      g.translate(x + (width - fm.stringWidth(text)) * 0.5, y + (height + asc) * 0.5);
      g.drawString(text, 0, 0);
      g.setTransform(oldTransform);
    }

    private static String formatMonth( LocalDate date ) {
      return date.getMonth().toString() + " " + date.getYear();
    }

    private static String formatDay( LocalDate date ) {
      return String.valueOf(date.getDayOfMonth());
    }
  }
}
