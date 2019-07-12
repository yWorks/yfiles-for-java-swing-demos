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
package analysis.shortestpath;

import com.yworks.yfiles.analysis.AllPairsShortestPaths;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.LabelTextValidatingEventArgs;
import com.yworks.yfiles.view.input.PopulateItemPopupMenuEventArgs;
import com.yworks.yfiles.view.input.QueryItemToolTipEventArgs;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  Use graph analysis algorithms in yFiles for Java (Swing), specifically, shortest path search, together with
 *  the <code>YGraphAdapter</code> analysis adapter class.
 * </p>
 * <p>
 *   The set of edges that lie on shortest paths between two sets of nodes (sources and targets) is calculated dynamically.
 *   Geometric edge lengths are used for the weight of the edges, unless there are numeric labels attached to
 *   them in which case the value of the label text is used.
 * </p>
 */
public class ShortestPathDemo extends AbstractDemo {
  // holds the currently chosen layout algorithm
  private ILayoutAlgorithm currentLayout;

  // the styles to use for source nodes, target nodes, and ordinary nodes
  private ShinyPlateNodeStyle defaultNodeStyle, targetNodeStyle, sourceNodeStyle, sourceAndTargetNodeStyle;
  // the style to use for ordinary edges and edges that lie on a shortest path
  private PolylineEdgeStyle defaultEdgeStyle, pathEdgeStyle;

  // the current source nodes
  private List<INode> sourceNodes;
  // the current target nodes
  private List<INode> targetNodes;

  // whether to use directed path calculation
  private boolean directed;

  // for creating sample graphs
  private RandomGraphGenerator randomGraphGenerator;

  // the set of the edges that are currently part of the path
  private Set<IEdge> pathEdges = new HashSet<>();
  
