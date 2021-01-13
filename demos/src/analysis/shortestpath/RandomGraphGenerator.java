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
package analysis.shortestpath;

import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.IPortOwner;
import com.yworks.yfiles.graph.Mapper;

import java.util.Random;

/**
 * A class that creates random graphs.
 * <p>
 *   The size of the graph and other options may be specified. These options influence
 *   the properties of the created graph.
 * </p>
 */
class RandomGraphGenerator {
  private int nodeCount;
  private int edgeCount;
  private boolean cycleCreationAllowed;
  private boolean selfLoopCreationAllowed;
  private boolean parallelEdgeCreationAllowed;

  private final Random random;

  /** Constructs a new random graph generator */
  public RandomGraphGenerator() {
    this((int) System.currentTimeMillis());
  }

  /** Constructs a new random graph generator that uses the given random seed to initialize. */
  public RandomGraphGenerator(int seed) {
    random = new Random(seed);
    nodeCount = 30;
    edgeCount = 40;
    selfLoopCreationAllowed = false;
    cycleCreationAllowed = true;
    parallelEdgeCreationAllowed = false;
  }

  /**
   * Returns the node count of the graph to be generated.
   * <p>
   *   The default value is 30.
   * </p>
   */
  public int getNodeCount() {
    return nodeCount;
  }

  /**
   * Specifies the node count of the graph to be generated.
   * <p>
   *   The default value is 30.
   * </p>
   */
  public void setNodeCount(int nodeCount) {
    this.nodeCount = nodeCount;
  }

  /**
   * Returns the edge count of the graph to be generated.
   * <p>
   *   The default value is 40. If the edge count is higher than it is theoretically possible by the generator options
   *   set, then the highest possible edge count is applied instead.
   * </p>
   */
  public int getEdgeCount() {
    return edgeCount;
  }

  /**
   * Specifies the edge count of the graph to be generated.
   * <p>
   *   The default value is 40. If the edge count is higher than it is theoretically possible by the generator options
   *   set, then the highest possible edge count is applied instead.
   * </p>
   */
  public void setEdgeCount(int edgeCount) {
    this.edgeCount = edgeCount;
  }

  /**
   * Returns whether or not to allow the generation of cyclic graphs, i.e. graphs that contain directed cyclic paths. If
   * allowed it still could happen by chance that the generated graph is acyclic. By default allowed.
   */
  public boolean isCycleCreationAllowed() {
    return cycleCreationAllowed;
  }

  /**
   * Specifies whether or not to allow the generation of cyclic graphs, i.e. graphs that contain directed cyclic paths.
   * If allowed it still could happen by chance that the generated graph is acyclic. By default allowed.
   */
  public void setCycleCreationAllowed(boolean cycleCreationAllowed) {
    this.cycleCreationAllowed = cycleCreationAllowed;
  }

  /**
   * Returns whether or not to allow the generation of selfloops, i.e. edges with same source and target nodes. If
   * allowed it still could happen by chance that the generated graph contains no selfloops. By default disallowed.
   */
  public boolean isSelfLoopCreationAllowed() {
    return selfLoopCreationAllowed;
  }

  /**
   * Specifies whether or not to allow the generation of selfloops, i.e. edges with same source and target nodes. If
   * allowed it still could happen by chance that the generated graph contains no selfloops. By default disallowed.
   */
  public void setSelfLoopCreationAllowed(boolean selfLoopCreationAllowed) {
    this.selfLoopCreationAllowed = selfLoopCreationAllowed;
  }

  /**
   * Returns whether or not to allow the generation of graphs that contain parallel edges, i.e. graphs that has more
   * than one edge that connect the same pair of nodes. If allowed it still could happen by chance that the generated
   * graph does not contain parallel edges. By default disallowed.
   */
  public boolean isParallelEdgeCreationAllowed() {
    return parallelEdgeCreationAllowed;
  }

  /**
   * Specifies whether or not to allow the generation of graphs that contain parallel edges, i.e. graphs that has more
   * than one edge that connect the same pair of nodes. If allowed it still could happen by chance that the generated
   * graph does not contain parallel edges. By default disallowed.
   */
  public void setParallelEdgeCreationAllowed(boolean parallelEdgeCreationAllowed) {
    this.parallelEdgeCreationAllowed = parallelEdgeCreationAllowed;
  }

  /**
   * Returns a newly created random graph that obeys the specified settings.
   */
  public IGraph generate() {
    IGraph graph = new DefaultGraph();
    generate(graph);
    return graph;
  }

