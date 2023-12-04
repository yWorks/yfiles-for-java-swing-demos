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
package builder.compatibility;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelDefaults;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.builder.EdgeCreator;
import com.yworks.yfiles.graph.builder.NodeCreator;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.utils.IEventListener;
import com.yworks.yfiles.utils.IListEnumerable;

import java.util.Objects;
import java.util.function.Function;

final class GraphBuilderHelper<TNode, TGroup, TEdge> {

  private final INodeCreation<TNode> builderCreateNode;
  private final INodeUpdate<TNode> builderUpdateNode;
  private final IGroupNodeCreation<TGroup> builderCreateGroupNode;
  private final IGroupNodeUpdate<TGroup> builderUpdateGroupNode;
  private final IEdgeCreation<TEdge> builderCreateEdge;
  private final IEdgeUpdate<TEdge> builderUpdateEdge;



  Function<TNode, Object> nodeLabelProvider;
  Function<TEdge, Object> edgeLabelBinding;
  Function<TGroup, Object> groupLabelProvider;
  EdgeLabelProvider<TNode> edgeLabelProvider;


  private final IGraph graph;

  GraphBuilderHelper(IGraph graph,
                     INodeCreation<TNode> createNode,
                     INodeUpdate<TNode> updateNode,
                     IGroupNodeCreation<TGroup> createGroupNode,
                     IGroupNodeUpdate<TGroup> updateGroupNode,
                     IEdgeCreation<TEdge> createEdge,
                     IEdgeUpdate<TEdge> updateEdge) {
    this.graph = graph;
    this.builderCreateNode = createNode;
    this.builderUpdateNode = updateNode;
    this.builderCreateGroupNode = createGroupNode;
    this.builderUpdateGroupNode = updateGroupNode;
    this.builderCreateEdge = createEdge;
    this.builderUpdateEdge = updateEdge;
  }




  /**
   * Creates an edge in the given graph. Raises the {@link #addEdgeCreatedListener(IEventListener)} event.
   *
   * @param graph      The graph to create the edge in.
   * @param source     The source node of the edge.
   * @param target     The target node of the edge.
   * @param labelData  The data object to create a label from.
   * @param edgeObject The data object which is represented by the edge.
   * @return The newly created edge.
   */
  public IEdge createEdge(IGraph graph, INode source, INode target,
                          Object labelData, TEdge edgeObject) {
    if (source == null || target == null) {
      // early exit if source or target node doesn't exist
      return null;
    }
    IEdge edge = graph.createEdge(source, target, graph.getEdgeDefaults().getStyleInstance(), edgeObject);
    if (labelData != null) {
      graph.addLabel(edge, labelData.toString(), null, null, null, labelData);
    }
    this.onEdgeCreated(edge, edgeObject);
    return edge;
  }

  /**
   * Creates a group node in the given graph. Raises the {@link #addGroupNodeCreatedListener(IEventListener)} event.
   *
   * @param graph       The graph to create the node in.
   * @param labelData   The data object to create a label from.
   * @param groupObject The data object which is represented by the node.
   * @return The newly created node.
   */
  public INode createGroupNode(IGraph graph, Object labelData, TGroup groupObject) {
    INodeDefaults nodeDefaults = graph.getGroupNodeDefaults();
    RectD layout = new RectD(PointD.ORIGIN, nodeDefaults.getSize());
    INode groupNode = graph.createGroupNode(null, layout, nodeDefaults.getStyleInstance(), groupObject);
    if (labelData != null) {
      this.graph.addLabel(groupNode, labelData.toString(), null, null, null, labelData);
    }
    this.onGroupCreated(groupNode, groupObject);
    return groupNode;
  }

