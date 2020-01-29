/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
import analysis.graphanalysis.NodeInfo;
import analysis.graphanalysis.styles.MultiColorNodeStyle;
import com.yworks.yfiles.analysis.Chains;
import com.yworks.yfiles.analysis.Path;
import com.yworks.yfiles.analysis.Paths;
import com.yworks.yfiles.analysis.ResultItemCollection;
import com.yworks.yfiles.analysis.ResultItemMapping;
import com.yworks.yfiles.analysis.ShortestPath;
import com.yworks.yfiles.analysis.SingleSourceShortestPaths;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ISelectionModel;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration options for path algorithms.
 */
public class PathsConfig extends AlgorithmConfiguration {
  private final AlgorithmType algorithmType;

  private List<Path> paths;
  private ResultItemMapping<INode, Double> dist;
  private ResultItemMapping<INode, IEdge> pred;
  private List<INode> markedSources;
  private List<INode> markedTargets;

  /**
   * Initializes a new {@code PathsConfig} instance for the given
   * algorithm type.
   * @param algorithmType the type of path algorithm to be used.
   */
  public PathsConfig(AlgorithmType algorithmType) {
    this.algorithmType = algorithmType;
  }

  /**
   * Finds edges that belong to a path/chain in the graph.
   * @param graph The graph on which a path algorithm is executed.
   */
  @Override
  protected void runAlgorithm(IGraph graph) {
    // reset the graph to remove all previous markings
    resetGraph(graph);
    paths = new ArrayList<>();

    if (graph.getNodes().size() > 0) {
      if (markedSources == null || markedSources.isEmpty() || !nodesInGraph(markedSources, graph)) {
        // choose a source for the path if there isn't one already
        markedSources = new ArrayList<>();
        markedSources.add(graph.getNodes().first());
      }
      if (markedTargets == null || markedTargets.isEmpty() || !nodesInGraph(markedTargets, graph)) {
        // choose a target for the path if there isn't one already
        markedTargets = new ArrayList<>();
        markedTargets.add(graph.getNodes().last());
      }

      // apply one of the path algorithms
      switch (algorithmType) {
        case ALGORITHM_TYPE_SHORTEST_PATHS:
          calculateShortestPath(markedSources, markedTargets, graph);
          break;
        case ALGORITHM_TYPE_ALL_PATHS:
          calculateAllPaths(markedSources.get(0), markedTargets.get(0), graph);
          break;
        case ALGORITHM_TYPE_ALL_CHAINS:
          calculateAllChains(graph);
          break;
        case ALGORITHM_TYPE_SINGLE_SOURCE:
          calculateSingleSource(markedSources.get(0), graph);
          break;
        default:
          calculateShortestPath(markedSources, markedTargets, graph);
          break;
      }

      // mark the resulting paths
      markPaths(graph);
    }
  }

  @Override
  public boolean supportsDirectedEdges() {
    return true;
  }

  @Override
  public boolean supportsEdgeWeights() {
    return algorithmType == AlgorithmType.ALGORITHM_TYPE_SHORTEST_PATHS
        || algorithmType == AlgorithmType.ALGORITHM_TYPE_SINGLE_SOURCE;
  }

  /**
   * Calculates the shortest paths from each source to each target.
   * @param sources An array of source nodes.
   * @param targets An array of target nodes.
   * @param graph The graph on which the shortest path algorithm is executed.
   */
  private void calculateShortestPath(List<INode> sources, List<INode> targets, IGraph graph) {
    if (sources != null && targets != null && targets.size() > 0) {
      for (INode source : sources) {
        ShortestPath shortestPath = new ShortestPath();
        shortestPath.setDirected(isDirected());
        shortestPath.setCosts(this::getEdgeWeight);
        shortestPath.setSource(source);
        shortestPath.setSink(targets.get(0));

        ShortestPath.Result result = shortestPath.run(graph);
        if (result.getPath() != null) {
          paths.add(result.getPath());
        }
      }
    } else {
      paths.clear();
    }
  }

  /**
   * Calculates all paths between the given source and target nodes.
   * @param source The source node for the paths.
   * @param target The target node for the paths.
   * @param graph The graph on which the all paths algorithm is executed.
   */
  private void calculateAllPaths(INode source, INode target, IGraph graph) {
    // Check if graph contains source and target nodes
    if (source != null && target != null && graph.contains(source) && graph.contains(target)) {
      Paths paths = new Paths();
      paths.setDirected(isDirected());
      paths.setStartNodes(source);
      paths.setEndNodes(target);

      Paths.Result result = paths.run(graph);
      // add resulting edges to set
      result.getPaths().forEach(path -> this.paths.add(path));
    } else {
      paths.clear();
    }
  }

