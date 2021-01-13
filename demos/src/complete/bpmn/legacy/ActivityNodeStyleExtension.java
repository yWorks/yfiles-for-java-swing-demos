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
package complete.bpmn.legacy;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graphml.MarkupExtension;
import complete.bpmn.view.ActivityNodeStyle;

public class ActivityNodeStyleExtension extends MarkupExtension {
  private ActivityType activityType = ActivityType.TASK;

  public final ActivityType getActivityType() {
    return this.activityType;
  }

  public final void setActivityType( ActivityType value ) {
    this.activityType = value;
  }

  private TaskType taskType = TaskType.ABSTRACT;

  public final TaskType getTaskType() {
    return this.taskType;
  }

  public final void setTaskType( TaskType value ) {
    this.taskType = value;
  }

  private EventType triggerEventType = EventType.PLAIN;

  public final EventType getTriggerEventType() {
    return this.triggerEventType;
  }

  public final void setTriggerEventType( EventType value ) {
    this.triggerEventType = value;
  }

  private EventCharacteristic triggerEventCharacteristic = EventCharacteristic.START;

  public final EventCharacteristic getTriggerEventCharacteristic() {
    return this.triggerEventCharacteristic;
  }

  public final void setTriggerEventCharacteristic( EventCharacteristic value ) {
    this.triggerEventCharacteristic = value;
  }

  private LoopCharacteristic loopCharacteristic = LoopCharacteristic.NONE;

  public final LoopCharacteristic getLoopCharacteristic() {
    return this.loopCharacteristic;
  }

  public final void setLoopCharacteristic( LoopCharacteristic value ) {
    this.loopCharacteristic = value;
  }

  private SubState subState = SubState.NONE;

  public final SubState getSubState() {
    return this.subState;
  }

  public final void setSubState( SubState value ) {
    this.subState = value;
  }

  private boolean adHoc;

  public final boolean isAdHoc() {
    return this.adHoc;
  }

  public final void setAdHoc( boolean value ) {
    this.adHoc = value;
  }

  private boolean compensation;

  public final boolean isCompensation() {
    return this.compensation;
  }

  public final void setCompensation( boolean value ) {
    this.compensation = value;
  }

  private InsetsD insets = new InsetsD();

  public final InsetsD getInsets() {
    return this.insets;
  }

  public final void setInsets( InsetsD value ) {
    this.insets = value;
  }

  private SizeD minimumSize = new SizeD();

  public final SizeD getMinimumSize() {
    return this.minimumSize;
  }

  public final void setMinimumSize( SizeD value ) {
    this.minimumSize = value;
  }

  public ActivityNodeStyleExtension() {
    setActivityType(ActivityType.TASK);
    setTaskType(TaskType.ABSTRACT);
    setTriggerEventType(EventType.MESSAGE);
    setTriggerEventCharacteristic(EventCharacteristic.SUB_PROCESS_INTERRUPTING);
    setLoopCharacteristic(LoopCharacteristic.NONE);
    setSubState(SubState.NONE);
    setAdHoc(false);
    setCompensation(false);
    setInsets(new InsetsD(15));
    setMinimumSize(SizeD.EMPTY);
  }

  @Override
  public Object provideValue( ILookup serviceProvider ) {
    ActivityNodeStyle activityNodeStyle = new ActivityNodeStyle();
    activityNodeStyle.setActivityType(complete.bpmn.view.ActivityType.fromOrdinal(getActivityType().value()));
    activityNodeStyle.setTaskType(complete.bpmn.view.TaskType.fromOrdinal(getTaskType().value()));
    activityNodeStyle.setTriggerEventType(complete.bpmn.view.EventType.fromOrdinal(getTriggerEventType().value()));
    activityNodeStyle.setTriggerEventCharacteristic(complete.bpmn.view.EventCharacteristic.fromOrdinal(getTriggerEventCharacteristic().value()));
    activityNodeStyle.setLoopCharacteristic(complete.bpmn.view.LoopCharacteristic.fromOrdinal(getLoopCharacteristic().value()));
    activityNodeStyle.setSubState(complete.bpmn.view.SubState.fromOrdinal(getSubState().value()));
    activityNodeStyle.setAdHoc(isAdHoc());
    activityNodeStyle.setCompensation(isCompensation());
    activityNodeStyle.setInsets(getInsets());
    activityNodeStyle.setMinimumSize(getMinimumSize());
    return activityNodeStyle;
  }

}
