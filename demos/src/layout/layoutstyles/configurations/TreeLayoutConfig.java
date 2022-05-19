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
package layout.layoutstyles.configurations;

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.IDataMap;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.IEdgeMap;
import com.yworks.yfiles.algorithms.LayoutGraphHider;
import com.yworks.yfiles.algorithms.Maps;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.AbstractLayoutStage;
import com.yworks.yfiles.layout.ComponentArrangementStyles;
import com.yworks.yfiles.layout.ComponentLayout;
import com.yworks.yfiles.layout.EdgeBundleDescriptor;
import com.yworks.yfiles.layout.EdgeBundling;
import com.yworks.yfiles.layout.GroupingSupport;
import com.yworks.yfiles.layout.IEdgeLabelLayout;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.StraightLineEdgeRouter;
import com.yworks.yfiles.layout.tree.AbstractRotatableNodePlacer;
import com.yworks.yfiles.layout.tree.AspectRatioNodePlacer;
import com.yworks.yfiles.layout.tree.BusNodePlacer;
import com.yworks.yfiles.layout.tree.ChildPlacement;
import com.yworks.yfiles.layout.tree.CompactNodePlacer;
import com.yworks.yfiles.layout.tree.DefaultNodePlacer;
import com.yworks.yfiles.layout.tree.DefaultPortAssignment;
import com.yworks.yfiles.layout.tree.DelegatingNodePlacer;
import com.yworks.yfiles.layout.tree.DendrogramNodePlacer;
import com.yworks.yfiles.layout.tree.DoubleLineNodePlacer;
import com.yworks.yfiles.layout.tree.GridNodePlacer;
import com.yworks.yfiles.layout.tree.INodePlacer;
import com.yworks.yfiles.layout.tree.LayeredNodePlacer;
import com.yworks.yfiles.layout.tree.LayeredRoutingStyle;
import com.yworks.yfiles.layout.tree.LeftRightNodePlacer;
import com.yworks.yfiles.layout.tree.PortAssignmentMode;
import com.yworks.yfiles.layout.tree.RootAlignment;
import com.yworks.yfiles.layout.tree.SimpleNodePlacer;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeLayoutData;
import com.yworks.yfiles.layout.tree.TreeReductionStage;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
@Label("TreeLayout")
public class TreeLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public TreeLayoutConfig() {
    AspectRatioNodePlacer aspectRatioNodePlacer = new AspectRatioNodePlacer();

    setRoutingStyleForNonTreeEdgesItem(EnumRoute.ORTHOGONAL);
    setEdgeBundlingStrengthItem(0.95);
    setActingOnSelectionOnlyItem(false);

    setDefaultLayoutOrientationItem(LayoutOrientation.TOP_TO_BOTTOM);

    setConsideringNodeLabelsItem(false);

    setNodePlacerItem(EnumNodePlacer.DEFAULT);

    setSpacingItem(20);
    setRootAlignmentItem(EnumRootAlignment.CENTER);
    setAllowingMultiParentsItem(false);
    setPortAssignmentItem(PortAssignmentMode.NONE);

    setNodePlacerAspectRatioItem(aspectRatioNodePlacer.getAspectRatio());

    setEdgeLabelingItem(EnumEdgeLabeling.NONE);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    TreeLayout layout = getNodePlacerItem() != EnumNodePlacer.HV ? configureDefaultLayout() : new TreeLayout();

    layout.setParallelEdgeRouterEnabled(false);
    ((ComponentLayout)layout.getComponentLayout()).setStyle(ComponentArrangementStyles.MULTI_ROWS);
    layout.setSubgraphLayoutEnabled(isActingOnSelectionOnlyItem());

    layout.prependStage(createTreeReductionStage());

    boolean placeLabels = getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED || getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC;

    // required to prevent WrongGraphStructure exception which may be thrown by TreeLayout if there are edges
    // between group nodes
    layout.prependStage(new HandleEdgesBetweenGroupsStage(placeLabels));
    if (getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC) {
      layout.setIntegratedEdgeLabelingEnabled(false);

      GenericLabeling labeling = new GenericLabeling();
      labeling.setEdgeLabelPlacementEnabled(true);
      labeling.setNodeLabelPlacementEnabled(false);
      labeling.setAmbiguityReductionEnabled(isReducingAmbiguityItem());
      layout.setLabelingEnabled(true);
      layout.setLabeling(labeling);
    } else if (getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED) {
      layout.setIntegratedEdgeLabelingEnabled(true);
    }

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( final GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    LayoutData layoutData;
    if (getNodePlacerItem() == EnumNodePlacer.HV) {
      layoutData = createLayoutDataHorizontalVertical(graphComponent);
    } else if (getNodePlacerItem() == EnumNodePlacer.DELEGATING_LAYERED) {
      layoutData = createLayoutDataDelegatingPlacer(graphComponent);
    } else {
      final IGraph graph = graphComponent.getGraph();
      TreeLayoutData treeLayoutData = new TreeLayoutData();
      treeLayoutData.setGridNodePlacerRowIndices(node -> {
        Iterator<INode> predecessors = graph.predecessors(INode.class, node).iterator();
        if (predecessors.hasNext()) {
          INode parent = predecessors.next();
          List<INode> siblings = graph.successors(INode.class, parent).toList();
          return siblings.indexOf(node) % (int)Math.round(Math.sqrt(siblings.size()));
        }
        return 0;
      });
      treeLayoutData.setLeftRightNodePlacerLeftNodes(node -> {
        Iterator<INode> predecessors = graph.predecessors(INode.class, node).iterator();
        if (predecessors.hasNext()) {
          INode parent = predecessors.next();
          List<INode> siblings = graph.successors(INode.class, parent).toList();
          return siblings.indexOf(node) % 2 != 0;
        }
        return false;
      });
      treeLayoutData.setCompactNodePlacerStrategyMementos(new Mapper<INode, Object>());
      layoutData = treeLayoutData;
    }

    return layoutData.combineWith(createLabelingLayoutData(graphComponent.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem()));
  }

