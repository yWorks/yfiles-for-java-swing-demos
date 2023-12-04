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
package viewer.printing;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.utils.ObservableCollection;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.geometry.MutableRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.RectangleIndicatorInstaller;
import com.yworks.yfiles.view.RectangleVisualTemplate;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.HandleInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import com.yworks.yfiles.view.input.MoveInputMode;
import com.yworks.yfiles.view.input.RectangleReshapeHandleProvider;
import com.yworks.yfiles.view.CanvasPrintable;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.Pen;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;
import toolkit.PrintPreview;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.util.Arrays;

/**
 * For printing the contents of a yFiles {@link GraphComponent}, the library provides the class {@link CanvasPrintable}.
 *
 * Different settings can be applied to modify the output.
 */
public class PrintingDemo extends AbstractDemo {

  /**
   * The center content of this demo: A tabbed pane with two tabs,
   * one for editing a graph in a GraphComponent, another for the printing and the preview.
   */
  private JTabbedPane tabbedPane;

  /**
   * Displays the preview for printing and provides the printing action.
   */
  private PrintPreview printPreview;

  /**
   * The rectangle which defines which area to print of the graph in world coordinates.
   * This is updated by the PrintingDemo class with either the print rectangle or with
   * the viewport of the GraphComponent, depend on the "Use Rectangle Contents" setting.
   */
  private MutableRectangle printRectangle;

  /**
   * The canvas object that represents the print rectangle in the GraphComponent.
   */
  private ICanvasObject printRectangleCanvasObject;

  /**
   * One of the InputModes that enable the interaction with the print rectangle.
   * This one controls the handles that can be used to resize it.
   */
  private HandleInputMode handleInputMode;

  /**
   * One of the InputModes that enable the interaction with the print rectangle.
   * This one controls the dragging.
   */
  private MoveInputMode moveInputMode;

  /**
   * The yFiles library class CanvasPrintable which will do the actual printing.
   */
  private CanvasPrintable canvasPrintable;

  /**
   * Determines whether or not graph decorations like handles are printed as well.
   * Set by the a checkbox in the toolbar.
   */
  private boolean printDecorations = true;

  /**
   * Determines what region will be printed,
   * the area defined by the print rectangle or the whole viewport of the GraphComponent.
   */
  private boolean clipPrintableArea = true;

  @Override
  protected void configure(JRootPane rootPane) {
    Container contentPane = rootPane.getContentPane();
    // create a tabbed pane with graphComponent on the first tab and a print preview panel
    // on the second tab and place it to the center of the frame
    tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Graph", graphComponent);

    // create the CanvasPrintable to use for printing and the preview.
    canvasPrintable = new CanvasPrintable(graphComponent);
    // create the tab with the print preview.
    printPreview = new PrintPreview(canvasPrintable);
    JPanel printPreviewPanel = printPreview.getContentPane();
    tabbedPane.addTab("Print Preview", printPreviewPanel);
    printPreviewPanel.setBackground(Color.GRAY);
    // update the content of the preview tab if it has been selected
    tabbedPane.addChangeListener(e -> {
      if (tabbedPane.getSelectedIndex() == 1) {
        updatePrintPreview();
      }
    });
    contentPane.add(tabbedPane, BorderLayout.CENTER);
    // configure a toolbar and add it to the top of the frame
    JToolBar toolBar = createToolBar();
    configureToolBar(toolBar);
    contentPane.add(toolBar, BorderLayout.NORTH);
    // create a help pane and add it to the right of the frame
    contentPane.add(createHelpPane(), BorderLayout.EAST);
  }

