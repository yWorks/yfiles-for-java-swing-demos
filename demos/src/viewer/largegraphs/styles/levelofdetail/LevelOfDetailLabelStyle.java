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
package viewer.largegraphs.styles.levelofdetail;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Graphics2D;

/**
 * Level-of-detail style for labels that delegates to different {@link ILabelStyle}s depending on the zoom level.
 */
public class LevelOfDetailLabelStyle extends AbstractLabelStyle {

  // the style container
  private LevelOfDetailStyleContainer<ILabelStyle> styles = new LevelOfDetailStyleContainer<>();

  /**
   * Gets the style container.
   * <p>
   *  Styles have to be added in ascending order of zoom level.
   * </p>
   */
  public LevelOfDetailStyleContainer<ILabelStyle> getStyles() {
    return styles;
  }

  @Override
  protected IVisual createVisual(IRenderContext context, ILabel label) {
    ILabelStyle style = styles.getStyle(context.getZoom());
    IVisual visual = style.getRenderer().getVisualCreator(label, style).createVisual(context);
    LabelVisual labelVisual = new LabelVisual(visual, context.getZoom());
    context.setDisposeCallback(labelVisual, (context1, removedVisual, dispose) -> dispose ? null : removedVisual);
    return labelVisual;
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, ILabel label) {
    LabelVisual labelVisual = oldVisual instanceof LabelVisual ? (LabelVisual) oldVisual : null;
    if (labelVisual == null) {
      return createVisual(context, label);
    }

    IVisual oldDelegate = labelVisual.delegate;
    double oldZoom = labelVisual.zoom;
    if (styles.hasSameStyle(context.getZoom(), oldZoom)) {
      ILabelStyle style = styles.getStyle(oldZoom);
      IVisual v = style.getRenderer().getVisualCreator(label, style).updateVisual(context, oldDelegate);
      if (oldDelegate != null && !oldDelegate.equals(v)) {
        context.childVisualRemoved(oldDelegate);
      }
      labelVisual.delegate = v;
    } else {
      if (oldDelegate != null) {
        context.childVisualRemoved(oldDelegate);
      }
      ILabelStyle style = styles.getStyle(context.getZoom());
      labelVisual.delegate = style.getRenderer().getVisualCreator(label, style).createVisual(context);
    }
    labelVisual.zoom = context.getZoom();
    return labelVisual;
  }

  // region Delegate methods

  @Override
  protected RectD getBounds(ICanvasContext context, ILabel label) {
    ILabelStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getBoundsProvider(label, style).getBounds(context);
  }

  @Override
  protected boolean isVisible(ICanvasContext context, RectD rectangle, ILabel label) {
    ILabelStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getVisibilityTestable(label, style).isVisible(context, rectangle);
  }

  @Override
  protected boolean isHit(IInputModeContext context, PointD location, ILabel label) {
    ILabelStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getHitTestable(label, style).isHit(context, location);
  }

  @Override
  protected boolean isInBox(IInputModeContext context, RectD rectangle, ILabel label) {
    ILabelStyle style = styles.getStyle(context.getZoom());
    return style.getRenderer().getMarqueeTestable(label, style).isInBox(context, rectangle);
  }

  /**
   * Returns the preferred {@link SizeD size} of the label.
   * <p>
   * This level-of-detail style assumes that the most-detailed style has the most correct preferred size. In most
   * cases that would simply be a {@link DefaultLabelStyle} instance.
   * </p>
   * @param label The label to which this style instance is assigned.
   * @return The preferred size.
   */
  @Override
  protected SizeD getPreferredSize(ILabel label) {
    ILabelStyle mostDetailedStyle = styles.getStyle(Double.POSITIVE_INFINITY);
    return mostDetailedStyle.getRenderer().getPreferredSize(label, mostDetailedStyle);
  }

  // endregion

  private static class LabelVisual implements IVisual {
    IVisual delegate;
    double zoom;

    public LabelVisual(IVisual delegate, double zoom) {
      this.delegate = delegate;
      this.zoom = zoom;
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      if (delegate != null) {
        delegate.paint(context, g);
      }
    }
  }

}