  /**
   * Calculates all chains in the graph.
   * @param graph The graph on which the all chains algorithm is executed.
   */
  private void calculateAllChains(IGraph graph) {
    // run algorithm
    Chains chains = new Chains();
    chains.setDirected(isDirected());

    Chains.Result result = chains.run(graph);
    result.getChains().forEach(chain -> paths.add(chain));
  }

  /**
   * Calculates the paths from the given source node to all reachable nodes in the graph.
   * @param source The source node for the paths.
   * @param graph The graph on which the single source algorithm is executed.
   */
  private void calculateSingleSource(INode source, IGraph graph) {
    // run algorithm
    SingleSourceShortestPaths singleSourceShortestPaths = new SingleSourceShortestPaths();
    singleSourceShortestPaths.setSource(source);
    singleSourceShortestPaths.setSinks(graph.getNodes());
    singleSourceShortestPaths.setDirected(isDirected());
    singleSourceShortestPaths.setCosts(this::getEdgeWeight);

    SingleSourceShortestPaths.Result result = singleSourceShortestPaths.run(graph);
    dist = result.getDistances();
    pred = result.getPredecessors();
  }

  /**
   * Adds different colors to independent paths.
   * If some nodes or edges are selected, only paths that depend on them are marked.
   * @param graph The graph in which the paths are marked.
   */
  private void markPaths(IGraph graph) {
    if (algorithmType != AlgorithmType.ALGORITHM_TYPE_SINGLE_SOURCE) {
      List<IModelItem>[] allPaths = new List[paths.size()];
      for (int i = 0; i < allPaths.length; i++) {
        allPaths[i] = new ArrayList<>();
      }
      if (paths != null && paths.size() > 0) {
        // change the styles for the nodes and edges that belong to a path
        for (int i = 0; i < paths.size(); i++) {
          ResultItemCollection<IEdge> path = paths.get(i).getEdges();
          for (int index = 0; index < path.size(); index++) {
            IEdge edge = path.getItem(index);
            INode source = edge.getSourceNode();
            INode target = edge.getTargetNode();

            graph.setStyle(source, new MultiColorNodeStyle());
            graph.setStyle(target, new MultiColorNodeStyle());
            graph.setStyle(edge, getMarkedEdgeStyle(isDirected(), getComponentColor(i)));

            if (path.size() == 1 ||
                (index == 0 &&
                    (target == path.getItem(index + 1).getSourceNode() ||
                     target == path.getItem(index + 1).getTargetNode())) ||
                (index > 0 &&
                    (source == path.getItem(index - 1).getSourceNode() ||
                     source == path.getItem(index - 1).getTargetNode()))
            ) {
              allPaths[i].add(source);
              allPaths[i].add(edge);
              allPaths[i].add(target);
            } else {
              allPaths[i].add(target);
              allPaths[i].add(edge);
              allPaths[i].add(source);
            }
          }
        }

        graph.getNodes().forEach(node -> {
          List<Integer> nodePaths = new ArrayList<>();
          for (int i = 0; i < allPaths.length; i++) {
            if (allPaths[i].indexOf(node) > -1) {
              nodePaths.add(i);
            }
          }
          node.setTag(new NodeInfo(null, nodePaths));
        });

        graph.getEdges().forEach(edge -> {
          edge.setTag(new EdgeInfo(null));
        });
      }
    } else {
      // add a gradient to indicate the distance of the nodes to the source
      markedTargets.clear();
      if (dist != null && pred != null) {
        double maxDistance = 0;
        for (INode node : graph.getNodes()) {
          double dist = this.dist.getValue(node);
          if (dist < Double.POSITIVE_INFINITY) {
            maxDistance = Math.max(maxDistance, dist);
          }
        }
        int count = (int) maxDistance + 1;
        Color[] colors = newGradient(false, count);
        for (INode node : graph.getNodes()) {
          double distToSource = dist.getValue(node);
          IEdge predEdge = pred.getValue(node);

          node.setTag(null);

          if (node == markedSources.get(0)) {
            graph.setStyle(node, getMarkedNodeStyle(colors[0]));
          } else if (distToSource < Double.POSITIVE_INFINITY) {
            int idx = (int) Math.round(distToSource);
            Color color = colors[idx % colors.length];
            graph.setStyle(node, getMarkedNodeStyle(color));
            graph.setStyle(predEdge, getMarkedEdgeStyle(isDirected(), color));
            predEdge.setTag(null);
          }
        }
      }
    }

    if (algorithmType != AlgorithmType.ALGORITHM_TYPE_ALL_CHAINS) {
      if (algorithmType == AlgorithmType.ALGORITHM_TYPE_SHORTEST_PATHS) {
        // mark source and target of the paths
        if (markedSources != null) {
          markedSources.forEach(source ->
              graph.setStyle(source, getSourceTargetNodeStyle(true, markedTargets.indexOf(source) >= 0))
          );
        }
        if (markedTargets != null) {
          markedTargets.forEach(target ->
              graph.setStyle(target, getSourceTargetNodeStyle(markedSources.indexOf(target) >= 0, true))
          );
        }
      } else {
        // mark the source
        if (hasFirst(markedSources)) {
          graph.setStyle(
              markedSources.get(0),
              getSourceTargetNodeStyle(true,
                  hasFirst(markedTargets) && markedSources.get(0) == markedTargets.get(0)));
        }
        if (hasFirst(markedTargets) && algorithmType == AlgorithmType.ALGORITHM_TYPE_ALL_PATHS) {
          graph.setStyle(
              markedTargets.get(0),
              getSourceTargetNodeStyle(markedSources.get(0) == markedTargets.get(0), true));
        }
      }
    }
  }

