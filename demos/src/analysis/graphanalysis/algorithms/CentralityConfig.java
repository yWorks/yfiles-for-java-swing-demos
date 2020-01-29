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

import com.yworks.yfiles.analysis.BetweennessCentrality;
import com.yworks.yfiles.analysis.ClosenessCentrality;
import com.yworks.yfiles.analysis.ConnectedComponents;
import com.yworks.yfiles.analysis.DegreeCentrality;
import com.yworks.yfiles.analysis.GraphCentrality;
import com.yworks.yfiles.analysis.GraphStructureAnalyzer;
import com.yworks.yfiles.analysis.ResultItemMapping;
import com.yworks.yfiles.analysis.WeightCentrality;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.FilteredGraphWrapper;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.layout.CopiedLayoutGraph;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.organic.OrganicRemoveOverlapsStage;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.Pen;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Configuration options for centrality algorithms.
 */
public class CentralityConfig extends AlgorithmConfiguration {
  private final AlgorithmType algorithmType;

  /**
   * Initializes a new {@code CentralityConfig} instance for the given
   * algorithm type.
   * @param algorithmType the type of centrality algorithm to be used.  
   */
  public CentralityConfig(AlgorithmType algorithmType) {
    this.algorithmType = algorithmType;
  }

  /**
   * Runs the selected centrality algorithm.
   * @param graph the graph on which a centrality algorithm is executed.
   */
  @Override
  protected void runAlgorithm(IGraph graph) {
    resetGraph(graph);

    switch (algorithmType) {
      case WEIGHT_CENTRALITY: {
        WeightCentrality weightCentrality = new WeightCentrality();
        weightCentrality.setWeights(this::getEdgeWeight);
        weightCentrality.setOutgoingEdgesConsiderationEnabled(true);
        weightCentrality.setIncomingEdgesConsiderationEnabled(true);

        WeightCentrality.Result result = weightCentrality.run(graph);
        applyNodeCentralityColor(graph, result.getNormalizedNodeCentrality());
        break;
      }
      case GRAPH_CENTRALITY: {
        GraphCentrality graphCentrality = new GraphCentrality();
        graphCentrality.setWeights(this::getEdgeWeight);
        graphCentrality.setDirected(isDirected());

        GraphCentrality.Result result = graphCentrality.run(graph);
        applyNodeCentralityColor(graph, result.getNormalizedNodeCentrality());
        break;
      }
      case NODE_EDGE_BETWEENESS_CENTRALITY: {
        BetweennessCentrality betweennessCentrality = new BetweennessCentrality();
        betweennessCentrality.setWeights(this::getEdgeWeight);
        betweennessCentrality.setDirected(isDirected());

        BetweennessCentrality.Result result = betweennessCentrality.run(graph);
        applyNodeCentralityColor(graph, result.getNormalizedNodeCentrality());
        applyEdgeCentralityColor(graph, result.getNormalizedEdgeCentrality());
        break;
      }
      case CLOSENESS_CENTRALITY: {
        GraphStructureAnalyzer analyzer = new GraphStructureAnalyzer(graph);
        if (analyzer.isConnected()) {
          ClosenessCentrality closenessCentrality = new ClosenessCentrality();
          closenessCentrality.setWeights(this::getEdgeWeight);
          closenessCentrality.setDirected(isDirected());

          ClosenessCentrality.Result result = closenessCentrality.run(graph);
          applyNodeCentralityColor(graph, result.getNormalizedNodeCentrality());
        } else {
          Set<INode> componentsSet = new HashSet<>();
          FilteredGraphWrapper filteredGraph = new FilteredGraphWrapper(
              graph,
              componentsSet::contains,
              edge -> true
          );

          // if the graph in not connected, the algorithm is run separately for each connected component
          new ConnectedComponents().run(graph)
              .getComponents()
              .forEach(component -> {
            // update the filtered graph so that it contains only the nodes of the current component
            componentsSet.clear();
            componentsSet.addAll(component.getNodes().toList());
            filteredGraph.nodePredicateChanged();

            ClosenessCentrality closenessCentrality = new ClosenessCentrality();
            closenessCentrality.setWeights(this::getEdgeWeight);
            closenessCentrality.setDirected(isDirected());

            ClosenessCentrality.Result result = closenessCentrality.run(filteredGraph);
            applyNodeCentralityColor(filteredGraph, result.getNormalizedNodeCentrality());
          });
          // dispose the filtered graph
          filteredGraph.dispose();
        }
        break;
      }
      case DEGREE_CENTRALITY:
      default: {
        DegreeCentrality degreeCentrality = new DegreeCentrality();
        degreeCentrality.setOutgoingEdgesConsiderationEnabled(true);
        degreeCentrality.setIncomingEdgesConsiderationEnabled(true);

        DegreeCentrality.Result result = degreeCentrality.run(graph);
        applyNodeCentralityColor(graph, result.getNormalizedNodeCentrality());
        break;
      }
    }
  }

