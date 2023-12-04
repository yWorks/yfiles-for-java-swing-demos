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
package complete.orgchart;

import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.geometry.IRectangle;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
* A visual that displays a detailed version of the employee including the icon, full name, position, email, fax and phone number as
 * well as a special visual that depicts the current status of the employee.
*/
class DetailVisual extends OrgChartVisual {
  private static final Font font = new Font("Dialog", Font.PLAIN, 10);
  private static final Font font2 = new Font("Dialog", Font.ITALIC, 8);

  /**
   * A visual that visualizes the status of the employee by colored circles.
   */
  private StatusVisual statusVisual;

  public DetailVisual(Employee employee, IRectangle layout, boolean isFocused, boolean isHovered) {
    super(employee, layout, isFocused, isHovered);
    this.statusVisual = new StatusVisual(employee.getStatus());
  }

  @Override
  public void paintContent(IRenderContext context, Graphics2D g) {
    final AffineTransform oldTransform = g.getTransform();
    final Paint oldPaint = g.getPaint();
    final Font oldFont = g.getFont();
    try {

      // draw the icon for the employee
      paintIcon(g);

      // draw the status indicator in the north east corner
      paintStatus(context, g);

      // draw the properties of the employee
      paintProperties(g);

    } finally {
      g.setTransform(oldTransform);
      g.setPaint(oldPaint);
      g.setFont(oldFont);
    }
  }

  /**
   * Paints the various properties of the employee such as the name, position, email etc.
   */
  private void paintProperties(Graphics2D g) {
    g.setPaint(Color.BLACK);
    g.setFont(font);
    Employee employee = getEmployee();
    g.drawString(employee.getFirstName() + " " + employee.getName(), 80, 20);
    g.setFont(font2);
    g.drawString(employee.getPosition(), 80, 38);
    g.setFont(font);
    g.drawString(employee.getEmail(), 80, 56);
    g.drawString(employee.getPhone(), 80, 74);
    g.drawString(employee.getFax(), 80, 92);
  }

  /**
   * Paints the icon of the employee on the left side of the rectangle
   */
  private void paintIcon(Graphics2D g) {
    final AffineTransform oldTransform = g.getTransform();
    final RenderingHints oldHints = new RenderingHints((Map) g.getRenderingHints());

    try {
      g.translate(5, 15);
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(getEmployee().getIconImage(), 0, 0, 57, 67, null);
    } finally {
      g.setTransform(oldTransform);
      g.setRenderingHints(oldHints);
    }

  }

  /**
   * Paints the status visual for the employee in the north east corner of the rectangle.
   */
  private void paintStatus(IRenderContext context, Graphics2D g) {
    final AffineTransform oldTransform = g.getTransform();
    try {
      g.translate(235, 15);
      statusVisual.paint(context, g);
    } finally {
      g.setTransform(oldTransform);
    }
  }
}
