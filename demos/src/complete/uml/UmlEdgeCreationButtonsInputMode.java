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
package complete.uml;

import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.NodeEventArgs;
import com.yworks.yfiles.graph.SimpleEdge;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.graph.SimplePort;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.utils.IEventListener;
import com.yworks.yfiles.utils.ObservableCollection;
import com.yworks.yfiles.utils.PropertyChangedEventArgs;
import com.yworks.yfiles.view.DefaultSelectionModel;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IBoundsProvider;
import com.yworks.yfiles.view.ICanvasContext;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.ICanvasObjectInstaller;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.ISelectionIndicatorInstaller;
import com.yworks.yfiles.view.IVisibilityTestable;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Mouse2DEventArgs;
import com.yworks.yfiles.view.SelectionIndicatorManager;
import com.yworks.yfiles.view.input.AbstractInputMode;
import com.yworks.yfiles.view.input.ConcurrencyController;
import com.yworks.yfiles.view.input.CreateEdgeInputMode;
import com.yworks.yfiles.view.input.DefaultPortCandidate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IHitTestable;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.InputModeEventArgs;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * An {@link IInputMode} which will provide buttons for edge creation for the graph component's current item.
 * When one of the buttons is dragged, a new edge with the specified style is created.
 *
 * @see UmlCreateEdgeInputMode
 */
public class UmlEdgeCreationButtonsInputMode extends AbstractInputMode {

  private final IEventListener<PropertyChangedEventArgs> onCurrentItemChanged = this::onCurrentItemChanged;
  private IEventListener<NodeEventArgs> onNodeRemoved = this::onNodeRemoved;
  private IEventListener<Mouse2DEventArgs> onMousePressed = this::onMousePressed;
  private IEventListener<Mouse2DEventArgs> onMouseMoved = this::onMouseMoved;
  private DefaultSelectionModel<IModelItem> buttonNodes = new DefaultSelectionModel<>();
  private SelectionIndicatorManager<IModelItem> manager;
  private ICanvasObjectGroup buttonGroup;

  @Override
  public void install(IInputModeContext context, ConcurrencyController concurrencyController) {
    super.install(context, concurrencyController);

    // use a selection indicator manager which only "selects" the current item
    // so the buttons are only displayed for one node
    GraphComponent graphComponent = (GraphComponent) getInputModeContext().getCanvasComponent();
    this.buttonGroup = graphComponent.getInputModeGroup().addGroup(); // draw on top of selection etc.
    this.manager = new SelectionIndicatorManager<IModelItem>(graphComponent,
        graphComponent.getSelectionIndicatorManager().getModel(), buttonNodes) {
      @Override
      protected ICanvasObjectInstaller getInstaller(IModelItem item) {
        if (item instanceof INode) {
          return (ISelectionIndicatorInstaller) (iCanvasContext, iCanvasObjectGroup, o) -> iCanvasObjectGroup.addChild(
              o, new UmlEdgeCreationButtonsDescriptor());
        }
        return super.getInstaller(item);
      }

      @Override
      protected ICanvasObjectGroup getCanvasObjectGroup(IModelItem item) {
        return buttonGroup;
      }
    };

    // keep buttons updated and their add interaction
    graphComponent.addCurrentItemChangedListener(onCurrentItemChanged);
    graphComponent.addMouse2DPressedListener(onMousePressed);
    graphComponent.addMouse2DMovedListener(onMouseMoved);
    graphComponent.getGraph().addNodeRemovedListener(onNodeRemoved);
  }

  /**
   * Called when the graph component's current item changed to move the buttons to the current item.
   */
  private void onCurrentItemChanged(Object o, PropertyChangedEventArgs propertyChangedEventArgs) {
    GraphComponent graphComponent = (GraphComponent) getInputModeContext().getCanvasComponent();
    if (graphComponent.getCurrentItem() instanceof INode) {
      buttonNodes.clear();
      buttonNodes.setSelected(graphComponent.getCurrentItem(), true);
    }
  }

  /**
   * Called when the graph component's current item changed to move the buttons to the current item.
   */
  private void onNodeRemoved(Object o, NodeEventArgs nodeEventArgs) {
    buttonNodes.setSelected(nodeEventArgs.getItem(), false);
  }

