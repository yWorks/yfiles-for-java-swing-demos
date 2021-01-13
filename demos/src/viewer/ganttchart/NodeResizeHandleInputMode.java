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
import com.yworks.yfiles.view.input.HandleInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeHitTester;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;

import java.util.Iterator;

/**
 * Provides resize handles for lead and follow-up time of an activity if the
 * mouse is over the left or right border of the corresponding activity node
 * and resize handles for the main activity time frame if the mouse is over
 * the right border of the lead time section or
 * the left border of the follow-up time section.
 */
public class NodeResizeHandleInputMode extends HandleInputMode {
  /**
   * Specifies how close the mouse needs to be  
   */
  private static final int FUZZYNESS = 3;

  /**
   * Finds the closest hit handle for the given world coordinate pair.
   * @param location The coordinates in the world coordinate system.
   */
  @Override
  protected IHandle getClosestHitHandle( PointD location ) {
    IHandle handle = super.getClosestHitHandle(location);
    if (handle instanceof TimeHandleProvider.TimeHandle) {
      // if a time handle is hit, it has priority over the resize handles
      return handle;
    }

    IInputModeContext context = this.getInputModeContext();
    // get the node in the location
    INodeHitTester hitTest = context.lookup(INodeHitTester.class);
    if (hitTest != null) {
      Iterator<INode> hits = hitTest.enumerateHits(context, location).iterator();
      if (hits.hasNext()) {
        // there is a node in the location
        INode node = hits.next();
        // get the IReshapeHandleProvider
        IReshapeHandleProvider handleProvider = node.lookup(IReshapeHandleProvider.class);

        Activity activity = (Activity) node.getTag();
        double x = node.getLayout().getX() + activity.leadTimeWidth();
        if (Math.abs(x - location.x) < FUZZYNESS) {
          // mouse is over the right border of the lead time section
          return handleProvider.getHandle(context, HandlePositions.WEST);
        }
        double width = node.getLayout().getWidth()
          - activity.leadTimeWidth() - activity.followUpTimeWidth();
        if (Math.abs(x + width - location.x) < FUZZYNESS) {
          // mouse is over left border of the follow-up time section
          return handleProvider.getHandle(context, HandlePositions.EAST);
        }
      }
    }
    return null;
  }
}