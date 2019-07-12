/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.rotatablenodes;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.EdgePathLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel;
import com.yworks.yfiles.graph.styles.BevelNodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.circular.CircularLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.radial.RadialLayout;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.tree.BalloonLayout;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeReductionStage;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.LabelSnapContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Demo code that shows how support for rotated node visualizations can be implemented on top of the yFiles library.
 * A custom {@link INodeStyle} implementation is used to encapsulate most of the added functionality.
 */
public class RotatableNodesDemo extends AbstractDemo {

  private Action applyLayoutButton;
  private JComboBox<LayoutEntry> layoutComboBox;

  private JComboBox<SampleEntry> sampleComboBox;
  private JToggleButton orthogonalEditingButton;

  /**
   * Configures the toolbar with according buttons and functionality.
   */
  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, graphComponent));
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE_AS, null, graphComponent));
    toolBar.addSeparator();
    //adding buttons for adjusting the view
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Cut", "cut-16.png",
            ICommand.CUT, null, graphComponent));
    toolBar.add(createCommandButtonAction("Copy", "copy-16.png",
            ICommand.COPY, null, graphComponent));
    toolBar.add(createCommandButtonAction("Paste", "paste-16.png",
            ICommand.PASTE, null, graphComponent));
    toolBar.add(createCommandButtonAction("Delete", "delete2-16.png",
            ICommand.DELETE, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png",
            ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png",
            ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();
    //adding snapping and orthogonal edge editing
    toolBar.add(createSnappingButton());
    toolBar.add(orthogonalEditingButton = createOrthogonalEditingButton());
    toolBar.addSeparator();
    //adding grouping buttons
    toolBar.add(createCommandButtonAction("Group selected elements", "group-16.png",
            ICommand.GROUP_SELECTION, null, graphComponent));
    toolBar.add(createCommandButtonAction("Ungroup selected elements", "ungroup-16.png",
            ICommand.UNGROUP_SELECTION, null, graphComponent));
    toolBar.add(createCommandButtonAction("Enter group", "enter-group-16.png",
            ICommand.ENTER_GROUP, null, graphComponent));
    toolBar.add(createCommandButtonAction("Exit group", "exit-group-16.png",
            ICommand.EXIT_GROUP, null, graphComponent));
    toolBar.addSeparator();
    //Adding ComboBoxes for layout and which sample
    toolBar.add(sampleComboBox = createSampleComboBox());

    toolBar.addSeparator();
    toolBar.add(layoutComboBox = createLayoutComboBox());
    toolBar.addSeparator();
    toolBar.add(applyLayoutButton = createApplyLayoutButton());
  }


  /**
   * Creates button to apply the chosen layout.
   */
  private Action createApplyLayoutButton() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applyLayout();
      }
    };
    action.putValue(Action.NAME, "ApplyLayout");
    action.putValue(Action.SHORT_DESCRIPTION, "Apply layout");
    action.putValue(Action.SMALL_ICON, createIcon("reload-16.png"));
    return action;
  }

  /**
   * Creates the ComboBox for choosing the different layouts.
   */
  private JComboBox<LayoutEntry> createLayoutComboBox() {
    IGraph graph = graphComponent.getGraph();

    SizeD size = graph.getNodeDefaults().getSize();
    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setPreferredEdgeLength(1.5 * Math.max(size.getWidth(), size.getHeight()));

    TreeReductionStage treeLayout = new TreeReductionStage(new TreeLayout());
    treeLayout.setNonTreeEdgeRouter(new OrganicEdgeRouter());

    TreeReductionStage balloonLayout = new TreeReductionStage(new BalloonLayout());
    balloonLayout.setNonTreeEdgeRouter(new OrganicEdgeRouter());

    OrganicEdgeRouter organicEdgeRouter = new OrganicEdgeRouter();
    organicEdgeRouter.setEdgeNodeOverlapAllowed(false);
    LayoutEntry[] layoutEntries = {
        new LayoutEntry("Layout: Hierarchic", new HierarchicLayout(), RotatedNodeLayoutStage.RoutingMode.SHORTEST_STRAIGHT_PATH_TO_BORDER),
        new LayoutEntry("Layout: Organic", organicLayout, RotatedNodeLayoutStage.RoutingMode.NO_ROUTING),
        new LayoutEntry("Layout: Orthogonal", new OrthogonalLayout(), RotatedNodeLayoutStage.RoutingMode.SHORTEST_STRAIGHT_PATH_TO_BORDER),
        new LayoutEntry("Layout: Circular", new CircularLayout(), RotatedNodeLayoutStage.RoutingMode.NO_ROUTING),
        new LayoutEntry("Layout: Tree", treeLayout, RotatedNodeLayoutStage.RoutingMode.SHORTEST_STRAIGHT_PATH_TO_BORDER),
        new LayoutEntry("Layout: Balloon", balloonLayout, RotatedNodeLayoutStage.RoutingMode.NO_ROUTING),
        new LayoutEntry("Layout: Radial", new RadialLayout(), RotatedNodeLayoutStage.RoutingMode.NO_ROUTING),
        new LayoutEntry("Routing: Polyline", new EdgeRouter(), RotatedNodeLayoutStage.RoutingMode.SHORTEST_STRAIGHT_PATH_TO_BORDER),
        new LayoutEntry("Routing: Organic", organicEdgeRouter, RotatedNodeLayoutStage.RoutingMode.NO_ROUTING)
    };

    //Create comboBox with all layout options
    JComboBox<LayoutEntry> comboBox = new JComboBox<>(layoutEntries);
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setToolTipText("Set the layout");
    comboBox.setSelectedIndex(7);

    //add actionListener and propagates chosen index to renderingOrderBoxChanged which sets the new renderingOrder
    comboBox.addActionListener(e -> layoutChooserBoxSelectedIndexChanged());

    return comboBox;
  }

  /**
   * Entry of the {@link #layoutComboBox} containing the display name and the routing mode for a given layout algorithm.
   */
  private static class LayoutEntry {
    // The name to display in the combobox.
    String displayName;

    // The layout algorithm.
    ILayoutAlgorithm layoutAlgorithm;

    // The routing mode that suits the selected layout algorithm.
    // Layout algorithm that place edge ports in the center of the node don't need to add a routing step.
    RotatedNodeLayoutStage.RoutingMode routingMode;

    LayoutEntry(String displayName, ILayoutAlgorithm layoutAlgorithm, RotatedNodeLayoutStage.RoutingMode routingMode) {
      this.displayName = displayName;
      this.layoutAlgorithm = layoutAlgorithm;
      this.routingMode = routingMode;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }

  /**
   * Called when the layout combobox is changed, applies new layout.
   */
  private void layoutChooserBoxSelectedIndexChanged() {
    if (graphComponent != null) {
      applyLayout();
    }
  }

  /**
   * Runs a layout algorithm which is configured to consider node rotations.
   */
  public void applyLayout() {
    IGraph graph = graphComponent.getGraph();

    // provide the rotated outline and layout for the layout algorithm
    graph.getMapperRegistry().createFunctionMapper(RotatedNodeLayoutStage.ROTATED_NODE_LAYOUT_DP_KEY, node -> {
      INodeStyle style = node.getStyle();
      return new RotatedNodeLayoutStage.RotatedNodeShape(style.getRenderer().getShapeGeometry(node, style).getOutline(),
          style instanceof RotatableNodeStyleDecorator
              ? ((RotatableNodeStyleDecorator) style).getRotatedLayout(node)
              : new OrientedRectangle(node.getLayout()));
    });

    // get the selected layout algorithm
    int index = layoutComboBox.getSelectedIndex();
    LayoutEntry layoutEntry = layoutComboBox.getItemAt(index);

    // wrap the algorithm in RotatedNodeLayoutStage to make it aware of the node rotations
    RotatedNodeLayoutStage rotatedLayout = new RotatedNodeLayoutStage(layoutEntry.layoutAlgorithm);
    rotatedLayout.setEdgeRoutingMode(layoutEntry.routingMode);

    // apply the layout
    graphComponent.morphLayout(rotatedLayout, Duration.ofMillis(700));

    // clean up mapper registry
    graph.getMapperRegistry().removeMapper(RotatedNodeLayoutStage.ROTATED_NODE_LAYOUT_DP_KEY);
  }

  /**
   * Creates the ComboBox for choosing the current sample graph.
   */
  private JComboBox<SampleEntry> createSampleComboBox() {
    SampleEntry[] samples = {
        new SampleEntry("Sample: Sine", "sine"),
        new SampleEntry("Sample: Circle", "circle")
    };

    JComboBox<SampleEntry> comboBox = new JComboBox<>(samples);
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setToolTipText("Choose the sample graph");

    comboBox.addActionListener(e -> {
      int index = comboBox.getSelectedIndex();
      loadGraph(comboBox.getItemAt(index).fileName);
    });

    return comboBox;
  }

  /**
   * Entry of the {@link #sampleComboBox} containing the file name and the display name of a sample graph.
   */
  private static class SampleEntry {
    private String fileName;
    private String displayName;

    SampleEntry(String displayName, String fileName) {
      this.fileName = fileName;
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }

  /**
   * Creates the snapping button to choose if snapping is enabled or not.
   */
  private JToggleButton createSnappingButton() {
    JToggleButton button = new JToggleButton();

    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JToggleButton) {
          boolean selected = ((JToggleButton) e.getSource()).isSelected();
          GraphEditorInputMode inputMode = (GraphEditorInputMode) graphComponent.getInputMode();
          inputMode.getSnapContext().setEnabled(selected);
          inputMode.getLabelSnapContext().setEnabled(selected);
        }
      }
    };

    button.setAction(action);
    button.setToolTipText("Toggle snapping");
    button.setIcon(createIcon("snap-16.png"));

    return button;
  }

  /**
   * Creates button to toggle the orthogonal edge editing.
   */
  private JToggleButton createOrthogonalEditingButton() {
    JToggleButton button = new JToggleButton();

    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JToggleButton) {
          GraphEditorInputMode inputMode = (GraphEditorInputMode) graphComponent.getInputMode();
          boolean selected = ((JToggleButton) e.getSource()).isSelected();
          inputMode.getOrthogonalEdgeEditingContext().setEnabled(selected);
        }
      }
    };

    button.setAction(action);
    button.setIcon(createIcon("orthogonal-editing-16.png"));
    button.setToolTipText("Toggle orthogonal edge editing ");

    return button;
  }

  /**
   * Initializes the demo with rectangles and sample nodes.
   */
  @Override
  public void initialize() {
    graphComponent.setFileIOEnabled(true);

    initializeInputMode();
    initializeGraphML();
    initializeGraph();
    orthogonalEditingButton.doClick();
    sampleComboBox.setSelectedIndex(0);
  }

  /**
   * Initialize the interaction with the graph.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();

    //enable orthogonal edge editing
    geim.setOrthogonalEdgeEditingContext(new OrthogonalEdgeEditingContext());

    //enable snapping only for resizing nodes and only to the same size of other nodes
    GraphSnapContext snapContext = new GraphSnapContext();
    snapContext.setEnabled(false);
    snapContext.setCollectingNodePairSegmentSnapLinesEnabled(false);
    snapContext.setCollectingNodePairSnapLinesEnabled(false);
    snapContext.setCollectingEdgeSnapLinesEnabled(false);
    snapContext.setCollectingNodeSnapLinesEnabled(false);
    snapContext.setCollectingPortSnapLinesEnabled(false);
    snapContext.setSnappingBendAdjacentSegmentsEnabled(false);
    snapContext.setCollectingNodeSizesEnabled(true);
    geim.setSnapContext(snapContext);

    LabelSnapContext labelSnapContext = new LabelSnapContext();
    labelSnapContext.setEnabled(false);
    geim.setLabelSnapContext(labelSnapContext);

    geim.setClipboardOperationsAllowed(true);
    geim.setGroupingOperationsAllowed(true);
    geim.getOrthogonalEdgeEditingContext().setEnabled(false);

    geim.getWaitInputMode().addWaitingStartedListener((source, args) -> enableControls(false));
    geim.getWaitInputMode().addWaitingEndedListener((source, args) -> enableControls(true));

    graphComponent.setInputMode(geim);
  }

  private void enableControls(boolean enable) {

    applyLayoutButton.setEnabled(enable);
    layoutComboBox.setEnabled(enable);
    sampleComboBox.setEnabled(enable);
  }

  /**
   * Initialize loading from and saving to graphml-flies.
   */
  private void initializeGraphML() {
    //initialize (de-)serialization for load/save commands
    GraphMLIOHandler graphMLHandler = new GraphMLIOHandler();
    String namespace = "http://www.yworks.com/yfiles-for-java/demos/RotatableNodes/1.0";
    graphMLHandler.addXamlNamespaceMapping(namespace, "complete.rotatablenodes", getClass().getClassLoader());
    graphMLHandler.addNamespace(namespace, "demo");

    graphMLHandler.addParsedListener((o, parseEventArgs) -> {
      IGraph graph = graphComponent.getGraph();

      //Iterate over every node which isn't a group node and is instance of the rotatableNodeStyleDecorator
      for (INode node : graph.getNodes()) {
        if (!graph.isGroupNode(node) && !(node.getStyle() instanceof RotatableNodeStyleDecorator)) {
          graph.setStyle(node, new RotatableNodeStyleDecorator(node.getStyle(), 0));
        }

        //iterate over every label of the current node which is instance of the corresponding decorator
        for (ILabel label : node.getLabels()) {
          ILabelModel model = label.getLayoutParameter().getModel();
          if(!(model instanceof RotatableNodeLabelModelDecorator)){
            graph.setLabelLayoutParameter(label,
                    new RotatableNodeLabelModelDecorator(model)
                            .createWrappingParameter(label.getLayoutParameter()));
          }
        }

        //iterate over every port of the current node which is instance of the corresponding decorator
        for(IPort port : node.getPorts()) {
          IPortLocationModel model = port.getLocationParameter().getModel();
          if(!(model instanceof RotatablePortLocationModelDecorator)){
            graph.setPortLocationParameter(port, RotatablePortLocationModelDecorator.INSTANCE
                    .createWrappingParameter(port.getLocationParameter()));
          }
        }
      }
    });
    graphComponent.setGraphMLIOHandler(graphMLHandler);
  }

  /**
   * Initializes styles and decorators for the graph.
   */
  private void  initializeGraph() {
    FoldingManager foldingManager = new FoldingManager();
    IGraph graph = foldingManager.createFoldingView().getGraph();

    GraphDecorator decorator = graph.getDecorator();

    //for rotated nodes we need to provide port candidates that are backed by a rotatable port location model
    //if you want to support non-rotated port candidates you can just provide undecorated instances here
    decorator.getNodeDecorator().getPortCandidateProviderDecorator().setFactory(
            node -> node.getStyle() instanceof  RotatableNodeStyleDecorator,
            RotatableNodesDemo::createPortCandidateProvider);

    decorator.getPortDecorator().getEdgePathCropperDecorator().setImplementation(new AdjustOutlinePortInsidenessEdgePathCropper());
    decorator.getNodeDecorator().getGroupBoundsCalculatorDecorator().setImplementation(new RotationAwareGroupBoundsCalculator());

    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Colors.ORANGE);
    nodeStyle.setShadowDrawingEnabled(false);

    graph.getNodeDefaults().setStyle(new RotatableNodeStyleDecorator(nodeStyle, 0));
    graph.getNodeDefaults().setStyleInstanceSharingEnabled(false);
    graph.getNodeDefaults().setSize(new SizeD(100, 50));

    InteriorLabelModel coreLabelModel = new InteriorLabelModel();
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(
            new RotatableNodeLabelModelDecorator(coreLabelModel).createWrappingParameter(InteriorLabelModel.CENTER));

    //Make ports visible
    ShapeNodeStyle portStyle = new ShapeNodeStyle();
    portStyle.setShape(ShapeNodeShape.ELLIPSE);
    portStyle.setPaint(Colors.RED);
    graph.getNodeDefaults().getPortDefaults().setStyle(new NodeStylePortStyleAdapter(portStyle));

    //usa a rotatable port model as default
    graph.getNodeDefaults().getPortDefaults().setLocationParameter(
            new RotatablePortLocationModelDecorator().createWrappingParameter(FreeNodePortLocationModel.NODE_TOP_ANCHORED));

    PanelNodeStyle groupNodesStyle = new PanelNodeStyle();
    groupNodesStyle.setColor(Colors.LIGHT_BLUE);
    graph.getGroupNodeDefaults().setStyle(groupNodesStyle);

    EdgePathLabelModel edgePathLabelModel = new EdgePathLabelModel();
    edgePathLabelModel.setDistance(10);
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(edgePathLabelModel.createDefaultParameter());

    //enable undo
    foldingManager.getMasterGraph().setUndoEngineEnabled(true);

    graphComponent.setGraph(graph);
  }

  /**
   * Creates an IPortCandidateProvider that considers the node's shape and rotation.
   */
  private static IPortCandidateProvider createPortCandidateProvider(INode node){
    RotatablePortLocationModelDecorator rotatedPortModel = RotatablePortLocationModelDecorator.INSTANCE;
    FreeNodePortLocationModel freeModel = FreeNodePortLocationModel.INSTANCE;

    RotatableNodeStyleDecorator rnsd = (RotatableNodeStyleDecorator) node.getStyle();
    INodeStyle wrapped = rnsd.getWrapped();
    ShapeNodeStyle sns = wrapped instanceof ShapeNodeStyle ? (ShapeNodeStyle)wrapped : null;

    if (wrapped instanceof  ShinyPlateNodeStyle || wrapped instanceof BevelNodeStyle ||
       sns != null && sns.getShape() == ShapeNodeShape.ROUND_RECTANGLE) {
      return IPortCandidateProvider.combine(
              //take all existing ports (assumed they have the correct port location model)
              IPortCandidateProvider.fromUnoccupiedPorts(node),

              //provide explicit candidates (all backed by a rotatable port location model)
              IPortCandidateProvider.fromCandidates(
                      //Port candidates at the corners that are slightly inset
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              freeModel.createParameter(new PointD(0, 0), new PointD(5, 5)))),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              freeModel.createParameter(new PointD(0, 1), new PointD(5, -5)))),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              freeModel.createParameter(new PointD(1, 0), new PointD(-5, 5)))),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              freeModel.createParameter(new PointD(1, 1), new PointD(-5, -5)))),

                      //Port candidates at the sides and center
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_LEFT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_CENTER_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_TOP_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_RIGHT_ANCHORED))
              ));
    }

    if (sns != null && sns.getShape() == ShapeNodeShape.RECTANGLE) {
      return IPortCandidateProvider.combine(
              IPortCandidateProvider.fromUnoccupiedPorts(node),
              IPortCandidateProvider.fromCandidates(
                      //Port candidates at the corners
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_TOP_LEFT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_TOP_RIGHT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_BOTTOM_LEFT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_BOTTOM_RIGHT_ANCHORED)),

                      //Port candidates at the sides and the center
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_LEFT_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_CENTER_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_TOP_ANCHORED)),
                      new DefaultPortCandidate(node, rotatedPortModel.createWrappingParameter(
                              FreeNodePortLocationModel.NODE_RIGHT_ANCHORED))
              ));
    }

    if(sns != null) {
      //can be arbitrary shape. first create a dummy node that is not rotated
      SimpleNode dummyNode = new SimpleNode();
      dummyNode.setStyle(sns);
      dummyNode.setLayout(node.getLayout());

      AbstractPortCandidateProvider shapeProvider = IPortCandidateProvider.fromShapeGeometry(dummyNode, 0);
      Iterable<IPortCandidate> shapeCandidates = shapeProvider.getTargetPortCandidates(null);

      List<IPortCandidate> rotatingCandidates = new ArrayList<>();
      for (IPortCandidate candidate : shapeCandidates) {
        rotatingCandidates.add(new DefaultPortCandidate(node,
                rotatedPortModel.createWrappingParameter(candidate.getLocationParameter())));
      }

      return IPortCandidateProvider.combine(
              IPortCandidateProvider.fromUnoccupiedPorts(node),
              IPortCandidateProvider.fromCandidates(rotatingCandidates));

    }
    return  null;
  }

  /**
   * Loads the graph from the "resources" folder.
   */
  private void loadGraph(String graphName) {
    graphComponent.getGraph().clear();

    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/" + graphName + ".graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Centers and arranges the graph in the graph component.
   */
  @Override
  public void onVisible() {
    //center the graph to prevent the initial layout fading in from the top left corner
    graphComponent.setFitContentViewMargins(new InsetsD(50));
    graphComponent.fitGraphBounds();
  }

  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new RotatableNodesDemo().start();
    });
  }
}

