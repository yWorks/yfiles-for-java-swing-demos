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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.MatrixOrder;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;
import java.awt.Paint;
import java.awt.geom.AffineTransform;

class PathIcon extends AbstractIcon {
  private Paint paint;

  public final Paint getPaint() {
    return this.paint;
  }

  public final void setPaint( Paint value ) {
    this.paint = value;
  }

  private Pen pen;

  public final Pen getPen() {
    return this.pen;
  }

  public final void setPen( Pen value ) {
    this.pen = value;
  }

  private GeneralPath path;

  final GeneralPath getPath() {
    return this.path;
  }

  final void setPath( GeneralPath value ) {
    this.path = value;
  }

  @Override
  public IVisual createVisual( IRenderContext context ) {
    RectD bounds = getBounds().toRectD();
    MyVisual container = new MyVisual(bounds.getSize());

    Matrix2D matrix2D = new Matrix2D();
    matrix2D.scale(Math.max(0, bounds.getWidth()), Math.max(0, bounds.getHeight()), MatrixOrder.PREPEND);

    container.add(new ShapeVisual(getPath().createPath(matrix2D), getPen(), getPaint()));

    container.setTransform(AffineTransform.getTranslateInstance(bounds.getX(), bounds.getY()));
    return container;
  }

  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    RectD bounds = getBounds().toRectD();
    MyVisual container = (oldVisual instanceof MyVisual) ? (MyVisual)oldVisual : null;
    if (container == null || container.getChildren().size() != 1) {
      return createVisual(context);
    }
    IVisual visual = container.getChildren().get(0);
    ShapeVisual path = (visual instanceof ShapeVisual) ? (ShapeVisual)visual : null;
    if (path == null || !SizeD.equals(container.getSize(), bounds.getSize())) {
      return createVisual(context);
    }

    Pen pen = getPen();
    if (path.getPen() != pen) {
      path.setPen(pen);
    }
    Paint paint = getPaint();
    if (path.getFill() != paint) {
      path.setFill(paint);
    }

    // arrange visual
    container.getTransform().setToTranslation(bounds.getX(), bounds.getY());
    container.setSize(bounds.getSize());
    return container;
  }

  private static class MyVisual extends VisualGroup {
    private SizeD size;

    public MyVisual(SizeD size) {
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
