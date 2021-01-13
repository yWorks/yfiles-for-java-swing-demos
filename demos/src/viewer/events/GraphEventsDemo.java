/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.events;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.BendEventArgs;
import com.yworks.yfiles.graph.EdgeEventArgs;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphClipboard;
import com.yworks.yfiles.graph.GraphCopier;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IBendLocationChangedHandler;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.INodeLayoutChangedHandler;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.ItemChangedEventArgs;
import com.yworks.yfiles.graph.ItemCopiedEventArgs;
import com.yworks.yfiles.graph.LabelEventArgs;
import com.yworks.yfiles.graph.LookupDecorator;
import com.yworks.yfiles.graph.NodeEventArgs;
import com.yworks.yfiles.graph.PortEventArgs;
import com.yworks.yfiles.graph.UndoEngine;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.utils.IEventListener;
import com.yworks.yfiles.utils.ItemEventArgs;
import com.yworks.yfiles.utils.PropertyChangedEventArgs;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ItemSelectionChangedEventArgs;
import com.yworks.yfiles.view.Mouse2DEventArgs;
import com.yworks.yfiles.view.PrepareRenderContextEventArgs;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.HandleInputMode;
import com.yworks.yfiles.view.input.HoveredItemChangedEventArgs;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IEdgeReconnectionPortCandidateProvider;
import com.yworks.yfiles.view.input.IPortCandidate;
import com.yworks.yfiles.view.input.InputModeEventArgs;
import com.yworks.yfiles.view.input.ItemClickedEventArgs;
import com.yworks.yfiles.view.input.LabelEditingEventArgs;
import com.yworks.yfiles.view.input.LabelTextValidatingEventArgs;
import com.yworks.yfiles.view.input.MoveInputMode;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.PopulateItemPopupMenuEventArgs;
import com.yworks.yfiles.view.input.PopulateMenuEventArgs;
import com.yworks.yfiles.view.input.QueryItemToolTipEventArgs;
import com.yworks.yfiles.view.input.SelectionEventArgs;
import com.yworks.yfiles.view.input.TextEventArgs;
import com.yworks.yfiles.view.input.ToolTipQueryEventArgs;
import toolkit.AbstractDemo;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Demonstrates the various events provided by the {@link IGraph},
 * the {@link com.yworks.yfiles.view.GraphComponent} and the input modes.
 */
public class GraphEventsDemo extends AbstractDemo {

  // region Private Fields

  private GraphEditorInputMode editorMode;
  private FoldingManager manager;
  private GraphViewerInputMode viewerMode;

  private JCheckBox logInputModeEvents;
  private JCheckBox logNavigationModeEvents;
  private JCheckBox logClickModeEvents;
  private JCheckBox logMoveModeEvents;
  private JCheckBox logMoveViewportModeEvents;
  private JCheckBox logHandleModeEvents;
  private JCheckBox logMouseHoverModeEvents;
  private JCheckBox logTextEditorModeEvents;
  private JCheckBox logPopupMenuModeEvents;
  private JCheckBox logCreateBendModeEvents;
  private JCheckBox logCreateEdgeModeEvents;
  private JCheckBox logItemHoverModeEvents;
  private JCheckBox logMoveLabelModeEvents;
  private JCheckBox logClipboardEvents;
  private JCheckBox logUndoEvents;
  private JCheckBox logClipboardCopierEvents;
  private JCheckBox logMouseEvents;
  private JCheckBox logKeyEvents;
  private JCheckBox logSelectionEvents;
  private JCheckBox logViewportEvents;
  private JCheckBox logRenderEvents;
  private JCheckBox logGraphComponentEvents;
  private JCheckBox logNodeEvents;
  private JCheckBox logEdgeEvents;
  private JCheckBox logLabelEvents;
  private JCheckBox logPortEvents;
  private JCheckBox logBendEvents;
  private JCheckBox logNodeBoundsEvents;
  private JCheckBox logGraphRenderEvents;
  private JCheckBox logAllInputEvents;
  private JCheckBox logAllGraphComponentEvents;
  private JCheckBox logAllGraphEvents;
  private JToggleButton toggleEditingButton;
  private JCheckBox groupEvents;

  private JList<ILogEntry> eventList;

  // endregion

  // region Configure JRootPane

  /**
   * Adds the panels for the event log options, help text and event logging as well as the
   * default graph component with toolbar and menu.
   */
  protected void configure(JRootPane rootPane) {
    JMenuBar menuBar = new JMenuBar();
    configureMenu(menuBar);
    rootPane.setJMenuBar(menuBar);

    Container contentPane = rootPane.getContentPane();

    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(graphComponent, BorderLayout.CENTER);
    JToolBar toolBar = createToolBar();
    configureToolBar(toolBar);
    centerPanel.add(toolBar, BorderLayout.NORTH);
    contentPane.add(centerPanel, BorderLayout.CENTER);

    JPanel leftPanel = new JPanel(new BorderLayout());
    JTabbedPane eventLogOptionsPane = new JTabbedPane(JTabbedPane.TOP);
    eventLogOptionsPane.setBorder(new TitledBorder("Event Log Options"));
    eventLogOptionsPane.addTab("Input Mode Events", createInputModeEventsPanel());
    eventLogOptionsPane.addTab("GraphComponent Events", createGraphComponentEventsPanel());
    eventLogOptionsPane.addTab("Graph Events", createGraphEventsPanel());
    leftPanel.add(eventLogOptionsPane, BorderLayout.NORTH);

    JComponent helpPane = createHelpPane();
    leftPanel.add(helpPane, BorderLayout.CENTER);
    contentPane.add(leftPanel, BorderLayout.WEST);

    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.setMinimumSize(new Dimension(400, Integer.MAX_VALUE));
    rightPanel.setPreferredSize(new Dimension(400, Integer.MAX_VALUE));
    rightPanel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
    JPanel eventLogPanel = new JPanel();
    eventLogPanel.setLayout(new BoxLayout(eventLogPanel, BoxLayout.X_AXIS));
    eventLogPanel.add(new JLabel("Event Log\t"));
    eventLogPanel.add(Box.createRigidArea(new Dimension(50, 1)));
    eventLogPanel.add(groupEvents = createCheckBox("Group Identical Events", null, true, (e) -> {}));
    eventLogPanel.add(new JButton(createClearAction()));
    rightPanel.add(eventLogPanel, BorderLayout.NORTH);
    // set the list of log entries as the list for the list view.
    eventList = new JList<>(entries);
    eventList.setEnabled(false);
    eventList.setCellRenderer(new MessageListCellRenderer());
    JScrollPane eventListScrollPane = new JScrollPane(eventList);
    rightPanel.add(eventListScrollPane, BorderLayout.CENTER);

    contentPane.add(rightPanel, BorderLayout.EAST);
  }

