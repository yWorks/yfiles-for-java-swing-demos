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
package input.customlabelmodel;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

/**
 * This demo shows how to create and use a custom {@link ILabelModel} that provides either continuous or discrete label
 * positions directly outside the node border.
 */
public class CustomLabelModelDemo extends AbstractDemo {

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {

    // initializes the input mode
    createInputMode();

    // configure XML namespaces for I/O
    initializeGraphMLIOHandler();

    // initializes a sample graph
    initializeGraph();
  }

  /**
   * Initializes the GraphML reader/writer with a "nice", specific XML namespace
   * registered for types from this demo
   */
  private void initializeGraphMLIOHandler() {
    String namespace = "http://www.yworks.com/yfiles-for-java/CustomPortModel/1.0";
    GraphMLIOHandler handler = graphComponent.getGraphMLIOHandler();
    handler.addXamlNamespaceMapping(namespace, MyNodeLabelLocationModel.class);
    handler.addNamespace(namespace, "demo");
  }

  /**
   * Sets a custom node label model parameter instance for newly created
   * node labels in the graph, creates an example node with a label using
   * the default parameter and another node with a label without restrictions
   * on the number of possible placements.
   */
  private void initializeGraph() {
    IGraph graph = graphComponent.getGraph();

    DemoStyles.initDemoStyles(graph);

    graph.getNodeDefaults().setSize(new SizeD(50, 50));
    INode node1 = graph.createNode(new RectD(250, 90, 100, 100));
    INode node2 = graph.createNode(new RectD(90, 90, 100, 100));

    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(new MyNodeLabelLocationModel().createDefaultParameter());
    graph.addLabel(node1, "Click and Drag To Snap");

    MyNodeLabelLocationModel myNodeLabelLocationModel = new MyNodeLabelLocationModel();
    myNodeLabelLocationModel.setCandidateCount(0);
    myNodeLabelLocationModel.setOffset(20);
    graph.addLabel(node2, "Click and Drag", myNodeLabelLocationModel.createDefaultParameter());
  }

  /**
   * Creates the input mode
   */
  private void createInputMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    graphComponent.setInputMode(mode);
    graphComponent.setFileIOEnabled(true);
  }

  /**
   * Adjusts the view by the first start of the demo
   */
  @Override
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

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
    fileMenu.addSeparator();
    fileMenu.add(new AbstractAction("Exit") {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    menuBar.add(fileMenu);
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new CustomLabelModelDemo().start();
    });
  }
}
