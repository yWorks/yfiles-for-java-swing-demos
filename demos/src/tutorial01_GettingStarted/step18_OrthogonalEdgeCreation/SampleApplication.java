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
package tutorial01_GettingStarted.step18_OrthogonalEdgeCreation;

import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.InsideOutsidePortLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.graph.labelmodels.EdgeSegmentLabelModel;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.view.GridVisualCreator;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.GridSnapTypes;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.GridConstraintProvider;

import com.yworks.yfiles.view.input.CommandAction;
import com.yworks.yfiles.view.Pen;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * <h1>Step 18: Orthogonal Edge Creation.</h1>
 * Enable orthogonal edge creation.
 * <p>
 * Please see the file help.html for more details.
 * </p>
 */
public class SampleApplication {
  private GraphComponent graphComponent;
  private FoldingManager manager;
  private GraphSnapContext snapContext;
  private boolean snappingEnabled;

  // visualizes the grid
  private GridVisualCreator grid;
  private boolean gridVisible;
  private GridSnapTypes gridSnapType;

  ///////////////////////////////////////////////////////
  //////////// YFILES STUFF /////////////////////////////
  ///////////////////////////////////////////////////////

  /**
   * Initializes the application after its user interface has been built up.
   */
  private void initialize() {
    // Specifies the default style for group nodes.
    configureGroupNodeStyles();

    // Customizes the provided ports that an edge can connect to when a user interacts with edges and their endpoints.
    // Note that this has to be done on the backing graph since we have to change some settings that are structurally
    // important at this stage (before folding is enabled, this works like in the previous demos) if folding was
    // enabled, here already, the below function would have to retrieve the IFoldedGraph first and obtain the master
    // backing graph from it to perform the customization on it.
    customizePortHandling();

    // Enables all kinds of interaction with a graph and its graph elements. In particular, this includes editing the
    // graph, i.e., creation and deletion of graph elements.
    // Note that this step has been moved before enableDataBinding, because one approach for setting up the custom data
    // depends on the input mode being already initialized. Creating the input mode does not depend on one of the other
    // setup steps.
    configureInteraction();

    // Initializes interactive snapping of graph elements to other graph elements during a drag-like operation.
    initializeSnapping();

    // Initializes support for interactive snapping of graph elements to a grid.
    initializeGrid();

    //////// New in this sample /////////////////////////
    // Enables support for manual creation of orthogonal edge paths and for maintaining orthogonal edge paths during
    // manual editing operations as well.
    setOrthogonalEdgeEditingEnabled(true);
    /////////////////////////////////////////////////////

    // Enables the ability to collapse and expand group nodes. Collapsing a group node hides all of its children, while
    // expanding the group node makes them visible again.
    enableFolding();

    // Enables file operations on the graph component to be able to interactively save or load a graph.
    enableGraphMLIO();

    // Specifies the default label model parameters for node and edge label. Label model parameters control the actual
    // label placement, as well as the available placement candidates when moving the label interactively.
    setDefaultLabelParameters();

    // Specifies a default style for each type of graph element. These styles are applied to new graph elements if no
    // style is explicitly specified during element creation.
    setDefaultStyles();

    // Creates a sample graph and introduces all important graph elements present in yFiles: nodes, edges, bends, ports
    // and labels.
    populateGraph();

    // Undo and redo are provided by the graph out-of-the-box, but have to be enabled before they can be used.
    // This needs to be done on the backing graph, too - so we do have to change this method.
    enableUndo();

    // Updates the content rectangle that encloses the graph and adjust the zoom level to show the whole graph in the
    // view.
    updateViewPort();
  }

  //////// New in this sample ///////////////////////////
  /**
   * Enables support for manual creation of orthogonal edge paths and for maintaining orthogonal edge paths during
   * manual editing operations as well.
   */
  void setOrthogonalEdgeEditingEnabled( boolean enabled ) {
    IInputMode inputMode = graphComponent.getInputMode();
    if (inputMode instanceof GraphEditorInputMode) {
      if (enabled) {
        OrthogonalEdgeEditingContext context = new OrthogonalEdgeEditingContext();
        context.setEnabled(true);
        ((GraphEditorInputMode) inputMode).setOrthogonalEdgeEditingContext(context);
      } else {
        ((GraphEditorInputMode) inputMode).setOrthogonalEdgeEditingContext(null);
      }
    }
  }
  ///////////////////////////////////////////////////////

