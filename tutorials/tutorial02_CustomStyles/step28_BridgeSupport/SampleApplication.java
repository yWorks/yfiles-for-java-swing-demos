/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package tutorial02_CustomStyles.step28_BridgeSupport;

import com.yworks.yfiles.view.Animator;
import com.yworks.yfiles.view.IAnimation;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.view.GraphObstacleProvider;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.BridgeManager;
import com.yworks.yfiles.utils.ItemEventArgs;

import javax.swing.AbstractAction;
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
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;

/**
 * Enable bridges for a custom edge style.
 */
public class SampleApplication {
  private GraphComponent graphComponent;

  private Animator animator;
  private IAnimation animation;

  // variable to remember the state of the animation
  private boolean animatorIsRunning;


  //////////////// New in this sample ////////////////
  /**
   * Adds and configures the {@link com.yworks.yfiles.view.BridgeManager}.
   */
  private void configureBridges() {
    // The graph item styles are responsible for both providing obstacles and drawing bridges.
    // the bridge manager collects the obstacles and updates given edge paths to add bridges.
    // for an example implementation, have a look at the create/get path methods in the
    // MySimpleEdgeStyle class of this demo.
    BridgeManager bridgeManager = new BridgeManager();

    // convenience class that just queries all model items
    GraphObstacleProvider provider = new GraphObstacleProvider();

    // register the obstacle provider to the BridgeManager. It will query all registered
    // obstacle providers to determine if a bridge must be created.
    bridgeManager.addObstacleProvider(provider);
    // bind the bridge manager to the GraphComponent...
    bridgeManager.setCanvasComponent(graphComponent);
  }
  ////////////////////////////////////////////////////

  /**
   * Creates a sample graph, specifies the default styles and sets the default input mode.
   */
  private void initialize() {
    // initialize the input mode
    graphComponent.setInputMode(createEditorMode());

    // initialize the default styles for newly created graph items
    initializeDefaultStyles();

    // create some graph elements with the above defined styles
    createSampleGraph();

    // show the whole graph in the view
    graphComponent.fitGraphBounds();

    // initialize an animation that moves the gradient paint of selected edges
    initializeSelectedEdgeAnimation();

    //////////////// New in this sample ////////////////
    // add bridge support by configuring a BridgeManager and binding it to the graph component
    configureBridges();
    ////////////////////////////////////////////////////
  }

