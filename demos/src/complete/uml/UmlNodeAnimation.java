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
package complete.uml;

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.IEdgeCursor;
import com.yworks.yfiles.algorithms.IEdgeMap;
import com.yworks.yfiles.algorithms.INodeCursor;
import com.yworks.yfiles.algorithms.Maps;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YDimension;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.ICompoundEdit;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.LayoutUtilities;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModel;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.VoidLabelStyle;
import com.yworks.yfiles.layout.AbstractLayoutStage;
import com.yworks.yfiles.layout.CopiedLayoutGraph;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.LayoutGraphAdapter;
import com.yworks.yfiles.layout.LayoutKeys;
import com.yworks.yfiles.layout.router.Scope;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.view.Animator;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IAnimation;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.input.ICommand;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Animates opening and closing of sections of the UML class.
 */
abstract class UmlNodeAnimation {
  private static double STROKE_WIDTH = 2d;

  GraphComponent graphComponent;
  IGraph graph;
  INode node;

  private INodeStyle oldStyle;
  private boolean isClosing;
  private double fixedY;
  private double movingY;
  private RectD closedLayout;
  private RectD openLayout;
  private ICompoundEdit edit;

  private final Map<IEdge, IPortLocationModel> sourcePortModels;
  private final Map<IEdge, IPortLocationModel> targetPortModels;


  UmlNodeAnimation(GraphComponent graphComponent, INode node, boolean isClosing) {
    this.node = node;
    this.graphComponent = graphComponent;
    this.graph = graphComponent.getGraph();
    this.isClosing = isClosing;

    sourcePortModels = new HashMap<>();
    targetPortModels = new HashMap<>();
  }

  void play() {
    edit = graph.beginEdit("Animated Change", "Animated Change");

    sourcePortModels.clear();
    targetPortModels.clear();

    // store source and target port locations to set node ports at these positions
    Map<IEdge, PointD> sourcePortLocations = new HashMap<>();
    Map<IEdge, PointD> targetPortLocations = new HashMap<>();
    for (IEdge edge : graph.edgesAt(node)) {
      IPort sp = edge.getSourcePort();
      sourcePortLocations.put(edge, sp.getLocation());
      sourcePortModels.put(edge, sp.getLocationParameter().getModel());
      IPort tp = edge.getTargetPort();
      targetPortLocations.put(edge, tp.getLocation());
      targetPortModels.put(edge, tp.getLocationParameter().getModel());
    }

    if (isClosing) { // is open
      this.openLayout = node.getLayout().toRectD();
      this.fixedY = getFixedY();
      this.movingY = getMovingY();
      this.closedLayout = calculateClosedBounds();
    } else { // is closed
      this.closedLayout = calculateClosedBounds();
      open();
      this.openLayout = node.getLayout().toRectD();
      this.fixedY = getFixedY();
      this.movingY = getMovingY();
    }

    // add node ports for all adjacent edges, these node ports get animated afterwards
    List<IEdge> outEdges = graph.outEdgesAt(node).stream().collect(Collectors.toList());
    for (IEdge edge : outEdges) {
      IPort sp = graph.addPort(node, AbsolutePortLocationModel.INSTANCE.createParameter(node, sourcePortLocations.get(edge)));
      graph.setEdgePorts(edge, sp, edge.getTargetPort());
    }
    List<IEdge> inEdges = graph.inEdgesAt(node).stream().collect(Collectors.toList());
    for (IEdge edge : inEdges) {
      IPort tp = graph.addPort(node, AbsolutePortLocationModel.INSTANCE.createParameter(node, targetPortLocations.get(edge)));
      graph.setEdgePorts(edge, edge.getSourcePort(), tp);
    }

    // if it is a closing animation, store the future size of the current node for layout
    if (isClosing) {
      graph.getMapperRegistry().createFunctionMapper(INode.class, YDimension.class, PreserveNodeSizeLayoutStage.NODE_SIZE_DPKEY,
          n -> n == node ? toYDimension(closedLayout) : null);
    }

    // only route adjacent edges of the current node
    EdgeRouter edgeRouter = new EdgeRouter();
    edgeRouter.setScope(Scope.ROUTE_EDGES_AT_AFFECTED_NODES);
    graph.getMapperRegistry().createFunctionMapper(INode.class, Boolean.class, LayoutKeys.AFFECTED_NODES_DPKEY, n -> n == node);

    CopiedLayoutGraph layoutGraph = new LayoutGraphAdapter(graph).createCopiedLayoutGraph();
    new PreserveNodeSizeLayoutStage(edgeRouter).applyLayout(layoutGraph);

    graph.getMapperRegistry().removeMapper(LayoutKeys.AFFECTED_NODES_DPKEY);
    graph.getMapperRegistry().removeMapper(PreserveNodeSizeLayoutStage.NODE_SIZE_DPKEY);

    // create visual for animation
    IVisual animationVisual = createAnimationVisual();
    useVoidStyles();

    IAnimation openCloseAnimation = new OpenCloseAnimation(animationVisual);
    IAnimation edgeAnimation = LayoutUtilities.createLayoutAnimation(graph, layoutGraph, Duration.ofMillis(300));
    IAnimation animation = IAnimation.createParallelAnimation(openCloseAnimation, edgeAnimation);

    // play animation
    Animator animator = new Animator(graphComponent);
    animator.animate(animation).thenRun(this::cleanup);
  }

