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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.labelmodels.FreeLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.HtmlLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.TextTrimming;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.VerticalAlignment;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Provides tool tips for detail information on activities.
 */
class ToolTipHelper {
  /**
   * Shared singleton instance.
   */
  private static ToolTipHelper INSTANCE;


  /**
   * The canvas object group that holds the tool tip visualizations.
   */
  private ICanvasObjectGroup infoGroup;

  /**
   * The label used to create the activity info tool tip visualization.
   */
  private SimpleLabel activityInfoLabel;

  /**
   * The canvas object that holds the activity info tool tip visualization.
   */
  private ICanvasObject activityInfo;

  /**
   * The label used to create the time info tool tip visualization.
   */
  private SimpleLabel timeInfoLabel;

  /**
   * The canvas object that holds the time info tool tip visualization.
   */
  private ICanvasObject timeInfo;


  private ToolTipHelper() {
  }


  /**
   * Creates the activity info tool tip visualization.
   * @param activity for which the infos should be shown
   * @param center of the clicked node
   * @param viewPoint the viewPoint coordinates
   * @param viewPortWidth the width of the view port
   */
  private void createActivityInfoCanvasObjects(Activity activity, PointD center, PointD viewPoint, double viewPortWidth) {
    StringBuilder sb = new StringBuilder("<html><body>");
    sb.append("<b><u>").append(activity.getName()).append("</u></b>");
    sb.append("<p>Start Time: ").append(format(activity.getStartDate())).append("</p>");
    sb.append("<p>End Time: ").append(format(activity.getEndDate())).append("</p>");
    sb.append("<p>Lead Time: ").append(Math.floor(activity.getLeadTime())).append(" h</p>");
    sb.append("<p>Follow-up Time: ").append(Math.floor(activity.getFollowUpTime())).append(" h</p>");
    sb.append("<p>Total Duration: ").append(Math.floor(GanttDataUtil.getTotalActivityDuration(activity))).append(" h</p>");
    sb.append("<p>Task: ").append(activity.getTask().getName()).append("</p></body></html>");

    // configure label style for displaying HTML-formatted text
    HtmlLabelStyle style = new HtmlLabelStyle();
    float oldFontSize = style.getFont().getSize();
    style.setFont(style.getFont().deriveFont(oldFontSize + 2));
    style.setInsets(new InsetsD(20));
    style.setBackgroundPaint(activity.getTask().getColor());
    style.setTextColor(Color.WHITE);

    FreeLabelModel model = new FreeLabelModel();
    activityInfoLabel = new SimpleLabel(null, sb.toString(), model.createAbsolute(PointD.ORIGIN));

    // apply styles and create the activity info group
    activityInfoLabel.setStyle(style);
    activityInfo = infoGroup.addChild(activityInfoLabel, ICanvasObjectDescriptor.ALWAYS_DIRTY_LOOKUP);

    // set size for info
    SizeD size = style.getRenderer().getPreferredSize(activityInfoLabel, style);
    activityInfoLabel.setPreferredSize(size);

    // check if there is enough space to display the tool tip above the activity node
    PointD location;
    if (center.getY() - GanttDataUtil.ACTIVITY_HEIGHT - size.getHeight() - 10 < viewPoint.getY()) {
      // not enough speace above - draw below node
      location = new PointD(
        center.getX() - size.getWidth() / 2,
        center.getY() + GanttDataUtil.ACTIVITY_HEIGHT + size.getHeight());
    } else {
      // enough space above - draw above node
      location = new PointD(
        center.getX() - size.getWidth() / 2,
        center.getY() - GanttDataUtil.ACTIVITY_HEIGHT);
    }

    // fit label into viewport
    if (location.getX() < viewPoint.getX()) {
      location = new PointD(viewPoint.getX(), location.getY());
    }
    else if (location.getX() + size.width > viewPoint.getX() + viewPortWidth) {
      location = new PointD(viewPoint.getX() + (viewPortWidth - size.width), location.getY());
    }

    activityInfoLabel.setLayoutParameter(model.createAbsolute(location));

    infoGroup.toFront();
  }

  /**
   * Removes the activity info group and label.
   */
  private void removeActivityInfoImpl() {
    ICanvasObject group = activityInfo;
    if (group != null) {
      group.remove();
      activityInfo = null;
    }
    activityInfoLabel = null;
  }

  /**
   * Returns a human-readable representation of the given date-time.
   */
  private String format( LocalDateTime date ) {
    return date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu, HH:mm:ss"));
  }


