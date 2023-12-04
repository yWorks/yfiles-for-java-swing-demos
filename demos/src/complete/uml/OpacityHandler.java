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
package complete.uml;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.IEventListener;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.Mouse2DEventArgs;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeHitTester;

import java.util.Iterator;

/**
 * Shows and hides the add and remove buttons for UML attributes and UML
 * operations in the UML class node that is below the mouse cursor.
 */
class OpacityHandler implements IEventListener<Mouse2DEventArgs> {
  private INode last;

  /**
   * Shows and hides the add and remove buttons depending on mouse location.
   */
  @Override
  public void onEvent( Object source, Mouse2DEventArgs args ) {
    switch (args.getEventType()) {
      case MOVED:
        onMoved(source, args);
        break;
      case EXITED:
        onExited(source, args);
        break;
    }
  }

  private void onMoved( Object source, Mouse2DEventArgs args ) {
    GraphComponent graphComponent = (GraphComponent) source;
    IInputModeContext ctx = graphComponent.getInputModeContext();
    INodeHitTester tester = ctx.lookup(INodeHitTester.class);
    if (tester == null) {
      return;
    }

    IEnumerable<INode> nodes = tester.enumerateHits(ctx, args.getLocation());
    if (nodes == null) {
      return;
    }

    Iterator<INode> it = nodes.iterator();
    if (it.hasNext()) {
      INode node = it.next();
      if (node != last) {
        last = node;
        OpacityProvider.INSTANCE.setOpacity(node, 1);
        graphComponent.repaint();
      }
    } else {
      clear(graphComponent);
    }
  }

  private void onExited( Object source, Mouse2DEventArgs args ) {
    clear((GraphComponent) source);
  }

  private void clear( GraphComponent graphComponent ) {
    INode node = last;
    if (node != null) {
      last = null;
      OpacityProvider.INSTANCE.setOpacity(node, 0);
      graphComponent.repaint();
    }
  }
}
