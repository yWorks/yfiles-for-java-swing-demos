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
package layout.layoutstyles.configurations;

import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.analysis.FeedbackEdgeSet;
import com.yworks.yfiles.analysis.LongestPath;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.OrientationLayout;
import com.yworks.yfiles.layout.SimpleProfitModel;
import com.yworks.yfiles.layout.hierarchic.AsIsLayerer;
import com.yworks.yfiles.layout.hierarchic.BusDescriptor;
import com.yworks.yfiles.layout.hierarchic.ComponentArrangementPolicy;
import com.yworks.yfiles.layout.hierarchic.EdgeLayoutDescriptor;
import com.yworks.yfiles.layout.hierarchic.EdgeRoutingStyle;
import com.yworks.yfiles.layout.hierarchic.GroupAlignmentPolicy;
import com.yworks.yfiles.layout.hierarchic.GroupCompactionPolicy;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.IIncrementalHintsFactory;
import com.yworks.yfiles.layout.hierarchic.ILayerer;
import com.yworks.yfiles.layout.hierarchic.LayeringStrategy;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.layout.hierarchic.NodeLabelMode;
import com.yworks.yfiles.layout.hierarchic.NodeLayoutDescriptor;
import com.yworks.yfiles.layout.hierarchic.PortAssignmentMode;
import com.yworks.yfiles.layout.hierarchic.RecursiveEdgeStyle;
import com.yworks.yfiles.layout.hierarchic.RoutingStyle;
import com.yworks.yfiles.layout.hierarchic.SimplexNodePlacer;
import com.yworks.yfiles.layout.hierarchic.SubcomponentDescriptor;
import com.yworks.yfiles.layout.hierarchic.TopLevelGroupToSwimlaneStage;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.tree.LeftRightNodePlacer;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IGraphSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Predicate;
import toolkit.optionhandler.ComponentType;
import toolkit.optionhandler.ComponentTypes;
import toolkit.optionhandler.EnumValueAnnotation;
import toolkit.optionhandler.Label;
import toolkit.optionhandler.MinMax;
import toolkit.optionhandler.OptionGroupAnnotation;

