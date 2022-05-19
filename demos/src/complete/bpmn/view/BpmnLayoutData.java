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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.*;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.layout.GenericLayoutData;
import com.yworks.yfiles.layout.hierarchic.*;
import com.yworks.yfiles.layout.ItemCollection;
import com.yworks.yfiles.layout.ItemMapping;
import com.yworks.yfiles.layout.LabelPlacements;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.LayoutKeys;
import com.yworks.yfiles.layout.NodeHalo;
import com.yworks.yfiles.layout.PortConstraint;
import com.yworks.yfiles.layout.PortConstraintKeys;
import com.yworks.yfiles.layout.PortSide;
import com.yworks.yfiles.layout.PreferredPlacementDescriptor;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.ISelectionModel;
import complete.bpmn.layout.BpmnLayout;
import complete.bpmn.layout.PortLocationAdjuster;
import complete.bpmn.layout.Scope;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Specifies custom data for the {@link BpmnLayout}.
 * <p>
 * Prepares BPMN layout information provided by the styles for assignment of layout information calculated by
 * {@link BpmnLayout}.
 * </p>
 */
public class BpmnLayoutData {
  private static final double MIN_LABEL_TO_LABEL_DISTANCE = 5;

  private boolean startNodesFirst;

  /**
   * Determines whether or not start node are pulled to the leftmost or topmost layer.
   * <p>
   * Default value is {@code false}.
   * </p>
   * @return The StartNodesFirst.
   * @see #setStartNodesFirst(boolean)
   */
  public boolean isStartNodesFirst() {
    return this.startNodesFirst;
  }

  /**
   * Determines whether or not start node are pulled to the leftmost or topmost layer.
   * <p>
   * Default value is {@code false}.
   * </p>
   * @param value The StartNodesFirst to set.
   * @see #isStartNodesFirst()
   */
  public void setStartNodesFirst( boolean value ) {
    this.startNodesFirst = value;
  }

  private boolean compactMessageFlowLayering;

  /**
   * Determines whether or not message flows have only weak impact on the layering.
   * <p>
   * Having weak impact, message flows are more likely to be back edges. This often results in more compact layouts.
   * </p>
   * <p>
   * Default value is {@code false}.
   * </p>
   * @return The CompactMessageFlowLayering.
   * @see #setCompactMessageFlowLayering(boolean)
   */
  public boolean isCompactMessageFlowLayering() {
    return this.compactMessageFlowLayering;
  }

  /**
   * Determines whether or not message flows have only weak impact on the layering.
   * <p>
   * Having weak impact, message flows are more likely to be back edges. This often results in more compact layouts.
   * </p>
   * <p>
   * Default value is {@code false}.
   * </p>
   * @param value The CompactMessageFlowLayering to set.
   * @see #isCompactMessageFlowLayering()
   */
  public void setCompactMessageFlowLayering( boolean value ) {
    this.compactMessageFlowLayering = value;
  }

  private double minimumEdgeLength;

  /**
   * The minimum length of edges.
   * <p>
   * Defaults to {@code 20.0}.
   * </p>
   * @return The MinimumEdgeLength.
   * @see #setMinimumEdgeLength(double)
   */
  public final double getMinimumEdgeLength() {
    return this.minimumEdgeLength;
  }

  /**
   * The minimum length of edges.
   * <p>
   * Defaults to {@code 20.0}.
   * </p>
   * @param value The MinimumEdgeLength to set.
   * @see #getMinimumEdgeLength()
   */
  public final void setMinimumEdgeLength( double value ) {
    this.minimumEdgeLength = value;
  }

  public BpmnLayoutData() {
    setMinimumEdgeLength(20);
  }

