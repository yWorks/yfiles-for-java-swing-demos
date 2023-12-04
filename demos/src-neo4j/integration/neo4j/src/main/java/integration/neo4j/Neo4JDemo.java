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
package integration.neo4j;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.builder.EdgesSource;
import com.yworks.yfiles.graph.builder.GraphBuilder;
import com.yworks.yfiles.graph.builder.NodesSource;
import com.yworks.yfiles.graph.labelmodels.EdgePathLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.organic.ChainSubstructureStyle;
import com.yworks.yfiles.layout.organic.CycleSubstructureStyle;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.organic.ParallelSubstructureStyle;
import com.yworks.yfiles.layout.organic.StarSubstructureStyle;
import com.yworks.yfiles.layout.radial.CenterNodesPolicy;
import com.yworks.yfiles.layout.radial.RadialLayout;
import com.yworks.yfiles.layout.radial.RadialLayoutData;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.EdgeStyleDecorationInstaller;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.HighlightIndicatorManager;
import com.yworks.yfiles.view.NodeStyleDecorationInstaller;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.StyleDecorationZoomPolicy;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import org.neo4j.driver.AccessMode;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.neo4j.driver.internal.value.StringValue;
import org.neo4j.driver.types.Entity;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import toolkit.DemoStyles;
import toolkit.Themes;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Loads data from a Neo4j database and displays it in a {@link GraphComponent}.
 */
public class Neo4JDemo {
  private final GraphComponent graphComponent;

  private Session session;

  /**
   * Initializes the application after its user interface has been built up.
   */
  public void initialize() {
    initializeGraphDefaults();
    initializeHighlighting();
    initializeInputMode();
    loadDataBaseGraph();
    doLayout();
  }