/**
 * Configuration options for the layout algorithm of the same name.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
@Label("HierarchicLayout")
public class HierarchicLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public HierarchicLayoutConfig() {
    setGroupHorizontalCompactionItem(GroupCompactionPolicy.NONE);
    setGroupAlignmentItem(GroupAlignmentPolicy.TOP);
    setConsideringNodeLabelsItem(true);
    setMaximumSizeItem(1000);
    setScaleItem(1);
    setComponentArrangementPolicyItem(ComponentArrangementPolicy.TOPMOST);
    setNodeCompactionEnabledItem(false);
    setRankingPolicyItem(LayeringStrategy.HIERARCHICAL_OPTIMAL);
    setMinimumSlopeItem(0.25);
    setConsideringEdgeDirectednessItem(true);
    setConsideringEdgeThicknessItem(true);
    setMinimumEdgeDistanceItem(15);
    setMinimumEdgeLengthItem(20);
    setMinimumLastSegmentLengthItem(15);
    setMinimumFirstSegmentLengthItem(10);
    setEdgeRoutingItem(EdgeRoutingStyle.ORTHOGONAL);
    setMinimumLayerDistanceItem(10);
    setEdgeToEdgeDistanceItem(15);
    setNodeToEdgeDistanceItem(15);
    setNodeToNodeDistanceItem(30);
    setPlacingSymmetricItem(true);
    setRecursiveEdgeStyleItem(RecursiveEdgeStyle.OFF);
    setMaximumDurationItem(5);
    setEdgeLabelingItem(EnumEdgeLabeling.NONE);
    setCompactingEdgeLabelPlacementItem(true);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10);
    setGroupLayeringStrategyItem(GroupLayeringStrategyOptions.LAYOUT_GROUPS);
    setGridEnabledItem(false);
    setGridSpacingItem(5);
    setGridPortAssignmentItem(PortAssignmentMode.DEFAULT);
    OrientationItem = LayoutOrientation.TOP_TO_BOTTOM;
    setPlacingSubComponentsSeparatelyItem(false);
    setHighlightCriticalPath(false);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    HierarchicLayout layout = new HierarchicLayout();

    //  mark incremental elements if required
    boolean fromSketch = isUsingDrawingAsSketchItem();
    boolean incrementalLayout = isPlacingSelectedElementsIncrementallyItem();
    boolean selectedElements = graphComponent.getSelection().getSelectedEdges().size() > 0 || graphComponent.getSelection().getSelectedNodes().size() > 0;

    if (incrementalLayout && selectedElements) {
      layout.setLayoutMode(LayoutMode.INCREMENTAL);
    } else if (fromSketch) {
      layout.setLayoutMode(LayoutMode.INCREMENTAL);
    } else {
      layout.setLayoutMode(LayoutMode.FROM_SCRATCH);
    }

    ((SimplexNodePlacer)layout.getNodePlacer()).setBarycenterModeEnabled(isPlacingSymmetricItem());


    layout.setComponentLayoutEnabled(isPlacingComponentsSeparatelyItem());


    layout.setMinimumLayerDistance(getMinimumLayerDistanceItem());
    layout.setNodeToEdgeDistance(getNodeToEdgeDistanceItem());
    layout.setNodeToNodeDistance(getNodeToNodeDistanceItem());
    layout.setEdgeToEdgeDistance(getEdgeToEdgeDistanceItem());

    NodeLayoutDescriptor nld = layout.getNodeLayoutDescriptor();
    EdgeLayoutDescriptor eld = layout.getEdgeLayoutDescriptor();

    layout.setAutomaticEdgeGroupingEnabled(isAutomaticEdgeGroupingEnabledItem());

    eld.setRoutingStyle(new RoutingStyle(getEdgeRoutingItem()));
    eld.getRoutingStyle().setCurveShortcutsAllowed(isCurveShortcutsItem());
    eld.getRoutingStyle().setCurveUTurnSymmetry(getCurveUTurnSymmetryItem());
    eld.setMinimumFirstSegmentLength(getMinimumFirstSegmentLengthItem());
    eld.setMinimumLastSegmentLength(getMinimumLastSegmentLengthItem());

    eld.setMinimumDistance(getMinimumEdgeDistanceItem());
    eld.setMinimumLength(getMinimumEdgeLengthItem());

    eld.setMinimumSlope(getMinimumSlopeItem());

    eld.setSourcePortOptimizationEnabled(isPcOptimizationEnabledItem());
    eld.setTargetPortOptimizationEnabled(isPcOptimizationEnabledItem());

    eld.setRecursiveEdgeStyle(getRecursiveEdgeStyleItem());

    nld.setMinimumDistance(Math.min(layout.getNodeToNodeDistance(), layout.getNodeToEdgeDistance()));
    nld.setMinimumLayerHeight(0);
    nld.setLayerAlignment(getLayerAlignmentItem());


    OrientationLayout ol = (OrientationLayout)layout.getOrientationLayout();
    ol.setOrientation(OrientationItem);

    if (isConsideringNodeLabelsItem()) {
      layout.setNodeLabelConsiderationEnabled(true);
      layout.getNodeLayoutDescriptor().setNodeLabelMode(NodeLabelMode.CONSIDER_FOR_DRAWING);
    } else {
      layout.setNodeLabelConsiderationEnabled(false);
    }

    if (getEdgeLabelingItem() != EnumEdgeLabeling.NONE) {
      if (getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC) {
        layout.setIntegratedEdgeLabelingEnabled(false);

        GenericLabeling labeling = new GenericLabeling();
        labeling.setNodeLabelPlacementEnabled(false);
        labeling.setEdgeLabelPlacementEnabled(true);
        labeling.setAutoFlippingEnabled(true);
        labeling.setAmbiguityReductionEnabled(isReducingAmbiguityItem());
        labeling.setProfitModel(new SimpleProfitModel());
        layout.setLabelingEnabled(true);
        layout.setLabeling(labeling);
      } else if (getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED) {
        layout.setIntegratedEdgeLabelingEnabled(true);
        ((SimplexNodePlacer)layout.getNodePlacer()).setLabelCompactionEnabled(isCompactingEdgeLabelPlacementItem());
      }
    } else {
      layout.setIntegratedEdgeLabelingEnabled(false);
    }

    layout.setFromScratchLayeringStrategy(getRankingPolicyItem());
    layout.setComponentArrangementPolicy(getComponentArrangementPolicyItem());
    ((SimplexNodePlacer)layout.getNodePlacer()).setNodeCompactionEnabled(isNodeCompactionEnabledItem());
    ((SimplexNodePlacer)layout.getNodePlacer()).setEdgeStraighteningEnabled(isStraighteningEdgesItem());

    //configure AsIsLayerer
    ILayerer layerer = layout.getLayoutMode() == LayoutMode.FROM_SCRATCH ? layout.getFromScratchLayerer() : layout.getFixedElementsLayerer();
    AsIsLayerer ail = (layerer instanceof AsIsLayerer) ? (AsIsLayerer)layerer : null;
    if (ail != null) {
      ail.setNodeHalo(getHaloItem());
      ail.setNodeScalingFactor(getScaleItem());
      ail.setMinimumNodeSize(getMinimumSizeItem());
      ail.setMaximumNodeSize(getMaximumSizeItem());
    }

    //configure grouping
    ((SimplexNodePlacer)layout.getNodePlacer()).setGroupCompactionStrategy(getGroupHorizontalCompactionItem());

    if (!fromSketch && getGroupLayeringStrategyItem() == GroupLayeringStrategyOptions.LAYOUT_GROUPS) {
      layout.setGroupAlignmentPolicy(getGroupAlignmentItem());
      layout.setGroupCompactionEnabled(isGroupCompactionEnabledItem());
      layout.setRecursiveGroupLayeringEnabled(true);
    } else {
      layout.setRecursiveGroupLayeringEnabled(false);
    }

    if (isTreatingRootGroupAsSwimlanesItem()) {
      TopLevelGroupToSwimlaneStage stage = new TopLevelGroupToSwimlaneStage();
      stage.setSwimlanesFromSketchOrderingEnabled(isUsingOrderFromSketchItem());
      stage.setSpacing(getSwimlineSpacingItem());
      layout.appendStage(stage);
    }

    layout.setBackLoopRoutingEnabled(isBackloopRoutingEnabledItem());
    layout.setBackLoopRoutingForSelfLoopsEnabled(isBackloopRoutingForSelfLoopsEnabledItem());
    layout.setMaximumDuration(getMaximumDurationItem() * 1000);

    if (isGridEnabledItem()) {
      layout.setGridSpacing(getGridSpacingItem());
    }

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    HierarchicLayoutData layoutData = new HierarchicLayoutData();

    boolean incrementalLayout = isPlacingSelectedElementsIncrementallyItem();
    IGraphSelection selection = graphComponent.getSelection();
    boolean selectedElements = selection.getSelectedEdges().size() > 0 || selection.getSelectedNodes().size() > 0;

    if (incrementalLayout && selectedElements) {
      // configure the mode
      final IIncrementalHintsFactory ihf = ((HierarchicLayout)layout).createIncrementalHintsFactory();
      layoutData.setIncrementalHints(item -> {
        // Return the correct hint type for each model item that appears in one of these sets
        if (item instanceof INode && selection.isSelected(item)) {
          return ihf.createLayerIncrementallyHint(item);
        }
        if (item instanceof IEdge && selection.isSelected(item)) {
          return ihf.createSequenceIncrementallyHint(item);
        }
        return null;
      });
    }

    if (getRankingPolicyItem() == LayeringStrategy.BFS) {
      layoutData.setBfsLayererCoreNodes(selection::isSelected);
    }

    if (isGridEnabledItem()) {
      final NodeLayoutDescriptor nld = ((HierarchicLayout)layout).getNodeLayoutDescriptor();
      layoutData.setNodeLayoutDescriptors(node -> {
        NodeLayoutDescriptor descriptor = new NodeLayoutDescriptor();
        descriptor.setLayerAlignment(nld.getLayerAlignment());
        descriptor.setMinimumDistance(nld.getMinimumDistance());
        descriptor.setMinimumLayerHeight(nld.getMinimumLayerHeight());
        descriptor.setNodeLabelMode(nld.getNodeLabelMode());
        // anchor nodes on grid according to their alignment within the layer
        descriptor.setGridReference(new YPoint(0.0, (nld.getLayerAlignment() - 0.5) * node.getLayout().getHeight()));
        descriptor.setPortAssignment(HierarchicLayoutConfig.this.getGridPortAssignmentItem());
        return descriptor;
      });
    }

    if (isConsideringEdgeDirectednessItem()) {
      layoutData.setEdgeDirectedness(new Function<IEdge, Double>(){
        final Double one = Double.valueOf(1.0);
        final Double zero = Double.valueOf(0.0);
        public Double apply( IEdge edge ) {
          IEdgeStyle style = edge.getStyle();
          return style instanceof PolylineEdgeStyle &&
                 ((PolylineEdgeStyle) style).getTargetArrow() != IArrow.NONE
                 ? one : zero;
        }
      });
    }

    if (isConsideringEdgeThicknessItem()) {
      layoutData.setEdgeThickness(edge -> {
        IEdgeStyle style = edge.getStyle();
        return style instanceof PolylineEdgeStyle ? ((PolylineEdgeStyle) style).getPen().getThickness() : 1.0;
      });
    }

    if (isPlacingSubComponentsSeparatelyItem()) {
      // layout all siblings with label 'TL' separately with tree layout
      TreeLayout treeLayout = new TreeLayout();
      treeLayout.setDefaultNodePlacer(new LeftRightNodePlacer());
      for (Collection<INode> listOfNodes : findSubComponents(graphComponent.getGraph(), "TL")) {
        layoutData.getSubcomponents().add(new SubcomponentDescriptor(treeLayout)).setItems(listOfNodes);
      }
      // layout all siblings with label 'HL' separately with hierarchical layout
      HierarchicLayout hierarchicLayout = new HierarchicLayout();
      hierarchicLayout.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
      for (Collection<INode> listOfNodes : findSubComponents(graphComponent.getGraph(), "HL")) {
        layoutData.getSubcomponents().add(new SubcomponentDescriptor(hierarchicLayout)).setItems(listOfNodes);
      }
      // layout all siblings with label 'OL' separately with organic layout
      OrganicLayout organicLayout = new OrganicLayout();
      organicLayout.setPreferredEdgeLength(100);
      organicLayout.setDeterministicModeEnabled(true);
      for (Collection<INode> listOfNodes : findSubComponents(graphComponent.getGraph(), "OL")) {
        layoutData.getSubcomponents().add(new SubcomponentDescriptor(organicLayout)).setItems(listOfNodes);
      }
    }

    if (isHighlightCriticalPath()) {
      // highlight the longest path in the graph as critical path
      // since the longest path algorithm only works for acyclic graphs,
      // feedback edges and self loops have to be excluded here
      final FeedbackEdgeSet.Result feedbackEdgeSetResult = new FeedbackEdgeSet().run(graphComponent.getGraph());
      LongestPath longestPath2 = new LongestPath();
      longestPath2.getSubgraphEdges().getExcludes().setPredicate(new Predicate<IEdge>(){
        public boolean test( IEdge edge ) {
          return feedbackEdgeSetResult.getFeedbackEdgeSet().contains(edge) || edge.isSelfloop();
        }
      });
      final LongestPath.Result longestPath = longestPath2.run(graphComponent.getGraph());
      if (longestPath.getEdges().size() > 0) {
        layoutData.getCriticalEdgePriorities().setFunction(new Function<IEdge, Integer>(){
          public Integer apply( IEdge edge ) {
            if (longestPath.getEdges().contains(edge)) {
              return 10;
            }
            return 1;
          }
        });
      }
    }

    if (isAutomaticBusRouting()) {
      HashSet<INode> allBusNodes = new HashSet<INode>();
      for (INode node : graphComponent.getGraph().getNodes()) {
        if (!graphComponent.getGraph().isGroupNode(node) && !allBusNodes.contains(node)) {
          // search for good opportunities for bus structures rooted at this node
          if (graphComponent.getGraph().inDegree(node) >= 4) {
            BusDescriptor busDescriptor = new BusDescriptor();
            Collection<IEdge> busEdges = getBusEdges(graphComponent.getGraph(), node, allBusNodes, graphComponent.getGraph().inEdgesAt(node));
            if (!busEdges.isEmpty()) {
              layoutData.getBuses().add(busDescriptor).setItems(busEdges);
            }
          }
          if (graphComponent.getGraph().outDegree(node) >= 4) {
            BusDescriptor busDescriptor = new BusDescriptor();
            Collection<IEdge> busEdges = getBusEdges(graphComponent.getGraph(), node, allBusNodes, graphComponent.getGraph().outEdgesAt(node));
            if (!busEdges.isEmpty()) {
              layoutData.getBuses().add(busDescriptor).setItems(busEdges);
            }
          }
        }
      }
    }

    return layoutData.combineWith(createLabelingLayoutData(graphComponent.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem()));
  }

  private static boolean test( INode node, String text ) {
    Iterator<ILabel> it = node.getLabels().iterator();
    return it.hasNext() && text.equals(it.next().getText());
  }

  private Collection<IEdge> getBusEdges( IGraph graph, INode node, HashSet<INode> allBusNodes, Iterable<IEdge> edges ) {
    if (graph.isGroupNode(node)) {
      // exclude groups for bus structures
      return new ArrayList<IEdge>();
    }

    HashSet<INode> busNodes = new HashSet<INode>();
    // count the edges that are not bus edges but connect to the bus nodes
    // -> if there are many, then the bus structure may not look that great
    int interEdgeCount = 0;
    ArrayList<IEdge> busEdges = new ArrayList<IEdge>();
    Stack<INode> busNodesWithOtherEdges = new Stack<INode>();
    HashMap<INode, IEdge> node2BusEdge = new HashMap<INode, IEdge>();
    for (IEdge edge : edges) {
      INode other = (INode)edge.opposite(node);
      if (!busNodes.contains(other)) {
        busNodes.add(other);
        busEdges.add(edge);
        node2BusEdge.put(other, edge);
        int otherEdgesCount = graph.degree(other) - 1;
        if (otherEdgesCount > 0) {
          busNodesWithOtherEdges.push(other);
          interEdgeCount += otherEdgesCount;
        }
      }
    }

    ArrayList<IEdge> finalBusEdges = new ArrayList<IEdge>();
    HashSet<IEdge> removedBusEdges = new HashSet<IEdge>();
    int busSize = busNodes.size();
    while (busSize >= 4) {
      if (interEdgeCount <= busSize * 0.25) {
        // okay accept this as a bus
        for (IEdge edge : busEdges) {
          if (!removedBusEdges.contains(edge)) {
            finalBusEdges.add(edge);
            allBusNodes.add((INode)edge.opposite(node));
          }
        }
        break;
      } else {
        // this bus has too many other edges remove some if possible
        if (!busNodesWithOtherEdges.isEmpty()) {
          INode nodeToRemove = busNodesWithOtherEdges.pop();
          removedBusEdges.add(node2BusEdge.get(nodeToRemove));
          busSize--;
          interEdgeCount -= graph.degree(nodeToRemove) - 1;
        }
      }
    }

    return finalBusEdges;
  }

  /**
   * Determines sub-components by label text and group membership. This is necessary because {@link HierarchicLayout} does
   * not support sub-components with nodes that belong to different parent groups.
   */
  private Collection<Collection<INode>> findSubComponents( IGraph graph, String labelText ) {
    HashMap<INode, Collection<INode>> nodeToComponent = new HashMap<INode, Collection<INode>>();
    for (INode node : graph.getNodes()) {
      if (node.getLabels().size() > 0 && node.getLabels().getItem(0).getText().compareTo(labelText) == 0) {
        INode tmp;
        INode parent = (tmp = graph.getParent(node)) != null ? tmp : root;
        if (!nodeToComponent.containsKey(parent)) {
          nodeToComponent.put(parent, new ArrayList<INode>());
        }
        nodeToComponent.get(parent).add(node);
      }
    }
    return nodeToComponent.values();
  }

  private final INode root = new SimpleNode();

  /**
   * Enables different layout styles for possible detected sub-components.
   */
  public final void enableSubComponents() {
    setPlacingSubComponentsSeparatelyItem(true);
  }

  public final void enableAutomaticBusRouting() {
    setAutomaticBusRouting(true);
  }

  public final void enableCurvedRouting() {
    setEdgeRoutingItem(EdgeRoutingStyle.CURVED);
  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GeneralGroup;

  @Label("Incremental Layout")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 70)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object IncrementalGroup;

  @OptionGroupAnnotation(name = "GeneralGroup", position = 60)
  @Label("Minimum Distances")
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DistanceGroup;

  @Label("Edges")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object EdgeSettingsGroup;

  @Label("Layers")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object RankGroup;

  @Label("Labeling")
  @OptionGroupAnnotation(name = "RootGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LabelingGroup;

  @Label("Node Settings")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object NodePropertiesGroup;

  @Label("Edge Settings")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object EdgePropertiesGroup;

  @Label("Preferred Edge Label Placement")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object PreferredPlacementGroup;

  @Label("Grouping")
  @OptionGroupAnnotation(name = "RootGroup", position = 50)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GroupingGroup;

  @Label("Swimlanes")
  @OptionGroupAnnotation(name = "RootGroup", position = 60)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object SwimlanesGroup;

  @Label("Grid")
  @OptionGroupAnnotation(name = "RootGroup", position = 70)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GridGroup;

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>The hierarchical layout style highlights the main direction or flow of a directed graph. The layout algorithm places " +
           "the nodes of a graph in hierarchically arranged layers such that the (majority of) its edges follows the overall " +
           "orientation, for example, top-to-bottom.</p>" +
           "<p>This style is tailored for application domains in which it is crucial to clearly visualize the dependency relations between " +
           "entities. In particular, if such relations form a chain of dependencies between entities, this " +
           "layout style nicely exhibits them. Generally, whenever the direction of information flow matters, the hierarchical " +
           "layout style is an invaluable tool.</p>" +
           "<p>Suitable application domains of this layout style include, for example:</p>" +
           "<ul>" +
           "<li><p>Workflow visualization</p></li>" +
           "<li><p>Software engineering like call graph visualization or activity diagrams</p></li>" +
           "<li><p>Process modeling</p></li>" +
           "<li><p>Database modeling and Entity-Relationship diagrams</p></li>" +
           "<li><p>Bioinformatics, for example biochemical pathways</p></li>" +
           "<li><p>Network management</p></li>" +
           "<li><p>Decision diagrams</p></li>" +
           "</ul>";
  }

  private boolean placingSelectedElementsIncrementallyItem;

  @Label("Selected Elements Incrementally")
  @OptionGroupAnnotation(name = "IncrementalGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isPlacingSelectedElementsIncrementallyItem() {
    return this.placingSelectedElementsIncrementallyItem;
  }

  @Label("Selected Elements Incrementally")
  @OptionGroupAnnotation(name = "IncrementalGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setPlacingSelectedElementsIncrementallyItem( boolean value ) {
    this.placingSelectedElementsIncrementallyItem = value;
  }

  private boolean usingDrawingAsSketchItem;

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "IncrementalGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUsingDrawingAsSketchItem() {
    return this.usingDrawingAsSketchItem;
  }

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "IncrementalGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUsingDrawingAsSketchItem( boolean value ) {
    this.usingDrawingAsSketchItem = value;
  }

  @Label("Orientation")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutOrientation.class, stringValue = "TOP_TO_BOTTOM")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public LayoutOrientation OrientationItem;

  private boolean placingComponentsSeparatelyItem;

  @Label("Layout Components Separately")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isPlacingComponentsSeparatelyItem() {
    return this.placingComponentsSeparatelyItem;
  }

  @Label("Layout Components Separately")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setPlacingComponentsSeparatelyItem( boolean value ) {
    this.placingComponentsSeparatelyItem = value;
  }

  private boolean placingSymmetricItem;

  @Label("Symmetric Placement")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isPlacingSymmetricItem() {
    return this.placingSymmetricItem;
  }

  @Label("Symmetric Placement")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setPlacingSymmetricItem( boolean value ) {
    this.placingSymmetricItem = value;
  }

  private boolean placingSubComponentsSeparatelyItem;

  @Label("Layout Sub-components Separately")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 45)
  public final boolean isPlacingSubComponentsSeparatelyItem() {
    return this.placingSubComponentsSeparatelyItem;
  }

  @Label("Layout Sub-components Separately")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 45)
  public final void setPlacingSubComponentsSeparatelyItem( boolean value ) {
    this.placingSubComponentsSeparatelyItem = value;
  }

  private int maximumDurationItem;

  @Label("Maximum Duration")
  @MinMax(min = 0, max = 150)
  @DefaultValue(intValue = 5, valueType = DefaultValue.ValueType.INT_TYPE)
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMaximumDurationItem() {
    return this.maximumDurationItem;
  }

  @Label("Maximum Duration")
  @MinMax(min = 0, max = 150)
  @DefaultValue(intValue = 5, valueType = DefaultValue.ValueType.INT_TYPE)
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMaximumDurationItem( int value ) {
    this.maximumDurationItem = value;
  }

  private double nodeToNodeDistanceItem;

  @Label("Node to Node Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 10)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getNodeToNodeDistanceItem() {
    return this.nodeToNodeDistanceItem;
  }

  @Label("Node to Node Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 10)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setNodeToNodeDistanceItem( double value ) {
    this.nodeToNodeDistanceItem = value;
  }

  private double nodeToEdgeDistanceItem;

  @Label("Node to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getNodeToEdgeDistanceItem() {
    return this.nodeToEdgeDistanceItem;
  }

  @Label("Node to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setNodeToEdgeDistanceItem( double value ) {
    this.nodeToEdgeDistanceItem = value;
  }

  private double edgeToEdgeDistanceItem;

  @Label("Edge to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 30)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getEdgeToEdgeDistanceItem() {
    return this.edgeToEdgeDistanceItem;
  }

  @Label("Edge to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 30)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setEdgeToEdgeDistanceItem( double value ) {
    this.edgeToEdgeDistanceItem = value;
  }

  private double minimumLayerDistanceItem;

  @Label("Layer to Layer Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 40)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumLayerDistanceItem() {
    return this.minimumLayerDistanceItem;
  }

  @Label("Layer to Layer Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 40)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumLayerDistanceItem( double value ) {
    this.minimumLayerDistanceItem = value;
  }

  private EdgeRoutingStyle edgeRoutingItem = EdgeRoutingStyle.ORTHOGONAL;

  @Label("Edge Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeRoutingStyle.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Octilinear", value = "OCTILINEAR")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Polyline", value = "POLYLINE")
  @EnumValueAnnotation(label = "Curved", value = "CURVED")
  public final EdgeRoutingStyle getEdgeRoutingItem() {
    return this.edgeRoutingItem;
  }

  @Label("Edge Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeRoutingStyle.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Octilinear", value = "OCTILINEAR")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Polyline", value = "POLYLINE")
  @EnumValueAnnotation(label = "Curved", value = "CURVED")
  public final void setEdgeRoutingItem( EdgeRoutingStyle value ) {
    this.edgeRoutingItem = value;
  }

  private boolean backloopRoutingEnabledItem;

  @Label("Backloop Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isBackloopRoutingEnabledItem() {
    return this.backloopRoutingEnabledItem;
  }

  @Label("Backloop Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setBackloopRoutingEnabledItem( boolean value ) {
    this.backloopRoutingEnabledItem = value;
  }

  private boolean backloopRoutingForSelfLoopsEnabledItem;

  @Label("Backloop Routing For Self-loops")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isBackloopRoutingForSelfLoopsEnabledItem() {
    return this.backloopRoutingForSelfLoopsEnabledItem;
  }

  @Label("Backloop Routing For Self-loops")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setBackloopRoutingForSelfLoopsEnabledItem( boolean value ) {
    this.backloopRoutingForSelfLoopsEnabledItem = value;
  }

  public final boolean isBackloopRoutingForSelfLoopsItemDisabled() {
    return !isBackloopRoutingEnabledItem();
  }

  private boolean automaticEdgeGroupingEnabledItem;

  @Label("Automatic Edge Grouping")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAutomaticEdgeGroupingEnabledItem() {
    return this.automaticEdgeGroupingEnabledItem;
  }

  @Label("Automatic Edge Grouping")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAutomaticEdgeGroupingEnabledItem( boolean value ) {
    this.automaticEdgeGroupingEnabledItem = value;
  }

  private boolean automaticBusRouting;

  @Label("Automatic Bus Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 45)
  public final boolean isAutomaticBusRouting() {
    return this.automaticBusRouting;
  }

  @Label("Automatic Bus Routing")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 45)
  public final void setAutomaticBusRouting( boolean value ) {
    this.automaticBusRouting = value;
  }

  private boolean highlightCriticalPath;

  @Label("Highlight Critical Path")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 50)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isHighlightCriticalPath() {
    return this.highlightCriticalPath;
  }

  @Label("Highlight Critical Path")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 50)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setHighlightCriticalPath( boolean value ) {
    this.highlightCriticalPath = value;
  }

  private double minimumFirstSegmentLengthItem;

  @Label("Minimum First Segment Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 60)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumFirstSegmentLengthItem() {
    return this.minimumFirstSegmentLengthItem;
  }

  @Label("Minimum First Segment Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 60)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumFirstSegmentLengthItem( double value ) {
    this.minimumFirstSegmentLengthItem = value;
  }

  private double minimumLastSegmentLengthItem;

  @Label("Minimum Last Segment Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 70)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumLastSegmentLengthItem() {
    return this.minimumLastSegmentLengthItem;
  }

  @Label("Minimum Last Segment Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 70)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumLastSegmentLengthItem( double value ) {
    this.minimumLastSegmentLengthItem = value;
  }

  private double minimumEdgeLengthItem;

  @Label("Minimum Edge Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 80)
  @DefaultValue(doubleValue = 20.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumEdgeLengthItem() {
    return this.minimumEdgeLengthItem;
  }

  @Label("Minimum Edge Length")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 80)
  @DefaultValue(doubleValue = 20.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumEdgeLengthItem( double value ) {
    this.minimumEdgeLengthItem = value;
  }

  private double minimumEdgeDistanceItem;

  @Label("Minimum Edge Distance")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 90)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumEdgeDistanceItem() {
    return this.minimumEdgeDistanceItem;
  }

  @Label("Minimum Edge Distance")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 90)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumEdgeDistanceItem( double value ) {
    this.minimumEdgeDistanceItem = value;
  }

  private double minimumSlopeItem;

  @MinMax(min = 0.0d, max = 5.0d, step = 0.01d)
  @Label("Minimum Slope")
  @DefaultValue(doubleValue = 0.25d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumSlopeItem() {
    return this.minimumSlopeItem;
  }

  @MinMax(min = 0.0d, max = 5.0d, step = 0.01d)
  @Label("Minimum Slope")
  @DefaultValue(doubleValue = 0.25d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumSlopeItem( double value ) {
    this.minimumSlopeItem = value;
  }

  public final boolean isMinimumSlopeItemDisabled() {
    return getEdgeRoutingItem() != EdgeRoutingStyle.POLYLINE && getEdgeRoutingItem() != EdgeRoutingStyle.CURVED;
  }

  private boolean consideringEdgeDirectednessItem;

  @Label("Arrows Define Edge Direction")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 110)
  public final boolean isConsideringEdgeDirectednessItem() {
    return this.consideringEdgeDirectednessItem;
  }

  @Label("Arrows Define Edge Direction")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 110)
  public final void setConsideringEdgeDirectednessItem( boolean value ) {
    this.consideringEdgeDirectednessItem = value;
  }

  private boolean consideringEdgeThicknessItem;

  @Label("Consider Edge Thickness")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 120)
  public final boolean isConsideringEdgeThicknessItem() {
    return this.consideringEdgeThicknessItem;
  }

  @Label("Consider Edge Thickness")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 120)
  public final void setConsideringEdgeThicknessItem( boolean value ) {
    this.consideringEdgeThicknessItem = value;
  }

  private boolean pcOptimizationEnabledItem;

  @Label("Port Constraint Optimization")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 130)
  public final boolean isPcOptimizationEnabledItem() {
    return this.pcOptimizationEnabledItem;
  }

  @Label("Port Constraint Optimization")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 130)
  public final void setPcOptimizationEnabledItem( boolean value ) {
    this.pcOptimizationEnabledItem = value;
  }

  private boolean straighteningEdgesItem;

  @Label("Straighten Edges")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 140)
  public final boolean isStraighteningEdgesItem() {
    return this.straighteningEdgesItem;
  }

  @Label("Straighten Edges")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 140)
  public final void setStraighteningEdgesItem( boolean value ) {
    this.straighteningEdgesItem = value;
  }

  public final boolean isStraighteningEdgesItemDisabled() {
    return isPlacingSymmetricItem();
  }

  private RecursiveEdgeStyle recursiveEdgeStyleItem = RecursiveEdgeStyle.OFF;

  @Label("Recursive Edge Routing Style")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 150)
  @EnumValueAnnotation(label = "Off", value = "OFF")
  @EnumValueAnnotation(label = "Directed", value = "DIRECTED")
  @EnumValueAnnotation(label = "Undirected", value = "UNDIRECTED")
  public final RecursiveEdgeStyle getRecursiveEdgeStyleItem() {
    return this.recursiveEdgeStyleItem;
  }

  @Label("Recursive Edge Routing Style")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 150)
  @EnumValueAnnotation(label = "Off", value = "OFF")
  @EnumValueAnnotation(label = "Directed", value = "DIRECTED")
  @EnumValueAnnotation(label = "Undirected", value = "UNDIRECTED")
  public final void setRecursiveEdgeStyleItem( RecursiveEdgeStyle value ) {
    this.recursiveEdgeStyleItem = value;
  }

  private double curveUTurnSymmetryItem;

  @Label("U-turn Symmetry")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 160)
  @DefaultValue(intValue = 0, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.1d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getCurveUTurnSymmetryItem() {
    return this.curveUTurnSymmetryItem;
  }

  @Label("U-turn Symmetry")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 160)
  @DefaultValue(intValue = 0, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.1d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setCurveUTurnSymmetryItem( double value ) {
    this.curveUTurnSymmetryItem = value;
  }

  public final boolean isCurveUTurnSymmetryItemDisabled() {
    return getEdgeRoutingItem() != EdgeRoutingStyle.CURVED;
  }

  private boolean curveShortcutsItem;

  @Label("Allow Shortcuts")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 170)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isCurveShortcutsItem() {
    return this.curveShortcutsItem;
  }

  @Label("Allow Shortcuts")
  @OptionGroupAnnotation(name = "EdgeSettingsGroup", position = 170)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setCurveShortcutsItem( boolean value ) {
    this.curveShortcutsItem = value;
  }

  public final boolean isCurveShortcutsItemDisabled() {
    return getEdgeRoutingItem() != EdgeRoutingStyle.CURVED;
  }

  private LayeringStrategy rankingPolicyItem = LayeringStrategy.HIERARCHICAL_TOPMOST;

  @Label("Layer Assignment Policy")
  @OptionGroupAnnotation(name = "RankGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayeringStrategy.class, stringValue = "HIERARCHICAL_OPTIMAL")
  @EnumValueAnnotation(label = "Hierarchical - Optimal", value = "HIERARCHICAL_OPTIMAL")
  @EnumValueAnnotation(label = "Hierarchical - Tight Tree Heuristic", value = "HIERARCHICAL_TIGHT_TREE")
  @EnumValueAnnotation(label = "BFS Layering", value = "BFS")
  @EnumValueAnnotation(label = "From Sketch", value = "FROM_SKETCH")
  @EnumValueAnnotation(label = "Hierarchical - Topmost", value = "HIERARCHICAL_TOPMOST")
  public final LayeringStrategy getRankingPolicyItem() {
    return this.rankingPolicyItem;
  }

  @Label("Layer Assignment Policy")
  @OptionGroupAnnotation(name = "RankGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayeringStrategy.class, stringValue = "HIERARCHICAL_OPTIMAL")
  @EnumValueAnnotation(label = "Hierarchical - Optimal", value = "HIERARCHICAL_OPTIMAL")
  @EnumValueAnnotation(label = "Hierarchical - Tight Tree Heuristic", value = "HIERARCHICAL_TIGHT_TREE")
  @EnumValueAnnotation(label = "BFS Layering", value = "BFS")
  @EnumValueAnnotation(label = "From Sketch", value = "FROM_SKETCH")
  @EnumValueAnnotation(label = "Hierarchical - Topmost", value = "HIERARCHICAL_TOPMOST")
  public final void setRankingPolicyItem( LayeringStrategy value ) {
    this.rankingPolicyItem = value;
  }

  private double layerAlignmentItem;

  @Label("Alignment within Layer")
  @OptionGroupAnnotation(name = "RankGroup", position = 20)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @EnumValueAnnotation(label = "Top Border of Nodes", value = "0")
  @EnumValueAnnotation(label = "Center of Nodes", value = "0.5")
  @EnumValueAnnotation(label = "Bottom Border of Nodes", value = "1")
  public final double getLayerAlignmentItem() {
    return this.layerAlignmentItem;
  }

  @Label("Alignment within Layer")
  @OptionGroupAnnotation(name = "RankGroup", position = 20)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @EnumValueAnnotation(label = "Top Border of Nodes", value = "0")
  @EnumValueAnnotation(label = "Center of Nodes", value = "0.5")
  @EnumValueAnnotation(label = "Bottom Border of Nodes", value = "1")
  public final void setLayerAlignmentItem( double value ) {
    this.layerAlignmentItem = value;
  }

  private ComponentArrangementPolicy componentArrangementPolicyItem = ComponentArrangementPolicy.COMPACT;

  @Label("Component Arrangement")
  @OptionGroupAnnotation(name = "RankGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ComponentArrangementPolicy.class, stringValue = "TOPMOST")
  @EnumValueAnnotation(label = "Topmost", value = "TOPMOST")
  @EnumValueAnnotation(label = "Compact", value = "COMPACT")
  public final ComponentArrangementPolicy getComponentArrangementPolicyItem() {
    return this.componentArrangementPolicyItem;
  }

  @Label("Component Arrangement")
  @OptionGroupAnnotation(name = "RankGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ComponentArrangementPolicy.class, stringValue = "TOPMOST")
  @EnumValueAnnotation(label = "Topmost", value = "TOPMOST")
  @EnumValueAnnotation(label = "Compact", value = "COMPACT")
  public final void setComponentArrangementPolicyItem( ComponentArrangementPolicy value ) {
    this.componentArrangementPolicyItem = value;
  }

  private boolean nodeCompactionEnabledItem;

  @OptionGroupAnnotation(name = "RankGroup", position = 40)
  @Label("Stacked Placement")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isNodeCompactionEnabledItem() {
    return this.nodeCompactionEnabledItem;
  }

  @OptionGroupAnnotation(name = "RankGroup", position = 40)
  @Label("Stacked Placement")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setNodeCompactionEnabledItem( boolean value ) {
    this.nodeCompactionEnabledItem = value;
  }

  @OptionGroupAnnotation(name = "RankGroup", position = 50)
  @Label("From Sketch Layer Assignment")
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object SketchGroup;

  private double scaleItem;

  @OptionGroupAnnotation(name = "SketchGroup", position = 10)
  @MinMax(min = 0.0d, max = 5.0d, step = 0.01d)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @Label("Scale")
  @ComponentType(ComponentTypes.SLIDER)
  public final double getScaleItem() {
    return this.scaleItem;
  }

  @OptionGroupAnnotation(name = "SketchGroup", position = 10)
  @MinMax(min = 0.0d, max = 5.0d, step = 0.01d)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @Label("Scale")
  @ComponentType(ComponentTypes.SLIDER)
  public final void setScaleItem( double value ) {
    this.scaleItem = value;
  }

  public final boolean isScaleItemDisabled() {
    return this.getRankingPolicyItem() != LayeringStrategy.FROM_SKETCH;
  }

  private double haloItem;

  @OptionGroupAnnotation(name = "SketchGroup", position = 20)
  @Label("Halo")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getHaloItem() {
    return this.haloItem;
  }

  @OptionGroupAnnotation(name = "SketchGroup", position = 20)
  @Label("Halo")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setHaloItem( double value ) {
    this.haloItem = value;
  }

  public final boolean isHaloItemDisabled() {
    return this.getRankingPolicyItem() != LayeringStrategy.FROM_SKETCH;
  }

  private double minimumSizeItem;

  @OptionGroupAnnotation(name = "SketchGroup", position = 30)
  @Label("Minimum Size")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 1000)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumSizeItem() {
    return this.minimumSizeItem;
  }

  @OptionGroupAnnotation(name = "SketchGroup", position = 30)
  @Label("Minimum Size")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 1000)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumSizeItem( double value ) {
    this.minimumSizeItem = value;
  }

  public final boolean isMinimumSizeItemDisabled() {
    return this.getRankingPolicyItem() != LayeringStrategy.FROM_SKETCH;
  }

  private double maximumSizeItem;

  @OptionGroupAnnotation(name = "SketchGroup", position = 40)
  @Label("Maximum Size")
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 1000)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMaximumSizeItem() {
    return this.maximumSizeItem;
  }

  @OptionGroupAnnotation(name = "SketchGroup", position = 40)
  @Label("Maximum Size")
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 1000)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMaximumSizeItem( double value ) {
    this.maximumSizeItem = value;
  }

  public final boolean isMaximumSizeItemDisabled() {
    return this.getRankingPolicyItem() != LayeringStrategy.FROM_SKETCH;
  }

  private boolean consideringNodeLabelsItem;

  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @Label("Consider Node Labels")
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsideringNodeLabelsItem() {
    return this.consideringNodeLabelsItem;
  }

  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @Label("Consider Node Labels")
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsideringNodeLabelsItem( boolean value ) {
    this.consideringNodeLabelsItem = value;
  }

  public enum EnumEdgeLabeling {
    NONE(0),

    INTEGRATED(1),

    GENERIC(2);

    private final int value;

    private EnumEdgeLabeling( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumEdgeLabeling fromOrdinal( int ordinal ) {
      for (EnumEdgeLabeling current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  private EnumEdgeLabeling edgeLabelingItem = EnumEdgeLabeling.NONE;

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumEdgeLabeling.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Integrated", value = "INTEGRATED")
  @EnumValueAnnotation(label = "Generic", value = "GENERIC")
  public final EnumEdgeLabeling getEdgeLabelingItem() {
    return this.edgeLabelingItem;
  }

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumEdgeLabeling.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Integrated", value = "INTEGRATED")
  @EnumValueAnnotation(label = "Generic", value = "GENERIC")
  public final void setEdgeLabelingItem( EnumEdgeLabeling value ) {
    this.edgeLabelingItem = value;
  }

  private boolean compactingEdgeLabelPlacementItem;

  @Label("Compact Placement")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 30)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isCompactingEdgeLabelPlacementItem() {
    return this.compactingEdgeLabelPlacementItem;
  }

  @Label("Compact Placement")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 30)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setCompactingEdgeLabelPlacementItem( boolean value ) {
    this.compactingEdgeLabelPlacementItem = value;
  }

  public final boolean isCompactingEdgeLabelPlacementItemDisabled() {
    return getEdgeLabelingItem() != EnumEdgeLabeling.INTEGRATED;
  }

  private boolean reducingAmbiguityItem;

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 40)
  public final boolean isReducingAmbiguityItem() {
    return this.reducingAmbiguityItem;
  }

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 40)
  public final void setReducingAmbiguityItem( boolean value ) {
    this.reducingAmbiguityItem = value;
  }

  public final boolean isReducingAmbiguityItemDisabled() {
    return getEdgeLabelingItem() != EnumEdgeLabeling.GENERIC;
  }

  private LayoutConfiguration.EnumLabelPlacementOrientation labelPlacementOrientationItem = LayoutConfiguration.EnumLabelPlacementOrientation.PARALLEL;

  @Label("Orientation")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementOrientation.class, stringValue = "HORIZONTAL")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Vertical", value = "VERTICAL")
  public final LayoutConfiguration.EnumLabelPlacementOrientation getLabelPlacementOrientationItem() {
    return this.labelPlacementOrientationItem;
  }

  @Label("Orientation")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementOrientation.class, stringValue = "HORIZONTAL")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Vertical", value = "VERTICAL")
  public final void setLabelPlacementOrientationItem( LayoutConfiguration.EnumLabelPlacementOrientation value ) {
    this.labelPlacementOrientationItem = value;
  }

  public final boolean isLabelPlacementOrientationItemDisabled() {
    return getEdgeLabelingItem() == EnumEdgeLabeling.NONE;
  }

  private LayoutConfiguration.EnumLabelPlacementAlongEdge labelPlacementAlongEdgeItem = LayoutConfiguration.EnumLabelPlacementAlongEdge.ANYWHERE;

  @Label("Along Edge")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementAlongEdge.class, stringValue = "CENTERED")
  @EnumValueAnnotation(label = "Anywhere", value = "ANYWHERE")
  @EnumValueAnnotation(label = "At Source", value = "AT_SOURCE")
  @EnumValueAnnotation(label = "At Source Port", value = "AT_SOURCE_PORT")
  @EnumValueAnnotation(label = "At Target", value = "AT_TARGET")
  @EnumValueAnnotation(label = "At Target Port", value = "AT_TARGET_PORT")
  @EnumValueAnnotation(label = "Centered", value = "CENTERED")
  public final LayoutConfiguration.EnumLabelPlacementAlongEdge getLabelPlacementAlongEdgeItem() {
    return this.labelPlacementAlongEdgeItem;
  }

  @Label("Along Edge")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementAlongEdge.class, stringValue = "CENTERED")
  @EnumValueAnnotation(label = "Anywhere", value = "ANYWHERE")
  @EnumValueAnnotation(label = "At Source Port", value = "AT_SOURCE_PORT")
  @EnumValueAnnotation(label = "At Target Port", value = "AT_TARGET_PORT")
  @EnumValueAnnotation(label = "At Source", value = "AT_SOURCE")
  @EnumValueAnnotation(label = "At Target", value = "AT_TARGET")
  @EnumValueAnnotation(label = "Centered", value = "CENTERED")
  public final void setLabelPlacementAlongEdgeItem( LayoutConfiguration.EnumLabelPlacementAlongEdge value ) {
    this.labelPlacementAlongEdgeItem = value;
  }

  public final boolean isLabelPlacementAlongEdgeItemDisabled() {
    return getEdgeLabelingItem() == EnumEdgeLabeling.NONE;
  }

  private LayoutConfiguration.EnumLabelPlacementSideOfEdge labelPlacementSideOfEdgeItem = LayoutConfiguration.EnumLabelPlacementSideOfEdge.ANYWHERE;

  @Label("Side of Edge")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementSideOfEdge.class, stringValue = "ON_EDGE")
  @EnumValueAnnotation(label = "Anywhere", value = "ANYWHERE")
  @EnumValueAnnotation(label = "On Edge", value = "ON_EDGE")
  @EnumValueAnnotation(label = "Left", value = "LEFT")
  @EnumValueAnnotation(label = "Right", value = "RIGHT")
  @EnumValueAnnotation(label = "Left or Right", value = "LEFT_OR_RIGHT")
  public final LayoutConfiguration.EnumLabelPlacementSideOfEdge getLabelPlacementSideOfEdgeItem() {
    return this.labelPlacementSideOfEdgeItem;
  }

  @Label("Side of Edge")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementSideOfEdge.class, stringValue = "ON_EDGE")
  @EnumValueAnnotation(label = "Anywhere", value = "ANYWHERE")
  @EnumValueAnnotation(label = "On Edge", value = "ON_EDGE")
  @EnumValueAnnotation(label = "Left", value = "LEFT")
  @EnumValueAnnotation(label = "Right", value = "RIGHT")
  @EnumValueAnnotation(label = "Left or Right", value = "LEFT_OR_RIGHT")
  public final void setLabelPlacementSideOfEdgeItem( LayoutConfiguration.EnumLabelPlacementSideOfEdge value ) {
    this.labelPlacementSideOfEdgeItem = value;
  }

  public final boolean isLabelPlacementSideOfEdgeItemDisabled() {
    return getEdgeLabelingItem() == EnumEdgeLabeling.NONE;
  }

  private double labelPlacementDistanceItem;

  @Label("Distance")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 40)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 40.0d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getLabelPlacementDistanceItem() {
    return this.labelPlacementDistanceItem;
  }

  @Label("Distance")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 40)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 40.0d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setLabelPlacementDistanceItem( double value ) {
    this.labelPlacementDistanceItem = value;
  }

  public final boolean isLabelPlacementDistanceItemDisabled() {
    return getEdgeLabelingItem() == EnumEdgeLabeling.NONE || getLabelPlacementSideOfEdgeItem() == LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE;
  }

  public enum GroupLayeringStrategyOptions {
    LAYOUT_GROUPS(0),

    IGNORE_GROUPS(1);

    private final int value;

    private GroupLayeringStrategyOptions( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final GroupLayeringStrategyOptions fromOrdinal( int ordinal ) {
      for (GroupLayeringStrategyOptions current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  private GroupLayeringStrategyOptions groupLayeringStrategyItem = GroupLayeringStrategyOptions.LAYOUT_GROUPS;

  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @Label("Layering Strategy")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupLayeringStrategyOptions.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final GroupLayeringStrategyOptions getGroupLayeringStrategyItem() {
    return this.groupLayeringStrategyItem;
  }

  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @Label("Layering Strategy")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupLayeringStrategyOptions.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final void setGroupLayeringStrategyItem( GroupLayeringStrategyOptions value ) {
    this.groupLayeringStrategyItem = value;
  }

  public final boolean isGroupLayeringStrategyItemDisabled() {
    return isUsingDrawingAsSketchItem();
  }

  private GroupAlignmentPolicy groupAlignmentItem = null;

  @OptionGroupAnnotation(name = "GroupingGroup", position = 20)
  @Label("Vertical Alignment")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupAlignmentPolicy.class, stringValue = "TOP")
  @EnumValueAnnotation(label = "Top", value = "TOP")
  @EnumValueAnnotation(label = "Center", value = "CENTER")
  @EnumValueAnnotation(label = "Bottom", value = "BOTTOM")
  public final GroupAlignmentPolicy getGroupAlignmentItem() {
    return this.groupAlignmentItem;
  }

  @OptionGroupAnnotation(name = "GroupingGroup", position = 20)
  @Label("Vertical Alignment")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupAlignmentPolicy.class, stringValue = "TOP")
  @EnumValueAnnotation(label = "Top", value = "TOP")
  @EnumValueAnnotation(label = "Center", value = "CENTER")
  @EnumValueAnnotation(label = "Bottom", value = "BOTTOM")
  public final void setGroupAlignmentItem( GroupAlignmentPolicy value ) {
    this.groupAlignmentItem = value;
  }

  public final boolean isGroupAlignmentItemDisabled() {
    return getGroupLayeringStrategyItem() != GroupLayeringStrategyOptions.LAYOUT_GROUPS || isGroupCompactionEnabledItem();
  }

  private boolean groupCompactionEnabledItem;

  @OptionGroupAnnotation(name = "GroupingGroup", position = 30)
  @Label("Compact Layers")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isGroupCompactionEnabledItem() {
    return this.groupCompactionEnabledItem;
  }

  @OptionGroupAnnotation(name = "GroupingGroup", position = 30)
  @Label("Compact Layers")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setGroupCompactionEnabledItem( boolean value ) {
    this.groupCompactionEnabledItem = value;
  }

  public final boolean isGroupCompactionEnabledItemDisabled() {
    return getGroupLayeringStrategyItem() != GroupLayeringStrategyOptions.LAYOUT_GROUPS || isUsingDrawingAsSketchItem();
  }

  private GroupCompactionPolicy groupHorizontalCompactionItem = GroupCompactionPolicy.NONE;

  @OptionGroupAnnotation(name = "GroupingGroup", position = 40)
  @Label("Horizontal Group Compaction")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupCompactionPolicy.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Weak", value = "NONE")
  @EnumValueAnnotation(label = "Strong", value = "MAXIMAL")
  public final GroupCompactionPolicy getGroupHorizontalCompactionItem() {
    return this.groupHorizontalCompactionItem;
  }

  @OptionGroupAnnotation(name = "GroupingGroup", position = 40)
  @Label("Horizontal Group Compaction")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupCompactionPolicy.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Weak", value = "NONE")
  @EnumValueAnnotation(label = "Strong", value = "MAXIMAL")
  public final void setGroupHorizontalCompactionItem( GroupCompactionPolicy value ) {
    this.groupHorizontalCompactionItem = value;
  }

  private boolean treatingRootGroupAsSwimlanesItem;

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 10)
  @Label("Treat Groups as Swimlanes")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isTreatingRootGroupAsSwimlanesItem() {
    return this.treatingRootGroupAsSwimlanesItem;
  }

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 10)
  @Label("Treat Groups as Swimlanes")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setTreatingRootGroupAsSwimlanesItem( boolean value ) {
    this.treatingRootGroupAsSwimlanesItem = value;
  }

  private boolean usingOrderFromSketchItem;

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 20)
  @Label("Use Sketch for Lane Order")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUsingOrderFromSketchItem() {
    return this.usingOrderFromSketchItem;
  }

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 20)
  @Label("Use Sketch for Lane Order")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUsingOrderFromSketchItem( boolean value ) {
    this.usingOrderFromSketchItem = value;
  }

  public final boolean isUsingOrderFromSketchItemDisabled() {
    return !isTreatingRootGroupAsSwimlanesItem();
  }

  private double swimlineSpacingItem;

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 30)
  @Label("Lane Spacing")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getSwimlineSpacingItem() {
    return this.swimlineSpacingItem;
  }

  @OptionGroupAnnotation(name = "SwimlanesGroup", position = 30)
  @Label("Lane Spacing")
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setSwimlineSpacingItem( double value ) {
    this.swimlineSpacingItem = value;
  }

  public final boolean isSwimlineSpacingItemDisabled() {
    return !isTreatingRootGroupAsSwimlanesItem();
  }

  private boolean gridEnabledItem;

  @OptionGroupAnnotation(name = "GridGroup", position = 10)
  @Label("Grid")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isGridEnabledItem() {
    return this.gridEnabledItem;
  }

  @OptionGroupAnnotation(name = "GridGroup", position = 10)
  @Label("Grid")
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setGridEnabledItem( boolean value ) {
    this.gridEnabledItem = value;
  }

  private double gridSpacingItem;

  @OptionGroupAnnotation(name = "GridGroup", position = 20)
  @Label("Grid Spacing")
  @DefaultValue(doubleValue = 5.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getGridSpacingItem() {
    return this.gridSpacingItem;
  }

  @OptionGroupAnnotation(name = "GridGroup", position = 20)
  @Label("Grid Spacing")
  @DefaultValue(doubleValue = 5.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setGridSpacingItem( double value ) {
    this.gridSpacingItem = value;
  }

  public final boolean isGridSpacingItemDisabled() {
    return !isGridEnabledItem();
  }

  private PortAssignmentMode gridPortAssignmentItem = PortAssignmentMode.DEFAULT;

  @OptionGroupAnnotation(name = "GridGroup", position = 30)
  @Label("Grid Port Style")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortAssignmentMode.class, stringValue = "DEFAULT")
  @EnumValueAnnotation(label = "Default", value = "DEFAULT")
  @EnumValueAnnotation(label = "On Grid", value = "ON_GRID")
  @EnumValueAnnotation(label = "On Subgrid", value = "ON_SUBGRID")
  public final PortAssignmentMode getGridPortAssignmentItem() {
    return this.gridPortAssignmentItem;
  }

  @OptionGroupAnnotation(name = "GridGroup", position = 30)
  @Label("Grid Port Style")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortAssignmentMode.class, stringValue = "DEFAULT")
  @EnumValueAnnotation(label = "Default", value = "DEFAULT")
  @EnumValueAnnotation(label = "On Grid", value = "ON_GRID")
  @EnumValueAnnotation(label = "On Subgrid", value = "ON_SUBGRID")
  public final void setGridPortAssignmentItem( PortAssignmentMode value ) {
    this.gridPortAssignmentItem = value;
  }

  public final boolean isGridPortAssignmentItemDisabled() {
    return !isGridEnabledItem();
  }

}
