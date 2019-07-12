/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package input.reshapehandleprovider;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.utils.FlagsEnum;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.view.input.ConstrainedHandle;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;
import com.yworks.yfiles.view.Pen;
import toolkit.AbstractDemo;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

/**
 * Customize the resize behavior of nodes by implementing a custom
 * {@link com.yworks.yfiles.view.input.IReshapeHandleProvider}.
 */
public class ReshapeHandleProviderDemo extends AbstractDemo {

  /**
   * Registers a callback function as a decorator that provides a custom {@link com.yworks.yfiles.view.input.IReshapeHandleProvider}
   * for each node. <p> This callback function is called whenever a node in the graph is queried for its
   * <code>IReshapeHandleProvider</code>. In this case, the 'node' parameter will be set to that node and the
   * 'delegateHandler' parameter will be set to the reshape handle provider that would have been returned without
   * setting this function as a decorator. </p>
   */
  public void registerReshapeHandleProvider(Rectangle2D boundaryRectangle) {
    NodeDecorator nodeDecorator = graphComponent.getGraph().getDecorator().getNodeDecorator();
    nodeDecorator.getReshapeHandleProviderDecorator().setImplementationWrapper(
        (node, delegateProvider) -> {
          // Obtain the tag from the node
          Object nodeTag = node.getTag();

          // Check if it is a known tag and choose the respective implementation.
          // Fallback to the default behavior otherwise.
          if (Colors.DARK_ORANGE.equals(nodeTag)) {
            // An implementation that delegates certain behavior to the default implementation
            return new OrangeReshapeHandleProvider(boundaryRectangle, delegateProvider);
          } else if (Colors.FIREBRICK.equals(nodeTag)) {
            // A simple implementation that prohibits resizing
            return new RedReshapeHandleProvider();
          } else if (Colors.ROYAL_BLUE.equals(nodeTag)) {
            // An implementation that uses two levels of delegation to create a combined behavior
            return new OrangeReshapeHandleProvider(boundaryRectangle, new GreenReshapeHandleProvider(delegateProvider, node));
          } else if (Colors.FOREST_GREEN.equals(nodeTag)) {
            // Another implementation that delegates certain behavior to the default implementation
            return new GreenReshapeHandleProvider(delegateProvider, node);
          } else {
            return delegateProvider;
          }
        });
  }

  /**
   * Initializes this demo by configuring the input mode and the model item lookup and creating an example graph together
   * with an enclosing rectangle some of the nodes may not stretch over.
   */
  public void initialize() {
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(new Color(153, 153, 153));
    graphComponent.getGraph().getNodeDefaults().setStyle(nodeStyle);

    // initialize the input mode
    initializeInputMode();

    // Create the rectangle that limits the movement of some nodes
    Rectangle2D.Double boundaryRectangle = new Rectangle2D.Double(20, 20, 480, 400);

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
    graphComponent.fitContent();
  }