  public final LayoutData create(IGraph graph, ISelectionModel<IModelItem> selection, Scope layoutScope ) {
    GenericLayoutData data = new GenericLayoutData();
    HierarchicLayoutData hierarchicLayoutData = new HierarchicLayoutData();

    // check if only selected elements should be laid out
    boolean layoutOnlySelection = layoutScope == Scope.SELECTED_ELEMENTS;

    // mark 'flow' edges, i.e. sequence flows, default flows and conditional flows
    data.addItemCollection(BpmnLayout.SEQUENCE_FLOW_EDGES_DPKEY).setPredicate(BpmnLayoutData::isSequenceFlow);

    // mark boundary interrupting edges for the BalancingPortOptimizer
    data.addItemCollection(BpmnLayout.BOUNDARY_INTERRUPTING_EDGES_DPKEY).setPredicate(edge -> edge.getSourcePort().getStyle() instanceof EventPortStyle);



    // mark conversations, events and gateways so their port locations are adjusted
    data.addItemCollection(PortLocationAdjuster.AFFECTED_NODES_DPKEY).setPredicate(node ->
        node.getStyle() instanceof ConversationNodeStyle || node.getStyle() instanceof EventNodeStyle || node.getStyle() instanceof GatewayNodeStyle);

    // add NodeHalos around nodes with event ports or specific exterior labels so the layout keeps space for the event ports and labels as well
    addNodeHalos(data, graph, selection, layoutOnlySelection);

    // add PreferredPlacementDescriptors for labels on sequence, default or conditional flows to place them at source side
    addEdgeLabelPlacementDescriptors(data);

    // mark nodes, edges and labels as either fixed or affected by the layout and configure port constraints and incremental hints
    markFixedAndAffectedItems(data, hierarchicLayoutData, selection, layoutOnlySelection);

    // mark associations and message flows as undirected so they have less impact on layering
    hierarchicLayoutData.getEdgeDirectedness().setFunction(edge -> ((isMessageFlow(edge) || isAssociation(edge)) ? 0.0 : 1));

    // add layer constraints for start events, sub processes and message flows
    addLayerConstraints(graph, hierarchicLayoutData);

    // add EdgeLayoutDescriptor to specify minimum edge length for edges
    addMinimumEdgeLength(getMinimumEdgeLength(), hierarchicLayoutData);


    return data.combineWith(hierarchicLayoutData);
  }


  private void addLayerConstraints( IGraph graph, HierarchicLayoutData hierarchicLayoutData ) {
    // use layer constraints via HierarchicLayoutData
    LayerConstraintData layerConstraintData = hierarchicLayoutData.getLayerConstraints();

    for (IEdge edge : graph.getEdges()) {
      if (isMessageFlow(edge) && !isCompactMessageFlowLayering()) {
        // message flow layering compaction is disabled, we add a 'weak' same layer constraint, i.e. source node shall be placed at
        // least 0 layers above target node
        layerConstraintData.placeAbove(edge.getTargetNode(), edge.getSourceNode(), 0, 1);
      } else if (isSequenceFlow(edge)) {
        if ((isSubprocess(edge.getSourceNode()) && !(edge.getSourcePort().getStyle() instanceof EventPortStyle)) || isSubprocess(edge.getTargetNode())) {
          // For edges to or from a subprocess that are not attached to an (interrupting) event port, the flow should be considered.
          // If the subprocess is a group node, any constraints to it are ignored so we have to add the constraints to the content nodes
          // of the subprocess
          addAboveLayerConstraint(layerConstraintData, edge, graph);
        }
      }
    }

    // if start events should be pulled to the first layer, add PlaceNodeAtTop constraint.
    if (isStartNodesFirst()) {
      for (INode node : graph.getNodes()) {
        if (node.getStyle() instanceof EventNodeStyle && ((EventNodeStyle)node.getStyle()).getCharacteristic() == EventCharacteristic.START && graph.inDegree(node) == 0 && (graph.getParent(node) == null || graph.getParent(node).getStyle() instanceof PoolNodeStyle)) {
          layerConstraintData.placeAtTop(node);
        }
      }
    }
  }

  private static void addAboveLayerConstraint( LayerConstraintData layerConstraintData, IEdge edge, IGraph graph ) {
    INode sourceNode = edge.getSourceNode();
    INode targetNode = edge.getTargetNode();

    ArrayList<INode> sourceNodes = new ArrayList<INode>();
    ArrayList<INode> targetNodes = new ArrayList<INode>();
    collectLeafNodes(graph, sourceNode, sourceNodes);
    collectLeafNodes(graph, targetNode, targetNodes);
    for (INode source : sourceNodes) {
      for (INode target : targetNodes) {
        layerConstraintData.placeAbove(target, source, 1, 0);
      }
    }
  }

