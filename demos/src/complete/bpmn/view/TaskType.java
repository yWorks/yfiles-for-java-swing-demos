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

import com.yworks.yfiles.utils.Obfuscation;

/**
 * Specifies the type of a task according to BPMN.
 * @see ActivityNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum TaskType {
  /**
   * Specifies the type of a task to be an Abstract Task according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  ABSTRACT(0),

  /**
   * Specifies the type of a task to be a Send Task according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  SEND(1),

  /**
   * Specifies the type of a task to be a Receive Task according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  RECEIVE(2),

  /**
   * Specifies the type of a task to be a User Task according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  USER(3),

  /**
   * Specifies the type of a task to be a Manual Task according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  MANUAL(4),

  /**
   * Specifies the type of a task to be a Business Rule Task according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  BUSINESS_RULE(5),

  /**
   * Specifies the type of a task to be a Service Task according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  SERVICE(6),

  /**
   * Specifies the type of a task to be a Script Task according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  SCRIPT(7),

  /**
   * Specifies the type of a task to be an Event-Triggered Sub-Task according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  EVENT_TRIGGERED(8);

  private final int value;

  private TaskType( final int value ) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final TaskType fromOrdinal( int ordinal ) {
    for (TaskType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }

}
