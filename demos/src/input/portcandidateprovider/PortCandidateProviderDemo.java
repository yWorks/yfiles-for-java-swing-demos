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
package input.portcandidateprovider;

import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.AbstractPortStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.input.AbstractPortCandidateProvider;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.PortCandidateValidity;
import com.yworks.yfiles.view.Pen;
import toolkit.AbstractDemo;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.geom.Ellipse2D;

/**
 * Customize the port relocation feature by implementing a custom {@link com.yworks.yfiles.view.input.IPortCandidateProvider}.
 */
public class PortCandidateProviderDemo extends AbstractDemo {

  /**
   * Initializes this demo by configuring the input modes, setting the node style defaults, creating the sample graph
   * and registering the port candidate providers.
   */
  public void initialize() {
    IGraph graph = graphComponent.getGraph();

    // Initialize the input mode
    initializeInputMode();

    // Disable automatic cleanup of unconnected ports since some nodes have a predefined set of ports
    graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);
    // set a port style that makes the pre-defined ports visible
    ShapeNodeStyle roundNodeStyle = new ShapeNodeStyle();
    roundNodeStyle.setShape(ShapeNodeShape.ELLIPSE);
    roundNodeStyle.setPaint(Color.BLACK);
    roundNodeStyle.setPen(null);
    NodeStylePortStyleAdapter simplePortStyle = new NodeStylePortStyleAdapter(roundNodeStyle);
    simplePortStyle.setRenderSize(new SizeD(3, 3));
    graph.getNodeDefaults().getPortDefaults().setStyle(simplePortStyle);
    // enable the undo feature
    graph.setUndoEngineEnabled(true);

    // register custom provider implementations
    registerPortCandidateProvider();

