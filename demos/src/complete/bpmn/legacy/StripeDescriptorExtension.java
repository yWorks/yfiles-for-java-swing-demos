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
package complete.bpmn.legacy;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.view.Colors;
import complete.bpmn.view.StripeDescriptor;
import java.awt.Paint;

public class StripeDescriptorExtension extends MarkupExtension {
  private Paint backgroundPaint;

  public final Paint getBackgroundPaint() {
    return this.backgroundPaint;
  }

  public final void setBackgroundPaint( Paint value ) {
    this.backgroundPaint = value;
  }

  private Paint insetPaint;

  public final Paint getInsetPaint() {
    return this.insetPaint;
  }

  public final void setInsetPaint( Paint value ) {
    this.insetPaint = value;
  }

  private Paint borderPaint;

  public final Paint getBorderPaint() {
    return this.borderPaint;
  }

  public final void setBorderPaint( Paint value ) {
    this.borderPaint = value;
  }

  private double borderThickness = 0;

  public final double getBorderThickness() {
    return this.borderThickness;
  }

  public final void setBorderThickness( double value ) {
    this.borderThickness = value;
  }

  public StripeDescriptorExtension() {
    setBackgroundPaint(Colors.TRANSPARENT);
    setInsetPaint(Colors.TRANSPARENT);
    setBorderPaint(Colors.BLACK);
    setBorderThickness(1);
  }

  @Override
  public Object provideValue( ILookup serviceProvider ) {
    StripeDescriptor stripeDescriptor = new StripeDescriptor();
    stripeDescriptor.setBackgroundPaint(getBackgroundPaint());
    stripeDescriptor.setBorderPaint(getBorderPaint());
    stripeDescriptor.setBorderThickness(getBorderThickness());
    stripeDescriptor.setInsetPaint(getInsetPaint());
    return stripeDescriptor;
  }

}
