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

import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.PortCandidateValidity;

import java.util.Collections;

/**
 * This port candidate provider always returns an invalid port candidate
 * and thus prevents edge creation.
 */
class RedPortCandidateProvider extends AbstractPortCandidateProvider {

  private final INode node;

  public RedPortCandidateProvider(INode node) {
    this.node = node;
  }

  /**
   * Creates an enumeration of possible port candidates. In this
   * case an enumerable consisting of one invalid candidate is returned.
   * This candidate is located in the center of the node to display
   * the invalid port highlight at that location.
   * <p>
   *   Note that the various variants of getPortCandidates of
   *   {@link AbstractPortCandidateProvider} delegate to this method. This can be
   *   used to provide the same candidates for all use-cases.
   * </p>
   */
  @Override
  protected Iterable<IPortCandidate> getPortCandidates(IInputModeContext context) {
    DefaultPortCandidate defaultPortCandidate = new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_CENTER_ANCHORED);
    defaultPortCandidate.setValidity(PortCandidateValidity.INVALID);
    return Collections.singletonList(defaultPortCandidate);
  }
}