  private LayoutData createLayoutDataHorizontalVertical( final GraphComponent graphComponent ) {
    TreeLayoutData data = new TreeLayoutData();
    data.setNodePlacers(node -> {
        // children of selected nodes should be placed vertical and to the right of their child nodes, while
        // the children of non-selected horizontal downwards
        ChildPlacement childPlacement = graphComponent.getSelection().isSelected(node) ? ChildPlacement.VERTICAL_TO_RIGHT : ChildPlacement.HORIZONTAL_DOWNWARD;

        return new DefaultNodePlacer(childPlacement, RootAlignment.LEADING_ON_BUS, getSpacingItem(), getSpacingItem());
    });
    return data;
  }

  private TreeLayoutData createLayoutDataDelegatingPlacer( GraphComponent graphComponent ) {
    final IGraph graph = graphComponent.getGraph();
    //half the subtrees are delegated to the left placer and half to the right placer
    final HashSet<INode> leftNodes = new HashSet<INode>();
    final INode root = graph.getNodes().stream().filter(node -> graph.inDegree(node) == 0).findFirst().get();
    boolean left = true;
    for (INode successor : graph.successors(INode.class, root)) {
      ArrayList<INode> stack = new ArrayList<INode>();
      stack.add(successor);
      while (stack.size() > 0) {
        INode child = stack.get(stack.size() - 1);
        stack.remove(stack.size() - 1);
        if (left) {
          leftNodes.add(child);
        } // else: right node
        //push successors on stack -> whole subtree is either left or right
        graph.successors(INode.class, child).forEach(stack::add);
      }
      left = !left;
    }

    TreeLayoutData layoutData = new TreeLayoutData();
    layoutData.setDelegatingNodePlacerPrimaryNodes(leftNodes::contains
    );
    layoutData.setNodePlacers(node -> {
        if (node == root) {
          return delegatingRootPlacer;
        }
        if (leftNodes.contains(node)) {
          return delegatingLeftPlacer;
        }
        return delegatingRightPlacer;
    });
    layoutData.getTreeRoot().setItem(root);
    return layoutData;
  }

  /**
   * Configures the tree reduction stage that will handle edges that do not belong to the tree.
   */
  private TreeReductionStage createTreeReductionStage() {
    TreeReductionStage reductionStage = new TreeReductionStage((ILayoutAlgorithm)null);
    if (getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED) {
      reductionStage.setNonTreeEdgeLabelingAlgorithm(new GenericLabeling());
    }
    reductionStage.setMultiParentAllowed((getNodePlacerItem() == EnumNodePlacer.DEFAULT || getNodePlacerItem() == EnumNodePlacer.BUS || getNodePlacerItem() == EnumNodePlacer.LEFT_RIGHT || getNodePlacerItem() == EnumNodePlacer.DENDROGRAM) && isAllowingMultiParentsItem());

    if (getRoutingStyleForNonTreeEdgesItem() == EnumRoute.ORGANIC) {
      reductionStage.setNonTreeEdgeRouter(new OrganicEdgeRouter());
      reductionStage.setNonTreeEdgeSelectionKey(OrganicEdgeRouter.AFFECTED_EDGES_DPKEY);
    } else if (getRoutingStyleForNonTreeEdgesItem() == EnumRoute.ORTHOGONAL) {
      EdgeRouter edgeRouter = new EdgeRouter();
      edgeRouter.setReroutingEnabled(true);
      edgeRouter.setScope(Scope.ROUTE_AFFECTED_EDGES);
      reductionStage.setNonTreeEdgeRouter(edgeRouter);
      reductionStage.setNonTreeEdgeSelectionKey(edgeRouter.getAffectedEdgesDpKey());
    } else if (getRoutingStyleForNonTreeEdgesItem() == EnumRoute.STRAIGHT_LINE) {
      reductionStage.setNonTreeEdgeRouter(reductionStage.createStraightLineRouter());
    } else if (getRoutingStyleForNonTreeEdgesItem() == EnumRoute.BUNDLED) {
      EdgeBundling ebc = reductionStage.getEdgeBundling();
      ebc.setBundlingStrength(getEdgeBundlingStrengthItem());
      EdgeBundleDescriptor edgeBundleDescriptor = new EdgeBundleDescriptor();
      edgeBundleDescriptor.setBundled(true);
      ebc.setDefaultBundleDescriptor(edgeBundleDescriptor);
    }
    return reductionStage;
  }

