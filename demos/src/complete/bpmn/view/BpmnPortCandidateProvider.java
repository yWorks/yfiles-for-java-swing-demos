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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.AdjacencyTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.CreateEdgeInputMode;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.PortCandidateValidity;
import java.util.ArrayList;

/**
 * Provides some existing ports as well as ports on the north, south, east and west center of the visual bounds of a BPMN
 * node.
 * <p>
 * An existing port is provided if it either uses an {@link EventPortStyle} or is used by at least one edge.
 * </p>
 */
public class BpmnPortCandidateProvider extends AbstractPortCandidateProvider {
  private final INode node;

  public BpmnPortCandidateProvider( INode node ) {
    this.node = node;
  }

  @Override
  protected Iterable<IPortCandidate> getPortCandidates( IInputModeContext context ) {
    ArrayList<IPortCandidate> portCandidates = new ArrayList<>();

    // provide existing ports as candidates only if they use EventPortStyle and have no edges attached to them.
    for (IPort port : node.getPorts()) {
      if (port.getStyle() instanceof EventPortStyle && context.lookup(IGraph.class).edgesAt(port, AdjacencyTypes.ALL).size() == 0) {
        portCandidates.add(new DefaultPortCandidate(port));
      }
    }

    INodeStyle nodeStyle = node.getStyle();
    if (nodeStyle instanceof ActivityNodeStyle
        || nodeStyle instanceof ChoreographyNodeStyle
        || nodeStyle instanceof DataObjectNodeStyle
        || nodeStyle instanceof AnnotationNodeStyle
        || nodeStyle instanceof GroupNodeStyle
        || nodeStyle instanceof DataStoreNodeStyle) {
      portCandidates.add(new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_TOP_ANCHORED));
      portCandidates.add(new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_BOTTOM_ANCHORED));
      portCandidates.add(new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_LEFT_ANCHORED));
      portCandidates.add(new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_RIGHT_ANCHORED));
    } else if (nodeStyle instanceof EventNodeStyle
        || nodeStyle instanceof GatewayNodeStyle) {
      double dmax = Math.min(node.getLayout().getWidth() / 2, node.getLayout().getHeight() / 2);
      FreeNodePortLocationModel model = FreeNodePortLocationModel.INSTANCE;
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(0, -dmax)), PortCandidateValidity.VALID));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(dmax, 0)), PortCandidateValidity.VALID));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(0, dmax)), PortCandidateValidity.VALID));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(-dmax, 0)), PortCandidateValidity.VALID));
    } else if (nodeStyle instanceof ConversationNodeStyle) {
      double dx = 0.5 * Math.min(node.getLayout().getWidth(), node.getLayout().getHeight() / BpmnConstants.CONVERSATION_WIDTH_HEIGHT_RATIO);
      double dy = dx * BpmnConstants.CONVERSATION_WIDTH_HEIGHT_RATIO;
      FreeNodePortLocationModel model = FreeNodePortLocationModel.INSTANCE;
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(0, -dy)), PortCandidateValidity.VALID));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(dx, 0)), PortCandidateValidity.VALID));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(0, dy)), PortCandidateValidity.VALID));
      portCandidates.add(new DefaultPortCandidate(node, model.createParameter(new PointD(0.5, 0.5), new PointD(-dx, 0)), PortCandidateValidity.VALID));
    }
    CreateEdgeInputMode ceim = context.getParentInputMode() instanceof CreateEdgeInputMode ? (CreateEdgeInputMode)context.getParentInputMode() : null;
    CanvasComponent canvasControl = context.getCanvasComponent();
    if (ceim == null || canvasControl == null || IEventRecognizer.SHIFT_PRESSED.isRecognized(canvasControl, canvasControl.getLastInputEvent())) {
      // add a dynamic candidate
      portCandidates.add(new DefaultPortCandidate(node, new FreeNodePortLocationModel()));
    }
    return portCandidates;
  }

}
