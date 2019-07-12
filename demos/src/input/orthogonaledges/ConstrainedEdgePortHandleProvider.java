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
package input.orthogonaledges;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.view.input.ConstrainedHandle;
import com.yworks.yfiles.view.input.IEdgePortHandleProvider;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;

/**
 * An {@link IEdgePortHandleProvider} that constraints a
 * port location handle to the layout rectangle of the port's owner node.
 * <p>
 * If a {@link ConstrainedEdgePortHandleProvider#ConstrainedEdgePortHandleProvider(IEdgePortHandleProvider) provider shall be wrapped},
 * the port location handle of the wrapped provider is constrained,
 * {@link ConstrainedEdgePortHandleProvider#ConstrainedEdgePortHandleProvider() otherwise} the original port location handle.
 * </p>
 */
class ConstrainedEdgePortHandleProvider implements IEdgePortHandleProvider {

  private IEdgePortHandleProvider wrapped;

  /**
   * Creates a new instance constraining the original port location handle.
   */
  public ConstrainedEdgePortHandleProvider() {
  }

  /**
   * Creates a new instance constraining the port location handle of the {@code wrapped} provider.
   * @param wrapped The provider whose handle shall be constrained.
   */
  public ConstrainedEdgePortHandleProvider(IEdgePortHandleProvider wrapped) {
    this.wrapped = wrapped;
  }

  /**
   * Returns a handle that is constrained to the layout rectangle of the
   * port's owner node.
   */
  @Override
  public IHandle getHandle(IInputModeContext context, IEdge edge, boolean sourceHandle) {
    IPort port = sourceHandle ? edge.getSourcePort() : edge.getTargetPort();
    IHandle portHandle = wrapped != null
        ? wrapped.getHandle(context, edge, sourceHandle)
        : port.lookup(IHandle.class);
    return port.getOwner() instanceof INode
        ? new ConstrainedPortLocationHandle((INode) port.getOwner(), portHandle)
        : portHandle;
  }

  /**
   * Handle that is constrained to the layout rectangle of
   * the port's owner node.
   */
  static class ConstrainedPortLocationHandle extends ConstrainedHandle {
    private INode node;

    ConstrainedPortLocationHandle(INode node, IHandle wrappedHandle) {
      super(wrappedHandle);
      this.node = node;
    }

    @Override
    protected PointD constrainNewLocation(IInputModeContext context, PointD originalLocation, PointD newLocation) {
      return newLocation.getConstrained(node.getLayout().toRectD());
    }
  }
}
