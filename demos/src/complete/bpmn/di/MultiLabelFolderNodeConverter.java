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
package complete.bpmn.di;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.DefaultFolderNodeConverter;
import com.yworks.yfiles.graph.FolderNodeState;
import com.yworks.yfiles.graph.FoldingLabelState;
import com.yworks.yfiles.graph.FoldingPortState;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.utils.IListEnumerable;
import complete.bpmn.view.ChoreographyNodeStyle;

/**
 * A {@link com.yworks.yfiles.graph.IFolderNodeConverter} that handles multiple nodes labels.
 */
public class MultiLabelFolderNodeConverter extends DefaultFolderNodeConverter {
  private boolean copyLabels;

  /**
   * Gets a value indicating whether all labels of the {@link IFoldingView#getMasterItem(com.yworks.yfiles.graph.IModelItem) master group node}
   * should be recreated for the collapsed group node instance.
   * <p>
   * This setting can be used to initially create a copy of all the labels of the master group node (if any) and subsequently
   * synchronize the {@link ILabel#getText() Text} property with the master's node label text. Set it to {@code true} if all
   * labels should be copied; {@code false} otherwise. The default is {@code false}.
   * </p>
   * @return The CopyLabels.
   * @see DefaultFolderNodeConverter#getLabelStyle()
   * @see DefaultFolderNodeConverter#getLabelLayoutParameter()
   * @see #setCopyLabels(boolean)
   */
  public final boolean isCopyLabels() {
    return this.copyLabels;
  }

  /**
   * Sets a value indicating whether all labels of the {@link IFoldingView#getMasterItem(com.yworks.yfiles.graph.IModelItem) master group node}
   * should be recreated for the collapsed group node instance.
   * <p>
   * This setting can be used to initially create a copy of all the labels of the master group node (if any) and subsequently
   * synchronize the {@link ILabel#getText() Text} property with the master's node label text. Set it to {@code true} if all
   * labels should be copied; {@code false} otherwise. The default is {@code false}.
   * </p>
   * @param value The CopyLabels to set.
   * @see DefaultFolderNodeConverter#getLabelStyle()
   * @see DefaultFolderNodeConverter#getLabelLayoutParameter()
   * @see #isCopyLabels()
   */
  public final void setCopyLabels( boolean value ) {
    this.copyLabels = value;
  }

  @Override
  public void updateFolderNodeState( FolderNodeState state, IFoldingView foldingView, INode viewNode, INode masterNode ) {
    synchronizeLabels(state, foldingView, viewNode, masterNode);

    // Copies the changed master Style to the state
    state.setStyle(masterNode.getStyle());
  }

  /**
   * Called by {@link #updateFolderNodeState(FolderNodeState, IFoldingView, INode, INode)} to synchronize all labels, if {@link #isCopyLabels() CopyLabels}
   * is enabled. Also synchronizes all port labels of ports connected to the node.
   * <p>
   * This will adjust the label text property.
   * </p>
   * @param state The node view state whose labels should be synchronized.
   * @param foldingView The folding view.
   * @param viewNode The local node instance.
   * @param masterNode The master node.
   */
  @Override
  protected void synchronizeLabels( FolderNodeState state, IFoldingView foldingView, INode viewNode, INode masterNode ) {

    if (isCopyLabels()) {
      if (masterNode.getLabels().size() > 0 && state.getLabels().size() > 0) {
        for (int i = 0; i < masterNode.getLabels().size(); i++) {
          ILabel masterLabel = masterNode.getLabels().getItem(i);
          FoldingLabelState labelViewState = state.getLabels().getItem(i);
          labelViewState.setText(masterLabel.getText());
          labelViewState.setPreferredSize(masterLabel.getPreferredSize());
          labelViewState.setTag(masterLabel.getTag());
        }
      }

      if (masterNode.getPorts().size() > 0) {
        for (int j = 0; j < masterNode.getPorts().size(); j++) {
          IPort port = masterNode.getPorts().getItem(j);
          if (port.getLabels().size() > 0) {
            for (int i = 0; i < port.getLabels().size(); i++) {
              ILabel masterLabel = port.getLabels().getItem(i);
              FoldingLabelState labelViewState = state.getPorts().getItem(j).getLabels().getItem(i);
              labelViewState.setText(masterLabel.getText());
              labelViewState.setPreferredSize(masterLabel.getPreferredSize());
              labelViewState.setTag(masterLabel.getTag());
            }
          }
        }
      }
    }
  }

  @Override
  protected void initializeFolderNodePorts( FolderNodeState state, IFoldingView foldingView, INode viewNode, INode masterNode ) {
    for (IPort port : viewNode.getPorts()) {
      IPort masterPort = foldingView.getMasterItem(port);
      IPortStyle newStyle = createPortStyle(foldingView, port, masterPort);
      FoldingPortState portState = state.getFoldingPortState(masterPort);
      if (newStyle != null) {
        portState.setStyle(newStyle);
      }
      IPortLocationModelParameter newLocationParameter = createPortLocationParameter(foldingView, port, masterPort);
      if (newLocationParameter != null) {
        portState.setLocationParameter(newLocationParameter);
      }

      if (masterPort.getLabels().size() > 0) {
        for (int i = 0; i < masterPort.getLabels().size(); i++) {
          ILabel label = masterPort.getLabels().getItem(i);
          ILabelStyle labelStyle = createLabelStyle(foldingView, null, label);
          ILabelModelParameter labelLayoutParameter = createLabelLayoutParameter(foldingView, null, label);

          portState.addLabel(label.getText(), labelLayoutParameter != null ? labelLayoutParameter : label.getLayoutParameter(), labelStyle != null ? labelStyle : label.getStyle(), label.getPreferredSize(), label.getTag());
        }
      }

    }
  }

  @Override
  protected void initializeFolderNodeLabels( FolderNodeState state, IFoldingView foldingView, INode viewNode, INode masterNode ) {
    state.clearLabels();
    if (isCopyLabels()) {
      IListEnumerable<ILabel> labels = masterNode.getLabels();
      if (labels.size() > 0) {
        for (int i = 0; i < labels.size(); i++) {
          ILabel label = labels.getItem(i);
          // If the node is a choreographyNode, just copy all Labels
          if (masterNode.getStyle() instanceof ChoreographyNodeStyle) {
            state.addLabel(label.getText(), label.getLayoutParameter(), label.getStyle(), label.getPreferredSize(), label.getTag());
          } else {
            // if subProcessNode, create new Layout & Style
            ILabelStyle labelStyle = createLabelStyle(foldingView, null, label);
            InteriorStretchLabelModel labelModel = new InteriorStretchLabelModel();
            labelModel.setInsets(InsetsD.fromLTRB(3, 3, 3, 3));
            ILabelModelParameter labelLayoutParameter = labelModel.createParameter(InteriorStretchLabelModel.Position.CENTER);
            state.addLabel(label.getText(), labelLayoutParameter, labelStyle != null ? labelStyle : label.getStyle(), label.getPreferredSize(), label.getTag());
          }
        }
      }
    }
  }

}
