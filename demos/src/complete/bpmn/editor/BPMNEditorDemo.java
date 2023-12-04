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
package complete.bpmn.editor;

import complete.bpmn.di.BpmnDiParser;
import complete.bpmn.di.BpmnLabelStyle;
import complete.bpmn.layout.BpmnLayout;
import complete.bpmn.layout.LayoutOrientation;
import complete.bpmn.legacy.ActivityNodeStyleExtension;
import complete.bpmn.view.ActivityNodeStyle;
import complete.bpmn.view.AnnotationNodeStyle;
import complete.bpmn.view.BpmnConstants;
import complete.bpmn.view.BpmnEdgeStyle;
import complete.bpmn.view.BpmnLayoutData;
import complete.bpmn.view.BpmnNodeStyle;
import complete.bpmn.view.BpmnPortCandidateProvider;
import complete.bpmn.view.ChoreographyLabelModel;
import complete.bpmn.view.ChoreographyNodeStyle;
import complete.bpmn.view.ConversationNodeStyle;
import complete.bpmn.view.ConversationType;
import complete.bpmn.view.DataObjectNodeStyle;
import complete.bpmn.view.DataStoreNodeStyle;
import complete.bpmn.view.EdgeType;
import complete.bpmn.view.EventNodeStyle;
import complete.bpmn.view.EventPortStyle;
import complete.bpmn.view.GatewayNodeStyle;
import complete.bpmn.view.GroupNodeStyle;
import complete.bpmn.view.MessageLabelStyle;
import complete.bpmn.view.Participant;
import complete.bpmn.view.PoolNodeStyle;
import complete.bpmn.view.config.ActivityNodeStyleConfiguration;
import complete.bpmn.view.config.AnnotationNodeStyleConfiguration;
import complete.bpmn.view.config.BpmnEdgeStyleConfiguration;
import complete.bpmn.view.config.ChoreographyNodeStyleConfiguration;
import complete.bpmn.view.config.ConversationNodeStyleConfiguration;
import complete.bpmn.view.config.DataObjectNodeStyleConfiguration;
import complete.bpmn.view.config.EdgeStyleConfiguration;
import complete.bpmn.view.config.EventNodeStyleConfiguration;
import complete.bpmn.view.config.GatewayNodeStyleConfiguration;
import complete.bpmn.view.config.NodeStyleConfiguration;
import complete.bpmn.view.config.PoolNodeStyleConfiguration;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultFoldingEdgeConverter;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IEdgeDefaults;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.IRow;
import com.yworks.yfiles.graph.IStripe;
import com.yworks.yfiles.graph.ITable;
import com.yworks.yfiles.graph.StripeTypes;
import com.yworks.yfiles.graph.Table;
import com.yworks.yfiles.graph.labelmodels.CompositeLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSegmentLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.VoidStripeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.view.input.CommandAction;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.ItemClickedEventArgs;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import com.yworks.yfiles.view.input.MoveViewportInputMode;
import com.yworks.yfiles.view.input.NodeAlignmentPolicy;
import com.yworks.yfiles.view.input.NodeDropInputMode;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.PopulateItemPopupMenuEventArgs;
import com.yworks.yfiles.view.input.PortCandidateValidity;
import com.yworks.yfiles.view.input.ReparentNodeHandler;
import com.yworks.yfiles.view.input.ReparentStripeHandler;
import com.yworks.yfiles.view.input.SelectionEventArgs;
import com.yworks.yfiles.view.input.StripeSubregion;
import com.yworks.yfiles.view.input.StripeSubregionTypes;
import com.yworks.yfiles.view.input.TableEditorInputMode;
import toolkit.AbstractDemo;
import toolkit.DragAndDropSupport;
import toolkit.optionhandler.Label;
import toolkit.optionhandler.OptionEditor;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * This demo shows how an editor for business process diagrams can be created using yFiles for Java (Swing).
 *
 * <p>The visualization and business logic is based on the BPMN 1.1 specification but isn't meant to
 * implement all aspects of the specification but to demonstrate what techniques offered by
 * yFiles for Java (Swing) can be used to create such an editor:
 * </p>
 * <ul>
 * <li>
 * Custom NodeStyles
 * </li>
 * <li>
 * Custom EdgeStyle with custom Arrows
 * </li>
 * <li>
 * Usage of group node insets: Group nodes make use of the {@link INodeInsetsProvider} interface to define
 * what insets they want to have. These insets are used e.g. during the layout.
 * </li>
 * <li>
 * Node creation via Drag'n'Drop: Like in the DragNDrop demo it is shown how a drag'n'drop mechanism can be used by
 * the user to generate nodes with different default styles.
 * </li>
 * <li>
 * Usage of a PortCandidateProvider: The BPMN specification regulates what type of relations are allowed between what
 * type of diagram elements. How the creation of an edge as well as the relation of one of its ports can be restricted
 * to follow this specification is demonstrated using PortCandidateProvider.
 * </li>
 * <li>
 * Usage of Tables: This demo showcases how table nodes can be used for visualization and interaction. It is
 * demonstrated how the layout can be made aware of the table nodes.
 * </li>
 * </ul>
 */
public class BPMNEditorDemo extends AbstractDemo {
  private static final ICommand APPLY_STYLE = ICommand.createCommand("Apply");
  private static final ICommand RESET_STYLE = ICommand.createCommand("Reset");

  private TableEditorInputMode tableEditorInputMode;
  private JComboBox<String> graphChooserBox;
  private boolean inLoadSample;

  // the list containing the nodes to drag from
  private JList<INode> palette;

  //Option stuff

  private JPanel editorPanel;
  private OptionEditor builder;
  private JLabel styleOptionLabel;
  private static final HashMap<Class, NodeStyleConfiguration> styleConfigurationHashMap;
  private static final BpmnEdgeStyleConfiguration bpmnEdgeStyleConfiguration = new BpmnEdgeStyleConfiguration();

  static {
    styleConfigurationHashMap = new HashMap<>();
    addConfigToMap(ActivityNodeStyle.class, new ActivityNodeStyleConfiguration());
    addConfigToMap(AnnotationNodeStyle.class, new AnnotationNodeStyleConfiguration());
    addConfigToMap(ChoreographyNodeStyle.class, new ChoreographyNodeStyleConfiguration());
    addConfigToMap(ConversationNodeStyle.class, new ConversationNodeStyleConfiguration());
    addConfigToMap(DataObjectNodeStyle.class, new DataObjectNodeStyleConfiguration());
    addConfigToMap(EventNodeStyle.class, new EventNodeStyleConfiguration());
    addConfigToMap(GatewayNodeStyle.class, new GatewayNodeStyleConfiguration());
    addConfigToMap(PoolNodeStyle.class, new PoolNodeStyleConfiguration());
  }

