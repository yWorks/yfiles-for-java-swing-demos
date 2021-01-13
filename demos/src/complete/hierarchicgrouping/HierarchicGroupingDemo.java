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
package complete.hierarchicgrouping;

import com.yworks.yfiles.geometry.ISize;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.FolderNodeState;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GroupingSupport;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.layout.CompositeLayoutData;
import com.yworks.yfiles.layout.FixNodeLayoutData;
import com.yworks.yfiles.layout.FixNodeLayoutStage;
import com.yworks.yfiles.layout.FixPointPolicy;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.layout.hierarchic.EdgeRoutingStyle;
import com.yworks.yfiles.layout.hierarchic.RoutingStyle;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.utils.ItemEventArgs;
import toolkit.AbstractDemo;

import java.awt.EventQueue;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Show graph hierarchies and automatically trigger an incremental layout when opening or closing groups.
 */
public class HierarchicGroupingDemo extends AbstractDemo {
  private Set<INode> incrementalNodes;
  private Set<IModelItem> incrementalEdges;
  private IFoldingView foldingView;

  /**
   * Initializes the graph and the input modes and runs a initial layout.
   */
  public void initialize() {
    incrementalNodes = new HashSet<>();
    incrementalEdges = new HashSet<>();

    // initialize the graph
    initializeGraph();

    // initialize the input mode
    initializeInputModes();
  }

  /**
   * Centers and arranges the graph in the graph component.
   */
  public void onVisible() {
    graphComponent.fitGraphBounds();
    graphComponent.getGraph().applyLayout(createLayoutAlgorithm());
    // top align the graph
    graphComponent.setViewPoint(new PointD(graphComponent.getViewPoint().getX(), graphComponent.getContentRect().getMinY() - 50));
  }

  /**
   * Creates and initializes an {@link com.yworks.yfiles.layout.hierarchic.HierarchicLayout} instance.
   */
  private HierarchicLayout createLayoutAlgorithm() {
    HierarchicLayout ihl = new HierarchicLayout();
    ihl.setRecursiveGroupLayeringEnabled(false);
    ihl.getEdgeLayoutDescriptor().setRoutingStyle(new RoutingStyle(EdgeRoutingStyle.ORTHOGONAL));
    return ihl;
  }

  /**
   * Initializes the graph instance, sets default styles and loads a small sample graph.
   */
  private void initializeGraph() {
    // create the manager for the folding operations
    FoldingManager foldingManager = new FoldingManager();

    // create a view
    foldingView = foldingManager.createFoldingView();

    // and set it to the GraphComponent
    graphComponent.setGraph(foldingView.getGraph());

    // decorate the behavior of nodes
    IGraph graph = graphComponent.getGraph();
    NodeDecorator nodeDecorator = graph.getDecorator().getNodeDecorator();

    // adjust the insets so that labels are considered
    nodeDecorator.getInsetsProviderDecorator().setImplementationWrapper((node, insetsProvider) -> {
      if (insetsProvider != null) {
        InsetsD insets = insetsProvider.getInsets(node);
        return new LabelInsetsProvider(insets);
      } else {
        return new LabelInsetsProvider();
      }
    });

    // constrain group nodes to at least the size of their labels
    nodeDecorator.getSizeConstraintProviderDecorator().setImplementationWrapper(
        (node, sizeConstraintProvider) -> new LabelSizeConstraintProvider(sizeConstraintProvider));

    loadSampleGraph();
    graph.getNodes().forEach(incrementalNodes::add);
  }

