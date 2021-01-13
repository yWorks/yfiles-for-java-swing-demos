/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.algorithms.YDimension;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IMapperRegistry;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IArrowOwner;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.ComponentArrangementStyles;
import com.yworks.yfiles.layout.ComponentLayout;
import com.yworks.yfiles.layout.HideGroupsStage;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.organic.ChainSubstructureStyle;
import com.yworks.yfiles.layout.organic.CycleSubstructureStyle;
import com.yworks.yfiles.layout.organic.GroupNodeMode;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayoutData;
import com.yworks.yfiles.layout.organic.OutputRestriction;
import com.yworks.yfiles.layout.organic.ParallelSubstructureStyle;
import com.yworks.yfiles.layout.organic.Scope;
import com.yworks.yfiles.layout.organic.StarSubstructureStyle;
import com.yworks.yfiles.layout.PortConstraintKeys;
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
@Label("OrganicLayout")
public class OrganicLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public OrganicLayoutConfig() {
    OrganicLayout layout = new OrganicLayout();
    setScopeItem(Scope.ALL);
    setPreferredEdgeLengthItem(layout.getPreferredEdgeLength());
    setAllowingNodeOverlapsItem(layout.isNodeOverlapsAllowed());
    setMinimumNodeDistanceItem(10);
    setAvoidingNodeEdgeOverlapsItem(layout.isAvoidingNodeEdgeOverlapsEnabled());
    setCompactnessItem(layout.getCompactnessFactor());
    setUsingAutoClusteringItem(layout.isNodeClusteringEnabled());
    setAutoClusteringQualityItem(layout.getClusteringQuality());

    setRestrictOutputItem(EnumOutputRestrictions.NONE);
    setRectCageUsingViewItem(true);
    setCageXItem(0);
    setCageYItem(0);
    setCageWidthItem(1000);
    setCageHeightItem(1000);
    setArCageUsingViewItem(true);
    setCageRatioItem(1);

    setGroupLayoutPolicyItem(EnumGroupLayoutPolicy.LAYOUT_GROUPS);

    setQualityTimeRatioItem(layout.getQualityTimeRatio());
    setMaximumDurationItem((int)(layout.getMaximumDuration() / 1000));
    setDeterministicModeEnabledItem(layout.isDeterministicModeEnabled());

    setCycleSubstructureStyleItem(CycleSubstructureStyle.NONE);
    setChainSubstructureStyleItem(ChainSubstructureStyle.NONE);
    setStarSubstructureStyleItem(StarSubstructureStyle.NONE);
    setParallelSubstructureStyleItem(ParallelSubstructureStyle.NONE);

    setConsideringNodeLabelsItem(layout.isNodeLabelConsiderationEnabled());
    setEdgeLabelingEnabledItem(false);
    setLabelPlacementAlongEdgeItem(LayoutConfiguration.EnumLabelPlacementAlongEdge.CENTERED);
    setLabelPlacementSideOfEdgeItem(LayoutConfiguration.EnumLabelPlacementSideOfEdge.ON_EDGE);
    setLabelPlacementOrientationItem(LayoutConfiguration.EnumLabelPlacementOrientation.HORIZONTAL);
    setLabelPlacementDistanceItem(10);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    OrganicLayout layout = new OrganicLayout();
    layout.setPreferredEdgeLength(getPreferredEdgeLengthItem());
    layout.setNodeLabelConsiderationEnabled(isConsideringNodeLabelsItem());
    layout.setNodeOverlapsAllowed(isAllowingNodeOverlapsItem());
    layout.setMinimumNodeDistance(getMinimumNodeDistanceItem());
    layout.setScope(getScopeItem());
    layout.setCompactnessFactor(getCompactnessItem());
    layout.setNodeSizeConsiderationEnabled(true);
    layout.setNodeClusteringEnabled(isUsingAutoClusteringItem());
    layout.setClusteringQuality(getAutoClusteringQualityItem());
    layout.setAvoidingNodeEdgeOverlapsEnabled(isAvoidingNodeEdgeOverlapsItem());
    layout.setDeterministicModeEnabled(isDeterministicModeEnabledItem());
    layout.setMaximumDuration(1000 * getMaximumDurationItem());
    layout.setQualityTimeRatio(getQualityTimeRatioItem());