  // action that creates a new random sample graph 
  private Action newAction;
  // action that perform a layout run
  private Action layoutAction;
  // combo box that provides different layout algorithms
  private JComboBox layoutComboBox;

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(newAction = createNewAction());
    toolBar.addSeparator();
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(layoutComboBox = createLayoutComboBox(layoutAction = createLayoutAction()));
    toolBar.add(layoutAction);
    toolBar.addSeparator();
    toolBar.add(createMarkAction(true));
    toolBar.add(createMarkAction(false));
    toolBar.add(createEdgeTypeComboBox());
    toolBar.addSeparator();
    toolBar.add(createUniformEdgeLabelsAction());
    toolBar.add(createRemoveLabelsAction());
  }

  /**
   * Creates an {@link javax.swing.Action} to create a new random graph.
   */
  private Action createNewAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        generateGraph();
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "New random graph");
    action.putValue(Action.SMALL_ICON, createIcon("new-document-16.png"));
    return action;
  }

  /**
   * Creates an {@link javax.swing.Action} to run a layout algorithm.
   */
  private Action createLayoutAction() {
    Action action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applyLayout();
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Apply layout");
    action.putValue(Action.SMALL_ICON, createIcon("new-document-16.png"));
    return action;
  }

  /**
   * Creates a {@link javax.swing.JComboBox} to select a layout algorithm.
   * @param layoutAction the {@link javax.swing.Action} to the change the icon
   */
  private JComboBox createLayoutComboBox( Action layoutAction) {
    JComboBox comboBox = new JComboBox<>(new Object[]{"Hierarchic Layout", "Organic Layout", "Orthogonal Layout"});
    OrganicLayout sol = new OrganicLayout();
    sol.setMinimumNodeDistance(40);
    ILayoutAlgorithm[] algorithms = new ILayoutAlgorithm[]{new HierarchicLayout(), sol, new OrthogonalLayout()};
    Icon[] icons = new Icon[]{
        createIcon("layout-hierarchic.png"),
        createIcon("layout-organic-16.png"),
        createIcon("layout-orthogonal-16.png")};
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setToolTipText("Select layout");
    comboBox.setSelectedIndex(1);
    comboBox.addActionListener(e -> {
      int index = comboBox.getSelectedIndex();
      currentLayout = algorithms[index];
      layoutAction.putValue(Action.SMALL_ICON, icons[index]);
      applyLayout();
    });
    currentLayout = algorithms[1];
    layoutAction.putValue(Action.SMALL_ICON, icons[1]);
    return comboBox;
  }

  /**
   * Creates an {@link javax.swing.Action} to mark selected nodes as start respectively target nodes.
   */
  private Action createMarkAction(boolean asSource) {
    Action action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        List<INode> selectedNodes = new ArrayList<>();
        for (INode node : graphComponent.getSelection().getSelectedNodes()) {
          selectedNodes.add(node);
        }
        mark(selectedNodes, asSource);
      }
    };
    action.putValue(Action.NAME, asSource ? "Mark as Source" : "Mark as Target");
    action.putValue(Action.SHORT_DESCRIPTION, asSource ? "Mark selected nodes as source" : "Mark selected nodes as target");
    return action;
  }

  /**
   * Creates a {@link javax.swing.JComboBox} to select the type of the edges.
   */
  private JComboBox createEdgeTypeComboBox() {
    JComboBox comboBox = new JComboBox<>(new Object[]{"Directed Edges", "Undirected Edges"});
    IArrow[] arrows = new IArrow[]{IArrow.DEFAULT, IArrow.NONE};
    comboBox.setToolTipText("Select edge type");
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setSelectedIndex(directed ? 0 : 1);
    comboBox.addActionListener(e -> {
      int index = comboBox.getSelectedIndex();
      defaultEdgeStyle.setTargetArrow(arrows[index]);
      pathEdgeStyle.setTargetArrow(arrows[index]);
      directed = (index == 0);
      calculateShortestPath(graphComponent, IEventArgs.EMPTY);
    });
    return comboBox;
  }

  /**
   * Creates an {@link javax.swing.Action} to uniform the labels of all edges.
   */
  private Action createUniformEdgeLabelsAction() {
    Action action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String weight;
        do {
          String message = "Enter Uniform Edge Weight.\nOnly non-negative numbers allowed.";
          weight = JOptionPane.showInputDialog(graphComponent, message, "Uniform Edge Weight", JOptionPane.QUESTION_MESSAGE);
          if (weight == null) {
            return;
          }
        } while (!isValidEdgeWeight(weight));

        for (IEdge edge : graphComponent.getGraph().getEdges()) {
          if (edge.getLabels().size() > 0) {
            graphComponent.getGraph().setLabelText(edge.getLabels().getItem(0), weight);
          } else {
            graphComponent.getGraph().addLabel(edge, weight);
          }
        }
        calculateShortestPath(graphComponent, IEventArgs.EMPTY);
      }
    };
    action.putValue(Action.NAME, "Uniform Edge Labels");
    action.putValue(Action.SHORT_DESCRIPTION, "Set uniform labels for all edges");
    return action;
  }

  /**
   * Checks if teh edge weight is valid, i.e. is a non-negative double value.
   */
  private boolean isValidEdgeWeight(String weight) {
    try {
      return !weight.isEmpty() && Double.parseDouble(weight) >= 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  /**
   * Creates an {@link javax.swing.Action} to remove the labels of all edges.
   */
  private Action createRemoveLabelsAction() {
    Action action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (IEdge edge : graphComponent.getGraph().getEdges()) {
          List<ILabel> labels = new ArrayList<>();
          edge.getLabels().forEach(labels::add);
          labels.forEach(graphComponent.getGraph()::remove);
        }
        calculateShortestPath(graphComponent, IEventArgs.EMPTY);
      }
    };
    action.putValue(Action.NAME, "Remove Labels");
    action.putValue(Action.SHORT_DESCRIPTION, "Remove all labels");
    return action;
  }

  /**
   * Initializes the input modes and styles, initializes the random graph generator and loads a sample graph.
   */
  public void initialize() {
    sourceNodes = new ArrayList<>();
    targetNodes = new ArrayList<>();

    initializeInputModes();
    initializeStyles();

    // initialize random graph generator
    randomGraphGenerator = new RandomGraphGenerator();
    randomGraphGenerator.setCycleCreationAllowed(true);
    randomGraphGenerator.setParallelEdgeCreationAllowed(true);
    randomGraphGenerator.setSelfLoopCreationAllowed(true);
    randomGraphGenerator.setEdgeCount(40);
    randomGraphGenerator.setNodeCount(30);

    graphComponent.getGraph().clear();
    randomGraphGenerator.generate(graphComponent.getGraph());
  }

  /**
   * Centers and arranges the graph in the graph component.
   */
  public void onVisible() {
    // center the graph to prevent the initial layout fading in from the top left corner
    graphComponent.fitGraphBounds();
    applyLayout();
  }

  /**
   * Builds an input mode and registers for the various events that could change the shortest path calculation.
   */
  private void initializeInputModes() {
    // build an input mode and register for the various events
    // that could change the shortest path calculation
    GraphEditorInputMode editMode = new GraphEditorInputMode();

    // deletion
    editMode.addDeletedSelectionListener(this::calculateShortestPath);
    // edge creation
    editMode.getCreateEdgeInputMode().addEdgeCreatedListener(this::calculateShortestPath);
    // edge reversal
    graphComponent.getGraph().addEdgePortsChangedListener(this::calculateShortestPath);
    // movement of items
    editMode.getMoveInputMode().addDragFinishedListener(this::calculateShortestPath);
    // resizing of items as well as creating and moving bends
    editMode.getHandleInputMode().addDragFinishedListener(this::calculateShortestPath);
    // adding or changing labels
    editMode.addLabelAddedListener(this::calculateShortestPath);
    editMode.addLabelTextChangedListener(this::calculateShortestPath);

    // allow only numeric label texts
    editMode.addValidateLabelTextListener(this::validateLabelText);

    // also prepare a popup menu
    editMode.addPopulateItemPopupMenuListener(this::populateNodePopupMenu);

    // show weight tooltips
    editMode.setToolTipItems(GraphItemTypes.EDGE);
    editMode.addQueryItemToolTipListener(this::queryItemToolTip);

    // when the WaitInputMode kicks in, disable buttons which might cause
    // inconsistencies when performed while a layout calculation is running
    editMode.getWaitInputMode().addWaitingStartedListener((source, args) -> {
      newAction.setEnabled(false);
      layoutComboBox.setEnabled(false);
      layoutAction.setEnabled(false);
    });
    editMode.getWaitInputMode().addWaitingEndedListener((source, args) -> {
      newAction.setEnabled(true);
      layoutComboBox.setEnabled(true);
      layoutAction.setEnabled(true);
    });

    graphComponent.setInputMode(editMode);
  }

  /**
   * Allows only empty labels (for deletion) and labels containing a floating point value.
   */
  private void validateLabelText(Object sender, LabelTextValidatingEventArgs args) {
    args.setNewText(args.getNewText().trim());
    if (args.getNewText().isEmpty()) {
      return;
    }
    try {
      if (Double.parseDouble(args.getNewText()) < 0) {
        args.setCanceling(true);
      }
    } catch (NumberFormatException e) {
      args.setCanceling(true);
    }
  }

  /**
   *  Populates the popup menu for nodes.
   */
  private void populateNodePopupMenu(Object sender, PopulateItemPopupMenuEventArgs<IModelItem> args) {
    ISelectionModel<INode> selection = graphComponent.getSelection().getSelectedNodes();
    if (args.getItem() instanceof INode) {
      INode node = (INode) args.getItem();
      if (!selection.isSelected(node)) {
        selection.clear();
        selection.setSelected(node, true);
        graphComponent.setCurrentItem(node);
      }
    }
    if (selection.size() > 0) {
      JMenuItem markAsSourceItem = new JMenuItem("Mark as Source");
      markAsSourceItem.addActionListener(e -> {
        List<INode> selectedNodes = new ArrayList<>();
        for (INode node : selection) {
          selectedNodes.add(node);
        }
        mark(selectedNodes, true);
      });
      JPopupMenu popupMenu = (JPopupMenu) args.getMenu();
      popupMenu.add(markAsSourceItem);
      JMenuItem markAsTargetItem = new JMenuItem("Mark as Target");
      markAsTargetItem.addActionListener(e -> {
        List<INode> selectedNodes = new ArrayList<>();
        selection.forEach(selectedNodes::add);
        mark(selectedNodes, false);
      });
      popupMenu.add(markAsTargetItem);

      // check if one or more of the selected nodes are already marked as source or target
      boolean marked = false;
      for (INode n : selection) {
        if (sourceNodes.contains(n) || targetNodes.contains(n)) {
          marked = true;
          break;
        }
      }
      if (marked) {
        // add the 'Remove Mark' item
        JMenuItem removeMarkItem = new JMenuItem("Remove Mark");
        removeMarkItem.addActionListener(e -> {
          List<INode> sn = new ArrayList<>(sourceNodes);
          sourceNodes.stream().filter(selection::isSelected).forEach(sn::remove);
          mark(sn, true);
          List<INode> tn = new ArrayList<>(targetNodes);
          targetNodes.stream().filter(selection::isSelected).forEach(tn::remove);
          mark(tn, false);
        });
        popupMenu.add(removeMarkItem);
      }
    }
    args.setHandled(true);
  }

  /**
   * Shows the weight of the edge as a tooltip.
   */
  private void queryItemToolTip(Object sender, QueryItemToolTipEventArgs<IModelItem> args) {
    IEdge edge = (IEdge) args.getItem();
    if (edge != null) {
      args.setToolTip("Weight = " + getEdgeWeight(edge));
    }
  }

  /**
   * Initializes the styles to use for the graph.
   */
  private void initializeStyles() {
    defaultNodeStyle = new ShinyPlateNodeStyle();
    defaultNodeStyle.setPaint(Colors.DARK_ORANGE);
    sourceNodeStyle = new ShinyPlateNodeStyle();
    sourceNodeStyle.setPaint(Colors.LIME_GREEN);
    targetNodeStyle = new ShinyPlateNodeStyle();
    targetNodeStyle.setPaint(Colors.ORANGE_RED);

    float[] fractions = {0f, 0.49f, 0.51f, 1f};
    Color[] colors = {Colors.LIME_GREEN, Colors.LIME_GREEN, Colors.ORANGE_RED, Colors.ORANGE_RED};
    LinearGradientPaint paint = new LinearGradientPaint(0, 0, 10, 10, fractions, colors, MultipleGradientPaint.CycleMethod.REPEAT);
    sourceAndTargetNodeStyle = new ShinyPlateNodeStyle();
    sourceAndTargetNodeStyle.setPaint(paint);

    defaultEdgeStyle = new PolylineEdgeStyle();
    defaultEdgeStyle.setPen(Pen.getBlack());
    defaultEdgeStyle.setTargetArrow(directed ? IArrow.DEFAULT : IArrow.NONE);

    pathEdgeStyle = new PolylineEdgeStyle();
    pathEdgeStyle.setPen(new Pen(Color.RED, 4.0));
    pathEdgeStyle.setTargetArrow(directed ? IArrow.DEFAULT : IArrow.NONE);

    graphComponent.getGraph().getNodeDefaults().setStyle(defaultNodeStyle);
    graphComponent.getGraph().getNodeDefaults().setSize(new SizeD(30, 30));
    graphComponent.getGraph().getEdgeDefaults().setStyle(defaultEdgeStyle);

    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setFont(new Font("Dialog", Font.PLAIN, 10));
    labelStyle.setTextPaint(Color.BLACK);
    labelStyle.setBackgroundPaint(Color.WHITE);
    graphComponent.getGraph().getEdgeDefaults().getLabelDefaults().setStyle(labelStyle);
  }

  /**
   * Generates a new graph, applies the layout and recalculates the shortest paths.
   */
  private void generateGraph() {
    graphComponent.getGraph().clear();
    randomGraphGenerator.generate(graphComponent.getGraph());

    // center the graph to prevent the initial layout fading in from the top left corner
    graphComponent.fitGraphBounds();
    applyLayout();
  }

  /**
   * Applies the layout and recalculates the shortest paths.
   */
  private void applyLayout() {
    if (currentLayout != null) {
      graphComponent.morphLayout(currentLayout, Duration.ofSeconds(1), this::calculateShortestPath);
    }
  }

  /**
   * Marks the list of nodes as source respectively target nodes.
   */
  private void mark(List<INode> nodes, boolean asSource) {
    // Reset style of old target nodes
    (asSource ? sourceNodes : targetNodes).stream()
        .filter(graphComponent.getGraph()::contains)
        .forEach(node -> graphComponent.getGraph().setStyle(node, defaultNodeStyle));

    if (asSource) {
      sourceNodes = nodes;
    } else {
      targetNodes = nodes;
    }

    setStyles();
    calculateShortestPath(graphComponent, IEventArgs.EMPTY);
  }

  /**
   * Sets the node styles for source and target nodes.
   */
  private void setStyles() {
    // set target node styles
    for (INode targetNode : targetNodes) {
      graphComponent.getGraph().setStyle(targetNode, targetNodeStyle);
    }

    // set source node styles
    for (INode sourceNode : sourceNodes) {
      // check for nodes which are both - source and target
      if (targetNodes.contains(sourceNode)) {
        graphComponent.getGraph().setStyle(sourceNode, sourceAndTargetNodeStyle);
      } else {
        graphComponent.getGraph().setStyle(sourceNode, sourceNodeStyle);
      }
    }
  }

  /**
   * Calculates the shortest paths from a set of source nodes to a set of target nodes and marks it.
   * <p>
   *   This is the implementation for a list of source and target nodes.
   * </p>
   */
  private void calculateShortestPath(final Object source, final IEventArgs args) {
    // reset old path edges
    pathEdges.stream()
        .filter(graphComponent.getGraph()::contains)
        .forEach(edge -> graphComponent.getGraph().setStyle(edge, defaultEdgeStyle));

    // remove deleted nodes
    ArrayList<INode> sn = new ArrayList<>(sourceNodes);
    sn.stream()
        .filter(sourceNode -> !graphComponent.getGraph().contains(sourceNode))
        .forEach(sourceNodes::remove);
    ArrayList<INode> tn = new ArrayList<>(targetNodes);
    tn.stream()
        .filter(targetNode -> !graphComponent.getGraph().contains(targetNode))
        .forEach(targetNodes::remove);

    // show wait cursor while running the shortest path algorithm asynchronously
    GraphEditorInputMode geim = (GraphEditorInputMode) graphComponent.getInputMode();
    geim.setWaiting(true);

    AllPairsShortestPaths allPaths = new AllPairsShortestPaths();
    allPaths.setCosts(this::getEdgeWeight);
    allPaths.setDirected(directed);
    allPaths.setSources(sourceNodes);
    allPaths.setSinks(targetNodes);

    allPaths.runAsync(graphComponent.getGraph()).thenAccept(result -> {
      // collect all edges of all paths
      pathEdges = result.getPaths().stream()
          .flatMap(path -> path.getEdges().stream())
          .collect(Collectors.toSet());

      // mark path with path style
      pathEdges.stream()
          .filter(graphComponent.getGraph()::contains)
          .forEach(edge -> graphComponent.getGraph().setStyle(edge, pathEdgeStyle));

      geim.setWaiting(false);

      graphComponent.repaint();
    });
  }

  /**
   * Returns the edge weight for a given edge.
   */
  private double getEdgeWeight(IEdge edge) {
    // if edge has at least one label...
    if (edge.getLabels().size() > 0) {
      // ..try to return its value
      try {
        double weight = Double.parseDouble(edge.getLabels().getItem(0).getText());
        return Math.max(weight, 0);
      } catch (NumberFormatException e) {
        // do nothing
      }
    }

    // calculate geometric edge length
    PointD[] edgePoints = new PointD[edge.getBends().size() + 2];

    edgePoints[0] = getLocation(edge.getSourcePort());
    edgePoints[edge.getBends().size() + 1] = getLocation(edge.getTargetPort());

    for (int i = 0; i < edge.getBends().size(); i++) {
      edgePoints[i + 1] = edge.getBends().getItem(i).getLocation().toPointD();
    }

    double totalEdgeLength = 0;
    for (int i = 0; i < edgePoints.length - 1; i++) {
      totalEdgeLength += edgePoints[i].distanceTo(edgePoints[i + 1]);
    }
    return totalEdgeLength;
  }

  /**
   * Gets a snapshot of the current location of the port.
   * Unlike {@link com.yworks.yfiles.graph.IPort#getLocation()} this does not return a dynamic point that always refers to the current location.
   * @param port The port to retrieve the location from.
   * @return The current port location.
   */
  private static PointD getLocation(IPort port) {
    IPortLocationModelParameter param = port.getLocationParameter();
    return param.getModel().getLocation(port, param);
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new ShortestPathDemo().start();
    });
  }
}
