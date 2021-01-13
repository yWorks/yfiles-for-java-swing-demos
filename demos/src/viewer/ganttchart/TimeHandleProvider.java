/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IHandleProvider;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;

import java.util.ArrayList;

/**
 * Provides resize handles for adjusting the lead and follow-up time of an
 * activity when resizing an activity node.
 */
public class TimeHandleProvider implements IHandleProvider {
  /**
   * The activity node that will be resized.
   */
  private final INode node;
  private final IReshapeHandleProvider wrappedProvider;

  /**
   * Initializes a new {@code TimeHandleProvider} instance for the given node.
   */
  public TimeHandleProvider( INode node ) {
    this.node = node;
    this.wrappedProvider = getReshapeHandleProvider(node);
  }

  /**
   * Returns resize handles for adjusting the lead and follow-up time of an
   * activity when resizing an activity node.
   */
  @Override
  public Iterable<IHandle> getHandles( IInputModeContext context ) {
    ArrayList<IHandle> handles = new ArrayList<IHandle>();
    handles.add(new TimeHandle(node, wrappedProvider.getHandle(context, HandlePositions.WEST), false));
    handles.add(new TimeHandle(node, wrappedProvider.getHandle(context, HandlePositions.EAST), true));
    return handles;
  }

  /**
   * Retrieves the reshape handle provider associated to the given node.
   */
  private static IReshapeHandleProvider getReshapeHandleProvider( INode node ) {
    IReshapeHandleProvider provider = node.lookup(IReshapeHandleProvider.class);
    return provider instanceof NodeResizeHandleProvider
      ? ((NodeResizeHandleProvider) provider).getWrapped()
      : provider;
  }


  /**
   * Adjusts the lead and follow-up time of an activity node.
   */
  public static class TimeHandle extends ConstrainedHandle {
    private final INode node;
    private final boolean isFollowUp;
    private double oldTimeWidth;

    TimeHandle( INode node, IHandle wrappedHandle, boolean isFollowUp ) {
      super(wrappedHandle);
      this.node = node;
      this.isFollowUp = isFollowUp;
    }

    /**
     * Returns the lead or follow-up time width depending on this handle's
     * {@link #isFollowUp purpose}.
     */
    private double getTimeWidth() {
      Activity activity = (Activity) node.getTag();
      return isFollowUp()
        ? activity.followUpTimeWidth()
        : activity.leadTimeWidth();
    }

    /**
     * Sets the lead of follow-up time width depending on this handle's
     * {@link #isFollowUp purpose}.  
     */
    private void setTimeWidth( double timeWidth ) {
      Activity activity = (Activity) node.getTag();
      if (isFollowUp()) {
        activity.setFollowUpTime(GanttDataUtil.worldLengthToHours(timeWidth));
      } else {
        activity.setLeadTime(GanttDataUtil.worldLengthToHours(timeWidth));
      }
    }

    /**
     * Calculates the lead or follow-up time width for the given new location.
     */
    private double calculateTimeWidth( PointD originalLocation, PointD newLocation ) {
      if (isFollowUp()) {
        return Math.max(0, oldTimeWidth + newLocation.getX() - originalLocation.getX());
      } else {
        return Math.max(0, oldTimeWidth + originalLocation.getX() - newLocation.getX());
      }
    }

    /**
     * Determines if this handle adjusts the lead or follow-up time of an
     * activity node.
     */
    public boolean isFollowUp() {
      return isFollowUp;
    }

    /**
     * Adjusts the lead or follow-up time for the associated activity node
     * during node resize operations.
     */
    @Override
    protected void onMoved(
      IInputModeContext context, PointD originalLocation, PointD newLocation
    ) {
      // get current value
      double oldTime = getTimeWidth();
      // get value from new handle location
      double newTime = calculateTimeWidth(originalLocation, newLocation);
      if (oldTime != newTime) {
        setTimeWidth(newTime);
      }
    }

    /**
     * Determines the original lead or follow-up time for the associated
     * activity node when a resize gesture starts.
     */
    @Override
    protected void onInitialized( IInputModeContext context, PointD originalLocation ) {
      oldTimeWidth = getTimeWidth();
    }

    /**
     * Resets the lead of follow-up time for the associated activity node
     * when a resize gesture is cancelled.
     */
    @Override
    protected void onCanceled( IInputModeContext context, PointD originalLocation ) {
      setTimeWidth(oldTimeWidth);
    }

    /**
     * Adjusts the lead or follow-up time for the associated activity node
     * when a node resize is finished.
     */
    @Override
    protected void onFinished(
      IInputModeContext context, PointD originalLocation, PointD newLocation
    ) {
      setTimeWidth(calculateTimeWidth(originalLocation, newLocation));
    }

    /**
     * Restricts the resize operation to the associated node's width and
     * limits shrinking the node's width below zero-size lead or follow-up time.
     */
    @Override
    protected PointD constrainNewLocation( 
      IInputModeContext context, PointD originalLocation, PointD newLocation
    ) {
      double x = isFollowUp()
        ? Math.max(newLocation.x, originalLocation.x - oldTimeWidth)
        : Math.min(newLocation.x, originalLocation.x + oldTimeWidth);
      return new PointD(x, originalLocation.y);
    }
  }
}