  private TreeLayout configureDefaultLayout() {
    TreeLayout layout = new TreeLayout();
    layout.setLayoutOrientation(getNodePlacerItem() == EnumNodePlacer.ASPECT_RATIO ? LayoutOrientation.TOP_TO_BOTTOM : getDefaultLayoutOrientationItem());
    RootAlignment rootAlignment1 = RootAlignment.CENTER;
    AbstractRotatableNodePlacer.RootAlignment rootAlignment2 = AbstractRotatableNodePlacer.RootAlignment.CENTER;
    switch (getRootAlignmentItem()) {
      case CENTER:
        rootAlignment1 = RootAlignment.CENTER;
        rootAlignment2 = AbstractRotatableNodePlacer.RootAlignment.CENTER;
        break;
      case MEDIAN:
        rootAlignment1 = RootAlignment.MEDIAN;
        rootAlignment2 = AbstractRotatableNodePlacer.RootAlignment.MEDIAN;
        break;
      case LEFT:
        rootAlignment1 = RootAlignment.LEADING;
        rootAlignment2 = AbstractRotatableNodePlacer.RootAlignment.LEFT;
        break;
      case LEADING:
        rootAlignment1 = RootAlignment.LEADING_OFFSET;
        rootAlignment2 = AbstractRotatableNodePlacer.RootAlignment.LEADING;
        break;
      case RIGHT:
        rootAlignment1 = RootAlignment.TRAILING;
        rootAlignment2 = AbstractRotatableNodePlacer.RootAlignment.RIGHT;
        break;
      case TRAILING:
        rootAlignment1 = RootAlignment.TRAILING_OFFSET;
        rootAlignment2 = AbstractRotatableNodePlacer.RootAlignment.TRAILING;
        break;
    }

    boolean allowMultiParents = isAllowingMultiParentsItem();

    switch (getNodePlacerItem()) {
      case DEFAULT:
        DefaultNodePlacer defaultNodePlacer = new DefaultNodePlacer();
        defaultNodePlacer.setHorizontalDistance(getSpacingItem());
        defaultNodePlacer.setVerticalDistance(getSpacingItem());
        defaultNodePlacer.setRootAlignment(rootAlignment1);
        layout.setDefaultNodePlacer(defaultNodePlacer);
        layout.setMultiParentAllowed(allowMultiParents);
        break;
      case SIMPLE:
        SimpleNodePlacer simpleNodePlacer = new SimpleNodePlacer();
        simpleNodePlacer.setSpacing(getSpacingItem());
        simpleNodePlacer.setRootAlignment(rootAlignment2);
        layout.setDefaultNodePlacer(simpleNodePlacer);
        break;
      case BUS:
        BusNodePlacer busNodePlacer = new BusNodePlacer();
        busNodePlacer.setSpacing(getSpacingItem());
        layout.setDefaultNodePlacer(busNodePlacer);
        layout.setMultiParentAllowed(allowMultiParents);
        break;
      case DOUBLE_LINE:
        DoubleLineNodePlacer doubleLineNodePlacer = new DoubleLineNodePlacer();
        doubleLineNodePlacer.setSpacing(getSpacingItem());
        doubleLineNodePlacer.setRootAlignment(rootAlignment2);
        layout.setDefaultNodePlacer(doubleLineNodePlacer);
        break;
      case LEFT_RIGHT:
        LeftRightNodePlacer leftRightNodePlacer = new LeftRightNodePlacer();
        leftRightNodePlacer.setSpacing(getSpacingItem());
        layout.setDefaultNodePlacer(leftRightNodePlacer);
        layout.setMultiParentAllowed(allowMultiParents);
        break;
      case LAYERED:
        LayeredNodePlacer layeredNodePlacer = new LayeredNodePlacer();
        layeredNodePlacer.setSpacing(getSpacingItem());
        layeredNodePlacer.setLayerSpacing(getSpacingItem());
        layeredNodePlacer.setRootAlignment(rootAlignment2);
        layout.setDefaultNodePlacer(layeredNodePlacer);
        break;
      case ASPECT_RATIO:
        AspectRatioNodePlacer aspectRatioNodePlacer = new AspectRatioNodePlacer();
        aspectRatioNodePlacer.setHorizontalDistance(getSpacingItem());
        aspectRatioNodePlacer.setVerticalDistance(getSpacingItem());
        aspectRatioNodePlacer.setAspectRatio(getNodePlacerAspectRatioItem());
        layout.setDefaultNodePlacer(aspectRatioNodePlacer);
        break;
      case DENDROGRAM:
        DendrogramNodePlacer dendrogramNodePlacer = new DendrogramNodePlacer();
        dendrogramNodePlacer.setMinimumRootDistance(getSpacingItem());
        dendrogramNodePlacer.setMinimumSubtreeDistance(getSpacingItem());
        layout.setDefaultNodePlacer(dendrogramNodePlacer);
        layout.setMultiParentAllowed(allowMultiParents);
        break;
      case GRID:
        GridNodePlacer gridNodePlacer = new GridNodePlacer();
        gridNodePlacer.setSpacing(getSpacingItem());
        gridNodePlacer.setRootAlignment(rootAlignment2);
        layout.setDefaultNodePlacer(gridNodePlacer);
        break;
      case COMPACT:
        CompactNodePlacer compactNodePlacer = new CompactNodePlacer();
        compactNodePlacer.setHorizontalDistance(getSpacingItem());
        compactNodePlacer.setVerticalDistance(getSpacingItem());
        compactNodePlacer.setPreferredAspectRatio(getNodePlacerAspectRatioItem());
        layout.setDefaultNodePlacer(compactNodePlacer);
        break;
      case DELEGATING_LAYERED:
        LayeredNodePlacer layeredNodePlacer2 = new LayeredNodePlacer(AbstractRotatableNodePlacer.Matrix.ROT270, AbstractRotatableNodePlacer.Matrix.ROT270);
        layeredNodePlacer2.setVerticalAlignment(0);
        layeredNodePlacer2.setRoutingStyle(LayeredRoutingStyle.ORTHOGONAL);
        layeredNodePlacer2.setSpacing(getSpacingItem());
        layeredNodePlacer2.setLayerSpacing(getSpacingItem());
        layeredNodePlacer2.setRootAlignment(rootAlignment2);
        this.delegatingLeftPlacer = layeredNodePlacer2;
        LayeredNodePlacer layeredNodePlacer3 = new LayeredNodePlacer(AbstractRotatableNodePlacer.Matrix.ROT90, AbstractRotatableNodePlacer.Matrix.ROT90);
        layeredNodePlacer3.setVerticalAlignment(0);
        layeredNodePlacer3.setRoutingStyle(LayeredRoutingStyle.ORTHOGONAL);
        layeredNodePlacer3.setLayerSpacing(getSpacingItem());
        layeredNodePlacer3.setRootAlignment(rootAlignment2);

        this.delegatingRightPlacer = layeredNodePlacer3;

        this.delegatingRootPlacer = new DelegatingNodePlacer(AbstractRotatableNodePlacer.Matrix.DEFAULT, this.delegatingLeftPlacer, this.delegatingRightPlacer);
        break;
    }

    layout.setDefaultPortAssignment(new DefaultPortAssignment(getPortAssignmentItem(), 0.5));
    layout.setGroupingSupportEnabled(true);

    return layout;
  }

