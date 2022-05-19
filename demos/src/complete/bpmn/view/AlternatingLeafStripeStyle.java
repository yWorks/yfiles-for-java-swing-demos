/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IColumn;
import com.yworks.yfiles.graph.IRow;
import com.yworks.yfiles.graph.IStripe;
import com.yworks.yfiles.graph.styles.AbstractStripeStyle;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 * Custom stripe style that alternates the visualizations for the leaf nodes and uses a different style for all parent
 * stripes.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = false)
public class AlternatingLeafStripeStyle extends AbstractStripeStyle {
  private StripeDescriptor evenLeafDescriptor;

  /**
   * Visualization for all leaf stripes that have an even index.
   * @return The EvenLeafDescriptor.
   * @see #setEvenLeafDescriptor(StripeDescriptor)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final StripeDescriptor getEvenLeafDescriptor() {
    return this.evenLeafDescriptor;
  }

  /**
   * Visualization for all leaf stripes that have an even index.
   * @param value The EvenLeafDescriptor to set.
   * @see #getEvenLeafDescriptor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final void setEvenLeafDescriptor( StripeDescriptor value ) {
    this.evenLeafDescriptor = value;
  }

  private StripeDescriptor parentDescriptor;

  /**
   * Visualization for all stripes that are not leafs.
   * @return The ParentDescriptor.
   * @see #setParentDescriptor(StripeDescriptor)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final StripeDescriptor getParentDescriptor() {
    return this.parentDescriptor;
  }

  /**
   * Visualization for all stripes that are not leafs.
   * @param value The ParentDescriptor to set.
   * @see #getParentDescriptor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final void setParentDescriptor( StripeDescriptor value ) {
    this.parentDescriptor = value;
  }

  private StripeDescriptor oddLeafDescriptor;

  /**
   * Visualization for all leaf stripes that have an odd index.
   * @return The OddLeafDescriptor.
   * @see #setOddLeafDescriptor(StripeDescriptor)
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final StripeDescriptor getOddLeafDescriptor() {
    return this.oddLeafDescriptor;
  }

  /**
   * Visualization for all leaf stripes that have an odd index.
   * @param value The OddLeafDescriptor to set.
   * @see #getOddLeafDescriptor()
   */
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  public final void setOddLeafDescriptor( StripeDescriptor value ) {
    this.oddLeafDescriptor = value;
  }


  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected StripeVisual createVisual( IRenderContext context, IStripe stripe ) {
    StripeVisual visual = new StripeVisual();
    visual.update(stripe.getLayout(), getStripeInsets(stripe), getStripeDescriptor(stripe));
    return visual;
  }

  /**
   * Returns the insets of the given stripe. Depending on the stripe's type, we need to consider horizontal or vertical
   * insets.
   */
  private static InsetsD getStripeInsets(IStripe stripe) {
    if (stripe instanceof IColumn) {
      IColumn col = (IColumn)stripe;
      InsetsD actualInsets = col.getActualInsets();
      return new InsetsD(actualInsets.getTop(), 0, actualInsets.getBottom(), 0);
    } else {
      IRow row = (IRow)stripe;
      InsetsD actualInsets = row.getActualInsets();
      return new InsetsD(0, actualInsets.getLeft(), 0, actualInsets.getRight());
    }
  }

  /**
   * Returns the {@link complete.tableeditor.StripeDescriptor} for the given stripe.
   */
  private StripeDescriptor getStripeDescriptor(IStripe stripe) {
    if (stripe.getChildStripes().iterator().hasNext()) {
      //Parent stripe - use the parent descriptor
      return getParentDescriptor();
    } else {
      int index;
      if (stripe instanceof IColumn) {
        //Get all leaf columns
        index = getIndex(stripe, stripe.getTable().getRootColumn().getLeaves());
        //Use the correct descriptor
        return index % 2 == 0 ? getEvenLeafDescriptor() : getOddLeafDescriptor();
      } else {
        index = getIndex(stripe, stripe.getTable().getRootRow().getLeaves());
        return index % 2 == 0 ? getEvenLeafDescriptor() : getOddLeafDescriptor();
      }
    }
  }
  @Override
  @Obfuscation(stripAfterObfuscation = false, exclude = true)
  protected StripeVisual updateVisual( IRenderContext context, IVisual oldVisual, IStripe stripe ) {
    StripeVisual visual = (StripeVisual) oldVisual;
    visual.update(stripe.getLayout(), getStripeInsets(stripe), getStripeDescriptor(stripe));
    return visual;
  }


