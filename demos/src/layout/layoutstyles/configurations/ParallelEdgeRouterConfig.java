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
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.ParallelEdgeRouter;
import com.yworks.yfiles.layout.ParallelEdgeRouterData;
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
@Label("ParallelEdgeRouter")
public class ParallelEdgeRouterConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public ParallelEdgeRouterConfig() {
    ParallelEdgeRouter router = new ParallelEdgeRouter();
    setScopeItem(EnumScope.SCOPE_ALL_EDGES);
    setUsingSelectedEdgesAsMasterItem(false);
    setConsideringEdgeDirectionItem(router.isDirectedModeEnabled());
    setUsingAdaptiveLineDistanceItem(router.isAdaptiveLineDistancesEnabled());
    setLineDistanceItem((int)router.getLineDistance());
    setJoiningEndsItem(router.isJoiningEndsEnabled());
    setJoinDistanceItem(router.getAbsJoinEndDistance());
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    ParallelEdgeRouter router = new ParallelEdgeRouter();
    router.setLeadingEdgeAdjustmentEnabled(false);
    router.setDirectedModeEnabled(isConsideringEdgeDirectionItem());
    router.setAdaptiveLineDistancesEnabled(isUsingAdaptiveLineDistanceItem());
    router.setLineDistance(getLineDistanceItem());
    router.setJoiningEndsEnabled(isJoiningEndsItem());
    router.setAbsJoinEndDistance(getJoinDistanceItem());

    return router;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    ParallelEdgeRouterData layoutData = new ParallelEdgeRouterData();
    final IGraphSelection selection = graphComponent.getSelection();

    if (getScopeItem() == EnumScope.SCOPE_AT_SELECTED_NODES) {
      layoutData.setAffectedEdges(edge -> selection.isSelected(edge.getSourceNode()) || selection.isSelected(edge.getTargetNode()));
    } else if (getScopeItem() == EnumScope.SCOPE_SELECTED_EDGES) {
      layoutData.setAffectedEdges(selection.getSelectedEdges());
    } else {
      layoutData.setAffectedEdges(edge -> Boolean.TRUE);
    }

    if (isUsingSelectedEdgesAsMasterItem()) {
      layoutData.setLeadingEdges(selection.getSelectedEdges());
    }

    return layoutData;
  }

  @Label("Description")
  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object DescriptionGroup;

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>The parallel edge routing algorithm routes parallel edges which connect the same pair of nodes in a graph." +
           " It is often used as layout stage for other layout algorithms to handle the parallel edges for those.</p>";
  }

  @Label("General")
  @OptionGroupAnnotation(name = "RootGroup", position = 10)
  @ComponentType(ComponentTypes.OPTION_GROUP)
  public Object LayoutGroup;

  public enum EnumScope {
    SCOPE_ALL_EDGES(0),

    SCOPE_SELECTED_EDGES(1),

    SCOPE_AT_SELECTED_NODES(2);

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

  private EnumScope scopeItem = EnumScope.SCOPE_ALL_EDGES;

  @Label("Scope")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumScope.class, stringValue = "SCOPE_ALL_EDGES")
  @EnumValueAnnotation(label = "All Edges", value = "SCOPE_ALL_EDGES")
  @EnumValueAnnotation(label = "Selected Edges", value = "SCOPE_SELECTED_EDGES")
  @EnumValueAnnotation(label = "Edges at Selected Nodes", value = "SCOPE_AT_SELECTED_NODES")
  public final EnumScope getScopeItem() {
    return this.scopeItem;
  }

  @Label("Scope")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EnumScope.class, stringValue = "SCOPE_ALL_EDGES")
  @EnumValueAnnotation(label = "All Edges", value = "SCOPE_ALL_EDGES")
  @EnumValueAnnotation(label = "Selected Edges", value = "SCOPE_SELECTED_EDGES")
  @EnumValueAnnotation(label = "Edges at Selected Nodes", value = "SCOPE_AT_SELECTED_NODES")
  public final void setScopeItem( EnumScope value ) {
    this.scopeItem = value;
  }

  private boolean usingSelectedEdgesAsMasterItem;

  @Label("Use Selected Edges As Leading Edges")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUsingSelectedEdgesAsMasterItem() {
    return this.usingSelectedEdgesAsMasterItem;
  }

  @Label("Use Selected Edges As Leading Edges")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUsingSelectedEdgesAsMasterItem( boolean value ) {
    this.usingSelectedEdgesAsMasterItem = value;
  }

  private boolean consideringEdgeDirectionItem;

  @Label("Consider Edge Direction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isConsideringEdgeDirectionItem() {
    return this.consideringEdgeDirectionItem;
  }

  @Label("Consider Edge Direction")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setConsideringEdgeDirectionItem( boolean value ) {
    this.consideringEdgeDirectionItem = value;
  }

  private boolean usingAdaptiveLineDistanceItem;

  @Label("Use Adaptive Line Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isUsingAdaptiveLineDistanceItem() {
    return this.usingAdaptiveLineDistanceItem;
  }

  @Label("Use Adaptive Line Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setUsingAdaptiveLineDistanceItem( boolean value ) {
    this.usingAdaptiveLineDistanceItem = value;
  }

  private int lineDistanceItem;

  @Label("Line Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 50)
  @ComponentType(ComponentTypes.SLIDER)
  public final int getLineDistanceItem() {
    return this.lineDistanceItem;
  }

  @Label("Line Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(intValue = 10, valueType = DefaultValue.ValueType.INT_TYPE)
  @MinMax(min = 0, max = 50)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setLineDistanceItem( int value ) {
    this.lineDistanceItem = value;
  }

  private boolean joiningEndsItem;

  @Label("Join Ends")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isJoiningEndsItem() {
    return this.joiningEndsItem;
  }

  @Label("Join Ends")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 60)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setJoiningEndsItem( boolean value ) {
    this.joiningEndsItem = value;
  }

  private double joinDistanceItem;

  @Label("Join Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(doubleValue = 20.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 50)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getJoinDistanceItem() {
    return this.joinDistanceItem;
  }

  @Label("Join Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 70)
  @DefaultValue(doubleValue = 20.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 50)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setJoinDistanceItem( double value ) {
    this.joinDistanceItem = value;
  }

  public final boolean isJoinDistanceItemDisabled() {
    return !isJoiningEndsItem();
  }

}
