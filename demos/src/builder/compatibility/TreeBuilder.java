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
package builder.compatibility;

import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.utils.IEventListener;

import java.util.function.Function;

/**
 * Populates a graph from custom data where objects corresponding to nodes have a parent-child relationship.
 * <p>
 * This class can be used when the data specifies a collection of nodes, each of which knows its child nodes,
 * and&#x2014;optionally&#x2014;a collection of groups. The properties {@link #getNodesSource() NodesSource} and {@link
 * #getGroupsSource() GroupsSource} define the source collections from which nodes and groups will be created.
 * </p>
 * <p>
 * Generally, using the {@link TreeBuilder} class consists of a few basic steps:
 * </p>
 * <ol>
 * <li>Set up the {@link #getGraph() Graph} with the proper defaults for items ({@link IGraph#getNodeDefaults() NodeDefaults},
 * {@link IGraph#getGroupNodeDefaults() GroupNodeDefaults}, {@link IGraph#getEdgeDefaults() EdgeDefaults})</li>
 * <li>Create a {@link TreeBuilder}.</li>
 * <li>Set the items sources. At the very least the {@link #getNodesSource() NodesSource} is needed. Note that the {@link #getNodesSource() NodesSource}
 * does not have to contain all nodes, as nodes that are implicitly specified through the {@link #getChildProvider() ChildProvider}
 * are automatically added to the graph as well. If the items in the nodes collection are grouped somehow, then also set
 * the {@link #getGroupsSource() GroupsSource} property.</li>
 * <li>Set up the bindings so that a graph structure can actually be created from the items sources. This involves setting up
 * the {@link #getChildProvider() ChildProvider} property so that edges can be created. If the node objects don't actually
 * contain their children objects, but instead identifiers of other node objects, then {@link #getChildProvider() ChildProvider}
 * would return those identifiers and {@link #getNodeIdProvider() NodeIdProvider} must be set to return that identifier
 * when given a node object.</li>
 * <li>If {@link #getGroupsSource() GroupsSource} is set, then you also need to set the {@link #getGroupProvider() GroupProvider}
 * property to enable mapping nodes to groups. Just like with a node's children, if the node object only contains an
 * identifier for a group node and not the actual group object, then return the identifier in the {@link #getGroupProvider() GroupProvider}
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
 * objects know their neighbors, or {@link GraphBuilder} which is a more general approach to creating arbitrary graphs.
 * </p>
 * <p>
 * The different graph builders are discussed in the section in the section
 * <a href="@DGUIDE_PREFIX@/graph_source.html">Creating a Graph from Business Data</a>. Class {@code TreeBuilder}, in
 * particular, is topic of section <a href="@DGUIDE_PREFIX@/graph_source-TreeBuilder">TreeBuilder</a>.
 * </p>
 *
 * @param <TNode>  The type of object nodes are created from. This type must implement {@link Object#equals(Object)} and
 *                 {@link Object#hashCode()} properly.
 * @param <TGroup> The type of object group nodes are created from. This type must implement {@link
 *                 Object#equals(Object)} and {@link Object#hashCode()} properly.
 * @y.note This class serves as a convenient way to create trees or forests and has some limitations:
 * <ul>
 * <li>When populating the graph for the first time it will be cleared of all existing items.</li>
 * <li>When using a {@link #getNodeIdProvider() NodeIdProvider}, all nodes have to exist in the
 * {@link #getNodesSource() NodesSource}. Nodes cannot be created on demand from IDs only.</li>
 * <li>Elements manually created on the graph in between calls to {@link #updateGraph()} may not be preserved.</li>
 * </ul>
 * <p>
 * If updates get too complex it's often better to write the code interfacing with the graph by hand instead of relying on
 * {@link TreeBuilder}.
 * </p>
 * @see GraphBuilder
 * @see AdjacentNodesGraphBuilder
 */
public class TreeBuilder<TNode, TGroup> {
  private final MyAdjacentNodesGraphBuilder<TNode, TGroup> graphBuilder;

  /**
   * Initializes a new instance of the {@link TreeBuilder} class that operates on the given graph.
   * <p>
   * The {@code graph} will be {@link IGraph#clear() cleared} and re-built from the data in {@link #getNodesSource()
   * NodesSource} and {@link #getGroupsSource() GroupsSource} when {@link #buildGraph()} is called.
   * </p>
   */
  public TreeBuilder() {
    this(null);
  }

