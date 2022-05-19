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
package complete.rotatablenodes;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IReshapeHandler;
import com.yworks.yfiles.view.input.PortLocationModelParameterHandle;
import com.yworks.yfiles.view.input.SnapContext;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;

/**
 * A node reshape handle that adjusts its position according to the node rotation.
 */
public class RotatedNodeResizeHandle implements IHandle, IPoint {
  private static final double EIGHTH_FROM_CIRCLE = Math.PI * 0.25;

  private final HandlePositions position;
  private final INode node;
  private final IReshapeHandler reshapeHandler;

  private final boolean symmetricResize;
  private final List<IHandle> portHandles;
  private final OrientedRectangle initialLayout;

  private PointD dummyLocation;
  private SizeD dummySize;
  private RectD initialRect;


  RotatedNodeResizeHandle(HandlePositions position, INode node, IReshapeHandler reshapeHandler, boolean symmetricResize) {
    this.position = position;
    this.node = node;
    this.reshapeHandler = reshapeHandler;
    this.symmetricResize = symmetricResize;
    portHandles = new ArrayList<IHandle>();
    initialLayout = new OrientedRectangle(getNodeBasedOrientedRectangle());
  }

  /**
   * Returns the node rotation information.
   */
  private CachingOrientedRectangle getNodeBasedOrientedRectangle() {
    if (node.getStyle() instanceof RotatableNodeStyleDecorator) {
      return ((RotatableNodeStyleDecorator) node.getStyle()).getRotatedLayout(node);
    } else {
      return new CachingOrientedRectangle();
    }
  }

  /**
   * Sets the node rotation information.
   */
  private RectD setNodeLocationAndSize(IInputModeContext inputModeContext, PointD anchor, SizeD size) {
    IGraph graph = inputModeContext.getGraph();
    if (graph == null) {
      return RectD.EMPTY;
    }

    OrientedRectangle orientedRectangle = new OrientedRectangle(anchor.getX(), anchor.getY(),
            size.getWidth(), size.getHeight(), initialLayout.getUpX(), initialLayout.getUpY());
    PointD center = orientedRectangle.getCenter();

    return RectD.fromCenter(center, size);
  }

  /**
   * Returns whether or not the node is symmetrical resized.
   */
  public boolean getSymmetricResize() {
    return symmetricResize;
  }

  /**
   * Returns the visualization of the handle. In this case a dot that rotates nicely.
   */
  @Override
  public HandleTypes getType() {
    return HandleTypes.RESIZE;
  }

  @Override
  public Cursor getCursor() {
    CachingOrientedRectangle layout = getNodeBasedOrientedRectangle();
    double angle = layout.getAngle();
    int[] cursors = new int[] {
            Cursor.N_RESIZE_CURSOR,
            Cursor.NW_RESIZE_CURSOR,
            Cursor.W_RESIZE_CURSOR,
            Cursor.SW_RESIZE_CURSOR,
            Cursor.S_RESIZE_CURSOR,
            Cursor.SE_RESIZE_CURSOR,
            Cursor.E_RESIZE_CURSOR,
            Cursor.NE_RESIZE_CURSOR,
    };

    int index = 0;

    if (position.equals(HandlePositions.NORTH)) {
      index = 0;
    } else if (position.equals(HandlePositions.NORTH_WEST)) {
      index = 1;
    } else if (position.equals(HandlePositions.WEST)) {
      index = 2;
    } else if (position.equals(HandlePositions.SOUTH_WEST)) {
      index = 3;
    } else if (position.equals(HandlePositions.SOUTH)) {
      index = 4;
    } else if (position.equals(HandlePositions.SOUTH_EAST)) {
      index = 5;
    } else if(position.equals(HandlePositions.EAST)) {
      index = 6;
    } else if (position.equals(HandlePositions.NORTH_EAST)) {
      index = 7;
    }

    //Pick the right array index for the respective handle position
    //Then shift the array position according to the rotation angle
    index += (int) Math.round(angle / EIGHTH_FROM_CIRCLE);
    index %= cursors.length;
    if(index < 0) {
      index += cursors.length;
    }

    return Cursor.getPredefinedCursor(cursors[index]);
  }

