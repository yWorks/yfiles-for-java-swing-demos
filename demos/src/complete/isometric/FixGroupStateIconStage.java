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
package complete.isometric;

import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.NodeList;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.layout.FixNodeLayoutStage;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.INodeLayout;
import com.yworks.yfiles.layout.LayoutGraph;

/**
 * When the user opens/closes a folder/group node by clicking its state icon, the layout algorithm calculates a new
 * layout. This {@link com.yworks.yfiles.layout.ILayoutStage} moves the graph afterwards so, that the state icon of the group/folder
 * node remains under the mouse cursor.
 */
class FixGroupStateIconStage extends FixNodeLayoutStage {
  public FixGroupStateIconStage( ILayoutAlgorithm core ) {
    super(core);
  }

  /**
   * Overwritten to fix the lower left corner (where the state icon is placed) of the isometric painted folder/group
   * node.
   */
  @Override
  protected YPoint calculateFixPoint( LayoutGraph graph, NodeList fixedNodes ) {
    Node node = fixedNodes.firstNode();
    IDataProvider provider = graph.getDataProvider(IsometricTransformationLayoutStage.TRANSFORMATION_DATA_DPKEY);
    IsometricGeometry geometry = (IsometricGeometry) provider.get(node);

    double[] corners = IsometricTransformationSupport.calculateCorners(geometry);
    INodeLayout nodeLayout = graph.getLayout(node);
    IsometricTransformationSupport.moveTo(nodeLayout.getX(), nodeLayout.getY(), corners);
    return new YPoint(corners[IsometricTransformationSupport.C3_X], corners[IsometricTransformationSupport.C3_Y]);
  }
}
