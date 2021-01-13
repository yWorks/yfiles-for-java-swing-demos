/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.tableeditor;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IColumn;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.IRow;
import com.yworks.yfiles.graph.IStripe;
import com.yworks.yfiles.graph.ITable;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.StripeTypes;
import com.yworks.yfiles.graph.Table;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.INodeStyleRenderer;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.TableNodeStyle;
import com.yworks.yfiles.graph.styles.TableRenderingOrder;
import com.yworks.yfiles.graph.styles.VoidStripeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.IEventListener;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.IReparentNodeHandler;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import com.yworks.yfiles.view.input.NodeDropInputMode;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.PopulateItemPopupMenuEventArgs;
import com.yworks.yfiles.view.input.ReparentStripeHandler;
import com.yworks.yfiles.view.input.StripeSubregion;
import com.yworks.yfiles.view.input.StripeSubregionTypes;
import com.yworks.yfiles.view.input.TableEditorInputMode;
import com.yworks.yfiles.view.input.ToolTipQueryEventArgs;
import toolkit.AbstractDemo;
import toolkit.DragAndDropSupport;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.function.Predicate;

/**
 * Configure an instance of {@link com.yworks.yfiles.view.input.TableEditorInputMode} that is used to
 * interactively modify the tables, as well as several child modes of {@link com.yworks.yfiles.view.input.GraphEditorInputMode}
 * that handle popup menus and tool tips.
 * Perform a hierarchic layout that automatically respects the table structure.
 */
public class TableEditorDemo extends AbstractDemo {
  // command used in the toolbar to layout the graph
  private static final ICommand RUN_LAYOUT = ICommand.createCommand("Run Layout");
  // the default style for group nodes
  private static final ShapeNodeStyle DEFAULT_GROUP_NODE_STYLE;
  // the default style for normal nodes
  private static final ShinyPlateNodeStyle DEFAULT_NODE_STYLE;
   // the default node size for normal nodes
  private static final SizeD DEFAULT_NODE_SIZE = new SizeD(80, 50);

  // provides graph editing capabilities
  private GraphEditorInputMode graphEditorInputMode;
  // provides table editing capabilities
  private TableEditorInputMode tableEditorInputMode;

  static {
    DEFAULT_GROUP_NODE_STYLE = new ShapeNodeStyle();
    DEFAULT_GROUP_NODE_STYLE.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    Pen pen = new Pen(Color.BLACK, 1);
    pen.setDashStyle(DashStyle.getDashDot());
    DEFAULT_GROUP_NODE_STYLE.setPen(pen);
    DEFAULT_GROUP_NODE_STYLE.setPaint(Colors.TRANSPARENT);

    DEFAULT_NODE_STYLE = new ShinyPlateNodeStyle();
    DEFAULT_NODE_STYLE.setPaint(Color.ORANGE);
    DEFAULT_NODE_STYLE.setRadius(0);
  }

  // the list containing the nodes to drag from
  private JList<INode> palette;

  /**
   * Adds a palette to the JRootPane of the application frame in addition to the default
   * graph component, toolbar, and help pane.
   */
  protected void configure(JRootPane rootPane) {
    super.configure(rootPane);

    // the list that will contain the nodes which may be dragged into the
    // graph component
    // see populatePalette() for the list's contents
    palette = new JList<>();
    JScrollPane pane = new JScrollPane(palette);
    pane.setPreferredSize(new Dimension(300, 850));
    rootPane.getContentPane().add(pane, BorderLayout.WEST);
  }

  /**
   * Populates the palette, configures the input modes, and loads a sample graph.
   */
  public void initialize() {
    // add node templates as well as column and row templates to the palette
    populatePalette();

    // configure the graph and table editing capabilities
    configureInputModes();

    IGraph graph = graphComponent.getGraph();

    // set the default styles
    initializeVisualizationDefaults(graph);

    // customize IO handling to support the styles of this demo
    initializeIO();

    // enable undo/redo support
    initializeUndoSupport(graph);

    // prevent connecting edges to table nodes and click-selecting tables to support marquee selection inside tables
    initializeTableInteraction(graph);

    // enable save/load operations
    graphComponent.setFileIOEnabled(true);
  }