  /**
   * Initializes support for interactive snapping of graph elements to a grid.
   */
  private void initializeGrid() {
    // initializes GridInfo which holds the basic information about the grid
    // sets horizontal and vertical space between grid lines
    GridInfo gridInfo = new GridInfo();
    gridInfo.setHorizontalSpacing(50);
    gridInfo.setVerticalSpacing(50);

    // creates grid visualization and adds it to GraphComponent
    grid = new GridVisualCreator(gridInfo);
    graphComponent.getBackgroundGroup().addChild(grid, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);

    // sets constraint provider to make nodes and bends snap to grid
    snapContext.setNodeGridConstraintProvider(new GridConstraintProvider<>(gridInfo));
    snapContext.setBendGridConstraintProvider(new GridConstraintProvider<>(gridInfo));

    setGridVisible(gridVisible);
    setGridSnapType(gridSnapType);
  }

  /**
   * Returns whether the grid is visible or not.
   */
  public boolean isGridVisible() {
    return gridVisible;
  }

  /**
   * Specifies whether the grid is visible or not.
   */
  public void setGridVisible(boolean visible) {
    gridVisible = visible;
    // toggles visibility
    if (grid != null) {
      grid.setVisible(gridVisible);
    }
    // ...and functionality
    if (snapContext != null) {
      snapContext.setGridSnapType(gridVisible ? getGridSnapType() : GridSnapTypes.NONE);
    }
    // trigger a repaint
    graphComponent.repaint();
  }

  /**
   * Returns the current used {@link GridSnapTypes}.
   */
  public GridSnapTypes getGridSnapType() {
    return gridSnapType;
  }

  /**
   * Sets the {@link GridSnapTypes} to use.
   */
  public void setGridSnapType(GridSnapTypes type) {
    gridSnapType = type;
    if (isGridVisible() && snapContext != null) {
      snapContext.setGridSnapType(gridSnapType);
    }
  }

  /**
   * Initializes support for interactive snapping of graph elements.
   */
  private void initializeSnapping() {
    // Initialize SnapContext
    GraphEditorInputMode geim = (GraphEditorInputMode) graphComponent.getInputMode();
    if (geim != null) {
      snapContext = new GraphSnapContext();
      geim.setSnapContext(snapContext);
      snapContext.setEnabled(snappingEnabled);
    }
  }

  /**
   * Enables folding. Change the GraphComponent's graph to a folding view
   * that provides the actual collapse/expand state.
   */
  private void enableFolding() {
    // create the folding manager
    manager = new FoldingManager(getGraph());
    // replace the displayed graph with a folding view
    graphComponent.setGraph(manager.createFoldingView().getGraph());
    wrapGroupNodeStyles();
  }

  /**
   * Changes the default style for group nodes.
   * <p>We use {@link com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator} to wrap the
   * {@link com.yworks.yfiles.graph.styles.PanelNodeStyle} from the last demo, since we want to have nice
   * +/- buttons for collapse/expand. Note that if you haven't defined
   * a custom group node style, you don't have to do anything at all, since
   * {@link FoldingManager} already
   * provides such a decorated group node style by default.</p>
   */
  private void wrapGroupNodeStyles() {
    IFoldingView foldingView = getGraph().getFoldingView();
    if (foldingView != null) {
      //Wrap the style with CollapsibleNodeStyleDecorator
      INodeDefaults groupNodeDefaults = foldingView.getGraph().getGroupNodeDefaults();
      groupNodeDefaults.setStyle(new CollapsibleNodeStyleDecorator(groupNodeDefaults.getStyle()));
    }
  }