  private static void collectLeafNodes( IGraph graph, INode node, Collection<INode> leafNodes ) {
    IListEnumerable<INode> children = graph.getChildren(node);
    if (children.size() > 0) {
      for (INode child : children) {
        collectLeafNodes(graph, child, leafNodes);
      }
    } else {
      leafNodes.add(node);
    }
  }



  private void addMinimumEdgeLength( final double minimumEdgeLength, HierarchicLayoutData hierarchicLayoutData ) {
    // each edge should have a minimum length so that all its labels can be placed on it one
    // after another with a minimum label-to-label distance
     hierarchicLayoutData.setEdgeLayoutDescriptors(edge -> {
      EdgeLayoutDescriptor descriptor = new EdgeLayoutDescriptor();
      descriptor.setRoutingStyle(new RoutingStyle(EdgeRoutingStyle.ORTHOGONAL, false));
      double minLength = 0;
      for (ILabel label : edge.getLabels()) {
        RectD labelSize = label.getLayout().getBounds();
        minLength += Math.max(labelSize.width, labelSize.height);
      }
      if (edge.getLabels().size() > 1) {
        minLength += (edge.getLabels().size() - 1) * MIN_LABEL_TO_LABEL_DISTANCE;
      }
      descriptor.setMinimumLength(Math.max(minLength, minimumEdgeLength));
      return descriptor;
    });
  }



  private static boolean isSubprocess( INode node ) {
    INodeStyle nodeStyle = node.getStyle();
    return nodeStyle instanceof ActivityNodeStyle && isProcess(((ActivityNodeStyle) nodeStyle).getActivityType());
  }

  private static boolean isProcess( ActivityType activityType ) {
    return activityType == ActivityType.SUB_PROCESS || activityType == ActivityType.EVENT_SUB_PROCESS;
  }

  private static boolean isMessageFlow( IEdge edge ) {
    IEdgeStyle edgeStyle = edge.getStyle();
    return edgeStyle instanceof BpmnEdgeStyle && ((BpmnEdgeStyle) edgeStyle).getType() == EdgeType.MESSAGE_FLOW;
  }

  private static boolean isSequenceFlow( IEdge edge ) {
    IEdgeStyle edgeStyle = edge.getStyle();
    return edgeStyle instanceof BpmnEdgeStyle && isFlow(((BpmnEdgeStyle) edgeStyle).getType()); 
  }

  private static boolean isFlow( EdgeType edgeType ) {
    return edgeType == EdgeType.SEQUENCE_FLOW || edgeType == EdgeType.DEFAULT_FLOW || edgeType == EdgeType.CONDITIONAL_FLOW;
  }

  private static boolean isAssociation( IEdge edge ) {
    IEdgeStyle edgeStyle = edge.getStyle();
    return edgeStyle instanceof BpmnEdgeStyle && isAssociation(((BpmnEdgeStyle) edgeStyle).getType());
  }

  private static boolean isAssociation( EdgeType edgeType ) {
    return edgeType == EdgeType.ASSOCIATION || edgeType == EdgeType.BIDIRECTED_ASSOCIATION || edgeType == EdgeType.DIRECTED_ASSOCIATION;
  }



