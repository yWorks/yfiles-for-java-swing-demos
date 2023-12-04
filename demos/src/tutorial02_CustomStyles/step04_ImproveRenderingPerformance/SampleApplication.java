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
package tutorial02_CustomStyles.step04_ImproveRenderingPerformance;

import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.view.Animator;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.IAnimation;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.view.Pen;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Random;

/**
 * Implement high-performance rendering for nodes.
 */
public class SampleApplication {

  private GraphComponent graphComponent;

  // The following code exist only in this tutorial step in order to point out the difference in rendering performance
  private static final int NODE_COUNT = 400;
  private int nodeCount = NODE_COUNT;
  private Animator animator;
  private boolean usePrerenderedNodes;

  /**
   * Animates the nodes in random fashion.
   */
  private void startAnimation() {
    Random random = new Random();
    int nodeCountSqrt = (int) Math.sqrt(nodeCount);

    // create a random position for each node to move to in an animated fashion
    IMapper<INode,IRectangle> nodeLayouts = IMapper.fromFunction(
        node -> new RectD(
            random.nextDouble() * 40 * nodeCountSqrt,
            random.nextDouble() * 40 * nodeCountSqrt,
            node.getLayout().getWidth(),
            node.getLayout().getHeight()));
    animator.animate(IAnimation.createGraphAnimation(graphComponent.getGraph(), nodeLayouts, null, null, null, Duration.ofSeconds(5)));
  }

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

    // create an Animator used to perform node movements on the given GraphComponent
    animator = new Animator(graphComponent);
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
   * Initializes the default styles that are used as templates for newly created graph items.
   */
  private void initializeDefaultStyles() {
    IGraph graph = graphComponent.getGraph();

    // create a new style and use it as default node style
    MySimpleNodeStyle nodeStyle = new MySimpleNodeStyle();
    nodeStyle.setUsingPrerenderedNodesEnabled(usePrerenderedNodes);
    graph.getNodeDefaults().setStyle(nodeStyle);
    // use the SimpleLabelStyle as default label style
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setBackgroundPen(Pen.getBlack());
    labelStyle.setBackgroundPaint(Color.WHITE);
    graph.getNodeDefaults().getLabelDefaults().setStyle(labelStyle);
    graph.getEdgeDefaults().getLabelDefaults().setStyle(labelStyle);
    // node labels should be placed above the node by default
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(ExteriorLabelModel.NORTH);

    // use 50x50 as default node size
    graph.getNodeDefaults().setSize(new SizeD(50, 50));
  }

  /**
   * Creates the sample graph with nodeCount nodes in a square array.
   */
  private void createSampleGraph() {
    IGraph graph = graphComponent.getGraph();
    graph.clear();

    int nodeCountSqrt = (int) Math.sqrt(nodeCount);
    for (int i = 0; i < nodeCountSqrt; i++) {
      for (int j = 0; j < nodeCountSqrt; j++) {
        graph.createNode(new RectD(40 * i, 40 * j, 30, 30));
      }
    }
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
    toolBar.addSeparator();
    toolBar.add(createHighPerformanceToggleButton());
    toolBar.add(createStartAnimationAction());
    toolBar.addSeparator();
    toolBar.add(new JLabel("Node count: "));
    toolBar.add(createNodeCountTextField());
    return toolBar;
  }

  /**
   * Creates a {@link javax.swing.JToggleButton} to switch using pre-rendered nodes on or off.
   */
  private JToggleButton createHighPerformanceToggleButton() {
    usePrerenderedNodes = false;
    JToggleButton toggleButton = new JToggleButton("High Performance");
    toggleButton.setToolTipText("Toggle using pre-rendered nodes on or off");
    toggleButton.setSelected(usePrerenderedNodes);
    toggleButton.addActionListener(e -> {
      INodeStyle nodeStyle = graphComponent.getGraph().getNodeDefaults().getStyle();
      if (nodeStyle instanceof MySimpleNodeStyle) {
        MySimpleNodeStyle myNodeStyle = (MySimpleNodeStyle) nodeStyle;
        myNodeStyle.setUsingPrerenderedNodesEnabled(toggleButton.isSelected());
      }
    });
    return toggleButton;
  }

  /**
   * Creates an {@link javax.swing.Action} to start animating the nodes.
   */
  private AbstractAction createStartAnimationAction() {
    AbstractAction action = new AbstractAction("Animate Nodes") {
      @Override
      public void actionPerformed(ActionEvent e) {
        startAnimation();
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Animate the nodes for 5 seconds");
    return action;
  }

  /**
   * Creates a {@link javax.swing.JTextField} to enter the count of nodes to animate.
   */
  private JTextField createNodeCountTextField() {
    JTextField textField = new JTextField(Integer.toString(NODE_COUNT));
    textField.setToolTipText("Enter the count of nodes to animate");
    textField.setMaximumSize(new Dimension(50, (int) textField.getPreferredSize().getHeight()));
    textField.addActionListener(e -> {
      nodeCount = Integer.parseInt(textField.getText());
      createSampleGraph();
      graphComponent.fitGraphBounds();
    });
    return textField;
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
    EventQueue.invokeLater(() -> new SampleApplication("Step 4 - Improve Rendering Performance").initialize());
  }
}