  /**
   * Creates a node in the given graph. Raises the {@link #addNodeCreatedListener(IEventListener)} event.
   *
   * @param graph      The graph to create the node in.
   * @param parent     The parent node of the node.
   * @param labelData  The data object to create a label from.
   * @param nodeObject The data object which is represented by the node.
   * @return The newly created node.
   */
  public INode createNode(IGraph graph, INode parent, Object labelData, TNode nodeObject) {
    try {
      INode node = graph.createNode(parent, null, graph.getNodeDefaults().getStyleInstance(), nodeObject);
      if (labelData != null) {
        this.graph.addLabel(node, labelData.toString(), null, null, null, labelData);
      }
      this.onNodeCreated(node, nodeObject);
      return node;
    } catch (Exception e) {
      if ("No node created!".equals(e.getMessage())) {
        // This usually only happens when the GraphBuilder is used on a foldingView
        throw new IllegalStateException(
            "Could not create node. When folding is used, make sure to use the master graph in the GraphBuilder."
        );
      }
      throw e;
    }
  }

  /**
   * Updates an edge in the given graph. Assumes that the edge already exists in the graph. Renews the tag and updates
   * the edge's labels. Raises the {@link #addEdgeUpdatedListener(IEventListener)} event.
   *
   * @param graph      The graph to create the edge in.
   * @param edge       The edge to update.
   * @param labelData  The data object to create a label from.
   * @param edgeObject The data object which is represented by the edge.
   */
  public void updateEdge(IGraph graph, IEdge edge, Object labelData, TEdge edgeObject) {
    if (edge.getTag() != edgeObject) {
      edge.setTag(edgeObject);
    }
    updateLabels(graph, graph.getEdgeDefaults().getLabelDefaults(), edge, labelData);
    this.onEdgeUpdated(edge, edgeObject);
  }

  /**
   * Updates a group node in the given graph. Assumes that the node already exists in the graph. Renews the tag and
   * updates the node's labels. Raises the {@link #addGroupNodeUpdatedListener(IEventListener)} event.
   *
   * @param graph       The graph to create the node in.
   * @param groupNode   The node to update.
   * @param labelData   The data object to create a label from.
   * @param groupObject The data object which is represented by the node.
   */
  public void updateGroupNode(IGraph graph, INode groupNode, Object labelData, TGroup groupObject) {
    if (groupNode.getTag() != groupObject) {
      groupNode.setTag(groupObject);
    }
    updateLabels(graph, graph.getNodeDefaults().getLabelDefaults(), groupNode, labelData);
    this.onGroupUpdated(groupNode, groupObject);
  }

  /**
   * Updates a node in the given graph. Assumes that the node already exists in the graph. Renews the tag and updates
   * the node's labels. Raises the {@link #addNodeUpdatedListener(IEventListener)} event.
   *
   * @param graph      The graph to create the node in.
   * @param node       The node to update.
   * @param parent     The (new) parent of the node to update.
   * @param labelData  The data object to create a label from.
   * @param nodeObject The data object which is represented by the node.
   */
  public void updateNode(IGraph graph, INode node, INode parent, Object labelData, TNode nodeObject) {
    if (node.getTag() != (Object) nodeObject) {
      node.setTag(nodeObject);
    }
    updateLabels(graph, graph.getNodeDefaults().getLabelDefaults(), node, labelData);
    if (graph.getParent(node) != parent) {
      graph.setParent(node, parent);
    }
    this.onNodeUpdated(node, nodeObject);
  }

  /**
   * Updates the labels of a given owner.
   *
   * @param graph         The graph the owner belongs to.
   * @param labelDefaults The defaults to create the labels with.
   * @param item          The owner of the label.
   * @param labelData     The data to create the labels from.
   */
  private static void updateLabels(IGraph graph, ILabelDefaults labelDefaults, ILabelOwner item, Object labelData) {
    IListEnumerable<ILabel> labels = item.getLabels();
    if (labelData == null) {
      while (labels.size() > 0) {
        graph.remove(labels.getItem(labels.size() - 1));
      }
    } else if (labels.size() == 0) {
      ILabelModelParameter layoutParameter = labelDefaults.getLayoutParameterInstance(item);
      ILabelStyle labelStyle = labelDefaults.getStyleInstance(item);
      graph.addLabel(item, labelData.toString(), layoutParameter, labelStyle, null, labelData);
    } else if (labels.size() > 0) {
      ILabel label = labels.getItem(0);
      if (!Objects.equals(label.getText(), labelData.toString())) {
        graph.setLabelText(label, labelData.toString());
      }
      if (label.getTag() != labelData) {
        label.setTag(labelData);
      }
    }
  }



