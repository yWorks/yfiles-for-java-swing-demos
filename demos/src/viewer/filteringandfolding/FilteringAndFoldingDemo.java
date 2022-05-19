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
package viewer.filteringandfolding;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.FilteredGraphWrapper;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import toolkit.AbstractDemo;

import javax.swing.JButton;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.util.function.Predicate;

/**
 * This demo shows how to configure filtering and folding in the same application.
 */
public class FilteringAndFoldingDemo extends AbstractDemo {
  private JButton filterButton;
  private JButton resetButton;

  /**
   * Enables filtering and folding.
   * When utilizing both features on the same graph, it is important to apply
   * filtering before folding. Therefore, {@link FilteredGraphWrapper} to create
   * filtered view of the graph and after that, use {@link FoldingManager} to
   * create a folding view of the filtered graph.
   *
   * @param fullGraph The unfiltered, unfolded full graph
   * @return The filtered and folded view graph.
   */
  private IGraph enableFilteringAndFolding(IGraph fullGraph){
    // create the filtered graph - hide items whose tag contains the string 'filtered'
    Predicate<INode> nodePredicate = node -> !"filtered".equals(node.getTag());
    Predicate<IEdge> edgePredicate = edge -> !"filtered".equals(edge.getTag());
    FilteredGraphWrapper filteredGraph = new FilteredGraphWrapper(fullGraph, nodePredicate, edgePredicate);

    // create the folding manager
    FoldingManager manager = new FoldingManager(filteredGraph);

    // create a folding view that manages the folded graph
    IFoldingView foldingView = manager.createFoldingView();

    // return the view graph of the foldingView
    return foldingView.getGraph();
  }

  private void wrapGroupNodeStyle(IGraph fullGraph) {
    // add a collapse/expand button to the group node style default
    INodeStyle groupNodeStyle = fullGraph.getGroupNodeDefaults().getStyle();
    CollapsibleNodeStyleDecorator decoratedStyle = new CollapsibleNodeStyleDecorator(groupNodeStyle);
    fullGraph.getGroupNodeDefaults().setStyle(decoratedStyle);
  }

  private FilteredGraphWrapper getFilteredGraph() {
    // the FilteredGraphWrapper is the master graph of our FoldingView:
    IFoldingView foldingView = graphComponent.getGraph().getFoldingView();
    IGraph masterGraph = foldingView.getManager().getMasterGraph();
    return (FilteredGraphWrapper) masterGraph;
  }

  /**
   * Hides the selected items.
   */
  public void filterItems() {
    // marks the selected items such that the nodePredicate or edgePredicate will filter them
    graphComponent.getSelection().getSelectedNodes().forEach(node -> node.setTag("filtered"));
    graphComponent.getSelection().getSelectedEdges().forEach(edge -> edge.setTag("filtered"));

    // re-evaluate the filter predicates to actually hide the items
    FilteredGraphWrapper filteredGraph = getFilteredGraph();
    filteredGraph.nodePredicateChanged();
    filteredGraph.edgePredicateChanged();

    // enable the reset buttons
    resetButton.setEnabled(true);
  }

  /**
   * Restores the filtered items.
   */
  public void resetFilter() {
    // access the unfiltered, unfolded graph to remove the filter mark from all items
    FilteredGraphWrapper filteredGraph = getFilteredGraph();
    IGraph fullGraph = filteredGraph.getWrappedGraph();

    // unmark the selected items
    fullGraph.getNodes().forEach(node -> node.setTag(null));
    fullGraph.getEdges().forEach(edge -> edge.setTag(null));

    // re-evaluate the filter predicates to actually show the items again
    filteredGraph.nodePredicateChanged();
    filteredGraph.edgePredicateChanged();

    // disable the reset button
    resetButton.setEnabled(false);
  }

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    // create and initialize the full graph
    IGraph fullGraph = initializeFullGraph();

    // enable filtering and folding for the graph
    IGraph viewGraph = enableFilteringAndFolding(fullGraph);

    // assign the folded graph to the graph component
    graphComponent.setGraph(viewGraph);

    // initializes the input modes
    initializeInputMode();

