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
import analysis.graphanalysis.NodeInfo;
import analysis.graphanalysis.styles.MultiColorNodeStyle;
import com.yworks.yfiles.analysis.ConnectedComponents;
import com.yworks.yfiles.analysis.CycleEdges;
import com.yworks.yfiles.analysis.ResultItemCollection;
import com.yworks.yfiles.graph.FilteredGraphWrapper;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Configuration options for the cycles algorithm.
 */
public class CyclesConfig extends AlgorithmConfiguration {
  private ResultItemCollection<IEdge> cycleEdges;

  /**
   * Finds edges that belong to a cycle in the graph.
   * @param graph The graph on which the cycle algorithm is executed.
   */
  @Override
  protected void runAlgorithm(IGraph graph) {
    // reset cycles to remove previous markings
    resetCycles(graph);

    CycleEdges cycleEdges = new CycleEdges();
    cycleEdges.setDirected(isDirected());

    // find all edges that belong to a cycle
    CycleEdges.Result result = cycleEdges.run(graph);
    this.cycleEdges = result.getEdges();

    markCycles(graph);
  }

  @Override
  public boolean supportsDirectedEdges() {
    return true;
  }

  @Override
  public boolean supportsEdgeWeights() {
    return false;
  }

  /**
   * Adds different styles to independent cycles.
   * If some nodes or edges are selected only cycles that depend on them are marked.
   * @param graph The graph whose cycles are marked.
   */
  private void markCycles(IGraph graph) {
    List<IEdge> cycleEdges = this.cycleEdges.toList();

    // hide all non-cycle edges to be able to find independent cycles
    Set<IEdge> cycleEdgeSet = new HashSet<>(cycleEdges);
    FilteredGraphWrapper filteredGraph = new FilteredGraphWrapper(
        graph,
        node -> true,
        edge -> cycleEdgeSet.contains(edge)
    );

    // find the edges that belong to the same component (without non-cycle edges) and treat them as dependent
    ConnectedComponents.Result result = new ConnectedComponents().run(filteredGraph);

    // dispose the filtered graph
    filteredGraph.dispose();

    Set<INode> cyclesNodeSet = new HashSet<>();
    // change styles for nodes and edges that belong to a cycle
    graph.getEdges().forEach(edge -> {
      INode source = edge.getSourceNode();
      INode target = edge.getTargetNode();

      int componentIndex = result.getNodeComponentIds().getValue(source);
      if (cycleEdgeSet.contains(edge)) {
        Color color = getComponentColor(componentIndex);
        graph.setStyle(edge, getMarkedEdgeStyle(isDirected(), color));
        edge.setTag(new EdgeInfo(color));

        if (cyclesNodeSet.add(source)) {
          graph.setStyle(source, new MultiColorNodeStyle());
          source.setTag(new NodeInfo(color, Collections.singletonList(componentIndex)));
        }
        if (cyclesNodeSet.add(target)) {
          graph.setStyle(target, new MultiColorNodeStyle());
          target.setTag(new NodeInfo(color, Collections.singletonList(componentIndex)));
        }
      } else {
        graph.setStyle(edge, graph.getEdgeDefaults().getStyle());
        edge.setTag(new EdgeInfo(getComponentColor(0)));
      }
    });

    // for all edges that do not belong to the cycle, reset their style
    graph.getNodes().forEach(node -> {
      if (!cyclesNodeSet.contains(node)) {
        graph.setStyle(node, graph.getNodeDefaults().getStyle());
      }
    });

    // clean up
    if (getIncrementalElements() != null) {
      getIncrementalElements().clear();
      setIncrementalElements(null);
      setEdgeRemoved(false);
    }
  }

  /**
   * Resets the style of all edges that belong to a cycle and the source and
   * target nodes of those edges.
   */
  private void resetCycles(IGraph graph) {
    // reset style of previous cycle edges
    ResultItemCollection<IEdge> cycleEdges = this.cycleEdges;
    if (cycleEdges != null) {
      cycleEdges.forEach(cycleEdge -> {
        if (graph.contains(cycleEdge)) {
          IEdgeStyle defaultEdgeStyle = graph.getEdgeDefaults().getStyle();
          graph.setStyle(cycleEdge, defaultEdgeStyle);

          INode sourceNode = cycleEdge.getSourceNode();
          INode targetNode = cycleEdge.getTargetNode();
          INodeStyle defaultNodeStyle = graph.getNodeDefaults().getStyle();
          graph.setStyle(sourceNode, defaultNodeStyle);
          graph.setStyle(targetNode, defaultNodeStyle);
        }
      });
    }
  }
}
