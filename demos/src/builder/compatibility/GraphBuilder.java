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
package builder.compatibility;

import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.builder.EdgesSource;
import com.yworks.yfiles.graph.builder.NodesSource;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.IEventListener;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Populates a graph from custom data.
 * <p>
 * This class can be used when the data specifies a collection of nodes, a collection of edges,
 * and&#x2014;optionally&#x2014;a collection of groups. The properties {@link #getNodesSource() NodesSource}, {@link
 * #getGroupsSource() GroupsSource}, and {@link #getEdgesSource() EdgesSource} define the source collections from which
 * nodes, groups, and edges will be created.
 * </p>
 * <p>
 * Generally, using the {@link GraphBuilder} class consists of a few basic steps:
 * </p>
 * <ol>
 * <li>Set up the {@link #getGraph() Graph} with the proper defaults for items ({@link IGraph#getNodeDefaults() NodeDefaults},
 * {@link IGraph#getGroupNodeDefaults() GroupNodeDefaults}, {@link IGraph#getEdgeDefaults() EdgeDefaults})</li>
 * <li>Create a {@link GraphBuilder}.</li>
 * <li>Set the items sources. At the very least the {@link #getNodesSource() NodesSource} (unless using
 * {@link #isLazyNodeDefinitionEnabled() LazyNodeDefinitionEnabled}) and {@link #getEdgesSource() EdgesSource} are
 * needed. If the items in the nodes collection are grouped somehow, then also set the {@link #getGroupsSource() GroupsSource}
 * property.</li>
 * <li>Set up the bindings so that a graph structure can actually be created from the items sources. This involves at least
 * setting up the {@link #getSourceNodeProvider() SourceNodeProvider} and {@link #getTargetNodeProvider() TargetNodeProvider}
 * properties so that edges can be created. If the edge objects don't actually contain the node objects as source and
 * target, but instead an identifier of the node objects, then {@link #getSourceNodeProvider() SourceNodeProvider} and {@link #getTargetNodeProvider() TargetNodeProvider}
 * would return those identifiers and {@link #getNodeIdProvider() NodeIdProvider} must be set to return that identifier
 * when given a node object.</li>
 * <li>If {@link #getGroupsSource() GroupsSource} is set, then you also need to set the {@link #getGroupProvider() GroupProvider}
 * property to enable mapping nodes to groups. Just like with edges and their source and target nodes, if the node object
 * only contains an identifier for a group node and not the actual group object, then return the identifier in the {@link #getGroupProvider() GroupProvider}
 * and set up the {@link #getGroupIdProvider() GroupIdProvider} to map group node objects to their identifiers. If group
 * nodes can nest, you also need the {@link #getParentGroupProvider() ParentGroupProvider}.</li>
 * <li>You can also easily create labels for nodes, groups, and edges by using the
 * {@link #getNodeLabelProvider() NodeLabelProvider}, {@link #getGroupLabelProvider() GroupLabelProvider}, and {@link #getEdgeLabelProvider() EdgeLabelProvider}
 * properties.</li>
 * <li>Call {@link #buildGraph()} to populate the graph. You can apply a layout algorithm afterward to make the graph look
 * nice.</li>
 * <li>If your items or collections change later, call {@link #updateGraph()} to make those changes visible in the graph.</li>
 * </ol>
 * <p>
 * You can further customize how nodes, groups, and edges are created by adding event handlers to the various events and
 * modifying the items there. This can be used to modify items in ways that are not directly supported by the available
 * bindings or defaults. This includes scenarios such as the following:
 * </p>
 * <ul>
 * <li>Setting node positions or adding bends to edges. Often a layout is applied to the graph after building it, so these
 * things are only rarely needed.</li>
 * <li>Modifying individual items, such as setting a different style for every nodes, depending on the bound object.</li>
 * <li>Adding more than one label for an item, as the {@link #getNodeLabelProvider() NodeLabelProvider} and {@link #getEdgeLabelProvider() EdgeLabelProvider}
 * will only create a single label, or adding labels to group nodes.</li>
 * </ul>
 * <p>
 * There are creation and update events for all three types of items, which allows separate customization when nodes,
 * groups, and edges are created or updated. For completely static graphs where {@link #updateGraph()} is not needed, the
 * update events can be safely ignored.
 * </p>
 * <p>
 * Depending on how the source data is laid out, you may also consider using {@link AdjacentNodesGraphBuilder}, where node
 * objects know their neighbors, or {@link TreeBuilder} where the graph is a tree and node objects know their children.
 * Both of those other graph builders make edges implicit through the relationships between nodes and thus have no
 * {@link #getEdgesSource() EdgesSource}.
 * </p>
 * <p>
 * The different graph builders are discussed in the section in the section
 * <a href="@DGUIDE_PREFIX@/graph_source.html">Creating a Graph from Business Data</a>. Class {@code GraphBuilder}, in
 * particular, is topic of section <a href="@DGUIDE_PREFIX@/graph_source-GraphBuilder">GraphBuilder</a>.
 * </p>
 *
 * @param <TNode>  The type of object nodes are created from. This type must implement {@link Object#equals(Object)} and
 *                 {@link Object#hashCode()} properly.
 * @param <TEdge>  The type of object edges are created from. This type must implement {@link Object#equals(Object)} and
 *                 {@link Object#hashCode()} properly.
 * @param <TGroup> The type of object group nodes are created from. This type must implement {@link
 *                 Object#equals(Object)} and {@link Object#hashCode()} properly.
 * @y.note This class serves as a convenient way to create general graphs and has some limitations:
 * <ul>
 * <li>When populating the graph for the first time it will be cleared of all existing items.</li>
 * <li>Elements manually created on the graph in between calls to {@link #updateGraph()} may not be preserved.</li>
 * <li>Edge objects in {@link #getEdgesSource() EdgesSource} cannot change their source or target node. {@link #getSourceNodeProvider() SourceNodeProvider}
 * and {@link #getTargetNodeProvider() TargetNodeProvider} are only used during edge creation.</li>
 * </ul>
 * <p>
 * If updates get too complex it's often better to write the code interfacing with the graph by hand instead of relying on
 * {@link GraphBuilder}.
 * </p>
 * @y.note {@link #getGroupProvider() GroupProvider} may also return objects or IDs corresponding to normal nodes
 * instead of items from {@link #getGroupsSource() GroupsSource} (and in fact, {@link #getGroupsSource() GroupsSource}
 * does not have to be set at all). This enables convenient use of group nodes when any node can also become a group
 * node at a later time. However, the feature comes with a few caveats:
 * <ul>
 * <li>Nodes are converted to group nodes automatically as soon as another node has that node as a parent via
 * {@link #getGroupProvider() GroupProvider}. The node's style is automatically changed to the graph's default style
 * for group nodes. Group nodes created this way will never be changed back to normal nodes, so the result on the graph
 * after several changes in data and calls to {@link #updateGraph()} can be different than a fresh {@link #buildGraph()}
 * with the same data.</li>
 * <li>{@link #isLazyNodeDefinitionEnabled() LazyNodeDefinitionEnabled} is not supported for
 * {@link #getGroupProvider() GroupProvider}.</li>
 * <li>If {@link #getGroupProvider() GroupProvider} references a node that will never exist, the resulting graph may have wrong
 * parent/child relationships from what the backing data specifies. Those can in some cases be corrected by calling {@link #updateGraph()}
 * once more.</li>
 * </ul>
 * @see AdjacentNodesGraphBuilder
 * @see TreeBuilder
 */
public class GraphBuilder<TNode, TEdge, TGroup> {
  // the GraphBuilder to delegate to
  private final com.yworks.yfiles.graph.builder.GraphBuilder graphBuilder;
  private final NodesSource<TNode> builderNodesSource;
  private final NodesSource<TGroup> builderGroupsSource;
  private final EdgesSource<TEdge> builderEdgesSource;

  private final GraphBuilderHelper<TNode, TGroup, TEdge> graphBuilderHelper;
  private Function<TGroup, Object> parentGroupProvider;
  private Function<TGroup, Object> groupIdProvider;
  private Function<TNode, Object> groupProvider;
  private Function<TNode, Object> nodeIdProvider;
  private Iterable<TGroup> groupsSource;
  private Iterable<TNode> nodesSource;
  private boolean lazyNodeDefinition;

  /**
   * Initializes a new instance of the {@link GraphBuilder} class that operates on the given graph.
   * <p>
   * The {@code graph} will be {@link IGraph#clear() cleared} and re-built from the data in {@link #getNodesSource()
   * NodesSource}, {@link #getGroupsSource() GroupsSource}, and {@link #getEdgesSource() EdgesSource} when {@link
   * #buildGraph()} is called.
   * </p>
   */
  public GraphBuilder() {
    this(null);
  }

  /**
   * Initializes a new instance of the {@link GraphBuilder} class that operates on the given graph.
   * <p>
   * The {@code graph} will be {@link IGraph#clear() cleared} and re-built from the data in {@link #getNodesSource()
   * NodesSource}, {@link #getGroupsSource() GroupsSource}, and {@link #getEdgesSource() EdgesSource} when {@link
   * #buildGraph()} is called.
   * </p>
   */
  public GraphBuilder(IGraph graph) {
    graph = graph != null ? graph : new DefaultGraph();
    graphBuilderHelper = new GraphBuilderHelper<TNode, TGroup, TEdge>(
        graph,
        this::createNode,
        this::updateNode,
        this::createGroupNode,
        this::updateGroupNode,
        this::createEdge,
        this::updateEdge);
    this.graphBuilder = new com.yworks.yfiles.graph.builder.GraphBuilder(graph);

    this.builderNodesSource = this.graphBuilder.createNodesSource((Iterable<TNode>) IEnumerable.EMPTY);
    this.builderNodesSource.setNodeCreator(this.graphBuilderHelper.createNodeCreator());

    this.builderGroupsSource = this.graphBuilder.createGroupNodesSource((Iterable<TGroup>) IEnumerable.EMPTY);
    this.builderGroupsSource.setNodeCreator(this.graphBuilderHelper.createGroupCreator());

    this.builderEdgesSource = this.graphBuilder.createEdgesSource((Iterable<TEdge>) IEnumerable.EMPTY,
        item -> this.getSourceNodeProvider() != null ? this.getSourceNodeProvider().apply(item) : null,
        item -> this.getTargetNodeProvider() != null ? this.getTargetNodeProvider().apply(item) : null
    );
    this.builderEdgesSource.setEdgeCreator(this.graphBuilderHelper.createEdgeCreator());
  }

  /**
   * Gets the {@link IGraph graph} used by this class.
   *
   * @return The Graph.
   */
  public final IGraph getGraph() {
    return graphBuilder.getGraph();
  }

  /**
   * Gets a value indicating whether or not to automatically create nodes for values returned from {@link
   * #getSourceNodeProvider() SourceNodeProvider} and {@link #getTargetNodeProvider() TargetNodeProvider} that don't
   * exist in {@link #getNodesSource() NodesSource}.
   * <p>
   * When this property is set to {@code false}, nodes in the graph are <em>only</em> created from {@link
   * #getNodesSource() NodesSource}, and edge objects that result in source or target nodes not in {@link
   * #getNodesSource() NodesSource} will have no edge created.
   * </p>
   * <p>
   * If this property is set to {@code true}, edges will always be created, and if {@link #getSourceNodeProvider()
   * SourceNodeProvider} or {@link #getTargetNodeProvider() TargetNodeProvider} return values not in {@link
   * #getNodesSource() NodesSource}, additional nodes are created as needed.
   * </p>
   *
   * @return The LazyNodeDefinitionEnabled.
   * @see #getNodesSource()
   * @see #getEdgesSource()
   * @see #setLazyNodeDefinitionEnabled(boolean)
   */
  public final boolean isLazyNodeDefinitionEnabled() {
    return this.lazyNodeDefinition;
  }

  /**
   * Sets a value indicating whether or not to automatically create nodes for values returned from {@link
   * #getSourceNodeProvider() SourceNodeProvider} and {@link #getTargetNodeProvider() TargetNodeProvider} that don't
   * exist in {@link #getNodesSource() NodesSource}.
   * <p>
   * When this property is set to {@code false}, nodes in the graph are <em>only</em> created from {@link
   * #getNodesSource() NodesSource}, and edge objects that result in source or target nodes not in {@link
   * #getNodesSource() NodesSource} will have no edge created.
   * </p>
   * <p>
   * If this property is set to {@code true}, edges will always be created, and if {@link #getSourceNodeProvider()
   * SourceNodeProvider} or {@link #getTargetNodeProvider() TargetNodeProvider} return values not in {@link
   * #getNodesSource() NodesSource}, additional nodes are created as needed.
   * </p>
   *
   * @param value The LazyNodeDefinitionEnabled to set.
   * @see #getNodesSource()
   * @see #getEdgesSource()
   * @see #isLazyNodeDefinitionEnabled()
   */
  public final void setLazyNodeDefinitionEnabled(boolean value) {
    this.lazyNodeDefinition = value;
  }

  /**
   * Gets the objects to be represented as nodes of the {@link #getGraph() Graph}.
   *
   * @return The NodesSource.
   * @see #setNodesSource(Iterable)
   */
  public final Iterable<TNode> getNodesSource() {
    return this.nodesSource;
  }

  /**
   * Sets the objects to be represented as nodes of the {@link #getGraph() Graph}.
   *
   * @param value The NodesSource to set.
   * @see #getNodesSource()
   */
  public final void setNodesSource(Iterable<TNode> value) {
    this.nodesSource = value;
  }

  private Iterable<TEdge> edgesSource;

  /**
   * Gets the objects to be represented as edges of the {@link #getGraph() Graph}.
   *
   * @return The EdgesSource.
   * @see #setEdgesSource(Iterable)
   */
  public final Iterable<TEdge> getEdgesSource() {
    return this.edgesSource;
  }

  /**
   * Sets the objects to be represented as edges of the {@link #getGraph() Graph}.
   *
   * @param value The EdgesSource to set.
   * @see #getEdgesSource()
   */
  public final void setEdgesSource(Iterable<TEdge> value) {
    this.edgesSource = value;
  }

  /**
   * Gets the objects to be represented as group nodes of the {@link #getGraph() Graph}.
   *
   * @return The GroupsSource.
   * @see #setGroupsSource(Iterable)
   */
  public final Iterable<TGroup> getGroupsSource() {
    return this.groupsSource;
  }

  /**
   * Sets the objects to be represented as group nodes of the {@link #getGraph() Graph}.
   *
   * @param value The GroupsSource to set.
   * @see #getGroupsSource()
   */
  public final void setGroupsSource(Iterable<TGroup> value) {
    this.groupsSource = value;
  }

  /**
   * Gets a delegate that maps node objects to their identifier.
   * <p>
   * This maps an object that represents a node to its identifier. This is needed when {@link #getEdgesSource() edge
   * objects} only contain an identifier to specify their source and target nodes instead of pointing directly to the
   * respective node object.
   * </p>
   *
   * @return The NodeIdProvider.
   * @y.warning The identifiers returned by the delegate must be stable and not change over time. Otherwise the {@link
   * #updateGraph() update mechanism} cannot determine whether nodes have been added or updated. For the same reason
   * this property must not be changed after having built the graph once.
   * @see #getNodesSource()
   * @see #getSourceNodeProvider()
   * @see #getTargetNodeProvider()
   * @see #setNodeIdProvider(Function)
   */
  public final Function<TNode, Object> getNodeIdProvider() {
    return this.nodeIdProvider;
  }

  /**
   * Sets a delegate that maps node objects to their identifier.
   * <p>
   * This maps an object that represents a node to its identifier. This is needed when {@link #getEdgesSource() edge
   * objects} only contain an identifier to specify their source and target nodes instead of pointing directly to the
   * respective node object.
   * </p>
   *
   * @param value The NodeIdProvider to set.
   * @y.warning The identifiers returned by the delegate must be stable and not change over time. Otherwise the {@link
   * #updateGraph() update mechanism} cannot determine whether nodes have been added or updated. For the same reason
   * this property must not be changed after having built the graph once.
   * @see #getNodesSource()
   * @see #getSourceNodeProvider()
   * @see #getTargetNodeProvider()
   * @see #getNodeIdProvider()
   */
  public final void setNodeIdProvider(Function<TNode, Object> value) {
    this.nodeIdProvider = value;
  }

  /**
   * Gets a delegate that maps a node object to a label.
   * <p>
   * This maps an object that represents a node to an object that represents the label for the node.
   * </p>
   * <p>
   * The resulting object will be converted into a string to be displayed as the label's text. If this is insufficient,
   * a label can also be created directly in an event handler of the {@link #addNodeCreatedListener(IEventListener)
   * NodeCreated} event.
   * </p>
   * <p>
   * Returning {@code null} from the delegate will not create a label for that node.
   * </p>
   *
   * @return The NodeLabelProvider.
   * @see #getNodesSource()
   * @see #setNodeLabelProvider(Function)
   */
  public final Function<TNode, Object> getNodeLabelProvider() {
    return graphBuilderHelper.nodeLabelProvider;
  }

  /**
   * Sets a delegate that maps a node object to a label.
   * <p>
   * This maps an object that represents a node to an object that represents the label for the node.
   * </p>
   * <p>
   * The resulting object will be converted into a string to be displayed as the label's text. If this is insufficient,
   * a label can also be created directly in an event handler of the {@link #addNodeCreatedListener(IEventListener)
   * NodeCreated} event.
   * </p>
   * <p>
   * Returning {@code null} from the delegate will not create a label for that node.
   * </p>
   *
   * @param value The NodeLabelProvider to set.
   * @see #getNodesSource()
   * @see #getNodeLabelProvider()
   */
  public final void setNodeLabelProvider(Function<TNode, Object> value) {
    graphBuilderHelper.nodeLabelProvider = value;
  }

  /**
   * Gets a delegate that maps node objects to their containing groups.
   * <p>
   * This maps an object <i>N</i> that represents a node to another object <i>G</i> that specifies the containing group
   * of
   * <i>N</i>. If <i>G</i> is contained in {@link #getGroupsSource() GroupsSource}, then the node for <i>N</i> becomes
   * a
   * child node of the group for <i>G</i>.
   * </p>
   * <p>
   * If a {@link #getGroupIdProvider() GroupIdProvider} is set, the returned object <i>G</i> must be the ID of the
   * object that specifies the group instead of that object itself.
   * </p>
   *
   * @return The GroupProvider.
   * @see #getNodesSource()
   * @see #getGroupsSource()
   * @see #getGroupIdProvider()
   * @see #setGroupProvider(Function)
   */
  public final Function<TNode, Object> getGroupProvider() {
    return this.groupProvider;
  }

  /**
   * Sets a delegate that maps node objects to their containing groups.
   * <p>
   * This maps an object <i>N</i> that represents a node to another object <i>G</i> that specifies the containing group
   * of
   * <i>N</i>. If <i>G</i> is contained in {@link #getGroupsSource() GroupsSource}, then the node for <i>N</i> becomes
   * a
   * child node of the group for <i>G</i>.
   * </p>
   * <p>
   * If a {@link #getGroupIdProvider() GroupIdProvider} is set, the returned object <i>G</i> must be the ID of the
   * object that specifies the group instead of that object itself.
   * </p>
   *
   * @param value The GroupProvider to set.
   * @see #getNodesSource()
   * @see #getGroupsSource()
   * @see #getGroupIdProvider()
   * @see #getGroupProvider()
   */
  public final void setGroupProvider(Function<TNode, Object> value) {
    this.groupProvider = value;
  }

  /**
   * Gets a delegate that maps an edge object to a label.
   * <p>
   * This maps an object that represents an edge to an object that represents the label for the edge.
   * </p>
   * <p>
   * The resulting object will be converted into a string to be displayed as the label's text. If this is insufficient,
   * a label can also be created directly in an event handler of the {@link #addEdgeCreatedListener(IEventListener)
   * EdgeCreated} event.
   * </p>
   * <p>
   * Returning {@code null} from the delegate will not create a label for that edge.
   * </p>
   *
   * @return The EdgeLabelProvider.
   * @see #getEdgesSource()
   * @see #setEdgeLabelProvider(Function)
   */
  public final Function<TEdge, Object> getEdgeLabelProvider() {
    return graphBuilderHelper.edgeLabelBinding;
  }

  /**
   * Sets a delegate that maps an edge object to a label.
   * <p>
   * This maps an object that represents an edge to an object that represents the label for the edge.
   * </p>
   * <p>
   * The resulting object will be converted into a string to be displayed as the label's text. If this is insufficient,
   * a label can also be created directly in an event handler of the {@link #addEdgeCreatedListener(IEventListener)
   * EdgeCreated} event.
   * </p>
   * <p>
   * Returning {@code null} from the delegate will not create a label for that edge.
   * </p>
   *
   * @param value The EdgeLabelProvider to set.
   * @see #getEdgesSource()
   * @see #getEdgeLabelProvider()
   */
  public final void setEdgeLabelProvider(Function<TEdge, Object> value) {
    graphBuilderHelper.edgeLabelBinding = value;
  }

  private Function<TEdge, Object> sourceNodeProvider;

  /**
   * Gets a delegate that maps edge objects to their source node.
   * <p>
   * This maps an object <i>E</i> that represents an edge to another object <i>N</i> that specifies the source node of
   * <i>E</i>. This source node may be a group node from {@link #getGroupsSource() GroupsSource} as well.
   * </p>
   * <p>
   * If a {@link #getNodeIdProvider() NodeIdProvider} is set, the returned object <i>N</i> must be the ID of the object
   * that specifies the node instead of that object itself. The same holds for {@link #getGroupIdProvider()
   * GroupIdProvider} when trying to specify a group node that way. If both a node and a group node share the same ID,
   * the node takes precedence over the group node.
   * </p>
   * <p>
   * If {@link #isLazyNodeDefinitionEnabled() LazyNodeDefinitionEnabled} is {@code true}, the resulting node object does
   * not have to exist in {@link #getNodesSource() NodesSource}; instead, nodes are created as needed.
   * </p>
   *
   * @return The SourceNodeProvider.
   * @see #getNodesSource()
   * @see #getTargetNodeProvider()
   * @see #getNodeIdProvider()
   * @see #isLazyNodeDefinitionEnabled()
   * @see #setSourceNodeProvider(Function)
   */
  public final Function<TEdge, Object> getSourceNodeProvider() {
    return this.sourceNodeProvider;
  }

  /**
   * Sets a delegate that maps edge objects to their source node.
   * <p>
   * This maps an object <i>E</i> that represents an edge to another object <i>N</i> that specifies the source node of
   * <i>E</i>. This source node may be a group node from {@link #getGroupsSource() GroupsSource} as well.
   * </p>
   * <p>
   * If a {@link #getNodeIdProvider() NodeIdProvider} is set, the returned object <i>N</i> must be the ID of the object
   * that specifies the node instead of that object itself. The same holds for {@link #getGroupIdProvider()
   * GroupIdProvider} when trying to specify a group node that way. If both a node and a group node share the same ID,
   * the node takes precedence over the group node.
   * </p>
   * <p>
   * If {@link #isLazyNodeDefinitionEnabled() LazyNodeDefinitionEnabled} is {@code true}, the resulting node object does
   * not have to exist in {@link #getNodesSource() NodesSource}; instead, nodes are created as needed.
   * </p>
   *
   * @param value The SourceNodeProvider to set.
   * @see #getNodesSource()
   * @see #getTargetNodeProvider()
   * @see #getNodeIdProvider()
   * @see #isLazyNodeDefinitionEnabled()
   * @see #getSourceNodeProvider()
   */
  public final void setSourceNodeProvider(Function<TEdge, Object> value) {
    this.sourceNodeProvider = value;
  }

  private Function<TEdge, Object> targetNodeProvider;

  /**
   * Gets a delegate that maps edge objects to their target node.
   * <p>
   * This maps an object <i>E</i> that represents an edge to another object <i>N</i> that specifies the target node of
   * <i>E</i>. This target node may be a group node from {@link #getGroupsSource() GroupsSource} as well.
   * </p>
   * <p>
   * If a {@link #getNodeIdProvider() NodeIdProvider} is set, the returned object <i>N</i> must be the ID of the object
   * that specifies the node instead of that object itself. The same holds for {@link #getGroupIdProvider()
   * GroupIdProvider} when trying to specify a group node that way. If both a node and a group node share the same ID,
   * the node takes precedence over the group node.
   * </p>
   * <p>
   * If {@link #isLazyNodeDefinitionEnabled() LazyNodeDefinitionEnabled} is {@code true}, the resulting node object does
   * not have to exist in {@link #getNodesSource() NodesSource}; instead, nodes are created as needed.
   * </p>
   *
   * @return The TargetNodeProvider.
   * @see #getNodesSource()
   * @see #getSourceNodeProvider()
   * @see #getNodeIdProvider()
   * @see #isLazyNodeDefinitionEnabled()
   * @see #setTargetNodeProvider(Function)
   */
  public final Function<TEdge, Object> getTargetNodeProvider() {
    return this.targetNodeProvider;
  }

  /**
   * Sets a delegate that maps edge objects to their target node.
   * <p>
   * This maps an object <i>E</i> that represents an edge to another object <i>N</i> that specifies the target node of
   * <i>E</i>. This target node may be a group node from {@link #getGroupsSource() GroupsSource} as well.
   * </p>
   * <p>
   * If a {@link #getNodeIdProvider() NodeIdProvider} is set, the returned object <i>N</i> must be the ID of the object
   * that specifies the node instead of that object itself. The same holds for {@link #getGroupIdProvider()
   * GroupIdProvider} when trying to specify a group node that way. If both a node and a group node share the same ID,
   * the node takes precedence over the group node.
   * </p>
   * <p>
   * If {@link #isLazyNodeDefinitionEnabled() LazyNodeDefinitionEnabled} is {@code true}, the resulting node object does
   * not have to exist in {@link #getNodesSource() NodesSource}; instead, nodes are created as needed.
   * </p>
   *
   * @param value The TargetNodeProvider to set.
   * @see #getNodesSource()
   * @see #getSourceNodeProvider()
   * @see #getNodeIdProvider()
   * @see #isLazyNodeDefinitionEnabled()
   * @see #getTargetNodeProvider()
   */
  public final void setTargetNodeProvider(Function<TEdge, Object> value) {
    this.targetNodeProvider = value;
  }

  /**
   * Gets a delegate that maps group objects to their identifier.
   * <p>
   * This maps an object that represents a group node to its identifier. This is needed when {@link #getNodesSource()
   * node objects} only contain an identifier to specify the group they belong to instead of pointing directly to the
   * respective group object. The same goes for the parent group in group objects.
   * </p>
   *
   * @return The GroupIdProvider.
   * @y.warning The identifiers returned by the delegate must be stable and not change over time. Otherwise the {@link
   * #updateGraph() update mechanism} cannot determine whether groups have been added or updated. For the same reason
   * this property must not be changed after having built the graph once.
   * @see #getGroupsSource()
   * @see #getGroupProvider()
   * @see #getParentGroupProvider()
   * @see #setGroupIdProvider(Function)
   */
  public final Function<TGroup, Object> getGroupIdProvider() {
    return this.groupIdProvider;
  }

  /**
   * Sets a delegate that maps group objects to their identifier.
   * <p>
   * This maps an object that represents a group node to its identifier. This is needed when {@link #getNodesSource()
   * node objects} only contain an identifier to specify the group they belong to instead of pointing directly to the
   * respective group object. The same goes for the parent group in group objects.
   * </p>
   *
   * @param value The GroupIdProvider to set.
   * @y.warning The identifiers returned by the delegate must be stable and not change over time. Otherwise the {@link
   * #updateGraph() update mechanism} cannot determine whether groups have been added or updated. For the same reason
   * this property must not be changed after having built the graph once.
   * @see #getGroupsSource()
   * @see #getGroupProvider()
   * @see #getParentGroupProvider()
   * @see #getGroupIdProvider()
   */
  public final void setGroupIdProvider(Function<TGroup, Object> value) {
    this.groupIdProvider = value;
  }

  /**
   * Gets a delegate that maps a group object to a label.
   * <p>
   * This maps an object that represents a group node to an object that represents the label for the group node.
   * </p>
   * <p>
   * The resulting object will be converted into a string to be displayed as the label's text. If this is insufficient,
   * a label can also be created directly in an event handler of the {@link #addGroupNodeCreatedListener(IEventListener)
   * GroupNodeCreated} event.
   * </p>
   * <p>
   * Returning {@code null} from the delegate will not create a label for that group node.
   * </p>
   *
   * @return The GroupLabelProvider.
   * @see #getGroupsSource()
   * @see #setGroupLabelProvider(Function)
   */
  public final Function<TGroup, Object> getGroupLabelProvider() {
    return graphBuilderHelper.groupLabelProvider;
  }

  /**
   * Sets a delegate that maps a group object to a label.
   * <p>
   * This maps an object that represents a group node to an object that represents the label for the group node.
   * </p>
   * <p>
   * The resulting object will be converted into a string to be displayed as the label's text. If this is insufficient,
   * a label can also be created directly in an event handler of the {@link #addGroupNodeCreatedListener(IEventListener)
   * GroupNodeCreated} event.
   * </p>
   * <p>
   * Returning {@code null} from the delegate will not create a label for that group node.
   * </p>
   *
   * @param value The GroupLabelProvider to set.
   * @see #getGroupsSource()
   * @see #getGroupLabelProvider()
   */
  public final void setGroupLabelProvider(Function<TGroup, Object> value) {
    graphBuilderHelper.groupLabelProvider = value;
  }

  /**
   * Gets a delegate that maps group objects to their containing groups.
   * <p>
   * This maps an object <i>G</i> that represents a group node to another object <i>P</i> that specifies the containing
   * group of <i>G</i>. If <i>P</i> is contained in {@link #getGroupsSource() GroupsSource}, then the group node for
   * <i>G</i> becomes a child node of the group for <i>P</i>.
   * </p>
   * <p>
   * If a {@link #getGroupIdProvider() GroupIdProvider} is set, the returned object <i>P</i> must be the ID of the
   * object that specifies the group instead of that object itself.
   * </p>
   *
   * @return The ParentGroupProvider.
   * @see #getGroupsSource()
   * @see #getGroupIdProvider()
   * @see #setParentGroupProvider(Function)
   */
  public final Function<TGroup, Object> getParentGroupProvider() {
    return this.parentGroupProvider;
  }

  /**
   * Sets a delegate that maps group objects to their containing groups.
   * <p>
   * This maps an object <i>G</i> that represents a group node to another object <i>P</i> that specifies the containing
   * group of <i>G</i>. If <i>P</i> is contained in {@link #getGroupsSource() GroupsSource}, then the group node for
   * <i>G</i> becomes a child node of the group for <i>P</i>.
   * </p>
   * <p>
   * If a {@link #getGroupIdProvider() GroupIdProvider} is set, the returned object <i>P</i> must be the ID of the
   * object that specifies the group instead of that object itself.
   * </p>
   *
   * @param value The ParentGroupProvider to set.
   * @see #getGroupsSource()
   * @see #getGroupIdProvider()
   * @see #getParentGroupProvider()
   */
  public final void setParentGroupProvider(Function<TGroup, Object> value) {
    this.parentGroupProvider = value;
  }

  /**
   * Populates the graph with items generated from the bound data.
   * <p>
   * The graph is cleared, and then new nodes, groups, and edges are created as defined by the source collections.
   * </p>
   *
   * @return The created graph.
   * @see #updateGraph()
   */
  public IGraph buildGraph() {
    this.getGraph().clear();
    initialize();
    return this.graphBuilder.buildGraph();
  }

  /**
   * Updates the graph after changes in the bound data.
   * <p>
   * In contrast to {@link #buildGraph()}, the graph is not cleared. Instead, graph elements corresponding to objects
   * that are still present in the source collections are kept, new graph elements are created for new objects in the
   * collections, and obsolete ones are removed.
   * </p>
   */
  public void updateGraph() {
    this.initialize();
    graphBuilder.updateGraph();
  }

  private void initialize() {
    if (getNodesSource() == null) {
      throw new IllegalStateException("NodesSource must be set.");
    }

    if (getEdgesSource() != null && (getSourceNodeProvider() == null || getTargetNodeProvider() == null)) {
      throw new IllegalStateException(
          "Since EdgesSource is set, SourceNodeProvider and TargetNodeProvider must be set, too.");
    }

    if (isLazyNodeDefinitionEnabled() && getNodeIdProvider() != null) {
      throw new IllegalStateException("LazyNodeDefinition cannot be used with NodeIdProvider.");
    }
    this.initializeProviders();
    this.prepareData();
  }

  private void initializeProviders() {
    this.builderNodesSource.setIdProvider(
        this.getNodeIdProvider() != null ? item -> this.getNodeIdProvider().apply(item) : null);
    this.builderGroupsSource.setIdProvider(
        this.getGroupIdProvider() != null ? item -> this.getGroupIdProvider().apply(item) : null);

    this.builderNodesSource.setParentIdProvider(
        this.getGroupProvider() != null ? item -> this.getGroupProvider().apply(item) : null);
    this.builderGroupsSource.setParentIdProvider(
        this.getParentGroupProvider() != null ? item -> this.getParentGroupProvider().apply(item) : null);
  }

  private void prepareData() {
    Iterable<TNode> nodesSource = this.getNodesSource();

    if (this.isLazyNodeDefinitionEnabled() && this.getEdgesSource() != null) {
      ArrayList<TNode> clonedNodesSource = new ArrayList<TNode>();
      nodesSource.forEach(clonedNodesSource::add);
      for (TEdge edgeDataItem : this.getEdgesSource()) {
        Object sourceNodeDataItem = this.getSourceNodeProvider().apply(edgeDataItem);
        if (!clonedNodesSource.contains((TNode) sourceNodeDataItem)) {
          clonedNodesSource.add((TNode) sourceNodeDataItem);
        }
        Object targetNodeDataItem = this.getTargetNodeProvider().apply(edgeDataItem);
        if (!clonedNodesSource.contains((TNode) targetNodeDataItem)) {
          clonedNodesSource.add((TNode) targetNodeDataItem);
        }
      }
      nodesSource = clonedNodesSource;
    }

    this.graphBuilder.setData(this.builderNodesSource, nodesSource);
    if (this.getEdgesSource() != null) {
      this.graphBuilder.setData(this.builderEdgesSource, this.getEdgesSource());
    }
    if (this.getGroupsSource() != null) {
      this.graphBuilder.setData(this.builderGroupsSource, this.getGroupsSource());
    }
  }

  /**
   * Creates a node with the specified parent from the given {@code nodeObject} and {@code labelData}.
   * <p>
   * This method is called for every node that is created either when {@link #buildGraph() building the graph}, or when
   * new items appear in the {@link #getNodesSource() NodesSource} when {@link #updateGraph() updating it}.
   * </p>
   * <p>
   * The default behavior is to create the node with the given parent node, assign the {@code nodeObject} to the node's
   * {@link com.yworks.yfiles.graph.ITagOwner#getTag() Tag} property, and create a label from {@code labelData}, if
   * present.
   * </p>
   * <p>
   * Customizing how nodes are created is usually easier by adding an event handler to the {@link
   * #addNodeCreatedListener(IEventListener) NodeCreated} event than by overriding this method.
   * </p>
   *
   * @param graph      The graph in which to create the node.
   * @param parent     The node's parent node.
   * @param labelData  The optional label data of the node if an {@link #getNodeLabelProvider() NodeLabelProvider} is
   *                   specified.
   * @param nodeObject The object from {@link #getNodesSource() NodesSource} from which to create the node.
   * @return The created node.
   * @y.expert
   */
  protected INode createNode(IGraph graph, INode parent, Object labelData,
                             TNode nodeObject) {
    return graphBuilderHelper.createNode(graph, parent, labelData, nodeObject);
  }

  /**
   * Updates an existing node when the {@link #updateGraph() graph is updated}.
   * <p>
   * This method is called during {@link #updateGraph() updating the graph} for every node that already exists in the
   * graph where its corresponding object from {@link #getNodesSource() NodesSource} is also still present.
   * </p>
   * <p>
   * Customizing how nodes are updated is usually easier by adding an event handler to the {@link
   * #addNodeUpdatedListener(IEventListener) NodeUpdated} event than by overriding this method.
   * </p>
   *
   * @param graph      The node's containing graph.
   * @param node       The node to update.
   * @param parent     The node's parent node.
   * @param labelData  The optional label data of the node if an {@link #getNodeLabelProvider() NodeLabelProvider} is
   *                   specified.
   * @param nodeObject The object from {@link #getNodesSource() NodesSource} from which the node has been created.
   * @y.expert
   */
  protected void updateNode(IGraph graph, INode node, INode parent,
                            Object labelData, TNode nodeObject) {
    graphBuilderHelper.updateNode(graph, node, parent, labelData, nodeObject);
  }

  /**
   * Creates an edge from the given {@code edgeObject} and {@code labelData}.
   * <p>
   * This method is called for every edge that is created either when {@link #buildGraph() building the graph}, or when
   * new items appear in the {@link #getEdgesSource() EdgesSource} when {@link #updateGraph() updating it}.
   * </p>
   * <p>
   * The default behavior is to create the edge, assign the {@code edgeObject} to the edge's {@link
   * com.yworks.yfiles.graph.ITagOwner#getTag() Tag} property, and create a label from {@code labelData}, if present.
   * </p>
   * <p>
   * Customizing how edges are created is usually easier by adding an event handler to the {@link
   * #addEdgeCreatedListener(IEventListener) EdgeCreated} event than by overriding this method.
   * </p>
   *
   * @param graph      The graph in which to create the edge.
   * @param source     The source node for the edge.
   * @param target     The target node for the edge.
   * @param labelData  The optional label data of the edge if an {@link #getEdgeLabelProvider() EdgeLabelProvider} is
   *                   specified.
   * @param edgeObject The object from {@link #getEdgesSource() EdgesSource} from which to create the edge.
   * @return The created edge.
   * @y.expert
   */
  protected IEdge createEdge(IGraph graph, INode source, INode target,
                             Object labelData, TEdge edgeObject) {
    return graphBuilderHelper.createEdge(graph, source, target, labelData, edgeObject);
  }

  /**
   * Updates an existing edge when the {@link #updateGraph() graph is updated}.
   * <p>
   * This method is called during {@link #updateGraph() updating the graph} for every edge that already exists in the
   * graph where its corresponding object from {@link #getEdgesSource() EdgesSource} is also still present.
   * </p>
   * <p>
   * Customizing how edges are updated is usually easier by adding an event handler to the {@link
   * #addEdgeUpdatedListener(IEventListener) EdgeUpdated} event than by overriding this method.
   * </p>
   *
   * @param graph      The edge's containing graph.
   * @param edge       The edge to update.
   * @param labelData  The optional label data of the edge if an {@link #getEdgeLabelProvider() EdgeLabelProvider} is
   *                   specified.
   * @param edgeObject The object from {@link #getEdgesSource() EdgesSource} from which the edge has been created.
   * @y.expert
   */
  protected void updateEdge(IGraph graph, IEdge edge, Object labelData,
                            TEdge edgeObject) {
    graphBuilderHelper.updateEdge(graph, edge, labelData, edgeObject);
  }

  /**
   * Creates a group node from the given {@code groupObject} and {@code labelData}.
   * <p>
   * This method is called for every group node that is created either when {@link #buildGraph() building the graph}, or
   * when new items appear in the {@link #getGroupsSource() GroupsSource} when {@link #updateGraph() updating it}.
   * </p>
   * <p>
   * The default behavior is to create the group node, assign the {@code groupObject} to the group node's {@link
   * com.yworks.yfiles.graph.ITagOwner#getTag() Tag} property, and create a label from {@code labelData}, if present.
   * </p>
   * <p>
   * Customizing how group nodes are created is usually easier by adding an event handler to the {@link
   * #addGroupNodeCreatedListener(IEventListener) GroupNodeCreated} event than by overriding this method.
   * </p>
   *
   * @param graph       The graph in which to create the group node.
   * @param labelData   The optional label data of the group node if an {@link #getGroupLabelProvider()
   *                    GroupLabelProvider} is specified.
   * @param groupObject The object from {@link #getGroupsSource() GroupsSource} from which to create the group node.
   * @return The created group node.
   * @y.expert
   */
  protected INode createGroupNode(IGraph graph, Object labelData, TGroup groupObject) {
    return graphBuilderHelper.createGroupNode(graph, labelData, groupObject);
  }

  /**
   * Updates an existing group node when the {@link #updateGraph() graph is updated}.
   * <p>
   * This method is called during {@link #updateGraph() updating the graph} for every group node that already exists in
   * the graph where its corresponding object from {@link #getGroupsSource() GroupsSource} is also still present.
   * </p>
   * <p>
   * Customizing how group nodes are updated is usually easier by adding an event handler to the {@link
   * #addGroupNodeUpdatedListener(IEventListener) GroupNodeUpdated} event than by overriding this method.
   * </p>
   *
   * @param graph       The group node's containing graph.
   * @param groupNode   The group node to update.
   * @param labelData   The optional label data of the group node if an {@link #getGroupLabelProvider()
   *                    GroupLabelProvider} is specified.
   * @param groupObject The object from {@link #getGroupsSource() GroupsSource} from which the group node has been
   *                    created.
   * @y.expert
   */
  protected void updateGroupNode(IGraph graph, INode groupNode, Object labelData,
                                 TGroup groupObject) {
    graphBuilderHelper.updateGroupNode(graph, groupNode, labelData, groupObject);
  }

  /**
   * Adds the given listener for the {@code NodeCreated} event that occurs when a node has been created.
   * <p>
   * This event can be used to further customize the created node.
   * </p>
   * <p>
   * New nodes are created either in response to calling {@link #buildGraph()}, or in response to calling {@link
   * #updateGraph()} when there are new items in {@link #getNodesSource() NodesSource}.
   * </p>
   *
   * @param nodeCreatedEvent The listener to add.
   * @see #addNodeUpdatedListener(IEventListener)
   * @see #removeNodeCreatedListener(IEventListener)
   */
  public final void addNodeCreatedListener(IEventListener<GraphBuilderItemEventArgs<INode, TNode>> nodeCreatedEvent) {
    graphBuilderHelper.addNodeCreatedListener(nodeCreatedEvent);
  }

  /**
   * Removes the given listener for the {@code NodeCreated} event that occurs when a node has been created.
   * <p>
   * This event can be used to further customize the created node.
   * </p>
   * <p>
   * New nodes are created either in response to calling {@link #buildGraph()}, or in response to calling {@link
   * #updateGraph()} when there are new items in {@link #getNodesSource() NodesSource}.
   * </p>
   *
   * @param nodeCreatedEvent The listener to remove.
   * @see #addNodeUpdatedListener(IEventListener)
   * @see #addNodeCreatedListener(IEventListener)
   */
  public final void removeNodeCreatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TNode>> nodeCreatedEvent) {
    graphBuilderHelper.removeNodeCreatedListener(nodeCreatedEvent);
  }

  /**
   * Adds the given listener for the {@code NodeUpdated} event that occurs when a node has been updated.
   * <p>
   * This event can be used to update customizations added in an event handler for {@link
   * #addNodeCreatedListener(IEventListener) NodeCreated}.
   * </p>
   * <p>
   * Nodes are updated in response to calling {@link #updateGraph()} for items that haven't been added anew in {@link
   * #getNodesSource() NodesSource} since the last call to {@link #buildGraph()} or {@link #updateGraph()}.
   * </p>
   *
   * @param nodeUpdatedEvent The listener to add.
   * @see #addNodeCreatedListener(IEventListener)
   * @see #removeNodeUpdatedListener(IEventListener)
   */
  public final void addNodeUpdatedListener(IEventListener<GraphBuilderItemEventArgs<INode, TNode>> nodeUpdatedEvent) {
    graphBuilderHelper.addNodeUpdatedListener(nodeUpdatedEvent);
  }

  /**
   * Removes the given listener for the {@code NodeUpdated} event that occurs when a node has been updated.
   * <p>
   * This event can be used to update customizations added in an event handler for {@link
   * #addNodeCreatedListener(IEventListener) NodeCreated}.
   * </p>
   * <p>
   * Nodes are updated in response to calling {@link #updateGraph()} for items that haven't been added anew in {@link
   * #getNodesSource() NodesSource} since the last call to {@link #buildGraph()} or {@link #updateGraph()}.
   * </p>
   *
   * @param nodeUpdatedEvent The listener to remove.
   * @see #addNodeCreatedListener(IEventListener)
   * @see #addNodeUpdatedListener(IEventListener)
   */
  public final void removeNodeUpdatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TNode>> nodeUpdatedEvent) {
    graphBuilderHelper.removeNodeUpdatedListener(nodeUpdatedEvent);
  }

  /**
   * Adds the given listener for the {@code EdgeCreated} event that occurs when an edge has been created.
   * <p>
   * This event can be used to further customize the created edge.
   * </p>
   * <p>
   * New edges are created either in response to calling {@link #buildGraph()}, or in response to calling {@link
   * #updateGraph()} when there are new items in {@link #getEdgesSource() EdgesSource}.
   * </p>
   *
   * @param edgeCreatedEvent The listener to add.
   * @see #addEdgeUpdatedListener(IEventListener)
   * @see #removeEdgeCreatedListener(IEventListener)
   */
  public final void addEdgeCreatedListener(IEventListener<GraphBuilderItemEventArgs<IEdge, TEdge>> edgeCreatedEvent) {
    graphBuilderHelper.addEdgeCreatedListener(edgeCreatedEvent);
  }

  /**
   * Removes the given listener for the {@code EdgeCreated} event that occurs when an edge has been created.
   * <p>
   * This event can be used to further customize the created edge.
   * </p>
   * <p>
   * New edges are created either in response to calling {@link #buildGraph()}, or in response to calling {@link
   * #updateGraph()} when there are new items in {@link #getEdgesSource() EdgesSource}.
   * </p>
   *
   * @param edgeCreatedEvent The listener to remove.
   * @see #addEdgeUpdatedListener(IEventListener)
   * @see #addEdgeCreatedListener(IEventListener)
   */
  public final void removeEdgeCreatedListener(
      IEventListener<GraphBuilderItemEventArgs<IEdge, TEdge>> edgeCreatedEvent) {
    graphBuilderHelper.removeEdgeCreatedListener(edgeCreatedEvent);
  }

  /**
   * Adds the given listener for the {@code EdgeUpdated} event that occurs when an edge has been updated.
   * <p>
   * This event can be used to update customizations added in an event handler for {@link
   * #addEdgeCreatedListener(IEventListener) EdgeCreated}.
   * </p>
   * <p>
   * Edges are updated in response to calling {@link #updateGraph()} for items that haven't been added anew in {@link
   * #getEdgesSource() EdgesSource} since the last call to {@link #buildGraph()} or {@link #updateGraph()}.
   * </p>
   *
   * @param edgeUpdatedEvent The listener to add.
   * @see #addEdgeCreatedListener(IEventListener)
   * @see #removeEdgeUpdatedListener(IEventListener)
   */
  public final void addEdgeUpdatedListener(IEventListener<GraphBuilderItemEventArgs<IEdge, TEdge>> edgeUpdatedEvent) {
    graphBuilderHelper.addEdgeUpdatedListener(edgeUpdatedEvent);
  }

  /**
   * Removes the given listener for the {@code EdgeUpdated} event that occurs when an edge has been updated.
   * <p>
   * This event can be used to update customizations added in an event handler for {@link
   * #addEdgeCreatedListener(IEventListener) EdgeCreated}.
   * </p>
   * <p>
   * Edges are updated in response to calling {@link #updateGraph()} for items that haven't been added anew in {@link
   * #getEdgesSource() EdgesSource} since the last call to {@link #buildGraph()} or {@link #updateGraph()}.
   * </p>
   *
   * @param edgeUpdatedEvent The listener to remove.
   * @see #addEdgeCreatedListener(IEventListener)
   * @see #addEdgeUpdatedListener(IEventListener)
   */
  public final void removeEdgeUpdatedListener(
      IEventListener<GraphBuilderItemEventArgs<IEdge, TEdge>> edgeUpdatedEvent) {
    graphBuilderHelper.removeEdgeUpdatedListener(edgeUpdatedEvent);
  }

  /**
   * Adds the given listener for the {@code GroupNodeCreated} event that occurs when a group node has been created.
   * <p>
   * This event can be used to further customize the created group node.
   * </p>
   * <p>
   * New group nodes are created either in response to calling {@link #buildGraph()}, or in response to calling {@link
   * #updateGraph()} when there are new items in {@link #getGroupsSource() GroupsSource}.
   * </p>
   *
   * @param groupNodeCreatedEvent The listener to add.
   * @see #addGroupNodeUpdatedListener(IEventListener)
   * @see #removeGroupNodeCreatedListener(IEventListener)
   */
  public final void addGroupNodeCreatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TGroup>> groupNodeCreatedEvent) {
    graphBuilderHelper.addGroupNodeCreatedListener(groupNodeCreatedEvent);
  }

  /**
   * Removes the given listener for the {@code GroupNodeCreated} event that occurs when a group node has been created.
   * <p>
   * This event can be used to further customize the created group node.
   * </p>
   * <p>
   * New group nodes are created either in response to calling {@link #buildGraph()}, or in response to calling {@link
   * #updateGraph()} when there are new items in {@link #getGroupsSource() GroupsSource}.
   * </p>
   *
   * @param groupNodeCreatedEvent The listener to remove.
   * @see #addGroupNodeUpdatedListener(IEventListener)
   * @see #addGroupNodeCreatedListener(IEventListener)
   */
  public final void removeGroupNodeCreatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TGroup>> groupNodeCreatedEvent) {
    graphBuilderHelper.removeGroupNodeCreatedListener(groupNodeCreatedEvent);
  }

  /**
   * Adds the given listener for the {@code GroupNodeUpdated} event that occurs when a group node has been updated.
   * <p>
   * This event can be used to update customizations added in an event handler for {@link
   * #addGroupNodeCreatedListener(IEventListener) GroupNodeCreated}.
   * </p>
   * <p>
   * Group nodes are updated in response to calling {@link #updateGraph()} for items that haven't been added anew in
   * {@link #getGroupsSource() GroupsSource} since the last call to {@link #buildGraph()} or {@link #updateGraph()}.
   * </p>
   *
   * @param groupNodeUpdatedEvent The listener to add.
   * @see #addGroupNodeCreatedListener(IEventListener)
   * @see #removeGroupNodeUpdatedListener(IEventListener)
   */
  public final void addGroupNodeUpdatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TGroup>> groupNodeUpdatedEvent) {
    graphBuilderHelper.addGroupNodeUpdatedListener(groupNodeUpdatedEvent);
  }

  /**
   * Removes the given listener for the {@code GroupNodeUpdated} event that occurs when a group node has been updated.
   * <p>
   * This event can be used to update customizations added in an event handler for {@link
   * #addGroupNodeCreatedListener(IEventListener) GroupNodeCreated}.
   * </p>
   * <p>
   * Group nodes are updated in response to calling {@link #updateGraph()} for items that haven't been added anew in
   * {@link #getGroupsSource() GroupsSource} since the last call to {@link #buildGraph()} or {@link #updateGraph()}.
   * </p>
   *
   * @param groupNodeUpdatedEvent The listener to remove.
   * @see #addGroupNodeCreatedListener(IEventListener)
   * @see #addGroupNodeUpdatedListener(IEventListener)
   */
  public final void removeGroupNodeUpdatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TGroup>> groupNodeUpdatedEvent) {
    graphBuilderHelper.removeGroupNodeUpdatedListener(groupNodeUpdatedEvent);
  }

  /**
   * Retrieves the object from which a given item has been created.
   *
   * @param item The item to get the object for.
   * @return The object from which the graph item has been created.
   * @see #getNode(Object)
   * @see #getEdge(Object)
   * @see #getGroup(Object)
   */
  public final Object getSourceObject(IModelItem item) {
    return item.getTag();
  }

  /**
   * Retrieves the node associated with an object from the {@link #getNodesSource() NodesSource}.
   *
   * @param nodeObject An object from the {@link #getNodesSource() NodesSource}.
   * @return The node associated with {@code nodeObject}, or {@code null} in case there is no node associated with that
   * object. This can happen if {@code nodeObject} is new since the last call to {@link #updateGraph()}.
   * @see #getEdge(Object)
   * @see #getGroup(Object)
   * @see #getSourceObject(IModelItem)
   */
  public final INode getNode(TNode nodeObject) {
    return getGraph().getNodes().stream().filter(n -> n.getTag() == nodeObject).findFirst().orElse(null);
  }

  /**
   * Retrieves the group node associated with an object from the {@link #getGroupsSource() GroupsSource}.
   *
   * @param edgeObject An object from the {@link #getGroupsSource() GroupsSource}.
   * @return The group node associated with {@code edgeObject}, or {@code null} in case there is no group node
   * associated with that object. This can happen if {@code edgeObject} is new since the last call to {@link
   * #updateGraph()}.
   * @see #getNode(Object)
   * @see #getGroup(Object)
   * @see #getSourceObject(IModelItem)
   */
  public final IEdge getEdge(TEdge edgeObject) {
    return getGraph().getEdges().stream().filter(e -> e.getTag() == edgeObject).findFirst().orElse(null);
  }

  /**
   * Retrieves the group node associated with an object from the {@link #getGroupsSource() GroupsSource}.
   *
   * @param groupObject An object from the {@link #getGroupsSource() GroupsSource}.
   * @return The group node associated with {@code groupObject}, or {@code null} in case there is no group node
   * associated with that object. This can happen if {@code groupObject} is new since the last call to {@link
   * #updateGraph()}.
   * @see #getNode(Object)
   * @see #getEdge(Object)
   * @see #getSourceObject(IModelItem)
   */
  public final INode getGroup(TGroup groupObject) {
    return getGraph().getNodes().stream().filter(n -> n.getTag() == groupObject).findFirst().orElse(null);
  }
}
