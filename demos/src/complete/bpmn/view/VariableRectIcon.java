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

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;
import java.awt.Paint;
import java.awt.geom.AffineTransform;

class VariableRectIcon extends AbstractIcon {
  private double topLeftRadius;

  final double getTopLeftRadius() {
    return this.topLeftRadius;
  }

  final void setTopLeftRadius( double value ) {
    this.topLeftRadius = value;
  }

  private double topRightRadius;

  final double getTopRightRadius() {
    return this.topRightRadius;
  }

  final void setTopRightRadius( double value ) {
    this.topRightRadius = value;
  }

  private double bottomLeftRadius;

  final double getBottomLeftRadius() {
    return this.bottomLeftRadius;
  }

  final void setBottomLeftRadius( double value ) {
    this.bottomLeftRadius = value;
  }

  private double bottomRightRadius;

  final double getBottomRightRadius() {
    return this.bottomRightRadius;
  }

  final void setBottomRightRadius( double value ) {
    this.bottomRightRadius = value;
  }

  private Paint paint;

  final Paint getPaint() {
    return this.paint;
  }

  final void setPaint( Paint value ) {
    this.paint = value;
  }

  private Pen pen;

  final Pen getPen() {
    return this.pen;
  }

  final void setPen( Pen value ) {
    this.pen = value;
  }

  @Override
  public IVisual createVisual( IRenderContext context ) {
    IRectangle bounds = getBounds();
    double width = bounds.getWidth();
    double height = bounds.getHeight();
    MyVisual container = new MyVisual(bounds.toSizeD());

    GeneralPath path = new GeneralPath(16);
    path.moveTo(0, getTopLeftRadius());
    path.quadTo(0, 0, getTopLeftRadius(), 0);
    path.lineTo(width - getTopRightRadius(), 0);
    path.quadTo(width, 0, width, getTopRightRadius());
    path.lineTo(width, height - getBottomRightRadius());
    path.quadTo(width, height, width - getBottomRightRadius(), height);
    path.lineTo(getBottomLeftRadius(), height);
    path.quadTo(0, height, 0, height - getBottomRightRadius());
    path.close();

    container.add(new ShapeVisual(path.createPath(new Matrix2D()),getPen(), getPaint()));

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
    if (path == null || SizeD.notEquals(container.getSize(), bounds.getSize())) {
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
