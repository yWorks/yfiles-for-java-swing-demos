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

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.PortCandidateValidity;

import java.util.Collections;

/**
 * This port candidate provider allows only incoming edges and at most one
 * such edge from any given source node.
 */
class OlivePortCandidateProvider extends AbstractPortCandidateProvider {
  private final INode node;

  OlivePortCandidateProvider( INode node ) {
    this.node = node;
  }

  /**
   * Returns a single port candidate in the center of this provider's
   * associated node. The candidate is valid, if there is no other edge from
   * this provider's associated node to the owner of the given target candidate
   * and invalid otherwise.
   */
  @Override
  public Iterable<IPortCandidate> getSourcePortCandidates(
    IInputModeContext context, IPortCandidate target
  ) {
    IGraph graph = context.getGraph();
    if (graph.getEdge(node, target.getOwner()) == null) {
      return newSingleCandidate(PortCandidateValidity.VALID);
    } else {
      return getPortCandidates(context);
    }
  }

  /**
   * Returns a single port candidate in the center of this provider's
   * associated node. The candidate is valid, if there is no other edge from
   * the owner of the given source candidate to this provider's associated node 
   * and invalid otherwise.
   */
  @Override
  public Iterable<IPortCandidate> getTargetPortCandidates(
    IInputModeContext context, IPortCandidate source
  ) {
    IGraph graph = context.getGraph();
    if (graph.getEdge(source.getOwner(), node) == null) {
      return newSingleCandidate(PortCandidateValidity.VALID);
    } else {
      return getPortCandidates(context);
    }
  }

  /**
   * Returns a single invalid port candidate. Both
   * {@link #getSourcePortCandidates(IInputModeContext)} and
   * {@link #getTargetPortCandidates(IInputModeContext)} delegate to this
   * method.
   */
  @Override
  protected Iterable<IPortCandidate> getPortCandidates( IInputModeContext context ) {
    return newSingleCandidate(PortCandidateValidity.INVALID);
  }

  /**
   * Returns a single port candidate in the center of this provider's
   * associated node.
   */
  private Iterable<IPortCandidate> newSingleCandidate(
    PortCandidateValidity validity
  ) {
    DefaultPortCandidate defaultPortCandidate = new DefaultPortCandidate(
      node, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);
    defaultPortCandidate.setValidity(validity);
    return Collections.singletonList(defaultPortCandidate);
  }
}
