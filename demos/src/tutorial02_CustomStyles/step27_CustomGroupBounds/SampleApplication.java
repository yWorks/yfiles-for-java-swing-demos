/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package tutorial02_CustomStyles.step27_CustomGroupBounds;

import com.yworks.yfiles.graph.labelmodels.NinePositionsEdgeLabelModel;
import com.yworks.yfiles.view.Animator;
import com.yworks.yfiles.view.IAnimation;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
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
 * Improve the visual representation of folder and group nodes further by
 * <ul>
 *   <li>
 *    customizing the way the group insets are calculated by implementing an
 *    {@link com.yworks.yfiles.graph.IGroupBoundsCalculator} to include
 *    the node labels.
 *   </li>
 *   <li>
 *     changing the visual representation of folder nodes compared to group nodes.
 *   </li>
 *   <li>
 *     moving the collapse/expand button to the south-east corner of the group node card.
 *   </li>
 * </ul>
 *
 */
public class SampleApplication {
  private GraphComponent graphComponent;

  private Animator animator;
  private IAnimation animation;

  // variable to remember the state of the animation
  private boolean animatorIsRunning;


  //////////////// New in this sample ////////////////
  /**
   * Adjusts the group bounds to enclose the given node label.
   * @param label the label to enclose
   */
  private void adjustGroupBounds(ILabel label) {
    if (label != null && label.getOwner() instanceof INode) {
      INode node = (INode) label.getOwner();
      IGraph graph = graphComponent.getGraph();
      IFoldingView foldingView = graph.getFoldingView();
      if (foldingView != null) {
        // traverse the hierarchy up to the root to adjust the bounds of all ancestors of the node
        while (node != null) {
          if (graph.isGroupNode(node)) {
            graph.adjustGroupNodeLayout(node);
          }
          node = graph.getParent(node);
        }
      }
    }
  }
  ////////////////////////////////////////////////////

  /**
   * Creates a sample graph, specifies the default styles and sets the default input mode.
   */
  private void initialize() {
    // configures the default style for group nodes
    configureGroupNodeStyles();

    // from now on, everything can be done on the actual managed view instance
    enableFolding();

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
  }

  /**
   * Configures the default style for group nodes.
   */
  private void configureGroupNodeStyles() {
    // use the custom group style
    graphComponent.getGraph().getGroupNodeDefaults().setStyle(new MyGroupNodeStyle());
  }

  /**
   * Enables folding - changes the GraphComponent's graph to a managed view that provides the actual collapse/expand state.
   */
  private void enableFolding() {
    // creates the folding manager and sets its master graph to
    // the single graph that has served for all purposes up to this point
    FoldingManager manager = new FoldingManager(graphComponent.getGraph());
    // creates a managed view from the master graph and
    // replaces the existing graph view with a managed view
    graphComponent.setGraph(manager.createFoldingView().getGraph());
    wrapGroupNodeStyles();
  }

  /**
   * Changes the default style for group nodes. We use {@link com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator}
   * to wrap the group style, since we want to have nice -/+ buttons for collapsing/expanding. The {@link
   * com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecoratorRenderer renderer} is customized to change the
   * button visualization.
   */
  private void wrapGroupNodeStyles() {
    IFoldingView foldingView = graphComponent.getGraph().getFoldingView();
    if (foldingView != null) {
      // wrap the style with a custom CollapsibleNodeStyleDecorator to change the collapse button visualization
      CollapsibleNodeStyleDecorator nodeStyleDecorator = new MyCollapsibleNodeStyleDecorator(
              foldingView.getGraph().getGroupNodeDefaults().getStyle(), new SizeD(14, 14));

      //////////////// New in this sample ////////////////
      // use a different label model for button placement
      InteriorLabelModel labelModel = new InteriorLabelModel();
      labelModel.setInsets(new InsetsD(2));

      // place the button in the south-east corner of the group node
      nodeStyleDecorator.setButtonPlacement(labelModel.createParameter(InteriorLabelModel.Position.SOUTH_EAST));
      ////////////////////////////////////////////////////

      foldingView.getGraph().getGroupNodeDefaults().setStyle(nodeStyleDecorator);
    }
  }

  /**
   * Creates the default input mode for the GraphComponent, a {@link com.yworks.yfiles.view.input.GraphEditorInputMode}.
   * @return a new GraphEditorInputMode instance
   */
  private IInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();

    // enable label editing
    mode.setEditLabelAllowed(true);
    // enable grouping operations such as grouping selected nodes moving nodes
    // into group nodes
    mode.setGroupingOperationsAllowed(true);

    //////////////// New in this sample ////////////////
    // adjust group node bounds if a label was created
    mode.addLabelAddedListener((source, args) -> adjustGroupBounds(args.getItem()));

    // adjust group node bounds if a label was moved
    mode.getMoveLabelInputMode().addDragFinishedListener(
        (source, args) -> adjustGroupBounds(mode.getMoveLabelInputMode().getMovedLabel()));
    ////////////////////////////////////////////////////

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

    // create a new style and use it as default port style
    graph.getNodeDefaults().getPortDefaults().setStyle(new MySimplePortStyle());

    // create a new style and use it as default node style
    graph.getNodeDefaults().setStyle(new MySimpleNodeStyle());

    // create a new style and use it as default edge style
    graph.getEdgeDefaults().setStyle(new MySimpleEdgeStyle());

    // create a new style and use it as default label style
    graph.getNodeDefaults().getLabelDefaults().setStyle(new MySimpleLabelStyle());
    graph.getEdgeDefaults().getLabelDefaults().setStyle(new MySimpleLabelStyle());
    // node labels should be placed below and left of the node by default, so we can see the connector to its node
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(ExteriorLabelModel.SOUTH_WEST);

    // use 50x50 as default node size
    graph.getNodeDefaults().setSize(new SizeD(50, 50));
  }

  /**
   * Creates the initial sample graph.
   */
  private void createSampleGraph() {
    IGraph graph = graphComponent.getGraph();
    INode node0 = graph.createNode(new RectD(180, 40, 30, 30));
    INode node1 = graph.createNode(new RectD(260, 50, 30, 30));
    INode node2 = graph.createNode(new RectD(284, 200, 30, 30));
    INode node3 = graph.createNode(new RectD(350, 40, 30, 30));
    IEdge edge0 = graph.createEdge(node1, node2);
    // add some bends
    graph.addBend(edge0, new PointD(350, 130));
    graph.addBend(edge0, new PointD(230, 170));
    graph.createEdge(node1, node0);
    graph.createEdge(node1, node3);
    ILabel label0 = graph.addLabel(edge0, "Edge Label", NinePositionsEdgeLabelModel.CENTER_CENTERED);
    ILabel label1 = graph.addLabel(node1, "Node Label");

    // create group nodes containing some of the above node
    INode group1 = graph.groupNodes(node0, node1);
    INode group2 = graph.groupNodes(node2);
    group1.setTag(Colors.GOLD);
    group2.setTag(Colors.LIME_GREEN);
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
    EventQueue.invokeLater(() -> new SampleApplication("Step 27 - Custom Group Bounds").initialize());
  }
}