  /**
   * Adds buttons to the toolbar to update the preview panel and change settings to the printing as well as buttons to change the zoom level.
   */
  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("Print with the current settings", "print-16.png", ICommand.PRINT, null, graphComponent));
    toolBar.addSeparator();
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(createCheckBox("Print Decorations", "Print decorations like selection and handles?", printDecorations,
        e -> {
          printDecorations = ((JCheckBox) e.getSource()).isSelected();
          updatePrintPreview(true);
        }));
    toolBar.add(createCheckBox("Clip Printable Area", "Clip the printable area to the rectangle or print the whole viewport?",
        clipPrintableArea,
        e -> {
          setClipPrintableAreaEnabled(((JCheckBox) e.getSource()).isSelected());
          updatePrintPreview(true);
        }));
    toolBar.addSeparator();
    toolBar.add(createCheckBox("Center Content", "Center the content of the printed page?", canvasPrintable.isCenteringContentEnabled(),
        e -> {
          canvasPrintable.setCenteringContentEnabled(((JCheckBox) e.getSource()).isSelected());
          updatePrintPreview(true);
        }));
    toolBar.add(createCheckBox("Scale Down", "Scale down the content to fit on one page if too large?", canvasPrintable.isScalingDownToFitPageEnabled(),
        e -> {
          canvasPrintable.setScalingDownToFitPageEnabled(((JCheckBox) e.getSource()).isSelected());
          updatePrintPreview(true);
        }));
    toolBar.add(createCheckBox("Scale Up", "Scale up the content to fit on one page if too small?", canvasPrintable.isScalingUpToFitPageEnabled(),
        e -> {
          canvasPrintable.setScalingUpToFitPageEnabled(((JCheckBox) e.getSource()).isSelected());
          updatePrintPreview(true);
        }));
    toolBar.add(createCheckBox("Print Page Marks", "Print page marks at the border of each page?", canvasPrintable.isPageMarkPrintingEnabled(),
        e -> {
          printPreview.setPageMarkPrintingEnabled(((JCheckBox) e.getSource()).isSelected());
          updatePrintPreview(true);
        }));
  }

  /**
   * Selects the print preview tab and updates its content.
   * @param showPreview if <code>true</code> this method will switch to the
   * preview tab if that is not yet visible.
   */
  private void updatePrintPreview(boolean showPreview) {
    // select the print preview tab, the print preview is automatically updated when the tab is selected
    if (showPreview && tabbedPane.getSelectedIndex() != 1) {
      tabbedPane.setSelectedIndex(1);
    } else {
      // already displayed - update manually
      updatePrintPreview();
    }
  }

  /**
   * Updates the print preview panel with the values for the GraphComponent and the area to print
   * by calling {@link #getGraphComponentToPrint()} and {@link #getPrintableArea()}
   */
  private void updatePrintPreview() {
    // get the GraphComponent to print which has the correct settings (e.g. hidden decorations)
    GraphComponent graphComponent = getGraphComponentToPrint();
    // get the area to print according to the setting for the printable area
    RectD bounds = getPrintableArea();

    printPreview.update(graphComponent, bounds.getEnlarged(-2));
  }

  /**
   * Returns the component to use for printing.
   * For printing an 'undecorated' graph (hideDecorations == true), we use fresh instance one.
   */
  public GraphComponent getGraphComponentToPrint() {
    GraphComponent component = this.graphComponent;
    // check whether decorations (selection, handles, ...) should be hidden
    if (!printDecorations) {
      // if so, create a new GraphComponent with the same graph
      component = new GraphComponent();
      component.setSize(this.graphComponent.getSize());
      component.setGraph(this.graphComponent.getGraph());
      component.setViewPoint(this.graphComponent.getViewPoint());
      component.setZoom(graphComponent.getZoom());
      component.repaint();
    }
    return component;
  }

  /**
   * Returns the area of the graph to print.
   *
   * This is either the printRectangle or the whole viewport of the GraphComponent.
   */
  public RectD getPrintableArea() {
    return clipPrintableArea ? printRectangle.toRectD() : graphComponent.getViewport();
  }

  /**
   * Initializes the input modes and build a sample graph.
   */
  public void onVisible() {
    // build the input modes and wire up the print rectangle
    initializeInputModes();

    // construct a sample graph
    initializeGraph();
    // initially update the print preview
    updatePrintPreview();
  }

  /**
   * Initializes a {@link com.yworks.yfiles.view.input.GraphEditorInputMode} and the rectangle that indicates the region to
   * print.
   */
  private void initializeInputModes() {
    // create a GraphEditorInputMode instance
    GraphEditorInputMode editMode = new GraphEditorInputMode();
    // and install the edit mode into the canvas.
    graphComponent.setInputMode(editMode);

    // create the model for the print rectangle
    printRectangle = new MutableRectangle(0, 0, 100, 100);

    // we want to display a rectangle that works just like the selection indicators and also has handles.
    // for this, we can use an implementation of the ISelectionIndicatorInstaller interface.
    addPrintRectangle();

    // initialize the command bindings for printing
    KeyboardInputMode kim = editMode.getKeyboardInputMode();
    kim.addCommandBinding(
            ICommand.PRINT,
            // the print command delegates to PrintPreview's print method
            (command, parameter, source) -> {
              printPreview.print();
              return true;
            },
            // the print command is always executable
            (command, parameter, source) -> true);
    kim.addCommandBinding(
            ICommand.PRINT_PREVIEW,
            // switch to print preview panel
            (command, parameter, source) -> {
              tabbedPane.setSelectedComponent(printPreview.getContentPane());
              return true;
            },
            // the print preview command is executable when the preview tab isn't displayed already
            (command, parameter, source) -> printPreview.getContentPane() != tabbedPane.getSelectedComponent());
  }

  /**
   * Installs the visual representation of the print rectangle onto the GraphComponent and wires up the input modes to us it.
   */
  private void addPrintRectangle() {
    // build an installer using the bounds of our rectangle
    RectangleIndicatorInstaller rectangleIndicatorInstaller = new RectangleIndicatorInstaller(printRectangle);
    // we want to have our own visualization of said rectangle.
    // for this, we register our own RectangleVisualTemplate that slightly differs from the default implementation
    rectangleIndicatorInstaller.setTemplate(new PrintRectangleVisualTemplate());
    // now add our rectangle to the input mode group which will be rendered on top of the graph items
    printRectangleCanvasObject = rectangleIndicatorInstaller.addCanvasObject(graphComponent.getCanvasContext(), graphComponent.getInputModeGroup(),
        printRectangle);

    // add view modes that handle the resizing and movement of the print rectangle
    addInputModesForRectangle();
  }

  /**
   * Removes the visual representation of the print rectangle from the GraphComponent and removes
   * the InputModes from the GraphComponent that dealt with the print rectangle as well.
   */
  private void removePrintRectangle(){
    if (printRectangleCanvasObject != null) {
      // to remove the print rectangle that we previously installed using the RectangularSelectionIndicatorInstaller,
      printRectangleCanvasObject.remove();
      // we also need to remove the input modes for the print rectangle from the GraphEditorInputMode that we set up earlier.
      removeInputModesForRectangle();
      // we delete the canvas object, when the print rectangle is enabled again we build another one.
      printRectangleCanvasObject = null;
    }
  }

  /**
   * Adds view modes that handle the resizing and movement of the print rectangle.
   */
  private void addInputModesForRectangle(){
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphComponent.getInputMode();
    if (handleInputMode == null && moveInputMode == null) {
      // create handles for interactively resizing the print rectangle
      RectangleReshapeHandleProvider rectangleHandles = new RectangleReshapeHandleProvider(printRectangle);
      rectangleHandles.setMinimumSize(new SizeD(10, 10));
      // create a input mode that renders the handles and deals with mouse gestures to drag the handles
      handleInputMode = new HandleInputMode();
      // specify certain handles the input mode should manage
      IInputModeContext inputModeContext = handleInputMode.getInputModeContext();
      handleInputMode.setHandles(new ObservableCollection<>(
          Arrays.asList(
              rectangleHandles.getHandle(inputModeContext, HandlePositions.NORTH_EAST),
              rectangleHandles.getHandle(inputModeContext, HandlePositions.NORTH_WEST),
              rectangleHandles.getHandle(inputModeContext, HandlePositions.SOUTH_EAST),
              rectangleHandles.getHandle(inputModeContext, HandlePositions.SOUTH_WEST))));

      // create a mode that allows for dragging the print rectangle at the sides
      moveInputMode = new MoveInputMode();
      // assign the print rectangle as moveable so that it will be repositioned during dragging
      moveInputMode.setPositionHandler(new PrintRectanglePositionHandler(printRectangle));
      // define the hit test that determines where the user can begin dragging the rectangle
      moveInputMode.setHitTestable((context, location) -> {
        GeneralPath path = new GeneralPath(5);
        path.appendRectangle(printRectangle, false);
        return path.pathContains(location, context.getHitTestRadius());
      });
    }

    // add the HandleInputMode to the graph editor mode
    handleInputMode.setPriority(1);
    inputMode.add(handleInputMode);
    // Add the MoveInputMode to the graph editor mode:
    // The MoveInputMode that controls the node dragging behavior have a higher priority than the
    // MoveInputMode that is responsible for moving the rectangle around.
    moveInputMode.setPriority(inputMode.getMoveInputMode().getPriority() + 1);
    inputMode.add(moveInputMode);
  }

  /**
   * Removes the InputModes from the GraphComponent that deal with the print rectangle.
   */
  private void removeInputModesForRectangle(){
    GraphEditorInputMode inputMode = (GraphEditorInputMode) graphComponent.getInputMode();
    inputMode.remove(handleInputMode);
    inputMode.remove(moveInputMode);
  }

  /**
   * Initializes a simple sample graph and makes the print rectangle enclose a part of it.
   */
  private void initializeGraph() {
    IGraph graph = graphComponent.getGraph();

    // initialize defaults
    DemoStyles.initDemoStyles(graph);

    // create sample graph
    graph.addLabel(graph.createNode(new PointD(30, 30)), "Node");
    INode node = graph.createNode(new PointD(90, 30));
    graph.createEdge(node, graph.createNode(new PointD(90, 90)));

    // fit the graph bounds now to enclose the current graph
    graphComponent.fitGraphBounds();
    // initially set the print rect to enclose part of the graph's contents
    printRectangle.reshape(graphComponent.getContentRect());
    // create graph elements that are outside the current content rect (and print rect)
    graph.createEdge(node, graph.createNode(new PointD(200, 30)));
    // now fit the graph bounds again to make the whole graph visible
    graphComponent.fitGraphBounds();
    // the print rect still encloses the same part of the graph as before

    // update the print preview when the zoom changes
    graphComponent.addZoomChangedListener((source,args) -> updatePrintPreview(false));
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new PrintingDemo().start();
    });
  }

  /**
   * Sets the option to use the contents of the rectangle for printing.
   * If the option is disabled, this method will call the removePrintRectangle method which
   * will remove the rectangle from the GraphComponent and uninstall the InputModes for it.
   * Otherwise it will install the rectangle and add the InputModes.
   */
  public void setClipPrintableAreaEnabled(boolean clipPrintableArea) {
    this.clipPrintableArea = clipPrintableArea;
    if (!clipPrintableArea){
      removePrintRectangle();
    } else {
      addPrintRectangle();
    }
  }

  /**
   * A custom visual template that draws a red dotted rectangle for the print rectangle.
   */
  private static class PrintRectangleVisualTemplate extends RectangleVisualTemplate {

    // the pen for drawing the rectangle.
    private static final Pen PEN = new Pen(Colors.CRIMSON, 2);

    static {
      PEN.setDashStyle(DashStyle.getDot());
    }

    PrintRectangleVisualTemplate() {
      setPen(PEN);
    }

    @Override
    public IVisual createVisual(IRenderContext context, RectD bounds, Object dataObject) {
      // make the rectangle visually a little bigger so that it is not contained in the printed area
      // (the pen leans into the area so consider its thickness)
      return super.createVisual(context, bounds.getEnlarged(2), dataObject);
    }

    @Override
    public IVisual updateVisual(IRenderContext context, IVisual oldVisual, RectD bounds, Object dataObject) {
      // make the rectangle visually a little bigger so that it is not contained in the printed area
      // (the pen leans into the area so consider its thickness)
      return super.updateVisual(context, oldVisual, bounds.getEnlarged(2), dataObject);
    }
  }
}
