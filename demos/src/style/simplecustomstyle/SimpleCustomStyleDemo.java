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
package style.simplecustomstyle;

import com.yworks.yfiles.utils.ItemEventArgs;
import com.yworks.yfiles.view.Animator;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.IAnimation;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.util.Random;

/**
 * Create custom styles for nodes, edges, labels, ports, and edge arrows from scratch.
 */
public class SimpleCustomStyleDemo extends AbstractDemo {
  private Animator animator;
  private IAnimation animation;

  // variable to remember the state of the animation
  private boolean animatorIsRunning;

  /**
   * Creates a sample graph, specifies the default styles and sets the default input mode.
   */
  public void initialize() {
    // initialize the input mode
    graphComponent.setInputMode(createEditorMode());

    // initialize the default styles for newly created graph items
    initializeDefaultStyles();

    // create some graph elements with the above defined styles
    createSampleGraph();

    // initialize an animation that moves the gradient paint of selected edges
    initializeSelectedEdgeAnimation();
  }

  /**
   * Centers the graph in the graph component.
   */
  public void onVisible() {
    // show the whole graph in the view
    graphComponent.fitGraphBounds();
  }

  /**
   * Creates the default input mode for the GraphComponent, a {@link com.yworks.yfiles.view.input.GraphEditorInputMode}.
   * @return a new GraphEditorInputMode instance
   */
  private IInputMode createEditorMode() {
    final GraphEditorInputMode mode = new GraphEditorInputMode();

    // we enable label editing
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
      public void animate(final double time) {
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
    graph.getEdgeDefaults().getPortDefaults().setStyle(new MySimplePortStyle());

    // create a new style and use it as default label style
    graph.getNodeDefaults().getLabelDefaults().setStyle(new MySimpleLabelStyle());
    ExteriorLabelModel labelModel = new ExteriorLabelModel();
    labelModel.setInsets(new InsetsD(15));
    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(labelModel.createParameter(ExteriorLabelModel.Position.NORTH));
    graph.getEdgeDefaults().getLabelDefaults().setStyle(new MySimpleLabelStyle());

    // create a new style and use it as default port style
    graph.getNodeDefaults().getPortDefaults().setStyle(new MySimplePortStyle());
    graph.getNodeDefaults().setSize(new SizeD(50, 50));
  }

  /**
   * Creates the initial sample graph.
   */
  private void createSampleGraph() {
    IGraph graph = graphComponent.getGraph();

    // create a few nodes that are arranged in a circle.
    // We use the tag of the node to store the color of its style.
    INode n0 = graph.createNode(new PointD(291, 433));
    INode n1 = graph.createNode(new PointD(396, 398));
    INode n2 = graph.createNode(new PointD(462, 308));
    INode n3 = graph.createNode(new PointD(462, 197));
    INode n4 = graph.createNode(new PointD(396, 107));
    INode n5 = graph.createNode(new PointD(291, 73));
    INode n6 = graph.createNode(new PointD(185, 107));
    INode n7 = graph.createNode(new PointD(119, 197));
    INode n8 = graph.createNode(new PointD(119, 308));
    INode n9 = graph.createNode(new PointD(185, 398));
    n0.setTag(new Color(108, 0, 255));
    n1.setTag(new Color(210, 255, 0));
    n2.setTag(new Color(0, 72, 255));
    n3.setTag(new Color(255, 0, 84));
    n4.setTag(new Color(255, 30, 0));
    n5.setTag(new Color(0, 42, 255) );
    n6.setTag(new Color(114, 255, 0));
    n7.setTag(new Color(216, 0, 255));
    n8.setTag(new Color(36, 255, 0) );
    n9.setTag(new Color(216, 0, 255));

    // create a label for each node and place it on a border or corner outside the node's bounds.
    ExteriorLabelModel labelModel = new ExteriorLabelModel();
    labelModel.setInsets(new InsetsD(15));
    graph.addLabel(n0, "Node 0", labelModel.createParameter(ExteriorLabelModel.Position.SOUTH));
    graph.addLabel(n1, "Node 1", labelModel.createParameter(ExteriorLabelModel.Position.SOUTH_EAST));
    graph.addLabel(n2, "Node 2", labelModel.createParameter(ExteriorLabelModel.Position.EAST));
    graph.addLabel(n3, "Node 3", labelModel.createParameter(ExteriorLabelModel.Position.EAST));
    graph.addLabel(n4, "Node 4", labelModel.createParameter(ExteriorLabelModel.Position.NORTH_EAST));
    graph.addLabel(n5, "Node 5", labelModel.createParameter(ExteriorLabelModel.Position.NORTH));
    graph.addLabel(n6, "Node 6", labelModel.createParameter(ExteriorLabelModel.Position.NORTH_WEST));
    graph.addLabel(n7, "Node 7", labelModel.createParameter(ExteriorLabelModel.Position.WEST));
    graph.addLabel(n8, "Node 8", labelModel.createParameter(ExteriorLabelModel.Position.WEST));
    graph.addLabel(n9, "Node 9", labelModel.createParameter(ExteriorLabelModel.Position.SOUTH_WEST));

    // create some edges between the nodes above
    graph.createEdge(n0, n4);
    graph.createEdge(n6, n0);
    graph.createEdge(n6, n5);
    graph.createEdge(n5, n2);
    graph.createEdge(n3, n7);
    graph.createEdge(n9, n4);
  }

  /**
   * Adds buttons to the toolbar to change the zoom level and one to change the color of selected nodes.
   */
  @Override
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(createModifyColorAction());
  }

  /**
   * Returns an {@link javax.swing.Action} to change the color of selected nodes.
   */
  private Action createModifyColorAction() {
    Action action = new AbstractAction() {
      private final Random random = new Random();

      @Override
      public void actionPerformed(ActionEvent e) {
        // modify the tag that holds the color of the node's style
        graphComponent.getSelection().getSelectedNodes().forEach(node -> node.setTag(new Color(Color.HSBtoRGB(random.nextInt(360) / 360f, 1, 1))) );

        // and update the view as the graph cannot know that we changed the styles
        graphComponent.repaint();
      }
    };
    action.putValue(Action.NAME, "Modify selected nodes");
    action.putValue(Action.SHORT_DESCRIPTION, "Modify color of selected nodes");
    return action;
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new SimpleCustomStyleDemo().start();
    });
  }
}
