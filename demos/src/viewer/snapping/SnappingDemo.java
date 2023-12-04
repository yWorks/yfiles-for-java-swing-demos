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
package viewer.snapping;

import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.LabelSnapContext;
import toolkit.AbstractDemo;

import java.awt.EventQueue;
import javax.swing.JToolBar;

/**
 * Demonstrates how to enable snapping functionality for graph elements.
 */
public class SnappingDemo extends AbstractDemo {

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    // enable snapping for labels and other graph items
    initializeSnapping();

    // loads a sample graph
    loadGraph();

    // enable undoability
    enableUndo();
  }

  /**
   * Initializes snapping for labels and other graph items. The default snapping behavior can easily be enabled by
   * setting a properly configured snap context. A snap context provides many options to fine tune its behavior.
   * Please see the documentation of {@link GraphSnapContext} and {@link LabelSnapContext} for more information.
   */
  private void initializeSnapping() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    graphComponent.setInputMode(geim);

    // enables snapping all items except labels
    geim.setSnapContext(new GraphSnapContext());
    // enables snapping for labels
    geim.setLabelSnapContext(new LabelSnapContext());

    // enables grouping operations such as grouping selected nodes moving nodes
    // into group nodes
    geim.setGroupingOperationsAllowed(true);
  }

  /**
   * Loads an initial sample graph.
   */
  private void loadGraph() {
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Adds controls for cut/copy/paste actions, undo/redo actions, and
   * grouping actions to the demo tool bar.
   */
  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    //adds buttons for adjusting the view
    super.configureToolBar(toolBar);

    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction(
            "Cut", "cut-16.png", ICommand.CUT, null, graphComponent));
    toolBar.add(createCommandButtonAction(
            "Copy", "copy-16.png", ICommand.COPY, null, graphComponent));
    toolBar.add(createCommandButtonAction(
            "Paste", "paste-16.png", ICommand.PASTE, null, graphComponent));
    toolBar.add(createCommandButtonAction(
            "Delete", "delete2-16.png", ICommand.DELETE, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction(
            "Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction(
            "Redo", "redo-16.png", ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction(
            "Group selected elements", "group-16.png", ICommand.GROUP_SELECTION, null, graphComponent));
    toolBar.add(createCommandButtonAction(
            "Ungroup selected elements", "ungroup-16.png", ICommand.UNGROUP_SELECTION, null, graphComponent));
  }

  /**
   * Enables undo and redo functionality.
   */
  private void enableUndo() {
    graphComponent.getGraph().setUndoEngineEnabled(true);
  }

  /**
   * Adjusts the view port.
   */
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main( final String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new SnappingDemo().start();
    });
  }
}
