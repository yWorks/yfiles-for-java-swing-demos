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
package style.compositenodestyle;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.styles.ImageNodeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import toolkit.AbstractDemo;

import java.awt.Color;
import java.awt.EventQueue;
import java.net.URL;


/**
 * Demonstrates how to implement a custom {@link yfiles.graph.styles.INodeStyle} that combines several other node styles
 */
public class CompositeNodeStyleDemo extends AbstractDemo {


  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new CompositeNodeStyleDemo().start();
    });
  }

  /**
   * Initializes the application
   */
  public void initialize() {

    // Configures how the user may interact with the graph
    configureUserInteraction();

    // Sets some defaults for node sizes and styles
    configureGraphDefaults(graphComponent.getGraph());

    // Creates a sample graph that uses composite styles for it's nodes
    populateGraph();
  }

  /**
   * Centers the graph in the graph component after the window is shown.
   */
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Enables interactive editing, but prevents interactive resizing of nodes.
   */
  public void configureUserInteraction() {
    GraphEditorInputMode inputMode = new GraphEditorInputMode();

    // removing nodes from showHandleItems effectively turns off resizing for nodes
    // resizing is turned off, because the node visualization used in this demo combine
    // circular background visualizations with rectangular foreground icons and due to
    // CompositeNodeStyle's approach of using absolute insets, increasing the size of a node
    // might lead to a rectangular icon no longer fitting inside its circular border
    inputMode.setShowHandleItems(GraphItemTypes.BEND.or(GraphItemTypes.EDGE.or(GraphItemTypes.EDGE_LABEL.or(
            GraphItemTypes.NODE_LABEL.or(GraphItemTypes.PORT.or(GraphItemTypes.PORT_LABEL))))));

    graphComponent.setInputMode(inputMode);
  }

  /**
   * Sets defaults style and node sizes for the given graph.
   */
  public void configureGraphDefaults(IGraph graph) {
    graph.getNodeDefaults().setSize(new SizeD(96, 96));

    ShapeNodeStyle defaultStyle = new ShapeNodeStyle();
    defaultStyle.setShape(ShapeNodeShape.ELLIPSE);
    defaultStyle.setPaint(new Color(238, 238, 238));
    defaultStyle.setPen(new Pen(new Color(91, 121, 132), 3));
    graph.getNodeDefaults().setStyle(defaultStyle);

    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(ExteriorLabelModel.SOUTH);

    PolylineEdgeStyle polylineEdgeStyle = new PolylineEdgeStyle();
    polylineEdgeStyle.setPen(new Pen(new Color(97, 121, 132), 2));
    graph.getEdgeDefaults().setStyle(polylineEdgeStyle);
  }

  /**
   * Creates a sample graph that uses the {@link CompositeNodeStyle} to combine styles for the nodes
   */
  private void populateGraph() {
    IGraph graph = graphComponent.getGraph();

    // create a filled circle for the background
    ShapeNodeStyle circleBackgroundStyle = new ShapeNodeStyle();
    circleBackgroundStyle.setShape(ShapeNodeShape.ELLIPSE);
    circleBackgroundStyle.setPaint(new Color(238, 238, 238));
    circleBackgroundStyle.setPen(Pen.getTransparent());

    // create another style for the circle outline. Note that this is purely for instructional reasons,
    // as ShapeNodeStyle offers separate fill and outline
    ShapeNodeStyle circleOutlineStyle = new ShapeNodeStyle();
    circleOutlineStyle.setShape(ShapeNodeShape.ELLIPSE);
    circleOutlineStyle.setPaint(new Color(0, 0, 0, 0));
    circleOutlineStyle.setPen(new Pen(new Color(91, 121, 132), 3));

    // create some ImageNodeStyles
    ImageNodeStyle printerImageStyle = new ImageNodeStyle(getResourceUrl("printer.png"));
    ImageNodeStyle workstationImageStyle = new ImageNodeStyle(getResourceUrl("workstation.png"));
    ImageNodeStyle scannerImageStyle = new ImageNodeStyle(getResourceUrl("scanner.png"));
    ImageNodeStyle routerImageStyle = new ImageNodeStyle(getResourceUrl("router.png"));
    ImageNodeStyle switchImageStyle = new ImageNodeStyle(getResourceUrl("switch.png"));
    ImageNodeStyle serverImageStyle = new ImageNodeStyle(getResourceUrl("server.png"));

    // combine the styles
    CompositeNodeStyle combinedPrinterStyle = new CompositeNodeStyle(circleBackgroundStyle);
    combinedPrinterStyle.addNodeStyle(circleOutlineStyle);
    combinedPrinterStyle.addNodeStyle(printerImageStyle, new InsetsD(20, 20, 20, 20));

    CompositeNodeStyle combinedWorkstationStyle = new CompositeNodeStyle(circleBackgroundStyle);
    combinedWorkstationStyle.addNodeStyle(circleOutlineStyle);
    combinedWorkstationStyle.addNodeStyle(workstationImageStyle, new InsetsD(20, 20, 20, 20));

    CompositeNodeStyle combinedSwitchStyle = new CompositeNodeStyle(circleBackgroundStyle);
    combinedSwitchStyle.addNodeStyle(circleOutlineStyle);
    combinedSwitchStyle.addNodeStyle(switchImageStyle, new InsetsD(20, 20, 20, 20));

    CompositeNodeStyle combinedRouterStyle = new CompositeNodeStyle(circleBackgroundStyle);
    combinedRouterStyle.addNodeStyle(circleOutlineStyle);
    combinedRouterStyle.addNodeStyle(routerImageStyle, new InsetsD(20, 20, 20, 20));

    CompositeNodeStyle combinedServerStyle = new CompositeNodeStyle(circleBackgroundStyle);
    combinedServerStyle.addNodeStyle(circleOutlineStyle);
    combinedServerStyle.addNodeStyle(serverImageStyle, new InsetsD(10, 10, 20, 10));

    CompositeNodeStyle combinedScannerStyle = new CompositeNodeStyle(circleBackgroundStyle);
    combinedScannerStyle.addNodeStyle(circleOutlineStyle);
    combinedScannerStyle.addNodeStyle(scannerImageStyle, new InsetsD(20, 20, 20, 20));

    // create some nodes that use the combined styles
    INode node0 = graph.createNode(new PointD(-47.61, 396.02));
    graph.addLabel(node0, "oschi");
    graph.setStyle(node0, combinedPrinterStyle);

    INode node1 = graph.createNode(new PointD(322.01, -130.0));
    graph.addLabel(node1, "scanner");
    graph.setStyle(node1, combinedScannerStyle);

    INode node2 = graph.createNode(new PointD(237.76, 296.27));
    graph.addLabel(node2, "router");
    graph.setStyle(node2, combinedRouterStyle);

    INode node3 = graph.createNode(new PointD(78.21, 12.57));
    graph.addLabel(node3, "klotz");
    graph.setStyle(node3, combinedServerStyle);

    INode node4 = graph.createNode(new PointD(98.51, 558.62));
    graph.addLabel(node4, "brummer");
    graph.setStyle(node4, combinedWorkstationStyle);

    INode node5 = graph.createNode(new PointD(371.03, 4.28));
    graph.addLabel(node5, "schnucki");
    graph.setStyle(node5, combinedWorkstationStyle);

    INode node6 = graph.createNode(new PointD(-47.61, 172.37));
    graph.addLabel(node6, "power");
    graph.setStyle(node6, combinedWorkstationStyle);

    INode node7 = graph.createNode(new PointD(481.73, -45.72));
    graph.addLabel(node7, "color");
    graph.setStyle(node7, combinedPrinterStyle);

    INode node8 = graph.createNode(new PointD(-183.3, 438.47));
    graph.addLabel(node8, "deskjet");
    graph.setStyle(node8, combinedPrinterStyle);

    INode node9 = graph.createNode(new PointD(964.64, 233.16));
    graph.addLabel(node9, "laser");
    graph.setStyle(node9, combinedPrinterStyle);

    INode node10 = graph.createNode(new PointD(570.69, 213.43));
    graph.addLabel(node10, "scanner");
    graph.setStyle(node10, combinedScannerStyle);

    INode node11 = graph.createNode(new PointD(759.14, 362.41));
    graph.addLabel(node11, "switch");
    graph.setStyle(node11, combinedSwitchStyle);

    INode node12 = graph.createNode(new PointD(866.5, 308.77));
    graph.addLabel(node12, "trumm");
    graph.setStyle(node12, combinedWorkstationStyle);

    INode node13 = graph.createNode(new PointD(836.21, 478.16));
    graph.addLabel(node13, "garelli");
    graph.setStyle(node13, combinedWorkstationStyle);

    INode node14 = graph.createNode(new PointD(700.4, 263.83));
    graph.addLabel(node14, "brocken");
    graph.setStyle(node14, combinedWorkstationStyle);

    INode node15 = graph.createNode(new PointD(664.83, 462.74));
    graph.addLabel(node15, "kreidler");
    graph.setStyle(node15, combinedWorkstationStyle);

    INode node16 = graph.createNode(new PointD(727.38, 133.29));
    graph.addLabel(node16, "phaser");
    graph.setStyle(node16, combinedPrinterStyle);

    INode node17 = graph.createNode(new PointD(554.57, 546.24));
    graph.addLabel(node17, "phaser");
    graph.setStyle(node17, combinedPrinterStyle);

    INode node18 = graph.createNode(new PointD(315.66, 583.79));
    graph.addLabel(node18, "protz");
    graph.setStyle(node18, combinedWorkstationStyle);

    // create  edges between the nodes
    graph.createEdge(node2, node0);
    graph.createEdge(node2, node6);
    graph.createEdge(node2, node5);
    graph.createEdge(node2, node4);
    graph.createEdge(node2, node3);
    graph.createEdge(node0, node8);
    graph.createEdge(node5, node7);
    graph.createEdge(node5, node1);
    graph.createEdge(node11, node15);
    graph.createEdge(node11, node14);
    graph.createEdge(node11, node13);
    graph.createEdge(node11, node12);
    graph.createEdge(node12, node9);
    graph.createEdge(node13, node14);
    graph.createEdge(node14, node16);
    graph.createEdge(node14, node10);
    graph.createEdge(node2, node11);
    graph.createEdge(node15, node17);
    graph.createEdge(node2, node18);

  }

  /**
   * Helper function to retrieve a URL for a resource
   *
   * @param resourceFileName the filename of the resource in the <em>resources</em> subfolder.
   * @return the URL of the resource.
   */
  private URL getResourceUrl(String resourceFileName) {
    URL url = getClass().getResource("resources/" + resourceFileName);
    if (url == null) {
      throw new IllegalArgumentException("resource <resources/" + resourceFileName + "> not available.");
    }
    return url;
  }
}
