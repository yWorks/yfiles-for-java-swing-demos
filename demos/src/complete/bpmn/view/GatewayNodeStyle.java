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
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.input.IInputModeContext;
import java.awt.Paint;
import java.util.List;

import java.util.Arrays;
/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing a Gateway according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class GatewayNodeStyle extends BpmnNodeStyle {
  private IIcon gatewayIcon;

  private GatewayType type;

  /**
   * Gets the gateway type for this style.
   * @return The Type.
   * @see #setType(GatewayType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GatewayType.class, stringValue = "EXCLUSIVE_WITHOUT_MARKER")
  public final GatewayType getType() {
    return type;
  }

  /**
   * Sets the gateway type for this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = GatewayType.class, stringValue = "EXCLUSIVE_WITHOUT_MARKER")
  public final void setType( GatewayType value ) {
    if (type != value) {
      incrementModCount();
      type = value;
      updateTypeIcon();
    }
  }

  private Paint background = BpmnConstants.GATEWAY_DEFAULT_BACKGROUND;

  /**
   * Gets the background color of the gateway.
   * @return The Background.
   * @see #setBackground(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "GatewayDefaultBackground", classValue = BpmnConstants.class)
  public final Paint getBackground() {
    return background;
  }

  /**
   * Sets the background color of the gateway.
   * @param value The Background to set.
   * @see #getBackground()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "GatewayDefaultBackground", classValue = BpmnConstants.class)
  public final void setBackground( Paint value ) {
    if (background != value) {
      setModCount(getModCount() + 1);
      background = value;
      updateGatewayIcon();
    }
  }

  private Paint outline = BpmnConstants.GATEWAY_DEFAULT_OUTLINE;

  /**
   * Gets the outline color of the gateway.
   * @return The Outline.
   * @see #setOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "GatewayDefaultOutline", classValue = BpmnConstants.class)
  public final Paint getOutline() {
    return outline;
  }

  /**
   * Sets the outline color of the gateway.
   * @param value The Outline to set.
   * @see #getOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "GatewayDefaultOutline", classValue = BpmnConstants.class)
  public final void setOutline( Paint value ) {
    if (outline != value) {
      setModCount(getModCount() + 1);
      outline = value;
      updateGatewayIcon();
    }
  }

  private Paint iconColor = BpmnConstants.DEFAULT_ICON_COLOR;

  /**
   * Gets the color for the icon.
   * @return The IconColor.
   * @see #setIconColor(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultIconColor", classValue = BpmnConstants.class)
  public final Paint getIconColor() {
    return iconColor;
  }

  /**
   * Sets the color for the icon.
   * @param value The IconColor to set.
   * @see #getIconColor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultIconColor", classValue = BpmnConstants.class)
  public final void setIconColor( Paint value ) {
    if (iconColor != value) {
      setModCount(getModCount() + 1);
      iconColor = value;
      updateTypeIcon();
    }
  }

  private IIcon typeIcon;

  /**
   * Creates a new instance.
   */
  public GatewayNodeStyle() {
    setMinimumSize(new SizeD(20, 20));
    setType(GatewayType.EXCLUSIVE_WITHOUT_MARKER);
  }

  private void updateGatewayIcon() {
    gatewayIcon = IconFactory.createPlacedIcon(IconFactory.createGateway(getBackground(), getOutline()), BpmnConstants.GATEWAY_PLACEMENT, SizeD.EMPTY);
  }

  private void updateTypeIcon() {
    typeIcon = IconFactory.createGatewayType(type, getIconColor());
    if (typeIcon != null) {
      typeIcon = IconFactory.createPlacedIcon(typeIcon, BpmnConstants.GATEWAY_TYPE_PLACEMENT, SizeD.EMPTY);
    }
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  void updateIcon( INode node ) {
    if (gatewayIcon == null) {
      updateGatewayIcon();
    }
    setIcon(typeIcon != null ? IconFactory.createCombinedIcon(Arrays.asList(gatewayIcon, typeIcon)) : gatewayIcon);
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    double size = Math.min(node.getLayout().getWidth(), node.getLayout().getHeight());
    RectD bounds = new RectD(node.getLayout().getX() + node.getLayout().getWidth() / 2 - size / 2,
        node.getLayout().getY() + node.getLayout().getHeight() / 2 - size / 2, size, size);

    GeneralPath path = new GeneralPath(16);
    path.moveTo(bounds.x, bounds.getCenterY()); // <
    path.lineTo(bounds.getCenterX(), bounds.y); // ^
    path.lineTo(bounds.getMaxX(), bounds.getCenterY()); // >
    path.lineTo(bounds.getCenterX(), bounds.getMaxY()); // v
    path.close();
    return path;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected boolean isHit( IInputModeContext context, PointD location, INode node ) {
    RectD layout = node.getLayout().toRectD();
    if (!layout.getEnlarged(context.getHitTestRadius()).contains(location)) {
      return false;
    }
    double size = Math.min(layout.getWidth(), layout.getHeight());

    PointD distVector = PointD.subtract(layout.getCenter(), location);
    double dist = Math.abs(distVector.x) + Math.abs(distVector.y);
    return dist < size / 2 + context.getHitTestRadius();
  }

}
