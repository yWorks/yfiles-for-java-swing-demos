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
package style.zoominvariantlabelstyle;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.IRenderContext;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * A render context that simulates the given zoom factor for rendering.
 */
class DummyContext implements IRenderContext {
  private final IRenderContext innerContext;
  private final double zoom;
  private final AffineTransform transform;
  private final AffineTransform viewTransform;
  private final AffineTransform intermediateTransform;
  private final AffineTransform projection;

  /**
   * Initializes a new {@code DummyContext} instance for the given zoom factor.
   */
  DummyContext(IRenderContext innerContext, double zoom, AffineTransform inverseTransform) {
    this.innerContext = innerContext;
    this.zoom = zoom;

    this.transform = inverseTransform;

    // multiply all necessary transforms with the given inverse transform to nullify the outer transform
    this.viewTransform = this.transformMatrix(this.innerContext.getToViewTransform());
    this.intermediateTransform = this.transformMatrix(this.innerContext.getToIntermediateTransform());
    this.projection = this.transformMatrix(this.innerContext.getProjection());
  }

  @Override
  public CanvasComponent getCanvasComponent() {
    return innerContext.getCanvasComponent();
  }

  @Override
  public RectD getClip() {
    return this.innerContext.getClip();
  }

  @Override
  public AffineTransform getProjection() {
    return this.projection;
  }

  @Override
  public AffineTransform getToIntermediateTransform() {
    return this.intermediateTransform;
  }

  @Override
  public AffineTransform getToViewTransform() {
    return viewTransform;
  }

  @Override
  public AffineTransform getToWorldTransform() {
    return transform;
  }

  @Override
  public PointD toViewCoordinates(PointD worldPoint) {
    Point2D src = worldPoint.toPoint2D();
    Point2D tgt = transform.transform(src, null);
    return PointD.fromPoint2D(tgt);
  }

  @Override
  public PointD worldToIntermediateCoordinates(PointD worldPoint) {
    Point2D src = worldPoint.toPoint2D();
    Point2D tgt = intermediateTransform.transform(src, null);
    return PointD.fromPoint2D(tgt);
  }

  @Override
  public PointD intermediateToViewCoordinates(PointD intermediatePoint) {
    Point2D src = intermediatePoint.toPoint2D();
    Point2D tgt = projection.transform(src, null);
    return PointD.fromPoint2D(tgt);
  }

  @Override
  public double getZoom() {
    return zoom;
  }

  @Override
  public double getHitTestRadius() {
    return innerContext.getHitTestRadius();
  }

  @Override
  public <TLookup> TLookup lookup(Class<TLookup> type) {
    return innerContext.lookup(type);
  }

  /**
   * Multiplies the given transform with the inverse transform of the invariant label style.
   *
   * @param baseTransform The transform to concatenate with this.transform
   */
  private AffineTransform transformMatrix(AffineTransform baseTransform) {
    AffineTransform transformed = (AffineTransform) baseTransform.clone();
    transformed.concatenate(this.transform);
    return transformed;
  }
}
