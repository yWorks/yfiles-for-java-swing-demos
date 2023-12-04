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
package complete.bpmn.di;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.VerticalAlignment;
import org.w3c.dom.Element;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;

/**
 * Class for BPMNLabelStyle objects.
 */
public class BpmnLabelStyle {
  /**
   * Constant that sets the standard Text size of Labels. yFiles Standard is 12 pt, but the Bpmn Demo files look better with
   * 11pt
   */
  private static final int LABEL_TEXT_SIZE = 11;

  private String id;

  /**
   * The id (name) of this style.
   * @return The Id.
   * @see #setId(String)
   */
  public final String getId() {
    return this.id;
  }

  /**
   * The id (name) of this style.
   * @param value The Id to set.
   * @see #getId()
   */
  public final void setId( String value ) {
    this.id = value;
  }

  private String font;

  /**
   * The font used in this style.
   * @return The Font.
   * @see #setFont(String)
   */
  public final String getFont() {
    return this.font;
  }

  /**
   * The font used in this style.
   * @param value The Font to set.
   * @see #getFont()
   */
  public final void setFont( String value ) {
    this.font = value;
  }

  private float size;

  /**
   * The font size used in this style.
   * @return The Size.
   * @see #setSize(float)
   */
  public final float getSize() {
    return this.size;
  }

  /**
   * The font size used in this style.
   * @param value The Size to set.
   * @see #getSize()
   */
  public final void setSize( float value ) {
    this.size = value;
  }

  private boolean bold;

  /**
   * Whether this style depicts text in bold.
   * @return The Bold.
   * @see #setBold(boolean)
   */
  public final boolean isBold() {
    return this.bold;
  }

  /**
   * Whether this style depicts text in bold.
   * @param value The Bold to set.
   * @see #isBold()
   */
  public final void setBold( boolean value ) {
    this.bold = value;
  }

  private boolean italic;

  /**
   * Whether this style depicts text in italic.
   * @return The Italic.
   * @see #setItalic(boolean)
   */
  public final boolean isItalic() {
    return this.italic;
  }

  /**
   * Whether this style depicts text in italic.
   * @param value The Italic to set.
   * @see #isItalic()
   */
  public final void setItalic( boolean value ) {
    this.italic = value;
  }

  private boolean underline;

  /**
   * Whether this style underlines text.
   * @return The Underline.
   * @see #setUnderline(boolean)
   */
  public final boolean isUnderline() {
    return this.underline;
  }

  /**
   * Whether this style underlines text.
   * @param value The Underline to set.
   * @see #isUnderline()
   */
  public final void setUnderline( boolean value ) {
    this.underline = value;
  }

  private boolean strikeThrough;

  /**
   * Whether this style depicts style with a StrikeThrough.
   * @return The StrikeThrough.
   * @see #setStrikeThrough(boolean)
   */
  public final boolean isStrikeThrough() {
    return this.strikeThrough;
  }

  /**
   * Whether this style depicts style with a StrikeThrough.
   * @param value The StrikeThrough to set.
   * @see #isStrikeThrough()
   */
  public final void setStrikeThrough( boolean value ) {
    this.strikeThrough = value;
  }

  private DefaultLabelStyle labelStyle;

  /**
   * {@link DefaultLabelStyle} that represents this BpmnLabelStyle.
   * @return The LabelStyle.
   * @see #setLabelStyle(DefaultLabelStyle)
   */
  public final DefaultLabelStyle getLabelStyle() {
    return this.labelStyle;
  }

  /**
   * {@link DefaultLabelStyle} that represents this BpmnLabelStyle.
   * @param value The LabelStyle to set.
   * @see #getLabelStyle()
   */
  public final void setLabelStyle( DefaultLabelStyle value ) {
    this.labelStyle = value;
  }

