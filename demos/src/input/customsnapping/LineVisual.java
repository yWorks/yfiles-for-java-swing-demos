/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package input.customsnapping;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;

import java.awt.geom.Line2D;

/**
 * A simple visual used to add additional visible and mutable snap lines to a {@link
 * com.yworks.yfiles.view.input.GraphSnapContext}.
 */
class LineVisual extends ShapeVisual {

  /**
   * Creates a new instance with the given start and end point of the snap line.
   *
   * @param from the location to start the additional snap line
   * @param to   the location to end the additional snap line
   */
  public LineVisual(PointD from, PointD to) {
    super(new Line2D.Double(from.getX(), from.getY(), to.getX(), to.getY()), Pen.getRed(), null);
  }

  /**
   * Returns the start point of the snap line.
   */
  public PointD getFrom() {
    Line2D line = (Line2D) getShape();
    return new PointD(line.getX1(), line.getY1());
  }

  /**
   * Sets the start point of the snap line.
   */
  public void setFrom(PointD from) {
    Line2D line = (Line2D) getShape();
    line.setLine(from.getX(), from.getY(), line.getX2(), line.getY2());
  }

  /**
   * Returns the end point of the snap line.
   */
  public PointD getTo() {
    Line2D line = (Line2D) getShape();
    return new PointD(line.getX2(), line.getY2());
  }

  /**
   * Sets the end point of the snap line.
   */
  public void setTo(PointD to) {
    Line2D line = (Line2D) getShape();
    line.setLine(line.getX1(), line.getY1(), to.getX(), to.getY());
  }
}
