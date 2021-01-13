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
import com.yworks.yfiles.view.Pen;
import java.awt.Paint;

/**
 * An {@link ILabelStyle} implementation representing a Message according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class MessageLabelStyle implements ILabelStyle {
  private final MessageLabelStyleRenderer renderer;

  /**
   * Gets if this Message is initiating.
   * @return The Initiating.
   * @see #setInitiating(boolean)
   */
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isInitiating() {
    return isInitiating;
  }

  /**
   * Sets if this Message is initiating.
   * @param value The Initiating to set.
   * @see #isInitiating()
   */
  @DefaultValue(booleanValue = true, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setInitiating( boolean value ) {
    if (isInitiating != value) {
      isInitiating = value;
      updateIcon();
    }
  }

  private Paint outline;

  Pen messagePen;

  /**
   * Gets the outline color of the message.
   * @return The Outline.
   * @see #setOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultMessageOutline", classValue = BpmnConstants.class)
  public final Paint getOutline() {
    return outline;
  }

  /**
   * Sets the outline color of the message.
   * @param value The Outline to set.
   * @see #getOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultMessageOutline", classValue = BpmnConstants.class)
  public final void setOutline( Paint value ) {
    if (outline != value) {
      outline = value;
      messagePen = (Pen)new Pen(outline, 1);
      updateIcon();
    }
  }

  private Paint initiatingColor = BpmnConstants.DEFAULT_INITIATING_MESSAGE_COLOR;

  /**
   * Gets the color for an initiating message.
   * @return The InitiatingColor.
   * @see #setInitiatingColor(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultInitiatingMessageColor", classValue = BpmnConstants.class)
  public final Paint getInitiatingColor() {
    return initiatingColor;
  }

  /**
   * Sets the color for an initiating message.
   * @param value The InitiatingColor to set.
   * @see #getInitiatingColor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultInitiatingMessageColor", classValue = BpmnConstants.class)
  public final void setInitiatingColor( Paint value ) {
    if (initiatingColor != value) {
      initiatingColor = value;
      if (isInitiating()) {
        updateIcon();
      }
    }
  }

  private Paint responseColor = BpmnConstants.DEFAULT_RECEIVING_MESSAGE_COLOR;

  private boolean isInitiating = true;

  /**
   * Gets the color for a response message.
   * @return The ResponseColor.
   * @see #setResponseColor(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultReceivingMessageColor", classValue = BpmnConstants.class)
  public final Paint getResponseColor() {
    return responseColor;
  }

  /**
   * Sets the color for a response message.
   * @param value The ResponseColor to set.
   * @see #getResponseColor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultReceivingMessageColor", classValue = BpmnConstants.class)
  public final void setResponseColor( Paint value ) {
    if (responseColor != value) {
      responseColor = value;
      if (!isInitiating()) {
        updateIcon();
      }
    }
  }

  public MessageLabelStyle() {
    final BpmnNodeStyle messageStyle = new BpmnNodeStyle();
    messageStyle.setIcon(IconFactory.createMessage(
        new Pen(BpmnConstants.DEFAULT_MESSAGE_OUTLINE, 1),
        BpmnConstants.DEFAULT_RECEIVING_MESSAGE_COLOR,
        false));
    messageStyle.setMinimumSize(BpmnConstants.MESSAGE_SIZE);
    renderer = new MessageLabelStyleRenderer(new NodeStyleLabelStyleAdapter(messageStyle, new DefaultLabelStyle()));

    setOutline(BpmnConstants.DEFAULT_MESSAGE_OUTLINE);
  }

  private void updateIcon() {
    NodeStyleLabelStyleAdapter adapter = renderer.adapter;
    BpmnNodeStyle nodeStyle = (BpmnNodeStyle)adapter.getNodeStyle();
    nodeStyle.setIcon(IconFactory.createMessage(messagePen, isInitiating() ? getInitiatingColor() : getResponseColor(), false));
    nodeStyle.setModCount(nodeStyle.getModCount() + 1);
  }

  public final MessageLabelStyle clone() {
    MessageLabelStyle messageLabelStyle = new MessageLabelStyle();
    messageLabelStyle.setInitiating(isInitiating());
    messageLabelStyle.setInitiatingColor(getInitiatingColor());
    messageLabelStyle.setResponseColor(getResponseColor());
    messageLabelStyle.setOutline(getOutline());
    return (MessageLabelStyle)messageLabelStyle;
  }

  public final ILabelStyleRenderer getRenderer() {
    return renderer;
  }

  /**
   * An {@link ILabelStyleRenderer} implementation used by {@link MessageLabelStyle}.
   */
  static class MessageLabelStyleRenderer implements ILabelStyleRenderer {
    NodeStyleLabelStyleAdapter adapter;

    public MessageLabelStyleRenderer( NodeStyleLabelStyleAdapter adapter ) {
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

}
