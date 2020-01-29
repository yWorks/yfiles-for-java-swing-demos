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
package input.edgereconnection;

import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link IEdgeReconnectionPortCandidateProvider} that uses candidates with a
 * dynamic NodeScaled port location model. It allows moving ports to any
 * location inside a green node.
 */
class GreenEdgeReconnectionPortCandidateProvider implements IEdgeReconnectionPortCandidateProvider {

  /**
   * Returns for each green node a candidate with a dynamic NodeScaled port
   * location model. When the Shift key is pressed, a port can be placed
   * anywhere inside that node.
   */
  public Iterable<IPortCandidate> getSourcePortCandidates(IInputModeContext context) {
    IGraph graph = context.getGraph();
    if (graph != null) {
      // add dynamic NodeScaled parameters for all green nodes in the graph
      // holding down shift allows for exact positioning of the edges.
      List<IPortCandidate> list = graph.getNodes().stream()
          .filter(node -> Colors.FOREST_GREEN.equals(node.getTag()))
          .map(node -> new DefaultPortCandidate(node, FreeNodePortLocationModel.INSTANCE))
          .collect(Collectors.toList());
      return list;
    } else {
      return Collections.EMPTY_LIST;
    }
  }

  /**
   * The same as {@link #getSourcePortCandidates(com.yworks.yfiles.view.input.IInputModeContext)}.
   */
  public Iterable<IPortCandidate> getTargetPortCandidates(IInputModeContext context) {
    return getSourcePortCandidates(context);
  }
}
