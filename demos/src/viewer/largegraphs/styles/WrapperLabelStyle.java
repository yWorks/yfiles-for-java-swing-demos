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
package viewer.largegraphs.styles;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyleRenderer;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IMarqueeTestable;


/**
 * Simple {@link ILabelStyle} wrapper to simplify changing every label's style.
 * <p>
 *   This class also implements {@link ILabelStyleRenderer} because renderer instances are tightly integrated with
 *   their styles and we cannot simply return another style's renderer from here.
 * </p>
 */
public class WrapperLabelStyle implements ILabelStyle, ILabelStyleRenderer {

  private ILabelStyle style;

  /**
   * Gets the wrapped style.
   * @return
   */
  public ILabelStyle getStyle() {
    return style;
  }

  /**
   * Sets the wrapped style.
   * @param style
   */
  public void setStyle(ILabelStyle style) {
    this.style = style;
  }

  /**
   * Initializes a new instance of the {@link WrapperLabelStyle} class, wrapping the given {@link ILabelStyle}.
   * @param style The style to wrap.
   */
  public WrapperLabelStyle(ILabelStyle style) {
    this.style = style;
  }

  @Override
  public ILabelStyleRenderer getRenderer() {
    return this;
  }

  @Override
  public Object clone() {
    return new WrapperLabelStyle(getStyle());
  }

  // region ILabelStyleRenderer

  @Override
  public SizeD getPreferredSize(ILabel label, ILabelStyle style) {
    return getStyle().getRenderer().getPreferredSize(label, getStyle());
  }

  @Override
  public IVisualCreator getVisualCreator(ILabel label, ILabelStyle style) {
    return getStyle().getRenderer().getVisualCreator(label, getStyle());
  }

  @Override
  public IBoundsProvider getBoundsProvider(ILabel label, ILabelStyle style) {
    return getStyle().getRenderer().getBoundsProvider(label, getStyle());
  }

  @Override
  public IVisibilityTestable getVisibilityTestable(ILabel label, ILabelStyle style) {
    return getStyle().getRenderer().getVisibilityTestable(label, getStyle());
  }

  @Override
  public IHitTestable getHitTestable(ILabel label, ILabelStyle style) {
    return getStyle().getRenderer().getHitTestable(label, getStyle());
  }

  @Override
  public IMarqueeTestable getMarqueeTestable(ILabel label, ILabelStyle style) {
    return getStyle().getRenderer().getMarqueeTestable(label, getStyle());
  }

  @Override
  public ILookup getContext(ILabel label, ILabelStyle style) {
    return getStyle().getRenderer().getContext(label, getStyle());
  }

  // endregion
}
