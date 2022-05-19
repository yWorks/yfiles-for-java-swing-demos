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
package style.simplecustomstyle;

import com.yworks.yfiles.geometry.GeomUtilities;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.graph.styles.IEdgeStyleRenderer;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.utils.ImageSupport;
import com.yworks.yfiles.geometry.MutableRectangle;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.ISize;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleEdge;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.SimplePort;
import com.yworks.yfiles.view.input.IInputModeContext;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple implementation of an {@link com.yworks.yfiles.graph.styles.INodeStyle} that uses the convenience class {@link
 * com.yworks.yfiles.graph.styles.AbstractNodeStyle} as the base class.
 */
public class MySimpleNodeStyle extends AbstractNodeStyle {
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
   * style.simplecustomstyle.MySimpleNodeStyle#getNodeColor()} unless the {@link com.yworks.yfiles.graph.INode#getTag()}}
   * is of type {@link Color}, in which case that color overrides this style's setting.
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
    // create a container that holds all visuals needed to paint the node
    VisualGroup container = new VisualGroup();

    // create the visual that paints the drop shadow
    DropShadowVisual shadow = new DropShadowVisual();
    shadow.update(node.getLayout().toSizeD());
    container.add(shadow);

    // create the visual that paints the ball
    BallVisual ball = new BallVisual();
    Color color = getNodeColor(node);
    ball.update(color, node.getLayout());
    container.add(ball);

    // create the visuals that paint edge-like connectors from the node to its labels
    LabelEdgesGroup labelEdges = new LabelEdgesGroup();
    List<PointD> labelLocations = getLabelLocations(node);
    labelEdges.update(context, node, labelLocations);
    container.add(labelEdges);

    // move the container to the location of the node since ball and shadow are painted in the origin
    container.setTransform(AffineTransform.getTranslateInstance(node.getLayout().getX(), node.getLayout().getY()));