  /**
   * Configures the default style for group nodes.
   */
  private void configureGroupNodeStyles() {
    IGraph graph = getGraph();

    // PanelNodeStyle is a style especially suited to group nodes
    // Creates a panel with a light blue background
    Color groupNodeColor = new Color(214, 229, 248);
    PanelNodeStyle panelNodeStyle = new PanelNodeStyle();
    panelNodeStyle.setColor(groupNodeColor);
    // Specifies insets that provide space for a label at the top
    panelNodeStyle.setInsets(new InsetsD(23, 5, 5, 5));
    panelNodeStyle.setLabelInsetsColor(groupNodeColor);
    graph.getGroupNodeDefaults().setStyle(panelNodeStyle);

    // Sets a label style with right-aligned text
    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setTextAlignment(TextAlignment.RIGHT);
    graph.getGroupNodeDefaults().getLabelDefaults().setStyle(defaultLabelStyle);

    // Places the label at the top inside of the panel.
    // For PanelNodeStyle, InteriorStretchLabelModel is usually the most appropriate label model
    graph.getGroupNodeDefaults().getLabelDefaults().setLayoutParameter(InteriorStretchLabelModel.NORTH);
  }

  /**
   * Creates a group node programmatically.
   * Creates a couple of nodes and puts them into a group node.
   */
  private INode createGroupNode(INode... childNodes) {
    IGraph graph = getGraph();

    // Creates a group node that encloses the given child nodes
    INode groupNode = graph.groupNodes(childNodes);

    // Creates a label for the group node
    graph.addLabel(groupNode, "Group Node");

    // Adjusts the bounds of the group nodes
    graph.adjustGroupNodeLayout(groupNode);
    return groupNode;
  }

  /**
   * Configures custom port handling with the help of {@link com.yworks.yfiles.graph.ILookup}.
   * <p>
   * When a user interacts with edges and their endpoints,
   * <code>node.lookup(IPortCandidateProvider.class)</code>
   * is called for the nodes in that graph,
   * and the framework returns the implementation of IPortCandidateProvider which
   * supplies the list of available ports.
   *
   * Instead of the default, we'll register a custom lookup for type IPortCandidateProvider.
   *
   * Note: we'll update this method in a future tutorial step to work with folding.
   * </p>
   */
  private void customizePortHandling() {
    IGraph graph = getGraph();
    // We don't want to remove "empty ports", since we want that our port candidate provider
    // can optionally return them too, even if they are unoccupied.
    graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);

    // Register a custom implementation that overrides
    // the default one present in the lookup for nodes
    // for some types (in this case, for type IPortCandidateProvider)

    // The net effect is that instead of the default port candidates
    // present for each node, a different set of port candidates will be returned
    // and used, e.g. during interactive edge creation.

    // To modify the existing lookup for a graph element, typically it
    // is decorated with the help of the getDecorator() method on IGraph,
    // which allows to dynamically insert custom implementations for the specified types.

    // Doing this can be seen as dynamically subclassing
    // the class in question (the INode implementation in this case), but only
    // for the node instances that live in the graph in question and then
    // overriding just their lookup(type) method. The only difference to traditional
    // subclassing is that you get the "this" passed in as a parameter.
    // Doing this more than once is like subclassing more and more, so the order matters.

