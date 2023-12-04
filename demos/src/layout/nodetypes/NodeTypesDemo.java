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
package layout.nodetypes;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.*;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.*;
import com.yworks.yfiles.layout.circular.CircularLayout;
import com.yworks.yfiles.layout.circular.CircularLayoutData;
import com.yworks.yfiles.layout.circular.NodeTypeAwareSequencer;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.organic.CycleSubstructureStyle;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayoutData;
import com.yworks.yfiles.layout.organic.ParallelSubstructureStyle;
import com.yworks.yfiles.layout.organic.StarSubstructureStyle;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.tree.CompactNodePlacer;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeLayoutData;
import com.yworks.yfiles.layout.tree.TreeReductionStage;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.PopulateItemPopupMenuEventArgs;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.Locale;

/**
 * A demo that shows how different layout algorithms handle nodes with types.
 */
public class NodeTypesDemo extends AbstractDemo {
  private JComboBox<Sample> sampleComboBox;
  private JCheckBox considerTypesCheckBox;
  private Action prevSampleAction;
  private Action nextSampleAction;
  private Action layoutAction;

  // region Style definitions

  // node visualizations for each node type
  private static final ShapeNodeStyle[] NODE_STYLES = {
      createNodeStyle(Color.decode("#17bebb"), Color.decode("#407271")),
      createNodeStyle(Color.decode("#ffc914"), Color.decode("#998953")),
      createNodeStyle(Color.decode("#ff6c00"), Color.decode("#662b00")),
  };

  // edge visualizations for directed and undirected edges
  private static final IEdgeStyle DIRECTED_EDGE_STYLE = createEdgeStyle(true);
  private static final IEdgeStyle UNDIRECTED_EDGE_STYLE = createEdgeStyle(false);

  private static ShapeNodeStyle createNodeStyle(Color fill, Color stroke) {
    ShapeNodeStyle style = new ShapeNodeStyle();
    style.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    style.setPaint(fill);
    style.setPen(new Pen(stroke, 1.5));
    return style;
  }

  private static IEdgeStyle createEdgeStyle(boolean directed) {
    PolylineEdgeStyle style = new PolylineEdgeStyle();
    style.setPen(new Pen(Color.BLACK, 1.5));
    style.setTargetArrow(directed ? IArrow.DEFAULT : IArrow.NONE);
    return style;
  }

  // endregion

  // region User interface event handling

