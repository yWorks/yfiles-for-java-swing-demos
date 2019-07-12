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
package analysis.graphanalysis;

import analysis.graphanalysis.algorithms.AlgorithmConfiguration;
import analysis.graphanalysis.algorithms.CentralityConfig;
import analysis.graphanalysis.algorithms.ConnectivityConfig;
import analysis.graphanalysis.algorithms.CyclesConfig;
import analysis.graphanalysis.algorithms.MinimumSpanningTreeConfig;
import analysis.graphanalysis.algorithms.PathsConfig;
import com.yworks.yfiles.graph.AdjacencyTypes;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.ComponentArrangementStyles;
import com.yworks.yfiles.layout.ComponentLayout;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LabelPlacements;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.PreferredPlacementDescriptor;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayoutData;
import com.yworks.yfiles.layout.organic.Scope;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.MoveInputMode;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * This demo showcases a selection of algorithms to analyse the structure of a graph.
 */
public class GraphAnalysisDemo extends AbstractDemo {
  private static final String validationPattern = "^(0*[1-9][0-9]*(\\.[0-9]+)?|0+\\.[0-9]*[1-9][0-9]*)$";

  private JComboBox<NamedEntry> sampleComboBox;
  private JComboBox<NamedEntry> algorithmComboBox;
  private JCheckBox edgeWeightsCheckBox;
  private JCheckBox directionCheckBox;

  private Action prevSampleAction;
  private Action nextSampleAction;
  private Action generateEdgeLabelsAction;
  private Action deleteEdgeLabelsAction;
  private Action layoutAction;

  private Mapper<INode, Boolean> incrementalElements;
  private Mapper<INode, Boolean> incrementalNodesMapper;

  private boolean preventLayout;
  private boolean enabledUI = true;

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("New", "new-document-16.png", ICommand.NEW, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Fit the graph content", "fit2-16.png", ICommand.FIT_CONTENT, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png", ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(prevSampleAction = createPrevSampleAction());
    toolBar.add(sampleComboBox = createSampleComboBox());
    toolBar.add(nextSampleAction = createNextSampleAction());
    toolBar.addSeparator();
    toolBar.add(algorithmComboBox = createAlgorithmComboBox());
    toolBar.addSeparator();
    toolBar.add(generateEdgeLabelsAction = createGenerateEdgeLabelsAction());
    toolBar.add(deleteEdgeLabelsAction = createDeleteEdgeLabelsAction());
    toolBar.addSeparator();
    toolBar.add(edgeWeightsCheckBox = createUniformEdgeWeightsCheckBox());
    toolBar.add(directionCheckBox = createDirectionCheckBox());
    toolBar.addSeparator();
    toolBar.add(new JButton(layoutAction = createLayoutAction()));
  }

  /**
   * Enables or disables UI elements while loading or layouting.
   */
  private void enableUI(boolean enable) {
    if (enabledUI != enable) {
      enabledUI = enable;

      AlgorithmConfiguration config = getAlgorithmConfig();

      sampleComboBox.setEnabled(enable);
      algorithmComboBox.setEnabled(enable);
      edgeWeightsCheckBox.setEnabled(enable && config.supportsEdgeWeights());
      directionCheckBox.setEnabled(enable && config.supportsDirectedEdges());
      prevSampleAction.setEnabled(enable && isPrevSampleActionEnabled());
      nextSampleAction.setEnabled(enable && isNextSampleActionEnabled());
      generateEdgeLabelsAction.setEnabled(enable && config.supportsEdgeWeights());
      deleteEdgeLabelsAction.setEnabled(enable && config.supportsEdgeWeights());
      layoutAction.setEnabled(enable);
    }
  }

