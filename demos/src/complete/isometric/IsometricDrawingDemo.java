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
package complete.isometric;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.MatrixOrder;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultFolderNodeConverter;
import com.yworks.yfiles.graph.DefaultFoldingEdgeConverter;
import com.yworks.yfiles.graph.FolderNodeState;
import com.yworks.yfiles.graph.FoldingLabelState;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.builder.EdgesSource;
import com.yworks.yfiles.graph.builder.GraphBuilder;
import com.yworks.yfiles.graph.builder.NodesSource;
import com.yworks.yfiles.graph.labelmodels.EdgeSegmentLabelModel;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.graphml.SerializationProperties;
import com.yworks.yfiles.layout.CompositeLayoutData;
import com.yworks.yfiles.layout.FixNodeLayoutData;
import com.yworks.yfiles.layout.FixNodeLayoutStage;
import com.yworks.yfiles.layout.FixPointPolicy;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.ItemCollection;
import com.yworks.yfiles.layout.LabelAngleReferences;
import com.yworks.yfiles.layout.LabelPlacements;
import com.yworks.yfiles.layout.LabelSideReferences;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.PreferredPlacementDescriptor;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayoutData;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.GridStyle;
import com.yworks.yfiles.view.GridVisualCreator;
import com.yworks.yfiles.view.HierarchicNestingPolicy;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.LabelLayerPolicy;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.Projections;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.NavigationInputMode;
import com.yworks.yfiles.view.input.NodeAlignmentPolicy;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import complete.isometric.model.EdgeData;
import complete.isometric.model.Geometry;
import complete.isometric.model.IsometricData;
import complete.isometric.model.NodeData;
import toolkit.AbstractDemo;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.geom.AffineTransform;
import java.time.Duration;
import java.util.Random;

/**
 * Displays graphs in an isometric fashion to create an impression of a 3-dimensional view.
 */
public class IsometricDrawingDemo extends AbstractDemo {
  private static final double MINIMUM_NODE_HEIGHT = 3;

  private LayoutType layoutType = LayoutType.HIERARCHIC;

  // A flag that signals whether or not a layout is currently running to prevent re-entrant layout calculations.
  private boolean layoutRunning;

  private Action zoomInAction;
  private Action zoomoutAction;
  private Action fitGraphContentAction;
  private Action openAction;

  private JButton hierarchicLayoutButton;
  private JButton orthogonalLayoutButton;

