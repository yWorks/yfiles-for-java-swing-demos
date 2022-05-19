/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

class RectIcon extends AbstractIcon {
  private double cornerRadius;

  final double getCornerRadius() {
    return this.cornerRadius;
  }

  final void setCornerRadius( double value ) {
    this.cornerRadius = value;
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
    RectD layout = getBounds().toRectD();

    final RoundRectangle2D.Double roundRect = new RoundRectangle2D.Double(
        layout.getX(),
        layout.getY(),
        layout.getWidth(),
        layout.getHeight(),
        getCornerRadius() * 2,
        getCornerRadius() * 2);

    return new ShapeVisual(roundRect, getPen(), getPaint());
  }

  @Override
  public IVisual updateVisual( IRenderContext context, IVisual oldVisual ) {
    if(!(oldVisual instanceof ShapeVisual)) {
      return createVisual(context);
    }
    ShapeVisual rectangle = (ShapeVisual) oldVisual;
    Shape shape = rectangle.getShape();
    if(!(shape instanceof RoundRectangle2D.Double) || rectangle.getPen() != getPen() || rectangle.getFill() != getPaint()) {
      return createVisual(context);
    }
    RoundRectangle2D rr = (RoundRectangle2D) shape;
    if(rr.getArcHeight() != getCornerRadius() * 2 || rr.getArcWidth() != getCornerRadius() * 2 ) {
      return createVisual(context);
    }
    updateRectangle(rr);
    return oldVisual;
  }

  private void updateRectangle(RoundRectangle2D rectangle) {
    RectD bounds = getBounds().toRectD();
    rectangle.setFrame(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
  }
}
