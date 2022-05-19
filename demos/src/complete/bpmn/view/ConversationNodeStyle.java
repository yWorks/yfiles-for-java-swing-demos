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

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.MatrixOrder;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.DefaultValue;
import java.util.Arrays;
import com.yworks.yfiles.utils.Obfuscation;
import java.awt.Paint;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing a Conversation according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class ConversationNodeStyle extends BpmnNodeStyle {
  private ConversationType type;

  /**
   * Gets the conversation type for this style.
   * @return The Type.
   * @see #setType(ConversationType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ConversationType.class, stringValue = "CONVERSATION")
  public final ConversationType getType() {
    return type;
  }

  /**
   * Sets the conversation type for this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = ConversationType.class, stringValue = "CONVERSATION")
  public final void setType( ConversationType value ) {
    if (type != value || getIcon() == null) {
      setModCount(getModCount() + 1);
      type = value;
      updateIcon();
    }
  }

  private Paint background = BpmnConstants.CONVERSATION_DEFAULT_BACKGROUND;

  /**
   * Gets the background color of the conversation.
   * @return The Background.
   * @see #setBackground(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ConversationDefaultBackground", classValue = BpmnConstants.class)
  public final Paint getBackground() {
    return background;
  }

  /**
   * Sets the background color of the conversation.
   * @param value The Background to set.
   * @see #getBackground()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ConversationDefaultBackground", classValue = BpmnConstants.class)
  public final void setBackground( Paint value ) {
    if (background != value) {
      setModCount(getModCount() + 1);
      background = value;
      updateIcon();
    }
  }

  private Paint outline = BpmnConstants.CONVERSATION_DEFAULT_OUTLINE;

  /**
   * Gets the outline color of the conversation.
   * @return The Outline.
   * @see #setOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ConversationDefaultOutline", classValue = BpmnConstants.class)
  public final Paint getOutline() {
    return outline;
  }

  /**
   * Sets the outline color of the conversation.
   * @param value The Outline to set.
   * @see #getOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "ConversationDefaultOutline", classValue = BpmnConstants.class)
  public final void setOutline( Paint value ) {
    if (outline != value) {
      setModCount(getModCount() + 1);
      outline = value;
      updateIcon();
    }
  }

  private Paint iconColor = BpmnConstants.DEFAULT_ICON_COLOR;

  /**
   * Gets the primary color for icons and markers.
   * @return The IconColor.
   * @see #setIconColor(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultIconColor", classValue = BpmnConstants.class)
  public final Paint getIconColor() {
    return iconColor;
  }

  /**
   * Sets the primary color for icons and markers.
   * @param value The IconColor to set.
   * @see #getIconColor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultIconColor", classValue = BpmnConstants.class)
  public final void setIconColor( Paint value ) {
    if (iconColor != value) {
      setModCount(getModCount() + 1);
      iconColor = value;
      updateIcon();
    }
  }

  /**
   * Creates a new instance.
   */
  public ConversationNodeStyle() {
    setType(ConversationType.CONVERSATION);
    setMinimumSize(BpmnConstants.CONVERSATION_SIZE);
  }

  private void updateIcon() {
    IIcon typeIcon = IconFactory.createConversation(type, getBackground(), getOutline());
    IIcon markerIcon = IconFactory.createConversationMarker(type, getIconColor());

    if (markerIcon != null) {
      markerIcon = IconFactory.createPlacedIcon(markerIcon, BpmnConstants.CONVERSATION_MARKER_PLACEMENT, BpmnConstants.MARKER_SIZE);
      typeIcon = IconFactory.createCombinedIcon(Arrays.asList(typeIcon, markerIcon));
    }

    setIcon(IconFactory.createPlacedIcon(typeIcon, BpmnConstants.CONVERSATION_PLACEMENT, BpmnConstants.CONVERSATION_SIZE));
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    IRectangle layout = node.getLayout().toRectD();
    double width = Math.min(layout.getWidth(), layout.getHeight() / BpmnConstants.CONVERSATION_WIDTH_HEIGHT_RATIO);
    double height = width * BpmnConstants.CONVERSATION_WIDTH_HEIGHT_RATIO;
    RectD bounds = new RectD(layout.getCenter().x - width / 2, layout.getCenter().y - height / 2, width, height);

    GeneralPath path = new GeneralPath(16);
    path.moveTo(0, 0.5);
    path.lineTo(0.25, 0);
    path.lineTo(0.75, 0);
    path.lineTo(1, 0.5);
    path.lineTo(0.75, 1);
    path.lineTo(0.25, 1);
    path.close();

    Matrix2D transform = new Matrix2D();
    transform.translate(bounds.getTopLeft(), MatrixOrder.PREPEND);
    transform.scale(bounds.width, bounds.height, MatrixOrder.PREPEND);
    path.transform(transform);
    return path;
  }

}
