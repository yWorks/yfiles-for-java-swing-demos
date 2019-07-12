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

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.MatrixOrder;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.DefaultValue;
import java.util.Arrays;
import com.yworks.yfiles.utils.Obfuscation;

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

      IIcon typeIcon = IconFactory.createConversation(type);
      IIcon markerIcon = IconFactory.createConversationMarker(type);

      if (markerIcon != null) {
        markerIcon = IconFactory.createPlacedIcon(markerIcon, BpmnConstants.Placements.CONVERSATION_MARKER, BpmnConstants.Sizes.MARKER);
        typeIcon = IconFactory.createCombinedIcon(Arrays.asList(typeIcon, markerIcon));
      }

      setIcon(IconFactory.createPlacedIcon(typeIcon, BpmnConstants.Placements.CONVERSATION, BpmnConstants.Sizes.CONVERSATION));
    }
  }


  /**
   * Creates a new instance.
   */
  public ConversationNodeStyle() {
    setType(ConversationType.CONVERSATION);
    setMinimumSize(BpmnConstants.Sizes.CONVERSATION);
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    IRectangle layout = node.getLayout().toRectD();
    double width = Math.min(layout.getWidth(), layout.getHeight() / BpmnConstants.Sizes.CONVERSATION_WIDTH_HEIGHT_RATIO);
    double height = width * BpmnConstants.Sizes.CONVERSATION_WIDTH_HEIGHT_RATIO;
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
