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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyleRenderer;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.input.IClickListener;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeInsetsProvider;
import com.yworks.yfiles.view.Pen;
import java.awt.Paint;
import java.util.ArrayList;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing an Activity according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class ActivityNodeStyle extends BpmnNodeStyle {
  private static final ShapeNodeStyle SNS;

  static {
    ShapeNodeStyleRenderer renderer = new ShapeNodeStyleRenderer();
    renderer.setRoundRectArcRadius(BpmnConstants.ACTIVITY_CORNER_RADIUS);
    ShapeNodeStyle shapeNodeStyle = new ShapeNodeStyle(renderer);
    shapeNodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    shapeNodeStyle.setPen(Pen.getBlack());
    shapeNodeStyle.setPaint(null);
    SNS = shapeNodeStyle;
  }

  private IIcon adHocIcon;

  private IIcon compensationIcon;

  private ActivityType activityType;

  /**
   * Gets the activity type for this style.
   * @return The ActivityType.
   * @see #setActivityType(ActivityType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ActivityType.class, stringValue = "TASK")
  public final ActivityType getActivityType() {
    return activityType;
  }

  /**
   * Sets the activity type for this style.
   * @param value The ActivityType to set.
   * @see #getActivityType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ActivityType.class, stringValue = "TASK")
  public final void setActivityType( ActivityType value ) {
    if (activityType != value || activityIcon == null) {
      incrementModCount();
      activityType = value;
      updateActivityIcon();
    }
  }

  private TaskType taskType;

  /**
   * Gets the task type for this style.
   * @return The TaskType.
   * @see #setTaskType(TaskType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = TaskType.class, stringValue = "ABSTRACT")
  public final TaskType getTaskType() {
    return taskType;
  }

  /**
   * Sets the task type for this style.
   * @param value The TaskType to set.
   * @see #getTaskType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = TaskType.class, stringValue = "ABSTRACT")
  public final void setTaskType( TaskType value ) {
    if (taskType != value) {
      setModCount(getModCount() + 1);
      taskType = value;
      updateTaskIcon();
    }
  }

  private EventType triggerEventType;

  /**
   * Gets the event type that is used for the task type {@link TaskType#EVENT_TRIGGERED}.
   * @return The TriggerEventType.
   * @see #setTriggerEventType(EventType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventType.class, stringValue = "MESSAGE")
  public final EventType getTriggerEventType() {
    return triggerEventType;
  }

  /**
   * Sets the event type that is used for the task type {@link TaskType#EVENT_TRIGGERED}.
   * @param value The TriggerEventType to set.
   * @see #getTriggerEventType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventType.class, stringValue = "MESSAGE")
  public final void setTriggerEventType( EventType value ) {
    if (triggerEventType != value) {
      triggerEventType = value;
      if (getTaskType() == TaskType.EVENT_TRIGGERED) {
        setModCount(getModCount() + 1);
        updateTaskIcon();
      }
    }
  }

  private EventCharacteristic triggerEventCharacteristic;

  /**
   * Gets the event characteristic that is used for the task type {@link TaskType#EVENT_TRIGGERED}.
   * @return The TriggerEventCharacteristic.
   * @see #setTriggerEventCharacteristic(EventCharacteristic)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventCharacteristic.class, stringValue = "SUB_PROCESS_INTERRUPTING")
  public final EventCharacteristic getTriggerEventCharacteristic() {
    return triggerEventCharacteristic;
  }

  /**
   * Sets the event characteristic that is used for the task type {@link TaskType#EVENT_TRIGGERED}.
   * @param value The TriggerEventCharacteristic to set.
   * @see #getTriggerEventCharacteristic()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventCharacteristic.class, stringValue = "SUB_PROCESS_INTERRUPTING")
  public final void setTriggerEventCharacteristic( EventCharacteristic value ) {
    if (triggerEventCharacteristic != value) {
      triggerEventCharacteristic = value;
      if (getTaskType() == TaskType.EVENT_TRIGGERED) {
        setModCount(getModCount() + 1);
        updateTaskIcon();
      }
    }
  }

  private LoopCharacteristic loopCharacteristic;

  /**
   * Gets the loop characteristic of this style.
   * @return The LoopCharacteristic.
   * @see #setLoopCharacteristic(LoopCharacteristic)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LoopCharacteristic.class, stringValue = "NONE")
  public final LoopCharacteristic getLoopCharacteristic() {
    return loopCharacteristic;
  }

  /**
   * Sets the loop characteristic of this style.
   * @param value The LoopCharacteristic to set.
   * @see #getLoopCharacteristic()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = LoopCharacteristic.class, stringValue = "NONE")
  public final void setLoopCharacteristic( LoopCharacteristic value ) {
    if (loopCharacteristic != value) {
      setModCount(getModCount() + 1);
      loopCharacteristic = value;
      updateLoopIcon();
    }
  }

  private SubState subState;

  /**
   * Gets the sub state of this style.
   * @return The SubState.
   * @see #setSubState(SubState)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = SubState.class, stringValue = "NONE")
  public final SubState getSubState() {
    return subState;
  }

  /**
   * Sets the sub state of this style.
   * @param value The SubState to set.
   * @see #getSubState()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = SubState.class, stringValue = "NONE")
  public final void setSubState( SubState value ) {
    if (subState != value) {
      setModCount(getModCount() + 1);
      subState = value;
    }
  }

  private boolean adHoc;

  /**
   * Gets whether this style represents an Ad Hoc Activity.
   * @return The AdHoc.
   * @see #setAdHoc(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isAdHoc() {
    return adHoc;
  }

  /**
   * Sets whether this style represents an Ad Hoc Activity.
   * @param value The AdHoc to set.
   * @see #isAdHoc()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setAdHoc( boolean value ) {
    if (adHoc != value) {
      incrementModCount();
      adHoc = value;
      updateAdHocIcon();
    }
  }

  private boolean compensation;

  /**
   * Gets whether this style represents a Compensation Activity.
   * @return The Compensation.
   * @see #setCompensation(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isCompensation() {
    return compensation;
  }

  /**
   * Sets whether this style represents a Compensation Activity.
   * @param value The Compensation to set.
   * @see #isCompensation()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setCompensation( boolean value ) {
    if (compensation != value) {
      incrementModCount();
      compensation = value;
      updateCompensationIcon();
    }
  }

  /**
   * Gets the insets for the node.
   * <p>
   * These insets are extended at the left and bottom side if markers are active and returned via an {@link INodeInsetsProvider}
   * if such an instance is queried through the
   * {@link com.yworks.yfiles.graph.styles.INodeStyleRenderer#getContext(INode, com.yworks.yfiles.graph.styles.INodeStyle) context lookup}.
   * </p>
   * @return The Insets.
   * @see INodeInsetsProvider
   * @see #setInsets(InsetsD)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "15", classValue = InsetsD.class)
  public final InsetsD getInsets() {
    return insets;
  }

  /**
   * Sets the insets for the node.
   * <p>
   * These insets are extended at the left and bottom side if markers are active and returned via an {@link INodeInsetsProvider}
   * if such an instance is queried through the
   * {@link com.yworks.yfiles.graph.styles.INodeStyleRenderer#getContext(INode, com.yworks.yfiles.graph.styles.INodeStyle) context lookup}.
   * </p>
   * @param value The Insets to set.
   * @see INodeInsetsProvider
   * @see #getInsets()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "15", classValue = InsetsD.class)
  public final void setInsets( InsetsD value ) {
    insets = value;
  }

  private Paint background = BpmnConstants.ACTIVITY_DEFAULT_BACKGROUND;

  /**
   * Gets the background color of the activity.
   * @return The Background.
   * @see #setBackground(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ActivityDefaultBackground", classValue = BpmnConstants.class)
  public final Paint getBackground() {
    return background;
  }

  /**
   * Sets the background color of the activity.
   * @param value The Background to set.
   * @see #getBackground()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ActivityDefaultBackground", classValue = BpmnConstants.class)
  public final void setBackground( Paint value ) {
    if (background != value || activityIcon == null) {
      setModCount(getModCount() + 1);
      background = value;
      updateActivityIcon();
      updateTaskIcon();
    }
  }

  private Paint outline = BpmnConstants.ACTIVITY_DEFAULT_OUTLINE;

  /**
   * Gets the outline color of the activity.
   * @return The Outline.
   * @see #setOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ActivityDefaultOutline", classValue = BpmnConstants.class)
  public final Paint getOutline() {
    return outline;
  }

  /**
   * Sets the outline color of the activity.
   * @param value The Outline to set.
   * @see #getOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ActivityDefaultOutline", classValue = BpmnConstants.class)
  public final void setOutline( Paint value ) {
    if (outline != value || activityIcon == null) {
      setModCount(getModCount() + 1);
      outline = value;
      updateActivityIcon();
    }
  }

  private Paint iconColor = BpmnConstants.DEFAULT_ICON_COLOR;

  /**
   * Gets the primary color for icons and markers.
   * @return The IconColor.
   * @see #setIconColor(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultIconColor", classValue = BpmnConstants.class)
  public final Paint getIconColor() {
    return iconColor;
  }

  /**
   * Sets the primary color for icons and markers.
   * @param value The IconColor to set.
   * @see #getIconColor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultIconColor", classValue = BpmnConstants.class)
  public final void setIconColor( Paint value ) {
    if (iconColor != value) {
      setModCount(getModCount() + 1);
      iconColor = value;
      updateTaskIcon();
      updateLoopIcon();
      updateAdHocIcon();
      updateCompensationIcon();
    }
  }

  // null is the default value which chooses a default color for the outline depending on the characteristic
  private Paint eventOutline = BpmnConstants.DEFAULT_EVENT_OUTLINE;

  /**
   * Gets the outline color for event icons if {@link #getTaskType() TaskType} is {@link TaskType#EVENT_TRIGGERED}.
   * <p>
   * If this is set to {@code null}, the outline color is automatic, based on the
   * {@link #getTriggerEventCharacteristic() TriggerEventCharacteristic}.
   * </p>
   * @return The EventOutline.
   * @see #setEventOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.NULL)
  public final Paint getEventOutline() {
    return eventOutline;
  }

  /**
   * Sets the outline color for event icons if {@link #getTaskType() TaskType} is {@link TaskType#EVENT_TRIGGERED}.
   * <p>
   * If this is set to {@code null}, the outline color is automatic, based on the
   * {@link #getTriggerEventCharacteristic() TriggerEventCharacteristic}.
   * </p>
   * @param value The EventOutline to set.
   * @see #getEventOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.NULL)
  public final void setEventOutline( Paint value ) {
    if (eventOutline != value) {
      setModCount(getModCount() + 1);
      eventOutline = value;
      updateTaskIcon();
    }
  }

  private IIcon activityIcon;

  private IIcon taskIcon;

  private IIcon loopIcon;

  private InsetsD insets;

  /**
   * Creates a new instance using the default values.
   */
  public ActivityNodeStyle() {
    setMinimumSize(new SizeD(40, 30));
    setActivityType(ActivityType.TASK);
    setInsets(new InsetsD(15));
    setSubState(SubState.NONE);
    setLoopCharacteristic(LoopCharacteristic.NONE);
    setTaskType(TaskType.ABSTRACT);
    setTriggerEventType(EventType.MESSAGE);
    setTriggerEventCharacteristic(EventCharacteristic.SUB_PROCESS_INTERRUPTING);
  }

  private void updateActivityIcon() {
    activityIcon = IconFactory.createActivity(activityType, getBackground(), getOutline());
  }

  private void updateTaskIcon() {
    if (getTaskType() == TaskType.EVENT_TRIGGERED) {
      EventNodeStyle eventNodeStyle = new EventNodeStyle();
      eventNodeStyle.setCharacteristic(getTriggerEventCharacteristic());
      eventNodeStyle.setType(getTriggerEventType());
      eventNodeStyle.setBackground(getBackground());
      eventNodeStyle.setOutline(getEventOutline());
      eventNodeStyle.setIconColor(getIconColor());
      eventNodeStyle.updateIcon(new SimpleNode());
      taskIcon = eventNodeStyle.getIcon();
    } else {
      taskIcon = IconFactory.createActivityTaskType(taskType, getIconColor(), getBackground());
    }
    if (taskIcon != null) {
      taskIcon = IconFactory.createPlacedIcon(taskIcon, BpmnConstants.TASK_TYPE_PLACEMENT, BpmnConstants.TASK_TYPE_SIZE);
    }
  }

  private void updateAdHocIcon() {
    adHocIcon = isAdHoc() ? IconFactory.createAdHoc(getIconColor()) : null;
  }

  private void updateCompensationIcon() {
    compensationIcon = isCompensation() ? IconFactory.createCompensation(false, getIconColor()) : null;
  }

  private void updateLoopIcon() {
    loopIcon = IconFactory.createLoopCharacteristic(getLoopCharacteristic(), getIconColor());
  }

  @Override
  void updateIcon( INode node ) {
    setIcon(createIcon(node));
  }

  final IIcon createIcon( INode node ) {
    double minimumWidth = 10.0;

    ArrayList<IIcon> icons = new ArrayList<>();
    if (activityIcon != null) {
      icons.add(activityIcon);
    }
    if (taskIcon != null) {
      icons.add(taskIcon);
    }

    ArrayList<IIcon> lineUpIcons = new ArrayList<>();
    if (loopIcon != null) {
      minimumWidth += BpmnConstants.MARKER_SIZE.width + 5;
      lineUpIcons.add(loopIcon);
    }
    if (isAdHoc()) {
      minimumWidth += BpmnConstants.MARKER_SIZE.width + 5;
      lineUpIcons.add(adHocIcon);
    }
    if (isCompensation()) {
      minimumWidth += BpmnConstants.MARKER_SIZE.width + 5;
      lineUpIcons.add(compensationIcon);
    }
    if (getSubState() != SubState.NONE) {
      minimumWidth += BpmnConstants.MARKER_SIZE.width + 5;
      if (getSubState() == SubState.DYNAMIC) {
        lineUpIcons.add(IconFactory.createDynamicSubState(node, getIconColor()));
      } else {
        lineUpIcons.add(IconFactory.createStaticSubState(getSubState(), getIconColor()));
      }
    }
    if (lineUpIcons.size() > 0) {
      IIcon lineUpIcon = IconFactory.createLineUpIcon(lineUpIcons, BpmnConstants.MARKER_SIZE, 5);
      icons.add(IconFactory.createPlacedIcon(lineUpIcon, BpmnConstants.TASK_MARKER_PLACEMENT, BpmnConstants.MARKER_SIZE));
    }

    setMinimumSize(new SizeD(Math.max(minimumWidth, 40), 40));
    if (icons.size() > 1) {
      return IconFactory.createCombinedIcon(icons);
    } else if (icons.size() == 1) {
      return icons.get(0);
    } else {
      return null;
    }
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    // Create a rounded rectangle path
    RectD layout = node.getLayout().toRectD();
    GeneralPath path = new GeneralPath(12);
    double x = layout.x;
    double y = layout.y;
    double w = layout.width;
    double h = layout.height;
    double arcX = Math.min(w * 0.5, 5);
    double arcY = Math.min(h * 0.5, 5);
    path.moveTo(x, y + arcY);
    path.quadTo(x, y, x + arcX, y);
    path.lineTo(x + w - arcX, y);
    path.quadTo(x + w, y, x + w, y + arcY);
    path.lineTo(x + w, y + h - arcY);
    path.quadTo(x + w, y + h, x + w - arcX, y + h);
    path.lineTo(x + arcX, y + h);
    path.quadTo(x, y + h, x, y + h - arcY);
    path.close();
    return path;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected boolean isHit( IInputModeContext context, PointD location, INode node ) {
    return SNS.getRenderer().getHitTestable(node, SNS).isHit(context, location);
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected Object lookup( INode node, Class type ) {
    if (type == INodeInsetsProvider.class) {
      return new ActivityInsetsProvider();
    } else if (type == IClickListener.class) {
      IIcon icon = createIcon(node);
      if (icon != null) {
        icon.setBounds(node.getLayout().toRectD());
        return getDelegatingClickListener(icon);
      }
    }
    return super.lookup(node, type);
  }

  /**
   * Uses the style insets extended by the size of the participant bands.
   */
  private class ActivityInsetsProvider implements INodeInsetsProvider {
    public final InsetsD getInsets( INode node ) {
      InsetsD outerInsets = ActivityNodeStyle.this.getInsets();
      double left = getTaskType() != TaskType.ABSTRACT ?
          BpmnConstants.TASK_TYPE_SIZE.width + ((InteriorLabelModel)BpmnConstants.TASK_TYPE_PLACEMENT.getModel()).getInsets().left
          : 0;
      double bottom = isAdHoc() || isCompensation() || getLoopCharacteristic() != LoopCharacteristic.NONE || getSubState() != SubState.NONE ?
          BpmnConstants.MARKER_SIZE.height + ((InteriorStretchLabelModel)BpmnConstants.TASK_MARKER_PLACEMENT.getModel()).getInsets().bottom
          : 0;
      return InsetsD.fromLTRB(left + outerInsets.left, outerInsets.top, outerInsets.right, bottom + outerInsets.bottom);
    }

  }

}
