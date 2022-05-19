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
package layout.cleararea;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.ICompoundEdit;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.layout.CompositeLayoutData;
import com.yworks.yfiles.layout.GivenCoordinatesStage;
import com.yworks.yfiles.layout.GivenCoordinatesStageData;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.partial.ClearAreaLayout;
import com.yworks.yfiles.layout.partial.ClearAreaLayoutData;
import com.yworks.yfiles.layout.partial.ClearAreaStrategy;
import com.yworks.yfiles.layout.partial.ComponentAssignmentStrategy;
import com.yworks.yfiles.view.GraphComponent;

import java.time.Duration;

public class ClearAreaLayoutHelper {
    /**
     * Performs the layout and the animation.
     */
    private LayoutExecutor executor;

    /**
     * The graph that is displayed.
     */
    private final IGraph graph;

    /**
     * The control that displays the graph.
     */
    private final GraphComponent graphComponent;

    /**
     * The graph layout copy that stores the original layout before the marquee rectangle has been dragged.
     */
    private GivenCoordinatesStageData resetToOriginalGraphStageData;

    /**
     * The marquee rectangle.
     */
    public RectD clearRectangle;

    /***
     * The group node within which the marquee was created, otherwise null.
     */
    public INode groupNode;

    /***
     * The {@link ILayoutAlgorithm} that makes space for the marquee rectangle.
     */
    private ClearAreaLayout clearAreaLayout;

    /**
     * Instantiates the helper.
     *
     * @param graphComponent the component displaying the graph
     */
    public ClearAreaLayoutHelper(GraphComponent graphComponent) {
        this.graphComponent = graphComponent;
        this.graph = graphComponent.getGraph();
    }

    public void setClearRectangle(RectD clearRectangle) {
        this.clearRectangle = clearRectangle;
    }

    public void setGroupNode(INode groupNode) {
        this.groupNode = groupNode;
    }

    /**
     * Creates a {@link GivenCoordinatesStageData} that store the layout of nodes and edges.
     *
     * @return The {@link GivenCoordinatesStageData}
     */
    private GivenCoordinatesStageData createGivenCoordinateStageData() {
        GivenCoordinatesStageData data = new GivenCoordinatesStageData();

        for (INode node : graphComponent.getGraph().getNodes()) {
            data.getNodeLocations().getMapper().setValue(node, node.getLayout().getTopLeft());
            data.getNodeSizes().getMapper().setValue(node, node.getLayout().toRectD().getSize());
        }

        for (IEdge edge : graphComponent.getGraph().getEdges()) {
            data.getEdgePaths().getMapper().setValue(edge, edge.getPathPoints());
        }

        return data;
    }

    /**
     * A {@link LayoutExecutor} that is used while dragging the marquee rectangle.
     * <remarks>
     * First, all nodes and edges are pushed back into place before the drag started. Then space
     * is made for the rectangle at its current position. The animation morphs all elements to the
     * calculated positions.
     * </remarks>
     */
    private LayoutExecutor createDraggingLayoutExecutor() {
        LayoutExecutor executor = new LayoutExecutor(graphComponent, createDraggingLayout());

        executor.setLayoutData(createDraggingLayoutData());
        executor.setRunningInThread(true);
        executor.setDuration(Duration.ofMillis(150));

        executor.addLayoutFinishedListener((source, args) -> {
            // after the layout run
            onExecutorFinished();
            // free the executor for the next layout
            layoutIsRunning = false;

            if (layoutPending) {
                runLayout();
            }
        });


        return executor;
    }

    /**
     * A {@link LayoutExecutor} that is used after the drag is canceled.
     * All nodes and edges are pushed back into place before the drag started.
     */
    private LayoutExecutor createCanceledLayoutExecutor() {
        LayoutExecutor executor = new LayoutExecutor(graphComponent, new GivenCoordinatesStage());

        executor.setLayoutData(resetToOriginalGraphStageData);
        executor.setRunningInThread(true);
        executor.setDuration(Duration.ofMillis(150));

        return executor;
    }

    /**
     * Creates a {@link ILayoutAlgorithm} used while dragging and finishing the gesture.
     */
    private ILayoutAlgorithm createDraggingLayout() {
        clearAreaLayout = new ClearAreaLayout();
        clearAreaLayout.setComponentAssignmentStrategy(ComponentAssignmentStrategy.SINGLE);
        clearAreaLayout.setClearAreaStrategy(ClearAreaStrategy.PRESERVE_SHAPES);
        clearAreaLayout.setEdgeConsiderationEnabled(true);

        return new GivenCoordinatesStage(clearAreaLayout);
    }

    /**
     * Creates a {@link LayoutData} used while dragging and finishing the gesture.
     */
    private LayoutData createDraggingLayoutData() {
        ClearAreaLayoutData layoutData = new ClearAreaLayoutData();
        layoutData.getAreaGroupNode().setPredicate(iNode -> iNode == groupNode);

        return new CompositeLayoutData(
                resetToOriginalGraphStageData,
                layoutData);
    }

    /**
     * A lock which prevents re-entrant layout execution.
     */
    private boolean layoutIsRunning;

    /**
     * Indicates whether a layout run has been requested while running a layout calculation.
     */
    private boolean layoutPending;

    /**
     * Indicates that the gesture has been canceled and the original layout should be restored.
     */
    private boolean canceled;

    /**
     * Indicates that the gesture has been finished and the new layout should be applied.
     */
    private boolean stopped;

    /**
     * Creates a single unit to undo and redo the complete reparent gesture.
     */
    private ICompoundEdit layoutEdit;

    /**
     * Starts a layout calculation if none is already running.
     */
    public void runLayout() {
        if (layoutIsRunning) {
            // if another layout is running: request a new layout and exit
            layoutPending = true;
            return;
        }
        // prevent other layouts from running
        layoutIsRunning = true;
        // clear the pending flag: the requested layout will run now
        layoutPending = false;
        // before the layout run
        onExecutorStarting();

        executor.start();
    }

    /**
     * Prepares the layout execution.
     */
    public void initializeLayout() {
        layoutEdit = graph.beginEdit("Clear Area", "Clear Area");
        resetToOriginalGraphStageData = createGivenCoordinateStageData();
        executor = createDraggingLayoutExecutor();
    }

    /**
     * Cancels the current layout calculation.
     */
    public void cancelLayout() {
        executor.stop();
        canceled = true;
        runLayout();
    }

    /**
     * Stops the current layout calculation.
     */
    public void stopLayout() {
        executor.stop();
        stopped = true;
        runLayout();
    }

    /**
     * Called before the a layout run starts.
     */
    private void onExecutorStarting() {
        if (canceled) {
            // reset to original graph layout
            executor = createCanceledLayoutExecutor();
        } else {
            clearAreaLayout.setArea(clearRectangle.toYRectangle());
        }
    }

    /**
     * Called after the a layout run finished.
     */
    private void onExecutorFinished() {
        if (canceled) {
            layoutEdit.cancel();
        } else if (stopped) {
            layoutEdit.commit();
        }
    }
}
