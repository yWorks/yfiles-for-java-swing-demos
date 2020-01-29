/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package layout.partitiongrid;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.LayoutData;
import com.yworks.yfiles.layout.LayoutExecutor;
import com.yworks.yfiles.layout.PartitionGridData;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.IAnimation;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import com.yworks.yfiles.view.input.PopulateItemPopupMenuEventArgs;
import com.yworks.yfiles.view.input.WaitInputMode;
import layout.LayoutFinishedListeners;
import toolkit.AbstractDemo;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The PartitionGrid application shows how a {@link com.yworks.yfiles.layout.PartitionGrid} can be used in layout
 * calculations to restrict the node positions to grid cells.
 * <p>
 * The assignment of a node to a grid column and row is visualized by the background and border color of its style
 * and can be changed via the context menu of the node.
 * </p>
 * <p>
 * A background visual using the node colors shows the PartitionGrid bounds calculated by the last layout.
 * </p>
 * <p>
 * The layout itself is triggered via {@link ICommand}s that finally call the method
 * {@link #executeLayout(ICommand, Object, Object)}  executeLayout}. The configuration of the
 * {@link com.yworks.yfiles.layout.PartitionGrid} is delegated to the class {@link PartitionGridConfigurator}.
 * </p>
 */
public class PartitionGridDemo extends AbstractDemo {

  /**
   * A {@link ICommand} that is usable from FXML to layout the given graph hierarchically.
   * <p>
   * The command can be triggered with the keyboard short cut Ctrl/ICommand+H
   * </p>
   */
  public static final ICommand RUN_HIERARCHIC_LAYOUT = ICommand.createCommand("RunHierarchicLayout");

  /**
   * A {@link ICommand} that is usable from FXML to layout the given graph organically.
   * <p>
   * The command can be triggered with the keyboard short cut Ctrl/ICommand+O
   * </p>
   */
  public static final ICommand RUN_ORGANIC_LAYOUT = ICommand.createCommand("RunOrganicLayout");

  public JCheckBox fixOrderBox;
  public JSlider minWidthSlider;
  public JCheckBox stretchGroupBox;

  private WaitInputMode waitInputMode;
  private Color defaultColor = new Color(69, 69, 69);
  private Pen defaultPen = new Pen(defaultColor, 1);
  private List<Color> columnColors;
  private List<Pen> rowPens;

  private PartitionGridVisualCreator partitionGridVisualCreator;


  /**
   * Configures the user interface.
   */
  @Override
  protected void configure(JRootPane rootPane) {
    Container contentPane = rootPane.getContentPane();
    contentPane.add(this.graphComponent, BorderLayout.CENTER);

    JToolBar toolBar = createToolBar();
    if (toolBar != null) {
      //add basic functionality provided by AbstractDemo
      configureToolBar(toolBar);

      toolBar.addSeparator();

      //adding group and ungroup buttons
      toolBar.add(createCommandButtonAction("Group selected elements", "group-16.png",
              ICommand.GROUP_SELECTION, null, graphComponent));
      toolBar.add(createCommandButtonAction("Ungroup selected elements", "ungroup-16.png",
              ICommand.UNGROUP_SELECTION, null, graphComponent));

      toolBar.addSeparator();

      //add Hierarchic and Organic Layoutbuttons
      toolBar.add(createCommandButtonAction("Run hierarchic layout", "layout-hierarchic.png",
              RUN_ORGANIC_LAYOUT, new HierarchicLayout(), graphComponent));

      toolBar.add(createCommandButtonAction("Run organic layout", "layout-organic-16.png",
              RUN_ORGANIC_LAYOUT, new OrganicLayout(), graphComponent));


      contentPane.add(toolBar, BorderLayout.NORTH);
    }

    //create new borderLayout for the East sidebar
    JPanel eastSideBar = new JPanel(new BorderLayout());
    contentPane.add(eastSideBar, BorderLayout.EAST);

    //Add helpPane to center so that it takes whole leftover space
    JComponent helpPane = createHelpPane();
    if (helpPane != null) {
      eastSideBar.add(helpPane, BorderLayout.CENTER);
    }

    //Configure controls pane
    JPanel controls = new JPanel(new GridLayout(3, 2));
    controls.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    eastSideBar.add(controls, BorderLayout.NORTH);


    //Add elements to choose if the column order ist fixed
    JPanel fixColumnOrderLabel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 5));
    fixColumnOrderLabel.add(new JLabel("Use fix column order: "));

    //add labels to show colors
    fixColumnOrderLabel.add(new JLabel(new ColorIcon(new Color(255, 175, 0), Pen.getTransparent(), 15, 15)));
    fixColumnOrderLabel.add(new JLabel(new ColorIcon(new Color(255, 136, 0), Pen.getTransparent(), 15, 15)));
    fixColumnOrderLabel.add(new JLabel(new ColorIcon(new Color(91,175,225), Pen.getTransparent(), 15, 15)));
    fixColumnOrderLabel.add(new JLabel(new ColorIcon(new Color(35, 108,182), Pen.getTransparent(), 15, 15)));

    controls.add(fixColumnOrderLabel);
    controls.add(fixOrderBox = new JCheckBox());

    //add elements for minimum column width
    controls.add(new JLabel("Minimum column width: "));

    JPanel minColumnWidthSlider = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 5));

    minColumnWidthSlider.add(minWidthSlider = new JSlider());

    minWidthSlider.setMinimum(0);
    minWidthSlider.setMaximum(300);

    JLabel sliderValue = new JLabel(Integer.toString(minWidthSlider.getValue()));

    //set preferred size a little bit bigger, so it can show 3 digits without needing to change the layout
    Dimension d = sliderValue.getPreferredSize();
    d.setSize(d.width + 10, d.height);
    sliderValue.setPreferredSize(d);

    minWidthSlider.addChangeListener(e -> sliderValue.setText(Integer.toString(minWidthSlider.getValue())));
    minColumnWidthSlider.add(sliderValue);

    //add everything for the min column width slider
    controls.add(minColumnWidthSlider);

    controls.add(new JLabel("Stretch group nodes: "));
    controls.add(stretchGroupBox = new JCheckBox());
  }


  /**
   * Handles the {@link #RUN_HIERARCHIC_LAYOUT} and {@link #RUN_ORGANIC_LAYOUT} commands.
   * <p>
   * The layout calculation is triggered by a {@link LayoutExecutor} and {@link PartitionGridData} are used as
   * {@link LayoutExecutor#setLayoutData(LayoutData) LayoutData} as well for the hierarchic as for the organic layout.
   * It would also be possible to use the {@link HierarchicLayoutData HierarchicLayoutData}
   * or {@link com.yworks.yfiles.layout.organic.OrganicLayoutData OrganicLayoutData} and configure their
   * {@link HierarchicLayoutData#getPartitionGridData() PartitionGridData properties} instead.
   * </p>
   */
  private boolean executeLayout(ICommand command, Object parameter, Object sender) {
    //check if actually a layout was handed over
    if (parameter instanceof ILayoutAlgorithm) {
      ILayoutAlgorithm layout = (ILayoutAlgorithm) parameter;

      // create the PartitionGridData
      PartitionGridConfigurator configurator = new PartitionGridConfigurator(graphComponent.getGraph(), this::getNodeGridData);
      PartitionGridData partitionGridData = configurator.createPartitionGridData(rowPens.size(), columnColors.size(),
          fixOrderBox.isSelected(), minWidthSlider.getValue(), stretchGroupBox.isSelected());

      // set the PartitionGrid on the partitionGridVisualCreator so it can use the new layout of the rows/columns
      // for its animation
      partitionGridVisualCreator.setGrid(partitionGridData.getGrid());
      // now layout the graph using the provided layout algorithm and animate the result
      LayoutExecutor executor = new LayoutExecutor(graphComponent, layout) {
        @Override
        protected IAnimation createMorphAnimation() {
          IAnimation graphMorphAnimation = super.createMorphAnimation();
          // we want to animate the graph itself as well as the partition
          // grid visualization so we use a parallel animation:
          return IAnimation.createParallelAnimation(graphMorphAnimation, partitionGridVisualCreator);
        }
      };
      executor.setDuration(Duration.ofMillis(500));
      executor.setLayoutData(partitionGridData);
      executor.setViewportAnimationEnabled(true);
      executor.addLayoutFinishedListener(LayoutFinishedListeners::handleErrors);
      executor.start();
      return true;
    }
    return false;
  }

  /**
   * Determines whether the {@link #RUN_HIERARCHIC_LAYOUT} can be executed.
   */
  private boolean canExecuteHierarchicLayout(ICommand command, Object parameter, Object sender) {
    return canExecuteAnyLayout(parameter);
  }

  /**
   * Determines whether the {@link #RUN_ORGANIC_LAYOUT} can be executed.
   */
  private boolean canExecuteOrganicLayout(ICommand command, Object parameter, Object sender) {
    if (!canExecuteAnyLayout(parameter)) {
      return false;
    }

    // the <em>Organic</em> layout doesn't support to stretch a group node if it contains child nodes assigned
    // to different rows or columns. In this case the <em>Organic</em> layout button shall be disabled.
    IGraph graph = graphComponent.getGraph();
    for (INode node : graph.getNodes()) {
      if (graph.isGroupNode(node)) {
        // for each group node...
        int rowIndex = -1;
        int columnIndex = -1;
        boolean firstValidIndices = true;
        for (INode child : graph.getChildren(node)) {
          // ... check the NodeGridData of its children...
          NodeGridData nodeGridData = getNodeGridData(child);
          if (!nodeGridData.hasValidIndices()) {
            continue;
          }
          // ... and if one has a valid index, check if it has a different row/column index then the other nodes
          if (firstValidIndices) {
            rowIndex = nodeGridData.getRowIndex();
            columnIndex = nodeGridData.getColumnIndex();
            firstValidIndices = false;
          } else {
            if (rowIndex != nodeGridData.getRowIndex() || columnIndex != nodeGridData.getColumnIndex()) {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  /**
   * Determines whether any layout can be executed.
   */
  private boolean canExecuteAnyLayout(final Object layoutParameter) {
    // if a layout algorithm is currently running, no other layout algorithm shall be executable for two reasons:
    // - the result of the current layout run shall be presented before executing a new layout
    // - layout algorithms are not thread safe, so calling applyLayout on a layout algorithm that currently calculates
    //   a layout may result in errors
    if (layoutParameter instanceof ILayoutAlgorithm && !waitInputMode.isWaiting()) {
      // don't allow layouts for empty graphs
      IGraph graph = graphComponent.getGraph();
      return graph.getNodes().size() != 0;
    } else {
      return false;
    }
  }

  /**
   * Initializes the demo and load a sample graph.
   */
  public void onVisible() {
    // initializes the colors and pens defining the columns and rows a node shall be assigned to.
    initializeColors();

    // initializes the visual creator for the partition grid and adds it to the background of the GraphComponent.
    initializePartitionGridVisualization();

    // initialize the default styles for normal nodes and group nodes
    initializeNodeDefaults();

    // creates the default input mode for the GraphComponent and registers it as the {@link CanvasControl#getInputMode()}.
    initializeInputModes();

    // loads a sample graph from GraphML for this demo.
    loadSampleGraph();
  }

  /**
   * Initializes the colors and pens defining the columns and rows a node shall be assigned to.
   */
  private void initializeColors() {
    columnColors = new ArrayList<>(4);
    columnColors.add(Color.decode("#ffaf00"));
    columnColors.add(Color.decode("#ff8800"));
    columnColors.add(Color.decode("#5bafe1"));
    columnColors.add(Color.decode("#236CB6"));

    rowPens = new ArrayList<>(3);
    rowPens.add(new Pen(Colors.DARK_GREEN, 2));
    rowPens.add(new Pen(Colors.WHITE, 1));
    rowPens.add(new Pen(Colors.DARK_RED, 2));
  }

  /**
   * Initializes the visual creator for the partition grid and adds it to the background of the GraphComponent.
   */
  private void initializePartitionGridVisualization() {
    partitionGridVisualCreator = new PartitionGridVisualCreator(columnColors, rowPens);
    graphComponent.getBackgroundGroup().addChild(partitionGridVisualCreator, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
  }

  /**
   * Initializes the default styles for normal nodes and group nodes.
   */
  private void initializeNodeDefaults() {
    IGraph graph = graphComponent.getGraph();

    ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
    nodeStyle.setPaint(defaultColor);
    nodeStyle.setPen(defaultPen);
    graph.getNodeDefaults().setStyle(nodeStyle);
    graph.getNodeDefaults().setStyleInstanceSharingEnabled(false);

    PanelNodeStyle groupNodeStyle = new PanelNodeStyle();
    groupNodeStyle.setColor(new Color(255, 255, 255, 102));
    graph.getGroupNodeDefaults().setStyle(groupNodeStyle);
    graph.getGroupNodeDefaults().setStyleInstanceSharingEnabled(false);
  }

  /**
   * Creates the default input mode for the GraphComponent and registers it as the {@link CanvasComponent#getInputMode()}.
   */
  private void initializeInputModes() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    waitInputMode = geim.getWaitInputMode();

    // enable grouping operations such as grouping selected nodes moving nodes into group nodes
    geim.setGroupingOperationsAllowed(true);

    // add our popup menu creator
    geim.addPopulateItemPopupMenuListener(this::createPopupMenus);
    graphComponent.setInputMode(geim);

    KeyboardInputMode kim = geim.getKeyboardInputMode();

    // add command bindings for the layout commands so the corresponding (can)executeLayout methods are used
    kim.addCommandBinding(RUN_HIERARCHIC_LAYOUT, this::executeLayout, this::canExecuteHierarchicLayout);
    kim.addCommandBinding(RUN_ORGANIC_LAYOUT, this::executeLayout, this::canExecuteOrganicLayout);

    // add key bindings for the layout commands so valid parameters are used when triggering the commands via keyboard short cuts
    OrganicLayout organicLayout = new OrganicLayout();
    organicLayout.setMinimumNodeDistance(50);
    organicLayout.setPreferredEdgeLength(100);
    kim.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK), RUN_HIERARCHIC_LAYOUT, new HierarchicLayout());
    kim.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), RUN_ORGANIC_LAYOUT, organicLayout);

    // The following line triggers a call to the can-execute-method of each registered action/binding. This is normally
    // done automatically by yFiles via input modes and on specific structural changes. But we want to have the above
    // added actions to be initially in the correct can-execute-state, so we trigger this method manually.
    ICommand.invalidateRequerySuggested();
  }

  /**
   * Fills the popup menu for nodes.
   */
  private void createPopupMenus(Object source, PopulateItemPopupMenuEventArgs<IModelItem> args) {
    //clicked item already handled or not an INode -> return
    if (args.isHandled() || !(args.getItem() instanceof INode)) {
      return;
    }

    //get clicked item
    INode node = (INode) args.getItem();

    if (!(node.getStyle() instanceof ShapeNodeStyle)) {
      // for group nodes we don't provide a context menu
      return;
    }

    JPopupMenu popupMenu = (JPopupMenu) args.getMenu();

    //get the color of the clicked node
    ShapeNodeStyle nodeStyle = (ShapeNodeStyle) node.getStyle();
    Color background = (Color) nodeStyle.getPaint();

    //the node is unassigned if the color equals the default color (black)
    boolean isUnassignedNode = defaultColor.equals(background);

    if (!isUnassignedNode) {
      // this node currently has grid restrictions so we add an entry to switch to a 'black' node with no grid restrictions
      JCheckBoxMenuItem colorOption = new JCheckBoxMenuItem("Remove grid restrictions",
              new ColorIcon(defaultColor, Pen.getTransparent(), 20, 20));

      //add actionListener to execute desired function
      colorOption.addActionListener(e -> {
        nodeStyle.setPaint(defaultColor);
        nodeStyle.setPen(defaultPen);
        updateNodeTag(node);
        graphComponent.invalidate();
        ICommand.invalidateRequerySuggested();
      });
      popupMenu.add(colorOption);
      popupMenu.addSeparator();
    }

    ButtonGroup columnColorGroup = new ButtonGroup();
    for (Color newColor : columnColors){
      // we add an entry for each valid column color
      ColorIcon colorRect = new ColorIcon(newColor, Pen.getTransparent(), 20, 20);
      JCheckBoxMenuItem colorOption = new JCheckBoxMenuItem("Switch column", colorRect);

      //set this menuItem as selected if its color equals the clicked color
      colorOption.setSelected(newColor.equals(background));
      columnColorGroup.add(colorOption);

      //add actionListener to execute desired function
      colorOption.addActionListener(e -> {
        nodeStyle.setPaint(newColor);
        updateNodeTag(node);
        graphComponent.invalidate();
        ICommand.invalidateRequerySuggested();
      });
      popupMenu.add(colorOption);
    }

    if (!isUnassignedNode) {
      // this node currently has column restrictions so we add an entry for each valid row pen
      popupMenu.addSeparator();
      Pen border = nodeStyle.getPen();
      ButtonGroup rowPenGroup = new ButtonGroup();

      for (Pen newPen : rowPens) {
        //create icon with transparent filling and a border according to newPen
        ColorIcon penRect = new ColorIcon(Colors.TRANSPARENT, newPen, 20, 20);
        JCheckBoxMenuItem penOption = new JCheckBoxMenuItem("Switch row", penRect);

        //set this menuItem as selected if its color equals the clicked color
        penOption.setSelected(newPen.getPaint().equals(border.getPaint()));
        rowPenGroup.add(penOption);

        //add actionListener to execute desired function
        penOption.addActionListener(e -> {
          nodeStyle.setPen(newPen);
          updateNodeTag(node);
          graphComponent.invalidate();
          ICommand.invalidateRequerySuggested();
        });
        popupMenu.add(penOption);
      }
    }
  }

  /**
   * Loads a sample graph from GraphML for this demo.
   */
  private void loadSampleGraph() {
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/example.graphml"));
      RUN_HIERARCHIC_LAYOUT.execute(new HierarchicLayout(), graphComponent);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // region Creating and updating NodeGridData

  public NodeGridData getNodeGridData(INode node) {
    if (!(node.getTag() instanceof NodeGridData)) {
      updateNodeTag(node);
    }
    return (NodeGridData) node.getTag();
  }

  private void updateNodeTag(INode node) {
    // calculate the new row and column index
    int newColumnIndex = -1;
    int newRowIndex = -1;
    if (node.getStyle() instanceof ShapeNodeStyle) {
      ShapeNodeStyle style = (ShapeNodeStyle) node.getStyle();
      newColumnIndex = getColumnIndex((Color) style.getPaint());
      newRowIndex = getRowIndex(style.getPen());
    }
    // update or create the NodeGridData in the node tag
    if (node.getTag() instanceof NodeGridData) {
      NodeGridData nodeGridData = (NodeGridData) node.getTag();
      nodeGridData.setColumnIndex(newColumnIndex);
      nodeGridData.setRowIndex(newRowIndex);
    } else {
      node.setTag(new NodeGridData(newRowIndex, newColumnIndex));
    }
  }

  private int getColumnIndex(Color color) {
    int index = -1;
    for (int i = 0; i < columnColors.size(); i++) {
      if (columnColors.get(i).equals(color)) {
        return i;
      }
    }
    return index;
  }

  private int getRowIndex(Pen pen) {
    int index = 1;
    for (int i = 0; i < rowPens.size(); i++) {
      if (rowPens.get(i).getPaint().equals(pen.getPaint())) {
        return i;
      }
    }
    return index;
  }

  // endregion

  /**
   * Using a JPopupMenu with JCheckBoxItems in Windows which use a
   * text and a icon, the check mark is behind the icon and cant
   * be seen and its hard to determine which option is chosen.
   * Therefore the LnF is disabled.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      new PartitionGridDemo().start();
    });
  }

  /**
   * Class for creating icons in the control panel and
   * creating the icons for the JPopupMenu as well.
   */
  private static class ColorIcon implements Icon {

    private final Color fillColor;
    private Pen pen;
    private final int width;
    private final int height;

    /**
     * Creates the icon Object
     * @param fillColor Color which is used for the filling
     * @param pen Determines how the border is drawn
     */
    ColorIcon(Color fillColor, Pen pen, int width, int height) {
      this.fillColor = fillColor;
      this.pen = pen;
      this.width = width;
      this.height = height;
    }

    /**
     * actually paints the icon
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D gfx = (Graphics2D) g.create();
      gfx.setColor(fillColor);
      gfx.fillRect(x, y, getIconWidth(), getIconHeight());
      pen.commit(gfx);
      gfx.drawRect(x, y, getIconWidth(), getIconHeight());
      gfx.dispose();
    }

    @Override
    public int getIconWidth() {
      return this.width;
    }

    @Override
    public int getIconHeight() {
      return this.height;
    }
  }
}
