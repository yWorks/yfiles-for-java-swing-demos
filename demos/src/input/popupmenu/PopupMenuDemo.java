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
package input.popupmenu;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.PopulateItemPopupMenuEventArgs;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.view.input.CommandAction;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import java.awt.EventQueue;

/**
 * Implement a dynamic popup menu for the nodes and for the background of a {@link com.yworks.yfiles.view.GraphComponent}.
 */
public class PopupMenuDemo extends AbstractDemo {

  private Action cutAction;
  private Action copyAction;
  private Action pasteAction;
  private Action deleteAction;
  private Action selectAllAction;

  /**
   * Initializes a new <code>PopupMenuDemo</code> instance.
   */
  public PopupMenuDemo() {
    // create actions that are dispatched when selecting popup menu items
    cutAction = createCommandMenuItemAction("Cut", ICommand.CUT, null, graphComponent);
    copyAction = createCommandMenuItemAction("Copy", ICommand.COPY, null, graphComponent);
    pasteAction = createCommandMenuItemAction("Paste", ICommand.PASTE, null, graphComponent);
    deleteAction = createCommandMenuItemAction("Delete", ICommand.DELETE, null, graphComponent);
    selectAllAction = createCommandMenuItemAction("Select all", ICommand.SELECT_ALL, null, graphComponent);
  }

  public void initialize() {
    // create a sample graph that contains just 3 simple nodes which can be selected and right clicked to show a popup menu
    IGraph graph = graphComponent.getGraph();
    DemoStyles.initDemoStyles(graph);
    graph.getNodeDefaults().setSize(new SizeD(40, 40));
    graph.addLabel(graph.createNode(new PointD(100, 100)), "1");
    graph.addLabel(graph.createNode(new PointD(200, 100)), "2");
    graph.addLabel(graph.createNode(new PointD(300, 100)), "3");

    // register the GraphEditorInputMode as default input mode
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    graphComponent.setInputMode(inputMode);

    // specify nodes as the types of the items that should be queried a popup menu for
    inputMode.setPopupMenuItems(GraphItemTypes.NODE);
    // simple implementations with static popup menus could just assign a popup menu here:
    // inputMode.getPopupMenuInputMode().setMenu(menu);
    // however, we use a more dynamic popup menu:
    // register an event handler that is called when a popup menu is about to be shown
    inputMode.addPopulateItemPopupMenuListener(this::onPopulateItemPopupMenu);
  }

  /**
   * Called when a popup menu is about to be shown and has to be populated. Here it is possible
   * to generate different popup menus depending on the situation. In our case we create one popup menu if at least
   * one node has been hit, and another if an empty spot on the canvas has been hit.
   */
  private void onPopulateItemPopupMenu(Object source, PopulateItemPopupMenuEventArgs<IModelItem> args){
    INode node = args.getItem() instanceof INode ? (INode) args.getItem() : null;

    // select the node that was hit or clear the selection if no node has been hit
    updateSelection(node);

    // The return type of the following method is Object to be able to support popup menus of different Java GUI
    // toolkits (see the JavaFxDemo). By default this is an instance of Swing's JPopupMenu.
    JPopupMenu popupMenu = (JPopupMenu) args.getMenu();

    // depending on the number of selected nodes populate the context menu ...
    if (graphComponent.getSelection().getSelectedNodes().size() > 0) {
      // at least one node is selected so populate the popup menu with the menu items defined above
      popupMenu.add(cutAction);
      popupMenu.add(copyAction);
      popupMenu.add(deleteAction);
    } else {
      // no nodes are selected
      popupMenu.add(selectAllAction);
      // items shall be pasted at the popup query location, so we use this location as parameter of the PASTE
      ((CommandAction) pasteAction).setParameter(args.getQueryLocation());
      popupMenu.add(pasteAction);
    }

    // make the menu show
    args.setShowingMenuRequested(true);
    // and mark the event as handled
    args.setHandled(true);
  }

  /**
   * Helper method that updates the node selection state when the popup menu is opened on a node.
   * @param node The node or <code>null</code>.
   */
  private void updateSelection(INode node) {
    // see if no node was hit
    if (node == null) {
      // clear the whole selection
      graphComponent.getSelection().clear();
    } else {
      // see if the node was selected already
      if (!graphComponent.getSelection().getSelectedNodes().isSelected(node)) {
        // no - clear the remaining selection
        graphComponent.getSelection().clear();
        // and select the node
        graphComponent.getSelection().getSelectedNodes().setSelected(node, true);
        // also update the current item
        graphComponent.setCurrentItem(node);
      }
    }
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new PopupMenuDemo().start();
    });
  }
}
