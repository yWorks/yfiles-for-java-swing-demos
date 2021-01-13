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
package input.portcandidateprovider;

import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.PortCandidateValidity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This port candidate provider only allows connections from green nodes.
 * To achieve this, this class returns different port candidates for source
 * and target ports.
 */
class GreenPortCandidateProvider extends AbstractPortCandidateProvider {

  private final INode node;

  public GreenPortCandidateProvider(INode node) {
    this.node = node;
  }

  /**
   * Returns a list of port candidates that contains one central port candidate
   * if the owner node of the source candidate is green, or an empty list otherwise.
   * <p>
   *   This method is called when during edge creation the mouse drags the edge to be
   *   created over a green node.
   * </p>
   * @param context a context containing information of the current state of the input mode.
   * @param source the port candidate that was chosen for the source port of the edge.
   */
  @Override
  public Iterable<IPortCandidate> getTargetPortCandidates(IInputModeContext context, IPortCandidate source) {
    // Check if the source node is green
    if (Colors.FOREST_GREEN.equals(source.getOwner().getTag())) {
      // if so, return port candidates
      return IPortCandidateProvider.fromNodeCenter(node).getTargetPortCandidates(context, source);
    } else {
      // else return empty list
      return Collections.EMPTY_LIST;
    }
  }

  /**
   * Returns a list of port candidates that contains one central port candidate
   * if the source node of the edge is green, or an empty list otherwise.
   * <p>
   *   This method is called when the target port of an edge at a green node is relocated.
   * </p>
   * @param context a context containing information of the current state of the input mode.
   */
  @Override
  public Iterable<IPortCandidate> getTargetPortCandidates(IInputModeContext context) {
    // Check if the source node is green
    if (Colors.FOREST_GREEN.equals(node.getTag())) {
      // if so, return port candidates
      return IPortCandidateProvider.fromNodeCenter(node).getTargetPortCandidates(context);
    } else {
      // else return empty list
      return Collections.EMPTY_LIST;
    }
  }

  /**
   * Returns a list that contains a port candidate for each of the node's
   * ports. Each candidate has the same location as the port. If a port
   * already has a connected edge, its port candidate is marked as invalid.
   * <p>
   *   Note that the variants of getPortCandidates for target ports are all
   *   implemented by this class. Therefore, this method is only used for
   *   source ports.
   * </p>
   * <p>
   *   More precisely, This method is called when the mouse hovers over
   *   a green node, when edge creation is started at a green node and
   *   when the source port of an edge at a green node is relocated.
   *   Thus, this method is potentially called very often.
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
          .filter(portCandidate -> graph.outDegree(portCandidate.getPort()) > 0)
          .forEach(portCandidate -> portCandidate.setValidity(PortCandidateValidity.INVALID));
      candidates.addAll(defaultPortCandidates);
    }
    if (!candidates.stream().anyMatch(portCandidate -> portCandidate.getValidity() == PortCandidateValidity.VALID)) {
      // If no valid candidates have been created so far, use the ShapeGeometryPortCandidateProvider as fallback.
      // This provides a candidate in the middle of each of the four sides of the node.
      IPortCandidateProvider.fromShapeGeometry(node).getSourcePortCandidates(context).forEach(candidates::add);
    }
    return candidates;
  }
}
