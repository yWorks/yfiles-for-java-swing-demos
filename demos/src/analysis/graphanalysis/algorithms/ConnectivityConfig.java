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
package analysis.graphanalysis.algorithms;

import analysis.graphanalysis.EdgeInfo;
import analysis.graphanalysis.ModelItemInfo;
import analysis.graphanalysis.NodeInfo;
import analysis.graphanalysis.styles.MultiColorNodeStyle;
import com.yworks.yfiles.analysis.BiconnectedComponents;
import com.yworks.yfiles.analysis.ConnectedComponents;
import com.yworks.yfiles.analysis.Reachability;
import com.yworks.yfiles.analysis.ResultItemCollection;
import com.yworks.yfiles.analysis.ResultItemMapping;
import com.yworks.yfiles.analysis.StronglyConnectedComponents;
import com.yworks.yfiles.graph.AdjacencyTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphComponent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Configuration options for connectivity algorithms.
 */
public class ConnectivityConfig extends AlgorithmConfiguration {
  private final AlgorithmType algorithmType;
  private INode markedSource;

  /**
   * Initializes a new {@code ConnectivityConfig} for the given algorithm type.
   * @param algorithmType the type of connectivity algorithm to be used.
   */
  public ConnectivityConfig(AlgorithmType algorithmType) {
    this.algorithmType = algorithmType;
  }

  /**
   * Returns the marked node.
   * @return the marked node
   */
  public INode getMarkedSource() {
    return markedSource;
  }

  /**
   * Specifies the marked node.
   * @param markedSource the marked node
   */
  public void setMarkedSource(INode markedSource) {
    this.markedSource = markedSource;
  }

  /**
   * Runs the selected connectivity algorithm.
   * @param graph the graph on which the algorithm is executed executed
   */
  @Override
  protected void runAlgorithm(IGraph graph) {
    switch (algorithmType) {
      case BICONNECTED_COMPONENTS:
        calculateBiconnectedComponents(graph);
        break;
      case REACHABILITY:
        calculateReachableNodes(graph);
        break;
      case STRONGLY_CONNECTED_COMPONENTS:
        calculateConnectedComponents(graph, true);
        break;
      case CONNECTED_COMPONENTS:
      default:
        calculateConnectedComponents(graph, false);
        break;
    }
  }

  @Override
  public boolean supportsDirectedEdges() {
    return algorithmType == AlgorithmType.REACHABILITY;
  }

  @Override
  public boolean supportsEdgeWeights() {
    return false;
  }

  @Override
  protected boolean isAlwaysDirected() {
    return algorithmType == AlgorithmType.STRONGLY_CONNECTED_COMPONENTS;
  }

  /**
   * Calculates the connected components of the given graph.
   * @param graph The graph whose components are determined.
   * @param strong true, if strongly connected components have to be calculated;
   * false otherwise.
   */
  private void calculateConnectedComponents(IGraph graph, boolean strong) {
    int compNum;
    ResultItemMapping<INode, Integer> components;

    if (strong) {
      StronglyConnectedComponents.Result result = new StronglyConnectedComponents().run(graph);
      compNum = result.getComponents().size();
      components = result.getNodeComponentIds();
    } else {
      ConnectedComponents.Result result = new ConnectedComponents().run(graph);
      compNum = result.getComponents().size();
      components = result.getNodeComponentIds();
    }

    if (compNum > 0) {
      // set style and tag for nodes
      graph.getNodes().forEach(node -> {
        int componentIdx = components.getValue(node);
        graph.setStyle(node, new MultiColorNodeStyle());
        Color color = getComponentColor(componentIdx);
        node.setTag(new NodeInfo(color, Collections.singletonList(componentIdx)));
      });

      // set style and tag for edges
      graph.getEdges().forEach(edge -> {
        int sourceComponentIdx = components.getValue(edge.getSourceNode());
        int targetComponentIdx = components.getValue(edge.getTargetNode());
        if (sourceComponentIdx == targetComponentIdx) {
          Color color = ModelItemInfo.getColor(edge.getSourceNode());
          edge.setTag(new EdgeInfo(color));
          graph.setStyle(edge, getMarkedEdgeStyle(strong, color));
        }
      });
    }

    // clean up
    if (getIncrementalElements() != null) {
      getIncrementalElements().clear();
      setIncrementalElements(null);
      setEdgeRemoved(false);
    }
  }

