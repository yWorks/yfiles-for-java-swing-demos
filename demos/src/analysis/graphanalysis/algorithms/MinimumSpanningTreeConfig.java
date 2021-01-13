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
package analysis.graphanalysis.algorithms;

import analysis.graphanalysis.EdgeInfo;
import analysis.graphanalysis.styles.MultiColorNodeStyle;
import analysis.graphanalysis.NodeInfo;
import com.yworks.yfiles.analysis.SpanningTree;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;

import java.awt.Color;
import java.util.Collections;

/**
 * Configuration options for the minimum spanning tree algorithm.
 */
public class MinimumSpanningTreeConfig extends AlgorithmConfiguration {
  /**
   * Finds edges that belong to a minimum spanning tree in the graph.
   * @param graph The graph on which the minimum spanning tree algorithm is executed.
   */
  @Override
  protected void runAlgorithm(IGraph graph) {
    calculateSpanningTree(graph);
  }

  @Override
  public boolean supportsDirectedEdges() {
    return false;
  }

  @Override
  public boolean supportsEdgeWeights() {
    return true;
  }

  /**
   * Calculates the minimum spanning tree of the given graph.
   * @param graph The graph on which the minimum spanning tree algorithm is executed.
   */
  private void calculateSpanningTree(IGraph graph) {
    if (graph.getEdges().size() == 0) {
      return;
    }

    // reset edge styles
    graph.getEdges().forEach(edge -> {
      graph.setStyle(edge, graph.getEdgeDefaults().getStyle());
    });

    // calculate the edges of the minimum spanning tree
    SpanningTree spanningTree = new SpanningTree();
    spanningTree.setCosts(this::getEdgeWeight);
    SpanningTree.Result result = spanningTree.run(graph);

    // mark those edges with the color style
    result.getEdges().forEach(edge -> {
      if (graph.contains(edge)) {
        Color color = getComponentColor(0);
        graph.setStyle(edge, getMarkedEdgeStyle(false, color));
        INode source = edge.getSourceNode();
        INode target = edge.getTargetNode();

        graph.setStyle(source, new MultiColorNodeStyle());
        graph.setStyle(target, new MultiColorNodeStyle());

        edge.setTag(new EdgeInfo(color));
        source.setTag(new NodeInfo(color, Collections.singletonList(0)));
        target.setTag(new NodeInfo(color, Collections.singletonList(0)));
      }
    });
  }
}
