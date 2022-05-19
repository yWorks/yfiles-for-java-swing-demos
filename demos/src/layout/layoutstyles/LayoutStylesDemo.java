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
package layout.layoutstyles;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.CommandAction;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import com.yworks.yfiles.view.input.LabelSnapContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.WaitInputMode;
import layout.layoutstyles.configurations.BalloonLayoutConfig;
import layout.layoutstyles.configurations.BusEdgeRouterConfig;
import layout.layoutstyles.configurations.ChannelEdgeRouterConfig;
import layout.layoutstyles.configurations.CircularLayoutConfig;
import layout.layoutstyles.configurations.ClassicTreeLayoutConfig;
import layout.layoutstyles.configurations.ComponentLayoutConfig;
import layout.layoutstyles.configurations.GraphTransformerConfig;
import layout.layoutstyles.configurations.HierarchicLayoutConfig;
import layout.layoutstyles.configurations.LabelingConfig;
import layout.layoutstyles.configurations.LayoutConfiguration;
import layout.layoutstyles.configurations.OrganicEdgeRouterConfig;
import layout.layoutstyles.configurations.OrganicLayoutConfig;
import layout.layoutstyles.configurations.OrthogonalLayoutConfig;
import layout.layoutstyles.configurations.ParallelEdgeRouterConfig;
import layout.layoutstyles.configurations.PartialLayoutConfig;
import layout.layoutstyles.configurations.PolylineEdgeRouterConfig;
import layout.layoutstyles.configurations.RadialLayoutConfig;
import layout.layoutstyles.configurations.SeriesParallelLayoutConfig;
import layout.layoutstyles.configurations.TabularLayoutConfig;
import layout.layoutstyles.configurations.TreeLayoutConfig;
import toolkit.AbstractDemo;
import toolkit.DemoGroupNodeStyle;
import toolkit.DemoNodeStyle;
import toolkit.optionhandler.OptionEditor;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Play around with the various layout algorithms of yFiles, including hierarchic, organic, orthogonal, tree, circular and balloon styles.
 */
public class LayoutStylesDemo extends AbstractDemo {
  private static final String HIERARCHIC_LAYOUT_STYLE = "Hierarchic";

  private static final ICommand GENERATE_NODE_LABELS = ICommand.createCommand("Generate Node Labels");
  private static final ICommand GENERATE_EDGE_LABELS = ICommand.createCommand("Generate Edge Labels");
  private static final ICommand GENERATE_EDGE_DIRECTION = ICommand.createCommand("Generate Edge Direction");
  private static final ICommand GENERATE_EDGE_THICKNESS = ICommand.createCommand("Generate Edge Thickness");
  private static final ICommand REMOVE_LABELS = ICommand.createCommand("Remove Labels");
  private static final ICommand RESET_EDGE_DIRECTION = ICommand.createCommand("Reset Edge Direction");
  private static final ICommand RESET_EDGE_THICKNESS = ICommand.createCommand("Reset Edge Thickness");
  private static final ICommand PREVIOUS_GRAPH = ICommand.createCommand("Previous Graph");
  private static final ICommand NEXT_GRAPH = ICommand.createCommand("Next Graph");
  private static final ICommand APPLY_SETTINGS = ICommand.createCommand("Apply");
  private static final ICommand RESET_SETTINGS = ICommand.createCommand("Reset");


  /**
   * Stores all available layout algorithms and maps each name to the corresponding configuration.
   */
  private Map<String, LayoutConfiguration> availableLayouts;

  private JComboBox<String> layoutComboBox;
  private JComboBox<String> graphChooserBox;

  private JPanel editorPanel;
  private OptionEditor builder;

  private boolean inLayout;
  private boolean inLoadSample;

  private WaitInputMode waitInputMode;

  private Random rnd;

  /**
   * Initializes a new instance of <code>LayoutStylesDemo</code>.
   */
  public LayoutStylesDemo() {
    layoutComboBox = new JComboBox<>();
    rnd = new Random();
  }

  @Override
  public void initialize() {
    // initialize the graph instance and default styles.
    initializeGraph();

    // initialize the input mode
    initializeInputModes();

    initializeLayoutAlgorithms();
  }

  /**
   * Initializes the graph instance and sets the default styles.
   */
  private void initializeGraph() {
    // enable undo support
    graphComponent.getGraph().setUndoEngineEnabled(true);

    // set the default style for normal nodes
    graphComponent.getGraph().getNodeDefaults().setStyle(new DemoNodeStyle());

    // set the default style for group nodes
    INodeDefaults groupNodeDefaults = graphComponent.getGraph().getGroupNodeDefaults();
    groupNodeDefaults.setStyle(new DemoGroupNodeStyle());
    // use a custom style and layout parameter for group node labels
    InteriorLabelModel interiorLabelModel = new InteriorLabelModel();
    interiorLabelModel.setInsets(new InsetsD(2));
    groupNodeDefaults.getLabelDefaults().setLayoutParameter(interiorLabelModel.createParameter(InteriorLabelModel.Position.NORTH_WEST));

    DefaultLabelStyle groupNodeLabelStyle = new DefaultLabelStyle();
    groupNodeLabelStyle.setFont(new Font("Dialog", Font.BOLD, 12));
    groupNodeLabelStyle.setTextPaint(Colors.WHITE);
    groupNodeDefaults.getLabelDefaults().setStyle(groupNodeLabelStyle);

    // set the default style for edges
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    graphComponent.getGraph().getEdgeDefaults().setStyle(edgeStyle);
  }