  private YDimension toYDimension(IRectangle rect) {
    return new YDimension(rect.getWidth(), rect.getHeight());
  }

  private IVisual createAnimationVisual() {
    VisualGroup visualGroup = new VisualGroup();
    IRenderContext renderContext = graphComponent.createRenderContext();

    INodeStyle nodeStyle = node.getStyle();
    IVisual nodeVisual = nodeStyle.getRenderer().getVisualCreator(node, nodeStyle).createVisual(renderContext);
    visualGroup.add(nodeVisual);

    for (ILabel label : node.getLabels()) {
      ILabelStyle labelStyle = label.getStyle();
      IVisual labelVisual = labelStyle.getRenderer().getVisualCreator(label, labelStyle).createVisual(renderContext);
      visualGroup.add(labelVisual);
    }

    return visualGroup;
  }

  private void useVoidStyles() {
    IGraph graph = graphComponent.getGraph();
    oldStyle = node.getStyle();
    graph.setStyle(node, new UmlNodeStyle(false));
    for (ILabel label : node.getLabels()) {
      graph.setStyle(label, VoidLabelStyle.INSTANCE);
    }
  }

  void cleanup() {
    graphComponent.getGraph().setStyle(node, oldStyle);

    // if it is a closing animation, close the node while keeping the calculated port positions
    if (isClosing) {
      Map<IEdge, PointD> sourcePorts = new HashMap<>();
      Map<IEdge, PointD> targetPorts = new HashMap<>();
      for (IEdge edge : graph.edgesAt(node)) {
        sourcePorts.put(edge, edge.getSourcePort().getLocation());
        targetPorts.put(edge, edge.getTargetPort().getLocation());
      }

      close();

      for (IEdge edge : graph.edgesAt(node)) {
        IPortLocationModel sm = sourcePortModels.get(edge);
        graph.setPortLocationParameter(
                edge.getSourcePort(),
                sm.createParameter(edge.getSourceNode(), sourcePorts.get(edge)));
        IPortLocationModel tm = targetPortModels.get(edge);
        graph.setPortLocationParameter(
                edge.getTargetPort(),
                tm.createParameter(edge.getTargetNode(), targetPorts.get(edge)));
      }
    } else {
      UmlClassLabelSupport.updateAllLabels(graph, node);
      UmlClassLabelSupport.updateNodeSize(graph, node);

      for (IEdge edge : graph.edgesAt(node)) {
        IPort sp = edge.getSourcePort();
        IPortLocationModel sm = sourcePortModels.get(edge);
        graph.setPortLocationParameter(sp,
                sm.createParameter(edge.getSourceNode(), sp.getLocation()));
        IPort tp = edge.getTargetPort();
        IPortLocationModel tm = targetPortModels.get(edge);
        graph.setPortLocationParameter(tp,
                tm.createParameter(edge.getTargetNode(), tp.getLocation()));
      }
    }

    sourcePortModels.clear();
    targetPortModels.clear();

    if (edit != null) {
      edit.commit();
      edit = null;
    }
  }

