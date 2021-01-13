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
package style.jcomponentstyle;

import com.yworks.yfiles.graph.labelmodels.EdgeSegmentLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPortDefaults;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.ComponentInputMode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.graph.ITagOwner;
import com.yworks.yfiles.view.ModifierKeys;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.ICommand;
import toolkit.AbstractDemo;

import javax.swing.JToolBar;
import javax.swing.UIManager;
import java.awt.EventQueue;
import java.io.IOException;

/**
 * Use {@link com.yworks.yfiles.graph.styles.AbstractJComponentNodeStyle},
 * {@link com.yworks.yfiles.graph.styles.AbstractJComponentNodeStyle}, and
 * {@link com.yworks.yfiles.graph.styles.AbstractJComponentPortStyle} to create complex node and label visualizations using
 * Swing components.
 * <p>
 * The yFiles for Java (Swing) component styles support arbitrary Swing <code>JComponent</code>s for graph item visualization
 * while being able to access all relevant context information as client properties.
 * </p>
 * <p>
 * So-called "tags" are used for data binding:
 * <ul>
 *   <li>the {@link ITagOwner#getTag() user-tag} to bind business data to graph items,</li>
 *   <li>the {@link com.yworks.yfiles.graph.styles.AbstractJComponentStyle#getStyleTag() style-tag} to bind visualization dependent data to
 *   implementations of {@link com.yworks.yfiles.graph.styles.AbstractJComponentStyle}.</li>
 * </ul>
 * </p>
 * <p>
 * Note: Swing components are not intended to be zoomed. Therefore zooming will cause rendering artifacts and problems
 * when interacting with complex components.
 * </p>
 */
public class JComponentStyleDemo extends AbstractDemo {

  private static final String JCOMPONENT_DEMO_NAMESPACE = "http://www.yworks.com/yfiles-for-java/demos/jcomponentstyles/1.0";

