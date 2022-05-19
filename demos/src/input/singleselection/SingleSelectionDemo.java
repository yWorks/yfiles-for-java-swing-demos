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
package input.singleselection;

import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.view.input.KeyboardInputModeBinding;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * Configure {@link com.yworks.yfiles.view.input.GraphEditorInputMode} to enable single selection
 * mode for interaction.
 * <p>
 * All default gestures that result in more than one item selected at a time are either switched
 * off or changed so only one item gets selected. This requires some configuration that is done in
 * {@link #enableSingleSelection(boolean)}. This method also restores the default selection behavior if
 * single selection is disabled.
 * </p>
 */
public class SingleSelectionDemo extends AbstractDemo {
  // node locations
  private final double[] nodeLocationsX = new double[]{317, 291, 220, 246, 221, 150, 142, 213, 232, 71, 0};
  private final double[] nodeLocationsY = new double[]{87, 2, 0, 73, 144, 180, 251, 286, 215, 285, 320};

  // the previously set multi-selection recognizer
  private IEventRecognizer oldMultiSelectionRecognizer;

  // the status of the single-selection
  private boolean singleSelectionEnabled;
  private GraphItemTypes oldPasteItems;

  // custom command binding for 'toggle item selection'
  private KeyboardInputModeBinding customToggleSelectionBinding;

  /**
   * Initializes this demo by configuring the graph defaults and the input mode, enabling single selection
   * and loading the sample graph.
   */
  public void initialize() {
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Colors.DARK_ORANGE);
    graphComponent.getGraph().getNodeDefaults().setStyle(nodeStyle);
    GraphEditorInputMode mode = new GraphEditorInputMode();
    graphComponent.setInputMode(mode);
    oldPasteItems = mode.getPasteSelectableItems();
    // initially enable single selection
    enableSingleSelection(true);

    // create a sample graph
    createSampleGraph();
  }

  /**
   * Centers the graph in the graph component.
   */
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);

    // add the single selection toggle button to the tool bar
    JToggleButton singleSelectionButton = new JToggleButton(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JToggleButton) {
          enableSingleSelection(((JToggleButton) e.getSource()).isSelected());
        }
      }
    });
    singleSelectionButton.setText("Single Selection Mode");

    // initially enable the button
    singleSelectionButton.setSelected(true);
    toolBar.add(singleSelectionButton);
  }

  private void createSampleGraph() {
    IGraph graph = graphComponent.getGraph();
    INode[] nodes = new INode[nodeLocationsX.length];
    for (int i = 0; i < nodes.length; i++) {
      nodes[i] = graph.createNode(new RectD(nodeLocationsX[i], nodeLocationsY[i], 30, 30));
    }

    graph.createEdge(nodes[2], nodes[1]);
    graph.createEdge(nodes[1], nodes[0]);
    graph.createEdge(nodes[0], nodes[3]);
    graph.createEdge(nodes[3], nodes[2]);
    graph.createEdge(nodes[3], nodes[1]);
    graph.createEdge(nodes[4], nodes[3]);
    graph.createEdge(nodes[4], nodes[5]);
    graph.createEdge(nodes[8], nodes[4]);
    graph.createEdge(nodes[7], nodes[8]);
    graph.createEdge(nodes[7], nodes[6]);
    graph.createEdge(nodes[6], nodes[5]);
    graph.createEdge(nodes[6], nodes[9]);
    graph.createEdge(nodes[9], nodes[10]);
  }

  /**
   * Enables or disables the single selection feature.
   */
  private void enableSingleSelection(boolean enable) {
    if (enable == this.singleSelectionEnabled) {
      return;
    }

    this.singleSelectionEnabled = enable;
    GraphEditorInputMode mode = (GraphEditorInputMode) graphComponent.getInputMode();
    if (enable) {
      // remember old recognizer so we can restore it later
      oldMultiSelectionRecognizer = mode.getMultiSelectionRecognizer();

      // disable marquee selection
      mode.getMarqueeSelectionInputMode().setEnabled(false);
      // disable multi selection with Ctrl-Click
      mode.setMultiSelectionRecognizer(IEventRecognizer.NEVER);

      // deactivate command that can lead to multi selection
      mode.getAvailableCommands().remove(ICommand.SELECT_ALL);

      // remove the default commands that are responsible to extend the selection via keyboard
      Collection<ICommand> cmds = mode.getNavigationInputMode().getAvailableCommands();
      cmds.remove(ICommand.EXTEND_SELECTION_LEFT);
      cmds.remove(ICommand.EXTEND_SELECTION_RIGHT);
      cmds.remove(ICommand.EXTEND_SELECTION_UP);
      cmds.remove(ICommand.EXTEND_SELECTION_DOWN);

      // add custom binding for toggle item selection
      customToggleSelectionBinding = mode.getKeyboardInputMode().addCommandBinding(ICommand.TOGGLE_ITEM_SELECTION,
          this::executedToggleItemSelection, this::canExecuteToggleItemSelection);

      //Disable selection of (possibly multiple) items
      oldPasteItems = mode.getPasteSelectableItems();
      mode.setPasteSelectableItems(GraphItemTypes.NONE);
      //Also clear the selection - even though the setup works when more than one item is selected, it looks a bit strange
      graphComponent.getSelection().clear();
    } else {
      // restore old settings
      mode.getMarqueeSelectionInputMode().setEnabled(true);
      mode.setMultiSelectionRecognizer(oldMultiSelectionRecognizer);
      mode.setPasteSelectableItems(oldPasteItems);

      // re-activate command
      mode.getAvailableCommands().add(ICommand.SELECT_ALL);

      // re-insert the default commands to extend the selection
      Collection<ICommand> cmds = mode.getNavigationInputMode().getAvailableCommands();
      cmds.add(ICommand.EXTEND_SELECTION_LEFT);
      cmds.add(ICommand.EXTEND_SELECTION_RIGHT);
      cmds.add(ICommand.EXTEND_SELECTION_UP);
      cmds.add(ICommand.EXTEND_SELECTION_DOWN);

      // remove custom binding for toggle item selection
      customToggleSelectionBinding.remove();
    }
  }

  /**
   * Checks whether an <code>IModelItem</code> can be determined whose selection state can be toggled.
   */
  private boolean canExecuteToggleItemSelection(ICommand command, Object parameter, Object source) {
    // if we have an item, the command can be executed
    IModelItem modelItem = parameter instanceof IModelItem ? (IModelItem) parameter : graphComponent.getCurrentItem();
    return modelItem != null;
  }

  /**
   * Custom command handler that allows toggling the selection state of an item respecting the single selection policy.
   */
  private boolean executedToggleItemSelection(ICommand command, Object parameter, Object source) {
    // get the item
    IModelItem modelItem = parameter instanceof IModelItem ? (IModelItem) parameter : graphComponent.getCurrentItem();
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphComponent.getInputMode();

    // check if it is allowed to be selected
    if (modelItem != null &&
        graphComponent.getGraph().contains(modelItem) &&
        inputMode.getSelectableItems().is(modelItem)) {
      boolean isSelected = inputMode.getGraphSelection().isSelected(modelItem);
      if (isSelected) {
        // the item is selected and needs to be unselected - just clear the selection
        inputMode.getGraphSelection().clear();
      } else {
        // the item is not selected - deselect all other items and select the currentItem
        inputMode.getGraphSelection().clear();
        inputMode.setSelected(modelItem, true);
      }
      return true;
    }
    return false;
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new SingleSelectionDemo().start();
    });
  }
}
