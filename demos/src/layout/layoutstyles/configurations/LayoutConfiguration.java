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
package layout.layoutstyles.configurations;

import com.yworks.yfiles.algorithms.AlgorithmAbortedException;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.layout.GenericLayoutData;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.ItemMapping;
import com.yworks.yfiles.layout.LabelAngleReferences;
import com.yworks.yfiles.layout.LabelPlacements;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.MinimumNodeSizeStage;
import com.yworks.yfiles.layout.PortAdjustmentPolicy;
import com.yworks.yfiles.layout.PreferredPlacementDescriptor;
import com.yworks.yfiles.view.GraphComponent;

import java.awt.EventQueue;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.swing.JOptionPane;

/**
 * Abstract base class for configurations that can be displayed in an option editor.
 * <p>
 * Subclasses at least have to implement the method {@link #createConfiguredLayout(GraphComponent)} so the {@link #apply(GraphComponent, Runnable)}
 * method can be called to run the returned layout algorithm and apply the layout result to the graph in the passed
 * {@link GraphComponent}.
 * </p>
 */
public abstract class LayoutConfiguration {
  /**
   * A guard to prevent running multiple layout calculations at the same time.
   */
  private boolean layoutRunning;

  /**
   * Applies this configuration to the given {@link GraphComponent}.
   * <p>
   * This is the main method of this class. Typically, it calls {@link #createConfiguredLayout(GraphComponent)} to create and
   * configure a layout and {@link #createConfiguredLayoutData(GraphComponent, ILayoutAlgorithm)} to get a suitable {@link LayoutData}
   * instance for the layout.
   * </p>
   * @param graphComponent The {@code GraphControl} to apply the configuration on.
   * @param doneHandler A callback that is called after the configuration is applied. Can be {@code null}
   */
  public CompletionStage apply( GraphComponent graphComponent, Runnable doneHandler ) {
    if (layoutRunning) {
      CompletableFuture future = new CompletableFuture();
      EventQueue.invokeLater(() -> {
        doneHandler.run();
        future.complete(null);
      });
      return future;
    }

    ILayoutAlgorithm layout = createConfiguredLayout(graphComponent);
    if (layout == null) {
      CompletableFuture future = new CompletableFuture();
      EventQueue.invokeLater(() -> {
        doneHandler.run();
        future.complete(null);
      });
      return future;
    }

    LayoutData layoutData = createConfiguredLayoutData(graphComponent, layout);

    // configure the LayoutExecutor
    LayoutExecutor layoutExecutor = new LayoutExecutor(graphComponent, new MinimumNodeSizeStage(layout));
    layoutExecutor.setDuration(Duration.ofMillis(750));
    layoutExecutor.setViewportAnimationEnabled(true);
    layoutExecutor.setEasedAnimationEnabled(true);
    layoutExecutor.setPortAdjustmentPolicy(PortAdjustmentPolicy.LENGTHEN);
    // set the cancel duration for the layout computation to 20s
    int duration = 20;
    layoutExecutor.getAbortHandler().setCancelDuration(Duration.ofSeconds(duration));

    // set the layout data to the LayoutExecutor
    if (layoutData != null) {
      layoutExecutor.setLayoutData(layoutData);
    }

    layoutExecutor.addLayoutFinishedListener(( sender, args ) -> {
      layoutRunning = false;
      postProcess(graphComponent);
      // clean up mapperRegistry
      graphComponent.getGraph().getMapperRegistry().removeMapper(LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY);
      doneHandler.run();

      RuntimeException ex = args.getException();
      if (ex instanceof AlgorithmAbortedException) {
        String message ="The layout computation has been canceled because the maximum\n" +
                "configured runtime of " + duration + " seconds has been exceeded.";
        JOptionPane.showMessageDialog(graphComponent, message, "Layout Canceled", JOptionPane.WARNING_MESSAGE);
      } else if (ex != null) {
        String message = ex.getMessage();
        if (message == null || message.isEmpty()) {
          message = "An error occurred during the layout calculation:\n" +
                  ex.getClass().getName();
        }
        JOptionPane.showMessageDialog(graphComponent, message, "Error during Layout", JOptionPane.ERROR_MESSAGE);
      }
      args.setHandled(true);
    });

    // start the LayoutExecutor
    return layoutExecutor.start();
  }

