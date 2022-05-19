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
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.LayoutGraphHider;
import com.yworks.yfiles.algorithms.NodeDpKey;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.AbstractLayoutStage;
import com.yworks.yfiles.layout.GenericLayoutData;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.ItemCollection;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.router.BusDescriptor;
import com.yworks.yfiles.layout.router.BusRouter;
import com.yworks.yfiles.layout.router.BusRouterData;
import com.yworks.yfiles.layout.router.RoutingPolicy;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IGraphSelection;
import java.util.HashSet;
import java.util.function.Predicate;
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
@Label("BusRouter")
public class BusEdgeRouterConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public BusEdgeRouterConfig() {
    BusRouter router = new BusRouter();

    setScopeItem(EnumScope.ALL);
    setBusesItem(EnumBuses.LABEL);
    setGridEnabledItem(router.isGridRoutingEnabled());
    setGridSpacingItem(router.getGridSpacing());
    setMinimumDistanceToNodesItem(router.getMinimumDistanceToNode());
    setMinimumDistanceToEdgesItem(router.getMinimumDistanceToEdge());

    setPreferredBackboneSegmentCountItem(1);
    setMinimumBackboneSegmentLengthItem(router.getMinimumBackboneSegmentLength());

    setCrossingCostItem(router.getCrossingCost());
    setReroutingCrossingEdgesItem(router.isReroutingEnabled());
    setMinimumBusConnectionsCountItem(6);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    BusRouter router = new BusRouter();

    switch (getScopeItem()) {
      case ALL:
        router.setScope(Scope.ROUTE_ALL_EDGES);
        break;
      case PARTIAL:
      case SUBSET:
      case SUBSET_BUS:
        router.setScope(Scope.ROUTE_AFFECTED_EDGES);
        break;
      default:
        router.setScope(Scope.ROUTE_ALL_EDGES);
        break;
    }
    router.setGridRoutingEnabled(isGridEnabledItem());
    router.setGridSpacing(getGridSpacingItem());
    router.setMinimumDistanceToNode(getMinimumDistanceToNodesItem());
    router.setMinimumDistanceToEdge(getMinimumDistanceToEdgesItem());
    router.setPreferredBackboneSegmentCount(getPreferredBackboneSegmentCountItem());
    router.setMinimumBackboneSegmentLength(getMinimumBackboneSegmentLengthItem());
    router.setMinimumBusConnectionsCount(getMinimumBusConnectionsCountItem());
    router.setCrossingCost(getCrossingCostItem());
    router.setReroutingEnabled(isReroutingCrossingEdgesItem());

    if (getScopeItem() == EnumScope.PARTIAL) {
      return new HideNonOrthogonalEdgesStage(router);
    }

    return router;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    BusRouterData layoutData = new BusRouterData();
    final IGraph graph = graphComponent.getGraph();
    IGraphSelection graphSelection = graphComponent.getSelection();
    boolean scopePartial = getScopeItem() == EnumScope.PARTIAL;

    final IMapper<IEdge, BusDescriptor> busIds = layoutData.getEdgeDescriptors().getMapper();

    for (IEdge edge : graph.getEdges()) {
      boolean isFixed = scopePartial && !graphSelection.isSelected(edge.getSourceNode()) && !graphSelection.isSelected(edge.getTargetNode());
      Object id = getBusId(edge, getBusesItem());
      BusDescriptor descriptor = new BusDescriptor(id, isFixed);
      descriptor.setRoutingPolicy(getRoutingPolicyItem());
      busIds.setValue(edge, descriptor);
    }