  private static void addNodeHalos( GenericLayoutData data, IGraph graph, ISelectionModel<IModelItem> selection, boolean layoutOnlySelection ) {
    Mapper<INode, NodeHalo> nodeHalos = new Mapper<INode, NodeHalo>();
    for (INode node : graph.getNodes()) {
      double top = 0.0;
      double left = 0.0;
      double bottom = 0.0;
      double right = 0.0;

      // for each port with an EventPortStyle extend the node halo to cover the ports render size
      for (IPort port : node.getPorts()) {
        IPortStyle portStyle = port.getStyle();
        if (portStyle instanceof EventPortStyle) {
          EventPortStyle eventPortStyle = (EventPortStyle)portStyle;
          SizeD renderSize = eventPortStyle.getRenderSize();
          PointD location = port.getLocation();
          top = Math.max(top, node.getLayout().getY() - location.y - renderSize.height / 2);
          left = Math.max(left, node.getLayout().getX() - location.x - renderSize.width / 2);
          bottom = Math.max(bottom, location.y + renderSize.height / 2 - node.getLayout().getMaxY());
          right = Math.max(right, location.x + renderSize.width / 2 - node.getLayout().getMaxX());
        }
      }

      // for each node without incoming or outgoing edges reserve space for laid out exterior labels
      if (graph.inDegree(node) == 0 || graph.outDegree(node) == 0) {
        for (ILabel label : node.getLabels()) {
          if (isNodeLabelAffected(graph, selection, label, layoutOnlySelection)) {
            RectD labelBounds = label.getLayout().getBounds();
            if (graph.inDegree(node) == 0) {
              left = Math.max(left, labelBounds.width);
              top = Math.max(top, labelBounds.height);
            }
            if (graph.outDegree(node) == 0) {
              right = Math.max(right, labelBounds.width);
              bottom = Math.max(bottom, labelBounds.height);
            }
          }
        }
      }

      nodeHalos.setValue(node, NodeHalo.create(top, left, bottom, right));
    }
    data.addItemMapping(NodeHalo.class, NodeHalo.NODE_HALO_DPKEY).setMapper(nodeHalos);
  }

  private static boolean isNodeLabelAffected( IGraph graph, ISelectionModel<IModelItem> selection, ILabel label, boolean layoutOnlySelection ) {
    ILabelOwner owner = label.getOwner();
    if (owner instanceof INode) {
      INode node = (INode) owner;
      boolean isInnerLabel = node.getLayout().contains(label.getLayout().getCenter());
      boolean isPool = node.getStyle() instanceof PoolNodeStyle;
      boolean isChoreography = node.getStyle() instanceof ChoreographyNodeStyle;
      boolean isGroupNode = graph.isGroupNode(node);
      return !isInnerLabel && !isPool && !isChoreography && !isGroupNode && (!layoutOnlySelection || selection.isSelected(node));
    }
    return false;
  }



  private static void addEdgeLabelPlacementDescriptors( GenericLayoutData data ) {
    final PreferredPlacementDescriptor atSourceDescriptor = new PreferredPlacementDescriptor();
    atSourceDescriptor.setPlaceAlongEdge(LabelPlacements.AT_SOURCE_PORT);
    atSourceDescriptor.setSideOfEdge(LabelPlacements.LEFT_OF_EDGE.or(LabelPlacements.RIGHT_OF_EDGE));
    data.addItemMapping(PreferredPlacementDescriptor.class, LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY).setFunction(label -> {
      EdgeType edgeType = ((BpmnEdgeStyle)((IEdge)label.getOwner()).getStyle()).getType();
      if (isFlow(edgeType)) {
        // labels on sequence, default and conditional flow edges should be placed at the source side.
        return atSourceDescriptor;
      }
      return null;
    });
  }