    // initialize the graph
    createSampleGraph();
  }

  private void initializeInputMode() {
    // set up our InputMode for this demo.
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    // disable the interactive creation of nodes to focus on the sample graph
    inputMode.setCreateNodeAllowed(false);
    // also restrict the interaction to edges and bends
    GraphItemTypes edgesOrBends = GraphItemTypes.EDGE.or(GraphItemTypes.BEND);
    inputMode.setClickSelectableItems(edgesOrBends);
    inputMode.setSelectableItems(edgesOrBends);
    inputMode.setDeletableItems(edgesOrBends);
    // disable focusing of items
    inputMode.setFocusableItems(GraphItemTypes.NONE);
    // disable label editing
    inputMode.setEditLabelAllowed(false);
    // or copy/paste
    inputMode.setClipboardOperationsAllowed(false);
    // Finally, set the input mode to the graph component.
    graphComponent.setInputMode(inputMode);
  }

  /**
   * Centers the graph in the graph component.
   */
  public void onVisible() {
    // center the graph
    graphComponent.fitGraphBounds();
  }

  /**
   * Constructs a sample graph that contains nodes that demonstrates each of the custom {@link
   * com.yworks.yfiles.view.input.IPortCandidateProvider} above.
   */
  private void createSampleGraph() {
    IGraph graph = graphComponent.getGraph();

    createNode(graph, 100, 100, 80, 30, Colors.FIREBRICK, "No Edge");
    createNode(graph, 350, 100, 80, 30, Colors.FOREST_GREEN, "Green Only");
    createNode(graph, 100, 200, 80, 30, Colors.FOREST_GREEN, "Green Only");
    createNode(graph, 350, 200, 80, 30, Colors.FIREBRICK, "No Edge");

    // The blue nodes have predefined ports
    IPortStyle portStyle = new ColorPortStyle();

    INode blue1 = createNode(graph, 100, 300, 80, 30, Colors.ROYAL_BLUE, "One   Port");
    graph.addPort(blue1, blue1.getLayout().getCenter(), portStyle).setTag(Colors.BLACK);

    INode blue2 = createNode(graph, 350, 275, 100, 100, Colors.ROYAL_BLUE, "Many Ports");
    // pre-define a bunch of ports at the outer border of one of the blue nodes
    AbstractPortCandidateProvider portCandidateProvider = IPortCandidateProvider.fromShapeGeometry(blue2, 0, 0.25, 0.5, 0.75);
    portCandidateProvider.setStyle(portStyle);
    portCandidateProvider.setTag(Colors.BLACK);
    Iterable<IPortCandidate> candidates = portCandidateProvider.getSourcePortCandidates(
        graphComponent.getInputModeContext());
    candidates.forEach(portCandidate -> {
      if (portCandidate.getValidity() != PortCandidateValidity.DYNAMIC) {
        portCandidate.createPort(graphComponent.getInputModeContext());
      }
    });

    // The orange node
    createNode(graph, 100, 400, 100, 100, Colors.DARK_ORANGE, "Dynamic Ports");

    INode n = createNode(graph, 100, 540, 100, 100, Colors.PURPLE, "Individual\nPort\nConstraints");
    addIndividualPorts(graph, n);

    INode n2 = createNode(graph, 350, 540, 100, 100, Colors.PURPLE, "Individual\nPort\nConstraints");
    addIndividualPorts(graph, n2);

    // The olive node
    createNode(graph, 350, 410, 100, 80, Colors.OLIVE, "No\nParallel\nEdges");
  }

  /**
   * Convenience method to create a node with a label.
   */
  private INode createNode(IGraph graph, double x, double y, double w, double h, Color color, String labelText) {
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(color);
    INode node = graph.createNode(new RectD(x, y, w, h), nodeStyle, color);
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setFont(new Font("Dialog", Font.BOLD, 12));
    labelStyle.setTextPaint(Color.WHITE);
    labelStyle.setUsingFractionalFontMetricsEnabled(true);
    graph.addLabel(node, labelText, InteriorLabelModel.CENTER, labelStyle);
    return node;
  }

  /**
   * Adds ports with different colors to the node.
   */
  private void addIndividualPorts(IGraph graph, INode node) {
    IPortStyle portStyle = new ColorPortStyle();
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.25, 0), PointD.ORIGIN), portStyle, Colors.FIREBRICK);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.75, 0), PointD.ORIGIN), portStyle, Colors.FOREST_GREEN);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0, 0.25), PointD.ORIGIN), portStyle, Colors.BLACK);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0, 0.75), PointD.ORIGIN), portStyle, Colors.BLACK);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(1, 0.25), PointD.ORIGIN), portStyle, Colors.ROYAL_BLUE);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(1, 0.75), PointD.ORIGIN), portStyle, Colors.DARK_ORANGE);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.25, 1), PointD.ORIGIN), portStyle, Colors.PURPLE);
    graph.addPort(node, FreeNodePortLocationModel.INSTANCE.createParameter(new PointD(0.75, 1), PointD.ORIGIN), portStyle, Colors.PURPLE);
  }

  /**
   * Registers a callback function as decorator that provides a custom
   * {@link IPortCandidateProvider} for each node.
   * This callback function is called whenever a node in the graph is queried
   * for its <code>IPortCandidateProvider</code>. In this case, the 'node'
   * parameter will be assigned that node.
   */
  private void registerPortCandidateProvider() {
    NodeDecorator nodeDecorator = graphComponent.getGraph().getDecorator().getNodeDecorator();
    nodeDecorator.getPortCandidateProviderDecorator().setFactory(
        node -> {
          // obtain the tag from the node
          Object nodeTag = node.getTag();

          // see if it is a known tag
          if (nodeTag instanceof Color) {
            // and decide what implementation to provide
            if (Colors.FIREBRICK.equals(nodeTag)) {
              return new RedPortCandidateProvider(node);
            } else if (Colors.ROYAL_BLUE.equals(nodeTag)) {
              return new BluePortCandidateProvider(node);
            } else if (Colors.FOREST_GREEN.equals(nodeTag)) {
              return new GreenPortCandidateProvider(node);
            } else if (Colors.DARK_ORANGE.equals(nodeTag)) {
              return new OrangePortCandidateProvider(node);
            } else if (Colors.PURPLE.equals(nodeTag)) {
              return new PurplePortCandidateProvider(node);
            } else if (Colors.OLIVE.equals(nodeTag)) {
              return new OlivePortCandidateProvider(node);
            }
          }
          // otherwise revert to default behavior
          return null;
        });
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new PortCandidateProviderDemo().start();
    });
  }

  /**
   *  A very simple port style implementation that uses the color in the port's tag.
   */
  static class ColorPortStyle extends AbstractPortStyle {

    private final int renderSize;
    private final double renderSizeHalf;

    public ColorPortStyle() {
      this(6);
    }

    public ColorPortStyle(int renderSize) {
      super();
      this.renderSize = renderSize;
      this.renderSizeHalf = renderSize * 0.5;
    }

    @Override
    protected ShapeVisual createVisual(IRenderContext context, IPort port) {
      Color color = port.getTag() instanceof Color ? (Color) port.getTag() : Colors.WHITE;
      IPoint location = getLocation(port);
      Ellipse2D ellipse = new Ellipse2D.Double(location.getX() - renderSizeHalf, location.getY() - renderSizeHalf, renderSize, renderSize);
      return new ShapeVisual(ellipse, Pen.getGray(), color);
    }

    @Override
    protected IVisual updateVisual(IRenderContext context, IVisual oldVisual, IPort port) {
      if (!(oldVisual instanceof ShapeVisual)) {
        return createVisual(context, port);
      }

      ShapeVisual shapeVisual = (ShapeVisual) oldVisual;

      Ellipse2D ellipse = (Ellipse2D) shapeVisual.getShape();
      IPoint location = getLocation(port);
      ellipse.setFrame(location.getX() - renderSizeHalf, location.getY() - renderSizeHalf, renderSize, renderSize);
      return shapeVisual;
    }

    @Override
    protected RectD getBounds(ICanvasContext context, IPort port) {
      IPoint location = getLocation(port);
      return new RectD(location.getX() - renderSizeHalf, location.getY() - renderSizeHalf, renderSize, renderSize);
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
  }
}