  /**
   * The location of this handle considering the node rotation.
   */
  @Override
  public IPoint getLocation() {
    return getLocation(getNodeBasedOrientedRectangle(), position);
  }

  /**
   * Stores the initial layout of the node in case the user cancels the resizing.
   */
  @Override
  public void initializeDrag(IInputModeContext inputModeContext) {
    if (reshapeHandler != null) {
      //if there is a reshape handler, initialize to ensure proper handling of the parent group node
      reshapeHandler.initializeReshape(inputModeContext);
    }

    initialLayout.reshape(getNodeBasedOrientedRectangle());
    dummyLocation = initialLayout.getAnchorLocation();
    dummySize = initialLayout.getSize().toSizeD();
    initialRect = node.getLayout().toRectD();

    portHandles.clear();
    DelegatingContext portContext = new DelegatingContext(inputModeContext);

    for (IPort port : node.getPorts()) {
      DummyPortLocationModelParameterHandle portHandle = new DummyPortLocationModelParameterHandle(port);
      portHandle.initializeDrag(portContext);
      portHandles.add(portHandle);
    }
  }

  /**
   * Adjusts the node location and size according to the new handle location.
   */
  @Override
  public void handleMove(IInputModeContext iInputModeContext, PointD originalLocation, PointD newLocation) {
    //calculate how much the handle was moved
    PointD upNormal = new PointD(-initialLayout.getUpY(), initialLayout.getUpX());
    double deltaW = getWidthDelta(originalLocation, newLocation, upNormal);
    PointD up = initialLayout.getUp();
    double deltaH = getHeightDelta(originalLocation, newLocation, up);

    //add one or two times delta to the width to expand the node right and left
    dummySize = new SizeD(
            initialLayout.getWidth() + deltaW * (symmetricResize ? 2 : 1),
            initialLayout.getHeight() + deltaH * (symmetricResize ? 2 : 1));

    //calculate the new location. Depending on our handle position, a different corner of the node should stay fixed
    if (symmetricResize) {
      double dx = upNormal.getX() * deltaW + up.getX() * deltaH;
      double dy = upNormal.getY() * deltaW + up.getY() * deltaH;
      dummyLocation = PointD.subtract(initialLayout.getAnchorLocation(), new PointD(dx, dy));
    } else {
      double w = dummySize.getWidth() - initialLayout.getWidth();
      double h = dummySize.getHeight() - initialLayout.getHeight();

      if (position.equals(HandlePositions.NORTH_WEST)) {
        dummyLocation = PointD.subtract(initialLayout.getAnchorLocation(),
                new PointD(-up.getY() * w, up.getX() * w));
      }

      if (position.equals(HandlePositions.SOUTH) || position.equals(HandlePositions.SOUTH_WEST) ||
              position.equals(HandlePositions.WEST)) {
        dummyLocation = PointD.subtract(initialLayout.getAnchorLocation(),
                new PointD(up.getX() * h - up.getY() * w, up.getY() * h + up.getX() * w));
      }

      if (position.equals(HandlePositions.SOUTH_EAST)) {
        dummyLocation = PointD.subtract(initialLayout.getAnchorLocation(),
                new PointD(up.getX() * h, up.getY() * h));
      }

      if (position.equals(HandlePositions.NORTH) || position.equals(HandlePositions.NORTH_EAST) ||
              position.equals(HandlePositions.EAST)) {
        dummyLocation = initialLayout.getAnchorLocation();
      }
    }

    RectD newLayout = setNodeLocationAndSize(iInputModeContext, dummyLocation, dummySize);

    DelegatingContext portContext = new DelegatingContext(iInputModeContext);
    for (IHandle portHandle : portHandles) {
      portHandle.handleMove(portContext, dummyLocation, newLocation);
    }

    if (reshapeHandler != null) {
      //if there is a reshape handler, ensure proper handling of a parent group node
      reshapeHandler.handleReshape(iInputModeContext, initialRect, newLayout);
    }
  }

