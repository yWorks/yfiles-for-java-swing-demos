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
package tutorial02_CustomStyles.step06_IsInside;

import com.yworks.yfiles.geometry.GeomUtilities;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.utils.ImageSupport;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.INodeStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractNodeStyle} as its base class.
 * This style visualizes nodes as balls with a semi-transparent gradient and
 * several reflections to achieve a shiny look.
 */
public class MySimpleNodeStyle extends AbstractNodeStyle {
  private static final Color DEFAULT_BALL_COLOR = new Color(0, 130, 180, 200);

  private Color nodeColor;

  //////////////// New in this sample ////////////////
  /**
   * Exact geometric check whether a point p lies inside the node. This is important for intersection calculation, among
   * others.
   */
  @Override
  protected boolean isInside(INode node, PointD point) {
    // return super.isInside(node, point);
    // GeomSupport provides an efficient contains implementation for ellipses that can be used
    return GeomUtilities.ellipseContains(node.getLayout().toRectD(), point, 0);
  }

  /**
   * Gets the outline of the node, an ellipse in this case.
   * This allows for e.g. correct edge path intersection calculation.
   */
  @Override
  protected GeneralPath getOutline(INode node) {
    // return super.getOutline(node);
    RectD rect = node.getLayout().toRectD();
    GeneralPath outline = new GeneralPath();
    outline.appendEllipse(rect, false);
    return outline;
  }
  ////////////////////////////////////////////////////

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

  /**
   * Creates the visual for a node.
   */
  @Override
  protected VisualGroup createVisual(IRenderContext context, INode node) {
    // create the visual that paints the ball
    BallVisual ball = new BallVisual();
    Color color = getNodeColor(node);
    ball.update(color, node.getLayout());

    // create the visual that paints the drop shadow
    DropShadowVisual shadow = new DropShadowVisual();
    shadow.update(node.getLayout().toSizeD());

    // create a container that holds all visuals needed to paint the node
    VisualGroup container = new VisualGroup();
    container.add(shadow);
    container.add(ball);

    // move the container to the location of the node since ball and shadow are painted in the origin
    container.setTransform(AffineTransform.getTranslateInstance(node.getLayout().getX(), node.getLayout().getY()));

    return container;
  }

  /**
   * Re-renders the node using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, {@link #createVisual(IRenderContext, INode)} is called instead.
   */
  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (!(oldVisual instanceof VisualGroup)) {
      return createVisual(context, node);
    }

    VisualGroup group = (VisualGroup) oldVisual;

    // update the visual that paints the drop shadow
    DropShadowVisual shadow = (DropShadowVisual) group.getChildren().get(0);
    shadow.update(node.getLayout().toSizeD());

    // update the visual that paints the ball
    BallVisual ball = (BallVisual) group.getChildren().get(1);
    Color color = getNodeColor(node);
    ball.update(color, node.getLayout());

    // update the transformation that moves the container to the location of the node
    double layoutX = node.getLayout().getX();
    double layoutY = node.getLayout().getY();
    AffineTransform transform = group.getTransform();
    transform.setToTranslation(layoutX, layoutY);