  /**
   * Creates an {@link Action} for choosing the previous sample graph.
   */
  private Action createPrevSampleAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sampleComboBox.setSelectedIndex(sampleComboBox.getSelectedIndex() - 1);
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Load previous graph");
    action.putValue(Action.SMALL_ICON, createIcon("arrow-left-16.png"));
    return action;
  }

  /**
   * Returns whether there are previous samples available.
   * @return whether there are previous samples available.
   */
  private boolean isPrevSampleActionEnabled() {
    return sampleComboBox.getSelectedIndex() > 0;
  }

  /**
   * Creates an {@link Action} for choosing the next sample graph.
   */
  private Action createNextSampleAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sampleComboBox.setSelectedIndex(sampleComboBox.getSelectedIndex() + 1);
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Load next graph");
    action.putValue(Action.SMALL_ICON, createIcon("arrow-right-16.png"));
    return action;
  }

  /**
   * Returns whether there are next samples available.
   * @return whether there are next samples available.
   */
  private boolean isNextSampleActionEnabled() {
    return sampleComboBox.getSelectedIndex() < sampleComboBox.getModel().getSize() - 1;
  }

  /**
   * Creates a {@link JComboBox} for choosing the current sample graph.
   */
  private JComboBox<NamedEntry> createSampleComboBox() {
    NamedEntry[] samples = {
        new NamedEntry("Sample: Minimum Spanning Tree", "minimumspanningtree"),
        new NamedEntry("Sample: Connected Components", "connectivity"),
        new NamedEntry("Sample: Biconnected Components", "connectivity"),
        new NamedEntry("Sample: Strongly Connected Components", "connectivity"),
        new NamedEntry("Sample: Reachability", "connectivity"),
        new NamedEntry("Sample: Shortest Paths", "paths"),
        new NamedEntry("Sample: All Paths", "paths"),
        new NamedEntry("Sample: All Chains", "paths"),
        new NamedEntry("Sample: Single Source", "paths"),
        new NamedEntry("Sample: Cycles", "cycles"),
        new NamedEntry("Sample: Degree Centrality", "centrality"),
        new NamedEntry("Sample: Weight Centrality", "centrality"),
        new NamedEntry("Sample: Graph Centrality", "centrality"),
        new NamedEntry("Sample: Node Edge Betweeness Centrality", "centrality"),
        new NamedEntry("Sample: Closeness Centrality", "centrality")
    };

    JComboBox<NamedEntry> comboBox = new JComboBox<>(samples);
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setToolTipText("Choose the sample graph");

    comboBox.addActionListener(e -> onSampleChanged());

    return comboBox;
  }

  /**
   * Creates a {@link JComboBox} for choosing the current algorithm.
   */
  private JComboBox<NamedEntry> createAlgorithmComboBox() {
    NamedEntry[] algorithms = {
        new NamedEntry("Algorithm: Minimum Spanning Tree",
            new MinimumSpanningTreeConfig()),
        new NamedEntry("Algorithm: Connected Components",
            new ConnectivityConfig(ConnectivityConfig.AlgorithmType.CONNECTED_COMPONENTS)),
        new NamedEntry("Algorithm: Biconnected Components",
            new ConnectivityConfig(ConnectivityConfig.AlgorithmType.BICONNECTED_COMPONENTS)),
        new NamedEntry("Algorithm: Strongly Connected Components",
            new ConnectivityConfig(ConnectivityConfig.AlgorithmType.STRONGLY_CONNECTED_COMPONENTS)),
        new NamedEntry("Algorithm: Reachability",
            new ConnectivityConfig(ConnectivityConfig.AlgorithmType.REACHABILITY)),
        new NamedEntry("Algorithm: Shortest Paths",
            new PathsConfig(PathsConfig.AlgorithmType.ALGORITHM_TYPE_SHORTEST_PATHS)),
        new NamedEntry("Algorithm: All Paths",
            new PathsConfig(PathsConfig.AlgorithmType.ALGORITHM_TYPE_ALL_PATHS)),
        new NamedEntry("Algorithm: All Chains",
            new PathsConfig(PathsConfig.AlgorithmType.ALGORITHM_TYPE_ALL_CHAINS)),
        new NamedEntry("Algorithm: Single Source",
            new PathsConfig(PathsConfig.AlgorithmType.ALGORITHM_TYPE_SINGLE_SOURCE)),
        new NamedEntry("Algorithm: Cycles",
            new CyclesConfig()),
        new NamedEntry("Algorithm: Degree Centrality",
            new CentralityConfig(CentralityConfig.AlgorithmType.DEGREE_CENTRALITY)),
        new NamedEntry("Algorithm: Weight Centrality",
            new CentralityConfig(CentralityConfig.AlgorithmType.WEIGHT_CENTRALITY)),
        new NamedEntry("Algorithm: Graph Centrality",
            new CentralityConfig(CentralityConfig.AlgorithmType.GRAPH_CENTRALITY)),
        new NamedEntry("Algorithm: Node Edge Betweeness Centrality",
            new CentralityConfig(CentralityConfig.AlgorithmType.NODE_EDGE_BETWEENESS_CENTRALITY)),
        new NamedEntry("Algorithm: Closeness Centrality",
            new CentralityConfig(CentralityConfig.AlgorithmType.CLOSENESS_CENTRALITY)),
    };

    JComboBox<NamedEntry> comboBox = new JComboBox<>(algorithms);
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setToolTipText("Choose the algorithm");

    comboBox.addActionListener(e -> onAlgorithmChanged());

    return comboBox;
  }

  /**
   * Returns the currently chosen {@link AlgorithmConfiguration}.
   */
  private AlgorithmConfiguration getAlgorithmConfig() {
    NamedEntry entry = (NamedEntry) algorithmComboBox.getSelectedItem();
    AlgorithmConfiguration config = (AlgorithmConfiguration) entry.value;
    config.setDirected(useDirectedEdges());
    config.setUseUniformWeights(useUniformEdgeWeights());
    return config;
  }

  /**
   * Creates an {@link Action} that generates edge labels.
   * @see #generateEdgeLabels()
   */
  private Action createGenerateEdgeLabelsAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        generateEdgeLabels();
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Generate edge labels");
    action.putValue(Action.SMALL_ICON, createIcon("edgelabel.png"));
    return action;
  }

  /**
   * Generates and adds random labels for the edges in the graph.
   * Existing labels will be deleted before new ones are added.
   */
  private void generateEdgeLabels() {
    IGraph graph = graphComponent.getGraph();

    deleteCustomEdgeLabels();

    // remove labels from edges
    for (IEdge edge : graph.getEdges()) {
      // select a weight from 1 to 20
      double weight = useUniformEdgeWeights() ? 1 : Math.floor(Math.random() * 20 + 1);
      ILabel label = graph.addLabel(edge, Double.toString(weight), FreeEdgeLabelModel.INSTANCE.createDefaultParameter());
      label.setTag("weight");
    }

    runLayout(true, false, true);
  }

  /**
   * Creates an {@link Action} that deletes all custom edge labels.
   * @see #deleteCustomEdgeLabels()
   */
  private Action createDeleteEdgeLabelsAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        deleteCustomEdgeLabels();
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Delete edge labels");
    action.putValue(Action.SMALL_ICON, createIcon("delete2-16.png"));
    return action;
  }

  /**
   * Deletes all edge labels with weight or centrality tags.
   */
  private void deleteCustomEdgeLabels() {
    IGraph graph = graphComponent.getGraph();
    for (IEdge edge : graph.getEdges()) {
      for (ILabel label : edge.getLabels().toArray(ILabel.class)) {
        Object tag = label.getTag();
        if ("weight".equals(tag) || "centrality".equals(tag)) {
          graph.remove(label);
        }
      }
    }
  }

  /**
   * Resets the styles of the nodes and edges to the default style.
   */
  void resetStyles() {
    getAlgorithmConfig().resetGraph(graphComponent.getGraph());
  }

  /**
   * Creates a {@link JCheckBox} for choosing whether to use the same weight for each edge.
   */
  private JCheckBox createUniformEdgeWeightsCheckBox() {
    JCheckBox checkBox = new JCheckBox("Uniform Edge Weights", true);
    checkBox.setToolTipText("Choose whether to use the same weight for each edge");
    checkBox.addActionListener(e -> generateEdgeLabels());
    return checkBox;
  }

  /**
   * Returns whether or not to use uniform weights for all edges.
   */
  private boolean useUniformEdgeWeights() {
    return edgeWeightsCheckBox.isSelected();
  }

  /**
   * Creates a {@link JCheckBox} for choosing whether to use directed edges or not.
   */
  private JCheckBox createDirectionCheckBox() {
    JCheckBox checkBox = new JCheckBox("Directed", false);
    checkBox.setToolTipText("Choose whether to use directed edges or not");
    checkBox.addActionListener(e -> runLayout(true, false, true));
    return checkBox;
  }

  /**
   * Returns whether or not to take edge direction into account.
   */
  private boolean useDirectedEdges() {
    return directionCheckBox.isSelected();
  }

  /**
   * Creates an {@link javax.swing.Action} that runs the layout.
   */
  private Action createLayoutAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        runLayout(false, false, false);
      }
    };
    action.putValue(Action.NAME, "Layout");
    action.putValue(Action.SHORT_DESCRIPTION, "Layout the current graph");
    action.putValue(Action.SMALL_ICON, createIcon("layout-organic-16.png"));
    return action;
  }

  /**
   * Arranges the given graph.
   * @param incremental if {@code true}, the layout should run in incremental mode.
   * @param clearUndo if {@code true}, the undo engine should be cleared.
   * @param runAlgorithm if {@code true}, the algorithm should be applied.
   */
  private void runLayout(boolean incremental, boolean clearUndo, boolean runAlgorithm) {
    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setDeterministicModeEnabled(true);
    organicLayout.setNodeSizeConsiderationEnabled(true);
    ((ComponentLayout) organicLayout.getComponentLayout()).setStyle(
        ComponentArrangementStyles.NONE.or(ComponentArrangementStyles.MODIFIER_NO_OVERLAP));
    organicLayout.setScope(incremental ? Scope.MAINLY_SUBSET : Scope.ALL);
    organicLayout.setLabelingEnabled(false);

    OrganicLayoutData organicLayoutData = new OrganicLayoutData();
    organicLayoutData.setPreferredEdgeLengths(100d);
    organicLayoutData.setMinimumNodeDistances(10d);
    organicLayoutData.setAffectedNodes(incrementalNodesMapper);

    ILayoutAlgorithm layoutAlgorithm = organicLayout;
    LayoutData layoutData = organicLayoutData;
    AlgorithmConfiguration currentConfig = getAlgorithmConfig();
    if (currentConfig instanceof CentralityConfig) {
      CentralityConfig centrality = (CentralityConfig) currentConfig;
      layoutAlgorithm = centrality.configure(layoutAlgorithm);
      layoutData = centrality.configure(layoutData);
    }

    IGraph graph = graphComponent.getGraph();

    enableUI(false);
    graphComponent.morphLayout(layoutAlgorithm, Duration.ofMillis(500), layoutData, (source, args) -> {
      // apply graph algorithms after layout
      if (runAlgorithm) {
        applyAlgorithm();
      }
    });

    GenericLabeling genericLabeling = new GenericLabeling();
    genericLabeling.setEdgeLabelPlacementEnabled(true);
    genericLabeling.setNodeLabelPlacementEnabled(false);
    genericLabeling.setDeterministicModeEnabled(true);

    Mapper<ILabel, PreferredPlacementDescriptor> mapper = new Mapper<>();
    graph.getLabels().forEach(label -> {
      if (label.getOwner() instanceof IEdge) {
        PreferredPlacementDescriptor preferredPlacementDescriptor = new PreferredPlacementDescriptor();
        if ("centrality".equals(label.getTag())) {
          preferredPlacementDescriptor.setSideOfEdge(LabelPlacements.ON_EDGE);
        } else {
          preferredPlacementDescriptor.setSideOfEdge(LabelPlacements.RIGHT_OF_EDGE.or(LabelPlacements.LEFT_OF_EDGE));
          preferredPlacementDescriptor.setDistanceToEdge(5);
        }
        mapper.setValue(label, preferredPlacementDescriptor);
      }
    });
    graph.getMapperRegistry().addMapper(
        ILabel.class,
        PreferredPlacementDescriptor.class,
        LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY,
        mapper
    );

    graphComponent.morphLayout(genericLabeling, Duration.ofMillis(200), (source, args) -> {
      if (clearUndo) {
        graph.getUndoEngine().clear();
      }

      // clean up data provider
      graph.getMapperRegistry().removeMapper(OrganicLayout.AFFECTED_NODES_DPKEY);
      graph.getMapperRegistry().removeMapper(LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY);
      incrementalNodesMapper.clear();

      enableUI(true);
    });
  }

  @Override
  public void initialize() {
    graphComponent.setInputMode(createEditorMode());

    incrementalNodesMapper = new Mapper<>();
    incrementalNodesMapper.setDefaultValue(false);

    // initialize the graph and the defaults
    initializeGraph();
  }

  @Override
  public void onVisible() {
    sampleComboBox.setSelectedIndex(0);
  }

  @Override
  protected JFrame createFrame(String title) {
    JFrame frame = super.createFrame(title);
    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    return frame;
  }

  /**
   * Initializes the graph instance and sets default styles.
   */
  private void initializeGraph() {
    IGraph graph = graphComponent.getGraph();

    // enable undo support.
    graph.setUndoEngineEnabled(true);

    // use circular node visualizations by default
    ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
    nodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    nodeStyle.setPaint(Colors.LIGHT_GRAY);
    nodeStyle.setPen(Pen.getBlack());
    graph.getNodeDefaults().setStyle(nodeStyle);

    // change the default edge label model to an imlementation that works well
    // with generic labeling used in method runLayout below
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(FreeEdgeLabelModel.INSTANCE.createDefaultParameter());

    // change the default label text color to something less contrasting
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setTextPaint(Color.GRAY);
    graph.getEdgeDefaults().getLabelDefaults().setStyle(labelStyle);
  }

  /**
   * Creates the default input mode for the graph component, a
   * {@link GraphEditorInputMode} instance configured for snapping and
   * orthogonal edge editing.
   * @return a configured <code>GraphEditorInputMode</code> instance
   */
  private GraphEditorInputMode createEditorMode() {
    incrementalElements = new Mapper<>();
    incrementalElements.setDefaultValue(Boolean.FALSE);

    // configure interaction
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setShowHandleItems(GraphItemTypes.BEND.or(GraphItemTypes.EDGE).or(GraphItemTypes.LABEL).or(GraphItemTypes.PORT));

    // make it easier to select nodes
    // by default, edges have a higher hit test precedence than nodes
    // due to the fairly thick edges used by the demo, the default hit test
    // order would make selecting nodes with incident edges difficult  
    inputMode.setClickHitTestOrder(GraphItemTypes.NODE, GraphItemTypes.ALL);

    // when deleting nodes, mark neighbors for incremental layout
    // when deleting edges, mark source and target node for incremental layout
    inputMode.addDeletingSelectionListener((sender, args) -> {
      getAlgorithmConfig().setEdgeRemoved(true);
      ISelectionModel<IModelItem> selection = args.getSelection();
      for (IModelItem item : selection) {
        if (item instanceof INode) {
          INode node = (INode) item;
          graphComponent.getGraph().edgesAt(node, AdjacencyTypes.ALL).forEach(edge -> {
            if (!selection.isSelected(edge.opposite(node))) {
              incrementalNodesMapper.setValue((INode) edge.opposite(node), true);
            }
          });
        } else if (item instanceof IEdge) {
          IEdge edge = (IEdge) item;
          if (!selection.isSelected(edge.getSourceNode())) {
            incrementalNodesMapper.setValue(edge.getSourceNode(), true);
            incrementalElements.setValue(edge.getSourceNode(), true);
          }
          if (!selection.isSelected(edge.getTargetNode())) {
            incrementalNodesMapper.setValue(edge.getTargetNode(), true);
            incrementalElements.setValue(edge.getTargetNode(), true);
          }
        }
      }

      getAlgorithmConfig().setIncrementalElements(incrementalElements);
    });

    // after elements are deleted, arrange the graph with the update incremental
    // elements information
    inputMode.addDeletedSelectionListener((sender, args) -> {
      runLayout(true, false, true);
    });

    // mark source and target nodes of new edges for incremental layout
    inputMode.getCreateEdgeInputMode().addEdgeCreatedListener((sender, args) -> {
      IEdge edge = args.getItem();
      incrementalNodesMapper.setValue(edge.getSourceNode(), true);
      incrementalNodesMapper.setValue(edge.getTargetNode(), true);

      incrementalElements.setValue(edge.getSourceNode(), true);
      incrementalElements.setValue(edge.getTargetNode(), true);

      getAlgorithmConfig().setIncrementalElements(incrementalElements);

      runLayout(true, false, true);
    });

    // mark new nodes for incremental layout
    inputMode.addNodeCreatedListener((sender, args) -> {
      incrementalElements.setValue(args.getItem(), true);

      getAlgorithmConfig().setIncrementalElements(incrementalElements);

      applyAlgorithm();
    });

    // arrange the graph anew, if only some of its nodes are moved
    // as a side effect, the currently chosen analysis algorithm is run as well
    // and might yield different results due to changed edge lenghts
    inputMode.getMoveInputMode().addDragFinishedListener((sender, args) -> {
      int count = 0;
      for (IModelItem item : ((MoveInputMode) sender).getAffectedItems()) {
        if (item instanceof INode) {
          count++;
        }
      }
      if (count < graphComponent.getGraph().getNodes().size()) {
        runLayout(true, false, true);
      }
    });

    // run the currently chosen analysis algorithm whenever the text of labels
    // is changed to account for possibly changed edge weights
    inputMode.addLabelTextChangedListener((sender, args) -> {
      applyAlgorithm();
    });

    inputMode.addValidateLabelTextListener((sender, args) -> {
      // labels must contain only positive numbers
      args.setCanceling(!Pattern.matches(validationPattern, args.getNewText()));
    });

    // also we add a popup menu
    initializePopupMenu(inputMode);

    // bind "new" command to its default shortcut CTRL+N on Windows and Linux
    // and COMMAND+N on Mac OS
    inputMode.getKeyboardInputMode().addCommandBinding(ICommand.NEW,
        (command, parameter, source) -> {
          graphComponent.getGraph().clear();
          ICommand.invalidateRequerySuggested();
          return true;
        },
        (command, parameter, source) ->
            graphComponent.getGraph().getNodes().size() != 0 && enabledUI);

    return inputMode;
  }

  /**
   * Initializes the popup menu.
   * @param inputMode The input mode.
   */
  private void initializePopupMenu(GraphEditorInputMode inputMode) {
    inputMode.setPopupMenuItems(GraphItemTypes.NODE);
    inputMode.addPopulateItemPopupMenuListener((source, args) -> {
      if (args.getItem() instanceof INode) {
        INode node = (INode) args.getItem();
        JPopupMenu popupMenu = (JPopupMenu) args.getMenu();

        AlgorithmConfiguration currentConfig = getAlgorithmConfig();
        if (currentConfig != null) {
          currentConfig.populateContextMenu(popupMenu, node, graphComponent);
          if (popupMenu.getComponentCount() > 0) {
            args.setShowingMenuRequested(true);
            args.setHandled(true);
          }
        }
      }
    });
  }

  /**
   * Handles a selection change in the sample combo box.
   */
  private void onSampleChanged() {
    int index = sampleComboBox.getSelectedIndex();
    loadSample((String) sampleComboBox.getItemAt(index).value);
    applyAlgorithm(index);
  }

  /**
   * Applies the algorithm to the selected file and runs the layout.
   */
  private void applyAlgorithm(int sampleSelectedIndex) {
    resetStyles();

    AlgorithmConfiguration currentConfig = getAlgorithmConfig();
    if (currentConfig != null &&
        currentConfig.getIncrementalElements() != null) {
      incrementalElements.clear();
      currentConfig.setIncrementalElements(incrementalElements);
      currentConfig.setEdgeRemoved(false);
    }

    // run the layout if the layout combo box is already correct
    int algorithmSelectedIndex = algorithmComboBox.getSelectedIndex();
    if (algorithmSelectedIndex != sampleSelectedIndex) {
      // otherwise, change the selection but prevent calculating a new layout
      // at this point
      preventLayout = true;
      algorithmComboBox.setSelectedIndex(sampleSelectedIndex);
    }

    preventLayout = false;
    runLayout(false, true, true);
  }

  /**
   * Handles a selection change in the algorithm combo box.
   */
  private void onAlgorithmChanged() {
    directionCheckBox.setEnabled(getAlgorithmConfig().supportsDirectedEdges());
    edgeWeightsCheckBox.setEnabled(getAlgorithmConfig().supportsEdgeWeights());

    resetStyles();

    if (!preventLayout) {
      runLayout(false, false, true);
    }
  }

  /**
   * Runs the currently chosen analysis algorithm for the current graph.
   */
  private void applyAlgorithm() {
    // apply the algorithm
    getAlgorithmConfig().apply(graphComponent);
  }

  /**
   * Handles a selection change in the sample combo box.
   */
  private void loadSample(String graphName) {
    graphComponent.getGraph().clear();

    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/" + graphName + ".graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new GraphAnalysisDemo().start();
    });
  }


  /**
   * Name-value struct for combo box entries.
   */
  private static class NamedEntry {
    final String displayName;
    final Object value;

    NamedEntry(String displayName, Object value) {
      this.displayName = displayName;
      this.value = value;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }
}