    if (isEdgeLabelingEnabledItem()) {
      GenericLabeling genericLabeling = new GenericLabeling();
      genericLabeling.setEdgeLabelPlacementEnabled(true);
      genericLabeling.setNodeLabelPlacementEnabled(false);
      genericLabeling.setAmbiguityReductionEnabled(isReducingAmbiguityItem());
      layout.setLabelingEnabled(true);
      layout.setLabeling(genericLabeling);
    }
    ((ComponentLayout)layout.getComponentLayout()).setStyle(ComponentArrangementStyles.MULTI_ROWS);

    configureOutputRestrictions(graphComponent, layout);

    layout.setChainSubstructureStyle(getChainSubstructureStyleItem());
    layout.setCycleSubstructureStyle(getCycleSubstructureStyleItem());
    layout.setStarSubstructureStyle(getStarSubstructureStyleItem());
    layout.setParallelSubstructureStyle(getParallelSubstructureStyleItem());

    if (isUsingEdgeGroupingItem()) {
      graphComponent.getGraph().getMapperRegistry().createConstantMapper(IEdge.class, Object.class, PortConstraintKeys.SOURCE_GROUP_ID_DPKEY, "Group");
      graphComponent.getGraph().getMapperRegistry().createConstantMapper(IEdge.class, Object.class, PortConstraintKeys.TARGET_GROUP_ID_DPKEY, "Group");
    }

    addPreferredPlacementDescriptor(graphComponent.getGraph(), getLabelPlacementAlongEdgeItem(), getLabelPlacementSideOfEdgeItem(), getLabelPlacementOrientationItem(), getLabelPlacementDistanceItem());

    if (getGroupLayoutPolicyItem() == EnumGroupLayoutPolicy.IGNORE_GROUPS) {
      layout.prependStage(new HideGroupsStage());
    }

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( final GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    OrganicLayoutData layoutData = new OrganicLayoutData();

    switch (getGroupLayoutPolicyItem()) {
      case FIX_GROUP_BOUNDS:
        layoutData.setGroupNodeModes(node -> graphComponent.getGraph().isGroupNode(node) ? GroupNodeMode.FIX_BOUNDS : GroupNodeMode.NORMAL);
        break;
      case FIX_GROUP_CONTENTS:
        layoutData.setGroupNodeModes(node -> graphComponent.getGraph().isGroupNode(node) ? GroupNodeMode.FIX_CONTENTS : GroupNodeMode.NORMAL);
        break;
    }

    layoutData.setAffectedNodes(graphComponent.getSelection().getSelectedNodes());

    if (isEdgeDirectednessEnabledItem()) {
      layoutData.setEdgeDirectedness(edge -> {
        IEdgeStyle style = edge.getStyle();
        return style instanceof IArrowOwner && IArrow.NONE != ((IArrowOwner) style).getTargetArrow() ? 1.0 : 0.0;
      });
    }

    return layoutData;
  }

  /**
   * Called after the layout animation is done.
   */
  @Override
  protected void postProcess( GraphComponent graphComponent ) {
    if (isUsingEdgeGroupingItem()) {
      IMapperRegistry mapperRegistry = graphComponent.getGraph().getMapperRegistry();
      mapperRegistry.removeMapper(PortConstraintKeys.SOURCE_GROUP_ID_DPKEY);
      mapperRegistry.removeMapper(PortConstraintKeys.TARGET_GROUP_ID_DPKEY);
    }
  }

  public final void enableSubstructures() {
    setCycleSubstructureStyleItem(CycleSubstructureStyle.CIRCULAR);
    setChainSubstructureStyleItem(ChainSubstructureStyle.STRAIGHT_LINE);
    setStarSubstructureStyleItem(StarSubstructureStyle.RADIAL);
    setParallelSubstructureStyleItem(ParallelSubstructureStyle.STRAIGHT_LINE);
  }

