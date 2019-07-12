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
package complete.orgchart;

import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.IRenderContext;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Ellipse2D;

/**
 * A visual that draws circles that represent the current availability status of the employee.
 */
class StatusVisual implements IVisual {

  private String status;

  private Ellipse2D inner;
  private Ellipse2D middle;
  private Ellipse2D outer;

  StatusVisual(final String status) {
    this.inner = new Ellipse2D.Double(-6, -6, 12, 12);
    this.middle = new Ellipse2D.Double(-9, -9, 18, 18);
    this.outer = new Ellipse2D.Double(-12, -12, 24, 24);
    setStatus(status);
  }

  /**
   * Sets the status that is used to determine the color to draw the circles in.
   */
  public void setStatus(String status){
    this.status = status;
  }

  @Override
  public void paint( IRenderContext context, Graphics2D g) {
    final Paint oldPaint = g.getPaint();

    try {

      // find out which color to use for the inner and outer circles dependent on the status
      Paint statusColor;

      switch (status) {
        case "Present":
          statusColor = Colors.FOREST_GREEN;
          break;
        case "Travel":
          statusColor = Colors.PURPLE;
          break;
        case "Unavailable":
        default:
          statusColor = Color.RED;
          break;
      }

      g.setPaint(statusColor);
      g.fill(outer);

      g.setPaint(Color.WHITE);
      g.fill(middle);

      g.setPaint(statusColor);
      g.fill(inner);

    } finally {
      g.setPaint(oldPaint);
    }
  }
}