    // initialize the behavior of the filter buttons
    initializeButtons();
  }

  private IGraph initializeFullGraph() {
    // create the unfiltered, unfolded full graph
    DefaultGraph fullGraph = new DefaultGraph();

    // configure the fullGraph
    initializeDefaults(fullGraph);

    // create an initial sample graph
    createInitialGraph(fullGraph);

    // enable the undo functionality
    fullGraph.setUndoEngineEnabled(true);

    // update the reset filter button depending on the current graph state
    fullGraph.getUndoEngine().addUnitUndoneListener((source, evt) -> updateState());
    fullGraph.getUndoEngine().addUnitRedoneListener((source, evt) -> updateState());

    return fullGraph;
  }

  /**
   * Updates the 'Reset Filter' button state based on the current graph state.
   */
  private void updateState() {
    FilteredGraphWrapper filteredGraph = getFilteredGraph();
    filteredGraph.nodePredicateChanged();
    filteredGraph.edgePredicateChanged();

    IGraph fullGraph = filteredGraph.getWrappedGraph();
    boolean hasFilteredItems = fullGraph.getNodes().stream().anyMatch(node -> node.getTag() != null && node.getTag() == "filtered") ||
        fullGraph.getEdges().stream().anyMatch(edge -> edge.getTag() != null && edge.getTag() == "filtered");
    resetButton.setEnabled(hasFilteredItems);
  }

  /**
   * Initializes the defaults for the styles.
   */
  private void initializeDefaults(IGraph graph) {
    // configure defaults for normal nodes
    ShinyPlateNodeStyle defaultNodeStyle = new ShinyPlateNodeStyle();
    defaultNodeStyle.setPaint(Colors.DARK_ORANGE);
    graph.getNodeDefaults().setStyle(defaultNodeStyle);
    graph.getNodeDefaults().setSize(new SizeD(40, 40));

    // configure defaults for group nodes and their labels
    PanelNodeStyle panelNodeStyle = new PanelNodeStyle();
    Color groupNodeColor = new Color(214, 229, 248);
    panelNodeStyle.setColor(groupNodeColor);
    panelNodeStyle.setInsets(new InsetsD(23, 5, 5, 5));
    panelNodeStyle.setLabelInsetsColor(groupNodeColor);
    graph.getGroupNodeDefaults().setStyle(panelNodeStyle);

    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setTextAlignment(TextAlignment.RIGHT);
    graph.getGroupNodeDefaults().getLabelDefaults().setStyle(defaultLabelStyle);

    graph.getGroupNodeDefaults().getLabelDefaults().setLayoutParameter(InteriorStretchLabelModel.NORTH);

    // wrap group node style to provie +/- buttons to expand/collapse the group
    wrapGroupNodeStyle(graph);
  }

  /**
   * Creates an initial sample graph.
   */
  private void createInitialGraph(IGraph graph) {
    INode node1 = graph.createNode(new PointD(110, 20));
    INode node2 = graph.createNode(new PointD(145, 95));
    INode node3 = graph.createNode(new PointD(75, 95));
    INode node4 = graph.createNode(new PointD(30, 175));
    INode node5 = graph.createNode(new PointD(100, 175));

    INode groupNode = graph.groupNodes(node1, node2, node3);
    graph.addLabel(groupNode, "Group 1");

    graph.createEdge(node1, node2);
    graph.createEdge(node1, node3);
    graph.createEdge(node3, node4);
    graph.createEdge(node3, node5);
    graph.createEdge(node1, node5);
  }

  /**
   * Creates the input mode.
   */
  private void initializeInputMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();

    // enables grouping operations such as grouping selected nodes moving nodes into group nodes
    mode.setGroupingOperationsAllowed(true);
    graphComponent.setInputMode(mode);
  }

  /**
   * Initializes the filter buttons.
   */
  private void initializeButtons() {
    // disable the filter buttons at startup
    filterButton.setEnabled(false);
    resetButton.setEnabled(false);

    // enable the filter button if at least one element is selected
    graphComponent.getSelection().addItemSelectionChangedListener(( object, args ) ->
      filterButton.setEnabled(graphComponent.getSelection().size() != 0)
    );
  }

  /**
   * Configures the toolbar with according buttons and functionality.
   */
  @Override
  protected void configureToolBar( JToolBar toolBar ) {
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
    // adding grouping buttons
    toolBar.add(createCommandButtonAction("Group selected elements", "group-16.png",
                                          ICommand.GROUP_SELECTION, null, graphComponent));
    toolBar.add(createCommandButtonAction("Ungroup selected elements", "ungroup-16.png",
                                          ICommand.UNGROUP_SELECTION, null, graphComponent));
    toolBar.add(createCommandButtonAction("Enter group", "enter-group-16.png",
                                          ICommand.ENTER_GROUP, null, graphComponent));
    toolBar.add(createCommandButtonAction("Exit group", "exit-group-16.png",
                                          ICommand.EXIT_GROUP, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(resetButton = createTextButton("Resets the filter state of all items", "Reset Filter", actionEvent -> resetFilter()));
    toolBar.add(filterButton = createTextButton("Filter selected items", "Filter Items", actionEvent -> filterItems()));
  }

  /**
   * Creates a {@link JButton specific button}.
   *
   * @param tooltip the tooltip to display for the button.
   * @param text    the text to show on the button.
   * @param action  the action to execute when the button is selected.
   */
  protected JButton createTextButton( String tooltip, String text, ActionListener action ) {
    JButton button = new JButton();
    button.setToolTipText(tooltip);
    button.setText(text);
    button.addActionListener(action);
    return button;
  }

  /**
   * Adjusts the view port.
   */
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new FilteringAndFoldingDemo().start();
    });
  }
}
