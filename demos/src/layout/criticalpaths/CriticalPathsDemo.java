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
package layout.criticalpaths;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.layout.hierarchic.EdgeLayoutDescriptor;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.SimplexNodePlacer;
import com.yworks.yfiles.layout.tree.LayeredNodePlacer;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeLayoutData;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Shows how to emphazise important, or 'critical', paths with hierarchic and
 * tree layout algorithms.
 */
public class CriticalPathsDemo extends AbstractDemo {
  /**
   * Specifies the layout algorithm used to demonstrate the effect of critical
   * path priorities.
   * Currently, only {@link HierarchicLayout} and {@link TreeLayout} support
   * critical paths.
   */
  private static final Algorithm LAYOUT_ALGORITHM = Algorithm.Hierarchic;

  /**
   * Styles for visualizing the critical path priority that may be associated
   * to an edge.
   */
  private static final IEdgeStyle[] PRIORITY_STYLES = createPriorityStyles();

  /**
   * Creates styles for visualizing the critical path priority that may be
   * associated to an edge.
   */
  private static IEdgeStyle[] createPriorityStyles() {
    Pen[] pens = {
            new Pen(new Color(51, 102, 153, 255), 1), // priority 0 (least)
            new Pen(Colors.GOLD, 3),                  // priority 1
            new Pen(Colors.ORANGE, 3),                // priority 2
            new Pen(Colors.DARK_ORANGE, 3),           // priority 3
            new Pen(Colors.ORANGE_RED, 3),            // priority 4
            new Pen(Colors.FIREBRICK, 3),             // priority 5 (highest)
    };

    IEdgeStyle[] styles = new IEdgeStyle[pens.length];
    for (int i = 0; i < styles.length; ++i) {
      PolylineEdgeStyle style = new PolylineEdgeStyle();
      style.setPen(pens[i]);
      styles[i] = style;
    }
    return styles;
  }


  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    // configure user interaction, namely the edge tool tip
    initializeInputMode();

    // load a sample graph
    loadGraph();

    // set colors for edges according to their associated critical path priority
    updateStyles(graphComponent.getGraph());

