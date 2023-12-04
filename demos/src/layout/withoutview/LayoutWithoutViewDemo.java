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
package layout.withoutview;

import com.yworks.yfiles.algorithms.Centrality;
import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.Graph;
import com.yworks.yfiles.algorithms.IEdgeMap;
import com.yworks.yfiles.algorithms.INodeMap;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YDimension;
import com.yworks.yfiles.algorithms.YOrientedRectangle;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.layout.BufferedLayout;
import com.yworks.yfiles.layout.DefaultLayoutGraph;
import com.yworks.yfiles.layout.IEdgeLabelLayout;
import com.yworks.yfiles.layout.IEdgeLayout;
import com.yworks.yfiles.layout.ILabelLayout;
import com.yworks.yfiles.layout.ILabelLayoutFactory;
import com.yworks.yfiles.layout.INodeLabelLayout;
import com.yworks.yfiles.layout.INodeLayout;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.LayoutGraphUtilities;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.hierarchic.EdgeRoutingStyle;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.RoutingStyle;
import com.yworks.yfiles.layout.hierarchic.SwimlaneDescriptor;

/**
 * This demo shows how to create a graph, run a graph analysis algorithm, and
 * calculate a layout <b>without</b> using a view or the
 * <a href="https://docs.yworks.com/yfilesjava/doc/api/#/api/com.yworks.yfiles.graph.IGraph">IGraph</a>
 * API.
 * <p>
 * There is no interactivity in this demo. The code creates a graph in-memory
 * with nodes and edges, adds placeholders for labels, creates and configures
 * both a graph analysis algorithm and a layout algorithm and then runs both
 * algorithms on the graph.
 * </p>
 * <p>
 * The results of the analysis and the layout as well as the resulting visual
 * properties of the elements in the graph are then dumped as text to the
 * text-area.
 * </p>
 */
public class LayoutWithoutViewDemo {
  public static void main( String[] args ) {
    // create the graph in memory
    DefaultLayoutGraph layoutGraph = new DefaultLayoutGraph();
    ILabelLayoutFactory labelFactory = LayoutGraphUtilities.getLabelFactory(layoutGraph);

    // build the graph
    Node node1 = createNode(layoutGraph, 0, 0, 30, 30);
    addLabel(labelFactory, node1, 50, 10);

    Node node2 = createNode(layoutGraph, 150, 0, 30, 30);
    addLabel(labelFactory, node2, 50, 10);

    Node node3 = createNode(layoutGraph, 100, 50, 30, 30);

    Edge edge = layoutGraph.createEdge(node1, node3);
    addBend(layoutGraph, edge, 50, 20);

    layoutGraph.createEdge(node2, node3);
    addLabel(labelFactory, layoutGraph.createEdge(node3, node1), 60, 20);

    log("Graph dump before algorithm");
    logGraph(layoutGraph);

    Node centralNode = runAlgorithm(layoutGraph);
    runLayout(layoutGraph, centralNode);

    log("Graph dump after analysis and layout");
    logGraph(layoutGraph);
  }

  static Node createNode(
    LayoutGraph layoutGraph, double x, double y, double width, double height
  ) {
    Node node = layoutGraph.createNode();
    layoutGraph.setSize(node, new YDimension(width, height));
    // Important:
    // Always set the location after changing a node's size, because setSize
    // will retain the node's center not its upper left corner. 
    layoutGraph.setLocation(node, new YPoint(x, y));
    return node;
  }

  static void addBend(LayoutGraph layoutGraph, Edge edge, double x, double y) {
    IEdgeLayout edgeLayout = layoutGraph.getLayout(edge);
    edgeLayout.addPoint(x, y);
  }

  static ILabelLayout addLabel(
    ILabelLayoutFactory labelFactory,
    Object nodeOrEdge,
    double width,
    double height
  ) {
    YOrientedRectangle labelBox = new YOrientedRectangle(0, 0, width, height);
    if (nodeOrEdge instanceof Node) {
      INodeLabelLayout layout = labelFactory.createLabelLayout((Node) nodeOrEdge, labelBox);
      labelFactory.addLabelLayout((Node) nodeOrEdge, layout);
      return layout;
    } else if (nodeOrEdge instanceof Edge) {
      IEdgeLabelLayout layout = labelFactory.createLabelLayout((Edge) nodeOrEdge, labelBox);
      labelFactory.addLabelLayout((Edge) nodeOrEdge, layout);
      return layout;
    } else {
      throw new IllegalArgumentException("item has to be a Node or an Edge.");
    }
  }

  static Node runAlgorithm( Graph graph ) {
    // create data storage
    INodeMap closenessResult = graph.createNodeMap();
    IEdgeMap edgeCosts = graph.createEdgeMap();

    // assign some arbitrary costs
    double edgeCount = graph.edgeCount();
    int i = 0;
    for (Edge edge : graph.getEdges()) {
      edgeCosts.setDouble(edge, i / edgeCount);
      ++i;
    }

    // run the algorithm
    Centrality.closenessCentrality(graph, closenessResult, true, edgeCosts);

    log("Centrality values");
    for (Node node : graph.getNodes()) {
      log(" node " + node.index() + " : " + closenessResult.getDouble(node));
    }

    // find the most central node
    Node centralNode = graph.firstNode();
    double centrality = closenessResult.getDouble(centralNode);
    for (Node node : graph.getNodes()) {
      double tmp = closenessResult.getDouble(node);
      if (centrality < tmp) {
        centrality = tmp;
        centralNode = node;
      }
    }

    // release resources
    graph.disposeEdgeMap(edgeCosts);
    graph.disposeNodeMap(closenessResult);

    log("");

    return centralNode;
  }

