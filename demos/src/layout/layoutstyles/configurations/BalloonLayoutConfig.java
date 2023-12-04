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

import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.ComponentArrangementStyles;
import com.yworks.yfiles.layout.ComponentLayout;
import com.yworks.yfiles.layout.EdgeBundleDescriptor;
import com.yworks.yfiles.layout.EdgeBundling;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.MultiStageLayout;
import com.yworks.yfiles.layout.NodeLabelingPolicy;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.tree.BalloonLayout;
import com.yworks.yfiles.layout.tree.BalloonLayoutData;
import com.yworks.yfiles.layout.tree.InterleavedMode;
import com.yworks.yfiles.layout.tree.RootNodePolicy;
import com.yworks.yfiles.layout.tree.TreeReductionStage;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ISelectionModel;
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
@Label("BalloonLayout")
public class BalloonLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public BalloonLayoutConfig() {
    BalloonLayout layout = new BalloonLayout();

    setRootNodePolicyItem(RootNodePolicy.DIRECTED_ROOT);
    setRoutingStyleForNonTreeEdgesItem(EnumRoute.ORTHOGONAL);
    setActingOnSelectionOnlyItem(false);
    setEdgeBundlingStrengthItem(1);
    setPreferredChildWedgeItem(layout.getPreferredChildWedge());
    setPreferredRootWedgeItem(layout.getPreferredRootWedge());
    setMinimumEdgeLengthItem(layout.getMinimumEdgeLength());
    setCompactnessFactorItem(layout.getCompactnessFactor());
    setAllowingOverlapsItem(layout.isOverlapsAllowed());
    setFromSketchModeEnabledItem(layout.isFromSketchModeEnabled());
    setPlacingChildrenInterleavedItem(layout.getInterleavedMode() == InterleavedMode.ALL_NODES);
    setStraighteningChainsItem(layout.isChainStraighteningModeEnabled());

    setNodeLabelingStyleItem(EnumNodeLabelingPolicies.CONSIDER_CURRENT_POSITION);
    setEdgeLabelingItem(EnumEdgeLabeling.NONE);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    BalloonLayout layout = new BalloonLayout();

    ((ComponentLayout)layout.getComponentLayout()).setStyle(ComponentArrangementStyles.MULTI_ROWS);

    layout.setRootNodePolicy(getRootNodePolicyItem());
    layout.setPreferredChildWedge(getPreferredChildWedgeItem());
    layout.setPreferredRootWedge(getPreferredRootWedgeItem());
    layout.setMinimumEdgeLength(getMinimumEdgeLengthItem());
    layout.setCompactnessFactor(1 - getCompactnessFactorItem());
    layout.setOverlapsAllowed(isAllowingOverlapsItem());
    layout.setFromSketchModeEnabled(isFromSketchModeEnabledItem());
    layout.setChainStraighteningModeEnabled(isStraighteningChainsItem());
    layout.setInterleavedMode(isPlacingChildrenInterleavedItem() ? InterleavedMode.ALL_NODES : InterleavedMode.OFF);

    switch (getNodeLabelingStyleItem()) {
      case NONE:
        layout.setNodeLabelConsiderationEnabled(false);
        break;
      case RAYLIKE_LEAVES:
        layout.setIntegratedNodeLabelingEnabled(true);
        layout.setNodeLabelingPolicy(NodeLabelingPolicy.RAY_LIKE_LEAVES);
        break;
      case CONSIDER_CURRENT_POSITION:
        layout.setNodeLabelConsiderationEnabled(true);
        break;
      case HORIZONTAL:
        layout.setIntegratedNodeLabelingEnabled(true);
        layout.setNodeLabelingPolicy(NodeLabelingPolicy.HORIZONTAL);
        break;
      default:
        layout.setNodeLabelConsiderationEnabled(false);
        break;
    }

    // configures tree reduction stage and non-tree edge routing.
    layout.setSubgraphLayoutEnabled(isActingOnSelectionOnlyItem());
    MultiStageLayout multiStageLayout = layout;

