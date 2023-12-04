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
 * Specifies the type of an edge according to BPMN.
 * @see BpmnEdgeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum EdgeType {
  /**
   * Specifies an edge to be a Sequence Flow according to BPMN.
   * @see complete.bpmn.view.BpmnEdgeStyle
   */
  SEQUENCE_FLOW(0),

  /**
   * Specifies an edge to be a Default Flow according to BPMN.
   * @see complete.bpmn.view.BpmnEdgeStyle
   */
  DEFAULT_FLOW(1),

  /**
   * Specifies an edge to be a Conditional Flow according to BPMN.
   * @see complete.bpmn.view.BpmnEdgeStyle
   */
  CONDITIONAL_FLOW(2),

  /**
   * Specifies an edge to be a Message Flow according to BPMN.
   * @see complete.bpmn.view.BpmnEdgeStyle
   */
  MESSAGE_FLOW(3),

  /**
   * Specifies an edge to be an undirected Association according to BPMN.
   * @see complete.bpmn.view.BpmnEdgeStyle
   */
  ASSOCIATION(4),

  /**
   * Specifies an edge to be a directed Association according to BPMN.
   * @see complete.bpmn.view.BpmnEdgeStyle
   */
  DIRECTED_ASSOCIATION(5),

  /**
   * Specifies an edge to be a bidirected Association according to BPMN.
   * @see complete.bpmn.view.BpmnEdgeStyle
   */
  BIDIRECTED_ASSOCIATION(6),

  /**
   * Specifies an edge to be a Conversation according to BPMN.
   * @see complete.bpmn.view.BpmnEdgeStyle
   */
  CONVERSATION(7);

  private final int value;

  private EdgeType( final int value ) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final EdgeType fromOrdinal( int ordinal ) {
    for (EdgeType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }

}
