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
package style.rectanglenodestyle;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.Corners;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.view.*;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Cursor;
import java.awt.geom.Rectangle2D;

/**
 * An {@link IHandle} for nodes with a {@link RectangleNodeStyle} to change the
 * {@link RectangleNodeStyle#getCornerSize()} interactively.
 */
public class CornerSizeHandle implements IHandle, IPoint, IVisualCreator {
  private final INode node;
  private final Runnable cornerSizeChanged;
  private final RectangleNodeStyle style;

  private double initialCornerSize;
  private double currentCornerSize;
  private ICanvasObject cornerRectCanvasObject;

  public CornerSizeHandle(INode node, Runnable cornerSizeChanged) {
    this.node = node;
    this.cornerSizeChanged = cornerSizeChanged;
    this.style = (RectangleNodeStyle) node.getStyle();
  }

  /**
   * Returns the absolute corner size of the current node's style.
   * This reflects the {@link RectangleNodeStyle#isCornerSizeScalingEnabled()} property of the style
   * and clamps the size to where the style would restrict it as well. This ensures that the handle
   * always appears where the corner ends visually.
   */
  private double getCornerSize() {
    IRectangle layout = node.getLayout();
    double smallerSize = Math.min(layout.getWidth(), layout.getHeight());
    double cornerSize = style.isCornerSizeScalingEnabled() ? style.getCornerSize() * smallerSize : style.getCornerSize();
    return Math.min(getMaximumCornerSize(), cornerSize);
  }

  /**
   * Determines the maximum corner size based on the style's current settings.
   */
  private double getMaximumCornerSize() {
    // if two corners can meet, the maximum size is half the height/width instead
    double maxHeight =
        style.getCorners().and(Corners.LEFT).equals(Corners.LEFT) ||
        style.getCorners().and(Corners.RIGHT).equals(Corners.RIGHT)
            ? node.getLayout().getHeight() * 0.5
            : node.getLayout().getHeight();
    double maxWidth =
        style.getCorners().and(Corners.TOP).equals(Corners.TOP) ||
        style.getCorners().and(Corners.BOTTOM).equals(Corners.BOTTOM)
            ? node.getLayout().getWidth() * 0.5
            : node.getLayout().getWidth();

    return Math.min(maxWidth, maxHeight);
  }

  /**
   * Gets a live view of the handle location.
   * The handle is placed at the top-left of the node with a vertical offset of the node styles
   * {@link RectangleNodeStyle#getCornerSize()} .
   */
  @Override
  public IPoint getLocation() {
    return this;
  }

  /**
   * Initializes the drag gesture and adds a rectangle representing the top-left corner of the node
   * using the absolute {@link RectangleNodeStyle#getCornerSize()} to the view.
   * @param context The current input mode context.
   */
  @Override
  public void initializeDrag(IInputModeContext context) {
    initialCornerSize = getCornerSize();
    currentCornerSize = initialCornerSize;
    cornerRectCanvasObject = context.getCanvasComponent().getInputModeGroup().addChild(this, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
  }

  /**
   * Calculates the new corner size depending on the new mouse location and updates the node style and corner
   * visualization.
   * @param context The current input mode context.
   * @param originalLocation The original handle location.
   * @param newLocation The new mouse location.
   */
  @Override
  public void handleMove(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    // determine delta for the corner size
    double dy = newLocation.getY() - originalLocation.getY();
    // ... and clamp to valid values
    currentCornerSize = Math.max(0, Math.min(initialCornerSize + dy, getMaximumCornerSize()));
    setCornerSize(currentCornerSize);
  }

  /**
   * Sets the corner size to {@link RectangleNodeStyle#getCornerSize()} considering whether the
   * {@link RectangleNodeStyle#isCornerSizeScalingEnabled() corner size is scaled} .
   * @param cornerSize The new absolute corner size.
   */
  private void setCornerSize(double cornerSize) {
    if (style.isCornerSizeScalingEnabled()) {
      IRectangle layout = node.getLayout();
      style.setCornerSize(cornerSize / Math.min(layout.getHeight(), layout.getWidth()));
    } else {
      style.setCornerSize(cornerSize);
    }
  }

  /**
   * Resets the initial corner size and removes the corner visualization.
   */
  @Override
  public void cancelDrag(IInputModeContext context, PointD originalLocation) {
    setCornerSize(initialCornerSize);
    cornerRectCanvasObject.remove();
  }

  /**
   * Sets the corner size for the new location, removes the corner visualization and triggers the cornerSizeChanged
   * callback.
   */
  @Override
  public void dragFinished(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    setCornerSize(currentCornerSize);
    cornerRectCanvasObject.remove();
    if (cornerSizeChanged != null) {
      cornerSizeChanged.run();
    }
  }

  /**
   * Returns {@link HandleTypes#ROTATE} as handle type that determines the visualization of the handle.
   */
  @Override
  public HandleTypes getType() {
    return HandleTypes.ROTATE;
  }

  /**
   * Returns {@link Cursors.Cross} as cursor that shall be used during the drag gesture.
   * @return
   */
  @Override
  public Cursor getCursor() {
    return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
  }

  /**
   * Ignore clicks.
   */
  @Override
  public void handleClick(ClickEventArgs eventArgs) {
    // ignore clicks
  }


  @Override
  public double getX() {
    return node.getLayout().getX();
  }

  @Override
  public double getY() {
    return node.getLayout().getY() + getCornerSize();
  }



  @Override
  public IVisual createVisual(IRenderContext context) {
    VisualGroup group = new VisualGroup();
    group.add(new ShapeVisual(new Rectangle2D.Double(), new Pen(Colors.CORNFLOWER_BLUE, 2), null));
    return this.updateVisual(context, group);
  }

  @Override
  public IVisual updateVisual(IRenderContext context, IVisual oldVisual) {
    VisualGroup group = (VisualGroup) oldVisual;
    ShapeVisual shapeVisual = (ShapeVisual) group.getChildren().get(0);
    Rectangle2D rectangle = (Rectangle2D) shapeVisual.getShape();

    double cornerSize = getCornerSize();
    PointD topLeftView = node.getLayout().getTopLeft();
    PointD bottomRightView = PointD.add(node.getLayout().getTopLeft(), new PointD(cornerSize, cornerSize));
    RectD bounds = new RectD(topLeftView, bottomRightView).getEnlarged(1);

    rectangle.setRect(bounds.x, bounds.y, bounds.width, bounds.height);
    shapeVisual.setPen(new Pen(Colors.CORNFLOWER_BLUE, 2 / context.getZoom()));

    return group;
  }

  
}