  static void runLayout(LayoutGraph layoutGraph, Node centralNode) {
    // assign the central node to its own swimlane
    // create the map that holds the information
    INodeMap swimlaneMap = layoutGraph.createNodeMap();
    // register it with the graph
    layoutGraph.addDataProvider(HierarchicLayout.SWIMLANE_DESCRIPTOR_DPKEY, swimlaneMap);

    // populate the map
    SwimlaneDescriptor centerLane = new SwimlaneDescriptor(0);
    centerLane.setIndexFixed(true);
    centerLane.setLeftLaneInset(5);
    centerLane.setRightLaneInset(5);
    centerLane.setMinimumLaneWidth(30);
    SwimlaneDescriptor otherLane = new SwimlaneDescriptor(1);
    otherLane.setIndexFixed(true);
    otherLane.setMinimumLaneWidth(30);

    for (Node node : layoutGraph.getNodes()) {
      if (node == centralNode) {
        swimlaneMap.set(node, centerLane);
      } else {
        swimlaneMap.set(node, otherLane);
      }
    }

    // create and configure the layout
    HierarchicLayout layout = new HierarchicLayout();
    layout.setLayoutOrientation(LayoutOrientation.TOP_TO_BOTTOM);
    layout.setMaximumDuration(500);
    layout.setBackLoopRoutingEnabled(true);
    layout.setIntegratedEdgeLabelingEnabled(true);
    layout.setNodeLabelConsiderationEnabled(true);
    layout.getEdgeLayoutDescriptor().setRoutingStyle(new RoutingStyle(EdgeRoutingStyle.ORTHOGONAL));

    // and run it
    new BufferedLayout(layout).applyLayout(layoutGraph);

    log("Swimlane results");
    log(" center lane (" + centerLane.getComputedLaneIndex() +
        "): " + centerLane.getComputedLanePosition() +
        ", " + centerLane.getComputedLaneWidth());
    log(" other  lane (${otherLane.computedLaneIndex}): ${otherLane.computedLanePosition}, ${otherLane.computedLaneWidth}");
    log("");

    // clean up map resources
    layoutGraph.disposeNodeMap(swimlaneMap);
  }

  private static void logGraph( LayoutGraph layoutGraph ) {
    for (Node node : layoutGraph.getNodes()) {
      log("node " + node.index());
      INodeLayout nodeLayout = layoutGraph.getLayout(node);
      log(" layout " + nodeLayout.getX() +
          ", " + nodeLayout.getY() +
          ", " + nodeLayout.getWidth() +
          ", " + nodeLayout.getHeight());
      INodeLabelLayout[] nodeLabelLayouts = layoutGraph.getLabelLayout(node);
      if (nodeLabelLayouts != null && nodeLabelLayouts.length > 0) {
        log(" labels");
        for (INodeLabelLayout label : nodeLabelLayouts) {
          logOrientedBox(label.getOrientedBox());
        }
      }
    }

    for (Edge edge : layoutGraph.getEdges()) {
      log("edge " + edge.index() + "  (" + edge.source().index() + " -> " + edge.target().index() + ')');
      IEdgeLayout edgeLayout = layoutGraph.getLayout(edge);
      YPoint sourcePortLocation = layoutGraph.getSourcePointRel(edge);
      log("  source port offset : " + sourcePortLocation.getX() + ", " + sourcePortLocation.getY());
      YPoint targetPortLocation = layoutGraph.getTargetPointRel(edge);
      log("  target port offset : " + targetPortLocation.getX() + ", " + targetPortLocation.getY());
      for (int i = 0, n = edgeLayout.pointCount(); i < n; ++i) {
        YPoint bendPoint = edgeLayout.getPoint(i);
        log("  bend " + i + ": " + bendPoint.getX() + ", " + bendPoint.getY());
      }
      IEdgeLabelLayout[] edgeLabelLayouts = layoutGraph.getLabelLayout(edge);
      if (edgeLabelLayouts != null && edgeLabelLayouts.length > 0) {
        log(" labels");
        for (IEdgeLabelLayout label : edgeLabelLayouts) {
          logOrientedBox(label.getOrientedBox());
        }
      }
    }
    log("");
  }

  private static void logOrientedBox( YOrientedRectangle orientedBox ) {
    String rotation = "";
    if (orientedBox.getUpY() != -1) {
      // rotated
      rotation = orientedBox.getUpX() + ", " + orientedBox.getUpY();
    } else {
      rotation = "";
    }
    log("    " + orientedBox.getAnchorX() +
        ", " + orientedBox.getAnchorY() +
        ", " + orientedBox.getWidth() +
        ", " + orientedBox.getHeight() +
        "  " + rotation);
  }

  private static void log( String msg ) {
    System.out.println(msg);
  }
}
