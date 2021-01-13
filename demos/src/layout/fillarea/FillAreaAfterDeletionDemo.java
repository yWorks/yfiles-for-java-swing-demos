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
package layout.fillarea;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.partial.ComponentAssignmentStrategy;
import com.yworks.yfiles.layout.partial.FillAreaLayout;
import com.yworks.yfiles.view.ISelectionModel;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.SelectionEventArgs;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.Locale;

/**
 * Shows how to fill free space after deleting nodes.
 */
public class FillAreaAfterDeletionDemo extends AbstractDemo {

  private LayoutExecutor layoutExecutor;

  private ComponentAssignmentStrategy componentAssignmentStrategy = ComponentAssignmentStrategy.SINGLE;

  private boolean layoutRunning;

  /**
   * Adds controls for selecting the {@link ComponentAssignmentStrategy}
   * as well as standard controls like as undo/redo etc.
   */
  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    super.configureToolBar(toolBar);

    toolBar.addSeparator();

    toolBar.add(createCommandButtonAction(
            "Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction(
            "Redo", "redo-16.png", ICommand.REDO, null, graphComponent));

    toolBar.addSeparator();

    toolBar.add(new JLabel("Component Assignment Strategy:"));

    JComboBox<String> strategyComboBox = new JComboBox<>(new String[]{"Single", "Connected", "Clustering"});
    strategyComboBox.addActionListener(e -> {
      String selectedItem = strategyComboBox.getItemAt(strategyComboBox.getSelectedIndex());
      switch (selectedItem) {
        case "Single":
          componentAssignmentStrategy = ComponentAssignmentStrategy.SINGLE;
          break;
        case "Connected":
          componentAssignmentStrategy = ComponentAssignmentStrategy.CONNECTED;
          break;
        case "Clustering":
          componentAssignmentStrategy = ComponentAssignmentStrategy.CLUSTERING;
          break;
        default:
          throw new IllegalArgumentException("Unsupported Component Assignment Strategy");
      }
    });
    strategyComboBox.setMaximumSize(strategyComboBox.getPreferredSize());
    toolBar.add(strategyComboBox);

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
    initializeInputModes();

    graphComponent.getGraph().setUndoEngineEnabled(true);

    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.orange);
    graphComponent.getGraph().getNodeDefaults().setStyle(nodeStyle);
    loadGraph();
    graphComponent.fitGraphBounds();
  }

  private void initializeInputModes() {
    // create a GraphEditorInputMode instance and install the edit mode into the canvas.
    GraphEditorInputMode graphEditorInputMode = new GraphEditorInputMode();
    graphComponent.setInputMode(graphEditorInputMode);

    // registers handlers which are called when selected nodes are deleted
    graphEditorInputMode.addDeletingSelectionListener(this::onDeletingSelection);
    graphEditorInputMode.addDeletedSelectionListener(this::onDeletedSelection);
  }

  private void onDeletingSelection(Object source, SelectionEventArgs<IModelItem> args) {
    FillAreaLayout fillAreaLayout = new FillAreaLayout();
    fillAreaLayout.setComponentAssignmentStrategy(componentAssignmentStrategy);
    fillAreaLayout.setArea(getBounds(args.getSelection()).toYRectangle());

    // configure the LayoutExecutor that will perform the layout and morph the result
    layoutExecutor = new LayoutExecutor(graphComponent, fillAreaLayout);
    layoutExecutor.setDuration(Duration.ofMillis(100));
  }

  private void onDeletedSelection(Object source, SelectionEventArgs<IModelItem> args) {
    layoutExecutor.start();
  }


  /**
   * Calculates the bounds including all nodes in a selection
   * @param selection the selection
   * @return the bounds of all nodes in the selection unified
   */
  public RectD getBounds(ISelectionModel<IModelItem> selection) {
    RectD bounds = RectD.EMPTY;

    for (IModelItem item : selection) {
      if (item instanceof INode) {
        bounds = RectD.add(bounds, ((INode) item).getLayout().toRectD());
      }
      else if (item instanceof IEdge) {
        IEdge edge = (IEdge) item;
        if (edge.getSourcePort() != null) {
          bounds = RectD.add(bounds, edge.getSourcePort().getLocation());
        }
        if (edge.getTargetPort() != null) {
          bounds = RectD.add(bounds, edge.getTargetPort().getLocation());
        }
      }
    }
    return bounds;
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
      graphComponent.importFromGraphML(getClass().getResource("resources/hierarchic.graphml"));
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

  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new FillAreaAfterDeletionDemo().start();
    });
  }
}
