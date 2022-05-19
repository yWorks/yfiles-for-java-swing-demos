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
package input.reshapehandleproviderconfiguration;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IReshapeHandler;
import com.yworks.yfiles.view.input.NodeReshapeHandleProvider;
import com.yworks.yfiles.view.input.NodeReshapeHandlerHandle;
import com.yworks.yfiles.view.input.ReshapePolicy;
import toolkit.AbstractDemo;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

/**
 * Customize the resize behavior of nodes by implementing a custom
 * {@link com.yworks.yfiles.view.input.IReshapeHandleProvider}.
 */
public class ReshapeHandleProviderConfigurationDemo extends AbstractDemo {

  /**
   * Registers a callback function as a decorator that provides a custom {@link com.yworks.yfiles.view.input.IReshapeHandleProvider}
   * for each node. <p> This callback function is called whenever a node in the graph is queried for its
   * <code>IReshapeHandleProvider</code>. In this case, the 'node' parameter will be set to that node and the
   * 'delegateHandler' parameter will be set to the reshape handle provider that would have been returned without
   * setting this function as a decorator. </p>
   */
  public void registerReshapeHandleProvider(Rectangle2D boundaryRectangle) {
    NodeDecorator nodeDecorator = graphComponent.getGraph().getDecorator().getNodeDecorator();

    // deactivate reshape handling for the red node
    nodeDecorator.getReshapeHandleProviderDecorator().hideImplementation(
        node -> Colors.FIREBRICK.equals(node.getTag()));

    // return customized reshape handle provider for the orange, blue and green node
    nodeDecorator.getReshapeHandleProviderDecorator().setFactory(
        node -> Colors.DARK_ORANGE.equals(node.getTag())
            || Colors.ROYAL_BLUE.equals(node.getTag())
            || Colors.FOREST_GREEN.equals(node.getTag())
            || Colors.PURPLE.equals(node.getTag())
            || Colors.GRAY.equals(node.getTag()),
        node -> {
          // Obtain the tag from the node
          Object nodeTag = node.getTag();
          RectD maximumBoundingArea = RectD.fromRectangle2D(boundaryRectangle);

          // Create a default reshape handle provider for nodes
          IReshapeHandler reshapeHandler = node.lookup(IReshapeHandler.class);
          NodeReshapeHandleProvider provider = new NodeReshapeHandleProvider(node, reshapeHandler,
              HandlePositions.BORDER);

          // Customize the handle provider depending on the node's color
          if (Colors.DARK_ORANGE.equals(nodeTag)) {
            // Restrict the node bounds to the boundaryRectangle
            provider.setMaximumBoundingArea(maximumBoundingArea);
          } else if (Colors.FOREST_GREEN.equals(nodeTag)) {
            // Show only handles at the corners and always use aspect ratio resizing
            provider.setHandlePositions(HandlePositions.CORNERS);
            provider.setRatioReshapeRecognizer(IEventRecognizer.ALWAYS);
          } else if (Colors.ROYAL_BLUE.equals(nodeTag)) {
            // Restrict the node bounds to the boundaryRectangle and
            // show only handles at the corners and always use aspect ratio resizing
            provider.setMaximumBoundingArea(maximumBoundingArea);
            provider.setHandlePositions(HandlePositions.CORNERS);
            provider.setRatioReshapeRecognizer(IEventRecognizer.ALWAYS);
          } else if (Colors.PURPLE.equals(nodeTag)) {
            provider = new PurpleNodeReshapeHandleProvider(node, reshapeHandler);
          } else if (Colors.GRAY.equals(nodeTag)) {
            provider.setHandlePositions(HandlePositions.SOUTH_EAST);
            provider.setCenterReshapeRecognizer(IEventRecognizer.ALWAYS);
          }
          return provider;
        });
  }

