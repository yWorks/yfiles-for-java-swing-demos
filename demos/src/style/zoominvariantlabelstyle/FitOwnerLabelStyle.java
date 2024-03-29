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
package style.zoominvariantlabelstyle;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;

/**
 * Scales the associated label to fit its owner.
 */
public class FitOwnerLabelStyle extends AbstractZoomInvariantLabelStyle {
  /**
   * Scales the label to fit its owner.
   * @param label the current label which will be styled
   * @param zoom ignored
   */
  @Override
  protected double getScaleForZoom( ILabel label, double zoom ) {
    double labelWidth = label.getLayout().getWidth();
    if (labelWidth < 1e-12) {
      return 1;
    }

    double ratio = 1;
    if (label.getOwner() instanceof INode) {
      double nodeWidth = ((INode) label.getOwner()).getLayout().getWidth();
      ratio = Math.min(1, nodeWidth / labelWidth);
    } else if (label.getOwner() instanceof IEdge) {
      IEdge edge = (IEdge) label.getOwner();
      PointD sp = edge.getSourcePort().getLocation();
      PointD tp = edge.getTargetPort().getLocation();
      double dx = sp.getX() - tp.getX();
      double dy = sp.getY() - tp.getY();
      double edgeLength = Math.sqrt(dx * dx + dy * dy);
      ratio = Math.min(1, edgeLength / labelWidth);
    }
    return ratio;
  }
}
