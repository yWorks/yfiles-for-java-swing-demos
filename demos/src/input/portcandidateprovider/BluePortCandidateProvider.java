/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package input.portcandidateprovider;

import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.PortCandidateValidity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This port candidate provider returns a list of port candidates that are
 * the equivalent to the node's pre-defined ports. Only candidates of
 * unconnected ports are valid, i.e. if a port already has a connected edge, its
 * port candidate is marked as invalid.
 */
class BluePortCandidateProvider extends AbstractPortCandidateProvider {

  private final INode node;

  public BluePortCandidateProvider(INode node) {
    this.node = node;
  }

  /**
   * Returns an enumeration that contains one port candidate for each of the node's pre-defined
   * ports. Each candidate has the same location as the port. Only candidates of
   * unconnected ports are valid, i.e. if a port already has a connected
   * edge, its port candidate is marked as invalid.
   * <p>
   *   Note that the various variants of getPortCandidates of
   *   {@link AbstractPortCandidateProvider} delegate to this method. This can be
   *   used to provide the same candidates for all use-cases.
   * </p>
   */
  @Override
  protected Iterable<IPortCandidate> getPortCandidates(IInputModeContext context) {
    List<IPortCandidate> candidates = new ArrayList<>();
    IGraph graph = context.getGraph();
    if (graph != null) {
      // create a port candidate for each port on the node
      List<DefaultPortCandidate> defaultPortCandidates = node.getPorts().stream().map(DefaultPortCandidate::new).collect(Collectors.toList());
      // per default, a DefaultPortCandidate is valid. we want to make those candidates with ports that have already edges connected to them to be invalid.
      defaultPortCandidates.stream()
          .filter(portCandidate -> graph.degree(portCandidate.getPort()) > 0)
          .forEach(portCandidate -> portCandidate.setValidity(PortCandidateValidity.INVALID));
      candidates.addAll(defaultPortCandidates);
    }
    if (candidates.isEmpty()) {
      // if there a no candidates at all, we create a fallback candidate in the middle of the node
      DefaultPortCandidate item = new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);
      item.setValidity(PortCandidateValidity.INVALID);
      candidates.add(item);
    }
    return candidates;
  }
}
