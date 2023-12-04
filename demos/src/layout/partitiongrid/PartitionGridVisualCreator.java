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
package layout.partitiongrid;

import com.yworks.yfiles.algorithms.YList;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.layout.ColumnDescriptor;
import com.yworks.yfiles.layout.PartitionGrid;
import com.yworks.yfiles.layout.RowDescriptor;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.IAnimation;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Visualizes the partition grid that has been used in the last layout.
 * <p>
 * Each column and row is visualized by a {@link Rectangle}.
 * </p>
 * <p>
 * This class implements {@link IAnimation} and allows to animate the partition grid changes between two layout calculations.
 * </p>
 */
class PartitionGridVisualCreator implements IVisualCreator, IAnimation {

  // the columns and rows to draw
  private final ShapeVisual[] columns;
  private final ShapeVisual[] rows;

  // start and end positions of the columns and rows to animate between
  private RectD[] columnStarts;
  private RectD[] columnEnds;
  private RectD[] rowStarts;
  private RectD[] rowEnds;
  private PartitionGrid grid;

  /**
   * Creates a new instance with one column per color and one row per pen.
   * @param colors The colors used for the grid column fills.
   * @param pens The pens used for the grid row borders.
   */
  PartitionGridVisualCreator(List<Color> colors, List<Pen> pens) {
    //to colorize the background we use shape visuals
    columns = new ShapeVisual[colors.size()];

    //iterate over every color(therefore any column) and create the color according shapeVisual
    for (int i = 0; i < colors.size(); i++) {
      columns[i] = new ShapeVisual(new Rectangle2D.Double(0, 0, 10, 10));

      //get current color
      Color color = colors.get(i);

      //create fill color with same color as currentColor plus ne Alpha-Value
      Paint fill = new Color(color.getRed(), color.getGreen(), color.getBlue(), 77);

      columns[i].setFill(fill);
    }

    rows = new ShapeVisual[pens.size()];
    for (int i = 0; i < pens.size(); i++) {
      rows[i] = new ShapeVisual(new Rectangle2D.Double(0, 0, 10, 10));
      rows[i].setFill(Colors.TRANSPARENT);
      rows[i].setPen(pens.get(i));
    }
  }

  // region IVisualCreator implementation

  @Override
  public VisualGroup createVisual(IRenderContext renderContext) {
    // create a new VisualGroup and update it with the current state
    VisualGroup container = new VisualGroup();
    for (ShapeVisual column: columns) {
      container.add(column);
    }
    for (ShapeVisual row: rows) {
      container.add(row);
    }
    return container;
  }

  @Override
  public IVisual updateVisual(IRenderContext renderContext, IVisual oldVisual) {
    return oldVisual;
  }

  // endregion

  // region IAnimation implementation

