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
package viewer.graphmlcompatibility;

import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.input.KeyboardInputMode;
import complete.bpmn.view.BpmnNodeStyle;
import complete.orgchart.Employee;
import toolkit.AbstractDemo;
import viewer.graphmlcompatibility.extensions.demo.BpmnStripeDescriptorExtension;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ICommand;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 * This demo shows how to enable backwards READ compatibility for GraphML.
 * <p>
 * Class {@link CompatibilitySupport} may be used to configure a given
 * {@link GraphMLIOHandler} instance so that it can read GraphML files written
 * in yFiles for Java (Swing) version 3.0.x.
 * You can enable backwards compatibility with this single line:
 * </p><pre>
 * CompatibilitySupport.configureIOHandler(ioh);
 * </pre><p>
 * The resources directory contains a number of sample files from previous distributions.
 * </p>
 */
public class GraphMLCompatibilityDemo extends AbstractDemo {
  /**
   * Opens legacy GraphML files.
   * In this context, <em>legacy</em> means yFiles for Java (Swing) 3.0.x.
   */
  private static final ICommand OPEN_LEGACY = ICommand.createCommand("OpenLegacy");

  /**
   * Initializes file operations for this demo.
   */
  @Override
  public void initialize() {
    // the convenience commands for reading graphs from GrapML and writing
    // graphs to GraphML have to be explicitly enabled
    graphComponent.setFileIOEnabled(true);

    // add key and command bindings for opening legacy GraphML files
    // note:
    // a GraphMLIOHandler instance configured for reading legacy GraphML
    // files cannot be used for writing yFiles for Java (Swing) 3.1.x GraphML files.
    // since a graph component's GraphMLIOHandler is used for both reading
    // and writing GraphML files, a legacy reader cannot be set as *the*
    // graph component's GraphMLIOHandler. Thus a custom command binding for
    // reading legacy GraphML files is used.
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    KeyboardInputMode kim = gvim.getKeyboardInputMode();
    kim.addKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), OPEN_LEGACY);
    kim.addCommandBinding(OPEN_LEGACY, this::executeOpen, this::canExecuteOpen);

    graphComponent.setInputMode(gvim);

    ICommand.invalidateRequerySuggested();
  }

  /**
   * Loads and centers a sample graph in the graph component.
   */
  public void onVisible() {
    readSampleGraph("computer-network");
  }

  /**
   * Adds controls for reading legacy GraphML files, writing current GraphML
   * files, and stepping through legacy samples.
   */
  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    // control for reading legacy GraphML files
    toolBar.add(createCommandButtonAction("Open", "open-16.png", OPEN_LEGACY, null, graphComponent));
    // control for writing current (i.e. yFiles for Java (Swing) 3.1.x) GraphML files
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE_AS, null, graphComponent));
    toolBar.addSeparator();

    super.configureToolBar(toolBar);

    toolBar.addSeparator();

    JComboBox<String> graphChooserBox = new JComboBox<>(new String[]{
            "computer-network",
            "orgchart",
            "activity-diagram",
            "swimlanes",
            "movies",
            "family-tree",
            "hierarchy",
            "nesting",
            "social-network",
            "uml-diagram",
            "large-tree",
            "project-application",
            "different-exception-flows",
    });
    graphChooserBox.setMaximumSize(graphChooserBox.getPreferredSize());
    graphChooserBox.addActionListener(( e ) -> 
      readSampleGraph((String) ((JComboBox) e.getSource()).getSelectedItem())
    );

    // control for stepping through legacy samples
    toolBar.add(new ShowGraph(graphChooserBox, false));
    // control for choosing legacy samples
    toolBar.add(graphChooserBox);
    // control for stepping through legacy samples
    toolBar.add(new ShowGraph(graphChooserBox, true));
  }

  /**
   * Reads the currently selected GraphML from the graph sample chooser box.
   */
  private void readSampleGraph( String sample ) {
    // first derive the file name
    URL graphML = getClass().getResource("resources/" + sample + ".graphml");

    // then load the graph
    try {
      IGraph graph = graphComponent.getGraph();
      graph.clear();

      newConfiguredReader().read(graph, graphML);

      // when done - fit the bounds
      this.graphComponent.fitGraphBounds();

      // the commands CanExecute state might have changed - suggest a re-query.
      ICommand.invalidateRequerySuggested();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * Creates a new {@link GraphMLIOHandler} instance configured for reading
   * legacy GraphML files.
   */
  private static GraphMLIOHandler newConfiguredReader() {
    GraphMLIOHandler reader = new GraphMLIOHandler();

    // IMPORTANT:
    // configure the reader for reading yFiles for Java (Swing) 3.0.x GraphML files
    CompatibilitySupport.configureIOHandler(reader);

    // CompatibilitySupport will enable reading library styles
    // support for custom styles (or types) such as demo styles has to be added
    // separately like e.g. support for the BPMNEditorDemo and the OrgChartDemo
    // styles and types
    reader.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-java/demos/OrgChartDemo/1.0", Employee.class);
    reader.addXamlNamespaceMapping("http://www.yworks.com/xml/yfiles-bpmn/1.0", BpmnNodeStyle.class);
    reader.addXamlNamespaceMapping("http://www.yworks.com/xml/yfiles-bpmn/1.0", "StripeDescriptor", BpmnStripeDescriptorExtension.class);

    return reader;
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new GraphMLCompatibilityDemo().start();
    });
  }

  /**
   * Creates an application frame for this demo and displays it.
   */
  @Override
  public void start() {
    start("GraphML Compatibility Demo - yFiles for Java (Swing)");
  }

  /**
   * Reads legacy GraphML files.
   * @param command the command that triggered this operation. Should be
   * {@link #OPEN_LEGACY}.
   * @param parameter an optional parameter for the operation. Ignored for
   * opening GraphML files.
   * @param source the graph component that will display the read graph.
   */
  private boolean executeOpen( ICommand command, Object parameter, Object source ) {
    if (source instanceof GraphComponent) {
      // since a graph component's GraphMLIOHandler is used for both reading
      // and writing GraphML files, a legacy reader is set only temporarily
      // such that subsequent write operations use a default GraphMLIOHandler
      GraphComponent graphComponent = (GraphComponent) source;
      GraphMLIOHandler oldHandler = graphComponent.getGraphMLIOHandler();
      try {
        graphComponent.setGraphMLIOHandler(newConfiguredReader());
  
        ICommand.OPEN.execute(parameter, source);
      } finally {
        graphComponent.setGraphMLIOHandler(oldHandler);
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Determines if the "open legacy GraphML files" command may be executed.
   * @param command the command for which the check is performed. Should be
   * {@link #OPEN_LEGACY}.  
   * @param parameter an optional parameter for the operation. Ignored for
   * opening GraphML files.
   * @param source the graph component that will display the read graph if the
   * command is executed.
   */
  private boolean canExecuteOpen( ICommand command, Object parameter, Object source ) {
    return ICommand.OPEN.canExecute(parameter, source);
  }



  /**
   * Steps through the legacy samples provided by this demo application.
   */
  private class ShowGraph extends AbstractAction {
    final JComboBox<?> jcb;
    final boolean next;

    ShowGraph( JComboBox<?> jcb, boolean next ) {
      super(next ? "Next" : "Previous");
      this.jcb = jcb;
      this.next = next;
      putValue(SHORT_DESCRIPTION,
               next ? "Show next graph" : "Show previous graph");
      putValue(SMALL_ICON,
               createIcon(next ? "arrow-right-16.png" : "arrow-left-16.png"));

      jcb.addItemListener(e -> {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          updateEnabledState();
        }
      });
      updateEnabledState();
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
      if (next) {
        jcb.setSelectedIndex(jcb.getSelectedIndex() + 1);
      } else {
        jcb.setSelectedIndex(jcb.getSelectedIndex() - 1);
      }
    }

    private void updateEnabledState() {
      if (next) {
        setEnabled(jcb.getSelectedIndex() < jcb.getItemCount() - 1);
      } else {
        setEnabled(jcb.getSelectedIndex() > 0);
      }
    }
  }
}
