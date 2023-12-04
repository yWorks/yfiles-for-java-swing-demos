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
package input.customsnapping;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.input.GridSnapTypes;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.view.GridStyle;
import com.yworks.yfiles.view.GridVisualCreator;
import com.yworks.yfiles.graph.styles.IShapeGeometry;
import com.yworks.yfiles.geometry.PathType;
import com.yworks.yfiles.graph.labelmodels.SmartEdgeLabelModel;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.CollectGraphSnapLinesEventArgs;
import com.yworks.yfiles.view.input.CollectSnapResultsEventArgs;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.ISnapLineProvider;
import com.yworks.yfiles.view.input.LabelSnapContext;
import com.yworks.yfiles.view.input.NodeSnapResultProvider;
import com.yworks.yfiles.view.input.OrthogonalSnapLine;
import com.yworks.yfiles.view.input.GridConstraintProvider;
import com.yworks.yfiles.view.input.SnapLine;
import com.yworks.yfiles.view.input.SnapLineOrientation;
import com.yworks.yfiles.view.input.SnapLineSnapTypes;
import com.yworks.yfiles.view.input.SnapPolicy;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.view.Pen;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Customize <code>SnapLine</code> behaviour.
 * <p>
 * It adds snap lines to the bounds of node and edge labels, lets star shaped nodes snap to the grid points
 * with their outline and uses free, movable snap lines nodes and labels can snap to.
 * </p>
 */
public class CustomSnappingDemo extends AbstractDemo {

  // the snap contexts used in this demo
  private GraphSnapContext graphSnapContext;
  private LabelSnapContext labelSnapContext;

  // the list of line visuals of the free, movable snap lines
  private List<LineVisual> freeSnapLineVisuals;

  /**
   * Returns a list of the {@link LineVisual}s of the free, movable snap lines used in this demo.
   */
  public List<LineVisual> getFreeSnapLineVisuals() {
    return freeSnapLineVisuals;
  }

  /**
   * Initializes this demo by configuring the model item lookups, the snap context, grid and input modes,
   * setting the graph defaults, adding custom snap lines and loading the sample graph.
   */
  public void initialize() {
    // add the custom snap line provider and snap result provider using the graph decorator
    initializeGraphDecorator();

    // initialize the snap context and registers our free snap lines
    initializeSnapContext();

    // initialize the grid the nodes can be snapped to
    initializeGrid();

    // initialize two free snap lines that are also visualized in the GraphCanvasComponent
    freeSnapLineVisuals = new ArrayList<>();
    addFreeSnapLine(new PointD(0, -70), new PointD(500, -70));
    addFreeSnapLine(new PointD(-230, -50), new PointD(-230, 400));

    // initialize the input mode for this demo
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setSnapContext(graphSnapContext);
    inputMode.setLabelSnapContext(labelSnapContext);

    // add an input mode that allows to move the free snap lines
    LineVisualMoveInputMode lineVisualMoveInputMode = new LineVisualMoveInputMode(freeSnapLineVisuals);
    lineVisualMoveInputMode.setPriority(-50);
    inputMode.add(lineVisualMoveInputMode);
    graphComponent.setInputMode(inputMode);

    // initialize the graph default so new nodes and label have the same look and feel as those in the example graph
    initializeGraphDefaults();
  }

  /**
   * Loads and centers a sample graph in the graph component.
   */
  public void onVisible() {
    loadSampleGraph();
  }

  /**
   * Decorates the ModelItem lookup for custom snapping behaviour.
   */
  private void initializeGraphDecorator() {
    GraphDecorator graphDecorator = graphComponent.getGraph().getDecorator();

    // add additional snap lines for orthogonal labels of nodes
    graphDecorator.getNodeDecorator().getSnapLineProviderDecorator().setImplementationWrapper(
        (item, baseImplementation) -> new OrthogonalLabelSnapLineProviderWrapper(baseImplementation));

    // add additional snap lines for orthogonal labels of edges
    graphDecorator.getEdgeDecorator().getSnapLineProviderDecorator().setImplementationWrapper(
        (item, baseImplementation) -> new OrthogonalLabelSnapLineProviderWrapper(baseImplementation));

    // for nodes using IShapeNodeStyle use a customized grid snapping behaviour based on their shape
    graphDecorator.getNodeDecorator().getNodeSnapResultProviderDecorator().setImplementation(
        node -> node.getStyle() instanceof ShapeNodeStyle, new ShapeBasedGridNodeSnapResultProvider());
  }

  /**
   * Creates and configures the snap context. Registers the free, movable snap lines to the snap context.
   */
  private void initializeSnapContext() {
    graphSnapContext = new GraphSnapContext();
    graphSnapContext.setGridSnapType(GridSnapTypes.ALL);
    graphSnapContext.setSnapDistance(10);

    labelSnapContext = new LabelSnapContext();
    labelSnapContext.setSnapDistance(10);
    labelSnapContext.setCollectingInitialLocationSnapLinesEnabled(false);

    // use the free, movable snap lines
    graphSnapContext.addCollectSnapLinesListener((source, args) -> collectFreeSnapLines(args::addAdditionalSnapLine));
    labelSnapContext.addCollectSnapLinesListener((source, args) -> collectFreeSnapLines(args::addSnapLine));
  }