  /**
   * Clears the given graph and generates new nodes and edges for it, so that the specified settings are obeyed.
   */
  public void generate(IGraph graph) {
    if (isParallelEdgeCreationAllowed()) {
      generateGraphWithParallelEdges(graph);
    } else {
      int n = getNodeCount();
      int m = getEdgeCount();
      if (n > 1 && m > 10 && Math.log(n) * n < m) {
        generateDenseGraph(graph);
      } else {
        generateSparseGraph(graph);
      }
    }
  }

  /**
   * Random graph generator in case parallel edges are allowed.
   */
  private void generateGraphWithParallelEdges(IGraph graph) {
    int n = getNodeCount();
    int m = getEdgeCount();
    IMapper<IPortOwner, Integer> index = new Mapper<>();

    int[] deg = new int[n];
    INode[] V = new INode[n];
    for (int i = 0; i < n; i++) {
      V[i] = graph.createNode();
      index.setValue(V[i], i);
    }

    for (int i = 0; i < m; i++) {
      deg[random.nextInt(n)]++;
    }

    for (int i = 0; i < n; i++) {
      INode v = V[i];
      int d = deg[i];
      while (d > 0) {
        int j = random.nextInt(n);
        if (j == i && (!isCycleCreationAllowed() || !isSelfLoopCreationAllowed())) {
          continue;
        }
        graph.createEdge(v, V[j]);
        d--;
      }
    }

    if (!isCycleCreationAllowed()) {
      for (IEdge edge : graph.getEdges()) {
        IPort sourcePort = edge.getSourcePort();
        IPort targetPort = edge.getTargetPort();
        if (index.getValue(sourcePort.getOwner()) > index.getValue(targetPort.getOwner())) {
          graph.setEdgePorts(edge, targetPort, sourcePort);
        }
      }
    }
  }

  /**
   * Random graph generator for dense graphs.
   */
  private void generateDenseGraph(IGraph graph) {
    graph.clear();
    INode[] nodes = new INode[getNodeCount()];

    for (int i = 0; i < getNodeCount(); i++) {
      nodes[i] = graph.createNode();
    }

    RandomSupport.permute(random, nodes);

    int m = Math.min(getMaxEdges(), edgeCount);
    int n = getNodeCount();

    int adder = (isSelfLoopCreationAllowed() && isCycleCreationAllowed()) ? 0 : 1;

    boolean[] edgeWanted = RandomSupport.getBoolArray(random, getMaxEdges(), m);
    for (int i = 0, k = 0; i < n; i++) {
      for (int j = i + adder; j < n; j++, k++) {
        if (edgeWanted[k]) {
          if (isCycleCreationAllowed() && random.nextDouble() > 0.5f) {
            graph.createEdge(nodes[j], nodes[i]);
          } else {
            graph.createEdge(nodes[i], nodes[j]);
          }
        }
      }
    }
  }

  /**
   * Random graph generator for sparse graphs.
   */
  private void generateSparseGraph(IGraph graph) {
    graph.clear();
    IMapper<IPortOwner, Integer> index = new Mapper<>();

    int n = getNodeCount();
    int m = Math.min(getMaxEdges(), getEdgeCount());

    INode[] V = new INode[n];

    for (int i = 0; i < n; i++) {
      V[i] = graph.createNode();
      index.setValue(V[i], i);
    }

    RandomSupport.permute(random, V);

    int count = m;
    while (count > 0) {
      int vi = random.nextInt(n);
      INode v = V[vi];
      INode w = V[random.nextInt(n)];

      if (graph.getEdge(v, w) != null || (v == w && (!isSelfLoopCreationAllowed() || !isCycleCreationAllowed()))) {
        continue;
      }
      graph.createEdge(v, w);
      count--;
    }

    if (!isCycleCreationAllowed()) {
      for (IEdge edge : graph.getEdges()) {
        IPort sourcePort = edge.getSourcePort();
        IPort targetPort = edge.getTargetPort();
        if (index.getValue(sourcePort.getOwner()) > index.getValue(targetPort.getOwner())) {
          graph.setEdgePorts(edge, targetPort, sourcePort);
        }
      }
    }
  }

  /**
   * Returns the maximum number of edges of a graph that still obeys the set structural constraints.
   */
  private int getMaxEdges() {
    if (isParallelEdgeCreationAllowed()) {
      return Integer.MAX_VALUE;
    }
    int n = getNodeCount();
    int maxEdges = n * (n - 1) / 2;
    if (isCycleCreationAllowed() && isSelfLoopCreationAllowed()) {
      maxEdges += n;
    }
    return maxEdges;
  }
}
