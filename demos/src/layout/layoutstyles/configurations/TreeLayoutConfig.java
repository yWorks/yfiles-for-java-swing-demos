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
import com.yworks.yfiles.layout.MultiStageLayout;
import com.yworks.yfiles.layout.OrientationLayout;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.StraightLineEdgeRouter;
import com.yworks.yfiles.layout.tree.AbstractRotatableNodePlacer;
import com.yworks.yfiles.layout.tree.AspectRatioNodePlacer;
import com.yworks.yfiles.layout.tree.BusNodePlacer;
import com.yworks.yfiles.layout.tree.ChildPlacement;
import com.yworks.yfiles.layout.tree.ClassicTreeLayout;
import com.yworks.yfiles.layout.tree.CompactNodePlacer;
import com.yworks.yfiles.layout.tree.DefaultNodePlacer;
import com.yworks.yfiles.layout.tree.DefaultPortAssignment;
import com.yworks.yfiles.layout.tree.DendrogramNodePlacer;
import com.yworks.yfiles.layout.tree.DoubleLineNodePlacer;
import com.yworks.yfiles.layout.tree.EdgeRoutingStyle;
import com.yworks.yfiles.layout.tree.GridNodePlacer;
import com.yworks.yfiles.layout.tree.LayeredNodePlacer;
import com.yworks.yfiles.layout.tree.LeafPlacement;
import com.yworks.yfiles.layout.tree.LeftRightNodePlacer;
import com.yworks.yfiles.layout.tree.PortAssignmentMode;
import com.yworks.yfiles.layout.tree.PortStyle;
import com.yworks.yfiles.layout.tree.RootAlignment;
import com.yworks.yfiles.layout.tree.SimpleNodePlacer;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeLayoutData;
import com.yworks.yfiles.layout.tree.TreeReductionStage;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;
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
    ClassicTreeLayout layout = new ClassicTreeLayout();
    AspectRatioNodePlacer aspectRatioNodePlacer = new AspectRatioNodePlacer();
    DefaultNodePlacer defaultNodePlacer = new DefaultNodePlacer();

    setLayoutStyleItem(EnumStyle.DEFAULT);
    setRoutingStyleForNonTreeEdgesItem(EnumRoute.ORTHOGONAL);
    setEdgeBundlingStrengthItem(0.95);
    setActingOnSelectionOnlyItem(false);

    setDefaultLayoutOrientationItem(LayoutOrientation.TOP_TO_BOTTOM);
    setClassicLayoutOrientationItem(LayoutOrientation.TOP_TO_BOTTOM);

    setMinimumNodeDistanceItem((int)layout.getMinimumNodeDistance());
    setMinimumLayerDistanceItem((int)layout.getMinimumLayerDistance());
    setPortStyleItem(PortStyle.NODE_CENTER);

    setConsideringNodeLabelsItem(false);

    setOrthogonalEdgeRoutingItem(false);

    setVerticalAlignmentItem(0.5);
    setChildPlacementPolicyItem(LeafPlacement.SIBLINGS_ON_SAME_LAYER);
    setEnforcingGlobalLayeringItem(false);

    setNodePlacerItem(EnumNodePlacer.DEFAULT);

    setSpacingItem(20);
    setRootAlignmentItem(EnumRootAlignment.CENTER);
    setAllowingMultiParentsItem(false);
    setPortAssignmentItem(PortAssignmentMode.NONE);

    setHvHorizontalSpaceItem((int)defaultNodePlacer.getHorizontalDistance());
    setHvVerticalSpaceItem((int)defaultNodePlacer.getVerticalDistance());

    setBusAlignmentItem(0.5);

    setArHorizontalSpaceItem((int)aspectRatioNodePlacer.getHorizontalDistance());
    setArVerticalSpaceItem((int)aspectRatioNodePlacer.getVerticalDistance());
    setNodePlacerAspectRatioItem(aspectRatioNodePlacer.getAspectRatio());

    setArUsingViewAspectRatioItem(true);
    setCompactPreferredAspectRatioItem(aspectRatioNodePlacer.getAspectRatio());

    setEdgeLabelingItem(EnumEdgeLabeling.NONE);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    MultiStageLayout layout;

    switch (getLayoutStyleItem()) {
      default:
      case DEFAULT:
        layout = configureDefaultLayout();
        break;
      case CLASSIC:
        layout = configureClassicLayout();
        break;
      case HORIZONTAL_VERTICAL:
        layout = new TreeLayout();
        break;
      case COMPACT:
        layout = configureCompactLayout(graphComponent);
        break;
    }

    layout.setParallelEdgeRouterEnabled(false);
    ((ComponentLayout)layout.getComponentLayout()).setStyle(ComponentArrangementStyles.MULTI_ROWS);
    layout.setSubgraphLayoutEnabled(isActingOnSelectionOnlyItem());

    layout.prependStage(createTreeReductionStage());

    boolean placeLabels = getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED || getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC;

    // required to prevent WrongGraphStructure exception which may be thrown by TreeLayout if there are edges
    // between group nodes
    layout.prependStage(new HandleEdgesBetweenGroupsStage(placeLabels));

    if (getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC) {
      layout.setLabelingEnabled(true);
      GenericLabeling genericLabeling = new GenericLabeling();
      genericLabeling.setEdgeLabelPlacementEnabled(true);
      genericLabeling.setNodeLabelPlacementEnabled(false);
      genericLabeling.setAmbiguityReductionEnabled(isReducingAmbiguityItem());
      layout.setLabeling(genericLabeling);
    }

    addPreferredPlacementDescriptor(graphComponent.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem());

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( final GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    if (getLayoutStyleItem() == EnumStyle.DEFAULT) {
      final IGraph graph = graphComponent.getGraph();
      TreeLayoutData treeLayoutData = new TreeLayoutData();
      treeLayoutData.setCompactNodePlacerStrategyMementos(new Mapper<INode, Object>());
      treeLayoutData.setLeftRightNodePlacerLeftNodes(node -> {
        Iterator<INode> predecessors = graph.predecessors(INode.class, node).iterator();
        if (predecessors.hasNext()) {
          INode parent = predecessors.next();
          List<INode> siblings = graph.successors(INode.class, parent).toList();
          return siblings.indexOf(node) % 2 != 0;
        }
        return false;
      });
      treeLayoutData.setGridNodePlacerRowIndices(node -> {
        Iterator<INode> predecessors = graph.predecessors(INode.class, node).iterator();
        if (predecessors.hasNext()) {
          INode parent = predecessors.next();
          List<INode> siblings = graph.successors(INode.class, parent).toList();
          return siblings.indexOf(node) % (int)Math.round(Math.sqrt(siblings.size()));
        }
        return 0;
      });
      return treeLayoutData;
    } else if (getLayoutStyleItem() == EnumStyle.HORIZONTAL_VERTICAL) {
      TreeLayoutData data = new TreeLayoutData();
      data.setNodePlacers(node -> {
        // children of selected nodes should be placed vertical and to the right of their child nodes, while
        // the children of non-selected horizontal downwards
        ChildPlacement childPlacement = graphComponent.getSelection().isSelected(node) ? ChildPlacement.VERTICAL_TO_RIGHT : ChildPlacement.HORIZONTAL_DOWNWARD;

        return new DefaultNodePlacer(childPlacement, RootAlignment.LEADING_ON_BUS, getHvVerticalSpaceItem(), getHvHorizontalSpaceItem());
      });
      return data;
    }
    return null;
  }

  /**
   * Configures the tree reduction stage that will handle edges that do not belong to the tree.
   */
  private TreeReductionStage createTreeReductionStage() {
    TreeReductionStage reductionStage = new TreeReductionStage((ILayoutAlgorithm)null);
    if (getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED) {
      reductionStage.setNonTreeEdgeLabelingAlgorithm(new GenericLabeling());
    }
    reductionStage.setMultiParentAllowed((getLayoutStyleItem() == EnumStyle.CLASSIC && !isEnforcingGlobalLayeringItem() && getChildPlacementPolicyItem() != LeafPlacement.ALL_LEAVES_ON_SAME_LAYER) || (getLayoutStyleItem() == EnumStyle.DEFAULT && (getNodePlacerItem() == EnumNodePlacer.DEFAULT || getNodePlacerItem() == EnumNodePlacer.BUS || getNodePlacerItem() == EnumNodePlacer.LEFT_RIGHT || getNodePlacerItem() == EnumNodePlacer.DENDROGRAM) && isAllowingMultiParentsItem()));

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

  private MultiStageLayout configureDefaultLayout() {
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
    }

    layout.setDefaultPortAssignment(new DefaultPortAssignment(getPortAssignmentItem(), 0.5));
    layout.setGroupingSupportEnabled(true);

    return layout;
  }

  private MultiStageLayout configureClassicLayout() {
    ClassicTreeLayout layout = new ClassicTreeLayout();
    layout.setMinimumNodeDistance(getMinimumNodeDistanceItem());
    layout.setMinimumLayerDistance(getMinimumLayerDistanceItem());

    ((OrientationLayout)layout.getOrientationLayout()).setOrientation(getClassicLayoutOrientationItem());

    if (isOrthogonalEdgeRoutingItem()) {
      layout.setEdgeRoutingStyle(EdgeRoutingStyle.ORTHOGONAL);
    } else {
      layout.setEdgeRoutingStyle(EdgeRoutingStyle.PLAIN);
    }

    layout.setLeafPlacement(getChildPlacementPolicyItem());
    layout.setGlobalLayeringEnforced(isEnforcingGlobalLayeringItem());
    layout.setPortStyle(getPortStyleItem());

    layout.setVerticalAlignment(getVerticalAlignmentItem());
    layout.setBusAlignment(getBusAlignmentItem());

    return layout;
  }

  private MultiStageLayout configureCompactLayout( GraphComponent graphComponent ) {
    TreeLayout layout = new TreeLayout();
    AspectRatioNodePlacer aspectRatioNodePlacer = new AspectRatioNodePlacer();

    if (graphComponent != null && isArUsingViewAspectRatioItem()) {
      aspectRatioNodePlacer.setAspectRatio(((double) graphComponent.getWidth()) / graphComponent.getHeight());
    } else {
      aspectRatioNodePlacer.setAspectRatio(getCompactPreferredAspectRatioItem());
    }

    aspectRatioNodePlacer.setHorizontalDistance(getArHorizontalSpaceItem());
    aspectRatioNodePlacer.setVerticalDistance(getArVerticalSpaceItem());

    layout.setDefaultNodePlacer(aspectRatioNodePlacer);
    return layout;
  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GeneralGroup;

  @Label("Default")
  @OptionGroupAnnotation(name = "RootGroup", position = 15)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DefaultGroup;

  @Label("Horizontal-Vertical")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object HVGroup;

  @Label("Compact")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object CompactGroup;

  @Label("Classic")
  @OptionGroupAnnotation(name = "RootGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object ClassicGroup;

  @Label("Labeling")
  @OptionGroupAnnotation(name = "RootGroup", position = 50)
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

  public enum EnumStyle {
    DEFAULT(0),

    HORIZONTAL_VERTICAL(1),

    COMPACT(2),

    CLASSIC(3);

    private final int value;

    private EnumStyle( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumStyle fromOrdinal( int ordinal ) {
      for (EnumStyle current : values()) {
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

    COMPACT(9);

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

  private EnumStyle layoutStyleItem = EnumStyle.DEFAULT;

  @Label("Layout Style")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumStyle.class, stringValue = "DEFAULT")
  @EnumValueAnnotation(label = "Default", value = "DEFAULT")
  @EnumValueAnnotation(label = "Horizontal-Vertical", value = "HORIZONTAL_VERTICAL")
  @EnumValueAnnotation(label = "Compact", value = "COMPACT")
  @EnumValueAnnotation(label = "Classic", value = "CLASSIC")
  public final EnumStyle getLayoutStyleItem() {
    return this.layoutStyleItem;
  }

  @Label("Layout Style")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumStyle.class, stringValue = "DEFAULT")
  @EnumValueAnnotation(label = "Default", value = "DEFAULT")
  @EnumValueAnnotation(label = "Horizontal-Vertical", value = "HORIZONTAL_VERTICAL")
  @EnumValueAnnotation(label = "Compact", value = "COMPACT")
  @EnumValueAnnotation(label = "Classic", value = "CLASSIC")
  public final void setLayoutStyleItem( EnumStyle value ) {
    this.layoutStyleItem = value;
  }

  private EnumRoute routingStyleForNonTreeEdgesItem = EnumRoute.ORTHOGONAL;

  @Label("Routing Style for Non-Tree Edges")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumRoute.class, stringValue = "BUNDLED")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHT_LINE")
  @EnumValueAnnotation(label = "Bundled", value = "BUNDLED")
  public final EnumRoute getRoutingStyleForNonTreeEdgesItem() {
    return this.routingStyleForNonTreeEdgesItem;
  }

  @Label("Routing Style for Non-Tree Edges")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
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
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(doubleValue = 0.95d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getEdgeBundlingStrengthItem() {
    return this.edgeBundlingStrengthItem;
  }

  @Label("Bundling Strength")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
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
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isActingOnSelectionOnlyItem() {
    return this.actingOnSelectionOnlyItem;
  }

  @Label("Act on Selection Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setActingOnSelectionOnlyItem( boolean value ) {
    this.actingOnSelectionOnlyItem = value;
  }

  private boolean consideringNodeLabelsItem;

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsideringNodeLabelsItem() {
    return this.consideringNodeLabelsItem;
  }

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsideringNodeLabelsItem( boolean value ) {
    this.consideringNodeLabelsItem = value;
  }

  private EnumNodePlacer nodePlacerItem = EnumNodePlacer.DEFAULT;

  @Label("Node Placer")
  @OptionGroupAnnotation(name = "DefaultGroup", position = 10)
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
  public final EnumNodePlacer getNodePlacerItem() {
    return this.nodePlacerItem;
  }

  @Label("Node Placer")
  @OptionGroupAnnotation(name = "DefaultGroup", position = 10)
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
  public final void setNodePlacerItem( EnumNodePlacer value ) {
    this.nodePlacerItem = value;
  }

  private double spacingItem;

  @Label("Spacing")
  @OptionGroupAnnotation(name = "DefaultGroup", position = 20)
  @MinMax(min = 0, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getSpacingItem() {
    return this.spacingItem;
  }

  @Label("Spacing")
  @OptionGroupAnnotation(name = "DefaultGroup", position = 20)
  @MinMax(min = 0, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setSpacingItem( double value ) {
    this.spacingItem = value;
  }

  private EnumRootAlignment rootAlignmentItem = EnumRootAlignment.CENTER;

  @Label("Root Alignment")
  @OptionGroupAnnotation(name = "DefaultGroup", position = 30)
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
  @OptionGroupAnnotation(name = "DefaultGroup", position = 30)
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
  @OptionGroupAnnotation(name = "DefaultGroup", position = 40)
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public final LayoutOrientation getDefaultLayoutOrientationItem() {
    return this.defaultLayoutOrientationItem;
  }

  @Label("Orientation")
  @OptionGroupAnnotation(name = "DefaultGroup", position = 40)
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
  @OptionGroupAnnotation(name = "DefaultGroup", position = 50)
  @MinMax(min = 0.1, max = 4, step = 0.01)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getNodePlacerAspectRatioItem() {
    return this.nodePlacerAspectRatioItem;
  }

  @Label("Aspect Ratio")
  @OptionGroupAnnotation(name = "DefaultGroup", position = 50)
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
  @OptionGroupAnnotation(name = "DefaultGroup", position = 60)
  public final boolean isAllowingMultiParentsItem() {
    return this.allowingMultiParentsItem;
  }

  @Label("Allow Multi-Parents")
  @OptionGroupAnnotation(name = "DefaultGroup", position = 60)
  public final void setAllowingMultiParentsItem( boolean value ) {
    this.allowingMultiParentsItem = value;
  }

  public final boolean isAllowingMultiParentsItemDisabled() {
    return getNodePlacerItem() != EnumNodePlacer.DEFAULT && getNodePlacerItem() != EnumNodePlacer.DENDROGRAM && getNodePlacerItem() != EnumNodePlacer.BUS && getNodePlacerItem() != EnumNodePlacer.LEFT_RIGHT;
  }

  private PortAssignmentMode portAssignmentItem = PortAssignmentMode.NONE;

  @Label("Port Assignment")
  @OptionGroupAnnotation(name = "DefaultGroup", position = 70)
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Distributed North", value = "DISTRIBUTED_NORTH")
  @EnumValueAnnotation(label = "Distributed South", value = "DISTRIBUTED_SOUTH")
  @EnumValueAnnotation(label = "Distributed East", value = "DISTRIBUTED_EAST")
  @EnumValueAnnotation(label = "Distributed West", value = "DISTRIBUTED_WEST")
  public final PortAssignmentMode getPortAssignmentItem() {
    return this.portAssignmentItem;
  }

  @Label("Port Assignment")
  @OptionGroupAnnotation(name = "DefaultGroup", position = 70)
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Distributed North", value = "DISTRIBUTED_NORTH")
  @EnumValueAnnotation(label = "Distributed South", value = "DISTRIBUTED_SOUTH")
  @EnumValueAnnotation(label = "Distributed East", value = "DISTRIBUTED_EAST")
  @EnumValueAnnotation(label = "Distributed West", value = "DISTRIBUTED_WEST")
  public final void setPortAssignmentItem( PortAssignmentMode value ) {
    this.portAssignmentItem = value;
  }

  private int hvHorizontalSpaceItem;

  @Label("Horizontal Spacing")
  @OptionGroupAnnotation(name = "HVGroup", position = 10)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getHvHorizontalSpaceItem() {
    return this.hvHorizontalSpaceItem;
  }

  @Label("Horizontal Spacing")
  @OptionGroupAnnotation(name = "HVGroup", position = 10)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setHvHorizontalSpaceItem( int value ) {
    this.hvHorizontalSpaceItem = value;
  }

  private int hvVerticalSpaceItem;

  @Label("Vertical Spacing")
  @OptionGroupAnnotation(name = "HVGroup", position = 20)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getHvVerticalSpaceItem() {
    return this.hvVerticalSpaceItem;
  }

  @Label("Vertical Spacing")
  @OptionGroupAnnotation(name = "HVGroup", position = 20)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setHvVerticalSpaceItem( int value ) {
    this.hvVerticalSpaceItem = value;
  }

  private int arHorizontalSpaceItem;

  @Label("Horizontal Spacing")
  @OptionGroupAnnotation(name = "CompactGroup", position = 10)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getArHorizontalSpaceItem() {
    return this.arHorizontalSpaceItem;
  }

  @Label("Horizontal Spacing")
  @OptionGroupAnnotation(name = "CompactGroup", position = 10)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setArHorizontalSpaceItem( int value ) {
    this.arHorizontalSpaceItem = value;
  }

  private int arVerticalSpaceItem;

  @Label("Vertical Spacing")
  @OptionGroupAnnotation(name = "CompactGroup", position = 20)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getArVerticalSpaceItem() {
    return this.arVerticalSpaceItem;
  }

  @Label("Vertical Spacing")
  @OptionGroupAnnotation(name = "CompactGroup", position = 20)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setArVerticalSpaceItem( int value ) {
    this.arVerticalSpaceItem = value;
  }

  private boolean arUsingViewAspectRatioItem;

  @Label("Use Aspect Ratio of View")
  @OptionGroupAnnotation(name = "CompactGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isArUsingViewAspectRatioItem() {
    return this.arUsingViewAspectRatioItem;
  }

  @Label("Use Aspect Ratio of View")
  @OptionGroupAnnotation(name = "CompactGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setArUsingViewAspectRatioItem( boolean value ) {
    this.arUsingViewAspectRatioItem = value;
  }

  private double compactPreferredAspectRatioItem;

  @Label("Preferred Aspect Ratio")
  @OptionGroupAnnotation(name = "CompactGroup", position = 50)
  @DefaultValue(doubleValue = 1.41d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.2d, max = 5.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getCompactPreferredAspectRatioItem() {
    return this.compactPreferredAspectRatioItem;
  }

  @Label("Preferred Aspect Ratio")
  @OptionGroupAnnotation(name = "CompactGroup", position = 50)
  @DefaultValue(doubleValue = 1.41d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.2d, max = 5.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setCompactPreferredAspectRatioItem( double value ) {
    this.compactPreferredAspectRatioItem = value;
  }

  public final boolean isCompactPreferredAspectRatioItemDisabled() {
    return isArUsingViewAspectRatioItem();
  }

  private LayoutOrientation classicLayoutOrientationItem = LayoutOrientation.TOP_TO_BOTTOM;

  @Label("Orientation")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutOrientation.class, stringValue = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public final LayoutOrientation getClassicLayoutOrientationItem() {
    return this.classicLayoutOrientationItem;
  }

  @Label("Orientation")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutOrientation.class, stringValue = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public final void setClassicLayoutOrientationItem( LayoutOrientation value ) {
    this.classicLayoutOrientationItem = value;
  }

  private int minimumNodeDistanceItem;

  @Label("Minimum Node Distance")
  @MinMax(min = 1, max = 100)
  @DefaultValue(intValue = 20, valueType = DefaultValue.ValueType.INT_TYPE)
  @OptionGroupAnnotation(name = "ClassicGroup", position = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumNodeDistanceItem() {
    return this.minimumNodeDistanceItem;
  }

  @Label("Minimum Node Distance")
  @MinMax(min = 1, max = 100)
  @DefaultValue(intValue = 20, valueType = DefaultValue.ValueType.INT_TYPE)
  @OptionGroupAnnotation(name = "ClassicGroup", position = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeDistanceItem( int value ) {
    this.minimumNodeDistanceItem = value;
  }

  private int minimumLayerDistanceItem;

  @Label("Minimum Layer Distance")
  @MinMax(min = 10, max = 300)
  @DefaultValue(intValue = 40, valueType = DefaultValue.ValueType.INT_TYPE)
  @OptionGroupAnnotation(name = "ClassicGroup", position = 30)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumLayerDistanceItem() {
    return this.minimumLayerDistanceItem;
  }

  @Label("Minimum Layer Distance")
  @MinMax(min = 10, max = 300)
  @DefaultValue(intValue = 40, valueType = DefaultValue.ValueType.INT_TYPE)
  @OptionGroupAnnotation(name = "ClassicGroup", position = 30)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumLayerDistanceItem( int value ) {
    this.minimumLayerDistanceItem = value;
  }

  private PortStyle portStyleItem = PortStyle.NODE_CENTER;

  @Label("Port Style")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortStyle.class, stringValue = "NODE_CENTER")
  @EnumValueAnnotation(label = "Node Centered", value = "NODE_CENTER")
  @EnumValueAnnotation(label = "Border Centered", value = "BORDER_CENTER")
  @EnumValueAnnotation(label = "Border Distributed", value = "BORDER_DISTRIBUTED")
  public final PortStyle getPortStyleItem() {
    return this.portStyleItem;
  }

  @Label("Port Style")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortStyle.class, stringValue = "NODE_CENTER")
  @EnumValueAnnotation(label = "Node Centered", value = "NODE_CENTER")
  @EnumValueAnnotation(label = "Border Centered", value = "BORDER_CENTER")
  @EnumValueAnnotation(label = "Border Distributed", value = "BORDER_DISTRIBUTED")
  public final void setPortStyleItem( PortStyle value ) {
    this.portStyleItem = value;
  }

  private boolean enforcingGlobalLayeringItem;

  @Label("Global Layering")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 50)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isEnforcingGlobalLayeringItem() {
    return this.enforcingGlobalLayeringItem;
  }

  @Label("Global Layering")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 50)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setEnforcingGlobalLayeringItem( boolean value ) {
    this.enforcingGlobalLayeringItem = value;
  }

  private boolean orthogonalEdgeRoutingItem;

  @Label("Orthogonal Edge Routing")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isOrthogonalEdgeRoutingItem() {
    return this.orthogonalEdgeRoutingItem;
  }

  @Label("Orthogonal Edge Routing")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setOrthogonalEdgeRoutingItem( boolean value ) {
    this.orthogonalEdgeRoutingItem = value;
  }

  private double busAlignmentItem;

  @Label("Edge Bus Alignment")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 70)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getBusAlignmentItem() {
    return this.busAlignmentItem;
  }

  @Label("Edge Bus Alignment")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 70)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setBusAlignmentItem( double value ) {
    this.busAlignmentItem = value;
  }

  public final boolean isBusAlignmentItemDisabled() {
    return isOrthogonalEdgeRoutingItem() == false || (isEnforcingGlobalLayeringItem() == false && getChildPlacementPolicyItem() != LeafPlacement.ALL_LEAVES_ON_SAME_LAYER);
  }

  private double verticalAlignmentItem;

  @Label("Vertical Child Alignment")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 80)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getVerticalAlignmentItem() {
    return this.verticalAlignmentItem;
  }

  @Label("Vertical Child Alignment")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 80)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setVerticalAlignmentItem( double value ) {
    this.verticalAlignmentItem = value;
  }

  public final boolean isVerticalAlignmentItemDisabled() {
    return !isEnforcingGlobalLayeringItem();
  }

  private LeafPlacement childPlacementPolicyItem = LeafPlacement.LEAVES_STACKED;

  @Label("Child Placement Policy")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 90)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LeafPlacement.class, stringValue = "SIBLINGS_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "Siblings in same Layer", value = "SIBLINGS_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "All Leaves in same Layer", value = "ALL_LEAVES_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "Leaves stacked", value = "LEAVES_STACKED")
  @EnumValueAnnotation(label = "Leaves stacked left", value = "LEAVES_STACKED_LEFT")
  @EnumValueAnnotation(label = "Leaves stacked right", value = "LEAVES_STACKED_RIGHT")
  @EnumValueAnnotation(label = "Leaves stacked left and right", value = "LEAVES_STACKED_LEFT_AND_RIGHT")
  public final LeafPlacement getChildPlacementPolicyItem() {
    return this.childPlacementPolicyItem;
  }

  @Label("Child Placement Policy")
  @OptionGroupAnnotation(name = "ClassicGroup", position = 90)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LeafPlacement.class, stringValue = "SIBLINGS_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "Siblings in same Layer", value = "SIBLINGS_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "All Leaves in same Layer", value = "ALL_LEAVES_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "Leaves stacked", value = "LEAVES_STACKED")
  @EnumValueAnnotation(label = "Leaves stacked left", value = "LEAVES_STACKED_LEFT")
  @EnumValueAnnotation(label = "Leaves stacked right", value = "LEAVES_STACKED_RIGHT")
  @EnumValueAnnotation(label = "Leaves stacked left and right", value = "LEAVES_STACKED_LEFT_AND_RIGHT")
  public final void setChildPlacementPolicyItem( LeafPlacement value ) {
    this.childPlacementPolicyItem = value;
  }

  private EnumEdgeLabeling edgeLabelingItem = EnumEdgeLabeling.NONE;

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumEdgeLabeling.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Integrated", value = "INTEGRATED")
  @EnumValueAnnotation(label = "Generic", value = "GENERIC")
  public final EnumEdgeLabeling getEdgeLabelingItem() {
    return edgeLabelingItem;
  }

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 20)
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
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 30)
  public final boolean isReducingAmbiguityItem() {
    return this.reducingAmbiguityItem;
  }

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 30)
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
  @EnumValueAnnotation(label = "At Target", value = "AT_TARGET")
  @EnumValueAnnotation(label = "Centered", value = "CENTERED")
  public final LayoutConfiguration.EnumLabelPlacementAlongEdge getLabelPlacementAlongEdgeItem() {
    return this.labelPlacementAlongEdgeItem;
  }

  @Label("Along Edge")
  @OptionGroupAnnotation(name = "PreferredPlacementGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumLabelPlacementAlongEdge.class, stringValue = "CENTERED")
  @EnumValueAnnotation(label = "Anywhere", value = "ANYWHERE")
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
