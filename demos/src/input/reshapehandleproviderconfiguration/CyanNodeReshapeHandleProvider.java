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
package input.reshapehandleproviderconfiguration;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.HandleTypes;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.IReshapeHandler;
import com.yworks.yfiles.view.input.NodeReshapeHandleProvider;
import com.yworks.yfiles.view.input.NodeReshapeHandlerHandle;

import java.awt.Cursor;

class CyanNodeReshapeHandleProvider extends NodeReshapeHandleProvider {
    private final ApplicationState applicationState;

    public CyanNodeReshapeHandleProvider(INode node, IReshapeHandler reshapeHandler, ApplicationState applicationState) {
        super(node, reshapeHandler, HandlePositions.BORDER);
        this.applicationState = applicationState;
    }

    @Override
    public IHandle getHandle(IInputModeContext inputModeContext, HandlePositions position) {
        NodeReshapeHandlerHandle wrapped = (NodeReshapeHandlerHandle) super.getHandle(inputModeContext, position);
        wrapped.setRatioReshapeRecognizer(applicationState.isKeepingAspectRatioEnabled() ? IEventRecognizer.ALWAYS : IEventRecognizer.NEVER);
        return new ClickableNodeReshapeHandlerHandle(wrapped);
    }

    /**
     * A handle delegating most functionality to a wrapped {@link NodeReshapeHandlerHandle}.
     * <p>
     * {@link IHandle#handleClick(ClickEventArgs)} toggles the {@link ApplicationState#isKeepingAspectRatioEnabled()} state.
     */
    private class ClickableNodeReshapeHandlerHandle implements IHandle {
        private NodeReshapeHandlerHandle wrapped;

        public ClickableNodeReshapeHandlerHandle(NodeReshapeHandlerHandle wrapped) {
            this.wrapped = wrapped;
        }

        /**
         * {@link ApplicationState#toggleAspectRatio() Toggles} the {@link ApplicationState#isKeepingAspectRatioEnabled()} state.
         *
         * @param eventArgs Arguments describing the click.
         */
        @Override
        public void handleClick(ClickEventArgs eventArgs) {
            applicationState.toggleAspectRatio();
        }

        /**
         * Modifies the wrapped {@link IHandle#getType()} by combining it with {@link HandleTypes.VARIANT2}.
         */
        @Override
        public HandleTypes getType() {
            return applicationState.isKeepingAspectRatioEnabled() ? wrapped.getType().or(HandleTypes.VARIANT2) : wrapped.getType();
        }

        @Override
        public IPoint getLocation() {
            return wrapped.getLocation();
        }

        @Override
        public Cursor getCursor() {
            return wrapped.getCursor();
        }

        @Override
        public void initializeDrag(IInputModeContext context) {
            wrapped.initializeDrag(context);
        }

        @Override
        public void handleMove(IInputModeContext context, PointD originalLocation, PointD newLocation) {
            wrapped.handleMove(context, originalLocation, newLocation);
        }

        @Override
        public void cancelDrag(IInputModeContext context, PointD originalLocation) {
            wrapped.cancelDrag(context, originalLocation);
        }

        @Override
        public void dragFinished(IInputModeContext context, PointD originalLocation, PointD newLocation) {
            wrapped.dragFinished(context, originalLocation, newLocation);
        }
    }
}
