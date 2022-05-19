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
package layout.treelayout;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.layout.MinimumNodeSizeStage;
import com.yworks.yfiles.layout.tree.AspectRatioNodePlacer;
import com.yworks.yfiles.layout.tree.AssistantNodePlacer;
import com.yworks.yfiles.layout.tree.ChildPlacement;
import com.yworks.yfiles.layout.tree.CompactNodePlacer;
import com.yworks.yfiles.layout.tree.DefaultNodePlacer;
import com.yworks.yfiles.layout.tree.FillStyle;
import com.yworks.yfiles.layout.tree.LeftRightNodePlacer;
import com.yworks.yfiles.layout.tree.RootAlignment;
import com.yworks.yfiles.layout.tree.RoutingStyle;
import com.yworks.yfiles.layout.tree.SimpleNodePlacer;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeLayoutData;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import toolkit.AbstractDemo;

import java.awt.EventQueue;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Demonstrates the tree layout style and the different ways in which this
 * algorithm can arrange a node and its children.
 */
public class TreeLayoutDemo extends AbstractDemo {
  /**
   * Specifies the node placement policy used for arranging the displayed graph.
   * <dl>
   *   <dt>Mixed</dt>
   *   <dd>
   *     arranges child nodes either with {@link DefaultNodePlacer} or
   *     {@link LeftRightNodePlacer}. Which of the two placers is used for a
   *     given subtree depends on the tree depth of the subtree's root. 
   *   </dd>
   *   <dt>The {@link DefaultNodePlacer}</dt>
   *   <dd>
   *     arranges child nodes horizontally aligned below their root node.
   *     It offers options to change the orientation of the subtree, the edge
   *     routing style, and the alignment of the root node.
   *   </dd>
   *   <dt>The {@link CompactNodePlacer}</dt>
   *   <dd>
   *     uses a dynamic optimization approach that chooses a placement strategy
   *     for children of a local root such that the overall result is compact
   *     with respect to the specified aspect ratio.
   *   </dd>
   *   <dt>The {@link AssistantNodePlacer}</dt>
   *   <dd>
   *     delegates to two different node placers to arrange the child nodes:
   *     Nodes that are marked as <em>Assistants</em> are placed using
   *     {@link LeftRightNodePlacer}.
   *     Other children are arranged below the assistant nodes using
   *     {@link SimpleNodePlacer}.
   *   </dd>
   *   <dt>The {@link AspectRatioNodePlacer}</dt>
   *   <dd>
   *     arranges child nodes such that a given aspect ratio is obeyed.
   *   </dd>
   * </dl>
   */
  private final static Policy NODE_PLACEMENT = Policy.Mixed;


  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    // configure user interaction
    initializeInputMode();

    // load a sample graph
    loadGraph();

