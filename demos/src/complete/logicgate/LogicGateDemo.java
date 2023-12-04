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
package complete.logicgate;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InsideOutsidePortLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.PortConstraint;
import com.yworks.yfiles.layout.PortSide;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRouterData;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.PixelImageExporter;
import com.yworks.yfiles.view.input.CreateEdgeInputMode;
import com.yworks.yfiles.view.input.EdgeDirectionPolicy;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IDropCreationCallback;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.ShowPortCandidates;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * This demo shows the usage of ports by taking the example of a digital system consisted of logic gates.
 */
public class LogicGateDemo extends AbstractDemo {

  // the XML namespace that corresponds to the demo package.
  private static final String YFILES_DEMO_NS = "http://www.yworks.com/xml/yfiles-logicgate/java/1.0";

  // the default namespace prefix for {@link #YFILES_DEMO_NS}
  private static final String YFILES_DEMO_PREFIX = "demo";

  // the default style
  private static final LogicGateNodeStyle DEFAULT_STYLE = new LogicGateNodeStyle(LogicGateType.AND);

  private JList<INode> palette;
  private JComboBox<NamedEntry> edgeDirectionPolicyComboBox;
  private JButton hlButton;
  private JButton erButton;


  /**
   * Adds a menu bar and palette to the JRootPane of the application frame in addition to the default
   * graph component, toolbar, and help pane.
   */
  protected void configure(JRootPane rootPane) {
    super.configure(rootPane);

    JMenuBar menuBar = new JMenuBar();
    configureMenu(menuBar);
    rootPane.setJMenuBar(menuBar);

    // set the list that will contain the nodes which may be dragged into the graph component
    // see populatePalette() for the list's contents
    palette = new JList<>();
    JScrollPane pane = new JScrollPane(palette);
    rootPane.getContentPane().add(pane, BorderLayout.WEST);
  }

