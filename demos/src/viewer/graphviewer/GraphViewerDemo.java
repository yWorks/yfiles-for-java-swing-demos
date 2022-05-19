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
package viewer.graphviewer;

import com.yworks.yfiles.graph.styles.ArcEdgeStyle;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.view.GraphOverviewComponent;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.view.EdgeStyleDecorationInstaller;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.view.Mouse2DEventArgs;
import com.yworks.yfiles.view.NodeStyleDecorationInstaller;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IMapperRegistry;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.StyleDecorationZoomPolicy;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.HoveredItemChangedEventArgs;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.ItemClickedEventArgs;
import com.yworks.yfiles.view.input.QueryItemToolTipEventArgs;
import com.yworks.yfiles.view.HighlightIndicatorManager;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.view.ModifierKeys;
import com.yworks.yfiles.view.Pen;

import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Display different graphs with additional information using a {@link com.yworks.yfiles.view.GraphComponent}.
 */
public class GraphViewerDemo extends AbstractDemo {

  /**
   * Another GraphComponent that displays a small overview on the left side of the application.
   */
  private GraphOverviewComponent overviewComponent;

  /**
   * The view for the properties of an employee in the panel on the left side of the application.
   */
  private PropertiesView propertiesView;

  /**
   * Maps the description defined in GraphML to the actual graph.
   * This is mainly necessary because graphs don't have tags, otherwise this information would
   * be stored there.
   */
  private Mapper<IGraph, String> graphDescriptionMapper;

  /**
   * Queries the mapper that maps description text defined in GraphML to a node.
   */
  private IMapper<INode, String> getDescriptionMapper() {
    return graphComponent.getGraph().getMapperRegistry().getMapper(INode.class, String.class, "Description");
  }

  /**
   * Queries the mapper that maps an url defined in GraphML to a node.
   */
  private IMapper<INode, String> getUrlMapper() {
    return graphComponent.getGraph().getMapperRegistry().getMapper(INode.class, String.class, "Url");
  }

  /**
   * Queries the mapper that maps tooltip text defined in GraphML to a node.
   */
  private IMapper<INode, String> getToolTipMapper() {
    return graphComponent.getGraph().getMapperRegistry().getMapper(INode.class, String.class, "ToolTip");
  }

