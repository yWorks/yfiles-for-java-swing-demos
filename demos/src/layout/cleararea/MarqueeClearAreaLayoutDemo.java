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
package layout.cleararea;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.RectangleVisualTemplate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.INodeHitTester;
import com.yworks.yfiles.view.input.MarqueeSelectionEventArgs;
import com.yworks.yfiles.view.input.MarqueeSelectionInputMode;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.Locale;

/**
 * Shows how to interactively move graph elements around a marquee rectangle in
 * a given graph layout so that the modifications in the graph are minimal.
 */
public class MarqueeClearAreaLayoutDemo extends AbstractDemo {

  /**
   * Performs layout and animation while dragging the marquee.
   */
  private ClearAreaLayoutHelper layoutHelper;

  /**
   * The marquee rectangle used to mark the area to clear
   */
  private RectangleVisualTemplate marqueeRectangle;


  private boolean layoutRunning;

  /**
   * Adds standard controls like as undo/redo etc.
   */
  @Override
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);

    toolBar.addSeparator();

    toolBar.add(createCommandButtonAction(
            "Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction(
            "Redo", "redo-16.png", ICommand.REDO, null, graphComponent));

    toolBar.addSeparator();

    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        layoutHierarchical();
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Apply Hierarchical Graph Layout");
    action.putValue(Action.SMALL_ICON, createIcon("layout-hierarchic.png"));
    toolBar.add(action);

  }


  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    marqueeRectangle = new RectangleVisualTemplate();
    marqueeRectangle.setFill(new Color(170, 100, 150, 240));
    marqueeRectangle.setPen(Pen.getDarkRed());
    graphComponent.getGraph().setUndoEngineEnabled(true);

    initializeInputModes();

    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.orange);
    graphComponent.getGraph().getNodeDefaults().setStyle(nodeStyle);

    loadGraph();
  }


  /**
   * Registers the {@link GraphEditorInputMode} as the {@link GraphComponent}s input mode
   * and initializes the marquee input mode that clears the area of the marquee rectangle.
   */
  private void initializeInputModes() {
    // enable undo/redo support
    graphComponent.getGraph().setUndoEngineEnabled(true);

    // create an input mode to edit graphs
    GraphEditorInputMode editMode = new GraphEditorInputMode();

    // create an input mode to clear the area of a marquee rectangle
    // using the right mouse button
    MarqueeSelectionInputMode marqueeClearInputMode = new MarqueeSelectionInputMode();

    marqueeClearInputMode.setPressedRecognizer(IEventRecognizer.MOUSE_RIGHT_PRESSED);
    marqueeClearInputMode.setDraggedRecognizer(IEventRecognizer.MOUSE_RIGHT_DRAGGED);
    marqueeClearInputMode.setReleasedRecognizer(IEventRecognizer.MOUSE_RIGHT_RELEASED);
    marqueeClearInputMode.setCancelRecognizer(IEventRecognizer.ESCAPE_PRESSED.or(IEventRecognizer.MOUSE_LOST_CAPTURE_DURING_DRAG));
    marqueeClearInputMode.setTemplate(marqueeRectangle);

    // handle dragging the marquee
    marqueeClearInputMode.addDragStartingListener(this::onDragStarting);
    marqueeClearInputMode.addDraggedListener(this::onDragged);
    marqueeClearInputMode.addDragCanceledListener(this::onDragCanceled);
    marqueeClearInputMode.addDragFinishedListener(this::onDragFinished);

    // add this mode to the edit mode
    editMode.add(marqueeClearInputMode);

    // and install the edit mode into the canvas
    graphComponent.setInputMode(editMode);
  }

  /**
   * Dragging the the marquee rectangle is starting
   */
  private void onDragStarting(Object sender, MarqueeSelectionEventArgs e) {
    INode hitGroupNode = getHitGroupNode(e.getContext(), e.getContext().getCanvasComponent().getLastEventLocation());

    layoutHelper = new ClearAreaLayoutHelper(graphComponent);
    layoutHelper.setClearRectangle(e.getRectangle());
    layoutHelper.setGroupNode(hitGroupNode);

    layoutHelper.initializeLayout();
  }

  /**
   * The marquee rectangle is currently dragged.
   * For each drag a new layout is calculated and applied if the previous one is completed.
   */
  private void onDragged(Object sender, MarqueeSelectionEventArgs e) {
    layoutHelper.setClearRectangle(e.getRectangle());
    layoutHelper.runLayout();
  }

  /**
   * Dragging the marquee rectangle has been canceled so
   * the state before the gesture must be restored.
   */
  private void onDragCanceled(Object sender, MarqueeSelectionEventArgs e) {
    layoutHelper.setClearRectangle(e.getRectangle());
    layoutHelper.cancelLayout();
  }

  /**
   * Dragging the marquee rectangle has been finished so
   * we execute the layout with the final rectangle.
   */
  private void onDragFinished(Object sender, MarqueeSelectionEventArgs e) {
    layoutHelper.setClearRectangle(e.getRectangle());
    layoutHelper.stopLayout();
  }

  /**
   * Returns the group node at the given location.
   * If there is no group node, <code>null</code> is returned.
   */
  private INode getHitGroupNode(IInputModeContext context, PointD location) {
    INodeHitTester hitTester = context.lookup(INodeHitTester.class);
    if (hitTester != null) {
      return hitTester
              .enumerateHits(context, location)
              .stream()
              .filter(iNode -> graphComponent.getGraph().isGroupNode(iNode))
              .findFirst().orElse(null);
    }
    return null;
  }


  /**
   * Centers and arranges the graph in the graph component.
   */
  @Override
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }


  /**
   * Loads the graph from the sample graphml file
   */
  private void loadGraph() {
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/grouping.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Applies the {@link HierarchicLayout} to the graph
   */
  private void layoutHierarchical() {
    if (layoutRunning) {
      return;
    }
    layoutRunning = true;

    HierarchicLayout layout = new HierarchicLayout();
    layout.setOrthogonalRoutingEnabled(true);

    LayoutExecutor executor = new LayoutExecutor(graphComponent, layout);
    executor.setDuration(Duration.ofMillis(500));
    executor.setContentRectUpdatingEnabled(true);

    executor.addLayoutFinishedListener((source, args) -> {
      layoutRunning = false;
    });

    executor.start();
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new MarqueeClearAreaLayoutDemo().start();
    });
  }
}
