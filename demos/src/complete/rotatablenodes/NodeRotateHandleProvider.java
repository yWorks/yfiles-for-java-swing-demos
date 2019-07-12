/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.rotatablenodes;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IHandleProvider;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IReshapeHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a rotate handle for a given node
 */
public class NodeRotateHandleProvider implements IHandleProvider {

  private final INode node;

  private final IReshapeHandler reshapeHandler;

  private static final double PI = Math.PI;

  /**
   * The angular step size to which rotation should snap (in radians).
   * Default is a quarter of pi. Setting to zero will disable snapping to predefined steps.
   */
  private double snapStep;

  /**
   * The snapping distance when rotation should snap (in radians).
   * The rotation will snap if the angle is less than this distance from
   * a {@link #snapStep snapping angle}. Setting this to a non-positive value
   * will disable snapping to predefined steps.
   */
  private double snapDelta;

  /**
   * The snapping distance (in radians) for snapping to the same angle as other visible nodes.
   * Rotation will snap to another node's rotation angle if the current angle
   * differs from the other one by less than this. Setting this to a
   * non-positive will disable same angle snapping
   */
  private double snapToSameAngleDelta;


  /**
   * Initializes a new instance for the given node.
   * @param node The node to handle.
   */
  NodeRotateHandleProvider(INode node) {
    this.node = node;
    reshapeHandler = node.lookup(IReshapeHandler.class);
    snapStep = PI / 4;
    snapDelta = Math.toRadians(10.0);
    snapToSameAngleDelta = Math.toRadians(5.0);
  }

  /**
   * Returns a set of handles for the rotated node.
   */
  public IEnumerable<IHandle> getHandles(IInputModeContext inputModeContext) {
    List<IHandle> handles = new ArrayList<>();

    NodeRotateHandle handle = new NodeRotateHandle(node, reshapeHandler, inputModeContext);
    handle.setSnapDelta(snapDelta);
    handle.setSnapStep(snapStep);
    handle.setSnapToSameAngleDelta(snapToSameAngleDelta);

    handles.add(handle);
    return IEnumerable.create(handles);
  }


  /**
   * Returns the angular step size to which rotation should snap (in radians).
   */
  public double getSnapStep() {
    return snapStep;
  }

  /**
   * Sets the angular step size to which rotation should snap (in radians).
   */
  public void setSnapStep(double snapStep) {
    this.snapStep = snapStep;
  }

  /**
   * Returns the snapping distance when rotation should snap (in radians).
   */
  public double getSnapDelta() {
    return snapDelta;
  }

  /**
   * Sets the snapping distance when rotation should snap (in radians).
   */
  public void setSnapDelta(double snapDelta) {
    this.snapDelta = snapDelta;
  }

  /**
   * Returns the snapping distance (in radians) for snapping to the same angle as other visible nodes.
   */
  public double getSnapToSameAngleDelta() {
    return snapToSameAngleDelta;
  }

  /**
   * Sets the snapping distance (in radians) for snapping to the same angle as other visible nodes.
   */
  public void setSnapToSameAngleDelta(double snapToSameAngleDelta) {
    this.snapToSameAngleDelta = snapToSameAngleDelta;
  }
}
