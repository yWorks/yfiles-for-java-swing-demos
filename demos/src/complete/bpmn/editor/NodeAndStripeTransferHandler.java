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
package complete.bpmn.editor;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IStripe;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.styles.INodeStyle;
import toolkit.DragAndDropSupport;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * Transfers {@link com.yworks.yfiles.graph.INode} and
 * {@link com.yworks.yfiles.graph.IStripe} instances from a
 * {@link javax.swing.JList} to another Swing component in the same JVM.
 */
class NodeAndStripeTransferHandler extends TransferHandler {
  /**
   * Data flavor the represents {@link com.yworks.yfiles.graph.INode}
   * instances.
   */
  private DataFlavor nodeFlavor;
  /**
   * Data flavor the represents {@link com.yworks.yfiles.graph.IStripe}
   * instances.
   */
  private DataFlavor stripeFlavor;

  /**
   * Initializes a new <code>MyTransferHandler</code> instance for
   * {@link com.yworks.yfiles.graph.INode} and
   * {@link com.yworks.yfiles.graph.IStripe} instances.
   */
  NodeAndStripeTransferHandler() {
    super("selectedValue");
    nodeFlavor = DragAndDropSupport.newFlavor(INode.class);
    stripeFlavor = DragAndDropSupport.newFlavor(IStripe.class);
  }

  /**
   * Creates a {@link java.awt.datatransfer.Transferable} instance for the
   * selected value from the given {@link javax.swing.JList}.
   * This method assumes that all values in the {@link javax.swing.JList}
   * are of type {@link INode}.
   */
  @Override
  protected Transferable createTransferable( JComponent c ) {
    INode value = (INode) ((JList) c).getSelectedValue();
    Object tag = value.getTag();
    if (tag instanceof IStripe) {
      // stripeFlavor will trigger com.yworks.yfiles.view.input.StripeDropInputMode
      return DragAndDropSupport.newTransferable(stripeFlavor, tag);
    } else {
      // we use a copy of the node since the style should not be shared
      SimpleNode node = new SimpleNode();
      node.setLayout(value.getLayout());
      node.setStyle((INodeStyle) value.getStyle().clone());
      node.setTag(value.getTag());
      // nodeFlavor will trigger com.yworks.yfiles.view.input.NodeDropInputMode
      return DragAndDropSupport.newTransferable(nodeFlavor, node);
    }
  }
}