  /**
   * Calculates the biconnected components of the given graph.
   * @param graph The graph whose biconnected components are determined.
   */
  private void calculateBiconnectedComponents(IGraph graph) {
    BiconnectedComponents.Result result = new BiconnectedComponents().run(graph);
    int bicompNum = result.getComponents().size();
    if (bicompNum > 0) {
      ResultItemMapping<IEdge, Integer> biconnectedComponents = result.getEdgeComponentIds();

      // set style and tag for edges
      graph.getEdges().forEach(edge -> {
        int componentIdx = biconnectedComponents.getValue(edge);
        Color color = null;
        if (componentIdx >= 0) {
          color = getComponentColor(componentIdx);

          graph.setStyle(edge, getMarkedEdgeStyle(false, color));
          graph.setStyle(edge.getSourceNode(), new MultiColorNodeStyle());
          graph.setStyle(edge.getTargetNode(), new MultiColorNodeStyle());
        }
        edge.setTag(new EdgeInfo(color));
      });

      // set style and  tag for nodes
      ResultItemCollection<INode> articulationNodes = result.getArticulationNodes();
      graph.getNodes().forEach(node -> {
        if (graph.edgesAt(node, AdjacencyTypes.ALL).size() == 0) {
          graph.setStyle(node, graph.getNodeDefaults().getStyle());
        } else {
          // get the first edge with non-negative component index of the node
          IEdge edge = findEdgeInBiconnectedComponent(graph, node, biconnectedComponents);
          // if an edge exists, use its color
          Color color = ModelItemInfo.getColor(edge);
          List<Integer> nodeComponents = edge != null
              ? Collections.singletonList(biconnectedComponents.getValue(edge))
              : Collections.EMPTY_LIST;
          node.setTag(new NodeInfo(color, nodeComponents));
        }
      });

      // reset style and tag for articulation points
      articulationNodes.forEach(node -> {
        Set<Integer> visitedComponents = new HashSet<>();
        graph.setStyle(node, new MultiColorNodeStyle());
        List<Integer> components = new ArrayList<>();
        Color color = null;

        for (IEdge edge : graph.edgesAt(node, AdjacencyTypes.ALL)) {
          int componentIdx = biconnectedComponents.getValue(edge);
          if (!visitedComponents.contains(componentIdx)) {
            visitedComponents.add(componentIdx);
            components.add(componentIdx);
            color = ModelItemInfo.getColor(edge);
          }
        };

        node.setTag(new NodeInfo(color, components));
      });
    }

    // clean up
    if (getIncrementalElements() != null) {
      getIncrementalElements().clear();
      setIncrementalElements(null);
      setEdgeRemoved(false);
    }
  }

  /**
   * Calculates the nodes reachable from the marked node.
   * @param graph The graph in which all reachable nodes are determined.
   */
  private void calculateReachableNodes(IGraph graph) {
    resetGraph(graph);

    if (graph.getNodes().size() > 0) {
      if (markedSource == null || !graph.contains(this.markedSource)) {
        markedSource = graph.getNodes().last();
      }
      Reachability reachability = new Reachability();
      reachability.setDirected(isDirected());
      reachability.setStartNodes(markedSource);

      Reachability.Result result = reachability.run(graph);

      Color color = getComponentColor(0);
      result.getReachableNodes().forEach(node -> {
        graph.setStyle(node, new MultiColorNodeStyle());
        node.setTag(new NodeInfo(color, Collections.singletonList(0)));
      });

      graph.getEdges().forEach(edge -> {
        if (result.isReachable(edge.getSourceNode()) && result.isReachable(edge.getTargetNode())) {
          graph.setStyle(edge, this.getMarkedEdgeStyle(isDirected(), color));
          edge.setTag(new EdgeInfo(color));
        }
      });

      graph.setStyle(markedSource, getSourceTargetNodeStyle(true, false));
    }
  }

  /**
   * Returns the first edge with non-negative component index of the given node.
   * @param graph the given graph
   * @param node the given node
   * @param biconnectedComponents the result of the biconnected components algorithm
   */
  private IEdge findEdgeInBiconnectedComponent(IGraph graph, INode node, ResultItemMapping<IEdge, Integer> biconnectedComponents) {
    IEdge edge = null;
    for (IEdge incidentEdge : graph.edgesAt(node, AdjacencyTypes.ALL)) {
      if (biconnectedComponents.getValue(incidentEdge) >= 0) {
        edge = incidentEdge;
      }
    }
    return edge;
  }

  /**
   * Adds a popup menu to mark the source node for the reachability algorithm.
   * @param contextMenu the context menu to which the entries are added
   * @param item the item that is affected by this context menu
   * @param graphComponent the given graph component
   */
  public void populateContextMenu(JPopupMenu contextMenu, IModelItem item, GraphComponent graphComponent) {
    if (algorithmType == AlgorithmType.REACHABILITY) {
      IGraph graph = graphComponent.getGraph();
      if (item instanceof INode) {
        contextMenu.add(new AbstractAction("Mark As Source") {
          @Override
          public void actionPerformed(ActionEvent e) {
            setMarkedSource((INode) item);
            resetGraph(graph);
            calculateReachableNodes(graph);
          }
        });
      }
    }
  }

  public enum AlgorithmType {
    CONNECTED_COMPONENTS,
    BICONNECTED_COMPONENTS,
    STRONGLY_CONNECTED_COMPONENTS,
    REACHABILITY
  }
}