  /**
   * Closes the part of the node whose change gets animated.
   */
  protected abstract void close();

  /**
   * Opens the part of the node whose change gets animated.
   */
  protected abstract void open();

  /**
   * Returns the lower y-coordinate of the part of the realizer that is fixed during the animation.
   *
   * @return the lower y-coordinate of the part of the realizer that is fixed during the animation
   */
  protected abstract double getFixedY();

  /**
   * Returns the upper y-coordinate of the part of the realizer that moves during the animation.
   *
   * @return the upper y-coordinate of the part of the realizer that moves during the animation
   */
  protected abstract double getMovingY();

  /**
   * Calculates the "closed state" node bounds.
   */
  protected abstract RectD calculateClosedBounds();


  static UmlNodeAnimation createAddAttributeAnimation(GraphComponent graphComponent, INode node) {
    return new AddItemAnimation(graphComponent, node, true);
  }

  static UmlNodeAnimation createAddOperationAnimation(GraphComponent graphComponent, INode node) {
    return new AddItemAnimation(graphComponent, node, false);
  }

  static UmlNodeAnimation createAttributeSectionAnimation(GraphComponent graphComponent, INode node, boolean isClosing) {
    return new AttributeSectionAnimation(graphComponent, node, isClosing);
  }

  static UmlNodeAnimation createOperationSectionAnimation(GraphComponent graphComponent, INode node, boolean isClosing) {
    return new OperationSectionAnimation(graphComponent, node, isClosing);
  }

  static UmlNodeAnimation createDetailSectionAnimation(GraphComponent graphComponent, INode node, boolean isClosing) {
    return new DetailSectionAnimation(graphComponent, node, isClosing);
  }

  static UmlNodeAnimation createRemoveItemAnimation(GraphComponent graphComponent, INode node) {
    return new RemoveItemAnimation(graphComponent, node);
  }



  private static final class AddItemAnimation extends UmlNodeAnimation {
    final boolean attribute;

    AddItemAnimation( GraphComponent graphComponent, INode node, boolean attribute) {
      super(graphComponent, node, false);
      this.attribute = attribute;
    }

    @Override
    protected void close() {
    }

    @Override
    protected void open() {
      if (attribute) {
        UmlClassLabelSupport.addAttribute(graph, node);
      } else {
        UmlClassLabelSupport.addOperation(graph, node);
      }
    }

    @Override
    protected double getFixedY() {
      ILabel label = UmlClassLabelSupport.getSelectedLabel(node);
      return label.getLayout().getBounds().getY();
    }

    @Override
    protected double getMovingY() {
      ILabel label = UmlClassLabelSupport.getSelectedLabel(node);
      return label.getLayout().getBounds().getMaxY();
    }

    @Override
    protected RectD calculateClosedBounds() {
      return node.getLayout().toRectD();
    }

    @Override
    void cleanup() {
      super.cleanup();

      ILabel label = UmlClassLabelSupport.getSelectedLabel(node);
      ICommand.EDIT_LABEL.execute(label, graphComponent);
    }
  }

  private static final class RemoveItemAnimation extends UmlNodeAnimation {
    RemoveItemAnimation( GraphComponent graphComponent, INode node ) {
      super(graphComponent, node, true);
    }

