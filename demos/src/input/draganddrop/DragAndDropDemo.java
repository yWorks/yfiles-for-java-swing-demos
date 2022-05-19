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
package input.draganddrop;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.SimpleEdge;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.SimplePort;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.VoidNodeStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.GridVisualCreator;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.PixelImageExporter;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.GridConstraintProvider;
import com.yworks.yfiles.view.input.GridSnapTypes;
import com.yworks.yfiles.view.input.IDropCreationCallback;
import com.yworks.yfiles.view.input.INodeCreationCallback;
import com.yworks.yfiles.view.input.LabelDropInputMode;
import com.yworks.yfiles.view.input.NodeDropInputMode;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.PortDropInputMode;
import toolkit.AbstractDemo;
import toolkit.DragAndDropSupport;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.util.WeakHashMap;

/**
 * Demonstrates how to use classes{@link NodeDropInputMode},
 * {@link LabelDropInputMode}, and {@link PortDropInputMode}.
 * In contrast to {@link com.yworks.yfiles.view.input.DropInputMode},
 * the aforementioned specialized modes show a preview of the item while
 * dragging and (in the case of nodes and ports) support snapping.
 *
 * <ul>
 *   <li>
 *     To create a node, drag the desired node style from the left panel onto the
 *     canvas. See the dragged node snap to the grid positions and to other nodes.
 *   </li>
 *   <li>
 *     To create a node label, drag the node label template from the left panel
 *     onto a node in the canvas.
 *   </li>
 *   <li>
 *     To create an edge label, drag the edge label template from the left panel
 *     onto an edge in the canvas.
 *   </li>
 *   <li>
 *     To create a port, drag the port template from the left panel
 *     onto a node in the canvas.
 *   </li>
 * </ul>
 */
public class DragAndDropDemo extends AbstractDemo {
  /**
   * Identifies a palette template as a template for edge labels.
   * I.e. when dragging a palette template with this ID, not the node but its
   * first label is transferred.
   */
  static final String ID_EDGE_LABEL = "Edge Label Container";
  /**
   * Identifies a palette template as a template for node labels.
   * I.e. when dragging a palette template with this ID, not the node but its
   * first label is transferred.
   */
  static final String ID_NODE_LABEL = "Node Label Container";
  /**
   * Identifies a palette template as a template for port.
   * I.e. when dragging a palette template with this ID, not the node but its
   * first port is transferred.
   */
  static final String ID_PORT = "Port Container";


