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
package complete.collapse;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.FilteredGraphWrapper;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.layout.FixNodeLayoutData;
import com.yworks.yfiles.layout.FixNodeLayoutStage;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.tree.BalloonLayout;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import toolkit.AbstractDemo;

import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.Random;

/**
 * Wrap and decorate {@link com.yworks.yfiles.graph.IGraph} instances to create
 * collapse/expand functionality for trees. Subtrees are filtered using {@link com.yworks.yfiles.graph.FilteredGraphWrapper}
 * and the layout is calculated with {@link com.yworks.yfiles.layout.tree.TreeLayout}, {@link
 * com.yworks.yfiles.layout.tree.BalloonLayout}, {@link com.yworks.yfiles.layout.organic.OrganicLayout} and {@link
 * com.yworks.yfiles.layout.tree.TreeLayout}.
 */
public class CollapsibleTreeDemo extends AbstractDemo {
  // command that toggles the state of the current node when Enter has been pressed
  private static final ICommand TOGGLE_CURRENT_NODE = ICommand.createCommand("Toggle");

  // graph that contains visible nodes
  private FilteredGraphWrapper filteredGraph;
  // graph containing all nodes
  private IGraph fullGraph;
  // currently selected layout
  private ILayoutAlgorithm currentLayout;
  // instance of java.util.Random to get the initial sample graph.
  private Random random = new Random(666);


