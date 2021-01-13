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

class CombinedIcon extends AbstractIcon {
  private final List<IIcon> icons;

  public CombinedIcon( List<IIcon> icons ) {
    this.icons = icons;
  }

  @Override
  public IVisual createVisual( IRenderContext context ) {
    RectD bounds = getBounds().toRectD();
    if (bounds == null) {
      return null;
    }
    MyVisual container = new MyVisual(bounds);

    RectD iconBounds = new RectD(PointD.ORIGIN, bounds.toSizeD());
    for (IIcon icon : icons) {
      icon.setBounds(iconBounds);
      container.add(icon.createVisual(context));
    }

    container.setTransform(AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY()));
    return container;
  }

  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    MyVisual container = (oldVisual instanceof MyVisual) ? (MyVisual)oldVisual : null;
    if (container == null || container.getChildren().size() != icons.size()) {
      return createVisual(context);
    }
    RectD bounds = getBounds().toRectD();

    if (!SizeD.equals(container.getBounds().getSize(), bounds.toSizeD())) {
      // size changed -> we have to update the icons
      RectD iconBounds = new RectD(PointD.ORIGIN, bounds.toSizeD());
      int index = 0;
      for (IIcon pathIcon : icons) {
        pathIcon.setBounds(iconBounds);
        IVisual oldPathVisual = container.getChildren().get(index);
        IVisual newPathVisual = pathIcon.updateVisual(context, oldPathVisual);
        if (!oldPathVisual.equals(newPathVisual)) {
          newPathVisual = newPathVisual != null ? newPathVisual : new VisualGroup();
          container.getChildren().remove(oldPathVisual);
          container.getChildren().add(index, newPathVisual);
        }
        index++;
      }
    } else if (PointD.equals(container.getBounds().getTopLeft(), bounds.getTopLeft())) {
      // bounds didn't change at all
      return container;
    }
    container.getTransform().setToTranslation(bounds.getX(), bounds.getY());
    container.setBounds(bounds);
    return container;
  }

  @Override
  public <TLookup> TLookup lookup( Class<TLookup> type ) {
    if (type == IClickListener.class) {
      return (TLookup) new MyActionButtonProvider();
    }
    return super.lookup(type);
  }

  private class MyActionButtonProvider implements IClickListener, IHitTestable {
    public IHitTestable getHitTestable() {
      return this;
    }

    public final void onClicked( IInputModeContext context, PointD p ) {
      RectD bounds = getBounds().toRectD();
      PointD topLeft = bounds.getTopLeft();
      for (IIcon icon : icons) {
        IClickListener abp = icon.lookup(IClickListener.class);
        if (abp != null) {
          RectD iconBounds = new RectD(PointD.ORIGIN, bounds.toSizeD());
          icon.setBounds(iconBounds);
          PointD d = PointD.subtract(p, topLeft);
          if (abp.getHitTestable().isHit(context, d)) {
            abp.onClicked(context, d);
            return;
          }
        }
      }
    }

    public final boolean isHit( IInputModeContext context, PointD p ) {
      RectD bounds = getBounds().toRectD();
      PointD topLeft = bounds.getTopLeft();
      for (IIcon icon : icons) {
        IClickListener abp = icon.lookup(IClickListener.class);
        if (abp != null) {
          RectD iconBounds = new RectD(PointD.ORIGIN, bounds.toSizeD());
          icon.setBounds(iconBounds);
          PointD d = PointD.subtract(p, topLeft);
          if (abp.getHitTestable().isHit(context, d)) {
            return true;
          }
        }
      }
      return false;
    }
  }

  private static class MyVisual extends VisualGroup {
    private RectD bounds;

    public MyVisual(RectD bounds) {
      this.bounds = bounds;
    }

    public RectD getBounds() {
      return bounds;
    }

    public void setBounds(RectD bounds) {
      this.bounds = bounds;
    }
  }
}
