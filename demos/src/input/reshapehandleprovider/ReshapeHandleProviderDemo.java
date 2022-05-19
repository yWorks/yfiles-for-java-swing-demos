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
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.PortDecorator;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ModifierKeys;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import toolkit.AbstractDemo;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Shows how to implement a custom {@link IReshapeHandleProvider} for {@link IPort IPorts} using a
 * {@link NodeStylePortStyleAdapter}.
 */
public class ReshapeHandleProviderDemo extends AbstractDemo {

    /**
     * Registers a callback function as decorator that provides a customized {@link IReshapeHandleProvider} for
     * each port with a {@link NodeStylePortStyleAdapter}.
     * This callback function is called whenever a port in the graph is queried for its <code>IReshapeHandleProvider</code>.
     */
    public void registerReshapeHandleProvider() {
        PortDecorator portDecorator = graphComponent.getGraph().getDecorator().getPortDecorator();
        portDecorator.getDecoratorFor(IReshapeHandleProvider.class).setFactory(
                port -> port.getStyle() instanceof NodeStylePortStyleAdapter,
                port -> {
                    NodeStylePortStyleAdapter style = (NodeStylePortStyleAdapter) port.getStyle();
                    PortReshapeHandleProvider provider = new PortReshapeHandleProvider(port, style);
                    provider.setMinimumSize(new SizeD(5, 5));
                    return provider;
                }
        );
    }


    /**
     * Initializes this demo by configuring the default styles, input mode, and the model item lookup and creating an example graph
     * together with an enclosing rectangle some nodes may not stretch over.
     */
    public void initialize() {
        super.initialize();

        // initialize the default of the graph
        initializeGraphDefaults();

        // initialize the GraphEditorInputMode
        initializeInputMode();

        // register the reshape handle provider for ports
        registerReshapeHandleProvider();

        // read initial graph from sample file
        loadGraph();
    }

    /**
     * Initializes the graph defaults.
     */
    private void initializeGraphDefaults() {
        IGraph graph = graphComponent.getGraph();

        ShapeNodeStyle adaptedStyle = new ShapeNodeStyle();
        adaptedStyle.setPaint(Colors.GREEN);
        adaptedStyle.setPen(Pen.getTransparent());

        NodeStylePortStyleAdapter portStyleAdapter = new NodeStylePortStyleAdapter(adaptedStyle);
        portStyleAdapter.setRenderSize(new SizeD(7, 7));
        graph.getNodeDefaults().getPortDefaults().setStyle(portStyleAdapter);
        // each port needs its own style instance to have its own render size
        graph.getNodeDefaults().getPortDefaults().setStyleInstanceSharingEnabled(false);
        // disable removing ports when all attached edges have been removed
        graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);

        PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
        edgeStyle.setPen(new Pen(Color.BLACK, 3));
        graph.getEdgeDefaults().setStyle(edgeStyle);
    }


    private void initializeInputMode() {
        // create a default editor input mode
        GraphEditorInputMode geim = new GraphEditorInputMode();

        // ports are preferred for clicks
        geim.setClickHitTestOrder(new GraphItemTypes[]{
                GraphItemTypes.PORT,
                GraphItemTypes.PORT_LABEL,
                GraphItemTypes.BEND,
                GraphItemTypes.EDGE_LABEL,
                GraphItemTypes.EDGE,
                GraphItemTypes.NODE,
                GraphItemTypes.NODE_LABEL,
        });
        // enable orthogonal edge editing
        geim.setOrthogonalEdgeEditingContext(new OrthogonalEdgeEditingContext());

        // PortReshapeHandlerProvider considers pressed Ctrl keys. Whenever Ctrl is pressed or released,
        // we force GraphEditorInputMode to requery the handles of selected items
        graphComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                updateHandles(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                updateHandles(e);
            }
        });

        // finally, set the input mode to the graph control.
        graphComponent.setInputMode(geim);
    }

    ModifierKeys lastKeyState = graphComponent.getLastMouse2DEvent().getModifiers();

    private void updateHandles(KeyEvent e) {
        ModifierKeys old = lastKeyState;
        lastKeyState = ModifierKeys.fromEvent(e);
        if (!old.equals(lastKeyState) && e.getKeyCode() == KeyEvent.VK_CONTROL) {
            // only update handles if a modifier state changed - not on redispatched pressed events
            ((GraphEditorInputMode) graphComponent.getInputMode()).requeryHandles();
        }
    }


    /**
     * Loads a sample graph.
     */
    private void loadGraph() {
        try {
            graphComponent.importFromGraphML(getClass().getResource("resources/defaultGraph.graphml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Centers the displayed content in the graph component.
     */
    public void onVisible() {
        super.onVisible();
        graphComponent.fitGraphBounds();
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

}
