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
package complete.uml;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.UndoEngine;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayoutData;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRouterData;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.input.GraphEditorInputMode;

import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.ItemClickedEventArgs;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import toolkit.AbstractDemo;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import java.time.Duration;

/**
 * This demo allows you to visualize and edit UML class diagrams. It shows how to
 * <ul>
 *  <li>create a custom node style that provides interactive buttons to show
 *  or hide parts of the node and to add or remove labels.</li>
 *  <li>load and save the UML model data.</li>
 *  <li>write a customized {@link com.yworks.yfiles.view.input.CreateEdgeInputMode}
 *  for creating UML relations.</li>
 *  <li>add a layout action that calculates a layout well-suited for UML diagrams.</li>
 *  <li>route edges incrementally after graph changes.</li>
 * </ul>
 */
public class UmlDemo extends AbstractDemo {
  /**
   * A {@link ICommand} that is used to layout the given graph.
   */
  private static final ICommand RUN_LAYOUT = ICommand.createCommand("RunLayout");

  /**
   * The namespace URI for yFiles uml extensions to graphml.
   * <p>
   * This field has the constant value {@code http://www.yworks.com/xml/yfiles-uml/java/1.0}
   * </p>
   */
  private static final String YFILES_UML_NS = "http://www.yworks.com/xml/yfiles-uml/java/1.0";

  /**
   * The default namespace prefix for {@link #YFILES_UML_NS}.
   * <p>
   * This field has the constant value {@code "uml"}
   * </p>
   * @see #YFILES_UML_NS
   */
  private final String YFILES_UML_PREFIX = "uml";
  
  @Override
  protected void configure( JRootPane rootPane ) {
    super.configure(rootPane);

    JMenuBar menuBar = new JMenuBar();
    configureMenuBar(menuBar);
    rootPane.setJMenuBar(menuBar);
  }

