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
package viewer.imageexport;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.MutableRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.utils.ObservableCollection;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.ContextConfigurator;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.RectangleIndicatorInstaller;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.HandleInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.MoveInputMode;
import com.yworks.yfiles.view.input.RectangleReshapeHandleProvider;
import toolkit.AbstractDemo;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Arrays;

/**
 * Abstract base class for yFiles for Java (Swing) image export demos. It uses a tabbed pane to support switching between the
 * {@link com.yworks.yfiles.view.GraphComponent} that contains the graph to export and a preview of the export result.
 */
public abstract class AbstractImageExportDemo extends AbstractDemo {

  // tabbed pane with a GraphComponent on the first tab
  protected JTabbedPane tabbedPane;
  // rectangle that indicates the region that gets exported
  protected MutableRectangle exportRect;
  // dialog to select a file where to save the image
  private JFileChooser chooser;

  // output options
  private boolean showDecorations = true;
  private boolean useRectangle = true;

  // bounds options
  private enum SizeMode { USE_ORIGINAL_SIZE, SPECIFY_WIDTH, SPECIFY_HEIGHT }
  private SizeMode sizeMode = SizeMode.USE_ORIGINAL_SIZE;
  private int width = 500;
  private int height = 500;
  private double scale = 1;

  // margin options
  private int leftMargin = 0;
  private int topMargin = 0;
  private int rightMargin = 0;
  private int bottomMargin = 0;

  /**
   * Replaces the default graph component with a tabbed pane that holds the graph
   * component as well as an export preview component.
   */
  protected void configure(JRootPane rootPane) {
    Container contentPane = rootPane.getContentPane();
    // create a tabbed pane with graphComponent on the first tab and place it in the center of the frame
    tabbedPane = new JTabbedPane();
    configureTabbedPane(tabbedPane);
    contentPane.add(tabbedPane, BorderLayout.CENTER);

    // configure a toolbar and add it to the top of the frame
    JToolBar toolBar = createToolBar();
    configureToolBar(toolBar);
    contentPane.add(toolBar, BorderLayout.NORTH);
    // create a help pane and add it to the right of the frame
    contentPane.add(createHelpPane(), BorderLayout.EAST);
  }

  protected void configureTabbedPane(JTabbedPane tabbedPane) {
    tabbedPane.addTab("Graph", graphComponent);
    // update the content of the second tab if it has been selected
    tabbedPane.addChangeListener(e -> updatePreview());
  }

