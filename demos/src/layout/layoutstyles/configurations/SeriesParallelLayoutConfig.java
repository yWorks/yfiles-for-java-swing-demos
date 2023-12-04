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

import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.StraightLineEdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.seriesparallel.DefaultPortAssignment;
import com.yworks.yfiles.layout.seriesparallel.EdgeLayoutDescriptor;
import com.yworks.yfiles.layout.seriesparallel.ForkStyle;
import com.yworks.yfiles.layout.seriesparallel.PortAssignmentMode;
import com.yworks.yfiles.layout.seriesparallel.RoutingStyle;
import com.yworks.yfiles.layout.seriesparallel.SeriesParallelLayout;
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
@Label("SeriesParallelLayout")
public class SeriesParallelLayoutConfig extends LayoutConfiguration {
  public SeriesParallelLayoutConfig() {
    SeriesParallelLayout layout = new SeriesParallelLayout();
    EdgeLayoutDescriptor edgeLayoutDescriptor = layout.getDefaultEdgeLayoutDescriptor();

    setOrientationItem(LayoutOrientation.TOP_TO_BOTTOM);
    setVerticalAlignmentItem(0.5);
    setUsingDrawingAsSketchItem(layout.isFromSketchMode());
    setMinimumNodeToNodeDistanceItem(30);
    setMinimumNodeToEdgeDistanceItem(15);
    setMinimumEdgeToEdgeDistanceItem(15);
    setConsideringNodeLabelsItem(true);
    setPlacingEdgeLabelsItem(true);

    setPortStyleItem(PortAssignmentMode.CENTER);
    setRoutingStyleItem(RoutingStyle.ORTHOGONAL);
    setPreferredOctilinearSegmentLengthItem(layout.getPreferredOctilinearSegmentLength());
    setMinimumPolylineSegmentLengthItem(layout.getMinimumPolylineSegmentLength());
    setMinimumSlopeItem(layout.getMinimumSlope());
    setRoutingStyleNonSeriesParallelItem(NonSeriesParallelRoutingStyle.ORTHOGONAL);
    setRoutingEdgesInFlowDirectionItem(true);
    setMinimumFirstSegmentLengthItem(edgeLayoutDescriptor.getMinimumFirstSegmentLength());
    setMinimumLastSegmentLengthItem(edgeLayoutDescriptor.getMinimumLastSegmentLength());
    setMinimumEdgeLengthItem(20);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    SeriesParallelLayout layout = new SeriesParallelLayout();
    layout.setGeneralGraphHandling(true);

    layout.setLayoutOrientation(getOrientationItem());

    layout.setVerticalAlignment(getVerticalAlignmentItem());
    layout.setFromSketchMode(isUsingDrawingAsSketchItem());

    layout.setMinimumNodeToNodeDistance(getMinimumNodeToNodeDistanceItem());
    layout.setMinimumNodeToEdgeDistance(getMinimumNodeToEdgeDistanceItem());
    layout.setMinimumEdgeToEdgeDistance(getMinimumEdgeToEdgeDistanceItem());

    layout.setConsiderNodeLabels(isConsideringNodeLabelsItem());
    layout.setIntegratedEdgeLabeling(isPlacingEdgeLabelsItem());

    DefaultPortAssignment portAssignment = (DefaultPortAssignment)layout.getDefaultPortAssignment();
    portAssignment.setMode(getPortStyleItem());
    portAssignment.setForkStyle(isRoutingEdgesInFlowDirectionItem() ? ForkStyle.OUTSIDE_NODE : ForkStyle.AT_NODE);

    layout.setRoutingStyle(getRoutingStyleItem());
    if (getRoutingStyleItem() == RoutingStyle.OCTILINEAR) {
      layout.setPreferredOctilinearSegmentLength(getPreferredOctilinearSegmentLengthItem());
    } else if (getRoutingStyleItem() == RoutingStyle.POLYLINE) {
      layout.setMinimumPolylineSegmentLength(getMinimumPolylineSegmentLengthItem());
      layout.setMinimumSlope(getMinimumSlopeItem());
    }

    if (getRoutingStyleNonSeriesParallelItem() == NonSeriesParallelRoutingStyle.ORTHOGONAL) {
      EdgeRouter edgeRouter = new EdgeRouter();
      edgeRouter.setReroutingEnabled(true);
      edgeRouter.setScope(Scope.ROUTE_AFFECTED_EDGES);
      layout.setNonSeriesParallelEdgeRouter(edgeRouter);
      layout.setNonSeriesParallelEdgesDpKey(edgeRouter.getAffectedEdgesDpKey());
    } else if (getRoutingStyleNonSeriesParallelItem() == NonSeriesParallelRoutingStyle.ORGANIC) {
      OrganicEdgeRouter edgeRouter = new OrganicEdgeRouter();
      layout.setNonSeriesParallelEdgeRouter(edgeRouter);
      layout.setNonSeriesParallelEdgesDpKey(OrganicEdgeRouter.AFFECTED_EDGES_DPKEY);
    } else if (getRoutingStyleNonSeriesParallelItem() == NonSeriesParallelRoutingStyle.STRAIGHT) {
      StraightLineEdgeRouter edgeRouter = new StraightLineEdgeRouter();
      edgeRouter.setScope(Scope.ROUTE_AFFECTED_EDGES);
      layout.setNonSeriesParallelEdgeRouter(edgeRouter);
      layout.setNonSeriesParallelEdgesDpKey(edgeRouter.getAffectedEdgesDpKey());
    }

    EdgeLayoutDescriptor edgeLayoutDescriptor = layout.getDefaultEdgeLayoutDescriptor();
    edgeLayoutDescriptor.setMinimumFirstSegmentLength(getMinimumFirstSegmentLengthItem());
    edgeLayoutDescriptor.setMinimumLastSegmentLength(getMinimumLastSegmentLengthItem());
    edgeLayoutDescriptor.setMinimumLength(getMinimumEdgeLengthItem());

    return layout;
  }