  /**
   * Creates the sample graph with four nodes. Each node has a different color that indicates which {@link
   * com.yworks.yfiles.view.input.IReshapeHandleProvider} is used.
   */
  private void createSampleGraph(IGraph graph) {
    createNode(graph, 80, 100, 140, 30, Colors.FIREBRICK, Colors.WHITE_SMOKE, "Fixed Size");
    createNode(graph, 300, 100, 140, 30, Colors.FOREST_GREEN, Colors.WHITE_SMOKE, "Keep Aspect Ratio");
    createNode(graph, 80, 260, 140, 30, Colors.DARK_ORANGE, Colors.BLACK, "Limited to Rectangle");
    createNode(graph, 300, 250, 150, 50, Colors.ROYAL_BLUE, Colors.WHITE_SMOKE, "Limited to Rectangle\nand Keep Aspect Ratio");
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
      new ReshapeHandleProviderDemo().start();
    });
  }

  /**
   * An {@link IReshapeHandleProvider} that limits the resizing of a node to be within an enclosing rectangle
   * and delegates for other aspects to another (the original) handler.
   */
  private static class OrangeReshapeHandleProvider implements IReshapeHandleProvider {
    private final Rectangle2D boundaryRectangle;
    private final IReshapeHandleProvider delegateProvider;

    public OrangeReshapeHandleProvider(Rectangle2D boundaryRectangle, IReshapeHandleProvider delegateProvider) {
      this.boundaryRectangle = boundaryRectangle;
      this.delegateProvider = delegateProvider;
    }

    /**
     * Returns the available handles of the delegate provider.
     */
    public HandlePositions getAvailableHandles(IInputModeContext inputModeContext) {
      return delegateProvider.getAvailableHandles(inputModeContext);
    }

    /**
     * Returns a handle for the given original position that is limited to
     * the bounds of the boundary rectangle of this class.
     */
    public IHandle getHandle(IInputModeContext inputModeContext, HandlePositions position) {
      // return handle that is constrained by a box
      IHandle handle = delegateProvider.getHandle(inputModeContext, position);
      return new BoxConstrainedHandle(handle, boundaryRectangle);
    }
  }

  /**
   * A {@link com.yworks.yfiles.view.input.ConstrainedHandle} that is limited to the interior of a given rectangle.
   */
  private static class BoxConstrainedHandle extends ConstrainedHandle {
    private final Rectangle2D boundaryRectangle;
    private RectD constraintRect;

    public BoxConstrainedHandle(IHandle handle, Rectangle2D boundaryRectangle) {
      super(handle);
      this.boundaryRectangle = boundaryRectangle;
    }

    /**
     * Makes sure that the constraintRect is set to the current boundary rectangle
     * and delegates to the base implementation.
     */
    protected void onInitialized(IInputModeContext inputModeContext, PointD originalLocation) {
      super.onInitialized(inputModeContext, originalLocation);
      constraintRect = new RectD(boundaryRectangle.getX(), boundaryRectangle.getY(), boundaryRectangle.getWidth(), boundaryRectangle.getHeight());
    }

    /**
     * Returns for the given new location the constrained location that is inside the boundary rectangle.
     */
    protected PointD constrainNewLocation(IInputModeContext context, PointD originalLocation, PointD newLocation) {
      // return location constrained by rectangle
      return newLocation.getConstrained(constraintRect);
    }
  }

  /**
   * An {@link com.yworks.yfiles.view.input.IReshapeHandleProvider} that doesn't provide any handles.
   */
  private static class RedReshapeHandleProvider implements IReshapeHandleProvider {

    /**
     * Returns the indicator for no valid position.
     */
    public HandlePositions getAvailableHandles(IInputModeContext inputModeContext) {
      return HandlePositions.NONE;
    }

    public IHandle getHandle(IInputModeContext inputModeContext, HandlePositions position) {
      // Never called since getAvailableHandles returns no valid position.
      return null;
    }
  }

  /**
   * An {@link IReshapeHandleProvider} that restricts the available
   * handles provided by the delegate provider to the ones in the four corners.
   * If the delegate provider doesn't provide all of these handles, this
   * handler doesn't do this as well. In addition, these handles have a
   * custom behavior: they maintain the current aspect ratio of the node.
   */
  private static class GreenReshapeHandleProvider implements IReshapeHandleProvider {
    private final IReshapeHandleProvider delegateProvider;
    private final INode node;

    public GreenReshapeHandleProvider(IReshapeHandleProvider delegateProvider, INode node) {
      this.delegateProvider = delegateProvider;
      this.node = node;
    }

    /**
     * Returns the available handles provided by the delegate provider
     * restricted to the ones in the four corners.
     */
    public HandlePositions getAvailableHandles(IInputModeContext inputModeContext) {
      // return only corner handles
      return FlagsEnum.and(
          delegateProvider.getAvailableHandles(inputModeContext),
          FlagsEnum.or(
              FlagsEnum.or(HandlePositions.NORTH_EAST, HandlePositions.NORTH_WEST),
              FlagsEnum.or(HandlePositions.SOUTH_EAST, HandlePositions.SOUTH_WEST)));
    }

    /**
     * Returns a custom handle to maintains the aspect ratio of the node.
     */
    public IHandle getHandle(IInputModeContext inputModeContext, HandlePositions position) {
      return new AspectRatioHandle(delegateProvider.getHandle(inputModeContext, position), position, node.getLayout());
    }
  }

  /**
   * A handle that maintains the aspect ratio of the node.
   * <p>
   * Note that the simpler solution for this use case is subclassing {@link ConstrainedHandle},
   * however the interface is completely implemented for illustration, here.
   * </p>
   */
  private static class AspectRatioHandle implements IHandle {
    private static final int MIN_SIZE = 5;
    private final IHandle handle;
    private final HandlePositions position;
    private final IRectangle layout;
    private PointD lastLocation;
    private double ratio;
    private SizeD originalSize;

    public AspectRatioHandle(IHandle handle, HandlePositions position, IRectangle layout) {
      this.handle = handle;
      this.position = position;
      this.layout = layout;
    }

    @Override
    public HandleTypes getType() {
      return handle.getType();
    }

    @Override
    public Cursor getCursor() {
      return handle.getCursor();
    }

    @Override
    public IPoint getLocation() {
      return handle.getLocation();
    }

    /**
     * Stores the initial location and aspect ratio for reference, and calls the base method.
     */
    public void initializeDrag(IInputModeContext inputModeContext) {
      handle.initializeDrag(inputModeContext);
      lastLocation = new PointD(handle.getLocation());
      originalSize = new SizeD(layout.toSizeD());
      if (HandlePositions.NORTH_WEST.equals(position) ||
          HandlePositions.SOUTH_EAST.equals(position)) {
        ratio = layout.getWidth() / layout.getHeight();
      } else if (HandlePositions.NORTH_EAST.equals(position) ||
          HandlePositions.SOUTH_WEST.equals(position)) {
        ratio = -layout.getWidth() / layout.getHeight();
      } else {
        throw new IllegalArgumentException();
      }
    }

    /**
     * Constrains the movement to maintain the aspect ratio. This is done
     * by calculating the constrained location for the given new location,
     * and invoking the original handler with the constrained location.
     */
    public void handleMove(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
      // For the given new location, the larger node side specifies the actual size change.
      PointD deltaDrag = PointD.subtract(newLocation, originalLocation);

      // manipulate the drag so that it respects the boundaries and enforces the ratio.
      if (Math.abs(ratio) > 1) {
        // the width is larger, so we take the north or south position as indicator for the dragY calculation
        // if the south handles are dragged, we have to add the value of the dragDeltaY to the original height
        // otherwise, we have to subtract the value.
        // calculate the dragX in respect to the dragY

        // the sign basically indicates from which side we are dragging and thus if
        // we have to add or subtract the drag delta y to the height
        double sign = (HandlePositions.SOUTH_EAST.equals(position) || HandlePositions.SOUTH_WEST.equals(position)) ? 1 : -1;
        double newHeight = originalSize.getHeight() + sign * (deltaDrag.getX() / ratio);

        if (newHeight > MIN_SIZE) {
          // if the new height is larger then the minimum size, set the deltaDragY to the deltaDragX with respect to the ratio.
          deltaDrag = new PointD(deltaDrag.getX(), deltaDrag.getX() / ratio);
        } else {
          // if the new height would fall below the minimum size, adjust the dragY so that the minimum size is satisfied and
          // then set the deltaDragX according to that value.
          double newDragY = Math.signum(deltaDrag.getX() / ratio) * (originalSize.getHeight() - MIN_SIZE);
          deltaDrag = new PointD(newDragY * ratio, newDragY);
        }
      } else {
        // the height is larger, so we take the west or east position as indicator for the dragX calculation
        // if the west handles are dragged, we have to add the value of the dragDeltaX to the original width
        // otherwise, we have to subtract the value.
        // calculate the dragY in respect to the dragX

        // the sign basically indicates from which side we are dragging and thus if
        // we have to add or subtract the drag delta x to the width
        double sign = (HandlePositions.NORTH_EAST.equals(position) || HandlePositions.SOUTH_EAST.equals(position)) ? 1 : -1;
        double newWidth = originalSize.getWidth() + sign * (deltaDrag.getY() * ratio);
        if (newWidth > MIN_SIZE) {
          // if the new width is larger then the minimum size, set the deltaDragX to the deltaDragY with respect to the ratio.
          deltaDrag = new PointD(deltaDrag.getY() * ratio, deltaDrag.getY());
        } else {
          // if the new width would fall below the minimum size, adjust the dragX so that the minimum size is satisfied and
          // then set the deltaDragY according to that value.
          double newDragX = Math.signum(deltaDrag.getY() * ratio) * (originalSize.getWidth() - MIN_SIZE);
          deltaDrag = new PointD(newDragX, newDragX / ratio);
        }
      }

      newLocation = PointD.add(originalLocation, deltaDrag);

      if (newLocation != lastLocation) {
        handle.handleMove(inputModeContext, originalLocation, newLocation);
        lastLocation = newLocation;
      }
    }

    @Override
    public void cancelDrag(IInputModeContext inputModeContext, PointD originalLocation) {
      handle.cancelDrag(inputModeContext, originalLocation);
    }

    @Override
    public void dragFinished(IInputModeContext inputModeContext, PointD originalLocation, PointD newLocation) {
      handle.dragFinished(inputModeContext, originalLocation, newLocation);
    }
  }
}
