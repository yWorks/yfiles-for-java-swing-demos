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
package complete.tableeditor;

import com.yworks.yfiles.view.Colors;

import java.awt.Color;
import java.awt.Paint;

/**
 * Bundles common visualization parameters for {@link com.yworks.yfiles.graph.IStripe}
 * instances.
 */
public class StripeDescriptor {
  // the paint to fill the background of a stripe with
  private Paint backgroundPaint = Colors.TRANSPARENT;
  // the paint to fill the inset of a stripe with
  private Paint insetPaint = Colors.TRANSPARENT;
  // the paint to draw the border of a stripe with
  private Paint borderPaint = Color.BLACK;
  // the thickness of a stripe's border
  private double borderThickness = 1;

  /**
   * Initializes a new <code>StripeDescriptor</code> instance.
   */
  public StripeDescriptor() {
  }

  /**
   * Initializes a new <code>StripeDescriptor</code> instance with the given background and insets paint.
   */
  public StripeDescriptor(Paint backgroundPaint, Paint insetPaint) {
    this.backgroundPaint = backgroundPaint;
    this.insetPaint = insetPaint;
  }

  /**
   * Returns the paint to fill the background of a stripe with.
   */
  public Paint getBackgroundPaint() {
    return backgroundPaint;
  }

  /**
   * Sets the paint to fill the background of a stripe with.
   */
  public void setBackgroundPaint(Paint backgroundPaint) {
    this.backgroundPaint = backgroundPaint;
  }

  /**
   * Returns the paint to fill the inset of a stripe with.
   */
  public Paint getInsetPaint() {
    return insetPaint;
  }

  /**
   * Sets the paint to fill the inset of a stripe with.
   */
  public void setInsetPaint(Paint insetPaint) {
    this.insetPaint = insetPaint;
  }

  /**
   * Returns the paint to draw the border of a stripe with.
   */
  public Paint getBorderPaint() {
    return borderPaint;
  }

  /**
   * Sets the paint to draw the border of a stripe with.
   */
  public void setBorderPaint(Paint borderPaint) {
    this.borderPaint = borderPaint;
  }

  /**
   * Returns the thickness of a stripe's border.
   */
  public double getBorderThickness() {
    return borderThickness;
  }

  /**
   * Sets the thickness of a stripe's border.
   */
  public void setBorderThickness(double borderThickness) {
    this.borderThickness = borderThickness;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    StripeDescriptor that = (StripeDescriptor) o;

    if (Double.compare(that.borderThickness, borderThickness) != 0) return false;
    if (backgroundPaint != null ? !backgroundPaint.equals(that.backgroundPaint) : that.backgroundPaint != null) return false;
    if (borderPaint != null ? !borderPaint.equals(that.borderPaint) : that.borderPaint != null) return false;
    if (insetPaint != null ? !insetPaint.equals(that.insetPaint) : that.insetPaint != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = backgroundPaint != null ? backgroundPaint.hashCode() : 0;
    result = 31 * result + (insetPaint != null ? insetPaint.hashCode() : 0);
    result = 31 * result + (borderPaint != null ? borderPaint.hashCode() : 0);
    temp = Double.doubleToLongBits(borderThickness);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }
}