  /**
   * Returns a {@link NodeCreator} which works on this instance.
   */
  public NodeCreator<TNode> createNodeCreator() {
    return new GraphBuilderNodeCreator(this);
  }

  /**
   * Returns a {@link NodeCreator} which works on this instance.
   */
  public NodeCreator<TGroup> createGroupCreator() {
    return new GraphBuilderGroupCreator(this);
  }

  /**
   * Returns a {@link EdgeCreator} which works on this instance.
   */
  public EdgeCreator<TEdge> createEdgeCreator(boolean labelDataFromSourceAndTarget) {
    return new GraphBuilderEdgeCreator(this, labelDataFromSourceAndTarget);
  }

  /**
   * Returns a {@link EdgeCreator} which works on this instance.
   */
  public EdgeCreator<TEdge> createEdgeCreator() {
    return new GraphBuilderEdgeCreator(this, false);
  }

  /**
   * A custom {@link NodeCreator} for groups which delegates to this instance's {@link
   * GraphBuilderHelper#builderCreateGroupNode} and {@link GraphBuilderHelper#builderUpdateGroupNode} callbacks.
   */
  class GraphBuilderNodeCreator extends NodeCreator<TNode> {
    private final GraphBuilderHelper<TNode, TGroup, TEdge> helper;

    GraphBuilderNodeCreator(GraphBuilderHelper<TNode, TGroup, TEdge> helper) {
      this.helper = helper;
    }

    public INode createNode(IGraph graph, INode parent, TNode dataItem) {
      Object labelData = this.getLabelData(dataItem);
      TNode nodeObject = this.getNodeObject(dataItem);
      return this.helper.builderCreateNode.create(graph, parent, labelData, nodeObject);
    }

    public void updateNode(IGraph graph, INode node, INode parent, TNode dataItem) {
      Object labelData = this.getLabelData(dataItem);
      TNode nodeObject = this.getNodeObject(dataItem);
      this.helper.builderUpdateNode.update(graph, node, parent, labelData, nodeObject);
    }

    private TNode getNodeObject(TNode dataItem) {
      if (this.getTagProvider() != null) {
        return (TNode) this.getTagProvider().apply(dataItem);
      }
      return dataItem;
    }

    private Object getLabelData(TNode dataItem) {
      return this.helper.nodeLabelProvider != null
          ? this.helper.nodeLabelProvider.apply(dataItem)
          : null;
    }
  }

  /**
   * A custom {@link NodeCreator} for groups which delegates to this instance's {@link
   * GraphBuilderHelper#builderCreateGroupNode} and {@link GraphBuilderHelper#builderUpdateGroupNode} callbacks.
   */
  class GraphBuilderGroupCreator extends NodeCreator<TGroup> {
    private final GraphBuilderHelper<TNode, TGroup, TEdge> helper;

    GraphBuilderGroupCreator(GraphBuilderHelper<TNode, TGroup, TEdge> helper) {
      this.helper = helper;
    }

    public INode createNode(IGraph graph, INode parent, TGroup dataItem) {
      Object labelData = this.getLabelData(dataItem);
      TGroup nodeObject = this.getNodeObject(dataItem);
      INode node = this.helper.builderCreateGroupNode.create(graph, labelData, nodeObject);
      graph.setParent(node, parent);
      return node;
    }

