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

import com.yworks.yfiles.algorithms.ILabelLayoutDpKey;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterFinder;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.SimpleProfitModel;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.labeling.LabelingData;
import com.yworks.yfiles.layout.labeling.OptimizationStrategy;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IGraphSelection;
import toolkit.optionhandler.ComponentType;
import toolkit.optionhandler.ComponentTypes;
import toolkit.optionhandler.EnumValueAnnotation;
import toolkit.optionhandler.Label;
import toolkit.optionhandler.MinMax;
import toolkit.optionhandler.OptionGroupAnnotation;

/**
 * Configuration options for the {@link GenericLabeling} algorithm.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
@Label("Labeling")
public class LabelingConfig extends LayoutConfiguration {
  private static final ILabelLayoutDpKey<Boolean> SELECTED_LABELS_KEY = new ILabelLayoutDpKey<Boolean>(Boolean.class, null, "SelectedLabels");

  /**
   * Setup default values for various configuration parameters.
   */
  public LabelingConfig() {
    setPlacingNodeLabelsItem(true);
    setPlacingEdgeLabelsItem(true);
    setConsideringSelectedFeaturesOnlyItem(false);

    setOptimizationStrategyItem(OptimizationStrategy.BALANCED);

    setAllowingNodeOverlapsItem(false);
    setAllowingEdgeOverlapsItem(true);
    setReducingAmbiguityItem(true);

    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    GenericLabeling labeling = new GenericLabeling();

    labeling.setAutoFlippingEnabled(true);
    labeling.setOptimizationStrategy(getOptimizationStrategyItem());
    if (labeling.getOptimizationStrategy() == OptimizationStrategy.NONE) {
      labeling.setProfitModel(new SimpleProfitModel());
    }

    labeling.setNodeOverlapsRemovalEnabled(!isAllowingNodeOverlapsItem());
    labeling.setEdgeOverlapsRemovalEnabled(!isAllowingEdgeOverlapsItem());
    labeling.setEdgeLabelPlacementEnabled(isPlacingEdgeLabelsItem());
    labeling.setNodeLabelPlacementEnabled(isPlacingNodeLabelsItem());
    labeling.setAmbiguityReductionEnabled(isReducingAmbiguityItem());

    boolean selectionOnly = isConsideringSelectedFeaturesOnlyItem();
    labeling.setAffectedLabelsDpKey(null);

    if (graphComponent.getSelection() != null && selectionOnly) {
      labeling.setAffectedLabelsDpKey(SELECTED_LABELS_KEY);
    }

    return labeling;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    LabelingData layoutData = new LabelingData();

    final IGraphSelection selection = graphComponent.getSelection();
    if (selection != null) {
      layoutData.getAffectedLabels().setDpKey(SELECTED_LABELS_KEY);
      layoutData.setAffectedLabels(label -> 
         selection.isSelected(label) || selection.isSelected(label.getOwner())
      );
    }

    if (isPlacingEdgeLabelsItem()) {
      setupEdgeLabelModels(graphComponent);
      return layoutData.combineWith(createLabelingLayoutData(graphComponent.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem()));
    }

    return layoutData;
  }

  private void setupEdgeLabelModels( GraphComponent graphComponent ) {

    FreeEdgeLabelModel model = new FreeEdgeLabelModel();

    boolean selectionOnly = isConsideringSelectedFeaturesOnlyItem();
    boolean placeEdgeLabels = isPlacingEdgeLabelsItem();
    if (!placeEdgeLabels) {
      return;
    }

    ILabelModelParameterFinder parameterFinder = model.lookup(ILabelModelParameterFinder.class);
    IGraph graph = graphComponent.getGraph();
    for (ILabel label : graph.getEdgeLabels()) {
      if (selectionOnly) {
        if (graphComponent.getSelection().isSelected(label)) {
          graph.setLabelLayoutParameter(label, parameterFinder.findBestParameter(label, model, label.getLayout()));
        }
      } else {
        graph.setLabelLayoutParameter(label, parameterFinder.findBestParameter(label, model, label.getLayout()));
      }
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

  @Label("Quality")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object QualityGroup;

  @Label("Preferred Edge Label Placement")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object PreferredPlacementGroup;

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>This algorithm finds good positions for the labels of nodes and edges. " +
           "Typically, a label should be placed near the item it belongs to and it should not overlap with other labels. " +
           "Optionally, overlaps with nodes and edges can be avoided as well.</p>";
  }

  private boolean placingNodeLabelsItem;

  @Label("Place Node Labels")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isPlacingNodeLabelsItem() {
    return this.placingNodeLabelsItem;
  }

  @Label("Place Node Labels")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setPlacingNodeLabelsItem( boolean value ) {
    this.placingNodeLabelsItem = value;
  }

  private boolean placingEdgeLabelsItem;

  @Label("Place Edge Labels")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isPlacingEdgeLabelsItem() {
    return this.placingEdgeLabelsItem;
  }

  @Label("Place Edge Labels")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setPlacingEdgeLabelsItem( boolean value ) {
    this.placingEdgeLabelsItem = value;
  }

  private boolean consideringSelectedFeaturesOnlyItem;

  @Label("Consider Selected Features Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsideringSelectedFeaturesOnlyItem() {
    return this.consideringSelectedFeaturesOnlyItem;
  }

  @Label("Consider Selected Features Only")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsideringSelectedFeaturesOnlyItem( boolean value ) {
    this.consideringSelectedFeaturesOnlyItem = value;
  }

  private boolean allowingNodeOverlapsItem;

  @Label("Allow Node Overlaps")
  @OptionGroupAnnotation(name = "QualityGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAllowingNodeOverlapsItem() {
    return this.allowingNodeOverlapsItem;
  }

  @Label("Allow Node Overlaps")
  @OptionGroupAnnotation(name = "QualityGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAllowingNodeOverlapsItem( boolean value ) {
    this.allowingNodeOverlapsItem = value;
  }

  private boolean allowingEdgeOverlapsItem;

  @Label("Allow Edge Overlaps")
  @OptionGroupAnnotation(name = "QualityGroup", position = 20)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAllowingEdgeOverlapsItem() {
    return this.allowingEdgeOverlapsItem;
  }

  @Label("Allow Edge Overlaps")
  @OptionGroupAnnotation(name = "QualityGroup", position = 20)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAllowingEdgeOverlapsItem( boolean value ) {
    this.allowingEdgeOverlapsItem = value;
  }

  private OptimizationStrategy optimizationStrategyItem = OptimizationStrategy.BALANCED;

  @Label("Reduce overlaps")
  @OptionGroupAnnotation(name = "QualityGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = OptimizationStrategy.class, stringValue = "BALANCED")
  @EnumValueAnnotation(label = "Balanced", value = "BALANCED")
  @EnumValueAnnotation(label = "With Nodes", value = "NODE_OVERLAP")
  @EnumValueAnnotation(label = "Between Labels", value = "LABEL_OVERLAP")
  @EnumValueAnnotation(label = "With Edges", value = "EDGE_OVERLAP")
  @EnumValueAnnotation(label = "Partition Grid Overlap", value = "PARTITION_GRID_OVERLAP")
  @EnumValueAnnotation(label = "Don't optimize", value = "NONE")
  public final OptimizationStrategy getOptimizationStrategyItem() {
    return this.optimizationStrategyItem;
  }

  @Label("Reduce overlaps")
  @OptionGroupAnnotation(name = "QualityGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = OptimizationStrategy.class, stringValue = "BALANCED")
  @EnumValueAnnotation(label = "Balanced", value = "BALANCED")
  @EnumValueAnnotation(label = "With Nodes", value = "NODE_OVERLAP")
  @EnumValueAnnotation(label = "Between Labels", value = "LABEL_OVERLAP")
  @EnumValueAnnotation(label = "With Edges", value = "EDGE_OVERLAP")
  @EnumValueAnnotation(label = "Partition Grid Overlap", value = "PARTITION_GRID_OVERLAP")
  @EnumValueAnnotation(label = "Don't optimize", value = "NONE")
  public final void setOptimizationStrategyItem( OptimizationStrategy value ) {
    this.optimizationStrategyItem = value;
  }

  private boolean reducingAmbiguityItem;

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "QualityGroup", position = 50)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isReducingAmbiguityItem() {
    return this.reducingAmbiguityItem;
  }

  @Label("Reduce Ambiguity")
  @OptionGroupAnnotation(name = "QualityGroup", position = 50)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setReducingAmbiguityItem( boolean value ) {
    this.reducingAmbiguityItem = value;
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
    return getLabelPlacementSideOfEdgeItem() == LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE;
  }

}
