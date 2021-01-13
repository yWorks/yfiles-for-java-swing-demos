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
package layout.edgegrouping;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IMapper;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.VoidPortStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.IncrementalHintItemMapping;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.layout.hierarchic.SimplexNodePlacer;
import com.yworks.yfiles.view.BridgeCrossingStyle;
import com.yworks.yfiles.view.BridgeManager;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphObstacleProvider;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import toolkit.AbstractDemo;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.IOException;
import java.time.Duration;
import java.util.Locale;

/**
 * Shows the effects of edge and port grouping when arranging graphs with
 * {@link HierarchicLayout}.
 */
public class EdgeGroupingDemo extends AbstractDemo {
  /** Holds group IDs for the edges' source end. */
  private IMapper<IEdge, Object> sourceGroupIdMapper;
  /** Holds group IDs for the edges' target end. */
  private IMapper<IEdge, Object> targetGroupIdMapper;

  /**
   * Specifies if the layout algorithm should use edge or port grouping.
   */
  private boolean portGroupMode = false;

  /**
   * Adds controls for switching between edge grouping and port grouping.
   */
  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    super.configureToolBar(toolBar);

    toolBar.addSeparator();

    JRadioButton edgeGroupingButton = new JRadioButton("Edge grouping", !portGroupMode);
    edgeGroupingButton.addActionListener(event -> {
      portGroupMode = false;
      updateEdgeStyles(graphComponent.getGraph());
      runLayout(true);
    });
    JRadioButton portGroupingButton = new JRadioButton("Port grouping", portGroupMode);
    portGroupingButton.addActionListener(event -> {
      portGroupMode = true;
      updateEdgeStyles(graphComponent.getGraph());
      runLayout(true);
    });

    // ensure only one of the two buttons can be selected
    ButtonGroup group = new ButtonGroup();
    group.add(edgeGroupingButton);
    group.add(portGroupingButton);

