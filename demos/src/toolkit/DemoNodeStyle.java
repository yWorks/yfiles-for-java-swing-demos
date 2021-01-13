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

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

/**
 * A simple node style for non-group nodes used by some of the demos.
 */
public class DemoNodeStyle extends AbstractNodeStyle {

  /**
   * The radius of the round corners in world coordinates.
   */
  public static final double CORNER_RADIUS = 2;

  /**
   * The background color of the node.
   */
  public static final Color BACKGROUND = Colors.DARK_ORANGE;

  /**
   * The border pen of the node.
   */
  public static final Pen PEN = new Pen(Colors.WHITE, 1);


  @Override
  protected IVisual createVisual(IRenderContext context, INode node) {
    return new DemoVisualGroup(node.getLayout().toRectD(), PEN, BACKGROUND);
  }

  @Override
  protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, INode node) {
    if (oldVisual instanceof DemoVisualGroup) {
      // reuse and update old visual
      DemoVisualGroup demoVisualGroup = (DemoVisualGroup) oldVisual;
      demoVisualGroup.update(node.getLayout().toRectD());
      return oldVisual;
    } else {
      // create a new visual
      return createVisual(context, node);
    }
  }

  /**
   * Creates a round rect path with the specified width and height and a fix {@link #CORNER_RADIUS}.
   */
  private static GeneralPath createPath(double width, double height) {
    GeneralPath shape = new GeneralPath();
    double x1 = width > CORNER_RADIUS * 2 ? CORNER_RADIUS : width /2;
    double x2 = width - x1;

    double y1 = height > CORNER_RADIUS * 2 ? CORNER_RADIUS : height /2;
    double y2 = height - y1;

    shape.moveTo(x1, 0);
    shape.lineTo(x2, 0);
    shape.quadTo(width, 0, width, y1);
    shape.lineTo(width, y2);
    shape.quadTo(width, height, x2, height);
    shape.lineTo(x1, height);
    shape.quadTo(0, height, 0, y2);
    shape.lineTo(0, y1);
    shape.quadTo(0, 0, x1, 0);
    return shape;
  }

  /**
   * This VisualGroup caches the node layout to speed-up updates of the position and/or size of the node hasn't changed.
   */
  private static class DemoVisualGroup extends VisualGroup {

    private static final double EPS = 0.001;

    private RectD layout;

    DemoVisualGroup(RectD layout, Pen pen, Paint fill) {
      GeneralPath path = createPath(layout.getWidth(), layout.getHeight());
      this.add(new ShapeVisual(path, pen, fill));
      this.setTransform(AffineTransform.getTranslateInstance(layout.getX(), layout.getY()));
      this.layout = layout;
    }

    public void update(RectD newLayout) {
      if (!RectD.equals(layout, newLayout)) {
        if (layout.getWidth() != newLayout.getWidth() || layout.getHeight() != newLayout.getHeight()) {
          // recreate path
          ShapeVisual shapeVisual = (ShapeVisual) getChildren().get(0);
          shapeVisual.setShape(createPath(newLayout.getWidth(), newLayout.getHeight()));
        }
        if (layout.getX() != newLayout.getX() || layout.getY() != newLayout.getY()) {
          // update transform
          this.setTransform(AffineTransform.getTranslateInstance(newLayout.getX(), newLayout.getY()));
        }
        this.layout = newLayout;
      }
    }
  }
}
