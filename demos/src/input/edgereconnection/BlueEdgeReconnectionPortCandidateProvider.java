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
package input.edgereconnection;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IEdgeReconnectionPortCandidateProvider} that allows moving ports to
 * any other existing port on any node. Additionally, this implementation
 * will reuse existing ports for nodes that are blue, and will create new
 * ports at the same location as the original port for the candidate of nodes
 * that are not blue.
 */
class BlueEdgeReconnectionPortCandidateProvider implements IEdgeReconnectionPortCandidateProvider {

  /**
   * Adds port candidates for all existing ports at all nodes in the graph as alternatives.
   * @see #getPortCandidates(com.yworks.yfiles.view.input.IInputModeContext)
   */
  public Iterable<IPortCandidate> getSourcePortCandidates(IInputModeContext context) {
    return getPortCandidates(context);
  }

  /**
   * Adds port candidates for all existing ports at all nodes in the graph as alternatives.
   * @see #getPortCandidates(com.yworks.yfiles.view.input.IInputModeContext)
   */
  public Iterable<IPortCandidate> getTargetPortCandidates(IInputModeContext context) {
    return getPortCandidates(context);
  }

  /**
   * Adds port candidates for all existing ports at all nodes in the graph as alternatives.
   */
  private Iterable<IPortCandidate> getPortCandidates(IInputModeContext context) {
    List<IPortCandidate> candidates = new ArrayList<>();
    IGraph graph = context.getGraph();
    if (graph != null) {
      graph.getNodes().forEach(node -> node.getPorts().forEach(port -> candidates.add(createPortCandidate(node, port))));
    }
    return candidates;
  }

  /**
   * Adds a port candidate dependent on the color tag of the given node.
   *
   * If the node is blue, then the existing port instances will be reused for
   * the port candidates. This has the effect that a port can potentially have
   * multiple edges connected to it.
   *
   * If the node is not blue, then a new port will be created at the same location
   * as the original port for the port candidate. This means that there are
   * potentially multiple ports at the same location and no edge shares a port
   * with another edge.
   *
   * @param node the node that the port belongs to
   *             (for convenience, it could also be retrieved directly from the port)
   * @param port the port to add the candidate for.
   */
  private IPortCandidate createPortCandidate(INode node, IPort port) {
    if (Colors.ROYAL_BLUE.equals(node.getTag())){
      // reuse the existing port - the edge will be connected to the very same port after reconnection
      return new DefaultPortCandidate(port);
    } else {
      // don't reuse the existing ports, but create new ones at the same location
      IPortLocationModelParameter param = port.getLocationParameter();
      return new DefaultPortCandidate(node, FreeNodePortLocationModel.INSTANCE.createParameter(node, param.getModel().getLocation(port, param)));
    }
  }
}