  /**
   * Called when the mouse button is pressed to initiate edge creation in case a button is hit.
   */
  private void onMousePressed(Object o, Mouse2DEventArgs mouse2DEventArgs) {
    if (this.isActive() && this.canRequestMutex()) {
      PointD p = mouse2DEventArgs.getLocation();
      GraphComponent graphComponent = (GraphComponent) this.getInputModeContext().getCanvasComponent();

      // check which node currently has the buttons and invoke create edge input mode to create a new edge
      for (IModelItem buttonNode : buttonNodes) {
        UmlButtonsVisual buttons = new UmlButtonsVisual((INode) buttonNode, graphComponent);
        if (buttons.hasButtonAt(p.getX(), p.getY())) {
          IInputMode parentInputMode = this.getInputModeContext().getParentInputMode();
          if (parentInputMode instanceof GraphEditorInputMode) {
            CreateEdgeInputMode createEdgeInputMode = ((GraphEditorInputMode) parentInputMode).getCreateEdgeInputMode();

            // initialize dummy edge
            IEdgeStyle umlEdgeType = getUmlEdgeType(p, buttons, graphComponent);
            IGraph dummyEdgeGraph = createEdgeInputMode.getDummyEdgeGraph();
            IEdge dummyEdge = createEdgeInputMode.getDummyEdge();
            dummyEdgeGraph.setStyle(dummyEdge, umlEdgeType);
            dummyEdgeGraph.getEdgeDefaults().setStyle(umlEdgeType);

            // start edge creation and hide buttons until the edge is finished
            buttonNodes.clear();
            createEdgeInputMode.doStartEdgeCreation(
                new DefaultPortCandidate((INode) buttonNode, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));
            IEventListener<InputModeEventArgs> edgeCreatedEvent = new IEventListener<InputModeEventArgs>() {
              @Override
              public void onEvent(Object source, InputModeEventArgs args) {
                buttonNodes.setSelected(graphComponent.getCurrentItem(), true);
                createEdgeInputMode.removeGestureCanceledListener(this);
                createEdgeInputMode.removeGestureFinishedListener(this);
              }
            };
            createEdgeInputMode.addGestureFinishedListener(edgeCreatedEvent);
            createEdgeInputMode.addGestureCanceledListener(edgeCreatedEvent);
            return;
          }
        }
      }
    }
  }

