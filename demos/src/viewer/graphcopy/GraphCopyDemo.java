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
package viewer.graphcopy;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphCopier;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import toolkit.AbstractDemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

/**
 * Shows how to use yFiles' {@link GraphCopier} utility class.
 */
public class GraphCopyDemo extends AbstractDemo {

  // the GraphComponent for the left side.
  public GraphComponent originalGraphComponent;
  // the GraphComponent for the right side.
  public GraphComponent copyGraphComponent;

  // the toolbars for the panels.
  private JToolBar originToolBar;
  private JToolBar copyToolBar;

  // the Button for the copy method.
  private JButton copyButton;


  /**
   * Copies the selected part of the original graph to the copy graph.
   */
  public void copyGraph() {
    IGraph source = originalGraphComponent.getGraph();
    IGraph target = copyGraphComponent.getGraph();
    target.clear();

    IGraphSelection selection = originalGraphComponent.getSelection();

    GraphCopier graphCopier = new GraphCopier();
    graphCopier.copy(source, item -> {
      if (item instanceof INode) {
        // copy selected node
        return selection.isSelected(item);
      } else if (item instanceof IEdge) {
        // copy selected edge when its source and target is also selected
        // because an edge cannot exist without its incident nodes
        return selection.isSelected(item) &&
               selection.isSelected(((IEdge) item).getSourceNode()) &&
               selection.isSelected(((IEdge) item).getTargetNode());
      } else if (item instanceof IPort) {
        return selection.isSelected(((IPort) item).getOwner());
      } else if (item instanceof IBend) {
        return selection.isSelected(((IBend) item).getOwner());
      } else if (item instanceof ILabel) {
        return selection.isSelected(((ILabel) item).getOwner());
      }
      return false;
    }, target);

    // notify all commands that the copy graph has changed
    // this will cause the commands to re-evaluate their enabled states
    ICommand.invalidateRequerySuggested();

    // ensure that all copied elements are visible
    copyGraphComponent.fitGraphBounds();
  }

  /**
   * Initializes the graph and the input modes.
   */
  public void initialize() {
    initializeDefaults();

    // Initializes the graph
    initializeGraph();

    // Enable undoability
    originalGraphComponent.getGraph().setUndoEngineEnabled(true);

    // Initializes the input modes
    createEditorInputMode(originalGraphComponent);
    createViewerInputMode(copyGraphComponent);
  }

  /**
   * Initializes the defaults for the styles.
   */
  private void initializeDefaults() {
    IGraph graph = originalGraphComponent.getGraph();
    // Sets the default style for nodes
    ShinyPlateNodeStyle defaultNodeStyle = new ShinyPlateNodeStyle();
    defaultNodeStyle.setPaint(Colors.DARK_ORANGE);
    graph.getNodeDefaults().setStyle(defaultNodeStyle);
    // Sets the default node size explicitly to 40x40
    graph.getNodeDefaults().setSize(new SizeD(40, 40));

    // Specifies the default style for group nodes.

    // PanelNodeStyle is a style especially suited to group nodes
    // Creates a panel with a light blue background
    PanelNodeStyle panelNodeStyle = new PanelNodeStyle();
    Color groupNodeColor = new Color(214, 229, 248);
    panelNodeStyle.setColor(groupNodeColor);
    // Specifies insets that provide space for a label at the top
    panelNodeStyle.setInsets(new InsetsD(23, 5, 5, 5));
    panelNodeStyle.setLabelInsetsColor(groupNodeColor);
    graph.getGroupNodeDefaults().setStyle(panelNodeStyle);

    // Sets a label style with right-aligned text
    DefaultLabelStyle defaultLabelStyle = new DefaultLabelStyle();
    defaultLabelStyle.setTextAlignment(TextAlignment.RIGHT);
    graph.getGroupNodeDefaults().getLabelDefaults().setStyle(defaultLabelStyle);

    // Places the label at the top inside of the panel.
    // For PanelNodeStyle, InteriorStretchLabelModel is usually the most appropriate label model
    graph.getGroupNodeDefaults().getLabelDefaults().setLayoutParameter(InteriorStretchLabelModel.NORTH);
  }

