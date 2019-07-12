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
package complete.orgchart;

import com.yworks.yfiles.view.GraphOverviewComponent;
import com.yworks.yfiles.view.ViewportLimiter;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.FilteredGraphWrapper;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.ItemHoverInputMode;
import com.yworks.yfiles.layout.CompositeLayoutData;
import com.yworks.yfiles.layout.FixNodeLayoutData;
import com.yworks.yfiles.layout.FixNodeLayoutStage;
import com.yworks.yfiles.layout.tree.AssistantNodePlacer;
import com.yworks.yfiles.layout.tree.ChildPlacement;
import com.yworks.yfiles.layout.tree.DefaultNodePlacer;
import com.yworks.yfiles.layout.tree.TreeLayoutData;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.INodePlacer;
import com.yworks.yfiles.layout.tree.LeftRightNodePlacer;
import com.yworks.yfiles.layout.tree.RootAlignment;
import com.yworks.yfiles.layout.tree.RoutingStyle;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import toolkit.AbstractDemo;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.time.Duration;
import java.util.HashSet;

/**
 * An organizational chart application.
 * <p>
 * The employees are visualized using a custom node style that renders different levels of detail based on the zoom
 * level. A <code>yfiles.graph.FilteredGraphWrapper</code> is used to display a subgraph of the model graph.
 * </p>
 * <p>
 * This is the class with the main logic. It is responsible for the connection of the model and the view. It holds all
 * actions and builds and populates the main components.
 * </p>
 */
public class OrgChartDemo extends AbstractDemo {

  /**
   * The view for the properties of an employee in the panel on the left of the application.
   */
  private OrgChartPropertiesView propertiesView;

  /**
   * Used by the predicate function to determine which nodes should not be shown.
   */
  private HashSet<INode> hiddenNodesSet = new HashSet<>();

  /**
   * The filtered graph instance that hides nodes from the graph to create smaller graphs for easier navigation.
   */
  private FilteredGraphWrapper filteredGraphWrapper;

  /**
   * Simple mutex for executing layouts to prevents two layouts to be executed at the same time.
   * Indicates whether a layout is calculated at the moment.
   */
  private boolean doingLayout;