  private void configureOutputRestrictions( GraphComponent graphComponent, OrganicLayout layout ) {
    boolean viewInfoIsAvailable = false;
    double[] visibleRect = getVisibleRectangle(graphComponent);
    double x = 0, y = 0, w = 0, h = 0;
    if (visibleRect != null) {
      viewInfoIsAvailable = true;
      x = visibleRect[0];
      y = visibleRect[1];
      w = visibleRect[2];
      h = visibleRect[3];
    }
    switch (getRestrictOutputItem()) {
      case NONE:
        layout.setComponentLayoutEnabled(true);
        layout.setOutputRestriction(OutputRestriction.NONE);
        break;
      case OUTPUT_CAGE:
        if (!viewInfoIsAvailable || !isRectCageUsingViewItem()) {
          x = getCageXItem();
          y = getCageYItem();
          w = getCageWidthItem();
          h = getCageHeightItem();
        }
        layout.setOutputRestriction(OutputRestriction.createRectangularCageRestriction(x, y, w, h));
        layout.setComponentLayoutEnabled(false);
        break;
      case OUTPUT_AR:
        double ratio;
        if (viewInfoIsAvailable && isArCageUsingViewItem()) {
          ratio = w / h;
        } else {
          ratio = getCageRatioItem();
        }
        layout.setOutputRestriction(OutputRestriction.createAspectRatioRestriction(ratio));
        layout.setComponentLayoutEnabled(true);
        ((ComponentLayout)layout.getComponentLayout()).setPreferredSize(new YDimension(ratio * 100, 100));
        break;
      case OUTPUT_ELLIPTICAL_CAGE:
        if (!viewInfoIsAvailable || !isRectCageUsingViewItem()) {
          x = getCageXItem();
          y = getCageYItem();
          w = getCageWidthItem();
          h = getCageHeightItem();
        }
        layout.setOutputRestriction(OutputRestriction.createEllipticalCageRestriction(x, y, w, h));
        layout.setComponentLayoutEnabled(false);
        break;
    }
  }

