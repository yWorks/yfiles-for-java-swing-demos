/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package tutorial02_CustomStyles.step02_NodeColor;

import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.graph.INode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.INodeStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractNodeStyle} as its base class.
 * This style visualizes nodes as balls with a semi-transparent gradient and
 * several reflections to achieve a shiny look.
 */
public class MySimpleNodeStyle extends AbstractNodeStyle {

  //////////////// New in this sample ////////////////
  private static final Color DEFAULT_BALL_COLOR = new Color(0, 130, 180, 200);

  private Color nodeColor;

  /**
   * Initializes a new <code>MySimpleNodeStyle</code> instance with a default node color.
   */
  public MySimpleNodeStyle() {
    nodeColor = DEFAULT_BALL_COLOR;
  }

  /**
   * Gets the fill color of the node.
   */
  public Color getNodeColor() {
    return nodeColor;
  }

  /**
   * Determines the color to use for filling the node. This implementation uses the {@link
   * MySimpleNodeStyle#getNodeColor()} unless the {@link com.yworks.yfiles.graph.INode#getTag()}}
   * is of type {@link java.awt.Color}, in which case that color overrides this style's setting.
   * @param node the node to determine the color for
   * @return the color for filling the node
   */
  public Color getNodeColor(INode node) {
    return node.getTag() instanceof Color ? (Color) node.getTag() : getNodeColor();
  }

  /**
   * Sets the fill color of the node.
   */
  public void setNodeColor(Color nodeColor) {
    this.nodeColor = nodeColor;
  }
  ////////////////////////////////////////////////////

  /**
   * Creates the visual for a node.
   */
  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    //////////////// New in this sample ////////////////
    // create the visual that paints the node
    return new BallVisual(node.getLayout(), getNodeColor(node));
    ////////////////////////////////////////////////////

  }

  /**
   * A {@link IVisual} that paints an ellipse with a semi-transparent gradient.
   * Three reflections of two ellipses and a closed curve make the ball look shiny.
   */
  private static class BallVisual implements IVisual {
    // distribution of the ball's colors along the gradient
    private static final float[] FRACTIONS = {0, 0.5f, 1};

    //the size and location of the ball to paint
    private IRectangle layout;

    //////////////// New in this sample ////////////////
    // the color of the ball
    private Color color;
    ////////////////////////////////////////////////////

    /**
     * Initializes a <code>BallVisual</code> instance with the given size, location and color.
     */
    public BallVisual(IRectangle layout, Color color) {
      this.layout = layout;
      this.color = color;
    }

    /**
     * Paints the shape and the reflections of the ball.
     * Note that we paint the ball at the origin and move the graphics context to the appropriate location.
     */
    @Override
    public void paint(IRenderContext context, Graphics2D gfx) {
      // we should not change the graphics context in this method since it is also used elsewhere. Thus we save its
      // state at the beginning and restore it at the end.
      Paint oldPaint = gfx.getPaint();
      AffineTransform oldTransform = gfx.getTransform();
      try {
        // transforms the graphic context to the location of the ball
        gfx.transform(AffineTransform.getTranslateInstance(layout.getX(), layout.getY()));

        // create the ball's shape
        double width = layout.getWidth();
        double height = layout.getHeight();
        Ellipse2D shape = new Ellipse2D.Double(0, 0, width, height);

        // create a semi-transparent gradient to fill the ball with
        double max = Math.max(width, height);
        double min = Math.min(width, height);

        //////////////// New in this sample ////////////////
        // create background gradient from specified background color
        Color[] colors = {new Color(
            Math.min(1.0f, 1.4f * color.getRed()/255f),
            Math.min(1.0f, 1.4f * color.getGreen()/255f),
            Math.min(1.0f, 1.4f * color.getBlue()/255f),
            Math.max(0, color.getAlpha()/255f - 0.2f)),
            color,
            new Color(
                Math.min(1.0f, 1.7f * color.getRed()/255f),
                Math.min(1.0f, 1.7f * color.getGreen()/255f),
                Math.min(1.0f, 1.7f * color.getBlue()/255f),
                Math.max(0, color.getAlpha()/255f - 0.2f))};
        Paint fillPaint = new LinearGradientPaint(0, 0,
            (float) ((0.5 * width) / (width / max)),
            (float) (height / (height / max)), FRACTIONS, colors);
        ////////////////////////////////////////////////////

        // paint the ball
        gfx.setPaint(fillPaint);
        gfx.fill(shape);

        // create and paint light reflection effects
        Ellipse2D reflection1 = new Ellipse2D.Double(width / 5, height / 5, min / 10, min / 10);
        gfx.setPaint(Color.WHITE);
        gfx.fill(reflection1);

        Ellipse2D reflection2 = new Ellipse2D.Double(width / 4.9, height / 4.9, min / 7, min / 7);
        gfx.setPaint(Colors.ALICE_BLUE);
        gfx.fill(reflection2);

        Point2D startPoint = new Point2D.Double(width / 2.5, height / 10 * 9);
        Point2D endPoint = new Point2D.Double(width / 10 * 9, height / 2.5);
        Point2D ctrlPoint1 = new Point2D.Double(startPoint.getX() + (endPoint.getX() - startPoint.getX()) / 2, height);
        Point2D ctrlPoint2 = new Point2D.Double(width, startPoint.getY() + (endPoint.getY() - startPoint.getY()) / 2);
        Point2D ctrlPoint3 = new Point2D.Double(ctrlPoint1.getX(), ctrlPoint1.getY() - height / 10);
        Point2D ctrlPoint4 = new Point2D.Double(ctrlPoint2.getX() - width / 10, ctrlPoint2.getY());

        Path2D reflection3 = new Path2D.Double();
        reflection3.moveTo(startPoint.getX(), startPoint.getY());
        reflection3.curveTo(ctrlPoint1.getX(), ctrlPoint1.getY(), ctrlPoint2.getX(), ctrlPoint2.getY(), endPoint.getX(), endPoint.getY());
        reflection3.curveTo(ctrlPoint4.getX(), ctrlPoint4.getY(), ctrlPoint3.getX(), ctrlPoint3.getY(), startPoint.getX(), startPoint.getY());
        reflection3.closePath();
        gfx.fill(reflection3);
      } finally {
        // reset graphics context state
        gfx.setPaint(oldPaint);
        gfx.setTransform(oldTransform);
      }
    }
  }
}
