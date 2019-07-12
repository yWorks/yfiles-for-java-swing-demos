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
package viewer.graphmlcompatibility.extensions.core;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.TextTrimming;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.VerticalAlignment;

import java.awt.Font;
import java.awt.Paint;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;SimpleLabelStyle&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class SimpleLabelStyleExtension extends MarkupExtension {
  private Paint backgroundPaint;
  private VerticalAlignment verticalTextAlignment;
  private boolean textClippingEnabled;
  private Pen backgroundPen;
  private Font font;
  private boolean autoFlippingEnabled;
  private Paint textPaint;
  private TextAlignment textAlignment;
  private InsetsD insets;
  private boolean usingFractionalFontMetricsEnabled;
  private boolean normalizingGradientsEnabled;
  private TextWrapping textWrapping;
  private TextTrimming textTrimming;

  public SimpleLabelStyleExtension() {
    DefaultLabelStyle prototype = new DefaultLabelStyle();
    verticalTextAlignment = prototype.getVerticalTextAlignment();
    textClippingEnabled = prototype.isTextClippingEnabled();
    autoFlippingEnabled = prototype.isAutoFlippingEnabled();
    textPaint = prototype.getTextPaint();
    textAlignment = prototype.getTextAlignment();
    insets = prototype.getInsets();
    normalizingGradientsEnabled = prototype.isNormalizingGradientsEnabled();
    textWrapping = prototype.getTextWrapping();
    textTrimming = prototype.getTextTrimming();
  }


  /**
   * Handles the GraphML alias <code>BackgroundBrush</code> used in yFiles for
   * Java 3.0.x for property <code>BackgroundPaint</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getBackgroundPaint()
   */
  public Paint getBackgroundBrush() {
    return getBackgroundPaint();
  }

  /**
   * Handles the GraphML alias <code>BackgroundBrush</code> used in yFiles for
   * Java 3.0.x for property <code>BackgroundPaint</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setBackgroundPaint(Paint)
   */
  public void setBackgroundBrush( Paint value ) {
    setBackgroundPaint(value);
  }

  public Paint getBackgroundPaint() {
    return backgroundPaint;
  }

  public void setBackgroundPaint( Paint value ) {
    backgroundPaint = value;
  }

  public VerticalAlignment getVerticalTextAlignment() {
    return verticalTextAlignment;
  }

  public void setVerticalTextAlignment( VerticalAlignment value ) {
    verticalTextAlignment = value;
  }

  /**
   * Handles the GraphML alias <code>ClipText</code> used in yFiles for
   * Java 3.0.x for property <code>TextClippingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isTextClippingEnabled()
   */
  public boolean isClipText() {
    return isTextClippingEnabled();
  }

  /**
   * Handles the GraphML alias <code>ClipText</code> used in yFiles for
   * Java 3.0.x for property <code>TextClippingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setTextClippingEnabled(boolean)
   */
  public void setClipText( boolean value ) {
    setTextClippingEnabled(value);
  }

  public boolean isTextClippingEnabled() {
    return textClippingEnabled;
  }

  public void setTextClippingEnabled( boolean value ) {
    textClippingEnabled = value;
  }

  public Pen getBackgroundPen() {
    return backgroundPen;
  }

  public void setBackgroundPen( Pen value ) {
    backgroundPen = value;
  }

  public Font getFont() {
    return font;
  }

  public void setFont( Font value ) {
    font = value;
  }

  /**
   * Handles the GraphML alias <code>AutoFlip</code> used in yFiles for
   * Java 3.0.x for property <code>AutoFlippingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isAutoFlippingEnabled()
   */
  public boolean isAutoFlip() {
    return isAutoFlippingEnabled();
  }

  /**
   * Handles the GraphML alias <code>AutoFlip</code> used in yFiles for
   * Java 3.0.x for property <code>AutoFlippingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setAutoFlippingEnabled(boolean)
   */
  public void setAutoFlip( boolean value ) {
    setAutoFlippingEnabled(value);
  }

  public boolean isAutoFlippingEnabled() {
    return autoFlippingEnabled;
  }

  public void setAutoFlippingEnabled( boolean value ) {
    autoFlippingEnabled = value;
  }

  /**
   * Handles the GraphML alias <code>TextBrush</code> used in yFiles for
   * Java 3.0.x for property <code>TextPaint</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getTextPaint()
   */
  public Paint getTextBrush() {
    return getTextPaint();
  }

  /**
   * Handles the GraphML alias <code>TextBrush</code> used in yFiles for
   * Java 3.0.x for property <code>TextPaint</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setTextPaint(Paint)
   */
  public void setTextBrush( Paint value ) {
    setTextPaint(value);
  }

  public Paint getTextPaint() {
    return textPaint;
  }

  public void setTextPaint( Paint value ) {
    textPaint = value;
  }

  public TextAlignment getHorizontalTextAlignment() {
    return textAlignment;
  }

  public void setHorizontalTextAlignment( TextAlignment value ) {
    textAlignment = value;
  }

  public InsetsD getInsets() {
    return insets;
  }

  public void setInsets( InsetsD value ) {
    insets = value;
  }

  public boolean isUsingFractionalFontMetricsEnabled() {
    return usingFractionalFontMetricsEnabled;
  }

  public void setUsingFractionalFontMetricsEnabled( boolean value ) {
    usingFractionalFontMetricsEnabled = value;
  }

  public boolean isNormalizingGradientsEnabled() {
    return normalizingGradientsEnabled;
  }

  public void setNormalizingGradientsEnabled( boolean value ) {
    normalizingGradientsEnabled = value;
  }

  public TextWrapping getTextWrapping() {
    return textWrapping;
  }

  public void setTextWrapping( TextWrapping value ) {
    textWrapping = value;
  }

  public TextTrimming getTextTrimming() {
    return textTrimming;
  }

  public void setTextTrimming( TextTrimming value ) {
    textTrimming = value;
  }


  @Override
  public Object provideValue( ILookup serviceProvider ) {
    DefaultLabelStyle style = new DefaultLabelStyle();
    style.setBackgroundPaint(getBackgroundPaint());
    VerticalAlignment verticalTextAlignment = getVerticalTextAlignment();
    if (verticalTextAlignment != null) {
      style.setVerticalTextAlignment(verticalTextAlignment);
    }
    style.setTextClippingEnabled(isTextClippingEnabled());
    Pen backgroundPen = getBackgroundPen();
    if (backgroundPen != null) {
      style.setBackgroundPen(backgroundPen);
    }
    Font font = getFont();
    if (font != null) {
      style.setFont(font);
    }
    style.setAutoFlippingEnabled(isAutoFlippingEnabled());
    Paint textPaint = getTextPaint();
    if (textPaint != null) {
      style.setTextPaint(textPaint);
    }
    TextAlignment horizontalTextAlignment = getHorizontalTextAlignment();
    if (horizontalTextAlignment != null) {
      style.setTextAlignment(horizontalTextAlignment);
    }
    InsetsD insets = getInsets();
    if (insets != null) {
      style.setInsets(insets);
    }
    style.setUsingFractionalFontMetricsEnabled(isUsingFractionalFontMetricsEnabled());
    style.setNormalizingGradientsEnabled(isNormalizingGradientsEnabled());
    TextWrapping textWrapping = getTextWrapping();
    if (textWrapping != null) {
      style.setTextWrapping(textWrapping);
    }
    TextTrimming textTrimming = getTextTrimming();
    if (textTrimming != null) {
      style.setTextTrimming(textTrimming);
    }
    return style;
  }
}
