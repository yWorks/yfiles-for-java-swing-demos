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
package input.edgereconnection;

import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IEdgeReconnectionPortCandidateProvider} that allows moving ports to
 * any other orange node, except for the opposite port's node.
 */
class OrangeReconnectionEdgePortCandidateProvider implements IEdgeReconnectionPortCandidateProvider {
  private IEdge edge;

  public OrangeReconnectionEdgePortCandidateProvider(IEdge edge) {
    this.edge = edge;
  }

  /**
   * Returns candidates for all ports at orange nodes in the graph, except for the current target node
   * to avoid the creation of self-loops
   */
  public Iterable<IPortCandidate> getSourcePortCandidates(IInputModeContext context) {
    List<IPortCandidate> candidates = new ArrayList<>();
    // add the current one as the default
    candidates.add(new DefaultPortCandidate(edge.getSourcePort()));

    IGraph graph = context.getGraph();
    if (graph != null) {
      graph.getNodes().stream()
          .filter(node -> node != edge.getTargetNode() && Colors.DARK_ORANGE.equals(node.getTag()))
          .forEach(node -> addCandidates(context, candidates, node, true));
    }
    return candidates;
  }

  /**
   * Returns candidates for all ports at orange nodes in the graph, except for the current source node
   * to avoid the creation of self-loops.
   */
  public Iterable<IPortCandidate> getTargetPortCandidates(IInputModeContext context) {
    List<IPortCandidate> candidates = new ArrayList<>();
    // add the current one as the default
    candidates.add(new DefaultPortCandidate(edge.getTargetPort()));

    IGraph graph = context.getGraph();
    if (graph != null) {
      graph.getNodes().stream()
          .filter(node -> node != edge.getSourceNode() && Colors.DARK_ORANGE.equals(node.getTag()))
          .forEach(node -> addCandidates(context, candidates, node, false));
    }
    return candidates;
  }

  /**
   * Adds candidates to the given list that are retrieved from the IPortCandidateProvider in the node's lookup.
   */
  private void addCandidates(IInputModeContext context, List<IPortCandidate> result, INode node, boolean source) {
    // use the candidates from the provider - if available
    IPortCandidateProvider provider = node.lookup(IPortCandidateProvider.class);
    if (provider != null) {
      (source ? provider.getSourcePortCandidates(context) : provider.getTargetPortCandidates(context)).forEach(result::add);
    } else {
      // add a default candidate...
      result.add(new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));
    }
  }
}
