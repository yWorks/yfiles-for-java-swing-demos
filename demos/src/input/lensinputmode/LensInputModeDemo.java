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
package input.lensinputmode;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.Projections;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

/**
 * Shows how to use a special {@link LensInputMode} to magnify the currently hovered-over part of the graph.
 */
public class LensInputModeDemo extends AbstractDemo {
  /**
   * The {@link LensInputMode} displaying the "magnifying glass".
   */
  private final LensInputMode lensInputMode = new LensInputMode();

  /**
   * Initializes this demo by configuring the graph defaults, adding the {@link LensInputMode} and
   * populating the graph.
   */
  public void initialize() {
    GraphEditorInputMode graphEditorInputMode = new GraphEditorInputMode();
    graphEditorInputMode.add(lensInputMode);
    graphComponent.setInputMode(graphEditorInputMode);

    IGraph graph = graphComponent.getGraph();

    // Set nicer styles ...
    DemoStyles.initDemoStyles(graph);

    // ... and create the sample graph
    initializeGraph(graph);

    // Finally, enable undo and redo
    graph.setUndoEngineEnabled(true);
  }

  /**
   * Centers the graph in the graph component.
   */
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Adds controls to the demo's tool bar for undo/redo, toggling the view
   * projection, and  configuring the demo's lens input mode.
   */
  @Override
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png",
        ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png",
        ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(new JLabel("Lens zoom: "));
    toolBar.add(createLensZoomSlider());
    toolBar.add(new JLabel("Lens size: "));
    toolBar.add(createLensSizeSlider());
    toolBar.add(createProjectionToggleButton());
  }

  /**
   * Creates a control for configuring the zoom factor of the demo's lens
   * input mode.
   */
  private JSlider createLensZoomSlider() {
    JSlider lensZoomSlider = new JSlider();
    lensZoomSlider.setMaximumSize(new Dimension(200, 50));

    lensZoomSlider.setMinimum(1);
    lensZoomSlider.setMaximum(20);
    lensZoomSlider.setValue(3);
    lensZoomSlider.setMinorTickSpacing(1);
    lensZoomSlider.setSnapToTicks(true);

    lensZoomSlider.addChangeListener(e -> lensInputMode.setZoomFactor(lensZoomSlider.getValue()));

    return lensZoomSlider;
  }

  /**
   * Creates a control for configuring the size of the demo's lens input mode.
   */
  private JSlider createLensSizeSlider() {
    JSlider lensSizeSlider = new JSlider();
    lensSizeSlider.setMaximumSize(new Dimension(200, 50));

    lensSizeSlider.setMinimum(100);
    lensSizeSlider.setMaximum(500);
    lensSizeSlider.setValue(250);
    lensSizeSlider.setMinorTickSpacing(10);
    lensSizeSlider.setSnapToTicks(true);

    lensSizeSlider.addChangeListener(e -> {
      double size = lensSizeSlider.getValue();
      lensInputMode.setSize(size);
    });

    return lensSizeSlider;
  }

  /**
   * Creates a control for toggling the projection of the demo's graph view.
   */
  private JToggleButton createProjectionToggleButton() {
    return new JToggleButton(new AbstractAction("Use Isometric Projection") {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JToggleButton) {
          boolean useProjection = ((JToggleButton) e.getSource()).isSelected();
          graphComponent.setProjection(useProjection ? Projections.getIsometric() : Projections.getIdentity());
        }
      }
    });
  }

  /**
   * Creates the demo's sample graph.
   */
  private void initializeGraph(IGraph graph) {
    INode[] nodes = new INode[16];
    int count = 0;
    for (int i = 1; i < 5; i++) {
      nodes[count++] = graph.createNode(new PointD(50 + 40*i, 260));
      nodes[count++] = graph.createNode(new PointD(50 + 40*i, 40));
      nodes[count++] = graph.createNode(new PointD(40, 50 + 40*i));
      nodes[count++] = graph.createNode(new PointD(260, 50 + 40*i));
    }

    for (int i = 0; i < nodes.length; i++) {
      graph.addLabel(nodes[i], "" + i);
    }

    graph.createEdge(nodes[0], nodes[1]);
    graph.createEdge(nodes[5], nodes[4]);
    graph.createEdge(nodes[2], nodes[3]);
    graph.createEdge(nodes[7], nodes[6]);
    graph.createEdge(nodes[2 + 8], nodes[3 + 8]);
    graph.createEdge(nodes[7 + 8], nodes[6 + 8]);
    graph.createEdge(nodes[0 + 8], nodes[1 + 8]);
    graph.createEdge(nodes[5 + 8], nodes[4 + 8]);
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new LensInputModeDemo().start();
    });
  }
}
