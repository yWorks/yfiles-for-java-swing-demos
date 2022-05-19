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
package tutorial02_CustomStyles.step27_CustomGroupBounds;

import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.INode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

/**
 * An implementation of {@link com.yworks.yfiles.graph.styles.IArrow} that creates and updates the visual representation of a
 * custom arrow. To reduce the amount of memory we apply the flyweight pattern here: since all the arrows with the
 * same thickness look the same, we can share one shape and fill among all ArrowVisuals painting an arrow with the same
 * thickness. The only thing that differs and must be stored separately in each ArrowVisual, is the transformation that
 * moves and rotates the arrow to the current location and direction of its edge's endpoint.
 * <p>
 * Note that the flyweight pattern used in this class only performs well if a MySimpleArrow instance is only shared by
 * edges having the same thickness. Otherwise the arrow shape and fill that are stored in the arrow instance are
 * updated several times in each render step.
 * </p>
 */
public class MySimpleArrow implements IArrow, IVisualCreator, IBoundsProvider {
  // distribution of the arrow's color along the gradient
  private static final float[] FRACTIONS = new float[]{0, 0.5f, 1};
  // colors used for the gradient to fill the arrow with
  private static final Color[] COLORS = new Color[]{new Color(180, 180, 180), new Color(50, 50, 50), new Color(150, 150, 150)};

  // these variables hold objects shared by all ArrowVisuals painting an arrow with the same thickness
  // the shape of the arrow at a normal node
  private Path2D nodeArrowShape;
  // the shape of the arrow at a group node
  private Path2D groupArrowShape;
  // the paint to fill the arrow with
  private Paint arrowFill;

  // these variables hold the state for the transformation and differ from ArrowVisual to ArrowVisual; they are
  // populated in getVisualCreator and getBoundsProvider and assigned to the ArrowVisual in createVisual() and
  // updateVisual()
  // the location of the arrow
  private PointD anchor;
  // the direction of the arrow
  private PointD direction;
  // the node at the arrow
  private INode node;

  // the thickness of the arrow used to calculate arrowShape and arrowFill
  private double thickness;

  // backing field for below getter/setter
  private double fallbackThickness;

  /**
   * Initializes a new <code>MySimpleArrow</code> instance and sets the fallback thickness to 2.0.
   */
  public MySimpleArrow() {
    setFallbackThickness(2.0);
    nodeArrowShape = new Path2D.Double();
    groupArrowShape = new Path2D.Double();
  }

  /**
   * Gets the fallback thickness of the arrow that is used if an edge doesn't use a MySimpleEdgeStyle.
   */
  public double getFallbackThickness() {
    return fallbackThickness;
  }

  /**
   * Sets the fallback thickness of the arrow that is used if an edge doesn't use a MySimpleEdgeStyle.
   */
  public void setFallbackThickness(double thickness) {
    this.fallbackThickness = thickness;
  }

  /**
   * Returns the length of the arrow, i.e. the distance from the arrow's tip to the position where the visual
   * representation of the edge's path should begin. Always returns 5.
   */
  @Override
  public double getLength() {
    return 5;
  }

  /**
   * Gets the cropping length associated with this instance. Always returns 1. This value is used by {@link
   * com.yworks.yfiles.graph.styles.IEdgeStyle}s to let the edge appear to end shortly before its actual target.
   */
  @Override
  public double getCropLength() {
    return 1;
  }

  /**
   * Gets an {@link com.yworks.yfiles.view.IVisualCreator} implementation that will create the visual
   * for this arrow at the given location using the given direction for the given edge.
   * @param edge      the edge this arrow belongs to
   * @param atSource  whether this will be the source arrow
   * @param anchor    the anchor point for the tip of the arrow
   * @param direction the direction the arrow is pointing in
   * @return itself as a flyweight
   */
  @Override
  public IVisualCreator getVisualCreator(IEdge edge, boolean atSource, PointD anchor, PointD direction) {
    updateThickness(edge);

    this.anchor = anchor;
    this.direction = direction;
    this.node = atSource ? edge.getSourceNode() : edge.getTargetNode();
    return this;
  }

  /**
   * Gets an {@link com.yworks.yfiles.view.IBoundsProvider} implementation that can yield this arrow's bounds if
   * painted at the given location using the given direction for the given edge.
   * @param edge      the edge this arrow belongs to
   * @param atSource  whether this will be the source arrow
   * @param anchor    the anchor point for the tip of the arrow
   * @param direction the direction the arrow is pointing in
   * @return an implementation of the {@link com.yworks.yfiles.view.IBoundsProvider} interface that can subsequently
   * be used to query the bounds. Clients will always call this method before using the implementation and may not cache
   * the instance returned. This allows for applying the flyweight design pattern to implementations.
   */
  @Override
  public IBoundsProvider getBoundsProvider(IEdge edge, boolean atSource, PointD anchor, PointD direction) {
    updateThickness(edge);

    this.anchor = anchor;
    this.direction = direction;
    this.node = (INode) (atSource ? edge.getSourceNode() : edge.getTargetPort().getOwner());
    return this;
  }

  /**
   * Calculates the thickness for the given edge and updates the shape and fill if necessary.
   */
  private void updateThickness(IEdge edge) {
    double newThickness = calculateThickness(edge);
    if (this.thickness != newThickness) {
      this.thickness = newThickness;
      // since the thickness has been changed, we must update the shape and fill of the arrow
      updateShape();
      updateFill();
    }
  }