  /**
   * Initializes the database and loads a graph from it.
   *
   * The initialization is inside a loop to try a different configuration in case any error happens.
   */
  private void loadDataBaseGraph() {
    while(true) {
      try {
        String initializationError = initializeDataBase();
        if (initializationError != null) {
          JOptionPane.showMessageDialog(graphComponent, initializationError, "Could not load initial graph", JOptionPane.ERROR_MESSAGE);
          continue;
        }
        loadGraph();
        break;
      } catch (Exception e) {
        JOptionPane.showMessageDialog(graphComponent, e.getMessage(), "Could not load initial graph", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Initializes the graph defaults.
   */
  private void initializeGraphDefaults() {
    IGraph graph = graphComponent.getGraph();

    // set the default node styles
    DemoStyles.initDemoStyles(graph, Themes.PALETTE_LIGHTBLUE);
    graph.getNodeDefaults().setStyle(DemoStyles.createDemoShapeNodeStyle(ShapeNodeShape.ELLIPSE, Themes.PALETTE_LIGHTBLUE));

    // and configure node labels to be truncated if they exceed a certain size
    ((DefaultLabelStyle) graph.getNodeDefaults().getLabelDefaults().getStyle()).setMaximumSize(new SizeD(116, 36));

    ExteriorLabelModel newExteriorLabelModel = new ExteriorLabelModel();
    newExteriorLabelModel.setInsets(new InsetsD(5));
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(
        newExteriorLabelModel.createParameter(ExteriorLabelModel.Position.SOUTH));

    // and finally specify the placement policy for edge labels.
    ILabelModelParameter labelModelParameter =
        new EdgePathLabelModel(3, 0, 0, true, EdgeSides.ABOVE_EDGE).createDefaultParameter();
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(labelModelParameter);
  }

  /**
   * Configures highlight styling. See the GraphViewer demo for more details.
   */
  private void initializeHighlighting() {
    Pen orangePen = new Pen(Colors.ORANGE_RED, 3);

    GraphDecorator decorator = graphComponent.getGraph().getDecorator();

    ShapeNodeStyle highlightShape = new ShapeNodeStyle();
    highlightShape.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    highlightShape.setPen(orangePen);
    highlightShape.setPaint(null);

    NodeStyleDecorationInstaller nodeStyleHighlight = new NodeStyleDecorationInstaller();
    nodeStyleHighlight.setNodeStyle(highlightShape);
    nodeStyleHighlight.setMargins(new InsetsD(5));
    nodeStyleHighlight.setZoomPolicy(StyleDecorationZoomPolicy.VIEW_COORDINATES);
    decorator.getNodeDecorator().getHighlightDecorator().setImplementation(nodeStyleHighlight);

    Arrow dummyCroppingArrow = new Arrow(ArrowType.NONE,null, null, 5, 1);

    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(orangePen);
    edgeStyle.setTargetArrow(dummyCroppingArrow);
    edgeStyle.setSourceArrow(dummyCroppingArrow);

    EdgeStyleDecorationInstaller edgeStyleHighlight = new EdgeStyleDecorationInstaller();
    edgeStyleHighlight.setEdgeStyle(edgeStyle);
    edgeStyleHighlight.setZoomPolicy(StyleDecorationZoomPolicy.VIEW_COORDINATES);
    decorator.getEdgeDecorator().getHighlightDecorator().setImplementation(edgeStyleHighlight);
  }

  /**
   * Initializes and configures support for (limited) user interaction.
   * Aside from panning and zooming, double-clicking on a node will arrange
   * the graph using the radial layout algorithm.
   */
  private void initializeInputMode() {
    GraphViewerInputMode inputMode = new GraphViewerInputMode();
    inputMode.setClickableItems(GraphItemTypes.NODE);
    inputMode.setFocusableItems(GraphItemTypes.NODE);
    inputMode.setSelectableItems(GraphItemTypes.NONE);
    inputMode.getMarqueeSelectionInputMode().setEnabled(false);

    inputMode.getItemHoverInputMode().setEnabled(true);
    // we are only interested in hover events for nodes and edges
    inputMode.getItemHoverInputMode().setHoverItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    // hovering over other elements should not result in hover events
    inputMode.getItemHoverInputMode().setInvalidItemsDiscardingEnabled(false);

    // when the user hovers over a node, we want to highlight all nodes that are reachable from this node
    inputMode.getItemHoverInputMode().addHoveredItemChangedListener((sender, args) -> {
      // we use the highlight manager of the GraphComponent to highlight related items
      HighlightIndicatorManager<IModelItem> manager = graphComponent.getHighlightIndicatorManager();
      // first remove previous highlights
      manager.clearHighlights();
      // then see where we are hovering over, now
      if (args.getItem() != null) {
        // we highlight the item itself
        manager.addHighlight(args.getItem());
        // and if it is a node, we highlight all connected edges, too
        if (args.getItem() instanceof INode) {
          graphComponent.getGraph().edgesAt((INode) args.getItem()).forEach(manager::addHighlight);
        } else if (args.getItem() instanceof IEdge) {
          // if it is an edge - we highlight the connected nodes
          manager.addHighlight(((IEdge) args.getItem()).getSourceNode());
          manager.addHighlight(((IEdge) args.getItem()).getTargetNode());
        }
      }
    });

    // display a tooltip when the mouse hovers over an item
    inputMode.addQueryItemToolTipListener((sender, args) -> {
      // the neo4j data is stored in the "tag" property of the item
      // if it contains "properties", show them in a simple list
      IModelItem item = args.getItem();
      Entity entity = item != null && item.getTag() instanceof Entity ? ((Entity) item.getTag()) : null;
      Map<String, Object> properties = entity != null  ? entity.asMap() : null;
      if (properties != null && !properties.isEmpty()) {
        StringBuilder tooltipText = new StringBuilder("<html><ul>");
        properties.forEach((key, value) ->
          tooltipText.append("<li>").append(key).append(" : ").append(properties.get(key)).append("</li>")
        );
        tooltipText.append("</ul></html>");
        args.setToolTip(tooltipText.toString());
      }
    });

    // when the user double-clicks on a node, we want to focus that node in a radial layout
    inputMode.addItemDoubleClickedListener((sender, args) -> {
      // clicks could also be on a label, edge, port, etc.
      if (args.getItem() instanceof INode) {
        // tell the engine that we don't want the default action for double-clicks to happen
        args.setHandled(true);
        // we configure the layout data
        RadialLayoutData layoutData = new RadialLayoutData();
        // and tell it to put the item into the center
        layoutData.setCenterNodes((INode) args.getItem());
        // we build the layout algorithm
        RadialLayout layout = new RadialLayout();
        layout.setCenterNodesPolicy(CenterNodesPolicy.CUSTOM);
        // now we calculate the layout and morph the results
        graphComponent.morphLayout(layout, Duration.ofSeconds(2), layoutData);
      }
    });

    graphComponent.setInputMode(inputMode);
  }

  /**
   * Initializes a connection to a Neo4J database.
   *
   * @return Returns <code>null</code> if the initialization succeeds and the message of the error otherwise.
   */
  private String initializeDataBase() {
    String message = "Neo4j data base configuration";

    Neo4jConfigurationPanel configPanel = new Neo4jConfigurationPanel();
    int response = JOptionPane.showConfirmDialog(graphComponent, configPanel, message, JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE);

    if (response == JOptionPane.OK_OPTION) {
      DBInformation db = configPanel.getDBInformation();
      if (db.url != null && db.url.length() > 0
          && db.username != null && db.username.length() > 0
          && db.password != null && db.password.length() > 0) {
        try {
          AuthToken authToken = AuthTokens.basic(db.username, db.password);
          Driver driver = GraphDatabase.driver(db.url, authToken);
          SessionConfig.Builder builder = db.name.isEmpty()
              ? SessionConfig.builder().withDefaultAccessMode(AccessMode.READ)
              : SessionConfig.builder().withDefaultAccessMode(AccessMode.READ).withDatabase(db.name);
          session = driver.session(builder.build());
        } catch (Exception e) {
          return "No valid Neo4j data base connection could be established: " + e.getMessage();
        }
      } else {
        return "No valid data base configuration was specified.";
      }
    } else {
      return "No Neo4j data base specified.";
    }
    return null;
  }

  /**
   * Loads the graph from the database.
   */
  private void loadGraph() {
    // first we query a limited number of arbitrary nodes
    // modify the query to suit your requirement!
    Result nodeResult = session.run("MATCH (node) RETURN node LIMIT 25");
    // we put the resulting records in a separate list
    List<Node> nodes = nodeResult.stream()
        .map(record -> record.get("node").asNode())
        .collect(Collectors.toList());

    // with the node ids we can query the edges between the nodes
    Long[] nodeIds = nodes.stream().map(Entity::id).toArray(Long[]::new);
    Result edgeResult = session.run(
      "MATCH (n)-[edge]-(m) " +
          "WHERE id(n) IN $nodes " +
          "AND id(m) IN $nodes " +
          "RETURN DISTINCT edge LIMIT 100",
        Values.parameters("nodes", nodeIds)
    );
    // and store the edges in a list
    List<Relationship> edges = edgeResult.stream()
        .map(record -> record.get("edge").asRelationship())
        .collect(Collectors.toList());

    // this triggers the initial construction of the graph
    GraphBuilder graphBuilder = createGraphBuilder(graphComponent.getGraph(), nodes, edges);
    graphBuilder.buildGraph();
  }

  /**
   * Returns a GraphBuilder that is configured to work well with Neo4J query results.
   */
  private GraphBuilder createGraphBuilder(IGraph graph, List<Node> nodes, List<Relationship> edges) {
    // now we create the helper class that will help us build the graph from the data
    GraphBuilder graphBuilder = new GraphBuilder(graph);

    // now we pass it the collection of nodes and tell it how to identify the nodes
    NodesSource<Node> nodesSource = graphBuilder.createNodesSource(nodes, Entity::id);
    Map<String, ShapeNodeStyle> nodeToStyle = createNodeStyleMapping(nodes);

    // whenever a node is created...
    nodesSource.getNodeCreator().setStyleProvider(n4jNode -> {
      // look for a mapping for any of the nodes labels and use the mapped style
      for (String labelName : nodeToStyle.keySet()) {
        if (n4jNode.hasLabel(labelName)) {
          return nodeToStyle.get(labelName);
        }
      }
      return nodesSource.getNodeCreator().getDefaults().getStyleInstance();
    });

    // as well as what text to use as the first label for each node
    nodesSource.getNodeCreator().createLabelBinding(node -> {
      // try to find a suitable node label
      String[] candidates = {"name", "title", "firstName", "lastName", "email", "content"};
      for (String candidate : candidates) {
        if (node.containsKey(candidate)) {
          // trim the label
          Value labelValue = node.get(candidate);
          String label = labelValue instanceof StringValue ? labelValue.asString() : labelValue.toString();
          return label.length() > 30 ? label.substring(0, 30) : label;
        }
      }
      return String.join(" - ", node.labels());
    });

    // specify how to identify the source and target nodes - this matches the nodeIdBinding above
    EdgesSource<Relationship> edgesSource = graphBuilder.createEdgesSource(edges, Relationship::startNodeId,
        Relationship::endNodeId);
    // and we display the label, too, using the type of the relationship
    edgesSource.getEdgeCreator().createLabelBinding(Relationship::type);

    return graphBuilder;
  }

  /**
   * Creates a mapping between node labels and node styles.
   */
  private Map<String, ShapeNodeStyle> createNodeStyleMapping(List<Node> nodes) {
    Map<String, Integer> labelCount = new HashMap<>();
    List<String> labels = new ArrayList<>();

    for (Node n4jNode : nodes) {
      for (String label : n4jNode.labels()) {
        if (!(labelCount.containsKey(label))) {
          labelCount.put(label, 0);
          labels.add(label);
        }
        labelCount.put(label, labelCount.get(label) + 1);
      }
    }

    // sort unique labels by their frequency
    labels.sort(Comparator.comparingInt(labelCount::get));
    // define some distinct looking styles
    ShapeNodeStyle[] styles = {
        DemoStyles.createDemoShapeNodeStyle(ShapeNodeShape.TRIANGLE, Themes.PALETTE_ORANGE),
        DemoStyles.createDemoShapeNodeStyle(ShapeNodeShape.DIAMOND, Themes.PALETTE_GREEN),
        DemoStyles.createDemoShapeNodeStyle(ShapeNodeShape.RECTANGLE, Themes.PALETTE_BLUE),
        DemoStyles.createDemoShapeNodeStyle(ShapeNodeShape.HEXAGON, Themes.PALETTE_PURPLE),
        DemoStyles.createDemoShapeNodeStyle(ShapeNodeShape.ELLIPSE, Themes.PALETTE_LIGHTBLUE),
    };
    // map label names to styles
    Map<String, ShapeNodeStyle> labelToStyle = new HashMap<>();
    for (int i = 0; i < labels.size(); i++) {
      labelToStyle.put(labels.get(i), styles[i % styles.length]);
    }
    return labelToStyle;
  }

  /**
   * Applies an organic layout to the current graph. Tries to highlight substructures in the process.
   */
  private void doLayout() {
    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setChainSubstructureStyle(ChainSubstructureStyle.STRAIGHT_LINE);
    organicLayout.setCycleSubstructureStyle(CycleSubstructureStyle.CIRCULAR);
    organicLayout.setParallelSubstructureStyle(ParallelSubstructureStyle.STRAIGHT_LINE);
    organicLayout.setStarSubstructureStyle(StarSubstructureStyle.SEPARATED_RADIAL);
    organicLayout.setMinimumNodeDistance(60);
    organicLayout.setNodeLabelConsiderationEnabled(true);
    organicLayout.setNodeSizeConsiderationEnabled(true);
    organicLayout.setDeterministicModeEnabled(true);
    organicLayout.setParallelEdgeRouterEnabled(false);

    LayoutExecutor executor = new LayoutExecutor(graphComponent, organicLayout);
    executor.setDuration(Duration.ofSeconds(1));
    executor.setViewportAnimationEnabled(true);
    executor.start();
  }

  private void closeSession() {
    if (session == null) {
      return;
    }

    try {
      session.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  ///////////////////////////////////////////////////////
  //////////// GUI STUFF ////////////////////////////////
  ///////////////////////////////////////////////////////

  /**
   * Creates a {@link javax.swing.JFrame} with a {@link
   * com.yworks.yfiles.view.GraphComponent} in the center and a help pane on the right.
   * @param title The title of the application.
   */
  public Neo4JDemo(String title) {
    JFrame frame = createFrame(title);
    graphComponent = new GraphComponent();
    frame.add(graphComponent, BorderLayout.CENTER);
    frame.add(createHelpPane(), BorderLayout.EAST);
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
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        cleanup(e);
      }

      public void windowClosed(WindowEvent e) {
        cleanup(e);
      }

      private void cleanup(WindowEvent e) {
        e.getWindow().removeWindowListener(this);
        closeSession();
      }
    });
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
   * Creates an {@link javax.swing.ImageIcon} from the specified file located in the resources folder.
   */
  static ImageIcon createIcon(String name) {
    return new ImageIcon(Neo4JDemo.class.getResource("/resources/" + name));
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> new Neo4JDemo("Neo4J Demo - yFiles for Java (Swing)").initialize());
  }
}

