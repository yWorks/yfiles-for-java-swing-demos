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
package tutorial02_CustomStyles.step26_CustomGroupStyle;

import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleButtonVisualCreator;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecoratorRenderer;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.view.input.INodeInsetsProvider;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;

/////////////// This class is new in this sample ///////////////

/**
 * Customizes the collapse button visualization of the {@link com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator}.
 */
public class MyCollapsibleNodeStyleDecorator extends CollapsibleNodeStyleDecorator {
  private SizeD buttonSize;

  /**
   * Initializes a new <code>MyCollapsibleNodeStyleDecorator</code> instance using a size of 15x15 for the
   * collapse button.
   */
  public MyCollapsibleNodeStyleDecorator(INodeStyle decoratedStyle) {
    this(decoratedStyle, new SizeD(15, 15));
  }

  /**
   * Initializes a new <code>MyCollapsibleNodeStyleDecorator</code> instance using the given size for the
   * collapse button.
   */
  public MyCollapsibleNodeStyleDecorator(INodeStyle decoratedStyle, SizeD buttonSize) {
    super(decoratedStyle, new MyCollapsibleNodeStyleDecoratorRenderer());
    this.buttonSize = buttonSize;
  }

  /**
   * Returns an IVisualCreator that provides our own implementation of the button visual.
   */
  @Override
  public CollapsibleNodeStyleButtonVisualCreator getButtonVisualCreator() {
    return new MyCollapsibleNodeStyleButtonVisualCreator();
  }

  /**
   * Customizes the {@link #lookup(Class)} method to provide the {@link com.yworks.yfiles.view.input.INodeInsetsProvider} of
   * the wrapped style.
   */
  private static class MyCollapsibleNodeStyleDecoratorRenderer extends CollapsibleNodeStyleDecoratorRenderer {
    /**
     * Overridden to avoid the base insets provider, which adds insets for the label.
     */
    @Override
    public <TLookup> TLookup lookup(Class<TLookup> type) {
      if (type == INodeInsetsProvider.class) {
        // return the implementation of the wrapped style directly
        INodeStyle wrappedStyle = getStyle().getWrapped();
        return wrappedStyle.getRenderer().getContext(getNode(), wrappedStyle).lookup(type);
      }
      return super.lookup(type);
    }
  }

  /**
   * An {@link IVisual} that paints the collapsed/expanded button.
   */
  private static class ButtonVisual implements IVisual {
    // determines whether to paint a collapsed or expanded button
    private boolean expanded;
    // the size the button is painted with
    private SizeD buttonSize;

    public ButtonVisual(boolean expanded) {
      this.expanded = expanded;
    }

    public void setExpanded(boolean expanded) {
      this.expanded = expanded;
    }

    public void setButtonSize(SizeD buttonSize) {
      this.buttonSize = buttonSize;
    }

    @Override
    public void paint(IRenderContext context, Graphics2D gfx) {
      // All paint methods must maintain the state of the graphics context.
      // To do this, remember the old state and reset it afterwards.
      AffineTransform oldTransform = gfx.getTransform();
      Paint oldPaint = gfx.getPaint();
      try {
        gfx.scale(buttonSize.getWidth() * 0.2, buttonSize.getHeight() * 0.2);
        gfx.setPaint(Colors.GRAY);
        gfx.fillOval(0, 0, 5, 5);
        gfx.setPaint(Colors.WHITE);
        gfx.fillRect(1, 2, 3, 1);
        if (!expanded) {
          gfx.fillRect(2, 1, 1, 3);
        }
      } finally {
        // after all is done, reset the state
        gfx.setTransform(oldTransform);
        gfx.setPaint(oldPaint);
      }
    }
  }

  /**
   * An implementation of {@link com.yworks.yfiles.view.IVisualCreator} that
   * can be set on the {@link CollapsibleNodeStyleDecorator} to return
   * our own implementation for the visual representation of a button.
   */
  private class MyCollapsibleNodeStyleButtonVisualCreator extends CollapsibleNodeStyleButtonVisualCreator {

    public MyCollapsibleNodeStyleButtonVisualCreator() {
      // set the button size to the instance variable
      super(buttonSize, true);
    }

    @Override
    public IVisual createVisual(IRenderContext context) {
      // create our button visual with the initial state that we get from the super class
      ButtonVisual buttonVisual = new ButtonVisual(isExpanded());
      // set the appropriate size on the visual
      buttonVisual.setButtonSize(buttonSize);
      return buttonVisual;
    }

    @Override
    public IVisual updateVisual(IRenderContext context, IVisual group) {
      if (group instanceof ButtonVisual) {
        // cast to our visual and update the properties
        ButtonVisual buttonVisual = (ButtonVisual) group;
        buttonVisual.setExpanded(isExpanded());
        buttonVisual.setButtonSize(buttonSize);
      }
      return super.updateVisual(context, group);
    }
  }
}
