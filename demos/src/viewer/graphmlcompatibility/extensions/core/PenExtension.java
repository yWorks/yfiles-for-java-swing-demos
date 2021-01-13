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
package viewer.graphmlcompatibility.extensions.core;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.Pen;

import java.awt.Paint;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;Pen&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class PenExtension extends MarkupExtension {
  private Paint paint;
  private int endCap;
  private int lineJoin;
  private DashStyle dashStyle;
  private double miterLimit;
  private double thickness;

  public PenExtension() {
    Pen prototype = new Pen();
    paint = prototype.getPaint();
    endCap = prototype.getEndCap();
    lineJoin = prototype.getLineJoin();
    dashStyle = prototype.getDashStyle();
    miterLimit = prototype.getMiterLimit();
    thickness = prototype.getThickness();
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

  public int getEndCap() {
    return endCap;
  }

  public void setEndCap( int value ) {
    endCap = value;
  }

  public int getLineJoin() {
    return lineJoin;
  }

  public void setLineJoin( int value ) {
    lineJoin = value;
  }

  public DashStyle getDashStyle() {
    return dashStyle;
  }

  public void setDashStyle( DashStyle value ) {
    dashStyle = value;
  }

  public double getMiterLimit() {
    return miterLimit;
  }

  public void setMiterLimit( double value ) {
    miterLimit = value;
  }

  public double getThickness() {
    return thickness;
  }

  public void setThickness( double value ) {
    thickness = value;
  }


  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    Pen pen = new Pen();
    pen.setPaint(getPaint());
    pen.setEndCap(getEndCap());
    pen.setLineJoin(getLineJoin());
    pen.setDashStyle(getDashStyle());
    pen.setMiterLimit(getMiterLimit());
    pen.setThickness(getThickness());
    return pen;
  }
}
