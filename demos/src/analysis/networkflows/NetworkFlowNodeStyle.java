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
package analysis.networkflows;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 * A NetworkFlowNodeStyle represents the flow that is regulated at the according node.
 * By setting a tag, the flow can be adjusted for this node.
 */
public class NetworkFlowNodeStyle extends AbstractNodeStyle {
  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    NetworkFlowNodeVisual visual = new NetworkFlowNodeVisual();
    visual.update(context, node);
    return visual;
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (oldVisual instanceof NetworkFlowNodeVisual) {
      ((NetworkFlowNodeVisual) oldVisual).update(context, node);
      return oldVisual;
    }
    return createVisual(context, node);
  }

  /**
   * The visual rendering the network flow node.
   */
  private static final class NetworkFlowNodeVisual implements IVisual {
    private static final Color SOURCE_BORDER_COLOR = Colors.YELLOW_GREEN;
    private static final Color SINK_BORDER_COLOR = Colors.INDIAN_RED;
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final BasicStroke BORDER_STROKE = new BasicStroke(3);

    private static final Color FLOW_COLOR_1 = Colors.DARK_BLUE;
    private static final Color FLOW_COLOR_2 = Colors.CORNFLOWER_BLUE;
    private static final Color BACKGROUND_COLOR = Colors.LIGHT_GRAY;

    private static final double INSET = 15;

    private final Line2D pipeBorder;
    private final Path2D pipePath;
    private final Rectangle2D infoShape;

    private RectD bounds;
    private double supply;
    private double flow;
    private boolean source;
    private boolean sink;

    NetworkFlowNodeVisual() {
      pipeBorder = new Line2D.Double();
      pipePath = new Path2D.Double();
      infoShape = new Rectangle2D.Double();
      bounds = RectD.EMPTY;
    }


    void update(IRenderContext context, INode node) {
      bounds = node.getLayout().toRectD();

      NodeData flowData = (NodeData) node.getTag();
      supply = flowData.getSupply();
      flow = flowData.getFlow();

      IGraph graph = ((GraphComponent) context.getCanvasComponent()).getGraph();
      int inDegree = graph.inDegree(node);
      int outDegree = graph.outDegree(node);
      source = inDegree == 0 && outDegree != 0;
      sink = inDegree != 0 && outDegree == 0;

      // update pipe path
      double w = bounds.getWidth();
      double h = bounds.getHeight();
      pipePath.reset();
      if (source) {
        setHalfPipePath(w, w, h);
      } else if (sink) {
        setHalfPipePath(0, w, h);
      } else {
        setFullPipePath(w, h);
      }
    }

    private void setHalfPipePath(double endX, double w, double h) {
      double x1 = w * 0.5;
      double y1 = h * 0.333;
      double x2 = endX;
      double y2 = 5;

      pipePath.moveTo(x1, y1);
      pipePath.quadTo((x1 + x2) * 0.5, y1, x2, y2);

      double x3 = endX;
      double y3 = h - 5;
      double x4 = w * 0.5;
      double y4 = h * 0.666;

      pipePath.lineTo(x3, y3);
      pipePath.quadTo((x3 + x4) * 0.5, y4, x4, y4);

      pipePath.closePath();
    }

    private void setFullPipePath(double w, double h) {
      pipePath.moveTo(0, 0);
      pipePath.quadTo(w * 0.5, h * 0.5, w, 0);

      pipePath.lineTo(w, h);
      pipePath.quadTo(w * 0.5, h * 0.5, 0, h);

      pipePath.closePath();
    }


    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      Graphics2D gfx = (Graphics2D) g.create();
      gfx.translate(bounds.getX(), bounds.getY());

      paintPipe(gfx);
      paintInfo(gfx, bounds.getWidth(), bounds.getHeight());

      gfx.dispose();
    }

    private void paintPipe(Graphics2D gfx) {
      // pipe path
      gfx.setPaint(BACKGROUND_COLOR);
      gfx.fill(pipePath);

      // pipe border
      if (!source) {
        paintPipeBorder(gfx, 0);
      }
      if (!sink) {
        paintPipeBorder(gfx, bounds.getWidth());
      }
    }

    private void paintPipeBorder(Graphics2D gfx, double x) {
      gfx.setColor(BORDER_COLOR);
      gfx.setStroke(BORDER_STROKE);
      pipeBorder.setLine(x, 0, x, bounds.getHeight());
      gfx.draw(pipeBorder);
    }

    /**
     * Visualizes flow and supply/demand information.
     */
    private void paintInfo(Graphics2D gfx, double w, double h) {
      double infoX = INSET;
      double infoWidth = w - 2 * INSET;
      final double supplyHeight = h * supply;

      // paint background
      infoShape.setRect(infoX, 0, infoWidth, h);
      gfx.setPaint(BACKGROUND_COLOR);
      gfx.fill(infoShape);

      // visualize incoming flow
      gfx.setPaint(new GradientPaint(0, 0, FLOW_COLOR_1, 0, (float) h, FLOW_COLOR_2));
      infoShape.setRect(
          infoX,
          h - flow,
          infoWidth,
          flow);
      gfx.fill(infoShape);

      // visualize supply/demand flow for min cost algorithm
      if (0 < supplyHeight) {
        gfx.setPaint(FLOW_COLOR_1);
        infoShape.setRect(infoX, h - supplyHeight, infoWidth, supplyHeight);
        gfx.fill(infoShape);
      } else if (supplyHeight < 0) {
        gfx.setPaint(FLOW_COLOR_2);
        infoShape.setRect(infoX, h - flow, infoWidth, Math.min(flow, -supplyHeight));
        gfx.fill(infoShape);
      }

      // paint border
      infoShape.setRect(infoX, 0, infoWidth, h);
      gfx.setStroke(BORDER_STROKE);
      if (source) {
        gfx.setPaint(SOURCE_BORDER_COLOR);
      } else if (sink) {
        gfx.setPaint(SINK_BORDER_COLOR);
      } else {
        gfx.setPaint(BORDER_COLOR);
      }
      gfx.draw(infoShape);

      paintText(gfx, w, h, flow);
    }

    private void paintText(Graphics2D gfx, double w, double h, double value) {
      String text = Long.toString(Math.round(value));
      FontMetrics fm = gfx.getFontMetrics();
      int textWidth = fm.stringWidth(text);
      int textBaseHeight = fm.getMaxDescent() + 2;
      int textTotalHeight = fm.getMaxAscent() + textBaseHeight;

      gfx.translate((w - textWidth) * 0.5, h - textBaseHeight);
      gfx.setPaint(value > textTotalHeight / 2.0 ? Color.WHITE : Color.BLACK);
      gfx.drawString(text, 0, 0);
    }
  }
}