    final HashSet<Object> selectedIds = new HashSet<Object>();
    switch (getScopeItem()) {
      case SUBSET:
        layoutData.setAffectedEdges(graphSelection::isSelected);
        break;
      case SUBSET_BUS:
        for (IEdge edge : graph.getEdges()) {
          if (graphSelection.isSelected(edge)) {
            selectedIds.add(busIds.getValue(edge).getBusId());
          }
        }
        layoutData.setAffectedEdges(edge ->
            selectedIds.contains(busIds.getValue(edge).getBusId())
        );
        break;
      case PARTIAL:
        for (INode node : graph.getNodes()) {
          if (graphSelection.isSelected(node)) {
            for (IEdge edge : graph.edgesAt(node)) {
              selectedIds.add(busIds.getValue(edge).getBusId());
            }
          }
        }
        layoutData.setAffectedEdges(edge ->
            selectedIds.contains(busIds.getValue(edge).getBusId())
        );
        GenericLayoutData hideNonOrthogonalEdgesLayoutData = new GenericLayoutData();
        hideNonOrthogonalEdgesLayoutData.addItemCollection(HideNonOrthogonalEdgesStage.SELECTED_NODES_DP_KEY, (ItemCollection<INode>)null).setSource(graphSelection.getSelectedNodes());
        return layoutData.combineWith(hideNonOrthogonalEdgesLayoutData);
    }

