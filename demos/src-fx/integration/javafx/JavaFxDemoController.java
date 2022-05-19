/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package integration.javafx;

import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.PopulateItemPopupMenuEventArgs;
import com.yworks.yfiles.graph.IModelItem;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.web.WebView;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

/**
 * Integrate yFiles for Java (Swing) in a JavaFX application.
 * <ul>
 *   <li>
 *     A toolbar that provides JavaFX buttons to change the zoom level of the GraphComponent that is a Swing component as
 *     well as JavaFX buttons for undo/redo functionality.
 *   </li>
 *   <li>
 *     A right click on a node shown in the GraphComponent opens a JavaFX context menu and allows the user to delete the
 *     clicked node from the GraphComponent.
 *   </li>
 *   <li>
 *     On the left side a JavaFX palette offers nodes with different styles that can be dragged into the GraphComponent.
 *   </li>
 * </ul>
 * <p>
 * JavaFX data should be accessed only on the JavaFX application thread. Whenever JavaFX data should be changed, the code must
 * be wrapped into a Runnable object and call the {@link javafx.application.Platform#runLater(Runnable)} method.
 * </p>
 * <p>
 * Swing data should be changed only on the EDT. To ensure that the code is implemented on the EDT, it must be wrapped
 * into a Runnable object and call the {@link javax.swing.SwingUtilities#invokeLater(Runnable)} method.
 * </p>
 * <p>
 * To simplify matters the drag data is provided in text format: we convert an enum constant to text when dragging
 * starts and convert the text back to the enum constant when the node is dropped. Have a look at
 * <a href="http://docs.oracle.com/javafx/2/drag_drop/jfxpub-drag_drop.htm">Drag-and-Drop Feature in JavaFX Applications</a>
 * from the Oracle resource web page that shows how to implement drag and drop with a custom data transfer.
 * </p>
 * <p>
 * Known issues of the Java 8u74 with the interoperability of JavaFX and Swing:
 * </p>
 * <ul>
 *   <li>rendering artifacts while resizing the window</li>
 *   <li>rendering artifacts while moving a dragged node outside the GraphControl</li>
 *   <li>setting the drop target of the SwingNode's content component (the GraphComponent in this case) to <code>null</code>
 *    once breaks any further drag'n'drop operations. Setting another drag target later has no effect
 *   </li>
 *   <li>drag and drop issues on Linux</li>
 * </ul>
 */
public class JavaFxDemoController {
  public SwingNode swingNode;
  public WebView helpPane;
  public Button zoomInButton;
  public Button zoomOutButton;
  public Button zoomOriginalButton;
  public Button fitContentButton;
  public Button undoButton;
  public Button redoButton;
  public ListView<NodeTemplate> palette;

  private GraphComponent graphComponent;
  // handles drop events from a JavaFX control
  private JavaFxNodeDropInputMode dropMode;

  public void initialize() {
    // To wire up the buttons of the JavaFX toolbar with commands acting on the GraphComponent
    // we need the GraphComponent instance to initializing the JavaFX part. Since the
    // GraphComponent is part of the Swing UI we have to initialize the Swing UI first and
    // then the JavaFX part afterwards on the JavaFX application thread.
    Runnable finishHandler = () -> ThreadUtils.runLater(this::initializeFX);

    // the Swing part of the user interface must be initialized on the EDT
    ThreadUtils.invokeLater(() -> initializeSwing(finishHandler));
  }

  /**
   * Initializes the JavaFX part of the user interface.
   */
  private void initializeFX() {
    // show a help text in the help pane
    helpPane.getEngine().load(getResourceUrl("help.html"));

    // connect the toolbar buttons with appropriate command bindings
    initializeCommandBindings();

    // populates palette and enable it as a drag source
    initializePalette();
  }

  /**
   * Connects the toolbar buttons with the appropriate command bindings.
   */
  private void initializeCommandBindings() {
    wireButtonWithCommand(zoomInButton, ICommand.INCREASE_ZOOM, null, graphComponent);
    wireButtonWithCommand(zoomOutButton, ICommand.DECREASE_ZOOM, null, graphComponent);
    wireButtonWithCommand(zoomOriginalButton, ICommand.ZOOM, 1, graphComponent);
    wireButtonWithCommand(fitContentButton, ICommand.FIT_GRAPH_BOUNDS, null, graphComponent);
    wireButtonWithCommand(undoButton, ICommand.UNDO, null, graphComponent);
    wireButtonWithCommand(redoButton, ICommand.REDO, null, graphComponent);
  }

  /**
   * Connects the given {@link javafx.scene.control.Button} with the given {@link com.yworks.yfiles.view.input.ICommand}.
   * @param button    the button to configure
   * @param command   the command to execute
   * @param parameter the parameter for the execution of the command
   * @param target    the target to execute the command on
   */
  private void wireButtonWithCommand(Button button, ICommand command, Object parameter, JComponent target) {
    // execute the command when the button is selected
    button.setOnAction(event ->
        // execute the command on the EDT
        SwingUtilities.invokeLater(() -> command.execute(parameter, target)));

    // enable/disable depending of the command's state
    command.addCanExecuteChangedListener((source, args) ->
        // set the state of the JavaFX button on JavaFX application thread
        Platform.runLater(() -> button.setDisable(!command.canExecute(parameter, target))));
  }

