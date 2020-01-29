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

import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.circular.CircularLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.partial.ComponentAssignmentStrategy;
import com.yworks.yfiles.layout.partial.EdgeRoutingStrategy;
import com.yworks.yfiles.layout.partial.LayoutOrientation;
import com.yworks.yfiles.layout.partial.PartialLayout;
import com.yworks.yfiles.layout.partial.PartialLayoutData;
import com.yworks.yfiles.layout.partial.SubgraphPlacement;
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
 * Configuration options for the layout algorithm of the same name.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
@Label("PartialLayout")
public class PartialLayoutConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public PartialLayoutConfig() {
    setRoutingToSubgraphItem(EdgeRoutingStrategy.AUTOMATIC);
    setComponentAssignmentStrategyItem(ComponentAssignmentStrategy.CONNECTED);
    setSubgraphLayoutItem(EnumSubgraphLayouts.HIERARCHIC);
    setSubgraphPlacementItem(SubgraphPlacement.FROM_SKETCH);
    setMinimumNodeDistanceItem(30);
    setOrientationItem(LayoutOrientation.AUTO_DETECT);
    setAligningNodesItem(true);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    PartialLayout layout = new PartialLayout();
    layout.setNodeAlignmentConsiderationEnabled(isAligningNodesItem());
    layout.setMinimumNodeDistance(getMinimumNodeDistanceItem());
    layout.setSubgraphPlacement(getSubgraphPlacementItem());
    layout.setComponentAssignmentStrategy(getComponentAssignmentStrategyItem());
    layout.setLayoutOrientation(getOrientationItem());
    layout.setEdgeRoutingStrategy(getRoutingToSubgraphItem());

    ILayoutAlgorithm subgraphLayout = null;
    if (getComponentAssignmentStrategyItem() != ComponentAssignmentStrategy.SINGLE) {
      switch (getSubgraphLayoutItem()) {
        case HIERARCHIC:
          subgraphLayout = new HierarchicLayout();
          break;
        case ORGANIC:
          subgraphLayout = new OrganicLayout();
          break;
        case CIRCULAR:
          subgraphLayout = new CircularLayout();
          break;
        case ORTHOGONAL:
          subgraphLayout = new OrthogonalLayout();
          break;
      }
    }
    layout.setCoreLayout(subgraphLayout);

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    PartialLayoutData layoutData = new PartialLayoutData();
    IGraphSelection selection = graphComponent.getSelection();

    layoutData.setAffectedNodes(selection.getSelectedNodes());
    layoutData.setAffectedEdges(selection.getSelectedEdges());

    return layoutData;
  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("Layout")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LayoutGroup;

  public enum EnumSubgraphLayouts {
    HIERARCHIC(0),

    ORGANIC(1),

    CIRCULAR(2),

    ORTHOGONAL(3),

    AS_IS(4);

    private final int value;

    private EnumSubgraphLayouts( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumSubgraphLayouts fromOrdinal( int ordinal ) {
      for (EnumSubgraphLayouts current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>Partial layout arranges user-specified parts of a diagram, the so-called partial elements, " +
           "while keeping the other parts fixed. " +
           "It is related to incremental graph layout. " +
           "This concept is a perfect fit for incremental scenarios where subsequently added parts should be arranged so that " +
           "they fit into a given, unchanged diagram.</p>" +
           "<p>In a first step, partial elements are combined to form subgraph components. " +
           "Subsequently, these are arranged and afterwards placed so that " +
           "the remainder of the diagram, which consists of the so-called fixed elements, is not affected.</p>" +
           "<p>Placing a subgraph component predominantly means finding a good position that both meets certain proximity criteria and " +
           "offers enough space to accommodate the subgraph component.</p>";
  }

  private EdgeRoutingStrategy routingToSubgraphItem = EdgeRoutingStrategy.ORTHOGONAL;

  @Label("Edge Routing Style")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeRoutingStrategy.class, stringValue = "AUTOMATIC")
  @EnumValueAnnotation(label = "Auto-Detect", value = "AUTOMATIC")
  @EnumValueAnnotation(label = "Octilinear", value = "OCTILINEAR")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHTLINE")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  public final EdgeRoutingStrategy getRoutingToSubgraphItem() {
    return this.routingToSubgraphItem;
  }

