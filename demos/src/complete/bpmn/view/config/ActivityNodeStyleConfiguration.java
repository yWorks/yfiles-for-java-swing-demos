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
package complete.bpmn.view.config;

import complete.bpmn.view.ActivityNodeStyle;
import complete.bpmn.view.ActivityType;
import complete.bpmn.view.EventCharacteristic;
import complete.bpmn.view.EventType;
import complete.bpmn.view.LoopCharacteristic;
import complete.bpmn.view.SubState;
import complete.bpmn.view.TaskType;
import toolkit.optionhandler.EnumValueAnnotation;
import toolkit.optionhandler.Label;
import toolkit.optionhandler.OptionGroupAnnotation;

/**
 * Configuration class for {@link ActivityNodeStyle}.
 * <p>
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer
 * applications will likely provide their own property configuration framework and won't need this part of the library.
 * </p>
 */
@Label("Activity Node")
public class ActivityNodeStyleConfiguration extends NodeStyleConfiguration<ActivityNodeStyle> {

  @OptionGroupAnnotation(name = "RootGroup", position = 0)
  @Label("Activity Type")
  @EnumValueAnnotation(label = "Task", value = "TASK")
  @EnumValueAnnotation(label = "Subprocess", value = "SUB_PROCESS")
  @EnumValueAnnotation(label = "Transaction", value = "TRANSACTION")
  @EnumValueAnnotation(label = "Event Subprocess", value = "EVENT_SUB_PROCESS")
  @EnumValueAnnotation(label = "Call Activity", value = "CALL_ACTIVITY")
  public final ActivityType getActivityType() {
    return getStyleTemplate().getActivityType();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 0)
  @Label("Activity Type")
  @EnumValueAnnotation(label = "Task", value = "TASK")
  @EnumValueAnnotation(label = "Subprocess", value = "SUB_PROCESS")
  @EnumValueAnnotation(label = "Transaction", value = "TRANSACTION")
  @EnumValueAnnotation(label = "Event Subprocess", value = "EVENT_SUB_PROCESS")
  @EnumValueAnnotation(label = "Call Activity", value = "CALL_ACTIVITY")
  public final void setActivityType( ActivityType value ) {
    getStyleTemplate().setActivityType(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 1)
  @Label("Task Type")
  @EnumValueAnnotation(label = "Abstract", value = "ABSTRACT")
  @EnumValueAnnotation(label = "Send", value = "SEND")
  @EnumValueAnnotation(label = "Receive", value = "RECEIVE")
  @EnumValueAnnotation(label = "User", value = "USER")
  @EnumValueAnnotation(label = "Manual", value = "MANUAL")
  @EnumValueAnnotation(label = "Business Rule", value = "BUSINESS_RULE")
  @EnumValueAnnotation(label = "Service", value = "SERVICE")
  @EnumValueAnnotation(label = "Script", value = "SCRIPT")
  @EnumValueAnnotation(label = "Event Triggered", value = "EVENT_TRIGGERED")
  public final TaskType getTaskType() {
    return getStyleTemplate().getTaskType();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 1)
  @Label("Task Type")
  @EnumValueAnnotation(label = "Abstract", value = "ABSTRACT")
  @EnumValueAnnotation(label = "Send", value = "SEND")
  @EnumValueAnnotation(label = "Receive", value = "RECEIVE")
  @EnumValueAnnotation(label = "User", value = "USER")
  @EnumValueAnnotation(label = "Manual", value = "MANUAL")
  @EnumValueAnnotation(label = "Business Rule", value = "BUSINESS_RULE")
  @EnumValueAnnotation(label = "Service", value = "SERVICE")
  @EnumValueAnnotation(label = "Script", value = "SCRIPT")
  @EnumValueAnnotation(label = "Event Triggered", value = "EVENT_TRIGGERED")
  public final void setTaskType( TaskType value ) {
    getStyleTemplate().setTaskType(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 2)
  @Label("Trigger Event Type")
  @EnumValueAnnotation(label = "Plain", value = "PLAIN")
  @EnumValueAnnotation(label = "Message", value = "MESSAGE")
  @EnumValueAnnotation(label = "Timer", value = "TIMER")
  @EnumValueAnnotation(label = "Escalation", value = "ESCALATION")
  @EnumValueAnnotation(label = "Conditional", value = "CONDITIONAL")
  @EnumValueAnnotation(label = "Link", value = "LINK")
  @EnumValueAnnotation(label = "Error", value = "ERROR")
  @EnumValueAnnotation(label = "Cancel", value = "CANCEL")
  @EnumValueAnnotation(label = "Compensation", value = "COMPENSATION")
  @EnumValueAnnotation(label = "Signal", value = "SIGNAL")
  @EnumValueAnnotation(label = "Multiple", value = "MULTIPLE")
  @EnumValueAnnotation(label = "Parallel Multiple", value = "PARALLEL_MULTIPLE")
  @EnumValueAnnotation(label = "Terminate", value = "TERMINATE")
  public final EventType getTriggerEventType() {
    return getStyleTemplate().getTriggerEventType();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 2)
  @Label("Trigger Event Type")
  @EnumValueAnnotation(label = "Plain", value = "PLAIN")
  @EnumValueAnnotation(label = "Message", value = "MESSAGE")
  @EnumValueAnnotation(label = "Timer", value = "TIMER")
  @EnumValueAnnotation(label = "Escalation", value = "ESCALATION")
  @EnumValueAnnotation(label = "Conditional", value = "CONDITIONAL")
  @EnumValueAnnotation(label = "Link", value = "LINK")
  @EnumValueAnnotation(label = "Error", value = "ERROR")
  @EnumValueAnnotation(label = "Cancel", value = "CANCEL")
  @EnumValueAnnotation(label = "Compensation", value = "COMPENSATION")
  @EnumValueAnnotation(label = "Signal", value = "SIGNAL")
  @EnumValueAnnotation(label = "Multiple", value = "MULTIPLE")
  @EnumValueAnnotation(label = "Parallel Multiple", value = "PARALLEL_MULTIPLE")
  @EnumValueAnnotation(label = "Terminate", value = "TERMINATE")
  public final void setTriggerEventType( EventType value ) {
    getStyleTemplate().setTriggerEventType(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 3)
  @Label("Trigger Event Characteristic")
  @EnumValueAnnotation(label = "Start", value = "START")
  @EnumValueAnnotation(label = "Subprocess Interrupting", value = "SUB_PROCESS_INTERRUPTING")
  @EnumValueAnnotation(label = "Subprocess Non Interrupting", value = "SUB_PROCESS_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Catching", value = "CATCHING")
  @EnumValueAnnotation(label = "Boundary Interrupting", value = "BOUNDARY_INTERRUPTING")
  @EnumValueAnnotation(label = "Boundary Non Interrupting", value = "BOUNDARY_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Throwing", value = "THROWING")
  @EnumValueAnnotation(label = "End", value = "END")
  public final EventCharacteristic getTriggerEventCharacteristic() {
    return getStyleTemplate().getTriggerEventCharacteristic();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 3)
  @Label("Trigger Event Characteristic")
  @EnumValueAnnotation(label = "Start", value = "START")
  @EnumValueAnnotation(label = "Subprocess Interrupting", value = "SUB_PROCESS_INTERRUPTING")
  @EnumValueAnnotation(label = "Subprocess Non Interrupting", value = "SUB_PROCESS_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Catching", value = "CATCHING")
  @EnumValueAnnotation(label = "Boundary Interrupting", value = "BOUNDARY_INTERRUPTING")
  @EnumValueAnnotation(label = "Boundary Non Interrupting", value = "BOUNDARY_NON_INTERRUPTING")
  @EnumValueAnnotation(label = "Throwing", value = "THROWING")
  @EnumValueAnnotation(label = "End", value = "END")
  public final void setTriggerEventCharacteristic( EventCharacteristic value ) {
    getStyleTemplate().setTriggerEventCharacteristic(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 4)
  @Label("Loop Characteristic")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Loop", value = "LOOP")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Sequential", value = "SEQUENTIAL")
  public final LoopCharacteristic getLoopCharacteristic() {
    return getStyleTemplate().getLoopCharacteristic();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 4)
  @Label("Loop Characteristic")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Loop", value = "LOOP")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Sequential", value = "SEQUENTIAL")
  public final void setLoopCharacteristic( LoopCharacteristic value ) {
    getStyleTemplate().setLoopCharacteristic(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @Label("Substate")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Expanded", value = "EXPANDED")
  @EnumValueAnnotation(label = "Collapsed", value = "COLLAPSED")
  @EnumValueAnnotation(label = "Dynamic", value = "DYNAMIC")
  public final SubState getSubState() {
    return getStyleTemplate().getSubState();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @Label("Substate")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Expanded", value = "EXPANDED")
  @EnumValueAnnotation(label = "Collapsed", value = "COLLAPSED")
  @EnumValueAnnotation(label = "Dynamic", value = "DYNAMIC")
  public final void setSubState( SubState value ) {
    getStyleTemplate().setSubState(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 6)
  @Label("Ad Hoc")
  public final boolean isAdHoc() {
    return getStyleTemplate().isAdHoc();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 6)
  @Label("Ad Hoc")
  public final void setAdHoc( boolean value ) {
    getStyleTemplate().setAdHoc(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 7)
  @Label("Compensation")
  public final boolean isCompensation() {
    return getStyleTemplate().isCompensation();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 7)
  @Label("Compensation")
  public final void setCompensation( boolean value ) {
    getStyleTemplate().setCompensation(value);
  }

  @Override
  protected ActivityNodeStyle createDefault() {
    return new ActivityNodeStyle();
  }
}