  /**
   * Paints a stripe by filling its background and insets as well as drawing its
   * border.
   */
  static class StripeVisual implements IVisual {
    // shape to fill the left inset with
    private Rectangle2D leftInsetRect;
    // shape to fill the top inset with
    private Rectangle2D topInsetRect;
    // shape to fill the right inset with
    private Rectangle2D rightInsetRect;
    // shape to fill the bottom inset with
    private Rectangle2D bottomInsetRect;
    // shape to fill the background and to draw the border with
    private Rectangle2D backgroundRect;
    // the pen to draw the border with
    private Pen borderPen;

    // the layout of the stripe
    private RectD layout;
    // the insets of the stripe
    private InsetsD insets;
    // specifies the paints of the background and insets as well as the paint
    // and thickness of the border of the stripe
    private StripeDescriptor descriptor;

    StripeVisual() {
      topInsetRect = new Rectangle2D.Double();
      rightInsetRect = new Rectangle2D.Double();
      bottomInsetRect = new Rectangle2D.Double();
      leftInsetRect = new Rectangle2D.Double();
      backgroundRect = new Rectangle2D.Double();
      borderPen = new Pen();

      layout = RectD.EMPTY;
      insets = InsetsD.EMPTY;
    }

    /**
     * Checks if the properties of the stripe have been changed. If so, updates all items needed to paint the stripe.
     * @param stripeLayout the layout of the stripe
     * @param insets     the insets of the stripe
     * @param descriptor specifies the paints of the background and insets as well as the paint and thickness of the
     *                   border of the stripe
     */
    void update(IRectangle stripeLayout, InsetsD insets, StripeDescriptor descriptor) {
      RectD layout = stripeLayout.toRectD();
      if (!layout.equals(this.layout) || !insets.equals(this.insets)) {
        this.layout = layout;
        this.insets = insets;
        leftInsetRect.setFrame(layout.getX(), layout.getY(), insets.getLeft(), layout.getHeight());
        topInsetRect.setFrame(layout.getX(), layout.getY(), layout.getWidth(), insets.getTop());
        rightInsetRect.setFrame(layout.getMaxX() - insets.getRight(), layout.getY(), insets.getRight(), layout.getHeight());
        bottomInsetRect.setFrame(layout.getX(), layout.getMaxY() - insets.getBottom(), layout.getWidth(), insets.getBottom());
        backgroundRect.setFrame(layout.getX(), layout.getY(), layout.getWidth(), layout.getHeight());
      }

      if (!descriptor.equals(this.descriptor)) {
        this.descriptor = descriptor;
        borderPen.setPaint(descriptor.getBorderPaint());
        borderPen.setThickness(descriptor.getBorderThickness());
      }
    }

    @Override
    public void paint(IRenderContext context, Graphics2D gfx) {
      // all paint methods must maintain the state of the graphics context:
      // remember the old state and reset it afterwards.
      final Paint oldPaint = gfx.getPaint();
      final Stroke oldStroke = gfx.getStroke(); // changed by the Pen
      try {
        // fill the background
        gfx.setPaint(descriptor.getBackgroundPaint());
        gfx.fill(backgroundRect);

        // fill the insets
        gfx.setPaint(descriptor.getInsetPaint());
        if (insets.getLeft() > 0) {
          gfx.fill(leftInsetRect);
        }
        if (insets.getTop() > 0) {
          gfx.fill(topInsetRect);
        }
        if (insets.getRight() > 0) {
          gfx.fill(rightInsetRect);
        }
        if (insets.getBottom() > 0) {
          gfx.fill(bottomInsetRect);
        }

        // draw the border
        borderPen.commit(gfx);
        gfx.draw(backgroundRect);
      } finally {
        // after all is done, reset the state
        gfx.setPaint(oldPaint);
        gfx.setStroke(oldStroke);
      }
    }
  }

  private static int getIndex(IStripe row, Iterable<IStripe> leafs) {
    int index;
    index = -1;
    int i = 0;
    for(IStripe leaf: leafs) {
      if(leaf == row) {
        index = i;
        break;
      }
      ++i;
    }
    return index;
  }
}
