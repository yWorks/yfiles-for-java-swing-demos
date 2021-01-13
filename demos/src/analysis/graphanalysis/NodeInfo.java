/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package analysis.graphanalysis;

import java.awt.Color;
import java.util.List;

/**
 * Stores information for rendering an {@link com.yworks.yfiles.graph.INode}.
 */
public class NodeInfo extends ModelItemInfo {
  private List<Integer> nodeComponents;

  /**
   * Initializes a new {@code NodeInfo} instance with the given color,
   * components, and component indices.
   * @param color The color to use by the style.
   * @param nodeComponents The indices of the components the node belongs to.
   */
  public NodeInfo(Color color, List<Integer> nodeComponents) {
    super(color);
    this.nodeComponents = nodeComponents;
  }

  /**
   * Returns the indices of the components the node belongs to.
   * @return the indices of the components the node belongs to.
   */
  public List<Integer> getNodeComponents() {
    return nodeComponents;
  }
}
