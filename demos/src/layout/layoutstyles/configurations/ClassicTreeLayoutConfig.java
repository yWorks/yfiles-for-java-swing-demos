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
import com.yworks.yfiles.layout.OrientationLayout;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.StraightLineEdgeRouter;
import com.yworks.yfiles.layout.tree.ClassicTreeLayout;
import com.yworks.yfiles.layout.tree.EdgeRoutingStyle;
import com.yworks.yfiles.layout.tree.LeafPlacement;
import com.yworks.yfiles.layout.tree.PortStyle;
import com.yworks.yfiles.layout.tree.TreeReductionStage;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;
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
@Label("ClassicTreeLayout")
public class ClassicTreeLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public ClassicTreeLayoutConfig() {
    ClassicTreeLayout layout = new ClassicTreeLayout();

    setRoutingStyleForNonTreeEdgesItem(EnumRoute.ORTHOGONAL);
    setEdgeBundlingStrengthItem(0.95);
    setActOnSelectionOnlyItem(false);

    setClassicLayoutOrientationItem(LayoutOrientation.TOP_TO_BOTTOM);

    setMinimumNodeDistanceItem((int)layout.getMinimumNodeDistance());
    setMinimumLayerDistanceItem((int)layout.getMinimumLayerDistance());
    setPortStyleItem(PortStyle.NODE_CENTER);

    setConsiderNodeLabelsItem(false);

    setOrthogonalEdgeRoutingItem(false);

    setVerticalAlignmentItem(0.5);
    setLeafPlacementPolicyItem(LeafPlacement.SIBLINGS_ON_SAME_LAYER);
    setEnforceGlobalLayeringItem(false);

    setBusAlignmentItem(0.5);

    setEdgeLabelingItem(EnumEdgeLabeling.NONE);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    ClassicTreeLayout layout = configureClassicLayout();

    layout.setParallelEdgeRouterEnabled(false);
    ((ComponentLayout)layout.getComponentLayout()).setStyle(ComponentArrangementStyles.MULTI_ROWS);
    layout.setSubgraphLayoutEnabled(isActOnSelectionOnlyItem());

    layout.prependStage(createTreeReductionStage());

    boolean placeLabels = getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED || getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC;

    // required to prevent WrongGraphStructure exception which may be thrown by TreeLayout if there are edges
    // between group nodes
    layout.prependStage(new HandleEdgesBetweenGroupsStage(placeLabels));

    layout.setNodeLabelConsiderationEnabled(this.isConsiderNodeLabelsItem());


