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
package input.reshapehandleprovider;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;

import java.awt.Cursor;

/**
 * An {@link IHandle} implementation for an {@link IPort} using a {@link NodeStylePortStyleAdapter}
 * that can be used to reshape the {@link NodeStylePortStyleAdapter#getRenderSize() render size}.
 */
public class PortReshapeHandle implements IHandle {
    private final IInputModeContext context;
    private final IPort port;
    private final NodeStylePortStyleAdapter adapter;
    private final HandlePositions position;

    // the initial RenderSize used to reset the size on Cancel
    private SizeD initialRenderSize;

    private double margins;
    private SizeD minimumSize;

    /**
     * Gets the margins the handle is placed form the port visualization bounds.
     *
     * @return
     */
    public double getMargins() {
        return margins;
    }

    /**
     * Sets the margins the handle is placed form the port visualization bounds.
     *
     * @param margins The margins to set.
     */
    public void setMargins(double margins) {
        this.margins = margins;
    }

    /**
     * Gets the minimum size the {@link NodeStylePortStyleAdapter#getRenderSize() render size} may have.
     *
     * @return
     */
    public SizeD getMinimumSize() {
        return minimumSize;
    }

    /**
     * Sets the minimum size the {@link NodeStylePortStyleAdapter#getRenderSize() render size} may have.
     *
     * @param minimumSize
     */
    public void setMinimumSize(SizeD minimumSize) {
        this.minimumSize = minimumSize;
    }

    /**
     * Creates a new instance for port and its adapter.
     *
     * @param context  The context of the reshape gesture.
     * @param port     The port whose visualization shall be resized.
     * @param adapter  The adapter whose render size shall be changed.
     * @param position The position of the handle.
     */
    public PortReshapeHandle(IInputModeContext context, IPort port, NodeStylePortStyleAdapter adapter, HandlePositions position) {
        this.context = context;
        this.port = port;
        this.adapter = adapter;
        this.position = position;
        this.margins = 4;
    }

    /**
     * Returns the current location of the handle.
     */
    @Override
    public IPoint getLocation() {
        return calculateLocation();
    }

    /**
     * Calculates the location of the handle considering the {@link IPort#getLocation()  port location},
     * {@link NodeStylePortStyleAdapter#getRenderSize() render size} and {@link PortReshapeHandle#getMargins() margins}.
     */
    private PointD calculateLocation() {
        PointD portLocation = port.getLocation();
        double handleX = portLocation.getX();
        double handleY = portLocation.getY();
        double marginsInViewCoordinates = getMargins() / context.getZoom();
        if (position == HandlePositions.NORTH_WEST || position == HandlePositions.WEST || position == HandlePositions.SOUTH_WEST) {
            handleX -= adapter.getRenderSize().getWidth() / 2.0 + marginsInViewCoordinates;
        } else if (position == HandlePositions.NORTH_EAST || position == HandlePositions.EAST || position == HandlePositions.SOUTH_EAST) {
            handleX += adapter.getRenderSize().getWidth() / 2.0 + marginsInViewCoordinates;
        }
        if (position == HandlePositions.NORTH_WEST || position == HandlePositions.NORTH || position == HandlePositions.NORTH_EAST) {
            handleY -= adapter.getRenderSize().getHeight() / 2.0 + marginsInViewCoordinates;
        } else if (position == HandlePositions.SOUTH_WEST || position == HandlePositions.SOUTH || position == HandlePositions.SOUTH_EAST) {
            handleY += adapter.getRenderSize().getHeight() / 2.0 + marginsInViewCoordinates;
        }
        return new PointD(handleX, handleY);
    }

    /**
     * Stores the initial {@link NodeStylePortStyleAdapter#getRenderSize() render size}.
     *
     * @param context The context to retrieve information about the drag from.
     */
    @Override
    public void initializeDrag(IInputModeContext context) {
        initialRenderSize = adapter.getRenderSize();
    }

    /**
     * Calculates and applies the new {@link NodeStylePortStyleAdapter#getRenderSize() render size}.
     *
     * @param context          The context to retrieve information about the drag from.
     * @param originalLocation The value of the {@link #getLocation() Location} property at the time of {@link #initializeDrag(IInputModeContext)}.
     * @param newLocation      The coordinates in the world coordinate system that the client wants the handle to be at.
     */
    @Override
    public void handleMove(IInputModeContext context, PointD originalLocation, PointD newLocation) {
        // calculate the size delta implied by the originalLocation and newLocation
        SizeD delta = calculateDelta(originalLocation, newLocation);
        // calculate and apply the new render size
        adapter.setRenderSize(calculateNewSize(delta));
    }

