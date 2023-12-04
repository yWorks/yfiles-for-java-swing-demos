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
package style.jcomponentstyle;

import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.IPortOwner;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.PortCandidateValidity;

import java.util.Collections;

/**
 * This port candidate provider only allows connections from Customer nodes to Product nodes.
 * To achieve this, this class returns different port candidates for source
 * and target ports.
 */
public class CustomerProductPortCandidateProvider extends AbstractPortCandidateProvider {

  private final IPortOwner portOwner;

  /**
   * Creates a new instance for the specified port owner.
   */
  public CustomerProductPortCandidateProvider(IPortOwner portOwner) {
    this.portOwner = portOwner;
  }

  /**
   * Returns an enumerable containing only an invalid candidate if the port owner is a Customer and
   * delegates to {@link #getPortCandidates(com.yworks.yfiles.view.input.IInputModeContext)} otherwise.
   * <p>
   *   This method is called when during edge creation the mouse drags the edge to be
   *   created over a node.
   * </p>
   * @param context a context containing information of the current state of the input mode.
   * @param source the port candidate that was chosen for the source port of the edge.
   */
  @Override
  public Iterable<IPortCandidate> getTargetPortCandidates(IInputModeContext context, IPortCandidate source) {
    IPortOwner owner = portOwner;
    if (owner.getTag() instanceof Customer) {
      // the target port owner is a Customer but as we don't allow edges to Customer nodes, an invalid candidate is returned
      IPortLocationModelParameter parameter = context.getGraph().getNodeDefaults().getPortDefaults().getLocationParameter();
      DefaultPortCandidate candidate = new DefaultPortCandidate(owner, parameter);
      candidate.setValidity(PortCandidateValidity.INVALID);
      return Collections.singletonList(candidate);
    }
    // the target port owner has to be a Product so we delegate to getPortCandidates where a valid candidate is created
    return getPortCandidates(context);
  }

  /**
   * Returns an empty enumerable if the port owner is a Product and
   * delegates to {@link #getPortCandidates(com.yworks.yfiles.view.input.IInputModeContext)} otherwise.
   * <p>
   *   This method is called when the mouse moves over the canvas to find port candidates to start an
   *   edge creation gesture from.
   * </p>
   * @param context a context containing information of the current state of the input mode.
   */
  @Override
  public Iterable<IPortCandidate> getSourcePortCandidates(IInputModeContext context) {
    IPortOwner owner = portOwner;
    if (owner.getTag() instanceof Product) {
      // the source port owner is a Product but as we don't allows edges from Product nodes, no candidates are returned
      return Collections.EMPTY_LIST;
    }
    // the target port owner has to be a Customer so we delegate to getPortCandidates where a valid candidate is created
    return getPortCandidates(context);
  }

  /**
   * Returns an enumeration that contains a port candidate for the port owner's first
   * port. The candidate has the same location as the port. If the node has no ports, yet,
   * a candidate with the default location for new ports is returned.
   */
  @Override
  protected Iterable<IPortCandidate> getPortCandidates(IInputModeContext context) {
    IPortOwner owner = portOwner;
    if (owner.getPorts().size() > 0) {
      // use the existing port as port candidate
      IPort port = owner.getPorts().getItem(0);
      return Collections.singletonList(new DefaultPortCandidate(port));
    }
    // if the node has no ports, yet, we create a candidate at the default location for new ports
    IPortLocationModelParameter parameter = context.getGraph().getNodeDefaults().getPortDefaults().getLocationParameter();
    return Collections.singletonList(new DefaultPortCandidate(owner, parameter));
  }
}