  /**
   * Loads and centers a sample graph in the graph component.
   */
  public void onVisible() {
    // loads a sample graph containing a table
    loadInitialGraph(graphComponent);
  }

  /**
   * Populates the palette with a table node, a column, a row, a normal node,
   * and a group node. The elements in the palette can be dragged over and
   * dropped into the demo's <code>GraphComponent</code> to create new elements
   * of the corresponding type in the displayed diagram.
   */
  private void populatePalette() {
    // fill the model of the palette with sample nodes
    DefaultListModel<INode> model = new DefaultListModel<>();
    model.addElement(createTableNode());
    model.addElement(createColumnNode());
    model.addElement(createRowNode());
    model.addElement(createNormalNode());
    model.addElement(createGroupNode());
    palette.setModel(model);

    // set a custom cell renderer painting the sample nodes in the drag list
    palette.setCellRenderer(new NodeCellRenderer());

    // configure the list as source for drag and drop operations
    palette.setDragEnabled(true);
    MyTransferHandler transferHandler = new MyTransferHandler();
    DragAndDropSupport.disableDefaultPreview(transferHandler);
    palette.setTransferHandler(transferHandler);
  }

  /**
   * Creates a table node for use as a palette template.
   */
  private INode createTableNode() {
    // binding the table is performed through a TableNodeStyle instance.
    // among other things, this also makes the table instance available in the node's lookup (use INode.lookup()...)
    TableNodeStyle tableNodeStyle = new TableNodeStyle(createTable());
    tableNodeStyle.setTableRenderingOrder(TableRenderingOrder.ROWS_FIRST);
    ShapeNodeStyle tableNodeBackgroundStyle = new ShapeNodeStyle();
    tableNodeBackgroundStyle.setPaint(new Color(236, 245, 255));
    tableNodeStyle.setBackgroundStyle(tableNodeBackgroundStyle);
    SimpleNode tableNode = new SimpleNode();
    tableNode.setLayout(tableNodeStyle.getTable().getLayout());
    tableNode.setStyle(tableNodeStyle);
    return tableNode;
  }

  /**
   * Creates a table for use in table node palette templates.
   */
  private ITable createTable() {
    Table sampleTable = new Table();
    sampleTable.setInsets(new InsetsD(30, 0, 0, 0));
    // configure the defaults for the sample table
    sampleTable.getColumnDefaults().setMinimumSize(50);
    sampleTable.getRowDefaults().setMinimumSize(50);
    // setup defaults for the sample table
    // we use a custom style for the rows that alternates the stripe colors and uses a special style for all parent stripes
    AlternatingLeafStripeStyle tableStyle = new AlternatingLeafStripeStyle(
        new StripeDescriptor(new Color(196, 215, 237), new Color(196, 215, 237)),
        new StripeDescriptor(new Color(171, 200, 226), new Color(171, 200, 226)),
        new StripeDescriptor(new Color(113, 146, 178), new Color(113, 146, 178)));
    sampleTable.getRowDefaults().setStyle(tableStyle);
    // we use a simpler style for the columns
    StripeDescriptor descriptor = new StripeDescriptor(Colors.TRANSPARENT, new Color(113, 146, 178));
    sampleTable.getColumnDefaults().setStyle(new AlternatingLeafStripeStyle(descriptor));
    // create a row and a column in the sample table
    sampleTable.createGrid(1, 1);
    // use twice the default width for this sample column (looks nicer in the preview...)
    IColumn firstCol = first(sampleTable.getColumns());
    sampleTable.setSize(firstCol, firstCol.getActualSize() * 2);
    return sampleTable;
  }