  /**
   * Creates a {@link GraphEditorInputMode} and registers it as
   * the {@link com.yworks.yfiles.view.CanvasComponent#setInputMode(IInputMode)}.
   */
  public void initializeInputModes() {
    // allow file operations such as open/save
    graphComponent.setFileIOEnabled(true);

    GraphEditorInputMode mode = new GraphEditorInputMode();
    waitInputMode = mode.getWaitInputMode();

    // initialize snapping
    GraphSnapContext snapContext = new GraphSnapContext();
    snapContext.setEnabled(false);
    mode.setSnapContext(snapContext);
    mode.setLabelSnapContext(new LabelSnapContext());
    OrthogonalEdgeEditingContext orthogonalEdgeEditingContext = new OrthogonalEdgeEditingContext();
    orthogonalEdgeEditingContext.setEnabled(false);
    mode.setOrthogonalEdgeEditingContext(orthogonalEdgeEditingContext);

    // enable grouping operations such as grouping selected nodes moving nodes into group nodes
    mode.setGroupingOperationsAllowed(true);

    KeyboardInputMode kim = mode.getKeyboardInputMode();
    kim.addCommandBinding(ICommand.NEW, this::executeNewCommand, this::canExecuteNewCommand);
    kim.addCommandBinding(APPLY_SETTINGS, this::executeApplyLayoutCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(RESET_SETTINGS, this::executeResetLayoutCommand, this::canExecuteChangeCommand);
    // we use the same handler method for both commands and use the parameter to distinguish both
    kim.addCommandBinding(PREVIOUS_GRAPH, this::executeShowGraphCommand, this::canExecuteShowGraphCommand);
    kim.addCommandBinding(NEXT_GRAPH, this::executeShowGraphCommand, this::canExecuteShowGraphCommand);
    kim.addCommandBinding(GENERATE_NODE_LABELS, this::executeGenerateLabelsCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(GENERATE_EDGE_LABELS, this::executeGenerateLabelsCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(REMOVE_LABELS, this::executeRemoveLabelsCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(GENERATE_EDGE_THICKNESS, this::executeEdgeStyleCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(RESET_EDGE_THICKNESS, this::executeEdgeStyleCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(GENERATE_EDGE_DIRECTION, this::executeEdgeStyleCommand, this::canExecuteChangeCommand);
    kim.addCommandBinding(RESET_EDGE_DIRECTION, this::executeEdgeStyleCommand, this::canExecuteChangeCommand);

    graphComponent.setInputMode(mode);
  }

  /**
   * Fills the layoutComboBox and creates the corresponding LayoutConfigurations
   */
  private void initializeLayoutAlgorithms() {
    availableLayouts = new HashMap<>();

    String[] layoutNames = new String[] {
            "Hierarchic",
            "Organic",
            "Orthogonal",
            "Circular",
            "Tree",
            "Classic Tree",
            "Balloon",
            "Radial",
            "Series-Parallel",
            "Tabular",
            "Edge Router",
            "Channel Router",
            "Bus Router",
            "Organic Router",
            "Parallel Router",
            "Labeling",
            "Components",
            "Partial",
            "Graph Transform"
    };
    DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>(layoutNames);
    layoutComboBox.setModel(comboBoxModel);
    layoutComboBox.setMaximumSize(layoutComboBox.getPreferredSize());
    layoutComboBox.addActionListener(e -> onLayoutChanged());

    HierarchicLayoutConfig hierarchicLayoutConfig = new HierarchicLayoutConfig();
    hierarchicLayoutConfig.enableSubComponents();
    availableLayouts.put(HIERARCHIC_LAYOUT_STYLE, hierarchicLayoutConfig);
    OrganicLayoutConfig organicLayoutConfig = new OrganicLayoutConfig();
    organicLayoutConfig.enableSubstructures();
    availableLayouts.put("Organic", organicLayoutConfig);
    OrthogonalLayoutConfig orthogonalLayoutConfig = new OrthogonalLayoutConfig();
    orthogonalLayoutConfig.enableSubstructures();
    availableLayouts.put("Orthogonal", orthogonalLayoutConfig);
    availableLayouts.put("Circular", new CircularLayoutConfig());
    availableLayouts.put("Tree", new TreeLayoutConfig());
    availableLayouts.put("Classic Tree", new ClassicTreeLayoutConfig());
    availableLayouts.put("Balloon", new BalloonLayoutConfig());
    availableLayouts.put("Radial", new RadialLayoutConfig());
    availableLayouts.put("Series-Parallel", new SeriesParallelLayoutConfig());
    availableLayouts.put("Tabular", new TabularLayoutConfig());
    availableLayouts.put("Edge Router", new PolylineEdgeRouterConfig());
    availableLayouts.put("Channel Router", new ChannelEdgeRouterConfig());
    availableLayouts.put("Bus Router", new BusEdgeRouterConfig());
    availableLayouts.put("Organic Router", new OrganicEdgeRouterConfig());
    availableLayouts.put("Parallel Router", new ParallelEdgeRouterConfig());
    availableLayouts.put("Labeling", new LabelingConfig());
    availableLayouts.put("Components", new ComponentLayoutConfig());
    availableLayouts.put("Partial", new PartialLayoutConfig());
    availableLayouts.put("Graph Transform", new GraphTransformerConfig());

    // load hierarchic sample graph and apply the hierarchic layout
    if (!"Hierarchic".equals(graphChooserBox.getSelectedItem())) {
      graphChooserBox.setSelectedItem("Hierarchic");
    } else {
      onSampleGraphChanged();
    }
  }

  /**
   * Arranges the displayed graph using the layout algorithm corresponding to
   * the given key.
   */
  private void applyLayoutForKey(String sampleKey) {
    // center the initial position of the animation
    ICommand.FIT_GRAPH_BOUNDS.execute(null, graphComponent);

    // get the actual key, as there are samples sharing the layout config (e.g. Organic with Substructures and Organic)
    String actualKey = getLayoutKey(sampleKey);

    // get the layout algorithm and use "Hierarchic" if the key is unknown (shouldn't happen in this demo)
    actualKey = availableLayouts != null && availableLayouts.containsKey(actualKey) ? actualKey : "Hierarchic";

    if (actualKey.equals(layoutComboBox.getSelectedItem())) {
      // run the layout if the layout combo box is already correct
      onLayoutChanged();
    } else {
      // otherwise, change the selection and indirectly trigger the layout
      layoutComboBox.setSelectedItem(actualKey);
    }
    applyLayout(true);
  }

  private String getLayoutKey(String sampleKey) {
    //for some special samples, we need to use the correct layout key, because the layout configurations are shared
    if (sampleKey.startsWith("Organic")) {
      return "Organic";
    } else if (sampleKey.startsWith("Hierarchic")) {
      return HIERARCHIC_LAYOUT_STYLE;
    } else if (sampleKey.startsWith("Orthogonal")) {
      return "Orthogonal";
    } else if (sampleKey.startsWith("Edge Router")) {
      return "Edge Router";
    }
    //... for other samples the layout key corresponds to the sample graph key
    return sampleKey;
  }

  /**
   * Arranges the displayed graph using the currently selected layout algorithm.
   *
   * @param clearUndo Specifies whether the undo queue should be cleared after the layout calculation.
   *                  This is set to <code>true</code> if this method is called directly after loading a new sample graph.
   */
  private void applyLayout(boolean clearUndo) {
    LayoutConfiguration config = (LayoutConfiguration) builder.getConfiguration();

    if (config == null || inLayout) {
      return;
    }

    // prevent starting another layout calculation
    inLayout = true;
    setUIEnabled(false);

    // calculate the layout and animate the result in one second
    config.apply(graphComponent, () ->
    {
      releaseLocks();
      setUIEnabled(true);
      if (clearUndo) {
        graphComponent.getGraph().getUndoEngine().clear();
      }
      // the commands CanExecute state might have changed - suggest a re-query. mainly to update the enabled status of the previous / next buttons.
      ICommand.invalidateRequerySuggested();
    });
  }

  private void onLayoutChanged() {
    String key = (String) layoutComboBox.getSelectedItem();
    if (key != null && availableLayouts.containsKey(key)) {
      LayoutConfiguration newLayoutConfig = availableLayouts.get(key);
      if (builder != null && editorPanel != null) {
        builder.setConfiguration(newLayoutConfig);
        editorPanel.removeAll();
        editorPanel.add(builder.buildEditor());
        editorPanel.revalidate();
      }
    }
  }

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("New", "new-document-16.png", ICommand.NEW, null, graphComponent));
    toolBar.add(createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, graphComponent));
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE_AS, null, graphComponent));
    toolBar.addSeparator();
    JComponent graphSampleComboBox = createGraphSampleComboBox();
    Action previousButton = createCommandButtonAction("Show previous graph", "arrow-left-16.png", PREVIOUS_GRAPH, "Previous", graphComponent);
    Action nextButton = createCommandButtonAction("Show next graph", "arrow-right-16.png", NEXT_GRAPH, "Next", graphComponent);
    toolBar.add(new JLabel("Sample"));
    toolBar.add(previousButton);
    toolBar.add(graphSampleComboBox);
    toolBar.add(nextButton);

    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom 1:1", "zoom-original2-16.png", ICommand.ZOOM, 1, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Fit the graph content", "fit2-16.png", ICommand.FIT_CONTENT, null, graphComponent));

    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Cut", "cut-16.png", ICommand.CUT, null, graphComponent));
    toolBar.add(createCommandButtonAction("Copy", "copy-16.png", ICommand.COPY, null, graphComponent));
    toolBar.add(createCommandButtonAction("Paste", "paste-16.png", ICommand.PASTE, null, graphComponent));
    toolBar.add(createCommandButtonAction("Delete", "delete3-16.png", ICommand.DELETE, null, graphComponent));

    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png", ICommand.REDO, null, graphComponent));