    // run the layout algorithm.
    runLayout();
  }

  /**
   * Configures user interaction.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.setEditLabelAllowed(false);
    geim.setClipboardOperationsAllowed(false);
    geim.setUndoOperationsAllowed(false);
    // do not allow node or edge creation
    // only deleting nodes is allowed to ensure that the displayed graph is
    // always a tree
    geim.setCreateNodeAllowed(false);
    geim.setCreateEdgeAllowed(false);
    geim.setSelectableItems(GraphItemTypes.NODE);
    geim.setDeletableItems(GraphItemTypes.NODE);
    geim.setFocusableItems(GraphItemTypes.NODE);

    // when deleting a node, always delete its whole subtree
    geim.addDeletingSelectionListener(( sender, args ) -> {
      ISelectionModel<IModelItem> selectedNodes = args.getSelection();
      ArrayList<INode> nodesToDelete = new ArrayList<>();

      // collect all selected nodes and their child nodes in the array
      for (IModelItem item : selectedNodes) {
        INode selectedNode = ((INode) item);
        collectSubtreeNodes(selectedNode, nodesToDelete);
      }

      // select child nodes to be deleted
      for (INode node : nodesToDelete) {
        args.getSelection().setSelected(node, true);
      }
    });

    // run the layout algorithm again if something got deleted
    geim.addDeletedSelectionListener(( sender, args ) -> {
      runLayout();
    });

    // assign the input mode to the graph component
    graphComponent.setInputMode(geim);
  }

  /**
   * Runs {@link TreeLayout} with the chosen node placement policy for the
   * displayed graph.
   */
  private void runLayout() {
    // create the layout algorithm ...
    TreeLayout layout = new TreeLayout();

    // ... and set a properly configured node placer
    TreeLayoutData layoutData = new TreeLayoutData();
    configure(layoutData, NODE_PLACEMENT);

    graphComponent.morphLayout(new MinimumNodeSizeStage(layout), Duration.ofMillis(500), layoutData);
  }

  /**
   * Collects the node in the subtree with the given root node.
   * @param root the root node of the subtree.
   * @param children the list to collect the subtree nodes.
   */
  private void collectSubtreeNodes( INode root, List<INode> children ) {
    children.add(root);

    for (IEdge edge : graphComponent.getGraph().outEdgesAt(root)) {
      collectSubtreeNodes(edge.getTargetNode(), children);
    }
  }

  /**
   * Loads a sample tree graph.
   */
  private void loadGraph() {
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Configures the node placer of the given layout data.
   */
  private void configure( TreeLayoutData layoutData, Policy policy ) {
    switch (policy) {
      case AspectRatio:
        AspectRatioNodePlacer aspectRatioNodePlacer = new AspectRatioNodePlacer();
        aspectRatioNodePlacer.setAspectRatio(1);
        aspectRatioNodePlacer.setFillStyle(FillStyle.LEADING);
        aspectRatioNodePlacer.setHorizontalDistance(40);
        aspectRatioNodePlacer.setVerticalDistance(40);
        aspectRatioNodePlacer.setHorizontal(true);
        layoutData.setNodePlacers(aspectRatioNodePlacer);
        break;

      case Assistant:
        // specifying assistant nodes has only an effect for CompactNodePlacer
        // and AssistantNodePlacer
        layoutData.setAssistantNodes(node -> Boolean.TRUE.equals(node.getTag()));

        SimpleNodePlacer childNodePlacer = new SimpleNodePlacer();
        childNodePlacer.setRootAlignment(SimpleNodePlacer.RootAlignment.CENTER);
        AssistantNodePlacer assistantNodePlacer = new AssistantNodePlacer();
        assistantNodePlacer.setChildNodePlacer(childNodePlacer);
        assistantNodePlacer.setSpacing(20);
        layoutData.setNodePlacers(assistantNodePlacer);
        break;

      case Compact:
        // specifying assistant nodes has only an effect for CompactNodePlacer
        // and AssistantNodePlacer
        layoutData.setAssistantNodes(node -> Boolean.TRUE.equals(node.getTag()));

        CompactNodePlacer compactNodePlacer = new CompactNodePlacer();
        compactNodePlacer.setPreferredAspectRatio(1);
        compactNodePlacer.setVerticalDistance(40);
        compactNodePlacer.setHorizontalDistance(40);
        compactNodePlacer.setMinimumFirstSegmentLength(10);
        compactNodePlacer.setMinimumLastSegmentLength(10);
        layoutData.setNodePlacers(compactNodePlacer);
        break;

      case Mixed:
        layoutData.setNodePlacers(node -> {
          int depth = getDepth(graphComponent.getGraph(), node);
          if (depth == 3) {
            return new LeftRightNodePlacer();
          } else {
            return new DefaultNodePlacer();
          }
        });
        break;

      default:
        // if none of the above is selected, the DefaultNodePlacer is used
        DefaultNodePlacer defaultNodePlacer = new DefaultNodePlacer();
        defaultNodePlacer.setChildPlacement(ChildPlacement.HORIZONTAL_DOWNWARD);
        defaultNodePlacer.setRoutingStyle(RoutingStyle.FORK);
        defaultNodePlacer.setHorizontalDistance(40);
        defaultNodePlacer.setVerticalDistance(40);
        defaultNodePlacer.setRootAlignment(RootAlignment.CENTER);
        defaultNodePlacer.setMinimumChannelSegmentDistance(0);
        layoutData.setNodePlacers(defaultNodePlacer);
        break;
    }
  }

  private int getDepth(IGraph graph, INode node) {
    int depth = 0;
    INode walker = node;
    while (walker != null) {
      IListEnumerable<IEdge> inEdges = graph.inEdgesAt(walker);
      int size = inEdges.size();
      if (size == 0) {
        return depth;
      } else if (size > 1) {
        throw new IllegalStateException("Graph is not a tree.");
      } else {
        walker = inEdges.first().getSourceNode();
        depth++;
      }
      if (depth > graph.getNodes().size()) {
        throw new IllegalStateException("Graph is not a tree.");
      }
    }
    return depth;
  }

  public static void main( final String[] args ) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new TreeLayoutDemo().start();
    });
  }

  /**
   * Lists the node placement policies supported in this demo.
   */
  private enum Policy {
    AspectRatio,
    Assistant,
    Compact,
    Default,
    Mixed
  }
}
