/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package tutorial02_CustomStyles.step22_CustomArrow;

import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IEdge;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

/////////////// This class is new in this sample ///////////////

/**
 * An implementation of {@link com.yworks.yfiles.graph.styles.IArrow} that creates and updates the visual representation of a
 * custom arrow. To reduce the amount of memory we apply the flyweight pattern here: since all the arrows look the
 * same, we can share one shape and fill among all ArrowVisuals. The only thing that differs and must be stored
 * separately in each ArrowVisual, is the transformation that moves and rotates the arrow to the current location and
 * direction of its edge's endpoint.
 */
public class MySimpleArrow implements IArrow, IVisualCreator, IBoundsProvider {
  // these variables hold objects shared by all ArrowVisuals
  // the shape of the arrow
  private static final Path2D ARROW_SHAPE;
  // the paint to fill the arrow with
  private static final Paint ARROW_FILL;

  // these variables hold the state for the transformation and differ from ArrowVisual to ArrowVisual; they are
  // populated in getVisualCreator and getBoundsProvider and assigned to the ArrowVisual in createVisual() and
  // updateVisual()
  // the location of the arrow
  private PointD anchor;
  // the direction of the arrow
  private PointD direction;

  static {
    ARROW_SHAPE = new Path2D.Double();
    ARROW_SHAPE.moveTo(-7, -1.5);
    ARROW_SHAPE.lineTo(-7, 1.5);
    ARROW_SHAPE.curveTo(-5, 1.5, -1.5, 1.5, 1, 5);
    ARROW_SHAPE.curveTo(0, 2.5, 0, -2.5, 1, -5);
    ARROW_SHAPE.curveTo(-1.5, -1.5, -5, -1.5, -7, -1.5);
    ARROW_SHAPE.closePath();

    ARROW_FILL = new LinearGradientPaint(0, -5f, 0, 5f,
        new float[]{0, 0.5f, 1},
        new Color[]{new Color(180, 180, 180), new Color(50, 50, 50), new Color(150, 150, 150)});
  }

  /**
   * Returns the length of the arrow, i.e. the distance from the arrow's tip to the position where the visual
   * representation of the edge's path should begin. Always returns 0.
   */
  @Override
  public double getLength() {
    return 0;
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
    this.anchor = anchor;
    this.direction = direction;
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
    this.anchor = anchor;
    this.direction = direction;
    return this;
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
    visual.update(anchor, direction);
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
    visual.update(anchor, direction);
    return visual;
  }

  /**
   * Returns the bounds of the arrow for the current flyweight configuration.
   */
  @Override
  public RectD getBounds(ICanvasContext context) {
    return new RectD(anchor.getX() - 8, anchor.getY() - 8, 32, 32);
  }

  /**
   * A {@link IVisual} that paints the arrow as {@link java.awt.geom.Path2D} that forms a
   * suction cup.  Note that we paint the arrow at the origin and move and rotate the graphics context to the current
   * location and direction of its edge's endpoint.
   */
  private static class ArrowVisual implements IVisual {
    // moves and rotates the graphics context to the current location and direction of its edge's endpoint
    private AffineTransform transform;

    ArrowVisual() {
      transform = new AffineTransform();
    }

    /**
     * Updates the location and direction of the edge's endpoint.
     */
    void update(PointD anchor, PointD direction) {
      transform.setTransform(direction.getX(), direction.getY(), -direction.getY(), direction.getX(), anchor.getX(),
          anchor.getY());
    }

    @Override
    public void paint(IRenderContext context, Graphics2D gfx) {
      // All paint methods must maintain the state of the graphics context.
      // To do this, remember the old state and reset it afterwards.
      AffineTransform oldTransform = gfx.getTransform();
      Paint oldPaint = gfx.getPaint();

      // move and rotate the graphics context to the current location and direction of its edge's endpoint
      gfx.transform(transform);
      gfx.setPaint(ARROW_FILL);
      try {
        // draw the arrow
        gfx.fill(ARROW_SHAPE);
      } finally {
        // reset graphics context state
        gfx.setPaint(oldPaint);
        gfx.setTransform(oldTransform);
      }
    }
  }
}