  public void initialize() {
    // calculate min and max values of the partition grid bounds for the start and the end of the animation
    double minStartX = Double.MAX_VALUE;
    double maxStartX = Double.MIN_VALUE;
    double minStartY = Double.MAX_VALUE;
    double maxStartY = Double.MIN_VALUE;
    double minEndX = Double.MAX_VALUE;
    double maxEndX = Double.MIN_VALUE;
    double minEndY = Double.MAX_VALUE;
    double maxEndY = Double.MIN_VALUE;

    // looking at the y-coordinate and height of each row before and after the layout we can define min/maxStart/EndY
    ArrayList gridRows = new ArrayList();
    grid.getRows().forEach(gridRows::add);
    for (int i = 0; i < gridRows.size(); i++) {
      RowDescriptor rowDescriptor = (RowDescriptor) gridRows.get(i);
      ShapeVisual rowRect = rows[i];
      Rectangle rowRectBounds = rowRect.getShape().getBounds();
      minStartY = Math.min(minStartY, rowRectBounds.getY());
      maxStartY = Math.max(maxStartY, rowRectBounds.getY() + rowRectBounds.getHeight());
      minEndY = Math.min(minEndY, rowDescriptor.getComputedPosition());
      maxEndY = Math.max(maxEndY, rowDescriptor.getComputedPosition() + rowDescriptor.getComputedHeight());
    }

    // looking at the x-coordinate and width of each column before and after the layout we can define min/maxStart/EndX
    ArrayList columns = new ArrayList();
    grid.getColumns().forEach(columns::add);
    columnStarts = new RectD[columns.size()];
    columnEnds = new RectD[columns.size()];
    for (int i = 0; i < columns.size(); i++) {
      ColumnDescriptor columnDescriptor = (ColumnDescriptor) columns.get(i);
      ShapeVisual columnRect = this.columns[i];

      double startX = columnRect.getShape().getBounds().getX();
      double startWidth = columnRect.getShape().getBounds().getWidth();
      minStartX = Math.min(minStartX, startX);
      maxStartX = Math.max(maxStartX, startX + startWidth);

      double endX = columnDescriptor.getComputedPosition();
      double endWidth = columnDescriptor.getComputedWidth();
      minEndX = Math.min(minEndX, endX);
      maxEndX = Math.max(maxEndX, endX + endWidth);

      // for each column we store its layout before and after the layout
      columnStarts[i] = new RectD(startX, minStartY, startWidth, maxStartY - minStartY);
      columnEnds[i] = new RectD(endX, minEndY, endWidth, maxEndY - minEndY);
    }

    rowStarts = new RectD[gridRows.size()];
    rowEnds = new RectD[gridRows.size()];
    for (int i = 0; i < gridRows.size(); i++) {
      RowDescriptor rowDescriptor = (RowDescriptor) gridRows.get(i);
      ShapeVisual rowRect = this.rows[i];

      double startY = rowRect.getShape().getBounds().getY();
      double startHeight = rowRect.getShape().getBounds().getHeight();
      double endY = rowDescriptor.getComputedPosition();
      double endHeight = rowDescriptor.getComputedHeight();

      // for each row we store its layout before and after the layout
      rowStarts[i] = new RectD(minStartX, startY, maxStartX - minStartX, startHeight);
      rowEnds[i] = new RectD(minEndX, endY, maxEndX - minEndX, endHeight);
    }
  }

  public void animate(double time) {
    // for each row and column we calculate and set an intermediate layout corresponding to the time ratio
    for (int i = 0; i < this.rows.length; i++) {
      ShapeVisual row = this.rows[i];
      RectD rowStart = rowStarts[i];
      RectD rowEnd = rowEnds[i];

      double xPos = (rowStart.getX() + time * (rowEnd.getX() - rowStart.getX()));
      double yPos = (rowStart.getY() + time * (rowEnd.getY() - rowStart.getY()));
      double width = (rowStart.getWidth() + time * (rowEnd.getWidth() - rowStart.getWidth()));
      double height = (rowStart.getHeight() + time * (rowEnd.getHeight() - rowStart.getHeight()));

      //get shape of the ShapeVisual, cast to Rectangle2D and set the frame
      ((Rectangle2D.Double) row.getShape()).setFrame(xPos,yPos, width, height);
    }

    for (int i = 0; i < this.columns.length; i++) {
      ShapeVisual column = this.columns[i];
      RectD columnStart = columnStarts[i];
      RectD columnEnd = columnEnds[i];

      double xPos = (columnStart.getX() + time * (columnEnd.getX() - columnStart.getX()));
      double yPos = (columnStart.getY() + time * (columnEnd.getY() - columnStart.getY()));
      double width = (columnStart.getWidth() + time * (columnEnd.getWidth() - columnStart.getWidth()));
      double height = (columnStart.getHeight() + time * (columnEnd.getHeight() - columnStart.getHeight()));

      //get shape of the ShapeVisual, cast to Rectangle2D and set the frame
      ((Rectangle2D.Double) column.getShape()).setFrame(xPos, yPos, width, height);
    }
  }

  @Override
  public void cleanUp() {
    grid = null;
    rowStarts = null;
    rowEnds = null;
    columnStarts = null;
    columnEnds = null;
  }

  @Override
  public Duration getPreferredDuration() {
    return Duration.ofMillis(500);
  }

  // endregion

  /**
   * Sets the grid for the next animation.
   */
  void setGrid(PartitionGrid grid) {
    this.grid = grid;
  }
}
