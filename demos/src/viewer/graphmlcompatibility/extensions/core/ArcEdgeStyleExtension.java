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
import com.yworks.yfiles.graph.styles.ArcEdgeStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.Pen;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;ArcEdgeStyle&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class ArcEdgeStyleExtension extends MarkupExtension {
  private boolean fixedHeightEnabled;
  private double height;
  private boolean heightHandleProviderEnabled;
  private Pen pen;
  private IArrow sourceArrow;
  private IArrow targetArrow;

  public ArcEdgeStyleExtension() {
    ArcEdgeStyle prototype = new ArcEdgeStyle();
    fixedHeightEnabled = prototype.isFixedHeightEnabled();
    height = prototype.getHeight();
    heightHandleProviderEnabled = prototype.isHeightHandleProviderEnabled();
    pen = prototype.getPen();
    sourceArrow = prototype.getSourceArrow();
    targetArrow = prototype.getTargetArrow();
  }

  /**
   * Handles the GraphML alias <code>FixedHeight</code> used in yFiles for
   * Java 3.0.x for property <code>FixedHeightEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isFixedHeightEnabled()
   */
  public boolean isFixedHeight() {
    return isFixedHeightEnabled();
  }

  /**
   * Handles the GraphML alias <code>FixedHeight</code> used in yFiles for
   * Java 3.0.x for property <code>FixedHeightEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setFixedHeightEnabled(boolean)
   */
  public void setFixedHeight( boolean value ) {
    setFixedHeightEnabled(value);
  }

  public boolean isFixedHeightEnabled() {
    return fixedHeightEnabled;
  }

  public void setFixedHeightEnabled( boolean value ) {
    fixedHeightEnabled = value;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight( double value ) {
    height = value;
  }

  /**
   * Handles the GraphML alias <code>ProvideHeightHandle</code> used in yFiles for
   * Java 3.0.x for property <code>HeightHandleProviderEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isHeightHandleProviderEnabled()
   */
  public boolean isProvideHeightHandle() {
    return isHeightHandleProviderEnabled();
  }

  /**
   * Handles the GraphML alias <code>ProvideHeightHandle</code> used in yFiles for
   * Java 3.0.x for property <code>HeightHandleProviderEnabled</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setHeightHandleProviderEnabled(boolean)
   */
  public void setProvideHeightHandle( boolean value ) {
    setHeightHandleProviderEnabled(value);
  }

  public boolean isHeightHandleProviderEnabled() {
    return heightHandleProviderEnabled;
  }

  public void setHeightHandleProviderEnabled( boolean value ) {
    heightHandleProviderEnabled = value;
  }

  public Pen getPen() {
    return pen;
  }

  public void setPen( Pen value ) {
    pen = value;
  }

  public IArrow getSourceArrow() {
    return sourceArrow;
  }

  public void setSourceArrow( IArrow value ) {
    sourceArrow = value;
  }

  public IArrow getTargetArrow() {
    return targetArrow;
  }

  public void setTargetArrow( IArrow value ) {
    targetArrow = value;
  }


  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    ArcEdgeStyle style = new ArcEdgeStyle();
    style.setFixedHeightEnabled(isFixedHeightEnabled());
    style.setHeight(getHeight());
    style.setHeightHandleProviderEnabled(isHeightHandleProviderEnabled());
    style.setPen(getPen());
    style.setSourceArrow(getSourceArrow());
    style.setTargetArrow(getTargetArrow());
    return style;
  }
}
