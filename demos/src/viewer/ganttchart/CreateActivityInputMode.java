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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.GraphModelManager;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualTemplate;
import com.yworks.yfiles.view.input.MarqueeSelectionEventArgs;
import com.yworks.yfiles.view.input.MarqueeSelectionInputMode;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * Creates new activity nodes when dragging the mouse over empty space.
 * For this purpose, the default template (blue marquee/rubber band rectangle)
 * is switched off and replaced by a template that does not create any visuals.
 * While dragging, a temporary dummy node is rendered. This node is not part of
 * the graph, but only used for visualization purposes. When the drag gesture
 * is finished, a node is created in the area defined by the task in which the
 * gesture started and the x-coordinates of the drag area.
 */
public class CreateActivityInputMode extends MarqueeSelectionInputMode {
  private final GanttDataUtil data;
  private final SimpleNode dummyNode;
  private Task task;
  private ICanvasObject canvasObject;
  private Consumer<INode> onNodeCreated;

  /**
   * Initializes a new {@code CreateActivityInputMode} instance.
   */
  public CreateActivityInputMode( GanttDataUtil data ) {
    this.data = data;
    this.dummyNode = new SimpleNode();
    this.onNodeCreated = node -> {};
    this.setTemplate(new HideMarquee());
  }

  /**
   * Returns the consumer that is invoked each time a new activity node is
   * created. Interested parties may specify a custom consumer to react on node
   * creation. 
   * The default consumer will not do anything.
   */
  public Consumer<INode> getOnNodeCreated() {
    return onNodeCreated;
  }

  /**
   * Specifies the consumer that is invoked each time a new activity node is
   * created. Interested parties may specify a custom consumer to react on node
   * creation. 
   * The default consumer will not do anything.
   */
  public void setOnNodeCreated( Consumer<INode> onNodeCreated ) {
    this.onNodeCreated = onNodeCreated;
  }


  /**
   * Creates the dummy node and adds the visualization to the graph control.
   */
  @Override
  protected void onDragStarted( MarqueeSelectionEventArgs args ) {
    // get the drag rectangle
    RectD marqueeRectangle = args.getRectangle();
    // get the index of the task at the mouse position
    task = data.getTask((int) marqueeRectangle.getY());

    // update the bounds of the dummy node
    RectD layout = getDummyNodeLayout(marqueeRectangle);

    LocalDateTime endDate = data.getDate(layout.x + layout.width);
    Activity activity = new Activity(task, data.getDate(layout.x), endDate);
    activity.setLeadTime(0);
    activity.setFollowUpTime(0);
    dummyNode.setLayout(layout);
    dummyNode.setTag(activity);

    Task t = data.getTask(layout.y);
    INodeStyle style = t.getColor() != null
      ? new ActivityNodeStyle()
      : args.getContext().getGraph().getNodeDefaults().getStyle();
    dummyNode.setStyle(style);

    // add the dummy node visualization to the graph control
    CanvasComponent graphComponent = args.getContext().getCanvasComponent();
    canvasObject = graphComponent.getContentGroup().addChild(dummyNode,
      GraphModelManager.DEFAULT_NODE_DESCRIPTOR);

    // show a time info tool tip
    String text = data.getDateString(endDate);
    ToolTipHelper.showTimeInfo(dummyNode, text, true);

    super.onDragStarted(args);
  }

  /**
   * Updates the dummy node visualization.
   */
  @Override
  protected void onDragging( MarqueeSelectionEventArgs args ) {
    RectD layout = getDummyNodeLayout(args.getRectangle());
    dummyNode.setLayout(layout);
    Activity activity = ((Activity) dummyNode.getTag());
    activity.setStartDate(data.getDate(layout.x));
    LocalDateTime endDate = data.getDate(layout.x + layout.width);
    activity.setEndDate(endDate);

    // update time info tooltip with the new text
    String text = data.getDateString(endDate);
    ToolTipHelper.showTimeInfo(dummyNode, text, true);

    super.onDragging(args);
  }

  /**
   * Removes the dummy node visualization and creates a new activity node in
   * its place.
   */
  @Override
  protected void onDragFinished( MarqueeSelectionEventArgs args ) {
    // remove the canvas object holding the dummy node visualization
    if (canvasObject != null) {
      canvasObject.remove();
      canvasObject = null;
    }

    IGraph graph = args.getContext().getGraph();
    RectD layout = getDummyNodeLayout(args.getRectangle());

    // create a new activity node
    INode node = graph.createNode(layout, null, dummyNode.getTag());
    String activityName = ((Activity) dummyNode.getTag()).getName();
    graph.addLabel(node, activityName, ActivityLabelModel.DEFAULT);

    // remove the time info tooltip
    ToolTipHelper.removeTimeInfo();

    onNodeCreated.accept(node);

    super.onDragFinished(args);
  }

  /**
   * Removes the dummy node.
   * @param args The event arguments that contains context information.
   */
  @Override
  protected void onDragCanceled( MarqueeSelectionEventArgs args ) {
    if (canvasObject != null) {
      canvasObject.remove();
    }
    ToolTipHelper.removeTimeInfo();

    super.onDragCanceled(args);
  }

  /**
   * Determines the bounds for the dummy nodes from the current {@link #task}
   * and the x-coordinates of the given marquee rectangle.
   */
  public RectD getDummyNodeLayout( RectD marqueeRectangle ) {
    double x = marqueeRectangle.getX();
    // get the y coordinate of the task the drag was started in
    double y = data.getTaskY(task) + GanttDataUtil.ACTIVITY_SPACING;
    double width = marqueeRectangle.getWidth();
    double height = GanttDataUtil.ACTIVITY_HEIGHT;
    return new RectD(x, y, width, height);
  }



  /**
   * Prevents the selection marquee from being shown when creating new activity
   * nodes.
   */
  private static final class HideMarquee implements IVisualTemplate {
    /**
     * Returns {@code null} to prevent any selection marquee from being shown.
     */
    @Override
    public IVisual createVisual(
      IRenderContext context, RectD bounds, Object dataObject
    ) {
      return null;
    }

    /**
     * Returns {@code null} to prevent any selection marquee from being shown.
     */
    @Override
    public IVisual updateVisual(
      IRenderContext context, IVisual oldVisual, RectD bounds, Object dataObject
    ) {
      return null;
    }
  }
}
