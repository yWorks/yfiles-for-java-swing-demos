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
package complete.collapse;

import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

/**
 * An implementation of {@link AbstractNodeStyle} which visualizes the nodes in the tree.
 * The drawing depends on the current state of the node which is stored in the node's tag.
 */
class CollapsibleNodeStyle extends AbstractNodeStyle {

  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    return new CollapsibleNodeVisual(node);
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    // we override updateVisual from AbstractNodeStyle because the superclass method always creates a new
    // visual. A better solution is to preserve the old visual and just update it with the needed information.
    if (oldVisual instanceof CollapsibleNodeVisual){
      ((CollapsibleNodeVisual) oldVisual).setNode(node);
      return oldVisual;
    } else {
      return super.updateVisual(context, oldVisual, node);
    }
  }

  /**
   * A {@link IVisual} that draws the visual representation of a
   * node depending on its {@link complete.collapse.CollapsedState}.
   */
  private static class CollapsibleNodeVisual implements IVisual {
    private static final Color COLLAPSED_COLOR_1 = new Color(255, 204, 0);
    private static final Color COLLAPSED_COLOR_2 = new Color(255, 153, 0);
    private static final Color EXPANDED_COLOR_1 = new Color(204, 255, 255);
    private static final Color EXPANDED_COLOR_2 = new Color(153, 204, 255);
    private static final Color LEAF_COLOR_1 = new Color(204, 255, 153);
    private static final Color LEAF_COLOR_2 = new Color(153, 204, 51);

    private INode node;

    private CollapsibleNodeVisual(INode node) {
      this.node = node;
    }

    /**
     * Sets the node for this visual to draw.
     */
    public void setNode(INode node) {
      this.node = node;
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      // remember the old state of the graphics context
      final Paint oldPaint = g.getPaint();
      final AffineTransform oldTransform = g.getTransform();

      try {
        CollapsedState state = (CollapsedState) node.getTag();

        // get the background colors depending on the collapsed state
        Color backgroundColor1 = getBackgroundColor(state, COLLAPSED_COLOR_1, EXPANDED_COLOR_1, LEAF_COLOR_1);
        Color backgroundColor2 = getBackgroundColor(state, COLLAPSED_COLOR_2, EXPANDED_COLOR_2, LEAF_COLOR_2);
        paintNodeShape(g, backgroundColor1, backgroundColor2);

        PointD center = node.getLayout().getCenter();
        g.translate(center.getX(), center.getY());
        if (state == CollapsedState.COLLAPSED) {
          paintPlusSign(g);
        } else if (state == CollapsedState.EXPANDED) {
          paintMinusSign(g);
        }
      } finally {
        g.setPaint(oldPaint);
        g.setTransform(oldTransform);
      }
    }

    private void paintMinusSign(Graphics2D g) {
      g.setPaint(Color.LIGHT_GRAY);
      g.fillRect(-10, -4, 20, 8);
      g.setPaint(Color.WHITE);
      g.fillRect(-9, -3, 18, 6);
      g.setPaint(Color.DARK_GRAY);
      g.fillRect(-8, -2, 16, 4);
    }

    private void paintPlusSign(Graphics2D g) {
      g.setPaint(Color.LIGHT_GRAY);
      g.fillRect(-10, -4, 20, 8);
      g.fillRect(-4, -10, 8, 20);
      g.setPaint(Color.WHITE);
      g.fillRect(-9, -3, 18, 6);
      g.fillRect(-3, -9, 6, 18);
      g.setPaint(Color.DARK_GRAY);
      g.fillRect(-8, -2, 16, 4);
      g.fillRect(-2, -8, 4, 16);
    }

    private void paintNodeShape(Graphics2D g, Color backgroundColor1, Color backgroundColor2) {
      double x = node.getLayout().getX();
      double y = node.getLayout().getY();
      double w = node.getLayout().getWidth();
      double h = node.getLayout().getHeight();
      // draw the shape of the node
      Paint paint = new GradientPaint((float) x, (float) y, backgroundColor1, (float) x, (float) (y + h), backgroundColor2);
      Shape rect = new RoundRectangle2D.Double(x, y, w, h, 10, 10);
      Pen.getLightGray().commit(g);
      g.setPaint(paint);
      g.fill(rect);
      g.draw(rect);
    }

    private Color getBackgroundColor(CollapsedState state, Color collapsedColor, Color expandedColor, Color leafColor) {
      switch (state) {
        case COLLAPSED:
          return collapsedColor;
        case EXPANDED:
          return expandedColor;
        default:
          return leafColor;
      }
    }
  }
}