  /**
   * Configures the given {@link javax.swing.JMenuBar}.
   * @param menuBar the {@link javax.swing.JMenuBar} to configure
   */
  private void configureMenuBar( JMenuBar menuBar ) {
    JMenu fileMenu = new JMenu("File");
    fileMenu.add(createCommandMenuItemAction("New", ICommand.NEW, null, graphComponent));
    fileMenu.add(createCommandMenuItemAction("Open", ICommand.OPEN, null, graphComponent));
    fileMenu.add(createCommandMenuItemAction("Save as...", ICommand.SAVE_AS, null, graphComponent));
    fileMenu.addSeparator();
    fileMenu.add(createCommandMenuItemAction("Print", ICommand.PRINT, null, graphComponent));
    fileMenu.add(createCommandMenuItemAction("Print preview...", ICommand.PRINT_PREVIEW, null, graphComponent));
    fileMenu.addSeparator();
    fileMenu.add(new AbstractAction("Exit") {
      @Override
      public void actionPerformed( ActionEvent e ) {
        System.exit(0);
      }
    });
    menuBar.add(fileMenu);
  }

  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    super.configureToolBar(toolBar);

    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png", ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Cut", "cut-16.png", ICommand.CUT, null, graphComponent));
    toolBar.add(createCommandButtonAction("Copy", "copy-16.png", ICommand.COPY, null, graphComponent));
    toolBar.add(createCommandButtonAction("Paste", "paste-16.png", ICommand.PASTE, null, graphComponent));
    toolBar.add(createCommandButtonAction("Delete", "delete3-16.png", ICommand.DELETE, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Run Layout", "layout-orthogonal-16.png", RUN_LAYOUT, null, graphComponent));
  }

  @Override
  public void initialize() {
    // initialize handler for use during serialization and deserialization
    graphComponent.setGraphMLIOHandler(newGraphMLIOHandler());

    // initialize the graph
    initializeGraph(graphComponent.getGraph());

    // initialize the input mode
    initializeInputModes(graphComponent);
  }

  void initializeGraph( IGraph graph ) {
    // enable undo/redo commands
    graph.setUndoEngineEnabled(true);
    graph.lookup(UndoEngine.class).setUnitMergingEnabled(false);

    graph.getEdgeDefaults().setStyle(UmlStyleFactory.createAssociationStyle());
    graph.getNodeDefaults().setStyle(new UmlNodeStyle());
    graph.getNodeDefaults().setSize(calculateDefaultSize());
  }

  private static SizeD calculateDefaultSize() {
    IGraph graph = new DefaultGraph();
    INode node = graph.createNode();
    node.setTag(new UmlClassModel());
    graph.setStyle(node, new UmlNodeStyle());
    UmlClassLabelSupport.updateAllLabels(graph, node);
    UmlClassLabelSupport.updateNodeSize(graph, node);
    IRectangle nl = node.getLayout();
    return new SizeD(Math.ceil(nl.getWidth()), Math.ceil(nl.getHeight()));
  }

  /**
   * Helper that determines whether the {@link #RUN_LAYOUT} can be executed.
   */
  private boolean canExecuteLayout(ICommand command, Object parameter, Object source) {
    IGraph graph = graphComponent.getGraph();
    return graph != null && graph.getNodes().size() != 0;
  }

  /**
   * Handler for the {@link #RUN_LAYOUT} command.
   */
  private boolean executeLayout(ICommand command, Object parameter, Object source) {
    // create a directed orthogonal layouter with reversed layout orientation to place the target nodes of the directed
    // edges above their source nodes
    OrthogonalLayout layout = new OrthogonalLayout();
    layout.setLayoutOrientation(LayoutOrientation.BOTTOM_TO_TOP);

    OrthogonalLayoutData layoutData = new OrthogonalLayoutData();
    // mark all inheritance edges (generalization, realization) as directed so their target nodes
    // will be placed above their source nodes
    // all other edges are treated as undirected
    layoutData.setDirectedEdges(edge -> UmlStyleFactory.isInheritance(edge.getStyle()));
    // combine all edges with a white delta as target arrow (generalization, realization) in edge groups according to
    // their line type
    // do not group the other edges
    layoutData.setTargetGroupIds(edge -> {
      if (edge.getStyle() instanceof PolylineEdgeStyle) {
        PolylineEdgeStyle style = (PolylineEdgeStyle) edge.getStyle();
        return UmlStyleFactory.isRealization(style) ? style.getPen().getDashStyle() : null;
      }
      return null;
    });

    //We use Layout executor convenience method that already sets up the whole layout pipeline correctly
    LayoutExecutor layoutExecutor = new LayoutExecutor(graphComponent, layout);
    layoutExecutor.setDuration(Duration.ofMillis(500));
    layoutExecutor.setViewportAnimationEnabled(true);
    layoutExecutor.setLayoutData(layoutData);
    layoutExecutor.start();
    return true;
  }
  
  void initializeInputModes( GraphComponent graphComponent ) {
    // enable open/save commands
    graphComponent.setFileIOEnabled(true);
    graphComponent.getGraph().getUndoEngine().setUnitMergingEnabled(true);
    graphComponent.getGraph().getUndoEngine().setAutoMergeDuration(Duration.ofMillis(1000));

    OpacityHandler handler = new OpacityHandler();
    graphComponent.addMouse2DMovedListener(handler);
    graphComponent.addMouse2DExitedListener(handler);

    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.addItemClickedListener(this::selectLabelAt);
    geim.setNodeCreator(this::newUmlNode);

    UmlCreateEdgeInputMode createEdgeInputMode = new UmlCreateEdgeInputMode();
    createEdgeInputMode.setPriority(geim.getCreateEdgeInputMode().getPriority());
    geim.setCreateEdgeInputMode(createEdgeInputMode);
    
    UmlEdgeCreationButtonsInputMode umlEdgeCreationButtonsInputMode = new UmlEdgeCreationButtonsInputMode();
    umlEdgeCreationButtonsInputMode.setPriority(geim.getClickInputMode().getPriority() - 1);
    geim.add(umlEdgeCreationButtonsInputMode);

    KeyboardInputMode keyboardInputMode = geim.getKeyboardInputMode();
    keyboardInputMode.addCommandBinding(RUN_LAYOUT, this::executeLayout, this::canExecuteLayout);

    geim.getMoveInputMode().addDragFinishedListener((o, inputModeEventArgs) -> routeEdgesAtSelectedNodes());
    geim.getHandleInputMode().addDragFinishedListener((o, inputModeEventArgs) -> routeEdgesAtSelectedNodes());
    geim.getCreateEdgeInputMode().addEdgeCreatedListener((o, inputModeEventArgs) -> routeEdge(inputModeEventArgs.getItem()));

    geim.addLabelTextChangedListener(( source, args ) -> {
      ILabelOwner owner = args.getOwner();
      if (owner instanceof INode) {
        UmlClassLabelSupport.adopt((INode) owner, args.getItem());
      }
    });
    graphComponent.setInputMode(geim);
  }

  /**
   * Routes all edges that connect to selected nodes. This is used when a selection of nodes is moved or resized.
   */
  private void routeEdgesAtSelectedNodes() {
    EdgeRouter edgeRouter = new EdgeRouter();
    edgeRouter.setScope(Scope.ROUTE_EDGES_AT_AFFECTED_NODES);

    EdgeRouterData routerData = new EdgeRouterData();
    routerData.setAffectedNodes(node -> graphComponent.getSelection().isSelected(node));

    LayoutExecutor layoutExecutor = new LayoutExecutor(graphComponent, edgeRouter);
    layoutExecutor.setLayoutData(routerData);
    layoutExecutor.setDuration(Duration.ofMillis(500));
    layoutExecutor.setViewportAnimationEnabled(false);
    layoutExecutor.setContentRectUpdatingEnabled(false);
    layoutExecutor.start();
  }

  /**
   * Routes just the given edge without adjusting the view port. This is used for applying an initial layout to newly
   * created edges.
   * @param affectedEdge The edge to route.
   */
  private void routeEdge(IEdge affectedEdge) {
    EdgeRouter edgeRouter = new EdgeRouter();
    edgeRouter.setScope(Scope.ROUTE_AFFECTED_EDGES);

    EdgeRouterData routerData = new EdgeRouterData();
    routerData.setAffectedEdges(edge -> edge.equals(affectedEdge));

    LayoutExecutor layoutExecutor = new LayoutExecutor(graphComponent, edgeRouter);
    layoutExecutor.setLayoutData(routerData);
    layoutExecutor.setDuration(Duration.ofMillis(500));
    layoutExecutor.setViewportAnimationEnabled(false);
    layoutExecutor.setContentRectUpdatingEnabled(false);
    layoutExecutor.start();
  }

  private GraphMLIOHandler newGraphMLIOHandler() {
    GraphMLIOHandler ioh = new GraphMLIOHandler();
    // map the classes in the complete.uml package to a separate UML namespace
    ioh.addXamlNamespaceMapping(YFILES_UML_NS, ModelExtension.class);
    ioh.addNamespace(YFILES_UML_NS, YFILES_UML_PREFIX);
    return ioh;
  }

  /**
   * Loads and centers a sample graph in the graph component.
   */
  public void onVisible() {
    // loads the example graph
    loadSampleGraph();
  }

  /**
   * Loads a sample graph.
   */
  private void loadSampleGraph() {
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/shopping.graphml"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a new UML node in response to a click on empty space.
   */
  private INode newUmlNode(
          IInputModeContext context, IGraph graph, PointD location, INode parent
  ) {
    RectD layout = RectD.fromCenter(location, graph.getNodeDefaults().getSize());
    INodeStyle style = graph.getNodeDefaults().getStyleInstance();
    INode node = graph.createNode(parent, layout, style, new UmlClassModel());
    UmlClassLabelSupport.updateAllLabels(graph, node);
    UmlClassLabelSupport.updateNodeSize(graph, node);

    IInputMode inputMode = context.getParentInputMode();
    if (inputMode instanceof GraphEditorInputMode) {
      ((GraphEditorInputMode) inputMode).snapToGrid(context, node);
    }

    return node;
  }

  /**
   * Checks if there is an UML attribute or operation label at the clicked
   * location and if that is the case selects the aforementioned label.
   */
  private void selectLabelAt( Object source, ItemClickedEventArgs<IModelItem> args ) {
    IModelItem item = args.getItem();
    if (item instanceof INode) {
      PointD p = args.getLocation();
      UmlClassLabelSupport.selectListItemAt(
              ((GraphEditorInputMode) source).getGraph(), (INode) item, p);
    }
  }

  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new UmlDemo().start();
    });
  }

}
