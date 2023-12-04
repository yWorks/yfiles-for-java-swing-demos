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
package input.orthogonaledges;

import com.yworks.yfiles.graph.styles.IShapeGeometry;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeHelper;

/**
 * The {@link OrthogonalEdgeHelper} for purple edges. It allows orthogonally edited edges to move the
 * source/target of the edge to another port, removes bends inside the bounds
 * of the node and relocates the port to the last bend inside the node.
 */
class PurpleOrthogonalEdgeHelper extends OrthogonalEdgeHelper {

  /**
   * Enable moving of source/target of the edge to other ports.
   */
  @Override
  public boolean shouldMoveEndImplicitly( IInputModeContext inputModeContext, IEdge edge, boolean sourceEnd ) {
    return true;
  }

  /**
   * Removes bends inside of nodes, in addition to the clean-ups provided by
   * the base implementation.
   */
  @Override
  public void cleanUpEdge(IInputModeContext inputModeContext, IGraph graph, IEdge edge) {
    // first let the base class perform the default clean up.
    super.cleanUpEdge(inputModeContext, graph, edge);

    // in this demo we know that the port owners are nodes.
    INode sourceNode = edge.getSourceNode();
    INode targetNode = edge.getTargetNode();

    // now check bends which lie inside the node bounds and remove them...
    if (sourceNode != null) {
      IShapeGeometry sourceContainsTest = sourceNode.lookup(IShapeGeometry.class);
      while (edge.getBends().size() > 0) {
        IBend firstBend = edge.getBends().getItem(0);
        if (!sourceContainsTest.isInside(firstBend.getLocation().toPointD())) {
          // we are done cutting
          break;
        }
        PointD bendLocation = firstBend.getLocation().toPointD();
        // we try to move to port to the bend location so that the edge shape stays the same
        graph.setPortLocation(edge.getSourcePort(), bendLocation);
        if (edge.getSourcePort().getLocation() != bendLocation) {
          break; // does not work - bail out
        }
        graph.remove(firstBend);
      }
    }
    if (targetNode != null) {
      IShapeGeometry targetContainsTest = targetNode.lookup(IShapeGeometry.class);
      // for the target node, iterate beginning from the last bend backwards
      while (edge.getBends().size() > 0) {
        IBend lastBend = edge.getBends().getItem(edge.getBends().size()-1);
        if (!targetContainsTest.isInside(lastBend.getLocation().toPointD())) {
          // we are done cutting
          break;
        }
        PointD bendLocation = lastBend.getLocation().toPointD();
        // we try to move to port to the bend location so that the edge shape stays the same
        graph.setPortLocation(edge.getTargetPort(), bendLocation);
        if (edge.getTargetPort().getLocation() != bendLocation) {
          break; // does not work - bail out
        }
        graph.remove(lastBend);
      }
    }
  }
}