  /**
   * Creates a table node for use as a palette template for new table columns.
   */
  private INode createColumnNode() {
    // create a sample table that displays a single columns 
    ITable columnTable = createColumnTable();
    TableNodeStyle columnSampleNodeStyle = new TableNodeStyle(columnTable);
    // the single-column table is bound to a node to have only objects of one
    // type in the palette
    SimpleNode columnNode = new SimpleNode();
    columnNode.setLayout(columnTable.getLayout());
    columnNode.setStyle(columnSampleNodeStyle);
    // provide easy access to the sample column through the node's tag
    columnNode.setTag(first(columnTable.getRootColumn().getChildColumns()));
    return columnNode;
  }

  /**
   * Creates a sample table that displays a single column for use in
   * nodes that are palette templates.
   */
  private ITable createColumnTable() {
    ITable columnSampleTable = new Table();
    // create the sample column by specifying the desired visualization
    StripeDescriptor descriptor = new StripeDescriptor(new Color(171, 200, 226), new Color(240, 248, 255));
    IColumn columnSampleColumn = columnSampleTable.createColumn(200);
    columnSampleTable.setStyle(columnSampleColumn, new AlternatingLeafStripeStyle(descriptor));
    // create an invisible sample row in this table, otherwise only the column
    // header is displayed
    IRow columnSampleRow = columnSampleTable.createRow(200);
    columnSampleTable.setStyle(columnSampleRow, VoidStripeStyle.INSTANCE);
    columnSampleTable.setStripeInsets(columnSampleRow, InsetsD.EMPTY);

    columnSampleTable.addLabel(columnSampleColumn, "Column");
    return columnSampleTable;
  }

  /**
   * Creates a table node for use as a palette template for new table rows.
   */
  private INode createRowNode() {
    ITable rowTable = createRowTable();
    TableNodeStyle rowNodeStyle = new TableNodeStyle(rowTable);
    SimpleNode rowNode = new SimpleNode();
    rowNode.setLayout(rowTable.getLayout());
    rowNode.setStyle(rowNodeStyle);
    rowNode.setTag(first(rowTable.getRootRow().getChildRows()));
    return rowNode;
  }

  /**
   * Creates a sample table that displays a single row for use in
   * nodes that are palette templates.
   */
  private ITable createRowTable() {
    ITable rowSampleTable = new Table();

    // create the sample row by configuring visualization defaults for rows
    StripeDescriptor descriptor = new StripeDescriptor(new Color(171, 200, 226), new Color(240, 248, 255));
    rowSampleTable.getRowDefaults().setStyle(new AlternatingLeafStripeStyle(descriptor, descriptor, descriptor));
    IRow rowSampleRow = rowSampleTable.createRow();

    // create an invisible sample column in this table, otherwise only the row
    // header is displayed
    IColumn rowSampleColumn = rowSampleTable.createColumn(200);
    rowSampleTable.setStyle(rowSampleColumn, VoidStripeStyle.INSTANCE);
    rowSampleTable.setStripeInsets(rowSampleColumn, InsetsD.EMPTY);

    rowSampleTable.addLabel(rowSampleRow, "Row");
    return rowSampleTable;
  }

  /**
   * Creates a normal node for use as a palette template.
   */
  private INode createNormalNode() {
    SimpleNode normalNode = new SimpleNode();
    normalNode.setStyle(DEFAULT_NODE_STYLE);
    normalNode.setLayout(new RectD(PointD.ORIGIN, DEFAULT_NODE_SIZE));
    return normalNode;
  }

  /**
   * Creates a group node for use as a palette template.
   */
  private INode createGroupNode() {
    SimpleNode groupNode = new SimpleNode();
    groupNode.setStyle(DEFAULT_GROUP_NODE_STYLE);
    groupNode.setLayout(new RectD(PointD.ORIGIN, DEFAULT_NODE_SIZE));
    // set a custom tag that identifies this node as group node
    // this tag is used in NodeDropInputMode's isGroupNodePredicate
    // to determine whether a group node or a normal node should be
    // created when dropping a node template from the palette
    groupNode.setTag("GroupNode");
    return groupNode;
  }

