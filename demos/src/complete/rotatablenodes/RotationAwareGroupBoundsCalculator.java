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
package complete.rotatablenodes;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IGroupBoundsCalculator;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.INodeInsetsProvider;

/**
 * Calculates group bounds taking the rotated layout for nodes which {@link RotatableNodeStyleDecorator}
 * support rotation.
 */
public class RotationAwareGroupBoundsCalculator implements IGroupBoundsCalculator {

  /**
   * Calculates the minimum bounds for the given group node to enclose all its children plus insets.
   */
  @Override
  public RectD calculateBounds(IGraph graph, INode groupNode) {
    RectD bounds = RectD.EMPTY;
    for (INode node : graph.getChildren(groupNode)) {
      if (node.getStyle() instanceof RotatableNodeStyleDecorator) {
        //if the node supports rotation: add the outer bounds of the rotated layout
        RotatableNodeStyleDecorator rotatableWrapper = ((RotatableNodeStyleDecorator) node.getStyle());
        bounds = RectD.add(bounds, rotatableWrapper.getRotatedLayout(node).getBounds());
      } else {
        bounds = RectD.add(bounds, node.getLayout().toRectD());
      }
    }
    //if we have content: add insets
    return bounds.isEmpty() ? bounds : bounds.getEnlarged(getInsets(groupNode));
  }

  /**
   * Returns insets to add to apply to the given groupNode.
   */
  private static InsetsD getInsets(INode groupNode) {
    INodeInsetsProvider provider = groupNode.lookup(INodeInsetsProvider.class);
    if(provider != null) {
      //get insets from the node's insets provider if there is one
      return provider.getInsets(groupNode);
    }
    // otherwise add 5 to each border
    return new InsetsD(5);
  }
}