  /**
   * Adds a properties view for focused graph elements to the JRootPane of the application frame
   * in addition to the default graph component, toolbar, and help pane.
   */
  protected void configure(JRootPane rootPane) {
    Container contentPane = rootPane.getContentPane();
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(graphComponent, BorderLayout.CENTER);
    contentPane.add(centerPanel, BorderLayout.CENTER);

    JToolBar toolBar = createToolBar();
    configureToolBar(toolBar);
    centerPanel.add(toolBar, BorderLayout.NORTH);

    JComponent helpPane = createHelpPane();
    contentPane.add(helpPane, BorderLayout.EAST);

    contentPane.add(createLeftPanel(), BorderLayout.WEST);
  }

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);
    toolBar.add(new JToolBar.Separator());
    toolBar.add(createCommandButtonAction("Show Superior", "arrow-up-16.png", SHOW_PARENT, null, graphComponent));
    toolBar.add(createCommandButtonAction("Hide Superior", "arrow-down-16.png", HIDE_PARENT, null, graphComponent));
    toolBar.add(createCommandButtonAction("Show Subordinates", "plus-16.png", SHOW_CHILDREN, null, graphComponent));
    toolBar.add(createCommandButtonAction("Hide Subordinates", "minus-16.png", HIDE_CHILDREN, null, graphComponent));
    toolBar.add(createCommandButtonAction("Show All", "star.png", SHOW_ALL, null, graphComponent));
  }

  /**
   * Creates the panel on the left which contains the overview and a properties view for focused employees.
   */
  private Component createLeftPanel() {
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(createGraphOverview(), BorderLayout.NORTH);
    propertiesView = new OrgChartPropertiesView(this);
    leftPanel.add(propertiesView.getContentPane(), BorderLayout.CENTER);
    return leftPanel;
  }

  /**
   * Creates a GraphOverviewComponent for our demo.
   */
  private JPanel createGraphOverview(){
    JPanel graphOverviewContainer = new JPanel();
    // Another GraphComponent that displays a small overview on the upper left corner of the application.
    GraphOverviewComponent overviewComponent = new GraphOverviewComponent(graphComponent);
    overviewComponent.setMinimumSize(new Dimension(250, 250));
    overviewComponent.setPreferredSize(new Dimension(250, 250));
    graphOverviewContainer.add(overviewComponent);
    graphOverviewContainer.setBorder(BorderFactory.createTitledBorder("Overview"));
    return graphOverviewContainer;
  }

  /**
   * Initializes the appearance and interaction within this demo.
   */
  public void initialize(){
    // set up defaults for the appearance of the graph elements
    this.registerElementDefaults();

    // disable the default visual representation for focused and highlighted nodes
    this.graphComponent.getSelectionIndicatorManager().setEnabled(false);
    this.graphComponent.getHighlightIndicatorManager().setEnabled(false);
    this.graphComponent.getFocusIndicatorManager().setEnabled(false);

    // wire up the interaction and the input modes for this demo
    this.initializeInputMode();

    // show the properties of the focused nodes in the properties view on the left
    this.graphComponent.addCurrentItemChangedListener(
        (sender, args) -> {
          this.propertiesView.showProperties((INode) this.graphComponent.getCurrentItem());

          // ensures that the states of the custom commands
          // Hide Parent, Show Parent, Hide Children, Show Children, Show All
          // are properly updated on current item changes
          // this is especially necessary when traversing the chart with the
          // keyboard's arrow keys
          ICommand.invalidateRequerySuggested();
        });
  }

  /**
   * Loads, arranges, and centers a sample graph in the graph component.
   */
  public void onVisible() {
    initializeGraph();
  }

  /**
   * Initializes the input mode for this demo. Interaction is restricted to
   * nodes and editing is disabled. Also the ItemHoverInputMode is set up to reflect hovering of nodes
   * immediately and the zooming to the item that is double clicked is enabled.
   */
  private void initializeInputMode() {
    GraphViewerInputMode inputMode = new GraphViewerInputMode();
    // disable almost every interaction
    inputMode.setClickableItems(GraphItemTypes.NODE);
    inputMode.setSelectableItems(GraphItemTypes.NONE);
    inputMode.setMarqueeSelectableItems(GraphItemTypes.NONE);
    inputMode.setToolTipItems(GraphItemTypes.NONE);
    inputMode.setPopupMenuItems(GraphItemTypes.NONE);
    // except for focusing of nodes
    inputMode.setFocusableItems(GraphItemTypes.NODE);

    // connect the commands to the methods for the execution and availability of the command
    KeyboardInputMode kim = inputMode.getKeyboardInputMode();
    kim.addCommandBinding(HIDE_CHILDREN, this::executeHideChildren, this::canExecuteHideChildren);
    kim.addCommandBinding(SHOW_CHILDREN, this::executeShowChildren, this::canExecuteShowChildren);
    kim.addCommandBinding(HIDE_PARENT, this::executeHideParent, this::canExecuteHideParent);
    kim.addCommandBinding(SHOW_PARENT, this::executeShowParent, this::canExecuteShowParent);
    kim.addCommandBinding(SHOW_ALL, this::executeShowAll, this::canExecuteShowAll);
    
    kim.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), SHOW_PARENT);
    kim.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), HIDE_PARENT);
    kim.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), SHOW_CHILDREN);
    kim.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), HIDE_CHILDREN);
    kim.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0), SHOW_ALL);

    // setup the HoverInputMode and let it trigger a repaint when hovering occurs
    ItemHoverInputMode itemHoverInputMode = inputMode.getItemHoverInputMode();
    itemHoverInputMode.setHoverItems(GraphItemTypes.NODE);
    itemHoverInputMode.addHoveredItemChangedListener((source, args) -> graphComponent.repaint());

    // zoom to the double clicked item
    inputMode.addItemDoubleClickedListener((source, args) -> zoomToCurrentItem());

    this.graphComponent.setInputMode(inputMode);
  }

  /**
   * Sets up defaults for the styles of nodes and edges.
   */
  private void registerElementDefaults() {
    IGraph graph = this.graphComponent.getGraph();
    graph.getNodeDefaults().setStyle(new LevelOfDetailNodeStyle());
    graph.getNodeDefaults().setSize(new SizeD(250, 100));
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(new Pen(new Color(36, 154, 231, 255), 2));
    edgeStyle.setTargetArrow(IArrow.NONE);
    graph.getEdgeDefaults().setStyle(edgeStyle);
  }

  /**
   * Loads the sample graph from disk.
   */
  private void initializeGraph() {
    try {
      URL url = this.getClass().getResource("resources/samplegraph.graphml");
      // create an IOHandler that will be used for all IO operations
      GraphMLIOHandler ioh = new GraphMLIOHandler();

      // we set the IO handler on the GraphComponent, so the GraphComponent's IO methods
      // will pick up our handler for use during serialization and deserialization.
      graphComponent.setGraphMLIOHandler(ioh);

      // we have declared the employees in GraphML using a specific namespace that we need to register with out own employee class
      // so the GraphMLIOHandler will automatically produce instances of this class as tags for our nodes.
      ioh.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-java/demos/OrgChartDemo/1.0", Employee.class);

      graphComponent.importFromGraphML(url);

      // check for consistency with the data (i.e., every node has a non-null employee tag)
      if (!graphComponent.getGraph().getNodes().stream().allMatch(node -> node.getTag() instanceof Employee)) {
        throw new RuntimeException("bad graph data");
      }

      // setup the superior / subordinates relationships of the employees
      initializeEmployeeHierarchy();

      // we wrap the graph instance by a filtered graph wrapper
      filteredGraphWrapper = new FilteredGraphWrapper(graphComponent.getGraph(), node -> !this.hiddenNodesSet.contains(node), edge -> true);

      this.graphComponent.setGraph(this.filteredGraphWrapper);

      this.doLayout();

      this.graphComponent.fitGraphBounds();
      this.limitViewport();
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Applies a tree layout of the Graph provided by the {@link TreeLayout}.
   * The layout and assistant attributes from the business data of the employees are used to
   * guide the the layout.
   */
  public void doLayout() {
    IGraph tree = graphComponent.getGraph();

    // use the TreeLayout to calculate a layout for the org-chart
    TreeLayout treeLayout = new TreeLayout();

    // provide additional data to configure the TreeLayout
    TreeLayoutData treeLayoutData = new TreeLayoutData();
    // specify a node placer for each node
    treeLayoutData.setNodePlacers(this::getNodePlacer);
    // specify for each node whether it represents an assistant or not
    treeLayoutData.setAssistantNodes(this::isAssistant);

    // run the layout algorithm (without animation)
    tree.applyLayout(treeLayout, treeLayoutData);
  }

  /**
   * Returns a node placer for the given node.
   */
  private INodePlacer getNodePlacer(INode node) {
    IGraph tree = graphComponent.getGraph();
    Employee employee = (Employee) node.getTag();
    if (tree.outDegree(node) == 0 || employee == null) {
      return null;
    }

    INodePlacer childNodePlacer;
    switch (employee.getLayout()) {
      case "RightHanging":
        childNodePlacer = RIGHT_HANGING_NODE_PLACER;
        break;
      case "LeftHanging":
        childNodePlacer = LEFT_HANGING_NODE_PLACER;
        break;
      case "BothHanging":
        childNodePlacer = BOTH_HANGING_NODE_PLACER;
        break;
      default:
        childNodePlacer = DEFAULT_NODE_PLACER;
        break;
    }
    AssistantNodePlacer assistantNodePlacer = new AssistantNodePlacer();
    assistantNodePlacer.setChildNodePlacer(childNodePlacer);
    return assistantNodePlacer;
  }

  /**
   * Returns whether or not the given node is an assistant node.
   */
  private boolean isAssistant(INode node){
    Employee employee = (Employee) node.getTag();
    return employee != null && employee.isAssistant();
  }


  /**
   * Setup a {@link ViewportLimiter} that makes sure that restricts the
   * explorable region.
   */
  private void limitViewport() {
    this.graphComponent.updateContentRect();
    ViewportLimiter limiter = this.graphComponent.getViewportLimiter();
    limiter.setHonoringBothDimensionsEnabled(false);
    updateViewportLimit(limiter);
  }

  /**
   * Restricts the explorable region to graph bounds for large graphs
   * and the graph component bounds for small graphs.
   */
  void updateViewportLimit( ViewportLimiter limiter ) {
    // determine the graph bounds 
    RectD limit = graphComponent.getContentRect().getEnlarged(20);

    // always allow zoom factor 1
    // i.e. if the graph bounds are smaller than the component bounds of
    // the graph component, then increase the bounds limit to the component
    // bounds
    int vw = graphComponent.getWidth();
    int vh = graphComponent.getHeight();
    if (limit.width < vw && limit.height < vh) {
      double dx = (vw - limit.width) * 0.5;
      double dy = (vh - limit.height) * 0.5;
      limit = new RectD(limit.x - dx, limit.y - dy, vw, vh);
    }

    limiter.setBounds(limit);
  }

  /**
   * Adds superior/subordinates relations to the employees of the graph.
   */
  private void initializeEmployeeHierarchy() {
    // first look for the root of the graph which should be the ceo.
    IGraph graph = graphComponent.getGraph();
    INode root = graph.getNodes().stream().filter(node -> graph.inDegree(node) == 0).findFirst().get();
    // then recursively add parents/subordinate relationships by descending the tree.
    initializeEmployeeHierarchy(root);

  }

  /**
   * Adds all nodes at the opposite side of all outgoing edges as "subordinates" to the given node and the node itself as
   * "superior" to the subordinate nodes and then calls the method recursively on this nodes to traverse the tree.
   */
  private void initializeEmployeeHierarchy(INode node) {
    IGraph graph = graphComponent.getGraph();
    Employee currentEmployee = (Employee) node.getTag();
    graph.outEdgesAt(node).forEach(edge -> {
      INode subordinateNode = edge.getTargetNode();
      Employee subordinate = (Employee) subordinateNode.getTag();
      subordinate.setSuperior(currentEmployee);
      currentEmployee.getSubordinates().add(subordinate);
      initializeEmployeeHierarchy(subordinateNode);
    });
  }

  /**
   * Returns the node representing the employee with the specified E-Mail address.
   */
  private INode getNodeForEMail(String email) {
    if (email == null) {
      return null;
    }
    return this.filteredGraphWrapper.getWrappedGraph().getNodes().stream()
        .filter(node -> email.equals(((Employee) node.getTag()).getEmail()))
        .findFirst().get();
  }

  /**
   * Focuses and zooms to the node representing the employee with the specified E-Mail address.
   */
  public void focusAndZoomToNodeWithEmail(String email) {
    INode nodeForEMail = this.getNodeForEMail(email);
    if (nodeForEMail != null) {
      this.focusAndZoomToNode(nodeForEMail);
    }
  }

  /**
   * Sets the given node as the current item of the graph component and zooms to it.
   */
  public void focusAndZoomToNode(INode node) {
    this.graphComponent.setCurrentItem(node);
    this.zoomToCurrentItem();
    this.graphComponent.requestFocus();
  }


  /**
   * Moves the ViewPort of the GraphComponent to its current item.
   */
  public void zoomToCurrentItem() {
    if (graphComponent.getCurrentItem() instanceof INode) {
      INode currentItem = (INode) graphComponent.getCurrentItem();
      // visible current item
      if (graphComponent.getGraph().contains(currentItem)) {
        ICommand.ZOOM_TO_CURRENT_ITEM.execute(null, graphComponent);
      } else {
        // see if it can be made visible
        IGraph fullGraph = filteredGraphWrapper.getWrappedGraph();
        if (fullGraph.contains(currentItem)) {
          // hide all nodes except the node to be displayed and all its descendants
          hiddenNodesSet.clear();
          fullGraph.getNodes().stream()
              .filter(testNode -> testNode != currentItem && fullGraph.inDegree(testNode) == 0)
              .forEach(testNode -> hideAllExcept(testNode, currentItem));

          // reset the layout to make the animation nicer
          filteredGraphWrapper.getNodes().forEach(node -> filteredGraphWrapper.setNodeCenter(node, PointD.ORIGIN));
          filteredGraphWrapper.getEdges().forEach(filteredGraphWrapper::clearBends);
          refreshLayout(-1, null);
        }
      }
    }
  }


  /**
   * The command that can be used by the buttons to show the parent node.
   * <p>
   *   This command requires the corresponding {@link INode} as the parameter.
   * </p>
   */
  public static final ICommand SHOW_PARENT = ICommand.createCommand("ShowParent");

  /**
   * The command that can be used by the buttons to hide the parent node.
   * <p>
   * This command requires the corresponding {@link INode} as the parameter.
   * </p>
   */
  public static final ICommand HIDE_PARENT = ICommand.createCommand("HideParent");

  /**
   * The command that can be used by the buttons to show the child nodes.
   * <p>
   *  This command requires the corresponding {@link INode} as the parameter.
   * </p>
   */
  public static final ICommand SHOW_CHILDREN = ICommand.createCommand("ShowChildren");

  /**
   * The command that can be used by the buttons to hide the child nodes.
   * <p>
   *  This command requires the corresponding {@link INode} as the parameter.
   * </p>
   */
  public static final ICommand HIDE_CHILDREN = ICommand.createCommand("HideChildren");

  /**
   * The command that can be used by the buttons to expand all collapsed nodes.
   */
  public static final ICommand SHOW_ALL = ICommand.createCommand("ShowAll");

  // ====== Command Binding Helpers ======

  /**
   * Determines whether the {@link #SHOW_CHILDREN} can be executed.
   */
  private boolean canExecuteShowChildren(ICommand command, Object parameter, Object source) {
    INode node;
    if (parameter == null && graphComponent.getCurrentItem() instanceof INode){
      node = (INode) graphComponent.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout && filteredGraphWrapper != null) {
      return filteredGraphWrapper.outDegree(node) != filteredGraphWrapper.getWrappedGraph().outDegree(node);
    } else {
      return false;
    }
  }

  /**
   * Handler for the {@link #SHOW_CHILDREN}
   */
  private boolean executeShowChildren(ICommand command, Object parameter, Object source) {
    INode node;
    if (parameter == null && graphComponent.getCurrentItem() instanceof INode){
      node = (INode) graphComponent.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout) {
      int count = hiddenNodesSet.size();
      IGraph fullGraph = filteredGraphWrapper.getWrappedGraph();
      for (IEdge childEdge : fullGraph.outEdgesAt(node)) {
        INode child = childEdge.getTargetNode();
        if (hiddenNodesSet.remove(child)) {
          fullGraph.setNodeCenter(child, node.getLayout().getCenter());
          fullGraph.clearBends(childEdge);
        }
      }
      refreshLayout(count, node);
      return true;
    }
    return false;
  }

  /**
   * Determines whether the {@link #SHOW_PARENT} can be executed.
   */
  private boolean canExecuteShowParent(ICommand command, Object parameter, Object source) {
    INode node;
    if (parameter == null && graphComponent.getCurrentItem() instanceof INode){
      node = (INode) graphComponent.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout && filteredGraphWrapper != null) {
      return filteredGraphWrapper.inDegree(node) == 0 && filteredGraphWrapper.getWrappedGraph().inDegree(node) > 0;
    } else {
      return false;
    }
  }

  /**
   * Handler for the {@link #SHOW_PARENT}
   */
  private boolean executeShowParent(ICommand command, Object parameter, Object source) {
    INode node;
    if (parameter == null && graphComponent.getCurrentItem() instanceof INode){
      node = (INode) graphComponent.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout) {
      int count = hiddenNodesSet.size();
      IGraph fullGraph = filteredGraphWrapper.getWrappedGraph();
      for (IEdge parentEdge : fullGraph.inEdgesAt(node)){
        INode parent = parentEdge.getSourceNode();
        if (hiddenNodesSet.remove(parent)) {
          fullGraph.setNodeCenter(parent, node.getLayout().getCenter());
          fullGraph.clearBends(parentEdge);
        }
      }
      refreshLayout(count, node);
      return true;
    }
    return false;
  }

  /**
   * Determines whether the {@link #HIDE_PARENT} can be executed.
   */
  private boolean canExecuteHideParent(ICommand command, Object parameter, Object source) {
    INode node;
    if (parameter == null && graphComponent.getCurrentItem() instanceof INode){
      node = (INode) graphComponent.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout && filteredGraphWrapper != null) {
      return filteredGraphWrapper.inDegree(node) > 0;
    } else {
      return false;
    }
  }

  /**
   * Handler for the {@link #HIDE_PARENT}
   */
  private boolean executeHideParent(ICommand command, Object parameter, Object source) {
    final INode node;
    if (parameter == null && graphComponent.getCurrentItem() instanceof INode){
      node = (INode) graphComponent.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout) {
      int count = hiddenNodesSet.size();

      // this is a root node - remove it and all children
      filteredGraphWrapper.getWrappedGraph().getNodes().stream()
          .filter(testNode -> testNode != node && filteredGraphWrapper.contains(testNode) && filteredGraphWrapper.inDegree(testNode) == 0)
          .forEach(testNode -> hideAllExcept(testNode, node));

      refreshLayout(count, node);
      return true;
    }
    return false;
  }

  /**
   * Determines whether the {@link #HIDE_CHILDREN} can be executed.
   */
  private boolean canExecuteHideChildren(ICommand command, Object parameter, Object source) {
    INode node;
    if (parameter == null && graphComponent.getCurrentItem() instanceof INode){
      node = (INode) graphComponent.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout && filteredGraphWrapper != null) {
      return filteredGraphWrapper.outDegree(node) > 0;
    } else {
      return false;
    }
  }

  /**
   * Handler for the {@link #HIDE_CHILDREN}
   */
  private boolean executeHideChildren(ICommand command, Object parameter, Object source) {
    INode node;
    if (parameter == null && graphComponent.getCurrentItem() instanceof INode){
      node = (INode) graphComponent.getCurrentItem();
    } else if (parameter instanceof INode) {
      node = (INode) parameter;
    } else {
      return false;
    }
    if (!doingLayout) {
      int count = hiddenNodesSet.size();
      for (INode child : filteredGraphWrapper.successors(INode.class, node)) {
        hideAllExcept(child, node);
      }
      refreshLayout(count, node);
      return true;
    }
    return false;
  }

  /**
   * Determines whether the {@link #SHOW_PARENT} can be executed.
   */
  private boolean canExecuteShowAll(ICommand command, Object parameter, Object source) {
    return filteredGraphWrapper != null && !hiddenNodesSet.isEmpty() && !doingLayout;
  }

  /**
   * Handler for the {@link #SHOW_ALL}
   */
  public boolean executeShowAll(ICommand command, Object parameter, Object source) {
    if (!doingLayout) {
      hiddenNodesSet.clear();
      INode node;
      if (parameter == null && graphComponent.getCurrentItem() instanceof INode){
        node = (INode) graphComponent.getCurrentItem();
      } else if (parameter instanceof INode) {
        node = (INode) parameter;
      } else {
        return false;
      }
      refreshLayout(-1, node);
      return true;
    }
    return false;
  }

  /**
   * Hides all nodes and its descendants except for the given node.
   */
  private void hideAllExcept(INode nodeToHide, INode exceptNode) {
    hiddenNodesSet.add(nodeToHide);
    Iterable<INode> successors = filteredGraphWrapper.getWrappedGraph().successors(INode.class, nodeToHide);
    for (INode child : successors) {
      if (exceptNode != child) {
        hideAllExcept(child, exceptNode);
      }
    }
  }

  /**
   * Refreshes the layout after children or parent nodes have been added or removed.
   */
  private void refreshLayout(int count, INode fixedNode) {
    if (doingLayout) {
      return;
    }

    if (count != hiddenNodesSet.size()) {
      // tell our filter to refresh the graph
      filteredGraphWrapper.nodePredicateChanged();
      // the commands CanExecute state might have changed - suggest a re-query.
      ICommand.invalidateRequerySuggested();

      // use the TreeLayout to calculate a layout for the org-chart
      TreeLayout treeLayout = new TreeLayout();

      // provide additional data to configure the TreeLayout
      TreeLayoutData treeLayoutData = new TreeLayoutData();
      // specify a node placer for each node
      treeLayoutData.setNodePlacers(this::getNodePlacer);
      // specify for each node whether it represents an assistant or not
      treeLayoutData.setAssistantNodes(this::isAssistant);

      // use the FixNodeLayoutStage to fix the position of the upper left
      // corner of the fixedNode
      FixNodeLayoutStage layoutStage = new FixNodeLayoutStage(treeLayout);
      treeLayout.appendStage(layoutStage);

      // provide additional data to configure the FixNodeLayoutStage
      FixNodeLayoutData fixNodeLayoutData = new FixNodeLayoutData();
      // specify the fixedNode whose upper left corner position should be fixed during layout
      fixNodeLayoutData.setFixedNodes(fixedNode);

      // run the layout algorithm and animate the result
      LayoutExecutor executor = new LayoutExecutor(graphComponent, layoutStage);
      executor.setViewportAnimationEnabled(fixedNode == null);
      executor.setEasedAnimationEnabled(true);
      executor.setRunningInThread(true);
      executor.setContentRectUpdatingEnabled(true);
      executor.setDuration(Duration.ofMillis(500));
      executor.setLayoutData(new CompositeLayoutData(treeLayoutData, fixNodeLayoutData));
      // add hook for cleanup
      executor.addLayoutFinishedListener((source, args) -> {
        doingLayout = false;

        // update viewport limiter to use the new content rect
        updateViewportLimit(graphComponent.getViewportLimiter());
      });

      doingLayout = true;
      executor.start();
    }
  }

  private static final INodePlacer RIGHT_HANGING_NODE_PLACER = new DefaultNodePlacer(ChildPlacement.VERTICAL_TO_RIGHT, RootAlignment.LEADING_ON_BUS, RoutingStyle.FORK_AT_ROOT, 30, 30);
  private static final INodePlacer LEFT_HANGING_NODE_PLACER = new DefaultNodePlacer(ChildPlacement.VERTICAL_TO_LEFT, RootAlignment.LEADING_ON_BUS, RoutingStyle.FORK_AT_ROOT, 30, 30);
  private static final INodePlacer BOTH_HANGING_NODE_PLACER = new LeftRightNodePlacer();
  private static final INodePlacer DEFAULT_NODE_PLACER = new DefaultNodePlacer(ChildPlacement.HORIZONTAL_DOWNWARD, RootAlignment.MEDIAN, 30, 30);

  static {
    ((LeftRightNodePlacer)BOTH_HANGING_NODE_PLACER).setLastOnBottomPlacementEnabled(false);
  }


  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new OrgChartDemo().start("Organization Chart Demo - yFiles for Java (Swing)");
    });
  }
}
