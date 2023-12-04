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
package input.positionhandler;

import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.view.input.ConstrainedPositionHandler;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IPositionHandler;
import com.yworks.yfiles.view.Pen;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;
import toolkit.Palette;
import toolkit.Themes;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

/**
 * Customize and restrict the movement behavior of nodes by implementing a custom
 * {@link com.yworks.yfiles.view.input.IPositionHandler}.
 */
public class PositionHandlerDemo extends AbstractDemo {

  /**
   * Registers a callback function as decorator that provides a custom
   * {@link com.yworks.yfiles.view.input.IPositionHandler} for each node.
   * <p>
   *   This callback function is called whenever a node in the graph is queried
   *   for its <code>IPositionHandler</code>. In this case, the 'node' parameter will be set
   *   to that node and the 'delegateHandler' parameter will be set to the
   *   position handler that would have been returned without setting this
   *   function as decorator.
   * </p>
   */
  public void registerPositionHandler(Rectangle2D.Double boundaryRectangle) {
    NodeDecorator nodeDecorator = graphComponent.getGraph().getDecorator().getNodeDecorator();
    nodeDecorator.getPositionHandlerDecorator().setImplementationWrapper((node, delegateHandler) ->
    {
      // Obtain the tag from the node
      Object nodeTag = node.getTag();

      // Check if it is a known tag and choose the respective implementation.
      // Fallback to the default behavior otherwise.
      if (Themes.PALETTE_ORANGE.equals(nodeTag)) {
        // This implementation delegates certain behavior to the default implementation
        return new OrangePositionHandler(boundaryRectangle, node, delegateHandler);
      } else if (Themes.PALETTE_RED.equals(nodeTag)) {
        // A simple implementation that prohibits moving
        return new RedPositionHandler();
      } else if (Themes.PALETTE_LIGHTBLUE.equals(nodeTag)) {
        // Implementation that uses two levels of delegation to create a combined behavior
        return new OrangePositionHandler(boundaryRectangle, node, new GreenPositionHandler(delegateHandler));
      } else if (Themes.PALETTE_GREEN.equals(nodeTag)) {
        // Another implementation that delegates certain behavior to the default implementation
        return new GreenPositionHandler(delegateHandler);
      } else {
        return delegateHandler;
      }
    });
  }

  /**
   * Initializes this demo by configuring the input mode and the model item lookup and creating an example graph together
   * with an enclosing rectangle some of the nodes may not move outside.
   */
  public void initialize() {
    DemoStyles.initDemoStyles(graphComponent.getGraph());

    // initialize the input mode
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    // do not allow creating nodes and edges
    inputMode.setCreateNodeAllowed(false);
    inputMode.setCreateEdgeAllowed(false);
    // do not allow deleting items
    inputMode.setDeletableItems(GraphItemTypes.NONE);
    // do not allow resizing nodes
    inputMode.setShowHandleItems(GraphItemTypes.NONE);
    // do not allow label editing
    inputMode.setEditLabelAllowed(false);
    // or copy/paste
    inputMode.setClipboardOperationsAllowed(false);
    graphComponent.setInputMode(inputMode);

    // initialize the boundary rectangle
    Rectangle2D.Double boundaryRectangle = new Rectangle2D.Double(20, 20, 480, 400);
    // create a visual representation using a black border and a transparent fill
    ShapeVisual rectangle = new ShapeVisual(boundaryRectangle, new Pen(Colors.BLACK, 2), Colors.TRANSPARENT);
    // and add it to the graph component
    graphComponent.getRootGroup().addChild(rectangle, ICanvasObjectDescriptor.VISUAL);

    // initialize the graph
    createSampleGraph(graphComponent.getGraph());

    // enable Undo/Redo for all edits after the initial graph has been constructed
    graphComponent.getGraph().setUndoEngineEnabled(true);

    // register custom provider implementations
    registerPositionHandler(boundaryRectangle);
  }

  /**
   * Centers the displayed content in the graph component.
   */
  public void onVisible() {
    graphComponent.fitContent();
  }

  /**
   * Creates the sample graph. Each node has a different color that indicates which {@link
   * com.yworks.yfiles.view.input.IPositionHandler} is used.
   */
  private void createSampleGraph(IGraph graph) {
    createNode(graph, 100, 100, 100, 30, Themes.PALETTE_RED, "Unmovable");
    createNode(graph, 300, 100, 100, 30, Themes.PALETTE_GREEN, "One Axis");
    createNode(graph, 80, 250, 140, 40, Themes.PALETTE_ORANGE, "Limited to Rectangle");
    createNode(graph, 280, 250, 140, 40, Themes.PALETTE_LIGHTBLUE, "Limited to Rectangle\nand One Axis");
  }

