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
package style.arrownodestyle;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.ArrowNodeDirection;
import com.yworks.yfiles.graph.styles.ArrowNodeStyle;
import com.yworks.yfiles.graph.styles.ArrowStyleShape;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Cursor;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * An {@link IHandle} for nodes with a {@link ArrowNodeStyle} to change the
 * {@link ArrowNodeStyle#getAngle()} interactively.
 */
public class ArrowNodeStyleAngleHandle implements IHandle, IPoint, IVisualCreator {
  private final double HandleOffset = 15.0;
  private final INode node;
  private final Runnable angleChanged;
  private final ArrowNodeStyle style;

  // x and y factors that are used to translate the mouse delta to the relative handle movement
  private double xFactor;
  private double yFactor;

  private double arrowSideWidth;
  private double initialAngle;
  private double initialHandleOffset;
  private double handleOffsetToHeadLengthForPositiveAngles;
  private double handleOffsetToHeadLengthForNegativeAngles;

  // minimum and maximum handle offsets that result in the minimum and maximum allowed angles
  private double handleOffsetForMinAngle;
  private double handleOffsetForMaxAngle;

  private ICanvasObject angleLineCanvasObject;

  /**
   * Creates a new instance for the given node.
   * @param node         The node whose style shall be changed.
   * @param angleChanged An action that is called when the handle has been moved.
   */
  public ArrowNodeStyleAngleHandle(INode node, Runnable angleChanged) {
    this.node = node;
    this.angleChanged = angleChanged;
    this.style = (ArrowNodeStyle) node.getStyle();
  }

  /**
   * Gets a live view of the handle location.
   * The handle is placed with an offset to the node bounds on the line from the arrow head tip
   * along the arrow blade.
   */
  @Override
  public IPoint getLocation() {
    return this;
  }

  /**
   * Initializes the drag gesture and adds a line from the arrow head tip along the arrow blade to
   * the handle to the view.
   *
   * @param context The current input mode context.
   */
  @Override
  public void initializeDrag(IInputModeContext context) {
    IRectangle nodeLayout = node.getLayout();
    boolean isParallelogram = style.getShape() == ArrowStyleShape.PARALLELOGRAM;
    boolean isTrapezoid = style.getShape() == ArrowStyleShape.TRAPEZOID;

    // negative angles are only allowed for trapezoids, parallelograms or arrows with shaft ratio = 1
    boolean negativeAngleAllowed = style.getShaftRatio() >= 1 || isTrapezoid || isParallelogram;

    arrowSideWidth = getArrowSideWidth(node.getLayout(), style);

    // calculate the factors to convert the handle offset to the new length of the arrowhead
    // note that for positive angles the angle rotates around the arrow tip while for negative ones it rotates around
    // a node corner
    handleOffsetToHeadLengthForPositiveAngles = arrowSideWidth / (HandleOffset + arrowSideWidth);
    handleOffsetToHeadLengthForNegativeAngles = arrowSideWidth / HandleOffset;

    initialAngle = getClampedAngle(style);
    initialHandleOffset = getArrowHeadLength(node.getLayout(), style) / (initialAngle < 0
        ? -handleOffsetToHeadLengthForNegativeAngles
        : handleOffsetToHeadLengthForPositiveAngles);

    // the maximum length of the arrow head depends on the direction and shape
    double maxHeadLength = getMaxArrowHeadLength(nodeLayout, style);

    // calculate handle offsets for the current node size that correspond to the minimum and maximum allowed angle
    handleOffsetForMaxAngle = maxHeadLength / handleOffsetToHeadLengthForPositiveAngles;
    handleOffsetForMinAngle = negativeAngleAllowed ? -maxHeadLength / handleOffsetToHeadLengthForNegativeAngles : 0;

    // xFactor and yFactor are used later to translate the mouse delta to the relative handle movement
    ArrowNodeDirection direction = style.getDirection();
    xFactor = direction == ArrowNodeDirection.LEFT ? 1 : direction == ArrowNodeDirection.RIGHT ? -1 : 0;
    yFactor = direction == ArrowNodeDirection.UP ? 1 : direction == ArrowNodeDirection.DOWN ? -1 : 0;
    if (isParallelogram) {
      // for parallelograms the slope of the arrow blade is in the opposite direction
      xFactor *= -1;
      yFactor *= -1;
    }

    // add a line from the arrow tip along the arrow blade to the handle location to the view
    // this line is created and updated in the CreateVisual and UpdateVisual methods
    angleLineCanvasObject =
        context.getCanvasComponent().getInputModeGroup().addChild(this, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
  }

  /**
   * Calculates the new angle depending on the new mouse location and updates the node style and
   * angle visualization.
   * @param context          The current input mode context.
   * @param originalLocation The original handle location.
   * @param newLocation      The new mouse location.
   */
  @Override
  public void handleMove(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    // determine delta of the handle
    double handleDelta =
        xFactor * (newLocation.getX() - originalLocation.getX()) +
        yFactor * (newLocation.getY() - originalLocation.getY());

    // determine handle offset from the location that corresponds to angle = 0
    double handleOffset = initialHandleOffset + handleDelta;
    // ... and clamp to valid values
    handleOffset = Math.max(handleOffsetForMinAngle, Math.min(handleOffset, handleOffsetForMaxAngle));

    // calculate the new arrow head length based on the offset of the handle
    double newHeadLength = handleOffset < 0
        ? handleOffset * handleOffsetToHeadLengthForNegativeAngles
        : handleOffset * handleOffsetToHeadLengthForPositiveAngles;

    double newAngle = Math.atan(newHeadLength / arrowSideWidth);
    style.setAngle(newAngle);

    if (angleChanged != null) {
     angleChanged.run();
    }
  }

  /**
   * Resets the initial angle and removes the angle visualization.
   */
  @Override
  public void cancelDrag(IInputModeContext context, PointD originalLocation) {
    style.setAngle(initialAngle);
    angleLineCanvasObject.remove();
  }

  /**
   * Sets the angle for the new location, removes the angle visualization and triggers the angleChanged callback.
   */
  @Override
  public void dragFinished(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    handleMove(context, originalLocation, newLocation);
    angleLineCanvasObject.remove();
  }

  @Override
  public HandleTypes getType() {
    return HandleTypes.ROTATE;
  }

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
    switch (style.getDirection()) {
      case RIGHT: {
        double offset = calculateHandleInDirectionOffset();
        return style.getShape() == ArrowStyleShape.PARALLELOGRAM
            ? node.getLayout().getX() + offset
            : node.getLayout().getMaxX() - offset;
      }
      case UP:
        return node.getLayout().getX() - HandleOffset;
      case LEFT: {
        double offset = calculateHandleInDirectionOffset();
        return style.getShape() == ArrowStyleShape.PARALLELOGRAM
            ? node.getLayout().getMaxX() - offset
            : node.getLayout().getX() + offset;
      }
      case DOWN:
        return style.getShape() == ArrowStyleShape.TRAPEZOID
            ? node.getLayout().getMaxX() + HandleOffset
            : node.getLayout().getX() - HandleOffset;
    }
    return 0;
  }

  @Override
  public double getY() {
    switch (style.getDirection()) {
      case RIGHT:
        return node.getLayout().getY() - HandleOffset;
      case UP: {
        double offset = calculateHandleInDirectionOffset();
        return style.getShape() == ArrowStyleShape.PARALLELOGRAM
            ? node.getLayout().getMaxY() - offset
            : node.getLayout().getY() + offset;
      }
      case LEFT:
        return style.getShape() == ArrowStyleShape.TRAPEZOID
            ? node.getLayout().getMaxY() + HandleOffset
            : node.getLayout().getY() - HandleOffset;
      case DOWN: {
        double offset = calculateHandleInDirectionOffset();
        return style.getShape() == ArrowStyleShape.PARALLELOGRAM
            ? node.getLayout().getY() + offset
            : node.getLayout().getMaxY() - offset;
      }
    }
    return 0;
  }



  /**
   * Returns the width of one arrow side for the given node layout and style.
   * @param nodeLayout The node layout whose size shall be used.
   * @param style      The style whose shape and direction shall be used.
   * @return The width of one arrow side for the given node layout and style.
   */
  private static double getArrowSideWidth(IRectangle nodeLayout, ArrowNodeStyle style) {
    ArrowStyleShape shape = style.getShape();
    boolean isParallelogram = shape == ArrowStyleShape.PARALLELOGRAM;
    boolean isTrapezoid = shape == ArrowStyleShape.TRAPEZOID;
    double againstDirectionSize =
            style.getDirection() == ArrowNodeDirection.UP ||
            style.getDirection() == ArrowNodeDirection.DOWN
        ? nodeLayout.getWidth()
        : nodeLayout.getHeight();
    // for parallelogram and trapezoid, one side of the arrow fills the full againstDirectionSize
    return againstDirectionSize * (isParallelogram || isTrapezoid ? 1 : 0.5);
  }

  /**
   * Clamps the {@link  ArrowNodeStyle#getAngle()} of the given style to a valid value.
   * A valid angle is less then <c>&#x03c0; / 2</c>.
   * For styles using {@link ArrowStyleShape#PARALLELOGRAM} or {@link ArrowStyleShape#TRAPEZOID}
   * shape or having {@link ArrowNodeStyle#getShaftRatio()} <c>1</c>, the angle also has to be
   * bigger then <c>-&#x03c0; / 2</c>, otherwise it has to be <c>&gt;= 0</c>
   * @param style The style to return the clamped angle for.
   * @return The angle of the style clamped to a valid value.
   */
  private static double getClampedAngle(ArrowNodeStyle style) {
    // clamp angle to be <= Math.PI/2
    double angle = Math.min(Math.PI * 0.5, style.getAngle());
    if (angle < 0) {
      // if a negative angle is set, check if the effective shaft ratio is 1
      if (style.getShaftRatio() >= 1 ||
          style.getShape() == ArrowStyleShape.PARALLELOGRAM ||
          style.getShape() == ArrowStyleShape.TRAPEZOID) {
        // negative angle allowed but has to be > -Math.PI/2
        angle = Math.max(-Math.PI * 0.5, angle);
      } else {
        angle = 0;
      }
    }
    return angle;
  }


  /**
   * Calculates the length of the arrow head for the given node layout and style.
   * @param nodeLayout The layout of the node.
   * @param style The style whose shape and angle shall be considered.
   * @return The length of the arrow head for the given style and node layout.
   */
  static double getArrowHeadLength(IRectangle nodeLayout, ArrowNodeStyle style) {
    double maxArrowLength = getMaxArrowHeadLength(nodeLayout, style);
    double arrowSideWidth = getArrowSideWidth(nodeLayout, style);
    double angle = getClampedAngle(style);
    double maxHeadLength = arrowSideWidth * Math.tan(Math.abs(angle));

    return Math.min(maxHeadLength, maxArrowLength);
  }

  /**
   * Returns the maximum possible arrow head length for the given node layout and style.
   * @param nodeLayout The node layout whose size shall be used.
   * @param style The style whose shape and direction shall be used.
   * @return The maximum possible arrow head length for the given node layout and style.
   */
  private static double getMaxArrowHeadLength(IRectangle nodeLayout, ArrowNodeStyle style) {
    ArrowStyleShape shape = style.getShape();
    boolean isTrapezoid = shape == ArrowStyleShape.TRAPEZOID;
    boolean isDoubleArrow = shape == ArrowStyleShape.DOUBLE_ARROW;
    double inDirectionSize =
            style.getDirection() == ArrowNodeDirection.UP ||
            style.getDirection() == ArrowNodeDirection.DOWN
        ? nodeLayout.getHeight()
        : nodeLayout.getWidth();
    // for double arrow and trapezoid the arrow may only fill half the inDirectionSize
    double maxArrowLength = inDirectionSize * (isDoubleArrow || isTrapezoid ? 0.5 : 1);
    return maxArrowLength;
  }

  /**
   * Calculates the offset of the current handle location to the location corresponding to an angle of 0.
   * @return The offset of the current handle location to the location corresponding to an angle of 0.
   */
  private double calculateHandleInDirectionOffset() {
    double headLength = getArrowHeadLength(node.getLayout(), style);
    double arrowSideWidth = getArrowSideWidth(node.getLayout(), style);
    double scaledHeadLength = headLength * (HandleOffset + arrowSideWidth) / (arrowSideWidth);
    double angle = getClampedAngle(style);
    double offset = angle >= 0 ? scaledHeadLength : (headLength - scaledHeadLength);
    return offset;
  }



  @Override
  public IVisual createVisual(IRenderContext context) {
    VisualGroup group = new VisualGroup();
    group.add(new ShapeVisual(new Line2D.Double(), new Pen(Colors.GOLDENROD, 2), null));
    return updateVisual(context, group);
  }

  @Override
  public IVisual updateVisual(IRenderContext context, IVisual oldVisual) {
    VisualGroup group = (VisualGroup) oldVisual;

    // line shall point from handle to arrow tip
    ShapeVisual shapeVisual = (ShapeVisual) group.getChildren().get(0);
    Line2D line = (Line2D) shapeVisual.getShape();

    // synchronize first line point with handle location
    Point2D lineStart = getLocation().toPoint2D();

    // synchronize second line point with arrow tip
    IRectangle nodeLayout = node.getLayout();
    boolean isParallelogram = style.getShape() == ArrowStyleShape.PARALLELOGRAM;
    boolean isTrapezoid = style.getShape() == ArrowStyleShape.TRAPEZOID;
    double againstDirectionRatio = isParallelogram || isTrapezoid ? 1 : 0.5;

    double toWorldX = 0;
    double toWorldY = 0;
    // for negative angles, the arrow tip is moved
    double arrowTipOffset = style.getAngle() < 0 ? getArrowHeadLength(node.getLayout(), style) : 0;
    switch (style.getDirection()) {
      case RIGHT: {
        toWorldX = isParallelogram ? nodeLayout.getX() + arrowTipOffset : nodeLayout.getMaxX() - arrowTipOffset;
        toWorldY = nodeLayout.getY() + nodeLayout.getHeight() * againstDirectionRatio;
        break;
      }
      case LEFT: {
        toWorldX = isParallelogram ? nodeLayout.getMaxX() - arrowTipOffset : nodeLayout.getX() + arrowTipOffset;
        toWorldY = isTrapezoid ? nodeLayout.getY() : nodeLayout.getY() + nodeLayout.getHeight() * againstDirectionRatio;
        break;
      }
      case UP: {
        toWorldX = nodeLayout.getX() + nodeLayout.getWidth() * againstDirectionRatio;
        toWorldY = isParallelogram ? nodeLayout.getMaxY() - arrowTipOffset : nodeLayout.getY() + arrowTipOffset;
        break;
      }
      case DOWN: {
        toWorldX = isTrapezoid ? nodeLayout.getX() : nodeLayout.getX() + nodeLayout.getWidth() * againstDirectionRatio;
        toWorldY = isParallelogram ? nodeLayout.getY() + arrowTipOffset : nodeLayout.getMaxY() - arrowTipOffset;
        break;
      }
    }

    Point2D lineEnd = new PointD(toWorldX, toWorldY).toPoint2D();
    line.setLine(lineStart, lineEnd);
    shapeVisual.setPen(new Pen(Colors.GOLDENROD, (2 / context.getZoom())));
    return group;
  }

}