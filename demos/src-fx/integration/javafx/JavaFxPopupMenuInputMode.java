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
package integration.javafx;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.view.input.AbstractPopupMenuInputMode;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.stage.Window;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * An implementation of {@link com.yworks.yfiles.view.input.AbstractPopupMenuInputMode} interface that will
 * display a {@link javafx.scene.control.ContextMenu JavaFX context menu} when the user right clicks on the {@link
 * com.yworks.yfiles.view.CanvasComponent} or presses the menu key.
 * <p>
 * Note: the JavaFX context menu must be accessed and changed on the JavaFX application thread.
 */
class JavaFxPopupMenuInputMode extends AbstractPopupMenuInputMode<ContextMenu> {
  // the node in whose space the context menu is to appear
  private Node parent;

  /**
   * Initializes a new <code>JavaFxPopupMenuInputMode</code> instance with a node in that the context menu is to
   * appear.
   * @param parent the node in whose space the context menu is to appear
   */
  public JavaFxPopupMenuInputMode(Node parent) {
    this.parent = parent;
  }

  @Override
  protected ContextMenu createMenu() {
    // since there is no way to create the context menu instance synchronously on the
    // JavaFX application thread, therefore we have to do it synchronously and ...
    FutureTask<ContextMenu> task = new FutureTask<>(ContextMenu::new);
    Platform.runLater(task);
    try {
      // ... wait for the result
      return task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  protected void showMenu(ContextMenu contextMenu, PointD viewLocation) {
    // we have to compute the position of the context menu using the view coordinates
    // of the given location, the position of the scene and of the window
    Platform.runLater(() -> {
      Point2D locationToScene = parent.localToScene(viewLocation.getX(), viewLocation.getY());
      Scene scene = parent.getScene();
      Window window = scene.getWindow();
      contextMenu.show(window,
          window.getX() + scene.getX() + locationToScene.getX(),
          window.getY() + scene.getY() + locationToScene.getY());
    });
  }

  @Override
  protected void hideMenu(ContextMenu contextMenu) {
    Platform.runLater(() -> {
      if (contextMenu != null && contextMenu.isShowing()) {
        contextMenu.hide();
      }
    });
  }

  @Override
  protected void clearMenu(ContextMenu contextMenu) {
    Platform.runLater(() -> contextMenu.getItems().clear());
  }

  @Override
  protected boolean isMenuEmpty(ContextMenu contextMenu) {
    // check asynchronously if the context menu is empty on the JavaFX application thread
    FutureTask<Boolean> task = new FutureTask<>(() -> contextMenu.getItems().isEmpty());
    Platform.runLater(task);
    try {
      // wait and return the result
      return task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  protected void setMenuClosedCallback(ContextMenu contextMenu, Runnable menuClosedCallback) {
    Platform.runLater(() -> {
      if (menuClosedCallback != null) {
        contextMenu.setOnHidden(event -> menuClosedCallback.run());
      } else {
        contextMenu.setOnHidden(null);
      }
    });
  }
}