  /**
   * Returns the delta by which the width of the node was changed.
   */
  private double getWidthDelta(PointD originalLocation, PointD newLocation, PointD vector) {

    if(position.equals(HandlePositions.NORTH_WEST) || position.equals(HandlePositions.WEST) ||
            position.equals(HandlePositions.SOUTH_WEST)) {
      // calculate the total distance the handle has been moved in this drag gesture
      // max with minus half the node size - because the node can't shrink below zero
      return Math.max(vector.scalarProduct(PointD.subtract(originalLocation, newLocation)),
              -initialLayout.getWidth() * (symmetricResize ? 0.5d : 1d));
    }

    if(position.equals(HandlePositions.NORTH_EAST) || position.equals(HandlePositions.EAST) ||
            position.equals(HandlePositions.SOUTH_EAST)) {
      return  Math.max(vector.scalarProduct(PointD.subtract(newLocation, originalLocation)),
              -initialLayout.getWidth() * (symmetricResize ? 0.5d : 1d));
    }
     //default return
    return 0.0d;
  }

  /**
   * Returns the delta by which the height of the node was changed.
   */
  private double getHeightDelta(PointD originalLocation, PointD newLocation, PointD vector) {

    if(position.equals(HandlePositions.NORTH_WEST) || position.equals(HandlePositions.NORTH) ||
            position.equals(HandlePositions.NORTH_EAST)) {
      return Math.max(vector.scalarProduct(PointD.subtract(newLocation, originalLocation)),
              -initialLayout.getHeight() * (symmetricResize ? 0.5d : 1d));
    }

    if(position.equals(HandlePositions.SOUTH_WEST) || position.equals(HandlePositions.SOUTH) ||
            position.equals(HandlePositions.SOUTH_EAST)) {
      return  Math.max(vector.scalarProduct(PointD.subtract(originalLocation, newLocation)),
              -initialLayout.getHeight() * (symmetricResize ? 0.5d : 1d));
    }
    //default return
    return 0.0d;
  }

  /**
   * Restores the original node layout.
   */
  @Override
  public void cancelDrag(IInputModeContext iInputModeContext, PointD originalLocation) {
    setNodeLocationAndSize(iInputModeContext, initialLayout.getAnchorLocation(), initialLayout.getSize().toSizeD());
    DelegatingContext portContext = new DelegatingContext(iInputModeContext);
    for(IHandle portHandle : portHandles) {
      portHandle.cancelDrag(portContext, originalLocation);
    }
    portHandles.clear();

    if(reshapeHandler != null) {
      //if there is a reshape handler, ensure proper handling of a parent group node
      reshapeHandler.cancelReshape(iInputModeContext, initialRect);
    }
  }

  /**
   * Applies the new node layout.
   */
  @Override
  public void dragFinished(IInputModeContext iInputModeContext, PointD originalLocation, PointD newLocation) {
    RectD newLayout = setNodeLocationAndSize(iInputModeContext, dummyLocation, dummySize);
    DelegatingContext portContext = new DelegatingContext(iInputModeContext);
    for(IHandle portHandle : portHandles) {
      portHandle.dragFinished(iInputModeContext, originalLocation, newLocation);
    }
    portHandles.clear();
    if(reshapeHandler != null) {
      //if there is a reshape handler, ensure proper handling of a parent group node
      reshapeHandler.reshapeFinished(iInputModeContext, initialRect, newLayout);
    }
  }

  /**
   * Gets the location that is specified by the given ratios.
   */
  private static PointD getLocation(IOrientedRectangle rectangle, double ratioWidth, double ratioHeight) {
    double x1 = rectangle.getAnchorX();
    double y1 = rectangle.getAnchorY();

    double upX = rectangle.getUpX();
    double upY = rectangle.getUpY();

    double w = rectangle.getWidth() * ratioWidth;
    double h = rectangle.getHeight() * ratioHeight;

    double x2 = x1 + upX * h - upY * w;
    double y2 = y1 + upY * h + upX * w;

    return new PointD(x2, y2);
  }

