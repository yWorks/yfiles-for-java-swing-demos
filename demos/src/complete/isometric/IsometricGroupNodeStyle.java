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
package complete.isometric;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeInsetsProvider;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class IsometricGroupNodeStyle extends AbstractNodeStyle {

  private static final Color HEADER_COLOR = new Color(153, 204, 255, 255);

  public static final Pen BORDER_PEN = new Pen(new Color(153, 204, 255, 255), 1);

  // The insets between the group node bounds and its children.
  private static final double INSETS = 20;

  // The height of a group node header.
  private static final double HEADER_HEIGHT = 18;

  private final IsometricNodeStyle wrapped = new IsometricNodeStyle();


  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    IsometricGroupNodeVisual isometricGroupNodeVisual = new IsometricGroupNodeVisual(wrapped);
    return isometricGroupNodeVisual.update(context, node);
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    IsometricGroupNodeVisual visual = (oldVisual instanceof IsometricGroupNodeVisual) ? (IsometricGroupNodeVisual)oldVisual : null;
    if (visual != null) {
      return visual.update(context, node);
    } else {
      return createVisual(context, node);
    }
  }

  /**
   * The visual that renders the group node as flat shape.
   */
  private static class IsometricGroupNodeVisual extends VisualGroup {
    private final IsometricNodeStyle wrapped;
    private INode node;
    private IRenderContext context;

    private RectD headerLayout;

    public IsometricGroupNodeVisual(IsometricNodeStyle wrapped) {
      this.wrapped = wrapped;
    }

    private IsometricGroupNodeVisual update(IRenderContext context, INode node) {
      this.node = node;
      this.context = context;

      double x = node.getLayout().getX();
      double width = node.getLayout().getWidth();
      // Calculate the box of the label. It uses the whole width of the node.
      double headerHeight = HEADER_HEIGHT;
      if (node.getLabels().size() > 0) {
        ILabel firstLabel = node.getLabels().first();
        if (firstLabel != null) {
          headerHeight = Math.max(headerHeight, firstLabel.getLayout().getHeight());
        }
      }
      double y = node.getLayout().getMaxY() - headerHeight;
      headerLayout = new RectD(x, y, width, headerHeight);

      this.getChildren().clear();
      configure();

      return this;
    }

    private void configure() {
      add(wrapped.getRenderer().getVisualCreator(node, wrapped).createVisual(context));
      Rectangle2D headerRect = new Rectangle2D.Double(headerLayout.x, headerLayout.y, headerLayout.width, headerLayout.height);
      add(new ShapeVisual(headerRect, BORDER_PEN, HEADER_COLOR));
    }
  }

  @Override
  protected RectD getBounds(ICanvasContext context, INode node) {
    return wrapped.getRenderer().getBoundsProvider(node, wrapped).getBounds(context);
  }

  @Override
  protected boolean isVisible(ICanvasContext context, RectD rectangle, INode node) {
    return wrapped.getRenderer().getVisibilityTestable(node, wrapped).isVisible(context, rectangle);
  }

  @Override
  protected boolean isHit(IInputModeContext context, PointD location, INode node) {
    return wrapped.getRenderer().getHitTestable(node, wrapped).isHit(context, location);
  }

  @Override
  protected PointD getIntersection(INode node, PointD inner, PointD outer) {
    return wrapped.getRenderer().getShapeGeometry(node, wrapped).getIntersection(inner, outer);
  }

  @Override
  protected boolean isInside(INode node, PointD location) {
    return wrapped.getRenderer().getShapeGeometry(node, wrapped).isInside(location);
  }

  @Override
  protected GeneralPath getOutline(INode node) {
    return wrapped.getRenderer().getShapeGeometry(node, wrapped).getOutline();
  }

  @Override
  protected Object lookup(INode node, Class type) {
    if (type == INodeInsetsProvider.class) {
      // use a group node insets provider that considers the header and the insets
      return GroupNodeInsetsProvider.Instance;
    }
    return super.lookup(node, type);
  }

  /**
   * A group node insets provider that considers the header and the insets.
   */
  public static class GroupNodeInsetsProvider implements INodeInsetsProvider {
    private final static GroupNodeInsetsProvider Instance = new GroupNodeInsetsProvider();

    public InsetsD getInsets(INode node) {
      double headerHeight = HEADER_HEIGHT;
      if (node.getLabels().size() > 0) {
        headerHeight = Math.max(headerHeight, node.getLabels().getItem(0).getLayout().getHeight());
      }
      return new InsetsD(INSETS, INSETS, INSETS, INSETS + headerHeight);
    }
  }
}