  /**
   * Called when the mouse is move over the canvas to change the cursor visualization when hovering over a edge creation
   * button.
   */
  private void onMouseMoved(Object o, Mouse2DEventArgs args) {
    PointD p = args.getLocation();
    for (IModelItem buttonNode : buttonNodes) {
      UmlButtonsVisual buttons = new UmlButtonsVisual((INode) buttonNode, (GraphComponent) getInputModeContext().getCanvasComponent());
      if (buttons.hasButtonAt(p.getX(), p.getY())) {
        getController().setPreferredCursor(new Cursor(Cursor.HAND_CURSOR));
        return;
      }
      getController().setPreferredCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }

  @Override
  public void uninstall(IInputModeContext context) {
    GraphComponent graphComponent = (GraphComponent) context.getCanvasComponent();
    graphComponent.removeMouse2DPressedListener(onMousePressed);
    graphComponent.removeMouse2DMovedListener(onMouseMoved);
    graphComponent.removeCurrentItemChangedListener(onCurrentItemChanged);
    graphComponent.getGraph().removeNodeRemovedListener(onNodeRemoved);
    this.buttonNodes.clear();
    this.manager.setModel(new ObservableCollection<>()); // clear all items and references to old model
    this.manager.setSelectionModel(new DefaultSelectionModel<>()); // clear reference to selection model
    this.manager = null;
    this.buttonGroup.remove();
    super.uninstall(context);
  }

  /**
   * Returns the edge style for the button at the given location.
   */
  public static IEdgeStyle getUmlEdgeType(PointD location, UmlButtonsVisual visual, GraphComponent graphComponent) {
    int type = visual.calculateButtonIndexAt(location.getX(), location.getY());
    return getUmlEdgeType(type, graphComponent);
  }

  /**
   * Returns the edge style for the given type.
   */
  public static IEdgeStyle getUmlEdgeType(int type, GraphComponent graphComponent) {
    switch (type) {
      case UmlButtonsVisual.TYPE_ASSOCIATION:
        return UmlStyleFactory.createAssociationStyle();
      case UmlButtonsVisual.TYPE_DEPENDENCY:
        return UmlStyleFactory.createDependencyStyle();
      case UmlButtonsVisual.TYPE_GENERALIZATION:
        return UmlStyleFactory.createGeneralizationStyle();
      case UmlButtonsVisual.TYPE_REALIZATION:
        return UmlStyleFactory.createRealizationStyle();
      case UmlButtonsVisual.TYPE_AGGREGATION:
        return UmlStyleFactory.createAggregationStyle();
      case UmlButtonsVisual.TYPE_COMPOSITION:
        return UmlStyleFactory.createCompositionStyle();
    }
    return graphComponent.getGraph().getEdgeDefaults().getStyle();
  }

  /**
   * The {@link ICanvasObjectDescriptor} which describes the edge creation buttons.
   */
  static class UmlEdgeCreationButtonsDescriptor implements ICanvasObjectDescriptor {
    @Override
    public IVisualCreator getVisualCreator(Object o) {
      return new UmlEdgeCreationButtonsVisualCreator((INode) o);
    }

    @Override
    public boolean isDirty(ICanvasContext iCanvasContext, ICanvasObject iCanvasObject) {
      return true;
    }

    @Override
    public IBoundsProvider getBoundsProvider(Object o) {
      INode node = (INode) o;
      return node.getStyle().getRenderer().getBoundsProvider(node, node.getStyle());
    }

    @Override
    public IVisibilityTestable getVisibilityTestable(Object o) {
      INode node = (INode) o;
      return node.getStyle().getRenderer().getVisibilityTestable(node, (node).getStyle());
    }

    @Override
    public IHitTestable getHitTestable(Object o) {
      INode node = (INode) o;
      return node.getStyle().getRenderer().getHitTestable(node, (node).getStyle());
    }
  }

  /**
   * The {@link IVisualCreator} which provides a visual of the edge creation buttons.
   */
  static class UmlEdgeCreationButtonsVisualCreator implements IVisualCreator {
    private final INode node;

    public UmlEdgeCreationButtonsVisualCreator(INode node) {
      this.node = node;
    }

    @Override
    public IVisual createVisual(IRenderContext iRenderContext) {
      return new UmlButtonsVisual(node, (GraphComponent) iRenderContext.getCanvasComponent());
    }

    @Override
    public IVisual updateVisual(IRenderContext iRenderContext, IVisual iVisual) {
      return this.createVisual(iRenderContext);
    }
  }

  /**
   * Visualization of edge creation buttons which provide several UML edge styles.
   */
  static class UmlButtonsVisual implements IVisual {
    private static final int BUTTON_COUNT = 6;
    private static final int RADIUS = 15;
    private static final int DIAMETER = RADIUS * 2;
    private static final int GAP = 20;

    static final int TYPE_ASSOCIATION = 0;
    static final int TYPE_DEPENDENCY = 1;
    static final int TYPE_GENERALIZATION = 2;
    static final int TYPE_REALIZATION = 3;
    static final int TYPE_AGGREGATION = 4;
    static final int TYPE_COMPOSITION = 5;

    private final INode node;
    private final GraphComponent graphComponent;
    private final YPoint startOffset;
    private final double[] angles;
    private int selectedIndex;
    private int progress;
    final SimpleEdge dummyEdge;

    public UmlButtonsVisual(INode node, GraphComponent graphComponent) {
      this.node = node;
      this.graphComponent = graphComponent;

      // initialize start position and angles between start and end positions for the buttons
      // start at 22.5 degrees inside the node and then add 45 degrees more to every next end position
      startOffset = new YPoint(DIAMETER * -1.35, DIAMETER * 0.6);
      angles = new double[BUTTON_COUNT];
      for (int i = 0; i < BUTTON_COUNT; i++) {
        angles[i] = (i + 1) * 0.7853;
      }

      this.selectedIndex = -1;
      this.progress = 1;

      // create a dummy edge for drawing the the icon with an appropriate style
      SimpleNode n1 = new SimpleNode();
      n1.setLayout(new RectD(DIAMETER * 0.25, DIAMETER * 0.75, 0, 0));
      SimpleNode n2 = new SimpleNode();
      n2.setLayout(new RectD(DIAMETER * 0.75, DIAMETER * 0.25, 0, 0));
      dummyEdge = new SimpleEdge(
          new SimplePort(n1, FreeNodePortLocationModel.NODE_CENTER_ANCHORED),
          new SimplePort(n2, FreeNodePortLocationModel.NODE_CENTER_ANCHORED));
    }

    /**
     * Returns the index of the currently selected button.
     *
     * @return the index of the currently selected button.
     */
    public int getSelectedIndex() {
      return selectedIndex;
    }

    /**
     * Selects the button at the given index.
     */
    public void setSelectedIndex(int selectedIndex) {
      this.selectedIndex = selectedIndex;
    }

    @Override
    public void paint(IRenderContext iRenderContext, Graphics2D graphics2D) {
      Graphics2D gfx = (Graphics2D) graphics2D.create();

      try {
        // scale graphics context for drawing the zoom-invariant buttons
        double zoom = 1 / iRenderContext.getCanvasComponent().getZoom();
        gfx.scale(zoom, zoom);

        paintButtons(gfx, iRenderContext);

      } finally {
        gfx.dispose();
      }
    }

    /**
     * Paints the circular buttons to their current position depending on the node's position and the progress value.
     */
    private void paintButtons(Graphics2D graphics, IRenderContext iRenderContext) {
      Point2D position = new Point2D.Double(0, 0);
      for (int i = 0; i < BUTTON_COUNT; i++) {
        calcPosition(i, position);
        if (i != selectedIndex) {
          graphics.setColor(UmlStyleFactory.COLOR_BACKGROUND);
        } else {
          graphics.setColor(UmlStyleFactory.COLOR_SELECTION);
        }
        graphics.fillOval((int) position.getX(), (int) position.getY(), DIAMETER, DIAMETER);

        graphics.setColor(Color.DARK_GRAY);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawOval((int) position.getX(), (int) position.getY(), DIAMETER, DIAMETER);

        paintIcon(graphics, position, i, iRenderContext);
      }
    }

    /**
     * Calculates the positions of the buttons depending on the current time step. The result position is returned in
     * the
     * passed point.
     */
    private void calcPosition(int buttonIndex, Point2D position) {
      int part = buttonIndex / BUTTON_COUNT;
      Point2D anchor = getAnchor();
      if (progress >= part) {
        double angle = angles[buttonIndex] * (progress - part);
        double offsetX = startOffset.getX() * Math.cos(angle) - startOffset.getY() * Math.sin(angle);
        double offsetY = startOffset.getX() * Math.sin(angle) + startOffset.getY() * Math.cos(angle);

        position.setLocation(anchor.getX() + offsetX - RADIUS, anchor.getY() + offsetY - RADIUS);
      } else {
        position.setLocation(anchor.getX() + startOffset.getX() - RADIUS, anchor.getY() + startOffset.getY() - RADIUS);
      }
    }

    /**
     * Returns the upper-right corner of the associated node as anchor point for the buttons.
     */
    private Point2D getAnchor() {
      double zoom = graphComponent.getZoom();
      IRectangle layout = node.getLayout();
      double outline = (UmlStyleFactory.LINE_EDGE_CREATION_BUTTON_OUTLINE.getLineWidth() * zoom) * 0.5;
      double x = (layout.getX() + layout.getWidth()) * zoom + outline;
      double y = layout.getY() * zoom - outline;
      return new Point2D.Double(x, y);
    }

    /**
     * Paints the passed type of edge icon on the at the given position.
     * @param graphics       the current graphics context.
     * @param position       the position of the icon.
     * @param type           which icon to paint.
     * @param iRenderContext the current context for rendering
     */
    private void paintIcon(Graphics2D graphics, Point2D position, int type, IRenderContext iRenderContext) {
      IEdgeStyle style = getUmlEdgeType(type, graphComponent);
      IVisual visual = style.getRenderer().getVisualCreator(dummyEdge, style).createVisual(iRenderContext);

      Graphics2D g = (Graphics2D) graphics.create();
      g.translate(position.getX(), position.getY());
      visual.paint(iRenderContext, g);
      g.dispose();
    }

    /**
     * Checks whether or not there is a button at the given coordinates.
     *
     * @param x x-coordinate in world coordinates.
     * @param y y-coordinate in world coordinates.
     *
     * @return <code>true</code> if there is a button at the given coordinates, <code>false</code> otherwise.
     */
    public boolean hasButtonAt(double x, double y) {
      int index = calculateButtonIndexAt(x, y);
      return index >= 0;
    }

    /**
     * Determines the index of the button at the given coordinates.
     *
     * @param x x-coordinate in world coordinates.
     * @param y y-coordinate in world coordinates.
     *
     * @return the index of the button at the given coordinates or <code>-1</code> if there is no button at that
     * location.
     */
    public int calculateButtonIndexAt(double x, double y) {
      double zoom = graphComponent.getZoom();

      Point2D.Double position = new Point2D.Double(0, 0);
      for (int i = 0; i < BUTTON_COUNT; i++) {
        calcPosition(i, position);
        double posX = (position.getX() + RADIUS) / zoom;
        double posY = (position.getY() + RADIUS) / zoom;
        double radius = RADIUS / zoom;
        if (radius > Math.sqrt((posX - x) * (posX - x) + (posY - y) * (posY - y))) {
          return i;
        }
      }
      return -1;
    }
  }
}
