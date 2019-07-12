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
package complete.bpmn.layout;

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.INodeCursor;
import com.yworks.yfiles.algorithms.LayoutGraphHider;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.NodeList;
import com.yworks.yfiles.layout.hierarchic.ConstraintIncrementalLayerer;
import com.yworks.yfiles.layout.hierarchic.ILayers;
import com.yworks.yfiles.layout.hierarchic.ILayoutDataProvider;
import com.yworks.yfiles.layout.hierarchic.TopologicalLayerer;
import com.yworks.yfiles.layout.LayoutGraph;

/**
 * A layerer stage that pulls back loop components to earlier layers to reduce the spanned layers of back edges.
 * <p>
 * A back loop component is a set of connected nodes satisfying the following rules:
 * </p>
 * <ul>
 * <li>the set contains no sink node, i.e. no node with out degree 0</li>
 * <li>all outgoing edges to nodes outside of this set are back edges.</li>
 * </ul>
 */
class BackLoopLayerer extends ConstraintIncrementalLayerer {
  /**
   * The state of a node while calculating those nodes on a back loop that might be pulled to a lower layer.
   */
  private enum NodeState {
    FIXED(0),

    BACK_LOOPING(1),

    BACK_LOOPING_CANDIDATE(2);

    private final int value;

    private NodeState( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final NodeState fromOrdinal( int ordinal ) {
      for (NodeState current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  private NodeState[] nodeStates;

  private int[] currentLayers;

  /**
   * Creates a new instance with the specified core layerer.
   */
  public BackLoopLayerer() {
    super(new TopologicalLayerer());
    setSameLayerEdgesAllowed(true);
  }

  @Override
  public void assignLayers( LayoutGraph graph, ILayers layers, ILayoutDataProvider ldp ) {
    // get core layer assignment
    super.assignLayers(graph, layers, ldp);

    // Hide all edges that are no sequence flows
    LayoutGraphHider graphHider = new LayoutGraphHider(graph);
    for (Edge edge : graph.getEdgeArray()) {
      if (!BpmnLayout.isSequenceFlow(edge, graph)) {
        graphHider.hide(edge);
      }
    }

    // determine current layer of all nodes
    currentLayers = new int[graph.nodeCount()];
    for (int i = 0; i < layers.size(); i++) {
      for (INodeCursor nc = layers.getLayer(i).getList().nodes(); nc.ok(); nc.next()) {
        currentLayers[nc.node().index()] = i;
      }
    }

    // mark nodes on a back-loop and candidates that may be on a back loop if other back-loop nodes are reassigned
    nodeStates = new NodeState[graph.nodeCount()];
    NodeList candidates = new NodeList();
    NodeList backLoopNodes = new NodeList();
    for (int i = layers.size() - 1; i >= 0; i--) {
      // check from last to first layer to detect candidates as well
      NodeList nodes = layers.getLayer(i).getList();
      updateNodeStates(nodes, backLoopNodes, candidates);
    }

    // swap layer for back-loop nodes
    while (backLoopNodes.size() > 0) {
      for (INodeCursor nc = backLoopNodes.nodes(); nc.ok(); nc.next()) {
        Node node = nc.node();
        int currentLayer = currentLayers[node.index()];
        // the target layer is the next layer after the highest fixed target node layer
        int targetLayer = 0;
        for (Edge edge = node.firstOutEdge(); edge != null; edge = edge.nextOutEdge()) {
          int targetNodeIndex = edge.target().index();
          if (nodeStates[targetNodeIndex] == NodeState.FIXED) {
            targetLayer = Math.max(targetLayer, currentLayers[targetNodeIndex] + 1);
          }
        }
        if (targetLayer == 0) {
          // no fixed target found, so all targets must be candidates
          // -> we skip the node as we don't know where the candidates will be placed at the end
          continue;
        }
        if (targetLayer < currentLayer) {
          layers.getLayer(currentLayer).remove(node);
          layers.getLayer(targetLayer).add(node);
          currentLayers[node.index()] = targetLayer;
          nodeStates[node.index()] = NodeState.FIXED;
        }
      }
      backLoopNodes.clear();

      // update states of the candidates
      candidates = updateNodeStates(candidates, backLoopNodes, new NodeList());
    }

    // remove empty layers
    for (int i = layers.size() - 1; i >= 0; i--) {
      if (layers.getLayer(i).getList().isEmpty()) {
        layers.remove(i);
      }
    }

    // cleanup
    graphHider.unhideAll();
    nodeStates = null;
    currentLayers = null;
  }

  private NodeList updateNodeStates( NodeList nodes, NodeList backLoopNodes, NodeList candidates ) {
    for (INodeCursor nc = nodes.nodes(); nc.ok(); nc.next()) {
      Node node = nc.node();
      NodeState nodeState = getNodeState(node);
      switch (nodeState) {
        case BACK_LOOPING:
          backLoopNodes.addFirst(node);
          break;
        case BACK_LOOPING_CANDIDATE:
          candidates.addFirst(node);
          break;
      }
      nodeStates[node.index()] = nodeState;
    }
    return candidates;
  }

  private NodeState getNodeState( Node node ) {
    int nodeLayer = currentLayers[node.index()];
    if (nodeLayer == 0) {
      // nodes in the first layer can't have any back edges
      return NodeState.FIXED;
    }
    NodeState nodeState = NodeState.FIXED;
    for (Edge edge = node.firstOutEdge(); edge != null; edge = edge.nextOutEdge()) {
      int targetIndex = edge.target().index();
      if (currentLayers[targetIndex] >= nodeLayer) {
        // no back-looping edge...
        if (nodeStates[targetIndex] == NodeState.BACK_LOOPING || nodeStates[targetIndex] == NodeState.BACK_LOOPING_CANDIDATE) {
          // ...but target is back-looping, so this one might be as well
          nodeState = NodeState.BACK_LOOPING_CANDIDATE;
        } else {
          // ... and target is fixed -> this node is fixed as well.
          nodeState = NodeState.FIXED;
          break;
        }
      } else {
        if (nodeState == NodeState.FIXED) {
          // no back looping candidate -> back-looping
          nodeState = NodeState.BACK_LOOPING;
        }
      }
    }
    return nodeState;
  }

}