  private static double[] getVisibleRectangle( GraphComponent graphComponent ) {
    double[] visibleRect = new double[4];
    if (graphComponent != null) {
      RectD viewPort = graphComponent.getViewport();
      visibleRect[0] = viewPort.x;
      visibleRect[1] = viewPort.y;
      visibleRect[2] = viewPort.width;
      visibleRect[3] = viewPort.height;
      return visibleRect;
    }
    return null;
  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object VisualGroup;

  @Label("Restrictions")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object RestrictionsGroup;

  @Label("Bounds")
  @OptionGroupAnnotation(name = "RestrictionsGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object CageGroup;

  @Label("Aspect Ratio")
  @OptionGroupAnnotation(name = "RestrictionsGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object ARGroup;

  @Label("Grouping")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object GroupingGroup;

  @Label("Algorithm")
  @OptionGroupAnnotation(name = "RootGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object AlgorithmGroup;

  @Label("Substructure Layout")
  @OptionGroupAnnotation(name = "RootGroup", position = 50)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object SubstructureLayoutGroup;

  @Label("Labeling")
  @OptionGroupAnnotation(name = "RootGroup", position = 60)
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

  public enum EnumOutputRestrictions {
    NONE(0),

    OUTPUT_CAGE(1),

    OUTPUT_AR(2),

    OUTPUT_ELLIPTICAL_CAGE(3);

    private final int value;

    private EnumOutputRestrictions( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumOutputRestrictions fromOrdinal( int ordinal ) {
      for (EnumOutputRestrictions current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  public enum EnumGroupLayoutPolicy {
    LAYOUT_GROUPS(0),

    FIX_GROUP_BOUNDS(1),

    FIX_GROUP_CONTENTS(2),

    IGNORE_GROUPS(3);

    private final int value;

    private EnumGroupLayoutPolicy( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumGroupLayoutPolicy fromOrdinal( int ordinal ) {
      for (EnumGroupLayoutPolicy current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>The organic layout style is based on the force-directed layout paradigm. This algorithm simulates physical forces " +
           "and rearranges the positions of the nodes in such a way that the sum of the forces emitted by the nodes and the edges " +
           "reaches a (local) minimum.</p>" +
           "<p>This style is well suited for the visualization of highly connected backbone regions with attached peripheral " +
           "ring or tree structures. In a diagram arranged by this algorithm, these regions of a network can be easily identified.</p>" +
           "<p>The organic layout style is a multi-purpose layout for undirected graphs. It produces clear representations of " +
           "complex networks and is especially fitted for application domains such as:</p>" +
           "<ul>" +
           "<li><p>Bioinformatics</p></li>" +
           "<li><p>Enterprise networking</p></li>" +
           "<li><p>Knowledge representation</p></li>" +
           "<li><p>System management</p></li>" +
           "<li><p>WWW visualization</p></li>" +
           "<li><p>Mesh visualization</p></li>" +
           "</ul>";
  }

  private Scope scopeItem = Scope.ALL;

  @Label("Scope")
  @OptionGroupAnnotation(name = "VisualGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = Scope.class, stringValue = "ALL")
  @EnumValueAnnotation(label = "All", value = "ALL")
  @EnumValueAnnotation(label = "Mainly Selection", value = "MAINLY_SUBSET")
  @EnumValueAnnotation(label = "Selection", value = "SUBSET")
  public final Scope getScopeItem() {
    return this.scopeItem;
  }

  @Label("Scope")
  @OptionGroupAnnotation(name = "VisualGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = Scope.class, stringValue = "ALL")
  @EnumValueAnnotation(label = "All", value = "ALL")
  @EnumValueAnnotation(label = "Mainly Selection", value = "MAINLY_SUBSET")
  @EnumValueAnnotation(label = "Selection", value = "SUBSET")
  public final void setScopeItem( Scope value ) {
    this.scopeItem = value;
  }

  private double preferredEdgeLengthItem;

  @Label("Preferred Edge Length")
  @OptionGroupAnnotation(name = "VisualGroup", position = 20)
  @DefaultValue(doubleValue = 40.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 5, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getPreferredEdgeLengthItem() {
    return this.preferredEdgeLengthItem;
  }

  @Label("Preferred Edge Length")
  @OptionGroupAnnotation(name = "VisualGroup", position = 20)
  @DefaultValue(doubleValue = 40.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 5, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setPreferredEdgeLengthItem( double value ) {
    this.preferredEdgeLengthItem = value;
  }

  private boolean allowingNodeOverlapsItem;

  @Label("Allow Overlapping Nodes")
  @OptionGroupAnnotation(name = "VisualGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAllowingNodeOverlapsItem() {
    return this.allowingNodeOverlapsItem;
  }

  @Label("Allow Overlapping Nodes")
  @OptionGroupAnnotation(name = "VisualGroup", position = 40)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAllowingNodeOverlapsItem( boolean value ) {
    this.allowingNodeOverlapsItem = value;
  }

  public final boolean isAllowingNodeOverlapsItemDisabled() {
    return isConsideringNodeLabelsItem();
  }

  private int minimumNodeDistanceItem;

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "VisualGroup", position = 30)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumNodeDistanceItem() {
    return this.minimumNodeDistanceItem;
  }

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "VisualGroup", position = 30)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeDistanceItem( int value ) {
    this.minimumNodeDistanceItem = value;
  }

  public final boolean isMinimumNodeDistanceItemDisabled() {
    return isAllowingNodeOverlapsItem() && !isConsideringNodeLabelsItem();
  }

  private boolean avoidingNodeEdgeOverlapsItem;

  @Label("Avoid Node/Edge Overlaps")
  @OptionGroupAnnotation(name = "VisualGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAvoidingNodeEdgeOverlapsItem() {
    return this.avoidingNodeEdgeOverlapsItem;
  }

  @Label("Avoid Node/Edge Overlaps")
  @OptionGroupAnnotation(name = "VisualGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAvoidingNodeEdgeOverlapsItem( boolean value ) {
    this.avoidingNodeEdgeOverlapsItem = value;
  }

  private double compactnessItem;

  @Label("Compactness Factor")
  @OptionGroupAnnotation(name = "VisualGroup", position = 70)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.1d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getCompactnessItem() {
    return this.compactnessItem;
  }

  @Label("Compactness Factor")
  @OptionGroupAnnotation(name = "VisualGroup", position = 70)
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.1d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setCompactnessItem( double value ) {
    this.compactnessItem = value;
  }

  private boolean usingAutoClusteringItem;

  @Label("Use Natural Clustering")
  @OptionGroupAnnotation(name = "VisualGroup", position = 80)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUsingAutoClusteringItem() {
    return this.usingAutoClusteringItem;
  }

  @Label("Use Natural Clustering")
  @OptionGroupAnnotation(name = "VisualGroup", position = 80)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUsingAutoClusteringItem( boolean value ) {
    this.usingAutoClusteringItem = value;
  }

  private double autoClusteringQualityItem;

  @Label("Natural Clustering Quality")
  @OptionGroupAnnotation(name = "VisualGroup", position = 90)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getAutoClusteringQualityItem() {
    return this.autoClusteringQualityItem;
  }

  @Label("Natural Clustering Quality")
  @OptionGroupAnnotation(name = "VisualGroup", position = 90)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setAutoClusteringQualityItem( double value ) {
    this.autoClusteringQualityItem = value;
  }

  public final boolean isAutoClusteringQualityItemDisabled() {
    return isUsingAutoClusteringItem() == false;
  }

  private EnumOutputRestrictions restrictOutputItem = EnumOutputRestrictions.NONE;

  @Label("Output Area")
  @OptionGroupAnnotation(name = "RestrictionsGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumOutputRestrictions.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Unrestricted", value = "NONE")
  @EnumValueAnnotation(label = "Rectangular", value = "OUTPUT_CAGE")
  @EnumValueAnnotation(label = "Aspect Ratio", value = "OUTPUT_AR")
  @EnumValueAnnotation(label = "Elliptical", value = "OUTPUT_ELLIPTICAL_CAGE")
  public final EnumOutputRestrictions getRestrictOutputItem() {
    return this.restrictOutputItem;
  }

  @Label("Output Area")
  @OptionGroupAnnotation(name = "RestrictionsGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumOutputRestrictions.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Unrestricted", value = "NONE")
  @EnumValueAnnotation(label = "Rectangular", value = "OUTPUT_CAGE")
  @EnumValueAnnotation(label = "Aspect Ratio", value = "OUTPUT_AR")
  @EnumValueAnnotation(label = "Elliptical", value = "OUTPUT_ELLIPTICAL_CAGE")
  public final void setRestrictOutputItem( EnumOutputRestrictions value ) {
    this.restrictOutputItem = value;
  }

  public final boolean isCageGroupDisabled() {
    return getRestrictOutputItem() != EnumOutputRestrictions.OUTPUT_CAGE && getRestrictOutputItem() != EnumOutputRestrictions.OUTPUT_ELLIPTICAL_CAGE;
  }

  private boolean rectCageUsingViewItem;

  @Label("Use Visible Area")
  @OptionGroupAnnotation(name = "CageGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isRectCageUsingViewItem() {
    return this.rectCageUsingViewItem;
  }

  @Label("Use Visible Area")
  @OptionGroupAnnotation(name = "CageGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setRectCageUsingViewItem( boolean value ) {
    this.rectCageUsingViewItem = value;
  }

  private double cageXItem;

  @Label("Top Left X")
  @OptionGroupAnnotation(name = "CageGroup", position = 20)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @ComponentType(ComponentTypes.SPINNER)
  public final double getCageXItem() {
    return this.cageXItem;
  }

  @Label("Top Left X")
  @OptionGroupAnnotation(name = "CageGroup", position = 20)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @ComponentType(ComponentTypes.SPINNER)
  public final void setCageXItem( double value ) {
    this.cageXItem = value;
  }

  public final boolean isCageXItemDisabled() {
    return isRectCageUsingViewItem();
  }

  private double cageYItem;

  @Label("Top Left Y")
  @OptionGroupAnnotation(name = "CageGroup", position = 30)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @ComponentType(ComponentTypes.SPINNER)
  public final double getCageYItem() {
    return this.cageYItem;
  }

  @Label("Top Left Y")
  @OptionGroupAnnotation(name = "CageGroup", position = 30)
  @DefaultValue(doubleValue = 0.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @ComponentType(ComponentTypes.SPINNER)
  public final void setCageYItem( double value ) {
    this.cageYItem = value;
  }

  public final boolean isCageYItemDisabled() {
    return isRectCageUsingViewItem();
  }

  private double cageWidthItem;

  @Label("Width")
  @OptionGroupAnnotation(name = "CageGroup", position = 40)
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1)
  @ComponentType(ComponentTypes.SPINNER)
  public final double getCageWidthItem() {
    return this.cageWidthItem;
  }

  @Label("Width")
  @OptionGroupAnnotation(name = "CageGroup", position = 40)
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1)
  @ComponentType(ComponentTypes.SPINNER)
  public final void setCageWidthItem( double value ) {
    this.cageWidthItem = value;
  }

  public final boolean isCageWidthItemDisabled() {
    return isRectCageUsingViewItem();
  }

  private double cageHeightItem;

  @Label("Height")
  @OptionGroupAnnotation(name = "CageGroup", position = 50)
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1)
  @ComponentType(ComponentTypes.SPINNER)
  public final double getCageHeightItem() {
    return this.cageHeightItem;
  }

  @Label("Height")
  @OptionGroupAnnotation(name = "CageGroup", position = 50)
  @DefaultValue(doubleValue = 1000.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1)
  @ComponentType(ComponentTypes.SPINNER)
  public final void setCageHeightItem( double value ) {
    this.cageHeightItem = value;
  }

  public final boolean isCageHeightItemDisabled() {
    return isRectCageUsingViewItem();
  }

  private boolean arCageUsingViewItem;

  @Label("Use Ratio of View")
  @OptionGroupAnnotation(name = "ARGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isArCageUsingViewItem() {
    return this.arCageUsingViewItem;
  }

  @Label("Use Ratio of View")
  @OptionGroupAnnotation(name = "ARGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setArCageUsingViewItem( boolean value ) {
    this.arCageUsingViewItem = value;
  }

  private double cageRatioItem;

  @Label("Aspect Ratio")
  @OptionGroupAnnotation(name = "ARGroup", position = 20)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.2d, max = 5.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getCageRatioItem() {
    return this.cageRatioItem;
  }

  @Label("Aspect Ratio")
  @OptionGroupAnnotation(name = "ARGroup", position = 20)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.2d, max = 5.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setCageRatioItem( double value ) {
    this.cageRatioItem = value;
  }

  public final boolean isCageRatioItemDisabled() {
    return isArCageUsingViewItem();
  }

  private EnumGroupLayoutPolicy groupLayoutPolicyItem = EnumGroupLayoutPolicy.LAYOUT_GROUPS;

  @Label("Group Layout Policy")
  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumGroupLayoutPolicy.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Fix Bounds of Groups", value = "FIX_GROUP_BOUNDS")
  @EnumValueAnnotation(label = "Fix Contents of Groups", value = "FIX_GROUP_CONTENTS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final EnumGroupLayoutPolicy getGroupLayoutPolicyItem() {
    return this.groupLayoutPolicyItem;
  }

  @Label("Group Layout Policy")
  @OptionGroupAnnotation(name = "GroupingGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumGroupLayoutPolicy.class, stringValue = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Layout Groups", value = "LAYOUT_GROUPS")
  @EnumValueAnnotation(label = "Fix Bounds of Groups", value = "FIX_GROUP_BOUNDS")
  @EnumValueAnnotation(label = "Fix Contents of Groups", value = "FIX_GROUP_CONTENTS")
  @EnumValueAnnotation(label = "Ignore Groups", value = "IGNORE_GROUPS")
  public final void setGroupLayoutPolicyItem( EnumGroupLayoutPolicy value ) {
    this.groupLayoutPolicyItem = value;
  }

  private double qualityTimeRatioItem;

  @Label("Quality")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 10)
  @DefaultValue(doubleValue = 0.6d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getQualityTimeRatioItem() {
    return this.qualityTimeRatioItem;
  }

  @Label("Quality")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 10)
  @DefaultValue(doubleValue = 0.6d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0.0d, max = 1.0d, step = 0.01d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setQualityTimeRatioItem( double value ) {
    this.qualityTimeRatioItem = value;
  }

  private int maximumDurationItem;

  @Label("Maximum Duration (sec)")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 20)
  @DefaultValue(intValue = 30, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 150)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMaximumDurationItem() {
    return this.maximumDurationItem;
  }

  @Label("Maximum Duration (sec)")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 20)
  @DefaultValue(intValue = 30, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 150)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMaximumDurationItem( int value ) {
    this.maximumDurationItem = value;
  }

  private boolean deterministicModeEnabledItem;

  @Label("Deterministic Mode")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isDeterministicModeEnabledItem() {
    return this.deterministicModeEnabledItem;
  }

  @Label("Deterministic Mode")
  @OptionGroupAnnotation(name = "AlgorithmGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setDeterministicModeEnabledItem( boolean value ) {
    this.deterministicModeEnabledItem = value;
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

  private CycleSubstructureStyle cycleSubstructureStyleItem = CycleSubstructureStyle.NONE;

  @Label("Cycles")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = CycleSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Circular", value = "CIRCULAR")
  public final CycleSubstructureStyle getCycleSubstructureStyleItem() {
    return this.cycleSubstructureStyleItem;
  }

  @Label("Cycles")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = CycleSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Circular", value = "CIRCULAR")
  public final void setCycleSubstructureStyleItem( CycleSubstructureStyle value ) {
    this.cycleSubstructureStyleItem = value;
  }

  private ChainSubstructureStyle chainSubstructureStyleItem = ChainSubstructureStyle.NONE;

  @Label("Chains")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ChainSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHT_LINE")
  @EnumValueAnnotation(label = "Rectangular", value = "RECTANGULAR")
  public final ChainSubstructureStyle getChainSubstructureStyleItem() {
    return this.chainSubstructureStyleItem;
  }

  @Label("Chains")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ChainSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHT_LINE")
  @EnumValueAnnotation(label = "Rectangular", value = "RECTANGULAR")
  public final void setChainSubstructureStyleItem( ChainSubstructureStyle value ) {
    this.chainSubstructureStyleItem = value;
  }

  private StarSubstructureStyle starSubstructureStyleItem = StarSubstructureStyle.NONE;

  @Label("Stars")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = StarSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Circular", value = "CIRCULAR")
  @EnumValueAnnotation(label = "Radial", value = "RADIAL")
  @EnumValueAnnotation(label = "Separated Radial", value = "SEPARATED_RADIAL")
  public final StarSubstructureStyle getStarSubstructureStyleItem() {
    return this.starSubstructureStyleItem;
  }

  @Label("Stars")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = StarSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Circular", value = "CIRCULAR")
  @EnumValueAnnotation(label = "Radial", value = "RADIAL")
  @EnumValueAnnotation(label = "Separated Radial", value = "SEPARATED_RADIAL")
  public final void setStarSubstructureStyleItem( StarSubstructureStyle value ) {
    this.starSubstructureStyleItem = value;
  }

  private ParallelSubstructureStyle parallelSubstructureStyleItem = ParallelSubstructureStyle.NONE;

  @Label("Parallel Structures")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ParallelSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Rectangular", value = "RECTANGULAR")
  @EnumValueAnnotation(label = "Radial", value = "RADIAL")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHT_LINE")
  public final ParallelSubstructureStyle getParallelSubstructureStyleItem() {
    return this.parallelSubstructureStyleItem;
  }

  @Label("Parallel Structures")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ParallelSubstructureStyle.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore", value = "NONE")
  @EnumValueAnnotation(label = "Rectangular", value = "RECTANGULAR")
  @EnumValueAnnotation(label = "Radial", value = "RADIAL")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHT_LINE")
  public final void setParallelSubstructureStyleItem( ParallelSubstructureStyle value ) {
    this.parallelSubstructureStyleItem = value;
  }

  private boolean edgeDirectednessEnabledItem;

  @Label("Arrows Define Edge Direction")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 50)
  public final boolean isEdgeDirectednessEnabledItem() {
    return this.edgeDirectednessEnabledItem;
  }

  @Label("Arrows Define Edge Direction")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 50)
  public final void setEdgeDirectednessEnabledItem( boolean value ) {
    this.edgeDirectednessEnabledItem = value;
  }

  private boolean usingEdgeGroupingItem;

  @Label("Use Edge Grouping")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 60)
  public final boolean isUsingEdgeGroupingItem() {
    return this.usingEdgeGroupingItem;
  }

  @Label("Use Edge Grouping")
  @OptionGroupAnnotation(name = "SubstructureLayoutGroup", position = 60)
  public final void setUsingEdgeGroupingItem( boolean value ) {
    this.usingEdgeGroupingItem = value;
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