    if (getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC) {
      layout.setIntegratedEdgeLabelingEnabled(false);

      GenericLabeling labeling = new GenericLabeling();
      labeling.setEdgeLabelPlacementEnabled(true);
      labeling.setNodeLabelPlacementEnabled(false);
      labeling.setAmbiguityReductionEnabled(isReduceAmbiguityItem());
      layout.setLabelingEnabled(true);
      layout.setLabeling(labeling);
    } else if (getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED) {
      layout.setIntegratedEdgeLabelingEnabled(true);
    }

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    return createLabelingLayoutData(graphComponent.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem());
  }

  /**
   * Configures the tree reduction stage that will handle edges that do not belong to the tree.
   */
  private TreeReductionStage createTreeReductionStage() {
    TreeReductionStage reductionStage = new TreeReductionStage((ILayoutAlgorithm)null);
    if (getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED) {
      reductionStage.setNonTreeEdgeLabelingAlgorithm(new GenericLabeling());
    }
    reductionStage.setMultiParentAllowed(!isEnforceGlobalLayeringItem() && getLeafPlacementPolicyItem() != LeafPlacement.ALL_LEAVES_ON_SAME_LAYER);

    if (getRoutingStyleForNonTreeEdgesItem() == EnumRoute.ORGANIC) {
      reductionStage.setNonTreeEdgeRouter(new OrganicEdgeRouter());
      reductionStage.setNonTreeEdgeSelectionKey(OrganicEdgeRouter.AFFECTED_EDGES_DPKEY);
    } else if (getRoutingStyleForNonTreeEdgesItem() == EnumRoute.ORTHOGONAL) {
      EdgeRouter edgeRouter = new EdgeRouter((ILayoutAlgorithm)null);
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

  private ClassicTreeLayout configureClassicLayout() {
    ClassicTreeLayout layout = new ClassicTreeLayout();
    layout.setMinimumNodeDistance(getMinimumNodeDistanceItem());
    layout.setMinimumLayerDistance(getMinimumLayerDistanceItem());

    ((OrientationLayout)layout.getOrientationLayout()).setOrientation(getClassicLayoutOrientationItem());

    if (isOrthogonalEdgeRoutingItem()) {
      layout.setEdgeRoutingStyle(EdgeRoutingStyle.ORTHOGONAL);
    } else {
      layout.setEdgeRoutingStyle(EdgeRoutingStyle.PLAIN);
    }

    layout.setLeafPlacement(getLeafPlacementPolicyItem());
    layout.setGlobalLayeringEnforced(isEnforceGlobalLayeringItem());
    layout.setPortStyle(getPortStyleItem());

    layout.setVerticalAlignment(getVerticalAlignmentItem());
    layout.setBusAlignment(getBusAlignmentItem());

    return layout;
  }

  // ReSharper disable UnusedMember.Global
  // ReSharper disable InconsistentNaming
  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GeneralGroup;

  @Label("Edges")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object EdgesGroup;

  @Label("Labeling")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LabelingGroup;

  @Label("Non-Tree Edges")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object NonTreeEdgesGroup;

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

  // ReSharper restore UnusedMember.Global
  // ReSharper restore InconsistentNaming
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

  /**
   * Gets the description text.
   * @return The description text.
   */
  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<Paragraph>This layout is designed to arrange directed and undirected trees that have a unique root node. " + "All children are placed below their parent in relation to the main layout direction. " + "The edges of the graph are routed as straight-line segments or in an orthogonal bus-like fashion.</Paragraph>" + "<Paragraph>Tree layout algorithms are commonly used for visualizing relational data and for producing diagrams of high quality that are able to reveal possible hierarchic properties of the graph." + " More precisely, they find applications in</Paragraph>" + "<List>" + "<ListItem><Paragraph>Dataflow analysis</Paragraph></ListItem>" + "<ListItem><Paragraph>Software engineering</Paragraph></ListItem>" + "<ListItem><Paragraph>Network management</Paragraph></ListItem>" + "<ListItem><Paragraph>Bioinformatics</Paragraph></ListItem>" + "<ListItem><Paragraph>Business Administration</Paragraph></ListItem>" + "</List>";
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
  @OptionGroupAnnotation(name = "NonTreeEdgesGroup", position = 30)
  @DefaultValue(doubleValue = 0.95d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getEdgeBundlingStrengthItem() {
    return this.edgeBundlingStrengthItem;
  }

  @Label("Bundling Strength")
  @OptionGroupAnnotation(name = "NonTreeEdgesGroup", position = 30)
  @DefaultValue(doubleValue = 0.95d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setEdgeBundlingStrengthItem( double value ) {
    this.edgeBundlingStrengthItem = value;
  }

  public final boolean isEdgeBundlingStrengthItemDisabled() {
    return getRoutingStyleForNonTreeEdgesItem() != EnumRoute.BUNDLED;
  }

  private boolean actOnSelectionOnlyItem;

  @Label("Act on Selection Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isActOnSelectionOnlyItem() {
    return this.actOnSelectionOnlyItem;
  }

  @Label("Act on Selection Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setActOnSelectionOnlyItem( boolean value ) {
    this.actOnSelectionOnlyItem = value;
  }

  private boolean considerNodeLabelsItem;

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsiderNodeLabelsItem() {
    return this.considerNodeLabelsItem;
  }

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsiderNodeLabelsItem( boolean value ) {
    this.considerNodeLabelsItem = value;
  }

  private LayoutOrientation classicLayoutOrientationItem = LayoutOrientation.TOP_TO_BOTTOM;

  @Label("Orientation")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutOrientation.class, stringValue = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public final LayoutOrientation getClassicLayoutOrientationItem() {
    return this.classicLayoutOrientationItem;
  }

  @Label("Orientation")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
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
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumNodeDistanceItem() {
    return this.minimumNodeDistanceItem;
  }

  @Label("Minimum Node Distance")
  @MinMax(min = 1, max = 100)
  @DefaultValue(intValue = 20, valueType = DefaultValue.ValueType.INT_TYPE)
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeDistanceItem( int value ) {
    this.minimumNodeDistanceItem = value;
  }

  private int minimumLayerDistanceItem;

  @Label("Minimum Layer Distance")
  @MinMax(min = 10, max = 300)
  @DefaultValue(intValue = 40, valueType = DefaultValue.ValueType.INT_TYPE)
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumLayerDistanceItem() {
    return this.minimumLayerDistanceItem;
  }

  @Label("Minimum Layer Distance")
  @MinMax(min = 10, max = 300)
  @DefaultValue(intValue = 40, valueType = DefaultValue.ValueType.INT_TYPE)
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumLayerDistanceItem( int value ) {
    this.minimumLayerDistanceItem = value;
  }

  private PortStyle portStyleItem = PortStyle.NODE_CENTER;

  @Label("Port Style")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortStyle.class, stringValue = "NODE_CENTER")
  @EnumValueAnnotation(label = "Node Centered", value = "NODE_CENTER")
  @EnumValueAnnotation(label = "Border Centered", value = "BORDER_CENTER")
  @EnumValueAnnotation(label = "Border Distributed", value = "BORDER_DISTRIBUTED")
  public final PortStyle getPortStyleItem() {
    return this.portStyleItem;
  }

  @Label("Port Style")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortStyle.class, stringValue = "NODE_CENTER")
  @EnumValueAnnotation(label = "Node Centered", value = "NODE_CENTER")
  @EnumValueAnnotation(label = "Border Centered", value = "BORDER_CENTER")
  @EnumValueAnnotation(label = "Border Distributed", value = "BORDER_DISTRIBUTED")
  public final void setPortStyleItem( PortStyle value ) {
    this.portStyleItem = value;
  }

  private boolean enforceGlobalLayeringItem;

  @Label("Global Layering")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 35)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isEnforceGlobalLayeringItem() {
    return this.enforceGlobalLayeringItem;
  }

  @Label("Global Layering")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 35)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setEnforceGlobalLayeringItem( boolean value ) {
    this.enforceGlobalLayeringItem = value;
  }

  private boolean orthogonalEdgeRoutingItem;

  @Label("Orthogonal Edge Routing")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isOrthogonalEdgeRoutingItem() {
    return this.orthogonalEdgeRoutingItem;
  }

  @Label("Orthogonal Edge Routing")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setOrthogonalEdgeRoutingItem( boolean value ) {
    this.orthogonalEdgeRoutingItem = value;
  }

  private double busAlignmentItem;

  @Label("Edge Bus Alignment")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 30)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getBusAlignmentItem() {
    return this.busAlignmentItem;
  }

  @Label("Edge Bus Alignment")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 30)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setBusAlignmentItem( double value ) {
    this.busAlignmentItem = value;
  }

  public final boolean isBusAlignmentItemDisabled() {
    return isOrthogonalEdgeRoutingItem() == false || (isEnforceGlobalLayeringItem() == false && getLeafPlacementPolicyItem() != LeafPlacement.ALL_LEAVES_ON_SAME_LAYER);
  }

  private double verticalAlignmentItem;

  @Label("Vertical Child Alignment")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getVerticalAlignmentItem() {
    return this.verticalAlignmentItem;
  }

  @Label("Vertical Child Alignment")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setVerticalAlignmentItem( double value ) {
    this.verticalAlignmentItem = value;
  }

  public final boolean isVerticalAlignmentItemDisabled() {
    return !isEnforceGlobalLayeringItem();
  }

  private LeafPlacement leafPlacementPolicyItem = LeafPlacement.LEAVES_STACKED;

  @Label("Leaf Placement")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LeafPlacement.class, stringValue = "SIBLINGS_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "Siblings in same Layer", value = "SIBLINGS_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "All Leaves in same Layer", value = "ALL_LEAVES_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "Leaves stacked", value = "LEAVES_STACKED")
  @EnumValueAnnotation(label = "Leaves stacked left", value = "LEAVES_STACKED_LEFT")
  @EnumValueAnnotation(label = "Leaves stacked right", value = "LEAVES_STACKED_RIGHT")
  @EnumValueAnnotation(label = "Leaves stacked left and right", value = "LEAVES_STACKED_LEFT_AND_RIGHT")
  public final LeafPlacement getLeafPlacementPolicyItem() {
    return this.leafPlacementPolicyItem;
  }

  @Label("Leaf Placement")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LeafPlacement.class, stringValue = "SIBLINGS_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "Siblings in same Layer", value = "SIBLINGS_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "All Leaves in same Layer", value = "ALL_LEAVES_ON_SAME_LAYER")
  @EnumValueAnnotation(label = "Leaves stacked", value = "LEAVES_STACKED")
  @EnumValueAnnotation(label = "Leaves stacked left", value = "LEAVES_STACKED_LEFT")
  @EnumValueAnnotation(label = "Leaves stacked right", value = "LEAVES_STACKED_RIGHT")
  @EnumValueAnnotation(label = "Leaves stacked left and right", value = "LEAVES_STACKED_LEFT_AND_RIGHT")
  public final void setLeafPlacementPolicyItem( LeafPlacement value ) {
    this.leafPlacementPolicyItem = value;
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

  private boolean reduceAmbiguityItem;

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 20)
  public final boolean isReduceAmbiguityItem() {
    return this.reduceAmbiguityItem;
  }

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 20)
  public final void setReduceAmbiguityItem( boolean value ) {
    this.reduceAmbiguityItem = value;
  }

  public final boolean isReduceAmbiguityItemDisabled() {
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
   * {@link TreeReductionStage}. Optionally, {@link layout.layoutstyles.configurations.TreeLayoutConfig.HandleEdgesBetweenGroupsStage}
   * can also place the labels of the edges that were temporarily removed right after they are restored back to the graph.
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
