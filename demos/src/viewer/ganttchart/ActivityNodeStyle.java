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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Creates stadium-shaped visualizations for activity nodes.
 * The visualizations include hatched sections for lead and follow-up time.
 */
public class ActivityNodeStyle extends AbstractNodeStyle {
  /**
   * Initializes a new {@code ActivityNodeStyle} instance.
   */
  public ActivityNodeStyle() {
  }

  /**
   * Creates a stadium-shaped visualization for the given activity node.
   * The visualization includes hatched sections for lead and follow-up time.
   */
  @Override
  protected IVisual createVisual( IRenderContext context, INode node ) {
    Activity activity = (Activity) node.getTag();
    IRectangle layout = node.getLayout();
    return new ActivityNodeVisual(layout, activity);
  }

  /**
   * Returns the stadium-shaped outline of the given activity node.
   */
  @Override
  protected GeneralPath getOutline( INode node ) {
    IRectangle nl = node.getLayout();
    double x = nl.getX();
    double y = nl.getY();
    double width = nl.getWidth();
    double height = nl.getHeight();
    double arcX = Math.min(width, height) * 0.5;
    double arcY = arcX;

    GeneralPath outline = new GeneralPath(12);
    outline.moveTo(x, y + arcY);
    outline.quadTo(x, y, x + arcX, y);
    outline.lineTo(x + width - arcX, y);
    outline.quadTo(x + width, y, x + width, y + arcY);
    outline.lineTo(x + width, y + height - arcY);
    outline.quadTo(x + width, y + height, x + width - arcX, y + height);
    outline.lineTo(x + arcX, y + height);
    outline.quadTo(x, y + height, x, y + height - arcY);
    outline.close();
    return outline;
  }



  private static final class ActivityNodeVisual implements IVisual {
    private final RoundRectangle2D.Double shape;
    private final Line2D.Double hatchLine;
    private final Pen borderPen;
    private final Pen hatchPen;
    private final Activity activity;

    /**
     * Creates a new activity node visual.
     * @param  activity The activity represented by this node
     */
    ActivityNodeVisual( IRectangle layout, Activity activity ) {
      this.activity = activity;

      this.borderPen = new Pen(activity.getTask().getColor(), 2);
      this.hatchPen = new Pen(activity.getTask().getColor(), 1);
      this.hatchLine = new Line2D.Double();

      // create the outline for a activity node (with lead and follow up time)
      RoundRectangle2D.Double shape = new RoundRectangle2D.Double();
      double r = Math.min(layout.getWidth(), layout.getHeight());
      shape.setRoundRect(layout.getX(), layout.getY(),
              layout.getWidth(),
              layout.getHeight(), r, r);
      this.shape = shape;
    }

    /**
     * Paints the activity node.
     */
    @Override
    public void paint( IRenderContext context, Graphics2D g ) {
      Shape oldClip = g.getClip();
      Color oldColor = g.getColor();
      Stroke oldStroke = g.getStroke();

      // draw the node outline (with lead and follow-up time)
      borderPen.commit(g);
      g.draw(shape);

      double minX = shape.getX();
      double minY = shape.getY();
      double w = shape.getWidth();
      double h = shape.getHeight();
      double maxX = minX + w;
      double maxY = minY + h;

      double offset = 8;

      // draw the hatches in the lead and follow-up time sections
      g.clip(shape);
      hatchPen.commit(g);
      for (double x = minX - h; x < maxX; x += offset){
        hatchLine.setLine(x, maxY, x + h, minY);
        g.draw(hatchLine);
      }

      // fill the main section, i.e. the area between lead and follow-up time
      double leadTimeWidth = activity.leadTimeWidth();
      double followUpTimeWidth = activity.followUpTimeWidth();
      if (leadTimeWidth > 0 || followUpTimeWidth > 0) {
        g.clip(new Rectangle2D.Double(minX + leadTimeWidth, minY, w - leadTimeWidth - followUpTimeWidth, h));
      }
      g.fill(shape);

      g.setStroke(oldStroke);
      g.setColor(oldColor);
      g.setClip(oldClip);
    }
  }
}