  private boolean hasFirst(List<INode> list) {
    return list != null && !list.isEmpty() && list.get(0) != null;
  }

  /**
   * Adds entries to the context menu to mark source and target nodes.
   * @param contextMenu The context menu to extend
   * @param item The item which is affected by the context menu
   * @param graphComponent The current graph component.
   */
  public void populateContextMenu(JPopupMenu contextMenu, IModelItem item, GraphComponent graphComponent) {
    IGraph graph = graphComponent.getGraph();
    if (item instanceof INode) {
      INode node = (INode) item;
      if (algorithmType == AlgorithmType.ALGORITHM_TYPE_SHORTEST_PATHS) {

        updateSelection(node, graphComponent);

        ISelectionModel<INode> selectedNodes = graphComponent.getSelection().getSelectedNodes();
        if (selectedNodes.size() > 0) {
          contextMenu.add(new AbstractAction("Mark as Source") {
            @Override
            public void actionPerformed(ActionEvent e) {
              markedSources = selectedNodes.toList();
              runAlgorithm(graph);
            }
          });
          contextMenu.add(new AbstractAction("Mark as Target") {
            @Override
            public void actionPerformed(ActionEvent e) {
              markedTargets = selectedNodes.toList();
              runAlgorithm(graph);
            }
          });
        }
      } else if (algorithmType != AlgorithmType.ALGORITHM_TYPE_ALL_CHAINS) {
        if (item instanceof INode) {
          contextMenu.add(new AbstractAction("Mark As Source") {
            @Override
            public void actionPerformed(ActionEvent e) {
              markedSources = new ArrayList<>();
              markedSources.add(node);
              runAlgorithm(graph);

            }
          });
          if (algorithmType != AlgorithmType.ALGORITHM_TYPE_SINGLE_SOURCE) {
            contextMenu.add(new AbstractAction("Mark As Target") {
              @Override
              public void actionPerformed(ActionEvent e) {
                markedTargets = new ArrayList<>();
                markedTargets.add(node);
                runAlgorithm(graph);
              }
            });
          }
        }
      }
    }
  }

  /**
   * Updates the selection of the graph so the given node is the only selected node.
   * @param node The only node that should be selected.
   * @param graphComponent The graph component that contains the graph to which the node belongs.
   */
  private void updateSelection(INode node, GraphComponent graphComponent) {
    if (node == null) {
      graphComponent.getSelection().clear();
    } else if (!graphComponent.getSelection().getSelectedNodes().isSelected(node)) {
      graphComponent.getSelection().clear();
      graphComponent.getSelection().getSelectedNodes().setSelected(node, true);
      graphComponent.setCurrentItem(node);
    }
  }

  /**
   * Returns whether or not all the given nodes belong to the graph.
   * @param nodes The nodes.
   * @param graph The graph.
   * @return {@code true} if all nodes belong to the graph, {@code false} otherwise.
   */
  private boolean nodesInGraph(List<INode> nodes, IGraph graph) {
    for (INode node : nodes) {
      if (!graph.contains(node)) {
        return false;
      }
    }
    return true;
  }



  public enum AlgorithmType {
    ALGORITHM_TYPE_SHORTEST_PATHS,
    ALGORITHM_TYPE_ALL_PATHS,
    ALGORITHM_TYPE_ALL_CHAINS,
    ALGORITHM_TYPE_SINGLE_SOURCE
  }
}