  /**
   * Creates and configures a layout algorithm.
   * @param graphComponent The {@link GraphComponent} to apply the configuration on.
   * @return The configured layout.
   */
  protected abstract ILayoutAlgorithm createConfiguredLayout( GraphComponent graphComponent );

  /**
   * Called by {@link #apply(GraphComponent, Runnable)} to create the layout data of the configuration.
   * <p>
   * This method is typically overridden to provide item-specific configuration of a layout algorithm.
   * </p>
   * @param graphComponent The {@link GraphComponent} to apply the configuration on.
   * @param layout The layout algorithm to run.
   * @return A layout-specific {@link LayoutData} instance or {@code null}.
   */
  protected LayoutData createConfiguredLayoutData( GraphComponent graphComponent, ILayoutAlgorithm layout ) {
    return null;
  }

  /**
   * Called by {@link #apply(GraphComponent, Runnable)} after the layout animation is done. This method is typically
   * overridden to remove mappers from the mapper registry of the graph.
   */
  protected void postProcess( GraphComponent graphComponent ) {
  }

  /**
   * Adds a mapper with a {@link PreferredPlacementDescriptor} that matches the given settings to the mapper registry of the
   * given graph. In addition, sets the label model of all edge labels to free since that model can realizes any label
   * placement calculated by a layout algorithm.
   */
  public final LayoutData createLabelingLayoutData( IGraph graph, EnumLabelPlacementAlongEdge placeAlongEdge, EnumLabelPlacementSideOfEdge sideOfEdge, EnumLabelPlacementOrientation orientation, double distanceToEdge ) {
    final PreferredPlacementDescriptor descriptor = createPreferredPlacementDescriptor(placeAlongEdge, sideOfEdge, orientation, distanceToEdge);

    // change to a free edge label model to support integrated edge labeling
    FreeEdgeLabelModel model = new FreeEdgeLabelModel();

    for (ILabel label : graph.getEdgeLabels()) {
      if (!(label.getLayoutParameter().getModel() instanceof FreeEdgeLabelModel)) {
        graph.setLabelLayoutParameter(label, model.findBestParameter(label, model, label.getLayout()));
      }
    }

    GenericLayoutData layoutData = new GenericLayoutData();
    ItemMapping<ILabel, PreferredPlacementDescriptor> preferredPlacementDescriptorMapping = new ItemMapping<>();
    preferredPlacementDescriptorMapping.setConstant(descriptor);
    layoutData.addItemMapping(PreferredPlacementDescriptor.class, LayoutGraphAdapter.EDGE_LABEL_LAYOUT_PREFERRED_PLACEMENT_DESCRIPTOR_DPKEY, preferredPlacementDescriptorMapping);

    return layoutData;
  }

