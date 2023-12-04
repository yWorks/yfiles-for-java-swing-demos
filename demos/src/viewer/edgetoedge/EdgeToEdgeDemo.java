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
package viewer.edgetoedge;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.portlocationmodels.BendAnchoredPortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.EdgePathPortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.SegmentRatioPortLocationModel;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.GridConstraintProvider;
import com.yworks.yfiles.view.input.GridSnapTypes;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IEdgePortHandleProvider;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.LabelSnapContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.PortRelocationHandleProvider;
import com.yworks.yfiles.view.input.Visualization;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;

import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This application demonstrates the use of edge-to-edge connections. Edges can be created interactively
 * between nodes, nodes and edges and between two edges. Also, this application enables moving the source or
 * target of an edge to another owner.
 * Connecting the source or target of an edge to itself is prohibited since this is conceptually forbidden.
 * <p>
 * Edge-to-edge connections have to be enabled explicitly using the property
 * {@link com.yworks.yfiles.view.input.CreateEdgeInputMode#setEdgeToEdgeConnectionsAllowed(boolean)}.
 * </p>
 * <p>
 * This demo also includes customized implementations of {@link IPortCandidateProvider},
 * {@link IEdgeReconnectionPortCandidateProvider}, {@link IHitTestable}, {@link IEdgePortHandleProvider} and
 * {@link IPortLocationModel} to enable custom behavior like reconnecting an existing edge to another edge,
 * starting edge creation from an edge etc.
 * </p>
 */
public class EdgeToEdgeDemo extends AbstractDemo {
  private static final int GRID_SIZE = 50;
  private static final GridInfo GRID_INFO = new GridInfo(GRID_SIZE);

  private GraphSnapContext snapContext;
  private LabelSnapContext labelSnapContext;

  /**
   * Initializes the graph and the input mode.
   */
  public void initialize() {
    // initialize the graph
    initializeGraph();

    // initialize the snapcontext
    initializeSnapContext();

    // initialize the input mode
    initializeInputModes();
  }

  /**
   * Centers the graph in the graph component.
   */
  public void onVisible() {
    // center the graph
    graphComponent.fitGraphBounds();
  }

  private void initializeSnapContext() {
    snapContext = new GraphSnapContext();
    snapContext.setEnabled(false);
    // disable grid snapping
    snapContext.setGridSnapType(GridSnapTypes.NONE);
    // add constraint provider for nodes, bends, and ports
    snapContext.setNodeGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));
    snapContext.setBendGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));
    snapContext.setPortGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));

    // initialize label snapping
    labelSnapContext = new LabelSnapContext();
    labelSnapContext.setEnabled(false);
    // set maximum distance between the current mouse coordinates and the coordinates to which the mouse will snap
    labelSnapContext.setSnapDistance(15);
    // set the amount by which snap lines that are induced by existing edge segments are being extended
    labelSnapContext.setSnapLineExtension(100);
  }

  /**
   * Calls {@link #createEditorMode}, registers the result as the
   * {@link com.yworks.yfiles.view.CanvasComponent#setInputMode(IInputMode)}
   * and enables edge-to-edge connections.
   */
  private void initializeInputModes() {
    GraphEditorInputMode inputMode = createEditorMode();
    enableEdgeToEdgeConnections(inputMode);
    graphComponent.setInputMode(inputMode);
  }

  /**
   * Creates the default input mode for the GraphControl, a {@link GraphEditorInputMode}.
   * @return a new GraphEditorInputMode instance.
   */
  private GraphEditorInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    mode.setGroupingOperationsAllowed(true);
    mode.setSnapContext(snapContext);
    mode.setLabelSnapContext(labelSnapContext);
    OrthogonalEdgeEditingContext editingContext = new OrthogonalEdgeEditingContext();
    editingContext.setEnabled(false);
    mode.setOrthogonalEdgeEditingContext(editingContext);

    // randomize edge color
    mode.getCreateEdgeInputMode().addEdgeCreationStartedListener((source, args) -> setRandomEdgeColor(args.getItem()));
    return mode;
  }

  /**
   * Enables edge-to-edge connections on the input mode.
   */
  private void enableEdgeToEdgeConnections(GraphEditorInputMode mode) {
    // enable edge-to-edge
    mode.getCreateEdgeInputMode().setEdgeToEdgeConnectionsAllowed(true);

    // create bends only when shift is pressed
    mode.getCreateBendInputMode().setPressedRecognizer(IEventRecognizer.MOUSE_LEFT_PRESSED.and(IEventRecognizer.SHIFT_PRESSED));
  }

  /**
   * Initializes the graph instance setting default styles and customizing behavior.
   */
  private void initializeGraph() {
    IGraph graph = graphComponent.getGraph();

    graphComponent.setFileIOEnabled(true);

    // Get the default graph instance and enable undo/redo support.
    graph.setUndoEngineEnabled(true);

    // set the default node style
    graph.getNodeDefaults().setStyle(DemoStyles.createDemoNodeStyle());

    // assign default edge style
    graph.getEdgeDefaults().setStyle(new PolylineEdgeStyle());
    graph.getEdgeDefaults().setStyleInstanceSharingEnabled(false);

    // assign a port style for the ports at the edges
    ShapeNodeStyle portNodeStyle = new ShapeNodeStyle();
    portNodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    portNodeStyle.setPaint(Colors.BLACK);
    portNodeStyle.setPen(null);
    NodeStylePortStyleAdapter portStyle = new NodeStylePortStyleAdapter();
    portStyle.setRenderSize(new SizeD(3,3));
    graph.getEdgeDefaults().getPortDefaults().setStyle(portStyle);

    // enable edge port candidates
    graph.getDecorator().getEdgeDecorator().getPortCandidateProviderDecorator().setFactory(
        EdgeSegmentPortCandidateProvider::new);
    // set IEdgeReconnectionPortCandidateProvider to allow re-connecting edges to other edges
    graph.getDecorator().getEdgeDecorator().getEdgeReconnectionPortCandidateProviderDecorator().setImplementation(
        IEdgeReconnectionPortCandidateProvider.ALL_NODE_AND_EDGE_CANDIDATES);
    graph.getDecorator().getEdgeDecorator().getHandleProviderDecorator().setFactory(edge -> {
      PortRelocationHandleProvider handleProvider = new PortRelocationHandleProvider(null, edge);
      handleProvider.setVisualization(Visualization.LIVE);
      return handleProvider;
    });

    // load a sample graph
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a random colored pen and uses that one for the style.
   */
  private void setRandomEdgeColor(IEdge edge) {
    if (edge.getStyle() instanceof PolylineEdgeStyle) {
      PolylineEdgeStyle style = (PolylineEdgeStyle) edge.getStyle();
      Random random = new Random();
      int r = random.nextInt(255);
      int g = random.nextInt(255);
      int b = random.nextInt(255);
      Pen pen = new Pen(new Color(r, g, b, 255), 2);
      style.setPen(pen);
    }
  }

  /**
   * A port candidate provider that aggregates different {@link IPortLocationModel} to provide a number of port
   * candidates along each segment of the edge.
   */
  static class EdgeSegmentPortCandidateProvider extends AbstractPortCandidateProvider {
    private final IEdge edge;

    EdgeSegmentPortCandidateProvider(IEdge edge) {
      this.edge = edge;
    }

    protected IEnumerable<IPortCandidate> getPortCandidates(IInputModeContext context) {
      List<IPortCandidate> candidates = new ArrayList<>();
      // add equally distributed port candidates along the edge
      for (int i = 1; i < 10; ++i) {
        candidates.add(new DefaultPortCandidate(edge, EdgePathPortLocationModel.INSTANCE.createRatioParameter(0.1 * i)));
      }
      // add a dynamic candidate that can be used if shift is pressed to assign the exact location.
      candidates.add(new DefaultPortCandidate(edge, EdgePathPortLocationModel.INSTANCE));
      return IEnumerable.create(candidates);
    }
  }

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, graphComponent));
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE_AS, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png", ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Cut", "cut-16.png", ICommand.CUT, null, graphComponent));
    toolBar.add(createCommandButtonAction("Copy", "copy-16.png", ICommand.COPY, null, graphComponent));
    toolBar.add(createCommandButtonAction("Paste", "paste-16.png", ICommand.PASTE, null, graphComponent));
    toolBar.add(createCommandButtonAction("Delete", "delete3-16.png", ICommand.DELETE, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom 1:1", "zoom-original2-16.png", ICommand.ZOOM, 1, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Adjust the view port to show the complete graph", "fit2-16.png", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Group selected elements", "group-16.png", ICommand.GROUP_SELECTION, null, graphComponent));
    toolBar.add(createCommandButtonAction("Ungroup selected elements", "ungroup-16.png", ICommand.UNGROUP_SELECTION, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(new JToggleButton(createToggleSnapAction()));
    toolBar.add(new JToggleButton(createToggleOrthogonalEdgeCreationAction()));
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new EdgeToEdgeDemo().start();
    });
  }
}