    @Override
    protected void close() {
    }

    @Override
    protected void open() {

    }

    @Override
    protected double getFixedY() {
      ILabel label = UmlClassLabelSupport.getSelectedLabel(node);
      return label.getLayout().getBounds().getY();
    }

    @Override
    protected double getMovingY() {
      ILabel label = UmlClassLabelSupport.getSelectedLabel(node);
      return label.getLayout().getBounds().getMaxY();
    }

    @Override
    protected RectD calculateClosedBounds() {
      ILabel label = UmlClassLabelSupport.getSelectedLabel(node);
      double lh = label.getLayout().getBounds().getHeight();
      IRectangle nl = node.getLayout();
      return new RectD(nl.getX(), nl.getY(), nl.getWidth(), nl.getHeight() - lh);
    }

    @Override
    void cleanup() {
      super.cleanup();

      UmlClassLabelSupport.removeSelectedLabel(graph, node);
    }
  }

  private static abstract class AbstractSectionAnimation extends UmlNodeAnimation  {
    AbstractSectionAnimation(GraphComponent graphComponent, INode node, boolean isClosing ) {
      super(graphComponent, node, isClosing);
    }

    protected void close() {
      UmlClassModel model = UmlClassLabelSupport.getModel(node);
      setSectionVisible(model, false);
      addUndoUnit(model);
      UmlClassLabelSupport.updateAllLabels(graph, node);
      UmlClassLabelSupport.updateNodeSize(graph, node);
    }

    protected void open() {
      UmlClassModel model = UmlClassLabelSupport.getModel(node);
      setSectionVisible(model, true);
      addUndoUnit(model);
      UmlClassLabelSupport.updateAllLabels(graph, node);
      UmlClassLabelSupport.updateNodeSize(graph, node);
    }

    @Override
    protected RectD calculateClosedBounds() {
      boolean visible = isSectionVisible(UmlClassLabelSupport.getModel(node));
      if (visible) {
        close();
      }
      RectD result = node.getLayout().toRectD();
      if (visible) {
        open();
      }
      return result;
    }

    abstract boolean isSectionVisible(UmlClassModel model);
    abstract void setSectionVisible(UmlClassModel model, boolean visible);

    abstract void addUndoUnit(UmlClassModel model);
  }

  /**
   * Animates the opening and closing of the attributes section.
   */
  private static class AttributeSectionAnimation extends AbstractSectionAnimation  {
    private AttributeSectionAnimation(GraphComponent graphComponent, INode node, boolean isClosing) {
      super(graphComponent, node, isClosing);
    }

    @Override
    boolean isSectionVisible(UmlClassModel model) {
      return model.areAttributesVisible();
    }

    @Override
    void setSectionVisible(UmlClassModel model, boolean visible) {
      model.setAttributesVisible(visible);
    }

    @Override
    void addUndoUnit( UmlClassModel model ) {
      UndoFactory.addVisibilityChangedUnit(graph, model, "attributes",
              UmlClassModel::areAttributesVisible,
              UmlClassModel::setAttributesVisible);
    }

    /**
     * The lower y-coordinate of the fixed part of the UML node is the bottom of its attribute caption label.
     */
    protected double getFixedY() {
      ILabel attributeCaptionLabel = UmlClassLabelSupport.getAttributeCaptionLabel(node);
      return attributeCaptionLabel.getLayout().getBounds().getMaxY();
    }

    /**
     * The upper y-coordinate of the moving part of the UML node is the top of its operation caption label.
     */
    protected double getMovingY() {
      ILabel operationCaptionLabel = UmlClassLabelSupport.getOperationCaptionLabel(node);
      return operationCaptionLabel.getLayout().getBounds().getY();
    }
  }

  /**
   * Animates the opening and closing of the operations section.
   */
  private static class OperationSectionAnimation extends AbstractSectionAnimation  {
    private OperationSectionAnimation(GraphComponent graphComponent, INode node, boolean isClosing) {
      super(graphComponent, node, isClosing);
    }

