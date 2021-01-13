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
package complete.rotatablenodes;

import com.yworks.yfiles.graph.IClipboardHelper;
import com.yworks.yfiles.graph.IGraphClipboardContext;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;


/**
 * Helper class to support clipboard operations for rotatable nodes.
 */
public class RotatableNodeClipboardHelper implements IClipboardHelper {

  /**
   * Returns whether or not copying the given item is possible.
   */
  @Override
  public boolean shouldCopy(IGraphClipboardContext context, IModelItem item) {
    return true;
  }

  /**
   * Returns whether or not cutting the given item is possible.
   */
  @Override
  public boolean shouldCut(IGraphClipboardContext context, IModelItem item) {
    return true;
  }

  /**
   * Returns whether or not pasting the given item is possible.
   */
  @Override
  public boolean shouldPaste(IGraphClipboardContext context, IModelItem item, Object userData) {
    return false;
  }

  /**
   * Adds no additional state to the copy-operation.
   */
  @Override
  public Object copy(IGraphClipboardContext context, IModelItem item) {
    return null;
  }

  /**
   * Adds no additional state to the cut-operation.
   */
  @Override
  public Object cut(IGraphClipboardContext context, IModelItem item) {
    return null;
  }

  /**
   * Copies the node style for the paste-operation because {@link RotatableNodeStyleDecorator} should
   * not be shared.
   */
  @Override
  public void paste(IGraphClipboardContext context, IModelItem item, Object userData) {
    if (!(item instanceof  INode)) {
      return;
    }

    INode node = ((INode) item);

    if (node.getStyle() instanceof RotatableNodeStyleDecorator) {
      RotatableNodeStyleDecorator styleWrapper = (RotatableNodeStyleDecorator) node.getStyle();
      if (context.getTargetGraph().getFoldingView() != null) {
        context.getTargetGraph().getFoldingView().getManager().getMasterGraph()
                .setStyle(node, styleWrapper.clone());
      } else {
        context.getTargetGraph().setStyle(node, styleWrapper.clone());
      }
    }
  }
}
