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
package input.orthogonaledges;

import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.EdgeDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeHelper;
import com.yworks.yfiles.view.Pen;
import toolkit.AbstractDemo;

import java.awt.Color;
import java.awt.EventQueue;

/**
 * Customize orthogonal edge editing by implementing the
 * {@link com.yworks.yfiles.view.input.IOrthogonalEdgeHelper IOrthogonalEdgeHelper} interface.
 */
public class OrthogonalEdgesDemo extends AbstractDemo {

  /**
   * Initializes the demo by setting the default styles, creating the sample graph and configuring the input modes.
   */
  public void initialize() {
    // setup initial graph
    IGraph graph = graphComponent.getGraph();
    initializeGraphDefaults(graph);
    createSampleGraph(graph);

    // and enable the undo feature.
    graph.setUndoEngineEnabled(true);

    // setup user interaction
    initializeInputModes();
  }

  /**
   * Centers the graph in the graph component.
   */
  public void onVisible() {
    // center the graph
    graphComponent.fitGraphBounds();
  }

  /**
   * Sets some default values for nodes and ports of the given graph.
   */
  private void initializeGraphDefaults(IGraph graph) {
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Color.ORANGE);
    graph.getNodeDefaults().setStyle(nodeStyle);
    graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);
  }

  /**
   * Creates and configures the {@link GraphEditorInputMode} to enable orthogonal edge editing
   * and configures the snapping behavior.
   * Finally the custom {@link com.yworks.yfiles.view.input.IOrthogonalEdgeHelper}s are registered.
   */
  private void initializeInputModes() {
    // create a default editor input mode
    GraphEditorInputMode inputMode = new GraphEditorInputMode();

    // enable orthogonal edge editing
    OrthogonalEdgeEditingContext orthogonalEdgeEditingContext = new OrthogonalEdgeEditingContext();
    inputMode.setOrthogonalEdgeEditingContext(orthogonalEdgeEditingContext);

    // disable the interactive creation of nodes or edges to focus on the sample graph
    inputMode.setCreateEdgeAllowed(false);
    inputMode.setCreateNodeAllowed(false);
    // also restrict the interaction to edges and bends
    GraphItemTypes edgesOrBends = GraphItemTypes.EDGE.or(GraphItemTypes.BEND);
    inputMode.setClickSelectableItems(edgesOrBends);
    inputMode.setSelectableItems(edgesOrBends);
    // disable the moving of arbitrary elements, we handle the movement manually
    inputMode.setMovableItems(GraphItemTypes.NONE);
    // disable focusing and deleting items
    inputMode.setFocusableItems(GraphItemTypes.NONE);
    inputMode.setDeletableItems(GraphItemTypes.NONE);
    // disable label editing
    inputMode.setEditLabelAllowed(false);
    // or copy/paste
    inputMode.setClipboardOperationsAllowed(false);

    // enable snapping for edges only...
    GraphSnapContext snapContext = new GraphSnapContext();
    snapContext.setCollectingNodeSnapLinesEnabled(false);
    snapContext.setCollectingNodePairCenterSnapLinesEnabled(false);
    snapContext.setCollectingNodePairSnapLinesEnabled(false);
    snapContext.setCollectingNodePairSegmentSnapLinesEnabled(false);
    snapContext.setCollectingNodeSizesEnabled(false);
    snapContext.setSnappingNodesToSnapLinesEnabled(false);
    snapContext.setSnappingOrthogonalMovementEnabled(false);
    inputMode.setSnapContext(snapContext);

    registerOrthogonalEdgeHelperDecorators();

    // and finally register our input mode with the component.
    graphComponent.setInputMode(inputMode);
  }

  /**
   * Creates custom {@link com.yworks.yfiles.view.input.IOrthogonalEdgeHelper}s and
   * registers them with the {@link EdgeDecorator} of the graph.
   * Additionally, it sets some other decorators that complete the desired behavior.
   */
  private void registerOrthogonalEdgeHelperDecorators() {
    EdgeDecorator edgeDecorator = graphComponent.getGraph().getDecorator().getEdgeDecorator();

    // Add different OrthogonalEdgeHelpers to demonstrate various custom behaviours
    edgeDecorator.getOrthogonalEdgeHelperDecorator().setImplementation(edge -> edge.getTag() == Colors.FIREBRICK, new RedOrthogonalEdgeHelper());

    // Green edges have the regular orthogonal editing behavior and therefore we don't need a custom implementation
    edgeDecorator.getOrthogonalEdgeHelperDecorator().setImplementation(edge -> edge.getTag() == Colors.FOREST_GREEN, new OrthogonalEdgeHelper());

    edgeDecorator.getOrthogonalEdgeHelperDecorator().setImplementation(edge -> edge.getTag() == Colors.PURPLE, new PurpleOrthogonalEdgeHelper());
    edgeDecorator.getOrthogonalEdgeHelperDecorator().setImplementation(edge -> edge.getTag() == Colors.DARK_ORANGE, new OrangeOrthogonalEdgeHelper());
    edgeDecorator.getOrthogonalEdgeHelperDecorator().setImplementation(edge -> edge.getTag() == Colors.ROYAL_BLUE, new BlueOrthogonalEdgeHelper());

    // Disable moving of the complete edge for orthogonal edges since this would create way too many bends
    edgeDecorator.getPositionHandlerDecorator().hideImplementation(
      edge -> (edge.getTag() == Colors.DARK_ORANGE) ||
              (edge.getTag() == Colors.FOREST_GREEN) ||
              (edge.getTag() == Colors.PURPLE)
      );

    // Add a custom BendCreator for blue edges that ensures orthogonality
    // if a bend is added to the first or last (non-orthogonal) segment
    edgeDecorator.getBendCreatorDecorator().setImplementation(edge -> edge.getTag() == Colors.ROYAL_BLUE, new BlueBendCreator());

    // Add a custom EdgePortHandleProvider to make the handles of purple and
    // orange edge move within the bounds of the node
    edgeDecorator.getEdgePortHandleProviderDecorator().setImplementationWrapper(edge -> edge.getTag() == Colors.PURPLE,
        (edge, provider) -> new ConstrainedEdgePortHandleProvider(provider));
    edgeDecorator.getEdgePortHandleProviderDecorator().setImplementation(edge -> edge.getTag() == Colors.DARK_ORANGE, new ConstrainedEdgePortHandleProvider());

    // Allow the relocating of an edge to another node
    edgeDecorator.getEdgeReconnectionPortCandidateProviderDecorator().setImplementation(IEdgeReconnectionPortCandidateProvider.ALL_NODE_CANDIDATES);
  }

  /**
   * Creates a graph with five orthogonal or partly orthogonal edges. The edges have different colors that are also
   * used as tag. That way, they can be treated differently during interactive change depending on their color.
   */
  private void createSampleGraph(IGraph graph) {
    createSubgraph(graph, Colors.FIREBRICK, 0, false);
    createSubgraph(graph, Colors.FOREST_GREEN, 110, false);
    createSubgraph(graph, Colors.PURPLE, 220, true);
    createSubgraph(graph, Colors.DARK_ORANGE, 330, false);

    // the blue edge has more bends then the other edges
    IEdge blueEdge = createSubgraph(graph, Colors.ROYAL_BLUE, 440, false);
    graph.clearBends(blueEdge);
    double sourcePortY = getLocation(blueEdge.getSourcePort()).getY();
    graph.addBend(blueEdge, new PointD(220, sourcePortY - 30));
    graph.addBend(blueEdge, new PointD(300, sourcePortY - 30));
    double targetPortY = getLocation(blueEdge.getTargetPort()).getY();
    graph.addBend(blueEdge, new PointD(300, targetPortY + 30));
    graph.addBend(blueEdge, new PointD(380, targetPortY + 30));

    graphComponent.updateContentRect();
  }

  /**
   * Creates the sample graph of the given color with two nodes and a single edge.
   */
  private IEdge createSubgraph(IGraph graph, Color color, double yOffset, boolean createPorts) {
    // Create two nodes
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(color);
    INode n1 = graph.createNode(new RectD(110, 100 + yOffset, 40, 40), nodeStyle, color);
    INode n2 = graph.createNode(new RectD(450, 130 + yOffset, 40, 40), nodeStyle, color);

    // Create an edge, either between the two nodes or between the nodes' ports
    // For the edge style, use a pen based on the color that is a tiny bit thicker than the normal pen
    IEdge edge;
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(new Pen(color, 1.5));
    if (!createPorts) {
      edge = graph.createEdge(n1, n2, edgeStyle, color);
    } else {
      IPort[] p1 = createSamplePorts(graph, n1, true);
      IPort[] p2 = createSamplePorts(graph, n2, false);
      edge = graph.createEdge(p1[1], p2[2], edgeStyle, color);
    }

    // Add bends that create a vertical segment in the middle of the edge
    PointD sourceLocation = getLocation(edge.getSourcePort());
    PointD targetLocation = getLocation(edge.getTargetPort());
    double x = (sourceLocation.getX() + targetLocation.getX()) / 2;
    graph.addBend(edge, new PointD(x, sourceLocation.getY()));
    graph.addBend(edge, new PointD(x, targetLocation.getY()));
    return edge;
  }

  /**
   * Adds some ports to the given node.
   */
  private static IPort[] createSamplePorts(IGraph graph, INode node, boolean toEastSide) {
    FreeNodePortLocationModel model = FreeNodePortLocationModel.INSTANCE;
    double x = toEastSide ? 0.9 : 0.1;
    IPort[] ports = new IPort[4];
    ports[0] = graph.addPort(node, model.createParameter(new PointD(x, 0.05), PointD.ORIGIN));
    ports[1] = graph.addPort(node, model.createParameter(new PointD(x, 0.35), PointD.ORIGIN));
    ports[2] = graph.addPort(node, model.createParameter(new PointD(x, 0.65), PointD.ORIGIN));
    ports[3] = graph.addPort(node, model.createParameter(new PointD(x, 0.95), PointD.ORIGIN));
    return ports;
  }

  /**
   * Gets a snapshot of the current location of the port.
   * Unlike {@link com.yworks.yfiles.graph.IPort#getLocation()} this does not return a dynamic point that always refers to the current location.
   * @param port The port to retrieve the location from.
   * @return The current port location.
   */
  private static PointD getLocation(IPort port) {
    IPortLocationModelParameter param = port.getLocationParameter();
    return param.getModel().getLocation(port, param);
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new OrthogonalEdgesDemo().start();
    });
  }
}
