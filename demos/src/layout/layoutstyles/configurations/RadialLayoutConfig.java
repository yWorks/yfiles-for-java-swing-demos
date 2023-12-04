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
import com.yworks.yfiles.layout.EdgeBundleDescriptor;
import com.yworks.yfiles.layout.EdgeBundling;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.NodeLabelingPolicy;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.radial.CenterNodesPolicy;
import com.yworks.yfiles.layout.radial.EdgeRoutingStrategy;
import com.yworks.yfiles.layout.radial.LayeringStrategy;
import com.yworks.yfiles.layout.radial.RadialLayout;
import com.yworks.yfiles.layout.radial.RadialLayoutData;
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
@Label("RadialLayout")
public class RadialLayoutConfig extends LayoutConfiguration {
  private static final int MAXIMUM_SMOOTHNESS = 10;

  private static final int MINIMUM_SMOOTHNESS = 1;

  private static final int SMOOTHNESS_ANGLE_FACTOR = 4;

  /**
   * Setup default values for various configuration parameters.
   */
  public RadialLayoutConfig() {
    RadialLayout layout = new RadialLayout();

    setCenterStrategyItem(CenterNodesPolicy.WEIGHTED_CENTRALITY);
    setLayeringStrategyItem(LayeringStrategy.BFS);
    setMinimumLayerDistanceItem((int)layout.getMinimumLayerDistance());
    setMinimumNodeToNodeDistanceItem((int)layout.getMinimumNodeToNodeDistance());
    setMaximumChildSectorSizeItem((int)layout.getMaximumChildSectorAngle());
    setEdgeRoutingStrategyItem(EdgeRoutingStyle.ARC);
    setEdgeSmoothnessItem((int)Math.min(MAXIMUM_SMOOTHNESS, (1 + MAXIMUM_SMOOTHNESS * SMOOTHNESS_ANGLE_FACTOR - layout.getMinimumBendAngle()) / SMOOTHNESS_ANGLE_FACTOR));
    setEdgeBundlingStrengthItem(0.95);

    setEdgeLabelingEnabledItem(false);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10);
    setNodeLabelingStyleItem(LayoutConfiguration.EnumNodeLabelingPolicies.CONSIDER_CURRENT_POSITION);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    RadialLayout layout = new RadialLayout();
    layout.setMinimumNodeToNodeDistance(getMinimumNodeToNodeDistanceItem());
    if (getEdgeRoutingStrategyItem() != EdgeRoutingStyle.BUNDLED) {
      layout.setEdgeRoutingStrategy(EdgeRoutingStrategy.fromOrdinal(getEdgeRoutingStrategyItem().value()));
    }

    int minimumBendAngle = 1 + (MAXIMUM_SMOOTHNESS - getEdgeSmoothnessItem()) * SMOOTHNESS_ANGLE_FACTOR;
    layout.setMinimumBendAngle(minimumBendAngle);
    layout.setMinimumLayerDistance(getMinimumLayerDistanceItem());
    layout.setMaximumChildSectorAngle(getMaximumChildSectorSizeItem());
    layout.setCenterNodesPolicy(getCenterStrategyItem());
    layout.setLayeringStrategy(getLayeringStrategyItem());

    EdgeBundling ebc = layout.getEdgeBundling();
    ebc.setBundlingStrength(getEdgeBundlingStrengthItem());
    EdgeBundleDescriptor edgeBundleDescriptor = new EdgeBundleDescriptor();
    edgeBundleDescriptor.setBundled(getEdgeRoutingStrategyItem() == EdgeRoutingStyle.BUNDLED);
    ebc.setDefaultBundleDescriptor(edgeBundleDescriptor);

    if (isEdgeLabelingEnabledItem()) {
      GenericLabeling labeling = new GenericLabeling();
      labeling.setEdgeLabelPlacementEnabled(true);
      labeling.setNodeLabelPlacementEnabled(false);
      labeling.setAmbiguityReductionEnabled(isReduceAmbiguityItem());
      layout.setLabelingEnabled(true);
      layout.setLabeling(labeling);
    }

    switch (getNodeLabelingStyleItem()) {
      case NONE:
        layout.setNodeLabelConsiderationEnabled(false);
        break;
      case RAYLIKE_LEAVES:
        layout.setIntegratedNodeLabelingEnabled(true);
        layout.setNodeLabelingPolicy(NodeLabelingPolicy.RAY_LIKE_LEAVES);
        break;
      case RAYLIKE:
        layout.setIntegratedNodeLabelingEnabled(true);
        layout.setNodeLabelingPolicy(NodeLabelingPolicy.RAY_LIKE);
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

    if (this.getNodeLabelingStyleItem() == LayoutConfiguration.EnumNodeLabelingPolicies.RAYLIKE_LEAVES || getNodeLabelingStyleItem() == LayoutConfiguration.EnumNodeLabelingPolicies.RAYLIKE || getNodeLabelingStyleItem() == LayoutConfiguration.EnumNodeLabelingPolicies.HORIZONTAL) {
      for (ILabel label : graphComponent.getGraph().getLabels()) {
        if (label.getOwner() instanceof INode) {
          graphComponent.getGraph().setLabelLayoutParameter(label, FreeNodeLabelModel.INSTANCE.findBestParameter(label, FreeNodeLabelModel.INSTANCE, label.getLayout()));
        }
      }
    }

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    RadialLayoutData layoutData = new RadialLayoutData();

    if (getCenterStrategyItem() == CenterNodesPolicy.CUSTOM) {
      layoutData.setCenterNodes(graphComponent.getSelection().getSelectedNodes());
    }

    return layoutData.combineWith(createLabelingLayoutData(graphComponent.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem()));
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
    return "<p>The radial layout arranges the nodes of a graph on concentric circles. Similar to hierarchic layouts, " +
           "the overall flow of the graph is nicely visualized.</p>" +
           "<p>This style is well suited for the visualization of directed graphs and tree-like structures.</p>";
  }

