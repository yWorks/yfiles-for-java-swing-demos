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
package complete.isometric;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultFolderNodeConverter;
import com.yworks.yfiles.graph.DefaultFoldingEdgeConverter;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IMapperRegistry;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.CompositeLayoutData;
import com.yworks.yfiles.layout.FixNodeLayoutData;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LabelAngleReferences;
import com.yworks.yfiles.layout.LabelPlacements;
import com.yworks.yfiles.layout.LabelSideReferences;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.PreferredPlacementDescriptor;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayoutData;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.NodeAlignmentPolicy;
import toolkit.AbstractDemo;

import java.awt.EventQueue;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays graphs in an isometric fashion to create an impression of a 3-dimensional view.
 */
public class IsometricDrawingDemo extends AbstractDemo {
  /**
   * The currently selected layout type determines whether {@link com.yworks.yfiles.layout.hierarchic.HierarchicLayout} or
   * {@link com.yworks.yfiles.layout.orthogonal.OrthogonalLayout} is applied to the graph.
   */
  private LayoutStyle layoutStyle = LayoutStyle.HIERARCHIC;
//  private LayoutStyle layoutStyle = LayoutStyle.ORTHOGONAL;

  /**
   * A flag that signals whether or not a layout is currently running to prevent re-entrant layout calculations.
   */
  private boolean layoutRunning = false;


  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new IsometricDrawingDemo().start();
    });
  }

  @Override
  public void initialize() {
    // configure graph component
    graphComponent.setMinimumZoom(0.05);
    graphComponent.setMaximumZoom(4.0);
    graphComponent.getBackgroundGroup().addChild(new GridVisual(), ICanvasObjectDescriptor.VISUAL);

    // enable/configure folding
    DefaultFolderNodeConverter nodeConverter = new DefaultFolderNodeConverter();
    nodeConverter.setCopyingFirstLabelEnabled(true);
    nodeConverter.setNodeStyleCloningEnabled(true);
    nodeConverter.setFolderNodeSize(new SizeD(210, 120));
    nodeConverter.setFolderNodeStyle(new GroupNodeStyle());
    DefaultFoldingEdgeConverter edgeConverter = new DefaultFoldingEdgeConverter();
    edgeConverter.setCopyingFirstLabelEnabled(true);
    FoldingManager manager = new FoldingManager(graphComponent.getGraph());
    manager.setFolderNodeConverter(nodeConverter);
    manager.setFoldingEdgeConverter(edgeConverter);
    graphComponent.setGraph(manager.createFoldingView().getGraph());

    // initialize interaction
    GraphViewerInputMode inputMode = new GraphViewerInputMode();
    inputMode.setSelectableItems(GraphItemTypes.NONE);
    inputMode.setFocusableItems(GraphItemTypes.NONE);
    inputMode.getNavigationInputMode().setCollapseGroupAllowed(true);
    inputMode.getNavigationInputMode().setExpandGroupAllowed(true);
    inputMode.getNavigationInputMode().setFittingContentAfterGroupActionsEnabled(false);
    inputMode.getNavigationInputMode().setAutoGroupNodeAlignmentPolicy(NodeAlignmentPolicy.BOTTOM_LEFT);
    graphComponent.setInputMode(inputMode);

    // add hierarchy change listeners to invoke an incremental layout when collapsing/expanding a group
    Map<INode, IsometricGeometry> geometries = new HashMap<>();
    PointD[] fixPoint = new PointD[1];
    inputMode.getNavigationInputMode().addGroupCollapsingListener((source, args) -> {
      INode group = args.getItem();
      fixPoint[0] = calculateFixPoint(group);
    });
    inputMode.getNavigationInputMode().addGroupCollapsedListener((source, args) -> {
      INode group = args.getItem();
      geometries.put(group, IsometricGeometry.get(group));
      group.setTag(new IsometricGeometry(155, 90, 0, true));
      restoreFixPoint(group, fixPoint[0]);
      runLayout(group);
    });
    inputMode.getNavigationInputMode().addGroupExpandingListener((source, args) -> {
      INode group = args.getItem();
      fixPoint[0] = calculateFixPoint(group);
    });
    inputMode.getNavigationInputMode().addGroupExpandedListener((source, args) -> {
      INode group = args.getItem();
      if (geometries.get(group) != null) {
        group.setTag(geometries.get(group));
      }
      restoreFixPoint(group, fixPoint[0]);
      runLayout(group);
    });

    // load sample graph
    loadGraph();
  }

  /**
   * Centers the displayed graph in the isometric graph view when it becomes
   * visible for the first time.
   */
  @Override
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Calculates the current location of the front corner of the given node in view space.
   * @param node The node that should be fixed
   */
  private static PointD calculateFixPoint( INode node ) {
    double[] corners = IsometricTransformationSupport.calculateCorners(
            node.getLayout(), IsometricGeometry.get(node));
    return new PointD(
            corners[IsometricTransformationSupport.C3_X],
            corners[IsometricTransformationSupport.C3_Y]);
  }

  /**
   * Moves the node with its front corner of the given node in view space to the fix point.
   * @param node The node that should be fixed
   * @param fixPoint the coordinates of the fixed point
   */
  private void restoreFixPoint( INode node, PointD fixPoint ) {
    IGraph graph = graphComponent.getGraph();
    double[] corners = IsometricTransformationSupport.calculateCorners(
            node.getLayout(), IsometricGeometry.get(node));

    double newCornerX = corners[IsometricTransformationSupport.C3_X];
    double newCornerY = corners[IsometricTransformationSupport.C3_Y];

    double dx = fixPoint.x - newCornerX;
    double dy = fixPoint.y - newCornerY;

    if (graph.isGroupNode(node)) {
      for (INode child : graph.getChildren(node)) {
        PointD center = child.getLayout().getCenter();
        graph.setNodeCenter(child, new PointD(center.getX() + dx, center.getY() + dy));
      }

    }
    PointD center = node.getLayout().getCenter();
    graph.setNodeCenter(node, new PointD(center.getX() + dx, center.getY() + dy));
  }

  /**
   * Loads a graph structure from GraphML.
   * The graph also gets an initial layout.
   */
  private void loadGraph() {
    IGraph graph = graphComponent.getGraph();
    try {
      graph.clear();

      Class resolver = getClass();
      GraphMLIOHandler reader = new GraphMLIOHandler();
      reader.addXamlNamespaceMapping(
              "http://www.yworks.com/yfiles-for-java/IsometricDrawing/1.0",
              "complete.isometric",
              resolver.getClassLoader());
      reader.read(graph, resolver.getResource("resources/example.graphml"));

      runLayout(null);
    } catch (Exception ex) {
      graph.clear();
    }
  }

  /**
   * Invokes a layout specified by the current {@link #layoutStyle}. If there is a fixed node, the layout is calculated
   * incrementally.
   * @param fixedNode if defined the layout will be incrementally and this node remains at its location
   */
  private void runLayout( INode fixedNode ) {
    if (layoutRunning) {
      return;
    }

    layoutRunning = true;

    boolean incremental = fixedNode != null;

    // add mapper to make transformation data stored in the user data of the nodes/edges available during layout
    IMapperRegistry mapperRegistry = graphComponent.getGraph().getMapperRegistry();
    mapperRegistry.createFunctionMapper(IModelItem.class, Object.class,
            IsometricTransformationLayoutStage.TRANSFORMATION_DATA_DPKEY,
            item -> {
              if (item instanceof INode ||
                  (item instanceof ILabel && ((ILabel) item).getOwner() instanceof IEdge)) {
                return item.getTag();
              }
              return null;
            });

    // configure layout
    ILayoutAlgorithm layout;
    LayoutData layoutData;
    if (layoutStyle == LayoutStyle.HIERARCHIC) {
      HierarchicLayout hl = new HierarchicLayout();
      hl.setOrthogonalRoutingEnabled(true);
      hl.setNodeToEdgeDistance(50);
      hl.setMinimumLayerDistance(40);
      hl.setLabelingEnabled(false);
      hl.setIntegratedEdgeLabelingEnabled(true);
      hl.setNodeLabelConsiderationEnabled(true);
      if (incremental) {
        hl.setLayoutMode(LayoutMode.INCREMENTAL);
      }
      layout = hl;

      // use preferred placement descriptors to place the labels vertically on the edges
      HierarchicLayoutData hld = new HierarchicLayoutData();
      hld.setEdgeLabelPreferredPlacement(getPreferredLabelPlacement());
      hld.getIncrementalHints().setIncrementalSequencingItems(item -> item instanceof IEdge);
      layoutData = hld;
    } else {
      // this label layout translator does nothing because the TransformationLayoutStage prepares the labels for layout
      // but OrthogonalLayout needs a label layout translator for integrated edge labeling and node label consideration
      LabelLayoutTranslator labelLayoutTranslator = new LabelLayoutTranslator();

      OrthogonalLayout ol = new OrthogonalLayout();
      ol.setIntegratedEdgeLabelingEnabled(true);
      ol.setNodeLabelConsiderationEnabled(true);
      ol.setLabeling(labelLayoutTranslator);
      layout = ol;

      OrthogonalLayoutData old = new OrthogonalLayoutData();
      old.setEdgeLabelPreferredPlacement(getPreferredLabelPlacement());
      layoutData = old;
    }

    layout = new IsometricTransformationLayoutStage(layout, incremental);

    if (incremental) {
      // fixate the location of the given fixed node
      layout = new FixGroupStateIconStage(layout);

      FixNodeLayoutData fixNodeLayoutData = new FixNodeLayoutData();
      fixNodeLayoutData.setFixedNodes(fixedNode);
      layoutData = new CompositeLayoutData(layoutData, fixNodeLayoutData);
    }

    // configure layout execution to not move the view port
    LayoutExecutor executor = new LayoutExecutor(graphComponent, layout);
    executor.setRunningInThread(incremental);
    executor.setViewportAnimationEnabled(!incremental);
    executor.setDuration(incremental ? Duration.ofMillis(500) : Duration.ZERO);
    executor.setLayoutData(layoutData);
    executor.setContentRectUpdatingEnabled(false);
    executor.addLayoutFinishedListener((source, args) -> {
      mapperRegistry.removeMapper(IsometricTransformationLayoutStage.TRANSFORMATION_DATA_DPKEY);
      mapperRegistry.removeMapper(LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY);
      layoutRunning = false;
    });
    executor.start();
  }


  private static PreferredPlacementDescriptor getPreferredLabelPlacement() {
    PreferredPlacementDescriptor descriptor = new PreferredPlacementDescriptor();
    descriptor.setAngle(0);
    descriptor.setAngleReference(LabelAngleReferences.RELATIVE_TO_EDGE_FLOW);
    descriptor.setSideOfEdge(LabelPlacements.LEFT_OF_EDGE);
    descriptor.setSideReference(LabelSideReferences.ABSOLUTE_WITH_RIGHT_IN_NORTH);
    return descriptor;
  }


  private enum LayoutStyle {
    HIERARCHIC,
    ORTHOGONAL
  }

  /**
   * Does nothing. Label geometries are updated directly in {@link IsometricTransformationLayoutStage}.
   */
  private static final class LabelLayoutTranslator extends com.yworks.yfiles.layout.LabelLayoutTranslator {
    @Override
    public void applyLayout( LayoutGraph graph ) {
      ILayoutAlgorithm core = getCoreLayout();
      if (core != null) {
        core.applyLayout(graph);
      }
    }
  }
}
