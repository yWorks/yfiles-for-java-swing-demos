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
package complete.logicgate;

import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.input.CreateEdgeInputMode;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.EdgeDirectionPolicy;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.PortCandidateValidity;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link IPortCandidateProvider} implementation, that provides all available ports with the specified
 * edge direction.
 */
public class DescriptorDependentPortCandidateProvider implements IPortCandidateProvider {
  @Override
  public Iterable<IPortCandidate> getSourcePortCandidates(IInputModeContext context, IPortCandidate target) {
    return getCandidatesForDirection(PortDescriptor.EdgeDirection.OUT, context);
  }

  @Override
  public Iterable<IPortCandidate> getTargetPortCandidates(IInputModeContext context, IPortCandidate source) {
    return getCandidatesForDirection(PortDescriptor.EdgeDirection.IN, context);
  }

  @Override
  public Iterable<IPortCandidate> getSourcePortCandidates(IInputModeContext context) {
    return getCandidatesForDirection(PortDescriptor.EdgeDirection.OUT, context);
  }

  @Override
  public Iterable<IPortCandidate> getTargetPortCandidates(IInputModeContext context) {
    return getCandidatesForDirection(PortDescriptor.EdgeDirection.IN, context);
  }

  /**
   * Returns the suitable candidates based on the specified {@link PortDescriptor.EdgeDirection}.
   */
  private Iterable<IPortCandidate> getCandidatesForDirection(PortDescriptor.EdgeDirection direction, IInputModeContext context) {
    // If EdgeDirectionPolicy.DETERMINE_FROM_PORT_CANDIDATES is used, CreateEdgeInputMode queries getSourcePortCandidates
    // as well as getTargetPortCandidates to collect possible port candidates to start the edge creation.
    // In this case this method is called twice (with EdgeDirection.IN and EdgeDirection.OUT) so for each call we
    // should only return the *valid* port candidates of a port as otherwise for each port a valid as well as an invalid
    // candidate is returned.
    boolean provideAllCandidates = true;
    if (context.getParentInputMode() instanceof CreateEdgeInputMode) {
      CreateEdgeInputMode ceim = (CreateEdgeInputMode) context.getParentInputMode();
      // check the edge direction policy as well as whether candidates are collected for starting or ending the edge creation
      provideAllCandidates = ceim.getEdgeDirectionPolicy() != EdgeDirectionPolicy.DETERMINE_FROM_PORT_CANDIDATES
          || ceim.isCreationInProgress();
    }

    List<IPortCandidate> candidates = new ArrayList<IPortCandidate>();
    // iterate over all available ports
    for (IPort port : context.getGraph().getPorts()) {
      // create a port candidate, invalidate it (so it is visible but not usable)
      DefaultPortCandidate candidate = new DefaultPortCandidate(port);
      candidate.setValidity(PortCandidateValidity.INVALID);
      // get the port descriptor
      if (port.getTag() instanceof PortDescriptor) {
        PortDescriptor portDescriptor = (PortDescriptor) port.getTag();
        // make the candidate valid if the direction is the same as the one supplied
        if (portDescriptor.getEdgeDirection() == direction) {
          candidate.setValidity(PortCandidateValidity.VALID);
        }
      }
      // add the candidate to the list
      if (provideAllCandidates || candidate.getValidity() == PortCandidateValidity.VALID) {
        candidates.add(candidate);
      }
    }
    // and return the list
    return candidates;
  }
}
