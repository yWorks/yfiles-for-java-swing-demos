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
package complete.isometric;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import complete.isometric.model.Geometry;
import complete.isometric.model.NodeData;

import java.awt.Cursor;

/**
 * A node handle that can be used to change the {@link Geometry} height of the nodes {@link NodeData}.
 */
public class HeightHandle implements IHandle {

  private final INode node;
  private final IInputModeContext inputModeContext;
  private final double minimumHeight;

  private double originalHeight = 0;

  public HeightHandle(INode node, IInputModeContext inputModeContext, double minimumHeight) {
    this.node = node;
    this.inputModeContext = inputModeContext;
    this.minimumHeight = minimumHeight;
  }

  @Override
  public IPoint getLocation() {
    CanvasComponent cc = this.inputModeContext.getCanvasComponent();
    PointD vp = cc.toViewCoordinates(this.node.getLayout().getCenter());
    PointD up = PointD.add(vp, new PointD(0, -getGeometry().getHeight() * this.inputModeContext.getZoom()));
    return cc.toWorldCoordinates(up);
  }

  /**
   * Initializes the drag.
   */
  @Override
  public void initializeDrag(IInputModeContext context) {
    this.originalHeight = getGeometry().getHeight();
  }

  /**
   * Updates the node according to the moving handle.
   */
  @Override
  public void handleMove(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    this.adjustNodeHeight(inputModeContext, originalLocation, newLocation);
  }

  /**
   * Cancels the drag and cleans up.
   */
  @Override
  public void cancelDrag(IInputModeContext context, PointD originalLocation) {
    getGeometry().setHeight(this.originalHeight);
  }

  /**
   * Finishes the drag an applies changes.
   */
  @Override
  public void dragFinished(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    this.adjustNodeHeight(context, originalLocation, newLocation);
  }

  /**
   * Adjusts the node height according to how much the handle was moved.
   */
  private void adjustNodeHeight(IInputModeContext context, PointD oldLocation, PointD newLocation) {
    double newY = context.getCanvasComponent().toViewCoordinates(newLocation).getY();
    double oldY = context.getCanvasComponent().toViewCoordinates(oldLocation).getY();
    double delta = (newY - oldY) / context.getZoom();
    double newHeight = this.originalHeight - delta;
    getGeometry().setHeight(Math.max(this.minimumHeight, newHeight));
  }


  @Override
  public HandleTypes getType() {
    return HandleTypes.RESIZE;
  }

  @Override
  public Cursor getCursor() {
    return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
  }

  public Geometry getGeometry() {
    return ((NodeData) node.getTag()).getGeometry();
  }
}
