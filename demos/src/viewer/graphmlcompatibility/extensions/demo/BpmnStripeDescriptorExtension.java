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
package viewer.graphmlcompatibility.extensions.demo;

import com.yworks.yfiles.utils.Obfuscation;
import complete.bpmn.view.StripeDescriptor;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graphml.MarkupExtension;

import java.awt.Paint;

/**
 * Handles the yFiles for Java (Swing) 3.0.x version of GraphML element
 * <code>&lt;StripeDescriptor&gt;</code> from namespace
 * <code>http://www.yworks.com/xml/yfiles-bpmn/1.0</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class BpmnStripeDescriptorExtension extends MarkupExtension {
  private Paint backgroundPaint;
  private Paint borderPaint;
  private double borderThickness;
  private Paint insetPaint;

  public BpmnStripeDescriptorExtension() {
    StripeDescriptor prototype = new StripeDescriptor();
    backgroundPaint = prototype.getBackgroundPaint();
    borderPaint = prototype.getBorderPaint();
    borderThickness = prototype.getBorderThickness();
    insetPaint = prototype.getInsetPaint();
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

  /**
   * Handles the GraphML alias <code>BorderBrush</code> used in yFiles for
   * Java 3.0.x for property <code>BorderPaint</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getBorderPaint()
   */
  public Paint getBorderBrush() {
    return getBorderPaint();
  }

  /**
   * Handles the GraphML alias <code>BorderBrush</code> used in yFiles for
   * Java 3.0.x for property <code>BorderPaint</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setBorderPaint(Paint)
   */
  public void setBorderBrush( Paint value ) {
    setBorderPaint(value);
  }

  public Paint getBorderPaint() {
    return borderPaint;
  }

  public void setBorderPaint( Paint value ) {
    borderPaint = value;
  }

  public double getBorderThickness() {
    return borderThickness;
  }

  public void setBorderThickness( double value ) {
    borderThickness = value;
  }

  /**
   * Handles the GraphML alias <code>InsetBrush</code> used in yFiles for
   * Java 3.0.x for property <code>InsetPaint</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #getInsetPaint()
   */
  public Paint getInsetBrush() {
    return getInsetPaint();
  }

  /**
   * Handles the GraphML alias <code>InsetBrush</code> used in yFiles for
   * Java 3.0.x for property <code>InsetPaint</code>.
   * yFiles for Java (Swing) 3.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for Java (Swing) 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setInsetPaint(Paint)
   */
  public void setInsetBrush( Paint value ) {
    setInsetPaint(value);
  }

  public Paint getInsetPaint() {
    return insetPaint;
  }

  public void setInsetPaint( Paint value ) {
    insetPaint = value;
  }


  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    StripeDescriptor descriptor = new StripeDescriptor();
    descriptor.setBackgroundPaint(getBackgroundPaint());
    descriptor.setBorderPaint(getBorderPaint());
    descriptor.setBorderThickness(getBorderThickness());
    descriptor.setInsetPaint(getInsetPaint());
    return descriptor;
  }
}