    return container;
  }

  /**
   * Re-renders the node using the old visual instead of creating a new one for each call. It is strongly recommended to
   * do it for performance reasons. Otherwise, the {@link #createVisual(IRenderContext, INode)} is called instead.
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

    // update the visuals that paint edge-like connectors from the node to its labels
    LabelEdgesGroup labelEdges = (LabelEdgesGroup) group.getChildren().get(2);
    List<PointD> labelLocations = getLabelLocations(node);
    labelEdges.update(context, node, labelLocations);

    // update the transformation that moves the container to the location of the node
    double layoutX = node.getLayout().getX();
    double layoutY = node.getLayout().getY();
    AffineTransform transform = group.getTransform();
    transform.setToTranslation(layoutX, layoutY);

    return group;
  }

  /**
   * Returns the center points of labels to draw edge-like connectors for, relative the node's top left corner. Note
   * that we assume the node's top left corner at the origin and move the the container to the current location of the
   * node.
   */
  private List<PointD> getLabelLocations(INode node) {
    return node.getLabels().stream()
        .map(label -> {
          PointD labelCenter = label.getLayout().getCenter();
          PointD nodeTopLeft = node.getLayout().getTopLeft();
          return PointD.subtract(labelCenter, nodeTopLeft);
        })
        .collect(Collectors.toList());
  }

  /**
   * Gets the outline of the node, an ellipse in this case.
   * This allows for correct edge path intersection calculation, among others.
   */
  @Override
  protected GeneralPath getOutline(INode node) {
    GeneralPath outline = new GeneralPath();
    outline.appendEllipse(node.getLayout(), false);
    return outline;
  }

  /**
   * Gets the bounding box of the node.
   * This is used for bounding box calculations and includes the visual shadow.
   */
  @Override
  protected RectD getBounds(ICanvasContext context, INode node) {
    RectD bounds = node.getLayout().toRectD();
    // expand bounds to include drop-shadow
    return new RectD(bounds.getX(), bounds.getY(), bounds.getWidth() + 3, bounds.getHeight() + 3);
  }

  /**
   * Overridden to take the connection lines to the label into account.
   * Otherwise label intersection lines might not be painted if the node is outside of the clipping bounds.
   */
  @Override
  protected boolean isVisible(ICanvasContext context, RectD clip, INode node) {
    if (super.isVisible(context, clip, node)) {
      return true;
    }

    // check for labels connection lines
    RectD enlargedClip = clip.getEnlarged(10);
    return node.getLabels().stream().anyMatch(label -> {
      PointD nodeCenter = node.getLayout().getCenter();
      PointD labelCenter = label.getLayout().getCenter();
      return enlargedClip.intersectsLine(nodeCenter, labelCenter);
    });
  }

  /**
   * Hit test which considers HitTestRadius specified in CanvasContext.
   * @return true if point is inside node
   */
  @Override
  protected boolean isHit(IInputModeContext context, PointD point, INode node) {
    RectD bounds = node.getLayout().toRectD();
    return GeomUtilities.ellipseContains(bounds, point, context.getHitTestRadius());
  }

  /**
   * Checks if a node is inside a certain box. Considers HitTestRadius.
   * @return <code>true</code> if the box intersects the elliptical shape of the node. Also <code>true</code> if box
   *         lies completely inside node.
   */
  @Override
  protected boolean isInBox(IInputModeContext context, RectD box, INode node) {
    if (!super.isInBox(context, box, node)) {
      // not even the bounds of the node are in the box
      return false;
    }

    double eps = context.getHitTestRadius();

    GeneralPath outline = getOutline(node);
    if (outline == null) return false;

    if (outline.intersects(box, eps)) {
      // the node and the box intersects
      return true;
    }
    if (outline.pathContains(box.getTopLeft(), eps) && outline.pathContains(box.getBottomRight(), eps)) {
      // the box is completely within the node
      return true;
    }
    // the node's bounds is completely within the box
    IRectangle nodeBounds = node.getLayout();
    return box.contains(nodeBounds.getTopLeft())
        && box.contains(nodeBounds.getBottomRight());
  }

  /**
   * Exact geometric check whether a point p lies inside the node. This is important for intersection calculation, among
   * others.
   */
  @Override
  protected boolean isInside(INode node, PointD point) {
    RectD bounds = node.getLayout().toRectD();
    return GeomUtilities.ellipseContains(bounds, point, 0);
  }

  /**
   * Exact geometric intersection calculation. This is important for edge cropping. Otherwise the approximation of the
   * {@link #getOutline(com.yworks.yfiles.graph.INode)}'s {@link GeneralPath} is used.
   */
  @Override
  protected PointD getIntersection(INode node, PointD inner, PointD outer) {
    RectD bounds = node.getLayout().toRectD();
    return GeomUtilities.findEllipseLineIntersection(bounds, inner, outer);
  }

  /**
   * A {@link IVisual} that paints the drop shadow of a ball. As shadow we use one pre-rendered
   * image of a semi-transparent ellipse for all balls. The image is blurred with a gaussian convolution to get a more
   * realistic drop shadow effect. When painting we move and scale the image so that it fits to the location and size of
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
    private final AffineTransform transform;

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
    void update(final SizeD size) {
      double imageWidth = size.getWidth() * SCALE_FACTOR;
      double imageHeight = size.getHeight() * SCALE_FACTOR;
      double imageX = POSITION_OFFSET - imageWidth * BORDER_SIZE / IMAGE_SIZE;
      double imageY = POSITION_OFFSET - imageHeight * BORDER_SIZE / IMAGE_SIZE;

      transform.setTransform(imageWidth / IMAGE_SIZE, 0, 0, imageHeight / IMAGE_SIZE, imageX, imageY);
    }

    @Override
    public void paint(IRenderContext context, Graphics2D gfx) {
      // All paint methods must maintain the state of the graphics context.
      // To do this, remember the old state and reset it afterwards.
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
   * A {@link IVisual} that paints the ball as an ellipse with a semi-transparent gradient.
   * Three reflections of two ellipses and a closed curve make the ball look shiny.
   */
  private static class BallVisual implements IVisual {
    // distribution of the ball's color along the gradient
    private static final float[] FRACTIONS = {0, 0.5f, 1};

    // color and size of the ball
    private Color color;
    private SizeD size;

    // shapes for the ball and its reflections
    private final Ellipse2D shape;
    private final Ellipse2D reflection1;
    private final Ellipse2D reflection2;
    private final Path2D reflection3;

    // the semi-transparent gradient of the ball
    private Paint fillPaint;

    public BallVisual() {
      shape = new Ellipse2D.Double();
      reflection1 = new Ellipse2D.Double();
      reflection2 = new Ellipse2D.Double();
      reflection3 = new Path2D.Double();
    }

    /**
     * Updates the color and size of the shape and the reflections of the ball. Note we do not need the location of the
     * node, because we paint the node at the origin and move the container containing this visual to the current
     * location of the node.
     * @param color the color of the ball
     * @param size the size of the ball.
     */
    public void update(Color color, ISize size) {
      SizeD layoutSize = size.toSizeD();
      if (!color.equals(this.color) || !layoutSize.equals(this.size)) {
        this.color = color;
        this.size = layoutSize;

        double width = layoutSize.getWidth();
        double height = layoutSize.getHeight();
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
      Paint oldPaint = gfx.getPaint();
      try {
        gfx.setPaint(fillPaint);
        gfx.fill(shape);
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

  /**
   * A {@link IVisual} that paints edge-like connectors from a node to one of its labels.
   */
  private static class LabelEdgeVisual implements IVisual {
    private PointD labelLocation;
    private IVisual edgeVisual;

    private LabelEdgeVisual() {
      this.labelLocation = PointD.ORIGIN;
    }

    /**
     * Updates the visual that paints the edge-like connector from a node to one of its labels.
     */
    public void update(IRenderContext renderContext, PointD labelLocation, SimpleEdge simpleEdge,
        SimpleNode targetDummyNode) {
      if (!labelLocation.equals(this.labelLocation)) {
        this.labelLocation = labelLocation;

        // move the dummy node to the location of the label
        targetDummyNode.setLayout(new MutableRectangle(labelLocation.getX(), labelLocation.getY(), 0, 0));

        // now create new visual or update an existing one that paints an edge-like connector using the style interface
        IEdgeStyleRenderer renderer = simpleEdge.getStyle().getRenderer();
        IVisualCreator creator = renderer.getVisualCreator(simpleEdge, simpleEdge.getStyle());
        if (edgeVisual == null) {
          edgeVisual = creator.createVisual(renderContext);
        } else {
          creator.updateVisual(renderContext, edgeVisual);
        }
      }
    }

    @Override
    public void paint(IRenderContext context, Graphics2D gfx) {
      edgeVisual.paint(context, gfx);
    }
  }

  /**
   * A {@link com.yworks.yfiles.view.VisualGroup} that holds all {@link style.simplecustomstyle.MySimpleNodeStyle.LabelEdgeVisual}
   * for one node.
   */
  private static class LabelEdgesGroup extends VisualGroup {
    private List<PointD> labelLocations;

    public LabelEdgesGroup() {
      this.labelLocations = new ArrayList<>();
    }

    /**
     * Updates the edge-like connectors from a node to its labels.
     */
    public void update(IRenderContext renderContext, INode node, List<PointD> labelLocations) {
      if (listsAreEqual(this.labelLocations, labelLocations)) {
        // nothing to update since the labels have not been changed
        return;
      }

      this.labelLocations = labelLocations;
      if (node.getLabels().size() > 0) {
        // create a SimpleEdge which will be used as a dummy for the rendering
        SimpleEdge simpleEdge = new SimpleEdge(null, null);

        // assign the style
        MySimpleEdgeStyle edgeStyle = new MySimpleEdgeStyle(new MySimpleArrow(), IArrow.NONE, 2);
        simpleEdge.setStyle(edgeStyle);

        // create a SimpleNode which provides the source port for the edge but won't be drawn itself
        SimpleNode sourceDummyNode = new SimpleNode();
        sourceDummyNode.setLayout(new RectD(0, 0, node.getLayout().getWidth(), node.getLayout().getHeight()));
        sourceDummyNode.setStyle(node.getStyle());

        // create a SimpleNode which provides the target port for the edge but won't be drawn itself
        SimpleNode targetDummyNode = new SimpleNode();

        // set source port to the port of the node using a dummy node that is located at the origin.
        simpleEdge.setSourcePort(new SimplePort(sourceDummyNode, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));
        // create port on target dummy node for the label target
        simpleEdge.setTargetPort(new SimplePort(targetDummyNode, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));

        // render one edge for each label
        int size = this.labelLocations.size();
        for (int index = 0; index < size; index++) {
          final PointD labelLocation = this.labelLocations.get(index);
          LabelEdgeVisual visual = getLabelEdgeVisual(index);

          // let the visual update itself with the new parameters
          visual.update(renderContext, labelLocation, simpleEdge, targetDummyNode);
        }

        // if the number of labels has been decreased we remove spare visuals
        if (size < getChildren().size()) {
          removeSpareVisuals(size);
        }
      } else {
        getChildren().clear();
      }
    }

    /**
     * Returns the visual in the children list of the VisualGroup or creates a new one if there is none.
     */
    private LabelEdgeVisual getLabelEdgeVisual(int index) {
      if (getChildren().size() > index && getChildren().get(index) != null) {
        return (LabelEdgeVisual) getChildren().get(index);
      } else {
        LabelEdgeVisual labelEdgeVisual = new LabelEdgeVisual();
        getChildren().add(labelEdgeVisual);
        return labelEdgeVisual;
      }

    }

    /**
     * Removes visuals that are spare from the children list.
     * @param index the index of the first spare child.
     */
    private void removeSpareVisuals(int index) {
      getChildren().subList(index, getChildren().size() - 1).clear();
    }

    /**
     * Helper method to decide if two lists are equal.
     */
    private <T> boolean listsAreEqual(List<T> list1, List<T> list2) {
      if (list1.size() != list2.size()) {
        return false;
      }
      for (int i = 0; i < list1.size(); i++) {
        if (!safeEquals(list1.get(i), list2.get(i))) {
          return false;
        }
      }
      return true;
    }

    // calls equals on the objects if both are not null
    private static boolean safeEquals(Object o1, Object o2) {
      return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }
  }
}
