/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.circular.CircularLayout;
import com.yworks.yfiles.layout.circular.CircularLayoutData;
import com.yworks.yfiles.layout.circular.LayoutStyle;
import com.yworks.yfiles.layout.circular.PartitionStyle;
import com.yworks.yfiles.layout.EdgeBundleDescriptor;
import com.yworks.yfiles.layout.EdgeBundling;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.tree.BalloonLayout;
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
@Label("CircularLayout")
public class CircularLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public CircularLayoutConfig() {
    CircularLayout layout = new CircularLayout();
    BalloonLayout treeLayout = layout.getBalloonLayout();

    setLayoutStyleItem(LayoutStyle.BCC_COMPACT);
    setActingOnSelectionOnlyItem(false);
    setFromSketchModeEnabledItem(false);
    setConsideringNodeLabelsItem(false);

    setPartitionStyleItem(PartitionStyle.CYCLE);
    setMinimumNodeDistanceItem(30);
    setChoosingRadiusAutomaticallyItem(true);
    setFixedRadiusItem(200);

    setEdgeBundlingEnabledItem(false);
    setEdgeBundlingStrengthItem(1);

    setPreferredChildWedgeItem(treeLayout.getPreferredChildWedge());
    setMinimumEdgeLengthItem(treeLayout.getMinimumEdgeLength());
    setMaximumDeviationAngleItem(layout.getMaximumDeviationAngle());
    setCompactnessFactorItem(treeLayout.getCompactnessFactor());
    setMinimumTreeNodeDistanceItem(treeLayout.getMinimumNodeDistance());
    setAllowingOverlapsItem(treeLayout.isOverlapsAllowed());
    setPlacingChildrenOnCommonRadiusItem(true);

    setEdgeLabelingEnabledItem(false);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    CircularLayout layout = new CircularLayout();
    BalloonLayout balloonLayout = layout.getBalloonLayout();

    layout.setLayoutStyle(getLayoutStyleItem());
    layout.setSubgraphLayoutEnabled(isActingOnSelectionOnlyItem());
    layout.setMaximumDeviationAngle(getMaximumDeviationAngleItem());
    layout.setFromSketchModeEnabled(isFromSketchModeEnabledItem());
    layout.setNodeLabelConsiderationEnabled(isConsideringNodeLabelsItem());

    layout.setPartitionStyle(getPartitionStyleItem());

    layout.getSingleCycleLayout().setMinimumNodeDistance(getMinimumNodeDistanceItem());
    layout.getSingleCycleLayout().setAutomaticRadiusEnabled(isChoosingRadiusAutomaticallyItem());
    layout.getSingleCycleLayout().setFixedRadius(getFixedRadiusItem());

    balloonLayout.setPreferredChildWedge(getPreferredChildWedgeItem());
    balloonLayout.setMinimumEdgeLength(getMinimumEdgeLengthItem());
    balloonLayout.setCompactnessFactor(getCompactnessFactorItem());
    balloonLayout.setOverlapsAllowed(isAllowingOverlapsItem());
    layout.setPlacingChildrenOnCommonRadiusEnabled(isPlacingChildrenOnCommonRadiusItem());
    balloonLayout.setMinimumNodeDistance(getMinimumTreeNodeDistanceItem());

    if (isEdgeLabelingEnabledItem()) {
      GenericLabeling genericLabeling = new GenericLabeling();
      genericLabeling.setEdgeLabelPlacementEnabled(true);
      genericLabeling.setNodeLabelPlacementEnabled(false);
      genericLabeling.setAmbiguityReductionEnabled(isReducingAmbiguityItem());
      layout.setLabelingEnabled(true);
      layout.setLabeling(genericLabeling);
    }

    EdgeBundling ebc = layout.getEdgeBundling();
    EdgeBundleDescriptor bundlingDescriptor = new EdgeBundleDescriptor();
    bundlingDescriptor.setBundled(isEdgeBundlingEnabledItem());
    ebc.setBundlingStrength(getEdgeBundlingStrengthItem());
    ebc.setDefaultBundleDescriptor(bundlingDescriptor);

    addPreferredPlacementDescriptor(graphComponent.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem());

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    CircularLayoutData layoutData = new CircularLayoutData();

    if (getLayoutStyleItem() == LayoutStyle.CUSTOM_GROUPS) {
      IGraph graph = graphComponent.getGraph();
      layoutData.setCustomGroups(node -> graph.getParent(node));
    }

    return layoutData;
  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GeneralGroup;

  @Label("Partition")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object CycleGroup;

  @Label("Edge Bundling")
  @OptionGroupAnnotation(name = "RootGroup", position = 25)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object EdgeBundlingGroup;