  /**
   * Adds controls to select different sample graphs, to calculate a layout and to enable the consideration of the node
   * types.
   */
  @Override
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(new JLabel("Sample Graph:"));
    toolBar.add(prevSampleAction = createPrevSampleAction());
    toolBar.add(sampleComboBox = createSampleComboBox());
    toolBar.add(nextSampleAction = createNextSampleAction());
    toolBar.addSeparator();
    toolBar.add(layoutAction = createLayoutAction());
    toolBar.add(considerTypesCheckBox = createConsiderTypesCheckBox());
  }

  /**
   * Creates an {@link Action} for choosing the previous sample graph.
   */
  private Action createPrevSampleAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = sampleComboBox.getSelectedIndex();
        int sampleCount = sampleComboBox.getItemCount();
        int newIndex = index > 0 ? index - 1 : sampleCount - 1;
        sampleComboBox.setSelectedIndex(newIndex);
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Load previous graph");
    action.putValue(Action.SMALL_ICON, createIcon("arrow-left-16.png"));
    return action;
  }

  /**
   * Creates an {@link Action} for choosing the next sample graph.
   */
  private Action createNextSampleAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int index = sampleComboBox.getSelectedIndex();
        int sampleCount = sampleComboBox.getItemCount();
        int newIndex = index < sampleCount - 1 ? index + 1 : 0;
        sampleComboBox.setSelectedIndex(newIndex);
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Load next graph");
    action.putValue(Action.SMALL_ICON, createIcon("arrow-right-16.png"));
    return action;
  }

  /**
   * Creates a {@link JComboBox} for choosing a sample graph.
   */
  private JComboBox<Sample> createSampleComboBox() {
    Sample[] samples = {
        createHierarchicSample(),
        createOrganicSample(),
        createTreeSample(),
        createCircularSample(),
        createComponentSample()
    };

    JComboBox<Sample> comboBox = new JComboBox<>(samples);
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setToolTipText("Choose the sample graph");
    comboBox.addActionListener(e -> loadSample());

    return comboBox;
  }

  /**
   * Creates an {@link Action} for invoking the layout calculation.
   */
  private Action createLayoutAction() {
    AbstractAction action = new AbstractAction("Layout") {
      @Override
      public void actionPerformed(ActionEvent e) {
        applyLayout(true);
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Apply layout");
    return action;
  }

  /**
   * Creates an {@link JCheckBox} for toggling type consideration.
   */
  private JCheckBox createConsiderTypesCheckBox() {
    JCheckBox checkBox = new JCheckBox("Consider Types", true);
    checkBox.addActionListener(e -> applyLayout(true));
    return checkBox;
  }

  /**
   * Activates or deactivates the UI elements for changing the sample.
   */
  private void enableUi(boolean enabled) {
    layoutAction.setEnabled(enabled);
    sampleComboBox.setEnabled(enabled);
    prevSampleAction.setEnabled(enabled);
    nextSampleAction.setEnabled(enabled);
    considerTypesCheckBox.setEnabled(enabled);
  }

  // endregion

  // region Initialization

  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    super.initialize();
    initializeInputModes();
    initializeGraph(graphComponent.getGraph());
  }


  /**
   * Loads the first sample graph.
   */
  @Override
  public void onVisible() {
    super.onVisible();
    sampleComboBox.setSelectedIndex(0);
  }


  /**
   * Initializes the edit mode and the context menu.
   */
  private void initializeInputModes() {
    GraphEditorInputMode editMode = new GraphEditorInputMode();
    editMode.setSelectableItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    editMode.setPopupMenuItems(GraphItemTypes.NODE);
    editMode.setNodeCreator((context, graph, location, parent) -> {
      INode node = graph.createNode(location);
      node.setTag(0);
      graph.setStyle(node, getNodeStyle(node));
      return node;
    });

    editMode.addPopulateItemPopupMenuListener(this::onPopulateItemPopupMenu);

    graphComponent.setInputMode(editMode);
  }

    /**
     * Opens a context menu to change the node type.
     */
    private void onPopulateItemPopupMenu(Object source, PopulateItemPopupMenuEventArgs<IModelItem> args) {
      IModelItem item = args.getItem();
      if (item instanceof INode) {
        // Select node if not already selected
        if (!graphComponent.getSelection().isSelected(item)) {
          graphComponent.getSelection().clear();
          graphComponent.getSelection().setSelected(item, true);
        }

        JPopupMenu menu = (JPopupMenu) args.getMenu();

        for (int i = 0; i < NODE_STYLES.length; i++) {
          int type = i;
          ShapeNodeStyle newNodeStyle = NODE_STYLES[type];
          JMenuItem menuItem = new JMenuItem();
          menuItem.setOpaque(true);
          menuItem.setBackground((Color) newNodeStyle.getPaint());
          menuItem.addActionListener(e -> {
            // Change color for all selected nodes
            for (INode node : graphComponent.getSelection().getSelectedNodes()) {
              node.setTag(type);
              graphComponent.getGraph().setStyle(node, newNodeStyle);
            }
            applyLayout(true);
          });
          menu.add(menuItem);
        }
      }
    }

    /**
     * Configures the defaults for the graph.
     */
    private void initializeGraph(IGraph graph) {
      graph.getNodeDefaults().setStyleInstanceSharingEnabled(false);
      graph.getNodeDefaults().setSize(new SizeD(40, 40));

      graph.setUndoEngineEnabled(true);
    }


  /**
   * Loads a sample graph and adapts the visualization of the nodes to their types and of the edges to their directions.
   */
  private void loadSample() {
    Sample selectedSample = (Sample) sampleComboBox.getSelectedItem();
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/" + selectedSample.file + ".graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    IGraph graph = graphComponent.getGraph();
    graph.getNodes().forEach(node -> graph.setStyle(node, getNodeStyle(node)));
    graph.getEdges().forEach(edge -> graph.setStyle(edge, getEdgeStyle(selectedSample.isDirected)));

    applyLayout(false);
    graphComponent.fitGraphBounds();
  }

  // endregion

  // region Node type handling

  /**
   * Gets the type of the given node from its tag.
   */
  private int getNodeType(INode node) {
    // The implementation of this demo assumes that on the INode.Tag a type property
    // (a number) exists. Note though that for the layout's node type feature arbitrary objects
    // from arbitrary sources may be used.
    return node.getTag() instanceof Integer ? (int) node.getTag() : 0;
  }

  /**
   * Determines the visualization of a node based on its type.
   */
  private ShapeNodeStyle getNodeStyle(INode node) {
    int type = getNodeType(node);
    return NODE_STYLES[type];
  }

  /**
   * Determines the visualization of an edge based on its direction.
   */
  private IEdgeStyle getEdgeStyle(boolean directed) {
    return directed ? DIRECTED_EDGE_STYLE : UNDIRECTED_EDGE_STYLE;
  }

  // endregion

  // region Sample creation

  /**
   * Creates and configures the {@link HierarchicLayout} and the {@link HierarchicLayoutData} such that node types are
   * considered.
   */
  private Sample createHierarchicSample() {
    // create hierarchic layout - no further settings on the algorithm necessary to support types
    HierarchicLayout layout = new HierarchicLayout();

    // the node types are specified as delegate on the nodeTypes property of the layout data
    HierarchicLayoutData layoutData = new HierarchicLayoutData();
    layoutData.setNodeTypes(this::getNodeType);

    return new Sample("Hierarchic", "hierarchic", layout, layoutData, true);
  }

  /**
   * Creates and configures the {@link OrganicLayout} and the {@link OrganicLayoutData} such that node types are
   * considered.
   */
  private Sample createOrganicSample() {
    // to consider node types, substructures handling (stars, parallel structures and cycles)
    // on the organic layout is enabled - otherwise types have no influence
    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setDeterministicModeEnabled(true);
    organicLayout.setNodeSizeConsiderationEnabled(true);
    organicLayout.setMinimumNodeDistance(30);
    organicLayout.setStarSubstructureStyle(StarSubstructureStyle.CIRCULAR);
    organicLayout.setStarSubstructureTypeSeparationEnabled(false);
    organicLayout.setParallelSubstructureStyle(ParallelSubstructureStyle.RECTANGULAR);
    organicLayout.setParallelSubstructureTypeSeparationEnabled(false);
    organicLayout.setCycleSubstructureStyle(CycleSubstructureStyle.CIRCULAR);

    // create an organic layout wrapped by an organic edge router
    OrganicEdgeRouter layout = new OrganicEdgeRouter(organicLayout);

    // the node types are specified as delegate on the nodeTypes property of the layout data
    OrganicLayoutData layoutData = new OrganicLayoutData();
    layoutData.setNodeTypes(this::getNodeType);

    return new Sample("Organic", "organic", layout, layoutData, false);
  }

  /**
   * Creates and configures the {@link TreeLayout} and the {@link TreeLayoutData} such that node types are considered.
   */
  private Sample createTreeSample() {
    // create a tree layout including a reduction stage to support non-tree graphs too
    TreeLayout layout = new TreeLayout();
    layout.setDefaultNodePlacer(new CompactNodePlacer());

    EdgeRouter edgeRouter = new EdgeRouter();
    edgeRouter.setScope(Scope.ROUTE_AFFECTED_EDGES);

    TreeReductionStage reductionStage = new TreeReductionStage();
    reductionStage.setNonTreeEdgeRouter(edgeRouter);
    reductionStage.setNonTreeEdgeSelectionKey(edgeRouter.getAffectedEdgesDpKey());

    layout.prependStage(reductionStage);

    // the node types are specified as delegate on the nodeTypes property of the layout data
    TreeLayoutData layoutData = new TreeLayoutData();
    layoutData.setNodeTypes(this::getNodeType);

    return new Sample("Tree", "tree", layout, layoutData, true);
  }

  /// <summary>
  /// Creates and configures the <see cref="CircularLayout"/> and the <see cref="CircularLayoutData"/>
  /// such that node types are considered.
  /// </summary>

  /**
   * Creates and configures the {@link CircularLayout} and the {@link CircularLayoutData} such that node types are
   * considered.
   */
  private Sample createCircularSample() {
    // create a circular layout and specify the NodeTypeAwareSequencer as sequencer responsible
    // for the ordering on the circle - this is necessary to support node types
    CircularLayout layout = new CircularLayout();
    layout.getSingleCycleLayout().setNodeSequencer(new NodeTypeAwareSequencer());

    // the node types are specified as delegate on the nodeTypes property of the layout data
    CircularLayoutData layoutData = new CircularLayoutData();
    layoutData.setNodeTypes(this::getNodeType);

    return new Sample("Circular", "circular", layout, layoutData, false);
  }

  /**
   * Creates and configures the {@link ComponentLayout} and the {@link ComponentLayoutData} such that node types are
   * considered.
   */
  private Sample createComponentSample() {
    // create a component layout with default settings
    ComponentLayout layout = new ComponentLayout();

    // note that with the default component arrangement style the types of nodes have an influence
    // already - however, if in a row only components with nodes of the same type should be
    // allowed, this can be achieved by specifying the style as follows:
    // layout.Style = ComponentArrangementStyles.MultiRowsTypeSeparated

    // the node types are specified as delegate on the nodeTypes property of the layout data
    ComponentLayoutData layoutData = new ComponentLayoutData();
    layoutData.setNodeTypes(this::getNodeType);

    return new Sample("Component", "component", layout, layoutData, false);
  }

  // endregion

  // region Layout calculation

  /**
   * Calculates a layout taking the node types into account.
   */
  private void applyLayout(boolean animate) {
    Sample sample = (Sample) sampleComboBox.getSelectedItem();
    boolean considerTypes = considerTypesCheckBox.isSelected();

    ILayoutAlgorithm layout = sample.layout;
    LayoutData layoutData = considerTypes ? sample.layoutData : null;

    LayoutExecutor layoutExecutor = new LayoutExecutor(graphComponent, layout);
    layoutExecutor.setLayoutData(layoutData);
    layoutExecutor.setPortAdjustmentPolicy(PortAdjustmentPolicy.ALWAYS);
    layoutExecutor.setRunningInThread(false);
    layoutExecutor.setViewportAnimationEnabled(true);
    if (animate) {
      layoutExecutor.setDuration(Duration.ofMillis(700));
    }

    enableUi(false);
    layoutExecutor.start().thenRun(() -> enableUi(true));
  }

  // endregion

  /**
   * Contains all information about a sample.
   */
  static class Sample {
    public final String name;
    public final String file;
    public final ILayoutAlgorithm layout;
    public final LayoutData layoutData;
    public final boolean isDirected;

    public Sample(String name, String file, ILayoutAlgorithm layout, LayoutData layoutData, boolean isDirected) {
      this.name = name;
      this.file = file;
      this.layout = layout;
      this.layoutData = layoutData;
      this.isDirected = isDirected;
    }

    public String toString() {
      return name;
    }
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new NodeTypesDemo().start();
    });
  }
}
