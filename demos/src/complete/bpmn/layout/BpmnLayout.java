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
package complete.bpmn.layout;

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.EdgeDpKey;
import com.yworks.yfiles.algorithms.Graph;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.ILabelLayoutDpKey;
import com.yworks.yfiles.layout.ColumnDescriptor;
import com.yworks.yfiles.layout.hierarchic.AsIsLayerer;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.layout.hierarchic.PortCandidateOptimizer;
import com.yworks.yfiles.layout.hierarchic.SimplexNodePlacer;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.PartitionGrid;
import com.yworks.yfiles.layout.RowDescriptor;

/**
 * An automatic layout algorithm for BPMN diagrams.
 * <p>
 * Some elements have to be marked with the DataProvider keys {@link #SEQUENCE_FLOW_EDGES_DPKEY} and
 * {@link #BOUNDARY_INTERRUPTING_EDGES_DPKEY}.
 * </p>
 */
public class BpmnLayout implements ILayoutAlgorithm {
  /**
   * {@link IDataProvider} key used to store if an edge represents a sequence flow, default flow or conditional flow.
   */
  public static final EdgeDpKey<Boolean> SEQUENCE_FLOW_EDGES_DPKEY = new EdgeDpKey<Boolean>(Boolean.class, BpmnLayout.class, "com.yworks.yfiles.bpmn.layout.BpmnLayout.SequenceFlowEdgesDpKey");

  /**
   * {@link IDataProvider} key used to store if an edge starts at a boundary interrupting event.
   */
  public static final EdgeDpKey<Boolean> BOUNDARY_INTERRUPTING_EDGES_DPKEY = new EdgeDpKey<Boolean>(Boolean.class, BpmnLayout.class, "com.yworks.yfiles.bpmn.layout.BpmnLayout.BoundaryInterruptingEdgesDpKey");

  /**
   * {@link IDataProvider} key used to store which labels shall be positioned by the labeling algorithm.
   */
  public static final ILabelLayoutDpKey<Boolean> AFFECTED_LABELS_DPKEY = new ILabelLayoutDpKey<Boolean>(Boolean.class, BpmnLayout.class, "com.yworks.yfiles.bpmn.layout.BpmnLayout.AffectedLabelsDpKey");

  /**
   * Creates a new instance of this class.
   */
  public BpmnLayout() {
    setScope(Scope.ALL_ELEMENTS);
    setLaneInsets(10);
    setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
    setMinimumNodeDistance(40);
  }

  private Scope scope = Scope.ALL_ELEMENTS;

  /**
   * The Scope that is laid out.
   * <p>
   * Possible values are {@link Scope#ALL_ELEMENTS} and {@link Scope#SELECTED_ELEMENTS}.
   * </p>
   * <p>
   * Defaults to {@link Scope#ALL_ELEMENTS}.
   * </p>
   * <p>
   * Note, if the scope is set to
   * {@link Scope#SELECTED_ELEMENTS}, non-selected elements may also be moved. However the layout algorithm uses the
   * initial position of such elements as sketch.
   * </p>
   * @return The Scope.
   * @see #setScope(Scope)
   */
  public final Scope getScope() {
    return this.scope;
  }

  /**
   * The Scope that is laid out.
   * <p>
   * Possible values are {@link Scope#ALL_ELEMENTS} and {@link Scope#SELECTED_ELEMENTS}.
   * </p>
   * <p>
   * Defaults to {@link Scope#ALL_ELEMENTS}.
   * </p>
   * <p>
   * Note, if the scope is set to
   * {@link Scope#SELECTED_ELEMENTS}, non-selected elements may also be moved. However the layout algorithm uses the
   * initial position of such elements as sketch.
   * </p>
   * @param value The Scope to set.
   * @see #getScope()
   */
  public final void setScope( Scope value ) {
    this.scope = value;
  }

  private double laneInsets;

  /**
   * The insets used for swim lanes.
   * <p>
   * The insets for swim lanes, that is the distance between a graph element and the border of its enclosing swim lane.
   * </p>
   * <p>
   * Defaults to {@code 10.0}.
   * </p>
   * @return The LaneInsets.
   * @see #setLaneInsets(double)
   */
  public final double getLaneInsets() {
    return this.laneInsets;
  }

  /**
   * The insets used for swim lanes.
   * <p>
   * The insets for swim lanes, that is the distance between a graph element and the border of its enclosing swim lane.
   * </p>
   * <p>
   * Defaults to {@code 10.0}.
   * </p>
   * @param value The LaneInsets to set.
   * @see #getLaneInsets()
   */
  public final void setLaneInsets( double value ) {
    this.laneInsets = value;
  }

  private double minimumNodeDistance;

  /**
   * The minimum distance between two node elements.
   * <p>
   * Defaults to {@code 40.0}
   * </p>
   * @return The MinimumNodeDistance.
   * @see #setMinimumNodeDistance(double)
   */
  public final double getMinimumNodeDistance() {
    return this.minimumNodeDistance;
  }

  /**
   * The minimum distance between two node elements.
   * <p>
   * Defaults to {@code 40.0}
   * </p>
   * @param value The MinimumNodeDistance to set.
   * @see #getMinimumNodeDistance()
   */
  public final void setMinimumNodeDistance( double value ) {
    this.minimumNodeDistance = value;
  }

