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
package layout.hierarchiclayout;

import com.yworks.yfiles.graph.LayoutUtilities;
import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IAnimation;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.PopulateItemPopupMenuEventArgs;
import com.yworks.yfiles.layout.PortConstraint;
import com.yworks.yfiles.layout.hierarchic.GivenLayersLayerer;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.utils.ItemEventArgs;
import com.yworks.yfiles.view.Pen;
import toolkit.AbstractDemo;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * See the usage of {@link com.yworks.yfiles.layout.hierarchic.HierarchicLayout} and how to
 * <ul>
 * <li>incrementally add nodes and edges</li>
 * <li>dynamically assign port constraints</li>
 * <li>create new nodes and observe how they are inserted into the drawing near the place they have been created</li>
 * <li>create new edges and watch the routings being calculated immediately</li>
 * <li>drag the first and last bend of an edge to interactively assign or reset port constraints</li>
 * <li>use the popup menu to reroute selected edges or optimize selected nodes locations</li>
 * </ul>
 */
public class HierarchicLayoutDemo extends AbstractDemo {
  // visualizes the layers and manages layer regions and contains tests
  private LayerVisualCreator layerVisualCreator = new LayerVisualCreator();

  // holds for each node the layer
  private IMapper<INode, Integer> layerMapper = new Mapper<>();

  // whether a layout is running
  private boolean updateLayout;

  // holds temporary layer reassignments that will be assigned during the next layout
  private Mapper<INode, Integer> newLayerMapper = new Mapper<>();

  // holds for each edge a port constraint for the source end
  private Mapper<IEdge, PortConstraint> sourcePortConstraints = new Mapper<>();

  // holds for each edge a port constraint for the target end
  private Mapper<IEdge, PortConstraint> targetPortConstraints = new Mapper<>();

  // holds a list of nodes to insert incrementally during the next layout
  private List<INode> incrementalNodes = new ArrayList<>();