  @Label("Edge Routing Style")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EdgeRoutingStrategy.class, stringValue = "AUTOMATIC")
  @EnumValueAnnotation(label = "Auto-Detect", value = "AUTOMATIC")
  @EnumValueAnnotation(label = "Octilinear", value = "OCTILINEAR")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHTLINE")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  public final void setRoutingToSubgraphItem( EdgeRoutingStrategy value ) {
    this.routingToSubgraphItem = value;
  }

  private ComponentAssignmentStrategy componentAssignmentStrategyItem = ComponentAssignmentStrategy.SINGLE;

  @Label("Placement Strategy")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ComponentAssignmentStrategy.class, stringValue = "CONNECTED")
  @EnumValueAnnotation(label = "Connected Nodes as a Unit", value = "CONNECTED")
  @EnumValueAnnotation(label = "Each Node Separately", value = "SINGLE")
  @EnumValueAnnotation(label = "All Nodes as a Unit", value = "CUSTOMIZED")
  @EnumValueAnnotation(label = "Clustering", value = "CLUSTERING")
  public final ComponentAssignmentStrategy getComponentAssignmentStrategyItem() {
    return this.componentAssignmentStrategyItem;
  }

  @Label("Placement Strategy")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ComponentAssignmentStrategy.class, stringValue = "CONNECTED")
  @EnumValueAnnotation(label = "Connected Nodes as a Unit", value = "CONNECTED")
  @EnumValueAnnotation(label = "Each Node Separately", value = "SINGLE")
  @EnumValueAnnotation(label = "All Nodes as a Unit", value = "CUSTOMIZED")
  @EnumValueAnnotation(label = "Clustering", value = "CLUSTERING")
  public final void setComponentAssignmentStrategyItem( ComponentAssignmentStrategy value ) {
    this.componentAssignmentStrategyItem = value;
  }

  private EnumSubgraphLayouts subgraphLayoutItem = EnumSubgraphLayouts.HIERARCHIC;

  @Label("Subgraph Layout")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumSubgraphLayouts.class, stringValue = "HIERARCHIC")
  @EnumValueAnnotation(label = "Hierarchical", value = "HIERARCHIC")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  @EnumValueAnnotation(label = "Circular", value = "CIRCULAR")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "As Is", value = "AS_IS")
  public final EnumSubgraphLayouts getSubgraphLayoutItem() {
    return this.subgraphLayoutItem;
  }

  @Label("Subgraph Layout")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumSubgraphLayouts.class, stringValue = "HIERARCHIC")
  @EnumValueAnnotation(label = "Hierarchical", value = "HIERARCHIC")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  @EnumValueAnnotation(label = "Circular", value = "CIRCULAR")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "As Is", value = "AS_IS")
  public final void setSubgraphLayoutItem( EnumSubgraphLayouts value ) {
    this.subgraphLayoutItem = value;
  }

  private SubgraphPlacement subgraphPlacementItem = null;

  @Label("Preferred Placement")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = SubgraphPlacement.class, stringValue = "FROM_SKETCH")
  @EnumValueAnnotation(label = "Close to Initial Position", value = "FROM_SKETCH")
  @EnumValueAnnotation(label = "Close to Neighbors", value = "BARYCENTER")
  public final SubgraphPlacement getSubgraphPlacementItem() {
    return this.subgraphPlacementItem;
  }

  @Label("Preferred Placement")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = SubgraphPlacement.class, stringValue = "FROM_SKETCH")
  @EnumValueAnnotation(label = "Close to Initial Position", value = "FROM_SKETCH")
  @EnumValueAnnotation(label = "Close to Neighbors", value = "BARYCENTER")
  public final void setSubgraphPlacementItem( SubgraphPlacement value ) {
    this.subgraphPlacementItem = value;
  }

  private int minimumNodeDistanceItem;

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(intValue = 30, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumNodeDistanceItem() {
    return this.minimumNodeDistanceItem;
  }

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(intValue = 30, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeDistanceItem( int value ) {
    this.minimumNodeDistanceItem = value;
  }

  private LayoutOrientation orientationItem = LayoutOrientation.TOP_TO_BOTTOM;

  @Label("Orientation")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutOrientation.class, stringValue = "AUTO_DETECT")
  @EnumValueAnnotation(label = "Auto Detect", value = "AUTO_DETECT")
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  @EnumValueAnnotation(label = "None", value = "NONE")
  public final LayoutOrientation getOrientationItem() {
    return this.orientationItem;
  }

  @Label("Orientation")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutOrientation.class, stringValue = "AUTO_DETECT")
  @EnumValueAnnotation(label = "Auto Detect", value = "AUTO_DETECT")
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  @EnumValueAnnotation(label = "None", value = "NONE")
  public final void setOrientationItem( LayoutOrientation value ) {
    this.orientationItem = value;
  }

  private boolean aligningNodesItem;

  @Label("Align Nodes")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAligningNodesItem() {
    return this.aligningNodesItem;
  }

  @Label("Align Nodes")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAligningNodesItem( boolean value ) {
    this.aligningNodesItem = value;
  }

}
