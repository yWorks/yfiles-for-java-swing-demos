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
 * Specifies the Loop Characteristic of an Activity or Choreography according to BPMN.
 * @see ActivityNodeStyle
 * @see ChoreographyNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum LoopCharacteristic {
  /**
   * Specifies that an Activity or Choreography in not looping according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   * @see complete.bpmn.view.ChoreographyNodeStyle
   */
  NONE(0),

  /**
   * Specifies that an Activity or Choreography has a Standard Loop Characteristic according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   * @see complete.bpmn.view.ChoreographyNodeStyle
   */
  LOOP(1),

  /**
   * Specifies that an Activity or Choreography has a parallel Multi-Instance Loop Characteristic according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   * @see complete.bpmn.view.ChoreographyNodeStyle
   */
  PARALLEL(2),

  /**
   * Specifies that an Activity or Choreography has a sequential Multi-Instance Loop Characteristic according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   * @see complete.bpmn.view.ChoreographyNodeStyle
   */
  SEQUENTIAL(3);

  private final int value;

  private LoopCharacteristic( final int value ) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final LoopCharacteristic fromOrdinal( int ordinal ) {
    for (LoopCharacteristic current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }

}
