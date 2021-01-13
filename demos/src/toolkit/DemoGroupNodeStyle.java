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
package toolkit;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.INodeInsetsProvider;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * A simple node style for group nodes used by some of the demos.
 */
public class DemoGroupNodeStyle extends AbstractNodeStyle {

  private static final int BORDER_THICKNESS = 4;
  private static final int HEADER_THICKNESS = 22;
  private static final int INSET = 4;

  private static final Color BORDER_COLOR = Color.decode("#68B0E3");
  private static final Color FOLDER_FRONT_COLOR = Color.decode("#68B0E3");
  private static final Color FOLDER_BACK_COLOR = Color.decode("#3C679B");
  private static final Pen OUTER_BORDER_PEN = new Pen(Colors.WHITE, 1);

  private static final InsetsD INSETS = new InsetsD(
      HEADER_THICKNESS + INSET,
      BORDER_THICKNESS + INSET,
      BORDER_THICKNESS + INSET,
      BORDER_THICKNESS + INSET);

  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    IRectangle layout = node.getLayout();
    DemoGroupNodeStyleVisual group = new DemoGroupNodeStyleVisual(layout.toSizeD());
    group.setTransform(AffineTransform.getTranslateInstance(layout.getX(), layout.getY()));

    Rectangle2D outerRect = new Rectangle2D.Double(0, 0, layout.getWidth(), layout.getHeight());
    ShapeVisual backgroundRectVisual = new ShapeVisual(outerRect);
    backgroundRectVisual.setPen(OUTER_BORDER_PEN);
    backgroundRectVisual.setFill(BORDER_COLOR);
    group.add(backgroundRectVisual);

    double innerWidth = layout.getWidth() - 2 * BORDER_THICKNESS;
    double innerHeight = layout.getHeight() -  HEADER_THICKNESS - BORDER_THICKNESS;
    Rectangle2D innerRect = new Rectangle2D.Double(BORDER_THICKNESS, HEADER_THICKNESS, innerWidth, innerHeight);
    ShapeVisual rectVisual = new ShapeVisual(innerRect);
    rectVisual.setFill(Colors.WHITE);
    group.add(rectVisual);

    return group;
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (!(oldVisual instanceof DemoGroupNodeStyleVisual)) {
      return createVisual(context, node);
    }

    DemoGroupNodeStyleVisual group = (DemoGroupNodeStyleVisual) oldVisual;
    IRectangle layout = node.getLayout();
    if (!SizeD.equals(layout.toSizeD(), group.getSize())) {
      return createVisual(context, node);
    }

    group.setTransform(AffineTransform.getTranslateInstance(layout.getX(), layout.getY()));
    return group;
  }

  @Override
  protected Object lookup(INode node, Class type) {
    if (type == INodeInsetsProvider.class) {
      return (INodeInsetsProvider) (node2) -> INSETS;
    }
    return super.lookup(node, type);
  }

  private static class DemoGroupNodeStyleVisual extends VisualGroup {
    SizeD size;

    DemoGroupNodeStyleVisual(SizeD size) {
      this.size = size;
    }

    public SizeD getSize() {
      return size;
    }

    public void setSize(SizeD size) {
      this.size = size;
    }
  }
}
