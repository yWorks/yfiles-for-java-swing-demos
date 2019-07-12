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
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.Pen;

import java.awt.Paint;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;ShinyPlateNodeStyle&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class ShinyPlateNodeStyleExtension extends MarkupExtension {
  private InsetsD insets;
  private Paint paint;
  private Pen pen;
  private double radius;
  private boolean shadowDrawingEnabled;

  public ShinyPlateNodeStyleExtension() {
    ShinyPlateNodeStyle prototype = new ShinyPlateNodeStyle();
    insets = prototype.getInsets();
    paint = prototype.getPaint();
    radius = prototype.getRadius();
    shadowDrawingEnabled = prototype.isShadowDrawingEnabled();
  }


  public InsetsD getInsets() {
    return insets;
  }

  public void setInsets( InsetsD value ) {
    insets = value;
  }

  /**
   * Handles the GraphML alias <code>Brush</code> used in yFiles for
   * Java 3.0.x for property <code>Paint</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getPaint()
   */
  public Paint getBrush() {
    return getPaint();
  }

  /**
   * Handles the GraphML alias <code>Brush</code> used in yFiles for
   * Java 3.0.x for property <code>Paint</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setPaint(Paint)
   */
  public void setBrush( Paint value ) {
    setPaint(value);
  }

  public Paint getPaint() {
    return paint;
  }

  public void setPaint( Paint value ) {
    paint = value;
  }

  public Pen getPen() {
    return pen;
  }

  public void setPen( Pen value ) {
    pen = value;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius( double value ) {
    radius = value;
  }

  /**
   * Handles the GraphML alias <code>DrawShadow</code> used in yFiles for
   * Java 3.0.x for property <code>ShadowDrawingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isShadowDrawingEnabled()
   */
  public boolean isDrawShadow() {
    return isShadowDrawingEnabled();
  }

  /**
   * Handles the GraphML alias <code>DrawShadow</code> used in yFiles for
   * Java 3.0.x for property <code>ShadowDrawingEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setShadowDrawingEnabled(boolean)
   */
  public void setDrawShadow( boolean value ) {
    setShadowDrawingEnabled(value);
  }

  public boolean isShadowDrawingEnabled() {
    return shadowDrawingEnabled;
  }

  public void setShadowDrawingEnabled( boolean value ) {
    shadowDrawingEnabled = value;
  }


  @Override
  public Object provideValue( ILookup serviceProvider ) {
    ShinyPlateNodeStyle style = new ShinyPlateNodeStyle();
    style.setInsets(getInsets());
    style.setPaint(getPaint());
    style.setPen(getPen());
    style.setRadius(getRadius());
    style.setShadowDrawingEnabled(isShadowDrawingEnabled());
    return style;
  }
}
