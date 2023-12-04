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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.ConstrainedHandle;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;

import java.awt.Cursor;

/**
 * Provides resize handles for adjusting the main activity time frame when
 * resizing an activity node.
 */
public class NodeResizeHandleProvider implements IReshapeHandleProvider {
  /**
   * The activity node that will be resized.
   */
  private final INode node;
  private final IReshapeHandleProvider wrappedProvider;

  /**
   * Initializes a new {@code NodeResizeHandleProvider} instance for the
   * given node and given original reshape handle provider.
   */
  public NodeResizeHandleProvider( INode node, IReshapeHandleProvider wrappedProvider ) {
    this.node = node;
    this.wrappedProvider = wrappedProvider;
  }

  /**
   * Returns {@link HandlePositions#HORIZONTAL}.
   */
  @Override
  public HandlePositions getAvailableHandles( IInputModeContext context ) {
    return HandlePositions.HORIZONTAL;
  }

  /**
   * Returns an invisible resize handle for adjusting the main activity time
   * frame when resizing an activity node.
   */
  @Override
  public IHandle getHandle( IInputModeContext context, HandlePositions position ) {
    return new NodeResizeHandle(
      node,
      wrappedProvider.getHandle(context, position),
      HandlePositions.WEST == position);
  }

  /**
   * Returns the original reshape handle provider wrapped in this provider.
   */
  IReshapeHandleProvider getWrapped() {
    return wrappedProvider;
  }



  /**
   * Adjusts the main activity time frame of an activity node.
   */
  public static class NodeResizeHandle extends ConstrainedHandle {
    private final INode node;
    private final boolean start;

    private double limit;

    NodeResizeHandle( INode node, IHandle wrappedHandle, boolean start ) {
      super(wrappedHandle);
      this.node = node;
      this.start = start;
    }

    /**
     * Determines if the start or end date of an activity is changed.
     */
    public boolean isStart() {
      return start;
    }

    /**
     * Returns {@link Cursor#MOVE_CURSOR}.
     */
    @Override
    public Cursor getCursor() {
      return new Cursor(Cursor.MOVE_CURSOR);
    }

    /**
     * Returns {@link HandleTypes#INVISIBLE}.
     */
    @Override
    public HandleTypes getType() {
      return HandleTypes.INVISIBLE;
    }

    /**
     * Restricts the resize operation to the associated node's width and
     * limits shrinking the node's width below the width required for the
     * node's lead and follow-up time.
     */
    @Override
    protected PointD constrainNewLocation(
      IInputModeContext context, PointD originalLocation, PointD newLocation
    ) {
      double x = isStart()
        ? Math.min(newLocation.x, limit)
        : Math.max(newLocation.x, limit);
      return new PointD(x, originalLocation.y);
    }

    /**
     * Determines the limit for shrinking the associated node when a resize
     * gesture starts.  
     */
    @Override
    protected void onInitialized( IInputModeContext context, PointD originalLocation ) {
      Activity activity = (Activity) node.getTag();
      double time = activity.leadTimeWidth() + activity.followUpTimeWidth();
      limit = isStart()
        ? node.getLayout().getMaxX() - time
        : node.getLayout().getX() + time;
    }
  }
}
