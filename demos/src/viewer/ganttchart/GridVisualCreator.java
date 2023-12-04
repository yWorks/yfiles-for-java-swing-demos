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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

/**
 * Manages and renders the background of the {@link GanttChartDemo#graphComponent}.
 */
public class GridVisualCreator implements IVisualCreator {
  private final GanttDataUtil data;

  /**
   * Initializes a new {@code GridVisualCreator} instance for the given project
   * schedule.
   */
  public GridVisualCreator( GanttDataUtil data ) {
    this.data = data;
  }

  /**
   * Creates the grid visualization.
   */
  @Override
  public IVisual createVisual( IRenderContext context ) {
    return new GridVisual(data);
  }

  /**
   * Updates the grid visualization.
   */
  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    if (oldVisual instanceof GridVisual) {
      return oldVisual;
    } else {
      return createVisual(context);
    }
  }



  /**
   * Renders a grid whose horizontal lines correspond to task boundaries
   * and whose vertical lines correspond to day and month boundaries.
   */
  private static final class GridVisual implements IVisual{
    private static final Color LINE_COLOR = new Color(204, 204, 204);
    private static final Pen SOLID_1 = new Pen(LINE_COLOR, 1);
    private static final Pen SOLID_3 = new Pen(LINE_COLOR, 3);
    private static final Pen DASH_1 = newDashedPen(LINE_COLOR);

    private final GanttDataUtil data;

    GridVisual( GanttDataUtil data ) {
      this.data = data;
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

      // paint the grid
      Paint oldColor = g.getPaint();
      Stroke oldStroke = g.getStroke();

      Line2D.Double line = new Line2D.Double(0, viewport.getY(), 0, viewport.getMaxY());
      drawDays(g, beginX, endX, line);
      drawMonths(g, beginX, endX, beginDate, line);
      drawTaskSeparators(g, beginX, endX, line);
      
      g.setPaint(oldColor);
      g.setStroke(oldStroke);
    }


    /**
     * Draws the vertical day separators
     */
    private void drawDays(
      Graphics2D g, int beginX, int endX, Line2D line
    ) {
      SOLID_1.commit(g);

      int x = beginX;
      while (x < endX) {
        line.setLine(x, line.getY1(), x, line.getY2());
        g.draw(line);

        x += GanttDataUtil.DAY_WIDTH;
      }
    }

    /**
     * Draws the vertical month separators
     */
    private void drawMonths(
      Graphics2D g, int beginX, int endX, LocalDate beginDate, Line2D line
    ) {
      SOLID_3.commit(g);

      int x = beginX;
      LocalDate currentDate = beginDate;
      while (x < endX) {
        line.setLine(x, line.getY1(), x, line.getY2());
        g.draw(line);

        x += currentDate.lengthOfMonth() * GanttDataUtil.DAY_WIDTH;
        currentDate = currentDate.plusMonths(1);
      }
    }

    /**
     * Draws the horizontal task separators.
     */
    private void drawTaskSeparators(
      Graphics2D g, int beginX, int endX, Line2D line
    ) {
      DASH_1.commit(g);

      // draw the separators
      for (Task task : data.getTasks()) {
        double y = data.getTaskY(task)
          + data.getCompleteTaskHeight(task)
          + GanttDataUtil.TASK_SPACING;
        line.setLine(beginX, y, endX, y);
        g.draw(line);
      }
    }
  }

  private static Pen newDashedPen( Color c ) {
    Pen pen = new Pen(c, 1);
    pen.setDashStyle(new DashStyle(new double[] {4.0, 4.0}, 2.0));
    return pen;
  }
}
