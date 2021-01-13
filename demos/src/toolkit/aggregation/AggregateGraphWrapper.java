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
package toolkit.aggregation;

import com.yworks.yfiles.geometry.IMutablePoint;
import com.yworks.yfiles.geometry.IMutableRectangle;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.MutablePoint;
import com.yworks.yfiles.geometry.MutableRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.AbstractGraphWrapper;
import com.yworks.yfiles.graph.AdjacencyTypes;
import com.yworks.yfiles.graph.BendEventArgs;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.EdgeDefaults;
import com.yworks.yfiles.graph.EdgeEventArgs;
import com.yworks.yfiles.graph.FilteredGraphWrapper;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IContextLookup;
import com.yworks.yfiles.graph.IContextLookupChainLink;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IEdgeDefaults;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.ILookupDecorator;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.IPortOwner;
import com.yworks.yfiles.graph.ItemChangedEventArgs;
import com.yworks.yfiles.graph.LabelEventArgs;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.LookupChain;
import com.yworks.yfiles.graph.NodeDefaults;
import com.yworks.yfiles.graph.NodeEventArgs;
import com.yworks.yfiles.graph.PortEventArgs;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.IList;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.utils.ItemEventArgs;
import com.yworks.yfiles.view.input.IPositionHandler;
import com.yworks.yfiles.view.input.IReshapeHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An IGraph implementation that wraps another graph and can replace some of its items by other items.
 * <p>
 * More precisely, a set of nodes can be aggregated to a new node with the {@link #aggregate(IListEnumerable, RectD, INodeStyle, Object)}
 * method. This will hide the set of nodes and create a new aggregation node while replacing adjacent edges with
 * aggregation edges.
 * </p>
 * <p>
 * Items of the wrapped graph ("original graph") are called <em>original items</em> while the temporary items that are
 * created for aggregation are called <em>aggregation items</em>.
 * </p>
 * <p>
 * This class implements a concept similar to grouping and folding. The conceptual difference is that with folding the
 * group nodes remain in the graph while the group is in expanded state. Contrary, with the AggregateGraphWrapper the
 * aggregation nodes are only in the graph when the nodes are aggregated. The difference in the implementation is that the
 * AggregateGraphWrapper reuses all original graph items, ensuring reference equality between items of the wrapped graph
 * and items of the AggregateGraphWrapper.
 * </p>
 * <p>
 * Note that this implementation does not support editing user gestures, e.g. with the
 * {@link com.yworks.yfiles.view.input.GraphEditorInputMode}.
 * </p>
 * <p>
 * Note also that this instance will register listeners with the wrapped graph instance, so {@link #dispose()} should be
 * called if this instance is not used any more.
 * </p>
 */
public final class AggregateGraphWrapper extends AbstractGraphWrapper {
  // This implementation combines a filtered graph (for hiding items) and additional aggregation items contained in
  // the aggregationNodes and aggregationEdges lists.
  // Events are forwarded from the wrapped graph to the filtered graph to this graph.
  // Most IGraph methods are overridden and "multiplex" between the filtered graph and the aggregation items.
  private FilteredGraphWrapper filteredGraph;

  // the set of hidden nodes and edges
  private final Set<IModelItem> filteredOriginalNodes = new HashSet<IModelItem>();

  private final Set<AggregationItem> filteredAggregationItems = new HashSet<AggregationItem>();

  private final List<AggregationNode> aggregationNodes = new ArrayList<AggregationNode>();

  private final List<AggregationEdge> aggregationEdges = new ArrayList<AggregationEdge>();

  // live views of the currently visible items
  private final IListEnumerable<INode> nodes;

  private final IListEnumerable<IEdge> edges;

  private final IListEnumerable<ILabel> labels;

  private final IListEnumerable<IPort> ports;

  private INodeDefaults aggregationNodeDefaults;

  private IEdgeDefaults aggregationEdgeDefaults;

  private final AggregateLookupDecorator lookupDecorator;

  /**
   * Creates a new instance of this graph wrapper.
   * @param graph The graph to be wrapped ("original graph").
   * @throws IllegalArgumentException If the {@code graph} is another {@link AggregateGraphWrapper}
   */
  public AggregateGraphWrapper( IGraph graph ) {
    super(graph);
    if (graph instanceof AggregateGraphWrapper) {
      throw new IllegalArgumentException("Parameter graph: Cannot wrap another AggregateGraphWrapper");
    }

    // the base constructor call sets graph as WrappedGraph
    // this triggers OnGraphChanged where filteredGraph is initialized as FilteredGraphWrapper of graph

    setEdgeReplacementPolicy(EdgeReplacementPolicy.UNDIRECTED);

    lookupDecorator = new AggregateLookupDecorator(this);

    nodes = IListEnumerable.create(IEnumerable.concat(
        filteredGraph.getNodes(),
        EnumerableExtensions.filter(aggregationNodes, this::aggregationItemPredicate)));
    edges = IListEnumerable.create(IEnumerable.concat(
        filteredGraph.getEdges(),
        EnumerableExtensions.filter(aggregationEdges, this::aggregationItemPredicate)));
    ports = IListEnumerable.create(IEnumerable.concat(
        EnumerableExtensions.flatMap(nodes, node -> node.getPorts()),
        EnumerableExtensions.flatMap(edges, edge -> edge.getPorts())));
    labels = IListEnumerable.create(IEnumerable.concat(IEnumerable.concat(
        EnumerableExtensions.flatMap(nodes, node -> node.getLabels()),
        EnumerableExtensions.flatMap(edges, edge -> edge.getLabels())),
        EnumerableExtensions.flatMap(ports, port -> port.getLabels())));
  }

  @Override
  public IListEnumerable<INode> getNodes() {
    return nodes;
  }

  @Override
  public IListEnumerable<IEdge> getEdges() {
    return edges;
  }

  @Override
  public IListEnumerable<ILabel> getLabels() {
    return labels;
  }

  @Override
  public IListEnumerable<IPort> getPorts() {
    return ports;
  }

  private EdgeReplacementPolicy edgeReplacementPolicy = EdgeReplacementPolicy.NONE;

  /**
   * Gets what kind of edges should be created when replacing original edges with aggregation edges.
   * <p>
   * The default value is {@link EdgeReplacementPolicy#UNDIRECTED}.
   * </p>
   * @return The EdgeReplacementPolicy.
   * @see #setEdgeReplacementPolicy(EdgeReplacementPolicy)
   */
  public final EdgeReplacementPolicy getEdgeReplacementPolicy() {
    return this.edgeReplacementPolicy;
  }

  /**
   * Sets what kind of edges should be created when replacing original edges with aggregation edges.
   * <p>
   * The default value is {@link EdgeReplacementPolicy#UNDIRECTED}.
   * </p>
   * @param value The EdgeReplacementPolicy to set.
   * @see #getEdgeReplacementPolicy()
   */
  public final void setEdgeReplacementPolicy( EdgeReplacementPolicy value ) {
    this.edgeReplacementPolicy = value;
  }

  /**
   * Gets the defaults for aggregation nodes.
   * @return The AggregationNodeDefaults.
   * @see IGraph#getNodeDefaults()
   * @see #setAggregationNodeDefaults(INodeDefaults)
   */
  public final INodeDefaults getAggregationNodeDefaults() {
    if (aggregationNodeDefaults == null) {
      aggregationNodeDefaults = new NodeDefaults();
    }
    return aggregationNodeDefaults;
  }

  /**
   * Sets the defaults for aggregation nodes.
   * @param value The AggregationNodeDefaults to set.
   * @see IGraph#getNodeDefaults()
   * @see #getAggregationNodeDefaults()
   */
  public final void setAggregationNodeDefaults( INodeDefaults value ) {
    aggregationNodeDefaults = value;
  }

  /**
   * Gets the defaults for aggregation edges.
   * <p>
   * Used when original edges are automatically replaced by aggregation edges.
   * </p>
   * @return The AggregationEdgeDefaults.
   * @see #setAggregationEdgeDefaults(IEdgeDefaults)
   */
  public final IEdgeDefaults getAggregationEdgeDefaults() {
    if (aggregationEdgeDefaults == null) {
      aggregationEdgeDefaults = new EdgeDefaults();
    }
    return aggregationEdgeDefaults;
  }

  /**
   * Sets the defaults for aggregation edges.
   * <p>
   * Used when original edges are automatically replaced by aggregation edges.
   * </p>
   * @param value The AggregationEdgeDefaults to set.
   * @see #getAggregationEdgeDefaults()
   */
  public final void setAggregationEdgeDefaults( IEdgeDefaults value ) {
    aggregationEdgeDefaults = value;
  }

  /**
   * Calls the base method with the {@link #filteredGraph} instead of the passed graph for correct event forwarding.
   */
  @Override
  protected void onGraphChanged( IGraph oldGraph, IGraph newGraph ) {
    if (oldGraph == null) {
      filteredGraph = new FilteredGraphWrapper(getWrappedGraph(), this::nodePredicate, null);
      super.onGraphChanged(null, filteredGraph);
    } else if (newGraph == null) {
      filteredGraph = null;
      super.onGraphChanged(filteredGraph, null);
    }
  }

  @Override
  public void dispose() {
    filteredGraph.dispose();
    super.dispose();
  }

  private boolean aggregationItemPredicate( IModelItem item ) {
    return !filteredAggregationItems.contains(item);
  }

  private boolean nodePredicate( INode node ) {
    return !filteredOriginalNodes.contains(node);
  }


  /**
   * Hides the {@code portOwner} and all items depending on it and raises the according removed events.
   * <p>
   * For nodes, their labels, ports and adjacent edges are hidden. For edges, their labels, ports and bends are hidden.
   * </p>
   */
  private void hide( IPortOwner portOwner ) {
    AggregationNode aggregationNode = (portOwner instanceof AggregationNode) ? (AggregationNode)portOwner : null;
    if (aggregationNode != null) {
      boolean oldIsGroupNode = isGroupNode(aggregationNode);
      INode oldParent = getParent(aggregationNode);
      hideAdjacentEdges(aggregationNode);
      filteredAggregationItems.add(aggregationNode);
      raiseLabelRemovedEvents(aggregationNode);
      raisePortRemovedEvents(aggregationNode);
      onNodeRemoved(new NodeEventArgs(aggregationNode, oldParent, oldIsGroupNode));
      return;
    }

    AggregationEdge aggregationEdge = (portOwner instanceof AggregationEdge) ? (AggregationEdge)portOwner : null;
    if (aggregationEdge != null) {
      hideAdjacentEdges(aggregationEdge);
      filteredAggregationItems.add(aggregationEdge);
      raiseLabelRemovedEvents(aggregationEdge);
      raisePortRemovedEvents(aggregationEdge);
      onEdgeRemoved(new EdgeEventArgs(aggregationEdge, (IPort)null, (IPort)null, (IPortOwner)null, (IPortOwner)null));
      return;
    }

    // hide adjacent aggregation edges (which are not hidden by filtered graph)
    for (IEdge edge : edgesAt(portOwner, AdjacencyTypes.ALL).toList()) {
      if (edge instanceof AggregationEdge) {
        hide(edge);
      }
    }
    filteredOriginalNodes.add(portOwner);
    predicateChanged(portOwner);
  }

  private void hideAdjacentEdges( AggregationLabelPortOwner portOwner ) {
    for (IEdge edge : edgesAt(portOwner, AdjacencyTypes.ALL).toList()) {
      hide(edge);
    }
  }

  /**
   * Shows an item, their labels/ports/bends, and their adjacent edges. Raises all necessary events.
   */
  private void show( IPortOwner item ) {
    AggregationNode aggregationNode = (item instanceof AggregationNode) ? (AggregationNode)item : null;
    if (aggregationNode != null) {
      filteredAggregationItems.remove(aggregationNode);
      onNodeCreated(new NodeEventArgs(aggregationNode, getParent(aggregationNode), isGroupNode(aggregationNode)));
      raisePortAddedEvents(aggregationNode);
      showAdjacentEdges(aggregationNode);
      raiseLabelAddedEvents(aggregationNode);
      return;
    }

    AggregationEdge aggregationEdge = (item instanceof AggregationEdge) ? (AggregationEdge)item : null;
    if (aggregationEdge != null) {
      filteredAggregationItems.remove(aggregationEdge);
      onEdgeCreated(new EdgeEventArgs(aggregationEdge, (IPort)null, (IPort)null, (IPortOwner)null, (IPortOwner)null));
      raisePortAddedEvents(aggregationEdge);
      showAdjacentEdges(aggregationEdge);
      raiseLabelAddedEvents(aggregationEdge);
      return;
    }

    filteredOriginalNodes.remove(item);
    predicateChanged(item);
    showAdjacentEdges(item);
  }

  private void showAdjacentEdges( final IPortOwner portOwner ) {
    // - cannot use EdgesAt() here, since hidden edges are not considered there
    // - aggregation edges are enough, since original edges are managed by FilteredGraphWrapper
    List<IPort> ownerPorts = portOwner.getPorts().toList();
    Iterable<AggregationEdge> adjacentEdges = aggregationEdges.stream()
        .filter(edge -> ownerPorts.contains(edge.getSourcePort()) || ownerPorts.contains(edge.getTargetPort()))
        .collect(Collectors.toList());
    List<IPort> ports = getPorts().toList();
    for (AggregationEdge edge : adjacentEdges) {
      if (ports.contains(edge.getSourcePort()) && ports.contains(edge.getTargetPort())) {
        show(edge);
      }
    }
  }

  private void raiseLabelAddedEvents( ILabelOwner labelOwner ) {
    for (ILabel label : labelOwner.getLabels()) {
      onLabelAdded(new LabelEventArgs(label, labelOwner));
    }
  }

  private void raisePortAddedEvents( IPortOwner portOwner ) {
    for (IPort port : portOwner.getPorts()) {
      onPortAdded(new PortEventArgs(port, portOwner));
      raiseLabelAddedEvents(port);
    }
  }

  private void raiseLabelRemovedEvents( AggregationLabelOwner labelOwner ) {
    for (ILabel label : labelOwner.getLabels()) {
      onLabelRemoved(new LabelEventArgs(label, labelOwner));
    }
  }

  private void raisePortRemovedEvents( AggregationLabelPortOwner portOwner ) {
    for (IPort port : portOwner.getPorts()) {
      raiseLabelRemovedEvents((port instanceof AggregationPort) ? (AggregationPort)port : null);
      onPortRemoved(new PortEventArgs(port, portOwner));
    }
  }

  private void predicateChanged( IModelItem item ) {
    if (item instanceof INode) {
      filteredGraph.nodePredicateChanged((INode)item);
    } else if (item instanceof IEdge) {
      filteredGraph.edgePredicateChanged((IEdge)item);
    }
  }



  /**
   * Aggregates the {@code nodes} to a new aggregation node.
   * <p>
   * This temporarily removes the {@code nodes} together with their labels, ports, and adjacent edges. Then a new aggregation
   * node is created and replacement edges for all removed edges are created: for each edge between a node in {@code nodes}
   * and a node not in {@code nodes} a new edge is created. If this would lead to multiple edges between the aggregation node
   * and another node, only one edge (or two, see {@link #getEdgeReplacementPolicy() EdgeReplacementPolicy}) is created.
   * </p>
   * @param nodes The nodes to be temporarily removed.
   * @param layout The layout for the new aggregation node or {@code null}
   * @param style The style for the new aggregation node or {@code null}
   * @return A new aggregation node.
   * @throws IllegalArgumentException Any of the {@code nodes} is not in the graph.
   * @see #separate(INode)
   */
  public final INode aggregate( IListEnumerable<INode> nodes, RectD layout, INodeStyle style ) {
    return aggregate(nodes, layout, style, (Object)null);
  }

  /**
   * Aggregates the {@code nodes} to a new aggregation node.
   * <p>
   * This temporarily removes the {@code nodes} together with their labels, ports, and adjacent edges. Then a new aggregation
   * node is created and replacement edges for all removed edges are created: for each edge between a node in {@code nodes}
   * and a node not in {@code nodes} a new edge is created. If this would lead to multiple edges between the aggregation node
   * and another node, only one edge (or two, see {@link #getEdgeReplacementPolicy() EdgeReplacementPolicy}) is created.
   * </p>
   * @param nodes The nodes to be temporarily removed.
   * @param layout The layout for the new aggregation node or {@code null}
   * @return A new aggregation node.
   * @throws IllegalArgumentException Any of the {@code nodes} is not in the graph.
   * @see #separate(INode)
   */
  public final INode aggregate( IListEnumerable<INode> nodes, RectD layout ) {
    return aggregate(nodes, layout, (INodeStyle)null, (Object)null);
  }

  /**
   * Aggregates the {@code nodes} to a new aggregation node.
   * <p>
   * This temporarily removes the {@code nodes} together with their labels, ports, and adjacent edges. Then a new aggregation
   * node is created and replacement edges for all removed edges are created: for each edge between a node in {@code nodes}
   * and a node not in {@code nodes} a new edge is created. If this would lead to multiple edges between the aggregation node
   * and another node, only one edge (or two, see {@link #getEdgeReplacementPolicy() EdgeReplacementPolicy}) is created.
   * </p>
   * @param nodes The nodes to be temporarily removed.
   * @return A new aggregation node.
   * @throws IllegalArgumentException Any of the {@code nodes} is not in the graph.
   * @see #separate(INode)
   */
  public final INode aggregate( IListEnumerable<INode> nodes ) {
    return aggregate(nodes, (RectD)null, (INodeStyle)null, (Object)null);
  }

  /**
   * Aggregates the {@code nodes} to a new aggregation node.
   * <p>
   * This temporarily removes the {@code nodes} together with their labels, ports, and adjacent edges. Then a new aggregation
   * node is created and replacement edges for all removed edges are created: for each edge between a node in {@code nodes}
   * and a node not in {@code nodes} a new edge is created. If this would lead to multiple edges between the aggregation node
   * and another node, only one edge (or two, see {@link #getEdgeReplacementPolicy() EdgeReplacementPolicy}) is created.
   * </p>
   * @param nodes The nodes to be temporarily removed.
   * @param layout The layout for the new aggregation node or {@code null}
   * @param style The style for the new aggregation node or {@code null}
   * @param tag The style for the new aggregation node or {@code null}
   * @return A new aggregation node.
   * @throws IllegalArgumentException Any of the {@code nodes} is not in the graph.
   * @see #separate(INode)
   */
  public final INode aggregate( IListEnumerable<INode> nodes, RectD layout, INodeStyle style, Object tag ) {
    for (INode node : nodes) {
      if (!contains(node)) {
        throw new IllegalArgumentException("Parameter nodes: Cannot aggregate node " + node + " that is not in this graph.");
      }
    }

    MutableRectangle nodeLayout = layout != null ? new MutableRectangle(layout) : new MutableRectangle(PointD.ORIGIN, getAggregationNodeDefaults().getSize());
    INodeStyle nodeStyle = style != null ? style : getAggregationNodeDefaults().getStyleInstance();
    List<INode> aggregatedNodes = nodes.toList();
    AggregationNode aggregationNode = new AggregationNode(this, aggregatedNodes);
    aggregationNode.layout = nodeLayout;
    aggregationNode.setStyle(nodeStyle);
    aggregationNode.setTag(tag);
    INode parent = this.getGroupingSupport().getNearestCommonAncestor(nodes);
    if (parent != null) {
      aggregationNode.parent = parent;
      AggregationNode aggregationNodeParent = (parent instanceof AggregationNode) ? (AggregationNode)parent : null;
      if (aggregationNodeParent != null) {
        aggregationNodeParent.children.add(aggregationNode);
      }
    }

    aggregationNodes.add(aggregationNode);
    onNodeCreated(new ItemEventArgs<INode>(aggregationNode));

    if (getEdgeReplacementPolicy() != EdgeReplacementPolicy.NONE) {
      replaceAdjacentEdges(nodes, aggregationNode);
    }

    // hide not until here, so old graph structure is still intact when replacing edges
    for (INode node : nodes) {
      hide(node);
    }

    return aggregationNode;
  }

  /**
   * Replaces adjacent edges by new aggregation edges. Prevents duplicate edges following
   * {@link #getEdgeReplacementPolicy() EdgeReplacementPolicy}.
   */
  private void replaceAdjacentEdges( IListEnumerable<INode> nodes, AggregationNode aggregationNode ) {
    boolean edgesAreDirected = getEdgeReplacementPolicy() == EdgeReplacementPolicy.DIRECTED;
    HashMap<IPortOwner, AggregationEdge> outgoingReplacementEdges = new HashMap<IPortOwner, AggregationEdge>();
    HashMap<IPortOwner, AggregationEdge> incomingReplacementEdges = edgesAreDirected ? new HashMap<IPortOwner, AggregationEdge>() : outgoingReplacementEdges;
    for (INode node : nodes) {
      replaceEdges(AdjacencyTypes.OUTGOING, node, aggregationNode, nodes, outgoingReplacementEdges);
      replaceEdges(AdjacencyTypes.INCOMING, node, aggregationNode, nodes, incomingReplacementEdges);
    }

    // raise edge created events not until here, so the aggregated items are complete
    for (AggregationEdge edge : outgoingReplacementEdges.values()) {
      onEdgeCreated(new EdgeEventArgs(edge, (IPort)null, (IPort)null, (IPortOwner)null, (IPortOwner)null));
    }

    if (edgesAreDirected) {
      for (AggregationEdge edge : incomingReplacementEdges.values()) {
        onEdgeCreated(new EdgeEventArgs(edge, (IPort)null, (IPort)null, (IPortOwner)null, (IPortOwner)null));
      }
    }
  }

  private void replaceEdges( AdjacencyTypes adjacencyType, INode node, IPortOwner aggregationPortOwner, IListEnumerable<INode> items, Map<IPortOwner, AggregationEdge> replacementEdges ) {
    List<IEdge> adjacentEdges = edgesAt(node, adjacencyType).toList();
    for (IEdge edge : adjacentEdges) {
      boolean isIncoming = AdjacencyTypes.INCOMING.equals(adjacencyType);
      IPort otherPort = isIncoming ? edge.getSourcePort() : edge.getTargetPort();

      IPortOwner otherPortOwner = otherPort.getOwner();
      if (items.toList().contains(otherPortOwner)) {
        // don't create aggregation edges for edges between aggregated items
        continue;
      }
      if (replacementEdges.containsKey(otherPortOwner)) {
        AggregationEdge existingReplacementEdge = replacementEdges.get(otherPortOwner);
        existingReplacementEdge.aggregatedEdges.add(edge);
        continue;
      }

      if (edge instanceof AggregationEdge) {
        // otherwise the edge is automatically hidden by filtered graph
        hide(edge);
      }

      AggregationEdge replacementEdge = replaceEdge(edge, aggregationPortOwner, otherPort, isIncoming);
      replacementEdges.put(otherPortOwner, replacementEdge);
    }
  }

  private AggregationEdge replaceEdge( IEdge edge, IPortOwner newPortOwner, IPort otherPort, boolean isIncoming ) {
    IPort aggregationPort = addPort(newPortOwner);
    AggregationEdge replacementEdge;
    if (isIncoming) {
      replacementEdge = createAggregationEdge(otherPort, aggregationPort, null);
    } else {
      replacementEdge = createAggregationEdge(aggregationPort, otherPort, null);
    }

    replacementEdge.aggregatedEdges.add(edge);
    return replacementEdge;
  }

  /**
   * Separates nodes again that were previously aggregated via
   * {@link #aggregate(IListEnumerable, RectD, INodeStyle, Object)}.
   * <p>
   * Removes the aggregation node permanently together with its labels, ports, and adjacent edges. Then inserts the items
   * that were temporarily removed in {@link #aggregate(IListEnumerable, RectD, INodeStyle, Object)} again.
   * </p>
   * @param node The aggregation node to separate.
   * @throws IllegalArgumentException The {@code node} is not in the graph or is currently hidden or is not an aggregation node.
   */
  public final void separate( INode node ) {
    AggregationNode aggregationNode = (node instanceof AggregationNode) ? (AggregationNode)node : null;
    if (aggregationNode == null) {
      throw new IllegalArgumentException("Parameter node: Cannot separate original node " + node + ".");
    }
    if (!contains(node)) {
      if (aggregationNodes.contains(node)) {
        throw new IllegalArgumentException("Parameter node: Cannot separate aggregation node " + node + ", because it is aggregated by another node.");
      }
      throw new IllegalArgumentException("Parameter node: Cannot separate aggregation node " + node + " that is not in this graph.");
    }

    List<IEdge> adjacentEdges = edgesAt(aggregationNode, AdjacencyTypes.ALL).toList();
    for (IEdge edge : adjacentEdges) {
      removeAggregationEdge((AggregationEdge)edge);
    }

    removeAggregationNode(aggregationNode);
    for (INode aggregatedNode : aggregationNode.aggregatedNodes) {
      show(aggregatedNode);
    }

    for (IEdge edge : adjacentEdges) {
      showOrRemoveAggregatedEdges((AggregationEdge)edge);
    }

    if (getEdgeReplacementPolicy() != EdgeReplacementPolicy.NONE) {
      replaceMissingEdges(aggregationNode);
    }
  }

  private void showOrRemoveAggregatedEdges( AggregationEdge aggregationEdge ) {
    for (IEdge aggregatedEdge : aggregationEdge.aggregatedEdges) {
      AggregationEdge replacedEdge = (aggregatedEdge instanceof AggregationEdge) ? (AggregationEdge)aggregatedEdge : null;
      if (replacedEdge != null) {
        // manually show aggregated AggregationEdges, other edges are handled by filtered graph
        if (contains(replacedEdge.getSourcePort()) && contains(replacedEdge.getTargetPort())) {
          show(aggregatedEdge);
        } else {
          removeAggregationEdge(replacedEdge);
        }
      }
    }
  }

  /**
   * When nodes are separated in a different order they were aggregated, we need to create new aggregation edges for the
   * nodes that were just expanded.
   */
  private void replaceMissingEdges( AggregationNode aggregationNode ) {
    boolean edgesAreDirected = getEdgeReplacementPolicy() == EdgeReplacementPolicy.DIRECTED;
    List<INode> aggregatedNodes = aggregationNode.aggregatedNodes;
    for (INode node : aggregatedNodes) {
      HashMap<INode, AggregationEdge> outgoingReplacementEdges = new HashMap<INode, AggregationEdge>();
      HashMap<INode, AggregationEdge> incomingReplacementEdges = edgesAreDirected ? new HashMap<INode, AggregationEdge>() : outgoingReplacementEdges;
      replaceMissingEdges(AdjacencyTypes.OUTGOING, node, outgoingReplacementEdges);
      replaceMissingEdges(AdjacencyTypes.INCOMING, node, incomingReplacementEdges);

      // raise edge created events not until here, so the aggregated items are complete
      for (AggregationEdge edge : outgoingReplacementEdges.values()) {
        onEdgeCreated(new EdgeEventArgs(edge));
      }

      if (edgesAreDirected) {
        for (AggregationEdge edge : incomingReplacementEdges.values()) {
          onEdgeCreated(new EdgeEventArgs(edge));
        }
      }
    }
  }

  private void replaceMissingEdges( AdjacencyTypes adjacencyType, final INode node, Map<INode, AggregationEdge> seenNodes ) {
    final boolean isIncoming = AdjacencyTypes.INCOMING.equals(adjacencyType);
    List<IPort> ports = node.getPorts().toList();

    Stream<? extends IEdge> edgesAt = aggregationEdges.stream()
        .filter(edge -> ports.contains(isIncoming ? edge.getTargetPort() : edge.getSourcePort()));

    if (!isAggregationItem(node)) {
      edgesAt = Stream.concat(edgesAt, super.edgesAt(node, AdjacencyTypes.ALL).stream());
    }

    for (IEdge edge : edgesAt.collect(Collectors.toList())) {
      if (contains(edge)) {
        // is already a proper edge
        continue;
      }

      IPort thisPort = isIncoming ? edge.getTargetPort() : edge.getSourcePort();
      IPort otherPort = isIncoming ? edge.getSourcePort() : edge.getTargetPort();
      // the node is contained in another aggregation node -> find it
      AggregationNode otherNode = otherPort.getOwner() instanceof INode ? findAggregationNode((INode) otherPort.getOwner())  : null;
      if (otherNode == null || !contains(otherNode)) {
        continue;
      }

      AggregationEdge aggregationEdge = seenNodes.getOrDefault(otherNode, null);
      if (aggregationEdge != null) {
        // we already created an edge between this and the other node
        aggregationEdge.aggregatedEdges.add(edge);
        continue;
      }

      aggregationEdge = replaceEdge(edge, otherNode, thisPort, isIncoming);
      seenNodes.put(otherNode, aggregationEdge);
    }
  }

  private AggregationNode findAggregationNode( final INode node ) {
    return aggregationNodes.stream()
        .filter(n -> n.aggregatedNodes.contains(node))
        .findFirst()
        .orElse(null);
  }

  /**
   * Separates all aggregation nodes such that this graph contains exactly the same items as the
   * {@link AbstractGraphWrapper#getWrappedGraph() WrappedGraph}.
   */
  public final void separateAll() {
    do {
      List<AggregationNode> visibleNodes = aggregationNodes.stream()
          .filter(this::aggregationItemPredicate)
          .collect(Collectors.toList());
      for (AggregationNode aggregationNode : visibleNodes) {
        separate(aggregationNode);
      }
    } while (aggregationNodes.size() > 0);
  }



  /**
   * Returns {@code true} iff the {@code item} is an aggregation item and therefore not contained in the wrapped graph.
   * <p>
   * Does not check if the item is currently {@link #contains(IModelItem) contained} in the graph or whether the items was
   * created by this graph instance.
   * </p>
   * @param item The item to check.
   * @return {@code true} iff the {@code item} is an aggregation item.
   */
  public final boolean isAggregationItem( IModelItem item ) {
    return item instanceof AggregationItem;
  }

  /**
   * Returns the items that are directly aggregated by the {@code item}.
   * <p>
   * In contrast to {@link #getAllAggregatedOriginalItems(IModelItem)} this method returns both original as well as
   * aggregation items, but only direct descendants in the aggregation hierarchy.
   * </p>
   * <p>
   * {@code item} doesn't need to be {@link #contains(IModelItem) contained} currently but might be aggregated in another
   * item.
   * </p>
   * @param item The aggregation item.
   * @return The items that are aggregated by the
   * {@code item}. If an aggregation node is passed, this will return the aggregated nodes. If an aggregation edge is
   * passed, this will return the edges it replaces. Otherwise an empty enumerable will be returned. The enumerable may
   * contain both aggregation items as well as original items.
   */
  public final IListEnumerable<IModelItem> getAggregatedItems( IModelItem item ) {
    AggregationNode node = (item instanceof AggregationNode) ? (AggregationNode)item : null;
    if (node != null) {
      return IListEnumerable.create((List) node.aggregatedNodes);
    }

    AggregationEdge edge = (item instanceof AggregationEdge) ? (AggregationEdge)item : null;
    if (edge != null) {
      return IListEnumerable.create((List)edge.aggregatedEdges);
    }

    return IListEnumerable.EMPTY;
  }

  /**
   * Returns the (recursively) aggregated original items of the {@code item}.
   * <p>
   * In contrast to {@link #getAggregatedItems(IModelItem)} this method returns only original items, but also items
   * recursively nested in the aggregation hierarchy.
   * </p>
   * @param item The aggregation item.
   * @return A list of items of the {@link AbstractGraphWrapper#getWrappedGraph() WrappedGraph} that is either directly contained in
   * the {@code item} or recursively in any contained aggregation items. This list consists only of items of the wrapped
   * graph.
   * @see #getAggregatedItems(IModelItem)
   */
  public final IListEnumerable<IModelItem> getAllAggregatedOriginalItems( IModelItem item ) {
    ArrayList<IModelItem> result = new ArrayList<IModelItem>();
    IListEnumerable<IModelItem> aggregatedItems = getAggregatedItems(item);
    for (IModelItem aggregatedItem : aggregatedItems) {
      if (isAggregationItem(aggregatedItem)) {
        getAllAggregatedOriginalItems(aggregatedItem).forEach(result::add);
      } else {
        result.add(aggregatedItem);
      }
    }
    return IListEnumerable.create(result);
  }



  /**
   * Removes the given item from the graph.
   * <p>
   * If {@code item} is an aggregation node or aggregation edge, all aggregated items are removed as well.
   * </p>
   * @param item The item to remove.
   */
  @Override
  public void remove( IModelItem item ) {
    if (!contains(item)) {
      throw new IllegalArgumentException("Parameter 'item': Item is not in this graph.");
    }
    removeCore(item);
  }

  private void removeCore( IModelItem item ) {
    AggregationNode aggregationNode = (item instanceof AggregationNode) ? (AggregationNode)item : null;
    if (aggregationNode != null) {
      if (aggregationNode.graph != this) {
        return;
      }
      removeAggregationNode(aggregationNode);
      for (INode aggregatedNode : aggregationNode.aggregatedNodes) {
        // we can remove the node without checking if it is in the graph
        removeCore(aggregatedNode);
      }
      return;
    }

    AggregationEdge aggregationEdge = (item instanceof AggregationEdge) ? (AggregationEdge)item : null;
    if (aggregationEdge != null) {
      if (aggregationEdge.graph != this) {
        return;
      }
      removeAggregationEdge(aggregationEdge);

      for (IEdge aggregatedEdge : aggregationEdge.aggregatedEdges) {
        removeCore(aggregatedEdge);
      }

      cleanupPort(aggregationEdge.getSourcePort());
      cleanupPort(aggregationEdge.getTargetPort());
      return;
    }

    if (item instanceof AggregationBend) {
      removeAggregationBend((AggregationBend)item);
    } else if (item instanceof AggregationPort) {
      removeAggregationPort((AggregationPort)item);
    } else if (item instanceof AggregationLabel) {
      removeAggregationLabel((AggregationLabel)item);
    } else {
      super.remove(item);
    }
  }

  private void cleanupPort( final IPort port ) {
    boolean isAggregationItem = isAggregationItem(port);
    // check the auto-cleanup policy to apply
    INodeDefaults nodeDefaults = isAggregationItem
        ? getAggregationNodeDefaults()
        : (port.getOwner() instanceof INode && isGroupNode((INode) port.getOwner()))
          ? getWrappedGraph().getGroupNodeDefaults()
          : getWrappedGraph().getNodeDefaults();
    boolean autoCleanUp = nodeDefaults.getPortDefaults().isAutoCleanUpEnabled();
    if (!autoCleanUp) {
      return;
    }
    int edgesAtPort = (int) aggregationEdges.stream()
        .filter(edge -> edge.getSourcePort() == port || edge.getTargetPort() == port)
        .count();
    if (!isAggregationItem) {
      edgesAtPort += super.edgesAt(port, AdjacencyTypes.ALL).size();
    }
    if (edgesAtPort == 0) {
      removeCore(port);
    }
  }

  private void removeAggregationNode( AggregationNode aggregationNode ) {
    for (IPort port : aggregationNode.getPorts().toList()) {
      removeAggregationPort((AggregationPort)port);
    }
    for (ILabel label : aggregationNode.getLabels().toList()) {
      removeAggregationLabel((AggregationLabel)label);
    }
    boolean oldIsGroupNode = isGroupNode(aggregationNode);
    INode oldParent = getParent(aggregationNode);
    aggregationNodes.remove(aggregationNode);
    aggregationNode.graph = null;
    onNodeRemoved(new NodeEventArgs(aggregationNode, oldParent, oldIsGroupNode));
  }

  private void removeAggregationEdge( AggregationEdge aggregationEdge ) {
    for (ILabel label : aggregationEdge.getLabels().toList()) {
      removeAggregationLabel((AggregationLabel)label);
    }
    for (IPort port : aggregationEdge.getPorts().toList()) {
      removeAggregationPort((AggregationPort)port);
    }
    for (IBend bend : aggregationEdge.getBends().toList()) {
      removeAggregationBend((AggregationBend)bend);
    }
    aggregationEdges.remove(aggregationEdge);
    filteredAggregationItems.remove(aggregationEdge);
    aggregationEdge.graph = null;
    onEdgeRemoved(new EdgeEventArgs(aggregationEdge));
  }

  private void removeAggregationBend( AggregationBend aggregationBend ) {
    List<AggregationBend> bendList = aggregationBend.owner.bends;
    int index = bendList.indexOf(aggregationBend);
    bendList.remove(aggregationBend);
    aggregationBend.graph = null;
    onBendRemoved(new BendEventArgs(aggregationBend, aggregationBend.getOwner(), index));
  }

  private void removeAggregationPort( AggregationPort aggregationPort ) {
    for (IEdge edge : edgesAt(aggregationPort, AdjacencyTypes.ALL).toList()) {
      removeAggregationEdge((AggregationEdge)edge);
    }
    for (ILabel label : aggregationPort.getLabels().toList()) {
      removeAggregationLabel((AggregationLabel)label);
    }
    aggregationPort.owner.ports.remove(aggregationPort);
    aggregationPort.graph = null;
    onPortRemoved(new PortEventArgs(aggregationPort, aggregationPort.getOwner()));
  }

  private void removeAggregationLabel( AggregationLabel aggregationLabel ) {
    aggregationLabel.owner.labels.remove(aggregationLabel);
    aggregationLabel.graph = null;
    onLabelRemoved(new LabelEventArgs(aggregationLabel, aggregationLabel.getOwner()));
  }

  @Override
  public IListEnumerable<IEdge> edgesAt( final IPortOwner owner, AdjacencyTypes type ) {
    if (!contains(owner)) {
      throw new IllegalArgumentException("Parameter 'owner': Owner is not in this graph");
    }
    List<IPort> ports = owner.getPorts().toList();
    switch (type.value()) {
      case 0:
        return IListEnumerable.EMPTY;
      case 1:
        return IListEnumerable.create(getEdges().stream()
            .filter(edge -> ports.contains(edge.getTargetPort()))
            .collect(Collectors.toList()));
      case 2:
        return IListEnumerable.create(getEdges().stream()
            .filter(edge -> ports.contains(edge.getSourcePort()))
            .collect(Collectors.toList()));
      default:
      case 3:
        return IListEnumerable.create(getEdges().stream()
            .filter(edge -> ports.contains(edge.getSourcePort()) || ports.contains(edge.getTargetPort()))
            .collect(Collectors.toList()));
    }
  }

  @Override
  public IListEnumerable<IEdge> edgesAt( final IPort port, AdjacencyTypes type ) {
    if (!contains(port)) {
      throw new IllegalArgumentException("Parameter 'port': Port is not in this graph.");
    }

    switch (type.value()) {
      case 0:
        return IListEnumerable.EMPTY;
      case 1:
        return IListEnumerable.create(getEdges().stream()
            .filter(edge -> port == edge.getTargetPort())
            .collect(Collectors.toList()));
      case 2:
        return IListEnumerable.create(getEdges().stream()
            .filter(edge -> port == edge.getSourcePort())
            .collect(Collectors.toList()));
      default:
      case 3:
        return IListEnumerable.create(getEdges().stream()
            .filter(edge -> port == edge.getSourcePort() || port == edge.getTargetPort())
            .collect(Collectors.toList()));
    }
  }

  @Override
  public void setEdgePorts( IEdge edge, IPort sourcePort, IPort targetPort ) {
    if (!contains(edge)) {
      throw new IllegalArgumentException("Parameter 'edge': Not in Graph");
    }
    if (!contains(sourcePort)) {
      throw new IllegalArgumentException("Parameter 'sourcePort': Not in Graph");
    }
    if (!contains(targetPort)) {
      throw new IllegalArgumentException("Parameter 'targetPort': Not in Graph");
    }

    if (edge instanceof AggregationEdge) {
      throw new UnsupportedOperationException("Cannot set ports of aggregation edge " + edge);
    }
    if (sourcePort instanceof AggregationPort) {
      throw new IllegalStateException("Cannot reconnect original edge to " + sourcePort + ".");
    }
    if (targetPort instanceof AggregationPort) {
      throw new IllegalStateException("Cannot reconnect original edge to " + targetPort + ".");
    }
    super.setEdgePorts(edge, sourcePort, targetPort);
  }

  @Override
  public boolean contains( IModelItem item ) {
    AggregationNode node = (item instanceof AggregationNode) ? (AggregationNode)item : null;
    if (node != null) {
      return node.graph == this && aggregationItemPredicate(node);
    }
    AggregationEdge edge = (item instanceof AggregationEdge) ? (AggregationEdge)item : null;
    if (edge != null) {
      return edge.graph == this && aggregationItemPredicate(edge);
    }
    AggregationPort port = (item instanceof AggregationPort) ? (AggregationPort)item : null;
    if (port != null) {
      return port.graph == this && contains(port.getOwner());
    }
    AggregationLabel label = (item instanceof AggregationLabel) ? (AggregationLabel)item : null;
    if (label != null) {
      return label.graph == this && contains(label.getOwner());
    }
    AggregationBend bend = (item instanceof AggregationBend) ? (AggregationBend)item : null;
    if (bend != null) {
      return bend.graph == this && contains(bend.getOwner());
    }
    return filteredGraph.contains(item);
  }

  @Override
  public void setNodeLayout( INode node, RectD layout ) {
    if (!contains(node)) {
      throw new IllegalArgumentException("Parameter 'node': Node is not in this graph.");
    }
    if (Double.isNaN(layout.x) || Double.isNaN(layout.y) || Double.isNaN(layout.width) || Double.isNaN(layout.height)) {
      throw new IllegalArgumentException("Parameter 'layout': The layout must not contain a NaN value.");
    }

    AggregationNode aggregationNode = (node instanceof AggregationNode) ? (AggregationNode)node : null;
    if (aggregationNode != null) {
      RectD oldLayout = aggregationNode.getLayout().toRectD();
      aggregationNode.layout.reshape(layout);
      onNodeLayoutChanged(aggregationNode, oldLayout);
    } else {
      super.setNodeLayout(node, layout);
    }
  }

  @Override
  public IPort addPort( IPortOwner owner, IPortLocationModelParameter locationParameter, IPortStyle style, Object tag ) {
    if (!contains(owner)) {
      throw new IllegalArgumentException("Parameter 'owner': Owner is not in this graph.");
    }
    if (owner instanceof AggregationEdge) {
      throw new IllegalArgumentException("Parameter 'owner': Edge ports are not supported for aggregated edges");
    }

    if (owner instanceof AggregationNode) {
      AggregationNode aggregationNode = (AggregationNode)owner;
      IPortLocationModelParameter portLocationParameter = locationParameter != null ? locationParameter : getAggregationNodeDefaults().getPortDefaults().getLocationParameterInstance(owner);
      IPortStyle portStyle = style != null ? style : getAggregationNodeDefaults().getPortDefaults().getStyleInstance(owner);
      AggregationPort aggregationPort = new AggregationPort(this);
      aggregationPort.owner = aggregationNode;
      aggregationPort.setLocationParameter(portLocationParameter);
      aggregationPort.setTag(tag);
      aggregationPort.setStyle(portStyle);
      aggregationNode.ports.add(aggregationPort);

      onPortAdded(new ItemEventArgs<IPort>(aggregationPort));
      return aggregationPort;
    }
    return super.addPort(owner, locationParameter, style, tag);
  }

  @Override
  public void setPortLocationParameter( IPort port, IPortLocationModelParameter locationParameter ) {
    if (port.getLocationParameter() == locationParameter) {
      return;
    }
    if (!contains(port)) {
      throw new IllegalArgumentException("Parameter 'port': Port does not belong to this graph.");
    }
    if (locationParameter == null) {
      throw new IllegalArgumentException("Parameter locationParameter may not be null.");
    }
    if (!locationParameter.supports(port.getOwner())) {
      throw new IllegalArgumentException("Parameter 'locationParameter': The parameter does not support this port");
    }

    if (port instanceof AggregationPort) {
      AggregationPort aggregationPort = (AggregationPort)port;
      IPortLocationModelParameter oldParameter = port.getLocationParameter();
      aggregationPort.setLocationParameter(locationParameter);
      onPortLocationParameterChanged(new ItemChangedEventArgs<IPort, IPortLocationModelParameter>(port, oldParameter));
    } else {
      super.setPortLocationParameter(port, locationParameter);
    }
  }

  @Override
  public IBend addBend( IEdge edge, PointD location, int index ) {
    if (!contains(edge)) {
      throw new IllegalArgumentException("Parameter 'edge': Edge is not in this graph.");
    }
    if (Double.isNaN(location.x) || Double.isNaN(location.y)) {
      throw new IllegalArgumentException("Parameter 'location': The location must not contain a NaN value.");
    }

    if (edge instanceof AggregationEdge) {
      AggregationEdge aggregationEdge = (AggregationEdge)edge;
      AggregationBend aggregationBend = new AggregationBend(this);
      aggregationBend.owner = aggregationEdge;
      aggregationBend.location = new MutablePoint(location);
      List<AggregationBend> bendList = aggregationEdge.bends;
      if (index < 0) {
        bendList.add(aggregationBend);
      } else {
        bendList.add(index, aggregationBend);
      }
      onBendAdded(new ItemEventArgs<IBend>(aggregationBend));
      return aggregationBend;
    }
    return super.addBend(edge, location, index);
  }

  @Override
  public void setBendLocation( IBend bend, PointD location ) {
    if (bend.getLocation().equals(location)) {
      return;
    }
    if (!contains(bend)) {
      throw new IllegalArgumentException("Parameter 'bend': Edge is not in this graph.");
    }
    if (Double.isNaN(location.x) || Double.isNaN(location.y)) {
      throw new IllegalArgumentException("Parameter 'location': The location must not contain a NaN value.");
    }

    if (bend instanceof AggregationBend) {
      AggregationBend aggregationBend = (AggregationBend)bend;
      PointD oldLocation = aggregationBend.getLocation().toPointD();
      aggregationBend.location.relocate(location);
      onBendLocationChanged(bend, oldLocation);
    } else {
      super.setBendLocation(bend, location);
    }
  }

  @Override
  public ILabel addLabel( ILabelOwner owner, String text, ILabelModelParameter layoutParameter, ILabelStyle style, SizeD preferredSize, Object tag ) {
    if (owner instanceof AggregationLabelOwner) {
      AggregationLabelOwner labelOwner = (AggregationLabelOwner)owner;
      if (!contains(owner)) {
        throw new IllegalArgumentException("Parameter 'owner': Owner is not in this graph.");
      }
      if ((preferredSize != null) && (Double.isNaN(preferredSize.width) || Double.isNaN(preferredSize.height))) {
        throw new IllegalArgumentException("Parameter 'preferredSize': The size must not contain a NaN value.");
      }

      ILabelModelParameter labelModelParameter = layoutParameter != null ? layoutParameter : getLabelModelParameter(labelOwner);
      ILabelStyle labelStyle = style != null ? style : getLabelStyle(labelOwner);
      SizeD labelPreferredSize = preferredSize != null ? preferredSize : this.calculateLabelPreferredSize(labelOwner, text, labelModelParameter, labelStyle, (Object)null);

      AggregationLabel aggregationLabel = new AggregationLabel(this);
      aggregationLabel.owner = labelOwner;
      aggregationLabel.setText(text);
      aggregationLabel.setLayoutParameter(labelModelParameter);
      aggregationLabel.setPreferredSize(labelPreferredSize);
      aggregationLabel.setTag(tag);
      aggregationLabel.setStyle(labelStyle);
      labelOwner.labels.add(aggregationLabel);

      onLabelAdded(new ItemEventArgs<ILabel>(aggregationLabel));
      return aggregationLabel;
    }
    return super.addLabel(owner, text, layoutParameter, style, preferredSize, tag);
  }

  private ILabelModelParameter getLabelModelParameter( AggregationLabelOwner owner ) {
    if (owner instanceof AggregationNode) {
      return getAggregationNodeDefaults().getLabelDefaults().getLayoutParameterInstance(owner);
    } else if (owner instanceof AggregationEdge) {
      return getAggregationEdgeDefaults().getLabelDefaults().getLayoutParameterInstance(owner);
    } else {
      if (owner instanceof AggregationPort) {
        AggregationPort aggregationPort = (AggregationPort)owner;
        if (aggregationPort.getOwner() instanceof INode) {
          return getAggregationNodeDefaults().getPortDefaults().getLabelDefaults().getLayoutParameterInstance(owner);
        } else {
          return getAggregationEdgeDefaults().getPortDefaults().getLabelDefaults().getLayoutParameterInstance(owner);
        }
      }
    }
    // won't happen
    return null;
  }

  private ILabelStyle getLabelStyle( AggregationLabelOwner owner ) {
    if (owner instanceof AggregationNode) {
      return getAggregationNodeDefaults().getLabelDefaults().getStyleInstance(owner);
    } else if (owner instanceof AggregationEdge) {
      return getAggregationEdgeDefaults().getLabelDefaults().getStyleInstance(owner);
    } else {
      if (owner instanceof AggregationPort) {
        AggregationPort aggregationPort = (AggregationPort)owner;
        if (aggregationPort.getOwner() instanceof INode) {
          return getAggregationNodeDefaults().getPortDefaults().getLabelDefaults().getStyleInstance(owner);
        } else {
          return getAggregationEdgeDefaults().getPortDefaults().getLabelDefaults().getStyleInstance(owner);
        }
      }
    }
    // won't happen
    return null;
  }

  @Override
  public void setLabelText( ILabel label, String text ) {
    if (text == null && label.getText() == null || text != null && text.compareTo(label.getText()) == 0) {
      return;
    }
    if (!contains(label)) {
      throw new IllegalArgumentException("Parameter 'label': Label is not in this graph.");
    }

    if (label instanceof AggregationLabel) {
      AggregationLabel aggregationLabel = (AggregationLabel)label;
      String oldText = label.getText();
      aggregationLabel.setText(text);
      onLabelTextChanged(new ItemChangedEventArgs<ILabel, String>(label, oldText));
    } else {
      super.setLabelText(label, text);
    }
  }

  @Override
  public void setLabelPreferredSize( ILabel label, SizeD preferredSize ) {
    if (label.getPreferredSize().equals(preferredSize)) {
      return;
    }
    if (!contains(label)) {
      throw new IllegalArgumentException("Parameter 'label': Label is not in this graph.");
    }
    if (Double.isNaN(preferredSize.width) || Double.isNaN(preferredSize.height)) {
      throw new IllegalArgumentException("Parameter 'preferredSize': The size must not contain a NaN value.");
    }

    if (label instanceof AggregationLabel) {
      AggregationLabel aggregationLabel = (AggregationLabel)label;
      SizeD oldPreferredSize = label.getPreferredSize();
      aggregationLabel.setPreferredSize(preferredSize);
      onLabelPreferredSizeChanged(new ItemChangedEventArgs<ILabel, SizeD>(label, oldPreferredSize));
    } else {
      super.setLabelPreferredSize(label, preferredSize);
    }
  }

  @Override
  public void setLabelLayoutParameter( ILabel label, ILabelModelParameter layoutParameter ) {
    if (label.getLayoutParameter() == layoutParameter) {
      return;
    }
    if (!contains(label)) {
      throw new IllegalArgumentException("Parameter 'label': Label does not belong to this graph.");
    }
    if (layoutParameter == null) {
      throw new IllegalArgumentException("Parameter locationParameter may not be null.");
    }
    if (!layoutParameter.supports(label)) {
      throw new IllegalArgumentException("Parameter 'layoutParameter': The parameter does not support the label.");
    }

    if (label instanceof AggregationLabel) {
      AggregationLabel aggregationLabel = (AggregationLabel)label;
      ILabelModelParameter oldParameter = label.getLayoutParameter();
      aggregationLabel.setLayoutParameter(layoutParameter);
      onLabelLayoutParameterChanged(new ItemChangedEventArgs<ILabel, ILabelModelParameter>(label, oldParameter));
    } else {
      super.setLabelLayoutParameter(label, layoutParameter);
    }
  }

  @Override
  public void setStyle( INode node, INodeStyle style ) {
    if (!contains(node)) {
      throw new IllegalArgumentException("Parameter 'node': Node is not in this graph.");
    }

    if (node instanceof AggregationNode) {
      AggregationNode aggregationNode = (AggregationNode)node;
      if (aggregationNode.getStyle() != style) {
        INodeStyle oldStyle = aggregationNode.getStyle();
        aggregationNode.setStyle(style);
        onNodeStyleChanged(new ItemChangedEventArgs<INode, INodeStyle>(node, oldStyle));
      }
    } else {
      super.setStyle(node, style);
    }
  }

  @Override
  public void setStyle( ILabel label, ILabelStyle style ) {
    if (!contains(label)) {
      throw new IllegalArgumentException("Parameter 'label': Label is not in this graph.");
    }

    if (label instanceof AggregationLabel) {
      AggregationLabel aggregationLabel = (AggregationLabel)label;
      if (aggregationLabel.getStyle() != style) {
        ILabelStyle oldStyle = aggregationLabel.getStyle();
        aggregationLabel.setStyle(style);
        onLabelStyleChanged(new ItemChangedEventArgs<ILabel, ILabelStyle>(label, oldStyle));
      }
    } else {
      super.setStyle(label, style);
    }
  }

  @Override
  public void setStyle( IEdge edge, IEdgeStyle style ) {
    if (!contains(edge)) {
      throw new IllegalArgumentException("Parameter 'edge': Edge is not in this graph.");
    }

    if (edge instanceof AggregationEdge) {
      AggregationEdge aggregationEdge = (AggregationEdge)edge;
      if (aggregationEdge.getStyle() != style) {
        IEdgeStyle oldStyle = aggregationEdge.getStyle();
        aggregationEdge.setStyle(style);
        onEdgeStyleChanged(new ItemChangedEventArgs<IEdge, IEdgeStyle>(edge, oldStyle));
      }
    } else {
      super.setStyle(edge, style);
    }
  }

  @Override
  public void setStyle( IPort port, IPortStyle style ) {
    if (!contains(port)) {
      throw new IllegalArgumentException("Parameter 'port': Port is not in this graph.");
    }

    if (port instanceof AggregationPort) {
      AggregationPort aggregationPort = (AggregationPort)port;
      if (aggregationPort.getStyle() != style) {
        IPortStyle oldStyle = aggregationPort.getStyle();
        aggregationPort.setStyle(style);
        onPortStyleChanged(new ItemChangedEventArgs<IPort, IPortStyle>(port, oldStyle));
      }
    } else {
      super.setStyle(port, style);
    }
  }

  @Override
  public IListEnumerable<INode> getChildren( final INode node ) {
    if (node == null) {
      // top-level nodes
      return IListEnumerable.create(getNodes().stream().filter(n -> getParent(n) == null).collect(Collectors.toList()));
    }

    if (!contains(node)) {
      throw new IllegalArgumentException("Parameter 'node': Node is not in this graph.");
    }

    if (node instanceof AggregationNode) {
      AggregationNode aggregationNode = (AggregationNode)node;
      return aggregationNode.children != null ? IListEnumerable.create(aggregationNode.children) : IListEnumerable.EMPTY;
    }

    return IListEnumerable.create(Stream.concat(super.getChildren(node).stream(),
        aggregationNodes.stream()
            .filter(n -> n.parent == node))
        .collect(Collectors.toList()));
  }

  @Override
  public INode getParent( final INode node ) {
    if (!contains(node)) {
      throw new IllegalArgumentException("Parameter 'node': Node is not in this graph.");
    }

    if (node instanceof AggregationNode) {
      return ((AggregationNode)node).parent;
    }

    AggregationNode aggregationNodeParent = aggregationNodes.stream()
        .filter(parent -> parent.children != null && parent.children.contains(node))
        .findFirst().orElse(null);
    if (aggregationNodeParent != null) {
      return aggregationNodeParent;
    }

    return super.getParent(node);
  }

  @Override
  public void setParent( INode node, INode parent ) {
    if (!contains(node)) {
      throw new IllegalArgumentException("Parameter 'node': Node is not in this graph.");
    }
    if (parent != null && !contains(parent)) {
      throw new IllegalArgumentException("Parameter 'parent': Parent is not in this graph.");
    }

    INode oldParent = getParent(node);
    AggregationNode oldAggregationParent = (oldParent instanceof AggregationNode) ? (AggregationNode)oldParent : null;
    if (oldAggregationParent != null && oldAggregationParent.children != null) {
      oldAggregationParent.children.remove(node);
    }

    if (node instanceof AggregationNode || parent instanceof AggregationNode) {
      if (!(node instanceof AggregationNode) && !(oldParent instanceof AggregationNode)) {
        // if neither node nor oldParent are AggregationNode, notify WrappedGraph that this relationship is no longer
        // valid
        super.setParent(node, null);
      }

      if (!isGroupNode(parent)) {
        setIsGroupNode(parent, true);
      }

      if (node instanceof AggregationNode) {
        ((AggregationNode)node).parent = node;
      }
      if (parent instanceof AggregationNode) {
        ((AggregationNode)parent).children.add(node);
      }

      onParentChanged(new NodeEventArgs(node, oldParent, isGroupNode(node)));
    } else {
      super.setParent(node, parent);
    }
  }

  @Override
  public void setIsGroupNode( INode node, boolean isGroupNode ) {
    if (node == null) {
      if (!isGroupNode) {
        throw new IllegalStateException("Cannot make the root a non-group node.");
      }
      // root stays a group node
      return;
    }
    if (!contains(node)) {
      throw new IllegalArgumentException("Parameter 'node': Node is not in this graph.");
    }

    if (node instanceof AggregationNode) {
      AggregationNode aggregationNode = (AggregationNode)node;
      if (isGroupNode && aggregationNode.children == null) {
        aggregationNode.children = new ArrayList<INode>();
        onIsGroupNodeChanged(new NodeEventArgs(node, getParent(node), false));
      } else if (!isGroupNode && aggregationNode.children != null) {
        if (aggregationNode.children.size() > 0) {
          throw new IllegalStateException("Cannot set the type of the node to non-group as long as it has children.");
        }
        aggregationNode.children = null;
        onIsGroupNodeChanged(new NodeEventArgs(node, getParent(node), true));
      }
    } else {
      super.setIsGroupNode(node, isGroupNode);
    }
  }

  @Override
  public boolean isGroupNode( INode node ) {
    if (node == null) {
      // null represents the root which is always a group node 
      return true;
    }

    if (!contains(node)) {
      throw new IllegalArgumentException("Parameter 'node': Node is not in this graph");
    }

    if (node instanceof AggregationNode) {
      return ((AggregationNode)node).children == null;
    }
    return super.isGroupNode(node);
  }

  @Override
  public IEdge createEdge( INode source, INode target, IEdgeStyle style, Object tag ) {
    if (!contains(source)) {
      throw new IllegalArgumentException("Parameter 'source': Cannot create edge from a node that doesn't belong to this graph.");
    }
    if (!contains(target)) {
      throw new IllegalArgumentException("Parameter 'target': Cannot create edge to a node that doesn't belong to this graph.");
    }

    if (source instanceof AggregationNode || target instanceof AggregationNode) {
      IPort sourcePort = addPort(source);
      IPort targetPort = addPort(target);
      return createEdge(sourcePort, targetPort, style, tag);
    }
    return super.createEdge(source, target, style, tag);
  }

  @Override
  public IEdge createEdge( IPort sourcePort, IPort targetPort, IEdgeStyle style, Object tag ) {
    if (!contains(sourcePort)) {
      throw new IllegalArgumentException("Parameter 'sourcePort': Cannot create edge from a port that doesn't belong to this graph.");
    }
    if (!contains(targetPort)) {
      throw new IllegalArgumentException("Parameter 'targetPort': Cannot create edge to a port that doesn't belong to this graph.");
    }

    if (sourcePort instanceof AggregationPort || targetPort instanceof AggregationPort) {
      AggregationEdge aggregationEdge = new AggregationEdge(this, new ArrayList<IEdge>());
      aggregationEdge.setSourcePort(sourcePort);
      aggregationEdge.setTargetPort(targetPort);
      aggregationEdge.setStyle(style != null ? style : getAggregationEdgeDefaults().getStyleInstance());
      aggregationEdge.setTag(tag);
      aggregationEdges.add(aggregationEdge);
      onEdgeCreated(new ItemEventArgs<IEdge>(aggregationEdge));
      return aggregationEdge;
    }
    return super.createEdge(sourcePort, targetPort, style, tag);
  }

  /**
   * Does not raise EdgeCreated event!!.
   */
  private AggregationEdge createAggregationEdge( IPort sourcePort, IPort targetPort, Object tag ) {
    AggregationEdge aggregationEdge = new AggregationEdge(this, new ArrayList<IEdge>());
    aggregationEdge.setSourcePort(sourcePort);
    aggregationEdge.setTargetPort(targetPort);
    aggregationEdge.setStyle(getAggregationEdgeDefaults().getStyleInstance());
    aggregationEdge.setTag(tag);
    aggregationEdges.add(aggregationEdge);
    return aggregationEdge;
  }



  @Override
  public <TLookup> TLookup lookup( Class<TLookup> type ) {
    return (TLookup)lookupDecorator.lookup(type);
  }

  public final void addLookup( IContextLookupChainLink lookup ) {
    lookupDecorator.addLookup(IGraph.class, lookup);
  }

  public final void removeLookup( IContextLookupChainLink lookup ) {
    lookupDecorator.removeLookup(IGraph.class, lookup);
  }

  private Object baseLookup( Class type ) {
    return super.lookup(type);
  }

  private Object delegateLookup( AggregationItem aggregationItem, Class type ) {
    return lookupDecorator.delegateLookup(aggregationItem, type);
  }

  /**
   * An ILookupDecorator implementation that contains its own lookup chains.
   * <p>
   * New chain links are added to the chains of this decorator as well as to the decorator of the
   * {@link AbstractGraphWrapper#getWrappedGraph() WrappedGraph}.
   * </p>
   */
  private static final class AggregateLookupDecorator implements ILookup, ILookupDecorator {
    private ILookupDecorator wrappedDecorator;

    private final AggregateGraphWrapper graph;

    private final LookupChain graphLookupChain = new LookupChain();

    private final LookupChain nodeLookupChain = new LookupChain();

    private final LookupChain edgeLookupChain = new LookupChain();

    private final LookupChain bendLookupChain = new LookupChain();

    private final LookupChain portLookupChain = new LookupChain();

    private final LookupChain labelLookupChain = new LookupChain();

    public AggregateLookupDecorator( AggregateGraphWrapper graph ) {
      this.graph = graph;

      graphLookupChain.add(new AggregateGraphWrapper.GraphFallBackLookup());

      nodeLookupChain.add(new AggregateGraphWrapper.ItemFallBackLookup());
      nodeLookupChain.add(new AggregateGraphWrapper.ItemDefaultLookup(DefaultGraph.getDefaultNodeLookup()));
      nodeLookupChain.add(new AggregateGraphWrapper.BlockReshapeAndPositionHandlerLookup());

      edgeLookupChain.add(new AggregateGraphWrapper.ItemFallBackLookup());
      edgeLookupChain.add(new AggregateGraphWrapper.ItemDefaultLookup(DefaultGraph.getDefaultEdgeLookup()));

      bendLookupChain.add(new AggregateGraphWrapper.ItemFallBackLookup());
      bendLookupChain.add(new AggregateGraphWrapper.ItemDefaultLookup(DefaultGraph.getDefaultBendLookup()));

      portLookupChain.add(new AggregateGraphWrapper.ItemFallBackLookup());
      portLookupChain.add(new AggregateGraphWrapper.ItemDefaultLookup(DefaultGraph.getDefaultPortLookup()));

      labelLookupChain.add(new AggregateGraphWrapper.ItemFallBackLookup());
      labelLookupChain.add(new AggregateGraphWrapper.ItemDefaultLookup(DefaultGraph.getDefaultLabelLookup()));
    }

    public final boolean canDecorate( Class t ) {
      if (t == INode.class || t == IEdge.class || t == IBend.class || t == IPort.class || t == ILabel.class || t == IModelItem.class || t == IGraph.class) {
        return wrappedDecorator == null || wrappedDecorator.canDecorate(t);
      }
      return false;
    }

    public final void addLookup( Class t, IContextLookupChainLink lookup ) {
      if (t == INode.class) {
        nodeLookupChain.add(lookup);
      } else if (t == IEdge.class) {
        edgeLookupChain.add(lookup);
      } else if (t == IBend.class) {
        bendLookupChain.add(lookup);
      } else if (t == IPort.class) {
        portLookupChain.add(lookup);
      } else if (t == ILabel.class) {
        labelLookupChain.add(lookup);
      } else if (t == IGraph.class) {
        graphLookupChain.add(lookup);
      } else {
        throw new IllegalArgumentException("Cannot decorate type " + t);
      }

      if (wrappedDecorator != null) {
        wrappedDecorator.addLookup(t, lookup);
      }
    }

    public final void removeLookup( Class t, IContextLookupChainLink lookup ) {
      if (t == INode.class) {
        nodeLookupChain.remove(lookup);
      } else if (t == IEdge.class) {
        edgeLookupChain.remove(lookup);
      } else if (t == IBend.class) {
        bendLookupChain.remove(lookup);
      } else if (t == IPort.class) {
        portLookupChain.remove(lookup);
      } else if (t == ILabel.class) {
        labelLookupChain.remove(lookup);
      } else if (t == IGraph.class) {
        graphLookupChain.remove(lookup);
      }

      if (wrappedDecorator != null) {
        wrappedDecorator.removeLookup(t, lookup);
      }
    }

    public final <TLookup> TLookup lookup( Class<TLookup> type ) {
      if (type == ILookupDecorator.class) {
        wrappedDecorator = (ILookupDecorator)graph.baseLookup(type);
        return (TLookup)this;
      }
      if (type == LookupChain.class) {
        return (TLookup)graphLookupChain;
      }

      ILookup lookup = graph.getLookup();
      if (lookup != null) {
        return (TLookup)lookup.lookup(type);
      }

      return (TLookup)graphLookupChain.lookup(graph, type);
    }

    public final Object delegateLookup( IModelItem item, Class type ) {
      if (item instanceof INode) {
        return nodeLookupChain.lookup(item, type);
      } else if (item instanceof IEdge) {
        return edgeLookupChain.lookup(item, type);
      } else if (item instanceof ILabel) {
        return labelLookupChain.lookup(item, type);
      } else if (item instanceof IBend) {
        return bendLookupChain.lookup(item, type);
      } else if (item instanceof IPort) {
        return portLookupChain.lookup(item, type);
      } else {
        return null;
      }
    }

  }

  private static final class GraphFallBackLookup extends ContextLookupChainLinkBase {
    @Override
    public Object lookup( Object item, Class type ) {
      Object result = ((AggregateGraphWrapper) item).baseLookup(type);
      if (result == null) {
        result = super.lookup(item, type);
      }
      return result;
    }

  }

  private static final class ItemFallBackLookup extends ContextLookupChainLinkBase {
    @Override
    public Object lookup( Object item, Class type ) {
      Object result = ((AggregateGraphWrapper.AggregationItem) item).innerLookup(type);
      if (result == null) {
        result = super.lookup(item, type);
      }
      return result;
    }

  }

  private static final class BlockReshapeAndPositionHandlerLookup extends ContextLookupChainLinkBase {
    @Override
    public Object lookup( Object item, Class type ) {
      // The default implementations of IPositionHandler and IReshapeHandler don't support AggregationNode, which is
      // why moving and reshaping such nodes is not supported by default.
      if (type == IPositionHandler.class || type == IReshapeHandler.class) {
        return null;
      }
      return super.lookup(item, type);
    }

  }

  private static final class ItemDefaultLookup extends ContextLookupChainLinkBase {
    private final IContextLookup defaultLookup;

    public ItemDefaultLookup( IContextLookup defaultLookup ) {
      this.defaultLookup = defaultLookup;
    }

    @Override
    public Object lookup( Object item, Class type ) {
      Object result = defaultLookup.lookup(item, type);
      if (result == null) {
        result = super.lookup(item, type);
      }
      return result;
    }

  }

  private abstract static class ContextLookupChainLinkBase implements IContextLookupChainLink {
    private IContextLookup nextLink;

    public Object lookup( Object item, Class type ) {
      return nextLink != null ? nextLink.lookup(item, type) : null;
    }

    public final void setNext( IContextLookup next ) {
      this.nextLink = next;
    }

  }



  private void onTagChanged( IModelItem item, Object oldTag ) {
    if (item instanceof INode) {
      onNodeTagChanged(new ItemChangedEventArgs<INode, Object>((INode)item, oldTag));
    } else if (item instanceof IEdge) {
      onEdgeTagChanged(new ItemChangedEventArgs<IEdge, Object>((IEdge)item, oldTag));
    } else if (item instanceof ILabel) {
      onLabelTagChanged(new ItemChangedEventArgs<ILabel, Object>((ILabel)item, oldTag));
    } else if (item instanceof IPort) {
      onPortTagChanged(new ItemChangedEventArgs<IPort, Object>((IPort)item, oldTag));
    } else if (item instanceof IBend) {
      onBendTagChanged(new ItemChangedEventArgs<IBend, Object>((IBend)item, oldTag));
    }
  }

  /**
   * A simple INode implementation for aggregation nodes.
   */
  private static final class AggregationNode extends AggregationLabelPortOwner implements INode {
    final List<INode> aggregatedNodes;

    IMutableRectangle layout;

    List<INode> children;

    INode parent;

    public AggregationNode( AggregateGraphWrapper graph, List<INode> aggregatedNodes ) {
      super(graph);
      this.aggregatedNodes = aggregatedNodes;
    }

    public final IRectangle getLayout() {
      return layout;
    }

    private INodeStyle style;

    public final INodeStyle getStyle() {
      return this.style;
    }

    final void setStyle( INodeStyle value ) {
      this.style = value;
    }

    @Override
    public Object innerLookup( Class type ) {
      if (type == INodeStyle.class) {
        return getStyle();
      }
      if (type.isInstance(layout)) {
        return layout;
      }
      return super.innerLookup(type);
    }

    @Override
    public String toString() {
      return getLabels().size() > 0 ? super.toString() : "Aggregation Node (" + aggregatedNodes.size() + ") [" + layout.getX() + ", " + layout.getY() + ", " + layout.getWidth() + ", " + layout.getHeight() + "]";
    }

  }

  /**
   * A simple IEdge implementation for aggregation edges.
   */
  private static final class AggregationEdge extends AggregationLabelPortOwner implements IEdge {
    final List<IEdge> aggregatedEdges;

    final List<AggregateGraphWrapper.AggregationBend> bends = new ArrayList<AggregateGraphWrapper.AggregationBend>();

    private IListEnumerable<IBend> bendEnumerable;

    public final IListEnumerable<IBend> getBends() {
      if (bendEnumerable == null) {
        bendEnumerable = IListEnumerable.create((List) bends);
      }
      return bendEnumerable;
    }

    private IPort sourcePort;

    public final IPort getSourcePort() {
      return this.sourcePort;
    }

    final void setSourcePort( IPort value ) {
      this.sourcePort = value;
    }

    private IPort targetPort;

    public final IPort getTargetPort() {
      return this.targetPort;
    }

    final void setTargetPort( IPort value ) {
      this.targetPort = value;
    }

    private IEdgeStyle style;

    public final IEdgeStyle getStyle() {
      return this.style;
    }

    final void setStyle( IEdgeStyle value ) {
      this.style = value;
    }

    public AggregationEdge( AggregateGraphWrapper graph, List<IEdge> aggregatedEdges ) {
      super(graph);
      this.aggregatedEdges = aggregatedEdges;
    }

    @Override
    public Object innerLookup( Class type ) {
      if (type == IEdgeStyle.class) {
        return getStyle();
      }
      if (type.isInstance(bends)) {
        return bends;
      }
      return super.innerLookup(type);
    }

    @Override
    public String toString() {
      if (labels.size() > 0) {
        return super.toString();
      } else {
        if ((getSourcePort() != null && getSourcePort().getOwner() instanceof IEdge) || (getTargetPort() != null && getTargetPort().getOwner() instanceof IEdge)) {
          return "Aggregation Edge [ At another Edge ]";
        } else {
          return "Aggregation Edge (" + aggregatedEdges.size() + ") [" + getSourcePort() + " -> " + getTargetPort() + "]";
        }
      }
    }

  }

  /**
   * A simple IBend implementation for bends of {@link AggregationEdge}s.
   */
  private static final class AggregationBend extends AggregationItem implements IBend {
    AggregateGraphWrapper.AggregationEdge owner;

    IMutablePoint location;

    public final IEdge getOwner() {
      return owner;
    }

    public final IPoint getLocation() {
      return location;
    }

    public AggregationBend( AggregateGraphWrapper graph ) {
      super(graph);
    }

    @Override
    public Object innerLookup( Class type ) {
      if (type.isInstance(location)) {
        return location;
      }
      return super.innerLookup(type);
    }

    @Override
    public String toString() {
      return "Aggregation Bend [" + location.getX() + ", " + location.getY() + "]";
    }

  }

  /**
   * A simple IPort implementation for ports of {@link AggregationLabelPortOwner}.
   */
  private static final class AggregationPort extends AggregationLabelOwner implements IPort {
    AggregateGraphWrapper.AggregationLabelPortOwner owner;

    public final IPortOwner getOwner() {
      return owner;
    }

    private IPortStyle style;

    public final IPortStyle getStyle() {
      return this.style;
    }

    final void setStyle( IPortStyle value ) {
      this.style = value;
    }

    private IPortLocationModelParameter locationParameter;

    public final IPortLocationModelParameter getLocationParameter() {
      return this.locationParameter;
    }

    final void setLocationParameter( IPortLocationModelParameter value ) {
      this.locationParameter = value;
    }

    public AggregationPort( AggregateGraphWrapper graph ) {
      super(graph);
    }

    @Override
    public Object innerLookup( Class type ) {
      if (type == IPortStyle.class) {
        return getStyle();
      }
      if (type == IPortLocationModelParameter.class) {
        return getLocationParameter();
      }
      if (type == IPoint.class) {
        return this.getDynamicLocation();
      }
      return super.innerLookup(type);
    }

    @Override
    public String toString() {
      String text = "Aggregation Port [";

      try {
        text += "Location: " + this.getLocation() + "; ";
      }catch (RuntimeException e) {
        // ignored
      }
      return text + "Parameter: " + getLocationParameter() + "; Owner: " + owner + "]";
    }

  }

  /**
   * A simple ILabel implementation for labels of {@link AggregationLabelOwner}.
   */
  private static final class AggregationLabel extends AggregationItem implements ILabel {
    AggregateGraphWrapper.AggregationLabelOwner owner;

    private ILabelStyle style;

    public final ILabelStyle getStyle() {
      return this.style;
    }

    final void setStyle( ILabelStyle value ) {
      this.style = value;
    }

    private SizeD preferredSize = new SizeD();

    public final SizeD getPreferredSize() {
      return this.preferredSize;
    }

    final void setPreferredSize( SizeD value ) {
      this.preferredSize = value;
    }

    public final ILabelOwner getOwner() {
      return owner;
    }

    private String text;

    public final String getText() {
      return this.text;
    }

    final void setText( String value ) {
      this.text = value;
    }

    private ILabelModelParameter layoutParameter;

    public final ILabelModelParameter getLayoutParameter() {
      return this.layoutParameter;
    }

    final void setLayoutParameter( ILabelModelParameter value ) {
      this.layoutParameter = value;
    }

    public AggregationLabel( AggregateGraphWrapper graph ) {
      super(graph);
    }

    @Override
    public Object innerLookup( Class type ) {
      if (type == ILabelStyle.class) {
        return getStyle();
      }
      if (type == ILabelModelParameter.class) {
        return getLayoutParameter();
      }
      return super.innerLookup(type);
    }

    @Override
    public String toString() {
      return "Aggregation Label [\"" + getText() + "\"; Owner: " + owner + "]";
    }

  }

  private abstract static class AggregationLabelPortOwner extends AggregationLabelOwner implements IPortOwner {
    final List<IPort> ports = new ArrayList<IPort>();

    private IListEnumerable<IPort> enumerable;

    public final IListEnumerable<IPort> getPorts() {
      if (enumerable == null) {
        enumerable = IListEnumerable.create(ports);
      }
      return enumerable;
    }

    protected AggregationLabelPortOwner( AggregateGraphWrapper graph ) {
      super(graph);
    }

  }

  private abstract static class AggregationLabelOwner extends AggregationItem implements ILabelOwner {
    final List<ILabel> labels = new ArrayList<ILabel>();

    private IListEnumerable<ILabel> enumerable;

    public final IListEnumerable<ILabel> getLabels() {
      if (enumerable == null) {
        enumerable = IListEnumerable.create(labels);
      }
      return enumerable;
    }

    protected AggregationLabelOwner( AggregateGraphWrapper graph ) {
      super(graph);
    }

    @Override
    public String toString() {
      return labels.size() > 0 ? labels.get(0).getText() : "ILabelOwner";
    }

  }

  private abstract static class AggregationItem implements IModelItem {
    AggregateGraphWrapper graph;

    private Object tag;

    protected AggregationItem( AggregateGraphWrapper graph ) {
      this.graph = graph;
    }

    public final Object getTag() {
      return tag;
    }

    public final void setTag( Object value ) {
      Object oldTag = tag;
      tag = value;
      if (graph != null) {
        graph.onTagChanged(this, oldTag);
      }
    }

    public final <TLookup> TLookup lookup( Class<TLookup> type ) {
      return (TLookup)(graph != null ? graph.delegateLookup(this, type) : null);
    }

    public Object innerLookup( Class type ) {
      if (type.isInstance(this)) {
        return this;
      }
      return null;
    }

  }

}
