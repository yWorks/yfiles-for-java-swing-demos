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
package integration.javafx;

import com.yworks.yfiles.view.input.NodeDropInputMode;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.io.IOException;

/**
 * A {@link com.yworks.yfiles.view.input.NodeDropInputMode} that converts transferred text to an enum constant of {@link
 * NodeTemplate} and drops a node applying that template.
 */
class JavaFxNodeDropInputMode extends NodeDropInputMode {

  private String data;

  public void setData(String data) {
    this.data = data;
  }

  @Override
  protected Object getTransferData(Transferable container, DataFlavor type) {
    if (type.isFlavorTextType()) {
      try {
        // get the text that has been transferred
        String transferData = (String) container.getTransferData(DataFlavor.stringFlavor);
        // if the container doesn't return the transfer data, yet, use the set data instead
        String templateName = transferData != null ? transferData : data;
        // determine the enum constant of NodeTemplate the text is representing
        NodeTemplate template = NodeTemplate.valueOf(templateName);
        // returns the node that applies the template
        return template.node();
      } catch (IllegalArgumentException | UnsupportedFlavorException | IOException e) {
        return null;
      }
    }
    return super.getTransferData(container, type);
  }

  @Override
  protected boolean acceptDrag(DropTargetDragEvent e) {
    boolean acceptDrag = super.acceptDrag(e);
    if (acceptDrag) {
      // if the drag can be accepted, DropTargetDragEvent.acceptDrag has to be called as otherwise no
      // DropTargetDropEvents are dispatched
      e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }
    return acceptDrag;
  }
}