    private SizeD calculateDelta(PointD originalLocation, PointD newLocation) {
        // calculate the delta the mouse has been moved since InitializeDrag
        PointD mouseDelta = PointD.subtract(newLocation, originalLocation);
        // depending on the handle position this mouse delta shall increase or decrease the render size
        if (HandlePositions.NORTH_WEST.equals(position)) {
            return new SizeD(-2 * mouseDelta.getX(), -2 * mouseDelta.getY());
        } else if (HandlePositions.NORTH.equals(position)) {
            return new SizeD(0, -2 * mouseDelta.getY());
        } else if (HandlePositions.NORTH_EAST.equals(position)) {
            return new SizeD(2 * mouseDelta.getX(), -2 * mouseDelta.getY());
        } else if (HandlePositions.WEST.equals(position)) {
            return new SizeD(-2 * mouseDelta.getX(), 0);
        } else if (HandlePositions.EAST.equals(position)) {
            return new SizeD(2 * mouseDelta.getX(), 0);
        } else if (HandlePositions.SOUTH_WEST.equals(position)) {
            return new SizeD(-2 * mouseDelta.getX(), 2 * mouseDelta.getY());
        } else if (HandlePositions.SOUTH.equals(position)) {
            return new SizeD(0, 2 * mouseDelta.getY());
        } else if (HandlePositions.SOUTH_EAST.equals(position)) {
            return new SizeD(2 * mouseDelta.getX(), 2 * mouseDelta.getY());
        }
        return SizeD.EMPTY;
    }

    private SizeD calculateNewSize(SizeD delta) {
        double newWidth = Math.max(getMinimumSize().getWidth(), initialRenderSize.getWidth() + delta.getWidth());
        double newHeight = Math.max(getMinimumSize().getHeight(), initialRenderSize.getHeight() + delta.getHeight());
        return new SizeD(newWidth, newHeight);
    }

    /**
     * Resets {@link NodeStylePortStyleAdapter#getRenderSize() render size} to its initial value.
     *
     * @param context          The context to retrieve information about the drag from.
     * @param originalLocation The value of the coordinate of the {@link #getLocation() Location} property at the
     *                         time of {@link #initializeDrag(IInputModeContext) initializeDrag}.
     */
    @Override
    public void cancelDrag(IInputModeContext context, PointD originalLocation) {
        adapter.setRenderSize(initialRenderSize);
    }

    /**
     * Calculates and applies the final {@link NodeStylePortStyleAdapter#getRenderSize() render size}.
     *
     * @param context          The context to retrieve information about the drag from.
     * @param originalLocation The value of the {@link #getLocation() Location} property at the time of
     *                         {@link #initializeDrag(IInputModeContext) initializeDrag}.
     * @param newLocation      The coordinates in the world coordinate system that the client wants the handle to be at. Depending on the
     *                         implementation the {@link #getLocation() Location} may or may not be modified to reflect the new value. This is the same
     *                         value as delivered in the last invocation of {@link #handleMove(IInputModeContext, PointD, PointD) handleMove}.
     */
    @Override
    public void dragFinished(IInputModeContext context, PointD originalLocation, PointD newLocation) {
        SizeD delta = calculateDelta(originalLocation, newLocation);
        adapter.setRenderSize(calculateNewSize(delta));
    }

    /**
     * Returns {@link HandleTypes#RESIZE}.
     */
    @Override
    public HandleTypes getType() {
        return HandleTypes.RESIZE;
    }

    @Override
    public Cursor getCursor() {
        if (HandlePositions.SOUTH.equals(position)) {
            return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
        } else if (HandlePositions.NORTH.equals(position)) {
            return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
        } else if (HandlePositions.EAST.equals(position)) {
            return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
        } else if (HandlePositions.WEST.equals(position)) {
            return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
        } else if (HandlePositions.NORTH_WEST.equals(position)) {
            return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
        } else if (HandlePositions.SOUTH_EAST.equals(position)) {
            return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
        } else if (HandlePositions.NORTH_EAST.equals(position)) {
            return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
        } else if (HandlePositions.SOUTH_WEST.equals(position)) {
            return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
        }
        return Cursor.getDefaultCursor();
    }

    @Override
    public void handleClick(ClickEventArgs eventArgs) {
    }
}
