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
package complete.bpmn.layout;

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.algorithms.YRectangle;
import com.yworks.yfiles.layout.IEdgeLabelLayout;
import com.yworks.yfiles.layout.INodeLabelLayout;
import com.yworks.yfiles.layout.INodeLayout;
import com.yworks.yfiles.layout.IProfitModel;
import com.yworks.yfiles.layout.LabelCandidate;
import com.yworks.yfiles.layout.LayoutGraph;

/**
 * A profit model for exterior node labels that prefers node sides that are far away from incoming or outgoing edges.
 */
class BpmnLabelProfitModel implements IProfitModel {
  private final LayoutGraph graph;

  public BpmnLabelProfitModel( LayoutGraph graph ) {
    this.graph = graph;
  }

  public double getProfit( LabelCandidate candidate ) {
    if (candidate.getOwner() instanceof IEdgeLabelLayout) {
      return 1;
    }
    double profit = 0;
    INodeLabelLayout nl = (INodeLabelLayout)candidate.getOwner();
    Node node = graph.getOwner(nl);
    INodeLayout nodeLayout = graph.getLayout(node);
    YRectangle candidateLayout = candidate.getBoundingBox();

    boolean isLeft = candidateLayout.getX() + candidateLayout.getWidth() / 2 < nodeLayout.getX();
    boolean isRight = candidateLayout.getX() + candidateLayout.getWidth() / 2 > (nodeLayout.getX() + nodeLayout.getWidth());
    boolean isTop = candidateLayout.getY() + candidateLayout.getHeight() / 2 < nodeLayout.getY();
    boolean isBottom = candidateLayout.getY() + candidateLayout.getHeight() / 2 > (nodeLayout.getY() + nodeLayout.getHeight());

    boolean horizontalCenter = !isLeft && !isRight;
    boolean verticalCenter = !isTop && !isBottom;
    if (horizontalCenter && verticalCenter) {
      // candidate is in center -> don't use
      return 0;
    } else if (horizontalCenter || verticalCenter) {
      profit = 0.95;
    } else {
      // diagonal candidates get a bit less profit
      profit = 0.9;
    }
    for (Edge edge : node.getEdges()) {
      YPoint portLocation = edge.source() == node ? graph.getSourcePointRel(edge) : graph.getTargetPointRel(edge);
      if (Math.abs(portLocation.getX()) > Math.abs(portLocation.getY())) {
        // edge at left or right
        if (portLocation.getX() < 0 && isLeft || portLocation.getX() > 0 && isRight) {
          if (isTop || isBottom) {
            profit -= 0.03;
          } else {
            // edge at same side as candidate
            profit -= 0.2;
          }
        } else if (horizontalCenter) {
          // candidate is close to the edge but not on the same side
          profit -= 0.01;
        }
      } else {
        // edge at top or bottom
        if (portLocation.getY() < 0 && isTop || portLocation.getY() > 0 && isBottom) {
          if (isLeft || isRight) {
            profit -= 0.03;
          } else {
            profit -= 0.2;
          }
        } else if (verticalCenter) {
          // candidate is close to the edge but not on the same side
          profit -= 0.01;
        }
      }
    }

    return Math.max(0, profit);
  }

}