  private ComponentNodeStyle customerNodeStyle;
  private ComponentNodeStyle productNodeStyle;

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    // enable file operation and add buttons to load and save a graph
    graphComponent.setFileIOEnabled(true);
    toolBar.add(createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, graphComponent));
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE_AS, null, graphComponent));
    toolBar.addSeparator();
    super.configureToolBar(toolBar);
  }

  /**
   * Initializes this demo by configuring the graph defaults, the input mode, and GraphMLIOHandler.
   */
  public void initialize() {

    // set the graph defaults
    initializeGraphDefaults();

    // disable the default visualization for selection, focus and highlighting
    disableDefaultStateVisualization();

    // initializes an editor input mode with customized behavior for node and edge creation
    initializeInputMode();

    // register a GraphMLIOHandler that uses a custom namespace mapping for the business data objects for all IO operations.
    initializeGraphMLIOHandler();
  }

  /**
   * Loads and centers a sample graph in the graph component.
   */
  public void onVisible() {
    // load the initial graph containing several Customer and Product nodes as well as Relations
    loadSampleGraph();
  }

  /**
   * Initialize the graph defaults.
   */
  private void initializeGraphDefaults() {
    final IGraph graph = graphComponent.getGraph();

    customerNodeStyle = new CustomerNodeStyle();
    productNodeStyle = new ProductNodeStyle();

    // set the customer node style as the default and use an initial size for all nodes
    graph.getNodeDefaults().setStyle(customerNodeStyle);
    graph.getNodeDefaults().setSize(new SizeD(160, 80));

    // set edge style
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(new Pen(Colors.DARK_GRAY, 2));
    edgeStyle.setTargetArrow(IArrow.TRIANGLE);
    graph.getEdgeDefaults().setStyle(edgeStyle);
    // set the relation label style as the default
    graph.getEdgeDefaults().getLabelDefaults().setStyle(new ComponentLabelStyle());
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(new EdgeSegmentLabelModel().createParameterFromSource(0, 0.5, EdgeSides.ON_EDGE));

    // set the component port style as the default
    IPortDefaults ports = graph.getNodeDefaults().getPortDefaults();
    ports.setStyle(new ComponentPortStyle());
    ports.setLocationParameter(FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED);
    ports.setLocationParameterInstanceSharingEnabled(true);
    ports.setAutoCleanUpEnabled(false);
  }

  /**
   * Disable most default state visualizations. The demo's component styles
   * provide their own custom visualization for these states.
   */
  private void disableDefaultStateVisualization() {
    // disable default highlight and focus decoration
    this.graphComponent.getHighlightIndicatorManager().setEnabled(false);
    this.graphComponent.getFocusIndicatorManager().setEnabled(false);

    // disable default selection decoration for nodes, labels and ports
    // (edges keep their default selection decoration)
    GraphDecorator graphDecorator = graphComponent.getGraph().getDecorator();
    graphDecorator.getNodeDecorator().getSelectionDecorator().hideImplementation();
    graphDecorator.getLabelDecorator().getSelectionDecorator().hideImplementation();
    graphDecorator.getPortDecorator().getSelectionDecorator().hideImplementation();

    // make sure the component still gets repainted when the selection changes
    graphComponent.getSelection().addItemSelectionChangedListener((source, evt) -> graphComponent.repaint());
  }

  /**
   * Sets a GraphEditorInputMode with customized behavior for node and edge creation and adds a ComponentInputMode
   * as child mode.
   */
  private void initializeInputMode() {
    final GraphEditorInputMode geim = new GraphEditorInputMode();
    // don't show resize handles
    geim.setShowHandleItems(GraphItemTypes.NONE);
    // disable default label editing for the ComponentLabelStyles
    geim.setEditLabelAllowed(false);

    // use a custom callback for clicks on the empty canvas to customize node creation
    geim.addCanvasClickedListener((source, args) -> canvasClicked(geim, args));

    // Use a custom portBasedEdgeCreator for the CreateEdgeInputMode that adds a label to the newly created edge and
    // sets up the correct business object.
    // Note that only edges from Customers to Products are allowed.
    geim.getCreateEdgeInputMode().setEdgeCreator((ctx, graph, sourcePC, targetPC, style) -> {
      Object sourceTag = sourcePC.getOwner().getTag();
      Object targetTag = targetPC.getOwner().getTag();
      if (sourceTag instanceof Customer && targetTag instanceof Product) {
        final IEdge edge = graph.createEdge(sourcePC.getPort(), targetPC.getPort());
        final Relation relation = new Relation((Customer) sourceTag, (Product) targetTag);
        ILabel relationLabel = graph.addLabel(edge, relation.toString());
        relationLabel.setTag(relation);
        return edge;
      } else {
        return null;
      }
    });

    // Use a custom port candidate provider that restricts edge creation and only allows edges from
    // Customer to Product nodes.
    graphComponent.getGraph().getDecorator().getNodeDecorator().getPortCandidateProviderDecorator().setFactory( CustomerProductPortCandidateProvider::new);

    // add a ComponentInputMode that allows editing the text fields and check boxes on the node components.
    ComponentInputMode componentInputMode = new ComponentInputMode();
    componentInputMode.setPriority(-50);
    geim.add(componentInputMode);

    graphComponent.setInputMode(geim);
  }

  /**
   * Creates a new Customer or Product node if the SHIFT or CTRL modifier was
   * pressed during the mouse click represented by the given click event args.
   */
  private void canvasClicked(GraphEditorInputMode geim, ClickEventArgs args) {
    // always clear the old selection
    graphComponent.getSelection().clear();

    Object tag;
    ComponentNodeStyle style;
    int id = (int) (Math.random() * 99999);

    if (ModifierKeys.SHIFT.and(args.getModifiers()).equals(ModifierKeys.SHIFT)) {
      // SHIFT + click -> create customer
      tag = new Customer("Max", id, "New York");
      style = customerNodeStyle;
    } else if (ModifierKeys.SHORTCUT.and(args.getModifiers()).equals(ModifierKeys.SHORTCUT)) {
      // SHORTCUT + click -> create product
      tag = new Product("Burger", id, true);
      style = productNodeStyle;
    } else {
      // only click -> do nothing
      args.setHandled(true);
      graphComponent.repaint();
      return;
    }

    // calculate node bounds and create node
    IGraph graph = graphComponent.getGraph();
    SizeD size = graph.getNodeDefaults().getSize();
    RectD bounds = RectD.fromCenter(args.getLocation(), size);
    INode node = graph.createNode(bounds, style, tag);

    // update the node size to respect the preferred size of the component visualizing the node
    style.updateNodeSize(graphComponent.createRenderContext(), node);

    // add a port to the new node
    graph.addPort(node);

    // set the new node as current item
    geim.setCurrentItem(node);
    args.setHandled(true);
  }

  /**
   * Register a GraphMLIOHandler that supports the demo's custom styles and user
   * data (Customer, Product, Relation) for all IO operations.
   */
  private void initializeGraphMLIOHandler() {
    final GraphMLIOHandler ioh = new GraphMLIOHandler();

    // add namespace mapping for saving/loading of custom classes in graphml
    ioh.addXamlNamespaceMapping(JCOMPONENT_DEMO_NAMESPACE, getClass());
    ioh.addNamespace(JCOMPONENT_DEMO_NAMESPACE,"demo");

    // we set the IO handler on the GraphComponent, so the GraphComponent's IO methods
    // will pick up our handler for use during serialization and deserialization.
    graphComponent.setGraphMLIOHandler(ioh);
  }

  /**
   * Loads a sample graph.
   */
  private void loadSampleGraph() {
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/example.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new JComponentStyleDemo().start("JComponentStyle Demo - yFiles for Java (Swing)");
    });
  }

  /**
   * Initializes the look and feel of the application's GUI.
   * By default the application uses the look and feel that is native to the 
   * system it is running on. However, the system look and feel of MacOSX cannot 
   * properly handle scaled graphics contexts. Therefore we use the Java look 
   * and feel (also called "Metal") in that case.
   */
  protected static void initLnF() {
    if (isMacOSX()) {
      try {
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      AbstractDemo.initLnF();
    }
  }
  
  private static boolean isMacOSX() {
    String os = System.getProperty("os.name");
    return os.toLowerCase().startsWith("mac");
  }

}