  private INodePlacer delegatingRootPlacer;

  private INodePlacer delegatingLeftPlacer;

  private INodePlacer delegatingRightPlacer;

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GeneralGroup;

  @Label("Node Placer")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object NodePlacerGroup;

  @Label("Edges")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object EdgesGroup;

  @Label("Non-Tree Edges")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object NonTreeEdgesGroup;

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

  public enum EnumRoute {
    ORTHOGONAL(0),

    ORGANIC(1),

    STRAIGHT_LINE(2),

    BUNDLED(3);

    private final int value;

    private EnumRoute( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumRoute fromOrdinal( int ordinal ) {
      for (EnumRoute current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

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

  public enum EnumNodePlacer {
    DEFAULT(0),

    SIMPLE(1),

    BUS(2),

    DOUBLE_LINE(3),

    LEFT_RIGHT(4),

    LAYERED(5),

    ASPECT_RATIO(6),

    DENDROGRAM(7),

    GRID(8),

    COMPACT(9),

    HV(10),

    DELEGATING_LAYERED(11);

    private final int value;

    private EnumNodePlacer( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumNodePlacer fromOrdinal( int ordinal ) {
      for (EnumNodePlacer current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  public enum EnumRootAlignment {
    CENTER(0),

    MEDIAN(1),

    LEFT(2),

    LEADING(3),

    RIGHT(4),

    TRAILING(5);

    private final int value;

    private EnumRootAlignment( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumRootAlignment fromOrdinal( int ordinal ) {
      for (EnumRootAlignment current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  /**
   * Gets the description text.
   * @return The description text.
   */
  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>The various flavors of the tree layout styles are great for highlighting child-parent relationships in graphs that form one or more trees, " +
           "or trees with only few additional edges.</p>" +
           "<p>The need to visualize directed or undirected trees arises in many application areas, for example</p>" +
           "<ul>" +
           "<li><p>Dataflow analysis</p></li>" +
           "<li><p>Software engineering</p></li>" +
           "<li><p>Network management</p></li>" +
           "<li><p>Bioinformatics</p></li>" +
           "</ul>";
  }
  private EnumRoute routingStyleForNonTreeEdgesItem = EnumRoute.ORTHOGONAL;

  @Label("Routing Style for Non-Tree Edges")
  @OptionGroupAnnotation(name = "NonTreeEdgesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumRoute.class, stringValue = "BUNDLED")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHT_LINE")
  @EnumValueAnnotation(label = "Bundled", value = "BUNDLED")
  public final EnumRoute getRoutingStyleForNonTreeEdgesItem() {
    return this.routingStyleForNonTreeEdgesItem;
  }

  @Label("Routing Style for Non-Tree Edges")
  @OptionGroupAnnotation(name = "NonTreeEdgesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumRoute.class, stringValue = "BUNDLED")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHT_LINE")
  @EnumValueAnnotation(label = "Bundled", value = "BUNDLED")
  public final void setRoutingStyleForNonTreeEdgesItem( EnumRoute value ) {
    this.routingStyleForNonTreeEdgesItem = value;
  }

  private double edgeBundlingStrengthItem;

  @Label("Bundling Strength")
  @OptionGroupAnnotation(name = "NonTreeEdgesGroup", position = 20)
  @DefaultValue(doubleValue = 0.95d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getEdgeBundlingStrengthItem() {
    return this.edgeBundlingStrengthItem;
  }

  @Label("Bundling Strength")
  @OptionGroupAnnotation(name = "NonTreeEdgesGroup", position = 20)
  @DefaultValue(doubleValue = 0.95d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setEdgeBundlingStrengthItem( double value ) {
    this.edgeBundlingStrengthItem = value;
  }

  public final boolean isEdgeBundlingStrengthItemDisabled() {
    return getRoutingStyleForNonTreeEdgesItem() != EnumRoute.BUNDLED;
  }

  private boolean actingOnSelectionOnlyItem;

  @Label("Act on Selection Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isActingOnSelectionOnlyItem() {
    return this.actingOnSelectionOnlyItem;
  }

  @Label("Act on Selection Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setActingOnSelectionOnlyItem( boolean value ) {
    this.actingOnSelectionOnlyItem = value;
  }

  private boolean consideringNodeLabelsItem;

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsideringNodeLabelsItem() {
    return this.consideringNodeLabelsItem;
  }

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsideringNodeLabelsItem( boolean value ) {
    this.consideringNodeLabelsItem = value;
  }

  private EnumNodePlacer nodePlacerItem = EnumNodePlacer.DEFAULT;

  @Label("Node Placer")
  @OptionGroupAnnotation(name = "NodePlacerGroup", position = 10)
  @EnumValueAnnotation(label = "Default", value = "DEFAULT")
  @EnumValueAnnotation(label = "Simple", value = "SIMPLE")
  @EnumValueAnnotation(label = "Bus", value = "BUS")
  @EnumValueAnnotation(label = "Double-Line", value = "DOUBLE_LINE")
  @EnumValueAnnotation(label = "Left-Right", value = "LEFT_RIGHT")
  @EnumValueAnnotation(label = "Layered", value = "LAYERED")
  @EnumValueAnnotation(label = "Aspect Ratio", value = "ASPECT_RATIO")
  @EnumValueAnnotation(label = "Dendrogram", value = "DENDROGRAM")
  @EnumValueAnnotation(label = "Grid", value = "GRID")
  @EnumValueAnnotation(label = "Compact", value = "COMPACT")
  @EnumValueAnnotation(label = "Horizontal-Vertical", value = "HV")
  @EnumValueAnnotation(label = "Delegating & Layered", value = "DELEGATING_LAYERED")
  public final EnumNodePlacer getNodePlacerItem() {
    return this.nodePlacerItem;
  }

  @Label("Node Placer")
  @OptionGroupAnnotation(name = "NodePlacerGroup", position = 10)
  @EnumValueAnnotation(label = "Default", value = "DEFAULT")
  @EnumValueAnnotation(label = "Simple", value = "SIMPLE")
  @EnumValueAnnotation(label = "Bus", value = "BUS")
  @EnumValueAnnotation(label = "Double-Line", value = "DOUBLE_LINE")
  @EnumValueAnnotation(label = "Left-Right", value = "LEFT_RIGHT")
  @EnumValueAnnotation(label = "Layered", value = "LAYERED")
  @EnumValueAnnotation(label = "Aspect Ratio", value = "ASPECT_RATIO")
  @EnumValueAnnotation(label = "Dendrogram", value = "DENDROGRAM")
  @EnumValueAnnotation(label = "Grid", value = "GRID")
  @EnumValueAnnotation(label = "Compact", value = "COMPACT")
  @EnumValueAnnotation(label = "Horizontal-Vertical", value = "HV")
  @EnumValueAnnotation(label = "Delegating & Layered", value = "DELEGATING_LAYERED")
  public final void setNodePlacerItem( EnumNodePlacer value ) {
    this.nodePlacerItem = value;
  }

  private double spacingItem;

  @Label("Spacing")
  @OptionGroupAnnotation(name = "NodePlacerGroup", position = 20)
  @MinMax(min = 0, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getSpacingItem() {
    return this.spacingItem;
  }

  @Label("Spacing")
  @OptionGroupAnnotation(name = "NodePlacerGroup", position = 20)
  @MinMax(min = 0, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setSpacingItem( double value ) {
    this.spacingItem = value;
  }

  private EnumRootAlignment rootAlignmentItem = EnumRootAlignment.CENTER;

  @Label("Root Alignment")
  @OptionGroupAnnotation(name = "NodePlacerGroup", position = 30)
  @EnumValueAnnotation(label = "Center", value = "CENTER")
  @EnumValueAnnotation(label = "Median", value = "MEDIAN")
  @EnumValueAnnotation(label = "Left", value = "LEFT")
  @EnumValueAnnotation(label = "Leading", value = "LEADING")
  @EnumValueAnnotation(label = "Right", value = "RIGHT")
  @EnumValueAnnotation(label = "Trailing", value = "TRAILING")
  public final EnumRootAlignment getRootAlignmentItem() {
    return this.rootAlignmentItem;
  }

  @Label("Root Alignment")
  @OptionGroupAnnotation(name = "NodePlacerGroup", position = 30)
  @EnumValueAnnotation(label = "Center", value = "CENTER")
  @EnumValueAnnotation(label = "Median", value = "MEDIAN")
  @EnumValueAnnotation(label = "Left", value = "LEFT")
  @EnumValueAnnotation(label = "Leading", value = "LEADING")
  @EnumValueAnnotation(label = "Right", value = "RIGHT")
  @EnumValueAnnotation(label = "Trailing", value = "TRAILING")
  public final void setRootAlignmentItem( EnumRootAlignment value ) {
    this.rootAlignmentItem = value;
  }

  public final boolean isRootAlignmentItemDisabled() {
    return getNodePlacerItem() == EnumNodePlacer.ASPECT_RATIO || getNodePlacerItem() == EnumNodePlacer.BUS || getNodePlacerItem() == EnumNodePlacer.DENDROGRAM || getNodePlacerItem() == EnumNodePlacer.COMPACT;
  }

  private LayoutOrientation defaultLayoutOrientationItem = LayoutOrientation.TOP_TO_BOTTOM;

  @Label("Orientation")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 5)
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public final LayoutOrientation getDefaultLayoutOrientationItem() {
    return this.defaultLayoutOrientationItem;
  }

  @Label("Orientation")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 5)
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public final void setDefaultLayoutOrientationItem( LayoutOrientation value ) {
    this.defaultLayoutOrientationItem = value;
  }

  public final boolean isDefaultLayoutOrientationItemDisabled() {
    return getNodePlacerItem() == EnumNodePlacer.ASPECT_RATIO || getNodePlacerItem() == EnumNodePlacer.COMPACT;
  }

  private double nodePlacerAspectRatioItem;

  @Label("Aspect Ratio")
  @OptionGroupAnnotation(name = "NodePlacerGroup", position = 50)
  @MinMax(min = 0.1, max = 4, step = 0.01)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getNodePlacerAspectRatioItem() {
    return this.nodePlacerAspectRatioItem;
  }

  @Label("Aspect Ratio")
  @OptionGroupAnnotation(name = "NodePlacerGroup", position = 50)
  @MinMax(min = 0.1, max = 4, step = 0.01)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setNodePlacerAspectRatioItem( double value ) {
    this.nodePlacerAspectRatioItem = value;
  }

  public final boolean isNodePlacerAspectRatioItemDisabled() {
    return getNodePlacerItem() != EnumNodePlacer.ASPECT_RATIO && getNodePlacerItem() != EnumNodePlacer.COMPACT;
  }

  private boolean allowingMultiParentsItem;

  @Label("Allow Multi-Parents")
  @OptionGroupAnnotation(name = "NodePlacerGroup", position = 60)
  public final boolean isAllowingMultiParentsItem() {
    return this.allowingMultiParentsItem;
  }

  @Label("Allow Multi-Parents")
  @OptionGroupAnnotation(name = "NodePlacerGroup", position = 60)
  public final void setAllowingMultiParentsItem( boolean value ) {
    this.allowingMultiParentsItem = value;
  }

  public final boolean isAllowingMultiParentsItemDisabled() {
    return getNodePlacerItem() != EnumNodePlacer.DEFAULT && getNodePlacerItem() != EnumNodePlacer.DENDROGRAM && getNodePlacerItem() != EnumNodePlacer.BUS && getNodePlacerItem() != EnumNodePlacer.LEFT_RIGHT;
  }

  private PortAssignmentMode portAssignmentItem = PortAssignmentMode.NONE;

  @Label("Port Assignment")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 10)
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Distributed North", value = "DISTRIBUTED_NORTH")
  @EnumValueAnnotation(label = "Distributed South", value = "DISTRIBUTED_SOUTH")
  @EnumValueAnnotation(label = "Distributed East", value = "DISTRIBUTED_EAST")
  @EnumValueAnnotation(label = "Distributed West", value = "DISTRIBUTED_WEST")
  public final PortAssignmentMode getPortAssignmentItem() {
    return this.portAssignmentItem;
  }

  @Label("Port Assignment")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 10)
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Distributed North", value = "DISTRIBUTED_NORTH")
  @EnumValueAnnotation(label = "Distributed South", value = "DISTRIBUTED_SOUTH")
  @EnumValueAnnotation(label = "Distributed East", value = "DISTRIBUTED_EAST")
  @EnumValueAnnotation(label = "Distributed West", value = "DISTRIBUTED_WEST")
  public final void setPortAssignmentItem( PortAssignmentMode value ) {
    this.portAssignmentItem = value;
  }

  private EnumEdgeLabeling edgeLabelingItem = EnumEdgeLabeling.NONE;

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumEdgeLabeling.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Integrated", value = "INTEGRATED")
  @EnumValueAnnotation(label = "Generic", value = "GENERIC")
  public final EnumEdgeLabeling getEdgeLabelingItem() {
    return edgeLabelingItem;
  }

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumEdgeLabeling.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Integrated", value = "INTEGRATED")
  @EnumValueAnnotation(label = "Generic", value = "GENERIC")
  public final void setEdgeLabelingItem( EnumEdgeLabeling value ) {
    edgeLabelingItem = value;
    if (value == EnumEdgeLabeling.INTEGRATED) {
      setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.PARALLEL);
      setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.AT_TARGET);
      setLabelPlacementDistanceItem(0);
    }
  }

  private boolean reducingAmbiguityItem;

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 20)
  public final boolean isReducingAmbiguityItem() {
    return this.reducingAmbiguityItem;
  }

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 20)
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
  @EnumValueAnnotation(label = "At Source", value = "AT_SOURCE")
  @EnumValueAnnotation(label = "At Source Port", value = "AT_SOURCE_PORT")
  @EnumValueAnnotation(label = "At Target", value = "AT_TARGET")
  @EnumValueAnnotation(label = "At Target Port", value = "AT_TARGET_PORT")
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

  /**
   * This stage temporarily removes edges that are incident to group nodes.
   * <p>
   * The stage must be prepended to the layout algorithm and applies the following three steps:
   * </p>
   * <ol>
   * <li>Removes edges from the graph that are incident to group nodes.</li>
   * <li>Invokes the core layout algorithm on the reduced graph.</li>
   * <li>Re-inserts all previously removed edges and optionally places their labels.</li>
   * </ol>
   * <p>
   * This stage can be useful for layout algorithms or stages that cannot handle edges between group nodes, e.g.,
   * {@link TreeReductionStage}. Optionally, {@link HandleEdgesBetweenGroupsStage} can also place the labels of the edges
   * that were temporarily removed right after they are restored back to the graph.
   * </p>
   * <p>
   * The routing of the temporarily hidden edges can be customized by specifying an {@link HandleEdgesBetweenGroupsStage#getMarkedEdgeRouter() edge routing algorithm}
   * for those edges.
   * </p>
   */
  private static class HandleEdgesBetweenGroupsStage extends AbstractLayoutStage {
    public HandleEdgesBetweenGroupsStage( boolean placeLabels ) {
      super((ILayoutAlgorithm)null);
      setConsiderEdgeLabels(placeLabels);
    }

    private Object edgeSelectionKey;

    /**
     * Gets the key to register a data provider that will be used by the edge routing algorithm to determine the edges that
     * need to be routed.
     * @return The EdgeSelectionKey.
     * @see #setEdgeSelectionKey(Object)
     */
    public final Object getEdgeSelectionKey() {
      return this.edgeSelectionKey;
    }

    /**
     * Sets the key to register a data provider that will be used by the edge routing algorithm to determine the edges that
     * need to be routed.
     * @param value The EdgeSelectionKey to set.
     * @see #getEdgeSelectionKey()
     */
    public final void setEdgeSelectionKey( Object value ) {
      this.edgeSelectionKey = value;
    }

    private ILayoutAlgorithm markedEdgeRouter;

    /**
     * Gets the edge routing algorithm that is applied to the set of marked edges.
     * <p>
     * Note that is required that a suitable edge selection key is specified and the router's scope is reduced to the affected
     * edges.
     * </p>
     * @return The MarkedEdgeRouter.
     * @see #setMarkedEdgeRouter(ILayoutAlgorithm)
     */
    public final ILayoutAlgorithm getMarkedEdgeRouter() {
      return this.markedEdgeRouter;
    }

    /**
     * Sets the edge routing algorithm that is applied to the set of marked edges.
     * <p>
     * Note that is required that a suitable edge selection key is specified and the router's scope is reduced to the affected
     * edges.
     * </p>
     * @param value The MarkedEdgeRouter to set.
     * @see #getMarkedEdgeRouter()
     */
    public final void setMarkedEdgeRouter( ILayoutAlgorithm value ) {
      this.markedEdgeRouter = value;
    }

    private boolean considerEdgeLabels;

    /**
     * Gets a value indicating whether the stage should place the labels of the edges that have been temporarily hidden, when
     * these edges will be restored back.
     * @return The ConsiderEdgeLabels.
     * @see #setConsiderEdgeLabels(boolean)
     */
    public final boolean isConsiderEdgeLabels() {
      return this.considerEdgeLabels;
    }

    /**
     * Sets a value indicating whether the stage should place the labels of the edges that have been temporarily hidden, when
     * these edges will be restored back.
     * @param value The ConsiderEdgeLabels to set.
     * @see #isConsiderEdgeLabels()
     */
    public final void setConsiderEdgeLabels( boolean value ) {
      this.considerEdgeLabels = value;
    }

    /**
     * Removes all edges that are incident to group nodes and passes it to the core layout algorithm.
     * <p>
     * This stage removes some edges from the graph such that no edges incident to group nodes exist. Then, it applies the core
     * layout algorithm to the reduced graph. After it produces the result, it re-inserts the previously removed edges and
     * routes them.
     * </p>
     */
    @Override
    public void applyLayout( LayoutGraph graph ) {
      GroupingSupport groupingSupport = new GroupingSupport(graph);

      if (!GroupingSupport.isGrouped(graph)) {
        applyLayoutCore(graph);
      } else {
        IEdgeMap hiddenEdgesMap = Maps.createHashedEdgeMap();

        LayoutGraphHider edgeHider = new LayoutGraphHider(graph);

        boolean existHiddenEdges = false;
        for (Edge edge : graph.getEdges()) {
          if (groupingSupport.isGroupNode(edge.source()) || groupingSupport.isGroupNode(edge.target())) {
            hiddenEdgesMap.set(edge, true);
            edgeHider.hide(edge);
            existHiddenEdges = true;
          } else {
            hiddenEdgesMap.set(edge, false);
          }
        }

        applyLayoutCore(graph);

        if (existHiddenEdges) {
          edgeHider.unhideAll();

          // routes the marked edges
          routeMarkedEdges(graph, hiddenEdgesMap);

          if (isConsiderEdgeLabels()) {
            // all labels of hidden edges should be marked
            String affectedLabelsDpKey = "affectedLabelsDpKey";
            IDataMap nonTreeLabelsMap = Maps.createHashedDataMap();

            for (Edge edge : graph.getEdges()) {
              IEdgeLabelLayout[] ell = graph.getLabelLayout(edge);
              for (IEdgeLabelLayout labelLayout : ell) {
                nonTreeLabelsMap.set(labelLayout, hiddenEdgesMap.get(edge));
              }
            }

            // add selection marker
            graph.addDataProvider(affectedLabelsDpKey, nonTreeLabelsMap);

            // place marked labels
            GenericLabeling labeling = new GenericLabeling();
            labeling.setNodeLabelPlacementEnabled(false);
            labeling.setEdgeLabelPlacementEnabled(true);
            labeling.setAffectedLabelsDpKey(affectedLabelsDpKey);
            labeling.applyLayout(graph);

            // dispose selection key
            graph.removeDataProvider(affectedLabelsDpKey);
          }
        }
      }
    }

    private void routeMarkedEdges( LayoutGraph graph, IDataMap markedEdgesMap ) {
      if (getMarkedEdgeRouter() == null) {
        return;
      }

      IDataProvider backupDp = null;
      if (getEdgeSelectionKey() != null) {
        backupDp = graph.getDataProvider(getEdgeSelectionKey());
        graph.addDataProvider(getEdgeSelectionKey(), markedEdgesMap);
      }
      if (getMarkedEdgeRouter() instanceof StraightLineEdgeRouter) {
        StraightLineEdgeRouter router = (StraightLineEdgeRouter)getMarkedEdgeRouter();
        router.setScope(Scope.ROUTE_AFFECTED_EDGES);
        router.setAffectedEdgesDpKey(getEdgeSelectionKey());
      }

      getMarkedEdgeRouter().applyLayout(graph);

      if (getEdgeSelectionKey() != null) {
        graph.removeDataProvider(getEdgeSelectionKey());

        if (backupDp != null) {
          graph.addDataProvider(getEdgeSelectionKey(), backupDp);
        }
      }
    }

  }

}