  /**
   * Creates and adds {@link com.yworks.yfiles.view.input.SnapLine}s for the free {@link LineVisual}.
   * While the {@link LineVisual}s are used to visualize and represent free snap lines, according
   * {@link com.yworks.yfiles.view.input.OrthogonalSnapLine}s have to be added to the snapping mechanism to describe
   * their snapping behaviour.
   *
   * @param snapLineConsumer The consumer that adds the created snap lines to the collection event.
   */
  private void collectFreeSnapLines(Consumer<OrthogonalSnapLine> snapLineConsumer) {
    for (LineVisual line : getFreeSnapLineVisuals()) {
      PointD center = new PointD(
          (line.getFrom().getX() + line.getTo().getX()) / 2.0,
          (line.getFrom().getY() + line.getTo().getY()) / 2.0);

      if (line.getFrom().getX() == line.getTo().getX()) { // it's vertical
        snapLineConsumer.accept(new OrthogonalSnapLine(SnapLineOrientation.VERTICAL, SnapLineSnapTypes.LEFT,
            SnapLine.SNAP_LINE_FIXED_LINE_KEY, center, line.getFrom().getY(), line.getTo().getY(), line, 50));
        snapLineConsumer.accept(new OrthogonalSnapLine(SnapLineOrientation.VERTICAL, SnapLineSnapTypes.RIGHT,
            SnapLine.SNAP_LINE_FIXED_LINE_KEY, center, line.getFrom().getY(), line.getTo().getY(), line, 50));

      } else if (line.getFrom().getY() == line.getTo().getY()) { // it's horizontal
        snapLineConsumer.accept(new OrthogonalSnapLine(SnapLineOrientation.HORIZONTAL, SnapLineSnapTypes.TOP,
            SnapLine.SNAP_LINE_FIXED_LINE_KEY, center, line.getFrom().getX(), line.getTo().getX(), line, 50));
        snapLineConsumer.accept(new OrthogonalSnapLine(SnapLineOrientation.HORIZONTAL, SnapLineSnapTypes.BOTTOM,
            SnapLine.SNAP_LINE_FIXED_LINE_KEY, center, line.getFrom().getX(), line.getTo().getX(), line, 50));
      }
    }
  }

  /**
   * Adds grid to the GraphComponent and grid constraint provider to the snap context.
   */
  private void initializeGrid() {
    GridInfo gridInfo = new GridInfo(200);
    GridVisualCreator grid = new GridVisualCreator(gridInfo);
    grid.setGridStyle(GridStyle.CROSSES);
    graphComponent.getBackgroundGroup().addChild(grid, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);

    graphSnapContext.setNodeGridConstraintProvider(new GridConstraintProvider<>(gridInfo));
    graphSnapContext.setBendGridConstraintProvider(new GridConstraintProvider<>(gridInfo));
  }

  /**
   * Adds a new {@link LineVisual snap line} to the {@link GraphComponent} that spans between <code>from</code> and
   * <code>to</code>.
   *
   * @param from The start location of the snap line.
   * @param to   The end location of the snap line.
   */
  private void addFreeSnapLine(PointD from, PointD to) {
    // create line visual add it to background group
    LineVisual line = new LineVisual(from, to);
    graphComponent.getBackgroundGroup().addChild(line, ICanvasObjectDescriptor.VISUAL);
    freeSnapLineVisuals.add(line);
  }

  /**
   * Initializes styles for the nodes and labels of the graph.
   */
  private void initializeGraphDefaults() {
    IGraph graph = graphComponent.getGraph();

    DemoStyles.initDemoStyles(graph);

    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(
        FreeNodeLabelModel.INSTANCE.createParameter(
            new PointD(0.5, 0.0), new PointD(0, -10), new PointD(0.5, 1.0), PointD.ORIGIN, 0.0));
    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(new SmartEdgeLabelModel().createParameterFromSource(0, 0, 0.5));

    graph.getNodeDefaults().setSize(new SizeD(50, 50));
  }