    return layoutData;
  }

  private static Object getBusId( IEdge e, EnumBuses busDetermination ) {
    switch (busDetermination) {
      case LABEL:
        return e.getLabels().size() > 0 ? e.getLabels().getItem(0).getText() : "";
      default:
        return SINGLE_BUS_ID;
    }
  }

  private static final Object SINGLE_BUS_ID = new Object();

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @Label("Layout")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LayoutGroup;

  @Label("Backbone Selection")
  @OptionGroupAnnotation(name = "RootGroup", position = 20)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object SelectionGroup;

  @Label("Routing and Recombination")
  @OptionGroupAnnotation(name = "RootGroup", position = 30)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object RoutingGroup;

  public enum EnumScope {
    ALL(0),

    SUBSET(1),

    SUBSET_BUS(2),

    PARTIAL(3);

    private final int value;

    private EnumScope( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumScope fromOrdinal( int ordinal ) {
      for (EnumScope current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  public enum EnumBuses {
    SINGLE(0),

    LABEL(1),

    TAG(2);

    private final int value;

    private EnumBuses( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumBuses fromOrdinal( int ordinal ) {
      for (EnumBuses current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>Orthogonal bus-style edge routing combines the (likely confusing) mass of edges in parts " +
           "of a diagram where each node is connected to each other node in a concise, orthogonal tree-like structure. " +
           "This algorithm does not change the positions of the nodes.</p>" +
           "<p>The algorithm aims to find routes where all edge paths share preferably long segments. On those long line segments " +
           "ideally all but the first and last segments of all edge paths are drawn on top of each other, with short " +
           "connections branching off to the nodes. The short connections bundle the respective first or last segments of a " +
           "node's incident edges.</p>";
  }

  private EnumScope scopeItem = EnumScope.ALL;

  @Label("Scope")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumScope.class, stringValue = "ALL")
  @EnumValueAnnotation(label = "All Edges", value = "ALL")
  @EnumValueAnnotation(label = "Reroute to Selected Nodes", value = "PARTIAL")
  @EnumValueAnnotation(label = "Buses of Selected Edges", value = "SUBSET_BUS")
  @EnumValueAnnotation(label = "Selected Edges", value = "SUBSET")
  public final EnumScope getScopeItem() {
    return this.scopeItem;
  }

  @Label("Scope")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumScope.class, stringValue = "ALL")
  @EnumValueAnnotation(label = "All Edges", value = "ALL")
  @EnumValueAnnotation(label = "Reroute to Selected Nodes", value = "PARTIAL")
  @EnumValueAnnotation(label = "Buses of Selected Edges", value = "SUBSET_BUS")
  @EnumValueAnnotation(label = "Selected Edges", value = "SUBSET")
  public final void setScopeItem( EnumScope value ) {
    this.scopeItem = value;
  }

  private EnumBuses busesItem = EnumBuses.SINGLE;

  @Label("Bus Membership")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumBuses.class, stringValue = "LABEL")
  @EnumValueAnnotation(label = "Single Bus", value = "SINGLE")
  @EnumValueAnnotation(label = "Defined by First Label", value = "LABEL")
  @EnumValueAnnotation(label = "Defined by User Tag", value = "TAG")
  public final EnumBuses getBusesItem() {
    return this.busesItem;
  }

  @Label("Bus Membership")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumBuses.class, stringValue = "LABEL")
  @EnumValueAnnotation(label = "Single Bus", value = "SINGLE")
  @EnumValueAnnotation(label = "Defined by First Label", value = "LABEL")
  @EnumValueAnnotation(label = "Defined by User Tag", value = "TAG")
  public final void setBusesItem( EnumBuses value ) {
    this.busesItem = value;
  }

  private boolean gridEnabledItem;

  @Label("Route on Grid")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isGridEnabledItem() {
    return this.gridEnabledItem;
  }

  @Label("Route on Grid")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setGridEnabledItem( boolean value ) {
    this.gridEnabledItem = value;
  }

  private int gridSpacingItem;

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 2, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getGridSpacingItem() {
    return this.gridSpacingItem;
  }

  @Label("Grid Spacing")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 2, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setGridSpacingItem( int value ) {
    this.gridSpacingItem = value;
  }

  public final boolean isGridSpacingItemDisabled() {
    return !isGridEnabledItem();
  }

  private int minimumDistanceToNodesItem;

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumDistanceToNodesItem() {
    return this.minimumDistanceToNodesItem;
  }

  @Label("Minimum Node Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumDistanceToNodesItem( int value ) {
    this.minimumDistanceToNodesItem = value;
  }

  private int minimumDistanceToEdgesItem;

  @Label("Minimum Edge Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(intValue = 5, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumDistanceToEdgesItem() {
    return this.minimumDistanceToEdgesItem;
  }

  @Label("Minimum Edge Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(intValue = 5, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumDistanceToEdgesItem( int value ) {
    this.minimumDistanceToEdgesItem = value;
  }

  private int preferredBackboneSegmentCountItem;

  @Label("Preferred Segment Count")
  @OptionGroupAnnotation(name = "SelectionGroup", position = 10)
  @DefaultValue(intValue = 1, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 10)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getPreferredBackboneSegmentCountItem() {
    return this.preferredBackboneSegmentCountItem;
  }

  @Label("Preferred Segment Count")
  @OptionGroupAnnotation(name = "SelectionGroup", position = 10)
  @DefaultValue(intValue = 1, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 10)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setPreferredBackboneSegmentCountItem( int value ) {
    this.preferredBackboneSegmentCountItem = value;
  }

  private double minimumBackboneSegmentLengthItem;

  @Label("Minimum Segment Length")
  @OptionGroupAnnotation(name = "SelectionGroup", position = 20)
  @DefaultValue(doubleValue = 100.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumBackboneSegmentLengthItem() {
    return this.minimumBackboneSegmentLengthItem;
  }

  @Label("Minimum Segment Length")
  @OptionGroupAnnotation(name = "SelectionGroup", position = 20)
  @DefaultValue(doubleValue = 100.0, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 500)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumBackboneSegmentLengthItem( double value ) {
    this.minimumBackboneSegmentLengthItem = value;
  }

  private RoutingPolicy routingPolicyItem = RoutingPolicy.ALWAYS;

  @Label("Routing Policy")
  @OptionGroupAnnotation(name = "RoutingGroup", position = 5)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = RoutingPolicy.class, stringValue = "ALWAYS")
  @EnumValueAnnotation(label = "Always", value = "ALWAYS")
  @EnumValueAnnotation(label = "Path As Needed", value = "PATH_AS_NEEDED")
  public final RoutingPolicy getRoutingPolicyItem() {
    return this.routingPolicyItem;
  }

  @Label("Routing Policy")
  @OptionGroupAnnotation(name = "RoutingGroup", position = 5)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = RoutingPolicy.class, stringValue = "ALWAYS")
  @EnumValueAnnotation(label = "Always", value = "ALWAYS")
  @EnumValueAnnotation(label = "Path As Needed", value = "PATH_AS_NEEDED")
  public final void setRoutingPolicyItem( RoutingPolicy value ) {
    this.routingPolicyItem = value;
  }

  private double crossingCostItem;

  @Label("Crossing Cost")
  @OptionGroupAnnotation(name = "RoutingGroup", position = 10)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 50)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getCrossingCostItem() {
    return this.crossingCostItem;
  }

  @Label("Crossing Cost")
  @OptionGroupAnnotation(name = "RoutingGroup", position = 10)
  @DefaultValue(doubleValue = 1.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 1, max = 50)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setCrossingCostItem( double value ) {
    this.crossingCostItem = value;
  }

  private boolean reroutingCrossingEdgesItem;

  @Label("Reroute Crossing Edges")
  @OptionGroupAnnotation(name = "RoutingGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isReroutingCrossingEdgesItem() {
    return this.reroutingCrossingEdgesItem;
  }

  @Label("Reroute Crossing Edges")
  @OptionGroupAnnotation(name = "RoutingGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setReroutingCrossingEdgesItem( boolean value ) {
    this.reroutingCrossingEdgesItem = value;
  }

  private int minimumBusConnectionsCountItem;

  @Label("Minimum Bus Connections Count")
  @OptionGroupAnnotation(name = "RoutingGroup", position = 30)
  @DefaultValue(intValue = 6, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getMinimumBusConnectionsCountItem() {
    return this.minimumBusConnectionsCountItem;
  }

  @Label("Minimum Bus Connections Count")
  @OptionGroupAnnotation(name = "RoutingGroup", position = 30)
  @DefaultValue(intValue = 6, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 1, max = 20)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumBusConnectionsCountItem( int value ) {
    this.minimumBusConnectionsCountItem = value;
  }

  private static final class HideNonOrthogonalEdgesStage extends AbstractLayoutStage {
    public HideNonOrthogonalEdgesStage( ILayoutAlgorithm layout ) {
      super(layout);
    }

    public static final NodeDpKey<Boolean> SELECTED_NODES_DP_KEY = new NodeDpKey<Boolean>(Boolean.class, HideNonOrthogonalEdgesStage.class, "BusEdgeRouterConfig.SELECTED_NODES_DP_KEY");

    @Override
    public void applyLayout( LayoutGraph graph ) {
      IDataProvider affectedEdges = graph.getDataProvider(BusRouter.DEFAULT_AFFECTED_EDGES_DPKEY);
      IDataProvider selectedNodes = graph.getDataProvider(SELECTED_NODES_DP_KEY);
      LayoutGraphHider hider = new LayoutGraphHider(graph);
      HashSet<Edge> hiddenEdges = new HashSet<Edge>();
      for (Edge edge : graph.getEdges()) {
        if (affectedEdges.getBool(edge) && selectedNodes != null && !selectedNodes.getBool(edge.source()) && !selectedNodes.getBool(edge.target())) {
          YPoint[] path = graph.getPath(edge).toArray();
          for (int i = 1; i < path.length; i++) {
            YPoint p1 = path[i - 1];
            YPoint p2 = path[i];
            if (Math.abs(p1.getX() - p2.getX()) >= 0.0001 && Math.abs(p1.getY() - p2.getY()) >= 0.0001) {
              hiddenEdges.add(edge);
            }
          }
        }
      }
      for (Edge edge : hiddenEdges) {
        hider.hide(edge);
      }

      applyLayoutCore(graph);

      hider.unhideEdges();
    }

  }

}
