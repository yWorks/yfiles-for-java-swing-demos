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
package complete.uml;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.EdgeEventArgs;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.IPortOwner;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.VoidNodeStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.GraphModelManager;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.IHighlightIndicatorInstaller;
import com.yworks.yfiles.view.NodeStyleDecorationInstaller;
import com.yworks.yfiles.view.input.CreateEdgeInputMode;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.InputModeEventArgs;

import java.util.ArrayList;

/**
 * A {@link IInputMode} that creates an edge with its own target node if a source node exists.
 * The target node can be dragged around to a desired position. When its center lies within another node at the time,
 * the edge is connected to that node and the dummy target node is deleted.
 */
public class UmlCreateEdgeInputMode extends CreateEdgeInputMode {
  private SimpleNode targetNode;
  private ICanvasObject canvasObject;
  private HelperGraph helperGraph;

  public UmlCreateEdgeInputMode() {
    // by default, ending edge creation is only possible over existing elements
    // removing the hit testable altogether signals the mode that any location
    // for which getTarget below returns a port owner is fine 
    setEndHitTestable(null);

    targetNode = null;
    helperGraph = new HelperGraph();
  }

  @Override
  protected void onGestureStarted(InputModeEventArgs args) {
    super.onGestureStarted(args);

    if (getSourcePortCandidate() != null){
      targetNode = new SimpleNode();
      // only create a target node visualization when there is an actual source node
      INode sourceNode = (INode) getSourcePortCandidate().getOwner();
      if (sourceNode != null) {
        IGraph graph = ((GraphComponent) getInputModeContext().getCanvasComponent()).getGraph();
        INodeDefaults nodeDefaults = graph.getNodeDefaults();
        targetNode.setLayout(RectD.fromCenter(getDummyEdge().getSourcePort().getLocation(), nodeDefaults.getSize()));
        targetNode.setStyle(nodeDefaults.getStyle());
        targetNode.setTag(new UmlClassModel());
        UmlClassLabelSupport.updateAllLabels(helperGraph, targetNode);
        UmlClassLabelSupport.updateNodeSize(helperGraph, targetNode);

        // do not show a target indicator, it is obvious that the new node will be the target
        NodeStyleDecorationInstaller decorationInstaller = new NodeStyleDecorationInstaller();
        decorationInstaller.setNodeStyle(VoidNodeStyle.INSTANCE);
        targetNode.setLookupImplementation(
            ILookup.createWrapped(targetNode.getLookupImplementation(), ILookup.createSingle(decorationInstaller,
                IHighlightIndicatorInstaller.class)));

        // visualize the dummy node in the input mode group
        ICanvasObjectGroup inputModeGroup = getInputModeContext().getCanvasComponent().getInputModeGroup();
        ICanvasObjectGroup tmp = inputModeGroup.addGroup();
        tmp.addChild(targetNode, GraphModelManager.DEFAULT_NODE_DESCRIPTOR);
        for (ILabel label : targetNode.getLabels()) {
          tmp.addChild(label, GraphModelManager.DEFAULT_LABEL_DESCRIPTOR);
        }
        canvasObject = tmp;
      }
    }
  }

  @Override
  protected void updateTargetLocation(PointD pointD) {
    super.updateTargetLocation(pointD);

    if (targetNode != null) {
      targetNode.setLayout(RectD.fromCenter(pointD, targetNode.getLayout().toSizeD()));
    }
  }

  @Override
  protected IPortOwner getTarget(PointD pointD) {
    IPortOwner target = super.getTarget(pointD);
    if (target != null) {
      return target;
    }
    return targetNode;
  }

  @Override
  protected IEdge createEdge() {
    IEdge edge;
    if (getTargetPortCandidate().getOwner().equals(targetNode)) {
      IGraph graph = getInputModeContext().getGraph();

      // create the target node
      INode node = graph.createNode(targetNode.getLayout().toRectD(), targetNode.getStyle(), targetNode.getTag());
      UmlClassLabelSupport.updateAllLabels(graph, node);
      UmlClassLabelSupport.updateNodeSize(graph, node);

      // create the edge
      edge = super.createEdge(graph, getSourcePortCandidate(),
          new DefaultPortCandidate(node, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));

      // fire edge created event
      onEdgeCreated(new EdgeEventArgs(edge));
    } else {
      edge = super.createEdge();
    }

    // clean up the dummy node visualization
    if (canvasObject != null) {
      canvasObject.remove();
    }
    canvasObject = null;
    targetNode = null;

    return edge;
  }

  @Override
  protected void onCanceled() {
    super.onCanceled();

    // clean up the dummy node visualization
    targetNode = null;
    if (canvasObject != null) {
      canvasObject.remove();
    }
  }

  /**
   * Enables modifying {@link SimpleNode} and {@link SimpleLabel} instances
   * through the {@link IGraph} interface.
   */
  private static class HelperGraph extends DefaultGraph {
    @Override
    public ILabel addLabel(
            ILabelOwner item,
            String text,
            ILabelModelParameter param,
            ILabelStyle style,
            SizeD size,
            Object tag
    ) {
      if (item instanceof SimpleNode) {
        SimpleNode node = (SimpleNode) item;

        SimpleLabel newLabel = new SimpleLabel(item, text, param);
        SizeD preferredSize = size != null ? size : style.getRenderer().getPreferredSize(newLabel, style);
        newLabel.setPreferredSize(preferredSize);
        newLabel.setStyle(style);
        newLabel.setTag(tag);

        ArrayList<ILabel> labels = new ArrayList<>();
        IListEnumerable<ILabel> facade = node.getLabels();
        if (facade != null) {
          for (ILabel oldLabel : facade) {
            labels.add(oldLabel);
          }
        }
        node.setLabels(IListEnumerable.create(labels));
        labels.add(newLabel);
        return newLabel;
      } else {
        return super.addLabel(item, text, param, style, size, tag);
      }
    }

    @Override
    public void setLabelLayoutParameter( ILabel label, ILabelModelParameter param ) {
      if (label instanceof SimpleLabel) {
        ((SimpleLabel) label).setLayoutParameter(param);
      } else {
        super.setLabelLayoutParameter(label, param);
      }
    }

    @Override
    public void setLabelPreferredSize( ILabel label, SizeD size ) {
      if (label instanceof SimpleLabel) {
        ((SimpleLabel) label).setPreferredSize(size);
      } else {
        super.setLabelPreferredSize(label, size);
      }
    }

    @Override
    public void setNodeLayout( INode node, RectD layout ) {
      if (node instanceof SimpleNode) {
        ((SimpleNode) node).setLayout(layout);
      } else {
        super.setNodeLayout(node, layout);
      }
    }
  }
}
