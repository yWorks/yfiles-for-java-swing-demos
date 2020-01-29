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
package complete.bpmn.layout;

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.IEdgeMap;
import com.yworks.yfiles.algorithms.INodeMap;
import com.yworks.yfiles.algorithms.Maps;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.layout.hierarchic.AbstractPortConstraintOptimizer;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.IEdgeData;
import com.yworks.yfiles.layout.hierarchic.IItemFactory;
import com.yworks.yfiles.layout.hierarchic.ILayers;
import com.yworks.yfiles.layout.hierarchic.ILayoutDataProvider;
import com.yworks.yfiles.layout.hierarchic.INodeData;
import com.yworks.yfiles.layout.hierarchic.IPortConstraintOptimizer;
import com.yworks.yfiles.layout.hierarchic.SwimlaneDescriptor;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.PortConstraint;
import com.yworks.yfiles.layout.PortSide;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This port optimizer tries to balance the edges on each node and distribute them to the four node sides.
 * <p>
 * To balances the edge distribution it calculates edges that should be on a {@link HierarchicLayout#CRITICAL_EDGE_PRIORITY_DPKEY critical path}
 * and define the flow of the diagram. Furthermore it uses {@link IItemFactory#setTemporaryPortConstraint(Edge, boolean, PortConstraint) temporary port constraints}
 * on the non-flow sides of the nodes.
 * </p>
 */
class BalancingPortOptimizer extends AbstractPortConstraintOptimizer {
  // weak port constraints that are assigned as temporary port constraints
  private static final PortConstraint PORT_CONSTRAINT_EAST = PortConstraint.create(PortSide.EAST, false);

  private static final PortConstraint PORT_CONSTRAINT_WEST = PortConstraint.create(PortSide.WEST, false);

  // a core optimizer that is executed before this one.
  private final IPortConstraintOptimizer coreOptimizer;

  private AbstractPortConstraintOptimizer.SameLayerData sameLayerData;

  private IEdgeMap edge2LaneCrossing;

  private INodeMap node2LaneAlignment;

  public BalancingPortOptimizer( IPortConstraintOptimizer coreOptimizer ) {
    this.coreOptimizer = coreOptimizer;
  }

  @Override
  public void optimizeAfterLayering( LayoutGraph graph, ILayers layers, ILayoutDataProvider ldp, IItemFactory itemFactory ) {
    if (coreOptimizer != null) {
      coreOptimizer.optimizeAfterLayering(graph, layers, ldp, itemFactory);
    }
  }

  @Override
  public void optimizeAfterSequencing( LayoutGraph graph, ILayers layers, ILayoutDataProvider ldp, IItemFactory itemFactory ) {
    if (coreOptimizer != null) {
      coreOptimizer.optimizeAfterSequencing(graph, layers, ldp, itemFactory);
    }
    super.optimizeAfterSequencing(graph, layers, ldp, itemFactory);
  }

  @Override
  protected void optimizeAfterSequencing( Node node, Comparator<Object> inEdgeOrder, Comparator<Object> outEdgeOrder, LayoutGraph graph, ILayoutDataProvider ldp, IItemFactory itemFactory ) {
    // this callback isn't used
  }

  @Override
  protected AbstractPortConstraintOptimizer.SameLayerData insertSameLayerStructures( LayoutGraph graph, ILayers layers, ILayoutDataProvider ldp, IItemFactory itemFactory ) {
    // store the SameLayerData for later use
    sameLayerData = super.insertSameLayerStructures(graph, layers, ldp, itemFactory);
    return sameLayerData;
  }

  @Override
  protected void optimizeAfterSequencing( Comparator<Object> inEdgeOrder, Comparator<Object> outEdgeOrder, LayoutGraph graph, ILayers layers, ILayoutDataProvider ldp, IItemFactory itemFactory ) {
    edge2LaneCrossing = Maps.createHashedEdgeMap();
    node2LaneAlignment = Maps.createHashedNodeMap();

    IEdgeMap criticalEdges = Maps.createHashedEdgeMap();

    // determine whether an edge crosses a swim lane border and if so in which direction
    for (Edge edge : graph.getEdges()) {
      Edge originalEdge = getOriginalEdge(edge, ldp);

      // now we have a 'real' edge with valid valid source and target nodes
      int originalSourceId = getLaneId(originalEdge.source(), ldp);
      int originalTargetId = getLaneId(originalEdge.target(), ldp);
      LaneCrossing crossing = LaneCrossing.NONE;
      if (originalSourceId != originalTargetId) {
        // check if we need to flip the sides because edge and original edge have different directions
        boolean flipSides = edge.source() != originalEdge.source();
        int sourceId = flipSides ? originalTargetId : originalSourceId;
        int targetId = flipSides ? originalSourceId : originalTargetId;

        crossing = sourceId > targetId ? LaneCrossing.TO_WEST : LaneCrossing.TO_EAST;
      }
      edge2LaneCrossing.set(edge, crossing);
    }

    // determine basic node alignment
    for (Node n : graph.getNodes()) {
      LaneAlignment alignment = calculateLaneAlignment(n);
      node2LaneAlignment.set(n, alignment);
    }

    for (Node n : graph.getNodes()) {
      // sort the edges with the provided comparer
      n.sortInEdges(inEdgeOrder);
      n.sortOutEdges(outEdgeOrder);

      // calculate 'critical' in and out-edges whose nodes should be aligned in flow
      Edge bestInEdge = n.inDegree() > 0 ? getBestFlowEdge(n.getInEdges(), ldp, graph) : null;
      Edge bestOutEdge = n.outDegree() > 0 ? getBestFlowEdge(n.getOutEdges(), ldp, graph) : null;
      if (bestInEdge != null) {
        criticalEdges.setDouble(bestInEdge, criticalEdges.getDouble(bestInEdge) + 0.5);
      }
      if (bestOutEdge != null) {
        criticalEdges.setDouble(bestOutEdge, criticalEdges.getDouble(bestOutEdge) + 0.5);
      }
      if (n.degree() <= 4) {
        // should usually be the case and we can distribute each edge to its own side

        // remember which node side is already taken by an in- or out-edge
        boolean westTakenByInEdge = false;
        boolean eastTakenByInEdge = false;
        boolean westTakenByOutEdge = false;
        boolean eastTakenByOutEdge = false;

        if (n.inDegree() > 0 && n.outDegree() < 3) {
          // if there are at least three out-edges, we distribute those first, otherwise we start with the in-edges

          Edge firstInEdge = n.firstInEdge();
          Edge lastInEdge = n.lastInEdge();
          if (getLaneCrossing(firstInEdge) == LaneCrossing.TO_EAST && (n.inDegree() > 1 || isSameLayerEdge(firstInEdge, ldp))) {
            // the first in-edge comes from west and is either a same layer edge or there are other in-edges
            constrainWest(firstInEdge, false, itemFactory);
            westTakenByInEdge = true;
          }
          if (!westTakenByInEdge || n.outDegree() < 2) {
            // don't use west and east side for in-edges if there are at least 2 out-edges
            if (getLaneCrossing(lastInEdge) == LaneCrossing.TO_WEST && (n.inDegree() > 1 || isSameLayerEdge(lastInEdge, ldp))) {
              // the last in-edge comes from east and is either
              // a same-layer edge or there are other in-edges
              constrainEast(lastInEdge, false, itemFactory);
              eastTakenByInEdge = true;
            }
          }
        }

        if (n.outDegree() > 0) {
          Edge firstOutEdge = n.firstOutEdge();
          Edge lastOutEdge = n.lastOutEdge();

          if (!westTakenByInEdge) {
            // the west side is still free
            if (BpmnLayout.isBoundaryInterrupting(firstOutEdge, graph) || (getLaneCrossing(firstOutEdge) == LaneCrossing.TO_WEST) && (n.outDegree() > 1 || isSameLayerEdge(firstOutEdge, ldp))) {
              // the first out-edge is either boundary interrupting or goes to west and 
              // is either a same layer edge or there are other out-edges
              constrainWest(firstOutEdge, true, itemFactory);
              westTakenByOutEdge = true;
            } else if (eastTakenByInEdge && n.outDegree() >= 2 && !isSameLayerEdge(firstOutEdge.nextOutEdge(), ldp)) {
              // the east side is already taken but we have more then one out edge.
              // if the second out edge is a same layer edge, constraining the firstOutEdge could lead to
              // no in-flow edge
              constrainWest(firstOutEdge, true, itemFactory);
              westTakenByOutEdge = true;
            }
          }
          if (!eastTakenByInEdge) {
            // the east side is still free
            if (getLaneCrossing(lastOutEdge) == LaneCrossing.TO_EAST && (n.outDegree() > 1 || isSameLayerEdge(lastOutEdge, ldp))) {
              // the last out-edge goes to east and 
              // is either a same layer edge or there are other out-edges
              constrainEast(lastOutEdge, true, itemFactory);
              eastTakenByOutEdge = true;
            } else if (westTakenByInEdge && n.outDegree() >= 2 && !isSameLayerEdge(lastOutEdge.prevOutEdge(), ldp)) {
              // the west side is already taken but we have more then one out edge.
              // if the second last out edge is a same layer edge, constraining the lastOutEdge could lead to
              // no in-flow edge
              constrainEast(lastOutEdge, true, itemFactory);
              eastTakenByOutEdge = true;
            }
          }
        }

        // distribute remaining in-edges
        if (n.inDegree() == 2 && !(eastTakenByInEdge || westTakenByInEdge)) {
          // two in-edges but none distributed, yet
          if (bestInEdge == n.firstInEdge() && !eastTakenByOutEdge) {
            // first in-edge is in-flow edge and east side is still free
            constrainEast(n.lastInEdge(), false, itemFactory);
            eastTakenByInEdge = true;
          } else if (bestInEdge == n.lastInEdge() && !westTakenByOutEdge) {
            // last in-edge is in-flow edge and west side is still free
            constrainWest(n.firstInEdge(), false, itemFactory);
            westTakenByInEdge = true;
          }
        } else if (n.inDegree() == 3 && !(eastTakenByInEdge && westTakenByInEdge) && !isSameLayerEdge(n.firstInEdge().nextInEdge(), ldp)) {
          // three in-edges but not both sides taken, yet and the middle edge is no same layer edge
          if (!eastTakenByOutEdge) {
            // if not already taken, constraint the last in-edge to east
            constrainEast(n.lastInEdge(), false, itemFactory);
            eastTakenByInEdge = true;
          }
          if (!westTakenByOutEdge) {
            // if not already taken, constraint the first in-edge to west
            constrainWest(n.firstInEdge(), false, itemFactory);
            westTakenByInEdge = true;
          }
        }

        // distribute remaining out-edges
        if (n.outDegree() == 2 && !(eastTakenByOutEdge || westTakenByOutEdge)) {
          // two out-edges but none distributed, yet
          if (bestOutEdge == n.firstOutEdge() && !eastTakenByInEdge) {
            // first out-edge is in-flow edge and east side is still free
            constrainEast(n.lastOutEdge(), true, itemFactory);
            eastTakenByOutEdge = true;
          } else if (bestOutEdge == n.lastOutEdge() && !westTakenByInEdge) {
            // last out-edge is in-flow edge and west side is still free
            constrainWest(n.firstOutEdge(), true, itemFactory);
            westTakenByOutEdge = true;
          }
        } else if (n.outDegree() == 3 && !(eastTakenByOutEdge && westTakenByOutEdge) && !isSameLayerEdge(n.firstOutEdge().nextOutEdge(), ldp)) {
          // three out-edges but not both sides taken, yet and the middle edge is no same layer edge
          if (!eastTakenByInEdge) {
            // if not already taken, constraint the last out-edge to east
            constrainEast(n.lastOutEdge(), true, itemFactory);
            eastTakenByOutEdge = true;
          }
          if (!westTakenByInEdge) {
            // if not already taken, constraint the first out-edge to west
            constrainWest(n.firstOutEdge(), true, itemFactory);
            westTakenByOutEdge = true;
          }
        }
      }
    }

    // register the data provider for critical edge paths. It is deregistered again by BpmnLayout itself
    graph.addDataProvider(HierarchicLayout.CRITICAL_EDGE_PRIORITY_DPKEY, criticalEdges);

    sameLayerData = null;
    edge2LaneCrossing = null;
    node2LaneAlignment = null;
  }

  private LaneCrossing getLaneCrossing( Edge edge ) {
    return (LaneCrossing)edge2LaneCrossing.get(edge);
  }

  private LaneAlignment getLaneAlignment( Node source ) {
    return (LaneAlignment)node2LaneAlignment.get(source);
  }

  /**
   * Get the {@link Edge} representing the original edge on the graph.
   * <p>
   * As the core layout algorithm creates temporary edges for example for same-layer edges and edges spanning multiple
   * layers, we need to lookup the original edge of the graph for example as key in data providers.
   * </p>
   */
  private Edge getOriginalEdge( Edge edge, ILayoutDataProvider ldp ) {
    Edge originalEdge = sameLayerData.getOriginalEdge(edge.source());
    if (originalEdge == null) {
      originalEdge = sameLayerData.getOriginalEdge(edge.target());
    }
    if (originalEdge == null) {
      originalEdge = edge;
    }
    IEdgeData edgeData = ldp.getEdgeData(originalEdge);
    Edge associatedEdge = edgeData.getAssociatedEdge();
    return associatedEdge != null ? associatedEdge : originalEdge;
  }

  /**
   * Returns the best suited edge in {@code edges} for use as in-flow edge or {@code null} if no such edge could be found.
   */
  private Edge getBestFlowEdge( Iterable<Edge> edges, ILayoutDataProvider ldp, LayoutGraph graph ) {
    ArrayList<Edge> weakCandidates = new ArrayList<Edge>();
    ArrayList<Edge> candidates = new ArrayList<Edge>();

    for (Edge edge : edges) {
      Edge originalEdge = getOriginalEdge(edge, ldp);
      if ((LaneCrossing)edge2LaneCrossing.get(edge) != LaneCrossing.NONE || BpmnLayout.isBoundaryInterrupting(originalEdge, graph) || isSameLayerEdge(originalEdge, ldp) || edge.isSelfLoop()) {
        // an edge should not be aligned if:
        // - it crosses stripe borders
        // - it is boundary interrupting
        // - it is a same-layer edge
        // - it is a self-loop
        continue;
      }
      if (ldp.getEdgeData(edge).isReversed() || !BpmnLayout.isSequenceFlow(originalEdge, graph)) {
        // it is only a weak candidate if:
        // - it is reversed
        // - it is no sequence flow
        weakCandidates.add(edge);
      } else {
        candidates.add(edge);
      }
    }
    if (candidates.size() > 0) {
      // if there are several candidates, choose the one that would keep the LaneAlignment 
      // of its source and target node consistent
      Collections.sort(candidates, new Comparator<Edge>(){
        public int compare( Edge edge1, Edge edge2 ) {
          int ac1 = getAlignmentConsistency(edge1);
          int ac2 = getAlignmentConsistency(edge2);
          return ac2 - ac1;
        }
      });
      return candidates.get(0);
    }
    if (weakCandidates.size() > 0) {
      return weakCandidates.get((int)Math.floor(weakCandidates.size() / 2.0));
    }
    return null;
  }

  /**
   * Returns how much the {@link LaneAlignment} of the source and target node is consistent.
   * <p>
   * The consistency is
   * </p>
   * <ul>
   * <li><code>2</code>, if both nodes have the same alignment. It is </li>
   * <li><code>1</code> if exactly one of the alignments is {@link LaneAlignment#NONE} and</li>
   * <li><code>0</code> otherwise.</li>
   * </ul>
   */
  private int getAlignmentConsistency( Edge edge ) {
    LaneAlignment sourceLA = getLaneAlignment(edge.source());
    LaneAlignment targetLA = getLaneAlignment(edge.target());
    int alignmentConsistency = sourceLA == targetLA ? 2 : (sourceLA == LaneAlignment.NONE || targetLA == LaneAlignment.NONE) ? 1 : 0;
    return alignmentConsistency;
  }

  /**
   * Sets a {@link IItemFactory#setTemporaryPortConstraint(Edge, boolean, PortConstraint) temporary east port constraint} on
   * {@code source} or target side of .{@code edge}
   */
  private static void constrainEast( Edge edge, boolean source, IItemFactory itemFactory ) {
    itemFactory.setTemporaryPortConstraint(edge, source, PORT_CONSTRAINT_EAST);
  }

  /**
   * Sets a {@link IItemFactory#setTemporaryPortConstraint(Edge, boolean, PortConstraint) temporary west port constraint} on
   * {@code source} or target side of .{@code edge}
   */
  private static void constrainWest( Edge edge, boolean source, IItemFactory itemFactory ) {
    itemFactory.setTemporaryPortConstraint(edge, source, PORT_CONSTRAINT_WEST);
  }

  /**
   * Returns if the source and target node of the {@link #getOriginalEdge(Edge, ILayoutDataProvider) original edge} of {@code edge}
   * are on the same layer.
   */
  private boolean isSameLayerEdge( Edge edge, ILayoutDataProvider ldp ) {
    Edge originalEdge = getOriginalEdge(edge, ldp);
    INodeData sourceNodeData = ldp.getNodeData(originalEdge.source());
    INodeData targetNodeData = ldp.getNodeData(originalEdge.target());
    return sourceNodeData != null && targetNodeData != null && (sourceNodeData.getLayer() == targetNodeData.getLayer());
  }

  /**
   * Determine the alignment of a node in its swim lane depending on the {@link LaneCrossing}s of its attached edges.
   */
  private LaneAlignment calculateLaneAlignment( Node n ) {
    int toRightCount = 0;
    int toLeftCount = 0;
    for (Edge edge : n.getEdges()) {
      LaneCrossing crossing = (LaneCrossing)edge2LaneCrossing.get(edge);
      if (n == edge.source()) {
        if (crossing == LaneCrossing.TO_EAST) {
          toRightCount++;
        } else if (crossing == LaneCrossing.TO_WEST) {
          toLeftCount++;
        }
      } else {
        if (crossing == LaneCrossing.TO_EAST) {
          toLeftCount++;
        } else if (crossing == LaneCrossing.TO_WEST) {
          toRightCount++;
        }
      }
    }
    if (toLeftCount > toRightCount) {
      return LaneAlignment.LEFT;
    } else if (toLeftCount < toRightCount) {
      return LaneAlignment.RIGHT;
    } else {
      return LaneAlignment.NONE;
    }
  }

  /**
   * Returns the {@link SwimlaneDescriptor#getComputedLaneIndex() ComputedLaneIndex} for {@code node}.
   */
  private static int getLaneId( Node node, ILayoutDataProvider ldp ) {
    INodeData nodeData = ldp.getNodeData(node);
    SwimlaneDescriptor laneDesc = nodeData != null ? nodeData.getSwimLaneDescriptor() : null;
    return laneDesc != null ? laneDesc.getComputedLaneIndex() : -1;
  }

  /**
   * Specifies the alignment of a node in its swimlane.
   * @see #calculateLaneAlignment(Node)
   */
  private enum LaneAlignment {
    /**
     * The node has no special alignment.
     */
    NONE(0),

    /**
     * The node is aligned to the left side.
     */
    LEFT(1),

    /**
     * The node is aligned to the right side.
     */
    RIGHT(2);

    private final int value;

    private LaneAlignment( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final LaneAlignment fromOrdinal( int ordinal ) {
      for (LaneAlignment current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  /**
   * Specifies in which direction an edge crosses swim lane borders.
   */
  private enum LaneCrossing {
    /**
     * The edge doesn't cross a swimlane border.
     */
    NONE(0),

    /**
     * The edge crosses swimlane borders to the east, so its source node is in a swim lane with a lower
     * {@link SwimlaneDescriptor#getComputedLaneIndex() ComputedLaneIndex}.
     */
    TO_EAST(1),

    /**
     * The edge crosses swimlane borders to the west, so its source node is in a swim lane with a higher
     * {@link SwimlaneDescriptor#getComputedLaneIndex() ComputedLaneIndex}.
     */
    TO_WEST(2);

    private final int value;

    private LaneCrossing( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final LaneCrossing fromOrdinal( int ordinal ) {
      for (LaneCrossing current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

}