  @Override
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(createLayoutComboBoxAndSetupLayouts());
  }

  /**
   * Creates the combobox for the toolbar in which the user can select a layout for the graph.
   * Also initializes all layouts and wires up the combobox to invoke a re-layout with the newly
   * selected layout.
   */
  private JComboBox createLayoutComboBoxAndSetupLayouts() {
    // create some layouts
    TreeLayout treeLayout = new TreeLayout();
    treeLayout.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
    treeLayout.prependStage(new FixNodeLayoutStage());

    BalloonLayout balloonLayout = new BalloonLayout();
    balloonLayout.setFromSketchModeEnabled(true);
    balloonLayout.setCompactnessFactor(1.0);
    balloonLayout.setOverlapsAllowed(true);
    balloonLayout.prependStage(new FixNodeLayoutStage());

    HierarchicLayout hierarchicLayout = new HierarchicLayout();
    hierarchicLayout.prependStage(new FixNodeLayoutStage());

    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setMinimumNodeDistance(40);
    organicLayout.prependStage(new FixNodeLayoutStage());

    OrthogonalLayout orthogonalLayout = new OrthogonalLayout();
    orthogonalLayout.prependStage(new FixNodeLayoutStage());

    // create combobox containing layouts and wire them up
    ILayoutAlgorithm[] layouts = {treeLayout, balloonLayout, hierarchicLayout, organicLayout, orthogonalLayout};
    String[] titles = {"Tree", "Balloon", "Hierarchic", "Organic", "Orthogonal"};
    JComboBox<String> layoutComboBox = new JComboBox<>(titles);
    layoutComboBox.setMaximumSize(layoutComboBox.getPreferredSize());
    layoutComboBox.setEditable(false);
    layoutComboBox.setToolTipText("Select layout");
    layoutComboBox.setSelectedIndex(0);
    currentLayout = layouts[0];
    layoutComboBox.addActionListener(e -> {
      // perform a re-layout with the newly selected layout
      currentLayout = layouts[layoutComboBox.getSelectedIndex()];
      runLayout(true, null);
    });
    return layoutComboBox;
  }

  /**
   * Shows or hides the children of the given node depending on its current state.
   */
  private void toggleNodeState(INode node) {
    CollapsedState state = (CollapsedState) node.getTag();
    // leaf nodes can not be toggled
    if (state != CollapsedState.LEAF) {
      // toggle the state of the node
      CollapsedState newState = state == CollapsedState.COLLAPSED ? CollapsedState.EXPANDED : CollapsedState.COLLAPSED;
      node.setTag(newState);
      // prepare the position of the children for a nice layout animation
      if (newState == CollapsedState.EXPANDED) {
        alignChildren(node);
      }
      // hide or show the descendants of the collapsed or expanded node
      filteredGraph.nodePredicateChanged();
      // the visible graph has been changed and should be re-layouted
      runLayout(false, node);
    }
  }

  /**
   * Positions all children of the given node on the same location as the node, so they appear to move out of their
   * parent node.
   * @param node The parent node.
   */
  private void alignChildren(INode node) {
    // This method is used to set the initial positions of the children
    // of a node which gets expanded to the position of the expanded node.
    // This looks nicer in the following animated layout. Try commenting
    // out the method body to see the difference.
    PointD center = node.getLayout().getCenter();
    fullGraph.outEdgesAt(node).stream()
        .filter(edge -> edge.getSourceNode() == node)
        .forEach(edge -> {
          fullGraph.clearBends(edge);
          INode child = edge.getTargetNode();
          fullGraph.setNodeCenter(child, center);
          alignChildren(child);
        });
  }

  /**
   * Builds a sample graph. The graph is a tree that is programmatically built dependent on the given parameters.
   */
  private void buildTree(IGraph graph, int children, int levels, int collapseLevel) {
    INode root = graph.createNode(new PointD(20, 20));
    root.setTag(CollapsedState.EXPANDED);
    addChildren(levels, graph, root, children, collapseLevel);
  }

  /**
   * Recursively adds children to the tree.
   */
  private void addChildren(int level, IGraph graph, INode root, int childCount, int collapseLevel) {
    int actualChildCount = random.nextInt(childCount) + 1; // random number between 1 and childCount + 1
    for (int i = 0; i < actualChildCount; i++) {
      INode child = graph.createNode(new PointD(20, 20));
      graph.createEdge(root, child);
      if (level == 0) {
        // we reached the leafs
        child.setTag(CollapsedState.LEAF);
      } else {
        if (level < collapseLevel) {
          // we reached a level from that all nodes should be collapsed
          child.setTag(CollapsedState.COLLAPSED);
        } else {
          // all others are expanded
          child.setTag(CollapsedState.EXPANDED);
        }
        addChildren(level - 1, graph, child, 4, 2);
      }
    }
  }

  /**
   * Predicate for the filtered graph wrapper that
   * indicates whether a node should be visible.
   * @return <code>true</code> if the node should be visible.
   */
  private boolean nodePredicate(INode node) {
    // return true if none of the parent nodes is collapsed
    IListEnumerable<IEdge> inEdges = fullGraph.inEdgesAt(node);
    if (inEdges.size() > 0) {
      // node is visible if all its ancestors are expanded
      INode parent = inEdges.first().getSourceNode();
      return parent.getTag().equals(CollapsedState.EXPANDED) && nodePredicate(parent);
    } else {
      // only the root has no incoming edges and is always visible
      return true;
    }
  }

  /**
   * Initializes the graph and the input mode.
   * @see #initializeInputModes()
   * @see #initializeGraph()
   */
  public void initialize() {
    // initialize the input mode
    initializeInputModes();

    // initialize the graph
    initializeGraph();
  }

  /**
   * Shows or hides the children of the current node.
   */
  private boolean toggleCurrentNode(ICommand command, Object parameter, Object source) {
    toggleNodeState((INode) graphComponent.getCurrentItem());
    return true;
  }

  /**
   * Determines whether the children of the current node can be shown or hidden.
   */
  private boolean canToggleCurrentNode(ICommand command, Object parameter, Object source) {
    IModelItem currentItem = graphComponent.getCurrentItem();
    INode node = currentItem instanceof INode ? (INode) currentItem : null;
    return node != null && node.getTag() != CollapsedState.LEAF;
  }

  /**
   * Centers and arranges the graph in the graph component.
   */
  public void onVisible() {
    // center the graph to prevent the initial layout fading in from the top left corner
    graphComponent.fitGraphBounds();

    // calculate and run the initial layout.
    runLayout(true, null);
  }

  /**
   * Initializes the graph instance, setting default styles
   * and creating a small sample graph.
   */
  private void initializeGraph() {
    // Create the graph instance that will hold the complete graph.
    fullGraph = graphComponent.getGraph();

    // Create a nice default style for the nodes
    CollapsibleNodeStyle style = new CollapsibleNodeStyle();
    fullGraph.getNodeDefaults().setStyle(style);
    fullGraph.getNodeDefaults().setSize(new SizeD(60, 30));

    // now build a simple sample tree
    buildTree(fullGraph, 3, 3, 3);

    // create a view of the graph that contains only non-collapsed subtrees.
    // use a predicate method to decide what nodes should be part of the graph.
    filteredGraph = new FilteredGraphWrapper(fullGraph, this::nodePredicate, edge -> true);

    // display the filtered graph in our component.
    graphComponent.setGraph(filteredGraph);
  }

  /**
   * Creates a mode and registers it as the
   * @see com.yworks.yfiles.view.CanvasComponent#getInputMode()
   */
  protected void initializeInputModes() {
    // create a simple mode that reacts to mouse clicks on nodes.
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    gvim.setClickableItems(GraphItemTypes.NODE);
    gvim.addItemClickedListener((source, args) -> {
      if (args.getItem() instanceof INode) {
        // toggle the collapsed state of the clicked node
        INode node = (INode) args.getItem();
        toggleNodeState(node);
      }
    });

    // add a command that toggles the state of the current node when pressing Enter
    KeyboardInputMode kim = gvim.getKeyboardInputMode();
    kim.addCommandBinding(TOGGLE_CURRENT_NODE, this::toggleCurrentNode, this::canToggleCurrentNode);
    kim.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), TOGGLE_CURRENT_NODE);

    graphComponent.setInputMode(gvim);
  }

  /**
   * Starts the layout in an animated fashion.
   * @param animateViewport whether the view port should be animated
   * @param toggledNode     the node that is collapsed or expanded and that should keep its position during layout
   */
  private void runLayout(boolean animateViewport, INode toggledNode) {
    if (currentLayout != null) {
      // provide additional data to configure the FixNodeLayoutStage
      FixNodeLayoutData fixNodeLayoutData = new FixNodeLayoutData();
      // specify the node whose position is to be fixed during layout
      fixNodeLayoutData.setFixedNodes(toggledNode);
      // run the layout and animate the result
      LayoutExecutor layoutExecutor = new LayoutExecutor(graphComponent, currentLayout);
      layoutExecutor.setContentRectUpdatingEnabled(true);
      layoutExecutor.setViewportAnimationEnabled(animateViewport);
      layoutExecutor.setDuration(Duration.ofMillis(300));
      layoutExecutor.setLayoutData(fixNodeLayoutData);
      layoutExecutor.start();
    }
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new CollapsibleTreeDemo().start();
    });
  }

}