  /**
   * Configures the given {@link javax.swing.JMenuBar}.
   */
  private void configureMenu(JMenuBar menuBar) {
    JMenu fileMenu = new JMenu("File");
    fileMenu.add(createCommandMenuItemAction("Open", ICommand.OPEN, null, graphComponent));
    fileMenu.add(createCommandMenuItemAction("Save as...", ICommand.SAVE_AS, null, graphComponent));
    fileMenu.add(new AbstractAction("Exit") {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    menuBar.add(fileMenu);

    JMenu viewMenu = new JMenu("View");
    viewMenu.add(createCommandMenuItemAction("Increase zoom", ICommand.INCREASE_ZOOM, null, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Zoom 1:1", ICommand.ZOOM, 1, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Decrease zoom", ICommand.DECREASE_ZOOM, null, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Fit Graph to Bounds", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
    menuBar.add(viewMenu);
  }

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, graphComponent));
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE_AS, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom 1:1", "zoom-original2-16.png", ICommand.ZOOM, 1, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Fit the graph content", "fit2-16.png", ICommand.FIT_CONTENT, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(new JLabel("Edge Direction Policy: "));
    toolBar.add(edgeDirectionPolicyComboBox = createEdgeDirectionPolicyComboBox());
    toolBar.addSeparator();
    toolBar.add(hlButton = createButton("Hierarchic Layout", "layout-hierarchic.png", () -> applyHierarchicLayout()));
    toolBar.add(erButton = createButton("Route Edges", "layout-tree-16.png", () -> applyEdgeRouting()));
  }

  /**
   * Creates a {@link JComboBox} for choosing the {@link EdgeDirectionPolicy}.
   */
  private JComboBox<NamedEntry> createEdgeDirectionPolicyComboBox() {
    NamedEntry[] samples = {
        new NamedEntry("Start at source", EdgeDirectionPolicy.START_AT_SOURCE),
        new NamedEntry("Start at target", EdgeDirectionPolicy.START_AT_TARGET),
        new NamedEntry("Keep direction", EdgeDirectionPolicy.KEEP_DIRECTION),
        new NamedEntry("Determine from port candidates", EdgeDirectionPolicy.DETERMINE_FROM_PORT_CANDIDATES)
    };

    JComboBox<NamedEntry> comboBox = new JComboBox<>(samples);
    comboBox.setMaximumSize(comboBox.getPreferredSize());
    comboBox.setToolTipText("Choose the edge direction policy");

    comboBox.addActionListener(e -> {
      int index = comboBox.getSelectedIndex();
      EdgeDirectionPolicy newPolicy = comboBox.getItemAt(index).value;
      CreateEdgeInputMode createEdgeInputMode = ((GraphEditorInputMode) graphComponent.getInputMode()).getCreateEdgeInputMode();
      createEdgeInputMode.setEdgeDirectionPolicy(newPolicy);
    });

    return comboBox;
  }

  private JButton createButton(String text, String icon, Runnable runnable) {
    JButton button = new JButton(text, createIcon(icon));
    button.setHorizontalTextPosition(SwingConstants.TRAILING);
    button.addActionListener(e -> runnable.run());
    return button;
  }



  @Override
  public void initialize() {
    populateNodesList();
  }

  /**
   * Called when the application frame is displayed.
   * This method initializes the graph and the input mode.
   */
  @Override
  public void onVisible() {
    initializeGraphComponent();
    initializeGraph();
    initializeInputModes();
    edgeDirectionPolicyComboBox.setSelectedIndex(0);
  }

  /**
   * Enables file IO and specifies a namespace for saving types of this demo.
   */
  private void initializeGraphComponent() {
    // enable opening and saving files
    graphComponent.setFileIOEnabled(true);

    // map the classes in the complete.logicgate package to a separate demo namespace
    GraphMLIOHandler ioHandler = graphComponent.getGraphMLIOHandler();
    ioHandler.addXamlNamespaceMapping(YFILES_DEMO_NS, LogicGateNodeStyle.class);
    ioHandler.addNamespace(YFILES_DEMO_NS, YFILES_DEMO_PREFIX);
  }

  /**
   * Initializes the graph instance by setting default styles and loading a small sample graph.
   */
  private void initializeGraph() {
    IGraph graph = graphComponent.getGraph();

    // set the default style for all new nodes
    graph.getNodeDefaults().setStyle(DEFAULT_STYLE);
    graph.getNodeDefaults().setSize(new SizeD(50, 30));

    // set the default style for all new node labels
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setFont(new Font("Dialog", Font.PLAIN, 10));
    graph.getNodeDefaults().getLabelDefaults().setStyle(labelStyle);

    // set the default style for all new edge labels
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setSourceArrow(new Arrow(ArrowType.NONE));
    edgeStyle.setTargetArrow(new Arrow(ArrowType.NONE));
    Pen pen = new Pen(Colors.BLACK, 3);
    pen.setEndCap(BasicStroke.CAP_SQUARE);
    edgeStyle.setPen(pen);
    graph.getEdgeDefaults().setStyle(edgeStyle);

    // disable edge cropping
    graph.getDecorator().getPortDecorator().getEdgePathCropperDecorator().hideImplementation();

    // don't delete ports a removed edge was connected to
    graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);

    // set a custom port candidate provider
    graph.getDecorator().getNodeDecorator().getPortCandidateProviderDecorator().setImplementation(
        new DescriptorDependentPortCandidateProvider());

    // read initial graph from embedded resource
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml").toExternalForm());
    } catch (IOException e) {
      e.printStackTrace();
    }