  /**
   * Calculates the thickness to use for the next visual creation.
   * @param edge the edge to read the thickness from
   */
  private double calculateThickness(IEdge edge) {
    // get the edge's thickness
    if (edge.getStyle() instanceof MySimpleEdgeStyle) {
      MySimpleEdgeStyle style = (MySimpleEdgeStyle) edge.getStyle();
      return style.getPathThickness();
    } else {
      return getFallbackThickness();
    }
  }

  /**
   * Updates the shape of the arrow with the current thickness.
   */
  private void updateShape() {
    nodeArrowShape.reset();
    nodeArrowShape.moveTo(-7, -thickness * 0.5);
    nodeArrowShape.lineTo(-7, thickness * 0.5);
    nodeArrowShape.curveTo(-5, thickness * 0.5, -1.5, thickness * 0.5, 1, thickness * 1.666);
    nodeArrowShape.curveTo(0, thickness * 0.833, 0, -thickness * 0.833, 1, -thickness * 1.666);
    nodeArrowShape.curveTo(-1.5, -thickness * 0.5, -5, -thickness * 0.5, -7, -thickness * 0.5);
    nodeArrowShape.closePath();

    //////////////// New in this sample ////////////////
    groupArrowShape.reset();
    groupArrowShape.moveTo(-7, -thickness * 0.5);
    groupArrowShape.lineTo(-7, thickness * 0.5);
    groupArrowShape.lineTo(-thickness * 0.5, thickness * 0.5);
    groupArrowShape.quadTo(0, thickness * 0.5, 0, 0);
    groupArrowShape.quadTo(0, -thickness * 0.5, -thickness * 0.5, -thickness * 0.5);
    groupArrowShape.closePath();
    ////////////////////////////////////////////////////
  }

  /**
   * Updates the paint depending on the current thickness to fill the arrow with.
   */
  private void updateFill() {
    // create a gradient depending on the thickness
    arrowFill = new LinearGradientPaint(0, (float) (-thickness * 1.666), 0, (float) (thickness * 1.666), FRACTIONS, COLORS);
  }

  /**
   * This method is called by the framework to create a {@link IVisual} that will
   * be included into the {@link com.yworks.yfiles.view.IRenderContext}.
   * @param context the context that describes where the visual will be used
   * @return the arrow visual to include in the canvas object visual tree
   */
  @Override
  public IVisual createVisual(IRenderContext context) {
    ArrowVisual visual = new ArrowVisual();
    Shape arrowShape = isGroupNode(node, (GraphComponent) context.getCanvasComponent()) ? groupArrowShape : nodeArrowShape;
    visual.update(anchor, direction, arrowShape, arrowFill);
    return visual;
  }

  /**
   * The {@link com.yworks.yfiles.view.CanvasComponent} uses this method to give implementations a chance to update an
   * existing visual that has previously been created by the same instance during a call to {@link
   * #createVisual(com.yworks.yfiles.view.IRenderContext)}.
   */
  @Override
  public IVisual updateVisual(IRenderContext context, IVisual group) {
    ArrowVisual visual = (ArrowVisual) group;
    Shape arrowShape = isGroupNode(node, (GraphComponent) context.getCanvasComponent()) ? groupArrowShape : nodeArrowShape;
    visual.update(anchor, direction, arrowShape, arrowFill);
    return visual;
  }

  /**
   * Checks whether or not a given node is a group node.
   */
  private static boolean isGroupNode(INode node, GraphComponent graphComponent) {
    if (!graphComponent.getGraph().contains(node)) {
      // node is a dummy for edge-like connectors from a node to its labels
      return false;
    }
    IFoldingView foldingView = graphComponent.getGraph().getFoldingView();
    return foldingView != null && foldingView.getManager().getMasterGraph().isGroupNode(foldingView.getMasterItem(node));
  }

  /**
   * Returns the bounds of the arrow for the current flyweight configuration.
   */
  @Override
  public RectD getBounds(ICanvasContext context) {
    return new RectD(anchor.getX() - 8 - thickness, anchor.getY() - 8 - thickness, 16 + thickness * 2, 16 + thickness * 2);
  }

  /**
   * A {@link IVisual} that paints the arrow as {@link java.awt.geom.Path2D} that forms a
   * suction cup.  Note that we paint the arrow at the origin and move and rotate the graphics context to the current
   * location and direction of its edge's endpoint.
   */
  private static class ArrowVisual implements IVisual {
    // moves and rotates the graphics context to the current location and direction of its edge's endpoint
    private AffineTransform transform;
    // reference to a shared arrow shape instance
    private Shape shape;
    // reference to a shared arrow fill instance
    private Paint fill;

    ArrowVisual() {
      transform = new AffineTransform();
    }

    /**
     * Updates the location and direction of the edge's endpoint as well as the shape and fill of the arrow.
     */
    void update(PointD anchor, PointD direction, Shape shape, Paint fill) {
      transform.setTransform(direction.getX(), direction.getY(), -direction.getY(), direction.getX(), anchor.getX(),
          anchor.getY());

      this.shape = shape;
      this.fill = fill;
    }

    @Override
    public void paint(IRenderContext context, Graphics2D gfx) {
      if (shape != null && fill != null) {
        // All paint methods must maintain the state of the graphics context.
        // To do this, remember the old state and reset it afterwards.
        AffineTransform oldTransform = gfx.getTransform();
        Paint oldPaint = gfx.getPaint();

        // move and rotate the graphics context to the current location and direction of its edge's endpoint
        gfx.transform(transform);
        gfx.setPaint(fill);
        try {
          // draw the arrow
          gfx.fill(shape);
        } finally {
          // reset graphics context state
          gfx.setPaint(oldPaint);
          gfx.setTransform(oldTransform);
        }
      }
    }
  }
}