    TreeReductionStage treeReductionStage = new TreeReductionStage();
    multiStageLayout.appendStage(treeReductionStage);
    if (getRoutingStyleForNonTreeEdgesItem() == EnumRoute.ORGANIC) {
      OrganicEdgeRouter organic = new OrganicEdgeRouter();
      treeReductionStage.setNonTreeEdgeRouter(organic);
      treeReductionStage.setNonTreeEdgeSelectionKey(OrganicEdgeRouter.AFFECTED_EDGES_DPKEY);
    } else if (getRoutingStyleForNonTreeEdgesItem() == EnumRoute.ORTHOGONAL) {
      EdgeRouter edgeRouter = new EdgeRouter();
      edgeRouter.setReroutingEnabled(true);
      edgeRouter.setScope(Scope.ROUTE_AFFECTED_EDGES);
      treeReductionStage.setNonTreeEdgeSelectionKey(edgeRouter.getAffectedEdgesDpKey());
      treeReductionStage.setNonTreeEdgeRouter(edgeRouter);
    } else if (getRoutingStyleForNonTreeEdgesItem() == EnumRoute.STRAIGHT_LINE) {
      treeReductionStage.setNonTreeEdgeRouter(treeReductionStage.createStraightLineRouter());
    } else if (getRoutingStyleForNonTreeEdgesItem() == EnumRoute.BUNDLED) {
      EdgeBundling ebc = treeReductionStage.getEdgeBundling();
      EdgeBundleDescriptor bundleDescriptor = new EdgeBundleDescriptor();
      bundleDescriptor.setBundled(true);
      ebc.setBundlingStrength(getEdgeBundlingStrengthItem());
      ebc.setDefaultBundleDescriptor(bundleDescriptor);
    }

    if (getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC) {
      layout.setIntegratedEdgeLabelingEnabled(false);
      GenericLabeling genericLabeling = new GenericLabeling();
      genericLabeling.setEdgeLabelPlacementEnabled(true);
      genericLabeling.setNodeLabelPlacementEnabled(false);
      genericLabeling.setAmbiguityReductionEnabled(isReducingAmbiguityItem());
      layout.setLabelingEnabled(true);
      layout.setLabeling(genericLabeling);
    } else if (getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED) {
      layout.setIntegratedEdgeLabelingEnabled(true);
      treeReductionStage.setNonTreeEdgeLabelingAlgorithm(new GenericLabeling());
    }

    if (getNodeLabelingStyleItem() == EnumNodeLabelingPolicies.RAYLIKE_LEAVES || getNodeLabelingStyleItem() == EnumNodeLabelingPolicies.HORIZONTAL) {
      for (ILabel label : graphComponent.getGraph().getNodeLabels()) {
        graphComponent.getGraph().setLabelLayoutParameter(label, FreeNodeLabelModel.INSTANCE.findBestParameter(label, FreeNodeLabelModel.INSTANCE, label.getLayout()));
      }
    }

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    BalloonLayoutData layoutData = new BalloonLayoutData();

    if (getRootNodePolicyItem() == RootNodePolicy.SELECTED_ROOT) {
      ISelectionModel<INode> selection = graphComponent.getSelection().getSelectedNodes();

      if (selection.size() > 0) {
        final INode root = selection.iterator().next();
        layoutData.setTreeRoot(node -> node == root);
      }
    }