  /**
   * Configures the given {@link javax.swing.JMenuBar}.
   * @param menuBar the {@link javax.swing.JMenuBar} to configure
   */
  private void configureMenu(JMenuBar menuBar) {
    JMenu fileMenu = new JMenu("File");
    fileMenu.add(createCommandMenuItemAction("New", ICommand.NEW, null, graphComponent));
    fileMenu.add(createCommandMenuItemAction("Open", ICommand.OPEN, null, graphComponent));
    fileMenu.add(createCommandMenuItemAction("Save as...", ICommand.SAVE_AS, null, graphComponent));
    menuBar.add(fileMenu);

    JMenu editMenu = new JMenu("Edit");
    editMenu.add(createCommandMenuItemAction("Cut", ICommand.CUT, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Copy", ICommand.COPY, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Paste", ICommand.PASTE, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Duplicate", ICommand.DUPLICATE, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Delete", ICommand.DELETE, null, graphComponent));
    editMenu.addSeparator();
    editMenu.add(createCommandMenuItemAction("Undo", ICommand.UNDO, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Redo", ICommand.REDO, null, graphComponent));
    editMenu.addSeparator();
    editMenu.add(createCommandMenuItemAction("Reverse edge direction", ICommand.REVERSE_EDGE, null, graphComponent));
    menuBar.add(editMenu);

    JMenu viewMenu = new JMenu("View");
    viewMenu.add(createCommandMenuItemAction("Increase zoom", ICommand.INCREASE_ZOOM, null, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Zoom 1:1", ICommand.ZOOM, 1, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Decrease zoom", ICommand.DECREASE_ZOOM, null, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Fit Graph to Bounds", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
    menuBar.add(viewMenu);

    JMenu groupingMenu = new JMenu("Grouping");
    groupingMenu.add(createCommandMenuItemAction("Group Selection", ICommand.GROUP_SELECTION, null, graphComponent));
    groupingMenu.add(createCommandMenuItemAction("Ungroup Selection", ICommand.UNGROUP_SELECTION, null, graphComponent));
    groupingMenu.addSeparator();
    groupingMenu.add(createCommandMenuItemAction("Expand Group", ICommand.EXPAND_GROUP, null, graphComponent));
    groupingMenu.add(createCommandMenuItemAction("Collapse Group", ICommand.COLLAPSE_GROUP, null, graphComponent));
    groupingMenu.addSeparator();
    groupingMenu.add(createCommandMenuItemAction("Enter Group", ICommand.ENTER_GROUP, null, graphComponent));
    groupingMenu.add(createCommandMenuItemAction("Exit Group", ICommand.EXIT_GROUP, null, graphComponent));
    menuBar.add(groupingMenu);
  }

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("New", "new-document-16.png", ICommand.NEW, null, graphComponent));
    toolBar.add(createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, graphComponent));
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE_AS, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom 1:1", "zoom-original2-16.png", ICommand.ZOOM, 1, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Adjust the view port to show the complete graph", "fit2-16.png", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(toggleEditingButton = createToggleEditing());
    toolBar.add(createToggleOrthogonalEditing());
    toolBar.add(createToggleLassoSelection());
  }

  private JPanel createInputModeEventsPanel() {
    JPanel inputModeEventsPanel = new JPanel();
    inputModeEventsPanel.setLayout(new BoxLayout(inputModeEventsPanel, BoxLayout.Y_AXIS));
    inputModeEventsPanel.add(logAllInputEvents = createCheckBox("Check All", "Selects all events", false, this::toggleAllInputEventListener));
    inputModeEventsPanel.add(Box.createVerticalStrut(10));
    inputModeEventsPanel.add(logInputModeEvents = createCheckBox("Viewer/Editor Events", "Events dispatched by GraphViewerInputMode or GraphEditorInputMode.", false, this::toggleInputModeEventListener));
    inputModeEventsPanel.add(logNavigationModeEvents = createCheckBox("Navigation Events", "Events dispatched by NavigationInputMode when a group node was collapsed, expanded, entered or exited.", false, this::toggleNavigationModeEventListener));
    inputModeEventsPanel.add(logClickModeEvents = createCheckBox("Click Events", "Events dispatched by ClickInputMode.", false, this::toggleClickModeEventListener));
    inputModeEventsPanel.add(logMoveModeEvents = createCheckBox("Move Events", "Events dispatched by MoveInputMode when an item was moved.", false, this::toggleMoveModeEventListener));
    inputModeEventsPanel.add(logMoveViewportModeEvents = createCheckBox("Move Viewport Events", "Events dispatched by MoveViewportInputMode when the graph was panned or zoomed.", false, this::toggleMoveViewportModeEventListener));
    inputModeEventsPanel.add(logHandleModeEvents = createCheckBox("Handle Move Events", "Events dispatched by HandleInputMode when a node was resized or another handle was moved.", false, this::toggleHandleModeEventListener));
    inputModeEventsPanel.add(logMouseHoverModeEvents = createCheckBox("Mouse Hover Events", "Events dispatched by MouseHoverInputMode when an item was hovered for a certain amount of time (e.g. to show a ToolTip).", false, this::toggleMouseHoverModeEventListener));
    inputModeEventsPanel.add(logTextEditorModeEvents = createCheckBox("Text Editor Events", "Events dispatched by TextEditorInputMode during Label Editing.", false, this::toggleTextEditorModeEventListener));
    inputModeEventsPanel.add(logPopupMenuModeEvents = createCheckBox("Context Menu Events", "Events dispatched by PopupMenuInputMode.", false, this::togglePopupMenuModeEventListener));
    inputModeEventsPanel.add(logCreateBendModeEvents = createCheckBox("Create Bend Events", "Events dispatched by CreateBendInputMode during bend creation.", false, this::toggleCreateBendModeEventListener));
    inputModeEventsPanel.add(logCreateEdgeModeEvents = createCheckBox("Create Edge Events", "Events dispatched by CreateEdgeInputMode during edge creation.", false, this::toggleCreateEdgeModeEventListener));
    inputModeEventsPanel.add(logItemHoverModeEvents = createCheckBox("Item Hover Events", "Events dispatched by ItemHoverInputMode when the mouse enters or leaves an item.", false, this::toggleItemHoverModeEventListener));
    inputModeEventsPanel.add(logMoveLabelModeEvents = createCheckBox("Move Label Events", "Events dispatched by MoveLabelInputMode when a label was moved.", false, this::toggleMoveLabelModeEventListener));
    inputModeEventsPanel.add(logClipboardEvents = createCheckBox("Clipboard Events", "Events dispatched by the Clipboard when a Cut, Copy, or Paste operation occured.", false, this::toggleClipboardEventListener));
    inputModeEventsPanel.add(logUndoEvents = createCheckBox("Undo Events", "Events dispatched by Undo engine when an operation was undone or redone.", false, this::toggleUndoEventListener));
    return inputModeEventsPanel;
  }

  private JPanel createGraphComponentEventsPanel() {
    JPanel graphComponentEventsPanel = new JPanel();
    graphComponentEventsPanel.setLayout(new BoxLayout(graphComponentEventsPanel, BoxLayout.Y_AXIS));
    graphComponentEventsPanel.add(logAllGraphComponentEvents = createCheckBox("Check All", "Selects all events", false, this::toggleAllGraphComponentEventListener));
    graphComponentEventsPanel.add(Box.createVerticalStrut(10));
    graphComponentEventsPanel.add(logClipboardCopierEvents = createCheckBox("Clipboard Copier Events", "Logs actions in the clipboard and during duplication", false, this::toggleClipboardCopierEventListener));
    graphComponentEventsPanel.add(logMouseEvents = createCheckBox("Mouse Events", "Dispatched when the mouse is moved or mouse buttons are pressed.", false, this::toggleGraphComponentMouseEventListener));
    graphComponentEventsPanel.add(logKeyEvents = createCheckBox("Key Events", "Dispatched when a key is pressed or released.", false, this::toggleKeyEventListener));
    graphComponentEventsPanel.add(logSelectionEvents = createCheckBox("Selection Events", "Dispatched by GraphComponent.Selection when graph items are selected or deselected.", false, this::toggleSelectionEventListener));
    graphComponentEventsPanel.add(logViewportEvents = createCheckBox("Viewport Events", "Report changes of the view port and zoom level.", false, this::toggleGraphComponentViewportEventListener));
    graphComponentEventsPanel.add(logRenderEvents = createCheckBox("Render Events", "Dispatched when the GraphComponent is rendered.", false, this::toggleGraphComponentRenderEventListener));
    graphComponentEventsPanel.add(logGraphComponentEvents = createCheckBox("Other Events", "Logs changes of the Current Item, the complete graph or the input mode.", false, this::toggleGraphComponentEventListener));
    return graphComponentEventsPanel;
  }

  private JPanel createGraphEventsPanel() {
    JPanel graphEventsPanel = new JPanel();
    graphEventsPanel.setLayout(new BoxLayout(graphEventsPanel, BoxLayout.Y_AXIS));
    graphEventsPanel.add(logAllGraphEvents = createCheckBox("Check All", "Selects all events", false, this::toggleAllGraphEventListener));
    graphEventsPanel.add(Box.createVerticalStrut(10));
    graphEventsPanel.add(logNodeEvents = createCheckBox("Node Events", "Dispatched when a node is created, removed, or changed.", false, this::toggleNodeEventListener));
    graphEventsPanel.add(logEdgeEvents = createCheckBox("Edge Events", "Dispatched when an edge is created, removed, or changed.", false, this::toggleEdgeEventListener));
    graphEventsPanel.add(logLabelEvents = createCheckBox("Label Events", "Dispatched when an label is created, removed, or changed.", false, this::toggleLabelEventListener));
    graphEventsPanel.add(logPortEvents = createCheckBox("Port Events", "Dispatched when an port is created, removed, or changed.", false, this::togglePortEventListener));
    graphEventsPanel.add(logBendEvents = createCheckBox("Bend Events", "Dispatched when an bend is created, removed, or changed.", false, this::toggleBendEventListener));
    graphEventsPanel.add(logNodeBoundsEvents = createCheckBox("Node Bounds Events", "Dispatched when the bounds of a node are changed.", false, this::toggleNodeBoundsEventListener));
    graphEventsPanel.add(logGraphRenderEvents = createCheckBox("Graph Render Events", "Reports events that occur when the graph is rendered within a control.", false, this::toggleGraphRenderEventListener));
    return graphEventsPanel;
  }

  private Action createClearAction() {
    return new AbstractAction("Clear Log") {
      @Override
      public void actionPerformed(ActionEvent e) {
        clearButtonClick();
      }
    };
  }

  private void click(JToggleButton toggleButton) {
    toggleButton.setSelected(!toggleButton.isSelected());
    for (ActionListener listener : toggleButton.getActionListeners()) {
      listener.actionPerformed(new ActionEvent(toggleButton, ActionEvent.ACTION_PERFORMED, toggleButton.getActionCommand()));
    }
  }

  // endregion

  // region Initialization

  public void initialize() {
    // initialize the yfiles functionality (editor and viewer stuff)
    enableFolding();
    initializeGraph();
    initializeInputModes();
    setupToolTips();
    setupPopupMenu();
    enableUndo();

    // enable file IO so we can load a sample graph
    graphComponent.setFileIOEnabled(true);

    // load sample graph
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // enable some buttons per default
    click(logInputModeEvents);
    click(toggleEditingButton);
  }

  private void enableFolding() {
    IGraph graph = graphComponent.getGraph();

    // enabled changing ports
    LookupDecorator decorator = graph.getDecorator().getEdgeDecorator().getEdgeReconnectionPortCandidateProviderDecorator();
    decorator.setImplementation(IEdgeReconnectionPortCandidateProvider.ALL_NODE_CANDIDATES);

    manager = new FoldingManager(graph);
    graphComponent.setGraph(manager.createFoldingView().getGraph());
  }

  private void initializeGraph() {
    IGraph graph = graphComponent.getGraph();
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Colors.ORANGE);
    graph.getNodeDefaults().setStyle(nodeStyle);

    PanelNodeStyle groupStyle = new PanelNodeStyle();
    groupStyle.setColor(new Color(214, 229, 248, 255));
    groupStyle.setLabelInsetsColor(new Color(214, 229, 248, 255));
    groupStyle.setInsets(new InsetsD(18, 5, 5, 5));
    INodeDefaults groupNodeDefaults = graph.getGroupNodeDefaults();
    groupNodeDefaults.setStyle(new CollapsibleNodeStyleDecorator(groupStyle));
    groupNodeDefaults.getLabelDefaults().setLayoutParameter(InteriorStretchLabelModel.NORTH);
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setTextAlignment(TextAlignment.RIGHT);
    groupNodeDefaults.getLabelDefaults().setStyle(labelStyle);
  }

  private void initializeInputModes() {
    editorMode = new GraphEditorInputMode();
    OrthogonalEdgeEditingContext oec = new OrthogonalEdgeEditingContext();
    oec.setEnabled(false);
    oec.setPortMovingEnabled(true);
    editorMode.setOrthogonalEdgeEditingContext(oec);
    editorMode.setGroupingOperationsAllowed(true);
    editorMode.getItemHoverInputMode().setHoverItems(GraphItemTypes.ALL);

    viewerMode = new GraphViewerInputMode();
    viewerMode.getItemHoverInputMode().setHoverItems(GraphItemTypes.ALL);

    // add command binding for 'new'
    editorMode.getKeyboardInputMode().addCommandBinding(ICommand.NEW, this::executeNew, this::canExecuteNew);

    graphComponent.setInputMode(editorMode);
  }

  private void setupToolTips() {
    editorMode.setToolTipItems(GraphItemTypes.NODE);
    editorMode.addQueryItemToolTipListener((sender, args) -> {
      args.setToolTip("ToolTip for " + args.getItem());
      args.setHandled(true);
    });

    viewerMode.setToolTipItems(GraphItemTypes.NODE);
    viewerMode.addQueryItemToolTipListener((sender, args) -> {
      args.setToolTip("ToolTip for " + args.getItem());
      args.setHandled(true);
    });
  }

  private void setupPopupMenu() {
    editorMode.setPopupMenuItems(GraphItemTypes.NODE);
    editorMode.addPopulateItemPopupMenuListener((sender, args) -> {
      ((JPopupMenu) args.getMenu()).add(new JMenuItem("Dummy Item"));
      args.setHandled(true);
    });

    viewerMode.setPopupMenuItems(GraphItemTypes.NODE);
    viewerMode.addPopulateItemPopupMenuListener((sender, args) -> {
      ((JPopupMenu) args.getMenu()).add(new JMenuItem("Dummy Item"));
      args.setHandled(true);
    });
  }

  private void enableUndo() {
    manager.getMasterGraph().setUndoEngineEnabled(true);
  }

  /**
   * Helper that determines whether the {@link ICommand#NEW} can be executed.
   */
  private boolean canExecuteNew(ICommand command, Object param, Object sender) {
    // don't allow layouts for empty graphs
    IGraph graph = graphComponent.getGraph();
    return graph != null && graph.getNodes().size() != 0;
  }

  /**
   * Handler for the {@link ICommand#NEW}
   */
  private boolean executeNew(ICommand command, Object param, Object sender) {
    graphComponent.getGraph().clear();

    // update the can-execute-states of the commands since this is not
    // triggered by clearing the graph programmatically
    ICommand.invalidateRequerySuggested();

    return true;
  }

  @Override
  public void onVisible() {
    // move the viewport so the graph is centered
    graphComponent.fitGraphBounds();
  }




  IEventListener<ClickEventArgs> geimOnCanvasClicked = (source, args) -> log("GraphEditorInputMode CanvasClicked");

  IEventListener<ItemEventArgs<? extends IModelItem>> geimOnDeletedItem = (source, args) -> log("GraphEditorInputMode DeletedItem");

  IEventListener<SelectionEventArgs<IModelItem>> geimOnDeletedSelection = (source, args) -> log("GraphEditorInputMode DeletedSelection");

  IEventListener<SelectionEventArgs<IModelItem>> geimOnDeletingSelection = (source, args) -> log("GraphEditorInputMode DeletingSelection");