  /**
   * Initializes a new instance of the {@link TreeBuilder} class that operates on the given graph.
   * <p>
   * The {@code graph} will be {@link IGraph#clear() cleared} and re-built from the data in {@link #getNodesSource()
   * NodesSource} and {@link #getGroupsSource() GroupsSource} when {@link #buildGraph()} is called.
   * </p>
   */
  public TreeBuilder(IGraph graph) {
    graphBuilder = new MyAdjacentNodesGraphBuilder<TNode, TGroup>(this, graph);
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
   * Gets the objects to be represented as nodes of the {@link #getGraph() Graph}.
   * <p>
   * Note that it is not necessary to include all nodes in this property, if they can be reached via the {@link
   * #getChildProvider() ChildProvider}. In this case it suffices to include all root nodes.
   * </p>
   *
   * @return The NodesSource.
   * @see #setNodesSource(Iterable)
   */
  public final Iterable<TNode> getNodesSource() {
    return graphBuilder.getNodesSource();
  }

  /**
   * Sets the objects to be represented as nodes of the {@link #getGraph() Graph}.
   * <p>
   * Note that it is not necessary to include all nodes in this property, if they can be reached via the {@link
   * #getChildProvider() ChildProvider}. In this case it suffices to include all root nodes.
   * </p>
   *
   * @param value The NodesSource to set.
   * @see #getNodesSource()
   */
  public final void setNodesSource(Iterable<TNode> value) {
    graphBuilder.setNodesSource(value);
  }

  /**
   * Gets the objects to be represented as group nodes of the {@link #getGraph() Graph}.
   *
   * @return The GroupsSource.
   * @see #setGroupsSource(Iterable)
   */
  public final Iterable<TGroup> getGroupsSource() {
    return graphBuilder.getGroupsSource();
  }

  /**
   * Sets the objects to be represented as group nodes of the {@link #getGraph() Graph}.
   *
   * @param value The GroupsSource to set.
   * @see #getGroupsSource()
   */
  public final void setGroupsSource(Iterable<TGroup> value) {
    graphBuilder.setGroupsSource(value);
  }

  /**
   * Gets a delegate that maps node objects to their identifier.
   * <p>
   * This maps an object that represents a node to its identifier. This is needed when {@link #getChildProvider()
   * children} are represented only by an identifier of nodes instead of pointing directly to the respective node
   * objects.
   * </p>
   *
   * @return The NodeIdProvider.
   * @y.warning The identifiers returned by the delegate must be stable and not change over time. Otherwise the {@link
   * #updateGraph() update mechanism} cannot determine whether nodes have been added or updated. For the same reason
   * this property must not be changed after having built the graph once.
   * @see #getNodesSource()
   * @see #getChildProvider()
   * @see #setNodeIdProvider(Function)
   */
  public final Function<TNode, Object> getNodeIdProvider() {
    return graphBuilder.getNodeIdProvider();
  }

  /**
   * Sets a delegate that maps node objects to their identifier.
   * <p>
   * This maps an object that represents a node to its identifier. This is needed when {@link #getChildProvider()
   * children} are represented only by an identifier of nodes instead of pointing directly to the respective node
   * objects.
   * </p>
   *
   * @param value The NodeIdProvider to set.
   * @y.warning The identifiers returned by the delegate must be stable and not change over time. Otherwise the {@link
   * #updateGraph() update mechanism} cannot determine whether nodes have been added or updated. For the same reason
   * this property must not be changed after having built the graph once.
   * @see #getNodesSource()
   * @see #getChildProvider()
   * @see #getNodeIdProvider()
   */
  public final void setNodeIdProvider(Function<TNode, Object> value) {
    graphBuilder.setNodeIdProvider(value);
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
    return graphBuilder.getNodeLabelProvider();
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
    graphBuilder.setNodeLabelProvider(value);
  }

  /**
   * Gets a delegate that maps node objects to their child nodes.
   * <p>
   * This maps an object that represents a node to a set of other objects that represent its child nodes.
   * </p>
   * <p>
   * If a {@link #getNodeIdProvider() NodeIdProvider} is set, the returned objects must be the IDs of node objects
   * instead of the node objects themselves.
   * </p>
   *
   * @return The ChildProvider.
   * @see #getNodesSource()
   * @see #getNodeIdProvider()
   * @see #setChildProvider(Function)
   */
  public final Function<TNode, Iterable> getChildProvider() {
    return graphBuilder.getSuccessorProvider();
  }

  /**
   * Sets a delegate that maps node objects to their child nodes.
   * <p>
   * This maps an object that represents a node to a set of other objects that represent its child nodes.
   * </p>
   * <p>
   * If a {@link #getNodeIdProvider() NodeIdProvider} is set, the returned objects must be the IDs of node objects
   * instead of the node objects themselves.
   * </p>
   *
   * @param value The ChildProvider to set.
   * @see #getNodesSource()
   * @see #getNodeIdProvider()
   * @see #getChildProvider()
   */
  public final void setChildProvider(Function<TNode, Iterable> value) {
    graphBuilder.setSuccessorProvider(value);
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
    return graphBuilder.getGroupProvider();
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
    graphBuilder.setGroupProvider(value);
  }

  /**
   * Gets a delegate that maps a node object representing the edge's target node to a label.
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
   * @see #setEdgeLabelProvider(EdgeLabelProvider)
   */
  public final EdgeLabelProvider<TNode> getEdgeLabelProvider() {
    return graphBuilder.getEdgeLabelProvider();
  }

  /**
   * Sets a delegate that maps a node object representing the edge's target node to a label.
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
   * @see #getEdgeLabelProvider()
   */
  public final void setEdgeLabelProvider(EdgeLabelProvider<TNode> value) {
    graphBuilder.setEdgeLabelProvider(value);
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
    return graphBuilder.getGroupIdProvider();
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
    graphBuilder.setGroupIdProvider(value);
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
    return graphBuilder.getGroupLabelProvider();
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
    graphBuilder.setGroupLabelProvider(value);
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
    return graphBuilder.getParentGroupProvider();
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
    graphBuilder.setParentGroupProvider(value);
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
    if (getNodesSource() == null || getChildProvider() == null) {
      throw new IllegalStateException(
          "The NodesSource and ChildProvider properties must be set before calling BuildGraph.");
    }
    return graphBuilder.buildGraph();
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
    graphBuilder.updateGraph();
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
    return graphBuilder.createNodeBase(graph, parent, labelData, nodeObject);
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
    graphBuilder.updateNodeBase(graph, node, parent, labelData, nodeObject);
  }

  /**
   * Creates an edge from the given {@code source}, {@code target}, and {@code labelData}.
   * <p>
   * This method is called for every edge that is created either when {@link #buildGraph() building the graph}, or when
   * new items appear in the {@link #getChildProvider() ChildProvider} when {@link #updateGraph() updating it}.
   * </p>
   * <p>
   * The default behavior is to create the edge and create a label from {@code labelData}, if present.
   * </p>
   * <p>
   * Customizing how edges are created is usually easier by adding an event handler to the {@link
   * #addEdgeCreatedListener(IEventListener) EdgeCreated} event than by overriding this method.
   * </p>
   *
   * @param graph     The graph in which to create the edge.
   * @param source    The source node for the edge.
   * @param target    The target node for the edge.
   * @param labelData The optional label data of the edge if an {@link #getEdgeLabelProvider() EdgeLabelProvider} is
   *                  specified.
   * @return The created edge.
   * @y.expert
   */
  protected IEdge createEdge(IGraph graph, INode source, INode target,
                             Object labelData) {
    return graphBuilder.createEdgeBase(graph, source, target, labelData);
  }

  /**
   * Updates an existing edge when the {@link #updateGraph() graph is updated}.
   * <p>
   * This method is called during {@link #updateGraph() updating the graph} for every edge that already exists in the
   * graph where its corresponding source and target node objects also still exist.
   * </p>
   * <p>
   * Customizing how edges are updated is usually easier by adding an event handler to the {@link
   * #addEdgeUpdatedListener(IEventListener) EdgeUpdated} event than by overriding this method.
   * </p>
   *
   * @param graph     The edge's containing graph.
   * @param edge      The edge to update.
   * @param labelData The optional label data of the edge if an {@link #getNodeLabelProvider() NodeLabelProvider} is
   *                  specified.
   * @y.expert
   */
  protected void updateEdge(IGraph graph, IEdge edge, Object labelData) {
    graphBuilder.updateEdgeBase(graph, edge, labelData);
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
    return graphBuilder.createGroupNodeBase(graph, labelData, groupObject);
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
    graphBuilder.updateGroupNodeBase(graph, groupNode, labelData, groupObject);
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
    graphBuilder.addNodeCreatedListener(nodeCreatedEvent);
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
    graphBuilder.removeNodeCreatedListener(nodeCreatedEvent);
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
    graphBuilder.addNodeUpdatedListener(nodeUpdatedEvent);
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
    graphBuilder.removeNodeUpdatedListener(nodeUpdatedEvent);
  }

  /**
   * Adds the given listener for the {@code EdgeCreated} event that occurs when an edge has been created.
   * <p>
   * This event can be used to further customize the created edge.
   * </p>
   * <p>
   * New edges are created either in response to calling {@link #buildGraph()}, or in response to calling {@link
   * #updateGraph()} when there are new items in {@link #getChildProvider() ChildProvider}.
   * </p>
   *
   * @param edgeCreatedEvent The listener to add.
   * @see #addEdgeUpdatedListener(IEventListener)
   * @see #removeEdgeCreatedListener(IEventListener)
   */
  public final void addEdgeCreatedListener(IEventListener<GraphBuilderItemEventArgs<IEdge, Object>> edgeCreatedEvent) {
    graphBuilder.addEdgeCreatedListener(edgeCreatedEvent);
  }

  /**
   * Removes the given listener for the {@code EdgeCreated} event that occurs when an edge has been created.
   * <p>
   * This event can be used to further customize the created edge.
   * </p>
   * <p>
   * New edges are created either in response to calling {@link #buildGraph()}, or in response to calling {@link
   * #updateGraph()} when there are new items in {@link #getChildProvider() ChildProvider}.
   * </p>
   *
   * @param edgeCreatedEvent The listener to remove.
   * @see #addEdgeUpdatedListener(IEventListener)
   * @see #addEdgeCreatedListener(IEventListener)
   */
  public final void removeEdgeCreatedListener(
      IEventListener<GraphBuilderItemEventArgs<IEdge, Object>> edgeCreatedEvent) {
    graphBuilder.removeEdgeCreatedListener(edgeCreatedEvent);
  }

  /**
   * Adds the given listener for the {@code EdgeUpdated} event that occurs when an edge has been updated.
   * <p>
   * This event can be used to update customizations added in an event handler for {@link
   * #addEdgeCreatedListener(IEventListener) EdgeCreated}.
   * </p>
   * <p>
   * Edges are updated in response to calling {@link #updateGraph()} for items that haven't been added anew in {@link
   * #getChildProvider() ChildProvider} since the last call to {@link #buildGraph()} or {@link #updateGraph()}.
   * </p>
   *
   * @param edgeUpdatedEvent The listener to add.
   * @see #addEdgeCreatedListener(IEventListener)
   * @see #removeEdgeUpdatedListener(IEventListener)
   */
  public final void addEdgeUpdatedListener(IEventListener<GraphBuilderItemEventArgs<IEdge, Object>> edgeUpdatedEvent) {
    graphBuilder.addEdgeUpdatedListener(edgeUpdatedEvent);
  }

  /**
   * Removes the given listener for the {@code EdgeUpdated} event that occurs when an edge has been updated.
   * <p>
   * This event can be used to update customizations added in an event handler for {@link
   * #addEdgeCreatedListener(IEventListener) EdgeCreated}.
   * </p>
   * <p>
   * Edges are updated in response to calling {@link #updateGraph()} for items that haven't been added anew in {@link
   * #getChildProvider() ChildProvider} since the last call to {@link #buildGraph()} or {@link #updateGraph()}.
   * </p>
   *
   * @param edgeUpdatedEvent The listener to remove.
   * @see #addEdgeCreatedListener(IEventListener)
   * @see #addEdgeUpdatedListener(IEventListener)
   */
  public final void removeEdgeUpdatedListener(
      IEventListener<GraphBuilderItemEventArgs<IEdge, Object>> edgeUpdatedEvent) {
    graphBuilder.removeEdgeUpdatedListener(edgeUpdatedEvent);
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
    graphBuilder.addGroupNodeCreatedListener(groupNodeCreatedEvent);
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
    graphBuilder.removeGroupNodeCreatedListener(groupNodeCreatedEvent);
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
    graphBuilder.addGroupNodeUpdatedListener(groupNodeUpdatedEvent);
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
    graphBuilder.removeGroupNodeUpdatedListener(groupNodeUpdatedEvent);
  }

  /**
   * Retrieves the object from which a given item has been created.
   *
   * @param item The item to get the object for.
   * @return The object from which the graph item has been created.
   * @see #getNode(Object)
   * @see #getGroup(Object)
   */
  public final Object getSourceObject(IModelItem item) {
    return graphBuilder.getSourceObject(item);
  }

  /**
   * Retrieves the node associated with an object from the {@link #getNodesSource() NodesSource}.
   *
   * @param nodeObject An object from the {@link #getNodesSource() NodesSource}.
   * @return The node associated with {@code nodeObject}, or {@code null} in case there is no node associated with that
   * object. This can happen if {@code nodeObject} is new since the last call to {@link #updateGraph()}.
   * @see #getGroup(Object)
   * @see #getSourceObject(IModelItem)
   */
  public final INode getNode(TNode nodeObject) {
    return graphBuilder.getNode(nodeObject);
  }

  /**
   * Retrieves the group node associated with an object from the {@link #getGroupsSource() GroupsSource}.
   *
   * @param groupObject An object from the {@link #getGroupsSource() GroupsSource}.
   * @return The group node associated with {@code groupObject}, or {@code null} in case there is no group node
   * associated with that object. This can happen if {@code groupObject} is new since the last call to {@link
   * #updateGraph()}.
   * @see #getNode(Object)
   * @see #getSourceObject(IModelItem)
   */
  public final INode getGroup(TGroup groupObject) {
    return graphBuilder.getGroup(groupObject);
  }

  /**
   * A {@link AdjacentNodesGraphBuilder} that provides the additional {@link #getEdgeLabelProvider() EdgeLabelProvider}
   * and makes protected methods work with the decorator pattern.
   * <p>
   * Protected methods work in the following way: Each overridden protected method delegates to the corresponding method
   * of the {@link TreeBuilder} instance to allow users to override it. Then, that method calls a new method of this
   * class that provides access to the original base implementation.
   * </p>
   */
  private static final class MyAdjacentNodesGraphBuilder<TNode, TGroup> extends AdjacentNodesGraphBuilder<TNode, TGroup> {
    private final TreeBuilder<TNode, TGroup> treeBuilder;

    public MyAdjacentNodesGraphBuilder(TreeBuilder<TNode, TGroup> treeBuilder, IGraph graph) {
      super(graph);
      this.treeBuilder = treeBuilder;
    }

    @Override
    protected INode createNode(IGraph graph, INode parent, Object labelData, TNode nodeObject) {
      return treeBuilder.createNode(graph, parent, labelData, nodeObject);
    }

    public final INode createNodeBase(IGraph graph, INode parent, Object labelData, TNode data) {
      return super.createNode(graph, parent, labelData, data);
    }

    @Override
    protected IEdge createEdge(IGraph graph, INode source, INode target, Object labelData) {
      return treeBuilder.createEdge(graph, source, target, labelData);
    }

    public final IEdge createEdgeBase(IGraph graph, INode sourceNode, INode targetNode, Object labelData) {
      return super.createEdge(graph, sourceNode, targetNode, labelData);
    }

    @Override
    protected INode createGroupNode(IGraph graph, Object labelData, TGroup groupObject) {
      return treeBuilder.createGroupNode(graph, labelData, groupObject);
    }

    public final INode createGroupNodeBase(IGraph graph, Object labelData, TGroup data) {
      return super.createGroupNode(graph, labelData, data);
    }

    @Override
    protected void updateNode(IGraph graph, INode node, INode parent, Object labelData, TNode nodeObject) {
      treeBuilder.updateNode(graph, node, parent, labelData, nodeObject);
    }

    public final void updateNodeBase(IGraph graph, INode node, INode parent, Object labelData, TNode data) {
      super.updateNode(graph, node, parent, labelData, data);
    }

    @Override
    protected void updateEdge(IGraph graph, IEdge edge, Object labelData) {
      treeBuilder.updateEdge(graph, edge, labelData);
    }

    public final void updateEdgeBase(IGraph graph, IEdge edge, Object labelData) {
      super.updateEdge(graph, edge, labelData);
    }

    @Override
    protected void updateGroupNode(IGraph graph, INode groupNode, Object labelData, TGroup groupObject) {
      treeBuilder.updateGroupNode(graph, groupNode, labelData, groupObject);
    }

    public final void updateGroupNodeBase(IGraph graph, INode groupNode, Object labelData, TGroup data) {
      super.updateGroupNode(graph, groupNode, labelData, data);
    }
  }

}