  private static <T extends INodeStyle> void addConfigToMap(Class<T> clazz, NodeStyleConfiguration<T> config) {
    styleConfigurationHashMap.put(clazz, config);
  }

  /**
   * Adds a menu bar to the JRootPane of the application frame in addition to the default
   * graph component, toolbar, and help pane.
   */
  protected void configure(JRootPane rootPane) {
    Container contentPane = rootPane.getContentPane();

    final JScrollPane optionContainer = new JScrollPane(newOptionPane());
    optionContainer.setPreferredSize(new Dimension(300, 340));
    optionContainer.setMinimumSize(new Dimension(50, 340));

    // the list that will contain the nodes which may be dragged into the
    // graph component
    // see populatePalette() for the list's contents
    palette = new JList<>();
    JScrollPane paletteContainer = new JScrollPane(palette);
    //paletteContainer.setPreferredSize(new Dimension(300, 250));

    final JSplitPane vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, paletteContainer, optionContainer);
    vSplit.setResizeWeight(1.0);

    vSplit.setPreferredSize(new Dimension(300, 100));

    final JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vSplit, graphComponent);
    leftSplit.setResizeWeight(0.0);

    contentPane.add(leftSplit, BorderLayout.CENTER);

    JToolBar toolBar = createToolBar();
    if (toolBar != null) {
      configureToolBar(toolBar);
      contentPane.add(toolBar, BorderLayout.NORTH);
    }

    JComponent helpPane = createHelpPane();
    if (helpPane != null) {
      contentPane.add(helpPane, BorderLayout.EAST);
    }

    JMenuBar menuBar = new JMenuBar();
    configureMenu(menuBar);
    rootPane.setJMenuBar(menuBar);
  }

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("New", "new-document-16.png", ICommand.NEW, null, graphComponent));
    toolBar.add(new OpenBpmnAction(graphComponent, true));
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE_AS, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png", ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Fit Graph to Bounds", "fit2-16.png", ICommand.FIT_GRAPH_BOUNDS,
        null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Run Layout", "layout-hierarchic.png", RUN_LAYOUT, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(new JLabel("Sample:"));
    toolBar.add(createGraphSampleComboBox());
  }


  /**
   * Configures the given {@link JMenuBar}.
   * @param menuBar the {@link JMenuBar} to configure
   */
  private void configureMenu(JMenuBar menuBar) {
    JMenu fileMenu = new JMenu("File");
    fileMenu.add(createCommandMenuItemAction("New", ICommand.NEW, null, graphComponent));
    fileMenu.add(new OpenBpmnAction(graphComponent, false));
    fileMenu.add(createCommandMenuItemAction("Save as...", ICommand.SAVE_AS, null, graphComponent));
    fileMenu.add(createExitAction());
    menuBar.add(fileMenu);

    JMenu viewMenu = new JMenu("View");
    viewMenu.add(createCommandMenuItemAction("Increase zoom", ICommand.INCREASE_ZOOM, null, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Zoom 1:1", ICommand.ZOOM, 1, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Decrease zoom", ICommand.DECREASE_ZOOM, null, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Fit Graph to Bounds", ICommand.FIT_GRAPH_BOUNDS, null,
        graphComponent));
    menuBar.add(viewMenu);
  }

  /**
   * Creates the JComboBox where the various graphs are selectable.
   */
  private JComponent createGraphSampleComboBox() {
    graphChooserBox = new JComboBox<>(new String[]{
        "GraphML: Business",
        "GraphML: Collaboration",
        "GraphML: Different Exception Flows",
        "GraphML: Expanded Subprocess",
        "GraphML: Lanes Segment",
        "GraphML: Lanes with Information Systems",
        "GraphML: Matrix Lanes",
        "GraphML: Process Normal Flow",
        "GraphML: Project Application",
        "GraphML: Simple BPMN Model",
        "GraphML: Vertical Swimlanes",
        "BPMN-DI: Choreography",
        "BPMN-DI: Collaboration",
        "BPMN-DI: Collapsed Subprocess",
        "BPMN-DI: Different Exception Flows",
        "BPMN-DI: Horizontal Swimlanes",
        "BPMN-DI: Label Styles",
        "BPMN-DI: Lanes with Information Systems",
        "BPMN-DI: Multiple Diagrams",
        "BPMN-DI: Process Normal Flow",
        "BPMN-DI: Project Application",
        "BPMN-DI: Simple BPMN Model",
        "BPMN-DI: Subprocess Hierarchy",
        "BPMN-DI: Vertical Swimlanes",
    });
    graphChooserBox.setMaximumSize(graphChooserBox.getPreferredSize());
    graphChooserBox.addActionListener(e -> onSampleGraphChanged());
    return graphChooserBox;
  }

  /**
   * Reads the currently selected GraphML from the graphChooserBox
   */
  private void onSampleGraphChanged() {
    if (inLoadSample) {
      return;
    }

    String item = (String) graphChooserBox.getSelectedItem();
    int idx = item.indexOf(':');
    boolean graphml = idx > -1 ? item.startsWith("GraphML") : true;
    String key = idx > -1 ? item.substring(idx + 1).trim() : item;
    if (key == null || "None".equals(key)) {
      // no specific item - just clear the graph
      graphComponent.getGraph().clear();
      // and fit the contents
      ICommand.FIT_GRAPH_BOUNDS.execute(null, graphComponent);
      return;
    }
    inLoadSample = true;
    setUIEnabled(false);
    // derive the file name from the key
    String type = graphml ? "graphml" : "bpmn";
    String fileName = "resources/" + type + '/' + key.toLowerCase();
    fileName = fileName.replace("-", "");
    fileName = fileName.replace(" ", "_") + '.' + type;

    try {
      // load the sample graph and start the layout algorithm
      if (graphml) {
        graphComponent.importFromGraphML(getClass().getResource(fileName));
      } else {
        new BpmnDiParser().load(graphComponent.getGraph(), getClass().getResource(fileName));
        graphComponent.fitGraphBounds();
      }
    } catch (IOException exc) {
      exc.printStackTrace();
    } finally {
      inLoadSample = false;
      setUIEnabled(true);
      clearOptionPane("No Properties to show");
    }
  }

  private void setUIEnabled(boolean enabled) {
    graphChooserBox.setEnabled(enabled);
    graphComponent.setFileIOEnabled(enabled);

    // Note:
    // Changing the enabled state triggers the CommandManager method
    // invalidateRequerySuggested() which in turn also ensures
    // that the enabled state of file IO actions are updated
    ((GraphEditorInputMode) graphComponent.getInputMode()).setEnabled(enabled);
  }

  /**
   * Creates an {@link Action} to exit the demo.
   */
  private Action createExitAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    };
    action.putValue(Action.NAME, "Exit");
    return action;
  }

  /**
   * Initializes the styles, the grid, the snap context, the input bindings and loads a sample graph.
   */
  public void initialize() {
    // initialize the graph
    initializeGraph();

    // initialize the input mode
    initializeInputModes();

    // setup the palette to drag nodes from
    configureNodePalette();
  }

  /**
   * Loads and centers a sample graph in the graph component.
   */
  public void onVisible() {
    // loads the example graph
    // load hierarchic sample graph and apply the hierarchic layout
    if (graphChooserBox.getSelectedItem() != "GraphML: Business") {
      graphChooserBox.setSelectedItem("GraphML: Business");
    } else {
      onSampleGraphChanged();
    }
    EventQueue.invokeLater(graphComponent::fitGraphBounds);
  }

  /**
   * Actually applies the style properties.
   */
  private void applyStyle() {
    Object config = builder.getConfiguration();
    if (config instanceof NodeStyleConfiguration) {
      graphComponent.getSelection().getSelectedNodes().stream().findFirst().ifPresent(node ->
      ((NodeStyleConfiguration) config).apply(graphComponent, node));
    } else if (config instanceof EdgeStyleConfiguration) {
      graphComponent.getSelection().getSelectedEdges().stream().findFirst().ifPresent(edge ->
      ((EdgeStyleConfiguration) config).apply(graphComponent, edge));
    }
  }

  private JComponent newOptionPane() {
    final JPanel optionPane = new JPanel(new BorderLayout());
    optionPane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

    builder = new OptionEditor();

    JPanel layoutComboPanel = new JPanel(new BorderLayout(0, 5));
    styleOptionLabel = new JLabel("No Item selected");
    styleOptionLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
    layoutComboPanel.add(styleOptionLabel, BorderLayout.NORTH);
    final JPanel innerLayoutComboPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    layoutComboPanel.add(innerLayoutComboPanel, BorderLayout.CENTER);
    layoutComboPanel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.SOUTH);

    optionPane.add(layoutComboPanel, BorderLayout.NORTH);
    editorPanel = new JPanel(new GridLayout(1, 1));
    optionPane.add(editorPanel, BorderLayout.CENTER);

    final JPanel buttonPane = new JPanel(new GridLayout(1, 2, 6, 6));
    buttonPane.add(new JButton(new CommandAction(APPLY_STYLE, null, graphComponent)));
    buttonPane.add(new JButton(new CommandAction(RESET_STYLE, null, graphComponent)));

    final JPanel intermediatePane = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    intermediatePane.add(buttonPane);

    optionPane.add(intermediatePane, BorderLayout.SOUTH);
    return optionPane;
  }

  private boolean executeApplyStyleCommand(ICommand command, Object parameter, Object source) {
    applyStyle();
    return true;
  }

  private boolean executeResetStyleCommand(ICommand command, Object parameter, Object source) {
    builder.resetEditor((JComponent) editorPanel.getComponent(0));
    return true;
  }

  private boolean canExecuteChangeCommand(ICommand command, Object parameter, Object source) {
    return builder.getConfiguration() != null;
  }

  /**
   * Initializes the graph instance and sets default styles.
   */
  public void initializeGraph() {
    GraphMLIOHandler ioh = new GraphMLIOHandler();

    // we set the IO handler on the GraphComponent, so the GraphComponent's IO methods
    // will pick up our handler for use during serialization and deserialization.
    graphComponent.setGraphMLIOHandler(ioh);

    // map the classes in the bpmn.view package to a separate bpmn namespace
    ioh.addXamlNamespaceMapping(BpmnConstants.YFILES_BPMN_NS, BpmnNodeStyle.class);
    ioh.addXamlNamespaceMapping(BpmnConstants.YFILES_BPMN_LEGACY_NS, ActivityNodeStyleExtension.class);
    ioh.addNamespace(BpmnConstants.YFILES_BPMN_NS, BpmnConstants.YFILES_BPMN_PREFIX);
    // after loading a sample diagram, the item property pane should be cleared
    ioh.addParsedListener((source, args) -> clearOptionPane("No items selected"));

    DefaultFoldingEdgeConverter edgeConverter = new DefaultFoldingEdgeConverter();
    edgeConverter.setReusingFolderNodePortsEnabled(true);
    edgeConverter.setReusingMasterPortsEnabled(true);
    FoldingManager manager = new FoldingManager();
    manager.setFoldingEdgeConverter(edgeConverter);
    IFoldingView foldingView = manager.createFoldingView();
    IGraph graph = foldingView.getGraph();
    graphComponent.setGraph(graph);

    // Ports should not be removed when an attached edge is deleted
    INodeDefaults nodeDefaults = graph.getNodeDefaults();
    nodeDefaults.getPortDefaults().setAutoCleanUpEnabled(false);

    // Set default styles and label model parameter
    IEdgeDefaults edgeDefaults = graph.getEdgeDefaults();
    BpmnEdgeStyle bpmnEdgeStyle = new BpmnEdgeStyle();
    bpmnEdgeStyle.setType(EdgeType.SEQUENCE_FLOW);
    edgeDefaults.setStyle(bpmnEdgeStyle);
    edgeDefaults.setStyleInstanceSharingEnabled(false);
    edgeDefaults.getLabelDefaults().setLayoutParameter(
        new EdgeSegmentLabelModel(10, 0, 0, true, EdgeSides.ABOVE_EDGE).createDefaultParameter());
    // For nodes we use a CompositeLabelModel that combines label placements inside and outside of the node
    CompositeLabelModel compositeLabelModel = new CompositeLabelModel();
    compositeLabelModel.getLabelModels().add(new InteriorLabelModel());
    ExteriorLabelModel exteriorLabelModel = new ExteriorLabelModel();
    exteriorLabelModel.setInsets(new InsetsD(10));
    compositeLabelModel.getLabelModels().add(exteriorLabelModel);
    nodeDefaults.getLabelDefaults().setLayoutParameter(compositeLabelModel.createDefaultParameter());
    nodeDefaults.getLabelDefaults().setStyle(BpmnLabelStyle.newDefaultInstance());

    graph.getGroupNodeDefaults().setStyle(new GroupNodeStyle());

    // use a specialized port candidate provider
    GraphDecorator decorator = manager.getMasterGraph().getDecorator();
    decorator.getNodeDecorator().getPortCandidateProviderDecorator().setFactory(
        node -> (node.getStyle() instanceof BpmnNodeStyle || node.getStyle() instanceof GroupNodeStyle),
        BpmnPortCandidateProvider::new);
    // Pools only have a dynamic PortCandidate
    decorator.getNodeDecorator().getPortCandidateProviderDecorator().setFactory(
        node -> (node.getStyle() instanceof PoolNodeStyle),
        node -> {
          DefaultPortCandidate candidate = new DefaultPortCandidate(node);
          candidate.setValidity(PortCandidateValidity.DYNAMIC);
          return IPortCandidateProvider.fromCandidates(candidate);
        });

    // allow reconnecting of edges
    decorator.getEdgeDecorator().getEdgeReconnectionPortCandidateProviderDecorator().setImplementation(
        IEdgeReconnectionPortCandidateProvider.ALL_NODE_CANDIDATES);

    // enable undo operations
    manager.getMasterGraph().setUndoEngineEnabled(true);
    // use the undo support from the graph also for all future table instances
    Table.installStaticUndoSupport(manager.getMasterGraph());
  }


  /**
   * Calls {@link #createEditorMode()}  and registers
   * the result as the {@link com.yworks.yfiles.view.CanvasComponent#getInputMode()}.
   */
  public void initializeInputModes() {
    graphComponent.setFileIOEnabled(true);
    graphComponent.setInputMode(createEditorMode());
  }

  /**
   * Initializes additional input bindings for running the layout algorithm and creating an empty graph component.
   */
  private void initializeInputBindings( KeyboardInputMode kim ) {
    kim.addCommandBinding(RUN_LAYOUT, this::executeLayout, this::canExecuteLayout);
    kim.addCommandBinding(ICommand.NEW, this::executeNewCommand, this::canExecuteNewCommand);
    kim.addCommandBinding(APPLY_STYLE, this::executeApplyStyleCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(RESET_STYLE, this::executeResetStyleCommand, this::canExecuteChangeCommand);
  }

  /**
   * Helper that determines whether the {@link ICommand#NEW} can be executed.
   */
  private boolean canExecuteNewCommand(ICommand command, Object parameter, Object source) {
    IGraph graph = graphComponent.getGraph();
    // if the graph has nodes in it, it can be cleared.
    return graph != null && graph.getNodes().size() > 0;
  }


  /**
   * Handler for the {@link ICommand#NEW}
   */
  private boolean executeNewCommand(ICommand command, Object parameter, Object source) {
    IGraph graph = graphComponent.getGraph();
    graph.clear();
    // Clearing the graph programmatically like this won't necessarily trigger an updating of the can-execute-states of the commands.
    // So we do this manually here
    ICommand.invalidateRequerySuggested();
    return true;
  }


  /**
   * A {@link ICommand} that is used to layout the given graph.
   */
  private static final ICommand RUN_LAYOUT = ICommand.createCommand("RunLayout");

  /**
   * Helper that determines whether the {@link #RUN_LAYOUT} can be executed.
   */
  private boolean canExecuteLayout(ICommand command, Object parameter, Object source) {
    IGraph graph = graphComponent.getGraph();
     return graph != null && !(graph.getNodes().size() == 0);
  }

  /**
   * Handler for the {@link #RUN_LAYOUT} command.
   */
  private boolean executeLayout(ICommand command, Object parameter, Object source) {
    setUIEnabled(false);

    // Create a new BpmnLayout using a Left-To-Right layout orientation
    BpmnLayout bpmnLayout = new BpmnLayout();
    bpmnLayout.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);

    //We use Layout executor convenience method that already sets up the whole layout pipeline correctly
    LayoutExecutor layoutExecutor = new LayoutExecutor(graphComponent, bpmnLayout);
    layoutExecutor.setDuration(Duration.ofMillis(500));
    layoutExecutor.setViewportAnimationEnabled(true);
    layoutExecutor.getTableLayoutConfigurator().setHorizontalLayoutEnabled(true);
    layoutExecutor.getTableLayoutConfigurator().setFromSketchEnabled(true);
    // The BpmnLayoutData provides information about the BPMN node and edge types to the layout algorithm.
    layoutExecutor.setLayoutData(new BpmnLayoutData().create(graphComponent.getGraph(), graphComponent.getSelection(), bpmnLayout.getScope()));
    layoutExecutor.addLayoutFinishedListener((src, args) -> setUIEnabled(true));

    layoutExecutor.start();
    return true;
  }

  /**
   * Creates the default input mode for the GraphComponent,
   * @see GraphEditorInputMode
   * @return a new GraphEditorInputMode instance and configures snapping and orthogonal edge editing
   */
  public IInputMode createEditorMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    // enable grouping operations
    geim.setGroupingOperationsAllowed(true);
    // We want orthogonal edge creation and editing
    geim.setOrthogonalEdgeEditingContext(new OrthogonalEdgeEditingContext());

    // don't allow node creation (except for context menu and drag'n'drop)
    geim.setCreateNodeAllowed(false);
    // Alter the ClickHitTestOrder so ports are tested before nodes
    geim.setClickHitTestOrder(new GraphItemTypes[]{
      GraphItemTypes.BEND, GraphItemTypes.EDGE_LABEL, GraphItemTypes.EDGE,
      GraphItemTypes.PORT, GraphItemTypes.NODE, GraphItemTypes.NODE_LABEL
    });
    geim.setDoubleClickHitTestOrder(new GraphItemTypes[] {
      GraphItemTypes.LABEL, GraphItemTypes.ALL
    });
    // automatically removing empty labels destroys choreographies
    geim.setAutoRemovingEmptyLabelsEnabled(false);

    // Enable snapping
    GraphSnapContext snapContext = new GraphSnapContext();
    snapContext.setEdgeToEdgeDistance(10);
    snapContext.setNodeToEdgeDistance(15);
    snapContext.setNodeToNodeDistance(20);
    snapContext.setSnappingBendsToSnapLinesEnabled(true);
    geim.setSnapContext(snapContext);
    // tables shall not become child nodes but reparenting for other nodes is always enabled
    ReparentNodeHandler reparentNodeHandler = new NoTableReparentNodeHandler();
    reparentNodeHandler.setReparentRecognizer(IEventRecognizer.ALWAYS);
    geim.setReparentNodeHandler(reparentNodeHandler);
    // we use a default MoveViewportInputMode that allows us to drag the viewport without pressing 'SHIFT'
    MoveViewportInputMode moveViewportInputMode = new MoveViewportInputMode();
    geim.setMoveViewportInputMode(moveViewportInputMode);
    // increase the priority value of the MoveViewportInputMode so other input modes are still preferred.
    moveViewportInputMode.setPriority(110);
    // disable marquee selection so the MoveViewportInputMode can work without modifiers
    geim.getMarqueeSelectionInputMode().setEnabled(false);

    // ensure that collapsing/expanding nodes does not move the nodes
    geim.getNavigationInputMode().setAutoGroupNodeAlignmentPolicy(NodeAlignmentPolicy.BOTTOM_CENTER);

    // Create a new TEIM instance which also allows drag and drop
    tableEditorInputMode = new TableEditorInputMode();
    // Enable drag & drop of stripes
    tableEditorInputMode.getStripeDropInputMode().setEnabled(true);
    // Maximal level for both reparent and drag and drop is 2
    ReparentStripeHandler reparentStripeHandler = new ReparentStripeHandler();
    reparentStripeHandler.setMaxColumnLevel(2);
    reparentStripeHandler.setMaxRowLevel(2);
    tableEditorInputMode.setReparentStripeHandler(reparentStripeHandler);
    // Add to GEIM - we set the priority higher than for the handle input mode so that handles win if both gestures are possible
    tableEditorInputMode.setPriority(geim.getHandleInputMode().getPriority() + 1);
    geim.add(tableEditorInputMode);

    // Palette drag and drop: configure node drop operations
    configureNodeDropping(geim);

    // setup the context menu
    initializeContextMenu(geim);

    // add double click event handler
    geim.addItemLeftDoubleClickedListener(this::onLeftDoubleClicked);

    // bind selection changes to property pane
    geim.addMultiSelectionFinishedListener(this::updateOptionPane);
    tableEditorInputMode.getStripeSelection().addItemSelectionChangedListener((source, args) -> clearOptionPane("No Properties to show"));

    // initialize additional input bindings
    initializeInputBindings(geim.getKeyboardInputMode());

    return geim;
  }


  private void onLeftDoubleClicked( Object source, ItemClickedEventArgs<IModelItem> args ) {
    if (args.isHandled()) {
      return;
    }

    IModelItem item = args.getItem();

    if (item instanceof ILabel) {
      ((GraphEditorInputMode) graphComponent.getInputMode()).editLabel((ILabel) item);
      args.setHandled(true);
      return;
    }

    if (item instanceof ILabelOwner) {
      ILabelOwner owner = (ILabelOwner) item;
      if (owner.getLabels().size() > 0) {
        ((GraphEditorInputMode) graphComponent.getInputMode()).editLabel(owner.getLabels().first());
        args.setHandled(true);
        return;
      }
    }

    args.setHandled(false);
  }

  private void updateOptionPane(Object source, SelectionEventArgs<IModelItem> args) {
    ISelectionModel<IModelItem> selection = args.getSelection();
    // only show the properties if a single node or edge is selected
    if (selection.size() > 1) {
      clearOptionPane("Multiple Items selected");
      return;
    } else if (selection.size() == 0) {
      clearOptionPane("No Item selected");
      return;
    } else {
      IModelItem item = selection.stream().findFirst().get();
      if (item instanceof INode) {
        INode node = (INode) item;
        INodeStyle style = node.getStyle();
        if (styleConfigurationHashMap.containsKey(style.getClass())) {
          // lookup the style configuration matching the node's style and initialize it with the current style properties
          NodeStyleConfiguration configuration = styleConfigurationHashMap.get(style.getClass());
          configuration.initializeFromExistingStyle(style);
          // update the Item Properties pane using an OptionEditor
          builder.setConfiguration(configuration);
          editorPanel.removeAll();
          editorPanel.add(builder.buildEditor());
          editorPanel.revalidate();
          setLabel(configuration.getClass());
          return;
        }
      } else if (item instanceof IEdge) {
        IEdge edge = (IEdge) item;
        IEdgeStyle style = edge.getStyle();
        if (style instanceof BpmnEdgeStyle) {
          // initialize the edge style configuration with the current edge style properties
          bpmnEdgeStyleConfiguration.initializeFromExistingStyle((BpmnEdgeStyle) style);
          // update the Item Properties pane using an OptionEditor
          builder.setConfiguration(bpmnEdgeStyleConfiguration);
          editorPanel.removeAll();
          editorPanel.add(builder.buildEditor());
          editorPanel.revalidate();
          setLabel(bpmnEdgeStyleConfiguration.getClass());
          return;
        }
      }
    }
    clearOptionPane("No Properties to show");
  }

  private void clearOptionPane(String text) {
    builder.setConfiguration(null);
    editorPanel.removeAll();
    editorPanel.revalidate();
    editorPanel.repaint();
    styleOptionLabel.setText(text);
  }

  private void setLabel(Class classobject) {
    // update the label in the Item Properties pane to show the type of the node or edge whose properties are displayed
    Annotation labelAnnotation = classobject.getDeclaredAnnotation(Label.class);
    String label;
    if (labelAnnotation != null) {
      Label attr = (Label) labelAnnotation;
      label = attr.value();
    } else {
      label = classobject.getName();
    }
    styleOptionLabel.setText(label);
  }


  private void initializeContextMenu(GraphEditorInputMode geim) {
    // open context menu when node or edge is clicked
    geim.setPopupMenuItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE).or(GraphItemTypes.PORT));
    geim.addPopulateItemPopupMenuListener(this::onPopulateItemPopupMenu);
  }

  /**
   * Builds different popup menus depending on the node or edge type
   */
  private void onPopulateItemPopupMenu(Object source, PopulateItemPopupMenuEventArgs<IModelItem> args) {
    if (args.isHandled()) {
      return;
    }
    IModelItem item = args.getItem();
    JPopupMenu menu = (JPopupMenu) args.getMenu();
    IGraph graph = graphComponent.getGraph();

    if (item instanceof INode) {
      INode node = (INode) item;
      INodeStyle style = node.getStyle();

      // If it is not a text annotation itself...
      if (style instanceof AnnotationNodeStyle) {
        // If it is a text annotation node, allow toggling the direction
        final AnnotationNodeStyle textAnnotationStyle = (AnnotationNodeStyle) style;
        addMenuItem(menu, "Toggle direction", (e) -> textAnnotationStyle.setLeft(!textAnnotationStyle.isLeft()));
      } else {
        // ... offer to add a text annotation node
        addMenuItem(menu, "Add Text Annotation", (e) -> {
          IRectangle nl = node.getLayout();
          INode annotationNode = graph.createNode(new PointD(nl.getX(), nl.getY() - 50));
          graph.setStyle(annotationNode, new AnnotationNodeStyle());
          // including a connecting edge
          BpmnEdgeStyle annotationEdgeStyle = new BpmnEdgeStyle();
          annotationEdgeStyle.setType(EdgeType.ASSOCIATION);
          graph.createEdge(node, annotationNode, annotationEdgeStyle);
          InteriorStretchLabelModel model = new InteriorStretchLabelModel();
          model.setInsets(new InsetsD(3, 3, 3, 3));
          ILabel newLabel = graph.addLabel(annotationNode, "", model.createParameter(InteriorStretchLabelModel.Position.CENTER));
          // and start to edit the label
          ((GraphEditorInputMode) graphComponent.getInputMode()).editLabel(newLabel);
        });
      }

      // If it is an Choreography node...
      if (style instanceof ChoreographyNodeStyle) {
        ChoreographyNodeStyle choreographyNodeStyle = (ChoreographyNodeStyle) style;
        menu.addSeparator();
        // ... check if a participant was right-clicked
        Participant participant = choreographyNodeStyle.getParticipant(node, args.getQueryLocation());
        if (participant != null) {
          // and if so, offer to remove it
          addMenuItem(menu, "Remove participant", (e) -> {
            if (!choreographyNodeStyle.getTopParticipants().remove(participant)) {
              choreographyNodeStyle.getBottomParticipants().remove(participant);
            }
          });
          // or toggle its Multi-Instance flag
          addMenuItem(menu, "Toggle Participant Multi-Instance", (e) ->
              participant.setMultiInstance(!participant.isMultiInstance()));
          // or edit its Label
          addMenuItem(menu, "Edit Label", (e) -> {
            ILabelModelParameter parameter = choreographyNodeStyle.getParticipantParameters(participant);
            ILabel label = getLabelFromParameter(node.getLabels(), parameter);
            if (label == null) {
              label = graph.addLabel(node, "");
              graph.setLabelLayoutParameter(label, parameter);
              graph.setStyle(label, BpmnLabelStyle.newDefaultInstance());
            }
            ((GraphEditorInputMode) graphComponent.getInputMode()).editLabel(label);
          });
        } else {
          // if no participant was clicked, a new one can be added to the top or bottom participants
          addMenuItem(menu, "Add Participant at Top", (e) -> {
            Participant newParticipant = new Participant();
            choreographyNodeStyle.getTopParticipants().add(newParticipant);
            ILabelModelParameter parameter = choreographyNodeStyle.getParticipantParameters(newParticipant);
            ILabel newLabel = graph.addLabel(node, "");
            graph.setLabelLayoutParameter(newLabel, parameter);
            graph.setStyle(newLabel, BpmnLabelStyle.newDefaultInstance());
            ((GraphEditorInputMode) graphComponent.getInputMode()).editLabel(newLabel);
          });
          addMenuItem(menu, "Add Participant at Bottom", (e) -> {
            Participant newParticipant = new Participant();
            choreographyNodeStyle.getBottomParticipants().add(newParticipant);
            ILabelModelParameter parameter = choreographyNodeStyle.getParticipantParameters(newParticipant);
            ILabel newLabel = graph.addLabel(node, "");
            graph.setLabelLayoutParameter(newLabel, parameter);
            graph.setStyle(newLabel, BpmnLabelStyle.newDefaultInstance());
            ((GraphEditorInputMode) graphComponent.getInputMode()).editLabel(newLabel);
          });
          addMenuItem(menu, "Edit Label", (e) -> {
            ILabel taskNameBandLabel = null;
            if (node.getLabels().size() < 1 || getLabelFromParameter(node.getLabels(), ChoreographyLabelModel.TASK_NAME_BAND) == null) {
              ILabelModelParameter parameter = ChoreographyLabelModel.TASK_NAME_BAND;
              taskNameBandLabel = graph.addLabel(node, "");
              graph.setLabelLayoutParameter(taskNameBandLabel, parameter);
              graph.setStyle(taskNameBandLabel, BpmnLabelStyle.newDefaultInstance());
            } else {
              taskNameBandLabel = getLabelFromParameter(node.getLabels(), ChoreographyLabelModel.TASK_NAME_BAND);
            }
            ((GraphEditorInputMode) graphComponent.getInputMode()).editLabel(taskNameBandLabel);
          });
        }
      }

      // If it is an Activity node...
      if (style instanceof ActivityNodeStyle) {
        menu.addSeparator();
        // allow to add a Boundary Event as port that uses an EventPortStyle
        addMenuItem(menu, "Add Boundary Event", (e) ->
            graph.addPort(node, FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED, new EventPortStyle()));
      }

      // If a row of a pool node has been hit...
      StripeSubregion stripeDescriptor = tableEditorInputMode.findStripe(args.getQueryLocation(), StripeTypes.ALL,
          StripeSubregionTypes.HEADER);

      if (stripeDescriptor != null) {
        IStripe stripe = stripeDescriptor.getStripe();
        // add the insert before menu item
        addMenuItem(menu, "Insert new lane before " + stripe, (e) -> {
          IStripe parent = stripe.getParentStripe();
          int index = stripe.getIndex();
          tableEditorInputMode.insertChild(parent, index);
        });
        // add the insert after menu item
        addMenuItem(menu, "Insert new lane after " + stripe, (e) -> {
          IStripe parent = stripe.getParentStripe();
          int index = stripe.getIndex();
          tableEditorInputMode.insertChild(parent, index + 1);
        });
        // add the delete menu item
        addMenuItem(menu, "Delete lane", (e) -> tableEditorInputMode.deleteStripe(stripe));

        if (stripe instanceof IRow) {
          // ... allow to increase or decrease the row header size
          InsetsD insets = stripe.getInsets();
          InsetsD defaultInsets = stripe.getTable().getRowDefaults().getInsets();

          menu.addSeparator();

          InsetsD insetsBefore = stripe.getTable().getAccumulatedInsets();
          if (insets.getLeft() > defaultInsets.getLeft()) {
            addMenuItem(menu, "Reduce header size", (e) -> {
              // by reducing the header size of one of the rows, the size of the table insets might change
              stripe.getTable().setStripeInsets(stripe,
                  InsetsD.fromLTRB(insets.getLeft() - defaultInsets.getLeft(), insets.getTop(), insets.getRight(),
                      insets.getBottom()));
              InsetsD insetsAfter = stripe.getTable().getAccumulatedInsets();
              // if the table insets have changed, the bounds of the pool node have to be adjusted as well
              double diff = insetsBefore.getLeft() - insetsAfter.getLeft();
              RectD layout = node.getLayout().toRectD();
              graph.setNodeLayout(node,
                  new RectD(layout.getX() + diff, layout.getY(), layout.getWidth() - diff, layout.getHeight()));
            });
          }
          addMenuItem(menu, "Increase header size", (e) -> {
            stripe.getTable().setStripeInsets(stripe,
                InsetsD.fromLTRB(insets.getLeft() + defaultInsets.getLeft(), insets.getTop(), insets.getRight(),
                    insets.getBottom()));
            InsetsD insetsAfter = stripe.getTable().getAccumulatedInsets();
            double diff = insetsBefore.getLeft() - insetsAfter.getLeft();
            RectD layout = node.getLayout().toRectD();
            graph.setNodeLayout(node,
                new RectD(layout.getX() + diff, layout.getY(), layout.getWidth() - diff, layout.getHeight()));
          });
        }
      }
      // we don't want to be queried again if there are more items at this location
      args.setHandled(true);
    } else if (item instanceof IEdge) {
      IEdge edge = (IEdge) item;
      // For edges a label with a Message Icon may be added
      addMenuItem(menu, "Add Initiating Message Icon Label", (evt) -> {
        ILabelModelParameter modelParameter = new EdgeSegmentLabelModel(0, 0, 0,
            false, EdgeSides.ON_EDGE).createDefaultParameter();
        graph.addLabel(edge, "", modelParameter, MessageLabelStyle.createInitiatingStyle(), new SizeD(20, 14));
      });
      addMenuItem(menu, "Add Response Message Icon Label", (evt) -> {
        ILabelModelParameter modelParameter = new EdgeSegmentLabelModel(0, 0, 0,
            false, EdgeSides.ON_EDGE).createDefaultParameter();
        graph.addLabel(edge, "", modelParameter, MessageLabelStyle.createResponseStyle(), new SizeD(20, 14));
      });

      // we don't want to be queried again if there are more items at this location
      args.setHandled(true);
    }
  }

  /**
   * Retrieves the label to the corresponding choreography participant.
   * @param nodeLabels All labels of this node
   * @param parameter Parameter of this taskNameBand
   */
  private static ILabel getLabelFromParameter(Iterable<ILabel> nodeLabels, ILabelModelParameter parameter) {
    for (ILabel label : nodeLabels) {
      if (ChoreographyLabelModel.areEqual(label.getLayoutParameter(), parameter)) {
        return label;
      }
    }

    return null;
  }

  private void addMenuItem(JPopupMenu menu, String name, Consumer<ActionEvent> consumer) {
    AbstractAction action = new AbstractAction(name) {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        consumer.accept(actionEvent);
      }
    };
    menu.add(action);
  }


  /**
   * Enables support for dropping nodes on the given {@link GraphEditorInputMode}.
   * @param editorInputMode The GraphEditorInputMode for this application.
   */
  private void configureNodeDropping(GraphEditorInputMode editorInputMode) {
    // dropping nodes from the palette
    NodeDropInputMode myNodeDropInputMode = new MyNodeDropInputMode();
    myNodeDropInputMode.setEnabled(true);
    // We identify the group nodes during a drag by either a custom tag or if they have a table associated.
    myNodeDropInputMode.setIsGroupNodePredicate(
        node -> node.lookup(ITable.class) != null || node.getTag() == "GroupNode");

    editorInputMode.setNodeDropInputMode(myNodeDropInputMode);
  }

  /**
   * Populates the palette with a pool node, a row and nodes using the different BPMN types.
   * The elements in the palette can be dragged over and dropped into the demo's
   * <code>GraphComponent</code> to create new elements of the corresponding type
   * in the displayed diagram.
   */
  private void configureNodePalette() {
    // create a new graph in which the palette nodes live
    DefaultGraph nodeContainer = new DefaultGraph();

    // Create the sample node for the pool
    PoolNodeStyle poolNodeStyle = new PoolNodeStyle();
    INode poolNode = nodeContainer.createNode(PointD.ORIGIN, poolNodeStyle);
    ITable poolTable = getTable(poolNodeStyle);
    poolTable.getColumnDefaults().setInsets(InsetsD.EMPTY);
    poolTable.setInsets(InsetsD.EMPTY);
    poolTable.createGrid(1, 1);
    //Use twice the default width for this sample column (looks nicer in the preview)
    poolTable.getRootColumn().getChildColumns().stream().findFirst().ifPresent(column -> {
      poolTable.setSize(column, column.getActualSize() * 2);
      nodeContainer.setNodeLayout(poolNode, poolTable.getLayout().toRectD());
      poolTable.addLabel(poolTable.getRootRow().getChildRows().iterator().next(), "Pool");
    });

    PoolNodeStyle rowPoolNodeStyle = new PoolNodeStyle();
    INode rowNode = nodeContainer.createNode(PointD.ORIGIN, rowPoolNodeStyle);
    ITable rowTable = getTable(rowPoolNodeStyle);

    IStripe rowSampleRow = rowTable.createRow(100d);
    IStripe rowSampleColumn = rowTable.createColumn(200d);
    rowTable.setStyle(rowSampleColumn, VoidStripeStyle.INSTANCE);
    rowTable.setStripeInsets(rowSampleColumn, InsetsD.EMPTY);
    rowTable.setInsets(InsetsD.EMPTY);
    rowTable.addLabel(rowSampleRow, "Row");
    nodeContainer.setNodeLayout(rowNode, rowTable.getLayout().toRectD());
    // Set the first row as tag so the NodeDragControl knows that a row and not a complete pool node shall be dragged
    rowTable.getRootRow().getChildRows().stream().findFirst().ifPresent(rowNode::setTag);

    // Add BPMN sample nodes - mark ActivityNodes and GroupNodes as 'GroupNode' so they may contain other nodes
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(80, 50)), new ActivityNodeStyle(), "GroupNode");
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(50, 50)), new GatewayNodeStyle());
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(50, 50)), new EventNodeStyle());
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(80, 20)), new AnnotationNodeStyle());
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(40, 60)), new DataObjectNodeStyle());
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(50, 50)), new DataStoreNodeStyle());
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(80, 60)), new GroupNodeStyle(), "GroupNode");

    // Add a Choreography node with 2 participants
    ChoreographyNodeStyle choreographyNodeStyle = new ChoreographyNodeStyle();
    choreographyNodeStyle.getTopParticipants().add(new Participant());
    choreographyNodeStyle.getBottomParticipants().add(new Participant());
    INode choreographyNode = nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(80, 90)),
        choreographyNodeStyle,
        "GroupNode");
    nodeContainer.addLabel(choreographyNode, "Participant 1",
        new ChoreographyLabelModel().createParticipantParameter(true, 0));
    nodeContainer.addLabel(choreographyNode, "Participant 2",
        new ChoreographyLabelModel().createParticipantParameter(false, 0));

    ConversationNodeStyle conversationNodeStyle = new ConversationNodeStyle();
    conversationNodeStyle.setType(ConversationType.CONVERSATION);
    nodeContainer.createNode(new RectD(PointD.ORIGIN, new SizeD(50, 50)), conversationNodeStyle);

    initializePaletteModel(nodeContainer);
  }

  private static ITable getTable(PoolNodeStyle poolNodeStyle) {
    return poolNodeStyle.getTableNodeStyle().getTable();
  }

  private void initializePaletteModel(DefaultGraph nodeContainer) {
    // fill the palette with sample nodes
    DefaultListModel<INode> model = new DefaultListModel<>();

    nodeContainer.getNodes().forEach(model::addElement);
    palette.setModel(model);

    // set a custom cell renderer painting the sample nodes on the palette
    palette.setCellRenderer(new PaletteNodeRenderer());

    // configure the palette as source for drag and drop operations
    palette.setDragEnabled(true);
    TransferHandler transferHandler = new NodeAndStripeTransferHandler();
    DragAndDropSupport.disableDefaultPreview(transferHandler);
    palette.setTransferHandler(transferHandler);

    // allow single selection only and select the first entry
    palette.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    palette.setSelectedIndex(0);
  }

  /**
   * Custom {@link NodeDropInputMode} that disallows creating a table node inside of a group node
   * (especially inside of another table node)
   */
  private static class MyNodeDropInputMode extends NodeDropInputMode {
    @Override
    protected IModelItem getDropTarget(PointD dragLocation) {
      //Ok, this node has a table associated -> disallow dragging it into a group node.
      if (getDraggedItem().lookup(ITable.class) != null) {
        return null;
      }
      return super.getDropTarget(dragLocation);
    }
  }

  /**
   * Custom {@link ReparentNodeHandler} that disallows reparenting a table node.
   */
  private static class NoTableReparentNodeHandler extends ReparentNodeHandler {
    @Override
    public boolean isValidParent(IInputModeContext context, INode node, INode newParent) {
      // table nodes shall not become child nodes
      return node.lookup(ITable.class) == null && super.isValidParent(context, node, newParent);
    }
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new BPMNEditorDemo().start("Business Process Models - yFiles for Java (Swing)");
    });
  }

  @Override
  protected JFrame createFrame(String title) {
    JFrame frame = super.createFrame(title);
    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    return frame;
  }


  /**
   * Opens diagrams from either GraphML format or BPMN DI format.
   */
  private static class OpenBpmnAction extends AbstractAction {
    private static final String KEY_FILE_CHOOSER = "OpenBpmnAction.fileChooser";


    final GraphComponent graphComponent;
    JFileChooser jfc;

    OpenBpmnAction( GraphComponent graphComponent, boolean showIcon ) {
      super("Open");
      this.graphComponent = graphComponent;

      if (showIcon) {
        final Class c = getClass();
        final ClassLoader cl = c.getClassLoader();
        final URL icon = cl.getResource("resources/open-16.png");
        if (icon != null) {
          try {
            putValue(SMALL_ICON, new ImageIcon(icon));
          } catch (Exception ex) {
            // ignore - do not use an icon if resource loading failed
          }
        }
      }
    }

    @Override
    public void actionPerformed( final ActionEvent e ) {
      if (jfc == null) {
        Object value = graphComponent.getClientProperty(KEY_FILE_CHOOSER);
        if (value instanceof JFileChooser) {
          jfc = (JFileChooser) value;
        } else {
          jfc = newFileChooser();
          graphComponent.putClientProperty(KEY_FILE_CHOOSER, jfc);
        }
      }
      if (jfc.showOpenDialog(graphComponent) == JFileChooser.APPROVE_OPTION) {
        File src = jfc.getSelectedFile();
        FileFilter filter = jfc.getFileFilter();
        if (".bpmn".equals(((SimpleFilter) filter).getExt())) {
          try {
            new BpmnDiParser().load(graphComponent.getGraph(), src.getAbsolutePath());
            graphComponent.fitGraphBounds();
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        } else {
          try {
            graphComponent.importFromGraphML(src);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }
    }

    private static JFileChooser newFileChooser() {
      JFileChooser jfc = new JFileChooser();
      jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
      FileFilter filter = new SimpleFilter(".bpmn", "BPMN Files (*.bpmn)");
      jfc.addChoosableFileFilter(filter);
      jfc.addChoosableFileFilter(new SimpleFilter(".graphml", "GraphML Files (*.graphml)"));
      jfc.setAcceptAllFileFilterUsed(false);
      jfc.setFileFilter(filter);
      return jfc;
    }
  }

  /**
   * Filters files in a file chooser depending on their file name extension.
   */
  private static class SimpleFilter extends FileFilter {
    final String ext;
    final String description;

    SimpleFilter( final String ext, final String description ) {
      this.ext = ext;
      this.description = description;
    }

    @Override
    public boolean accept( final File f ) {
      return f.isDirectory() || f.getName().toLowerCase().endsWith(ext);
    }

    @Override
    public String getDescription() {
      return description;
    }

    String getExt() {
      return ext;
    }
  }
}