    toolBar.add(new JLabel("Group mode:"));
    toolBar.add(edgeGroupingButton);
    toolBar.add(portGroupingButton);
  }


  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    // enable interactive navigation such as panning
    graphComponent.setInputMode(new GraphViewerInputMode());

    // creates mappers for storing group IDs
    createMappers();

    // configure default node, edge and arrow styles.
    configureBridges();

    // load a sample GraphML file with suitable group IDs
    loadGraph();

    // set the color and port styles for edges according to their grouping
    updateEdgeStyles(graphComponent.getGraph());

    // calculate an initial arrangement for the sample graph
    runLayout(false);
  }

  /**
   * Arranges the displayed graph using the {@link HierarchicLayout} algorithm.
   * @param incremental if {@code true} the algorithm will take "old" positions
   * of nodes into account; otherwise old position will have no effect on the
   * resulting arrangement.
   */
  private void runLayout( boolean incremental ) {
    HierarchicLayout algorithm = new HierarchicLayout();
    algorithm.setOrthogonalRoutingEnabled(true);
    algorithm.setMinimumLayerDistance(70);
    algorithm.setLayoutMode(incremental ? LayoutMode.INCREMENTAL : LayoutMode.FROM_SCRATCH);

    SimplexNodePlacer nodePlacer = new SimplexNodePlacer();
    nodePlacer.setBarycenterModeEnabled(true);
    nodePlacer.setBendReduction(false);
    algorithm.setNodePlacer(nodePlacer);

    HierarchicLayoutData layoutData = new HierarchicLayoutData();
    layoutData.setEdgeThickness(3);

    IncrementalHintItemMapping incrementalHints = new IncrementalHintItemMapping();
    incrementalHints.setIncrementalSequencingItems(item -> item instanceof IEdge);
    layoutData.setIncrementalHints(incrementalHints);

    if (portGroupMode) {
      layoutData.setSourcePortGroupIds(sourceGroupIdMapper);
      layoutData.setTargetPortGroupIds(targetGroupIdMapper);
    } else {
      layoutData.setSourceGroupIds(sourceGroupIdMapper);
      layoutData.setTargetGroupIds(targetGroupIdMapper);
    }
    graphComponent.morphLayout(algorithm, Duration.ofMillis(700), layoutData);
  }

  /**
   * Creates the mappers used for storing group IDs required for edge and port
   * grouping.
   */
  private void createMappers() {
    sourceGroupIdMapper = new Mapper<>();
    targetGroupIdMapper = new Mapper<>();
  }

  /**
   * Adds a {@link BridgeManager} for easier distinction between edge groups
   * and edge crossings.
   */
  private void configureBridges() {
    BridgeManager bridgeManager = new BridgeManager();
    bridgeManager.setCanvasComponent(graphComponent);
    bridgeManager.setDefaultBridgeCrossingStyle(BridgeCrossingStyle.GAP);
    bridgeManager.addObstacleProvider(new GraphObstacleProvider());
  }

  /**
   * Updates all edge styles according to the grouping type used for the
   * corresponding edges. In this context, grouping type means
   * "source grouping", "target grouping", or "source and target grouping".
   */
  private void updateEdgeStyles( IGraph graph ) {
    for (IEdge edge : graph.getEdges()) {
      updateEdgeStyle(graph, edge);
    }
  }

  /**
   * Adjusts the styles of the given edge and its ports depending on grouping
   * type. In this context, grouping type means "source grouping", "target
   * grouping", or "source and target grouping".
   */
  private void updateEdgeStyle( IGraph graph, IEdge edge ) {
    Object srcGrpId = sourceGroupIdMapper.getValue(edge);
    Object tgtGrpId = targetGroupIdMapper.getValue(edge);

    if (srcGrpId == null && tgtGrpId == null) {
      // the ungrouped edge default
      return;
    }

    // distinguish edges by color depending on grouping type (i.e. source
    // grouping, target grouping, or source and target grouping
    Color color = getEdgeColor(srcGrpId, tgtGrpId);

    // leverage ShapeNodeStyle to create a simple cycle visualization for ports
    ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
    nodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    nodeStyle.setPaint(color);
    nodeStyle.setPen(null);

    NodeStylePortStyleAdapter portStyleAdapter = new NodeStylePortStyleAdapter();
    portStyleAdapter.setNodeStyle(nodeStyle);
    portStyleAdapter.setRenderSize(new SizeD(7, 7));

    // apply port style
    if (srcGrpId != null) {
      graph.setStyle(edge.getSourcePort(), portStyleAdapter);
    } else {
      graph.setStyle(edge.getSourcePort(), VoidPortStyle.INSTANCE);
    }
    if (tgtGrpId != null) {
      graph.setStyle(edge.getTargetPort(), portStyleAdapter);
    } else {
      graph.setStyle(edge.getTargetPort(), VoidPortStyle.INSTANCE);
    }

    // apply edge style
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(new Pen(color, 3));
    edgeStyle.setTargetArrow(new Arrow(ArrowType.DEFAULT, color));
    edgeStyle.setSmoothingLength(15);
    graph.setStyle(edge, edgeStyle);
  }


  /**
   * Determines a edge path color for distinguishing edges by grouping type.
   * In this context, grouping type means "source grouping", "target grouping",
   * or "source and target grouping".
   */
  private Color getEdgeColor( Object srcGrpId, Object tgtGrpId ) {
    if (portGroupMode) {
      if (srcGrpId == null) {
        // target grouped
        return Colors.CHOCOLATE;
      } else if (tgtGrpId == null) {
        // source grouped
        return Colors.CRIMSON;
      } else {
        // source and target grouped
        return Colors.DARK_ORANGE;
      }
    } else {
      if (srcGrpId == null) {
        // target grouped
        return Colors.DARK_SLATE_BLUE;
      } else if (tgtGrpId ==  null) {
        // source grouped
        return Colors.ROYAL_BLUE;
      } else {
        // source and target grouped
        return Colors.CORNFLOWER_BLUE;
      }
    }
  }

  /**
   * Loads a sample graph. Populates {@link #sourceGroupIdMapper source} and
   * {@link #targetGroupIdMapper target} group IDs from data in the GraphML
   * file.
   */
  private void loadGraph() {
    GraphMLIOHandler graphMLIOHandler = graphComponent.getGraphMLIOHandler();

    graphMLIOHandler.addInputMapper(IEdge.class, Object.class, "sourceGroupId", sourceGroupIdMapper);
    graphMLIOHandler.addInputMapper(IEdge.class, Object.class, "targetGroupId", targetGroupIdMapper);

    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new EdgeGroupingDemo().start();
    });
  }
}