  IEventListener<ItemClickedEventArgs<IModelItem>> geimOnItemClicked = (source, args) -> log("GraphEditorInputMode ItemClicked " + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventListener<ItemClickedEventArgs<IModelItem>> geimOnItemDoubleClicked = (source, args) -> log("GraphEditorInputMode ItemDoubleClicked" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventListener<ItemClickedEventArgs<IModelItem>> geimOnItemLeftClicked = (source, args) -> log("GraphEditorInputMode ItemLeftClicked" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventListener<ItemClickedEventArgs<IModelItem>> geimOnItemLeftDoubleClicked = (source, args) -> log("GraphEditorInputMode ItemLeftDoubleClicked" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventListener<ItemClickedEventArgs<IModelItem>> geimOnItemRightClicked = (source, args) -> log("GraphEditorInputMode ItemRightClicked" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventListener<ItemClickedEventArgs<IModelItem>> geimOnItemRightDoubleClicked = (source, args) -> log("GraphEditorInputMode ItemRightDoubleClicked" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventListener<LabelEditingEventArgs> geimOnLabelAdding = (source, args) -> log("GraphEditorInputMode LabelAdding");

  IEventListener<LabelEventArgs> geimOnLabelAdded = (source, args) -> log("GraphEditorInputMode LabelAdded");

  IEventListener<LabelEditingEventArgs> geimOnLabelEditing = (source, args) -> log("GraphEditorInputMode LabelEditing");

  IEventListener<LabelEventArgs> geimOnLabelTextChanged = (source, args) -> log("GraphEditorInputMode LabelTextChanged");

  IEventListener<LabelEventArgs> geimOnLabelTextEditingStarted = (source, args) -> log("GraphEditorInputMode LabelTextEditingStarted");

  IEventListener<LabelEventArgs> geimOnLabelTextEditingCanceled = (source, args) -> log("GraphEditorInputMode LabelTextEditingCanceled");

  IEventListener<SelectionEventArgs<IModelItem>> geimOnMultiSelectionFinished = (source, args) -> log("GraphEditorInputMode MultiSelectionFinished");

  IEventListener<SelectionEventArgs<IModelItem>> geimOnMultiSelectionStarted = (source, args) -> log("GraphEditorInputMode MultiSelectionStarted");

  IEventListener<ItemEventArgs<INode>> geimOnNodeCreated = (source, args) -> log("GraphEditorInputMode NodeCreated");

  IEventListener<NodeEventArgs> geimOnNodeReparented = (source, args) -> log("GraphEditorInputMode NodeReparented");

  IEventListener<EdgeEventArgs> geimOnEdgePortsChanged = (source, args) -> log("GraphEditorInputMode Edge " + args.getItem() + " Ports Changed from " + args.getSourcePort() + "->" + args.getTargetPort() + " to "  + args.getItem().getSourcePort()+ "->" + args.getItem().getTargetPort());

  IEventListener<PopulateItemPopupMenuEventArgs<IModelItem>> geimOnPopulateItemPopupMenu = (source, args) -> log("GraphEditorInputMode PopulateItemPopupMenu" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventListener<QueryItemToolTipEventArgs<IModelItem>> geimOnQueryItemToolTip = (source, args) -> log("GraphEditorInputMode QueryItemToolTip" + (args.isHandled() ? "(Handled)" : "(Unhandled)"));

  IEventListener<LabelTextValidatingEventArgs> geimOnValidateLabelText = (source, args) -> log("GraphEditorInputMode ValidateLabelText");

  IEventListener<IEventArgs> geimOnElementsCopied = (source, args) -> log("GraphEditorInputMode ElementsCopied");

  IEventListener<IEventArgs> geimOnElementsCut = (source, args) -> log("GraphEditorInputMode ElementsCut");

  IEventListener<IEventArgs> geimOnElementsPasted = (source, args) -> log("GraphEditorInputMode ElementsPasted");



  IEventListener<ClickEventArgs> gvimOnCanvasClicked = (source, args) -> log("GraphViewerInputMode CanvasClicked");

  IEventListener<ItemClickedEventArgs<IModelItem>> gvimOnItemClicked = (source, args) -> log("GraphViewerInputMode ItemClicked");

  IEventListener<ItemClickedEventArgs<IModelItem>> gvimOnItemDoubleClicked = (source, args) -> log("GraphViewerInputMode ItemDoubleClicked");

  IEventListener<ItemClickedEventArgs<IModelItem>> gvimOnItemLeftClicked = (source, args) -> log("GraphViewerInputMode ItemLeftClicked");

  IEventListener<ItemClickedEventArgs<IModelItem>> gvimOnItemLeftDoubleClicked = (source, args) -> log("GraphViewerInputMode ItemLeftDoubleClicked");

  IEventListener<ItemClickedEventArgs<IModelItem>> gvimOnItemRightClicked = (source, args) -> log("GraphViewerInputMode ItemRightClicked");

  IEventListener<ItemClickedEventArgs<IModelItem>> gvimOnItemRightDoubleClicked = (source, args) -> log("GraphViewerInputMode ItemRightDoubleClicked");

  IEventListener<SelectionEventArgs<IModelItem>> gvimOnMultiSelectionFinished = (source, args) -> log("GraphViewerInputMode MultiSelectionFinished");

  IEventListener<SelectionEventArgs<IModelItem>> gvimOnMultiSelectionStarted = (source, args) -> log("GraphViewerInputMode MultiSelectionStarted");

  IEventListener<PopulateItemPopupMenuEventArgs<IModelItem>> gvimOnPopulateItemPopupMenu = (source, args) -> log("GraphViewerInputMode PopulateItemPopupMenu");

  IEventListener<QueryItemToolTipEventArgs<IModelItem>> gvimOnQueryItemToolTip = (source, args) -> log("GraphViewerInputMode QueryItemToolTip");

  IEventListener<IEventArgs> gvimOnElementsCopied = (source, args) -> log("GraphViewerInputMode ElementsCopied");




  IEventListener<ItemEventArgs<INode>> navigationInputModeOnGroupCollapsed = (source, evt) -> log("NavigationInputMode Group Collapsed: " + evt.getItem(), "GroupCollapsed");

  IEventListener<ItemEventArgs<INode>> navigationInputModeOnGroupCollapsing = (source, evt) -> log("NavigationInputMode Group Collapsing: " + evt.getItem(), "Group Collapsing");

  IEventListener<ItemEventArgs<INode>> navigationInputModeOnGroupEntered = (source, evt) -> log("NavigationInputMode Group Entered: " + evt.getItem(), "Group Entered");

  IEventListener<ItemEventArgs<INode>> navigationInputModeOnGroupEntering = (source, evt) -> log("NavigationInputMode Group Entering: " + evt.getItem(), "Group Entering");

  IEventListener<ItemEventArgs<INode>> navigationInputModeOnGroupExited = (source, evt) -> log("NavigationInputMode Group Exited: " + evt.getItem(), "Group Exited");

  IEventListener<ItemEventArgs<INode>> navigationInputModeOnGroupExiting = (source, evt) -> log("NavigationInputMode Group Exiting: " + evt.getItem(), "Group Exiting");

  IEventListener<ItemEventArgs<INode>> navigationInputModeOnGroupExpanded = (source, evt) -> log("NavigationInputMode Group Expanded: " + evt.getItem(), "Group Expanded");

  IEventListener<ItemEventArgs<INode>> navigationInputModeOnGroupExpanding = (source, evt) -> log("NavigationInputMode Group Expanding: " + evt.getItem(), "Group Expanding");



  IEventListener<ClickEventArgs> clickInputModeOnClicked = (source, args) -> log("ClickInputMode Clicked");

  IEventListener<ClickEventArgs> clickInputModeOnDoubleClicked = (source, args) -> log("ClickInputMode Double Clicked");

  IEventListener<ClickEventArgs> clickInputModeOnLeftClicked = (source, args) -> log("ClickInputMode Left Clicked");

  IEventListener<ClickEventArgs> clickInputModeOnLeftDoubleClicked = (source, args) -> log("ClickInputMode Left Double Clicked");

  IEventListener<ClickEventArgs> clickInputModeOnRightClicked = (source, args) -> log("ClickInputMode Right Clicked");

  IEventListener<ClickEventArgs> clickInputModeOnRightDoubleClicked = (source, args) -> log("ClickInputMode Right Double Clicked");



  KeyListener canvasComponentKeyListener = new KeyListener() {
    @Override
    public void keyTyped(KeyEvent e) {
      log("CanvasComponent KeyTyped");
    }

    @Override
    public void keyPressed(KeyEvent e) {
      log("CanvasComponent KeyPressed");
    }

    @Override
    public void keyReleased(KeyEvent e) {
      log("CanvasComponent KeyReleased");
    }
  };



  IEventListener<InputModeEventArgs> inputModeOnDragCanceled = (source, args) -> log(source.getClass().getSimpleName() + " DragCanceled", "DragCanceled");

  IEventListener<InputModeEventArgs> inputModeOnDragCanceling = (source, args) -> log(source.getClass().getSimpleName() + " DragCanceling", "DragCanceling");

  IEventListener<InputModeEventArgs> inputModeOnDragFinished = (source, args) -> log(source.getClass().getSimpleName() + " DragFinished", "DragFinished");

  IEventListener<InputModeEventArgs> inputModeOnDragFinishing = (source, args) -> log(source.getClass().getSimpleName() + " DragFinishing" + getAffectedItems(source), "DragFinishing");

  IEventListener<InputModeEventArgs> inputModeOnDragged = (source, args) -> log(source.getClass().getSimpleName() + " Dragged", "Dragged");

  IEventListener<InputModeEventArgs> inputModeOnDragging = (source, args) -> log(source.getClass().getSimpleName() + " Dragging", "Dragging");

  IEventListener<InputModeEventArgs> inputModeOnDragStarted = (source, args) -> log(source.getClass().getSimpleName() + " DragStarted" + getAffectedItems(source), "DragStarted");

  private static String getAffectedItems(Object sender) {
    IEnumerable<IModelItem> items = null;

    if (sender instanceof MoveInputMode) {
      MoveInputMode mim = (MoveInputMode)sender;
      items = mim.getAffectedItems();
    }
    if (sender instanceof HandleInputMode) {
      HandleInputMode him = (HandleInputMode)sender;
      items = him.getAffectedItems();
    }
    if (items != null) {
      int nodeCount = (int) items.stream().filter(iModelItem -> iModelItem instanceof INode).count();
      int edgeCount = (int) items.stream().filter(iModelItem -> iModelItem instanceof IEdge).count();
      int bendCount = (int) items.stream().filter(iModelItem -> iModelItem instanceof IBend).count();
      int labelCount = (int) items.stream().filter(iModelItem -> iModelItem instanceof ILabel).count();
      int portCount = (int) items.stream().filter(iModelItem -> iModelItem instanceof IPort).count();
      return String.format(" (%1$d items: %2$d nodes, %3$d bends, %4$d edges, %5$d labels, %6$d ports)", (int) items.stream().count(),
          nodeCount, bendCount, edgeCount, labelCount, portCount);
    } else {
      return "";
    }
  }

  IEventListener<InputModeEventArgs> inputModeOnDragStarting = (source, args) -> log(source.getClass().getSimpleName() + " DragStarting", "DragStarting");



  IEventListener<ToolTipQueryEventArgs> mouseHoverInputModeOnQueryToolTip = (source, args) -> log("MouseHoverInputMode QueryToolTip");



  IEventListener<TextEventArgs> textEditorInputModeOnEditingCanceled = (source, args) -> log("TextEditorInputMode Editing Canceled");

  IEventListener<TextEventArgs> textEditorInputModeOnEditingStarted = (source, args) -> log("TextEditorInputMode Editing Started");

  IEventListener<TextEventArgs> textEditorInputModeOnTextEdited = (source, args) -> log("TextEditorInputMode Text Edited");



  IEventListener<PopulateMenuEventArgs> popupMenuInputModeOnPopulateMenu = (source, args) -> log("PopupMenuInputMode Populate Menu");



  IEventListener<BendEventArgs> createBendInputModeOnBendCreated = (source, args) -> log("CreateBendInputMode Bend Created");



  IEventListener<EdgeEventArgs> createEdgeInputModeOnEdgeCreated = (source, args) -> log("CreateEdgeInputMode Edge Created");

  IEventListener<EdgeEventArgs> createEdgeInputModeOnEdgeCreationStarted = (source, args) -> log("CreateEdgeInputMode Edge Creation Started");

  IEventListener<InputModeEventArgs> createEdgeInputModeOnGestureCanceled = (source, args) -> log("CreateEdgeInputMode Gesture Canceled");

  IEventListener<InputModeEventArgs> createEdgeInputModeOnGestureCanceling = (source, args) -> log("CreateEdgeInputMode Gesture Canceling");

  IEventListener<InputModeEventArgs> createEdgeInputModeOnGestureFinished = (source, args) -> log("CreateEdgeInputMode Gesture Finished");

  IEventListener<InputModeEventArgs> createEdgeInputModeOnGestureFinishing = (source, args) -> log("CreateEdgeInputMode Gesture Finishing");

  IEventListener<InputModeEventArgs> createEdgeInputModeOnGestureStarted = (source, args) -> log("CreateEdgeInputMode Gesture Started");

  IEventListener<InputModeEventArgs> createEdgeInputModeOnGestureStarting = (source, args) -> log("CreateEdgeInputMode Gesture Starting");

  IEventListener<InputModeEventArgs> createEdgeInputModeOnMoved = (source, args) -> log("CreateEdgeInputMode Moved");

  IEventListener<InputModeEventArgs> createEdgeInputModeOnMoving = (source, args) -> log("CreateEdgeInputMode Moving");

  IEventListener<ItemEventArgs<IPort>> createEdgeInputModeOnPortAdded = (source, evt) -> log("CreateEdgeInputMode Port Added");

  IEventListener<ItemEventArgs<IPortCandidate>> createEdgeInputModeOnSourcePortCandidateChanged = (source, evt) -> log("CreateEdgeInputMode Source Port Candidate Changed");

  IEventListener<ItemEventArgs<IPortCandidate>> createEdgeInputModeOnTargetPortCandidateChanged = (source, evt) -> log("CreateEdgeInputMode Target Port Candidate Changed");



  IEventListener<HoveredItemChangedEventArgs> itemHoverInputModeOnHoveredItemChanged = (source, args) -> log("HoverInputMode Item changed from " + args.getOldItem() + " to " + (args.getItem() != null ? args.getItem().toString() : "null"), "HoveredItemChanged");



  IEventListener<ItemCopiedEventArgs<IGraph>> clipboardOnGraphCopiedToClipboard = (source, args) -> log("Graph copied to Clipboard");

  IEventListener<ItemCopiedEventArgs<INode>> clipboardOnNodeCopiedToClipboard = (source, args) -> log("Node " + args.getOriginal() + " copied to Clipboard: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<IEdge>> clipboardOnEdgeCopiedToClipboard = (source, args) -> log("Edge " + args.getOriginal() + " copied to Clipboard: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<IPort>> clipboardOnPortCopiedToClipboard = (source, args) -> log("Port " + args.getOriginal() + " copied to Clipboard: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<ILabel>> clipboardOnLabelCopiedToClipboard = (source, args) -> log("Label " + args.getOriginal() + " copied to Clipboard: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<Object>> clipboardOnObjectCopiedToClipboard = (source, args) -> log("Object " + args.getOriginal() + " copied to Clipboard: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<IGraph>> clipboardOnGraphCopiedFromClipboard = (source, args) -> log("Graph copied from Clipboard");

  IEventListener<ItemCopiedEventArgs<INode>> clipboardOnNodeCopiedFromClipboard = (source, args) -> log("Node " + args.getOriginal() + " copied from Clipboard: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<IEdge>> clipboardOnEdgeCopiedFromClipboard = (source, args) -> log("Edge " + args.getOriginal() + " copied from Clipboard: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<IPort>> clipboardOnPortCopiedFromClipboard = (source, args) -> log("Port " + args.getOriginal() + " copied from Clipboard: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<ILabel>> clipboardOnLabelCopiedFromClipboard = (source, args) -> log("Label " + args.getOriginal() + " copied from Clipboard: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<Object>> clipboardOnObjectCopiedFromClipboard = (source, args) -> log("Object " + args.getOriginal() + " copied from Clipboard: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<IGraph>> clipboardOnGraphDuplicated = (source, args) -> log("Graph duplicated.");

  IEventListener<ItemCopiedEventArgs<INode>> clipboardOnNodeDuplicated = (source, args) -> log("Node " + args.getOriginal() + " duplicated to: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<IEdge>> clipboardOnEdgeDuplicated = (source, args) -> log("Edge " + args.getOriginal() + " duplicated to: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<IPort>> clipboardOnPortDuplicated = (source, args) -> log("Port " + args.getOriginal() + " duplicated to: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<ILabel>> clipboardOnLabelDuplicated = (source, args) -> log("Label " + args.getOriginal() + " duplicated to: " + args.getCopy());

  IEventListener<ItemCopiedEventArgs<Object>> clipboardOnObjectDuplicated = (source, args) -> log("Object " + args.getOriginal() + " duplicated to: " + args.getCopy());

  IEventListener<IEventArgs> clipboardOnCut = (source, args) -> log("Clipboard operation: Cut");

  IEventListener<IEventArgs> clipboardOnCopy = (source, args) -> log("Clipboard operation: Copy");

  IEventListener<IEventArgs> clipboardOnPaste = (source, args) -> log("Clipboard operation: Paste");



  IEventListener<IEventArgs> undoEngineOnUnitUndone = (source, args) -> log("Undo performed");

  IEventListener<IEventArgs> undoEngineOnUnitRedone = (source, args) -> log("Redo performed");





  IEventListener<Mouse2DEventArgs> controlOnMouse2DClicked = (source, args) -> log("GraphComponent Mouse2DClicked");

  IEventListener<Mouse2DEventArgs> controlOnMouse2DDragged = (source, args) -> log("GraphComponent Mouse2DDragged");

  IEventListener<Mouse2DEventArgs> controlOnMouse2DEntered = (source, args) -> log("GraphComponent Mouse2DEntered");

  IEventListener<Mouse2DEventArgs> controlOnMouse2DExited = (source, args) -> log("GraphComponent Mouse2DExited");

  IEventListener<Mouse2DEventArgs> controlOnMouse2DMoved = (source, args) -> log("GraphComponent Mouse2DMoved");

  IEventListener<Mouse2DEventArgs> controlOnMouse2DPressed = (source, args) -> log("GraphComponent Mouse2DPressed");

  IEventListener<Mouse2DEventArgs> controlOnMouse2DReleased = (source, args) -> log("GraphComponent Mouse2DReleased");

  IEventListener<Mouse2DEventArgs> controlOnMouse2DWheelTurned = (source, args) -> log("GraphComponent Mouse2DWheelTurned");



  IEventListener<ItemSelectionChangedEventArgs<IModelItem>> onItemSelectionChanged = (source, evt) -> {
    if (evt.isItemSelected()) {
      log("GraphComponent Item Selected: " + evt.getItem(), "ItemSelected");
    } else {
      log("GraphComponent Item Deselected: " + evt.getItem(), "ItemDeselected");
    }
  };



  IEventListener<PropertyChangedEventArgs> controlOnViewportChanged = (source, args) -> log("GraphComponent ViewportChanged");

  IEventListener controlOnZoomChanged = (source, args) -> log("GraphComponent ZoomChanged");



  IEventListener<PrepareRenderContextEventArgs> controlOnPrepareRenderContext = (source, args) -> log("GraphComponent PrepareRenderContext");

  IEventListener controlOnUpdatedVisual = (source, args) -> log("GraphComponent UpdatedVisual");

  IEventListener controlOnUpdatingVisual = (source, args) -> log("GraphComponent UpdatingVisual");



  IEventListener<PropertyChangedEventArgs> controlOnCurrentItemChanged = (source, args) -> log("GraphComponent CurrentItemChanged");

  IEventListener<ItemEventArgs<IGraph>> controlOnGraphChanged = (source, args) -> log("GraphComponent GraphChanged");

  IEventListener controlOnInputModeChanged = (source, args) -> log("GraphComponent InputModeChanged");





  IEventListener<ItemChangedEventArgs<INode, INodeStyle>> onNodeStyleChanged = (source, args) -> log("Node Style Changed: " + args.getItem(), "NodeStyleChanged");

  IEventListener<ItemChangedEventArgs<INode, Object>> onNodeTagChanged = (source, args) -> log("Node Tag Changed: " + args.getItem(), "NodeTagChanged");

  IEventListener<ItemEventArgs<INode>> onNodeCreated = (source, args) -> log("Node Created: " + args.getItem(), "NodeCreated");

  IEventListener<NodeEventArgs> onNodeRemoved = (source, args) -> log("Node Removed: " + args.getItem(), "NodeRemoved");

  IEventListener<NodeEventArgs> onIsGroupNodeChanged = (source, args) -> log("GroupNode Status Changed: " + args.getItem(), "IsGroupNodeChanged");

  IEventListener<NodeEventArgs> onParentChanged = (source, args) -> log("Parent Changed: " + args.getItem(), "ParentChanged");



  IEventListener<ItemChangedEventArgs<IEdge, IEdgeStyle>> onEdgeStyleChanged = (source, args) -> log("Edge Style Changed: " + args.getItem(), "EdgeStyleChanged");

  IEventListener<EdgeEventArgs> onEdgePortsChanged = (source, args) -> log("Edge Ports Changed: " + args.getItem(), "EdgePortsChanged");

  IEventListener<ItemChangedEventArgs<IEdge, Object>> onEdgeTagChanged = (source, args) -> log("Edge Tag Changed: " + args.getItem(), "EdgeTagChanged");

  IEventListener<ItemEventArgs<IEdge>> onEdgeCreated = (source, args) -> log("Edge Created: " + args.getItem(), "EdgeCreated");

  IEventListener<EdgeEventArgs> onEdgeRemoved = (source, args) -> log("Edge Removed: " + args.getItem(), "EdgeRemoved");



  IEventListener<ItemEventArgs<ILabel>> onLabelAdded = (source, args) -> log("Label Added: " + args.getItem(), "LabelAdded");

  IEventListener<ItemChangedEventArgs<ILabel, ILabelModelParameter>> onLabelModelParameterChanged = (source, args) -> log("Label Layout Parameter Changed: " + args.getItem(), "LabelLayoutParameterChanged");

  IEventListener<ItemChangedEventArgs<ILabel, ILabelStyle>> onLabelStyleChanged = (source, args) -> log("Label Style Changed: " + args.getItem(), "LabelStyleChanged");

  IEventListener<ItemChangedEventArgs<ILabel, SizeD>> onLabelPreferredSizeChanged = (source, args) -> log("Label Preferrred Size Changed: " + args.getItem(), "LabelPreferredSizeChanged");

  IEventListener<ItemChangedEventArgs<ILabel, Object>> onLabelTagChanged = (source, args) -> log("Label Tag Changed: " + args.getItem(), "LabelTagChanged");

  IEventListener<ItemChangedEventArgs<ILabel, String>> onLabelTextChanged = (source, args) -> log("Label Text Changed: " + args.getItem(), "LabelTextChanged");

  IEventListener<LabelEventArgs> onLabelRemoved = (source, args) -> log("Label Removed: " + args.getItem(), "LabelRemoved");



  IEventListener<ItemEventArgs<IPort>> onPortAdded = (source, args) -> log("Port Added: " + args.getItem(), "PortAdded");

  IEventListener<ItemChangedEventArgs<IPort, IPortLocationModelParameter>> onPortLocationParameterChanged = (source, args) -> log("Port Location Parameter Changed: " + args.getItem(), "PortLocationParameterChanged");

  IEventListener<ItemChangedEventArgs<IPort, IPortStyle>> onPortStyleChanged = (source, args) -> log("Port Style Changed: " + args.getItem(), "PortStyleChanged");

  IEventListener<ItemChangedEventArgs<IPort, Object>> onPortTagChanged = (source, args) -> log("Port Tag Changed: " + args.getItem(), "PortTagChanged");

  IEventListener<PortEventArgs> onPortRemoved = (source, args) -> log("Port Removed: " + args.getItem(), "PortRemoved");



  IEventListener<ItemEventArgs<IBend>> onBendAdded = (source, args) -> log("Bend Added: " + args.getItem(), "BendAdded");

  IBendLocationChangedHandler onBendLocationChanged = (source, bend, oldLocation) -> log("Bend Location Changed: " + bend + "; " + oldLocation, "BendLocationChanged");

  IEventListener<ItemChangedEventArgs<IBend, Object>> onBendTagChanged = (source, args) -> log("Bend Tag Changed: " + args.getItem(), "BendTagChanged");

  IEventListener<BendEventArgs> onBendRemoved = (source, args) -> log("Bend Removed: " + args.getItem(), "BendRemoved");



  INodeLayoutChangedHandler onNodeLayoutChanged = (source, node, oldLayout) -> log("Node Layout Changed");



  IEventListener<IEventArgs> onDisplaysInvalidated = (source, args) -> log("Displays Invalidated");





  public void toggleInputModeEventListener(ActionEvent e) {
    boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerInputModeEventListener();
    } else {
      deregisterInputModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerInputModeEventListener() {
    editorMode.addCanvasClickedListener(geimOnCanvasClicked);
    editorMode.addDeletedItemListener(geimOnDeletedItem);
    editorMode.addDeletedSelectionListener(geimOnDeletedSelection);
    editorMode.addDeletingSelectionListener(geimOnDeletingSelection);
    editorMode.addItemClickedListener(geimOnItemClicked);
    editorMode.addItemDoubleClickedListener(geimOnItemDoubleClicked);
    editorMode.addItemLeftClickedListener(geimOnItemLeftClicked);
    editorMode.addItemLeftDoubleClickedListener(geimOnItemLeftDoubleClicked);
    editorMode.addItemRightClickedListener(geimOnItemRightClicked);
    editorMode.addItemRightDoubleClickedListener(geimOnItemRightDoubleClicked);
    editorMode.addLabelAddingListener(geimOnLabelAdding);
    editorMode.addLabelAddedListener(geimOnLabelAdded);
    editorMode.addLabelEditingListener(geimOnLabelEditing);
    editorMode.addLabelTextChangedListener(geimOnLabelTextChanged);
    editorMode.addLabelTextEditingCanceledListener(geimOnLabelTextEditingCanceled);
    editorMode.addLabelTextEditingStartedListener(geimOnLabelTextEditingStarted);
    editorMode.addMultiSelectionFinishedListener(geimOnMultiSelectionFinished);
    editorMode.addMultiSelectionStartedListener(geimOnMultiSelectionStarted);
    editorMode.addNodeCreatedListener(geimOnNodeCreated);
    editorMode.addNodeReparentedListener(geimOnNodeReparented);
    editorMode.addEdgePortsChangedListener(geimOnEdgePortsChanged);
    editorMode.addPopulateItemPopupMenuListener(geimOnPopulateItemPopupMenu);
    editorMode.addQueryItemToolTipListener(geimOnQueryItemToolTip);
    editorMode.addValidateLabelTextListener(geimOnValidateLabelText);
    editorMode.addElementsCopiedListener(geimOnElementsCopied);
    editorMode.addElementsCutListener(geimOnElementsCut);
    editorMode.addElementsPastedListener(geimOnElementsPasted);
    viewerMode.addCanvasClickedListener(gvimOnCanvasClicked);
    viewerMode.addItemClickedListener(gvimOnItemClicked);
    viewerMode.addItemDoubleClickedListener(gvimOnItemDoubleClicked);
    viewerMode.addItemLeftClickedListener(gvimOnItemLeftClicked);
    viewerMode.addItemLeftDoubleClickedListener(gvimOnItemLeftDoubleClicked);
    viewerMode.addItemRightClickedListener(gvimOnItemRightClicked);
    viewerMode.addItemRightDoubleClickedListener(gvimOnItemRightDoubleClicked);
    viewerMode.addMultiSelectionFinishedListener(gvimOnMultiSelectionFinished);
    viewerMode.addMultiSelectionStartedListener(gvimOnMultiSelectionStarted);
    viewerMode.addPopulateItemPopupMenuListener(gvimOnPopulateItemPopupMenu);
    viewerMode.addQueryItemToolTipListener(gvimOnQueryItemToolTip);
    viewerMode.addElementsCopiedListener(gvimOnElementsCopied);
  }

  private void deregisterInputModeEventListener() {
    editorMode.removeCanvasClickedListener(geimOnCanvasClicked);
    editorMode.removeDeletedItemListener(geimOnDeletedItem);
    editorMode.removeDeletedSelectionListener(geimOnDeletedSelection);
    editorMode.removeDeletingSelectionListener(geimOnDeletingSelection);
    editorMode.removeItemClickedListener(geimOnItemClicked);
    editorMode.removeItemDoubleClickedListener(geimOnItemDoubleClicked);
    editorMode.removeItemLeftClickedListener(geimOnItemLeftClicked);
    editorMode.removeItemLeftDoubleClickedListener(geimOnItemLeftDoubleClicked);
    editorMode.removeItemRightClickedListener(geimOnItemRightClicked);
    editorMode.removeItemRightDoubleClickedListener(geimOnItemRightDoubleClicked);
    editorMode.removeLabelAddingListener(geimOnLabelAdding);
    editorMode.removeLabelAddedListener(geimOnLabelAdded);
    editorMode.removeLabelEditingListener(geimOnLabelEditing);
    editorMode.removeLabelTextChangedListener(geimOnLabelTextChanged);
    editorMode.removeLabelTextEditingCanceledListener(geimOnLabelTextEditingCanceled);
    editorMode.removeLabelTextEditingStartedListener(geimOnLabelTextEditingStarted);
    editorMode.removeMultiSelectionFinishedListener(geimOnMultiSelectionFinished);
    editorMode.removeMultiSelectionStartedListener(geimOnMultiSelectionStarted);
    editorMode.removeNodeCreatedListener(geimOnNodeCreated);
    editorMode.removeNodeReparentedListener(geimOnNodeReparented);
    editorMode.removeEdgePortsChangedListener(geimOnEdgePortsChanged);
    editorMode.removePopulateItemPopupMenuListener(geimOnPopulateItemPopupMenu);
    editorMode.removeQueryItemToolTipListener(geimOnQueryItemToolTip);
    editorMode.removeValidateLabelTextListener(geimOnValidateLabelText);
    editorMode.removeElementsCopiedListener(geimOnElementsCopied);
    editorMode.removeElementsCutListener(geimOnElementsCut);
    editorMode.removeElementsPastedListener(geimOnElementsPasted);
    viewerMode.removeCanvasClickedListener(gvimOnCanvasClicked);
    viewerMode.removeItemClickedListener(gvimOnItemClicked);
    viewerMode.removeItemDoubleClickedListener(gvimOnItemDoubleClicked);
    viewerMode.removeItemLeftClickedListener(gvimOnItemLeftClicked);
    viewerMode.removeItemLeftDoubleClickedListener(gvimOnItemLeftDoubleClicked);
    viewerMode.removeItemRightClickedListener(gvimOnItemRightClicked);
    viewerMode.removeItemRightDoubleClickedListener(gvimOnItemRightDoubleClicked);
    viewerMode.removeMultiSelectionFinishedListener(gvimOnMultiSelectionFinished);
    viewerMode.removeMultiSelectionStartedListener(gvimOnMultiSelectionStarted);
    viewerMode.removePopulateItemPopupMenuListener(gvimOnPopulateItemPopupMenu);
    viewerMode.removeQueryItemToolTipListener(gvimOnQueryItemToolTip);
    viewerMode.removeElementsCopiedListener(gvimOnElementsCopied);
  }

  public void toggleNavigationModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerNavigationModeEventListener();
    } else {
      deregisterNavigationModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerNavigationModeEventListener() {
    editorMode.getNavigationInputMode().addGroupCollapsedListener(navigationInputModeOnGroupCollapsed);
    editorMode.getNavigationInputMode().addGroupCollapsingListener(navigationInputModeOnGroupCollapsing);
    editorMode.getNavigationInputMode().addGroupEnteredListener(navigationInputModeOnGroupEntered);
    editorMode.getNavigationInputMode().addGroupEnteringListener(navigationInputModeOnGroupEntering);
    editorMode.getNavigationInputMode().addGroupExitedListener(navigationInputModeOnGroupExited);
    editorMode.getNavigationInputMode().addGroupExitingListener(navigationInputModeOnGroupExiting);
    editorMode.getNavigationInputMode().addGroupExpandedListener(navigationInputModeOnGroupExpanded);
    editorMode.getNavigationInputMode().addGroupExpandingListener(navigationInputModeOnGroupExpanding);

    viewerMode.getNavigationInputMode().addGroupCollapsedListener(navigationInputModeOnGroupCollapsed);
    viewerMode.getNavigationInputMode().addGroupCollapsingListener(navigationInputModeOnGroupCollapsing);
    viewerMode.getNavigationInputMode().addGroupEnteredListener(navigationInputModeOnGroupEntered);
    viewerMode.getNavigationInputMode().addGroupEnteringListener(navigationInputModeOnGroupEntering);
    viewerMode.getNavigationInputMode().addGroupExitedListener(navigationInputModeOnGroupExited);
    viewerMode.getNavigationInputMode().addGroupExitingListener(navigationInputModeOnGroupExiting);
    viewerMode.getNavigationInputMode().addGroupExpandedListener(navigationInputModeOnGroupExpanded);
    viewerMode.getNavigationInputMode().addGroupExpandingListener(navigationInputModeOnGroupExpanding);
  }

  private void deregisterNavigationModeEventListener() {
    editorMode.getNavigationInputMode().removeGroupCollapsedListener(navigationInputModeOnGroupCollapsed);
    editorMode.getNavigationInputMode().removeGroupCollapsingListener(navigationInputModeOnGroupCollapsing);
    editorMode.getNavigationInputMode().removeGroupEnteredListener(navigationInputModeOnGroupEntered);
    editorMode.getNavigationInputMode().removeGroupEnteringListener(navigationInputModeOnGroupEntering);
    editorMode.getNavigationInputMode().removeGroupExitedListener(navigationInputModeOnGroupExited);
    editorMode.getNavigationInputMode().removeGroupExitingListener(navigationInputModeOnGroupExiting);
    editorMode.getNavigationInputMode().removeGroupExpandedListener(navigationInputModeOnGroupExpanded);
    editorMode.getNavigationInputMode().removeGroupExpandingListener(navigationInputModeOnGroupExpanding);

    viewerMode.getNavigationInputMode().removeGroupCollapsedListener(navigationInputModeOnGroupCollapsed);
    viewerMode.getNavigationInputMode().removeGroupCollapsingListener(navigationInputModeOnGroupCollapsing);
    viewerMode.getNavigationInputMode().removeGroupEnteredListener(navigationInputModeOnGroupEntered);
    viewerMode.getNavigationInputMode().removeGroupEnteringListener(navigationInputModeOnGroupEntering);
    viewerMode.getNavigationInputMode().removeGroupExitedListener(navigationInputModeOnGroupExited);
    viewerMode.getNavigationInputMode().removeGroupExitingListener(navigationInputModeOnGroupExiting);
    viewerMode.getNavigationInputMode().removeGroupExpandedListener(navigationInputModeOnGroupExpanded);
    viewerMode.getNavigationInputMode().removeGroupExpandingListener(navigationInputModeOnGroupExpanding);
  }

  public void toggleClickModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerClickModeEventListener();
    } else {
      deregisterClickModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerClickModeEventListener() {
    editorMode.getClickInputMode().addClickedListener(clickInputModeOnClicked);
    editorMode.getClickInputMode().addDoubleClickedListener(clickInputModeOnDoubleClicked);
    editorMode.getClickInputMode().addLeftClickedListener(clickInputModeOnLeftClicked);
    editorMode.getClickInputMode().addLeftDoubleClickedListener(clickInputModeOnLeftDoubleClicked);
    editorMode.getClickInputMode().addRightClickedListener(clickInputModeOnRightClicked);
    editorMode.getClickInputMode().addRightDoubleClickedListener(clickInputModeOnRightDoubleClicked);

    viewerMode.getClickInputMode().addClickedListener(clickInputModeOnClicked);
    viewerMode.getClickInputMode().addDoubleClickedListener(clickInputModeOnDoubleClicked);
    viewerMode.getClickInputMode().addLeftClickedListener(clickInputModeOnLeftClicked);
    viewerMode.getClickInputMode().addLeftDoubleClickedListener(clickInputModeOnLeftDoubleClicked);
    viewerMode.getClickInputMode().addRightClickedListener(clickInputModeOnRightClicked);
    viewerMode.getClickInputMode().addRightDoubleClickedListener(clickInputModeOnRightDoubleClicked);
  }

  private void deregisterClickModeEventListener() {
    editorMode.getClickInputMode().removeClickedListener(clickInputModeOnClicked);
    editorMode.getClickInputMode().removeDoubleClickedListener(clickInputModeOnDoubleClicked);
    editorMode.getClickInputMode().removeLeftClickedListener(clickInputModeOnLeftClicked);
    editorMode.getClickInputMode().removeLeftDoubleClickedListener(clickInputModeOnLeftDoubleClicked);
    editorMode.getClickInputMode().removeRightClickedListener(clickInputModeOnRightClicked);
    editorMode.getClickInputMode().removeRightDoubleClickedListener(clickInputModeOnRightDoubleClicked);

    viewerMode.getClickInputMode().removeClickedListener(clickInputModeOnClicked);
    viewerMode.getClickInputMode().removeDoubleClickedListener(clickInputModeOnDoubleClicked);
    viewerMode.getClickInputMode().removeLeftClickedListener(clickInputModeOnLeftClicked);
    viewerMode.getClickInputMode().removeLeftDoubleClickedListener(clickInputModeOnLeftDoubleClicked);
    viewerMode.getClickInputMode().removeRightClickedListener(clickInputModeOnRightClicked);
    viewerMode.getClickInputMode().removeRightDoubleClickedListener(clickInputModeOnRightDoubleClicked);
  }

  public void toggleKeyEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
      if (newValue) {
      registerKeyEventListener();
    } else {
      deregisterKeyEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerKeyEventListener() {
    graphComponent.addKeyListener(canvasComponentKeyListener);
  }

  private void deregisterKeyEventListener() {
    graphComponent.removeKeyListener(canvasComponentKeyListener);
  }

  public void toggleMoveModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerMoveModeEventListener();
    } else {
      deregisterMoveModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerMoveModeEventListener() {
    editorMode.getMoveInputMode().addDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveInputMode().addDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveInputMode().addDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveInputMode().addDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveInputMode().addDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveInputMode().addDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveInputMode().addDraggedListener(inputModeOnDragged);
    editorMode.getMoveInputMode().addDraggingListener(inputModeOnDragging);
  }

  private void deregisterMoveModeEventListener() {
    editorMode.getMoveInputMode().removeDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveInputMode().removeDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveInputMode().removeDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveInputMode().removeDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveInputMode().removeDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveInputMode().removeDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveInputMode().removeDraggedListener(inputModeOnDragged);
    editorMode.getMoveInputMode().removeDraggingListener(inputModeOnDragging);
  }

  public void toggleMoveViewportModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerMoveViewportModeEventListener();
    } else {
      deregisterMoveViewportModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerMoveViewportModeEventListener() {
    editorMode.getMoveViewportInputMode().addDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveViewportInputMode().addDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveViewportInputMode().addDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveViewportInputMode().addDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveViewportInputMode().addDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveViewportInputMode().addDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveViewportInputMode().addDraggedListener(inputModeOnDragged);
    editorMode.getMoveViewportInputMode().addDraggingListener(inputModeOnDragging);

    viewerMode.getMoveViewportInputMode().addDragCanceledListener(inputModeOnDragCanceled);
    viewerMode.getMoveViewportInputMode().addDragCancelingListener(inputModeOnDragCanceling);
    viewerMode.getMoveViewportInputMode().addDragFinishedListener(inputModeOnDragFinished);
    viewerMode.getMoveViewportInputMode().addDragFinishingListener(inputModeOnDragFinishing);
    viewerMode.getMoveViewportInputMode().addDragStartedListener(inputModeOnDragStarted);
    viewerMode.getMoveViewportInputMode().addDragStartingListener(inputModeOnDragStarting);

    viewerMode.getMoveViewportInputMode().addDraggedListener(inputModeOnDragged);
    viewerMode.getMoveViewportInputMode().addDraggingListener(inputModeOnDragging);
  }

  private void deregisterMoveViewportModeEventListener() {
    editorMode.getMoveViewportInputMode().removeDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveViewportInputMode().removeDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveViewportInputMode().removeDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveViewportInputMode().removeDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveViewportInputMode().removeDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveViewportInputMode().removeDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveViewportInputMode().removeDraggedListener(inputModeOnDragged);
    editorMode.getMoveViewportInputMode().removeDraggingListener(inputModeOnDragging);

    viewerMode.getMoveViewportInputMode().removeDragCanceledListener(inputModeOnDragCanceled);
    viewerMode.getMoveViewportInputMode().removeDragCancelingListener(inputModeOnDragCanceling);
    viewerMode.getMoveViewportInputMode().removeDragFinishedListener(inputModeOnDragFinished);
    viewerMode.getMoveViewportInputMode().removeDragFinishingListener(inputModeOnDragFinishing);
    viewerMode.getMoveViewportInputMode().removeDragStartedListener(inputModeOnDragStarted);
    viewerMode.getMoveViewportInputMode().removeDragStartingListener(inputModeOnDragStarting);

    viewerMode.getMoveViewportInputMode().removeDraggedListener(inputModeOnDragged);
    viewerMode.getMoveViewportInputMode().removeDraggingListener(inputModeOnDragging);
  }

  public void toggleHandleModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerHandleModeEventListener();
    } else {
      deregisterHandleModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerHandleModeEventListener() {
    editorMode.getHandleInputMode().addDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getHandleInputMode().addDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getHandleInputMode().addDragFinishedListener(inputModeOnDragFinished);
    editorMode.getHandleInputMode().addDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getHandleInputMode().addDragStartedListener(inputModeOnDragStarted);
    editorMode.getHandleInputMode().addDragStartingListener(inputModeOnDragStarting);

    editorMode.getHandleInputMode().addDraggedListener(inputModeOnDragged);
    editorMode.getHandleInputMode().addDraggingListener(inputModeOnDragging);
  }

  private void deregisterHandleModeEventListener() {
    editorMode.getHandleInputMode().removeDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getHandleInputMode().removeDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getHandleInputMode().removeDragFinishedListener(inputModeOnDragFinished);
    editorMode.getHandleInputMode().removeDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getHandleInputMode().removeDragStartedListener(inputModeOnDragStarted);
    editorMode.getHandleInputMode().removeDragStartingListener(inputModeOnDragStarting);

    editorMode.getHandleInputMode().removeDraggedListener(inputModeOnDragged);
    editorMode.getHandleInputMode().removeDraggingListener(inputModeOnDragging);
  }

  public void toggleMouseHoverModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerMouseHoverModeEventListener();
    } else {
      deregisterMouseHoverModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerMouseHoverModeEventListener() {
    editorMode.getMouseHoverInputMode().addQueryToolTipListener(mouseHoverInputModeOnQueryToolTip);
    viewerMode.getMouseHoverInputMode().addQueryToolTipListener(mouseHoverInputModeOnQueryToolTip);
  }

  private void deregisterMouseHoverModeEventListener() {
    editorMode.getMouseHoverInputMode().removeQueryToolTipListener(mouseHoverInputModeOnQueryToolTip);
    viewerMode.getMouseHoverInputMode().removeQueryToolTipListener(mouseHoverInputModeOnQueryToolTip);
  }

  public void toggleTextEditorModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerTextEditorModeEventListener();
    } else {
      deregisterTextEditorModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerTextEditorModeEventListener() {
    editorMode.getTextEditorInputMode().addEditingCanceledListener(textEditorInputModeOnEditingCanceled);
    editorMode.getTextEditorInputMode().addEditingStartedListener(textEditorInputModeOnEditingStarted);
    editorMode.getTextEditorInputMode().addTextEditedListener(textEditorInputModeOnTextEdited);
  }

  private void deregisterTextEditorModeEventListener() {
    editorMode.getTextEditorInputMode().removeEditingCanceledListener(textEditorInputModeOnEditingCanceled);
    editorMode.getTextEditorInputMode().removeEditingStartedListener(textEditorInputModeOnEditingStarted);
    editorMode.getTextEditorInputMode().removeTextEditedListener(textEditorInputModeOnTextEdited);
  }

  public void togglePopupMenuModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerPopupMenuModeEventListener();
    } else {
      deregisterPopupMenuModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerPopupMenuModeEventListener() {
    editorMode.getPopupMenuInputMode().addPopulateMenuListener(popupMenuInputModeOnPopulateMenu);
    viewerMode.getPopupMenuInputMode().addPopulateMenuListener(popupMenuInputModeOnPopulateMenu);
  }

  private void deregisterPopupMenuModeEventListener() {
    editorMode.getPopupMenuInputMode().removePopulateMenuListener(popupMenuInputModeOnPopulateMenu);
    viewerMode.getPopupMenuInputMode().removePopulateMenuListener(popupMenuInputModeOnPopulateMenu);
  }

  public void toggleCreateBendModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerCreateBendModeEventListener();
    } else {
      deregisterCreateBendModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerCreateBendModeEventListener() {
    editorMode.getCreateBendInputMode().addBendCreatedListener(createBendInputModeOnBendCreated);
    editorMode.getCreateBendInputMode().addDragCanceledListener(inputModeOnDragCanceled);

    editorMode.getCreateBendInputMode().addDraggedListener(inputModeOnDragged);
    editorMode.getCreateBendInputMode().addDraggingListener(inputModeOnDragging);
  }

  private void deregisterCreateBendModeEventListener() {
    editorMode.getCreateBendInputMode().removeBendCreatedListener(createBendInputModeOnBendCreated);
    editorMode.getCreateBendInputMode().removeDragCanceledListener(inputModeOnDragCanceled);

    editorMode.getCreateBendInputMode().removeDraggedListener(inputModeOnDragged);
    editorMode.getCreateBendInputMode().removeDraggingListener(inputModeOnDragging);
  }

  public void toggleCreateEdgeModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerCreateEdgeModeEventListener();
    } else {
      deregisterCreateEdgeModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerCreateEdgeModeEventListener() {
    editorMode.getCreateEdgeInputMode().addEdgeCreatedListener(createEdgeInputModeOnEdgeCreated);
    editorMode.getCreateEdgeInputMode().addEdgeCreationStartedListener(createEdgeInputModeOnEdgeCreationStarted);
    editorMode.getCreateEdgeInputMode().addGestureCanceledListener(createEdgeInputModeOnGestureCanceled);
    editorMode.getCreateEdgeInputMode().addGestureCancelingListener(createEdgeInputModeOnGestureCanceling);
    editorMode.getCreateEdgeInputMode().addGestureFinishedListener(createEdgeInputModeOnGestureFinished);
    editorMode.getCreateEdgeInputMode().addGestureFinishingListener(createEdgeInputModeOnGestureFinishing);
    editorMode.getCreateEdgeInputMode().addGestureStartedListener(createEdgeInputModeOnGestureStarted);
    editorMode.getCreateEdgeInputMode().addGestureStartingListener(createEdgeInputModeOnGestureStarting);
    editorMode.getCreateEdgeInputMode().addMovedListener(createEdgeInputModeOnMoved);
    editorMode.getCreateEdgeInputMode().addMovingListener(createEdgeInputModeOnMoving);
    editorMode.getCreateEdgeInputMode().addPortAddedListener(createEdgeInputModeOnPortAdded);
    editorMode.getCreateEdgeInputMode().addSourcePortCandidateChangedListener(createEdgeInputModeOnSourcePortCandidateChanged);
    editorMode.getCreateEdgeInputMode().addTargetPortCandidateChangedListener(createEdgeInputModeOnTargetPortCandidateChanged);
  }

  private void deregisterCreateEdgeModeEventListener() {
    editorMode.getCreateEdgeInputMode().removeEdgeCreatedListener(createEdgeInputModeOnEdgeCreated);
    editorMode.getCreateEdgeInputMode().removeEdgeCreationStartedListener(createEdgeInputModeOnEdgeCreationStarted);
    editorMode.getCreateEdgeInputMode().removeGestureCanceledListener(createEdgeInputModeOnGestureCanceled);
    editorMode.getCreateEdgeInputMode().removeGestureCancelingListener(createEdgeInputModeOnGestureCanceling);
    editorMode.getCreateEdgeInputMode().removeGestureFinishedListener(createEdgeInputModeOnGestureFinished);
    editorMode.getCreateEdgeInputMode().removeGestureFinishingListener(createEdgeInputModeOnGestureFinishing);
    editorMode.getCreateEdgeInputMode().removeGestureStartedListener(createEdgeInputModeOnGestureStarted);
    editorMode.getCreateEdgeInputMode().removeGestureStartingListener(createEdgeInputModeOnGestureStarting);
    editorMode.getCreateEdgeInputMode().removeMovedListener(createEdgeInputModeOnMoved);
    editorMode.getCreateEdgeInputMode().removeMovingListener(createEdgeInputModeOnMoving);
    editorMode.getCreateEdgeInputMode().removePortAddedListener(createEdgeInputModeOnPortAdded);
    editorMode.getCreateEdgeInputMode().removeSourcePortCandidateChangedListener(createEdgeInputModeOnSourcePortCandidateChanged);
    editorMode.getCreateEdgeInputMode().removeTargetPortCandidateChangedListener(createEdgeInputModeOnTargetPortCandidateChanged);
  }

  public void toggleItemHoverModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerItemHoverModeEventListener();
    } else {
      deregisterItemHoverModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerItemHoverModeEventListener() {
    editorMode.getItemHoverInputMode().addHoveredItemChangedListener(itemHoverInputModeOnHoveredItemChanged);
    viewerMode.getItemHoverInputMode().addHoveredItemChangedListener(itemHoverInputModeOnHoveredItemChanged);
  }

  private void deregisterItemHoverModeEventListener() {
    editorMode.getItemHoverInputMode().removeHoveredItemChangedListener(itemHoverInputModeOnHoveredItemChanged);
    viewerMode.getItemHoverInputMode().removeHoveredItemChangedListener(itemHoverInputModeOnHoveredItemChanged);
  }

  public void toggleMoveLabelModeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerMoveLabelModeEventListener();
    } else {
      deregisterMoveLabelModeEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerMoveLabelModeEventListener() {
    editorMode.getMoveLabelInputMode().addDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveLabelInputMode().addDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveLabelInputMode().addDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveLabelInputMode().addDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveLabelInputMode().addDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveLabelInputMode().addDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveLabelInputMode().addDraggedListener(inputModeOnDragged);
    editorMode.getMoveLabelInputMode().addDraggingListener(inputModeOnDragging);
  }

  private void deregisterMoveLabelModeEventListener() {
    editorMode.getMoveLabelInputMode().removeDragCanceledListener(inputModeOnDragCanceled);
    editorMode.getMoveLabelInputMode().removeDragCancelingListener(inputModeOnDragCanceling);
    editorMode.getMoveLabelInputMode().removeDragFinishedListener(inputModeOnDragFinished);
    editorMode.getMoveLabelInputMode().removeDragFinishingListener(inputModeOnDragFinishing);
    editorMode.getMoveLabelInputMode().removeDragStartedListener(inputModeOnDragStarted);
    editorMode.getMoveLabelInputMode().removeDragStartingListener(inputModeOnDragStarting);

    editorMode.getMoveLabelInputMode().removeDraggedListener(inputModeOnDragged);
    editorMode.getMoveLabelInputMode().removeDraggingListener(inputModeOnDragging);
  }

  public void toggleClipboardEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerClipboardEventListener();
    } else {
      deregisterClipboardEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerClipboardEventListener() {
    GraphClipboard clipboard = graphComponent.getClipboard();
    clipboard.addElementsCutListener(clipboardOnCut);
    clipboard.addElementsCopiedListener(clipboardOnCopy);
    clipboard.addElementsPastedListener(clipboardOnPaste);
  }

  private void deregisterClipboardEventListener() {
    GraphClipboard clipboard = graphComponent.getClipboard();
    clipboard.removeElementsCutListener(clipboardOnCut);
    clipboard.removeElementsCopiedListener(clipboardOnCopy);
    clipboard.removeElementsPastedListener(clipboardOnPaste);
  }

  public void toggleUndoEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerUndoEventListener();
    } else {
      deregisterUndoEventListener();
      logAllInputEvents.setSelected(false);
    }
  }

  private void registerUndoEventListener() {
    UndoEngine undoEngine = graphComponent.getGraph().getUndoEngine();
    undoEngine.addUnitUndoneListener(undoEngineOnUnitUndone);
    undoEngine.addUnitRedoneListener(undoEngineOnUnitRedone);
  }

  private void deregisterUndoEventListener() {
    UndoEngine undoEngine = graphComponent.getGraph().getUndoEngine();
    undoEngine.removeUnitUndoneListener(undoEngineOnUnitUndone);
    undoEngine.removeUnitRedoneListener(undoEngineOnUnitRedone);
  }



  public void toggleClipboardCopierEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerClipboardCopierEventListener();
    } else {
      deregisterClipboardCopierEventListener();
      logAllGraphComponentEvents.setSelected(false);
    }
  }

  private void registerClipboardCopierEventListener() {
    GraphClipboard clipboard = graphComponent.getClipboard();
    GraphCopier toClipboardCopier = clipboard.getToClipboardCopier();
    toClipboardCopier.addGraphCopiedListener(clipboardOnGraphCopiedToClipboard);
    toClipboardCopier.addNodeCopiedListener(clipboardOnNodeCopiedToClipboard);
    toClipboardCopier.addEdgeCopiedListener(clipboardOnEdgeCopiedToClipboard);
    toClipboardCopier.addPortCopiedListener(clipboardOnPortCopiedToClipboard);
    toClipboardCopier.addLabelCopiedListener(clipboardOnLabelCopiedToClipboard);
    toClipboardCopier.addObjectCopiedListener(clipboardOnObjectCopiedToClipboard);

    GraphCopier fromClipboardCopier = clipboard.getFromClipboardCopier();
    fromClipboardCopier.addGraphCopiedListener(clipboardOnGraphCopiedFromClipboard);
    fromClipboardCopier.addNodeCopiedListener(clipboardOnNodeCopiedFromClipboard);
    fromClipboardCopier.addEdgeCopiedListener(clipboardOnEdgeCopiedFromClipboard);
    fromClipboardCopier.addPortCopiedListener(clipboardOnPortCopiedFromClipboard);
    fromClipboardCopier.addLabelCopiedListener(clipboardOnLabelCopiedFromClipboard);
    fromClipboardCopier.addObjectCopiedListener(clipboardOnObjectCopiedFromClipboard);

    GraphCopier duplicateCopier = clipboard.getDuplicateCopier();
    duplicateCopier.addGraphCopiedListener(clipboardOnGraphDuplicated);
    duplicateCopier.addNodeCopiedListener(clipboardOnNodeDuplicated);
    duplicateCopier.addEdgeCopiedListener(clipboardOnEdgeDuplicated);
    duplicateCopier.addPortCopiedListener(clipboardOnPortDuplicated);
    duplicateCopier.addLabelCopiedListener(clipboardOnLabelDuplicated);
    duplicateCopier.addObjectCopiedListener(clipboardOnObjectDuplicated);
  }

  private void deregisterClipboardCopierEventListener() {
    GraphClipboard clipboard = graphComponent.getClipboard();
    GraphCopier toClipboardCopier = clipboard.getToClipboardCopier();
    toClipboardCopier.removeGraphCopiedListener(clipboardOnGraphCopiedToClipboard);
    toClipboardCopier.removeNodeCopiedListener(clipboardOnNodeCopiedToClipboard);
    toClipboardCopier.removeEdgeCopiedListener(clipboardOnEdgeCopiedToClipboard);
    toClipboardCopier.removePortCopiedListener(clipboardOnPortCopiedToClipboard);
    toClipboardCopier.removeLabelCopiedListener(clipboardOnLabelCopiedToClipboard);
    toClipboardCopier.removeObjectCopiedListener(clipboardOnObjectCopiedToClipboard);

    GraphCopier fromClipboardCopier = clipboard.getFromClipboardCopier();
    fromClipboardCopier.removeGraphCopiedListener(clipboardOnGraphCopiedFromClipboard);
    fromClipboardCopier.removeNodeCopiedListener(clipboardOnNodeCopiedFromClipboard);
    fromClipboardCopier.removeEdgeCopiedListener(clipboardOnEdgeCopiedFromClipboard);
    fromClipboardCopier.removePortCopiedListener(clipboardOnPortCopiedFromClipboard);
    fromClipboardCopier.removeLabelCopiedListener(clipboardOnLabelCopiedFromClipboard);
    fromClipboardCopier.removeObjectCopiedListener(clipboardOnObjectCopiedFromClipboard);

    GraphCopier duplicateCopier = clipboard.getDuplicateCopier();
    duplicateCopier.removeGraphCopiedListener(clipboardOnGraphDuplicated);
    duplicateCopier.removeNodeCopiedListener(clipboardOnNodeDuplicated);
    duplicateCopier.removeEdgeCopiedListener(clipboardOnEdgeDuplicated);
    duplicateCopier.removePortCopiedListener(clipboardOnPortDuplicated);
    duplicateCopier.removeLabelCopiedListener(clipboardOnLabelDuplicated);
    duplicateCopier.removeObjectCopiedListener(clipboardOnObjectDuplicated);
  }

  public void toggleGraphComponentMouseEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerGraphComponentMouseEventListener();
    } else {
      deregisterGraphComponentMouseEventListener();
      logAllGraphComponentEvents.setSelected(false);
    }
  }

  private void registerGraphComponentMouseEventListener() {
    graphComponent.addMouse2DClickedListener(controlOnMouse2DClicked);
    graphComponent.addMouse2DEnteredListener(controlOnMouse2DEntered);
    graphComponent.addMouse2DExitedListener(controlOnMouse2DExited);
    graphComponent.addMouse2DPressedListener(controlOnMouse2DPressed);
    graphComponent.addMouse2DReleasedListener(controlOnMouse2DReleased);
    graphComponent.addMouse2DWheelTurnedListener(controlOnMouse2DWheelTurned);
    graphComponent.addMouse2DDraggedListener(controlOnMouse2DDragged);
    graphComponent.addMouse2DMovedListener(controlOnMouse2DMoved);
  }

  private void deregisterGraphComponentMouseEventListener() {
    graphComponent.removeMouse2DClickedListener(controlOnMouse2DClicked);
    graphComponent.removeMouse2DEnteredListener(controlOnMouse2DEntered);
    graphComponent.removeMouse2DExitedListener(controlOnMouse2DExited);
    graphComponent.removeMouse2DPressedListener(controlOnMouse2DPressed);
    graphComponent.removeMouse2DReleasedListener(controlOnMouse2DReleased);
    graphComponent.removeMouse2DWheelTurnedListener(controlOnMouse2DWheelTurned);
    graphComponent.removeMouse2DDraggedListener(controlOnMouse2DDragged);
    graphComponent.removeMouse2DMovedListener(controlOnMouse2DMoved);
  }

  public void toggleSelectionEventListener(ActionEvent e) {
    boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerSelectionEventListener();
    } else {
      deregisterSelectionEventListener();
      logAllGraphComponentEvents.setSelected(false);
    }
  }

  private void registerSelectionEventListener() {
    graphComponent.getSelection().addItemSelectionChangedListener(onItemSelectionChanged);
  }

  private void deregisterSelectionEventListener() {
    graphComponent.getSelection().removeItemSelectionChangedListener(onItemSelectionChanged);
  }

  public void toggleGraphComponentViewportEventListener(ActionEvent e) {
    boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerGraphComponentViewportEventListener();
    } else {
      deregisterGraphComponentViewportEventListener();
      logAllGraphComponentEvents.setSelected(false);
    }
  }

  private void registerGraphComponentViewportEventListener() {
    graphComponent.addViewportChangedListener(controlOnViewportChanged);
    graphComponent.addZoomChangedListener(controlOnZoomChanged);
  }

  private void deregisterGraphComponentViewportEventListener() {
    graphComponent.removeViewportChangedListener(controlOnViewportChanged);
    graphComponent.removeZoomChangedListener(controlOnZoomChanged);
  }

  public void toggleGraphComponentRenderEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerGraphComponentRenderEventListener();
    } else {
      deregisterGraphComponentRenderEventListener();
      logAllGraphComponentEvents.setSelected(false);
    }
  }

  private void registerGraphComponentRenderEventListener() {
    graphComponent.addPrepareRenderContextListener(controlOnPrepareRenderContext);
    graphComponent.addUpdatedListener(controlOnUpdatedVisual);
    graphComponent.addUpdatingListener(controlOnUpdatingVisual);
  }

  private void deregisterGraphComponentRenderEventListener() {
    graphComponent.removePrepareRenderContextListener(controlOnPrepareRenderContext);
    graphComponent.removeUpdatedListener(controlOnUpdatedVisual);
    graphComponent.removeUpdatingListener(controlOnUpdatingVisual);
  }

  public void toggleGraphComponentEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerGraphComponentEventListener();
    } else {
      deregisterGraphComponentEventListener();
      logAllGraphComponentEvents.setSelected(false);
    }
  }

  private void registerGraphComponentEventListener() {
    graphComponent.addCurrentItemChangedListener(controlOnCurrentItemChanged);
    graphComponent.addGraphChangedListener(controlOnGraphChanged);
    graphComponent.addInputModeChangedListener(controlOnInputModeChanged);
  }

  private void deregisterGraphComponentEventListener() {
    graphComponent.removeCurrentItemChangedListener(controlOnCurrentItemChanged);
    graphComponent.removeGraphChangedListener(controlOnGraphChanged);
    graphComponent.removeInputModeChangedListener(controlOnInputModeChanged);
  }



  public void toggleNodeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerNodeEventListener();
    } else {
      deregisterNodeEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerNodeEventListener() {
    graphComponent.getGraph().addNodeStyleChangedListener(onNodeStyleChanged);
    graphComponent.getGraph().addNodeTagChangedListener(onNodeTagChanged);
    graphComponent.getGraph().addNodeCreatedListener(onNodeCreated);
    graphComponent.getGraph().addNodeRemovedListener(onNodeRemoved);
    graphComponent.getGraph().addIsGroupNodeChangedListener(onIsGroupNodeChanged);
    graphComponent.getGraph().addParentChangedListener(onParentChanged);
  }

  private void deregisterNodeEventListener() {
    graphComponent.getGraph().removeNodeStyleChangedListener(onNodeStyleChanged);
    graphComponent.getGraph().removeNodeTagChangedListener(onNodeTagChanged);
    graphComponent.getGraph().removeNodeCreatedListener(onNodeCreated);
    graphComponent.getGraph().removeNodeRemovedListener(onNodeRemoved);
    graphComponent.getGraph().removeIsGroupNodeChangedListener(onIsGroupNodeChanged);
    graphComponent.getGraph().removeParentChangedListener(onParentChanged);
  }

  public void toggleEdgeEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerEdgeEventListener();
    } else {
      deregisterEdgeEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerEdgeEventListener() {
    graphComponent.getGraph().addEdgeStyleChangedListener(onEdgeStyleChanged);
    graphComponent.getGraph().addEdgePortsChangedListener(onEdgePortsChanged);
    graphComponent.getGraph().addEdgeTagChangedListener(onEdgeTagChanged);
    graphComponent.getGraph().addEdgeCreatedListener(onEdgeCreated);
    graphComponent.getGraph().addEdgeRemovedListener(onEdgeRemoved);
  }

  private void deregisterEdgeEventListener() {
    graphComponent.getGraph().removeEdgeStyleChangedListener(onEdgeStyleChanged);
    graphComponent.getGraph().removeEdgePortsChangedListener(onEdgePortsChanged);
    graphComponent.getGraph().removeEdgeTagChangedListener(onEdgeTagChanged);
    graphComponent.getGraph().removeEdgeCreatedListener(onEdgeCreated);
    graphComponent.getGraph().removeEdgeRemovedListener(onEdgeRemoved);
  }

  public void toggleLabelEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerLabelEventListener();
    } else {
      deregisterLabelEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerLabelEventListener() {
    graphComponent.getGraph().addLabelAddedListener(onLabelAdded);
    graphComponent.getGraph().addLabelRemovedListener(onLabelRemoved);
    graphComponent.getGraph().addLabelLayoutParameterChangedListener(onLabelModelParameterChanged);
    graphComponent.getGraph().addLabelStyleChangedListener(onLabelStyleChanged);
    graphComponent.getGraph().addLabelPreferredSizeChangedListener(onLabelPreferredSizeChanged);
    graphComponent.getGraph().addLabelTagChangedListener(onLabelTagChanged);
    graphComponent.getGraph().addLabelTextChangedListener(onLabelTextChanged);
  }

  private void deregisterLabelEventListener() {
    graphComponent.getGraph().removeLabelAddedListener(onLabelAdded);
    graphComponent.getGraph().removeLabelRemovedListener(onLabelRemoved);
    graphComponent.getGraph().removeLabelLayoutParameterChangedListener(onLabelModelParameterChanged);
    graphComponent.getGraph().removeLabelStyleChangedListener(onLabelStyleChanged);
    graphComponent.getGraph().removeLabelPreferredSizeChangedListener(onLabelPreferredSizeChanged);
    graphComponent.getGraph().removeLabelTagChangedListener(onLabelTagChanged);
    graphComponent.getGraph().removeLabelTextChangedListener(onLabelTextChanged);
  }

  public void togglePortEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerPortEventListener();
    } else {
      deregisterPortEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerPortEventListener() {
    graphComponent.getGraph().addPortAddedListener(onPortAdded);
    graphComponent.getGraph().addPortLocationParameterChangedListener(onPortLocationParameterChanged);
    graphComponent.getGraph().addPortStyleChangedListener(onPortStyleChanged);
    graphComponent.getGraph().addPortTagChangedListener(onPortTagChanged);
    graphComponent.getGraph().addPortRemovedListener(onPortRemoved);
  }

  private void deregisterPortEventListener() {
    graphComponent.getGraph().removePortAddedListener(onPortAdded);
    graphComponent.getGraph().removePortLocationParameterChangedListener(onPortLocationParameterChanged);
    graphComponent.getGraph().removePortStyleChangedListener(onPortStyleChanged);
    graphComponent.getGraph().removePortTagChangedListener(onPortTagChanged);
    graphComponent.getGraph().removePortRemovedListener(onPortRemoved);
  }

  public void toggleBendEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerBendEventListener();
    } else {
      deregisterBendEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerBendEventListener() {
    graphComponent.getGraph().addBendAddedListener(onBendAdded);
    graphComponent.getGraph().addBendLocationChangedListener(onBendLocationChanged);
    graphComponent.getGraph().addBendTagChangedListener(onBendTagChanged);
    graphComponent.getGraph().addBendRemovedListener(onBendRemoved);
  }

  private void deregisterBendEventListener() {
    graphComponent.getGraph().removeBendAddedListener(onBendAdded);
    graphComponent.getGraph().removeBendLocationChangedListener(onBendLocationChanged);
    graphComponent.getGraph().removeBendTagChangedListener(onBendTagChanged);
    graphComponent.getGraph().removeBendRemovedListener(onBendRemoved);
  }

  public void toggleNodeBoundsEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerNodeBoundsEventListener();
    } else {
      deregisterNodeBoundsEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerNodeBoundsEventListener() {
    graphComponent.getGraph().addNodeLayoutChangedListener(onNodeLayoutChanged);
  }

  private void deregisterNodeBoundsEventListener() {
    graphComponent.getGraph().removeNodeLayoutChangedListener(onNodeLayoutChanged);
  }

  public void toggleGraphRenderEventListener(ActionEvent e) {
      boolean newValue = ((JToggleButton) e.getSource()).isSelected();
    if (newValue) {
      registerGraphRenderEventListener();
    } else {
      deregisterGraphRenderEventListener();
      logAllGraphEvents.setSelected(false);
    }
  }

  private void registerGraphRenderEventListener() {
    graphComponent.getGraph().addDisplaysInvalidatedListener(onDisplaysInvalidated);
  }

  private void deregisterGraphRenderEventListener() {
    graphComponent.getGraph().removeDisplaysInvalidatedListener(onDisplaysInvalidated);
  }




  public void clearButtonClick() {
    clearLog();
  }

  private JToggleButton createToggleEditing() {
    return new JToggleButton(new AbstractAction("Toggle Editing") {
      public void actionPerformed(ActionEvent e) {
        boolean newValue = ((JToggleButton) e.getSource()).isSelected();
        if (newValue) {
          graphComponent.setInputMode(editorMode);
        } else {
          graphComponent.setInputMode(viewerMode);
        }
      }
    });
  }

  private JToggleButton createToggleOrthogonalEditing() {
    return new JToggleButton(new AbstractAction("Orthogonal Edges") {
      public void actionPerformed(ActionEvent e) {
        boolean newValue = ((JToggleButton) e.getSource()).isSelected();
        editorMode.getOrthogonalEdgeEditingContext().setEnabled(newValue);
      }
    });
  }

  private JToggleButton createToggleLassoSelection() {
    return new JToggleButton(new AbstractAction("Lasso Selection") {
      public void actionPerformed(ActionEvent e) {
        boolean newValue = ((JToggleButton) e.getSource()).isSelected();
        editorMode.getLassoSelectionInputMode().setEnabled(newValue);
        viewerMode.getLassoSelectionInputMode().setEnabled(newValue);
      }
    });
  }

  public void toggleAllInputEventListener(ActionEvent e) {
    boolean selected = ((JToggleButton) e.getSource()).isSelected();
    if (selected != logInputModeEvents.isSelected()) click(logInputModeEvents);
    if (selected != logNavigationModeEvents.isSelected()) click(logNavigationModeEvents);
    if (selected != logClickModeEvents.isSelected()) click(logClickModeEvents);
    if (selected != logMoveModeEvents.isSelected()) click(logMoveModeEvents);
    if (selected != logMoveViewportModeEvents.isSelected()) click(logMoveViewportModeEvents);
    if (selected != logHandleModeEvents.isSelected()) click(logHandleModeEvents);
    if (selected != logMouseHoverModeEvents.isSelected()) click(logMouseHoverModeEvents);
    if (selected != logTextEditorModeEvents.isSelected()) click(logTextEditorModeEvents);
    if (selected != logPopupMenuModeEvents.isSelected()) click(logPopupMenuModeEvents);
    if (selected != logCreateBendModeEvents.isSelected()) click(logCreateBendModeEvents);
    if (selected != logCreateEdgeModeEvents.isSelected()) click(logCreateEdgeModeEvents);
    if (selected != logItemHoverModeEvents.isSelected()) click(logItemHoverModeEvents);
    if (selected != logMoveLabelModeEvents.isSelected()) click(logMoveLabelModeEvents);
    if (selected != logClipboardEvents.isSelected()) click(logClipboardEvents);
    if (selected != logUndoEvents.isSelected()) click(logUndoEvents);
  }

  public void toggleAllGraphComponentEventListener(ActionEvent e) {
    boolean selected = ((JToggleButton) e.getSource()).isSelected();
    if (selected != logClipboardCopierEvents.isSelected()) click(logClipboardCopierEvents);
    if (selected != logMouseEvents.isSelected()) click(logMouseEvents);
    if (selected != logKeyEvents.isSelected()) click(logKeyEvents);
    if (selected != logSelectionEvents.isSelected()) click(logSelectionEvents);
    if (selected != logViewportEvents.isSelected()) click(logViewportEvents);
    if (selected != logRenderEvents.isSelected()) click(logRenderEvents);
    if (selected != logGraphComponentEvents.isSelected()) click(logGraphComponentEvents);
  }

  public void toggleAllGraphEventListener(ActionEvent e) {
    boolean selected = ((JToggleButton) e.getSource()).isSelected();
    if (selected != logNodeEvents.isSelected()) click(logNodeEvents);
    if (selected != logEdgeEvents.isSelected()) click(logEdgeEvents);
    if (selected != logLabelEvents.isSelected()) click(logLabelEvents);
    if (selected != logPortEvents.isSelected()) click(logPortEvents);
    if (selected != logBendEvents.isSelected()) click(logBendEvents);
    if (selected != logNodeBoundsEvents.isSelected()) click(logNodeBoundsEvents);
    if (selected != logGraphRenderEvents.isSelected()) click(logGraphRenderEvents);
  }



  private final DefaultListModel<ILogEntry> entries = new DefaultListModel<>();

  private void log(String message) {
    log(message, null);
  }

  private void log(String message, String type) {
    if (type == null) {
      type = message;
    }
    Message msg = new Message(LocalDateTime.now(), message, type);

    entries.add(0, msg);

    if (groupEvents.isSelected()) {
      mergeEventListener();
    }
  }

  private void mergeEventListener() {
    mergeWithLatestGroup();
    createNewGroup();
  }

  private void mergeWithLatestGroup() {
    MessageGroup latestGroup = null;
    ArrayList<Message> precedingEvents = new ArrayList<>();
    Enumeration<ILogEntry> elements = entries.elements();
    while (elements.hasMoreElements()) {
      ILogEntry element = elements.nextElement();
      if (element instanceof MessageGroup) {
        latestGroup = (MessageGroup) element;
        break;
      } else if (element instanceof Message) {
        precedingEvents.add((Message) element);
      }
    }
    if (latestGroup == null) {
      return;
    }

    int groupCount = latestGroup.getRepeatedMessages().getSize();
    if (precedingEvents.size() < groupCount) {
      return;
    }

    Stream<String> precedingTypes = precedingEvents.stream().map(Message::getType);

    ArrayList<String> repeatedMessages = new ArrayList<>();
    Enumeration<Message> repeatedMessagesElements = latestGroup.getRepeatedMessages().elements();
    while (repeatedMessagesElements.hasMoreElements()) {
      repeatedMessages.add(repeatedMessagesElements.nextElement().getType());
    }
    Stream<String> groupTypes = repeatedMessages.stream();

    // Merge into group
    if (sequenceEquals(groupTypes, precedingTypes)) {
      latestGroup.getRepeatedMessages().clear();
      precedingEvents.forEach(latestGroup.getRepeatedMessages()::addElement);
      latestGroup.setRepeatCount(latestGroup.getRepeatCount()+1);
      for (int i = this.entries.indexOf(latestGroup) - 1; i >= 0; i--) {
        this.entries.remove(i);
      }
    }
  }

  /**
   * Convenience method that compares two streams for equality.
   */
  static boolean sequenceEquals(Stream<?> s1, Stream<?> s2) {
    Iterator<?> iter1 = s1.iterator(), iter2 = s2.iterator();
    while(iter1.hasNext() && iter2.hasNext())
      if (!(iter1.next().equals(iter2.next())))
        return false;
    return !iter1.hasNext() && !iter2.hasNext();
  }

  private void createNewGroup() {

    ArrayList<Message> ungroupedEvents = new ArrayList<>();
    Enumeration<ILogEntry> elements = this.entries.elements();
    while (elements.hasMoreElements()) {
      ILogEntry next = elements.nextElement();
      if (!(next instanceof Message)) {
        break;
      }
      ungroupedEvents.add((Message) next);
    }

    for (int start = ungroupedEvents.size() - 1; start >= 1; start--) {
      for (int length = 1; start - 2 * length + 1 >= 0; length++) {
        Stream<String> types = ungroupedEvents.subList(start - length + 1, start + 1).stream().map(Message::getType);
        List<Message> preceding = ungroupedEvents.subList(start - 2 * length + 1, start - length + 1);
        Stream<String> precedingTypes = preceding.stream().map(Message::getType);
        if (sequenceEquals(types, precedingTypes)) {
          MessageGroup group = new MessageGroup();
          group.setRepeatCount(2);
          preceding.forEach(group.getRepeatedMessages()::addElement);
          this.entries.add(start + 1, group);
          for (int i = start; i >= start - 2 * length + 1; i--) {
            this.entries.remove(i);
          }
          return;
        }
      }
    }
  }

  private void clearLog() {
    entries.clear();
  }



  /**
   * A simply interface for different log entries. There are two: Simple messages, and message groups.
   */
  public interface ILogEntry {}

  public class Message implements ILogEntry {
    public LocalDateTime timeStamp;
    public String text;
    public String type;

    public Message(final LocalDateTime timeStamp, final String text, final String type) {
      this.timeStamp = timeStamp;
      this.text = text;
      this.type = type;
    }

    public LocalDateTime getTimeStamp() {
      return timeStamp;
    }

    public void setTimeStamp(final LocalDateTime timeStamp) {
      this.timeStamp = timeStamp;
    }

    public String getText() {
      return text;
    }

    public void setText(final String text) {
      this.text = text;
    }

    public String getType() {
      return type;
    }

    public void setType(final String type) {
      this.type = type;
    }

    @Override
    public String toString() {
      return timeStamp.format(DateTimeFormatter.ISO_TIME) + " " + text;
    }
  }

  public class MessageGroup implements ILogEntry {
    private final DefaultListModel<Message> repeatedMessages = new DefaultListModel<>();
    private int repeatCount;

    public DefaultListModel<Message> getRepeatedMessages() {
      return repeatedMessages;
    }

    public int getRepeatCount() {
      return repeatCount;
    }

    public void setRepeatCount(final int repeatCount) {
      this.repeatCount = repeatCount;
    }
  }

  /**
   * A cell renderer for the event log that creates a JPanel with a TitledBorder for message groups
   * that contains another JList with the messages of that group.
   * Normal messages are simply displayed as strings.
   */
  private class MessageListCellRenderer extends JLabel implements ListCellRenderer<ILogEntry> {

    public Component getListCellRendererComponent(
      JList<? extends ILogEntry> list,           // the list
      ILogEntry item,            // value to display
      int index,               // cell index
      boolean isSelected,      // is the cell selected
      boolean cellHasFocus)    // does the cell have focus
    {
      if (item instanceof Message) {
        // if its a simple message, display it as a string and remove any previously set graphics. Also set up a tooltip.
        setText(item.toString());
        setToolTipText(getText());
        return this;
      } else if (item instanceof MessageGroup) {
        MessageGroup messageGroup = (MessageGroup) item;
        ListModel<Message> repeatedMessages = messageGroup.getRepeatedMessages();
        if (repeatedMessages.getSize() == 1) {
          setText(repeatedMessages.getElementAt(0) + " ("+messageGroup.getRepeatCount()+")");
          setToolTipText(getText());
          return this;
        } else {
          // If its a  message group containing more messages, we create a TitledPane that is collapsible,
          // display some information in the header and set a ListView containing the messages of that group in it.
          setText(null);
          setToolTipText(null);
          JPanel titledPane = new JPanel();
          titledPane.setLayout(new BorderLayout());
          TitledBorder titledBorder = new TitledBorder(repeatedMessages.getSize() + " Events, repeated " + messageGroup.getRepeatCount() + " times");
          titledBorder.setBorder(new LineBorder(Color.DARK_GRAY));
          titledPane.setBorder(titledBorder);

          JList<Message> messageListView = new JList<>(repeatedMessages);
          titledPane.add(messageListView, BorderLayout.CENTER);
          return titledPane;
        }
      } else {
        setText(null);
        setToolTipText(null);
        return this;
      }
    }
  }


  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new GraphEventsDemo().start();
    });
  }
}
