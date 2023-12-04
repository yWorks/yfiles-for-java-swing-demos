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
package viewer.smartclicknavigation;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.EdgeStyleDecorationInstaller;
import com.yworks.yfiles.view.HighlightIndicatorManager;
import com.yworks.yfiles.view.NodeStyleDecorationInstaller;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import toolkit.AbstractDemo;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This demo shows how to navigate in a large graph, especially
 * when only a part of the graph is visible in the viewport.
 */
public class SmartClickNavigationDemo extends AbstractDemo {

  /**
   * A timer that is scheduled to clear highlights after some time.
   */
  private Timer clearHighlightsTimer;

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {

    // initialize the input mode that prevents editing
    GraphViewerInputMode gvim = initializeInputMode();

    // configure a click handler that handles the navigation
    initializeClickNavigation(gvim);

    // initialize the highlight style
    initializeHighlightingStyle();

    // load a sample graph
    loadGraph();
  }

  /**
   * Configures the smart navigation.
   * @param gvim The input mode that handles item clicks.
   */
  private void initializeClickNavigation(GraphViewerInputMode gvim) {
    gvim.addItemLeftClickedListener(( sender, args ) -> {
      // get the location where we should zoom to
      PointD focusPoint = getFocusPoint(args.getItem());
      // the zoomToAnimated method takes the new viewport center as parameter
      // so to move the focus point to the click location we add the vector
      // from the click location to the viewport center
      PointD clickLocationToViewportCenter = PointD.subtract(graphComponent.getViewport().getCenter(), args.getLocation());
      PointD newViewportCenter = PointD.add(focusPoint, clickLocationToViewportCenter);
      // zooms to the new viewport center
      graphComponent.zoomToAnimated(newViewportCenter, graphComponent.getZoom());

      // highlights the concerned objects(node or edge with target and source node)
      updateHighlight(args.getItem());
    });
  }

  /**
   * Gets the focus point depending on the current focus.
   * @param item The element that was clicked.
   * @return The point we want to zoom to
   */
  private PointD getFocusPoint(IModelItem item) {
    PointD focusPoint = graphComponent.getViewport().getCenter();
    if (item instanceof IEdge) {
      PointD targetNodeCenter = ((IEdge) item).getTargetPort().getLocation();
      PointD sourceNodeCenter = ((IEdge) item).getSourcePort().getLocation();
      RectD viewport = graphComponent.getViewport();
      if (viewport.contains(targetNodeCenter) && viewport.contains(sourceNodeCenter)) {
        // if the source and the target node are in the view port, then zoom to the middle point of the edge
        focusPoint = new PointD(
                (sourceNodeCenter.x + targetNodeCenter.x) / 2,
                (sourceNodeCenter.y + targetNodeCenter.y) / 2);
      } else if (PointD.subtract(viewport.getCenter(), targetNodeCenter).getVectorLength() <
                 PointD.subtract(viewport.getCenter(), sourceNodeCenter).getVectorLength()) {
        // if the source node is out of the view port, then zoom to it
        focusPoint = sourceNodeCenter;
      } else {
        // else zoom to the target node
        focusPoint = targetNodeCenter;
      }
    } else if (item instanceof INode) {
      // zoom to the center of the clicked node
      focusPoint = ((INode) item).getLayout().getCenter();
    }
    return focusPoint;
  }

  /**
   * Highlight the given item for a short time.
   */
  private void updateHighlight(IModelItem item) {
    HighlightIndicatorManager<IModelItem> manager = graphComponent.getHighlightIndicatorManager();
    
    if (clearHighlightsTimer != null) {
      // a timer for clearing highlights is still running - cancel it and clear highlights now
      clearHighlightsTimer.cancel();
      manager.clearHighlights();
    }
    
    if (item instanceof IEdge) {
      manager.addHighlight(item);
      manager.addHighlight(((IEdge) item).getSourceNode());
      manager.addHighlight(((IEdge) item).getTargetNode());
    } else if (item instanceof INode) {
      manager.addHighlight(item);
    }

    // clear highlights after one second
    clearHighlightsTimer = new Timer();
    clearHighlightsTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        clearHighlightsTimer = null;
        manager.clearHighlights();
      }
    }, 1000);
  }

  /**
   * Configures the highlight styles that will be used for the clicked nodes and edges.
   */
  private void initializeHighlightingStyle() {
    // configure highlight for nodes
    NodeStyleDecorationInstaller nodeHighlightStyle = new NodeStyleDecorationInstaller();
    ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
    nodeStyle.setPaint(Colors.TRANSPARENT);
    Pen pen = new Pen(new Color(104, 176, 227), 3);
    nodeStyle.setPen(pen);
    // with a margin for the decoration
    nodeHighlightStyle.setMargins(new InsetsD(7));
    nodeHighlightStyle.setNodeStyle(nodeStyle);
    graphComponent.getGraph().getDecorator().getNodeDecorator().getHighlightDecorator().setImplementation(nodeHighlightStyle);

    // configure highlight for edges
    EdgeStyleDecorationInstaller edgeHighlightStyle = new EdgeStyleDecorationInstaller();
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setTargetArrow(new Arrow(ArrowType.DEFAULT, new Color(104, 176, 227), 0, 2));
    edgeStyle.setPen(new Pen(new Color(104, 176, 227), 3));
    edgeHighlightStyle.setEdgeStyle(edgeStyle);
    graphComponent.getGraph().getDecorator().getEdgeDecorator().getHighlightDecorator().setImplementation(edgeHighlightStyle);
  }

  /**
   * Initializes the input mode for the demo.
   */
  private GraphViewerInputMode initializeInputMode() {
    GraphViewerInputMode mode = new GraphViewerInputMode();
    mode.setSelectableItems(GraphItemTypes.NODE.or(GraphItemTypes.EDGE));
    graphComponent.setInputMode(mode);
    return mode;
  }

  /**
   * Adjusts the view by the first start of the demo.
   */
  @Override
  public void onVisible() {
    // zoom to a node that has "Sport" label
    for (INode node : graphComponent.getGraph().getNodes()) {
      if (node.getLabels().size() > 0 && node.getLabels().first().getText().contains("Sport")) {
        graphComponent.zoomToAnimated(node.getLayout().getCenter(), 0.8);
        return;
      }
    }
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

  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new SmartClickNavigationDemo().start();
    });
  }
}