  // some swing widgets that we use throughout the demo.
  private JComboBox<String> graphChooserBox;
  private JLabel graphDescription;

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
    Component comboBox = createComboBox();
    toolBar.add(new ShowGraph(false));
    toolBar.add(comboBox);
    toolBar.add(new ShowGraph(true));
    this.graphChooserBox.addActionListener(e -> readSampleGraph());
  }

  /**
   * Creates the JComboBox where the various graphs are selectable.
   */
  private Component createComboBox() {
    graphChooserBox = new JComboBox<>(new String[]{
        "computer-network",
        "activity-diagram",
        "movies",
        "family-tree",
        "hierarchy",
        "nesting",
        "social-network",
        "uml-diagram",
        "large-tree",
    });
    graphChooserBox.setMaximumSize(graphChooserBox.getPreferredSize());
    return graphChooserBox;
  }

  /**
   * Creates the panel on the left which contains the overview and a properties view for focused employees.
   */
  private Component createLeftPanel() {
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(createGraphDescriptionElement(), BorderLayout.NORTH);
    JPanel innerPanel = new JPanel(new BorderLayout());
    innerPanel.add(createGraphOverview(), BorderLayout.NORTH);
    propertiesView = new PropertiesView();
    innerPanel.add(propertiesView.getContentPane(), BorderLayout.CENTER);
    leftPanel.add(innerPanel, BorderLayout.CENTER);
    return leftPanel;
  }

  /**
   * Creates the swing element which displays the graph description.
   */
  private Component createGraphDescriptionElement() {
    JPanel graphDescriptionContainer = new JPanel();
    graphDescriptionContainer.setLayout(new GridLayout(1, 1));
    graphDescription = new JLabel();
    graphDescription.setFont(new Font("Dialog", Font.PLAIN, 12));
    Border line = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
    Border empty = new EmptyBorder(5, 5, 5, 5);
    graphDescription.setBorder(new CompoundBorder(line, empty));
    graphDescriptionContainer.add(graphDescription, BorderLayout.NORTH);
    graphDescriptionContainer.setBorder(BorderFactory.createTitledBorder("Graph Description"));
    return graphDescriptionContainer;
  }

  /**
   * Creates a GraphOverviewComponent for our demo.
   */
  private JPanel createGraphOverview(){
    JPanel graphOverviewContainer = new JPanel();
    overviewComponent = new GraphOverviewComponent(graphComponent);
    overviewComponent.setMinimumSize(new Dimension(250, 250));
    overviewComponent.setPreferredSize(new Dimension(250, 250));
    graphOverviewContainer.add(overviewComponent);
    graphOverviewContainer.setBorder(BorderFactory.createTitledBorder("Overview"));
    return graphOverviewContainer;
  }

  /**
   * Reads the currently selected GraphML from the graphChooserBox
   */
  private void readSampleGraph() {

    // first derive the file name
    URL graphML = getClass().getResource("resources/" + this.graphChooserBox.getSelectedItem() + ".graphml");

    // then load the graph
    try {
      graphComponent.importFromGraphML(graphML);

      // when done - fit the bounds
      this.graphComponent.fitGraphBounds();

      // and update the graph description pane
      String desc = this.graphDescriptionMapper.getValue(
          this.graphComponent.getGraph().getFoldingView().getManager().getMasterGraph());
      this.graphDescription.setText("<html><body width='200px'>" + (desc != null ? desc : ""));

      // the commands CanExecute state might have changed - suggest a re-query. mainly to update the enabled status of the previous / next buttons.
      ICommand.invalidateRequerySuggested();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the appearance and interaction within this demo.
   */
  public void initialize(){

    // first initialize the graph component
    this.initializeGraphComponent();

    // initialize the input mode
    this.initializeInputMode();
  }


  /**
   * Loads, arranges, and centers a sample graph in the graph component.
   */
  public void onVisible() {
    readSampleGraph();
  }

  /**
   * Configures the highlighting of nodes and edges, connects the overview with the main GraphComponent
   * and prepares the GraphMLIOHandler to load the GraphML for this demo.
   */
  private void initializeGraphComponent(){
    // we want to enable folding for loading and showing nested graphs
    this.enableFolding();

    // we want to create a non-default nice highlight styling
    // create semi transparent orange pen first
    Color orangeRed = Colors.ORANGE_RED;
    Pen orangePen = new Pen(new Color(orangeRed.getRed(), orangeRed.getGreen(), orangeRed.getBlue(), 220), 3);

    // now decorate the nodes and edges with custom highlight styles
    GraphDecorator decorator = this.graphComponent.getGraph().getDecorator();

    // nodes should be given a rectangular orange rectangle highlight shape
    ShapeNodeStyle highlightShape = new ShapeNodeStyle();
    highlightShape.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    highlightShape.setPen(orangePen);
    highlightShape.setPaint(null);

    NodeStyleDecorationInstaller nodeStyleHighlight = new NodeStyleDecorationInstaller();
    nodeStyleHighlight.setNodeStyle(highlightShape);
    // that should be slightly larger than the real node
    nodeStyleHighlight.setMargins(new InsetsD(5));
    // but have a fixed size in the view coordinates
    nodeStyleHighlight.setZoomPolicy(StyleDecorationZoomPolicy.VIEW_COORDINATES);

    // register it as the default implementation for all nodes
    decorator.getNodeDecorator().getHighlightDecorator().setImplementation(nodeStyleHighlight);

    // a similar style for the edges...
    decorator.getEdgeDecorator().getHighlightDecorator().setFactory(edge -> {
      // we cheat a little with transparent source / target arrows to make the highlight edge a little shorter than necessary.
      IArrow transparentArrow = new Arrow(ArrowType.NONE, null, 5, 1);
      // clone the edge's style so the highlight uses the same kind of style
      IEdgeStyle clonedStyle = (IEdgeStyle) edge.getStyle().clone();
      if (clonedStyle instanceof PolylineEdgeStyle) {
        PolylineEdgeStyle edgeStyle = (PolylineEdgeStyle) clonedStyle;
        edgeStyle.setPen(orangePen);
        edgeStyle.setSourceArrow(transparentArrow);
        edgeStyle.setTargetArrow(transparentArrow);
      } else if (clonedStyle instanceof ArcEdgeStyle) {
        ArcEdgeStyle edgeStyle = (ArcEdgeStyle) clonedStyle;
        edgeStyle.setPen(orangePen);
        edgeStyle.setSourceArrow(transparentArrow);
        edgeStyle.setTargetArrow(transparentArrow);
      }
      EdgeStyleDecorationInstaller edgeStyleHighlight = new EdgeStyleDecorationInstaller();
      edgeStyleHighlight.setEdgeStyle(clonedStyle);
      edgeStyleHighlight.setZoomPolicy(StyleDecorationZoomPolicy.VIEW_COORDINATES);
      return edgeStyleHighlight;
    });

    // connect the overview to the main component
    this.overviewComponent.setGraphComponent(this.graphComponent);

    // we register and create mappers for nodes and the graph to hold information about
    // the tooltips, descriptions, and associated urls
    IMapperRegistry masterRegistry = this.graphComponent.getGraph().getFoldingView().getManager().getMasterGraph().getMapperRegistry();

    masterRegistry.createWeakMapper(INode.class, String.class, "ToolTip");
    masterRegistry.createWeakMapper(INode.class, String.class, "Description");
    masterRegistry.createWeakMapper(INode.class, String.class, "Url");
    // we hold our own instance of the mapper for the description of the graphs
    this.graphDescriptionMapper = new Mapper<>();

    // create a custom IOHandler that will be used for all IO operations
    GraphMLIOHandler ioh = createGraphMLIOHandler();

    // we set the IO handler on the GraphComponent, so the GraphComponent's IO methods
    // will pick up our handler for use during serialization and deserialization.
    graphComponent.setGraphMLIOHandler(ioh);

    // whenever the currentItem property on the graph changes, we want to get notified...
    // show the properties of the focused nodes in the properties view on the left
    this.graphComponent.addCurrentItemChangedListener(this::onCurrentItemChanged);
  }

  /**
   * Enable folding - change the GraphComponents graph to a managed view
   * that provides the actual collapse/expand state.
   */
  private void enableFolding() {
    // create the manager
    FoldingManager manager = new FoldingManager();
    // replace the displayed graph with a managed view
    this.graphComponent.setGraph(manager.createFoldingView().getGraph());
  }

  /**
   * Creates and configures the GraphML parser.
   */
  private GraphMLIOHandler createGraphMLIOHandler() {
    GraphMLIOHandler ioHandler = new GraphMLIOHandler();
    // we also want to populate the mappers for "Description", "ToolTip", and "Url"
    ioHandler.addRegistryInputMapper(INode.class, String.class, "Description");
    ioHandler.addRegistryInputMapper(INode.class, String.class, "ToolTip");
    ioHandler.addRegistryInputMapper(INode.class, String.class, "Url");
    this.graphDescriptionMapper.clear();
    // as well as the description of the graph
    ioHandler.addInputMapper(IGraph.class, String.class, "GraphDescription", this.graphDescriptionMapper);
    return ioHandler;
  }

  /**
   * Configures a GraphViewerInputMode with the following interaction:
   * - tooltips on nodes and edges
   * - clicking on nodes
   * - focusing (via keyboard navigation) of nodes
   * - no selection
   * - no marquee
   * - collapsing/expanding of folders/groups
   */
  private void initializeInputMode() {
    // we have a viewer application, so we can use the GraphViewerInputMode
    GraphViewerInputMode graphViewerInputMode = new GraphViewerInputMode();
    graphViewerInputMode.setToolTipItems(GraphItemTypes.LABEL_OWNER);
    graphViewerInputMode.setClickableItems(GraphItemTypes.NODE);
    graphViewerInputMode.setFocusableItems(GraphItemTypes.NODE);
    graphViewerInputMode.setSelectableItems(GraphItemTypes.NONE);
    graphViewerInputMode.setMarqueeSelectableItems(GraphItemTypes.NONE);

    // we want to enable the user to collapse and expand groups interactively, even though we
    // are just a "viewer" application
    graphViewerInputMode.getNavigationInputMode().setCollapseGroupAllowed(true);
    graphViewerInputMode.getNavigationInputMode().setExpandGroupAllowed(true);
    // after expand/collapse/enter/exit operations - don't perform a fitContent operation to adjust
    // reachable area.
    graphViewerInputMode.getNavigationInputMode().setFittingContentAfterGroupActionsEnabled(false);
    // we don't have selection enabled and thus the commands should use the "currentItem"
    // property instead - this property is changed when clicking on items or navigating via
    // the keyboard.
    graphViewerInputMode.getNavigationInputMode().setUsingCurrentItemForCommandsEnabled(true);

    // we want to get reports of the mouse being hovered over nodes and edges
    // first enable queries
    graphViewerInputMode.getItemHoverInputMode().setEnabled(true);
    // set the items to be reported
    graphViewerInputMode.getItemHoverInputMode().setHoverItems(GraphItemTypes.EDGE.or(GraphItemTypes.NODE));
    // if there are other items (most importantly labels) in front of edges or nodes
    // they should be discarded, rather than be reported as "null"
    graphViewerInputMode.getItemHoverInputMode().setInvalidItemsDiscardingEnabled(false);
    // whenever the currently hovered item changes call our method
    graphViewerInputMode.getItemHoverInputMode().addHoveredItemChangedListener(this::onHoveredItemChanged);

    // when the mouse hovers for a longer time over an item we may optionally display a
    // tooltip. Use this callback for querying the tooltip contents.
    graphViewerInputMode.addQueryItemToolTipListener(this::onQueryItemToolTip);

    // if we click on an item we want to perform a custom action, so register a callback
    graphViewerInputMode.addItemClickedListener(this::onItemClicked);

    // also if someone clicked on an empty area we want to perform a custom group action
    graphViewerInputMode.getClickInputMode().addClickedListener(this::onClickInputModeOnClicked);

    this.graphComponent.setInputMode(graphViewerInputMode);
  }

  /**
   * Called when the mouse hovers over a different item.
   * This method will be called whenever the mouse moves over a different item. We show a highlight indicator
   * to make it easier for the user to understand the graph's structure.
   */
  private void onHoveredItemChanged(Object sender, HoveredItemChangedEventArgs hoveredItemChangedEventArgs) {
    // we use the highlight manager of the GraphComponent to highlight related items
    HighlightIndicatorManager<IModelItem> manager = this.graphComponent.getHighlightIndicatorManager();

    // first remove previous highlights
    manager.clearHighlights();
    // then see where we are hovering over, now
    IModelItem newItem = hoveredItemChangedEventArgs.getItem();
    if (newItem != null) {
      // we highlight the item itself
      manager.addHighlight(newItem);
      if (newItem instanceof INode) {
        // and if it's a node, we highlight all adjacent edges, too
        this.graphComponent.getGraph().edgesAt((INode)newItem).forEach(manager::addHighlight);
      } else if (newItem instanceof IEdge) {
        // if it's an edge - we highlight the adjacent nodes
        IEdge edge = (IEdge) newItem;
        manager.addHighlight(edge.getSourceNode());
        manager.addHighlight(edge.getTargetNode());
      }
    }
  }

  /**
   * Called when the mouse has been clicked somewhere.
   */
  private void onClickInputModeOnClicked(Object sender, ClickEventArgs args) {
    // if the user pressed a modifier key during the click...
    if (!args.isHandled() && isEnterExitModifiers(args.getModifiers())) {
      // we check if there was something at the provided location..
      if (this.graphComponent.getGraphModelManager().hitElementsAt(args.getLocation()).stream().count() == 0) {
        // an if there wasn't we try to exit the current group in case we are inside a folder node
        if (ICommand.EXIT_GROUP.canExecute(null, this.graphComponent)) {
          ICommand.EXIT_GROUP.execute(null, this.graphComponent);
          args.setHandled(true);
        }
      }
    }
  }

  /**
   * Determines if the given modifier keys state represents the modifier
   * keys for entering a group node or exiting a folder node.
   */
  private boolean isEnterExitModifiers( ModifierKeys state ) {
    ModifierKeys modifiers = ModifierKeys.SHIFT.or(ModifierKeys.SHORTCUT);
    return state.and(modifiers).equals(modifiers);
  }

  /**
   * Called when the currentItem property of the main GraphComponent component has changed. This happens when items are selected/focused.
   */
  private void onCurrentItemChanged(Object sender, IEventArgs args) {
    IModelItem currentItem = this.graphComponent.getCurrentItem();
    if (currentItem instanceof INode) {
      // for nodes display the label and the values of the mappers for description and URLs..
      INode node = (INode)currentItem;

      String label = node.getLabels().size() > 0 ? node.getLabels().getItem(0).getText() : "Empty";

      String content = getDescriptionMapper().getValue(node);
      String description = content != null ? content : "Empty";

      String url = getUrlMapper().getValue(node);
      String link = url != null ? url : "None";

      propertiesView.showProperties(label, description, link);
    } else {
      // if its not a node - we don't display anything
      propertiesView.reset();
    }
  }

  /**
   * If an item has been clicked, we can execute a custom command.
   */
  private void onItemClicked(Object sender, ItemClickedEventArgs e) {
    if (!e.isHandled() && e.getItem() instanceof INode) {
      // we set the focus to the clicked node
      INode node = (INode) e.getItem();
      this.graphComponent.setCurrentItem(node);

      Mouse2DEventArgs lastMouse2DEvent = this.graphComponent.getLastMouse2DEvent();
      if (isEnterExitModifiers(lastMouse2DEvent.getModifiers())) {
        // if the shift and control/command key had been pressed, we enter the group node if possible
        if (ICommand.ENTER_GROUP.canExecute(e.getItem(), this.graphComponent)) {
          ICommand.ENTER_GROUP.execute(e.getItem(), this.graphComponent);
          e.setHandled(true);
        }
      } else if (lastMouse2DEvent.getModifiers().and(ModifierKeys.SHIFT).equals((ModifierKeys.SHIFT))) {
        // if the shift key had been pressed, we browse to the url related with the clicked node if available
        String url = getUrlMapper().getValue(node);
        if (url != null) {
          try {
            openUrlInBrowser(new URL(url));
            e.setHandled(true);
          } catch (URISyntaxException | IOException e1) {
            e1.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * Callback that will determine the tooltip when the mouse hovers over a node.
   */
  private void onQueryItemToolTip(Object sender, QueryItemToolTipEventArgs queryItemToolTipEventArgs) {
    if (queryItemToolTipEventArgs.getItem() instanceof INode && !queryItemToolTipEventArgs.isHandled()) {
      INode node = (INode)queryItemToolTipEventArgs.getItem();
      IMapper<INode, String> descriptionMapper = getDescriptionMapper();
      IMapper<INode, String> toolTipMapper = getToolTipMapper();
      // to acquire the tooltip first look in the designated tooltip mapper that stores the tooltip that is set in GraphML.
      String toolTip = toolTipMapper.getValue(node);
      if (toolTip == null) {
        // if there was no tooltip set in GraphML, use the description instead.
        toolTip = (descriptionMapper != null ? descriptionMapper.getValue(node) : null);
      }
      if (toolTip == null) {
        // if there is also no description, use the first label of the node if possible.
        toolTip = node.getLabels().size() > 0 ? node.getLabels().getItem(0).getText() : null;
      }
      if (toolTip != null) {
        queryItemToolTipEventArgs.setToolTip(toolTip);
        queryItemToolTipEventArgs.setHandled(true);
      }
    }
  }

  /**
   * Launches the default browser to display the given URL.
   */
  static void openUrlInBrowser(URL url) throws URISyntaxException, IOException {
    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
      desktop.browse(url.toURI());
    }
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new GraphViewerDemo().start();
    });
  }

  private class ShowGraph extends AbstractAction {
    final boolean next;

    ShowGraph( boolean next ) {
      super(next ? "Next" : "Previous");
      this.next = next;
      putValue(SHORT_DESCRIPTION,
              next ? "Show next graph" : "Show previous graph");
      putValue(SMALL_ICON,
              createIcon(next ? "arrow-right-16.png" : "arrow-left-16.png"));

      graphChooserBox.addItemListener(e -> {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          updateEnabledState();
        }
      });
      updateEnabledState();
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
      final JComboBox jcb = GraphViewerDemo.this.graphChooserBox;
      if (next) {
        jcb.setSelectedIndex(jcb.getSelectedIndex() + 1);
      } else {
        jcb.setSelectedIndex(jcb.getSelectedIndex() - 1);
      }
    }

    private void updateEnabledState() {
      final JComboBox jcb = GraphViewerDemo.this.graphChooserBox;
      if (next) {
        setEnabled(jcb.getSelectedIndex() < jcb.getItemCount() - 1);
      } else {
        setEnabled(jcb.getSelectedIndex() > 0);
      }
    }
  }
}