  /**
   * Creates a sample graph and introduces all important graph elements present in yFiles.
   */
  private void initializeGraph() {
    IGraph graph = originalGraphComponent.getGraph();

    // Creates some nodes with the default node size
    // The location is specified for the center
    INode node1 = graph.createNode(new PointD(110, 20));
    INode node2 = graph.createNode(new PointD(145, 95));
    INode node3 = graph.createNode(new PointD(75, 95));
    INode node4 = graph.createNode(new PointD(30, 175));
    INode node5 = graph.createNode(new PointD(100, 175));

    // Creates some edges between the nodes
    graph.createEdge(node1, node2);
    graph.createEdge(node1, node3);
    IEdge edge3 = graph.createEdge(node3, node4);
    graph.createEdge(node3, node5);
    IEdge edge5 = graph.createEdge(node1, node5);

    // Changes the target position from the edge from node1 to node 5, so that there is no slope anymore
    IPort sp5 = edge5.getSourcePort();
    IPort tp5 = edge5.getTargetPort();
    double sp5x = sp5.getLocation().getX();
    double tp5y = tp5.getLocation().getY();
    // Creates a new FreeNodePortLocationModel
    FreeNodePortLocationModel model = new FreeNodePortLocationModel();
    // Create the new parameter for target port
    IPortLocationModelParameter parameter = model.createParameter(tp5.getOwner(), new PointD(sp5x, tp5y));
    // Set the new port location parameter
    graph.setPortLocationParameter(tp5, parameter);

    // Creates some bends
    IPort sp3 = edge3.getSourcePort();
    IPort tp3 = edge3.getTargetPort();
    double sp3x = sp3.getLocation().getX();
    double tp3x = tp3.getLocation().getX();
    double cy = (sp3.getLocation().getY() + tp3.getLocation().getY()) * 0.5;
    graph.addBend(edge3, new PointD(sp3x, cy));
    graph.addBend(edge3, new PointD(tp3x, cy));


    // Creates a group node
    INode groupNode = graph.createGroupNode();
    // Assigns some child nodes
    graph.setParent(node1, groupNode);
    graph.setParent(node2, groupNode);
    graph.setParent(node3, groupNode);
    // Ensures the group node bounds encompass the bounds of its child nodes
    graph.adjustGroupNodeLayout(groupNode);
    // Creates a label
    graph.addLabel(groupNode, "Group 1");
  }

  /**
   * Creates an input mode for interactive graph editing.
   */
  private void createEditorInputMode( GraphComponent graphComponent ) {
    GraphEditorInputMode mode = new GraphEditorInputMode();

    // Enables grouping operations such as grouping selected nodes moving nodes
    // into group nodes
    mode.setGroupingOperationsAllowed(true);

    // Binds "new" command to its default shortcut CTRL+N on Windows and Linux
    // and COMMAND+N on Mac OS.
    bindNewCommand(mode.getKeyboardInputMode());

    graphComponent.setInputMode(mode);
  }

  /**
   * Creates an input mode that prevents interactive editing except for
   * deleting all graph elements.
   */
  private void createViewerInputMode( GraphComponent graphComponent ) {
    GraphViewerInputMode mode = new GraphViewerInputMode();

    // Binds "new" command to its default shortcut CTRL+N on Windows and Linux
    // and COMMAND+N on Mac OS
    bindNewCommand(mode.getKeyboardInputMode());

    graphComponent.setInputMode(mode);
  }