  /**
   * Creates a new {@link PreferredPlacementDescriptor} that matches the given settings.
   */
  public final PreferredPlacementDescriptor createPreferredPlacementDescriptor( EnumLabelPlacementAlongEdge placeAlongEdge, EnumLabelPlacementSideOfEdge sideOfEdge, EnumLabelPlacementOrientation orientation, double distanceToEdge ) {
    PreferredPlacementDescriptor descriptor = new PreferredPlacementDescriptor();

    switch (sideOfEdge) {
      case ANYWHERE:
        descriptor.setSideOfEdge(LabelPlacements.ANYWHERE);
        break;
      case ON_EDGE:
        descriptor.setSideOfEdge(LabelPlacements.ON_EDGE);
        break;
      case LEFT:
        descriptor.setSideOfEdge(LabelPlacements.LEFT_OF_EDGE);
        break;
      case RIGHT:
        descriptor.setSideOfEdge(LabelPlacements.RIGHT_OF_EDGE);
        break;
      case LEFT_OR_RIGHT:
        descriptor.setSideOfEdge(LabelPlacements.LEFT_OF_EDGE.or(LabelPlacements.RIGHT_OF_EDGE));
        break;
    }

    switch (placeAlongEdge) {
      case ANYWHERE:
        descriptor.setPlaceAlongEdge(LabelPlacements.ANYWHERE);
        break;
      case AT_SOURCE_PORT:
        descriptor.setPlaceAlongEdge(LabelPlacements.AT_SOURCE_PORT);
        break;
      case AT_TARGET_PORT:
        descriptor.setPlaceAlongEdge(LabelPlacements.AT_TARGET_PORT);
        break;
      case AT_SOURCE:
        descriptor.setPlaceAlongEdge(LabelPlacements.AT_SOURCE);
        break;
      case AT_TARGET:
        descriptor.setPlaceAlongEdge(LabelPlacements.AT_TARGET);
        break;
      case CENTERED:
        descriptor.setPlaceAlongEdge(LabelPlacements.AT_CENTER);
        break;
    }

    switch (orientation) {
      case PARALLEL:
        descriptor.setAngle(0.0d);
        descriptor.setAngleReference(LabelAngleReferences.RELATIVE_TO_EDGE_FLOW);
        break;
      case ORTHOGONAL:
        descriptor.setAngle(Math.PI / 2);
        descriptor.setAngleReference(LabelAngleReferences.RELATIVE_TO_EDGE_FLOW);
        break;
      case HORIZONTAL:
        descriptor.setAngle(0.0d);
        descriptor.setAngleReference(LabelAngleReferences.ABSOLUTE);
        break;
      case VERTICAL:
        descriptor.setAngle(Math.PI / 2);
        descriptor.setAngleReference(LabelAngleReferences.ABSOLUTE);
        break;
    }

    descriptor.setDistanceToEdge(distanceToEdge);
    return descriptor;
  }

  public enum EdgeLabeling {
    NONE(0),

    INTEGRATED(1),

    GENERIC(2);

    private final int value;

    private EdgeLabeling( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EdgeLabeling fromOrdinal( int ordinal ) {
      for (EdgeLabeling current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  /**
   * Specifies constants for the preferred placement along an edge used by layout configurations.
   */
  public enum EnumLabelPlacementAlongEdge {
    ANYWHERE(0),

    AT_SOURCE_PORT(1),

    AT_TARGET_PORT(2),

    AT_SOURCE(3),

    AT_TARGET(4),

    CENTERED(5);

    private final int value;

    private EnumLabelPlacementAlongEdge( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumLabelPlacementAlongEdge fromOrdinal( int ordinal ) {
      for (EnumLabelPlacementAlongEdge current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  /**
   * Specifies constants for the preferred placement at a side of an edge used by layout configurations.
   */
  public enum EnumLabelPlacementSideOfEdge {
    ANYWHERE(0),

    ON_EDGE(1),

    LEFT(2),

    RIGHT(3),

    LEFT_OR_RIGHT(4);

    private final int value;

    private EnumLabelPlacementSideOfEdge( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumLabelPlacementSideOfEdge fromOrdinal( int ordinal ) {
      for (EnumLabelPlacementSideOfEdge current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

  /**
   * Specifies constants for the orientation of an edge label used by layout configurations.
   */
  public enum EnumLabelPlacementOrientation {
    PARALLEL(0),

    ORTHOGONAL(1),

    HORIZONTAL(2),

    VERTICAL(3);

    private final int value;

    private EnumLabelPlacementOrientation( final int value ) {
      this.value = value;
    }

    public int value() {
      return this.value;
    }

    public static final EnumLabelPlacementOrientation fromOrdinal( int ordinal ) {
      for (EnumLabelPlacementOrientation current : values()) {
        if (ordinal == current.value) return current;
      }
      throw new IllegalArgumentException("Enum has no value " + ordinal);
    }

  }

}