  /**
   * Initializes this demo by configuring the input mode and the model item lookup and creating an example graph
   * together with an enclosing rectangle some of the nodes may not stretch over.
   */
  public void initialize() {
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(new Color(153, 153, 153));
    graphComponent.getGraph().getNodeDefaults().setStyle(nodeStyle);

    // initialize the input mode
    initializeInputMode();

    // Create the rectangle that limits the movement of some nodes
    Rectangle2D.Double boundaryRectangle = new Rectangle2D.Double(20, 20, 480, 550);

    // and add it to the GraphComponent using a black border and a transparent fill
    ShapeVisual visual = new ShapeVisual(boundaryRectangle, new Pen(Colors.BLACK, 2), Colors.TRANSPARENT);
    graphComponent.getRootGroup().addChild(visual, ICanvasObjectDescriptor.VISUAL);

    registerReshapeHandleProvider(boundaryRectangle);

    // initialize the graph
    createSampleGraph(graphComponent.getGraph());

    // enable Undo/Redo for all edits after the initial graph has been constructed
    graphComponent.getGraph().setUndoEngineEnabled(true);
  }

  private void initializeInputMode() {
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    // do not allow for moving any graph items
    inputMode.setMovableItems(GraphItemTypes.NONE);
    // disable element creation and deletion
    inputMode.setCreateNodeAllowed(false);
    inputMode.setCreateEdgeAllowed(false);
    inputMode.setDeletableItems(GraphItemTypes.NONE);
    // disable label editing
    inputMode.setEditLabelAllowed(false);
    // or copy/paste
    inputMode.setClipboardOperationsAllowed(false);

    graphComponent.setInputMode(inputMode);
  }

  /**
   * Centers the displayed content in the graph component.
   */
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Creates the sample graph with four nodes. Each node has a different color that indicates which {@link
   * com.yworks.yfiles.view.input.IReshapeHandleProvider} is used.
   */
  private void createSampleGraph(IGraph graph) {
    createNode(graph, 80, 100, 140, 30, Colors.FIREBRICK, Colors.WHITE_SMOKE, "Fixed Size");
    createNode(graph, 300, 100, 140, 30, Colors.FOREST_GREEN, Colors.WHITE_SMOKE, "Keep Aspect Ratio");
    createNode(graph, 80, 250, 140, 50, Colors.GRAY, Colors.WHITE_SMOKE, "Keep Center");
    createNode(graph, 300, 250, 140, 50, Colors.PURPLE, Colors.WHITE_SMOKE, "Keep Aspect Ratio\nat corners");
    createNode(graph, 80, 410, 140, 30, Colors.DARK_ORANGE, Colors.BLACK, "Limited to Rectangle");
    createNode(graph, 300, 400, 140, 50, Colors.ROYAL_BLUE, Colors.WHITE_SMOKE,
        "Limited to Rectangle\nand Keep Aspect Ratio");
  }

  /**
   * Creates a sample node for this demo.
   */
  private static void createNode(IGraph graph, double x, double y, double w, double h, Color fillColor, Color textColor, String labelText) {
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(fillColor);
    INode node = graph.createNode(new RectD(x, y, w, h), nodeStyle, fillColor);
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setFont(new Font("Dialog", Font.BOLD, 12));
    labelStyle.setTextPaint(textColor);
    labelStyle.setUsingFractionalFontMetricsEnabled(true);
    graph.addLabel(node, labelText, InteriorLabelModel.CENTER, labelStyle);
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new ReshapeHandleProviderConfigurationDemo().start();
    });
  }


  /**
   * A NodeReshapeHandleProvider for purple nodes that provides different handles for corners and borders.
   */
  private static class PurpleNodeReshapeHandleProvider extends NodeReshapeHandleProvider {
    public PurpleNodeReshapeHandleProvider(INode node, IReshapeHandler reshapeHandler) {
      super(node, reshapeHandler, HandlePositions.BORDER);
    }

    public IHandle getHandle(IInputModeContext inputModeContext, HandlePositions position) {
      NodeReshapeHandlerHandle handle = new NodeReshapeHandlerHandle(getNode(), getReshapeHandler(), position);

      boolean atCorner = position.contains(HandlePositions.CORNERS);
      if (atCorner) {
        // handles at corners shall always keep the aspect ratio
        handle.setReshapePolicy(ReshapePolicy.PROJECTION);
        handle.setRatioReshapeRecognizer(IEventRecognizer.ALWAYS);
        handle.setType(HandleTypes.RESIZE);
      } else {
        // handles at the sides shall ignore the aspect ratio and use another handle visualization
        handle.setReshapePolicy(ReshapePolicy.NONE);
        handle.setRatioReshapeRecognizer(IEventRecognizer.NEVER);
        handle.setType(HandleTypes.WARP);
      }

      return handle;
    }
  }


}
