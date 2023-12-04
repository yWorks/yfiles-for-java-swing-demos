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
package analysis.graphanalysis.algorithms;

import com.yworks.yfiles.algorithms.Centrality;
import com.yworks.yfiles.algorithms.EdgeDpKey;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.IEdgeMap;
import com.yworks.yfiles.algorithms.ILineSegmentCursor;
import com.yworks.yfiles.algorithms.INodeMap;
import com.yworks.yfiles.algorithms.Maps;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YPointPath;
import com.yworks.yfiles.layout.AbstractLayoutStage;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.INodeLayout;
import com.yworks.yfiles.layout.LayoutGraph;

/**
 * Changes the sizes of nodes according to their centrality values.
 */
public class CentralityStage extends AbstractLayoutStage {
  public static final EdgeDpKey<Double> EDGE_WEIGHTS_DPKEY = new EdgeDpKey<>(Double.class, CentralityStage.class, "CentralityStage.EDGE_WEIGHTS_DPKEY");

  private CentralityConfig.AlgorithmType centrality;
  private boolean directed;

  /**
   * Initializes a new {@code CentralityStage} instance with the given core
   * layout algorithm.
   * @param layout The core layout algorithm.
   */
  public CentralityStage(ILayoutAlgorithm layout) {
    super(layout);
    this.centrality = CentralityConfig.AlgorithmType.DEGREE_CENTRALITY;
    this.directed = false;
  }

  /**
   * Returns the centrality algorithm that will be applied.
   * @return centrality the centrality algorithm
   */
  public CentralityConfig.AlgorithmType getCentrality() {
    return centrality;
  }

  /**
   * Specifies the centrality algorithm that will be applied.
   * @param centrality the centrality algorithm
   */
  public void setCentrality(CentralityConfig.AlgorithmType centrality) {
    this.centrality = centrality;
  }

  /**
   * Returns whether the edges should be considered directed or undirected.
   * @return true if the edges should be considered directed, false otherwise
   */
  public boolean isDirected() {
    return directed;
  }

  /**
   * Specifies whether the edges should be considered directed or undirected.
   * @param directed true if the edges should be considered directed, false otherwise
   */
  public void setDirected(boolean directed) {
    this.directed = directed;
  }

  @Override
  public void applyLayout(LayoutGraph graph) {
    // run the core layout
    this.applyLayoutCore(graph);

    // run the selected centrality algorithm
    INodeMap centrality = Maps.createHashedNodeMap();
    IDataProvider weightProvider = graph.getDataProvider(EDGE_WEIGHTS_DPKEY);

    IEdgeMap weights = Maps.createHashedEdgeMap();

    graph.getEdges().forEach(edge -> {
      if (weightProvider.get(edge) != null) {
        weights.setDouble(edge, weightProvider.getDouble(edge));
      } else {
        // calculate geometric edge length
        YPointPath path = graph.getPath(edge);
        double totalEdgeLength = 0;
        for (ILineSegmentCursor cursor = path.lineSegments(); cursor.ok(); cursor.next()) {
          totalEdgeLength += cursor.lineSegment().length();
        }
        weights.setDouble(edge, totalEdgeLength);
      }
    });

    switch (this.centrality) {
      case DEGREE_CENTRALITY:
        Centrality.degreeCentrality(graph, centrality, true, true);
        break;
      case WEIGHT_CENTRALITY:
        Centrality.weightCentrality(graph, centrality, true, true, weights);
        break;
      case GRAPH_CENTRALITY:
        Centrality.graphCentrality(graph, centrality, this.directed, weights);
        break;
      case NODE_EDGE_BETWEENESS_CENTRALITY:
        IEdgeMap edgeCentrality = Maps.createHashedEdgeMap();
        Centrality.nodeEdgeBetweenness(graph, centrality, edgeCentrality, this.directed, weightProvider);
        break;
      case CLOSENESS_CENTRALITY:
        Centrality.closenessCentrality(graph, centrality, this.directed, weights);
        break;
      default:
        // DEGREE_CENTRALITY
        Centrality.degreeCentrality(graph, centrality, true, true);
        break;
    }
    Centrality.normalize(graph, centrality);

    // change the node sizes
    double mostCentralSize = 100;
    double leastCentralSize = 30;
    double[] extrema = getCentralityValues(graph, centrality);
    double min = extrema[0];
    double diff = extrema[1] - min;
    graph.getNodes().forEach(node -> {
      double centralityId = centrality.getDouble(node);
      INodeLayout nodeLayout = graph.getLayout(node);
      if (diff > 0) {
        double sizeScale = (mostCentralSize - leastCentralSize) / diff;
        double size = Math.ceil(leastCentralSize + (centralityId - min) * sizeScale);
        nodeLayout.setSize(size, size);
        nodeLayout.setLocation(
            nodeLayout.getX() + (nodeLayout.getWidth() * 0.5 - size * 0.5),
            nodeLayout.getY() + (nodeLayout.getHeight() * 0.5 - size * 0.5)
        );
      } else {
        nodeLayout.setSize(leastCentralSize, leastCentralSize);
        nodeLayout.setLocation(
            nodeLayout.getX() + (nodeLayout.getWidth() * 0.5 - leastCentralSize * 0.5),
            nodeLayout.getY() + (nodeLayout.getHeight() * 0.5 - leastCentralSize * 0.5)
        );
      }
    });
  }

  /**
   * Determines the minimum and the maximum centrality value of the graph.
   * @param graph the given graph
   * @param centrality the centrality values for the graph's nodes.
   * @return the minimum and maximum of the centrality values
   */
  private double[] getCentralityValues(LayoutGraph graph, INodeMap centrality) {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;

    for (Node node : graph.getNodes()) {
      double centralityValue = centrality.getDouble(node);
      min = Math.min(min, centralityValue);
      max = Math.max(max, centralityValue);
    }

    return new double[]{min, max};
  }
}
