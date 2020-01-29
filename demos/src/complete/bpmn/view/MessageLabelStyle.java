/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyleRenderer;
import com.yworks.yfiles.graph.styles.NodeStyleLabelStyleAdapter;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IMarqueeTestable;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisualCreator;

/**
 * An {@link ILabelStyle} implementation representing a Message according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class MessageLabelStyle implements ILabelStyle {

  private static final ILabelStyleRenderer INITIATING_RENDERER;

  private static final ILabelStyleRenderer RESPONSE_RENDERER;



  private boolean initiating;

  /**
   * Gets if this Message is initiating.
   * @return The Initiating.
   * @see #setInitiating(boolean)
   */
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isInitiating() {
    return this.initiating;
  }

  /**
   * Sets if this Message is initiating.
   * @param value The Initiating to set.
   * @see #isInitiating()
   */
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setInitiating( boolean value ) {
    this.initiating = value;
  }


  public static final ILabelStyle createInitiatingStyle() {
    MessageLabelStyle messageLabelStyle = new MessageLabelStyle();
    messageLabelStyle.setInitiating(true);
    return messageLabelStyle;
  }

  public static final ILabelStyle createResponseStyle() {
    MessageLabelStyle messageLabelStyle = new MessageLabelStyle();
    messageLabelStyle.setInitiating(false);
    return messageLabelStyle;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final MessageLabelStyle clone() {
    return (MessageLabelStyle)this;
  }

  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final ILabelStyleRenderer getRenderer() {
    return isInitiating() ? INITIATING_RENDERER : RESPONSE_RENDERER;
  }

  /**
   * An {@link ILabelStyleRenderer} implementation used by {@link MessageLabelStyle}.
   */
  static class MessageLabelStyleRenderer implements ILabelStyleRenderer {
    private ILabelStyle adapter;

    public MessageLabelStyleRenderer( ILabelStyle adapter ) {
      this.adapter = adapter;
    }

    public final IVisualCreator getVisualCreator( ILabel item, ILabelStyle style ) {
      return adapter.getRenderer().getVisualCreator(item, adapter);
    }

    public final IBoundsProvider getBoundsProvider( ILabel item, ILabelStyle style ) {
      return adapter.getRenderer().getBoundsProvider(item, adapter);
    }

    public final IVisibilityTestable getVisibilityTestable( ILabel item, ILabelStyle style ) {
      return adapter.getRenderer().getVisibilityTestable(item, adapter);
    }

    public final IHitTestable getHitTestable( ILabel item, ILabelStyle style ) {
      return adapter.getRenderer().getHitTestable(item, adapter);
    }

    public final IMarqueeTestable getMarqueeTestable( ILabel item, ILabelStyle style ) {
      return adapter.getRenderer().getMarqueeTestable(item, adapter);
    }

    public final ILookup getContext( ILabel item, ILabelStyle style ) {
      return adapter.getRenderer().getContext(item, adapter);
    }

    public final SizeD getPreferredSize( ILabel label, ILabelStyle style ) {
      return adapter.getRenderer().getPreferredSize(label, adapter);
    }

  }

  static {

    // Initiate the renderer for the initiating Message
    IIcon messageIcon = IconFactory.createMessage(BpmnConstants.Pens.MESSAGE, BpmnConstants.Paints.INITIATING_MESSAGE);
    BpmnNodeStyle bpmnNodeStyle = new BpmnNodeStyle();
    bpmnNodeStyle.setIcon(messageIcon);
    bpmnNodeStyle.setMinimumSize(BpmnConstants.Sizes.MESSAGE);
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    NodeStyleLabelStyleAdapter adapter = new NodeStyleLabelStyleAdapter(bpmnNodeStyle, labelStyle);
    INITIATING_RENDERER = new MessageLabelStyleRenderer(adapter);

    // Initiate the renderer for the response Message
    messageIcon = IconFactory.createMessage(BpmnConstants.Pens.MESSAGE, BpmnConstants.Paints.RECEIVING_MESSAGE);
    bpmnNodeStyle = new BpmnNodeStyle();
    bpmnNodeStyle.setIcon(messageIcon);
    bpmnNodeStyle.setMinimumSize(BpmnConstants.Sizes.MESSAGE);
    labelStyle = new DefaultLabelStyle();
    adapter = new NodeStyleLabelStyleAdapter(bpmnNodeStyle, labelStyle);
    RESPONSE_RENDERER = new MessageLabelStyleRenderer(adapter);
  }

}