    @Override
    boolean isSectionVisible(UmlClassModel model) {
      return model.areOperationsVisible();
    }

    @Override
    void setSectionVisible(UmlClassModel model, boolean visible) {
      model.setOperationsVisible(visible);
    }

    @Override
    void addUndoUnit( UmlClassModel model ) {
      UndoFactory.addVisibilityChangedUnit(graph, model, "operations",
              UmlClassModel::areOperationsVisible,
              UmlClassModel::setOperationsVisible);
    }

    /**
     * The lower y-coordinate of the fixed part of the UML node is the bottom of its operation caption label.
     */
    protected double getFixedY() {
      ILabel operationCaptionLabel = UmlClassLabelSupport.getOperationCaptionLabel(node);
      return operationCaptionLabel.getLayout().getBounds().getMaxY();
    }

    /**
     * The upper y-coordinate of the moving part of the UML node is the bottom of its last label.
     */
    protected double getMovingY() {
      double movingY = getFixedY();
      for (ILabel label : node.getLabels()) {
        movingY = Math.max(movingY, label.getLayout().getBounds().getMaxY());
      }
      return movingY;
    }
  }

  /**
   * Animates the opening and closing of the details of the class.
   */
  private static class DetailSectionAnimation extends AbstractSectionAnimation  {
    private DetailSectionAnimation(GraphComponent graphComponent, INode node, boolean isClosing) {
      super(graphComponent, node, isClosing);
    }

    @Override
    boolean isSectionVisible(UmlClassModel model) {
      return model.areSectionsVisible();
    }

    @Override
    void setSectionVisible(UmlClassModel model, boolean visible) {
      model.setSectionsVisible(visible);
    }

    @Override
    void addUndoUnit( UmlClassModel model ) {
      UndoFactory.addVisibilityChangedUnit(graph, model, "sections",
              UmlClassModel::areSectionsVisible,
              UmlClassModel::setSectionsVisible);
    }

    /**
     * The lower y-coordinate of the fixed part of the UML node is the bottom of its name label.
     */
    protected double getFixedY() {
      ILabel nameLabel = UmlClassLabelSupport.getNameLabel(node);
      return nameLabel.getLayout().getBounds().getMaxY();
    }

    /**
     * The upper y-coordinate of the moving part of the UML node is the bottom of its last label.
     */
    protected double getMovingY() {
      double movingY = getFixedY();
      for (ILabel label : node.getLabels()) {
        movingY = Math.max(movingY, label.getLayout().getBounds().getMaxY());
      }
      return movingY;
    }
  }

  /**
   * Layout stage that takes care about the nodes sizes.
   * It resizes the current node to its future size after the animation so the layout is calculated correctly.
   * Afterwards, it restores the nodes size so it is consistent with node in the view graph.
   */
  private static final class PreserveNodeSizeLayoutStage extends AbstractLayoutStage {
    public static final Object NODE_SIZE_DPKEY = "PreserveNodeSizeLayoutStage.NODE_SIZE_DPKEY";

    public PreserveNodeSizeLayoutStage(ILayoutAlgorithm layout) {
      super(layout);
    }