  /**
   * Returns a new instance of the default label style.
   */
  public static final DefaultLabelStyle newDefaultInstance() {
    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();

    Font font = defaultLabelStyle.getFont();
    defaultLabelStyle.setFont(font.deriveFont(Font.PLAIN, LABEL_TEXT_SIZE));

    defaultLabelStyle.setTextWrapping(TextWrapping.WRAP);
    defaultLabelStyle.setTextAlignment(TextAlignment.CENTER);
    defaultLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    return defaultLabelStyle;
  }

  /**
   * Constructs an instance of {@link DefaultLabelStyle} representing this Style.
   * @param xStyle The XML Element to be converted into this style
   */
  public BpmnLabelStyle( Element xStyle ) {
    setId(null);
    setFont("Arial");
    setSize(0);
    setBold(false);
    setItalic(false);
    setUnderline(false);
    setStrikeThrough(false);

    setId(BpmnNamespaceManager.getAttributeValue(xStyle, BpmnNamespaceManager.NS_DC, BpmnDiConstants.ID_ATTRIBUTE));

    // Parse Values of the Label Style
    Element xFont = BpmnNamespaceManager.getElement(xStyle, BpmnNamespaceManager.NS_DC, BpmnDiConstants.FONT_ELEMENT);
    if (xFont != null) {
      setFont(BpmnNamespaceManager.getAttributeValue(xFont, BpmnNamespaceManager.NS_DC, BpmnDiConstants.NAME_ATTRIBUTE));

      String attr = BpmnNamespaceManager.getAttributeValue(xFont, BpmnNamespaceManager.NS_DC, BpmnDiConstants.SIZE_ATTRIBUTE);
      if (attr != null) {
        setSize(Float.parseFloat(attr));
      }

      attr = BpmnNamespaceManager.getAttributeValue(xFont, BpmnNamespaceManager.NS_DC, BpmnDiConstants.IS_BOLD_ATTRIBUTE);
      if (attr != null) {
        setBold(Boolean.parseBoolean(attr));
      }
      attr = BpmnNamespaceManager.getAttributeValue(xFont, BpmnNamespaceManager.NS_DC, BpmnDiConstants.IS_ITALIC_ATTRIBUTE);
      if (attr != null) {
        setItalic(Boolean.parseBoolean(attr));
      }

      attr = BpmnNamespaceManager.getAttributeValue(xFont, BpmnNamespaceManager.NS_DC, BpmnDiConstants.IS_UNDERLINE_ATTRIBUTE);
      if (attr != null) {
        setUnderline(Boolean.parseBoolean(attr));
      }

      attr = BpmnNamespaceManager.getAttributeValue(xFont, BpmnNamespaceManager.NS_DC, BpmnDiConstants.IS_STRIKE_THROUGH_ATTRIBUTE);
      if (attr != null) {
        setStrikeThrough(Boolean.parseBoolean(attr));
      }
    }

    HashMap attrs = new HashMap();

    // Set text size
    float size = getSize();
    attrs.put(TextAttribute.SIZE, Float.valueOf(size > 0 ? size : LABEL_TEXT_SIZE));

    // Set Boldness
    if (isBold()) {
      attrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
    }

    // Set Italic
    if (isItalic()) {
      attrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
    }

    // Set Underline
    if (isUnderline()) {
      attrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    }

    // Set StrikeThrough
    if (isStrikeThrough()) {
      attrs.put(TextAttribute.STRIKETHROUGH, Boolean.TRUE);
    }

    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setTextAlignment(TextAlignment.CENTER);
    labelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    labelStyle.setFont(labelStyle.getFont().deriveFont(attrs));
    labelStyle.setTextWrapping(TextWrapping.WRAP);
    labelStyle.setInsets(InsetsD.EMPTY);
    setLabelStyle(labelStyle);
  }

  /**
   * Returns the {@link DefaultLabelStyle} that represents this BpmnLabelStyle instance.
   */
  public final DefaultLabelStyle getStyle() {
    return getLabelStyle();
  }
}
