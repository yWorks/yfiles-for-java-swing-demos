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
package tutorial01_GettingStarted.step03_ManagingTheViewPort;

import com.yworks.yfiles.graph.labelmodels.InsideOutsidePortLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.view.input.CommandAction;
import com.yworks.yfiles.view.input.ICommand;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * <h1>Step 3: Managing the Viewport</h1>
 * <p>
 * Work with the viewport and the zooming and fitContent features.
 * </p>
 * <p>
 * Please see the file help.html for more details.
 * </p>
 * @see #updateViewport()
 */
public class SampleApplication {
  private GraphComponent graphComponent;

  ///////////////////////////////////////////////////////
  //////////// YFILES STUFF /////////////////////////////
  ///////////////////////////////////////////////////////

  /**
   * Initializes the application after its user interface has been built up.
   */
  private void initialize() {
    // Creates a sample graph and introduces all important graph elements present in yFiles: nodes, edges, bends, ports
    // and labels.
    populateGraph();

    //////////// New in this Sample ///////////////////////
    // Updates the content rectangle that encloses the graph and adjust the zoom level to show the whole graph in the
    // view.
    updateViewport();

    // Creates a node outside the initial content rectangle
    IGraph graph = getGraph();
    INode node4 = graph.createNode(new PointD(-200, -200));
    ILabel outsiderLabel = graph.addLabel(node4, "Outside initial viewport", ExteriorLabelModel.SOUTH);
    //////////////////////////////////////////////////////
  }

  /**
   * Creates a sample graph and introduces all important graph elements present in yFiles. Additionally, this method
   * specifies the label placement for some specific labels.
   */
  private void populateGraph() {
    IGraph graph = getGraph();

    //////////// Sample node creation ///////////////////
    // Creates two nodes with the default node size
    // The location is specified for the center
    INode node1 = graph.createNode(new PointD(50, 50));
    INode node2 = graph.createNode(new PointD(150, 50));
    // Creates a third node with a different size of 80x40
    // In this case, the location of (360,380) describes the upper left
    // corner of the node bounds
    INode node3 = graph.createNode(new RectD(360, 380, 80, 40));
    /////////////////////////////////////////////////////

    //////////// Sample edge creation ///////////////////
    // Creates some edges between the nodes
    IEdge edge1 = graph.createEdge(node1, node2);
    IEdge edge2 = graph.createEdge(node2, node3);
    /////////////////////////////////////////////////////

    //////////// Using Bends ////////////////////////////
    // Creates the first bend for edge2 at (400, 50)
    IBend bend1 = graph.addBend(edge2, new PointD(400, 50));
    /////////////////////////////////////////////////////

    //////////// Using Ports ////////////////////////////
    // Actually, edges connect "ports", not nodes directly.
    // If necessary, you can manually create ports at nodes
    // and let the edges connect to these.
    // Creates a port in the center of the node layout
    IPort port1AtNode1 = graph.addPort(node1, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);

    // Creates a port at the middle of the left border
    // Note to use absolute locations in world coordinates when placing ports using PointD.
    // The method obtains a model parameter that best matches the given port location.
    IPort port1AtNode3 = graph.addPort(node3, new PointD(node3.getLayout().getX(), node3.getLayout().getCenter().getY()));

    // Creates an edge that connects these specific ports
    IEdge edgeAtPorts = graph.createEdge(port1AtNode1, port1AtNode3);
    /////////////////////////////////////////////////////

    //////////// Sample label creation ///////////////////
    // Adds labels to several graph elements
    graph.addLabel(node1, "N 1");
    graph.addLabel(node2, "N 2");
    graph.addLabel(node3, "N 3");
    graph.addLabel(edgeAtPorts, "Edge at Ports");
    graph.addLabel(port1AtNode3, "Port at Node", new InsideOutsidePortLabelModel().createOutsideParameter());
    /////////////////////////////////////////////////////
  }

  //////////// New in this Sample ///////////////////////
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
   * viewport (i.e. the world coordinate rectangle that corresponds to the
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
  void updateViewport() {
    // Uncomment the following line to update the content rectangle
    // to include all graph elements
    // This should result in correct scrolling behaviour:

    // graphComponent.updateContentRect();

    // Additionally, we can also set the zoom level so that the
    // content rectangle fits exactly into the viewport area:
    // Uncomment this line in addition to UpdateContentRect:
    // Note that this changes the zoom level (i.e. the graph elements will look smaller)

    // graphComponent.fitContent();

    // The sequence above is equivalent to just calling:
    graphComponent.fitGraphBounds();
  }
  ///////////////////////////////////////////////////////

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
   * com.yworks.yfiles.view.GraphComponent} in the center, a help pane on the right and a {@link javax.swing.JToolBar}
   * on the top.
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

    //////////// New in this Sample ///////////////////////
    frame.add(createToolBar(), BorderLayout.NORTH);
    ///////////////////////////////////////////////////////

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

  //////////// New in this Sample ///////////////////////
  /**
   * Creates a {@link javax.swing.JToolBar} with components to change the visualization of the graph.
   */
  private JToolBar createToolBar() {
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    // Add buttons to the toolbar with actions to increase and decrease the zoom level and adjust the viewport that the
    // content rectangle encompasses all existing graph elements. When activating a button, the action performs the
    // associated command.
    toolBar.add(createCommandButtonAction("plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("zoom-original2-16.png", ICommand.ZOOM, 1, graphComponent));
    toolBar.add(createCommandButtonAction("minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("fit2-16.png", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
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
  ///////////////////////////////////////////////////////

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
    EventQueue.invokeLater(() -> new SampleApplication("Step 3 - Managing the Viewport").initialize());
  }
}
