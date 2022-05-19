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
package viewer.printing;

import com.yworks.yfiles.geometry.IMutablePoint;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPositionHandler;

/**
 * Simple implementation of an {@link IPositionHandler} that moves an
 * {@link IMutablePoint} by the dragged distance.
 */
public class PrintRectanglePositionHandler implements IPositionHandler {
  private IMutablePoint position;
  private PointD startPosition;

  /**
   * Creates a position handler that delegates to a mutable position.
   * @param position The position that will be read and changed.
   */
  public PrintRectanglePositionHandler(IMutablePoint position) {
    this.position = position;
  }

  public IPoint getLocation() {
    return position;
  }

  /**
   * Stores the initial position of the {@link IMutablePoint}.
   * @param context The context to retrieve information about the drag from.
   */
  public void initializeDrag(IInputModeContext context) {
    startPosition = position.toPointD();
  }

  /**
   * Moves the {@link IMutablePoint} away from the start position by the difference
   * between <code>newLocation</code> and <code>originalLocation</code>.
   */
  public void handleMove(IInputModeContext context, PointD originalLocation, PointD newLocation) {
    double currentX = startPosition.getX() + (newLocation.getX() - originalLocation.getX());
    double currentY = startPosition.getY() + (newLocation.getY() - originalLocation.getY());
    if (position.getX() != currentX || position.getY() != currentY) {
      position.setX(currentX);
      position.setY(currentY);
    }
  }

  /**
   * Moves the {@link IMutablePoint} back to the start position.
   */
  public void cancelDrag(IInputModeContext context, PointD originalLocation) {
    if (position.getX() != startPosition.getX() || position.getY() != startPosition.getY()) {
      position.setX(startPosition.getX());
      position.setY(startPosition.getY());
    }
  }

  public void dragFinished(IInputModeContext context, PointD originalLocation, PointD newLocation) {
  }
}