  /**
   * Creates a sample node for this demo.
   */
  private static void createNode(IGraph graph, double x, double y, double w, double h, Palette palette, String labelText) {
    RectangleNodeStyle nodeStyle = DemoStyles.createDemoNodeStyle(palette);
    INode node = graph.createNode(new RectD(x, y, w, h), nodeStyle, palette);
    DefaultLabelStyle labelStyle = DemoStyles.createDemoNodeLabelStyle(palette);
    graph.addLabel(node, labelText, InteriorLabelModel.CENTER, labelStyle);
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new PositionHandlerDemo().start();
    });
  }

  /**
   * A {@link com.yworks.yfiles.view.input.ConstrainedPositionHandler} that limits the movement of a
   * node to be within an rectangle and delegates for other aspects to another (the original) handler.
   */
  private static class OrangePositionHandler extends ConstrainedPositionHandler {
    private final Rectangle2D.Double boundaryRectangle;
    private final INode node;
    private RectD boundaryPositionRectangle;

    public OrangePositionHandler(Rectangle2D.Double boundaryRectangle, INode node, IPositionHandler delegateHandler) {
      super(delegateHandler);
      this.boundaryRectangle = boundaryRectangle;
      this.node = node;
    }

    /**
     * Prepares the rectangle that is actually used to limit the node
     * position, besides the base functionality. Since a position handler
     * works on points, the actual rectangle must be a limit for the upper
     * left corner of the node and not for the node's bounding box.
     */
    protected void onInitialized(IInputModeContext inputModeContext, PointD originalLocation) {
      super.onInitialized(inputModeContext, originalLocation);
      // Shrink the rectangle by the node size to get the limits for the upper left node corner
      boundaryPositionRectangle = new RectD(
          boundaryRectangle.getX(),
          boundaryRectangle.getY(),
          boundaryRectangle.getWidth() - node.getLayout().getWidth(),
          boundaryRectangle.getHeight() - node.getLayout().getHeight());
    }

    /**
     * Returns the position that is constrained by the rectangle.
     */
    protected PointD constrainNewLocation(IInputModeContext context, PointD originalLocation, PointD newLocation) {
      return newLocation.getConstrained(boundaryPositionRectangle);
    }
  }

  /**
   * A position handler that prevents node movements. This implementation is
   * very simple since most methods do nothing at all.
   */
  private static class RedPositionHandler implements IPositionHandler {

    @Override
    public IPoint getLocation() {
      return PointD.ORIGIN;
    }

    @Override
    public void initializeDrag(IInputModeContext inputModeContext) {
    }

    @Override
    public void handleMove(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
    }

    @Override
    public void cancelDrag(IInputModeContext inputModeContext, PointD originalLocation) {
    }

    @Override
    public void dragFinished(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
    }
  }

  /**
   * A position handler that constrains the movement of a node to one axis (for each gesture) and delegates
   * for other aspects to another (the original) handler.
   * <p>
   * Note that the simpler solution for this use case is subclassing {@link ConstrainedPositionHandler},
   * however the interface is completely implemented for illustration, here.
   * </p>
   */
  private static class GreenPositionHandler implements IPositionHandler {
    private final IPositionHandler delegateHandler;
    private PointD lastLocation;

    public GreenPositionHandler(IPositionHandler delegateHandler) {
      this.delegateHandler = delegateHandler;
    }

    @Override
    public IPoint getLocation() {
      return delegateHandler.getLocation();
    }

    /**
     * Stores the initial location of the movement for reference, and calls the base method.
     */
    public void initializeDrag(IInputModeContext inputModeContext) {
      delegateHandler.initializeDrag(inputModeContext);
      lastLocation = new PointD(delegateHandler.getLocation());
    }

    /**
     * Constrains the movement to one axis. This is done by calculating the
     * constrained location for the given new location, and invoking the
     * original handler with the constrained location.
     */
    public void handleMove(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
      // The larger difference in coordinates specifies whether this is
      // a horizontal or vertical movement.
      PointD delta = PointD.subtract(newLocation, originalLocation);

      if (Math.abs(delta.getX()) > Math.abs(delta.getY())) {
        newLocation = new PointD(newLocation.getX(), originalLocation.getY());
      } else {
        newLocation = new PointD(originalLocation.getX(), newLocation.getY());
      }
      if (!newLocation.equals(lastLocation)) {
        delegateHandler.handleMove(inputModeContext, originalLocation, newLocation);
        lastLocation = newLocation;
      }
    }

    @Override
    public void cancelDrag(IInputModeContext inputModeContext, PointD originalLocation) {
      delegateHandler.cancelDrag(inputModeContext, originalLocation);
    }

    @Override
    public void dragFinished(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
      delegateHandler.dragFinished(inputModeContext, originalLocation, newLocation);
    }
  }
}
