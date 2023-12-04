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
package complete.bpmn.view.config;

import complete.bpmn.view.ChoreographyNodeStyle;
import complete.bpmn.view.ChoreographyType;
import complete.bpmn.view.LoopCharacteristic;
import complete.bpmn.view.SubState;
import toolkit.optionhandler.EnumValueAnnotation;
import toolkit.optionhandler.Label;
import toolkit.optionhandler.OptionGroupAnnotation;

/**
 * Configuration class for {@link ChoreographyNodeStyle}.
 * <p>
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer
 * applications will likely provide their own property configuration framework and won't need this part of the library.
 * </p>
 */
@Label("Choreography Node")
public class ChoreographyNodeStyleConfiguration extends NodeStyleConfiguration<ChoreographyNodeStyle> {

  @OptionGroupAnnotation(name = "RootGroup", position = 0)
  @Label("Choreography Type")
  @EnumValueAnnotation(label = "Task", value = "TASK")
  @EnumValueAnnotation(label = "Call", value = "CALL")
  public final ChoreographyType getType() {
    return getStyleTemplate().getType();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 0)
  @Label("Choreography Type")
  @EnumValueAnnotation(label = "Task", value = "TASK")
  @EnumValueAnnotation(label = "Call", value = "CALL")
  public final void setType( ChoreographyType value ) {
    getStyleTemplate().setType(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 1)
  @Label("Loop Characteristic")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Loop", value = "LOOP")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Sequential", value = "SEQUENTIAL")
  public final LoopCharacteristic getLoopCharacteristic() {
    return getStyleTemplate().getLoopCharacteristic();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 1)
  @Label("Loop Characteristic")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Loop", value = "LOOP")
  @EnumValueAnnotation(label = "Parallel", value = "PARALLEL")
  @EnumValueAnnotation(label = "Sequential", value = "SEQUENTIAL")
  public final void setLoopCharacteristic( LoopCharacteristic value ) {
    getStyleTemplate().setLoopCharacteristic(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 2)
  @Label("Substate")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Expanded", value = "EXPANDED")
  @EnumValueAnnotation(label = "Collapsed", value = "COLLAPSED")
  @EnumValueAnnotation(label = "Dynamic", value = "DYNAMIC")
  public final SubState getSubState() {
    return getStyleTemplate().getSubState();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 2)
  @Label("Substate")
  @EnumValueAnnotation(label = "None", value = "NONE")
  @EnumValueAnnotation(label = "Expanded", value = "EXPANDED")
  @EnumValueAnnotation(label = "Collapsed", value = "COLLAPSED")
  @EnumValueAnnotation(label = "Dynamic", value = "DYNAMIC")
  public final void setSubState( SubState value ) {
    getStyleTemplate().setSubState(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 4)
  @Label("Initiating Message")
  public final boolean isInitiatingMessage() {
    return getStyleTemplate().isInitiatingMessage();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 4)
  @Label("Initiating Message")
  public final void setInitiatingMessage( boolean value ) {
    getStyleTemplate().setInitiatingMessage(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @Label("Response Message")
  public final boolean isResponseMessage() {
    return getStyleTemplate().isResponseMessage();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 5)
  @Label("Response Message")
  public final void setResponseMessage( boolean value ) {
    getStyleTemplate().setResponseMessage(value);
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 3)
  @Label("Initiating At Top")
  public final boolean isInitiatingAtTop() {
    return getStyleTemplate().isInitiatingAtTop();
  }

  @OptionGroupAnnotation(name = "RootGroup", position = 3)
  @Label("Initiating At Top")
  public final void setInitiatingAtTop( boolean value ) {
    getStyleTemplate().setInitiatingAtTop(value);
  }

  @Override
  protected ChoreographyNodeStyle createDefault() {
    return new ChoreographyNodeStyle();
  }
}
