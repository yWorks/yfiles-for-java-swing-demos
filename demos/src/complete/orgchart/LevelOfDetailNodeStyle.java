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
package complete.orgchart;

import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ItemHoverInputMode;

/**
 * An {@link com.yworks.yfiles.graph.styles.AbstractNodeStyle} implementation that returns either an
 * DetailVisual, IntermediateVisual or OverviewVisual for the given INode dependent on the current
 * zoom value of the GraphComponent.
 */
public class LevelOfDetailNodeStyle extends AbstractNodeStyle {

  // thresholds for the zoom level to choose the correct level of detail.
  private static final double DETAIL_THRESHOLD = 0.7;
  private static final double INTERMEDIATE_THRESHOLD = 0.4;

  /**
   * Default constructor for subclasses of AbstractNodeStyle that in our case calls the superclass constructor with
   * the type that indicates the return value and parameter type for the methods #createVisual and #updateVisual.
   * In this case this is the {@link OrgChartVisual} class because this is the common superclass of the three possible return values
   * DetailVisual, IntermediateVisual and OverviewVisual.
   */
  public LevelOfDetailNodeStyle() {
    super();
  }

  @Override
  protected OrgChartVisual createVisual(IRenderContext context, INode node) {
    double zoom = context.getZoom();
    GraphComponent component = (GraphComponent) context.getCanvasComponent();
    ItemHoverInputMode hoverInputMode = ((GraphViewerInputMode) component.getInputMode()).getItemHoverInputMode();

    Employee employee = (Employee) node.getTag();
    IRectangle nodeLayout = node.getLayout();
    boolean nodeIsFocused = component.getCurrentItem() == node;
    boolean nodeIsHovered = hoverInputMode.getCurrentHoverItem() == node;

    // build a visual that is appropriate for the given zoom level
    if (zoom >= DETAIL_THRESHOLD) {
      return new DetailVisual(employee, nodeLayout, nodeIsFocused, nodeIsHovered);
    } else if (zoom >= INTERMEDIATE_THRESHOLD) {
      return new IntermediateVisual(employee, nodeLayout, nodeIsFocused, nodeIsHovered);
    } else {
      return new OverviewVisual(employee, nodeLayout, nodeIsFocused, nodeIsHovered);
    }
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (!(oldVisual instanceof OrgChartVisual)) {
      return createVisual(context, node);
    }

    OrgChartVisual orgChartVisual = (OrgChartVisual) oldVisual;

    double zoom = context.getZoom();

    // find out if we can re-use the old visual or if we have to return a new one.
    // this is the case when the current zoom level doesn't match the current type of the old visual.
    if (   (orgChartVisual instanceof DetailVisual && zoom >= DETAIL_THRESHOLD)
        || (orgChartVisual instanceof IntermediateVisual && DETAIL_THRESHOLD >= zoom && zoom >= INTERMEDIATE_THRESHOLD)
        || (orgChartVisual instanceof OverviewVisual && zoom < INTERMEDIATE_THRESHOLD)) {

      GraphComponent component = (GraphComponent) context.getCanvasComponent();
      ItemHoverInputMode hoverInputMode = ((GraphViewerInputMode) component.getInputMode()).getItemHoverInputMode();

      IRectangle nodeLayout = node.getLayout();
      boolean nodeIsFocused = component.getCurrentItem() == node;
      boolean nodeIsHovered = hoverInputMode.getCurrentHoverItem() == node;

      orgChartVisual.setLayout(nodeLayout);
      orgChartVisual.setFocused(nodeIsFocused);
      orgChartVisual.setHovered(nodeIsHovered);
      return orgChartVisual;
    } else {
      // in this case we need another visual for the current zoom level, so simply delegate to the #createVisual method.
      return createVisual(context, node);
    }
  }

}