    return group;
  }

  /**
   * A {@link IVisual} that paints the drop shadow of a ball.
   * We use one pre-rendered image of a semi-transparent ellipse as shadow for all balls.
   * The image is blurred with a gaussian convolution to get a more realistic drop shadow effect.
   * When painting we move and scale the image so that it fits to the location and size of
   * its shadow-casting ball.
   */
  private static class DropShadowVisual implements IVisual {
    // the pre-rendered image of a semi-transparent ellipse
    private static final BufferedImage IMAGE;
    // size of the pre-rendered image
    private static final int IMAGE_SIZE = 32;
    // size of the semi-transparent ellipse
    private static final int ELLIPSE_SIZE = 20;
    // distance between the border of the image and the ellipse,
    // used to determine the translation of the image to match the location of its shadow-casting ball
    private static final int BORDER_SIZE = (IMAGE_SIZE - ELLIPSE_SIZE) / 2;
    // the light source that creates the shadow is on the top left,
    // thus the shadow is moved to the right and downward regarding its shadow-casting ball
    private static final int POSITION_OFFSET = 2;
    // ratio between image and ellipse size,
    // used to determine the scaling of the image to match the size of its shadow-casting ball
    private static final double SCALE_FACTOR = IMAGE_SIZE / (double) ELLIPSE_SIZE;
    // semi-transparent color of the shadow
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 128);

    // parameters of the gaussian convolution used to blur the image
    private static final int BLUR_SIZE = 4;
    private static final int BLUR_THETA = 2;

    // moved to the right and downward and scales the image to match the size of its shadow-casting ball
    private AffineTransform transform;

    static {
      // create an image used as drop shadow of a ball
      IMAGE = ImageSupport.createBufferedImage(IMAGE_SIZE, IMAGE_SIZE, true);

      // paint the shadow as a semi transparent ellipse on the image
      Graphics2D graphics = (Graphics2D) IMAGE.getGraphics();
      try {
        graphics.setColor(SHADOW_COLOR);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        graphics.fillOval(BORDER_SIZE, BORDER_SIZE, ELLIPSE_SIZE, ELLIPSE_SIZE);
      } finally {
        graphics.dispose();
      }

      // blur the image to get a more realistic drop shadow effect
      ImageSupport.gaussianBlur(IMAGE, BLUR_THETA, BLUR_SIZE);
    }

    DropShadowVisual() {
      transform = new AffineTransform();
    }

    /**
     * Updates the size of the drop shadow.
     * @param size the size of the shadow
     */
    void update(SizeD size) {
      double imageWidth = size.getWidth() * SCALE_FACTOR;
      double imageHeight = size.getHeight() * SCALE_FACTOR;
      double imageX = POSITION_OFFSET - imageWidth * BORDER_SIZE / IMAGE_SIZE;
      double imageY = POSITION_OFFSET - imageHeight * BORDER_SIZE / IMAGE_SIZE;

      transform.setTransform(imageWidth / IMAGE_SIZE, 0, 0, imageHeight / IMAGE_SIZE, imageX, imageY);
    }

    @Override
    public void paint(IRenderContext context, Graphics2D gfx) {
      RenderingHints oldHints = new RenderingHints((Map) gfx.getRenderingHints());
      // produces a softer gradient between the pixels of the shadow
      gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      try {
        gfx.drawImage(IMAGE, transform, null);
      } finally {
        gfx.setRenderingHints(oldHints);
      }
    }
  }

  /**
   * A {@link IVisual} that paints an ellipse with a semi-transparent gradient.
   * Three reflections of two ellipses and a closed curve make the ball look shiny.
   */
  private static class BallVisual implements IVisual {
    // distribution of the ball's colors along the gradient
    private static final float[] FRACTIONS = {0, 0.5f, 1};

    // color and size of the ball
    private Color color;
    private SizeD size;

    // shapes for the ball and its reflections
    private Ellipse2D shape;
    private Ellipse2D reflection1;
    private Ellipse2D reflection2;
    private Path2D reflection3;

    // the semi-transparent gradient to fill the ball with
    private Paint fillPaint;

    BallVisual() {
      shape = new Ellipse2D.Double();
      reflection1 = new Ellipse2D.Double();
      reflection2 = new Ellipse2D.Double();
      reflection3 = new Path2D.Double();
    }

    /**
     * Updates the color and size of the shape and the reflections of the ball.
     * Note that we paint the ball at the origin and move the its container to
     * the appropriate location.
     * @param color  the color of the ball
     * @param layout the location and size of the ball.
     */
    void update(Color color, IRectangle layout) {
      // update the shape and gradient only if color or size of the ball has been changed
      SizeD size = layout.toSizeD();
      if (!color.equals(this.color) || !size.equals(this.size)) {
        this.color = color;
        this.size = size;

        double width = layout.getWidth();
        double height = layout.getHeight();

        // set the size of the ball's shape
        shape.setFrame(0, 0, width, height);

        // max and min needed for reflection effect calculation
        double max = Math.max(width, height);
        double min = Math.min(width, height);

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
        fillPaint = new LinearGradientPaint(0, 0,
            (float) ((0.5 * width) / (width / max)),
            (float) (height / (height / max)), FRACTIONS, colors);

        // create light reflection effects
        reflection1.setFrame(width / 5, height / 5, min / 10, min / 10);

        reflection2.setFrame(width / 4.9, height / 4.9, min / 7, min / 7);

        Point2D startPoint = new Point2D.Double(width / 2.5, height / 10 * 9);
        Point2D endPoint = new Point2D.Double(width / 10 * 9, height / 2.5);
        Point2D ctrlPoint1 = new Point2D.Double(startPoint.getX() + (endPoint.getX() - startPoint.getX()) / 2, height);
        Point2D ctrlPoint2 = new Point2D.Double(width, startPoint.getY() + (endPoint.getY() - startPoint.getY()) / 2);
        Point2D ctrlPoint3 = new Point2D.Double(ctrlPoint1.getX(), ctrlPoint1.getY() - height / 10);
        Point2D ctrlPoint4 = new Point2D.Double(ctrlPoint2.getX() - width / 10, ctrlPoint2.getY());

        reflection3.reset();
        reflection3.moveTo(startPoint.getX(), startPoint.getY());
        reflection3.curveTo(ctrlPoint1.getX(), ctrlPoint1.getY(), ctrlPoint2.getX(), ctrlPoint2.getY(), endPoint.getX(), endPoint.getY());
        reflection3.curveTo(ctrlPoint4.getX(), ctrlPoint4.getY(), ctrlPoint3.getX(), ctrlPoint3.getY(), startPoint.getX(), startPoint.getY());
        reflection3.closePath();
      }
    }

    @Override
    public void paint(IRenderContext context, Graphics2D gfx) {
      // we should not change the graphics context in this method since it is also used elsewhere. Thus we save its
      // state at the beginning and restore it at the end.
      Paint oldPaint = gfx.getPaint();
      try {
        // paint the ball
        gfx.setPaint(fillPaint);
        gfx.fill(shape);
        // paint the reflections
        gfx.setPaint(Color.WHITE);
        gfx.fill(reflection1);
        gfx.setPaint(Colors.ALICE_BLUE);
        gfx.fill(reflection2);
        gfx.fill(reflection3);
      } finally {
        // reset graphics context state
        gfx.setPaint(oldPaint);
      }
    }
  }
}
