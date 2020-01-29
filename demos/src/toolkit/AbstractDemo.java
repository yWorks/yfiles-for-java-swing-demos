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
package toolkit;

import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.input.CommandAction;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Abstract base class for yFiles for Java (Swing) demos. The default implementation creates a frame with a {@link
 * com.yworks.yfiles.view.GraphComponent} in the center, a help pane on the right side and a toolbar.
 */
public abstract class AbstractDemo {
  protected GraphComponent graphComponent;

  /**
   * Initializes a <code>AbstractDemo</code> instance. Creates a {@link com.yworks.yfiles.view.GraphComponent} instance.
   */
  protected AbstractDemo() {
    graphComponent = new GraphComponent();
  }

  /**
   * Creates an application frame for this demo and displays it.
   * The title of the application frame is derived from the application's
   * simple class name.
   */
  public void start() {
    StringBuilder title = new StringBuilder();
    String name = getClass().getSimpleName();
    title.append(name.charAt(0));
    for (int i = 1, n = name.length(); i < n; ++i) {
      char c = name.charAt(i);
      if (Character.isUpperCase(c)) {
        title.append(' ');
      }
      title.append(c);
    }
    title.append(" - yFiles for Java (Swing)");

    start(title.toString());
  }

  /**
   * Creates an application frame for this demo and displays it.
   * @param title the title of the application frame.
   */
  public void start(String title) {
    Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> reportError(throwable, title));


    JFrame frame = createFrame(title);
    configure(frame.getRootPane());

    initialize();

