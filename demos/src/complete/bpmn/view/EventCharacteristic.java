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
package complete.bpmn.view;

import com.yworks.yfiles.utils.Obfuscation;

/**
 * Specifies the characteristic of an event.
 * @see EventNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum EventCharacteristic {
  /**
   * Specifies that an Event is a Start Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  START(0),

  /**
   * Specifies that an Event is a Start Event for a Sub-Process according to BPMN that interrupts the containing Process.
   * @see complete.bpmn.view.EventNodeStyle
   */
  SUB_PROCESS_INTERRUPTING(1),

  /**
   * Specifies that an Event is a Start Event for a Sub-Process according to BPMN that doesn`t interrupt the containing
   * Process.
   * @see complete.bpmn.view.EventNodeStyle
   */
  SUB_PROCESS_NON_INTERRUPTING(2),

  /**
   * Specifies that an Event is an Intermediate Catching Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  CATCHING(3),

  /**
   * Specifies that an Event is an Intermediate Event Attached to an Activity Boundary according to BPMN that interrupts the
   * Activity.
   * @see complete.bpmn.view.EventNodeStyle
   */
  BOUNDARY_INTERRUPTING(4),

  /**
   * Specifies that an Event is an Intermediate Event Attached to an Activity Boundary according to BPMN that doesn't
   * interrupt the Activity.
   * @see complete.bpmn.view.EventNodeStyle
   */
  BOUNDARY_NON_INTERRUPTING(5),

  /**
   * Specifies that an Event is an Intermediate Throwing Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  THROWING(6),

  /**
   * Specifies that an Event is an End Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  END(7);

  private final int value;

  private EventCharacteristic( final int value ) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final EventCharacteristic fromOrdinal( int ordinal ) {
    for (EventCharacteristic current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }

}
