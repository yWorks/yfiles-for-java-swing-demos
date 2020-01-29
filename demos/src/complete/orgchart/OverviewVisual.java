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
package complete.orgchart;

import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.geometry.IRectangle;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * A visual that is meant to display the employee at far-away zoom levels.
 * It simply paints the abbreviated name inside the rectangle that is already drawn by the superclass.
*/
class OverviewVisual extends OrgChartVisual{

  private static final Font font = new Font("Dialog", Font.BOLD, 26);

  private String cachedName;
  private Rectangle2D cachedLineBounds;

  public OverviewVisual(Employee employee, IRectangle layout, boolean isFocused, boolean isHovered) {
    super(employee, layout, isFocused, isHovered);
  }

  @Override
  public void paintContent(final IRenderContext context, final Graphics2D g) {
    final AffineTransform oldTransform = g.getTransform();
    final Paint oldPaint = g.getPaint();
    final Font oldFont = g.getFont();
    try {
      // draw the abbreviated first name and the last name in the center.
      String displayName = getEmployee().getFirstName().substring(0, 1) + ". " + getEmployee().getName();
      if (displayName != cachedName) {
        cachedName = displayName;
        cachedLineBounds = getLineBounds(displayName, font);
      }
      Rectangle2D bounds = cachedLineBounds;
      double textWidth = bounds.getWidth();
      double textHeight = bounds.getHeight();
      g.translate(125 - textWidth/2, 50 + textHeight/4); // no fancy calculations here, this a fitting enough approximation to center the drawn text.
      g.setPaint(Color.BLACK);
      g.setFont(font);
      g.drawString(displayName, 0, 0);

    } finally {
      g.setTransform(oldTransform);
      g.setPaint(oldPaint);
      g.setFont(oldFont);
    }
  }
}