    frame.setVisible(true);
    onVisible();
  }

  /**
   * Creates a new content pane for the given JRootPane and
   * configures it using the configure() method.
   * Used to display the demo in our DemoBrowser.
   */
  public void addContentTo(JRootPane rootPane) {
    JPanel contentPane = new JPanel(new BorderLayout());
    rootPane.setContentPane(contentPane);
    configure(rootPane);
  }

  /**
   * Adds components to the content pane of the given JRootPane which is
   * typically the JRootPane of the application frame.
   * By default, a graph component, a tool bar, and a help pane are added.
   */
  protected void configure(JRootPane rootPane) {
    Container contentPane = rootPane.getContentPane();
    contentPane.add(graphComponent, BorderLayout.CENTER);

    JToolBar toolBar = createToolBar();
    if (toolBar != null) {
      configureToolBar(toolBar);
      contentPane.add(toolBar, BorderLayout.NORTH);
    }

    JComponent helpPane = createHelpPane();
    if (helpPane != null) {
      contentPane.add(helpPane, BorderLayout.EAST);
    }
  }

  /**
   * Creates a {@link javax.swing.JToolBar}. For demos without toolbar override and return <code>null</code>.
   */
  protected JToolBar createToolBar() {
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    return toolBar;
  }

  /**
   * Initializes application state before the application frame is displayed.
   * By default, this method does nothing.
   */
  public void initialize() {
  }

  /**
   * Initializes application state after the application frame is displayed.
   * By default, this method does nothing.
   */
  public void onVisible() {
  }

  /**
   * Cleans up the application state.
   */
  public void dispose() {
    if (graphComponent != null) {
      // to cleanly dispose a GraphComponent, uninstall the main InputMode so that
      // modes that deal with the component hierarchy can reset the application state.
      graphComponent.setInputMode(null);
    }
  }

  /**
   * Creates a {@link javax.swing.JFrame} with the given title.
   * @param title The title of the demo.
   * @return a {@link javax.swing.JFrame} with the given title.
   */
  protected JFrame createFrame(String title) {
    JFrame frame = new JFrame(title);
    frame.setIconImages(Arrays.asList(
        createIcon("logo_16.png").getImage(),
        createIcon("logo_24.png").getImage(),
        createIcon("logo_32.png").getImage(),
        createIcon("logo_48.png").getImage(),
        createIcon("logo_64.png").getImage(),
        createIcon("logo_128.png").getImage()));
    frame.setSize(1365, 768);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    return frame;
  }

  /**
   * Creates a help pane with a help text as an html page located in the resource folder. The file should be named
   * <code>help</code> and located in a folder <code>resource</code> beside the demo class.
   * @return a help pane
   */
  protected JComponent createHelpPane() {
    JEditorPane editorPane = new JEditorPane();
    editorPane.setEditable(false);
    editorPane.setMargin(new Insets(0, 0, 0, 0));
    try {
      editorPane.setPage(getClass().getResource("resources/help.html"));
    } catch (IOException e) {
      editorPane.setContentType("text/plain");
      editorPane.setText(
          "Could not resolve help text. Please ensure that your build process or IDE adds " +
          "the folder \"resources\" containing the help.html file to the class path.");
    }
    // make links clickable
    editorPane.addHyperlinkListener(e -> {
      if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        if(Desktop.isDesktopSupported()) {
          try {
            Desktop.getDesktop().browse(e.getURL().toURI());
          } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
          }
        }
      }
    });

    JScrollPane scrollPane = new JScrollPane(editorPane);
    scrollPane.setPreferredSize(new Dimension(340, 250));
    return scrollPane;
  }

  /**
   * Configures the given {@link javax.swing.JToolBar} with buttons to zoom in and out and to adjust the view port to
   * fully encompass the graph.
   */
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom 1:1", "zoom-original2-16.png", ICommand.ZOOM, 1, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Adjust the view port to show the complete graph", "fit2-16.png", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
  }

  /**
   * Creates an {@link javax.swing.Action} for the given command used for buttons in a toolbar.
   * @param tooltip   The text to show as tooltip.
   * @param icon      The icon to show.
   * @param command   The command to execute.
   * @param parameter The parameter for the execution of the command.
   * @param target    The target to execute the command on.
   * @return an {@link javax.swing.Action} for the given command.
   */
  protected Action createCommandButtonAction(String tooltip, String icon, ICommand command, Object parameter, JComponent target) {
    Action action = createCommandAction(command, parameter, target);
    action.putValue(Action.SHORT_DESCRIPTION, tooltip);
    action.putValue(Action.SMALL_ICON, createIcon(icon));
    return action;
  }

  /**
   * Creates an {@link javax.swing.Action} for the given command used for menu items.
   * @param text      The text of the menu item.
   * @param command   The command to execute.
   * @param parameter The parameter for the execution of the command.
   * @param target    The target to execute the command on.
   * @return an {@link javax.swing.Action} for the given command.
   */
  protected Action createCommandMenuItemAction(String text, ICommand command, Object parameter, JComponent target) {
    Action action = createCommandAction(command, parameter, target);
    action.putValue(Action.NAME, text);
    return action;
  }


  /**
   * Creates an {@link Action} to toggle the snapping feature.
   * @return an {@link Action} to toggle the snapping feature.
   */
  protected Action createToggleSnapAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JToggleButton) {
          JToggleButton button = (JToggleButton) e.getSource();
          GraphEditorInputMode geim = (GraphEditorInputMode) graphComponent.getInputMode();
          geim.getSnapContext().setEnabled(button.isSelected());
          geim.getLabelSnapContext().setEnabled(button.isSelected());
        }
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Snapping");
    action.putValue(Action.SMALL_ICON, createIcon("snap-16.png"));
    return action;
  }

  /**
   * Creates an {@link Action} to toggle orthogonal edge creation.
   * @return an {@link Action} to toggle orthogonal edge creation.
   */
  protected Action createToggleOrthogonalEdgeCreationAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JToggleButton) {
          JToggleButton button = (JToggleButton) e.getSource();
          boolean selected = button.isSelected();
          GraphEditorInputMode inputMode = (GraphEditorInputMode) graphComponent.getInputMode();
          inputMode.getOrthogonalEdgeEditingContext().setEnabled(selected);
        }
      }
    };
    action.putValue(Action.SHORT_DESCRIPTION, "Orthogonal edges");
    action.putValue(Action.SMALL_ICON, createIcon("orthogonal-editing-16.png"));
    return action;
  }

  /**
   * Creates an {@link javax.swing.Action} for a given command.
   * @param command   The command to execute.
   * @param parameter The parameter for the execution of the command.
   * @param target    The target to execute the command on.
   * @return an {@link javax.swing.Action} for a given command.
   */
  private Action createCommandAction(ICommand command, Object parameter, JComponent target) {
    return new CommandAction(command, parameter, target);
  }

  /**
   * Creates a {@link javax.swing.JButton specific button}.
   * @param tooltip the tooltip to display for the button.
   * @param icon    the icon to show on the button.
   * @param action  the action to execute when the button is selected.
   */
  protected JButton createButton(String tooltip, String icon, ActionListener action) {
    JButton button = new JButton();
    button.setToolTipText(tooltip);
    button.setIcon(createIcon("" + icon));
    button.addActionListener(action);
    return button;
  }

  /**
   * Creates a {@link javax.swing.JCheckBox specific checkbox}.
   * @param title the label that appears to the right of the checkbox.
   * @param tooltip the tooltip to display for the checkbox.
   * @param init the initial checked state of the checkbox.
   * @param action the action to execute when the checkbox was checked or unchecked.
   */
  protected JCheckBox createCheckBox(String title, String tooltip, boolean init, ActionListener action) {
    JCheckBox checkBox = new JCheckBox(title);
    checkBox.setToolTipText(tooltip);
    checkBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    checkBox.setSelected(init);
    checkBox.addActionListener(action);
    return checkBox;
  }

  /**
   * Creates an {@link javax.swing.ImageIcon} from the specified file located in the resource's folder.
   * @param name filename of the icon
   * @return an {@link javax.swing.ImageIcon} from the specified file located in the resource's folder.
   */
  protected ImageIcon createIcon(String name) {
    return new ImageIcon(getClass().getResource("/resources/" + name));
  }

  /**
   * Tries to use the system look and feel.
   */
  protected static void initLnF() {
    try {
      if (!"com.sun.java.swing.plaf.motif.MotifLookAndFeel".equals(UIManager.getSystemLookAndFeelClassName())
          && !"com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(UIManager.getSystemLookAndFeelClassName())
          && !UIManager.getSystemLookAndFeelClassName().equals(UIManager.getLookAndFeel().getClass().getName())) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates an error reporter and invokes it by calling {@link EventQueue#invokeLater(Runnable)}.
   */
  private void reportError( Throwable throwable, String title ) {
    EventQueue.invokeLater(new ErrorReporter(getFrame(graphComponent), throwable, title));
  }

  private static JFrame getFrame( JComponent c ) {
    for (Container p = c.getParent(); p != null; p = p.getParent()) {
      if (p instanceof JFrame) {
        return (JFrame) p;
      }
    }
    return null;
  }
}