  /**
   * Applies colors to nodes according to their centrality values.
   * @param graph the given graph
   * @param centrality the centrality values for the graph's nodes.
   */
  private void applyNodeCentralityColor(IGraph graph, ResultItemMapping<INode, Double> centrality) {
    double[] extrema = getCentralityValues(graph, centrality);
    double min = extrema[0];
    double diff = extrema[1] - min;

    double mostCentralValue = 100;
    double leastCentralValue = 30;
    int colorNumber = 50;
    Color[] colors = newGradient(true, colorNumber);

    graph.getNodes().forEach(node -> {
      double centralityId = centrality.getValue(node);
      DefaultLabelStyle textLabelStyle = new DefaultLabelStyle();
      textLabelStyle.setTextPaint(Colors.WHITE);
      double centralityValue = Math.round(centralityId * 100d) / 100d;
      boolean isValid = !Double.isNaN(centralityValue) && !Double.isNaN(diff);

      node.setTag(isValid ? centralityValue : null);
      ILabel label = graph.addLabel(node, isValid ? Double.toString(centralityValue) : "Inf");
      graph.setStyle(label, textLabelStyle);
      label.setTag("centrality");

      if (diff == 0 || Double.isNaN(diff)) {
        graph.setStyle(node, getMarkedNodeStyle(colors[0]));
        graph.setNodeLayout(node, new RectD(node.getLayout().getX(), node.getLayout().getY(), leastCentralValue, leastCentralValue));
      } else {
        // adjust gradient color
        double colorScale = (colorNumber - 1) / diff;
        int index = (int) Math.ceil((centralityId - min) * colorScale);
        graph.setStyle(node, getMarkedNodeStyle(colors[index % colors.length]));

        // adjust size
        double sizeScale = (mostCentralValue - leastCentralValue) / diff;
        double size = Math.ceil(leastCentralValue + (centralityId - min) * sizeScale);
        graph.setNodeLayout(node, new RectD(node.getLayout().getX(), node.getLayout().getY(), size, size));
      }
    });
  }

  /**
   * Applies colors to edges according to their centrality values.
   * @param graph the given graph
   * @param centrality the centrality values for the graph's edges.
   */
  private void applyEdgeCentralityColor(IGraph graph, ResultItemMapping<IEdge, Double> centrality) {
    graph.getEdges().forEach(edge -> {
      double centralityId = centrality.getValue(edge);
      double centralityValue = Math.round(centralityId * 100d) / 100d;
      edge.setTag(centralityValue);

      DefaultLabelStyle labelStyle = new DefaultLabelStyle();
      labelStyle.setBackgroundPen(Pen.getLightSkyBlue());
      labelStyle.setBackgroundPaint(Colors.ALICE_BLUE);
      labelStyle.setAutoFlippingEnabled(false);
      labelStyle.setInsets(new InsetsD(3, 5, 3, 5));

      ILabel label = graph.addLabel(edge, Double.toString(centralityValue));
      graph.setStyle(label, labelStyle);
      label.setTag("centrality");
    });
  }

  /**
   * Returns a {@link LayoutStage} that changes node sizes according to the
   * nodes' centrality values.
   * @param coreLayout the core layout algorithm
   * @param directed true if edges should be considered directed, false otherwise
   * @return a layout stage that sets the node sizes according to their centrality value
   */
  public CentralityStage getCentralityStage(ILayoutAlgorithm coreLayout, boolean directed) {
    CentralityStage centralityStage = new CentralityStage(coreLayout);
    centralityStage.setCentrality(algorithmType);
    centralityStage.setDirected(directed);
    return centralityStage;
  }

  /**
   * Wraps the given core layout algorithm in a stage that changes node sizes
   * according to the nodes' centrality values.
   * @see #configure(LayoutData)
   */
  public ILayoutAlgorithm configure(ILayoutAlgorithm coreLayout) {
    // since the centrality stage changes the node sizes, node overlaps need to be removed
    return new OrganicRemoveOverlapsStage(getCentralityStage(coreLayout, isDirected()));
  }

  /**
   * Combines the given layout data with layout data necessary for node size
   * changes in {@link CentralityStage}.
   * @see #configure(ILayoutAlgorithm)
   */
  public LayoutData configure(LayoutData layoutData) {
    CentralityLayoutData cld = new CentralityLayoutData();
    cld.setEdgeWeight(this::getEdgeWeight);
    return layoutData.combineWith(cld);
  }

  /**
   * Determines the minimum and the maximum centrality value of the graph.
   * @param graph the given graph
   * @param centrality the centrality values for the graph's nodes.
   * @return the minimum and maximum of the centrality values
   */
  private double[] getCentralityValues(IGraph graph, ResultItemMapping<INode, Double> centrality) {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;

    for (INode node : graph.getNodes()) {
      Double centralityValue = centrality.getValue(node);
      min = Math.min(min, centralityValue);
      max = Math.max(max, centralityValue);
    }

    return new double[]{min, max};
  }

  @Override
  public boolean supportsDirectedEdges() {
    return false;
  }

  @Override
  public boolean supportsEdgeWeights() {
    return algorithmType != AlgorithmType.DEGREE_CENTRALITY;
  }



  public enum AlgorithmType {
    DEGREE_CENTRALITY,
    WEIGHT_CENTRALITY,
    GRAPH_CENTRALITY,
    NODE_EDGE_BETWEENESS_CENTRALITY,
    CLOSENESS_CENTRALITY
  }

  private static class CentralityLayoutData extends LayoutData {
    private Function<IEdge, Double> edgeWeight;

    Function<IEdge, Double> getEdgeWeight() {
      return edgeWeight;
    }

    void setEdgeWeight(Function<IEdge, Double> edgeWeight) {
      this.edgeWeight = edgeWeight;
    }

    @Override
    protected void apply(
            LayoutGraphAdapter adapter,
            ILayoutAlgorithm layout,
            CopiedLayoutGraph layoutGraph
    ) {
      adapter.addDataProvider(CentralityStage.EDGE_WEIGHTS_DPKEY, IMapper.fromFunction(getEdgeWeight()));
    }
  }
}