  /**
   * Creates the time info tool tip visualization.
   */
  private void createTimeInfoCanvasObjects() {
    // configure label style for displaying plain text
    DefaultLabelStyle style = new DefaultLabelStyle();
    style.setTextAlignment(TextAlignment.CENTER);
    style.setTextWrapping(TextWrapping.NO_WRAP);
    style.setTextTrimming(TextTrimming.NONE);
    style.setVerticalTextAlignment(VerticalAlignment.CENTER);
    style.setInsets(new InsetsD(2));
    style.setTextPaint(Color.BLACK);
    style.setBackgroundPaint(new Color(255, 255, 255, 128));
    style.setBackgroundPen(Pen.getBlack());

    timeInfoLabel = new SimpleLabel(null, "", new FreeLabelModel().createAbsolute(PointD.ORIGIN));
    timeInfoLabel.setStyle(style);

    timeInfo = infoGroup.addChild(timeInfoLabel, ICanvasObjectDescriptor.ALWAYS_DIRTY_LOOKUP);
  }

  /**
   * Updates the time info.
   */
  private void updateTimeInfoImpl(
    String text, PointD location, boolean isFollowUp
  ) {
    timeInfoLabel.setText(text);

    ILabelStyle style = timeInfoLabel.getStyle();
    SizeD size = style.getRenderer().getPreferredSize(timeInfoLabel, style);
    timeInfoLabel.setPreferredSize(size);

    FreeLabelModel model = (FreeLabelModel) timeInfoLabel.getLayoutParameter().getModel();
    double timeInfoOffset = 4;
    if (isFollowUp) {
      // draw on the right side
      PointD anchor = new PointD(location.x + timeInfoOffset, location.y);
      timeInfoLabel.setLayoutParameter(model.createAbsolute(anchor));
    } else {
      // draw on the left side
      PointD anchor = new PointD(location.x - size.width - timeInfoOffset, location.y);
      timeInfoLabel.setLayoutParameter(model.createAbsolute(anchor));
    }

    infoGroup.toFront();
  }

  /**
   * Removes the time info.
   */
  private void removeTimeInfoImpl() {
    ICanvasObject group = timeInfo;
    if (group != null) {
      group.remove();
      timeInfo = null;
    }
    timeInfoLabel = null;
  }

  /**
   * Removes the canvas group used for displaying activity information.
   */
  private void dispose() {
    ICanvasObjectGroup group = infoGroup;
    if (group != null) {
      group.remove();
      infoGroup = null;
      activityInfo = null;
      timeInfo = null;
    }
    activityInfoLabel = null;
    timeInfoLabel = null;
  }


  /**
   * Creates a new singleton {@code ToolTipHelper} instance for the given
   * graph component.
   */
  static ToolTipHelper newInstance( GraphComponent graphComponent ) {
    ToolTipHelper oldHelper = INSTANCE;
    if (oldHelper != null) {
      oldHelper.dispose();
    }

    ToolTipHelper newHelper = new ToolTipHelper();
    newHelper.infoGroup = graphComponent.getRootGroup().addGroup();
    INSTANCE = newHelper;
    return newHelper;
  }


  /**
   * Shows information about the activity associated to the given activity node.
   * @param viewPoint the viewpoint coordinates
   * @param viewPortWidth the width of the viewport
   */
  static void showActivityInfo(INode node, PointD viewPoint, double viewPortWidth) {
    Activity activity = (Activity) node.getTag();
    PointD center = node.getLayout().getCenter();
    ToolTipHelper helper = getInstance();
    if (helper.activityInfo != null) {
      helper.removeActivityInfoImpl();
    }
    helper.createActivityInfoCanvasObjects(activity, center, viewPoint, viewPortWidth);
  }

  /**
   * Removes the activity info group and label.
   */
  static void removeActivityInfo() {
    getInstance().removeActivityInfoImpl();
  }

  /**
   * Shows updated time info.
   */
  static void showTimeInfo( INode node, String text, boolean isFollowUp ) {
    ToolTipHelper helper = getInstance();
    if (helper.timeInfoLabel == null) {
      helper.createTimeInfoCanvasObjects();
    }
    IRectangle nl = node.getLayout();
    PointD p = isFollowUp ? nl.getTopRight() : nl.getTopLeft();
    helper.updateTimeInfoImpl(text, p, isFollowUp);
  }

  /**
   * Removes the time info.
   */
  static void removeTimeInfo() {
    getInstance().removeTimeInfoImpl();
  }

  /**
   * Returns the shared singleton instance.
   * @throws IllegalStateException if the singleton instance has not yet been
   * created.
   * @see #newInstance(GraphComponent)
   */
  private static ToolTipHelper getInstance() {
    ToolTipHelper helper = INSTANCE;
    if (helper == null) {
      throw new IllegalStateException("ToolTipHelper not initialized.");
    } else {
      return helper;
    }
  }
}
