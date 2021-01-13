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
package style.svgnodestyle;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.GraphEditorInputMode;

import toolkit.AbstractDemo;

import java.awt.EventQueue;
import java.net.URL;
import javax.swing.JCheckBox;
import javax.swing.JToolBar;

/**
 * Demonstrates how to implement a custom {@link INodeStyle} that
 * displays Scalable Vector Graphics. The BATIK library is used for handling
 * the required (low-level) SVG rendering.
 * Additionally, this demo shows how to configure yFiles for Java (Swing)'s
 * {@link GraphMLIOHandler} to embed displayed SVG documents in GraphML.
 * <p>
 * The demo uses a patched version of the Batik SVG library available on the
 * <a href="https://www.yworks.com/resources/yfilesjava/demos-support/3.1/batik.jar">yFiles website</a>.
 * </p>
 */
public class SVGNodeStyleDemo extends AbstractDemo {

  private final String SVG_DEMO_NAMESPACE = "http://www.yworks.com/yfiles-for-java/demos/SVGNodeStyleDemo/1.0";

  /**
   * Adds controls for opening and saving graphs in GraphML format as well as
   * enabling/disabling double-buffering for SVG nodes.
   */
  protected void configureToolBar(JToolBar toolBar) {
    GraphComponent view = graphComponent;

    toolBar.add(createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, view));
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE, null, view));
    toolBar.addSeparator();

    super.configureToolBar(toolBar);

    // enables/disable double-buffering
    // Note:
    // The double-buffering control is meant as a simple way to demonstrate the
    // rendering performance improvement through double-buffering.
    // It does not necessarily reflect the actual state of all displayed SVG
    // nodes, because a GraphML file could theoretically contain SVGNodeStyles
    // with both double-buffering enabled and double-buffering disabled 
    JCheckBox jcb = new JCheckBox("Use Double-buffering");
    jcb.setSelected(true);
    jcb.addItemListener(e -> {
      boolean state = ((JCheckBox) e.getSource()).isSelected();

      IGraph graph = view.getGraph();
      for (INode node : graph.getNodes()) {
        INodeStyle style = node.getStyle();
        if (style instanceof SVGNodeStyle) {
          ((SVGNodeStyle) style).setDoubleBufferingEnabled(state);
        }
      }
    });

    toolBar.addSeparator();
    toolBar.add(jcb);
  }

  /**
   * Registers a {@link GraphMLIOHandler} specifically configured for SVG node
   * styles, opens a sample graph with several SVG nodes, and enables
   * interactive editing of the displayed graph.
   */
  public void initialize() {
    // registers a GraphMLIOHandler specifically configured for SVG node styles
    graphComponent.setGraphMLIOHandler(createGraphMLIOHandler());

    // enables the default commands for (de-)serialization of the component's
    // displayed graph
    graphComponent.setFileIOEnabled(true);

    // limit the maximum zoom level to avoid out-of-memory errors when double-buffering is enabled
    graphComponent.setMaximumZoom(20);

    // opens a sample graph with several SVG nodes
    initializeGraph();
    // enables interactive editing of the displayed graph.
    initializeInputModes();
  }

  /**
   * Enables interactive editing of the displayed graph.
   */
  protected void initializeInputModes() {
    graphComponent.setInputMode(new GraphEditorInputMode());
  }

  /**
   * Opens a sample graph with several SVG nodes.
   */
  protected void initializeGraph() {
    // opens the sample graph
    URL resource = getClass().getResource("resources/sample.graphml");
    if (resource == null) {
      throw new IllegalStateException("Could not find SVG resources.");
    } else {
      try {
        graphComponent.importFromGraphML(resource);
      } catch (Exception ex) {
        throw new IllegalStateException("Could not open sample graph.", ex);
      }
    }

    // sets one of the SVG node style as the default style for new nodes
    IGraph graph = graphComponent.getGraph();
    if (graph.getNodes().size() > 0) {
      for (INode node : graph.getNodes()) {
        INodeStyle style = node.getStyle();
        if (style instanceof SVGNodeStyle) {
          SVGNodeStyle prototype = ((SVGNodeStyle) style).clone();
          graph.getNodeDefaults().setStyle(prototype);
          graph.getNodeDefaults().setSize(prototype.getPreferredSize());
          break;
        }
      }
    }
  }

  /**
   * Ensures that the sample graph is completely visible once the demo is
   * started.
   */
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Creates a new {@link GraphMLIOHandler} instance specifically configured
   * for SVG node styles.
   */
  private GraphMLIOHandler createGraphMLIOHandler() {
    GraphMLIOHandler ioHandler = new GraphMLIOHandler();

    // registers an explicit (short) namespace prefix for SVG node styles
    // to improve readability of generated GraphML files
    ioHandler.addXamlNamespaceMapping(SVG_DEMO_NAMESPACE, getClass());
    ioHandler.addNamespace(SVG_DEMO_NAMESPACE, "sns");

    // ensures that the SVG document definitions stored in SVGNodeStyle
    // instances are embedded as shared resources in GraphML files 
    SVGDocumentHandler definitionHandler = new SVGDocumentHandler();
    // resets the handler once GraphML serialization starts
    ioHandler.addWritingListener((source, args) -> definitionHandler.clear());
    // resets the handler once GraphML serialization stops
    ioHandler.addWrittenListener((source, args) -> definitionHandler.clear());
    // registers the handler that enforces the serialization as shared resources
    // since the SVG document definitions stored in SVG node styles are fairly
    // large chunks of text, this approach significantly improves readability
    // of generated GraphML files
    ioHandler.addQueryReferenceIdListener(definitionHandler);
    return ioHandler;
  }



  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(final String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new SVGNodeStyleDemo().start("SVG Display Demo - yFiles for Java (Swing)");
    });
  }
}
