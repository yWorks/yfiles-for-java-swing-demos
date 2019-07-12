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

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.Pen;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;PolylineEdgeStyle&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class PolylineEdgeStyleExtension extends MarkupExtension {
  private Pen pen;
  private double smoothingLength;
  private IArrow sourceArrow;
  private IArrow targetArrow;

  public PolylineEdgeStyleExtension() {
    PolylineEdgeStyle prototype = new PolylineEdgeStyle();
    pen = prototype.getPen();
    smoothingLength = prototype.getSmoothingLength();
    sourceArrow = prototype.getSourceArrow();
    targetArrow = prototype.getTargetArrow();
  }


  public Pen getPen() {
    return pen;
  }

  public void setPen( Pen value ) {
    pen = value;
  }

  /**
   * Handles the GraphML alias <code>Smoothing</code> used in yFiles for
   * Java 3.0.x for property <code>SmoothingLength</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getSmoothingLength()
   */
  public double getSmoothing() {
    return getSmoothingLength();
  }

  /**
   * Handles the GraphML alias <code>Smoothing</code> used in yFiles for
   * Java 3.0.x for property <code>SmoothingLength</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setSmoothingLength(double)
   */
  public void setSmoothing( double value ) {
    setSmoothingLength(value);
  }

  public double getSmoothingLength() {
    return smoothingLength;
  }

  public void setSmoothingLength( double value ) {
    smoothingLength = value;
  }

  public IArrow getTargetArrow() {
    return targetArrow;
  }

  public void setTargetArrow( IArrow value ) {
    targetArrow = value;
  }

  public IArrow getSourceArrow() {
    return sourceArrow;
  }

  public void setSourceArrow( IArrow value ) {
    sourceArrow = value;
  }

  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    PolylineEdgeStyle style = new PolylineEdgeStyle();
    style.setPen(getPen());
    style.setSmoothingLength(getSmoothingLength());
    style.setTargetArrow(getTargetArrow());
    style.setSourceArrow(getSourceArrow());
    return style;
  }
}
