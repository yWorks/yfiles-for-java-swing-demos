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
package complete.simpleeditor;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.circular.CircularLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.orthogonal.OrthogonalLayout;
import com.yworks.yfiles.layout.radial.RadialLayout;
import com.yworks.yfiles.layout.router.OrganicEdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRouter;
import com.yworks.yfiles.layout.router.polyline.EdgeRoutingStyle;
import com.yworks.yfiles.layout.tree.BalloonLayout;
import com.yworks.yfiles.layout.tree.TreeLayout;
import com.yworks.yfiles.layout.tree.TreeReductionStage;
import com.yworks.yfiles.view.CanvasPrintable;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.GraphOverviewComponent;
import com.yworks.yfiles.view.GridInfo;
import com.yworks.yfiles.view.GridVisualCreator;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.PixelImageExporter;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.GraphSnapContext;
import com.yworks.yfiles.view.input.GridConstraintProvider;
import com.yworks.yfiles.view.input.GridSnapTypes;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import com.yworks.yfiles.view.input.LabelSnapContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.WaitInputMode;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;
import toolkit.PrintPreview;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Locale;

/**
 * Host a {@link com.yworks.yfiles.view.GraphComponent} which enables graph editing via the default
 * {@link com.yworks.yfiles.view.input.GraphEditorInputMode} input mode for editing graphs.
 * <p>
 *   Grouped graphs are supported, i.e., selected nodes can be grouped in so-called group nodes using Ctrl/Command-G,
 *   and again be un-grouped using Ctrl/Command-U. To move sets of nodes into and out of group nodes using the mouse, hold down
 *   the SHIFT key while dragging.
 * </p>
 * <p>
 *   Apart from graph editing, various basic features are demonstrated that are already present on GraphComponent
 *   (either as predefined commands or as simple method calls), e.g. load/save/export.
 * </p>
 * <p>
 *   In addition to the GraphComponent itself, a GraphOverviewComponent is used.
 * </p>
 */
public class SimpleEditorDemo extends AbstractDemo {
  private static final Dimension OVERVIEW_SIZE = new Dimension(250, 250);
  private static final int GRID_SIZE = 50;
  private static final GridInfo GRID_INFO = new GridInfo(GRID_SIZE);

  private GridVisualCreator grid;
  private GraphSnapContext snapContext;
  private LabelSnapContext labelSnapContext;

  private WaitInputMode waitInputMode;

  /**
   * Adds a menu bar to the JRootPane of the application frame in addition to the default
   * graph component, toolbar, and help pane.
   */
  protected void configure(JRootPane rootPane) {
    super.configure(rootPane);

    JMenuBar menuBar = new JMenuBar();
    configureMenu(menuBar);
    rootPane.setJMenuBar(menuBar);

    // We want the overview to "float" on the GraphComponent in the upper left corner.
    // This can be achieved by placing it into the glass pane of the component.
    JPanel glassPane = graphComponent.getGlassPane();
    JComponent graphOverview = createGraphOverview();
    graphOverview.setBorder(BorderFactory.createTitledBorder("Overview"));
    glassPane.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
    glassPane.add(graphOverview);
  }

