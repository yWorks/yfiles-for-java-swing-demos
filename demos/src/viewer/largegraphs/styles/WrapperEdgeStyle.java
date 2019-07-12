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
package viewer.largegraphs.styles;

import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.IEdgeStyleRenderer;
import com.yworks.yfiles.graph.styles.IPathGeometry;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IMarqueeTestable;


/**
 * Simple {@link IEdgeStyle} wrapper to simplify changing every edge's style.
 * <p>
 *   This class also implements {@link IEdgeStyleRenderer} because renderer instances are tightly integrated with
 *   their styles and we cannot simply return another style's renderer from here.
 * </p>
 */
public class WrapperEdgeStyle implements IEdgeStyle, IEdgeStyleRenderer {

  private IEdgeStyle style;

  /**
   * Gets the wrapped style.
   * @return
   */
  public IEdgeStyle getStyle() {
    return style;
  }

  /**
   * Sets the wrapped style.
   * @param style
   */
  public void setStyle(IEdgeStyle style) {
    this.style = style;
  }

  /**
   * Initializes a new instance of the {@link WrapperEdgeStyle} class, wrapping the given {@link IEdgeStyle}.
   * @param style The style to wrap.
   */
  public WrapperEdgeStyle(IEdgeStyle style) {
    this.style = style;
  }

  @Override
  public IEdgeStyleRenderer getRenderer() {
    return this;
  }

  @Override
  public Object clone() {
    return new WrapperEdgeStyle(getStyle());
  }

  // region IEdgeStyleRenderer

  @Override
  public IPathGeometry getPathGeometry(IEdge edge, IEdgeStyle style) {
    return getStyle().getRenderer().getPathGeometry(edge, getStyle());
  }

  @Override
  public IVisualCreator getVisualCreator(IEdge edge, IEdgeStyle style) {
    return getStyle().getRenderer().getVisualCreator(edge, getStyle());
  }

  @Override
  public IBoundsProvider getBoundsProvider(IEdge edge, IEdgeStyle style) {
    return getStyle().getRenderer().getBoundsProvider(edge, getStyle());
  }

  @Override
  public IVisibilityTestable getVisibilityTestable(IEdge edge, IEdgeStyle style) {
    return getStyle().getRenderer().getVisibilityTestable(edge, getStyle());
  }

  @Override
  public IHitTestable getHitTestable(IEdge edge, IEdgeStyle style) {
    return getStyle().getRenderer().getHitTestable(edge, getStyle());
  }

  @Override
  public IMarqueeTestable getMarqueeTestable(IEdge edge, IEdgeStyle style) {
    return getStyle().getRenderer().getMarqueeTestable(edge, getStyle());
  }

  @Override
  public ILookup getContext(IEdge edge, IEdgeStyle style) {
    return getStyle().getRenderer().getContext(edge, getStyle());
  }

  // endregion
}