    // apply a new hierarchic layout
    applyHierarchicLayout();
  }

  /**
   * Calls {@link #createEditorMode()} and registers the result as the {@link CanvasComponent#setInputMode(IInputMode)}.
   */
  private void initializeInputModes() {
    graphComponent.setInputMode(createEditorMode());
  }

  /**
   * Creates the default input mode for the {@link GraphComponent}, a {@link GraphEditorInputMode}.
   * The control uses a custom node creation callback that creates business objects for newly created nodes.
   */
  private IInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    // don't allow nodes to be created using a mouse click
    mode.setCreateNodeAllowed(false);
    // don't allow bends to be created using a mouse drag on an edge
    mode.setCreateBendAllowed(false);
    // disable node resizing
    mode.setShowHandleItems(GraphItemTypes.BEND.or(GraphItemTypes.EDGE));
    // enable orthogonal edge creation and editing
    mode.setOrthogonalEdgeEditingContext(new OrthogonalEdgeEditingContext());
    // enable drag and drop
    mode.getNodeDropInputMode().setEnabled(true);
    // disable moving labels
    mode.getMoveLabelInputMode().setEnabled(false);
    // enable snapping for easier orthogonal edge editing
    mode.setSnapContext(new GraphSnapContext());

    // wrap the original node creator so it copies the ports and labels from the dragged node
    IDropCreationCallback<INode> originalNodeCreator = mode.getNodeDropInputMode().getItemCreator();
    mode.getNodeDropInputMode().setItemCreator((context, graph, draggedNode, dropTarget, layout) -> {
      if (draggedNode != null) {
        SimpleNode simpleNode = new SimpleNode();
        simpleNode.setStyle(draggedNode.getStyle());
        simpleNode.setLayout(draggedNode.getLayout());
        INode newNode = originalNodeCreator.createItem(context, graph, simpleNode, dropTarget, layout);
        // copy the ports
        for (IPort port : draggedNode.getPorts()) {
          PortDescriptor descriptor = (PortDescriptor) port.getTag();

          // specify port style using a node style
          ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
          nodeStyle.setPaint(descriptor.getEdgeDirection() == PortDescriptor.EdgeDirection.IN ?
              Colors.GREEN : Colors.DODGER_BLUE);
          nodeStyle.setPen(null);
          nodeStyle.setShape(ShapeNodeShape.RECTANGLE);

          NodeStylePortStyleAdapter portStyle = new NodeStylePortStyleAdapter(nodeStyle);
          portStyle.setRenderSize(new SizeD(5, 5));

          IPort newPort = graph.addPort(newNode, port.getLocationParameter(), portStyle, port.getTag());

          // create the port labels
          ILabelModelParameter parameter = new InsideOutsidePortLabelModel().createOutsideParameter();
          ILabel label = graph.addLabel(newPort, descriptor.getLabelText(), parameter);
          label.setTag(descriptor);
        }
        // copy the labels
        for (ILabel label : draggedNode.getLabels()) {
          ILabel newLabel = graph.addLabel(newNode, label.getText(), label.getLayoutParameter(), label.getStyle());
          newLabel.setTag(label.getTag());
        }
        return newNode;
      }
      // fallback
      return originalNodeCreator.createItem(context, graph, draggedNode, dropTarget, layout);
    });

    mode.getCreateEdgeInputMode().addEdgeCreatedListener((source, args) -> {
      if (args.getSourcePort().getLabels().size() > 0) {
        ILabel sourcePortLabel = args.getSourcePort().getLabels().first();
        replaceLabelModel(args.getSourcePort(), sourcePortLabel);
      }
      if (args.getTargetPort().getLabels().size() > 0) {
        ILabel targetPortLabel = args.getTargetPort().getLabels().first();
        replaceLabelModel(args.getTargetPort(), targetPortLabel);
      }
    });

    // only allow starting an edge creation over a valid port candidate
    mode.getCreateEdgeInputMode().setStartingOverCandidateOnlyEnabled(true);

    // show all port candidates when hovering over a node
    mode.getCreateEdgeInputMode().setShowPortCandidates(ShowPortCandidates.ALL);

    graphComponent.getGraph().addEdgeRemovedListener((source, args) -> {
      if (args.getSourcePort().getLabels().size() > 0 &&
          graphComponent.getGraph().edgesAt(args.getSourcePort()).size() == 0) {
        ILabel sourcePortLabel = args.getSourcePort().getLabels().first();
        graphComponent.getGraph().setLabelLayoutParameter(sourcePortLabel,
            new InsideOutsidePortLabelModel().createOutsideParameter());
      }
      if (args.getTargetPort().getLabels().size() > 0 &&
          graphComponent.getGraph().edgesAt(args.getTargetPort()).size() == 0) {
        ILabel targetPortLabel = args.getTargetPort().getLabels().first();
        graphComponent.getGraph().setLabelLayoutParameter(targetPortLabel,
            new InsideOutsidePortLabelModel().createOutsideParameter());
      }
    });

    return mode;
  }

  private void replaceLabelModel(IPort port, ILabel label) {
    PortDescriptor descriptor = (PortDescriptor) port.getTag();
    graphComponent.getGraph().setLabelLayoutParameter(label, descriptor.getLabelPlacementWithEdge());
  }

  /**
   * Fill the node list that acts as a source for nodes.
   */
  private void populateNodesList() {
    // Create a new Graph in which the palette nodes live and copy all relevant settings
    IGraph nodeContainer = new DefaultGraph();
    nodeContainer.getNodeDefaults().setStyle(DEFAULT_STYLE);
    nodeContainer.getNodeDefaults().setSize(new SizeD(50, 30));

    // Create some nodes
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.AND, "AND", null);
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.NAND, "NAND", null);
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.OR, "OR", null);
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.NOR, "NOR", null);
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.NOT, "NOT", null);
    // Create an IC
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.TIMER, "555", new SizeD(70, 120));
    createNode(nodeContainer, PointD.ORIGIN, LogicGateType.AD_CONVERTER, "2-bit A/D\nConverter", new SizeD(70, 120));

    // fill the model of the palette with sample nodes
    DefaultListModel<INode> model = new DefaultListModel<>();
    nodeContainer.getNodes().forEach(model::addElement);
    palette.setModel(model);

    // set a custom cell renderer painting the sample nodes on the palette
    palette.setCellRenderer(new NodeCellRenderer());

    // configure the palette as source for drag and drop operations
    palette.setDragEnabled(true);
    TransferHandler transferHandler = new TransferHandler("selectedValue");
    transferHandler.setDragImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    palette.setTransferHandler(transferHandler);

    // allow single selection only and select the first entry
    palette.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    palette.setSelectedIndex(0);
  }

  /**
   * Creates a node of the specified type.
   * The method will specify the ports that the node should have based on its type.
   */
  private void createNode(IGraph graph, PointD location, LogicGateType type, String label, SizeD size) {
    RectD newBounds = RectD.fromCenter(location, graph.getNodeDefaults().getSize());
    INode node;
    if (type == LogicGateType.TIMER || type == LogicGateType.AD_CONVERTER) {
      ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
      nodeStyle.setPen(new Pen(Colors.BLACK, 2));
      node = graph.createNode(RectD.fromCenter(location, size), nodeStyle);
    } else {
      node = graph.createNode(newBounds, new LogicGateNodeStyle(type));
    }

    graph.addLabel(node, label, InteriorLabelModel.CENTER);

    PortDescriptor[] portDescriptors = PortDescriptor.createPortDescriptors(type);

    // use relative port locations
    FreeNodePortLocationModel model = new FreeNodePortLocationModel();

    // add ports for all descriptors using the descriptor as the tag of the port
    for (PortDescriptor descriptor : portDescriptors) {
      // use the descriptor's location as offset
      IPortLocationModelParameter portLocationModelParameter =
          model.createParameter(PointD.ORIGIN, new PointD(descriptor.getX(), descriptor.getY()));
      IPort port = graph.addPort(node, portLocationModelParameter);
      port.setTag(descriptor);
    }
  }


  private void applyHierarchicLayout() {
    HierarchicLayout hl = new HierarchicLayout();
    hl.setOrthogonalRoutingEnabled(true);
    hl.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
    hl.setNodeLabelConsiderationEnabled(true);

    HierarchicLayoutData hlData = new HierarchicLayoutData();
    configurePortConstraints(hlData);

    applyLayout(hl, hlData, true);
  }

  private void applyEdgeRouting() {
    EdgeRouter er = new EdgeRouter();
    er.setNodeLabelConsiderationEnabled(true);

    EdgeRouterData erData = new EdgeRouterData();
    configurePortConstraints(erData);

    applyLayout(er, erData, false);
  }

  private void configurePortConstraints( LayoutData layoutData ) {
    // outgoing edges must be routed to the right of the node
    // we use the same value for all edges, which is a strong port constraint that forces
    // the edge to leave at the east (right) side
    PortConstraint east = PortConstraint.create(PortSide.EAST, true);
    // incoming edges must be routed to the left of the node
    // we use the same value for all edges, which is a strong port constraint that forces
    // the edge to enter at the west (left) side
    PortConstraint west = PortConstraint.create(PortSide.WEST, true);

    Function<IEdge, PortConstraint> sourceFunction =
            edge -> ((PortDescriptor) edge.getSourcePort().getTag()).getX() == 0 ? west : east;
    Function<IEdge, PortConstraint> targetFunction =
            edge -> ((PortDescriptor) edge.getTargetPort().getTag()).getX() == 0 ? west : east;

    if (layoutData instanceof HierarchicLayoutData) {
      HierarchicLayoutData hlData = (HierarchicLayoutData) layoutData;
      hlData.setSourcePortConstraints(sourceFunction);
      hlData.setTargetPortConstraints(targetFunction);
    } else if (layoutData instanceof EdgeRouterData) {
      EdgeRouterData erData = (EdgeRouterData) layoutData;
      erData.setSourcePortConstraints(sourceFunction);
      erData.setTargetPortConstraints(targetFunction);
    }
  }

  /**
   * Perform the layout operation.
   */
  private void applyLayout(ILayoutAlgorithm layout, LayoutData layoutData, boolean animateViewport) {
    // layout starting, disable button
    hlButton.setEnabled(false);
    erButton.setEnabled(false);
    // do the layout
    LayoutExecutor executor = new LayoutExecutor(graphComponent, layout);
    executor.setLayoutData(layoutData);
    executor.setDuration(Duration.ofSeconds(1));
    executor.setViewportAnimationEnabled(animateViewport);
    executor.addLayoutFinishedListener( (source, args) -> {
      // layout finished, enable layout button again
      hlButton.setEnabled(true);
      erButton.setEnabled(true);
    });
    executor.start();
  }


  /**
   * Paints {@link INode} instances in a {@link JList}.
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
    private static final int MAX_SIZE = 120;
    final BufferedImage image;

    NodeIcon(INode node) {
      // create a GraphComponent instance and add a copy of the given node with its labels
      GraphComponent graphComponent = new GraphComponent();
      RectD newLayout = new RectD(0, 0, Math.min(MAX_SIZE, node.getLayout().getWidth()), Math.min(MAX_SIZE, node.getLayout().getHeight()));
      INode newNode = graphComponent.getGraph().createNode(newLayout, node.getStyle(), node.getTag());
      node.getLabels().forEach(label ->
          graphComponent.getGraph().addLabel(newNode, label.getText(), label.getLayoutParameter(), label.getStyle(), label.getPreferredSize(), label.getTag()));

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
   * Name-value struct for combo box entries.
   */
  private static class NamedEntry {
    final String displayName;
    final EdgeDirectionPolicy value;

    NamedEntry(String displayName, EdgeDirectionPolicy value) {
      this.displayName = displayName;
      this.value = value;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new LogicGateDemo().start();
    });
  }
}