  /**
   * Adds buttons to the toolbar to update the preview panel and to export the graph or parts of the graph to an image
   * as well as buttons to change the zoom level.
   */
  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createButton("Export to file", "export-image-16.png", e1 -> onSaveButtonClicked()));
    toolBar.addSeparator();
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(createCheckBox("Show Decorations", "Show decorations like selection or handles?", showDecorations,
        e -> {
          showDecorations = ((JCheckBox) e.getSource()).isSelected();
          updatePreview();
        }));
    toolBar.add(createCheckBox("Export Rectangle Contents", "Export only the content of the rectangle or the whole graph?", useRectangle,
        e -> {
          useRectangle = ((JCheckBox) e.getSource()).isSelected();
          updatePreview();
        }));
  }

  /**
   * Initializes the input modes and build a sample graph.
   */
  public void onVisible() {
    // build the input modes and wire up the exporting rectangle
    initializeInputModes();
    // construct a sample graph
    initializeGraph();
  }

  /**
   * Initializes a {@link com.yworks.yfiles.view.input.GraphEditorInputMode} and the rectangle that indicates the region to
   * export.
   */
  protected void initializeInputModes() {
    // create a GraphEditorInputMode instance
    GraphEditorInputMode editMode = new GraphEditorInputMode();
    // and install the edit mode into the canvas.
    graphComponent.setInputMode(editMode);

    // create the model for the export rectangle
    exportRect = new MutableRectangle(0, 0, 100, 100);
    // now add our rectangle to the input mode group which will be rendered on top of the graph items
    RectangleIndicatorInstaller indicatorInstaller = new RectangleIndicatorInstaller(exportRect, RectangleIndicatorInstaller.SELECTION_TEMPLATE_KEY);
    indicatorInstaller.addCanvasObject(graphComponent.getCanvasContext(), graphComponent.getInputModeGroup(), exportRect);

    // add view modes that handle the resizing and movement of the export rectangle
    addExportRectInputModes(editMode);
  }

  /**
   * Adds view modes that handle the resizing and movement of the export rectangle.
   */
  private void addExportRectInputModes(GraphEditorInputMode inputMode){
    // create handles for interactively resizing the export rectangle
    RectangleReshapeHandleProvider rectangleHandles = new RectangleReshapeHandleProvider(exportRect);
    rectangleHandles.setMinimumSize(new SizeD(10, 10));
    // create a input mode that renders the handles and deals with mouse gestures to drag the handles
    HandleInputMode exportHandleInputMode = new HandleInputMode();
    // specify certain handles the input mode should manage
    IInputModeContext inputModeContext = exportHandleInputMode.getInputModeContext();
    exportHandleInputMode.setHandles(new ObservableCollection<>(
        Arrays.asList(
            rectangleHandles.getHandle(inputModeContext, HandlePositions.NORTH_EAST),
            rectangleHandles.getHandle(inputModeContext, HandlePositions.NORTH_WEST),
            rectangleHandles.getHandle(inputModeContext, HandlePositions.SOUTH_EAST),
            rectangleHandles.getHandle(inputModeContext, HandlePositions.SOUTH_WEST))));

    // create a mode that allows for dragging the export rectangle at the sides
    MoveInputMode moveInputMode = new MoveInputMode();
    // assign the exportRect as moveable so that it will be repositioned during dragging
    moveInputMode.setPositionHandler(new ExportRectanglePositionHandler(exportRect));
    // define the hit test that determines where the user can begin dragging the rectangle
    moveInputMode.setHitTestable((context, location) -> {
      GeneralPath path = new GeneralPath(5);
      path.appendRectangle(exportRect, false);
      return path.pathContains(location, context.getHitTestRadius());
    });

    // add the HandleInputMode to the graph editor mode
    exportHandleInputMode.setPriority(1);
    inputMode.add(exportHandleInputMode);
    // Add the MoveInputMode to the graph editor mode:
    // The MoveInputMode that controls the node dragging behavior have a higher priority than the
    // MoveInputMode that is responsible for moving the rectangle around.
    moveInputMode.setPriority(inputMode.getMoveInputMode().getPriority() + 1);
    inputMode.add(moveInputMode);
  }

  /**
   * Initializes a simple sample graph and makes the export rectangle enclose a part of it.
   */
  protected void initializeGraph() {
    IGraph graph = graphComponent.getGraph();

    initializeGraphDefaults(graph);

    // create sample graph
    graph.addLabel(graph.createNode(new PointD(30, 30)), "Node");
    INode node = graph.createNode(new PointD(90, 30));
    graph.createEdge(node, graph.createNode(new PointD(90, 90)));

    // fit the graph bounds now to enclose the current graph
    graphComponent.fitGraphBounds();
    // initially set the export rect to enclose part of the graph's contents
    exportRect.reshape(graphComponent.getContentRect());
    // create graph elements that are outside the current content rect (and export rect)
    graph.createEdge(node, graph.createNode(new PointD(200, 30)));
    // now fit the graph bounds again to make the whole graph visible
    graphComponent.fitGraphBounds();
    // the export rect still encloses the same part of the graph as before

    // update the print preview when the zoom changes
    graphComponent.addZoomChangedListener((source,args) -> updatePreview());
  }

  /**
   * Initializes the default node and edge style.
   */
  protected void initializeGraphDefaults(IGraph graph) {
    // initialize default node style
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(Colors.ORANGE);
    graph.getNodeDefaults().setStyle(nodeStyle);

    // initialize default edge style
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setTargetArrow(IArrow.DEFAULT);
    graph.getEdgeDefaults().setStyle(edgeStyle);
  }

  /**
   * Exports the graph or part of it to an image that is shown in the preview panel.
   */
  protected void updatePreview() {
  }

  /**
   * Returns the component to export from. For exporting an 'undecorated' image, we use a new one.
   */
  protected GraphComponent getExportingGraphComponent() {
    GraphComponent component = graphComponent;
    // check whether decorations (selection, handles, ...) should be hidden
    if (!showDecorations) {
      // if so, create a new GraphComponent with the same graph
      component = new GraphComponent();
      component.setSize(graphComponent.getSize());
      component.setGraph(graphComponent.getGraph());
      component.setViewPoint(graphComponent.getViewPoint());
      component.setBackground(graphComponent.getBackground());
      component.repaint();
    }
    return component;
  }

  /**
   * Returns a ContextConfigurator that considers the export rectangle and margins.
   */
  protected ContextConfigurator createContextConfigurator() {
    // check if the rectangular region or the whole view port should be printed
    RectD regionToExport = useRectangle ? exportRect.toRectD() : getExportingGraphComponent().getViewport();

    // create a configurator with the settings of the option panel
    ContextConfigurator configurator = new ContextConfigurator(regionToExport.getEnlarged(-1));
    setScale(configurator);
    // get the margins
    configurator.setMargins(new InsetsD(topMargin, leftMargin, bottomMargin, rightMargin));
    return configurator;
  }

  /**
   * Determines and sets the scale for exporting to the given {@link ContextConfigurator}.
   * @param configurator ContextConfigurator where to set the scale
   */
  private void setScale(ContextConfigurator configurator) {
    // consider the zoom level
    double zoomedScale = scale * graphComponent.getZoom();

    // look if a fixed size has been specified
    switch (sizeMode) {
      case SPECIFY_WIDTH:
        zoomedScale = configurator.calculateScaleForWidth(zoomedScale * width);
        break;
      case SPECIFY_HEIGHT:
        zoomedScale = configurator.calculateScaleForHeight(zoomedScale * height);
        break;
    }
    configurator.setScale(zoomedScale);
  }

  /**
   * Opens a save dialog and delegates to {@link #saveToFile(String)}.
   */
  private void onSaveButtonClicked() {
    // determine the file to save the graph as image
    JFileChooser dialog = getImageSaveDialog();
    if (dialog.showSaveDialog(graphComponent) != JFileChooser.APPROVE_OPTION) {
      // user has canceled saving the graph as image
      return;
    }

    String filename = dialog.getSelectedFile().getAbsolutePath();
    saveToFile(filename);
  }

  /**
   * Returns a {@link javax.swing.JFileChooser} to select a file where the image should be exported to.
   */
  private JFileChooser getImageSaveDialog() {
    if (chooser == null) {
      chooser = new JFileChooser();
      chooser.setDialogType(JFileChooser.SAVE_DIALOG);
      chooser.setAcceptAllFileFilterUsed(false);
      chooser.setDialogTitle("Save As Image...");
    }
    chooser.resetChoosableFileFilters();
    chooser.addChoosableFileFilter(getFileFilter());
    return chooser;
  }

  /**
   * Saves the graph or part of it as an image using the settings provided by the option panel and the specified filename.
   * @param filename The name of the file to export the image to.
   */
  protected abstract void saveToFile(String filename);

  /**
   * Returns the file filter used in the file chooser.
   */
  protected abstract FileFilter getFileFilter();
}
