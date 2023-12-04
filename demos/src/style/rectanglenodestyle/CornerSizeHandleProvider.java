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
package style.rectanglenodestyle;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IHandleProvider;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.util.ArrayList;

/**
 * An {@link IHandleProvider} for nodes using a {@link RectangleNodeStyle} that provides a single
 * {@link CornerSizeHandle} to change the {@link RectangleNodeStyle#getCornerSize()} of the node
 * style interactively.
 */
public class CornerSizeHandleProvider implements IHandleProvider {
  private final INode node;
  private final Runnable cornerSizeChanged;
  private final IHandleProvider delegateProvider;

  public CornerSizeHandleProvider(INode node, Runnable cornerSizeChanged, IHandleProvider delegateProvider) {
    this.node = node;
    this.cornerSizeChanged = cornerSizeChanged;
    this.delegateProvider = delegateProvider;
  }

  @Override
  public Iterable<IHandle> getHandles(IInputModeContext context) {
    ArrayList<IHandle> result = new ArrayList<>();
    if (delegateProvider != null) {
      delegateProvider.getHandles(context).forEach(result::add);
    }
    if (node.getStyle() instanceof RectangleNodeStyle) {
      result.add(new CornerSizeHandle(node, cornerSizeChanged));
    }
    return result;
  }
}