    return layoutData.combineWith(createLabelingLayoutData(graphComponent.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem()));
  }

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

  public enum EnumNodeLabelingPolicies {
    NONE(0),

    HORIZONTAL(1),

    RAYLIKE_LEAVES(2),

    CONSIDER_CURRENT_POSITION(3);

    private final int value;

    private EnumNodeLabelingPolicies( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumNodeLabelingPolicies fromOrdinal( int ordinal ) {
      for (EnumNodeLabelingPolicies current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GeneralGroup;

  @Label("Labeling")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
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

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>The balloon layout is a tree layout that positions the subtrees in a radial " +
           "fashion around their root nodes. It is ideally suited for larger trees.</p>";
  }

  private RootNodePolicy rootNodePolicyItem = RootNodePolicy.DIRECTED_ROOT;

  @Label("Root Node Policy")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = RootNodePolicy.class, stringValue = "DIRECTED_ROOT")
  @EnumValueAnnotation(label = "Directed Root", value = "DIRECTED_ROOT")
  @EnumValueAnnotation(label = "Center Root", value = "CENTER_ROOT")
  @EnumValueAnnotation(label = "Weighted Center Root", value = "WEIGHTED_CENTER_ROOT")
  @EnumValueAnnotation(label = "Selected Node", value = "SELECTED_ROOT")
  public final RootNodePolicy getRootNodePolicyItem() {
    return this.rootNodePolicyItem;
  }

  @Label("Root Node Policy")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = RootNodePolicy.class, stringValue = "DIRECTED_ROOT")
  @EnumValueAnnotation(label = "Directed Root", value = "DIRECTED_ROOT")
  @EnumValueAnnotation(label = "Center Root", value = "CENTER_ROOT")
  @EnumValueAnnotation(label = "Weighted Center Root", value = "WEIGHTED_CENTER_ROOT")
  @EnumValueAnnotation(label = "Selected Node", value = "SELECTED_ROOT")
  public final void setRootNodePolicyItem( RootNodePolicy value ) {
    this.rootNodePolicyItem = value;
  }

  private EnumRoute routingStyleForNonTreeEdgesItem = EnumRoute.ORTHOGONAL;

  @Label("Routing Style for Non-Tree Edges")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumRoute.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHT_LINE")
  @EnumValueAnnotation(label = "Bundled", value = "BUNDLED")
  public final EnumRoute getRoutingStyleForNonTreeEdgesItem() {
    return this.routingStyleForNonTreeEdgesItem;
  }

  @Label("Routing Style for Non-Tree Edges")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumRoute.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHT_LINE")
  @EnumValueAnnotation(label = "Bundled", value = "BUNDLED")
  public final void setRoutingStyleForNonTreeEdgesItem( EnumRoute value ) {
    this.routingStyleForNonTreeEdgesItem = value;
  }

  private boolean actingOnSelectionOnlyItem;

  @Label("Act on Selection Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isActingOnSelectionOnlyItem() {
    return this.actingOnSelectionOnlyItem;
  }

  @Label("Act on Selection Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setActingOnSelectionOnlyItem( boolean value ) {
    this.actingOnSelectionOnlyItem = value;
  }

  private double edgeBundlingStrengthItem;

  @Label("Bundling Strength")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(doubleValue = 1.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 1.0, step = 0.01)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getEdgeBundlingStrengthItem() {
    return this.edgeBundlingStrengthItem;
  }

  @Label("Bundling Strength")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(doubleValue = 1.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 1.0, step = 0.01)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setEdgeBundlingStrengthItem( double value ) {
    this.edgeBundlingStrengthItem = value;
  }

  public final boolean isEdgeBundlingStrengthItemDisabled() {
    return getRoutingStyleForNonTreeEdgesItem() != EnumRoute.BUNDLED;
  }

  private int preferredChildWedgeItem;

  @Label("Preferred Child Wedge")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @DefaultValue(intValue = 210, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 359)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getPreferredChildWedgeItem() {
    return this.preferredChildWedgeItem;
  }

  @Label("Preferred Child Wedge")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @DefaultValue(intValue = 210, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 359)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setPreferredChildWedgeItem( int value ) {
    this.preferredChildWedgeItem = value;
  }

  private int preferredRootWedgeItem;

  @Label("Preferred Root Wedge")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 60)
  @DefaultValue(intValue = 360, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 360)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getPreferredRootWedgeItem() {
    return this.preferredRootWedgeItem;
  }

  @Label("Preferred Root Wedge")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 60)
  @DefaultValue(intValue = 360, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 360)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setPreferredRootWedgeItem( int value ) {
    this.preferredRootWedgeItem = value;
  }

  private int minimumEdgeLengthItem;

  @Label("Minimum Edge Length")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 70)
  @DefaultValue(intValue = 40, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 10, max = 400)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumEdgeLengthItem() {
    return this.minimumEdgeLengthItem;
  }

  @Label("Minimum Edge Length")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 70)
  @DefaultValue(intValue = 40, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 10, max = 400)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumEdgeLengthItem( int value ) {
    this.minimumEdgeLengthItem = value;
  }

  private double compactnessFactorItem;

  @Label("Compactness Factor")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 80)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.1d, max = 0.9d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getCompactnessFactorItem() {
    return this.compactnessFactorItem;
  }

  @Label("Compactness Factor")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 80)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.1d, max = 0.9d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setCompactnessFactorItem( double value ) {
    this.compactnessFactorItem = value;
  }

  private boolean allowingOverlapsItem;

  @Label("Allow Overlaps")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 90)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAllowingOverlapsItem() {
    return this.allowingOverlapsItem;
  }

  @Label("Allow Overlaps")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 90)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAllowingOverlapsItem( boolean value ) {
    this.allowingOverlapsItem = value;
  }

  private boolean fromSketchModeEnabledItem;

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 100)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isFromSketchModeEnabledItem() {
    return this.fromSketchModeEnabledItem;
  }

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 100)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setFromSketchModeEnabledItem( boolean value ) {
    this.fromSketchModeEnabledItem = value;
  }

  private boolean placingChildrenInterleavedItem;

  @Label("Place Children Interleaved")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 110)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isPlacingChildrenInterleavedItem() {
    return this.placingChildrenInterleavedItem;
  }

  @Label("Place Children Interleaved")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 110)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setPlacingChildrenInterleavedItem( boolean value ) {
    this.placingChildrenInterleavedItem = value;
  }

  private boolean straighteningChainsItem;

  @Label("Straighten Chains")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 120)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isStraighteningChainsItem() {
    return this.straighteningChainsItem;
  }

  @Label("Straighten Chains")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 120)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setStraighteningChainsItem( boolean value ) {
    this.straighteningChainsItem = value;
  }

  private EnumNodeLabelingPolicies nodeLabelingStyleItem = EnumNodeLabelingPolicies.NONE;

  @Label("Node Labeling")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumNodeLabelingPolicies.class, stringValue = "CONSIDER_CURRENT_POSITION")
  @EnumValueAnnotation(label = "Ignore Labels", value = "NONE")
  @EnumValueAnnotation(label = "Consider Labels", value = "CONSIDER_CURRENT_POSITION")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Ray-like at Leaves", value = "RAYLIKE_LEAVES")
  public final EnumNodeLabelingPolicies getNodeLabelingStyleItem() {
    return this.nodeLabelingStyleItem;
  }

  @Label("Node Labeling")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumNodeLabelingPolicies.class, stringValue = "CONSIDER_CURRENT_POSITION")
  @EnumValueAnnotation(label = "Ignore Labels", value = "NONE")
  @EnumValueAnnotation(label = "Consider Labels", value = "CONSIDER_CURRENT_POSITION")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Ray-like at Leaves", value = "RAYLIKE_LEAVES")
  public final void setNodeLabelingStyleItem( EnumNodeLabelingPolicies value ) {
    this.nodeLabelingStyleItem = value;
  }

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

  private EnumEdgeLabeling edgeLabelingItem = EnumEdgeLabeling.NONE;

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
    return getEdgeLabelingItem() == EnumEdgeLabeling.NONE || getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED;
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
    return getEdgeLabelingItem() == EnumEdgeLabeling.NONE || getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED;
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
    return getEdgeLabelingItem() == EnumEdgeLabeling.NONE || getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED || getLabelPlacementSideOfEdgeItem() == LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE;
  }

}