  private static void markFixedAndAffectedItems( GenericLayoutData data, HierarchicLayoutData hierarchicLayoutData, final ISelectionModel<IModelItem> graphSelection, boolean layoutOnlySelection ) {
    if (layoutOnlySelection) {
      final IMapper<IEdge, Boolean> affectedEdges = IMapper.fromFunction(
              edge -> graphSelection.isSelected(edge) || graphSelection.isSelected(edge.getSourceNode()) || graphSelection.isSelected(edge.getTargetNode()));
      data.addItemCollection(LayoutKeys.AFFECTED_EDGES_DPKEY).setMapper(affectedEdges);

      // fix ports of unselected edges and edges at event ports
      data.addItemMapping(PortConstraint.class, PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY).setFunction(
              edge -> (!affectedEdges.getValue(edge) || edge.getSourcePort().getStyle() instanceof EventPortStyle) ? PortConstraint.create(getSide(edge, true), false) : null);
      data.addItemMapping(PortConstraint.class, PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY).setFunction(
              edge -> !affectedEdges.getValue(edge) ? PortConstraint.create(getSide(edge, false), false) : null);

      // give core layout hints that selected nodes and edges should be incremental
      hierarchicLayoutData.getIncrementalHints().setContextBiFunction((item, factory) -> {
        if (item instanceof INode && graphSelection.isSelected(item)) {
          return factory.createLayerIncrementallyHint(item);
        } else if (item instanceof IEdge && affectedEdges.getValue((IEdge)item)) {
          return factory.createSequenceIncrementallyHint(item);
        }
        return null;
      });
      data.addItemCollection(BpmnLayout.AFFECTED_LABELS_DPKEY).setPredicate(label -> {
        ILabelOwner owner = label.getOwner();
        if (owner instanceof IEdge) {
          return affectedEdges.getValue((IEdge) owner);
        }
        if (owner instanceof INode) {
          INode node = (INode) owner;
          boolean isInnerLabel = node.getLayout().contains(label.getLayout().getCenter());
          boolean isPool = node.getStyle() instanceof PoolNodeStyle;
          boolean isChoreography = node.getStyle() instanceof ChoreographyNodeStyle;
          return !isInnerLabel && !isPool && !isChoreography && graphSelection.isSelected(node);
        }
        return false;
      });
    } else {
      // fix source port of edges at event ports
      data.addItemMapping(PortConstraint.class, PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY).setFunction(
              edge -> edge.getSourcePort().getStyle() instanceof EventPortStyle ? PortConstraint.create(getSide(edge, true), false) : null);

      data.addItemCollection(BpmnLayout.AFFECTED_LABELS_DPKEY).setPredicate(label -> {
        ILabelOwner owner = label.getOwner();
        if (owner instanceof IEdge) {
          return true;
        }
        if (owner instanceof INode) {
          INode node = (INode) owner;
          boolean isInnerLabel = node.getLayout().contains(label.getLayout().getCenter());
          boolean isPool = node.getStyle() instanceof PoolNodeStyle;
          boolean isChoreography = node.getStyle() instanceof ChoreographyNodeStyle;
          return !isInnerLabel && !isPool && !isChoreography;
        }
        return false;
      });
    }
  }

  private static PortSide getSide( IEdge edge, boolean atSource ) {
    IPort port = atSource ? edge.getSourcePort() : edge.getTargetPort();
    IPortOwner owner = port.getOwner();
    INode node = owner instanceof INode ? (INode)owner : null;
    if (node == null) {
      return PortSide.ANY;
    }
    PointD relPortLocation = PointD.subtract(port.getLocation(), node.getLayout().getCenter());

    // calculate relative port position scaled by the node size
    double sdx = relPortLocation.x / (node.getLayout().getWidth() / 2);
    double sdy = relPortLocation.y / (node.getLayout().getHeight() / 2);

    if (Math.abs(sdx) > Math.abs(sdy)) {
      // east or west
      return sdx < 0 ? PortSide.WEST : PortSide.EAST;
    } else if (Math.abs(sdx) < Math.abs(sdy)) {
      return sdy < 0 ? PortSide.NORTH : PortSide.SOUTH;
    }

    // port is somewhere at the diagonals of the node bounds
    // so we can't decide the port side based on the port location
    // better use the attached segment to decide on the port side
    return getSideFromSegment(edge, atSource);
  }

  private static PortSide getSideFromSegment( IEdge edge, boolean atSource ) {
    IPort port = atSource ? edge.getSourcePort() : edge.getTargetPort();
    IPort opposite = atSource ? edge.getTargetPort() : edge.getSourcePort();
    PointD from = port.getLocation();

    IPoint to = edge.getBends().size() > 0 ? (atSource ? edge.getBends().getItem(0) : edge.getBends().last()).getLocation() : opposite.getLocation();

    double dx = to.getX() - from.x;
    double dy = to.getY() - from.y;
    if (Math.abs(dx) > Math.abs(dy)) {
      // east or west
      return dx < 0 ? PortSide.WEST : PortSide.EAST;
    } else {
      return dy < 0 ? PortSide.NORTH : PortSide.SOUTH;
    }
  }

}
