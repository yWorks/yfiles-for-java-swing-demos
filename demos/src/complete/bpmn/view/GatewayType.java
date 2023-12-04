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
 * Specifies the type of a Gateway according to BPMN.
 * @see GatewayNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum GatewayType {
  /**
   * Specifies that a Gateway has the type Exclusive according to BPMN but should not use a marker.
   * @see complete.bpmn.view.GatewayNodeStyle
   */
  EXCLUSIVE_WITHOUT_MARKER(0),

  /**
   * Specifies that a Gateway has the type Exclusive according to BPMN and should use a marker.
   * @see complete.bpmn.view.GatewayNodeStyle
   */
  EXCLUSIVE_WITH_MARKER(1),

  /**
   * Specifies that a Gateway has the type Inclusive according to BPMN.
   * @see complete.bpmn.view.GatewayNodeStyle
   */
  INCLUSIVE(2),

  /**
   * Specifies that a Gateway has the type Parallel according to BPMN.
   * @see complete.bpmn.view.GatewayNodeStyle
   */
  PARALLEL(3),

  /**
   * Specifies that a Gateway has the type Complex according to BPMN.
   * @see complete.bpmn.view.GatewayNodeStyle
   */
  COMPLEX(4),

  /**
   * Specifies that a Gateway has the type Event-Based according to BPMN.
   * @see complete.bpmn.view.GatewayNodeStyle
   */
  EVENT_BASED(5),

  /**
   * Specifies that a Gateway has the type Exclusive Event-Based according to BPMN.
   * @see complete.bpmn.view.GatewayNodeStyle
   */
  EXCLUSIVE_EVENT_BASED(6),

  /**
   * Specifies that a Gateway has the type Parallel Event-Based according to BPMN.
   * @see complete.bpmn.view.GatewayNodeStyle
   */
  PARALLEL_EVENT_BASED(7);

  private final int value;

  private GatewayType( final int value ) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final GatewayType fromOrdinal( int ordinal ) {
    for (GatewayType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }

}
