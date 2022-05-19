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
package analysis.graphanalysis.algorithms;

import analysis.graphanalysis.EdgeInfo;
import analysis.graphanalysis.NodeInfo;
import analysis.graphanalysis.styles.MultiColorNodeStyle;
import com.yworks.yfiles.analysis.ChainSubstructures;
import com.yworks.yfiles.analysis.CliqueSubstructures;
import com.yworks.yfiles.analysis.CycleSubstructures;
import com.yworks.yfiles.analysis.StarSubstructures;
import com.yworks.yfiles.analysis.TreeSubstructures;
import com.yworks.yfiles.analysis.ResultItemCollection;
import com.yworks.yfiles.analysis.Substructure;
import com.yworks.yfiles.graph.IGraph;

import java.awt.Color;
import java.util.Collections;

/**
 * Configuration options for substructure detection algorithms.
 */
public class SubstructuresConfig extends AlgorithmConfiguration {
  private final AlgorithmType algorithmType;

  /**
   * Initializes a new {@code SubstructuresConfig} for the given algorithm type.
   *
   * @param algorithmType the type of substructure detection algorithm to be used.
   */
  public SubstructuresConfig(AlgorithmType algorithmType) {
    this.algorithmType = algorithmType;
  }

  /**
   * Runs the selected substructure detection algorithm.
   *
   * @param graph the graph on which the algorithm is executed
   */
  @Override
  protected void runAlgorithm(IGraph graph) {
    switch (algorithmType) {
      case CHAIN_SUBSTRUCTURES: {
        ChainSubstructures chainSubstructures = new ChainSubstructures();
        chainSubstructures.setMinimumSize(2);
        chainSubstructures.setEdgeDirectedness(isDirected() ? 1 : 0);

        ChainSubstructures.Result substructures = chainSubstructures.run(graph);
        markSubstructures(graph, substructures.getChains());
        break;
      }
      case CLIQUE_SUBSTRUCTURES: {
        CliqueSubstructures cliqueSubstructures = new CliqueSubstructures();
        cliqueSubstructures.setMinimumSize(2);

        CliqueSubstructures.Result substructures = cliqueSubstructures.run(graph);
        markSubstructures(graph, substructures.getCliques());
        break;
      }
      case CYCLE_SUBSTRUCTURES: {
        CycleSubstructures cycleSubstructures = new CycleSubstructures();
        cycleSubstructures.setMinimumSize(2);
        cycleSubstructures.setEdgeDirectedness(isDirected() ? 1 : 0);

        CycleSubstructures.Result substructures = cycleSubstructures.run(graph);
        markSubstructures(graph, substructures.getCycles());
        break;
      }
      case STAR_SUBSTRUCTURES: {
        StarSubstructures starSubstructures = new StarSubstructures();
        starSubstructures.setMinimumSize(3);
        starSubstructures.setEdgeDirectedness(isDirected() ? 1 : 0);

        StarSubstructures.Result substructures = starSubstructures.run(graph);
        markSubstructures(graph, substructures.getStars());
        break;
      }
      case TREE_SUBSTRUCTURES: {
        TreeSubstructures treeSubstructures = new TreeSubstructures();
        treeSubstructures.setMinimumSize(2);
        treeSubstructures.setEdgeDirectedness(isDirected() ? 1 : 0);

        TreeSubstructures.Result substructures = treeSubstructures.run(graph);
        markSubstructures(graph, substructures.getTrees());
        break;
      }
    }
  }

  /**
   * Marks/Colorizes the substructures found by the substructure search algorithm.
   * @param graph the graph containing the substructures
   * @param substructures the substructures found
   */
  private void markSubstructures(IGraph graph, ResultItemCollection<Substructure> substructures) {
    resetGraph(graph);

    int componentIdx = 0;
    for (Substructure substructure : substructures) {
      componentIdx++;
      Color componentColor = getComponentColor(componentIdx);

      substructure.getEdges().forEach(edge -> {
        graph.setStyle(edge, getMarkedEdgeStyle(isDirected(), componentColor));
        edge.setTag(new EdgeInfo(componentColor));
      });

      int finalComponentIdx = componentIdx;
      substructure.getNodes().forEach(node -> {
        graph.setStyle(node, new MultiColorNodeStyle());
        graph.setStyle(node, new MultiColorNodeStyle());
        node.setTag(new NodeInfo(componentColor, Collections.singletonList(finalComponentIdx)));
      });
    }
  }

  @Override
  public boolean supportsDirectedEdges() {
    return AlgorithmType.CLIQUE_SUBSTRUCTURES != algorithmType;
  }

  @Override
  public boolean supportsEdgeWeights() {
    return false;
  }

  @Override
  protected boolean isAlwaysDirected() {
    return false;
  }

  public enum AlgorithmType {
    CHAIN_SUBSTRUCTURES,
    CLIQUE_SUBSTRUCTURES,
    CYCLE_SUBSTRUCTURES,
    STAR_SUBSTRUCTURES,
    TREE_SUBSTRUCTURES
  }
}