    public void updateNode(IGraph graph, INode node, INode parent, TGroup dataItem) {
      Object labelData = this.getLabelData(dataItem);
      TGroup nodeObject = this.getNodeObject(dataItem);
      this.helper.builderUpdateGroupNode.update(graph, node, labelData, nodeObject);
      if (graph.getParent(node) != parent) {
        graph.setParent(node, parent);
      }
    }

    private TGroup getNodeObject(TGroup dataItem) {
      if (this.getTagProvider() != null) {
        return (TGroup) this.getTagProvider().apply(dataItem);
      }
      return dataItem;
    }

    private Object getLabelData(TGroup dataItem) {
      return this.helper.groupLabelProvider != null
          ? this.helper.groupLabelProvider.apply(dataItem)
          : null;
    }
  }

  /**
   * A custom {@link EdgeCreator} which delegates to this instance's {@link GraphBuilderHelper#builderCreateEdge} and
   * {@link GraphBuilderHelper#builderUpdateEdge} callbacks
   */
  class GraphBuilderEdgeCreator extends EdgeCreator<TEdge> {
    private final GraphBuilderHelper<TNode, TGroup, TEdge> helper;
    private final boolean labelDataFromSourceAndTarget;

    public GraphBuilderEdgeCreator(GraphBuilderHelper<TNode, TGroup, TEdge> helper,
                                   boolean labelDataFromSourceAndTarget) {
      this.helper = helper;
      this.labelDataFromSourceAndTarget = labelDataFromSourceAndTarget;
    }

    public IEdge createEdge(IGraph graph, INode source, INode target, TEdge dataItem) {
      Object labelData = this.getLabelData(dataItem, source, target);
      TEdge edgeObject = this.getEdgeObject(dataItem);
      IEdge edge = this.helper.builderCreateEdge.create(graph, source, target, labelData, edgeObject);
      if (edge == null) {
        throw new IllegalStateException("An edge must be created by createEdge.");
      }
      return edge;
    }

    public void updateEdge(IGraph graph, IEdge edge, TEdge dataItem) {
      Object labelData = this.getLabelData(dataItem, edge.getSourceNode(), edge.getTargetNode());
      TEdge edgeObject = this.getEdgeObject(dataItem);
      this.helper.builderUpdateEdge.update(graph, edge, labelData, edgeObject);
    }


    private TEdge getEdgeObject(TEdge dataItem) {
      if (this.getTagProvider() != null) {
        return (TEdge) this.getTagProvider().apply(dataItem);
      }
      return dataItem;
    }

    private Object getLabelData(TEdge dataItem, INode source, INode target) {
      if (this.labelDataFromSourceAndTarget) {
        return this.helper.edgeLabelProvider != null
            ? this.helper.edgeLabelProvider.edgeLabelProvider((TNode) source.getTag(), (TNode) target.getTag())
            : null;
      } else {
        return this.helper.edgeLabelBinding != null
            ? this.helper.edgeLabelBinding.apply(dataItem)
            : null;
      }
    }
  }




  private IEventListener<GraphBuilderItemEventArgs<INode, TGroup>> groupNodeCreatedEvent;