  /**
   * Returns the x-coordinate of the rotated bounds.
   */
  @Override
  public double getX() {
    return getLocation(getNodeBasedOrientedRectangle(), position).x;
  }

  /**
   * Returns the y-coordinate of the rotated bounds.
   */
  @Override
  public double getY() {
    return getLocation(getNodeBasedOrientedRectangle(), position).y;
  }

  /**
   * Returns the location of the specified position on the border of the oriented rectangle.
   */
  private PointD getLocation(IOrientedRectangle layout, HandlePositions position) {
    if(layout == null){
      return node.getLayout().toPointD();
    }

    if(HandlePositions.NORTH_WEST.equals(position)){
      return getLocation(layout, 0.0, 1.0);

    } else if (HandlePositions.NORTH.equals(position)) {
      return getLocation(layout, 0.5, 1.0);

    } else if (HandlePositions.NORTH_EAST.equals(position)) {
      return getLocation(layout, 1.0, 1.0);

    } else if (HandlePositions.EAST.equals(position)) {
      return getLocation(layout, 1.0, 0.5);

    } else if (HandlePositions.SOUTH_EAST.equals(position)) {
      return  getLocation(layout, 1.0, 0.0);

    } else if (HandlePositions.SOUTH.equals(position)) {
      return getLocation(layout, 0.5, 0.0);

    } else if (HandlePositions.SOUTH_WEST.equals(position)) {
      return layout.getAnchorLocation();

    } else if (HandlePositions.WEST.equals(position)) {
      return getLocation(layout, 0.0, 0.5);

    } else {
      throw new IllegalArgumentException("position out of range");

    }
  }

  /**
   * A context that returns no snapContext in its lookup and delegates its other methods to an inner context.
   */
  public static class DelegatingContext implements IInputModeContext {

    private final IInputModeContext context;

    /**
     * Initialize a new instance.
     */
    DelegatingContext(IInputModeContext context) {
      this.context = context;
    }

    /**
     * The wrapped context's zoom
     */
    @Override
    public double getZoom() {
      return context.getZoom();
    }

    /**
     * Returns the wrapped context's hit test radius.
     */
    @Override
    public double getHitTestRadius() {
      return context.getHitTestRadius();
    }

    /**
     * Returns the wrapped context's canvas component.
     */
    @Override
    public CanvasComponent getCanvasComponent() {
      return context.getCanvasComponent();
    }

    /**
     * Returns the wrapped context's parent input mode.
     */
    @Override
    public IInputMode getParentInputMode() {
      return context.getParentInputMode();
    }

    /**
     * Delegates to the wrapped context's lookup but cancels the snap context.
     */
    @Override
    public <TLookup> TLookup lookup(Class<TLookup> aClass) {
      return aClass == SnapContext.class ? null : context.lookup(aClass);
    }
  }

  /**
   * This port handle is used only to trigger the updates of the orthogonal edge editing facility of yFiles.
   * <p>
   * In yFiles, everything code related to updates of the orthogonal edge editing facility is internal. As a workaround,
   * we explicitly call internal port handles from our custom node handles.
   * </p>
   */
  public static class DummyPortLocationModelParameterHandle extends PortLocationModelParameterHandle {

    DummyPortLocationModelParameterHandle(IPort port) {
      super(port);
    }

    /**
     * Does nothing since we don't want to change the port location.
     */
    @Override
    protected void setParameter(IGraph graph, IPort port, IPortLocationModelParameter param) {
      //do nothing
    }

    /**
     * Returns the current port location since we don't want to change the port location.
     */
    @Override
    protected IPortLocationModelParameter getNewParameter(IPort port, IPortLocationModel model, PointD location) {
      return port.getLocationParameter();
    }
  }
}