  /**
   * Adds components to the content pane of the given JRootPane which is
   * typically the JRootPane of the application frame.
   * By default, a graph component, a tool bar, and a help pane are added.
   * In this Demo the contenPane adds an split pane.
   */
  @Override
  protected void configure( JRootPane rootPane ) {
    originalGraphComponent = graphComponent;

    Container contentPane = rootPane.getContentPane();
    // changed for this demo
    contentPane.add(createSplitPane(), BorderLayout.CENTER);

    // creates the HelpPane
    JComponent helpPane = createHelpPane();
    if (helpPane != null) {
      contentPane.add(helpPane, BorderLayout.EAST);
    }
  }

  /**
   * Creates a split pane. The left side is for the originGraphComponent and
   * the right side for the copyGraphComponent. Both sides have a own independent toolbar.
   */
  private JSplitPane createSplitPane() {

    // creates the left and right side of the SplitPane
    JPanel leftPanel = new JPanel(new BorderLayout());
    JPanel rightPanel = new JPanel(new BorderLayout());

    // creates the pane for the original graph
    leftPanel.add(originalGraphComponent, BorderLayout.CENTER);
    originToolBar = new JToolBar();
    configureToolBar(originToolBar);
    leftPanel.add(originToolBar, BorderLayout.NORTH);

    // creates the pane for the copy graph
    rightPanel.add(copyGraphComponent = new GraphComponent(), BorderLayout.CENTER);
    copyToolBar = new JToolBar();
    configureCopyToolBar(copyToolBar);
    rightPanel.add(copyToolBar, BorderLayout.NORTH);

    // set up the split pane
    JSplitPane splitPane = new JSplitPane();
    splitPane.setLeftComponent(leftPanel);
    splitPane.setRightComponent(rightPanel);
    splitPane.setResizeWeight(0.5);
    splitPane.setEnabled(false);

    return splitPane;
  }


  /**
   * Configures the toolbar for the origin graph
   * with according buttons and functionality.
   */
  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    toolBar.add(createCommandButtonAction("New", "new-document-16.png", ICommand.NEW, null, originalGraphComponent));
    toolBar.addSeparator();
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    // adding the GraphCopier-functionality
    toolBar.add(copyButton = createButton("Copy", "copy-16.png", actionEvent -> copyGraph()));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png", ICommand.UNDO, null, originalGraphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png", ICommand.REDO, null, originalGraphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Group selected elements", "group-16.png", ICommand.GROUP_SELECTION, null, originalGraphComponent));
    toolBar.add(createCommandButtonAction("Ungroup selected elements", "ungroup-16.png", ICommand.UNGROUP_SELECTION, null, originalGraphComponent));
    toolBar.setFloatable(false);
  }

  /**
   * Configures the toolbar for the copy graph with according buttons and functionality.
   */
  protected void configureCopyToolBar( JToolBar toolBar ) {
    toolBar.add(createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, copyGraphComponent));
    toolBar.add(createCommandButtonAction("Zoom 1:1", "zoom-original2-16.png", ICommand.ZOOM, 1, copyGraphComponent));
    toolBar.add(createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, copyGraphComponent));
    toolBar.add(createCommandButtonAction("Adjust the view port to show the complete graph", "fit2-16.png", ICommand.FIT_GRAPH_BOUNDS, null, copyGraphComponent));
    toolBar.setFloatable(false);
  }

  /**
   * Assigns an effect to the new command if it is executed in the context
   * of the given keyboard input mode.
   */
  private void bindNewCommand( KeyboardInputMode mode ) {
    mode.addCommandBinding(
            ICommand.NEW,
            ( command, parameter, source ) -> {
              ((GraphComponent) source).getGraph().clear();
              ICommand.invalidateRequerySuggested();
              return true;
            },
            ( command, parameter, source ) ->
                    ((GraphComponent) source).getGraph().getNodes().size() != 0);
  }

  /**
   * Adjusts the views on the first start of the demo.
   */
  public void onVisible() {
    originalGraphComponent.fitGraphBounds();
    copyGraphComponent.fitGraphBounds();
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new GraphCopyDemo().start();
    });
  }
}
