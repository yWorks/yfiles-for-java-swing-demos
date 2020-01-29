/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.input.IClickListener;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;

class CollapseButtonIcon extends AbstractIcon implements IClickListener {
  private static final IIcon COLLAPSED_ICON;

  private static final IIcon EXPANDED_ICON;

  private INode node;

  public CollapseButtonIcon( INode node ) {
    this.node = node;
  }

  @Override
  public IVisual createVisual( IRenderContext context ) {
    COLLAPSED_ICON.setBounds(getBounds().toRectD());
    EXPANDED_ICON.setBounds(getBounds().toRectD());
    boolean expanded = true;
    CanvasComponent canvas = context != null ? context.getCanvasComponent() : null;

    if (canvas != null) {
      IGraph graph = canvas.lookup(IGraph.class);
      if (graph != null) {
        IFoldingView foldingView = graph.lookup(IFoldingView.class);
        if (foldingView != null && foldingView.getGraph().contains(node)) {
          expanded = foldingView.isExpanded(node);
        }
      }
    }
    if(expanded) {
      return EXPANDED_ICON.createVisual(context);
    }
    else {
      return COLLAPSED_ICON.createVisual(context);
    }
  }

  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    COLLAPSED_ICON.setBounds(getBounds().toRectD());
    EXPANDED_ICON.setBounds(getBounds().toRectD());
    boolean expanded = true;
    CanvasComponent canvas = context != null ? context.getCanvasComponent() : null;

    if (canvas != null) {
      IGraph graph = canvas.lookup(IGraph.class);
      if (graph != null) {
        IFoldingView foldingView = graph.lookup(IFoldingView.class);
        if (foldingView != null && foldingView.getGraph().contains(node)) {
          expanded = foldingView.isExpanded(node);
        }
      }
    }
    if(expanded) {
      return EXPANDED_ICON.updateVisual(context, oldVisual);
    }
    else {
      return COLLAPSED_ICON.updateVisual(context, oldVisual);
    }
  }

  public final IHitTestable getHitTestable() {
    return new DefaultButtonHitTestable(getBounds());
  }

  public final void onClicked( IInputModeContext context, PointD location ) {
    if (ICommand.TOGGLE_EXPANSION_STATE.canExecute(node, context.getCanvasComponent())) {
      ICommand.TOGGLE_EXPANSION_STATE.execute(node, context.getCanvasComponent());
    }
  }

  private static class DefaultButtonHitTestable implements IHitTestable {
    private final IRectangle rect;

    public DefaultButtonHitTestable( IRectangle rect ) {
      this.rect = rect;
    }

    public final boolean isHit( IInputModeContext context, PointD location ) {
      return rect.toRectD().contains(location, context.getHitTestRadius());
    }

  }

  static {
    COLLAPSED_ICON = IconFactory.createStaticSubState(SubState.COLLAPSED);
    EXPANDED_ICON = IconFactory.createStaticSubState(SubState.EXPANDED);
  }

  @Override
  public <T> T lookup(Class<T> type) {
    if(type == IClickListener.class) {
      return (T)this;
    }
    return super.lookup(type);
  }

}