    // run the chosen layout algorithm
    runLayout();
  }

  /**
   * Configures user interaction.
   * Adds priority tooltips to edges and context menus for edges and nodes.
   */
  private void initializeInputMode() {
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    gvim.setSelectableItems(GraphItemTypes.NONE);
    gvim.setFocusableItems(GraphItemTypes.NONE);

    // add tooltips for edges that display the associated critical path priority
    gvim.setToolTipItems(GraphItemTypes.EDGE);
    gvim.addQueryItemToolTipListener(( sender, args ) -> {
      if (!args.isHandled()) {
        args.setToolTip("Priority: " + getPriority((IEdge) args.getItem()));
      }
    });

    graphComponent.setInputMode(gvim);
  }

  
  /**
   * Adds controls for clearing and setting priorities for critical paths
   * to the application toolbar.
   */
  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(createClearPrioritiesAction());
    toolBar.add(createRandomPrioritiesAction());
  }

  /**
   * Creates an action for clearing critical path priorities.
   */
  private Action createClearPrioritiesAction () {
    Action action = new AbstractAction() {
      @Override
      public void actionPerformed( ActionEvent e ) {
        clearPriorities();
      }
    };
    action.putValue(Action.NAME, "Clear Priorities");
    action.putValue(Action.SHORT_DESCRIPTION, "Clears all critical path priorities.");
    return action;
  }

  /**
   * Creates an action for setting random priorities for critical paths.
   */
  private Action createRandomPrioritiesAction () {
    Action action = new AbstractAction() {
      @Override
      public void actionPerformed( ActionEvent e ) {
        randomPriorities();
      }
    };
    action.putValue(Action.NAME, "Random Priorities");
    action.putValue(Action.SHORT_DESCRIPTION, "Sets random priorities for critical paths.");
    return action;
  }


  /**
   * Colors each edge in the given graph according to the critical path
   * priority of each edge.
   */
  private static void updateStyles( IGraph graph ) {
    for (IEdge edge : graph.getEdges()) {
      graph.setStyle(edge, PRIORITY_STYLES[getPriority(edge)]);
    }
  }

  /**
   * Clears critical path priorities and updates the displayed graph
   * accordingly.
   */
  private void clearPriorities() {
    IGraph graph = graphComponent.getGraph();

    clearPrioritiesCore(graph);

    updateStyles(graph);

    runLayout();
  }

  /**
   * Clears all critical path priorities in the given graph.
   */
  private static void clearPrioritiesCore( IGraph graph ) {
    for (IEdge edge : graph.getEdges()) {
      edge.setTag(Integer.valueOf(0));
    }
  }

  /**
   * Assigns random critical path priorities and updates the displayed graph
   * accordingly.
   */
  private void randomPriorities() {
    IGraph graph = graphComponent.getGraph();

    randomPrioritiesCore(graph);

    updateStyles(graph);

    runLayout();
  }

  /**
   * Marks random upstream paths from leaf nodes to generate random long paths.
   */
  private static void randomPrioritiesCore( IGraph graph ) {
    // find all leaf nodes in the graph
    ArrayList<INode> leafs = new ArrayList<>();
    for (INode node : graph.getNodes()) {
      if (graph.outEdgesAt(node).size() == 0) {
        leafs.add(node);
      }
    }

    clearPrioritiesCore(graph);

    // mark the upstream path of random leaf nodes
    for (int i = 0, n = Math.min(10, leafs.size()); i < n; ++i) {
      int rndNodeIndex = (int) Math.floor(Math.random() * leafs.size());
      int rndPriority = (int) (Math.floor(Math.random() * 5) + 1);
      markPredecessorsPath(graph, leafs.get(rndNodeIndex), rndPriority);
    }
  }

  /**
   * Marks the upstream path from a given node with the given priority
   */
  private static void markPredecessorsPath( IGraph graph, INode node, int priority ) {
    for (IEdge edge = firstInEdge(graph, node);
         edge != null;
         edge = firstInEdge(graph, edge.getSourceNode())) {
      if (getPriority(edge) > priority) {
        // stop upstream path when a higher priority is found
        break;
      }
      edge.setTag(priority);
    }
  }

  /**
   * Returns the first ingoing edge of the given node or {@code null} if
   * the node has no ingoing edges.
   */
  private static IEdge firstInEdge( IGraph graph, INode node ) {
    IListEnumerable<IEdge> edges = graph.inEdgesAt(node);
    return edges.size() > 0 ? edges.first() : null;
  }

  /**
   * Returns the critical path priority stored in the given edge.
   * More specifically, returns the integral value of the edge's tag or
   * {@code 0} if the tag is not a number.
   */
  private static int getPriority( IEdge edge ) {
    Object tag = edge.getTag();
    return tag instanceof Number ? ((Number) tag).intValue() : 0;
  }


  /**
   * Arranges the displayed graph with the {@link #LAYOUT_ALGORITHM chosen layout algorithm}.
   */
  private void runLayout() {
    switch (LAYOUT_ALGORITHM) {
      case Tree:
        runTreeLayout();
        break;
      case Hierarchic:
        runHierarchicLayout();
        break;
    }
  }

  /**
   * Arranges the displayed graph with the {@link HierarchicLayout} algorithm.
   */
  private void runHierarchicLayout() {
    EdgeLayoutDescriptor layoutDescriptor = new EdgeLayoutDescriptor();
    layoutDescriptor.setMinimumFirstSegmentLength(30);
    layoutDescriptor.setMinimumLastSegmentLength(30);

    SimplexNodePlacer nodePlacer = new SimplexNodePlacer();
    nodePlacer.setBarycenterModeEnabled(true);

    HierarchicLayout layout = new HierarchicLayout();
    layout.setOrthogonalRoutingEnabled(true);
    layout.setEdgeLayoutDescriptor(layoutDescriptor);
    layout.setNodePlacer(nodePlacer);

    HierarchicLayoutData layoutData = new HierarchicLayoutData();
    layoutData.setCriticalEdgePriorities(CriticalPathsDemo::getPriority);
    // set edge crossing costs to reduce the probability of different critical
    // paths crossing each other
    layoutData.setEdgeCrossingCosts(edge -> getPriority(edge) + 1.0);

    graphComponent.morphLayout(layout, Duration.ofMillis(700), layoutData);
  }

  /**
   * Arranges the displayed graph with the {@link TreeLayout} algorithm.
   */
  private void runTreeLayout() {
    LayeredNodePlacer nodePlacer = new LayeredNodePlacer();
    nodePlacer.setLayerSpacing(60);
    nodePlacer.setSpacing(30);

    TreeLayout layout = new TreeLayout();
    layout.setDefaultNodePlacer(nodePlacer);

    TreeLayoutData layoutData = new TreeLayoutData();
    layoutData.setCriticalEdgePriorities(CriticalPathsDemo::getPriority);

    graphComponent.morphLayout(layout, Duration.ofMillis(700), layoutData);
  }


  /**
   * Loads a sample graph appropriate for the chosen
   * {@link #LAYOUT_ALGORITHM layout algorithm}.
   */
  private void loadGraph() {
    String path = LAYOUT_ALGORITHM == Algorithm.Tree
            ? "resources/tree.graphml"
            : "resources/hierarchic.graphml";

    try {
      graphComponent.importFromGraphML(getClass().getResource(path));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void main( final String[] args ) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new CriticalPathsDemo().start();
    });
  }

  /**
   * Lists the layout algorithms that support critical paths.
   */
  private enum Algorithm {
    Tree,
    Hierarchic
  }
}