  private Object descriptionGroup;

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public final Object getDescriptionGroup() {
    return this.descriptionGroup;
  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public final void setDescriptionGroup( Object value ) {
    this.descriptionGroup = value;
  }

  private Object generalGroup;

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public final Object getGeneralGroup() {
    return this.generalGroup;
  }

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public final void setGeneralGroup( Object value ) {
    this.generalGroup = value;
  }

  private Object edgesGroup;

  @Label("Edges")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public final Object getEdgesGroup() {
    return this.edgesGroup;
  }

  @Label("Edges")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public final void setEdgesGroup( Object value ) {
    this.edgesGroup = value;
  }

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionTextItem() {
    return "<p>The series-parallel layout algorithm highlights the main direction or flow of a graph, similar to the" +
           " hierarchic style. In comparison, this algorithm is usually faster but can be used only on special graphs," +
           " namely series-parallel graphs.</p>";
  }

  private LayoutOrientation orientationItem = LayoutOrientation.TOP_TO_BOTTOM;

  @Label("Orientation")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutOrientation.class, stringValue = "TOP_TO_BOTTOM")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public final LayoutOrientation getOrientationItem() {
    return this.orientationItem;
  }

  @Label("Orientation")
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LayoutOrientation.class, stringValue = "TOP_TO_BOTTOM")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 10)
  @EnumValueAnnotation(label = "Top to Bottom", value = "TOP_TO_BOTTOM")
  @EnumValueAnnotation(label = "Left to Right", value = "LEFT_TO_RIGHT")
  @EnumValueAnnotation(label = "Bottom to Top", value = "BOTTOM_TO_TOP")
  @EnumValueAnnotation(label = "Right to Left", value = "RIGHT_TO_LEFT")
  public final void setOrientationItem( LayoutOrientation value ) {
    this.orientationItem = value;
  }

  private double verticalAlignmentItem;

  @Label("Vertical Alignment")
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @EnumValueAnnotation(label = "Top", value = "0.0")
  @EnumValueAnnotation(label = "Center", value = "0.5")
  @EnumValueAnnotation(label = "Bottom", value = "1.0")
  public final double getVerticalAlignmentItem() {
    return this.verticalAlignmentItem;
  }

  @Label("Vertical Alignment")
  @DefaultValue(doubleValue = 0.5d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "GeneralGroup", position = 20)
  @EnumValueAnnotation(label = "Top", value = "0.0")
  @EnumValueAnnotation(label = "Center", value = "0.5")
  @EnumValueAnnotation(label = "Bottom", value = "1.0")
  public final void setVerticalAlignmentItem( double value ) {
    this.verticalAlignmentItem = value;
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

  private Object distanceGroup;

  @Label("Minimum Distances")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public final Object getDistanceGroup() {
    return this.distanceGroup;
  }

  @Label("Minimum Distances")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public final void setDistanceGroup( Object value ) {
    this.distanceGroup = value;
  }

  private double minimumNodeToNodeDistanceItem;

  @Label("Node to Node Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 10)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumNodeToNodeDistanceItem() {
    return this.minimumNodeToNodeDistanceItem;
  }

  @Label("Node to Node Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 10)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeToNodeDistanceItem( double value ) {
    this.minimumNodeToNodeDistanceItem = value;
  }

  private double minimumNodeToEdgeDistanceItem;

  @Label("Node to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumNodeToEdgeDistanceItem() {
    return this.minimumNodeToEdgeDistanceItem;
  }

  @Label("Node to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeToEdgeDistanceItem( double value ) {
    this.minimumNodeToEdgeDistanceItem = value;
  }

  private double minimumEdgeToEdgeDistanceItem;

  @Label("Edge to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 30)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumEdgeToEdgeDistanceItem() {
    return this.minimumEdgeToEdgeDistanceItem;
  }

  @Label("Edge to Edge Distance")
  @MinMax(min = 0, max = 100)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "DistanceGroup", position = 30)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumEdgeToEdgeDistanceItem( double value ) {
    this.minimumEdgeToEdgeDistanceItem = value;
  }

  private Object labelingGroup;

  @Label("Labeling")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public final Object getLabelingGroup() {
    return this.labelingGroup;
  }

  @Label("Labeling")
  @OptionGroupAnnotation(name = "GeneralGroup", position = 40)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public final void setLabelingGroup( Object value ) {
    this.labelingGroup = value;
  }

  private boolean consideringNodeLabelsItem;

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsideringNodeLabelsItem() {
    return this.consideringNodeLabelsItem;
  }

  @Label("Consider Node Labels")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 10)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsideringNodeLabelsItem( boolean value ) {
    this.consideringNodeLabelsItem = value;
  }

  private boolean placingEdgeLabelsItem;

  @Label("Place Edge Labels")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 20)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isPlacingEdgeLabelsItem() {
    return this.placingEdgeLabelsItem;
  }

  @Label("Place Edge Labels")
  @OptionGroupAnnotation(name = "LabelingGroup", position = 20)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setPlacingEdgeLabelsItem( boolean value ) {
    this.placingEdgeLabelsItem = value;
  }

  private PortAssignmentMode portStyleItem = PortAssignmentMode.CENTER;

  @Label("Port Style")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortAssignmentMode.class, stringValue = "CENTER")
  @EnumValueAnnotation(label = "Centered", value = "CENTER")
  @EnumValueAnnotation(label = "Distributed", value = "DISTRIBUTED")
  public final PortAssignmentMode getPortStyleItem() {
    return this.portStyleItem;
  }

  @Label("Port Style")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = PortAssignmentMode.class, stringValue = "CENTER")
  @EnumValueAnnotation(label = "Centered", value = "CENTER")
  @EnumValueAnnotation(label = "Distributed", value = "DISTRIBUTED")
  public final void setPortStyleItem( PortAssignmentMode value ) {
    this.portStyleItem = value;
  }

  private RoutingStyle routingStyleItem = RoutingStyle.ORTHOGONAL;

  @Label("Routing Style")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = RoutingStyle.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Octilinear", value = "OCTILINEAR")
  @EnumValueAnnotation(label = "Polyline", value = "POLYLINE")
  public final RoutingStyle getRoutingStyleItem() {
    return this.routingStyleItem;
  }

  @Label("Routing Style")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = RoutingStyle.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Octilinear", value = "OCTILINEAR")
  @EnumValueAnnotation(label = "Polyline", value = "POLYLINE")
  public final void setRoutingStyleItem( RoutingStyle value ) {
    this.routingStyleItem = value;
  }

  private double preferredOctilinearSegmentLengthItem;

  @Label("Preferred Octilinear Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 30)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getPreferredOctilinearSegmentLengthItem() {
    return this.preferredOctilinearSegmentLengthItem;
  }

  @Label("Preferred Octilinear Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 30)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setPreferredOctilinearSegmentLengthItem( double value ) {
    this.preferredOctilinearSegmentLengthItem = value;
  }

  public final boolean isPreferredOctilinearSegmentLengthItemDisabled() {
    return getRoutingStyleItem() != RoutingStyle.OCTILINEAR;
  }

  private double minimumPolylineSegmentLengthItem;

  @Label("Minimum Polyline Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 40)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumPolylineSegmentLengthItem() {
    return this.minimumPolylineSegmentLengthItem;
  }

  @Label("Minimum Polyline Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 40)
  @DefaultValue(doubleValue = 30.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumPolylineSegmentLengthItem( double value ) {
    this.minimumPolylineSegmentLengthItem = value;
  }

  public final boolean isMinimumPolylineSegmentLengthItemDisabled() {
    return getRoutingStyleItem() != RoutingStyle.POLYLINE;
  }

  private double minimumSlopeItem;

  @MinMax(min = 0.0d, max = 5.0d, step = 0.01d)
  @Label("Minimum Slope")
  @DefaultValue(doubleValue = 0.25d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "EdgesGroup", position = 50)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumSlopeItem() {
    return this.minimumSlopeItem;
  }

  @MinMax(min = 0.0d, max = 5.0d, step = 0.01d)
  @Label("Minimum Slope")
  @DefaultValue(doubleValue = 0.25d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @OptionGroupAnnotation(name = "EdgesGroup", position = 50)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumSlopeItem( double value ) {
    this.minimumSlopeItem = value;
  }

  public final boolean isMinimumSlopeItemDisabled() {
    return getRoutingStyleItem() != RoutingStyle.POLYLINE;
  }

  private NonSeriesParallelRoutingStyle routingStyleNonSeriesParallelItem = NonSeriesParallelRoutingStyle.ORTHOGONAL;

  @Label("Routing Style (Non-Series-Parallel Edges)")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 60)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = NonSeriesParallelRoutingStyle.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHT")
  public final NonSeriesParallelRoutingStyle getRoutingStyleNonSeriesParallelItem() {
    return this.routingStyleNonSeriesParallelItem;
  }

  @Label("Routing Style (Non-Series-Parallel Edges)")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 60)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = NonSeriesParallelRoutingStyle.class, stringValue = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Orthogonal", value = "ORTHOGONAL")
  @EnumValueAnnotation(label = "Organic", value = "ORGANIC")
  @EnumValueAnnotation(label = "Straight-Line", value = "STRAIGHT")
  public final void setRoutingStyleNonSeriesParallelItem( NonSeriesParallelRoutingStyle value ) {
    this.routingStyleNonSeriesParallelItem = value;
  }

  private boolean routingEdgesInFlowDirectionItem;

  @Label("Route Edges in Flow Direction")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 70)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isRoutingEdgesInFlowDirectionItem() {
    return this.routingEdgesInFlowDirectionItem;
  }

  @Label("Route Edges in Flow Direction")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 70)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setRoutingEdgesInFlowDirectionItem( boolean value ) {
    this.routingEdgesInFlowDirectionItem = value;
  }

  private double minimumFirstSegmentLengthItem;

  @Label("Minimum First Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 80)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumFirstSegmentLengthItem() {
    return this.minimumFirstSegmentLengthItem;
  }

  @Label("Minimum First Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 80)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumFirstSegmentLengthItem( double value ) {
    this.minimumFirstSegmentLengthItem = value;
  }

  private double minimumLastSegmentLengthItem;

  @Label("Minimum Last Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 90)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumLastSegmentLengthItem() {
    return this.minimumLastSegmentLengthItem;
  }

  @Label("Minimum Last Segment Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 90)
  @DefaultValue(doubleValue = 15.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumLastSegmentLengthItem( double value ) {
    this.minimumLastSegmentLengthItem = value;
  }

  private double minimumEdgeLengthItem;

  @Label("Minimum Edge Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 100)
  @DefaultValue(doubleValue = 20.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumEdgeLengthItem() {
    return this.minimumEdgeLengthItem;
  }

  @Label("Minimum Edge Length")
  @OptionGroupAnnotation(name = "EdgesGroup", position = 100)
  @DefaultValue(doubleValue = 20.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumEdgeLengthItem( double value ) {
    this.minimumEdgeLengthItem = value;
  }

  public enum NonSeriesParallelRoutingStyle {
    ORTHOGONAL(0),

    ORGANIC(1),

    STRAIGHT(2);

    private final int value;

    private NonSeriesParallelRoutingStyle( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final NonSeriesParallelRoutingStyle fromOrdinal( int ordinal ) {
      for (NonSeriesParallelRoutingStyle current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

}
