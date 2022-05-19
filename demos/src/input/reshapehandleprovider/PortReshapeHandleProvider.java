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
package input.reshapehandleprovider;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;

/**
 * An {@link IReshapeHandleProvider} implementation for {@link IPort}s using a {@link NodeStylePortStyleAdapter}.
 * <p>
 * The provided {@link PortReshapeHandle} modifies the {@link NodeStylePortStyleAdapter#getRenderSize() render size} of the
 * port style.
 * </p>
 */
public class PortReshapeHandleProvider implements IReshapeHandleProvider {

    private final IPort port;
    private final NodeStylePortStyleAdapter adapter;
    private SizeD minimumSize;

    /**
     * Gets the minimum size the {@link NodeStylePortStyleAdapter#getRenderSize() render size} may have.
     */
    public SizeD getMinimumSize() {
        return minimumSize;
    }

    /**
     * Sets the minimum size the {@link NodeStylePortStyleAdapter#getRenderSize() render size} may have.
     */
    public void setMinimumSize(SizeD minimumSize) {
        this.minimumSize = minimumSize;
    }

    /**
     * Creates a new instance for port and its adapter.
     *
     * @param port    The port whose visualization shall be resized.
     * @param adapter The adapter whose render size shall be changed.
     */
    public PortReshapeHandleProvider(IPort port, NodeStylePortStyleAdapter adapter) {
        this.port = port;
        this.adapter = adapter;
    }

    /**
     * Returns {@link HandlePositions#CORNERS} or {@link HandlePositions#BORDER} as available handle
     * positions depending on the modifier state of <code>Ctrl</code>.
     *
     * @param context The context the handles are created in.
     * @return {@link HandlePositions#CORNERS} or {@link HandlePositions#BORDER}.
     */
    @Override
    public HandlePositions getAvailableHandles(IInputModeContext context) {
        boolean ctrlPressed = IEventRecognizer.CTRL_PRESSED.isRecognized(this, context.getCanvasComponent().getLastInputEvent());
        // when Ctrl is pressed, all border positions are returned, otherwise only the corner positions
        return ctrlPressed ? HandlePositions.BORDER : HandlePositions.CORNERS;
    }

    /**
     * Returns a {@link PortReshapeHandle} for the port at the given position and
     * sets its {@link PortReshapeHandle#setMinimumSize(SizeD) minimum size} to {@link PortReshapeHandleProvider#getMinimumSize() MinimumSize}.
     *
     * @param context  The context for which the handles are queried.
     * @param position The single position a handle implementation should be returned for.
     * @return A {@link PortReshapeHandle} for the port at the position.
     */
    @Override
    public IHandle getHandle(IInputModeContext context, HandlePositions position) {
        PortReshapeHandle portReshapeHandle = new PortReshapeHandle(context, port, adapter, position);
        portReshapeHandle.setMinimumSize(getMinimumSize());
        return portReshapeHandle;
    }
}
