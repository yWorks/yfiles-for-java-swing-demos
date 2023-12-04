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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelDefaults;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.Animator;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.EdgeStyleDecorationInstaller;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.HighlightIndicatorManager;
import com.yworks.yfiles.view.IAnimation;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.MouseWheelBehaviors;
import com.yworks.yfiles.view.NodeStyleDecorationInstaller;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.VerticalAlignment;
import com.yworks.yfiles.view.ViewportChanges;
import com.yworks.yfiles.view.input.CreateEdgeInputMode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.GridConstraintProvider;
import com.yworks.yfiles.view.input.GridSnapTypes;
import com.yworks.yfiles.view.input.HandleInputMode;
import com.yworks.yfiles.view.input.HoveredItemChangedEventArgs;
import com.yworks.yfiles.view.input.IEventRecognizer;
import com.yworks.yfiles.view.input.IHandle;
import com.yworks.yfiles.view.input.IHandleProvider;
import com.yworks.yfiles.view.input.INodeSnapResultProvider;
import com.yworks.yfiles.view.input.IPortCandidateProvider;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;
import com.yworks.yfiles.view.input.ItemHoverInputMode;
import com.yworks.yfiles.view.input.MoveInputMode;
import toolkit.AbstractDemo;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


/**
 * Shows how to create a project schedule visualization or "Gantt chart" with
 * yFiles.
 */
public class GanttChartDemo extends AbstractDemo {
  private GraphComponent taskComponent;
  private GraphComponent timelineComponent;

  private final Mapper<INode, Activity> activityMapper = new Mapper<INode, Activity>();

  /**
   * A helper class that handles the data model and maps graph
   * coordinates to the corresponding dates.
   */
  private final GanttDataUtil data = new GanttDataUtil();


  /**
   * Configures the demo GUI. Adds a {@link #taskComponent} to the left of and
   * a {@link #timelineComponent} above the main {@link #graphComponent}.
   */
  @Override
  protected void configure( JRootPane rootPane ) {
    taskComponent = createTaskComponent();
    graphComponent = createMainGraphComponent();
    timelineComponent = createTimelineGraphComponent();

    JPanel demoPane = new JPanel(new GridBagLayout());
    demoPane.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 0;
    demoPane.add(timelineComponent, gbc);

    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.weightx = 0;
    gbc.weighty = 1;
    demoPane.add(taskComponent, gbc);

    gbc.fill = GridBagConstraints.BOTH;
    ++gbc.gridx;
    gbc.weightx = 1;
    gbc.weighty = 1;
    demoPane.add(graphComponent, gbc);

    Container contentPane = rootPane.getContentPane();
    contentPane.add(demoPane, BorderLayout.CENTER);

    JComponent helpPane = createHelpPane();
    if (helpPane != null) {
      helpPane.setPreferredSize(new Dimension(300, 250));
      contentPane.add(helpPane, BorderLayout.EAST);
    }
  }


  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    // configure default styles.
    initializeGraphDefaults();

    // configure highlighting
    initializeHighlightStyles();

    // make sure each component stays in sync with scrolling
    synchronizeComponents();

    // configure user interaction, part 1
    // install custom IPortCandidateProvider, INodeSnapResultProvider,
    // IReshapeHandleProvider, and IHandleProvider.
    configureGraphDecorations();

    // configure user interaction, part 2
    initializeInputModes();


    // add the ICanvasObjectGroup which contains the tooltips
    ToolTipHelper.newInstance(graphComponent);

    // show tooltips if a node is clicked
    graphComponent.addCurrentItemChangedListener(( sender, args ) -> {
      ToolTipHelper.removeActivityInfo();

      GraphComponent component = (GraphComponent) sender;
      IModelItem item = component.getCurrentItem();
      if (item instanceof INode) {
        INode node = (INode) item;
        ToolTipHelper.showActivityInfo(node, component.getViewPoint(), component.getViewport().getWidth());
      }
    });


    // fill the graph with the information from the data model
    populateGraph();