  /**
   * Returns the first element in the given iterable.
   * @throws java.util.NoSuchElementException if there are no elements
   * in the given iterable.
   */
  private static <T> T first(IEnumerable<T> enumerable) {
    return enumerable.stream().findFirst().orElseThrow(NoSuchElementException::new);
  }

  /**
   * Configures the main input mode for table editing and drag and drop support.
   */
  private void configureInputModes() {
    graphEditorInputMode = new TableGraphEditorInputMode();

    // we want orthogonal edge editing/creation
    OrthogonalEdgeEditingContext orthogonalEdgeEditingContext = new OrthogonalEdgeEditingContext();
    orthogonalEdgeEditingContext.setEnabled(true);
    graphEditorInputMode.setOrthogonalEdgeEditingContext(orthogonalEdgeEditingContext);

    // activate drag and drop from the style palette
    NodeDropInputMode nodeDropInputMode = new MyNodeDropInputMode();
    nodeDropInputMode.setPriority(70);
    nodeDropInputMode.setPreviewEnabled(true);
    nodeDropInputMode.setEnabled(true);
    // we identify the group nodes during a drag by either a custom tag or if they have a table associated
    nodeDropInputMode.setIsGroupNodePredicate(this::isGroupNode);
    graphEditorInputMode.setNodeDropInputMode(nodeDropInputMode);

    // disable node creation on click - new nodes have to be created using
    // drag and drop form the palette
    graphEditorInputMode.setCreateNodeAllowed(false);

    //Register custom re-parent handler that prevents re-parenting of table nodes (i.e. they may only appear on root level)
    graphEditorInputMode.setReparentNodeHandler(new MyReparentHandler(graphEditorInputMode.getReparentNodeHandler()));

    // enable interactive grouping operations
    graphEditorInputMode.setGroupingOperationsAllowed(true);

    configureTableEditing();

    // register RUN_LAYOUT as a command bindings for the GraphComponent
    KeyboardInputMode kim = graphEditorInputMode.getKeyboardInputMode();
    kim.addCommandBinding(RUN_LAYOUT, this::executeLayout, this::canExecuteLayout);
    graphComponent.setInputMode(graphEditorInputMode);
  }

  /**
   * Determines whether a node is a group node.
   */
  private boolean isGroupNode(INode node) {
    return isTableNode(node) || "GroupNode".equals(node.getTag());
  }

  /**
   * Configures table editing specific parts.
   */
  private void configureTableEditing() {
    tableEditorInputMode = new TableEditorInputMode();

    // enable drag and drop
    tableEditorInputMode.getStripeDropInputMode().setEnabled(true);
    // restrict the nesting depths of columns and rows
    // i.e. only top-level columns may have child columns; same for rows
    ReparentStripeHandler reparentStripeHandler = new ReparentStripeHandler();
    reparentStripeHandler.setMaxColumnLevel(2);
    reparentStripeHandler.setMaxRowLevel(2);
    tableEditorInputMode.setReparentStripeHandler(reparentStripeHandler);

    // add to GraphEditorInputMode - we set the priority lower than for the handle input mode so that handles win if
    // both gestures are possible
    tableEditorInputMode.setPriority(graphEditorInputMode.getHandleInputMode().getPriority() + 1);
    graphEditorInputMode.add(tableEditorInputMode);

    graphEditorInputMode.setPopupMenuItems(GraphItemTypes.NODE);
    graphEditorInputMode.addPopulateItemPopupMenuListener(new PopulateItemPopupMenuHandler());
    graphEditorInputMode.addPopulateItemPopupMenuListener(new PopulateNodePopupMenuHandler());

    // set up tooltips to appear when hovering over a column or row header
    graphEditorInputMode.getMouseHoverInputMode().addQueryToolTipListener(new QueryToolTipHandler());
  }

  /**
   * Retrieves the column or row at the specified location.
   */
  StripeSubregion getStripe( PointD location) {
    return tableEditorInputMode.findStripe(location, StripeTypes.ALL, StripeSubregionTypes.HEADER);
  }

