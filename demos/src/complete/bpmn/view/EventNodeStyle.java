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

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.GeomUtilities;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.input.IInputModeContext;
import java.awt.Paint;

import java.util.Arrays;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing an Event according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class EventNodeStyle extends BpmnNodeStyle {
  private EventType type;

  /**
   * Gets the event type for this style.
   * @return The Type.
   * @see #setType(EventType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventType.class, stringValue = "PLAIN")
  public final EventType getType() {
    return type;
  }

  /**
   * Sets the event type for this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventType.class, stringValue = "PLAIN")
  public final void setType( EventType value ) {
    if (type != value) {
      incrementModCount();
      type = value;
      createTypeIcon();
    }
  }

  private EventCharacteristic characteristic;

  /**
   * Gets the event characteristic for this style.
   * @return The Characteristic.
   * @see #setCharacteristic(EventCharacteristic)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventCharacteristic.class, stringValue = "START")
  public final EventCharacteristic getCharacteristic() {
    return characteristic;
  }

  /**
   * Sets the event characteristic for this style.
   * @param value The Characteristic to set.
   * @see #getCharacteristic()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = EventCharacteristic.class, stringValue = "START")
  public final void setCharacteristic( EventCharacteristic value ) {
    if (characteristic != value || eventIcon == null) {
      incrementModCount();
      characteristic = value;
      createEventIcon();
    }
  }

  private Paint background = BpmnConstants.DEFAULT_EVENT_BACKGROUND;

  /**
   * Gets the background color of the event.
   * @return The Background.
   * @see #setBackground(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultEventBackground", classValue = BpmnConstants.class)
  public final Paint getBackground() {
    return background;
  }

  /**
   * Sets the background color of the event.
   * @param value The Background to set.
   * @see #getBackground()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultEventBackground", classValue = BpmnConstants.class)
  public final void setBackground( Paint value ) {
    if (background != value) {
      setModCount(getModCount() + 1);
      background = value;
      createEventIcon();
    }
  }

  // null is the default value which chooses a default color for the outline depending on the characteristic
  private Paint outline = BpmnConstants.DEFAULT_EVENT_OUTLINE;

  /**
   * Gets the outline color of the event icon.
   * <p>
   * If this is set to {@code null}, the outline color is automatic, based on the
   * {@link #getCharacteristic() Characteristic}.
   * </p>
   * @return The Outline.
   * @see #setOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultEventOutline", classValue = BpmnConstants.class)
  public final Paint getOutline() {
    return outline;
  }

  /**
   * Sets the outline color of the event icon.
   * <p>
   * If this is set to {@code null}, the outline color is automatic, based on the
   * {@link #getCharacteristic() Characteristic}.
   * </p>
   * @param value The Outline to set.
   * @see #getOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DefaultEventOutline", classValue = BpmnConstants.class)
  public final void setOutline( Paint value ) {
    if (outline != value) {
      setModCount(getModCount() + 1);
      outline = value;
      createEventIcon();
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
      createTypeIcon();
    }
  }

  private IIcon eventIcon;

  private IIcon typeIcon;

  private boolean fillTypeIcon = false;

  /**
   * Creates a new instance.
   */
  public EventNodeStyle() {
    setMinimumSize(new SizeD(20, 20));
    setCharacteristic(EventCharacteristic.START);
    setType(EventType.PLAIN);
  }

  private void createTypeIcon() {
    typeIcon = IconFactory.createEventType(type, fillTypeIcon, getIconColor(), getBackground());
    if (typeIcon != null) {
      typeIcon = IconFactory.createPlacedIcon(typeIcon, BpmnConstants.EVENT_TYPE_PLACEMENT, SizeD.EMPTY);
    }
  }

  private void createEventIcon() {
    eventIcon = IconFactory.createEvent(getCharacteristic(), getBackground(), getOutline());
    eventIcon = IconFactory.createPlacedIcon(eventIcon, BpmnConstants.EVENT_PLACEMENT, getMinimumSize());
    boolean isFilled = getCharacteristic() == EventCharacteristic.THROWING || getCharacteristic() == EventCharacteristic.END;
    if (isFilled != fillTypeIcon) {
      fillTypeIcon = isFilled;
      createTypeIcon();
    }
  }

  @Override
  void updateIcon( INode node ) {
    if (eventIcon == null) {
      createEventIcon();
    }
    if (typeIcon != null) {
      setIcon(IconFactory.createCombinedIcon(Arrays.asList(eventIcon, typeIcon)));
    } else {
      setIcon(eventIcon);
    }
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    RectD layout = node.getLayout().toRectD();
    double size = Math.min(layout.getWidth(), layout.getHeight());
    RectD bounds = new RectD(layout.getCenter().x - size / 2, layout.getCenter().y - size / 2, size, size);

    GeneralPath path = new GeneralPath();
    path.appendEllipse(new RectD(bounds.getTopLeft(), bounds.toSizeD()), false);
    return path;
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected boolean isHit( IInputModeContext context, PointD location, INode node ) {
    RectD layout = node.getLayout().toRectD();
    double size = Math.min(layout.getWidth(), layout.getHeight());
    RectD bounds = new RectD(layout.getCenter().x - size / 2, layout.getCenter().y - size / 2, size, size);
    return GeomUtilities.ellipseContains(bounds, location, context.getHitTestRadius());
  }

}