  // input mode that handles node creation through drag and drop
  private NodeDropInputMode nodeDropInputMode;
  // input mode that handles label creation through drag and drop
  private LabelDropInputMode labelDropInputMode;
  // input mode that handles port creation through drag and drop
  private PortDropInputMode portDropInputMode;
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
    rootPane.getContentPane().add(pane, BorderLayout.WEST);
  }

  public void initialize() {
    // create and configure a GraphEditorInputMode for editing the graph
    GraphEditorInputMode editorInputMode = new GraphEditorInputMode();
    // configure interactive snapping of graph elements to a grid
    configureGridSnapping(editorInputMode);
    // configure node drag and drop operations
    configureNodeDropping(editorInputMode);
    // configure label drag and drop operations
    configureLabelDropping(editorInputMode);
    // configure port drag and drop operations
    configurePortDropping(editorInputMode);
    // enable grouping operations
    editorInputMode.setGroupingOperationsAllowed(true);
    // enable support for manual creation of orthogonal edge paths
    editorInputMode.setOrthogonalEdgeEditingContext(new OrthogonalEdgeEditingContext());
    // use the style, size and labels of the currently selected palette node for newly created nodes
    editorInputMode.setNodeCreator(getPaletteNodeCreator(editorInputMode.getNodeCreator()));

    // register the GraphEditorInputMode as the main input mode
    graphComponent.setInputMode(editorInputMode);

    // setup the list to drag nodes from
    configureNodePalette();

    // enables undo functionality
    graphComponent.getGraph().setUndoEngineEnabled(true);

    // populate the component with some nodes
    createSampleGraph();
  }

  /**
   * Returns an {@link INodeCreationCallback} wrapper that creates a node using the style, size and labels of the
   * currently selected palette node.
   * @param nodeCreator the original {@link INodeCreationCallback}
   */
  private INodeCreationCallback getPaletteNodeCreator(INodeCreationCallback nodeCreator) {
    return (context, graph, location, parent) -> {
      INode paletteNode = palette.getSelectedValue();
      if (paletteNode == null) {
        // there is currently no selected item in the palette,
        // thus do not create a new node 
        return null;
      }
      Object tag = paletteNode.getTag();
      if (tag != null && tag.toString().endsWith("Label Container")) {
        // the currently selected item in the palette is a label template,
        // thus do not create a new node
        return null;
      }
      INode newNode = nodeCreator.createNode(context, graph, location, parent);
      graph.setStyle(newNode, paletteNode.getStyle());
      graph.setNodeLayout(newNode, RectD.fromCenter(location, paletteNode.getLayout().toSizeD()));
      graph.setIsGroupNode(newNode, paletteNode.getStyle() instanceof PanelNodeStyle);
      newNode.setTag(paletteNode.getTag());
      paletteNode.getLabels().forEach(
          label -> graph.addLabel(newNode, label.getText(), label.getLayoutParameter(), label.getStyle()));
      return newNode;
    };
  }

  /**
   * Configures the visualisation and behavior of the grid snapping feature.
   * @param editorInputMode The GraphEditorInputMode for this application.
   */
  private void configureGridSnapping(GraphEditorInputMode editorInputMode) {
    // enable grid for comfortable snapping
    int gridWidth = 80;
    GridInfo gridInfo = new GridInfo(gridWidth);
    GridVisualCreator grid = new GridVisualCreator(gridInfo);
    graphComponent.getBackgroundGroup().addChild(grid, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);

    // create and configure (grid) snapping
    GraphSnapContext context = new GraphSnapContext();
    context.setNodeToNodeDistance(30);
    context.setNodeToEdgeDistance(20);
    context.setSnappingOrthogonalMovementEnabled(false);
    context.setSnapDistance(10);
    context.setSnappingSegmentsToSnapLinesEnabled(true);
    context.setNodeGridConstraintProvider(new GridConstraintProvider<>(gridInfo));
    context.setBendGridConstraintProvider(new GridConstraintProvider<>(gridInfo));
    context.setSnappingBendsToSnapLinesEnabled(true);
    context.setGridSnapType(GridSnapTypes.ALL);

    editorInputMode.setSnapContext(context);
  }

  /**
   * Enables support for dragging nodes from the demo's palette onto the
   * demo's graph component.
   * @param editorInputMode the demo's main input mode.
   */
  private void configureNodeDropping(GraphEditorInputMode editorInputMode) {
    // obtain an input mode for handling node drag and drop operations
    nodeDropInputMode = editorInputMode.getNodeDropInputMode();
    // by default the mode available in GraphEditorInputMode is disabled, so first enable it
    nodeDropInputMode.setEnabled(true);

    // The palette provides normal nodes and group nodes. In this sample the
    // NodeDropInputMode should handle nodes with PanelNodeStyle as group nodes.
    nodeDropInputMode.setIsGroupNodePredicate(draggedNode -> draggedNode.getStyle() instanceof PanelNodeStyle);

    // We allow the NodeDropInputMode to convert a normal node to a group
    // node when a node has been dropped on it
    nodeDropInputMode.setNonGroupNodeAsParentAllowed(true);
    // ... but we restrict that feature to the root and nodes with its tag set to true.
    nodeDropInputMode.setIsValidParentPredicate(node -> node == null || Boolean.TRUE.equals(node.getTag()));

    // In that case the node should look like a group node, so we have to set its
    // style to the group node style, remove all labels and add the "Group Node" label.
    IDropCreationCallback<INode> originalItemCreator = nodeDropInputMode.getItemCreator();
    nodeDropInputMode.setItemCreator((context, graph, draggedItem, dropTarget, dropLocation) -> {
      // create the dropped node
      INode item = originalItemCreator.createItem(context, graph, draggedItem, dropTarget, dropLocation);
      INode parent = graph.getParent(item);
      // check if the parent of the dropped node has become a group node
      if (parent != null && !(parent.getStyle() instanceof PanelNodeStyle)) {
        // let it look like a group node
        graph.setStyle(parent, createGroupNodeStyle(true));

        // add the group node label and remove all other labels
        IListEnumerable<ILabel> labels = parent.getLabels();
        while (labels.size() > 0) {
          graph.remove(labels.getItem(labels.size() - 1));
        }
        addGroupNodeLabel(graph, parent);

        graph.adjustGroupNodeLayout(parent);
      }
      return item;
    });
  }

  /**
   * Enables support for dragging labels from the demo's palette onto nodes
   * and edges in the demo's graph component.
   * @param editorInputMode the demo's main input mode.
   */
  private void configureLabelDropping( GraphEditorInputMode editorInputMode ) {
    // obtain an input mode for handling label drag and drop operations
    labelDropInputMode = editorInputMode.getLabelDropInputMode();
    // by default the mode available in GraphEditorInputMode is disabled, so first enable it
    labelDropInputMode.setEnabled(true);
    // right after a successful label drop, start the TextEditorInputMode to
    // allow users to enter meaningful text 
    labelDropInputMode.setAutoEditingLabelEnabled(true);
    // dynamically create a suitable label model parameter for dropped labels
    labelDropInputMode.setUsingBestMatchingParameterEnabled(true);
  }

  /**
   * Enables support for dragging ports from the demo's palette onto nodes
   * in the demo's graph component.
   * @param editorInputMode the demo's main input mode.
   */
  private void configurePortDropping( GraphEditorInputMode editorInputMode ) {
    // obtain an input mode for handling port drag and drop operations
    portDropInputMode = editorInputMode.getPortDropInputMode();
    // by default the mode available in GraphEditorInputMode is disabled, so first enable it
    portDropInputMode.setEnabled(true);
    // dynamically create a suitable port model parameter for dropped ports
    portDropInputMode.setUsingBestMatchingParameterEnabled(true);
  }

  /**
   * Populates the palette with several normal nodes that use different
   * node styles for visualization and a group node.
   * The elements in the palette can be dragged over and dropped into the demo's
   * <code>GraphComponent</code> to create new elements of the corresponding type
   * in the displayed diagram.
   */
  private void configureNodePalette() {
    // create a new graph in which the palette nodes live
    IGraph nodeContainer = new DefaultGraph();

    // create some node templates
    createGroupNode(nodeContainer, 0, 0, true);
    createGroupNode(nodeContainer, 0, 0, false);
    createNode(nodeContainer, 0, 0, true);
    createNode(nodeContainer, 0, 0, false);

    // create some label templates
    createLabel(nodeContainer, "Node Label", InteriorLabelModel.CENTER, ID_NODE_LABEL);
    createLabel(nodeContainer, "Edge Label", FreeNodeLabelModel.INSTANCE.createDefaultParameter(), ID_EDGE_LABEL);

    // create a port template
    createPort(nodeContainer, FreeNodePortLocationModel.NODE_CENTER_ANCHORED, ID_PORT);

    // fill the model of the palette with sample nodes
    DefaultListModel<INode> model = new DefaultListModel<>();
    nodeContainer.getNodes().forEach(model::addElement);
    palette.setModel(model);

    // set a custom cell renderer painting the sample nodes on the palette
    palette.setCellRenderer(new NodeCellRenderer());

    // configure the palette as source for drag and drop operations
    palette.setDragEnabled(true);
    TransferHandler transferHandler = new MyTransferHandler();
    // set a custom 1x1 drag image to avoid the Mac OS Java default behavior:
    // a rectangle of the size of the cell from the drag source as the drag image.
    DragAndDropSupport.disableDefaultPreview(transferHandler);
    palette.setTransferHandler(transferHandler);

    // allow single selection only and select the first entry
    palette.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    palette.setSelectedIndex(0);
  }


  /**
   * Creates a palette port template in the given graph.
   */
  private void createPort( IGraph graph, IPortLocationModelParameter parameter, String tag ) {
    ShapeNodeStyle shape = new ShapeNodeStyle();
    shape.setShape(ShapeNodeShape.ELLIPSE);
    shape.setPaint(Colors.STEEL_BLUE);
    shape.setPen(new Pen(Colors.STEEL_BLUE.darker()));
    NodeStylePortStyleAdapter style = new NodeStylePortStyleAdapter();
    style.setNodeStyle(shape);
    style.setRenderSize(new SizeD(8, 8));
    INode node = graph.createNode(new RectD(0, 0, 70, 70), VoidNodeStyle.INSTANCE, tag);
    graph.addPort(node, parameter, style);
  }

  /**
   * Creates a palette label template in the given graph.
   */
  private static void createLabel( IGraph graph, String text, ILabelModelParameter parameter, String tag ) {
    DefaultLabelStyle style = new DefaultLabelStyle();
    style.setBackgroundPen(new Pen(Colors.STEEL_BLUE.darker()));
    style.setBackgroundPaint(Colors.STEEL_BLUE);
    style.setTextPaint(Color.WHITE);
    style.setInsets(new InsetsD(4, 4, 4, 4));
    INode node = graph.createNode(new RectD(0, 0, 70, 70), VoidNodeStyle.INSTANCE, tag);
    graph.addLabel(node, text, parameter, style);
  }

  /**
   * Creates a group node in the given graph with a specific styling.
   */
  private static INode createGroupNode(IGraph graph, double x, double y, boolean isValidParent) {
    PanelNodeStyle groupNodeStyle = createGroupNodeStyle(isValidParent);
    INode groupNode = graph.createGroupNode(null, new RectD(x, y, 160, 130), groupNodeStyle, isValidParent);
    addGroupNodeLabel(graph, groupNode);
    return groupNode;
  }

  private static PanelNodeStyle createGroupNodeStyle(boolean isValidParent) {
    Color fillColor = isValidParent ? Colors.FOREST_GREEN : Colors.FIREBRICK;
    PanelNodeStyle groupNodeStyle = new PanelNodeStyle();
    groupNodeStyle.setInsets(new InsetsD(25, 5, 5, 5));
    groupNodeStyle.setColor(fillColor);
    groupNodeStyle.setLabelInsetsColor(fillColor);
    return groupNodeStyle;
  }

  private static void addGroupNodeLabel(IGraph graph, INode groupNode) {
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setTextPaint(Colors.WHITE);
    InteriorStretchLabelModel labelModel = new InteriorStretchLabelModel();
    labelModel.setInsets(new InsetsD(2, 5, 4, 5));
    ILabelModelParameter modelParameter = labelModel.createParameter(InteriorStretchLabelModel.Position.NORTH);
    graph.addLabel(groupNode, "Group Node", modelParameter, labelStyle);
  }

  /**
   * Creates a normal node in the given graph with a specific styling.
   */
  private static INode createNode(IGraph graph, double x, double y, boolean isValidParent) {
    Color fillColor = isValidParent ? Colors.FOREST_GREEN : Colors.FIREBRICK;
    ShinyPlateNodeStyle style = new ShinyPlateNodeStyle();
    style.setPaint(fillColor);
    return graph.createNode(new RectD(x, y, 40, 40), style, isValidParent);
  }

  private void createSampleGraph() {
    IGraph graph = graphComponent.getGraph();

    // create a group node into which dragged nodes can be dropped
    INode group1 = createGroupNode(graph, 100, 150, true);
    graph.addLabel(group1, "Drop a node onto me!", ExteriorLabelModel.SOUTH);

    // create a group node into which dragged nodes cannot be dropped
    INode group2 = createGroupNode(graph, 200, 400, false);
    graph.addLabel(group2, "I don't accept dropped nodes as children!", ExteriorLabelModel.SOUTH);

    // create a node to which dragged nodes can snap
    INode node1 = createNode(graph, 400, 100, false);
    graph.addLabel(node1, "Sample Node", ExteriorLabelModel.NORTH);
    graph.addLabel(node1, "You cannot convert \nme into a group node!", ExteriorLabelModel.SOUTH);

    // create a node which can be converted to a group node automatically, if a node is dropped onto it
    INode node2 = createNode(graph, 550, 300, true);
    graph.addLabel(node2, "Sample Node", ExteriorLabelModel.NORTH);
    graph.addLabel(node2, "Drop a node onto me to \nconvert me to a group node!", ExteriorLabelModel.SOUTH);
  }

  /**
   * Paints {@link com.yworks.yfiles.graph.INode} instances in a
   * {@link javax.swing.JList}.
   */
  private static class NodeCellRenderer implements ListCellRenderer<INode> {
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
      // we use a label as component that renders the list cell and sets the icon that paints the given node
      JLabel label = (JLabel) renderer.getListCellRendererComponent(list, node, index, isSelected, cellHasFocus);
      label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      label.setHorizontalAlignment(SwingConstants.CENTER);
      label.setIcon(getIcon(node));
      label.setText(null);
      Object tag = node.getTag();
      if (ID_EDGE_LABEL.equals(tag)) {
        label.setToolTipText("Edge Label");
      } else if (ID_NODE_LABEL.equals(tag)) {
        label.setToolTipText("Node Label");
      } else if (ID_PORT.equals(tag)) {
        label.setToolTipText("Port");
      } else {
        label.setToolTipText(null);
      }
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
  private static class NodeIcon implements Icon {
    private static final int MAX_SIZE = 70;
    final BufferedImage image;

    NodeIcon(INode node) {
      // create a GraphComponent instance and add a copy of the given node with its labels
      GraphComponent graphComponent = new GraphComponent();
      IGraph graph = graphComponent.getGraph();
      RectD newLayout = new RectD(0, 0, Math.min(MAX_SIZE, node.getLayout().getWidth()), Math.min(MAX_SIZE, node.getLayout().getHeight()));
      INode newNode = graph.createNode(newLayout, node.getStyle(), node.getTag());
      node.getLabels().forEach(label ->
          graph.addLabel(newNode, label.getText(), label.getLayoutParameter(), label.getStyle(), label.getPreferredSize(), label.getTag()));
      node.getPorts().forEach(port ->
          graph.addPort(newNode, port.getLocationParameter(), port.getStyle()));

      // create an image of the node with its labels
      graphComponent.updateContentRect();
      PixelImageExporter pixelImageExporter = new PixelImageExporter(graphComponent.getContentRect().getEnlarged(2));
      pixelImageExporter.setTransparencyEnabled(true);
      image =  pixelImageExporter.exportToBitmap(graphComponent);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.drawImage(image, x, y, null);
    }

    @Override
    public int getIconWidth() {
      return image.getWidth();
    }

    @Override
    public int getIconHeight() {
      return image.getHeight();
    }
  }

  /**
   * Adds a drop-down box to the toolbar for configuring preview and snapping
   * behavior during drag operations.
   */
  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(new JLabel("Enable Function: "));
    toolBar.add(createFunctionsComboBox());
  }

  /**
   * Creates a {@link javax.swing.JComboBox} for configuring preview and
   * snapping behavior during drag operations.
   */
  private JComboBox<String> createFunctionsComboBox() {
    JComboBox<String> comboBox = new JComboBox<>(new String[]{"Snapping & Preview", "Preview", "None"});
    comboBox.setToolTipText("Select drag function");
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setSelectedIndex(0);
    comboBox.addActionListener(e -> {
      int index = comboBox.getSelectedIndex();
      switch (index) {
        case 0:
          // "Snapping & Preview"
          nodeDropInputMode.setSnappingEnabled(true);
          nodeDropInputMode.setPreviewEnabled(true);
          labelDropInputMode.setPreviewEnabled(true);
          portDropInputMode.setPreviewEnabled(true);
          break;
        case 1:
          // "Preview"
          nodeDropInputMode.setSnappingEnabled(false);
          nodeDropInputMode.setPreviewEnabled(true);
          labelDropInputMode.setPreviewEnabled(true);
          portDropInputMode.setSnappingEnabled(false);
          portDropInputMode.setPreviewEnabled(true);
          break;
        case 2:
          // "None
          nodeDropInputMode.setSnappingEnabled(false);
          nodeDropInputMode.setPreviewEnabled(false);
          labelDropInputMode.setPreviewEnabled(false);
          portDropInputMode.setSnappingEnabled(false);
          portDropInputMode.setPreviewEnabled(false);
          break;
      }
    });
    return comboBox;
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new DragAndDropDemo().start();
    });
  }



  /**
   * Transfers {@link com.yworks.yfiles.graph.INode} and
   * {@link com.yworks.yfiles.graph.ILabel} instances from a
   * {@link javax.swing.JList} to another Swing component in the same JVM.
   */
  private static class MyTransferHandler extends TransferHandler {
    /**
     * Data flavor the represents {@link com.yworks.yfiles.graph.INode}
     * instances.
     */
    private final DataFlavor nodeFlavor;
    /**
     * Data flavor the represents {@link com.yworks.yfiles.graph.ILabel}
     * instances.
     */
    private final DataFlavor labelFlavor;
    /**
     * Data flavor the represents {@link com.yworks.yfiles.graph.IPort}
     * instances.
     */
    private final DataFlavor portFlavor;

    /**
     * Initializes a new <code>MyTransferHandler</code> instance for
     * {@link com.yworks.yfiles.graph.INode} and
     * {@link com.yworks.yfiles.graph.ILabel} instances.
     */
    MyTransferHandler() {
      super("selectedValue");
      nodeFlavor = DragAndDropSupport.newFlavor(INode.class);
      labelFlavor = DragAndDropSupport.newFlavor(ILabel.class);
      portFlavor = DragAndDropSupport.newFlavor(IPort.class);
    }

    /**
     * Creates a {@link java.awt.datatransfer.Transferable} instance for the
     * selected value from the given {@link javax.swing.JList}.
     * This method assumes that all values in the {@link javax.swing.JList}
     * are of type {@link com.yworks.yfiles.graph.INode}.
     */
    @Override
    protected Transferable createTransferable( JComponent c ) {
      Object value = ((JList) c).getSelectedValue();
      if (value instanceof INode) {
        INode node = (INode) value;
        if (ID_NODE_LABEL.equals(node.getTag())) {
          return DragAndDropSupport.newTransferable(labelFlavor, node.getLabels().first());
        } else if (ID_EDGE_LABEL.equals(node.getTag())) {
          ILabel labelTemplate = node.getLabels().first();

          //Not all label models return a valid geometry when the path is empty
          SimpleNode src = new SimpleNode();
          src.setLayout(new RectD(0, 0, 1, 1));
          SimpleNode tgt = new SimpleNode();
          tgt.setLayout(new RectD(0, 100, 1, 1));
          SimplePort p1 = new SimplePort(src, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);
          SimplePort p2 = new SimplePort(tgt, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);
          SimpleEdge edge = new SimpleEdge(p1, p2);

          SimpleLabel dummyLabel = new SimpleLabel(edge, labelTemplate.getText(), FreeEdgeLabelModel.INSTANCE.createDefaultParameter());
          dummyLabel.setStyle(labelTemplate.getStyle());
          dummyLabel.setTag(labelTemplate.getTag());
          dummyLabel.setPreferredSize(labelTemplate.getPreferredSize());

          return DragAndDropSupport.newTransferable(labelFlavor, dummyLabel);
        } else if (ID_PORT.equals(node.getTag())) {
          return DragAndDropSupport.newTransferable(portFlavor, node.getPorts().first());
        } else {
          return DragAndDropSupport.newTransferable(nodeFlavor, node);
        }
      } else {
        return null;
      }
    }
  }
}
