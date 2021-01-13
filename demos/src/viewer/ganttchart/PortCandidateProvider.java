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
package viewer.ganttchart;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;

/**
 * Restricts edge connections to the vertical center of an activity node's
 * left and right border.
 */
public class PortCandidateProvider implements IPortCandidateProvider {
  private final INode node;

  /**
   * Initializes a new {@code PortCandidateProvider} instance for the given node.
   */
  public PortCandidateProvider( INode node ) {
    this.node = node;
  }

  /**
   * Returns a port candidate on the right side of the node where an edge can start.
   * @param context The context for which the candidates should be provided.
   */
  @Override
  public Iterable<IPortCandidate> getSourcePortCandidates( IInputModeContext context ) {
    // create a port candidate at the right side of the node
    return IEnumerable.create(new DefaultPortCandidate(
      node, FreeNodePortLocationModel.NODE_RIGHT_ANCHORED));
  }

  /**
   * Returns a port candidate on the left side of the node where an edge can end.
   * @param context The context for which the candidates should be provided.
   */
  @Override
  public Iterable<IPortCandidate> getTargetPortCandidates( IInputModeContext context ) {
    // create a port candidate at the left side of the node
    return IEnumerable.create(new DefaultPortCandidate(
      node, FreeNodePortLocationModel.NODE_LEFT_ANCHORED));
  }

  /**
   * @param context The context for which the candidates should be provided.
   * @param source The opposite port candidate.
   */
  @Override
  public Iterable<IPortCandidate> getSourcePortCandidates(
    IInputModeContext context, IPortCandidate source
  ) {
    return getSourcePortCandidates(context);
  }

  /**
   * @param context The context for which the candidates should be provided.
   * @param target The opposite port candidate.
   */
  @Override
  public Iterable<IPortCandidate> getTargetPortCandidates(
    IInputModeContext context, IPortCandidate target
  ) {
    return this.getTargetPortCandidates(context);
  }
}