  // holds a list of edges to reroute incrementally during the next layout
  private List<IEdge> incrementalEdges = new ArrayList<>();

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, graphComponent));
    toolBar.addSeparator();
    super.configureToolBar(toolBar);
  }

  /**
   * Initializes the graph and the input modes and registers the data templates.
   */
  public void initialize() {
    // initialize the graph
    initializeGraph();

    // initialize the input mode
    initializeInputModes();

    // set a custom prepared GraphMLIOHandler instance to the GraphComponent which will be used for default file operations
    GraphMLIOHandler handler = new GraphMLIOHandler();
    graphComponent.setGraphMLIOHandler(handler);

    // enable file operations
    graphComponent.setFileIOEnabled(true);

    // configure the GraphMLIOHandler so that a layout automatically runs after loading a graph
    handler.addParsedListener((source, args) -> {
      if (!this.updateLayout) {
        // create an HierarchicLayout instance to provide an initial layout
        HierarchicLayout ihl = createLayoutAlgorithm();

        // provide additional data to configure the layout algorithm
        HierarchicLayoutData ihlData = new HierarchicLayoutData();
        // retrieve the layer of each node after the layout run to update the layer visualization
        ihlData.setLayerIndices(layerMapper);

        // run the layout algorithm
        graphComponent.getGraph().applyLayout(ihl, ihlData);

        // update the layer visualization using the collected the layer information
        layerVisualCreator.updateLayerBounds(graphComponent.getGraph(), layerMapper);

        // fit it nicely into the component
        graphComponent.fitGraphBounds();
      }
    });
  }

  /**
   * Centers the graph in the graph component.
   */
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Factory method that creates the layout algorithm instances that are used by this demo.
   */
  private HierarchicLayout createLayoutAlgorithm() {
    HierarchicLayout ihl = new HierarchicLayout();
    ihl.setOrthogonalRoutingEnabled(true);
    ihl.setRecursiveGroupLayeringEnabled(false);
    return ihl;
  }

  /**
   * Calls {@link #createEditorMode()} and registers the result as the {@link
   * com.yworks.yfiles.view.CanvasComponent#getInputMode()}.
   */
  protected void initializeInputModes() {
    // create the interaction mode
    graphComponent.setInputMode(createEditorMode());

    // display the layers
    graphComponent.getBackgroundGroup().addChild(layerVisualCreator, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
  }

  /**
   * Creates the default input mode for the {@link GraphComponent}, a {@link com.yworks.yfiles.view.input.GraphEditorInputMode}.
   * @return a specializes new GraphEditorInputMode instance
   */
  private IInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();

    // register hooks whenever something is dragged or resized
    mode.getHandleInputMode().addDragFinishedListener(this::updateLayout);
    mode.getMoveInputMode().addDragFinishedListener(this::updateLayout);

    // ... and when new nodes are created interactively
    mode.addNodeCreatedListener(this::onNodeCreated);
    // ... or edges
    mode.getCreateEdgeInputMode().addEdgeCreatedListener(this::onEdgeCreated);

    // create popup menu that allows the user to reroute an edge or re-layout a node
    mode.addPopulateItemPopupMenuListener(this::onPopulateItemPopupMenu);

    return mode;
  }

  /**
   * Called when an edge has been created interactively.
   */
  private void onEdgeCreated(Object source, ItemEventArgs<IEdge> args) {
    incrementalEdges.add(args.getItem());
    updateLayout(source, args);
  }

  /**
   * Called when the popup menu should be populated for a given item.
   * @param source the event source
   * @param args the event argument instance containing the event data
   */
  private void onPopulateItemPopupMenu(Object source, PopulateItemPopupMenuEventArgs<IModelItem> args) {
    JPopupMenu popupMenu = (JPopupMenu) args.getMenu();
    // see if it's a node but not a not empty group node
    if (args.getItem() instanceof INode) {
      INode node = (INode) args.getItem();

      if (graphComponent.getGraph().getChildren(node).size() == 0) {
        // see if it's already selected
        ISelectionModel<INode> selectedNodes = graphComponent.getSelection().getSelectedNodes();
        if (!selectedNodes.isSelected(node)) {
          // no - make it the only selected node
          selectedNodes.clear();
        }
        // make sure the node is selected
        selectedNodes.setSelected(node, true);
        graphComponent.setCurrentItem(node);
        // mark all selected nodes for incremental layout
        JMenuItem item = new JMenuItem("Reinsert Incrementally");
        item.addActionListener(e -> {
          selectedNodes.forEach(incrementalNodes::add);
          updateLayout(item, args);
        });
        popupMenu.add(item);
        args.setHandled(true);
      }
    }
    // if it's an edge...
    if (args.getItem() instanceof IEdge) {
      IEdge edge = (IEdge) args.getItem();

      // update selection state
      ISelectionModel<IEdge> selectedEdges = graphComponent.getSelection().getSelectedEdges();
      if (!selectedEdges.isSelected(edge)) {
        selectedEdges.clear();
      }
      selectedEdges.setSelected(edge, true);
      graphComponent.setCurrentItem(edge);
      // and offer option to reroute selected edges
      JMenuItem item = new JMenuItem("Reroute");
      item.addActionListener(e -> {
        selectedEdges.forEach(incrementalEdges::add);
        updateLayout(item, args);
      });
      popupMenu.add(item);
      args.setHandled(true);
    }
  }

  /**
   * Called when a node has been created interactively.
   */
  private void onNodeCreated(Object source, ItemEventArgs<INode> args) {
    int newLayer = layerVisualCreator.getLayer(args.getItem().getLayout().getCenter());
    newLayerMapper.setValue(args.getItem(), newLayer);
    updateLayout(source, args);
  }

  /**
   * Core method that recalculates and updates the layout.
   */
  private void updateLayout(Object source, IEventArgs args) {
    // make sure we are not re-entrant
    if (updateLayout) {
      return;
    }
    updateLayout = true;

    // update the layers for moved nodes
    updateMovedNodes();

    // create and configure the HierarchicLayout
    HierarchicLayout ihl = createLayoutAlgorithm();
    // rearrange only the incremental graph elements, the
    // remaining elements are not, or only slightly, changed
    ihl.setLayoutMode(LayoutMode.INCREMENTAL);
    // use the GivenLayersLayerer for all non-incremental nodes
    ihl.setFixedElementsLayerer(new GivenLayersLayerer());

    // provide additional data to configure the HierarchicLayout
    HierarchicLayoutData ihlData = new HierarchicLayoutData();
    // specify the layer of each non-incremental node
    ihlData.setGivenLayersLayererIds(layerMapper);
    // retrieve the layer of each incremental node after the layout run to update the layer visualization
    ihlData.setLayerIndices(layerMapper);
    // specify port constrains for the source of each edge
    ihlData.setSourcePortConstraints(sourcePortConstraints);
    // specify port constrains for the target of each edge
    ihlData.setTargetPortConstraints(targetPortConstraints);
    // specify the nodes to rearrange
    ihlData.getIncrementalHints().setIncrementalLayeringNodes(new ArrayList<>(incrementalNodes));
    // specify the edges to rearrange
    ihlData.getIncrementalHints().setIncrementalSequencingItems(new ArrayList<>(incrementalEdges));
    // forget the nodes and edges for the next run
    incrementalNodes.clear();
    incrementalEdges.clear();

    // now layout the graph using the HierarchicLayout and animate the result
    LayoutExecutor executor = new LayoutExecutor(graphComponent, graphComponent.getGraph(), ihl) {
      @Override
      protected IAnimation createMorphAnimation() {
        // use a customized morph animation that also morphs the layer visualization
        IAnimation layoutAnimation = LayoutUtilities.createLayoutAnimation(this.getGraph(), this.getLayoutGraph(), this.getDuration());
        IAnimation updateLayerAnimation = new IAnimation() {
          @Override
          public void initialize() {}

          @Override
          public void animate(final double time) {
            layerVisualCreator.updateLayerBounds(graphComponent.getGraph(), layerMapper);
          }

          @Override
          public void cleanUp() {}

          @Override
          public Duration getPreferredDuration() {
            return Duration.ofMillis(1);
          }
        };
        return IAnimation.createParallelAnimation(layoutAnimation, updateLayerAnimation);
      }
    };
    executor.setDuration(Duration.ofSeconds(1));
    executor.setViewportAnimationEnabled(true);
    executor.setEasedAnimationEnabled(true);
    executor.setTargetBoundsInsets(new InsetsD(20));
    executor.setContentRectUpdatingEnabled(true);
    executor.addLayoutFinishedListener((source1, args1) -> updateLayout = false);
    executor.setLayoutData(ihlData);
    executor.start();
  }

  /**
   * Updates the layers for moved nodes.
   */
  private void updateMovedNodes() {
    if (newLayerMapper.getEntries().iterator().hasNext()) {
      // spread out existing layers
      for (INode node : graphComponent.getGraph().getNodes()) {
        int layer = layerMapper.getValue(node) != null ? layerMapper.getValue(node) : 0;
        layerMapper.setValue(node, layer * 2);
      }
      for (Map.Entry<INode, Integer> pair : newLayerMapper.getEntries()) {
        INode node = pair.getKey();
        // if a node has been moved, reinsert the adjacent edges incrementally and not from sketch
        graphComponent.getGraph().edgesAt(node).forEach(incrementalEdges::add);
        int newLayerIndex = pair.getValue();
        if (newLayerIndex == Integer.MAX_VALUE) {
          continue;
        }
        if (newLayerIndex < 0) {
          int beforeLayer = -(newLayerIndex + 1);
          layerMapper.setValue(node, beforeLayer * 2 - 1);
        } else {
          layerMapper.setValue(node, newLayerIndex * 2);
        }
      }
      newLayerMapper.clear();
    }
  }

  /**
   * Initializes the graph instance setting default styles and creating a small sample graph.
   */
  private void initializeGraph() {
    IGraph graph = graphComponent.getGraph();

    // set some nice defaults
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.ORANGE);
    graph.getNodeDefaults().setStyle(nodeStyle);
    graph.getNodeDefaults().setSize(new SizeD(60, 30));

    ShapeNodeStyle groupNodeStyle = new ShapeNodeStyle();
    groupNodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    groupNodeStyle.setPen(new Pen(Colors.DARK_BLUE, 2));
    groupNodeStyle.setPaint(null);
    graph.getGroupNodeDefaults().setStyle(groupNodeStyle);

    // register a custom PositionHandler for the nodes.
    // this enables interactive layer reassignment with layer preview
    graphComponent.getGraph().getDecorator().getNodeDecorator().getPositionHandlerDecorator().setImplementationWrapper(
        node -> graph.getChildren(node).size() == 0,
        (node, positionHandler) -> new LayerPositionHandler(layerVisualCreator, node, positionHandler, newLayerMapper)
    );

    // register custom handles for the first and last bends of an edge
    // this enables interactive port constraint assignment.
    graphComponent.getGraph().getDecorator().getBendDecorator().getHandleDecorator().setImplementationWrapper(
        bend -> {
          IListEnumerable<IBend> bends = bend.getOwner().getBends();
          return bends.getItem(0) == bend || bends.getItem(bends.size() - 1) == bend;
        },
        this::createBendHandle
    );

    // create a small sample graph with given layers
    createSampleGraph(graph);
  }

  /**
   * Creates the small sample graph, calculates a layout and updates the layer visualization.
   */
  private void createSampleGraph(IGraph graph) {
    INode n1 = graph.createNode();
    INode n2 = graph.createNode();
    INode n3 = graph.createNode();
    INode n4 = graph.createNode();

    graph.createEdge(n1, n2);
    graph.createEdge(n2, n3);
    graph.createEdge(n1, n4);

    // assign each node to a layer
    layerMapper.setValue(n1, 0);
    layerMapper.setValue(n2, 1);
    layerMapper.setValue(n3, 2);
    layerMapper.setValue(n4, 2);

    // create an HierarchicLayout instance to provide an initial layout
    HierarchicLayout ihl = createLayoutAlgorithm();
    // use the GivenLayersLayerer to respect the above node to layer assignment
    ihl.setFromScratchLayerer(new GivenLayersLayerer());

    // provide additional data to configure the layout algorithm
    HierarchicLayoutData ihlData = new HierarchicLayoutData();
    // respect the above node to layer assignment
    ihlData.setGivenLayersLayererIds(layerMapper);

    // run the layout algorithm
    graph.applyLayout(ihl, ihlData);

    // and update the layer visualization
    layerVisualCreator.updateLayerBounds(graph, layerMapper);
  }

  /**
   * Callback that creates the bend IHandle for the first and last bends.
   * @param bend the bend
   * @param originalImplementation the original implementation to delegate to
   * @return the new handle that allows for interactively assign the port constraints
   */
  private IHandle createBendHandle(IBend bend, IHandle originalImplementation) {
    IListEnumerable<IBend> bends = bend.getOwner().getBends();
    if (bends.getItem(0) == bend) {
      // decorate first bend
      originalImplementation = new PortConstraintBendHandle(true, bend, originalImplementation, sourcePortConstraints);
    }
    if (bends.getItem(bends.size() - 1) == bend) {
      // decorate last bend - could be both first and last
      originalImplementation = new PortConstraintBendHandle(false, bend, originalImplementation, targetPortConstraints);
    }
    return originalImplementation;
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new HierarchicLayoutDemo().start();
    });
  }
}
