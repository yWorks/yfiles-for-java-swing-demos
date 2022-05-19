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

import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.FixGroupLayoutStage;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.InterEdgeRoutingStyle;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.orthogonal.ChainLayoutStyle;
import com.yworks.yfiles.layout.orthogonal.CycleLayoutStyle;
import com.yworks.yfiles.layout.orthogonal.LayoutStyle;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayoutData;
import com.yworks.yfiles.layout.orthogonal.SubstructureOrientation;
import com.yworks.yfiles.layout.orthogonal.TreeLayoutStyle;
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
@Label("OrthogonalLayout")
public class OrthogonalLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public OrthogonalLayoutConfig() {
    setStyleItem(LayoutStyle.NORMAL);
    setGridSpacingItem(15);
    setEdgeLengthReductionEnabledItem(true);
    setUsingExistingDrawingAsSketchItem(false);
    setCrossingReductionItem(true);
    setPerceivedBendsPostprocessingItem(true);
    setUsingRandomizationItem(true);
    setUsingFaceMaximizationItem(false);

    setConsideringNodeLabelsItem(false);
    setEdgeLabelingItem(EnumEdgeLabeling.NONE);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10.0d);

    setMinimumFirstSegmentLengthItem(15.0d);
    setMinimumSegmentLengthItem(15.0d);
    setMinimumLastSegmentLengthItem(15.0d);
    setConsideringEdgeDirectionItem(false);

    setChainSubstructureStyleItem(ChainLayoutStyle.NONE);
    setChainSubstructureSizeItem(2);
    setCycleSubstructureStyleItem(CycleLayoutStyle.NONE);
    setCycleSubstructureSizeItem(4);
    setTreeSubstructureStyleItem(TreeLayoutStyle.NONE);
    setTreeSubstructureSizeItem(3);
    setTreeSubstructureOrientationItem(SubstructureOrientation.AUTO_DETECT);

    setGroupLayoutPolicyItem(EnumGroupPolicy.LAYOUT_GROUPS);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    OrthogonalLayout layout = new OrthogonalLayout();
    if (getGroupLayoutPolicyItem() == EnumGroupPolicy.FIX_GROUPS) {
      FixGroupLayoutStage fgl = new FixGroupLayoutStage();
      fgl.setInterEdgeRoutingStyle(InterEdgeRoutingStyle.ORTHOGONAL);
      layout.prependStage(fgl);
    } else if (getGroupLayoutPolicyItem() == EnumGroupPolicy.IGNORE_GROUPS) {
      layout.setHideGroupsStageEnabled(true);
    }

    layout.setLayoutStyle(getStyleItem());
    layout.setGridSpacing(getGridSpacingItem());
    layout.setEdgeLengthReductionEnabled(isEdgeLengthReductionEnabledItem());
    layout.setPerceivedBendOptimizationEnabled(isPerceivedBendsPostprocessingItem());
    layout.setUniformPortAssignmentEnabled(isUniformPortAssignmentEnabledItem());
    layout.setCrossingReductionEnabled(isCrossingReductionItem());
    layout.setRandomizationEnabled(isUsingRandomizationItem());
    layout.setFaceMaximizationEnabled(isUsingFaceMaximizationItem());
    layout.setFromSketchModeEnabled(isUsingExistingDrawingAsSketchItem());
    layout.getEdgeLayoutDescriptor().setMinimumFirstSegmentLength(getMinimumFirstSegmentLengthItem());
    layout.getEdgeLayoutDescriptor().setMinimumLastSegmentLength(getMinimumLastSegmentLengthItem());
    layout.getEdgeLayoutDescriptor().setMinimumSegmentLength(getMinimumSegmentLengthItem());

    //set edge labeling options
    boolean normalStyle = layout.getLayoutStyle() == LayoutStyle.NORMAL;
    layout.setIntegratedEdgeLabelingEnabled(getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED && normalStyle);
    layout.setNodeLabelConsiderationEnabled(isConsideringNodeLabelsItem() && normalStyle);

    if (getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC || (getEdgeLabelingItem() == EnumEdgeLabeling.INTEGRATED && normalStyle)) {
      layout.setLabelingEnabled(true);
      if (getEdgeLabelingItem() == EnumEdgeLabeling.GENERIC) {
        ((GenericLabeling)layout.getLabeling()).setAmbiguityReductionEnabled(isReducingAmbiguityItem());
      }
    } else if (!isConsideringNodeLabelsItem() || !normalStyle) {
      layout.setLabelingEnabled(false);
    }

    layout.setChainStyle(getChainSubstructureStyleItem());
    layout.setChainSize(getChainSubstructureSizeItem());
    layout.setCycleStyle(getCycleSubstructureStyleItem());
    layout.setCycleSize(getCycleSubstructureSizeItem());
    layout.setTreeStyle(getTreeSubstructureStyleItem());
    layout.setTreeSize(getTreeSubstructureSizeItem());
    layout.setTreeOrientation(getTreeSubstructureOrientationItem());

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    OrthogonalLayoutData layoutData = new OrthogonalLayoutData();
    if (isConsideringEdgeDirectionItem()) {
      layoutData.setDirectedEdges(graphComponent.getSelection().getSelectedEdges());
    } else {
      layoutData.setDirectedEdges(edge -> false);
    }

    return layoutData.combineWith(createLabelingLayoutData(graphComponent.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem()));
  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("Layout")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LayoutGroup;

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

  @Label("Edges")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object EdgesGroup;

  @Label("Grouping")
  @OptionGroupAnnotation(name = "RootGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GroupingGroup;

  @Label("Substructure Layout")
  @OptionGroupAnnotation(name = "RootGroup", position = 50)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object SubstructureLayoutGroup;

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

  public enum EnumGroupPolicy {
    LAYOUT_GROUPS(0),

    FIX_GROUPS(1),

    IGNORE_GROUPS(2);

    private final int value;

    private EnumGroupPolicy( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumGroupPolicy fromOrdinal( int ordinal ) {
      for (EnumGroupPolicy current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>The orthogonal layout style is a multi-purpose layout style for undirected graphs. " +
           "It is well suited for medium-sized sparse graphs, and produces compact drawings with no overlaps, " +
           "few crossings, and few bends.</p>" +
           "<p>It is especially fitted for application domains such as</p>" +
           "<ul>" +
           "<li><p>Software engineering</p></li>" +
           "<li><p>Database schema</p></li>" +
           "<li><p>System management</p></li>" +
           "<li><p>Knowledge representation</p></li>" +
           "</ul>";
  }

  private LayoutStyle styleItem = LayoutStyle.NORMAL;

  @Label("Style")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutStyle.class, stringValue = "NORMAL")
  @EnumValueAnnotation(label = "Normal", value = "NORMAL")
  @EnumValueAnnotation(label = "Uniform Node Sizes", value = "UNIFORM")
  @EnumValueAnnotation(label = "Node Boxes", value = "BOX")
  @EnumValueAnnotation(label = "Mixed", value = "MIXED")
  @EnumValueAnnotation(label = "Node Boxes (Fixed Node Size)", value = "FIXED_BOX")
  @EnumValueAnnotation(label = "Mixed (Fixed Node Size)", value = "FIXED_MIXED")
  public final LayoutStyle getStyleItem() {
    return this.styleItem;
  }

  @Label("Style")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutStyle.class, stringValue = "NORMAL")
  @EnumValueAnnotation(label = "Normal", value = "NORMAL")
  @EnumValueAnnotation(label = "Uniform Node Sizes", value = "UNIFORM")
  @EnumValueAnnotation(label = "Node Boxes", value = "BOX")
  @EnumValueAnnotation(label = "Mixed", value = "MIXED")
  @EnumValueAnnotation(label = "Node Boxes (Fixed Node Size)", value = "FIXED_BOX")
  @EnumValueAnnotation(label = "Mixed (Fixed Node Size)", value = "FIXED_MIXED")
  public final void setStyleItem( LayoutStyle value ) {
    this.styleItem = value;
  }

  public final boolean isStyleItemDisabled() {
    return isUsingExistingDrawingAsSketchItem();
  }

  private int gridSpacingItem;

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(intValue = 15, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getGridSpacingItem() {
    return this.gridSpacingItem;
  }

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(intValue = 15, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setGridSpacingItem( int value ) {
    this.gridSpacingItem = value;
  }

  private boolean edgeLengthReductionEnabledItem;

  @Label("Edge Length Reduction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isEdgeLengthReductionEnabledItem() {
    return this.edgeLengthReductionEnabledItem;
  }

  @Label("Edge Length Reduction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setEdgeLengthReductionEnabledItem( boolean value ) {
    this.edgeLengthReductionEnabledItem = value;
  }

  private boolean usingExistingDrawingAsSketchItem;

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUsingExistingDrawingAsSketchItem() {
    return this.usingExistingDrawingAsSketchItem;
  }

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUsingExistingDrawingAsSketchItem( boolean value ) {
    this.usingExistingDrawingAsSketchItem = value;
  }

  private boolean crossingReductionItem;

  @Label("Crossing Reduction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isCrossingReductionItem() {
    return this.crossingReductionItem;
  }

  @Label("Crossing Reduction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setCrossingReductionItem( boolean value ) {
    this.crossingReductionItem = value;
  }

  public final boolean isCrossingReductionItemDisabled() {
    return isUsingExistingDrawingAsSketchItem();
  }

  private boolean perceivedBendsPostprocessingItem;

  @Label("Minimize Perceived Bends")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isPerceivedBendsPostprocessingItem() {
    return this.perceivedBendsPostprocessingItem;
  }

  @Label("Minimize Perceived Bends")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setPerceivedBendsPostprocessingItem( boolean value ) {
    this.perceivedBendsPostprocessingItem = value;
  }

  public final boolean isPerceivedBendsPostprocessingItemDisabled() {
    return isUsingExistingDrawingAsSketchItem();
  }

  private boolean uniformPortAssignmentEnabledItem;

  @Label("Uniform Port Assignment")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 65)
  public final boolean isUniformPortAssignmentEnabledItem() {
    return this.uniformPortAssignmentEnabledItem;
  }

  @Label("Uniform Port Assignment")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 65)
  public final void setUniformPortAssignmentEnabledItem( boolean value ) {
    this.uniformPortAssignmentEnabledItem = value;
  }

  private boolean usingRandomizationItem;

  @Label("Randomization")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUsingRandomizationItem() {
    return this.usingRandomizationItem;
  }

  @Label("Randomization")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUsingRandomizationItem( boolean value ) {
    this.usingRandomizationItem = value;
  }

  public final boolean isUsingRandomizationItemDisabled() {
    return isUsingExistingDrawingAsSketchItem();
  }

  private boolean usingFaceMaximizationItem;

  @Label("Face Maximization")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 80)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUsingFaceMaximizationItem() {
    return this.usingFaceMaximizationItem;
  }

  @Label("Face Maximization")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 80)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUsingFaceMaximizationItem( boolean value ) {
    this.usingFaceMaximizationItem = value;
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

  private double minimumFirstSegmentLengthItem;

  @Label("Minimum First Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 10)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumFirstSegmentLengthItem() {
    return this.minimumFirstSegmentLengthItem;
  }

  @Label("Minimum First Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 10)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumFirstSegmentLengthItem( double value ) {
    this.minimumFirstSegmentLengthItem = value;
  }

  private double minimumSegmentLengthItem;

  @Label("Minimum Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 20)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumSegmentLengthItem() {
    return this.minimumSegmentLengthItem;
  }

  @Label("Minimum Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 20)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumSegmentLengthItem( double value ) {
    this.minimumSegmentLengthItem = value;
  }

  private double minimumLastSegmentLengthItem;

  @Label("Minimum Last Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 30)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumLastSegmentLengthItem() {
    return this.minimumLastSegmentLengthItem;
  }

  @Label("Minimum Last Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 30)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumLastSegmentLengthItem( double value ) {
    this.minimumLastSegmentLengthItem = value;
  }

  private boolean consideringEdgeDirectionItem;

  @Label("Route selected Edges Downwards")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsideringEdgeDirectionItem() {
    return this.consideringEdgeDirectionItem;
  }

  @Label("Route selected Edges Downwards")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsideringEdgeDirectionItem( boolean value ) {
    this.consideringEdgeDirectionItem = value;
  }

  private EnumGroupPolicy groupLayoutPolicyItem = EnumGroupPolicy.LAYOUT_GROUPS;

  @Label("Group Layout Policy")
  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumGroupPolicy.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Fix Contents of Groups", value = "FIX_GROUPS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final EnumGroupPolicy getGroupLayoutPolicyItem() {
    return this.groupLayoutPolicyItem;
  }

  @Label("Group Layout Policy")
  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumGroupPolicy.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Fix Contents of Groups", value = "FIX_GROUPS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final void setGroupLayoutPolicyItem( EnumGroupPolicy value ) {
    this.groupLayoutPolicyItem = value;
  }

  private CycleLayoutStyle cycleSubstructureStyleItem = CycleLayoutStyle.NONE;

  @Label("Cycles")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 10)
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Circular with Nodes at Corners", value = "CIRCULAR_WITH_NODES_AT_CORNERS")
  @EnumValueAnnotation(label = "Circular with Bends at Corners", value = "CIRCULAR_WITH_BENDS_AT_CORNERS")
  public final CycleLayoutStyle getCycleSubstructureStyleItem() {
    return this.cycleSubstructureStyleItem;
  }

  @Label("Cycles")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 10)
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Circular with Nodes at Corners", value = "CIRCULAR_WITH_NODES_AT_CORNERS")
  @EnumValueAnnotation(label = "Circular with Bends at Corners", value = "CIRCULAR_WITH_BENDS_AT_CORNERS")
  public final void setCycleSubstructureStyleItem( CycleLayoutStyle value ) {
    this.cycleSubstructureStyleItem = value;
  }

  private int cycleSubstructureSizeItem;

  @Label("Minimum Cycle Size")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 20)
  @MinMax(min = 4, max = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getCycleSubstructureSizeItem() {
    return this.cycleSubstructureSizeItem;
  }

  @Label("Minimum Cycle Size")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 20)
  @MinMax(min = 4, max = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setCycleSubstructureSizeItem( int value ) {
    this.cycleSubstructureSizeItem = value;
  }

  public final boolean isCycleSubstructureSizeItemDisabled() {
    return getCycleSubstructureStyleItem() == CycleLayoutStyle.NONE;
  }

  private ChainLayoutStyle chainSubstructureStyleItem = ChainLayoutStyle.NONE;

  @Label("Chains")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 30)
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Straight", value = "STRAIGHT")
  @EnumValueAnnotation(label = "Wrapped with Nodes at Turns", value = "WRAPPED_WITH_NODES_AT_TURNS")
  @EnumValueAnnotation(label = "Wrapped with Bends at Turns", value = "WRAPPED_WITH_BENDS_AT_TURNS")
  public final ChainLayoutStyle getChainSubstructureStyleItem() {
    return this.chainSubstructureStyleItem;
  }

  @Label("Chains")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 30)
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Straight", value = "STRAIGHT")
  @EnumValueAnnotation(label = "Wrapped with Nodes at Turns", value = "WRAPPED_WITH_NODES_AT_TURNS")
  @EnumValueAnnotation(label = "Wrapped with Bends at Turns", value = "WRAPPED_WITH_BENDS_AT_TURNS")
  public final void setChainSubstructureStyleItem( ChainLayoutStyle value ) {
    this.chainSubstructureStyleItem = value;
  }

  private int chainSubstructureSizeItem;

  @Label("Minimum Chain Length")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 40)
  @MinMax(min = 2, max = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getChainSubstructureSizeItem() {
    return this.chainSubstructureSizeItem;
  }

  @Label("Minimum Chain Length")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 40)
  @MinMax(min = 2, max = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setChainSubstructureSizeItem( int value ) {
    this.chainSubstructureSizeItem = value;
  }

  public final boolean isChainSubstructureSizeItemDisabled() {
    return getChainSubstructureStyleItem() == ChainLayoutStyle.NONE;
  }

  private TreeLayoutStyle treeSubstructureStyleItem = TreeLayoutStyle.NONE;

  @Label("Tree Style")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 50)
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Default", value = "DEFAULT")
  @EnumValueAnnotation(label = "Integrated", value = "INTEGRATED")
  @EnumValueAnnotation(label = "Compact", value = "COMPACT")
  @EnumValueAnnotation(label = "Aspect Ratio", value = "ASPECT_RATIO")
  public final TreeLayoutStyle getTreeSubstructureStyleItem() {
    return this.treeSubstructureStyleItem;
  }

  @Label("Tree Style")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 50)
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Default", value = "DEFAULT")
  @EnumValueAnnotation(label = "Integrated", value = "INTEGRATED")
  @EnumValueAnnotation(label = "Compact", value = "COMPACT")
  @EnumValueAnnotation(label = "Aspect Ratio", value = "ASPECT_RATIO")
  public final void setTreeSubstructureStyleItem( TreeLayoutStyle value ) {
    this.treeSubstructureStyleItem = value;
  }

  private int treeSubstructureSizeItem;

  @Label("Minimum Tree Size")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 60)
  @MinMax(min = 3, max = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getTreeSubstructureSizeItem() {
    return this.treeSubstructureSizeItem;
  }

  @Label("Minimum Tree Size")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 60)
  @MinMax(min = 3, max = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setTreeSubstructureSizeItem( int value ) {
    this.treeSubstructureSizeItem = value;
  }

  public final boolean isTreeSubstructureSizeItemDisabled() {
    return getTreeSubstructureStyleItem() == TreeLayoutStyle.NONE;
  }

  private SubstructureOrientation treeSubstructureOrientationItem = SubstructureOrientation.TOP_TO_BOTTOM;

  @Label("Tree Orientation")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 70)
  @EnumValueAnnotation(label = "Automatic", value = "AUTO_DETECT")
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public final SubstructureOrientation getTreeSubstructureOrientationItem() {
    return this.treeSubstructureOrientationItem;
  }

  @Label("Tree Orientation")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 70)
  @EnumValueAnnotation(label = "Automatic", value = "AUTO_DETECT")
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public final void setTreeSubstructureOrientationItem( SubstructureOrientation value ) {
    this.treeSubstructureOrientationItem = value;
  }

  public final boolean isTreeSubstructureOrientationItemDisabled() {
    return getTreeSubstructureStyleItem() == TreeLayoutStyle.NONE;
  }

  public final void enableSubstructures() {
    setChainSubstructureStyleItem(ChainLayoutStyle.WRAPPED_WITH_NODES_AT_TURNS);
    setChainSubstructureSizeItem(2);
    setCycleSubstructureStyleItem(CycleLayoutStyle.CIRCULAR_WITH_BENDS_AT_CORNERS);
    setCycleSubstructureSizeItem(4);
    setTreeSubstructureStyleItem(TreeLayoutStyle.INTEGRATED);
    setTreeSubstructureSizeItem(3);
    setTreeSubstructureOrientationItem(SubstructureOrientation.AUTO_DETECT);
  }

}
