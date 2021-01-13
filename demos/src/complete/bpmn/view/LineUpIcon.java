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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.view.input.IClickListener;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VisualGroup;
import java.awt.geom.AffineTransform;
import java.util.List;

class LineUpIcon extends AbstractIcon {
  private final List<IIcon> icons;

  private final SizeD innerIconSize;

  private final double gap;

  private final SizeD combinedSize;

  public LineUpIcon( List<IIcon> icons, SizeD innerIconSize, double gap ) {
    this.icons = icons;
    this.innerIconSize = innerIconSize;
    this.gap = gap;

    double combinedWidth = icons.size() * innerIconSize.width + (icons.size() - 1) * gap;
    combinedSize = new SizeD(combinedWidth, innerIconSize.height);
  }

  @Override
  public IVisual createVisual( IRenderContext context ) {
    RectD bounds = getBounds().toRectD();
    if (bounds == null) {
      return null;
    }

    VisualGroup container = new VisualGroup();

    double offset = 0;
    for (IIcon pathIcon : icons) {
      pathIcon.setBounds(new RectD(offset, 0, innerIconSize.width, innerIconSize.height));
      container.add(pathIcon.createVisual(context));
      offset += innerIconSize.width + gap;
    }
    container.setTransform(AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY()));

    return container;
  }

  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    VisualGroup container = (oldVisual instanceof VisualGroup) ? (VisualGroup)oldVisual : null;
    if (container == null || container.getChildren().size() != icons.size()) {
      return createVisual(context);
    }
    RectD bounds = getBounds().toRectD();
    container.getTransform().setToTranslation(bounds.getX(), bounds.getY());
    return container;
  }

  @Override
  public void setBounds( IRectangle bounds ) {
    super.setBounds(RectD.fromCenter(bounds.getCenter(), combinedSize));
  }

  @Override
  public <TLookup> TLookup lookup( Class<TLookup> type ) {
    if (type == IClickListener.class) {
      return (TLookup)new MyActionButtonProvider();
    }
    return super.lookup(type);
  }

  private class MyActionButtonProvider implements IClickListener, IHitTestable {
    public IHitTestable getHitTestable() {
      return this;
    }

    public final void onClicked( IInputModeContext context, PointD p ) {
      PointD topLeft = getBounds().getTopLeft();
      double offset = 0;
      for (IIcon icon : icons) {
        IClickListener abp = icon.lookup(IClickListener.class);
        if (abp != null) {
          icon.setBounds(new RectD(offset, 0, innerIconSize.getWidth(), innerIconSize.getHeight()));
          PointD d = PointD.subtract(p, topLeft);
          if (abp.getHitTestable().isHit(context, d)) {
            abp.onClicked(context, d);
            return;
          }
        }
        offset += innerIconSize.getWidth() + gap;
      }
    }

    public final boolean isHit( IInputModeContext context, PointD p ) {
      PointD topLeft = getBounds().getTopLeft();
      double offset = 0;
      for (IIcon icon : icons) {
        IClickListener abp = icon.lookup(IClickListener.class);
        icon.setBounds(new RectD(offset, 0, innerIconSize.getWidth(), innerIconSize.getHeight()));
        if (abp != null) {
          PointD d = PointD.subtract(p, topLeft);
          if (abp.getHitTestable().isHit(context, d)) {
            return true;
          }
        }
        offset += innerIconSize.getWidth() + gap;
      }
      return false;
    }

  }

}
