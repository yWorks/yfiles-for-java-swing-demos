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

import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.layout.BendConverter;
import com.yworks.yfiles.layout.CompositeLayoutStage;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.organic.RemoveOverlapsStage;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.OrganicEdgeRouterData;
import com.yworks.yfiles.layout.SequentialLayout;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.GraphComponent;
import toolkit.optionhandler.ComponentType;
import toolkit.optionhandler.ComponentTypes;
import toolkit.optionhandler.Label;
import toolkit.optionhandler.MinMax;
import toolkit.optionhandler.OptionGroupAnnotation;

/**
 * Configuration options for the layout algorithm of the same name.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
@Label("OrganicEdgeRouter")
public class OrganicEdgeRouterConfig extends LayoutConfiguration {
  /**
   * Setup default values for various configuration parameters.
   */
  public OrganicEdgeRouterConfig() {
    OrganicEdgeRouter router = new OrganicEdgeRouter();
    setRoutingOnlySelectionItem(false);
    setMinimumNodeDistanceItem(router.getMinimumDistance());
    setKeepingExistingBendsItem(router.isKeepingExistingBendsEnabled());
    setRoutingOnlyNecessaryItem(!router.isRoutingAllEdgesEnabled());
    setAllowingMovingNodesItem(false);
  }

  @Override
  protected ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent ) {
    OrganicEdgeRouter router = new OrganicEdgeRouter();
    router.setMinimumDistance(getMinimumNodeDistanceItem());
    router.setKeepingExistingBendsEnabled(isKeepingExistingBendsItem());
    router.setRoutingAllEdgesEnabled(!isRoutingOnlyNecessaryItem());

    SequentialLayout layout = new SequentialLayout();
    if (isAllowingMovingNodesItem()) {
      //if we are allowed to move nodes, we can improve the routing results by temporarily enlarging nodes and removing overlaps
      //(this strategy ensures that there is enough space for the edges)
      CompositeLayoutStage cls = new CompositeLayoutStage();
      cls.appendStage(router.createNodeEnlargementStage());
      cls.appendStage(new RemoveOverlapsStage(0));
      layout.appendLayout(cls);
    }
    if (router.isKeepingExistingBendsEnabled()) {
      //we want to keep the original bends
      BendConverter bendConverter = new BendConverter();
      bendConverter.setAffectedEdgesDpKey(OrganicEdgeRouter.AFFECTED_EDGES_DPKEY);
      bendConverter.setAffectedEdgesAdoptionEnabled(isRoutingOnlySelectionItem());
      bendConverter.setCoreLayout(router);
      layout.appendLayout(bendConverter);
    } else {
      layout.appendLayout(router);
    }

    return layout;
  }

  @Override
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    OrganicEdgeRouterData layoutData = new OrganicEdgeRouterData();

    if (isRoutingOnlySelectionItem()) {
      layoutData.setAffectedEdges(graphComponent.getSelection().getSelectedEdges());
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

  @OptionGroupAnnotation(name = "DescriptionGroup", position = 10)
  @ComponentType(ComponentTypes.FORMATTED_TEXT)
  public final String getDescriptionText() {
    return "<p>The organic edge routing algorithm routes edges in soft curves to ensure that they do not overlap with nodes." +
           " It is especially well suited for non-orthogonal, organic or circular diagrams.</p>";
  }

  private boolean routingOnlySelectionItem;

  @Label("Route Selected Edges Only")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isRoutingOnlySelectionItem() {
    return this.routingOnlySelectionItem;
  }

  @Label("Route Selected Edges Only")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 10)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setRoutingOnlySelectionItem( boolean value ) {
    this.routingOnlySelectionItem = value;
  }

  private double minimumNodeDistanceItem;

  @Label("Minimum Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final double getMinimumNodeDistanceItem() {
    return this.minimumNodeDistanceItem;
  }

  @Label("Minimum Distance")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 20)
  @DefaultValue(doubleValue = 10.0d, valueType = DefaultValue.ValueType.DOUBLE_TYPE)
  @MinMax(min = 0, max = 100)
  @ComponentType(ComponentTypes.SLIDER)
  public final void setMinimumNodeDistanceItem( double value ) {
    this.minimumNodeDistanceItem = value;
  }

  private boolean keepingExistingBendsItem;

  @Label("Keep Existing Bends")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isKeepingExistingBendsItem() {
    return this.keepingExistingBendsItem;
  }

  @Label("Keep Existing Bends")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 30)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setKeepingExistingBendsItem( boolean value ) {
    this.keepingExistingBendsItem = value;
  }

  private boolean routingOnlyNecessaryItem;

  @Label("Route Only Necessary")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isRoutingOnlyNecessaryItem() {
    return this.routingOnlyNecessaryItem;
  }

  @Label("Route Only Necessary")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 40)
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setRoutingOnlyNecessaryItem( boolean value ) {
    this.routingOnlyNecessaryItem = value;
  }

  private boolean allowingMovingNodesItem;

  @Label("Allow Moving Nodes")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAllowingMovingNodesItem() {
    return this.allowingMovingNodesItem;
  }

  @Label("Allow Moving Nodes")
  @OptionGroupAnnotation(name = "LayoutGroup", position = 50)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAllowingMovingNodesItem( boolean value ) {
    this.allowingMovingNodesItem = value;
  }

}