    // Once node.lookup(IPortCandidateProvider.class) is called for the nodes in that graph,
    // the framework will delegate to the factory method below and finally yield the result.
    graph.getDecorator().getNodeDecorator().getPortCandidateProviderDecorator().setFactory(
        node -> IPortCandidateProvider.combine(
            IPortCandidateProvider.fromExistingPorts(node),   // provides already existing port candidates of the node
            IPortCandidateProvider.fromNodeCenter(node),      // provides a port candidate at the center of the node
            IPortCandidateProvider.fromShapeGeometry(node)    // provides a port candidate at the center of each straight line segment
        ));
  }

  /**
   * Enables GraphML I/O command bindings.
   */
  private void enableGraphMLIO() {
    graphComponent.setFileIOEnabled(true);
  }

  /**
   * Enables undo functionality.
   * <p>Undo functionality is disabled by default.</p>
   */
  private void enableUndo() {
    manager.getMasterGraph().setUndoEngineEnabled(true);
  }

  /**
   * Configures basic interaction.
   * <p>
   * Interaction is handled by so called input modes. {@link com.yworks.yfiles.view.input.GraphEditorInputMode} is the main
   * input mode that already provides a large number of graph interaction possibilities, such as moving, deleting,
   * creating, resizing graph elements. Note that to create or edit a label, just press F2. Also, try to move a label
   * around and see what happens.
   * </p>
   */
  private void configureInteraction() {
    // Creates a new GraphEditorInputMode instance and registers it as the main
    // input mode for the graphComponent
    GraphEditorInputMode geim = new GraphEditorInputMode();
    // Enable grouping operations such as grouping selected nodes moving nodes
    // into group nodes
    geim.setGroupingOperationsAllowed(true);

    // Add a label for interactive created group nodes
    geim.addNodeCreatedListener((source, args) -> {
      INode node = args.getItem();
      if (getGraph().isGroupNode(node)) {
        getGraph().addLabel(node, "Group Node");
      }
    });

    graphComponent.setInputMode(geim);
  }

  /**
   * Sets up default label model parameters for graph elements.
   * Label model parameters control the actual label placement, as well as the available
   * placement candidates when moving the label interactively.
   */
  private void setDefaultLabelParameters() {
    IGraph graph = getGraph();
    // For node labels, the default is a label position at the node center
    // Let's keep the default.  Here is how to set it manually
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(InteriorLabelModel.CENTER);

    // For edge labels, the default is a label that is rotated to match the associated edge segment
    // We'll start by creating a model that is similar to the default:
    EdgeSegmentLabelModel edgeSegmentLabelModel = new EdgeSegmentLabelModel();
    // However, by default, the rotated label is centered on the edge path.
    // Let's move the label off of the path:
    edgeSegmentLabelModel.setDistance(10);
    // Finally, we can set this label model as the default for edge labels using a location at the center of the first segment
    ILabelModelParameter labelModelParameter = edgeSegmentLabelModel.createParameterFromSource(0, 0.5, EdgeSides.RIGHT_OF_EDGE);
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(labelModelParameter);

    // For port labels, the default is a label that is placed outside the owner node
    graph.getNodeDefaults().getPortDefaults().getLabelDefaults().setLayoutParameter(new InsideOutsidePortLabelModel().createOutsideParameter());
  }

  /**
   * Sets up default styles for graph elements.
   * <p>
   * Default styles apply only to elements created after the default style has been set,
   * so typically, you'd set these as early as possible in your application.
   * </p>
   */
  private void setDefaultStyles() {
    IGraph graph = getGraph();
    // Sets the default style for nodes
    // Creates a nice ShinyPlateNodeStyle instance, using an orange color.
    // Sets this style as the default for all nodes that don't have another
    // style assigned explicitly
    ShinyPlateNodeStyle defaultNodeStyle = new ShinyPlateNodeStyle();
    defaultNodeStyle.setPaint(Color.ORANGE);
    graph.getNodeDefaults().setStyle(defaultNodeStyle);

    // Sets the default style for edges:
    // Creates an edge style that will apply a gray pen with thickness 1
    // to the entire line using PolyLineEdgeStyle,
    // which draws a polyline determined by the edge's control points (bends)
    PolylineEdgeStyle defaultEdgeStyle = new PolylineEdgeStyle();
    defaultEdgeStyle.setPen(Pen.getGray());

    // Sets the source and target arrows on the edge style instance
    // (Actually: no source arrow)
    // Note that IEdgeStyle itself does not have these properties
    // Also note that by default there are no arrows
    defaultEdgeStyle.setTargetArrow(IArrow.DEFAULT);

    // Sets the defined edge style as the default for all edges that don't have
    // another style assigned explicitly:
    graph.getEdgeDefaults().setStyle(defaultEdgeStyle);

    // Sets the default style for labels
    // Creates a label style with the label text color set to dark red
    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
    defaultLabelStyle.setTextPaint(Colors.DARK_RED);

    // Sets the defined style as the default for both edge and node labels:
    graph.getEdgeDefaults().getLabelDefaults().setStyle(defaultLabelStyle);
    graph.getNodeDefaults().getLabelDefaults().setStyle(defaultLabelStyle);

    // Sets the default node size explicitly to 40x40
    graph.getNodeDefaults().setSize(new SizeD(40, 40));
  }

  /**
   * Creates a sample graph and introduces all important graph elements present in yFiles. Additionally, this method
   * specifies the label placement for some specific labels.
   */
  private void populateGraph() {
    IGraph graph = getGraph();

    // Creates two nodes with the default node size
    // The location is specified for the center
    INode node1 = graph.createNode(new PointD(50, 50));
    INode node2 = graph.createNode(new PointD(150, 50));
    // Creates a third node with a different size of 80x40
    // In this case, the location of (360,380) describes the upper left
    // corner of the node bounds
    INode node3 = graph.createNode(new RectD(360, 380, 80, 40));

    // Creates some edges between the nodes
    IEdge edge1 = graph.createEdge(node1, node2);
    IEdge edge2 = graph.createEdge(node2, node3);

    // Creates the first bend for edge2 at (400, 50)
    IBend bend1 = graph.addBend(edge2, new PointD(400, 50));

    // Actually, edges connect "ports", not nodes directly.
    // If necessary, you can manually create ports at nodes
    // and let the edges connect to these.
    // Creates a port in the center of the node layout
    IPort port1AtNode1 = graph.addPort(node1, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);

    // Creates a port at the middle of the left border
    // Note to use absolute locations in world coordinates when placing ports using PointD.
    // The method obtains a model parameter that best matches the given port location.
    IPort port1AtNode3 = graph.addPort(node3, new PointD(node3.getLayout().getX(), node3.getLayout().getCenter().getY()));


    ///////////////// New in this Sample /////////////////
    // NOTE: the non-orthogonal edge is edited as if orthogonal edge editing was switched off.
    // Once one or more bends are added in a way that the edge becomes orthogonal
    // orthogonal edge editing is enabled for this edge, too.
    // Also note: if some segments of the edge are orthogonal and some are not
    // orthogonal edge editing is enabled for the orthogonal segments.
    /////////////////////////////////////////////////////

    IEdge edgeAtPorts = graph.createEdge(port1AtNode1, port1AtNode3);

    // Adds labels to several graph elements
    graph.addLabel(node1, "Node 1");
    graph.addLabel(node2, "Node 2");
    ILabel n3Label = graph.addLabel(node3, "Node 3");
    graph.addLabel(edgeAtPorts, "Not Orthogonal");
    graph.addLabel(port1AtNode3, "Port at Node");

    // Add some more elements to have a larger graph to edit
    INode n4 = graph.createNode(new PointD(50, -50));
    graph.addLabel(n4, "Node 4");
    INode n5 = graph.createNode(new PointD(50, -150));
    graph.addLabel(n5, "Node 5");
    INode n6 = graph.createNode(new PointD(-50, -50));
    graph.addLabel(n6, "Node 6");
    INode n7 = graph.createNode(new PointD(-50, -150));
    graph.addLabel(n7, "Node 7");
    INode n8 = graph.createNode(new PointD(150, -50));
    graph.addLabel(n8, "Node 8");

    graph.createEdge(n4, node1);
    graph.createEdge(n5, n4);
    graph.createEdge(n7, n6);
    IEdge e6_1 = graph.createEdge(n6, node1);
    graph.addBend(e6_1, new PointD(-50, 50), 0);

    // Creates a group node programmatically which groups the child nodes n4, n5, and n8
    INode groupNode = createGroupNode(n4, n5, n8);
    // creates an edge between the group node and node 2
    IEdge eg_2 = graph.createEdge(groupNode, node2);
    graph.addBend(eg_2, new PointD(100, 0), 0);
    graph.addBend(eg_2, new PointD(150, 0), 1);

    // Override default label placement
    // For our "special" label, we use a model that describes discrete positions
    // outside the node bounds
    ExteriorLabelModel exteriorLabelModel = new ExteriorLabelModel();

    // We use some extra insets from the label to the node bounds
    exteriorLabelModel.setInsets(new InsetsD(5));

    // We assign this label a specific symbolic position out of the eight possible
    // external locations valid for ExteriorLabelModel
    graph.setLabelLayoutParameter(n3Label, exteriorLabelModel.createParameter(ExteriorLabelModel.Position.SOUTH));
  }

  /**
   * Updates the content rectangle to encompass all existing graph elements.
   * <p>
   * If you create your graph elements programmatically, the content rectangle
   * (i.e. the rectangle in <b>world coordinates</b> that encloses the graph)
   * is <b>not</b> updated automatically to enclose these elements. Typically,
   * this manifests in wrong/missing scrollbars, incorrect
   * {@link com.yworks.yfiles.view.GraphOverviewComponent} behavior and the like.
   * </p>
   * <p>
   * This method demonstrates several ways to update the content rectangle, with
   * or without adjusting the zoom level to show the whole graph in the view.
   * </p>
   * <p>
   * Note that updating the content rectangle does not change the current
   * view port (i.e. the world coordinate rectangle that corresponds to the
   * currently visible area in view coordinates).
   * </p>
   * <p>
   * Try to uncomment the example code in this method and observe the different
   * effects.
   * </p>
   * <p>
   * The following steps in this tutorial assume you just called
   * <code>graphComponent.fitGraphBounds();</code> in this method.
   * </p>
   */
  void updateViewPort() {
    // Uncomment the following line to update the content rectangle
    // to include all graph elements
    // This should result in correct scrolling behaviour:

    // graphComponent.updateContentRect();

    // Additionally, we can also set the zoom level so that the
    // content rectangle fits exactly into the view port area:
    // Uncomment this line in addition to UpdateContentRect:
    // Note that this changes the zoom level (i.e. the graph elements will look smaller)

    // graphComponent.fitContent();

    // The sequence above is equivalent to just calling:
    graphComponent.fitGraphBounds();
  }

  /**
   * Convenience method to retrieve the graph.
   */
  public IGraph getGraph() {
    return graphComponent.getGraph();
  }

  ///////////////////////////////////////////////////////
  //////////// GUI STUFF ////////////////////////////////
  ///////////////////////////////////////////////////////

  /**
   * Initializes a new <code>SampleApplication</code> instance. Creates a {@link javax.swing.JFrame} with a {@link
   * com.yworks.yfiles.view.GraphComponent} in the center, a help pane on the right, a {@link javax.swing.JToolBar} on
   * the top and a {@link javax.swing.JMenuBar}.
   * @param title The title of the application.
   */
  public SampleApplication(String title) {
    JFrame frame = createFrame(title);

    // Create an instance of GraphComponent, which is one of the most important classes of yFiles. It can hold, display,
    // and edit an IGraph instance and provides access to the Selection instance.
    // In addition it offers convenience methods for exporting the graph to and importing it from GraphML.
    graphComponent = new GraphComponent();
    frame.add(graphComponent, BorderLayout.CENTER);
    frame.add(createHelpPane(), BorderLayout.EAST);
    frame.add(createToolBar(), BorderLayout.NORTH);
    frame.setJMenuBar(createMenuBar());
    frame.setVisible(true);
  }

  /**
   * Creates a {@link javax.swing.JFrame} with the given title.
   */
  private JFrame createFrame(String title) {
    JFrame frame = new JFrame(title);
    frame.setIconImages(Arrays.asList(
        createIcon("logo_16.png").getImage(),
        createIcon("logo_24.png").getImage(),
        createIcon("logo_32.png").getImage(),
        createIcon("logo_48.png").getImage(),
        createIcon("logo_64.png").getImage(),
        createIcon("logo_128.png").getImage()));
    frame.setSize(1365, 768);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    return frame;
  }

  /**
   * Creates a help pane with a help text which is defined in an html file named <code>help.html</code>.
   * This file resides in the same directory as the application class.
   */
  private JComponent createHelpPane() {
    JEditorPane editorPane = new JEditorPane();
    editorPane.setEditable(false);
    try {
      editorPane.setPage(getClass().getResource("help.html"));
    } catch (IOException e) {
      editorPane.setContentType("text/plain");
      editorPane.setText("Could not resolve help text. Please ensure that your build process or IDE adds the " +
           "help.html file to the class path.");
    }
    // make links clickable
    editorPane.addHyperlinkListener(e -> {
      if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        if(Desktop.isDesktopSupported()) {
          try {
            Desktop.getDesktop().browse(e.getURL().toURI());
          } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
          }
        }
      }
    });

    JScrollPane scrollPane = new JScrollPane(editorPane);
    scrollPane.setPreferredSize(new Dimension(340, 750));
    return scrollPane;
  }

  /**
   * Creates a {@link javax.swing.JToolBar} with components to change the visualization of the graph.
   */
  private JToolBar createToolBar() {
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.add(createCommandButtonAction("plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("zoom-original2-16.png", ICommand.ZOOM, 1, graphComponent));
    toolBar.add(createCommandButtonAction("minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("fit2-16.png", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("cut-16.png", ICommand.CUT, null, graphComponent));
    toolBar.add(createCommandButtonAction("copy-16.png", ICommand.COPY, null, graphComponent));
    toolBar.add(createCommandButtonAction("paste-16.png", ICommand.PASTE, null, graphComponent));
    toolBar.add(createCommandButtonAction("delete3-16.png", ICommand.DELETE, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("redo-16.png", ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();

    //////// New in this sample /////////////////////////
    toolBar.add(createOrthogonalEdgeCreationToggleButton());
    /////////////////////////////////////////////////////

    toolBar.add(createSnapToggleButton());
    toolBar.add(createGridToggleButton());
    toolBar.add(createGridSnapTypeComboBox());
    return toolBar;
  }

  /**
   * Creates an {@link javax.swing.Action} for the given command used for buttons in a toolbar.
   * @param icon      The icon to show.
   * @param command   The command to execute.
   * @param parameter The parameter for the execution of the command.
   * @param target    The target to execute the command on.
   * @return an {@link javax.swing.Action} for the given command.
   */
  private Action createCommandButtonAction(String icon, ICommand command, Object parameter, JComponent target) {
    Action action = new CommandAction(command, parameter, target);
    action.putValue(Action.SHORT_DESCRIPTION, command.getName());
    action.putValue(Action.SMALL_ICON, createIcon(icon));
    return action;
  }

  //////// New in this sample /////////////////////////
  /**
   * Creates an {@link javax.swing.JToggleButton} to toggle the orthogonal edge creation feature.
   */
  private JToggleButton createOrthogonalEdgeCreationToggleButton() {
    JToggleButton button = new JToggleButton();
    button.setToolTipText("Enable orthogonal edge editing mode");
    button.setIcon(createIcon("orthogonal-editing-16.png"));
    button.setSelected(true);
    button.addActionListener(e -> setOrthogonalEdgeEditingEnabled(button.isSelected()));
    return button;
  }
  /////////////////////////////////////////////////////

  /**
   * Creates an {@link javax.swing.JToggleButton} to toggle the snapping feature.
   */
  private JToggleButton createSnapToggleButton() {
    snappingEnabled = false;
    JToggleButton button = new JToggleButton();
    button.setToolTipText("Show snap lines for dragged items");
    button.setIcon(createIcon("snap-16.png"));
    button.setSelected(snappingEnabled);
    button.addActionListener(e -> snapContext.setEnabled(button.isSelected()));
    return button;
  }

  /**
   * Creates an {@link javax.swing.JToggleButton} to toggle the grid feature.
   */
  private JToggleButton createGridToggleButton() {
    gridVisible = false;
    JToggleButton button = new JToggleButton();
    button.setToolTipText("Show grid");
    button.setIcon(createIcon("grid-16.png"));
    button.setSelected(gridVisible);
    button.addActionListener(e -> setGridVisible(button.isSelected()));
    return button;
  }

  /**
   * Creates an {@link javax.swing.JComboBox} to select the type of grid snapping.
   */
  private JComboBox createGridSnapTypeComboBox() {
    String[] titles = {"None", "Horizontal Lines", "Vertical Lines", "Lines", "Points", "All"};
    GridSnapTypes[] types = {GridSnapTypes.NONE, GridSnapTypes.HORIZONTAL_LINES, GridSnapTypes.VERTICAL_LINES,
        GridSnapTypes.LINES, GridSnapTypes.GRID_POINTS, GridSnapTypes.ALL};
    JComboBox<String> comboBox = new JComboBox<>(titles);
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setToolTipText("Select the grid snapping type");
    comboBox.setSelectedIndex(5);
    comboBox.addActionListener(e -> setGridSnapType(types[comboBox.getSelectedIndex()]));
    gridSnapType = types[5];
    return comboBox;
  }

  /**
   * Creates a {@link javax.swing.JMenuBar} with file and edit items.
   */
  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");
    fileMenu.add(createCommandMenuItemAction(ICommand.OPEN, null, graphComponent));
    fileMenu.add(createCommandMenuItemAction(ICommand.SAVE, null, graphComponent));
    fileMenu.add(createCommandMenuItemAction(ICommand.SAVE_AS, null, graphComponent));
    fileMenu.addSeparator();
    fileMenu.add(createExitAction());
    menuBar.add(fileMenu);

    JMenu editMenu = new JMenu("Edit");
    editMenu.add(createCommandMenuItemAction(ICommand.CUT, null, graphComponent));
    editMenu.add(createCommandMenuItemAction(ICommand.COPY, null, graphComponent));
    editMenu.add(createCommandMenuItemAction(ICommand.PASTE, null, graphComponent));
    editMenu.add(createCommandMenuItemAction(ICommand.DELETE, null, graphComponent));
    editMenu.addSeparator();
    editMenu.add(createCommandMenuItemAction(ICommand.UNDO, null, graphComponent));
    editMenu.add(createCommandMenuItemAction(ICommand.REDO, null, graphComponent));
    menuBar.add(editMenu);

    JMenu groupingMenu = new JMenu("Grouping");
    groupingMenu.add(createCommandMenuItemAction(ICommand.GROUP_SELECTION, null, graphComponent));
    groupingMenu.add(createCommandMenuItemAction(ICommand.UNGROUP_SELECTION, null, graphComponent));
    groupingMenu.addSeparator();
    groupingMenu.add(createCommandMenuItemAction(ICommand.EXPAND_GROUP, null, graphComponent));
    groupingMenu.add(createCommandMenuItemAction(ICommand.COLLAPSE_GROUP, null, graphComponent));
    groupingMenu.add(createCommandMenuItemAction(ICommand.ENTER_GROUP, null, graphComponent));
    groupingMenu.add(createCommandMenuItemAction(ICommand.EXIT_GROUP, null, graphComponent));
    menuBar.add(groupingMenu);

    return menuBar;
  }

  /**
   * Creates an {@link javax.swing.Action} for the given command used for menu items.
   * @param command   The command to execute.
   * @param parameter The parameter for the execution of the command.
   * @param target    The target to execute the command on.
   * @return an {@link javax.swing.Action} for the given command.
   */
  private Action createCommandMenuItemAction(ICommand command, Object parameter, JComponent target) {
    return new CommandAction(command, parameter, target);
  }

  /**
   * Creates an {@link javax.swing.Action} to exit the demo.
   */
  private Action createExitAction() {
    return new AbstractAction("Exit") {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    };
  }

  /**
   * Creates an {@link javax.swing.ImageIcon} from the specified file located in the resources folder.
   */
  static ImageIcon createIcon(String name) {
    return new ImageIcon(SampleApplication.class.getResource("/resources/" + name));
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> new SampleApplication("Step 18 - Orthogonal Edge Editing").initialize());
  }
}