  /**
   * Loads a sample graph.
   */
  private void loadSampleGraph() {
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a mode and registers it as the {@link com.yworks.yfiles.view.CanvasComponent#getInputMode()}.
   */
  private void initializeInputModes() {
    GraphViewerInputMode inputMode = new GraphViewerInputMode();
    inputMode.getNavigationInputMode().setCollapseGroupAllowed(true);
    inputMode.getNavigationInputMode().setExpandGroupAllowed(true);
    // FitContent interferes with our view port animation setup
    inputMode.getNavigationInputMode().setFittingContentAfterGroupActionsEnabled(false);
    inputMode.getNavigationInputMode().addGroupExpandedListener(this::onGroupExpanded);
    inputMode.getNavigationInputMode().addGroupCollapsedListener(this::onGroupCollapsed);
    graphComponent.setInputMode(inputMode);
  }

  /**
   * Event handler that is triggered when a group is expanded interactively.
   * This method performs an incremental layout on the newly expanded group node and its descendants.
   */
  private void onGroupExpanded(Object source, ItemEventArgs<INode> evt) {
    IGraph graph = graphComponent.getGraph();
    GroupingSupport groupingSupport = graph.getGroupingSupport();
    incrementalNodes.clear();
    incrementalEdges.clear();
    // we mark the group node and its descendants as incremental
    INode groupNode = evt.getItem();
    incrementalNodes.add(groupNode);
    Iterable<INode> descendants = groupingSupport.getDescendants(groupNode);
    descendants.forEach(incrementalNodes::add);

    INode master = foldingView.getMasterItem(groupNode);

    // retrieve the state of the view node *before* the grouping operation
    FolderNodeState state = foldingView.getManager().getFolderNodeState(master);
    // and set these bounds so that the animation will move it to the correct location
    graph.setNodeLayout(groupNode, createRectFromTopRight(state.getLayout().getTopRight(), groupNode.getLayout()));

    // reset the paths and the centers of the child nodes so that morphing looks smoother
    descendants.forEach(descendant -> {
      graph.edgesAt(descendant).forEach(graph::clearBends);
      graph.setNodeCenter(descendant, groupNode.getLayout().getCenter());
    });

    // reset adjacent edge paths to get smoother layout transitions
    graph.edgesAt(groupNode).forEach(graph::clearBends);

    applyLayout(groupNode);
  }

  /**
   * Event handler that is triggered when a group is closed interactively.
   * This method performs an incremental layout on the newly collapsed group node.
   */
  private void onGroupCollapsed(Object source, ItemEventArgs<INode> evt) {
    incrementalNodes.clear();
    incrementalEdges.clear();
    // we mark the group node and its adjacent edges as incremental
    IGraph graph = graphComponent.getGraph();
    INode groupNode = evt.getItem();
    incrementalNodes.add(groupNode);
    graph.edgesAt(groupNode).forEach(incrementalEdges::add);

    // retrieve the state of the view node *before* the grouping operation
    INode master = foldingView.getMasterItem(groupNode);
    // and set these bounds so that the animation will move it to the correct location
    graph.setNodeLayout(groupNode, createRectFromTopRight(master.getLayout().getTopRight(), groupNode.getLayout()));

    // reset adjacent edge paths to get smoother layout transitions
    graph.edgesAt(groupNode).forEach(graph::clearBends);

    applyLayout(groupNode);
  }

  private RectD createRectFromTopRight(PointD topRight, ISize size) {
    return new RectD(topRight.getX() - size.getWidth(), topRight.getY(), size.getWidth(), size.getHeight());
  }

  /**
   * Calculates and applies a layout in an animated fashion.
   * @param fixGroupNode The group node whose top right corner shall be fixed.
   */
  private void applyLayout(INode fixGroupNode) {
    // create a pre-configured HierarchicLayout
    HierarchicLayout ihl = createLayoutAlgorithm();
    // rearrange only the incremental graph elements, the
    // remaining elements are not, or only slightly, changed
    ihl.setLayoutMode(LayoutMode.INCREMENTAL);

    // provide additional data to configure the HierarchicLayout
    HierarchicLayoutData ihlData = new HierarchicLayoutData();
    // specify the nodes to rearrange
    ihlData.getIncrementalHints().setIncrementalLayeringNodes(incrementalNodes);
    // specify the edges to rearrange
    ihlData.getIncrementalHints().setIncrementalSequencingItems(incrementalEdges);

    // append the FixNodeLayoutStage to fix the position of the upper right corner
    // of the currently expanded or collapsed group node so that the mouse cursor
    // remains on the expand/collapse button during layout
    FixNodeLayoutStage fixNodeLayoutStage = new FixNodeLayoutStage();
    fixNodeLayoutStage.setFixPointPolicy(FixPointPolicy.UPPER_RIGHT);
    ihl.appendStage(fixNodeLayoutStage);

    // provide additional data to configure the FixNodeLayoutStage
    FixNodeLayoutData fixNodeLayoutData = new FixNodeLayoutData();
    // specify the group node whose upper right corner position should be fixed during layout
    fixNodeLayoutData.setFixedNodes(fixGroupNode);

    // run the layout algorithm and animate the result
    LayoutExecutor executor = new LayoutExecutor(graphComponent, ihl);
    executor.setViewportAnimationEnabled(false);
    executor.setEasedAnimationEnabled(true);
    executor.setRunningInThread(true);
    executor.setContentRectUpdatingEnabled(true);
    executor.setDuration(Duration.ofMillis(500));
    // compose layout data from HierarchicLayoutData and FixNodeLayoutData
    executor.setLayoutData(new CompositeLayoutData(ihlData, fixNodeLayoutData));
    executor.start();
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new HierarchicGroupingDemo().start();
    });
  }
}
