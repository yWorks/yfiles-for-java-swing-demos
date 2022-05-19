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
 * Specifies the type of a Conversation according to BPMN.
 * @see ConversationNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum ConversationType {
  /**
   * Specifies that a Conversation is a plain Conversation according to BPMN.
   * @see complete.bpmn.view.ConversationNodeStyle
   */
  CONVERSATION(0),

  /**
   * Specifies that a Conversation is a Sub-Conversation according to BPMN.
   * @see complete.bpmn.view.ConversationNodeStyle
   */
  SUB_CONVERSATION(1),

  /**
   * Specifies that a Conversation is a Call Conversation according to BPMN where a Global Conversation is called.
   * @see complete.bpmn.view.ConversationNodeStyle
   */
  CALLING_GLOBAL_CONVERSATION(2),

  /**
   * Specifies that a Conversation is a Call Conversation according to BPMN where a Collaboration is called.
   * @see complete.bpmn.view.ConversationNodeStyle
   */
  CALLING_COLLABORATION(3);

  private final int value;

  private ConversationType( final int value ) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final ConversationType fromOrdinal( int ordinal ) {
    for (ConversationType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }

}