  /**
   * Creates the default input mode for the GraphComponent, a {@link com.yworks.yfiles.view.input.GraphEditorInputMode}.
   * @return a new GraphEditorInputMode instance
   */
  private IInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    // enable label editing
    mode.setEditLabelAllowed(true);
    return mode;
  }

  /**
   * Initializes an animation that sets the value of a cycling time counter to the tag of the selected edges. The edge
   * style uses this value to move the gradient of the edge paint along the time. The animation starts as soon as at
   * least one edge has been selected and ends when no more edges are selected.
   */
  private void initializeSelectedEdgeAnimation() {
    // create an animator that automatically updates the GraphComponent
    // and allows user interaction while the animation is running
    animator = new Animator(graphComponent);
    animator.setAutoUpdateEnabled(true);
    animator.setUserInteractionAllowed(true);

    // create an animation that sets the value of a cycling time counter to the tag of the selected edges
    animation = IAnimation.createCyclicAnimation(new IAnimation() {
      @Override
      public void initialize() {
      }

      @Override
      public void animate(double time) {
        // set the value of a time counter to the tag of the selected edges
        graphComponent.getSelection().getSelectedEdges().forEach(edge -> edge.setTag(time));
      }

      @Override
      public void cleanUp() {
      }

      @Override
      public Duration getPreferredDuration() {
        return Duration.ofMillis(666);
      }
    }, Duration.ofDays(100));

    // trigger startAnimator if the selection of the edges has been changed
    graphComponent.getSelection().getSelectedEdges().addItemSelectionChangedListener(this::startAnimator);
  }

  /**
   * Start the animation when at least one edge has been selected and stops it when no edge is selected
   */
  public void startAnimator(Object source, ItemEventArgs<IEdge> evt) {
    if (graphComponent.getSelection().getSelectedEdges().size() > 0) {
      // don't start if the animator is still running, because this would reset the timeline values and lead to stutters in the animation
      if (!animatorIsRunning) {
        animatorIsRunning = true;
        animator.animate(animation);
      }
    } else {
      // stop all animations in the animator
      animator.stop();
      animatorIsRunning = false;
    }
  }

  /**
   * Initializes the default styles that are used as templates for newly created graph items.
   */
  private void initializeDefaultStyles() {
    IGraph graph = graphComponent.getGraph();

    // create a new style and use it as default node style
    graph.getNodeDefaults().setStyle(new MySimpleNodeStyle());
    // create a new style and use it as default edge style
    graph.getEdgeDefaults().setStyle(new MySimpleEdgeStyle());
    // use 50x50 as default node size
    graph.getNodeDefaults().setSize(new SizeD(50, 50));
  }

  /**
   * Creates the initial sample graph.
   */
  private void createSampleGraph() {
    IGraph graph = graphComponent.getGraph();
    INode node0 = graph.createNode(new RectD(-100, -50, 30, 30));
    INode node1 = graph.createNode(new RectD(100, -50, 30, 30));
    IEdge edge0 = graph.createEdge(node0, node1);
    INode node2 = graph.createNode(new RectD(-100, 50, 30, 30));
    INode node3 = graph.createNode(new RectD(100, 50, 30, 30));
    IEdge edge2 = graph.createEdge(node2, node3);
    INode node4 = graph.createNode(new RectD(-50, -100, 30, 30));
    INode node5 = graph.createNode(new RectD(-50, 100, 30, 30));
    IEdge edge3 = graph.createEdge(node4, node5);
    INode node6 = graph.createNode(new RectD(50, -100, 30, 30));
    INode node7 = graph.createNode(new RectD(50, 100, 30, 30));
    IEdge edge4 = graph.createEdge(node6, node7);
  }

  /**
   * Initializes a new <code>SampleApplication</code> instance. Creates a {@link javax.swing.JFrame} with a {@link
   * com.yworks.yfiles.view.GraphComponent} in the center, a help pane on the right and a {@link javax.swing.JToolBar}
   * on the top.
   * @param title The title of the application.
   */
  public SampleApplication(String title) {
    JFrame frame = createFrame(title);
    frame.add(graphComponent = new GraphComponent(), BorderLayout.CENTER);
    frame.add(createHelpPane(), BorderLayout.EAST);
    frame.add(createToolBar(), BorderLayout.NORTH);
    frame.setVisible(true);
  }

  /**
   * Creates a {@link javax.swing.JFrame} with the given title.
   */
  private JFrame createFrame(String title) {
    JFrame frame = new JFrame(title);
    frame.setIconImages(Arrays.asList(
        createIcon("logo_29.png").getImage(),
        createIcon("logo_36.png").getImage(),
        createIcon("logo_48.png").getImage(),
        createIcon("logo_57.png").getImage(),
        createIcon("logo_129.png").getImage()));
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

  /**
   * Creates a {@link javax.swing.JToolBar} with components to change the visualization of the graph.
   */
  private JToolBar createToolBar() {
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.add(createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom 1:1", "zoom-original2-16.png", ICommand.ZOOM, 1, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Fit content", "fit2-16.png", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
    return toolBar;
  }

  /**
   * Creates an {@link javax.swing.Action} for the given command used for buttons in a toolbar.
   * @param tooltip   The text to show as tooltip.
   * @param icon      The icon to show.
   * @param command   The command to execute.
   * @param parameter The parameter for the execution of the command.
   * @param target    The target to execute the command on.
   * @return an {@link javax.swing.Action} for the given command.
   */
  private Action createCommandButtonAction(
      String tooltip, String icon, ICommand command, Object parameter, JComponent target) {
    Action action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        command.execute(parameter, target);
      }
    };
    command.addCanExecuteChangedListener((source, args) -> action.setEnabled(command.canExecute(parameter, target)));
    action.putValue(Action.SHORT_DESCRIPTION, tooltip);
    action.putValue(Action.SMALL_ICON, createIcon(icon));
    return action;
  }

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
    EventQueue.invokeLater(() -> new SampleApplication("Step 28 - Bridge Support").initialize());
  }
}