  public final void addGroupNodeCreatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TGroup>> groupNodeCreatedEvent) {
    this.groupNodeCreatedEvent = groupNodeCreatedEvent;
  }

  public final void removeGroupNodeCreatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TGroup>> groupNodeCreatedEvent) {
    this.groupNodeCreatedEvent =null;
  }

  private IEventListener<GraphBuilderItemEventArgs<INode, TNode>> nodeCreatedEvent;

  public final void addNodeCreatedListener(IEventListener<GraphBuilderItemEventArgs<INode, TNode>> nodeCreatedEvent) {
    this.nodeCreatedEvent = nodeCreatedEvent;
  }

  public final void removeNodeCreatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TNode>> nodeCreatedEvent) {
    this.nodeCreatedEvent = null;
  }

  private IEventListener<GraphBuilderItemEventArgs<INode, TGroup>> groupNodeUpdatedEvent;

  public final void addGroupNodeUpdatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TGroup>> groupNodeUpdatedEvent) {
    this.groupNodeUpdatedEvent = groupNodeUpdatedEvent;
  }

  public final void removeGroupNodeUpdatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TGroup>> groupNodeUpdatedEvent) {
    this.groupNodeUpdatedEvent = null;
  }

  private IEventListener<GraphBuilderItemEventArgs<INode, TNode>> nodeUpdatedEvent;

  public final void addNodeUpdatedListener(IEventListener<GraphBuilderItemEventArgs<INode, TNode>> nodeUpdatedEvent) {
    this.nodeUpdatedEvent = nodeUpdatedEvent;
  }

  public final void removeNodeUpdatedListener(
      IEventListener<GraphBuilderItemEventArgs<INode, TNode>> nodeUpdatedEvent) {
    this.nodeUpdatedEvent = null;
  }

  private IEventListener<GraphBuilderItemEventArgs<IEdge, TEdge>> edgeCreatedEvent;

  public final void addEdgeCreatedListener(IEventListener<GraphBuilderItemEventArgs<IEdge, TEdge>> edgeCreatedEvent) {
    this.edgeCreatedEvent = edgeCreatedEvent;
  }

  public final void removeEdgeCreatedListener(
      IEventListener<GraphBuilderItemEventArgs<IEdge, TEdge>> edgeCreatedEvent) {
    this.edgeCreatedEvent = null;
  }

  private IEventListener<GraphBuilderItemEventArgs<IEdge, TEdge>> edgeUpdatedEvent;

  public final void addEdgeUpdatedListener(IEventListener<GraphBuilderItemEventArgs<IEdge, TEdge>> edgeUpdatedEvent) {
    this.edgeUpdatedEvent = edgeUpdatedEvent;
  }

  public final void removeEdgeUpdatedListener(
      IEventListener<GraphBuilderItemEventArgs<IEdge, TEdge>> edgeUpdatedEvent) {
    this.edgeUpdatedEvent = null;
  }

  private void onNodeCreated(INode node, TNode dataItem) {
    if (this.nodeCreatedEvent != null) {
      nodeCreatedEvent.onEvent(this, new GraphBuilderItemEventArgs<INode, TNode>(this.graph, node, dataItem));
    }
  }

  private void onNodeUpdated(INode node, TNode dataItem) {
    if (this.nodeUpdatedEvent != null) {
      nodeUpdatedEvent.onEvent(this, new GraphBuilderItemEventArgs<INode, TNode>(this.graph, node, dataItem));
    }
  }

  private void onGroupCreated(INode groupNode, TGroup dataItem) {
    if (this.groupNodeCreatedEvent != null) {
      groupNodeCreatedEvent.onEvent(this,
          new GraphBuilderItemEventArgs<INode, TGroup>(this.graph, groupNode, dataItem));
    }
  }

  private void onGroupUpdated(INode groupNode, TGroup dataItem) {
    if (this.groupNodeUpdatedEvent != null) {
      this.groupNodeUpdatedEvent.onEvent(this,
          new GraphBuilderItemEventArgs<INode, TGroup>(this.graph, groupNode, dataItem));
    }
  }

  private void onEdgeCreated(IEdge edge, TEdge dataItem) {
    if (this.edgeCreatedEvent != null) {
      this.edgeCreatedEvent.onEvent(this, new GraphBuilderItemEventArgs<IEdge, TEdge>(this.graph, edge, dataItem));
    }
  }

  private void onEdgeUpdated(IEdge edge, TEdge dataItem) {
    if (this.edgeUpdatedEvent != null) {
      this.edgeUpdatedEvent.onEvent(this, new GraphBuilderItemEventArgs<IEdge, TEdge>(this.graph, edge, dataItem));
    }
  }


}
