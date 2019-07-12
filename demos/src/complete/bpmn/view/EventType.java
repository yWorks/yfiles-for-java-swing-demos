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
package complete.bpmn.view;

import com.yworks.yfiles.utils.Obfuscation;

/**
 * Specifies the type of an Event according to BPMN.
 * @see EventNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum EventType {
  /**
   * Specifies that an Event is a Plain Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  PLAIN(0),

  /**
   * Specifies that an Event is a Message Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  MESSAGE(1),

  /**
   * Specifies that an Event is a Timer Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  TIMER(2),

  /**
   * Specifies that an Event is an Escalation Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  ESCALATION(3),

  /**
   * Specifies that an Event is a Conditional Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  CONDITIONAL(4),

  /**
   * Specifies that an Event is a Link Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  LINK(5),

  /**
   * Specifies that an Event is an Error Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  ERROR(6),

  /**
   * Specifies that an Event is a Cancel Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  CANCEL(7),

  /**
   * Specifies that an Event is a Compensation Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  COMPENSATION(8),

  /**
   * Specifies that an Event is a Signal Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  SIGNAL(9),

  /**
   * Specifies that an Event is a Multiple Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  MULTIPLE(10),

  /**
   * Specifies that an Event is a Parallel Multiple Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  PARALLEL_MULTIPLE(11),

  /**
   * Specifies that an Event is a Terminate Event according to BPMN.
   * @see complete.bpmn.view.EventNodeStyle
   */
  TERMINATE(12);

  private final int value;

  private EventType( final int value ) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final EventType fromOrdinal( int ordinal ) {
    for (EventType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }

}