    toolBar.addSeparator();
    toolBar.add(new JToggleButton(createToggleSnapAction()));
    toolBar.add(new JToggleButton(createToggleOrthogonalEdgeCreationAction()));

    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Group selected elements", "group-16.png", ICommand.GROUP_SELECTION, null, graphComponent));
    toolBar.add(createCommandButtonAction("Ungroup selected elements", "ungroup-16.png", ICommand.UNGROUP_SELECTION, null, graphComponent));


    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction(GENERATE_NODE_LABELS.getName(), "nodelabel.png", GENERATE_NODE_LABELS, "Node", graphComponent));
    toolBar.add(createCommandButtonAction(GENERATE_EDGE_LABELS.getName(), "edgelabel.png", GENERATE_EDGE_LABELS, "Edge", graphComponent));
    toolBar.add(createCommandButtonAction(REMOVE_LABELS.getName(), "delete2-16.png", REMOVE_LABELS, null, graphComponent));

    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction(GENERATE_EDGE_THICKNESS.getName(), "edge-thickness-16.png", GENERATE_EDGE_THICKNESS, null, graphComponent));
    toolBar.add(createCommandButtonAction(RESET_EDGE_THICKNESS.getName(), "delete2-16.png", RESET_EDGE_THICKNESS, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction(GENERATE_EDGE_DIRECTION.getName(), "edge-direction-16.png", GENERATE_EDGE_DIRECTION, null, graphComponent));
    toolBar.add(createCommandButtonAction(RESET_EDGE_DIRECTION.getName(), "delete2-16.png", RESET_EDGE_DIRECTION, null, graphComponent));

    graphChooserBox.addActionListener(e -> onSampleGraphChanged());
  }

  @Override
  protected void configure(JRootPane rootPane) {
    Container contentPane = rootPane.getContentPane();

    JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            newOptionPane(),
            graphComponent);

    contentPane.add(split, BorderLayout.CENTER);

    JToolBar toolBar = createToolBar();
    if (toolBar != null) {
      configureToolBar(toolBar);
      contentPane.add(toolBar, BorderLayout.NORTH);
    }

    JComponent helpPane = createHelpPane();
    if (helpPane != null) {
      contentPane.add(helpPane, BorderLayout.EAST);
    }
  }

  private JComponent newOptionPane() {
    int margin = 6;

    JPanel optionPane = new JPanel(new BorderLayout());
    optionPane.setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));

    builder = new OptionEditor();

    JPanel layoutComboPanel = new JPanel(new BorderLayout(0, 5));
    JLabel layoutStyleLabel = new JLabel("Layout Style");
    Font labelFont = layoutStyleLabel.getFont();
    layoutStyleLabel.setFont(labelFont.deriveFont(Font.PLAIN, labelFont.getSize() + 2f));
    layoutComboPanel.add(layoutStyleLabel, BorderLayout.NORTH);
    JPanel innerLayoutComboPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    innerLayoutComboPanel.add(layoutComboBox);
    layoutComboPanel.add(innerLayoutComboPanel, BorderLayout.CENTER);
    layoutComboPanel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.SOUTH);

    optionPane.add(layoutComboPanel, BorderLayout.NORTH);
    editorPanel = new JPanel(new GridLayout(1, 1));
    JScrollPane jsp = new JScrollPane(editorPanel);
    jsp.setBorder(BorderFactory.createEmptyBorder());
    jsp.setPreferredSize(new Dimension(340, 250));
    configure(jsp.getHorizontalScrollBar());
    configure(jsp.getVerticalScrollBar());
    optionPane.add(jsp, BorderLayout.CENTER);


    JPanel buttonPane = new JPanel(new GridLayout(1, 2, margin, margin));
    buttonPane.add(new JButton(new CommandAction(APPLY_SETTINGS, null, graphComponent)));
    buttonPane.add(new JButton(new CommandAction(RESET_SETTINGS, null, graphComponent)));

    JPanel intermediatePane = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    intermediatePane.setBorder(BorderFactory.createEmptyBorder(margin, 0, 0, 0));
    intermediatePane.add(buttonPane);

    JPanel controlPane = new JPanel(new BorderLayout());
    controlPane.add(new JSeparator(), BorderLayout.NORTH);
    controlPane.add(intermediatePane, BorderLayout.CENTER);

    optionPane.add(controlPane, BorderLayout.SOUTH);
    return optionPane;
  }

  /**
   * Configures the given scoll bar for faster (mouse wheel) scrolling.
   */
  private void configure(JScrollBar jsb) {
    jsb.setUnitIncrement(jsb.getUnitIncrement() * 10);
  }

  private void setUIEnabled(boolean enabled) {
    layoutComboBox.setEnabled(enabled);
    graphChooserBox.setEnabled(enabled);
    graphComponent.setFileIOEnabled(enabled);
  }


  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      (new LayoutStylesDemo()).start();
    });
  }

  /**
   * Helper that determines whether the {@link ICommand#NEW} can be executed.
   */
  private boolean canExecuteNewCommand(ICommand command, Object parameter, Object source) {
    IGraph graph = graphComponent.getGraph();
    // if the graph has nodes in it, it can be cleared.
    return graph != null && !(graph.getNodes().size() == 0) && !inLoadSample && !inLayout && !waitInputMode.isWaiting();
  }


  /**
   * Handler for the {@link ICommand#NEW}
   */
  private boolean executeNewCommand(ICommand command, Object parameter, Object source) {
    graphComponent.getGraph().clear();

    // update the can-execute-states of the commands since this is not
    // triggered by clearing the graph programmatically
    ICommand.invalidateRequerySuggested();

    return true;
  }

  /**
   * Handler for the ShowGraphCommand. Changes the current graph to the previous or next graph.
   */
  private boolean executeShowGraphCommand(ICommand command, Object parameter, Object source) {
    if (parameter instanceof String) {
      switch ((String) parameter){
        case "Previous":
          graphChooserBox.setSelectedIndex(graphChooserBox.getSelectedIndex()-1);
          return true;
        case "Next":
          graphChooserBox.setSelectedIndex(graphChooserBox.getSelectedIndex()+1);
          return true;
      }
    }
    return false;
  }

  /**
   * Determines if the ShowGraphCommand can be executed which depends on the current index of the graphChooserBox.
   */
  private boolean canExecuteShowGraphCommand(ICommand command, Object parameter, Object source) {
    if (parameter instanceof String) {
      JComboBox<String> jcb = this.graphChooserBox;
      switch ((String) parameter){
        case "Previous":
          return jcb.isEnabled() && jcb.getSelectedIndex() > 0;
        case "Next":
          return jcb.isEnabled() && jcb.getSelectedIndex() < jcb.getItemCount() - 1;
      }
    }
    return false;
  }

  /**
   * Generates node or edge labels when the appropriate commands are triggered.
   */
  private boolean executeGenerateLabelsCommand(ICommand command, Object parameter, Object source) {
    if (parameter instanceof String) {
      switch ((String) parameter) {
        case "Edge":
          onGenerateItemLabels(graphComponent.getGraph().getEdges());
          return true;
        case "Node":
          onGenerateItemLabels(graphComponent.getGraph().getNodes());
          return true;
      }
    }
    return false;
  }

  /**
   * Removes all node and edge labels when the appropriate command is triggered.
   */
  private boolean executeRemoveLabelsCommand(ICommand command, Object parameter, Object source) {
    if (source instanceof GraphComponent) {
      onRemoveItemLabels(((GraphComponent) source).getGraph());
      return true;
    } else {
      return false;
    }
  }

  /**
   * Adjusts edge styles when the commands for generating edge direction or
   * thickness are triggered.
   */
  private boolean executeEdgeStyleCommand( ICommand command, Object parameter, Object source) {
    if (source instanceof GraphComponent) {
      if (GENERATE_EDGE_THICKNESS == command) {
        onGenerateEdgeThicknesses(((GraphComponent) source).getGraph());
        return true;
      } else if (RESET_EDGE_THICKNESS == command) {
        onResetEdgeThicknesses(((GraphComponent) source).getGraph());
        return true;
      } else if (GENERATE_EDGE_DIRECTION == command) {
        onGenerateEdgeDirections(((GraphComponent) source).getGraph());
        return true;
      } else if (RESET_EDGE_DIRECTION == command) {
        onResetEdgeDirections(((GraphComponent) source).getGraph());
        return true;
      }
    }
    return false;
  }

  private boolean executeApplyLayoutCommand(ICommand command, Object parameter, Object source) {
    applyLayout(false);
    return true;
  }

  private boolean executeResetLayoutCommand(ICommand command, Object parameter, Object source) {
    builder.resetEditor((JComponent) editorPanel.getComponent(0));
    return true;
  }

  /**
   * Determines if commands that change the displayed graph may be executed.
   * These commands include generating node or edge labels as well as
   * starting a layout calculation.
   */
  private boolean canExecuteChangeCommand(ICommand command, Object parameter, Object source) {
    return !inLoadSample && !inLayout && (waitInputMode == null || !waitInputMode.isWaiting());
  }

  /**
   * Creates the JComboBox where the various graphs are selectable.
   */
  private JComponent createGraphSampleComboBox() {
    graphChooserBox = new JComboBox<>(new String[]{
        "Hierarchic",
        "Hierarchic Groups",
        "Organic",
        "Orthogonal",
        "Circular",
        "Tree",
        "Classic Tree",
        "Balloon",
        "Radial",
        "Series-Parallel",
        "Edge Router",
        "Bus Router",
        "Labeling",
        "Components",
        "Tabular",
        "Organic with Substructures",
        "Hierarchic with Substructures",
        "Orthogonal with Substructures",
        "Hierarchic with Buses",
        "Edge Router with Buses"
    });
    graphChooserBox.setMaximumSize(graphChooserBox.getPreferredSize());
    return graphChooserBox;
  }

  /**
   * Reads the currently selected GraphML from the graphChooserBox
   */
  private void onSampleGraphChanged() {
    if (inLayout || inLoadSample) {
      return;
    }
    String key = (String) graphChooserBox.getSelectedItem();
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
    String fileName = "resources/" + key.toLowerCase();
    fileName = fileName.replace("-", "");
    fileName = fileName.replace(" ", "") + ".graphml";

    try {
      if ("Hierarchic with Buses".equals(key)) {
        //for this specific hierarchic layout sample we make sure to enable the bus structure feature
        final HierarchicLayoutConfig hlc = (HierarchicLayoutConfig) availableLayouts.get("Hierarchic");
        hlc.enableAutomaticBusRouting();
      } else if ("Edge Router with Buses".equals(key)) {
        final PolylineEdgeRouterConfig edgeRouterConfig = (PolylineEdgeRouterConfig) availableLayouts.get("Edge Router");
        edgeRouterConfig.setBusRoutingItem(PolylineEdgeRouterConfig.EnumBusRouting.BY_COLOR);
      } else if ("Edge Router".equals(key)) {
        final PolylineEdgeRouterConfig edgeRouterConfig = (PolylineEdgeRouterConfig) availableLayouts.get("Edge Router");
        edgeRouterConfig.setBusRoutingItem(PolylineEdgeRouterConfig.EnumBusRouting.NONE);
      }

      // load the sample graph and start the layout algorithm
      graphComponent.importFromGraphML(getClass().getResource(fileName));
      applyLayoutForKey(key);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void releaseLocks() {
    inLoadSample = false;
    inLayout = false;
  }

  // region Label generation

  /**
   * Generates and adds labels for a random subset of the given graph elements.
   * <p>
   * Existing labels will be deleted before adding the new labels.
   * </p>
   * @param items The collection of items the labels are generated for.
   */
  private <T extends ILabelOwner> void onGenerateItemLabels(IListEnumerable<T> items)  {
    int wordCountMin = 1;
    int wordCountMax = 3;
    double labelPercMin = 0.2;
    double labelPercMax = 0.7;
    int labelCount = (int) Math.floor(items.size() * (rnd.nextDouble() * (labelPercMax - labelPercMin) + labelPercMin));
    ArrayList<ILabelOwner> labelOwners = new ArrayList<>(items.size());
    for (T item : items) {
      labelOwners.add(item);
    }

    IGraph graph = graphComponent.getGraph();

    //remove all existing item labels
    items.stream()
         // gather all labels of the given items
         .flatMap(item -> item.getLabels().stream())
         // copy them into a list to avoid concurrent modification
         .collect(Collectors.toList())
         // remove them from the graph
         .forEach(graph::remove);

    //add random item labels
    String[] loremList = getLoremIpsum();
    for (int i = 0; i < labelCount; i++) {
      String label = "";
      int wordCount = rnd.nextInt(wordCountMax - wordCountMin + 1) + wordCountMin;
      for(int j = 0; j < wordCount; j++) {
        int k = rnd.nextInt(loremList.length);
        label += (j == 0) ? "" : " ";
        label = label + loremList[k];
      }
      int itemIdx = rnd.nextInt(labelOwners.size());
      ILabelOwner item = labelOwners.remove(itemIdx);
      graph.addLabel(item, label);
    }
  }

  /**
   * Removes all node and edge labels in the given graph.
   */
  private void onRemoveItemLabels( IGraph graph ) {
    ArrayList<ILabel> labels = new ArrayList<>();
    for (ILabel nl : graph.getNodeLabels()) {
      labels.add(nl);
    }
    for (ILabel el : graph.getEdgeLabels()) {
      labels.add(el);
    }
    for (ILabel l : labels) {
      graph.remove(l);
    }
  }

  /**
   * Sets a random thickness in the range of <code>1.0</code> to
   * <code>5.0</code> for all edges.
   */
  private void onGenerateEdgeThicknesses( IGraph graph ) {
    PolylineEdgeStyle defaultStyle = (PolylineEdgeStyle) graph.getEdgeDefaults().getStyle();
    Pen defaultPen = defaultStyle.getPen();
    for (IEdge edge : graph.getEdges()) {
      IEdgeStyle oldStyle = edge.getStyle();

      Pen pen = new Pen(defaultPen.getPaint());
      pen.setThickness(rnd.nextDouble() * 4 + 1);

      PolylineEdgeStyle newStyle = defaultStyle.clone();
      newStyle.setPen(pen);
      // keep the target arrow in case the edge direction has been changed as well
      if (oldStyle instanceof PolylineEdgeStyle) {
        newStyle.setTargetArrow(((PolylineEdgeStyle) oldStyle).getTargetArrow());
      }

      graph.setStyle(edge, newStyle);
    }
  }

  /**
   * Resets the thickness of all edges to the previously specified default thickness.
   * @see #initializeGraph()
   */
  private void onResetEdgeThicknesses( IGraph graph ) {
    PolylineEdgeStyle defaultStyle = (PolylineEdgeStyle) graph.getEdgeDefaults().getStyle();
    for (IEdge edge : graph.getEdges()) {
      IEdgeStyle oldStyle = edge.getStyle();

      PolylineEdgeStyle newStyle = defaultStyle.clone();
      if (oldStyle instanceof PolylineEdgeStyle) {
        newStyle.setTargetArrow(((PolylineEdgeStyle) oldStyle).getTargetArrow());
      }

      graph.setStyle(edge, newStyle);
    }
  }

  /**
   * Removes the target arrow for random edges.
   */
  private void onGenerateEdgeDirections( IGraph graph ) {
    PolylineEdgeStyle defaultStyle = (PolylineEdgeStyle) graph.getEdgeDefaults().getStyle();
    for (IEdge edge : graph.getEdges()) {
      IEdgeStyle oldStyle = edge.getStyle();

      PolylineEdgeStyle newStyle = defaultStyle.clone();
      if (rnd.nextDouble() >= 0.5) {
        newStyle.setTargetArrow(IArrow.DEFAULT);
      } else {
        newStyle.setTargetArrow(IArrow.NONE);
      }
      // keep the pen in case the edge thickness has been changed as well
      if (oldStyle instanceof PolylineEdgeStyle) {
        newStyle.setPen(((PolylineEdgeStyle) oldStyle).getPen());
      }

      graph.setStyle(edge, newStyle);
    }
  }

  /**
   * Resets the target arrows of all edges to the previously specified default
   * target arrow.
   * @see #initializeGraph()
   */
  private void onResetEdgeDirections( IGraph graph ) {
    PolylineEdgeStyle defaultStyle = (PolylineEdgeStyle) graph.getEdgeDefaults().getStyle();
    for (IEdge edge : graph.getEdges()) {
      IEdgeStyle oldStyle = edge.getStyle();

      PolylineEdgeStyle newStyle = defaultStyle.clone();
      if (oldStyle instanceof PolylineEdgeStyle) {
        newStyle.setPen(((PolylineEdgeStyle) oldStyle).getPen());
      }

      graph.setStyle(edge, newStyle);
    }
  }

  private static String[] getLoremIpsum() {
    return new String[] {
            "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit", "donec", "felis", "erat"
            , "malesuada", "quis", "ipsum", "et", "condimentum",
            "ultrices", "orci", "nullam", "interdum", "vestibulum", "eros", "sed", "porta", "donec", "ac",
            "eleifend", "dolor", "at", "dictum", "ipsum", "pellentesque", "vel", "suscipit", "mi", "nullam",
            "aliquam", "turpis", "et", "dolor", "porttitor", "varius", "nullam", "vel", "arcu", "rutrum", "iaculis"
            , "est", "sit", "amet", "rhoncus", "turpis", "vestibulum", "lacinia", "sollicitudin",
            "urna", "nec", "vestibulum", "nulla", "id", "lacinia", "metus", "etiam", "ac", "felis", "rutrum",
            "sollicitudin", "erat", "vitae", "egestas", "tortor", "curabitur", "quis", "libero", "aliquet",
            "mattis", "mauris", "nec", "tempus", "nibh", "in", "at", "lectus", "luctus", "mattis", "urna",
            "pretium", "eleifend", "lacus", "sed", "interdum", "sapien", "nec", "justo", "vestibulum", "non",
            "scelerisque",
            "nibh", "sollicitudin", "interdum", "et", "malesuada", "fames", "ac", "ante", "ipsum", "primis", "in",
            "faucibus", "vivamus", "congue", "tristique", "magna", "quis", "elementum", "phasellus", "sit", "amet",
            "tristique", "massa", "vestibulum", "eu", "leo", "vitae", "quam", "dictum", "venenatis", "eu", "id",
            "nibh", "donec", "eget", "eleifend", "felis", "nulla", "ac", "suscipit", "ante", "et", "sollicitudin",
            "dui", "mauris",
            "in", "pulvinar", "tortor", "vestibulum", "pulvinar", "arcu", "vel", "tellus", "maximus", "blandit",
            "morbi", "sed", "sem", "vehicula", "fermentum", "nisi", "eu", "fringilla", "metus", "duis", "ut", "quam",
            "eget",
            "odio", "hendrerit", "finibus", "ut", "a", "lectus", "cras", "ullamcorper", "turpis", "in", "purus",
            "facilisis", "vestibulum", "donec", "maximus", "ac", "tortor", "tempus", "egestas", "aenean", "est", "diam",
            "dictum", "et", "sodales", "vel", "efficitur", "ac", "libero", "vivamus", "vehicula", "ligula", "eu",
            "diam", "auctor", "at", "dapibus", "nulla", "pellentesque", "morbi", "et", "dapibus", "dolor", "quis",
            "auctor",
            "turpis", "nunc", "sed", "pretium", "diam", "quisque", "non", "massa", "consectetur", "tempor", "augue"
            , "vel", "volutpat", "ex", "vivamus", "vestibulum", "dolor", "risus", "quis", "mollis", "urna", "fermentum",
            "sed",
            "sed", "porttitor", "venenatis", "volutpat", "nulla", "facilisi", "donec", "aliquam", "mi", "vitae",
            "ligula", "dictum", "ornare", "suspendisse", "finibus", "ligula", "vitae", "congue", "iaculis", "donec",
            "vestibulum", "erat", "vel", "tortor", "iaculis", "tempor", "vivamus", "et", "purus", "eu", "ipsum",
            "rhoncus", "pretium", "sit", "amet", "nec", "nisl", "nunc", "molestie", "consectetur", "rhoncus", "duis",
            "ex",
            "nunc", "interdum", "at", "molestie", "quis", "blandit", "quis", "diam", "nunc", "imperdiet", "lorem",
            "vel", "scelerisque", "facilisis", "eros", "massa", "auctor", "nisl", "vitae", "efficitur", "leo", "diam",
            "vel",
            "felis", "aliquam", "tincidunt", "dapibus", "arcu", "in", "pulvinar", "metus", "tincidunt", "et",
            "etiam", "turpis", "ligula", "sodales", "a", "eros", "vel", "fermentum", "imperdiet", "purus", "fusce",
            "mollis",
            "enim", "sed", "volutpat", "blandit", "arcu", "orci", "iaculis", "est", "non", "iaculis", "lorem",
            "sapien", "sit", "amet", "est", "morbi", "ut", "porttitor", "elit", "aenean", "ac", "sodales", "lectus",
            "morbi", "ut",
            "bibendum", "arcu", "maecenas", "tincidunt", "erat", "vel", "maximus", "pellentesque", "ut", "placerat",
            "quam", "sem", "a", "auctor", "ligula", "imperdiet", "quis", "pellentesque", "gravida", "consectetur",
            "urna", "suspendisse", "vitae", "nisl", "et", "ante", "ornare", "vulputate", "sed", "a", "est", "lorem",
            "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit", "sed", "eu", "facilisis", "lectus",
            "nullam",
            "iaculis", "dignissim", "eros", "eget", "tincidunt", "metus", "viverra", "at", "donec", "nec", "justo",
            "vitae", "risus", "eleifend", "imperdiet", "eget", "ut", "ante", "ut", "arcu", "ex", "convallis", "in",
            "lobortis",
            "at", "mattis", "sed", "velit", "ut", "viverra", "ultricies", "lacus", "suscipit", "feugiat", "eros",
            "luctus", "et", "vestibulum", "et", "aliquet", "mauris", "quisque", "convallis", "purus", "posuere",
            "aliquam",
            "nulla", "sit", "amet", "posuere", "orci", "nullam", "sed", "iaculis", "mauris", "ut", "volutpat",
            "est", "suspendisse", "in", "vestibulum", "felis", "nullam", "gravida", "nulla", "at", "varius",
            "fringilla", "ipsum",
            "ipsum", "finibus", "lectus", "nec", "vestibulum", "lorem", "arcu", "ut", "magna", "aliquam", "aliquam"
            , "erat", "erat", "ac", "euismod", "orci", "iaculis", "blandit", "morbi", "tincidunt", "posuere", "mi",
            "non",
            "eleifend", "vivamus", "accumsan", "dolor", "magna", "in", "cursus", "eros", "malesuada", "eu", "sed",
            "auctor", "consectetur", "tempus", "maecenas", "luctus", "turpis", "a"
    };
  }

  // endregion
}