  /**
   * Loads a sample graph.
   */
  private void loadSampleGraph() {
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/example.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new CustomSnappingDemo().start();
    });
  }

  /**
   * Wraps a given {@link com.yworks.yfiles.view.input.ISnapLineProvider} and adds additional
   * {@link com.yworks.yfiles.view.input.OrthogonalSnapLine}s for orthogonal labels of an {@link com.yworks.yfiles.graph.IModelItem}.
   * For each orthogonal label there are {@link com.yworks.yfiles.view.input.OrthogonalSnapLine}s added for its top,
   * bottom, left and right side.
   */
  private static class OrthogonalLabelSnapLineProviderWrapper implements ISnapLineProvider {
    private static final double EPS = 0.000001;

    private final ISnapLineProvider wrapped;

    /**
     * Creates a new instance that wraps the given <code>wrapped</code> snap line provider.
     *
     * @param wrapped the snap line provider that shall be wrapped
     */
    public OrthogonalLabelSnapLineProviderWrapper(ISnapLineProvider wrapped) {
      this.wrapped = wrapped;
    }

    /**
     * Calls {@link ISnapLineProvider#addSnapLines(com.yworks.yfiles.view.input.GraphSnapContext, com.yworks.yfiles.view.input.CollectGraphSnapLinesEventArgs, com.yworks.yfiles.graph.IModelItem)}
     * of the wrapped provider and adds custom {@link com.yworks.yfiles.view.input.OrthogonalSnapLine}s for the <code>item</code>.
     *
     * @param context The context which holds the settings for the snap lines.
     * @param args    The argument to use for adding snap lines.
     * @param item    The item to add snap lines for.
     */
    @Override
    public void addSnapLines(GraphSnapContext context, CollectGraphSnapLinesEventArgs args, IModelItem item) {
      wrapped.addSnapLines(context, args, item);

      // add snap lines for orthogonal labels
      if (item instanceof ILabelOwner) {
        ILabelOwner labeledItem = (ILabelOwner) item;
        for (ILabel label : labeledItem.getLabels()) {
          double upX = label.getLayout().getUpX();
          double upY = label.getLayout().getUpY();
          if (Math.abs(upX) < EPS || Math.abs(upY) < EPS) { // check if it's orthogonal
            // label is orthogonal
            RectD bounds = label.getLayout().getBounds();

            // add snap lines to the top, bottom, left and right border of the label
            PointD topCenter = PointD.add(bounds.getTopLeft(), new PointD(label.getLayout().getWidth() / 2, 0));
            OrthogonalSnapLine snapLine = new OrthogonalSnapLine(SnapLineOrientation.HORIZONTAL, SnapLineSnapTypes.BOTTOM,
                SnapLine.SNAP_LINE_FIXED_LINE_KEY, topCenter, bounds.getMinX() - 10, bounds.getMaxX() + 10, label, 100);
            args.addAdditionalSnapLine(snapLine);

            PointD bottomCenter = PointD.add(bounds.getBottomLeft(), new PointD(label.getLayout().getWidth() / 2, 0));
            snapLine = new OrthogonalSnapLine(SnapLineOrientation.HORIZONTAL, SnapLineSnapTypes.TOP,
                SnapLine.SNAP_LINE_FIXED_LINE_KEY, bottomCenter, bounds.getMinX() - 10, bounds.getMaxX() + 10, label,
                100);
            args.addAdditionalSnapLine(snapLine);

            PointD leftCenter = PointD.add(bounds.getTopLeft(), new PointD(0, label.getLayout().getHeight() / 2));
            snapLine = new OrthogonalSnapLine(SnapLineOrientation.VERTICAL, SnapLineSnapTypes.RIGHT,
                SnapLine.SNAP_LINE_FIXED_LINE_KEY, leftCenter, bounds.getMinY() - 10, bounds.getMaxY() + 10, label,
                100);
            args.addAdditionalSnapLine(snapLine);

            PointD rightCenter = PointD.add(bounds.getTopRight(), new PointD(0, label.getLayout().getHeight() / 2));
            snapLine = new OrthogonalSnapLine(SnapLineOrientation.VERTICAL, SnapLineSnapTypes.LEFT,
                SnapLine.SNAP_LINE_FIXED_LINE_KEY, rightCenter, bounds.getMinY() - 10, bounds.getMaxY() + 10, label,
                100);
            args.addAdditionalSnapLine(snapLine);
          }
        }
      }
    }
  }

  /**
   * Customizes the grid snapping behaviour of {@link com.yworks.yfiles.view.input.NodeSnapResultProvider} by providing
   * {@link com.yworks.yfiles.view.input.SnapResult}s for each point of the node's shape path instead of the node's center.
   */
  private static class ShapeBasedGridNodeSnapResultProvider extends NodeSnapResultProvider {

    @Override
    public void collectGridSnapResults(GraphSnapContext context, CollectSnapResultsEventArgs args,
                                       RectD suggestedLayout, INode node) {
      // The node layout isn't updated, yet, so we have to calculate the delta
      // between the the new suggested layout and the current node.Layout
      PointD delta = PointD.subtract(suggestedLayout.getTopLeft(), node.getLayout().getTopLeft());

      // get outline of the shape and iterate over its path point
      IShapeGeometry geometry = node.getStyle().getRenderer().getShapeGeometry(node, node.getStyle());
      GeneralPath outline = geometry.getOutline();
      if (outline != null) {
        GeneralPath.PathCursor cursor = outline.createCursor();
        while (cursor.moveNext()) {
          // ignore PathType.Close as we had the path point as first point
          // and cursor.CurrentEndPoint is always (0, 0) for PathType.Close
          if (cursor.getPathType() != PathType.CLOSE) {
            // adjust path point by the delta calculated above and add an according SnapResult
            PointD endPoint = PointD.add(cursor.getCurrentEndPoint(), delta);
            addGridSnapResultCore(context, args, endPoint, node, GridSnapTypes.GRID_POINTS, SnapPolicy.TO_NEAREST,
                SnapPolicy.TO_NEAREST);
          }
        }
      }
    }
  }
}