    // update the task view with the information from the data model
    updateTasks();

    graphComponent.updateContentRect();
    taskComponent.updateContentRect();
  }

  /**
   * Initialize the styles that are used for highlighting nodes or edges.
   */
  private void initializeHighlightStyles() {
    GraphDecorator decorator = graphComponent.getGraph().getDecorator();

    // decorate the nodes and edges with custom hover highlight style
    NodeStyleDecorationInstaller decorationInstaller = new NodeStyleDecorationInstaller();
    decorationInstaller.setNodeStyle(new ActivityNodeHighlightStyle());
    decorationInstaller.setMargins(new InsetsD(2));
    decorator.getNodeDecorator().getHighlightDecorator().setImplementation(decorationInstaller);
    decorator.getNodeDecorator().getSelectionDecorator().hideImplementation();
    decorator.getNodeDecorator().getFocusIndicatorDecorator().hideImplementation();

    RoutingEdgeStyle edgeStyle = new RoutingEdgeStyle(10, 20, Colors.GOLDENROD, 3);
    EdgeStyleDecorationInstaller edgeStyleDecorationInstaller = new EdgeStyleDecorationInstaller();
    edgeStyleDecorationInstaller.setEdgeStyle(edgeStyle);
    decorator.getEdgeDecorator().getHighlightDecorator().setImplementation(edgeStyleDecorationInstaller);
    decorator.getEdgeDecorator().getSelectionDecorator().hideImplementation();
    decorator.getEdgeDecorator().getFocusIndicatorDecorator().hideImplementation();
  }

  /**
   * Configures the default styles nodes, edges, and labels.
   */
  private void initializeGraphDefaults() {
    IGraph graph = graphComponent.getGraph();
    graph.getNodeDefaults().setStyle(new ActivityNodeStyle());

    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setTextPaint(Color.WHITE);
    defaultLabelStyle.setTextWrapping(TextWrapping.WRAP);
    defaultLabelStyle.setTextAlignment(TextAlignment.CENTER);
    defaultLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    ILabelDefaults labelDefaults = graph.getNodeDefaults().getLabelDefaults();
    labelDefaults.setStyle(defaultLabelStyle);
    labelDefaults.setLayoutParameter(InteriorStretchLabelModel.CENTER);

    graph.getEdgeDefaults().setStyle(new RoutingEdgeStyle(10, 20));
  }

  /**
   * Creates and initialzes graph and subrows.
   */
  private void populateGraph() {
    // load sample a graph ...
    loadGraph();

    // ... and update the datamodel
    updateModel();

    // put overlapping nodes in subrows of their assigned task
    updateSubrows(false, () -> {});
  }

  /**
   * Updates the data model with the information from the sample graph.
   */
  private void updateModel() {
    IGraph graph = graphComponent.getGraph();
    HashSet<Task> tasks = new HashSet<Task>();
    for (INode node : graph.getNodes()) {
      Activity activity = (Activity) node.getTag();
      activityMapper.setValue(node, activity);
      tasks.add(activity.getTask());
    }
    Task[] tmp = new Task[tasks.size()];
    tasks.toArray(tmp);
    Arrays.sort(tmp, Comparator.comparingInt(Task::getId));
    for (Task task : tmp) {
      data.addTask(task);
    }
  }

  /**
   * Calculates the new height for each task row, distributing overlapping
   * activity nodes over multiple subrows.
   */
  private void updateSubrowMappings() {
    // maps the task id to the task's activities
    HashMap<Task, List<INode>> task2Activities = new HashMap<>();

    // add each activity to its corresponding task
    for (INode node : graphComponent.getGraph().getNodes()) {
      Activity activity = (Activity) node.getTag();
      Task task = activity.getTask();
      if (task2Activities.containsKey(task)) {
        task2Activities.get(task).add(node);
      } else {
        ArrayList<INode> nodes = new ArrayList<>();
        nodes.add(node);
        task2Activities.put(task, nodes);
      }
    }

    // calculate the number of subrows for each task
    for (Task task : data.getTasks()) {
      task.setSubrowCount(calculateActivitySubrowMappingForTask(
        task2Activities.get(task)));
    }
  }

  /**
   * Analyzes node overlaps within a task lane and distributes overlapping
   * activity nodes over several subrows.
   * If nodes are moved in an animated fashion, node bounds are changed
   * asynchronously.
   * @param animate if {@code true}, the resulting node layout change is animated
   * @param onDone executed when all changes (synchronous and asynchronous) are done
   */
  private void updateSubrows( boolean animate, Runnable onDone ) {
    // update the subrow information stored in tasks
    updateSubrowMappings();

    // determine the nodes to re-position
    ArrayList<IAnimation> animations = new ArrayList<>();
    IGraph graph = graphComponent.getGraph();
    for (INode node : graph.getNodes()) {
      Activity activity = (Activity) node.getTag();
      // get the subrow index calculated earlier
      int subrowIndex = activity.getSubrowIndex();
      if (subrowIndex != -1) {
        IRectangle layout = node.getLayout();
        int yTop = data.getActivityY(activity);
        // check if we need to update the current layout
        if (layout.getY() != yTop) {
          RectD newLayout = new RectD(layout.getX(), yTop, layout.getWidth(), layout.getHeight());
          if (animate) {
            // create an animation for updating the node bounds
            IAnimation animation = IAnimation.createNodeAnimation(graph,
              node,
              newLayout,
              Duration.ofMillis(250));
            animations.add(animation);
          } else {
            // set the new bounds without animation
            graph.setNodeLayout(node, newLayout);
          }
        }
      }
    }

    if (animate && !animations.isEmpty()){
      // create a composite animation that executes all node transitions at the same time
      IAnimation compositeAnimation = IAnimation.createParallelAnimation(animations);
      // start the animation
      new Animator(graphComponent).animate(compositeAnimation).thenRun(onDone);
    } else {
      onDone.run();
    }
  }

  /**
   * Updates the task view.
   */
  private void updateTasks() {
    IGraph graph = taskComponent.getGraph();
    graph.clear();

    for (Task task : data.getTasks()) {
      //get the y coordinate and height
      double from = data.getTaskY(task);
      double height = data.getCompleteTaskHeight(task) + GanttDataUtil.TASK_SPACING;

      // create the node
      Color color = null != task.getColor() ? task.getColor() : new Color(51, 102, 255);
      ShapeNodeStyle style = new ShapeNodeStyle();
      style.setPaint(new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));
      style.setPen(Pen.getTransparent());

      double width = taskComponent.getPreferredSize().getWidth();
      INode node = graph.createNode(new RectD(0, from, width, height), style);

      node.setTag(task);
      graph.addLabel(node, task.getName());
    }
  }

  /**
   * Calculates the respective subrow for each of the given activities.
   * This achieved be means of a sweep line, or scan line, algorithm:
   * The activities are sorted by their x-coordinate. The algorithm runs over
   * the activities from left to right and chooses the first available subrow
   * for each task until all activities have been assigned to a subrow.
   * @param activities all activities that make up a task
   * @return The number of subrows needed for the given activities.
   */
  private int calculateActivitySubrowMappingForTask( List<INode> activities ) {
    int subrowCount = 0;
    if (activities != null && !activities.isEmpty()) {
      //create an event array for the sweep-line algorithm
      ArrayList<Event> sweeplineData = new ArrayList<>();

      for (INode node : activities) {
        INodeStyle nodeStyle = node.getStyle();
        RectD bounds = nodeStyle.getRenderer().getBoundsProvider(node, nodeStyle)
          .getBounds(graphComponent.getCanvasContext());
        double xStart = bounds.getX();
        double xEnd = bounds.getX() + bounds.getWidth();
        // push the information where the activity starts
        sweeplineData.add(new Event(xStart, node, true));
        // push the information where the task ends
        sweeplineData.add(new Event(xEnd, node, false));
      }

      // sort by x coordinates
      sweeplineData.sort(Comparator.comparingDouble(t -> t.x));

      // count how many activities intersect each other.
      // This is the amount of subrows needed for the given task.
      ArrayList<Activity> open = new ArrayList<Activity>();
      for (Event event : sweeplineData) {
        Activity activity = (Activity) event.node.getTag();
        if (event.open) {
          int idx = 0;
          for (int n = open.size(); idx < n; ++idx) {
            if (idx < open.get(idx).getSubrowIndex()) {
              break;
            }
          }
          activity.setSubrowIndex(idx);
          open.add(idx, activity);

          subrowCount = Math.max(subrowCount, open.size());
        } else {
          open.remove(activity);
        }
      }
    }
    return subrowCount;
  }

  /**
   * Configures Gantt-specific user interaction.
   */
  private void initializeInputModes() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.addDeletedItemListener(( sender, args ) -> onGraphModified());
    geim.setToolTipItems(GraphItemTypes.NONE);

    initializeSnapping(geim);

    geim.setCreateBendAllowed(false); // disable bend creation
    geim.setCreateNodeAllowed(false); // disable default node creation
    geim.setShowHandleItems(GraphItemTypes.NODE); // show only node handles
    geim.setClickHitTestOrder(
      GraphItemTypes.BEND,
      GraphItemTypes.EDGE_LABEL,
      GraphItemTypes.NODE,
      GraphItemTypes.EDGE,
      GraphItemTypes.NODE_LABEL,
      GraphItemTypes.PORT);
    geim.setFocusableItems(GraphItemTypes.NODE);
    geim.setSelectableItems(GraphItemTypes.NODE);

    // on clicks on empty space, hide the node info
    geim.addCanvasClickedListener(( sender, args ) -> hideActivityInfo());

    // disable default marquee selection
    geim.getMarqueeSelectionInputMode().setEnabled(false);

    // configure edge creation
    configureCreateEdgeInputMode(geim);

    // assign a custom HandleInputMode for node resize handles
    geim.setHandleInputMode(createHandleInputMode());

    // add an input mode for creating new tasks with the same priority as MarqueeSelectionInputMode
    //create the customized input mode
    CreateActivityInputMode createActivityInputMode = new CreateActivityInputMode(data);
    createActivityInputMode.setOnNodeCreated(this::onNodeCreated);
    createActivityInputMode.setPriority(geim.getMarqueeSelectionInputMode().getPriority());
    geim.setMarqueeSelectionInputMode(createActivityInputMode);

    // disable default move gestures
    geim.getMoveInputMode().setEnabled(false);
    // create and add input mode that moves unselected nodes
    configureMoveInputMode(geim);

    geim.getTextEditorInputMode().addEditingStartedListener(( sender, args ) -> hideActivityInfo());

    // If an activity node's text changed, update the activity name accordingly
    geim.addLabelTextChangedListener(( sender, args ) -> {
      INode node = (INode) args.getOwner();
      ILabel label = args.getItem();
      ((Activity) node.getTag()).setName(label.getText());
    });

    // configure node and edge highlights on hover
    ItemHoverInputMode itemHoverInputMode = geim.getItemHoverInputMode();
    itemHoverInputMode.setEnabled(true);
    itemHoverInputMode.setHoverItems(GraphItemTypes.EDGE.or(GraphItemTypes.NODE));
    itemHoverInputMode.setInvalidItemsDiscardingEnabled(false);
    itemHoverInputMode.addHoveredItemChangedListener(this::onHoveredItemChanged);

    // assign editor input mode
    graphComponent.setInputMode(geim);
  }

  /**
   * Initializes snapping for moving and resizing nodes.
   */
  private void initializeSnapping( GraphEditorInputMode geim ) {
    // grid to snap to hours
    GridInfo gridInfo = new GridInfo();
    gridInfo.setHorizontalSpacing(GanttDataUtil.DAY_WIDTH);

    GraphSnapContext snapContext = new GraphSnapContext();
    snapContext.setEnabled(true);
    snapContext.setSnappingBendAdjacentSegmentsEnabled(false);
    snapContext.setSnappingBendsToSnapLinesEnabled(false);
    snapContext.setSnappingNodesToSnapLinesEnabled(false);
    snapContext.setSnappingOrthogonalMovementEnabled(false);
    snapContext.setSnappingPortAdjacentSegmentsEnabled(false);
    snapContext.setSnappingSegmentsToSnapLinesEnabled(false);

    snapContext.setGridSnapType(GridSnapTypes.VERTICAL_LINES);
    snapContext.setSnapResultVisualizationEnabled(false);
    snapContext.setNodeGridConstraintProvider(new GridConstraintProvider<>(gridInfo));

    geim.setSnapContext(snapContext);
  }

  /**
   * Configures edge creation gestures.
   * This method will ensure that edge creation is only started if the
   * SHIFT modifier key is pressed. This is necessry to support moving nodes
   * that are not selected.
   * @see #configureMoveInputMode(GraphEditorInputMode)
   */
  private void configureCreateEdgeInputMode( GraphEditorInputMode geim ) {
    CreateEdgeInputMode ceim = geim.getCreateEdgeInputMode();
    ceim.setPrepareRecognizer(IEventRecognizer.createAndRecognizer(
      IEventRecognizer.MOUSE_LEFT_PRESSED,
      IEventRecognizer.SHIFT_PRESSED));
    ceim.setSelfloopsAllowed(false);
    ceim.setCreateBendAllowed(false);
    ceim.setForcingSnapToCandidateEnabled(true);

    // only allow edges to connect to explicit candidates to make sure edges
    // only connect to the correct side of the node
    ceim.setUsingHitItemsCandidatesOnlyEnabled(true);
    ceim.setEnforceBendCreationRecognizer(IEventRecognizer.NEVER);
    ceim.setPortCandidateResolutionRecognizer(IEventRecognizer.NEVER);
  }

  /**
   * Configures node movement.
   * This method will ensure that nodes may be moved irrespective of selection
   * state provided the SHIFT modifier key is not pressed.
   * @see #configureCreateEdgeInputMode(GraphEditorInputMode)
   */
  private void configureMoveInputMode( GraphEditorInputMode geim ) {
    MoveInputMode moveUnselectedInputMode = geim.getMoveUnselectedInputMode();
    moveUnselectedInputMode.setPriority(geim.getCreateEdgeInputMode().getPriority() + 1);
    moveUnselectedInputMode.setEnabled(true);

    moveUnselectedInputMode.addDragStartedListener(( sender, args ) -> {
      hideActivityInfo();
      updateTimeInfo((MoveInputMode) sender);
    });

    moveUnselectedInputMode.addDraggingListener(
      ( sender, args ) -> updateTimeInfo((MoveInputMode) sender));

    moveUnselectedInputMode.addDragFinishedListener(( sender, args ) -> {
      MoveInputMode mode = (MoveInputMode) sender;

      INode node = getFirstNode(mode);
      if (node != null) {
        ToolTipHelper.removeTimeInfo();
        onNodeMoved(node);
      }
    });
    
    moveUnselectedInputMode.addDragCanceledListener(
      ( sender, args ) -> ToolTipHelper.removeTimeInfo());
  }

  /**
   * Creates a handle input mode that provides custom node resize handles.
   */
  private HandleInputMode createHandleInputMode() {
    NodeResizeHandleInputMode handleInputMode = new NodeResizeHandleInputMode();

    // show time tooltip for dragging gestures
    handleInputMode.addDragStartedListener(( sender, args ) -> {
      hideActivityInfo();
      updateTimeInfo((HandleInputMode) sender);
    });

    // show time tooltip for dragging gestures
    handleInputMode.addDraggingListener(( sender, args ) -> updateTimeInfo((HandleInputMode) sender));

    // apply the graph modifications and remove the info node when a handle has been dragged
    handleInputMode.addDragFinishedListener(( sender, args ) -> {
      ToolTipHelper.removeTimeInfo();

      HandleInputMode mode = (HandleInputMode) sender;
      IHandle handle = mode.getCurrentHandle();

      if (handle instanceof NodeResizeHandleProvider.NodeResizeHandle) {
        // depending on which side was dragged, update start or end date
        if (((NodeResizeHandleProvider.NodeResizeHandle) handle).isStart()) {
          onStartDateChanged(getFirstNode(mode));
        } else {
          onEndDateChanged(getFirstNode(mode));
        }
      } else if (handle instanceof TimeHandleProvider.TimeHandle) {
        onGraphModified();
      }
    });

    handleInputMode.addDragCanceledListener(
      ( sender, args ) -> ToolTipHelper.removeTimeInfo());

    return handleInputMode;
  }

  /**
   * Returns the first node that is affected by the given handle input mode.
   */
  private static INode getFirstNode( HandleInputMode mode ) {
    IEnumerable<IModelItem> items = mode.getAffectedItems();
    return getFirstNode(items);
  }

  /**
   * Returns the first node that is affected by the given move input mode.
   */
  private static INode getFirstNode( MoveInputMode mode ) {
    return getFirstNode(mode.getAffectedItems());
  }

  private static INode getFirstNode( Iterable<IModelItem> items ) {
    for (IModelItem item : items) {
      if (item instanceof INode) {
        return (INode) item;
      }
    }
    return null;
  }

  /**
   * Hides the tool tip that displays detailed information for selected nodes.
   */
  private void hideActivityInfo() {
    ToolTipHelper.removeActivityInfo();
    graphComponent.setCurrentItem(null);
  }

  /**
   * Updates the start date tool tip that is displayed while moving nodes. 
   */
  private void updateTimeInfo( MoveInputMode mode ) {
    INode node = getFirstNode(mode);
    if (node != null) {
      String text = data.getDateTimeString(node.getLayout().getX());
      ToolTipHelper.showTimeInfo(node, text, false);
    }
  }

  /**
   * Updates the start/end date tool tip that is displayed while resizing nodes. 
   */
  private void updateTimeInfo( HandleInputMode mode ) {
    INode node = getFirstNode(mode);
    if (node == null) {
      return;
    }

    Activity activity = (Activity) node.getTag();
    boolean followUp = true;

    String text;
    IHandle handle = mode.getCurrentHandle();
    if (handle instanceof TimeHandleProvider.TimeHandle) {
      followUp = ((TimeHandleProvider.TimeHandle) handle).isFollowUp();
      if (followUp) {
        text = "Follow-up time: " + ((int) activity.getFollowUpTime()) + 'h';
      } else {
        text = "Lead time: " + ((int) activity.getLeadTime()) + 'h';
      }
    } else if (handle instanceof NodeResizeHandleProvider.NodeResizeHandle) {
      followUp = !((NodeResizeHandleProvider.NodeResizeHandle) handle).isStart();
      text = data.getDateTimeString(handle.getLocation().getX());
    } else {
      double x = handle.getLocation().getX();
      double lead = activity.leadTimeWidth();
      IRectangle nl = node.getLayout();
      followUp = x > nl.getX() + lead +
        (nl.getWidth() - lead - activity.getFollowUpTime()) * 0.5;
      text = data.getDateTimeString(x);
    }

    ToolTipHelper.showTimeInfo(node, text, followUp);
  }

  /**
   * Updates data model and graph for the given node.
   */
  private void onNodeMoved( INode node ) {
    updateModel(node);
    onGraphModified();
  }

  /**
   * Configures user interaction through custom 
   * {@link IPortCandidateProvider},
   * {@link IReshapeHandleProvider},
   * {@link INodeSnapResultProvider}, and
   * {@link IHandleProvider}
   * implementations.
   */
  private void configureGraphDecorations() {
    NodeDecorator decorator = graphComponent.getGraph().getDecorator().getNodeDecorator();

    // install a custom port candidate provider that's needed for edge creation
    decorator.getPortCandidateProviderDecorator().setFactory(PortCandidateProvider::new);

    // install a custom reshape handle provider to customize node resizing behavior
    decorator.getReshapeHandleProviderDecorator().setImplementationWrapper(NodeResizeHandleProvider::new);

    // install a custom snap result provider to let the nodes snap to hours
    decorator.getNodeSnapResultProviderDecorator().setImplementation(new LeftNodeSnapResultProvider());

    // install a custom handle provider that provides the lead/followUp time handles
    decorator.getHandleProviderDecorator().setFactory(TimeHandleProvider::new);
  }

  /**
   * Updates the highlighted nodes and edges when the mouse is moved over a
   * node or an edge.
   */
  private void onHoveredItemChanged( Object sender, HoveredItemChangedEventArgs args ) {
    HighlightIndicatorManager<IModelItem> manager = graphComponent.getHighlightIndicatorManager();

    // remove previous highlights
    manager.clearHighlights();

    IModelItem item = args.getItem();
    if (item != null) {
      // highlight the node or edge
      manager.addHighlight(item);

      if (item instanceof INode) {
        // also highlight dependencies and their activities
        for (IEdge edge : graphComponent.getGraph().inEdgesAt((INode) item)) {
          manager.addHighlight(edge);
          manager.addHighlight(edge.getSourceNode());
        }
      } else if (item instanceof IEdge) {
        // highlight the source and target activity
        IEdge edge = (IEdge) item;
        manager.addHighlight(edge.getSourceNode());
        manager.addHighlight(edge.getTargetNode());
      }
    }
  }

  /**
   * Synchronizes scrolling of the main component with the other components.
   */
  private void synchronizeComponents() {
    graphComponent.addViewportChangedListener(( sender, args ) -> {
      GraphComponent component = (GraphComponent) sender;
      // synchronize the x-axis with time control and timeline control
      timelineComponent.setViewPoint(new PointD(
        component.getViewPoint().getX(),
        timelineComponent.getViewPoint().getY()));
      // synchronize y-axis with task control
      taskComponent.setViewPoint(new PointD(
        taskComponent.getViewPoint().getX(),
        component.getViewPoint().getY()));
    });
  }

  /**
   * Updates the start date of the activity represented by the given node.
   * @param node The node whose start date has been changed.
   */
  private void onStartDateChanged( INode node ) {
    // synchronize start time
    Activity activity = (Activity) node.getTag();
    double x = node.getLayout().getX() + activity.leadTimeWidth();
    LocalDateTime startDate = data.getDate(x);
    activity.setStartDate(startDate);
    onGraphModified();
  }

  /**
   * Updates the end date of the activity represented by the given node.
   * @param node The node whose end date has been changed.
   */
  private void onEndDateChanged( INode node ) {
    // synchronize end time
    Activity activity = (Activity) node.getTag();
    double x = node.getLayout().getMaxX() - activity.followUpTimeWidth();
    LocalDateTime endDate = data.getDate(x);
    activity.setEndDate(endDate);
    onGraphModified();
  }

  /**
   * Updates the data model when a new node is created.
   */
  public void onNodeCreated(INode node) {
    updateModel(node);
    onGraphModified();
  }

  /**
   * Does the necessary updates after all structural graph changes,
   * such as updating subrow information and refreshing the background.
   */
  private void onGraphModified() {
    // updates the multi-line placement
    updateSubrows(false, () -> {
      // updates the lane height of each task
      updateTasks();

      // update the scrollable area of each component
      graphComponent.updateContentRect();
      taskComponent.updateContentRect();

      // trigger a background refresh
      graphComponent.getBackgroundGroup().setDirty(true);
      graphComponent.invalidate();
    });
  }

  /**
   * Writes the start and end dates of the given node back to the data model.
   */
  private void updateModel( INode node ) {
    Activity activity = (Activity) node.getTag();
    IRectangle layout = node.getLayout();
    double minX = layout.getX() + activity.leadTimeWidth();
    activity.setStartDate(data.getDate(minX));
    double maxX = layout.getMaxX() - activity.followUpTimeWidth();
    activity.setEndDate(data.getDate(maxX));
    activityMapper.setValue(node, activity);

    Task newTask = data.getTask(layout.getY());
    if (activity.getTask() != newTask) {
      activity.setTask(newTask);
    }
  }

  /**
   * Creates the main {@link #graphComponent} displaying activities and their
   * dependencies.
   */
  private GraphComponent createMainGraphComponent() {
    GraphComponent component = new GraphComponent();
    component.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    component.setMouseWheelBehavior(MouseWheelBehaviors.SCROLL);
    component.setMouseWheelScrollFactor(20.0);
    component.setAnimatedViewportChanges(component.getAnimatedViewportChanges().and((ViewportChanges.SCROLL_COMMAND.inverse())));

    // install a viewport limiter so it's impossible to scroll out of the graph area
    component.setViewportLimiter(new RestrictedViewportLimiter(taskComponent));

    //limit zoom to 1
    component.setMaximumZoom(1);
    component.setMinimumZoom(1);

    //add the background visualization to the component
    component.getBackgroundGroup().addChild(
      new GridVisualCreator(data), ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);

    return component;
  }

  /**
   * Creates the {@link #taskComponent} showing all available tasks.
   */
  private GraphComponent createTaskComponent() {
    GraphComponent component = new GraphComponent();
    // hide scrollbars, switch off autodrag and the mouse-wheel
    component.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    component.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    component.setAutoDragEnabled(false);
    component.setMouseWheelBehavior(MouseWheelBehaviors.NONE);
    component.setPreferredSize(new Dimension(150, component.getHeight()));

    ILabelDefaults labelDefaults =
      component.getGraph().getNodeDefaults().getLabelDefaults();

    InteriorStretchLabelModel labelModel = new InteriorStretchLabelModel();
    labelModel.setInsets(new InsetsD(10));
    labelDefaults.setLayoutParameter(
      labelModel.createParameter(InteriorStretchLabelModel.Position.CENTER));

    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    defaultLabelStyle.setTextWrapping(TextWrapping.WRAP);
    defaultLabelStyle.setTextPaint(Color.WHITE);
    labelDefaults.setStyle(defaultLabelStyle);
    return component;
  }

  /**
   * Creates the {@link #timelineComponent} showing days and months.
   */
  private GraphComponent createTimelineGraphComponent() {
    GraphComponent component = new GraphComponent();
    // hide scrollbars, switch off autodrag and the mouse-wheel
    component.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    component.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    component.setAutoDragEnabled(false);
    component.setMouseWheelBehavior(MouseWheelBehaviors.NONE);
    component.setPreferredSize(new Dimension(component.getWidth(), 70));

    // add the visualization of days and months to the timeline component
    component.getBackgroundGroup().addChild(
      new TimelineVisualCreator(data),
      ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);

    return component;
  }
  
  @Override
  public void onVisible() {
    graphComponent.setViewPoint(PointD.ORIGIN);
    onGraphModified();
  }

  /**
   * Loads a sample graph.
   */
  private void loadGraph() {
    String namespace = "http://www.yworks.com/yFiles-for-java/demos/GanttChartDemo/1.0";

    Class<?> c = getClass();
    GraphMLIOHandler ioh = graphComponent.getGraphMLIOHandler();
    ioh.addNamespace(namespace, "demo");
    ioh.addXamlNamespaceMapping(namespace, c.getPackage().getName(), c.getClassLoader());

    try {
      graphComponent.importFromGraphML(c.getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main( final String[] args ) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new GanttChartDemo().start();
    });
  }



  /**
   * Event data struct for the sweep line algorithm used in
   * {@link #calculateActivitySubrowMappingForTask}.
   */
  private static final class Event {
    final double x;
    final INode node;
    final boolean open;

    Event( double x, INode node, boolean open ) {
      this.x = x;
      this.node = node;
      this.open = open;
    }
  }
}