  /**
   * Sets the default styles of the normal and group nodes.
   */
  private void initializeVisualizationDefaults(IGraph graph) {
    // set defaults for normal nodes
    INodeDefaults nodeDefaults = graph.getNodeDefaults();
    nodeDefaults.setStyle(DEFAULT_NODE_STYLE);
    nodeDefaults.setSize(DEFAULT_NODE_SIZE);

    // set defaults for group nodes
    INodeDefaults groupNodeDefaults = graph.getGroupNodeDefaults();
    groupNodeDefaults.setStyle(DEFAULT_GROUP_NODE_STYLE);
    groupNodeDefaults.setSize(DEFAULT_NODE_SIZE);
  }

  /**
   * Customizes IO handling to support the styles of this demo.
   */
  private void initializeIO() {
    // create an IOHandler that will be used for all IO operations
    GraphMLIOHandler ioh = new GraphMLIOHandler();

    // we set the IO handler on the GraphComponent, so the GraphComponent's IO methods
    // will pick up our handler for use during serialization and deserialization.
    graphComponent.setGraphMLIOHandler(ioh);

    // add namespace for styles of this demo
    ioh.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-java/demos/TableEditor/1.0", getClass());
  }

  /**
   * Loads a sample graph.
   */
  private void loadInitialGraph( GraphComponent graphComponent ) {
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml").toExternalForm());
    } catch (IOException e) {
      e.printStackTrace();
    }
    graphComponent.fitGraphBounds();
  }

  /**
   * Enables undo/redo support.
   */
  private void initializeUndoSupport(IGraph graph) {
    // configure Undo...
    // enable general undo support
    graph.setUndoEngineEnabled(true);
    // use the undo support from the graph also for all future table instances
    Table.installStaticUndoSupport(graph);
  }

  /**
   * Prevents connecting edges to table nodes and click-selecting tables to
   * support marquee selection inside tables.
   */
  private void initializeTableInteraction(IGraph graph) {
    NodeDecorator nodeDecorator = graph.getDecorator().getNodeDecorator();

    // provide no candidates for edge creation at table nodes - this effectively disables edge creations for those nodes
    Predicate<INode> nodeTablePredicate = TableEditorDemo::isTableNode;
    nodeDecorator.getPortCandidateProviderDecorator().setImplementation(nodeTablePredicate, IPortCandidateProvider.NO_CANDIDATES);

    // customize marquee selection handling for table nodes
    nodeDecorator.getMarqueeTestableDecorator().setFactory(nodeTablePredicate, node -> new TableNodeMarqueeTestable(node.getLayout()));
  }

  /**
   * Determines whether or not the given item is associated to an
   * {@link com.yworks.yfiles.graph.ITable} instance.
   * @param item the item to check.
   * @return <code>true</code> if the given item is associated to an
   * {@link com.yworks.yfiles.graph.ITable} instance; <code>false</code>
   * otherwise.
   */
  static boolean isTableNode( ILookup item ) {
    return item.lookup(ITable.class) != null;
  }

  /**
   * A {@link javax.swing.ListCellRenderer} that paints {@link com.yworks.yfiles.graph.INode}s in the cells of a {@link
   * javax.swing.JList}.
   */
  private class NodeCellRenderer implements ListCellRenderer<INode> {
    // renders the list cell
    private DefaultListCellRenderer renderer;
    // holds an icon for each node
    private WeakHashMap<INode, NodeIcon> node2icon;

    NodeCellRenderer() {
      renderer = new DefaultListCellRenderer();
      node2icon = new WeakHashMap<>();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends INode> list, INode node, int index, boolean isSelected, boolean cellHasFocus) {
      // we use a label as component that renders the list cell and sets an icon that paints the given node
      JLabel label = (JLabel) renderer.getListCellRendererComponent(list, node, index, isSelected, cellHasFocus);
      label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      label.setHorizontalAlignment(SwingConstants.CENTER);
      label.setIcon(getIcon(node));
      label.setText(null);
      label.setToolTipText("Drag it");
      return label;
    }

    /**
     * Returns an {@link javax.swing.Icon} painting the given node.
     */
    private Icon getIcon(INode node) {
      NodeIcon icon = node2icon.get(node);
      if (icon == null) {
        icon = new NodeIcon(node);
        node2icon.put(node, icon);
      }
      return icon;
    }
  }

  /**
   * An {@link javax.swing.Icon} that paints an {@link com.yworks.yfiles.graph.INode}.
   */
  private class NodeIcon implements Icon {
    private int width;
    private int height;
    private IVisual visual;

    NodeIcon(INode node) {
      // determine the dimension of the icon
      IRectangle nl = node.getLayout();
      width = (int) nl.getWidth();
      height = (int) nl.getHeight();

      // create a visual painting the node from the node's style
      INodeStyle style = node.getStyle();
      INodeStyleRenderer renderer = style.getRenderer();
      IVisualCreator creator = renderer.getVisualCreator(node, style);
      visual = creator.createVisual(graphComponent.createRenderContext());
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D gfx = (Graphics2D) g.create();
      gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      gfx.translate(x, y);
      visual.paint(graphComponent.createRenderContext(), gfx);
      gfx.dispose();
    }

    @Override
    public int getIconWidth() {
      return width;
    }

    @Override
    public int getIconHeight() {
      return height;
    }
  }

  /**
   * Prevents click-selection of table nodes to support marquee selection inside
   * tables.
   */
  private static class TableGraphEditorInputMode extends GraphEditorInputMode {
    /**
     * Prevents click-selection of table nodes to support marquee selection inside
     * tables.
     */
    @Override
    protected boolean shouldClickSelect(IModelItem item) {
      return !isTableNode(item) && super.shouldClickSelect(item);
    }
  }

  /**
   * Prevents dropping table nodes inside existing group (or table) nodes.
   */
  private static class MyNodeDropInputMode extends NodeDropInputMode {
    /**
     * Prevents dropping table nodes inside existing group (or table) nodes.
     */
    @Override
    protected IModelItem getDropTarget(PointD dragLocation) {
      return isTableNode(getDraggedItem()) ? null : super.getDropTarget(dragLocation);
    }
  }

  /**
   * Prevents table nodes from being moved into other group (or table) nodes.
   */
  private static class MyReparentHandler implements IReparentNodeHandler {

    private IReparentNodeHandler wrappedReparentHandler;

    public MyReparentHandler(IReparentNodeHandler wrappedReparentHandler) {
      this.wrappedReparentHandler = wrappedReparentHandler;
    }

    @Override
    public boolean isReparentGesture(IInputModeContext context, INode node) {
      return wrappedReparentHandler.isReparentGesture(context, node);
    }

    /**
     * Prevents table nodes from being moved into other group (or table) nodes.
     */
    @Override
    public boolean shouldReparent(IInputModeContext context, INode node) {
      return !isTableNode(node) && wrappedReparentHandler.shouldReparent(context, node);
    }

    @Override
    public boolean isValidParent(IInputModeContext context, INode node, INode newParent) {
      return wrappedReparentHandler.isReparentGesture(context, node);
    }

    @Override
    public void reparent(IInputModeContext context, INode node, INode newParent) {
      wrappedReparentHandler.reparent(context, node, newParent);
    }
  }

  /**
   * Builds popup menus for column and row headers.
   * We show only a simple popup menu that demonstrates the
   * {@link TableEditorInputMode#insertChild(com.yworks.yfiles.graph.IStripe, int)}
   * convenience method.
   */
  private class PopulateItemPopupMenuHandler implements IEventListener<PopulateItemPopupMenuEventArgs<IModelItem>> {
    @Override
    public void onEvent(Object source, PopulateItemPopupMenuEventArgs<IModelItem> args) {
      if (!args.isHandled()) {
        StripeSubregion stripe = getStripe(args.getQueryLocation());
        if (stripe != null) {
          JPopupMenu popupMenu = (JPopupMenu) args.getMenu();
          popupMenu.add(createDeleteAction(stripe));
          popupMenu.add(createInsertBeforeAction(stripe));
          popupMenu.add(createInsertAfterAction(stripe));
          args.setHandled(true);
        }
      }
    }

    /**
     * Creates an {@link javax.swing.Action} to delete the given stripe.
     */
    private Action createDeleteAction(StripeSubregion stripe) {
      Action action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          ICommand.DELETE.execute(stripe.getStripe(), graphComponent);
        }
      };
      action.putValue(Action.NAME, "Delete " + stripe.getStripe());
      return action;
    }

    /**
     * Creates an {@link javax.swing.Action} to insert a new stripe before the given one.
     */
    private Action createInsertBeforeAction(StripeSubregion stripe) {
      Action action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          IStripe parent = stripe.getStripe().getParentStripe();
          int index = stripe.getStripe().getIndex();
          tableEditorInputMode.insertChild(parent, index);
        }
      };
      action.putValue(Action.NAME, "Insert new stripe before " + stripe.getStripe());
      return action;
    }

    /**
     * Creates an {@link javax.swing.Action} to insert a new stripe after the given one.
     */
    private Action createInsertAfterAction(StripeSubregion stripe) {
      Action action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          IStripe parent = stripe.getStripe().getParentStripe();
          int index = stripe.getStripe().getIndex();
          tableEditorInputMode.insertChild(parent, index + 1);
        }
      };
      action.putValue(Action.NAME, "Insert new stripe after " + stripe.getStripe());
      return action;
    }
  }

  /**
   * Show tool tips for column and row headers.
   */
  private class QueryToolTipHandler implements IEventListener<ToolTipQueryEventArgs> {
    @Override
    public void onEvent(Object source, ToolTipQueryEventArgs args) {
      if (!args.isHandled()){
        StripeSubregion stripeDescriptor = getStripe(args.getQueryLocation());
        if (stripeDescriptor != null) {
          // pass the stripes title to the tooltip
          args.setToolTip(stripeDescriptor.getStripe().toString());
          args.setHandled(true);
        }
      }
    }
  }

  /**
   * Builds popup menus for table nodes.
   * We show only a dummy popup menu to demonstrate the basic principle.
   */
  private class PopulateNodePopupMenuHandler implements IEventListener<PopulateItemPopupMenuEventArgs<IModelItem>> {
    @Override
    public void onEvent(Object source, PopulateItemPopupMenuEventArgs<IModelItem> args) {
      if (!args.isHandled()) {
        Predicate<IModelItem> predicate = TableEditorDemo::isTableNode;
        Iterator<IModelItem> items = graphEditorInputMode.findItems(args.getContext(), args.getQueryLocation(),
            new GraphItemTypes[]{GraphItemTypes.NODE}, predicate).iterator();
        IModelItem tableNode = items.hasNext() ? items.next() : null;
        if (tableNode != null) {
          JPopupMenu popupMenu = (JPopupMenu) args.getMenu();
          popupMenu.add("Popup Menu for " + tableNode);
          args.setHandled(true);
        }
      }
    }
  }

  /**
   * Customizes marquee selection so that a table node gets selected only if
   * its bounds are completely enclosed by the marquee rectangle.
   */
  private static class TableNodeMarqueeTestable implements IMarqueeTestable {
    private IRectangle layout;

    public TableNodeMarqueeTestable( IRectangle layout ) {
      this.layout = layout;
    }

    @Override
    public boolean isInBox(IInputModeContext context, RectD box) {
      return box.contains(layout.getTopLeft()) && box.contains(layout.toRectD().getBottomRight());
    }
  }

  /**
   * Adds buttons to the toolbar for loading and saving GraphML, undoability, clipboard, changing the zoom level and
   * applying a layout.
   */
  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, graphComponent));
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE_AS, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png", ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Cut", "cut-16.png", ICommand.CUT, null, graphComponent));
    toolBar.add(createCommandButtonAction("Copy", "copy-16.png", ICommand.COPY, null, graphComponent));
    toolBar.add(createCommandButtonAction("Paste", "paste-16.png", ICommand.PASTE, null, graphComponent));
    toolBar.addSeparator();
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    HierarchicLayout ihl = new HierarchicLayout();
    ihl.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
    ihl.setOrthogonalRoutingEnabled(true);
    toolBar.add(createCommandButtonAction("Apply hierarchic layout", "layout-hierarchic.png", RUN_LAYOUT, ihl, graphComponent));
  }

  /**
   * Determines whether or not the {@link #RUN_LAYOUT} can be executed.
   */
  private boolean canExecuteLayout(ICommand command, Object parameter, Object source) {
    // if a layout algorithm is currently running, no other layout algorithm shall be executable for two reasons:
    // - the result of the current layout run shall be presented before executing a new layout
    // - layout algorithms are not thread safe, so calling applyLayout on a layout algorithm that currently calculates
    //    a layout may result in errors
    if (parameter instanceof ILayoutAlgorithm && !graphEditorInputMode.getWaitInputMode().isWaiting()) {
      // don't allow layouts for empty graphs
      IGraph graph = graphComponent.getGraph();
      return graph != null && !(graph.getNodes().size() == 0);
    } else {
      return false;
    }
  }

  /**
   * Executes the {@link #RUN_LAYOUT}.
   */
  private boolean executeLayout(ICommand command, Object parameter, Object source) {
    if (parameter instanceof ILayoutAlgorithm) {
      ILayoutAlgorithm layout = (ILayoutAlgorithm) parameter;
      graphComponent.morphLayout(layout, Duration.ofMillis(500));
      return true;
    }
    return false;
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new TableEditorDemo().start();
    });
  }

  /**
   * Transfers {@link com.yworks.yfiles.graph.INode} and
   * {@link com.yworks.yfiles.graph.IStripe} instances from a
   * {@link javax.swing.JList} to another Swing component in the same JVM.
   */
  private static class MyTransferHandler extends TransferHandler {
    /**
     * Data flavor the represents {@link com.yworks.yfiles.graph.INode}
     * instances.
     */
    private DataFlavor nodeFlavor;
    /**
     * Data flavor the represents {@link com.yworks.yfiles.graph.IStripe}
     * instances.
     */
    private DataFlavor stripeFlavor;

    /**
     * Initializes a new <code>MyTransferHandler</code> instance for
     * {@link com.yworks.yfiles.graph.INode} and
     * {@link com.yworks.yfiles.graph.IStripe} instances.
     */
    MyTransferHandler() {
      super("selectedValue");
      nodeFlavor = DragAndDropSupport.newFlavor(INode.class);
      stripeFlavor = DragAndDropSupport.newFlavor(IStripe.class);
    }

    /**
     * Creates a {@link java.awt.datatransfer.Transferable} instance for the
     * selected value from the given {@link javax.swing.JList}.
     * This method assumes that all values in the {@link javax.swing.JList}
     * are of type {@link com.yworks.yfiles.graph.INode}.
     */
    @Override
    protected Transferable createTransferable( JComponent c ) {
      INode value = (INode) ((JList) c).getSelectedValue();
      Object tag = value.getTag();
      if (tag instanceof IStripe) {
        // stripeFlavor will trigger com.yworks.yfiles.view.input.StripeDropInputMode
        return DragAndDropSupport.newTransferable(stripeFlavor, tag);
      } else {
        // we use a copy of the node since the style should not be shared
        SimpleNode node = new SimpleNode();
        node.setLayout(value.getLayout());
        node.setStyle((INodeStyle) value.getStyle().clone());
        node.setTag(value.getTag());
        // nodeFlavor will trigger com.yworks.yfiles.view.input.NodeDropInputMode
        return DragAndDropSupport.newTransferable(nodeFlavor, node);
      }
    }
  }
}