  /**
   * Initializes the palette containing the nodes which may be dragged into the GraphComponent.
   */
  private void initializePalette() {
    // populate the palette with the enum constants of NodeTemplate
    palette.getItems().addAll(NodeTemplate.values());
    // specify a cell factory that shows images of the node template in the list
    palette.setCellFactory(listView -> new PaletteCell());

    // enable the palette as a drag source
    palette.setOnDragDetected(event -> {
      ClipboardContent content = new ClipboardContent();
      // set the name of the selected node template as drag data
      NodeTemplate selectedNodeTemplate = palette.getSelectionModel().getSelectedItem();
      String nodeTemplateName = selectedNodeTemplate.name();
      content.put(getOrCreateStringFormat(), nodeTemplateName);

      // The set drag data is only accessible later on drag drop events.
      // As it is needed during drag over events as well to show the node preview,
      // we store it in the used MyNodeDropInputMode.
      JavaFxDemoController.this.dropMode.setData(nodeTemplateName);
      Dragboard dragboard = palette.startDragAndDrop(TransferMode.COPY);
      dragboard.setContent(content);
      // disable the default drag icon
      dragboard.setDragView(new WritableImage(1, 1));
      event.consume();
    });
  }

  /**
   * Initializes the Swing part of the user interface.
   */
  private void initializeSwing(Runnable finishHandler) {
    // create a GraphControl
    graphComponent = new GraphComponent();

    // initialize the input mode and enable context menu for nodes
    GraphEditorInputMode editorMode = new GraphEditorInputMode();
    editorMode.setGroupingOperationsAllowed(true);
    editorMode.setPopupMenuInputMode(new JavaFxPopupMenuInputMode(swingNode));
    editorMode.setPopupMenuItems(GraphItemTypes.NODE);
    editorMode.addPopulateItemPopupMenuListener(this::populateNodeContextMenu);

    // activate drag and drop from the palette
    dropMode = new JavaFxNodeDropInputMode();
    // we identify the group nodes during a drag by the type of its style
    dropMode.setIsGroupNodePredicate(node -> node.getStyle() instanceof PanelNodeStyle);
    // the drag data is provided in text format
    dropMode.setDataFlavor(DataFlavor.stringFlavor);
    dropMode.setPreviewEnabled(true);
    dropMode.setEnabled(true);
    editorMode.setNodeDropInputMode(dropMode);

    graphComponent.setInputMode(editorMode);

    // load and show a sample graph after the GraphComponent has got its size
    graphComponent.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        super.componentResized(e);
        if (graphComponent.getWidth() > 0 || graphComponent.getHeight() > 0) {
          graphComponent.removeComponentListener(this);
          initializeGraph();
        }
      }
    });

    // add a dummy drop target listener to the graph component so there is always at least one listener and the
    // graph component doesn't set its drop target to null.
    // This is necessary due to a bug in SwingNode.
    graphComponent.addDropTargetListener(new DropTargetAdapter() {
      @Override
      public void drop(DropTargetDropEvent dtde) {
      }
    });

    // embed the GraphControl instance in the SwingNode
    swingNode.setContent(graphComponent);

    // the Swing part of the UI is now complete
    finishHandler.run();
  }

  /**
   *  Populates the context menu for nodes.
   */
  private void populateNodeContextMenu(Object source, PopulateItemPopupMenuEventArgs<IModelItem> args) {
    if (args.getItem() instanceof INode) {
      INode node = (INode) args.getItem();
      // The return type of the following method is Object to be able to support context menus of different Java GUI
      // toolkits. By default this is an instance of Swing's JPopupMenu, but our JavaFxContextMenuInputMode specifies
      // the Java FX ContextMenu to be used as context menu control.
      ContextMenu contextMenu = (ContextMenu) args.getMenu();
      // show JavaFX context menu on JavaFX application thread
      Platform.runLater(() -> addDeleteNodeMenuItem(contextMenu, node));
      args.setHandled(true);
    }
  }

  /**
   * Adds a {@link javafx.scene.control.MenuItem} to the given {@link javafx.scene.control.ContextMenu} that enables the
   * user to delete the given node.
   * @param contextMenu the context menu to add the menu item
   * @param node        the node to delete with the context menu
   */
  private void addDeleteNodeMenuItem(ContextMenu contextMenu, INode node) {
    MenuItem menuItem = new MenuItem("Delete node", new ImageView("@../../resources/delete3-16.png"));
    menuItem.setOnAction(event ->
        // remove the node on the EDT
        SwingUtilities.invokeLater(() -> graphComponent.getGraph().remove(node)));
    contextMenu.getItems().add(menuItem);
  }

  /**
   * Creates a DataFormat using the same mime type as DataFlavor.stringFlavor or returns such a DataFormat if it had
   * been created before.
   */
  private DataFormat getOrCreateStringFormat() {
    String stringMimeType = DataFlavor.stringFlavor.getMimeType();
    DataFormat stringDataFormat = DataFormat.lookupMimeType(stringMimeType);
    if (stringDataFormat != null) {
      return stringDataFormat;
    } else {
      return new DataFormat(DataFlavor.stringFlavor.getMimeType());
    }
  }

  /**
   * Enables grouping support, loads a sample graph and initializes the default node style.
   */
  private void initializeGraph() {
    // enable undo
    graphComponent.getGraph().setUndoEngineEnabled(true);

    // load the sample graph
    try {
      graphComponent.importFromGraphML(getResourceUrl("example.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // set the default node style
    ShinyPlateNodeStyle style = new ShinyPlateNodeStyle();
    style.setPaint(Color.ORANGE);
    graphComponent.getGraph().getNodeDefaults().setStyle(style);
  }

  /**
   * Returns an URL to the given resource file.
   */
  private static String getResourceUrl(String resource) {
    return JavaFxDemo.class.getResource("resources/" + resource).toExternalForm();
  }
}
