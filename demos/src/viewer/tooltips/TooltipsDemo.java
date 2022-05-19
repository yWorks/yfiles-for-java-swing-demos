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
package viewer.tooltips;

import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.utils.IEventListener;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.MouseHoverInputMode;
import com.yworks.yfiles.view.input.QueryItemToolTipEventArgs;
import com.yworks.yfiles.view.input.ToolTipQueryEventArgs;
import toolkit.AbstractDemo;

import javax.swing.JToolBar;
import java.awt.EventQueue;

/**
 * This demo shows how to add tooltips to graph items.
 */
public class TooltipsDemo extends AbstractDemo {

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    // configure the interaction
    GraphEditorInputMode geim = initializeInputMode();

    // initializes the tooltips
    initializeTooltips(geim);

    // load a sample graph
    loadGraph();

    // enable undoability
    enableUndo();
  }

  /**
   * Dynamic tooltips are managed by {@link MouseHoverInputMode} that dispatches
   * {@link MouseHoverInputMode#addQueryToolTipListener QueryToolTip} events for
   * hovered {@link ToolTipQueryEventArgs#getQueryLocation() locations} (in world coordinates)
   * and displays the {@link ToolTipQueryEventArgs#setToolTip(String) tooltip set on the event args}.
   * <p>
   * The {@link ToolTipQueryEventArgs#setHandled(boolean) Handled} property of the event args is a flag which indicates
   * whether the tooltip was already set by one of possibly several tooltip providers.
   * </p>
   * <p>
   * To simplify displaying tooltips for {@link IModelItem}s, {@link GraphEditorInputMode}
   * listens to the original event, checks for an {@link IModelItem} at the query location
   * and dispatches its {@link GraphEditorInputMode#addQueryItemToolTipListener(IEventListener) QueryItemToolTip}
   * event that not only contains the Handled, QueryLocation, and ToolTip properties, but also the
   * hit {@link QueryItemToolTipEventArgs#getItem() Item}.
   * </p>
   * @param geim The input mode that is used to edit the graph.
   */
  private void initializeTooltips(GraphEditorInputMode geim) {
    // register a listener that sets a tooltip for a hovered item
    geim.addQueryItemToolTipListener((source, args) -> {
      if (args.isHandled()) {
        // a tooltip has already been assigned -> nothing to do
        return;
      }

      // creates a text for the tooltips
      String title = "";
      if (args.getItem() instanceof INode) {
        title = "Node Tooltip";
      } else if (args.getItem() instanceof IEdge) {
        title = "Edge Tooltip";
      } else if (args.getItem() instanceof IPort) {
        title = "Port Tooltip";
      } else if (args.getItem() instanceof ILabel) {
        title = "Label Tooltip";
      }

      // extends the text with label information if available
      String label = "";
      IModelItem item = args.getItem();
      if (item instanceof INode || item instanceof IEdge || item instanceof IPort) {
        if (((ILabelOwner) item).getLabels().size() > 0) {
          label = ((ILabelOwner) item).getLabels().first().getText();
        }
      } else if (item instanceof ILabel) {
        label = ((ILabel) item).getText();
      }

      // use some HTML to format the tooltip
      args.setToolTip("<html>" +
          "<p><b>" + title + "</b></p>" +
          "<p>" + label + "</p>" +
          "</html>");

      // indicate that the tooltip has been set
      args.setHandled(true);
    });
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
   * Initializes the input mode for the demo.
   */
  private GraphEditorInputMode initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.setGroupingOperationsAllowed(true);
    graphComponent.setInputMode(geim);
    return geim;
  }

  /**
   * Configures the toolbar with according buttons and functionality.
   */
  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Cut", "cut-16.png", ICommand.CUT, null, graphComponent));
    toolBar.add(createCommandButtonAction("Copy", "copy-16.png", ICommand.COPY, null, graphComponent));
    toolBar.add(createCommandButtonAction("Paste", "paste-16.png", ICommand.PASTE, null, graphComponent));
    toolBar.add(createCommandButtonAction("Delete", "delete2-16.png", ICommand.DELETE, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png", ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Group selected elements", "group-16.png", ICommand.GROUP_SELECTION, null, graphComponent));
    toolBar.add(createCommandButtonAction("Ungroup selected elements", "ungroup-16.png", ICommand.UNGROUP_SELECTION, null, graphComponent));
  }

  /**
   * Enables undo functionality.
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

  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new TooltipsDemo().start();
    });
  }
}