  private int minimumLayerDistanceItem;

  @Label("Minimum Circle Distance")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(intValue = 100, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 1000)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumLayerDistanceItem() {
    return this.minimumLayerDistanceItem;
  }

  @Label("Minimum Circle Distance")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(intValue = 100, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 1000)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumLayerDistanceItem( int value ) {
    this.minimumLayerDistanceItem = value;
  }

  private int minimumNodeToNodeDistanceItem;

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 300)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumNodeToNodeDistanceItem() {
    return this.minimumNodeToNodeDistanceItem;
  }

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 300)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeToNodeDistanceItem( int value ) {
    this.minimumNodeToNodeDistanceItem = value;
  }

  private int maximumChildSectorSizeItem;

  @Label("Maximum Child Sector Size")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(intValue = 180, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 15, max = 360)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMaximumChildSectorSizeItem() {
    return this.maximumChildSectorSizeItem;
  }

  @Label("Maximum Child Sector Size")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(intValue = 180, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 15, max = 360)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMaximumChildSectorSizeItem( int value ) {
    this.maximumChildSectorSizeItem = value;
  }

  private EdgeRoutingStyle edgeRoutingStrategyItem = null;

  @Label("Routing Style")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeRoutingStyle.class, stringValue = "ARC")
  @EnumValueAnnotation(label = "Straight", value = "POLYLINE")
  @EnumValueAnnotation(label = "Arc", value = "ARC")
  @EnumValueAnnotation(label = "Curved", value = "CURVED")
  @EnumValueAnnotation(label = "Radial Polyline", value = "RADIAL_POLYLINE")
  @EnumValueAnnotation(label = "Bundled", value = "BUNDLED")
  public final EdgeRoutingStyle getEdgeRoutingStrategyItem() {
    return this.edgeRoutingStrategyItem;
  }

  @Label("Routing Style")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeRoutingStyle.class, stringValue = "ARC")
  @EnumValueAnnotation(label = "Straight", value = "POLYLINE")
  @EnumValueAnnotation(label = "Arc", value = "ARC")
  @EnumValueAnnotation(label = "Curved", value = "CURVED")
  @EnumValueAnnotation(label = "Radial Polyline", value = "RADIAL_POLYLINE")
  @EnumValueAnnotation(label = "Bundled", value = "BUNDLED")
  public final void setEdgeRoutingStrategyItem( EdgeRoutingStyle value ) {
    this.edgeRoutingStrategyItem = value;
  }

  private int edgeSmoothnessItem;

  @Label("Arc Smoothness")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @DefaultValue(intValue = 9, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = MINIMUM_SMOOTHNESS, max = MAXIMUM_SMOOTHNESS)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getEdgeSmoothnessItem() {
    return this.edgeSmoothnessItem;
  }

  @Label("Arc Smoothness")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 50)
  @DefaultValue(intValue = 9, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = MINIMUM_SMOOTHNESS, max = MAXIMUM_SMOOTHNESS)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setEdgeSmoothnessItem( int value ) {
    this.edgeSmoothnessItem = value;
  }

  public final boolean isEdgeSmoothnessItemDisabled() {
    return getEdgeRoutingStrategyItem() != EdgeRoutingStyle.ARC;
  }

  private double edgeBundlingStrengthItem;

  @Label("Bundling Strength")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 55)
  @DefaultValue(doubleValue = 0.95d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getEdgeBundlingStrengthItem() {
    return this.edgeBundlingStrengthItem;
  }

  @Label("Bundling Strength")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 55)
  @DefaultValue(doubleValue = 0.95d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setEdgeBundlingStrengthItem( double value ) {
    this.edgeBundlingStrengthItem = value;
  }

  public final boolean isEdgeBundlingStrengthItemDisabled() {
    return getEdgeRoutingStrategyItem() != EdgeRoutingStyle.BUNDLED;
  }

  private CenterNodesPolicy centerStrategyItem = CenterNodesPolicy.DIRECTED;

  @Label("Center Allocation Strategy")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 60)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = CenterNodesPolicy.class, stringValue = "WEIGHTED_CENTRALITY")
  @EnumValueAnnotation(label = "Directed", value = "DIRECTED")
  @EnumValueAnnotation(label = "Centrality", value = "CENTRALITY")
  @EnumValueAnnotation(label = "Weighted Centrality", value = "WEIGHTED_CENTRALITY")
  @EnumValueAnnotation(label = "Selected Nodes", value = "CUSTOM")
  public final CenterNodesPolicy getCenterStrategyItem() {
    return this.centerStrategyItem;
  }

  @Label("Center Allocation Strategy")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 60)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = CenterNodesPolicy.class, stringValue = "WEIGHTED_CENTRALITY")
  @EnumValueAnnotation(label = "Directed", value = "DIRECTED")
  @EnumValueAnnotation(label = "Centrality", value = "CENTRALITY")
  @EnumValueAnnotation(label = "Weighted Centrality", value = "WEIGHTED_CENTRALITY")
  @EnumValueAnnotation(label = "Selected Nodes", value = "CUSTOM")
  public final void setCenterStrategyItem( CenterNodesPolicy value ) {
    this.centerStrategyItem = value;
  }

  private LayeringStrategy layeringStrategyItem = null;

  @Label("Circle Assignment Strategy")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 70)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayeringStrategy.class, stringValue = "BFS")
  @EnumValueAnnotation(label = "Distance From Center", value = "BFS")
  @EnumValueAnnotation(label = "Hierarchic", value = "HIERARCHICAL")
  @EnumValueAnnotation(label = "Dendrogram", value = "DENDROGRAM")
  public final LayeringStrategy getLayeringStrategyItem() {
    return this.layeringStrategyItem;
  }

  @Label("Circle Assignment Strategy")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 70)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayeringStrategy.class, stringValue = "BFS")
  @EnumValueAnnotation(label = "Distance From Center", value = "BFS")
  @EnumValueAnnotation(label = "Hierarchic", value = "HIERARCHICAL")
  @EnumValueAnnotation(label = "Dendrogram", value = "DENDROGRAM")
  public final void setLayeringStrategyItem( LayeringStrategy value ) {
    this.layeringStrategyItem = value;
  }

  private LayoutConfiguration.EnumNodeLabelingPolicies nodeLabelingStyleItem = LayoutConfiguration.EnumNodeLabelingPolicies.NONE;

  @Label("Node Labeling")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumNodeLabelingPolicies.class, stringValue = "CONSIDER_CURRENT_POSITION")
  @EnumValueAnnotation(label = "Ignore Labels", value = "NONE")
  @EnumValueAnnotation(label = "Consider Labels", value = "CONSIDER_CURRENT_POSITION")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Ray-like at Leaves", value = "RAYLIKE_LEAVES")
  @EnumValueAnnotation(label = "Ray-like", value = "RAYLIKE")
  public final LayoutConfiguration.EnumNodeLabelingPolicies getNodeLabelingStyleItem() {
    return this.nodeLabelingStyleItem;
  }

  @Label("Node Labeling")
  @OptionGroupAnnotation(name = "NodePropertiesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumNodeLabelingPolicies.class, stringValue = "CONSIDER_CURRENT_POSITION")
  @EnumValueAnnotation(label = "Ignore Labels", value = "NONE")
  @EnumValueAnnotation(label = "Consider Labels", value = "CONSIDER_CURRENT_POSITION")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Ray-like at Leaves", value = "RAYLIKE_LEAVES")
  @EnumValueAnnotation(label = "Ray-like", value = "RAYLIKE")
  public final void setNodeLabelingStyleItem( LayoutConfiguration.EnumNodeLabelingPolicies value ) {
    this.nodeLabelingStyleItem = value;
  }

  private boolean edgeLabelingEnabledItem;

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isEdgeLabelingEnabledItem() {
    return this.edgeLabelingEnabledItem;
  }

  @Label("Edge Labeling")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setEdgeLabelingEnabledItem( boolean value ) {
    this.edgeLabelingEnabledItem = value;
  }

  private boolean reduceAmbiguityItem;

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isReduceAmbiguityItem() {
    return this.reduceAmbiguityItem;
  }

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "EdgePropertiesGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setReduceAmbiguityItem( boolean value ) {
    this.reduceAmbiguityItem = value;
  }

  public final boolean isReduceAmbiguityItemDisabled() {
    return !isEdgeLabelingEnabledItem();
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
    return !isEdgeLabelingEnabledItem();
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
    return !isEdgeLabelingEnabledItem();
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
    return !isEdgeLabelingEnabledItem();
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
    return !isEdgeLabelingEnabledItem() || getLabelPlacementSideOfEdgeItem() == LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE;
  }

  public enum EdgeRoutingStyle {
    POLYLINE(1),

    ARC(5),

    RADIAL_POLYLINE(6),

    CURVED(7),

    BUNDLED(8);

    private final int value;

    private EdgeRoutingStyle( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EdgeRoutingStyle fromOrdinal( int ordinal ) {
      for (EdgeRoutingStyle current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

}