  private LayoutOrientation layoutOrientation = LayoutOrientation.TOP_TO_BOTTOM;

  /**
   * The layout orientation.
   * <p>
   * Defaults to {@link LayoutOrientation#LEFT_TO_RIGHT}.
   * </p>
   * @return The LayoutOrientation.
   * @see #setLayoutOrientation(LayoutOrientation)
   */
  public final LayoutOrientation getLayoutOrientation() {
    return this.layoutOrientation;
  }

  /**
   * The layout orientation.
   * <p>
   * Defaults to {@link LayoutOrientation#LEFT_TO_RIGHT}.
   * </p>
   * @param value The LayoutOrientation to set.
   * @see #getLayoutOrientation()
   */
  public final void setLayoutOrientation( LayoutOrientation value ) {
    this.layoutOrientation = value;
  }

  /**
   * Lays out the specified graph.
   */
  public void applyLayout( LayoutGraph graph ) {
    if (graph.isEmpty()) {
      return;
    }
    // set the laneInsets to all partition grid columns and rows
    configurePartitionGrid(graph);

    // run core layout
    applyHierarchicLayout(graph);

    // apply generic labeling
    applyLabeling(graph);

    // adjust endpoints of edges
    new PortLocationAdjuster().applyLayout(graph);

    //remove data provider for CriticalEdgePriorityDpKey that was added by BalancingPortOptimizer
    graph.removeDataProvider(HierarchicLayout.CRITICAL_EDGE_PRIORITY_DPKEY);
  }

  private void configurePartitionGrid( LayoutGraph graph ) {
    PartitionGrid grid = PartitionGrid.getPartitionGrid(graph);
    if (grid != null) {
      for (ColumnDescriptor column : grid.getColumns()) {
        column.setLeftInset(column.getLeftInset() + getLaneInsets());
        column.setRightInset(column.getRightInset() + getLaneInsets());
      }
      for (RowDescriptor row : grid.getRows()) {
        row.setTopInset(row.getTopInset() + getLaneInsets());
        row.setBottomInset(row.getBottomInset() + getLaneInsets());
      }
    }
  }

  private void applyHierarchicLayout( LayoutGraph graph ) {
    HierarchicLayout hl = new HierarchicLayout();
    hl.setOrthogonalRoutingEnabled(true);
    hl.setRecursiveGroupLayeringEnabled(false);
    hl.setComponentLayoutEnabled(false);
    hl.setFromScratchLayerer(new BackLoopLayerer());
    hl.setMinimumLayerDistance(getMinimumNodeDistance());
    hl.setNodeToNodeDistance(getMinimumNodeDistance());
    ((SimplexNodePlacer)hl.getNodePlacer()).setBarycenterModeEnabled(true);
    ((SimplexNodePlacer)hl.getNodePlacer()).setEdgeStraighteningEnabled(true);
    hl.setLayoutOrientation(getLayoutOrientation() == LayoutOrientation.LEFT_TO_RIGHT ? com.yworks.yfiles.layout.LayoutOrientation.LEFT_TO_RIGHT : com.yworks.yfiles.layout.LayoutOrientation.TOP_TO_BOTTOM);
    hl.getHierarchicLayoutCore().setPortConstraintOptimizer(new BalancingPortOptimizer(new PortCandidateOptimizer()));
    if (getScope() == Scope.SELECTED_ELEMENTS) {
      AsIsLayerer asIsLayerer = new AsIsLayerer();
      asIsLayerer.setMaximumNodeSize(5);
      hl.setFixedElementsLayerer(asIsLayerer);
      hl.setLayoutMode(LayoutMode.INCREMENTAL);
    }
    hl.applyLayout(graph);
  }

  private void applyLabeling( LayoutGraph graph ) {
    GenericLabeling labeling = new GenericLabeling();
    labeling.setMaximumDuration(0);
    labeling.setAmbiguityReductionEnabled(true);
    labeling.setNodeLabelPlacementEnabled(true);
    labeling.setEdgeLabelPlacementEnabled(true);
    labeling.setAffectedLabelsDpKey(AFFECTED_LABELS_DPKEY);
    labeling.setProfitModel(new BpmnLabelProfitModel(graph));
    labeling.setCustomProfitModelRatio(0.15);
    labeling.applyLayout(graph);
  }

  /**
   * Returns if the edge represents a sequence flow, default flow or conditional flow.
   * @see #SEQUENCE_FLOW_EDGES_DPKEY
   */
  public static final boolean isSequenceFlow( Edge edge, Graph graph ) {
    IDataProvider flowDP = graph.getDataProvider(SEQUENCE_FLOW_EDGES_DPKEY);
    return flowDP != null && flowDP.getBool(edge);
  }

  /**
   * Returns if the edge is attached to a boundary interrupting event.
   * @see #BOUNDARY_INTERRUPTING_EDGES_DPKEY
   */
  public static final boolean isBoundaryInterrupting( Edge edge, LayoutGraph graph ) {
    IDataProvider isInterruptingDP = graph.getDataProvider(BOUNDARY_INTERRUPTING_EDGES_DPKEY);
    return isInterruptingDP != null && isInterruptingDP.getBool(edge);
  }

}
