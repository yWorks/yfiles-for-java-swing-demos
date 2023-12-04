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
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.DefaultValue;
import com.yworks.yfiles.utils.Obfuscation;
import java.awt.Paint;
import java.util.ArrayList;

/**
 * An {@link com.yworks.yfiles.graph.styles.INodeStyle} implementation representing a Data Object according to the BPMN.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class DataObjectNodeStyle extends BpmnNodeStyle {
  private IIcon dataIcon;

  private IIcon collectionIcon;

  private boolean collection;

  /**
   * Gets whether this is a Collection Data Object.
   * @return The Collection.
   * @see #setCollection(boolean)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final boolean isCollection() {
    return collection;
  }

  /**
   * Sets whether this is a Collection Data Object.
   * @param value The Collection to set.
   * @see #isCollection()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(booleanValue = false, valueType = DefaultValue.ValueType.BOOLEAN_TYPE)
  public final void setCollection( boolean value ) {
    if (collection != value) {
      incrementModCount();
      collection = value;
    }
  }

  private DataObjectType type;

  /**
   * Gets the data object type for this style.
   * @return The Type.
   * @see #setType(DataObjectType)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = DataObjectType.class, stringValue = "NONE")
  public final DataObjectType getType() {
    return type;
  }

  /**
   * Sets the data object type for this style.
   * @param value The Type to set.
   * @see #getType()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(valueType = DefaultValue.ValueType.ENUM_TYPE, classValue = DataObjectType.class, stringValue = "NONE")
  public final void setType( DataObjectType value ) {
    if (type != value) {
      incrementModCount();
      type = value;
      updateTypeIcon();
    }
  }

  private Paint background = BpmnConstants.DATA_OBJECT_DEFAULT_BACKGROUND;

  /**
   * Gets the background color of the data object.
   * @return The Background.
   * @see #setBackground(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DataObjectDefaultBackground", classValue = BpmnConstants.class)
  public final Paint getBackground() {
    return background;
  }

  /**
   * Sets the background color of the data object.
   * @param value The Background to set.
   * @see #getBackground()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DataObjectDefaultBackground", classValue = BpmnConstants.class)
  public final void setBackground( Paint value ) {
    if (background != value) {
      setModCount(getModCount() + 1);
      background = value;
      updateDataIcon();
    }
  }

  private Paint outline = BpmnConstants.DATA_OBJECT_DEFAULT_OUTLINE;

  /**
   * Gets the outline color of the data object.
   * @return The Outline.
   * @see #setOutline(Paint)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DataObjectDefaultOutline", classValue = BpmnConstants.class)
  public final Paint getOutline() {
    return outline;
  }

  /**
   * Sets the outline color of the data object.
   * @param value The Outline to set.
   * @see #getOutline()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  @DefaultValue(stringValue = "DataObjectDefaultOutline", classValue = BpmnConstants.class)
  public final void setOutline( Paint value ) {
    if (outline != value) {
      setModCount(getModCount() + 1);
      outline = value;
      updateDataIcon();
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
      updateCollectionIcon();
    }
  }

  private IIcon typeIcon;

  /**
   * Creates a new instance.
   */
  public DataObjectNodeStyle() {
    setMinimumSize(new SizeD(25, 30));
    setType(DataObjectType.NONE);
  }

  private void updateCollectionIcon() {
    collectionIcon = IconFactory.createPlacedIcon(IconFactory.createLoopCharacteristic(LoopCharacteristic.PARALLEL, getIconColor()), BpmnConstants.DATA_OBJECT_MARKER_PLACEMENT, BpmnConstants.MARKER_SIZE);
  }

  private void updateTypeIcon() {
    typeIcon = IconFactory.createDataObjectType(getType(), getIconColor());
    if (typeIcon != null) {
      typeIcon = IconFactory.createPlacedIcon(typeIcon, BpmnConstants.DATA_OBJECT_TYPE_PLACEMENT, BpmnConstants.DATA_OBJECT_TYPE_SIZE);
    }
  }

  private void updateDataIcon() {
    dataIcon = IconFactory.createDataObject(getBackground(), getOutline());
  }

  @Override
  void updateIcon( INode node ) {
    if (dataIcon == null) {
      updateDataIcon();
    }
    if (collectionIcon == null) {
      updateCollectionIcon();
    }

    ArrayList<IIcon> icons = new ArrayList<IIcon>();
    icons.add(dataIcon);
    if (isCollection()) {
      icons.add(collectionIcon);
    }
    if (typeIcon != null) {
      icons.add(typeIcon);
    }
    if (icons.size() > 1) {
      setIcon(IconFactory.createCombinedIcon(icons));
    } else {
      setIcon(dataIcon);
    }
  }

  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected GeneralPath getOutline( INode node ) {
    IRectangle layout = node.getLayout().toRectD();
    double cornerSize = Math.min(layout.getWidth(), layout.getHeight()) * 0.4;

    GeneralPath path = new GeneralPath();
    path.moveTo(0, 0);
    path.lineTo(layout.getWidth() - cornerSize, 0);
    path.lineTo(layout.getWidth(), cornerSize);
    path.lineTo(layout.getWidth(), layout.getHeight());
    path.lineTo(0, layout.getHeight());
    path.close();

    Matrix2D transform = new Matrix2D();
    transform.translate(layout.getTopLeft());
    path.transform(transform);
    return path;
  }

}