    public void applyLayout(LayoutGraph graph) {
      // store old sizes and apply new sizes
      HashMap<Node, YDimension> oldSizes = new HashMap<>();
      IDataProvider dp = graph.getDataProvider(NODE_SIZE_DPKEY);
      if (dp != null) {
        for (INodeCursor nc = graph.getNodeCursor(); nc.ok(); nc.next()) {
          Node node = nc.node();
          Object size = dp.get(node);
          if (size instanceof YDimension) {
            oldSizes.put(node, graph.getSize(node));
            YPoint location = graph.getLocation(node);
            graph.setSize(node, (YDimension) size);
            graph.setLocation(node, location);
          }
        }
      }

      // do the actual layout
      applyLayoutCore(graph);

      // restore the old sizes
      for (INodeCursor nc = graph.getNodeCursor(); nc.ok(); nc.next()) {
        Node node = nc.node();
        YDimension size = oldSizes.get(node);
        if (size != null) {
          // the node gets closed
          // store source and target ports
          IEdgeMap sourcePorts = Maps.createHashedEdgeMap();
          IEdgeMap targetPorts = Maps.createHashedEdgeMap();
          for (IEdgeCursor ec = node.getEdgeCursor(); ec.ok(); ec.next()) {
            Edge edge = ec.edge();
            sourcePorts.set(edge, graph.getSourcePointAbs(edge));
            targetPorts.set(edge, graph.getTargetPointAbs(edge));
          }

          // change node size (ports are changed)
          YPoint location = graph.getLocation(node);
          graph.setSize(node, size);
          graph.setLocation(node, location);

          // restore source and target ports
          for (IEdgeCursor ec = node.getEdgeCursor(); ec.ok(); ec.next()) {
            Edge edge = ec.edge();
            graph.setSourcePointAbs(edge, (YPoint) sourcePorts.get(edge));
            graph.setTargetPointAbs(edge, (YPoint) targetPorts.get(edge));
          }
        }
      }
    }
  }

  class OpenCloseAnimation implements IAnimation, IVisual {
    private double state;
    private IVisual nodeVisual;
    private ICanvasObject canvasObject;

    OpenCloseAnimation(IVisual nodeVisual) {
      this.nodeVisual = nodeVisual;
      this.state = isClosing ? 1d : 0d;
    }

    @Override
    public void initialize() {
      // add the visual to the graph component
      canvasObject = graphComponent.getContentGroup().addChild(this, ICanvasObjectDescriptor.VISUAL);
    }

    @Override
    public void animate(double time) {
      state = isClosing ? 1.0 - time : time;
      animateNodeLayout(state);
    }

    private void animateNodeLayout(double state) {
      IGraph graph = graphComponent.getGraph();

      double x = openLayout.getX();
      double y = openLayout.getY();
      double w = closedLayout.getWidth() + (openLayout.getWidth() - closedLayout.getWidth()) * state;
      double h = closedLayout.getHeight() + (openLayout.getHeight() - closedLayout.getHeight()) * state;
      graph.setNodeLayout(node, new RectD(x, y, w, h));
    }

    @Override
    public void cleanUp() {
      // remove the visual from the graph component again
      canvasObject.remove();
    }

    @Override
    public Duration getPreferredDuration() {
      return Duration.ofMillis(300);
    }

    @Override
    public void paint(IRenderContext context, Graphics2D g) {
      Graphics2D graphics = (Graphics2D) g.create();
      try {
        double offset = fixedY + (movingY - fixedY) * state;

        // Paint the fix (upper) part of the node.
        Rectangle2D clipRect = new Rectangle2D.Double(
            openLayout.getX() - STROKE_WIDTH * 0.5,
            openLayout.getY() - STROKE_WIDTH * 0.5,
            openLayout.getWidth() * STROKE_WIDTH,
            offset - (openLayout.getY() - STROKE_WIDTH * 0.5));
        graphics.setClip(clipRect);
        nodeVisual.paint(context, graphics);

        // Paint the moving (lower) part of the node.
        clipRect.setFrame(
            openLayout.getX() - STROKE_WIDTH * 0.5,
            offset,
            openLayout.getWidth() + STROKE_WIDTH,
            openLayout.getMaxY()  + STROKE_WIDTH * 0.5 - movingY);
        graphics.setClip(clipRect);

        // Shift the lower part by offset.
        graphics.translate(0, -(movingY - offset));
        nodeVisual.paint(context, graphics);
      } finally {
        graphics.dispose();
      }
    }
  }
}
