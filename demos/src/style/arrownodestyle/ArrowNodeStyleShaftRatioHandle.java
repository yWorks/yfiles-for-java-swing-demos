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
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Cursor;

/**
 * An {@link IHandle} for nodes with a {@link ArrowNodeStyle} to change the
 * {@link ArrowNodeStyle#getShaftRatio()} interactively.
 */
public class ArrowNodeStyleShaftRatioHandle implements IHandle, IPoint {
  private final INode node;
  private final ArrowNodeStyle style;
  private final Runnable shaftRatioChanged;
  private double xFactor;
  private double yFactor;
  private double initialShaftRatio;

  /**
   * Creates a new instance for the given node.
   * @param node The node whose style shall be changed.
   * @param stylePropertyChanged An action that is called when the handle has been moved.
   */
  public ArrowNodeStyleShaftRatioHandle(INode node, Runnable shaftRatioChanged) {
    this.node = node;
    this.shaftRatioChanged = shaftRatioChanged;
    this.style = (ArrowNodeStyle) node.getStyle();
  }

  /**
   * Gets a live view of the handle location.
   * The handle is placed on the shaft border half-way along the shaft.
   */
  @Override
  public IPoint getLocation() {
    return this;
  }

  /**
   * Initializes the drag gesture.
   * @param context The current input mode context.
   */
  @Override
  public void initializeDrag(IInputModeContext context) {
    switch (style.getDirection()) {
      case RIGHT:
      case LEFT:
        xFactor = 0;
        yFactor = -2 / node.getLayout().getHeight();
        break;
      case UP:
      case DOWN:
        xFactor = -2 / node.getLayout().getWidth();
        yFactor = 0;
        break;
    }
    initialShaftRatio = style.getShaftRatio();
  }

  /**
   * Calculates the new shaft ratio depending on the new mouse location and updates the node style.
   * @param context          The current input mode context.
   * @param originalLocation The original handle location.
   * @param newLocation      The new mouse location.
   */
  @Override
  public void handleMove(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    // determine delta for the shaft ratio
    double delta =
        xFactor * (newLocation.getX() - originalLocation.getX()) +
        yFactor * (newLocation.getY() - originalLocation.getY());
    // ... and clamp to valid values
    double newShaftRatio = Math.max(0, Math.min(initialShaftRatio + delta, 1));
    style.setShaftRatio(newShaftRatio);

    if (shaftRatioChanged != null) {
      shaftRatioChanged.run();
    }
  }

  /**
   * Resets the initial shaft ratio.
   */
  @Override
  public void cancelDrag(IInputModeContext context, PointD originalLocation) {
    style.setShaftRatio(initialShaftRatio);
  }

  /**
   * Sets the shaft ratio for the new location, and triggers the shaftRatioChanged action.
   */
  @Override
  public void dragFinished(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    handleMove(context, originalLocation, newLocation);
  }

  /**
   * Returns {@link HandleTypes#SHEAR} as handle type that determines the visualization of the handle.
   */
  @Override
  public HandleTypes getType() {
    return HandleTypes.SHEAR;
  }

  /**
   * Returns a double-arrow cursor as cursor that shall be used during the drag gesture.
   */
  @Override
  public Cursor getCursor() {
    switch (style.getDirection()) {
      default:
      case LEFT:
      case RIGHT:
        return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
      case UP:
      case DOWN:
        return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
    }
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
    IRectangle nodeLayout = node.getLayout();
    if (style.getDirection() == ArrowNodeDirection.UP ||
        style.getDirection() == ArrowNodeDirection.DOWN) {
      return nodeLayout.getX() + nodeLayout.getWidth() * (1 - style.getShaftRatio()) / 2;
    }
    if (style.getShape() == ArrowStyleShape.DOUBLE_ARROW) {
      return nodeLayout.getX() + nodeLayout.getWidth() / 2;
    }

    double headLength = ArrowNodeStyleAngleHandle.getArrowHeadLength(nodeLayout, style);
    if (style.getDirection() == ArrowNodeDirection.RIGHT) {
      return nodeLayout.getX() + (nodeLayout.getWidth() - headLength) / 2;
    }
    return nodeLayout.getX() + headLength + (nodeLayout.getWidth() - headLength) / 2;
  }

  @Override
  public double getY() {
    IRectangle nodeLayout = node.getLayout();
    if (style.getDirection() == ArrowNodeDirection.LEFT ||
        style.getDirection() == ArrowNodeDirection.RIGHT) {
      return nodeLayout.getY() + nodeLayout.getHeight() * (1 - style.getShaftRatio()) / 2;
    }
    if (style.getShape() == ArrowStyleShape.DOUBLE_ARROW) {
      return nodeLayout.getY() + nodeLayout.getHeight() / 2;
    }

    double headLength = ArrowNodeStyleAngleHandle.getArrowHeadLength(nodeLayout, style);
    if (style.getDirection() == ArrowNodeDirection.DOWN) {
      return nodeLayout.getY() + (nodeLayout.getHeight() - headLength) / 2;
    }
    return nodeLayout.getY() + headLength + (nodeLayout.getHeight() - headLength) / 2;
  }

}