  private JSlider rotationSlider;
  private JLabel rotationLabel;

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(openAction = createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(zoomInAction = createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(zoomoutAction = createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(fitGraphContentAction = createCommandButtonAction("Fit the graph content", "fit2-16.png", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
    toolBar.addSeparator();

    toolBar.add(hierarchicLayoutButton = new JButton("Hierarchic Layout", createIcon("layout-hierarchic.png")));
    hierarchicLayoutButton.addActionListener(e -> {
      layoutType = LayoutType.HIERARCHIC;
      runLayout(null);
    });

    toolBar.add(orthogonalLayoutButton = new JButton("Orthogonal Layout", createIcon("layout-orthogonal-16.png")));
    orthogonalLayoutButton.addActionListener(e -> {
      layoutType = LayoutType.ORTHOGONAL;
      runLayout(null);
    });

    toolBar.addSeparator();
    toolBar.add(new JLabel("Rotation angle: "));
    toolBar.add(rotationSlider = configureRotationSlider());
    toolBar.add(rotationLabel = new JLabel(" 0\u00B0"));
  }

  private JSlider configureRotationSlider() {
    rotationSlider = new JSlider();
    rotationSlider.setMaximumSize(new Dimension(200, 50));

    rotationSlider.setMinimum(0);
    rotationSlider.setMaximum(360);
    rotationSlider.setValue(0);

    rotationSlider.addChangeListener(e -> {
      updateRotation(rotationSlider.getValue());
      rotationLabel.setText(" " + rotationSlider.getValue() + "\u00B0");
    });
    return rotationSlider;
  }


  @Override
  public void initialize() {
    // set an isometric projection and a GraphModelManager that considers the isometric render order
    initializeProjection();

    // configure the GraphML loading
    initializeLoadingFiles();

    // enable and configure folding support
    initializeFolding();

    // initialize interaction
    initializeInputMode();

    // add a grid visual as background
    initializeGridVisual();

    loadInitialGraph();
  }

  /**
   * Sets an isometric projection and a GraphModelManager that considers the isometric render order.
   */
  private void initializeProjection() {
    // set an isometric projection
    graphComponent.setProjection(Projections.getIsometric());
    // use a graph model manager that renders the nodes in their correct z-order
    IsometricGraphModelManager graphModelManager = new IsometricGraphModelManager(graphComponent, graphComponent.getContentGroup());
    graphModelManager.setHierarchicNestingPolicy(HierarchicNestingPolicy.GROUP_NODES);
    graphModelManager.setLabelLayerPolicy(LabelLayerPolicy.AT_OWNER);
    graphComponent.setGraphModelManager(graphModelManager);
  }

  /**
   * Configures the {@link GraphMLIOHandler} used to load the graph.
   */
  private void initializeLoadingFiles() {
    // ignore deserialization errors when loading graphs that use different styles
    // the styles will be replaced with isometric styles later
    graphComponent.getGraphMLIOHandler().getDeserializationPropertyOverrides().set(
            SerializationProperties.IGNORE_XAML_DESERIALIZATION_ERRORS,
            true
    );

    graphComponent.getGraphMLIOHandler().addParsingListener((source, args) -> setUIState(false));
    graphComponent.getGraphMLIOHandler().addParsedListener((source, args) -> {
      applyIsometricStyles();
      runLayout(null);
    });

    graphComponent.setFileIOEnabled(true);
  }

  private void initializeFolding() {
    FoldingManager manager = new FoldingManager(graphComponent.getGraph());

    DefaultFolderNodeConverter folderNodeConverter = new DefaultFolderNodeConverter();
    folderNodeConverter.setCopyingFirstLabelEnabled(true);
    folderNodeConverter.setNodeStyleCloningEnabled(true);
    folderNodeConverter.setFolderNodeSize(new SizeD(210, 200));
    CollapsibleNodeStyleDecorator nodeStyleDecorator = new CollapsibleNodeStyleDecorator(new IsometricGroupNodeStyle());
    nodeStyleDecorator.setButtonPlacement(InteriorLabelModel.SOUTH_WEST);
    folderNodeConverter.setFolderNodeStyle(nodeStyleDecorator);

    DefaultFoldingEdgeConverter foldingEdgeConverter = new DefaultFoldingEdgeConverter();
    foldingEdgeConverter.setCopyingFirstLabelEnabled(true);

    manager.setFolderNodeConverter(folderNodeConverter);
    manager.setFoldingEdgeConverter(foldingEdgeConverter);

    graphComponent.setGraph(manager.createFoldingView().getGraph());
    graphComponent.getGraph().addIsGroupNodeChangedListener((source, args) -> adaptGroupNodes());

  }

  private void initializeInputMode() {
    // initialize interaction
    GraphEditorInputMode geim = new GraphEditorInputMode();

    NavigationInputMode navigationInputMode = new NavigationInputMode();
    navigationInputMode.setFittingContentAfterGroupActionsEnabled(false);
    navigationInputMode.setAutoGroupNodeAlignmentPolicy(NodeAlignmentPolicy.BOTTOM_LEFT);

    geim.setNavigationInputMode(navigationInputMode);

    // we use orthogonal edge editing and snapping, both very helpful for editing in isometric views
    geim.setOrthogonalEdgeEditingContext(new OrthogonalEdgeEditingContext());
    geim.setSnapContext(new GraphSnapContext());

    // allow expand/collapse for group nodes
    geim.setGroupingOperationsAllowed(true);

    // add listeners to invoke an incremental layout when collapsing/expanding a group
    geim.getNavigationInputMode().addGroupCollapsedListener((source, args) -> runLayout(args.getItem()));
    geim.getNavigationInputMode().addGroupExpandedListener((source, args) -> runLayout(args.getItem()));

    // ensure that every node has geometry and color information
    geim.addNodeCreatedListener((source, args) -> {
      ensureNodeTag(args.getItem());
      if (graphComponent.getGraph().isGroupNode(args.getItem())) {
        adaptGroupNodes();
      }
    });
    graphComponent.setInputMode(geim);

    // changing a node's layout may require to adjust its render order
    graphComponent.getGraph().addNodeLayoutChangedListener((source, node, oldLayout) -> graphComponent.getGraphModelManager().updateDescriptor(node));

    // add handle that enables the user to change the height of a (non-group) node
    graphComponent.getGraph().getDecorator().getNodeDecorator().getHandleProviderDecorator()
            .setImplementationWrapper(node -> {
                      IFoldingView foldingView = graphComponent.getGraph().getFoldingView();
                      return !foldingView.getManager().getMasterGraph().isGroupNode(foldingView.getMasterItem(node));
                    },
                    (node, handleProvider) -> new HeightHandleProvider(node, handleProvider, MINIMUM_NODE_HEIGHT));
  }

  private void initializeGridVisual() {
    GridVisualCreator grid = new GridVisualCreator(new GridInfo());

    grid.setGridStyle(GridStyle.LINES);
    grid.setPen(new Pen(new Color(210, 210, 210, 255)));
    grid.setVisibilityThreshold(10.0);

    graphComponent.getBackgroundGroup().addChild(grid, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
  }

  /**
   * Loads a graph from {@link IsometricData} using a {@link GraphBuilder} and initializes
   * all styles and isometric data.
   *
   * The graph also gets an initial layout.
   */
  private void loadInitialGraph() {
    IGraph graph = graphComponent.getGraph();

    graph.getNodeDefaults().setStyle(new IsometricNodeStyle());
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(ExteriorLabelModel.SOUTH);

    CollapsibleNodeStyleDecorator nodeStyleDecorator = new CollapsibleNodeStyleDecorator(new IsometricGroupNodeStyle());
    nodeStyleDecorator.setButtonPlacement(InteriorLabelModel.SOUTH_WEST);
    graph.getGroupNodeDefaults().setStyle(nodeStyleDecorator);

    DefaultLabelStyle groupNodeLabelStyle = new DefaultLabelStyle();
    groupNodeLabelStyle.setInsets(new InsetsD(3));
    graph.getGroupNodeDefaults().getLabelDefaults().setStyle(groupNodeLabelStyle);
    graph.getGroupNodeDefaults().getLabelDefaults().setLayoutParameter(InteriorLabelModel.SOUTH_EAST);

    DefaultLabelStyle edgeLabelStyle = new DefaultLabelStyle();
    edgeLabelStyle.setInsets(new InsetsD(3));
    graph.getEdgeDefaults().getLabelDefaults().setStyle(edgeLabelStyle);

    EdgeSegmentLabelModel edgeSegmentLabelModel = new EdgeSegmentLabelModel();
    edgeSegmentLabelModel.setAutoRotationEnabled(true);
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(edgeSegmentLabelModel.createDefaultParameter());

    GraphBuilder graphBuilder = new GraphBuilder(graph);

    NodesSource<NodeData> nodesSource = graphBuilder.createNodesSource(IsometricData.NodesData, NodeData::getId);
    nodesSource.setParentIdProvider(NodeData::getGroup);
    nodesSource.getNodeCreator().setLayoutProvider(item -> new RectD(0, 0, item.getGeometry().getWidth(), item.getGeometry().getDepth()));
    nodesSource.getNodeCreator().createLabelBinding(NodeData::getLabel);

    NodesSource<NodeData> groupNodesSource = graphBuilder.createGroupNodesSource(IsometricData.GroupsData, NodeData::getId);
    groupNodesSource.setParentIdProvider(NodeData::getGroup);
    groupNodesSource.getNodeCreator().setLayoutProvider(item -> new RectD(0, 0, item.getGeometry().getWidth(), item.getGeometry().getDepth()));
    groupNodesSource.getNodeCreator().createLabelBinding(NodeData::getLabel);

    EdgesSource<EdgeData> edgesSource = graphBuilder.createEdgesSource(IsometricData.EdgesData, EdgeData::getFrom, EdgeData::getTo);
    edgesSource.getEdgeCreator().createLabelBinding(EdgeData::getLabel);

    graphBuilder.buildGraph();

    runLayout(null);
  }

  /**
   * Invokes a layout specified by the current layout type.
   * If there is a fixed node, the layout is calculated incrementally.
   *
   * @param fixedNode If defined, the layout will be incrementally and this node remains at its location.
   */
  private void runLayout(INode fixedNode) {
    if (layoutRunning) {
      return;
    }
    layoutRunning = true;
    boolean incremental = fixedNode != null;

    // configure layout
    ILayoutAlgorithm layout = layoutType == LayoutType.HIERARCHIC ? getHierarchicLayout(incremental) : getOrthogonalLayout();
    LayoutData layoutData = layoutType == LayoutType.ORTHOGONAL ? getHierarchicLayoutData() : getOrthogonalLayoutData();

    if (incremental) {
      // fixate the location of the given fixed node
      FixNodeLayoutStage fixNodeLayoutStage = new FixNodeLayoutStage(layout);
      fixNodeLayoutStage.setFixPointPolicy(FixPointPolicy.LOWER_LEFT);

      FixNodeLayoutData fixNodeLayoutData = new FixNodeLayoutData();
      ItemCollection<INode> nodes = new ItemCollection<>();
      nodes.setItem(fixedNode);
      fixNodeLayoutData.setFixedNodes(nodes);
      layoutData = new CompositeLayoutData(layoutData, fixNodeLayoutData);
    }

    setUIState(false);

    // configure layout execution to not move the view port
    LayoutExecutor executor = new LayoutExecutor(graphComponent, layout);
    executor.setLayoutData(layoutData);
    executor.setViewportAnimationEnabled(!incremental);
    executor.setDuration(Duration.ofMillis(500));
    executor.setContentRectUpdatingEnabled(true);

    executor.addLayoutFinishedListener((source, args) -> {
      layoutRunning = false;
      setUIState(true);
    });

    executor.start();
  }

  /**
   * Disables and enables UI control. Used during long running calculations.
   *
   * @param enabled state the UI should be set to
   */
  private void setUIState(boolean enabled) {
    SwingUtilities.invokeLater(() -> {
      zoomInAction.setEnabled(enabled);
      zoomoutAction.setEnabled(enabled);
      fitGraphContentAction.setEnabled(enabled);
      hierarchicLayoutButton.setEnabled(enabled);
      orthogonalLayoutButton.setEnabled(enabled);
      rotationSlider.setEnabled(enabled);
      openAction.setEnabled(enabled);
    });
  }

  /**
   * Creates a configured hierarchic layout.
   *
   * @param incremental whether the layout should run in {@link LayoutMode#INCREMENTAL}
   */
  public HierarchicLayout getHierarchicLayout(boolean incremental) {
    HierarchicLayout layout = new HierarchicLayout();
    layout.setOrthogonalRoutingEnabled(true);
    layout.setNodeToEdgeDistance(50);
    layout.setMinimumLayerDistance(40);
    layout.setLabelingEnabled(false);
    layout.setIntegratedEdgeLabelingEnabled(true);
    layout.setNodeLabelConsiderationEnabled(true);
    layout.setGridSpacing(10);

    if (incremental) {
      layout.setLayoutMode(LayoutMode.INCREMENTAL);
    }

    return layout;
  }

  /**
   * Creates a configured hierarchic layout data object.
   */
  public HierarchicLayoutData getHierarchicLayoutData() {
    // use preferred placement descriptors to place the labels vertically on the edges
    HierarchicLayoutData layoutData = new HierarchicLayoutData();
    layoutData.setEdgeLabelPreferredPlacement(getPreferredLabelPlacement());
    layoutData.setIncrementalHints((modelItem, incrementalHintsFactory) ->
        modelItem instanceof IEdge
            ? incrementalHintsFactory.createSequenceIncrementallyHint(modelItem)
            : null);
    return layoutData;
  }

  /**
   * Creates a configured orthogonal layout.
   */
  public OrthogonalLayout getOrthogonalLayout() {
    OrthogonalLayout layout = new OrthogonalLayout();
    layout.setIntegratedEdgeLabelingEnabled(true);
    layout.setNodeLabelConsiderationEnabled(true);
    layout.setGridSpacing(10);
    return layout;
  }

  /**
   * Creates a configured orthogonal layout data.
   */
  public OrthogonalLayoutData getOrthogonalLayoutData() {
    OrthogonalLayoutData layoutData = new OrthogonalLayoutData();
    layoutData.setEdgeLabelPreferredPlacement(getPreferredLabelPlacement());
    return layoutData;
  }

  private PreferredPlacementDescriptor getPreferredLabelPlacement() {
    PreferredPlacementDescriptor preferredPlacementDescriptor = new PreferredPlacementDescriptor();
    preferredPlacementDescriptor.setAngle(0);
    preferredPlacementDescriptor.setAngleReference(LabelAngleReferences.RELATIVE_TO_EDGE_FLOW);
    preferredPlacementDescriptor.setSideOfEdge(LabelPlacements.LEFT_OF_EDGE);
    preferredPlacementDescriptor.setSideReference(LabelSideReferences.ABSOLUTE_WITH_RIGHT_IN_NORTH);
    return preferredPlacementDescriptor;
  }


  /**
   * Adapt the group node height and colors: group nodes should be flat and use a pen for the bounds.
   */
  private void adaptGroupNodes() {
    IGraph graph = graphComponent.getGraph();

    graph.getNodes().stream().filter(graph::isGroupNode).forEach(groupNode -> {
      NodeData nodeData = (NodeData) groupNode.getTag();
      nodeData.getGeometry().setHeight(0);
      nodeData.setColor(new Color(202, 236, 255, 128));
      nodeData.setPen(IsometricGroupNodeStyle.BORDER_PEN);
    });

    graphComponent.invalidate();
  }

  private final Random random = new Random();

  /**
   * Ensures that the node has geometry and color information present in its tag.
   *
   * @param node The node to check.
   */
  private void ensureNodeTag(INode node) {
    NodeData nodeData = (NodeData) node.getTag();
    if (nodeData == null) {
      nodeData = new NodeData();
      nodeData.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), 255));
      node.setTag(nodeData);
    }
    if (nodeData.getGeometry() == null) {
      nodeData.setGeometry(new Geometry(0, MINIMUM_NODE_HEIGHT + Math.round(random.nextDouble() * 30), 0));
    }
  }

  /**
   * Centers the displayed graph in the isometric graph view when it becomes
   * visible for the first time.
   */
  @Override
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  private void updateRotation(double degrees) {
    // calculate new rotated isometric projection
    AffineTransform transform = Projections.getIsometric();
    Matrix2D rotate = Matrix2D.createRotateInstance((Math.PI * degrees) / 180);
    rotate.multiply(Matrix2D.fromTransform(transform), MatrixOrder.APPEND);

    // set new projection keeping the center of the viewport
    graphComponent.updateContentRect();
    PointD center = graphComponent.getViewport().getCenter();
    graphComponent.setProjection(rotate.toTransform());
    // update z-order of model items according to new rotation
    ((IsometricGraphModelManager) graphComponent.getGraphModelManager()).update();
    graphComponent.setCenter(center);
    graphComponent.invalidate();
  }

  /**
   * Adds isometric styles and geometry data to nodes and labels of the graph.
   *
   * Also free label and port location models are applied to retrieve the correct positions
   * calculated by the layout algorithm.
   */
  public void applyIsometricStyles() {
    FoldingManager foldingManager = graphComponent.getGraph().getFoldingView().getManager();
    IGraph graph = foldingManager.getMasterGraph();

    graph.getNodes().forEach(node -> {
      boolean group = graph.isGroupNode(node);
      updateGeometry(node, node.getLayout().toRectD().getSize(), group ? 0 : 20, null);

      if (group) {
        ((NodeData) node.getTag()).setColor(new Color(202, 236, 255, 128));
        ((NodeData) node.getTag()).setPen(IsometricGroupNodeStyle.BORDER_PEN);

        CollapsibleNodeStyleDecorator nodeStyleDecorator = new CollapsibleNodeStyleDecorator(new IsometricGroupNodeStyle());
        nodeStyleDecorator.setButtonPlacement(InteriorLabelModel.SOUTH_WEST);
        graph.setStyle(node, nodeStyleDecorator);

        FolderNodeState folderNodeState = foldingManager.getFolderNodeState(node);
        CollapsibleNodeStyleDecorator folderNodeStyleDecorator = new CollapsibleNodeStyleDecorator(new IsometricGroupNodeStyle());
        folderNodeStyleDecorator.setButtonPlacement(InteriorLabelModel.SOUTH_WEST);
        folderNodeState.setStyle(folderNodeStyleDecorator);

        ILabel firstLabel = node.getLabels().first();
        if (firstLabel != null) {
          IOrientedRectangle layout = firstLabel.getLayout();
          updateGeometry(firstLabel, layout.getBounds().getSize(), layout.getHeight(), new InsetsD(3));
          DefaultLabelStyle labelStyle = new DefaultLabelStyle();
          labelStyle.setInsets(new InsetsD(3));
          graph.setStyle(firstLabel, labelStyle);
          graph.setLabelLayoutParameter(firstLabel, graph.getGroupNodeDefaults().getLabelDefaults().getLayoutParameter());
        }

        FoldingLabelState firstFolderLabel = folderNodeState.getLabels().first();
        if (firstFolderLabel != null) {
          ILabel label = firstFolderLabel.asLabel();
          IOrientedRectangle layout = label.getLayout();
          updateGeometry(label, layout.getBounds().getSize(), layout.getHeight(), new InsetsD(3));
          DefaultLabelStyle labelStyle = new DefaultLabelStyle();
          labelStyle.setInsets(new InsetsD(3));
          firstFolderLabel.setStyle(labelStyle);
          firstFolderLabel.setLayoutParameter(graph.getGroupNodeDefaults().getLabelDefaults().getLayoutParameter());
        }
      } else {
        ((NodeData) node.getTag()).setColor(new Color(255, 153, 0, 255));
        graph.setStyle(node, new IsometricNodeStyle());
      }
    });

    graph.getEdges().forEach(edge -> {
      graph.setStyle(edge, new PolylineEdgeStyle());

      edge.getLabels().forEach(label -> {
        IOrientedRectangle layout = label.getLayout();
        updateGeometry(label, layout.getBounds().getSize(), layout.getHeight(), new InsetsD(3));
        DefaultLabelStyle labelStyle = new DefaultLabelStyle();
        labelStyle.setInsets(new InsetsD(3));
        graph.setStyle(label, labelStyle);
        EdgeSegmentLabelModel edgeSegmentLabelModel = new EdgeSegmentLabelModel();
        edgeSegmentLabelModel.setAutoRotationEnabled(true);
        graph.setLabelLayoutParameter(label, edgeSegmentLabelModel.createDefaultParameter());
      });
    });

    graph.getPorts().forEach(port ->
      graph.setPortLocationParameter(port, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));
  }

  /**
   * Updates the tag of the given item with geometry data.
   * In case the tag already contains valid geometry data, it stays unchanged.
   * @param item The item for which the tag is updated.
   * @param layout The 2D-geometry for the item.
   * @param height The height of the resulting solid figure.
   * @param insets Insets that are added to the layout information to create a padding.
   */
  private void updateGeometry(IModelItem item, SizeD layout, double height, InsetsD insets) {
    NodeData nodeData = (NodeData) item.getTag();
    if (nodeData != null && nodeData.getGeometry() != null) {
      return;
    }
    InsetsD inset = insets == null ? InsetsD.EMPTY : insets;
    Geometry geometry = new Geometry(
        layout.getWidth() + inset.getHorizontalInsets(),
        height,
        layout.getHeight() + inset.getVerticalInsets()
    );

    if (nodeData != null) {
      nodeData.setGeometry(geometry);
    } else {
      NodeData data = new NodeData();
      data.setGeometry(geometry);
      item.setTag(data);
    }
  }

  private enum LayoutType {
    HIERARCHIC,
    ORTHOGONAL
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new IsometricDrawingDemo().start();
    });
  }
}
