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
package viewer.graphmlcompatibility.extensions.core;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.GeneralPathNodeStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.Pen;

import java.awt.Paint;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;GeneralPathNodeStyle&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class GeneralPathNodeStyleExtension extends MarkupExtension {
  private boolean normalizingGradientsEnabled;
  private Paint paint;
  private GeneralPath path;
  private Pen pen;

  public GeneralPathNodeStyleExtension() {
    GeneralPathNodeStyle prototype = new GeneralPathNodeStyle();
    normalizingGradientsEnabled = prototype.isNormalizingGradientsEnabled();
    paint = prototype.getPaint();
    pen = prototype.getPen();
  }


  public boolean isNormalizingGradientsEnabled() {
    return normalizingGradientsEnabled;
  }

  public void setNormalizingGradientsEnabled( boolean value ) {
    normalizingGradientsEnabled = value;
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

  public GeneralPath getPath() {
    return path;
  }

  public void setPath( GeneralPath value ) {
    path = value;
  }

  public Pen getPen() {
    return pen;
  }

  public void setPen( Pen value ) {
    pen = value;
  }


  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    GeneralPathNodeStyle style = new GeneralPathNodeStyle();
    style.setNormalizingGradientsEnabled(isNormalizingGradientsEnabled());
    style.setPaint(getPaint());
    style.setPath(getPath());
    style.setPen(getPen());
    return style;
  }
}
