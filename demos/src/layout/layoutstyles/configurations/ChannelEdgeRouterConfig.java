/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.router.ChannelEdgeRouter;
import com.yworks.yfiles.layout.router.ChannelEdgeRouterData;
import com.yworks.yfiles.layout.router.OrthogonalPatternEdgeRouter;
import com.yworks.yfiles.layout.router.OrthogonalSegmentDistributionStage;
import com.yworks.yfiles.layout.router.Scope;
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
@Label("ChannelEdgeRouter")
public class ChannelEdgeRouterConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public ChannelEdgeRouterConfig() {
    setScopeItem(Scope.ROUTE_ALL_EDGES);
    setMinimumDistanceItem(10);
    setActivatingGridRoutingItem(true);
    setGridSpacingItem(20);

    setBendCostItem(1);
    setEdgeCrossingCostItem(5);
    setNodeCrossingCostItem(50);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    ChannelEdgeRouter router = new ChannelEdgeRouter();

    OrthogonalPatternEdgeRouter orthogonalPatternEdgeRouter = new OrthogonalPatternEdgeRouter();

    orthogonalPatternEdgeRouter.setAffectedEdgesDpKey(ChannelEdgeRouter.AFFECTED_EDGES_DPKEY);
    orthogonalPatternEdgeRouter.setMinimumDistance(getMinimumDistanceItem());
    orthogonalPatternEdgeRouter.setGridRouting(isActivatingGridRoutingItem());
    orthogonalPatternEdgeRouter.setGridSpacing(getGridSpacingItem());

    orthogonalPatternEdgeRouter.setBendCost(getBendCostItem());
    orthogonalPatternEdgeRouter.setEdgeCrossingCost(getEdgeCrossingCostItem());
    orthogonalPatternEdgeRouter.setNodeCrossingCost(getNodeCrossingCostItem());

    // disable edge overlap costs when Edge distribution will run afterwards anyway
    orthogonalPatternEdgeRouter.setEdgeOverlapCost(0);

    router.setPathFinderStrategy(orthogonalPatternEdgeRouter);

    OrthogonalSegmentDistributionStage segmentDistributionStage = new OrthogonalSegmentDistributionStage();
    segmentDistributionStage.setAffectedEdgesDpKey(ChannelEdgeRouter.AFFECTED_EDGES_DPKEY);
    segmentDistributionStage.setPreferredDistance(getMinimumDistanceItem());
    segmentDistributionStage.setGridRouting(isActivatingGridRoutingItem());
    segmentDistributionStage.setGridSpacing(getGridSpacingItem());

    router.setEdgeDistributionStrategy(segmentDistributionStage);

    return router;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    ChannelEdgeRouterData layoutData = new ChannelEdgeRouterData();
    final IGraphSelection selection = graphComponent.getSelection();
    if (getScopeItem() == Scope.ROUTE_EDGES_AT_AFFECTED_NODES) {
      layoutData.setAffectedEdges(edge -> selection.isSelected(edge.getSourceNode()) || selection.isSelected(edge.getTargetNode()));
    } else if (getScopeItem() == Scope.ROUTE_AFFECTED_EDGES) {
      layoutData.setAffectedEdges(selection.getSelectedEdges());
    } else {
      layoutData.setAffectedEdges(graphComponent.getGraph().getEdges());
    }

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

  @Label("Costs")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object CostsGroup;

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>Channel edge router uses a rather fast but " +
           "simple algorithm for finding orthogonal edge routes. As other routing algorithms it does not change the positions of the nodes. " +
           "Compared to polyline and orthogonal edge router, edge segments can be very " +
           "close to each other and edges may also overlap with nodes. However, this algorithm is faster in many situations.</p>";
  }

  private Scope scopeItem = Scope.ROUTE_ALL_EDGES;

  @Label("Scope")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = Scope.class, stringValue = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "All Edges", value = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "Selected Edges", value = "ROUTE_AFFECTED_EDGES")
  @EnumValueAnnotation(label = "Edges at Selected Nodes", value = "ROUTE_EDGES_AT_AFFECTED_NODES")
  public final Scope getScopeItem() {
    return this.scopeItem;
  }

  @Label("Scope")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = Scope.class, stringValue = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "All Edges", value = "ROUTE_ALL_EDGES")
  @EnumValueAnnotation(label = "Selected Edges", value = "ROUTE_AFFECTED_EDGES")
  @EnumValueAnnotation(label = "Edges at Selected Nodes", value = "ROUTE_EDGES_AT_AFFECTED_NODES")
  public final void setScopeItem( Scope value ) {
    this.scopeItem = value;
  }

  private int minimumDistanceItem;

  @Label("Minimum Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0.0d, max = 100.0d)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumDistanceItem() {
    return this.minimumDistanceItem;
  }

  @Label("Minimum Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0.0d, max = 100.0d)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumDistanceItem( int value ) {
    this.minimumDistanceItem = value;
  }

  private boolean activatingGridRoutingItem;

  @Label("Route on Grid")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isActivatingGridRoutingItem() {
    return this.activatingGridRoutingItem;
  }

  @Label("Route on Grid")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setActivatingGridRoutingItem( boolean value ) {
    this.activatingGridRoutingItem = value;
  }

  private int gridSpacingItem;

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(intValue = 20, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 2, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getGridSpacingItem() {
    return this.gridSpacingItem;
  }

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(intValue = 20, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 2, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setGridSpacingItem( int value ) {
    this.gridSpacingItem = value;
  }

  public final boolean isGridSpacingItemDisabled() {
    return !isActivatingGridRoutingItem();
  }

  private double bendCostItem;

  @Label("Bend Cost")
  @OptionGroupAnnotation(name = "CostsGroup", position = 10)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getBendCostItem() {
    return this.bendCostItem;
  }

  @Label("Bend Cost")
  @OptionGroupAnnotation(name = "CostsGroup", position = 10)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setBendCostItem( double value ) {
    this.bendCostItem = value;
  }

  private double edgeCrossingCostItem;

  @Label("Edge Crossing Cost")
  @OptionGroupAnnotation(name = "CostsGroup", position = 20)
  @DefaultValue(doubleValue = 5.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getEdgeCrossingCostItem() {
    return this.edgeCrossingCostItem;
  }

  @Label("Edge Crossing Cost")
  @OptionGroupAnnotation(name = "CostsGroup", position = 20)
  @DefaultValue(doubleValue = 5.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setEdgeCrossingCostItem( double value ) {
    this.edgeCrossingCostItem = value;
  }

  private double nodeCrossingCostItem;

  @Label("Node Overlap Cost")
  @OptionGroupAnnotation(name = "CostsGroup", position = 30)
  @DefaultValue(doubleValue = 50.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getNodeCrossingCostItem() {
    return this.nodeCrossingCostItem;
  }

  @Label("Node Overlap Cost")
  @OptionGroupAnnotation(name = "CostsGroup", position = 30)
  @DefaultValue(doubleValue = 50.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setNodeCrossingCostItem( double value ) {
    this.nodeCrossingCostItem = value;
  }

}
