/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package databinding.interactivenodesgraphbuilder;

import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.IEventListener;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.Mouse2DEventArgs;
import com.yworks.yfiles.view.input.AbstractInputMode;
import com.yworks.yfiles.view.input.ConcurrencyController;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeHitTester;
import toolkit.DragAndDropSupport;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * A custom input mode that starts a drag and drag operation when dragging from
 * a node.
 */
class NodeDragInputMode extends AbstractInputMode {
  private final IEventListener<Mouse2DEventArgs> pressedHandler;
  private final IEventListener<Mouse2DEventArgs> releasedHandler;
  private final IEventListener<Mouse2DEventArgs> draggedHandler;
  private final Class<?> itemType;

  /**
   * Initializes a new {@code NodeDragInputMode} instance for the given
   * business data item type.
   */
  NodeDragInputMode( Class<?> itemType ) {
    this.itemType = itemType;
    pressedHandler = this::canvasOnMouse2DPressed;
    releasedHandler = this::canvasOnMouse2DReleased;
    draggedHandler = this::canvasOnMouse2DDragged;
    addCanceledListener((source, args) -> cleanUp());
    addStoppedListener((source, args) -> cleanUp());
  }

  /**
   * Registers mouse listeners for starting Drag and Drop operations and
   * transfer handlers for transferring business data.
   */
  @Override
  public void install( IInputModeContext context, ConcurrencyController controller ) {
    super.install(context, controller);

    CanvasComponent canvas = context.getCanvasComponent();
    canvas.addMouse2DPressedListener(pressedHandler);
    canvas.addMouse2DReleasedListener(releasedHandler);
    canvas.addMouse2DDraggedListener(draggedHandler);

    canvas.setTransferHandler(new DragHandler(itemType));
  }

  /**
   * Removes the previously registered mouse listeners and transfer handlers.
   */
  @Override
  public void uninstall( IInputModeContext context ) {
    CanvasComponent canvas = context.getCanvasComponent();
    canvas.setTransferHandler(null);

    canvas.removeMouse2DDraggedListener(draggedHandler);
    canvas.removeMouse2DReleasedListener(releasedHandler);
    canvas.removeMouse2DPressedListener(pressedHandler);

    super.uninstall(context);
  }

  /**
   * Clears the internal state of the registered transfer handler.
   */
  @Override
  protected void onConcurrencyControllerDeactivated() {
    cleanUp();
    super.onConcurrencyControllerDeactivated();
  }

  /**
   * Prepares the internal state of the registered transfer handler for an
   * impeding Drag and Drop operation start. More specifically, this method
   * checks if the mouse was pressed on a graph node and extract the node's
   * associated business data from its {@code tag} property.
   */
  void canvasOnMouse2DPressed( Object source, Mouse2DEventArgs args ) {
    // get hitTester from context
    IInputModeContext context = getInputModeContext();
    INodeHitTester hitTest = context.lookup(INodeHitTester.class);

    // take first node
    INode node = first(hitTest.enumerateHits(context, args.getLocation()));
    if (node == null) {
      return;
    }
    Object tag = node.getTag();
    if (!itemType.isInstance(tag)) {
      return;
    }

    DragHandler handler = getTransferHandler((CanvasComponent) source);
    if (handler != null) {
      handler.setTransferData(tag);
    }
  }

  /**
   * Clears the internal state of the registered transfer handler.
   */
  void canvasOnMouse2DReleased( Object source, Mouse2DEventArgs args ) {
    cancel();
  }

  /**
   * Starts a Drag and Drop drag operation if the registered transfer handler
   * currently stores some business data for transfer.
   */
  void canvasOnMouse2DDragged( Object source, Mouse2DEventArgs args ) {
    // determine if there is an appropriate transfer handler
    CanvasComponent canvas = (CanvasComponent) source;
    DragHandler handler = getTransferHandler(canvas);

    // check if the transfer handler currently stores some business data
    Object data = handler == null ? null : handler.getTransferData();
    if (data == null) {
      return;
    }

    // abort if the mouse event is a "fake" event
    MouseEvent me = args.getOriginalEvent();
    if (me == null) {
      return;
    }

    // start the Drag and Drop drag operation if no other input mode is handling
    // the current mouse event
    if (canRequestMutex()) {
      requestMutex();

      handler.exportAsDrag(canvas, me);

      cleanUp();
      releaseMutex();
    }
  }

  /**
   * Clears the internal state of the registered transfer handler.
   */
  void cleanUp() {
    CanvasComponent canvas = getInputModeContext().getCanvasComponent();
    DragHandler handler = getTransferHandler(canvas);
    if (handler != null) {
      handler.setTransferData(null);
    }
  }

  /**
   * Returns the first item of the given enumerable or {@code null} if there
   * are no items.
   */
  private static <T> T first( IEnumerable<T> enumerable ) {
    Iterator<T> it = enumerable.iterator();
    return it.hasNext() ? it.next() : null;
  }

  /**
   * Retrieves the transfer handler registered with the given canvas component
   * or {@code null} if no appropriate transfer handler is available.
   */
  private static DragHandler getTransferHandler( CanvasComponent canvas ) {
    TransferHandler handler = canvas.getTransferHandler();
    if (handler instanceof DragHandler) {
      return (DragHandler) handler;
    } else {
      return null;
    }
  }


  /**
   * Creates transferable wrappers for business data.
   */
  private static final class DragHandler extends TransferHandler {
    static final int SUPPORTED_ACTION = DnDConstants.ACTION_COPY;

    private final DataFlavor dataFlavor;
    /** The business data item to be transferred. */
    private Object data;

    /**
     * Initializes a new {@code DragHandler} instance for the given business
     * data item type.
     */
    DragHandler( Class<?> itemType ) {
      dataFlavor = DragAndDropSupport.newFlavor(itemType);
    }

    /**
     * Creates a transferable for the currently stored business data.
     */
    @Override
    protected Transferable createTransferable( JComponent c ) {
      return data == null ? null : DragAndDropSupport.newTransferable(dataFlavor, data);
    }

    @Override
    public int getSourceActions( JComponent c ) {
      // In theory all Drag and Drop action types are supported.
      // However, the demo application decides on what to do with the data
      // (i.e. copy or remove) depending on drop location and not on Drag and
      // Drop action type. Thus it does not matter *for the demo's purposes*
      // which action is used (as long as it is a generally valid action type).
      return SUPPORTED_ACTION;
    }

    void exportAsDrag( CanvasComponent canvas, MouseEvent me ) {
      // Strctly speaking the Drag and Drop action type would have to be
      // inferred from the modifiers of the given mouse event.
      // However, the demo application decides on what to do with the data
      // (i.e. copy or remove) depending on drop location and not on Drag and
      // Drop action type. Thus it does not matter *for the demo's purposes*
      // which action is used (as long as it is a generally valid action type).
      super.exportAsDrag(canvas, me, SUPPORTED_ACTION);
    }

    /**
     * Returns the currently stored business data.
     */
    Object getTransferData() {
      return data;
    }

    /**
     * Sets the currently stored business data.
     */
    void setTransferData( Object data ) {
      this.data = data;
    }
  }
}