  /**
   * Creates a {@link com.yworks.yfiles.view.GraphOverviewComponent}.
   */
  private JComponent createGraphOverview(){
    GraphOverviewComponent graphOverviewComponent = new GraphOverviewComponent(graphComponent);
    graphOverviewComponent.setMinimumSize(OVERVIEW_SIZE);
    graphOverviewComponent.setPreferredSize(OVERVIEW_SIZE);
    graphOverviewComponent.setMaximumSize(OVERVIEW_SIZE);
    return graphOverviewComponent;
  }

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("New", "new-document-16.png", ICommand.NEW, null, graphComponent));
    toolBar.add(createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, graphComponent));
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE_AS, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Print", "print-16.png", ICommand.PRINT, null, graphComponent));
    toolBar.add(createExportImageAction(true));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png", ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Cut", "cut-16.png", ICommand.CUT, null, graphComponent));
    toolBar.add(createCommandButtonAction("Copy", "copy-16.png", ICommand.COPY, null, graphComponent));
    toolBar.add(createCommandButtonAction("Paste", "paste-16.png", ICommand.PASTE, null, graphComponent));
    toolBar.add(createCommandButtonAction("Delete", "delete3-16.png", ICommand.DELETE, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom 1:1", "zoom-original2-16.png", ICommand.ZOOM, 1, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Fit the graph content", "fit2-16.png", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Group selected elements", "group-16.png", ICommand.GROUP_SELECTION, null, graphComponent));
    toolBar.add(createCommandButtonAction("Ungroup selected elements", "ungroup-16.png", ICommand.UNGROUP_SELECTION, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Bring to front", "z-order-top-16.png", ICommand.TO_FRONT, null, graphComponent));
    toolBar.add(createCommandButtonAction("Bring forward", "z-order-up-16.png", ICommand.RAISE, null, graphComponent));
    toolBar.add(createCommandButtonAction("Send backward", "z-order-down-16.png", ICommand.LOWER, null, graphComponent));
    toolBar.add(createCommandButtonAction("Send to back", "z-order-bottom-16.png", ICommand.TO_BACK, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(new JToggleButton(createToggleSnapAction()));
    toolBar.add(new JToggleButton(createToggleOrthogonalEdgeCreationAction()));
    toolBar.add(new JToggleButton(createToggleGridAction()));
    toolBar.addSeparator();
    toolBar.add(new JToggleButton(createToggleSelectionAction()));
  }

  /**
   * Creates an {@link javax.swing.Action} to export the content as image.
   * @param forToolBar <code>true</code> if the action is used for a tool bar; <code>false</code> if it is used for a
   *                   menu
   * @return an {@link javax.swing.Action} to export the content as image.
   */
  private Action createExportImageAction(boolean forToolBar) {
    AbstractAction action = new AbstractAction() {

      private JFileChooser dialog;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (dialog == null) {
          dialog = new JFileChooser();
          dialog.setDialogType(JFileChooser.SAVE_DIALOG);
          dialog.setAcceptAllFileFilterUsed(false);
          dialog.addChoosableFileFilter(new FormatFilter("PNG Files", "png"));
          dialog.addChoosableFileFilter(new FormatFilter("JPEG Files", "jpg", "jpeg", "jpe"));
          dialog.addChoosableFileFilter(new FormatFilter("Bitmap Files", "bmp"));
          dialog.setDialogTitle("Export Diagram...");
        }

        if (dialog.showSaveDialog(graphComponent) == JFileChooser.APPROVE_OPTION) {
          File fileToSave = dialog.getSelectedFile();
          FileFilter filter = dialog.getFileFilter();
          String format =
                  filter instanceof FormatFilter
                  ? ((FormatFilter) filter).getFormat() :
                  "png";

          RectD contentBounds = getContentBounds(graphComponent);
          PixelImageExporter pixelImageExporter = new PixelImageExporter(contentBounds);
          pixelImageExporter.setBackgroundFill(Color.LIGHT_GRAY);

          try (FileOutputStream stream = new FileOutputStream(fileToSave)) {
            pixelImageExporter.export(graphComponent, stream, format);
          } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        }
      }

      /**
       * Calculates the bounds of all items displayed in the given component.
       */
      RectD getContentBounds( GraphComponent graphComponent ) {
        RectD oldRect = graphComponent.getContentRect();
        graphComponent.updateContentRect(new InsetsD(5, 5, 5, 5));
        RectD newRect = graphComponent.getContentRect();
        graphComponent.setContentRect(oldRect);
        return newRect;
      }
    };
    if (forToolBar) {
      action.putValue(Action.SHORT_DESCRIPTION, "Export to image");
      action.putValue(Action.SMALL_ICON, createIcon("export-image-16.png"));
    } else {
      action.putValue(Action.NAME, "Export");
    }
    return action;
  }

  /**
   * A file filter for well-known image formats.
   */
  private static class FormatFilter extends FileFilter {
    private String description;
    private String[] extensions;

    /**
     * Initializes a new <code>FormatFilter</code> instance for a single
     * image format.
     * @param description a human-readable description of the image format.
     * @param extensions the file name extensions used by the image format.
     * The first given extension should be the canonical filename extension
     * of the image format.
     */
    FormatFilter( String description, String... extensions ) {
      this.description = description;
      this.extensions = extensions;
    }

    /**
     * Returns a description of this filter.
     */
    public String getDescription() {
      return description;
    }

    /**
     * Accepts directories and files whose filename extension matches of the
     * filename extensions of the image format represented by this filter.
     */
    public boolean accept( File f ) {
      return f.isDirectory() || accept(f.getName().toLowerCase());
    }

    /**
     * Accepts the given filename if it ends with one of the filename extensions
     * of the image format represented by this filter.
     */
    boolean accept( String fn ) {
      for (String extension : extensions) {
        if (fn.endsWith("." + extension)) {
          return true;
        }
      }
      return false;
    }

    /**
     * Returns the canonical filename extension for the image format represented
     * by this filter.
     */
    String getFormat() {
      return extensions[0];
    }
  }

  /**
   * Creates an {@link javax.swing.Action} to toggle the grid feature.
   * @return an {@link javax.swing.Action} to toggle the grid feature.
   */
  private Action createToggleGridAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JToggleButton) {
          JToggleButton button = (JToggleButton) e.getSource();
          boolean selected = button.isSelected();
          if (selected) {
            snapContext.setGridSnapType(GridSnapTypes.ALL);
          } else {
            snapContext.setGridSnapType(GridSnapTypes.NONE);
          }
          grid.setVisible(selected);
          // trigger a repaint on the GraphComponent to show the new grid state
          graphComponent.repaint();
        }
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Grid");
    action.putValue(Action.SMALL_ICON, createIcon("grid-16.png"));
    return action;
  }

  /**
   * Creates an {@link javax.swing.Action} to toggle between marquee and lasso selection.
   * @return an {@link javax.swing.Action} to toggle between marquee and lasso selection.
   */
  private Action createToggleSelectionAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JToggleButton && graphComponent.getInputMode() instanceof GraphEditorInputMode) {
          JToggleButton button = (JToggleButton) e.getSource();
          boolean selected = button.isSelected();
          GraphEditorInputMode geim = (GraphEditorInputMode) graphComponent.getInputMode();
          geim.getLassoSelectionInputMode().setEnabled(selected);
        }
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Enable Lasso Selection");
    action.putValue(Action.SMALL_ICON, createIcon("lasso.png"));
    return action;
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
    fileMenu.add(createCommandMenuItemAction("Print", ICommand.PRINT, null, graphComponent));
    fileMenu.add(createCommandMenuItemAction("Print preview...", ICommand.PRINT_PREVIEW, null, graphComponent));
    fileMenu.add(createExportImageAction(false));
    fileMenu.add(createExitAction());
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

    JMenu layoutMenu = new JMenu("Layout");
    layoutMenu.add(createCommandMenuItemAction("Hierarchic", RUN_LAYOUT, new HierarchicLayout(), graphComponent));
    layoutMenu.add(createCommandMenuItemAction("Organic", RUN_LAYOUT, new OrganicLayout(), graphComponent));
    layoutMenu.add(createCommandMenuItemAction("Orthogonal", RUN_LAYOUT, new OrthogonalLayout(), graphComponent));
    layoutMenu.add(createCommandMenuItemAction("Circular", RUN_LAYOUT, new CircularLayout(), graphComponent));
    // TreeLayout will fail with exception for graphs that are not trees.
    // If we prepend a TreeReductionStage, those graphs will be reduced
    // to trees and then those layouts can safely be calculated.
    TreeLayout treeLayout = new TreeLayout();
    treeLayout.prependStage(new TreeReductionStage());
    layoutMenu.add(createCommandMenuItemAction("Tree", RUN_LAYOUT, treeLayout, graphComponent));
    // BalloonLayout will fail with exception for graphs that are not trees.
    // If we prepend a TreeReductionStage, those graphs will be reduced
    // to trees and then those layouts can safely be calculated.
    BalloonLayout balloonLayout = new BalloonLayout();
    balloonLayout.prependStage(new TreeReductionStage());
    layoutMenu.add(createCommandMenuItemAction("Balloon", RUN_LAYOUT, balloonLayout, graphComponent));
    layoutMenu.add(createCommandMenuItemAction("Radial", RUN_LAYOUT, new RadialLayout(), graphComponent));
    layoutMenu.addSeparator();
    layoutMenu.add(createCommandMenuItemAction("Orthogonal Router", RUN_LAYOUT, new EdgeRouter(), graphComponent));
    layoutMenu.add(createCommandMenuItemAction("Organic Router", RUN_LAYOUT, new OrganicEdgeRouter(), graphComponent));
    EdgeRouter polylineRouter = new EdgeRouter();
    polylineRouter.getDefaultEdgeLayoutDescriptor().setRoutingStyle(EdgeRoutingStyle.OCTILINEAR);
    layoutMenu.add(createCommandMenuItemAction("Polyline Router", RUN_LAYOUT, polylineRouter, graphComponent));
    menuBar.add(layoutMenu);
  }

  /**
   * Creates an {@link javax.swing.Action} to exit the demo.
   */
  private Action createExitAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    };
    action.putValue(Action.NAME, "Exit");
    return action;
  }

  /**
   * Initializes the styles, the grid, the snap context, the input bindings and loads a sample graph.
   */
  public void initialize() {
    // initialize the graph
    initializeGraph();

    // Set margins for the Fit Content command to make sure that the graph is placed besides the overview. Note that
    // these margins are also respected by the layout calculation.
    graphComponent.setContentMargins(new InsetsD(10, OVERVIEW_SIZE.getWidth() + 20, 10, 10));

    // initialize the grid for grid snapping
    initializeGrid();

    // initialize the snap context
    initializeSnapContext();

    // initialize the input mode
    initializeInputModes();
  }

  /**
   * Loads and centers a sample graph in the graph component.
   */
  public void onVisible() {
    // loads the example graph
    loadSampleGraph();
  }

  /**
   * Initializes the graph instance and sets default styles.
   */
  public void initializeGraph() {
    //Enable folding
    IFoldingView view = new FoldingManager().createFoldingView();
    IGraph graph = view.getGraph();

    // Enable undoability
    // Get the master graph instance and enable undoability support.
    IGraph masterGraph = view.getManager().getMasterGraph();
    masterGraph.setUndoEngineEnabled(true);

    DemoStyles.initDemoStyles(masterGraph, true);

    graphComponent.setGraph(graph);
  }

  /**
   * Initializes the grid feature.
   */
  public void initializeGrid() {
    grid = new GridVisualCreator(GRID_INFO);
    graphComponent.getBackgroundGroup().addChild(grid, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
    // disable the grid by default
    grid.setVisible(false);
  }

  /**
   * Initializes the snapping feature.
   */
  public void initializeSnapContext() {
    snapContext = new GraphSnapContext();
    snapContext.setEnabled(false);
    // disable grid snapping because grid is disabled by default
    snapContext.setGridSnapType(GridSnapTypes.NONE);
    // add constraint provider for nodes, bends, and ports
    snapContext.setNodeGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));
    snapContext.setBendGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));
    snapContext.setPortGridConstraintProvider(new GridConstraintProvider<>(GRID_INFO));

    // initialize label snapping
    labelSnapContext = new LabelSnapContext();
    labelSnapContext.setEnabled(false);
    // set maximum distance between the current mouse coordinates and the coordinates to which the mouse will snap
    labelSnapContext.setSnapDistance(15);
    // set the amount by which snap lines that are induced by existing edge segments are being extended
    labelSnapContext.setSnapLineExtension(100);
  }

  /**
   * Calls {@link #createEditorMode()}  and registers
   * the result as the {@link com.yworks.yfiles.view.CanvasComponent#getInputMode()}.
   */
  public void initializeInputModes() {
    graphComponent.setFileIOEnabled(true);
    graphComponent.setInputMode(createEditorMode());
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
   * Handler for the {@link ICommand#PRINT_PREVIEW} that brings up the print preview panel.
   */
  private boolean executePrintPreviewCommand(ICommand command, Object parameter, Object source) {
    // create the CanvasPrintable to use for printing and the preview.
    CanvasPrintable canvasPrintable = new CanvasPrintable(graphComponent);
    // create the tab with the print preview.
    PrintPreview printPreview = new PrintPreview(canvasPrintable);
    // create a dialog to show the print preview
    JOptionPane optionPane = new JOptionPane(printPreview.getContentPane(), JOptionPane.PLAIN_MESSAGE);
    JDialog dialog = optionPane.createDialog("Print Preview");
    dialog.setResizable(true);
    dialog.setSize(new Dimension(500, 700));
    dialog.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        printPreview.onZoomChanged();
      }
    });
    dialog.setVisible(true);
    return true;
  }

  /**
   * Handler for the {@link ICommand#PRINT} that initiates the printing process.
   */
  private boolean executePrintCommand(ICommand command, Object parameter, Object source) {
    // create the CanvasPrintable to use for printing.
    CanvasPrintable canvasPrintable = new CanvasPrintable(graphComponent);
    // create a printer job and configure it.
    PrinterJob printerJob = PrinterJob.getPrinterJob();
    PageFormat pageFormat = new PageFormat();

    printerJob.setPageable(new Pageable() {
      @Override
      public int getNumberOfPages() {
        // query the number of pages from the CanvasPrintable with the current set PageFormat
        return canvasPrintable.pageCount(pageFormat);
      }

      @Override
      public PageFormat getPageFormat(final int pageIndex) throws IndexOutOfBoundsException {
        // delegate to the pageFormat property of the print preview
        return pageFormat;
      }

      @Override
      public Printable getPrintable(final int pageIndex) throws IndexOutOfBoundsException {
        return canvasPrintable;
      }
    });

    // finally, initiate the printing.
    if (printerJob.printDialog()) {
      try {
        printerJob.print();
        return true;
      } catch (PrinterException pe) {
        pe.printStackTrace();
      }
    }
    return false;
  }

  /**
   * Helper that determines whether the {@link ICommand#NEW} can be executed.
   */
  private boolean canExecuteNewCommand(ICommand command, Object parameter, Object source) {
    IGraph graph = graphComponent.getGraph();
    // if the graph has nodes in it, it can be cleared.
    return graph != null && !(graph.getNodes().size() == 0) && !waitInputMode.isWaiting();
  }


  /**
   * Handler for the {@link ICommand#NEW}
   */
  private boolean executeNewCommand(ICommand command, Object parameter, Object source) {
    IGraph graph = graphComponent.getGraph();
    graph.clear();
    // Clearing the graph programmatically like this won't necessarily trigger an updating of the can-execute-states of the commands.
    // So we do this manually here
    ICommand.invalidateRequerySuggested();

    return true;
  }

  /**
   * A {@link ICommand} that is used to layout the given graph.
   */
  public static final ICommand RUN_LAYOUT = ICommand.createCommand("Run Layout");

  /**
   * Helper that determines whether the {@link #RUN_LAYOUT} can be executed.
   */
  private boolean canExecuteLayout(ICommand command, Object parameter, Object source) {
    // if a layout algorithm is currently running, no other layout algorithm shall be executable for two reasons:
    // - the result of the current layout run shall be presented before executing a new layout
    // - layout algorithms are not thread safe, so calling applyLayout on a layout algorithm that currently calculates
    //   a layout may result in errors
    if (parameter instanceof ILayoutAlgorithm && !waitInputMode.isWaiting()) {
      // don't allow layouts for empty graphs
      IGraph graph = graphComponent.getGraph();
      return graph != null && !(graph.getNodes().size() == 0);
    } else {
      return false;
    }
  }

  /**
   * Handler for the {@link #RUN_LAYOUT}
   */
  private boolean executeLayout(ICommand command, Object parameter, Object source) {
    if (parameter instanceof ILayoutAlgorithm) {
      ILayoutAlgorithm layout = (ILayoutAlgorithm) parameter;
      graphComponent.morphLayout(layout, Duration.ofMillis(500));
      return true;
    }
    return false;
  }
  /**
   * Creates the default input mode for the GraphComponent,
   * @see GraphEditorInputMode
   * @return a new GraphEditorInputMode instance and configures snapping and orthogonal edge editing
   */
  public IInputMode createEditorMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();
    waitInputMode = mode.getWaitInputMode();
    mode.setSnapContext(snapContext);
    mode.setLabelSnapContext(labelSnapContext);
    OrthogonalEdgeEditingContext orthogonalEdgeEditingContext = new OrthogonalEdgeEditingContext();
    orthogonalEdgeEditingContext.setEnabled(false);
    mode.setOrthogonalEdgeEditingContext(orthogonalEdgeEditingContext);

    // make bend creation more important than moving of selected edges
    // this has the effect that dragging a selected edge (not its bends)
    // will create a new bend instead of moving all bends
    // This is especially nicer in conjunction with orthogonal
    // edge editing because this creates additional bends every time
    // the edge is moved otherwise
    mode.getCreateBendInputMode().setPriority(mode.getMoveInputMode().getPriority() - 1);

    // enable grouping operations such as grouping selected nodes moving nodes
    // into group nodes
    mode.setGroupingOperationsAllowed(true);


    // initialize additional input bindings
    KeyboardInputMode kim = mode.getKeyboardInputMode();
    kim.addCommandBinding(RUN_LAYOUT, this::executeLayout, this::canExecuteLayout);
    kim.addCommandBinding(ICommand.NEW, this::executeNewCommand, this::canExecuteNewCommand);
    kim.addCommandBinding(ICommand.PRINT, this::executePrintCommand, (command, parameter, source) -> true); // the print commands are always executable
    kim.addCommandBinding(ICommand.PRINT_PREVIEW, this::executePrintPreviewCommand, (command, parameter, source) -> true);

    // The following line triggers a call to the can-execute-method of each registered action/binding. This is normally
    // done automatically by yFiles via input modes and on specific structural changes. But we want to have the above
    // added actions to be initially in the correct can-execute-state, so we trigger this method manually.
    ICommand.invalidateRequerySuggested();

    return mode;
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new SimpleEditorDemo().start();
    });
  }
}
