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
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.ItemMapping;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.NodeLabelingPolicy;
import com.yworks.yfiles.layout.RecursiveGroupLayout;
import com.yworks.yfiles.layout.RecursiveGroupLayoutData;
import com.yworks.yfiles.layout.circular.CompactDiskLayout;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.utils.IterableExtensions;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;
import java.util.function.Function;
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
@Label("CompactDiskLayout")
public class CompactDiskLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public CompactDiskLayoutConfig() {
    CompactDiskLayout layout = new CompactDiskLayout();

    setUsingDrawingAsSketchItem(layout.isFromSketchModeEnabled());
    setMinimumNodeDistanceItem(layout.getMinimumNodeDistance());
    setNodeLabelingStyleItem(LayoutConfiguration.EnumNodeLabelingPolicies.NONE);
    setLayoutGroupsItem(GroupLayout.NONE);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( final GraphComponent graphComponent ) {
    if (getLayoutGroupsItem() == GroupLayout.RECURSIVE && IterableExtensions.any(graphComponent.getGraph().getNodes(), new Function<INode, Boolean>(){
      public Boolean apply( INode n ) {
        return graphComponent.getGraph().isGroupNode(n);
      }
    })) {
      // if the recursive group layout option is enabled, use RecursiveGroupLayout with organic for
      // the top-level hierarchy - the actual compact disk layout will be specified as layout for
      // each group content in function createConfiguredLayoutData
      RecursiveGroupLayout recursiveGroupLayout = new RecursiveGroupLayout();
      OrganicLayout organicLayout = new OrganicLayout();
      organicLayout.setDeterministicModeEnabled(true);
      organicLayout.setNodeOverlapsAllowed(false);
      organicLayout.setMinimumNodeDistance(getMinimumNodeDistanceItem());
      recursiveGroupLayout.setCoreLayout(organicLayout);
      recursiveGroupLayout.setFromSketchModeEnabled(isUsingDrawingAsSketchItem());
      return recursiveGroupLayout;
    }

    // just use plain CompactDiskLayout
    return this.createCompactDiskLayout(graphComponent);
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    if (getLayoutGroupsItem() == GroupLayout.RECURSIVE) {
      CompactDiskLayout compactDiskLayout = this.createCompactDiskLayout(graphComponent);
      RecursiveGroupLayoutData recursiveGroupLayoutData = new RecursiveGroupLayoutData();
      ItemMapping<INode, ILayoutAlgorithm> groupNodeLayouts = new ItemMapping<INode, ILayoutAlgorithm>(ILayoutAlgorithm.class);
      groupNodeLayouts.setConstant(compactDiskLayout);
      recursiveGroupLayoutData.setGroupNodeLayouts(groupNodeLayouts);
      return recursiveGroupLayoutData;
    }
    return null;
  }

  private CompactDiskLayout createCompactDiskLayout( GraphComponent graphComponent ) {
    CompactDiskLayout layout = new CompactDiskLayout();

    layout.setFromSketchModeEnabled(isUsingDrawingAsSketchItem());

    layout.setMinimumNodeDistance(getMinimumNodeDistanceItem());

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

    if (getNodeLabelingStyleItem() == LayoutConfiguration.EnumNodeLabelingPolicies.RAYLIKE_LEAVES || getNodeLabelingStyleItem() == LayoutConfiguration.EnumNodeLabelingPolicies.HORIZONTAL) {
      for (ILabel label : graphComponent.getGraph().getLabels()) {
        if (label.getOwner() instanceof INode) {
          graphComponent.getGraph().setLabelLayoutParameter(label, FreeNodeLabelModel.INSTANCE.findBestParameter(label, FreeNodeLabelModel.INSTANCE, label.getLayout()));
        }
      }
    }

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

  @Label("Labeling")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LabelingGroup;

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>The nodes are arranged on a disk such that the disk's radius is minimized.</p>"
        + "<p>The layout mostly optimizes the dense placement of the nodes, "
        + "while edges play a minor role. Hence, the compact disk layout is mostly suitable for graphs with "
        + "small components whose loosely connected nodes should be grouped and packed in a small area.</p>";
  }

  private boolean usingDrawingAsSketchItem;

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUsingDrawingAsSketchItem() {
    return this.usingDrawingAsSketchItem;
  }

  @Label("Use Drawing as Sketch")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUsingDrawingAsSketchItem( boolean value ) {
    this.usingDrawingAsSketchItem = value;
  }

  private double minimumNodeDistanceItem;

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(intValue = 0, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumNodeDistanceItem() {
    return this.minimumNodeDistanceItem;
  }

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @DefaultValue(intValue = 0, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeDistanceItem( double value ) {
    this.minimumNodeDistanceItem = value;
  }

  private GroupLayout layoutGroupsItem = GroupLayout.NONE;

  @Label("Layout Groups")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupLayout.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore Groups", value = "NONE")
  @EnumValueAnnotation(label = "Layout Recursively", value = "RECURSIVE")
  public final GroupLayout getLayoutGroupsItem() {
    return this.layoutGroupsItem;
  }

  @Label("Layout Groups")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GroupLayout.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore Groups", value = "NONE")
  @EnumValueAnnotation(label = "Layout Recursively", value = "RECURSIVE")
  public final void setLayoutGroupsItem( GroupLayout value ) {
    this.layoutGroupsItem = value;
  }

  private LayoutConfiguration.EnumNodeLabelingPolicies nodeLabelingStyleItem = LayoutConfiguration.EnumNodeLabelingPolicies.NONE;

  @Label("Node Labeling")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumNodeLabelingPolicies.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore Labels", value = "NONE")
  @EnumValueAnnotation(label = "Consider Labels", value = "CONSIDER_CURRENT_POSITION")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Ray-like at Leaves", value = "RAYLIKE_LEAVES")
  public final LayoutConfiguration.EnumNodeLabelingPolicies getNodeLabelingStyleItem() {
    return this.nodeLabelingStyleItem;
  }

  @Label("Node Labeling")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutConfiguration.EnumNodeLabelingPolicies.class, stringValue = "NONE")
  @EnumValueAnnotation(label = "Ignore Labels", value = "NONE")
  @EnumValueAnnotation(label = "Consider Labels", value = "CONSIDER_CURRENT_POSITION")
  @EnumValueAnnotation(label = "Horizontal", value = "HORIZONTAL")
  @EnumValueAnnotation(label = "Ray-like at Leaves", value = "RAYLIKE_LEAVES")
  public final void setNodeLabelingStyleItem( LayoutConfiguration.EnumNodeLabelingPolicies value ) {
    this.nodeLabelingStyleItem = value;
  }

  public enum GroupLayout {
    NONE(0),

    RECURSIVE(1);

    private final int value;

    private GroupLayout( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final GroupLayout fromOrdinal( int ordinal ) {
      for (GroupLayout current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

}
