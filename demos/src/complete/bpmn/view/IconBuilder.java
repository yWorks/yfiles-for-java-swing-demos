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
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.view.Pen;
import java.awt.Paint;
import java.util.List;

/**
 * Builder class to create {@link IIcon}s.
 */
class IconBuilder {
  private GeneralPath path;

  private GeneralPath getPath() {
    return path != null ? path : (path = new GeneralPath(16));
  }

  private void setPath( GeneralPath value ) {
    path = value;
  }

  private Pen pen;

  public final Pen getPen() {
    return this.pen;
  }

  public final void setPen( Pen value ) {
    this.pen = value;
  }

  private Paint paint;

  public final Paint getPaint() {
    return this.paint;
  }

  public final void setPaint( Paint value ) {
    this.paint = value;
  }

  public IconBuilder() {
    clear();
  }

  public final void moveTo( double x, double y ) {
    getPath().moveTo(x, y);
  }

  public final void lineTo( double x, double y ) {
    getPath().lineTo(x, y);
  }

  public final void quadTo( double cx, double cy, double x, double y ) {
    getPath().quadTo(cx, cy, x, y);
  }

  public final void cubicTo( double c1x, double c1y, double c2x, double c2y, double x, double y ) {
    getPath().cubicTo(c1x, c1y, c2x, c2y, x, y);
  }

  public final void arcTo( double r, double cx, double cy, double fromAngle, double toAngle ) {
    double a = (toAngle - fromAngle) / 2.0;
    int sgn = a < 0 ? -1 : 1;
    if (Math.abs(a) > Math.PI / 4) {
      // bigger then a quarter circle -> split into multiple arcs
      double start = fromAngle;
      double end = fromAngle + sgn * Math.PI / 2;
      while (sgn * end < sgn * toAngle) {
        arcTo(r, cx, cy, start, end);
        start = end;
        end += sgn * Math.PI / 2;
      }
      arcTo(r, cx, cy, start, toAngle);
      return;
    }

    // calculate unrotated control points
    double x1 = r * Math.cos(a);
    double y1 = -r * Math.sin(a);

    double m = (Math.sqrt(2) - 1) * 4 / 3;
    double mTanA = m * Math.tan(a);

    double x2 = x1 - mTanA * y1;
    double y2 = y1 + mTanA * x1;
    double x3 = x2;
    double y3 = -y2;

    // rotate the control points by (fromAngle + a)
    double rot = fromAngle + a;
    double sinRot = Math.sin(rot);
    double cosRot = Math.cos(rot);

    getPath().cubicTo(cx + x2 * cosRot - y2 * sinRot, cy + x2 * sinRot + y2 * cosRot, cx + x3 * cosRot - y3 * sinRot, cy + x3 * sinRot + y3 * cosRot, cx + r * Math.cos(toAngle), cy + r * Math.sin(toAngle));
  }

  public final IIcon createEllipseIcon() {
    getPath().appendEllipse(new RectD(0, 0, 1, 1), false);
    return getPathIcon();
  }

  public final void close() {
    getPath().close();
  }

  public final IIcon combineIcons( List<IIcon> icons ) {
    CombinedIcon icon = new CombinedIcon(icons);
    clear();
    return icon;
  }

  public final IIcon createLineUpIcon( List<IIcon> icons, SizeD innerIconSize, double gap ) {
    LineUpIcon icon = new LineUpIcon(icons, innerIconSize, gap);
    clear();
    return icon;
  }

  public final IIcon getPathIcon() {
    PathIcon pathIcon = new PathIcon();
    pathIcon.setPath(path);
    pathIcon.setPen(getPen());
    pathIcon.setPaint(getPaint());
    IIcon icon = pathIcon;
    clear();
    return icon;
  }

  public final IIcon createRectIcon( double cornerRadius ) {
    RectIcon rectIcon = new RectIcon();
    rectIcon.setPen(getPen());
    rectIcon.setPaint(getPaint());
    rectIcon.setCornerRadius(cornerRadius);
    clear();
    return rectIcon;
  }

  public final IIcon createRectIcon( double topLeftRadius, double topRightRadius, double bottomLeftRadius, double bottomRightRadius ) {
    VariableRectIcon rectIcon = new VariableRectIcon();
    rectIcon.setPen(getPen());
    rectIcon.setPaint(getPaint());
    rectIcon.setTopLeftRadius(topLeftRadius);
    rectIcon.setTopRightRadius(topRightRadius);
    rectIcon.setBottomLeftRadius(bottomLeftRadius);
    rectIcon.setBottomRightRadius(bottomRightRadius);
    clear();
    return rectIcon;
  }

  public final void clear() {
    setPen(Pen.getBlack());
    setPaint(null);
    setPath(null);
  }

}
