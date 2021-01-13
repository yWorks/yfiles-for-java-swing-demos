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
package complete.bpmn.view.config;

import complete.bpmn.view.BpmnEdgeStyle;
import complete.bpmn.view.EdgeType;
import toolkit.optionhandler.EnumValueAnnotation;
import toolkit.optionhandler.Label;

/**
 * Configuration class for {@link BpmnEdgeStyle}.
 * <p>
 * This is only needed for the sample application to provide an easy way to configure the option pane. Customer
 * applications will likely provide their own property configuration framework and won't need this part of the library.
 * </p>
 */
@Label("Bpmn Edge")
public class BpmnEdgeStyleConfiguration extends EdgeStyleConfiguration<BpmnEdgeStyle> {

  @Label("Edge Type")
  @EnumValueAnnotation(label = "Sequence Flow", value = "SEQUENCE_FLOW")
  @EnumValueAnnotation(label = "Default Flow", value = "DEFAULT_FLOW")
  @EnumValueAnnotation(label = "Conditional Flow", value = "CONDITIONAL_FLOW")
  @EnumValueAnnotation(label = "Message Flow", value = "MESSAGE_FLOW")
  @EnumValueAnnotation(label = "Association", value = "ASSOCIATION")
  @EnumValueAnnotation(label = "Directed Association", value = "DIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Bidirected Association", value = "BIDIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Conversation", value = "CONVERSATION")
  public final EdgeType getType() {
    return getStyleTemplate().getType();
  }

  @Label("Edge Type")
  @EnumValueAnnotation(label = "Sequence Flow", value = "SEQUENCE_FLOW")
  @EnumValueAnnotation(label = "Default Flow", value = "DEFAULT_FLOW")
  @EnumValueAnnotation(label = "Conditional Flow", value = "CONDITIONAL_FLOW")
  @EnumValueAnnotation(label = "Message Flow", value = "MESSAGE_FLOW")
  @EnumValueAnnotation(label = "Association", value = "ASSOCIATION")
  @EnumValueAnnotation(label = "Directed Association", value = "DIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Bidirected Association", value = "BIDIRECTED_ASSOCIATION")
  @EnumValueAnnotation(label = "Conversation", value = "CONVERSATION")
  public final void setType( EdgeType value ) {
    getStyleTemplate().setType(value);
  }

  @Override
  protected BpmnEdgeStyle createDefault() {
    return new BpmnEdgeStyle();
  }
}