  @Label("Tree")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object TreeGroup;

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

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>The circular layout style emphasizes group and tree structures within a network. It creates node partitions " +
           "by analyzing the connectivity structure of the network, and arranges the partitions as separate circles. The circles " +
           "themselves are arranged in a radial tree layout fashion.</p>" +
           "<p>This layout style portraits interconnected ring and star topologies and is excellent for " +
           "applications in:</p>" +
           "<ul>" +
           "<li><p>Social networking (criminology, economics, fraud detection, etc.)</p></li>" +
           "<li><p>Network management</p></li>" +
           "<li><p>WWW visualization</p></li>" +
           "<li><p>eCommerce</p></li>" +
           "</ul>";
  }

  private LayoutStyle layoutStyleItem = LayoutStyle.BCC_COMPACT;

  @Label("Layout Style")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutStyle.class, stringValue = "BCC_COMPACT")
  @EnumValueAnnotation(label = "BCC Compact", value = "BCC_COMPACT")
  @EnumValueAnnotation(label = "BCC Isolated", value = "BCC_ISOLATED")
  @EnumValueAnnotation(label = "Custom Groups", value = "CUSTOM_GROUPS")
  @EnumValueAnnotation(label = "Single Cycle", value = "SINGLE_CYCLE")
  public final LayoutStyle getLayoutStyleItem() {
    return this.layoutStyleItem;
  }

  @Label("Layout Style")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutStyle.class, stringValue = "BCC_COMPACT")
  @EnumValueAnnotation(label = "BCC Compact", value = "BCC_COMPACT")
  @EnumValueAnnotation(label = "BCC Isolated", value = "BCC_ISOLATED")
  @EnumValueAnnotation(label = "Custom Groups", value = "CUSTOM_GROUPS")
  @EnumValueAnnotation(label = "Single Cycle", value = "SINGLE_CYCLE")
  public final void setLayoutStyleItem( LayoutStyle value ) {
    this.layoutStyleItem = value;
  }

  private boolean actingOnSelectionOnlyItem;

  @Label("Act on Selection Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isActingOnSelectionOnlyItem() {
    return this.actingOnSelectionOnlyItem;
  }

  @Label("Act on Selection Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setActingOnSelectionOnlyItem( boolean value ) {
    this.actingOnSelectionOnlyItem = value;
  }

  private boolean fromSketchModeEnabledItem;

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isFromSketchModeEnabledItem() {
    return this.fromSketchModeEnabledItem;
  }

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setFromSketchModeEnabledItem( boolean value ) {
    this.fromSketchModeEnabledItem = value;
  }

  private PartitionStyle partitionStyleItem = PartitionStyle.CYCLE;

  @Label("Partition Layout Style")
  @OptionGroupAnnotation(name = "CycleGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PartitionStyle.class, stringValue = "CYCLE")
  @EnumValueAnnotation(label = "Circle", value = "CYCLE")
  @EnumValueAnnotation(label = "Disk", value = "DISK")
  @EnumValueAnnotation(label = "Organic Disk", value = "ORGANIC")
  public final PartitionStyle getPartitionStyleItem() {
    return this.partitionStyleItem;
  }

  @Label("Partition Layout Style")
  @OptionGroupAnnotation(name = "CycleGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PartitionStyle.class, stringValue = "CYCLE")
  @EnumValueAnnotation(label = "Circle", value = "CYCLE")
  @EnumValueAnnotation(label = "Disk", value = "DISK")
  @EnumValueAnnotation(label = "Organic Disk", value = "ORGANIC")
  public final void setPartitionStyleItem( PartitionStyle value ) {
    this.partitionStyleItem = value;
  }

  private int minimumNodeDistanceItem;

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "CycleGroup", position = 20)
  @DefaultValue(intValue = 30, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 999)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumNodeDistanceItem() {
    return this.minimumNodeDistanceItem;
  }

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "CycleGroup", position = 20)
  @DefaultValue(intValue = 30, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 999)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeDistanceItem( int value ) {
    this.minimumNodeDistanceItem = value;
  }

  public final boolean isMinimumNodeDistanceItemDisabled() {
    return isChoosingRadiusAutomaticallyItem() == false;
  }

  private boolean choosingRadiusAutomaticallyItem;

  @Label("Choose Radius Automatically")
  @OptionGroupAnnotation(name = "CycleGroup", position = 30)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isChoosingRadiusAutomaticallyItem() {
    return this.choosingRadiusAutomaticallyItem;
  }

  @Label("Choose Radius Automatically")
  @OptionGroupAnnotation(name = "CycleGroup", position = 30)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setChoosingRadiusAutomaticallyItem( boolean value ) {
    this.choosingRadiusAutomaticallyItem = value;
  }

  private int fixedRadiusItem;

  @Label("Fixed Radius")
  @OptionGroupAnnotation(name = "CycleGroup", position = 40)
  @DefaultValue(intValue = 200, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 800)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getFixedRadiusItem() {
    return this.fixedRadiusItem;
  }

  @Label("Fixed Radius")
  @OptionGroupAnnotation(name = "CycleGroup", position = 40)
  @DefaultValue(intValue = 200, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 800)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setFixedRadiusItem( int value ) {
    this.fixedRadiusItem = value;
  }

  public final boolean isFixedRadiusItemDisabled() {
    return isChoosingRadiusAutomaticallyItem();
  }

  private boolean edgeBundlingEnabledItem;

  @Label("Enable Edge Bundling")
  @OptionGroupAnnotation(name = "EdgeBundlingGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isEdgeBundlingEnabledItem() {
    return this.edgeBundlingEnabledItem;
  }

  @Label("Enable Edge Bundling")
  @OptionGroupAnnotation(name = "EdgeBundlingGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setEdgeBundlingEnabledItem( boolean value ) {
    this.edgeBundlingEnabledItem = value;
  }

  public final boolean isEdgeBundlingEnabledItemDisabled() {
    return getPartitionStyleItem() != PartitionStyle.CYCLE || getLayoutStyleItem() == LayoutStyle.BCC_ISOLATED;
  }

  private double edgeBundlingStrengthItem;

  @Label("Bundling Strength")
  @OptionGroupAnnotation(name = "EdgeBundlingGroup", position = 50)
  @DefaultValue(doubleValue = 0.95d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getEdgeBundlingStrengthItem() {
    return this.edgeBundlingStrengthItem;
  }

  @Label("Bundling Strength")
  @OptionGroupAnnotation(name = "EdgeBundlingGroup", position = 50)
  @DefaultValue(doubleValue = 0.95d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setEdgeBundlingStrengthItem( double value ) {
    this.edgeBundlingStrengthItem = value;
  }

  public final boolean isEdgeBundlingStrengthItemDisabled() {
    return getPartitionStyleItem() != PartitionStyle.CYCLE || getLayoutStyleItem() == LayoutStyle.BCC_ISOLATED;
  }

  private int preferredChildWedgeItem;

  @Label("Preferred Child Wedge")
  @OptionGroupAnnotation(name = "TreeGroup", position = 10)
  @DefaultValue(intValue = 340, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 360)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getPreferredChildWedgeItem() {
    return this.preferredChildWedgeItem;
  }

  @Label("Preferred Child Wedge")
  @OptionGroupAnnotation(name = "TreeGroup", position = 10)
  @DefaultValue(intValue = 340, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 360)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setPreferredChildWedgeItem( int value ) {
    this.preferredChildWedgeItem = value;
  }

  private int minimumEdgeLengthItem;

  @Label("Minimum Edge Length")
  @OptionGroupAnnotation(name = "TreeGroup", position = 20)
  @DefaultValue(intValue = 40, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 5, max = 400)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumEdgeLengthItem() {
    return this.minimumEdgeLengthItem;
  }

  @Label("Minimum Edge Length")
  @OptionGroupAnnotation(name = "TreeGroup", position = 20)
  @DefaultValue(intValue = 40, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 5, max = 400)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumEdgeLengthItem( int value ) {
    this.minimumEdgeLengthItem = value;
  }

  private int maximumDeviationAngleItem;

  @Label("Maximum Deviation Angle")
  @OptionGroupAnnotation(name = "TreeGroup", position = 30)
  @DefaultValue(intValue = 90, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 360)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMaximumDeviationAngleItem() {
    return this.maximumDeviationAngleItem;
  }

  @Label("Maximum Deviation Angle")
  @OptionGroupAnnotation(name = "TreeGroup", position = 30)
  @DefaultValue(intValue = 90, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 360)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMaximumDeviationAngleItem( int value ) {
    this.maximumDeviationAngleItem = value;
  }

  private double compactnessFactorItem;

  @Label("Compactness Factor")
  @OptionGroupAnnotation(name = "TreeGroup", position = 40)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.1d, max = 0.9d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getCompactnessFactorItem() {
    return this.compactnessFactorItem;
  }

  @Label("Compactness Factor")
  @OptionGroupAnnotation(name = "TreeGroup", position = 40)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.1d, max = 0.9d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setCompactnessFactorItem( double value ) {
    this.compactnessFactorItem = value;
  }

  private int minimumTreeNodeDistanceItem;

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "TreeGroup", position = 50)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumTreeNodeDistanceItem() {
    return this.minimumTreeNodeDistanceItem;
  }

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "TreeGroup", position = 50)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumTreeNodeDistanceItem( int value ) {
    this.minimumTreeNodeDistanceItem = value;
  }

  private boolean allowingOverlapsItem;

  @Label("Allow Overlaps")
  @OptionGroupAnnotation(name = "TreeGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAllowingOverlapsItem() {
    return this.allowingOverlapsItem;
  }

  @Label("Allow Overlaps")
  @OptionGroupAnnotation(name = "TreeGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAllowingOverlapsItem( boolean value ) {
    this.allowingOverlapsItem = value;
  }

  private boolean placingChildrenOnCommonRadiusItem;

  @Label("Place Children on Common Radius")
  @OptionGroupAnnotation(name = "TreeGroup", position = 70)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isPlacingChildrenOnCommonRadiusItem() {
    return this.placingChildrenOnCommonRadiusItem;
  }

  @Label("Place Children on Common Radius")
  @OptionGroupAnnotation(name = "TreeGroup", position = 70)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setPlacingChildrenOnCommonRadiusItem( boolean value ) {
    this.placingChildrenOnCommonRadiusItem = value;
  }

  public final boolean isTreeGroupItemDisabled() {
    return getLayoutStyleItem() == LayoutStyle.SINGLE_CYCLE;
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

}
