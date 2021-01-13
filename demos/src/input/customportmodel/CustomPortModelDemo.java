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
package input.customportmodel;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.PortsHandleProvider;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

/**
 * Demo code that shows how to implement custom {@link com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel}.
 */
public class CustomPortModelDemo extends AbstractDemo {

  /**
   * Adds a menu bar to the JRootPane of the application frame in addition to the default
   * graph component, toolbar, and help pane.
   */
  protected void configure(JRootPane rootPane) {
    super.configure(rootPane);

    JMenuBar menuBar = new JMenuBar();
    configureMenu(menuBar);
    rootPane.setJMenuBar(menuBar);
  }

  /**
   * Configures the given {@link javax.swing.JMenuBar}.
   * @param menuBar the {@link javax.swing.JMenuBar} to configure
   */
  private void configureMenu(JMenuBar menuBar) {
    JMenu fileMenu = new JMenu("File");
    fileMenu.add(createCommandMenuItemAction("Open", ICommand.OPEN, null, graphComponent));
    fileMenu.add(createCommandMenuItemAction("Save as...", ICommand.SAVE_AS, null, graphComponent));
    fileMenu.add(createExitAction());
    menuBar.add(fileMenu);

    JMenu editMenu = new JMenu("Edit");
    editMenu.add(createCommandMenuItemAction("Cut", ICommand.CUT, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Copy", ICommand.COPY, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Paste", ICommand.PASTE, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Delete", ICommand.DELETE, null, graphComponent));
    menuBar.add(editMenu);

    JMenu viewMenu = new JMenu("View");
    viewMenu.add(createCommandMenuItemAction("Increase zoom", ICommand.INCREASE_ZOOM, null, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Decrease zoom", ICommand.DECREASE_ZOOM, null, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Fit Graph to Bounds", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
    menuBar.add(viewMenu);
  }

  /**
   * Creates an {@link javax.swing.Action} to exit the demo.
   */
  private Action createExitAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    };
    action.putValue(Action.NAME, "Exit");
    return action;
  }

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    // initialize the graph
    initializeGraph(graphComponent.getGraph());
    initializeStructure(graphComponent.getGraph());

    // configure XML namespaces for I/O
    graphComponent.setGraphMLIOHandler(createGraphMLIOHandler());
    // enable load and save for the demo's graphs
    graphComponent.setFileIOEnabled(true);

    // initialize the input mode
    initializeInputModes();
  }

  /**
   * Calls {@link #createEditorMode()} and registers the result as the {@link CanvasControl#getInputMode()}.
   */
  private void initializeInputModes() {
    graphComponent.setInputMode(createEditorMode());
  }

  /**
   * Creates the default input mode for the GraphComponent, a {@link GraphEditorInputMode}.
   */
  private IInputMode createEditorMode() {
    return new GraphEditorInputMode();
  }

  /**
   * Callback used by the decorator in {@link #createEditorMode()}.
   */
  private IPortCandidateProvider getPortCandidateProvider(INode forNode) {
    MyNodePortLocationModel model = new MyNodePortLocationModel(10);
    return IPortCandidateProvider.fromCandidates(
    new DefaultPortCandidate(forNode, model.createParameter(PortLocation.CENTER)),
    new DefaultPortCandidate(forNode, model.createParameter(PortLocation.NORTH)),
    new DefaultPortCandidate(forNode, model.createParameter(PortLocation.EAST)),
    new DefaultPortCandidate(forNode, model.createParameter(PortLocation.SOUTH)),
    new DefaultPortCandidate(forNode, model.createParameter(PortLocation.WEST)));
  }

  /**
   * Sets a custom node port model parameter instance for newly created node ports in the graph, creates a example nodes
   * with a ports using the our model and an edge to connect the ports.
   */
  private void initializeGraph(IGraph graph) {
    INodeDefaults nodeDefaults = graph.getNodeDefaults();
    nodeDefaults.getPortDefaults().setLocationParameter(new MyNodePortLocationModel().createParameter(PortLocation.CENTER));
    ShapeNodeStyle innerPortNodeStyle = new ShapeNodeStyle();
    innerPortNodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    innerPortNodeStyle.setPaint(Colors.RED);
    nodeDefaults.getPortDefaults().setStyle(new NodeStylePortStyleAdapter(innerPortNodeStyle));
    nodeDefaults.setSize(new SizeD(50, 50));
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Colors.ORANGE);
    nodeDefaults.setStyle(nodeStyle);

    NodeDecorator nodeDecorator = graph.getDecorator().getNodeDecorator();
    // for selected nodes show the handles
    nodeDecorator.getHandleProviderDecorator().setFactory(PortsHandleProvider::new);
    // for nodes add a custom port candidate provider implementation which uses our model
    nodeDecorator.getPortCandidateProviderDecorator().setFactory(this::getPortCandidateProvider);
  }

  /**
   * Creates the structure of the demo's sample graph.
   */
  private void initializeStructure(IGraph graph) {
    INode source = graph.createNode(new RectD(90, 90, 100, 100));
    INode target = graph.createNode(new RectD(250, 90, 100, 100));

    // creates a port using the default declared above
    IPort sourcePort = graph.addPort(source);
    // creates a port using the custom model instance
    IPort targetPort = graph.addPort(target, new MyNodePortLocationModel(10).createParameter(
            PortLocation.NORTH));

    // create an edge
    graph.createEdge(sourcePort, targetPort);
  }

  /**
   * Creates a GraphML reader/writer with a "nice", specific XML namespace
   * registered for types from this demo (or rather from
   * {@link MyNodePortLocationModel}'s package).
   */
  private GraphMLIOHandler createGraphMLIOHandler() {
    String namespace = "http://www.yworks.com/yfiles-for-java/CustomPortModel/1.0";
    GraphMLIOHandler handler = new GraphMLIOHandler();
    handler.addXamlNamespaceMapping(namespace, MyNodePortLocationModel.class);
    handler.addNamespace(namespace, "demo");
    return handler;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new CustomPortModelDemo().start();
    });
  }
}