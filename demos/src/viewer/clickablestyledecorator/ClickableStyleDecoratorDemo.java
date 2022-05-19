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
package viewer.clickablestyledecorator;

import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import toolkit.AbstractDemo;

import java.awt.EventQueue;
import java.util.Locale;

/**
 * Shows how to handle mouse clicks in specific areas of a node's visualization.
 */
public class ClickableStyleDecoratorDemo extends AbstractDemo {

  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    // initialize graph interaction
    initializeInputMode();

    // loads an initial sample graph
    loadGraph();

    graphComponent.getGraph().setUndoEngineEnabled(true);
  }

  /**
   * Loads an initial sample graph.
   */
  private void loadGraph() {
    try {
      GraphMLIOHandler reader = graphComponent.getGraphMLIOHandler();
      reader.addXamlNamespaceMapping(
              "http://www.yworks.com/yfiles-for-java/demos/ClickableStyleDecorator/1.0",
              NodeStyleDecorator.class);
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Centers the graph in the visible area.
   */
  @Override
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Configures user interaction.
   */
  private void initializeInputMode() {
    graphComponent.setInputMode(new GraphEditorInputMode());
  }

  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new ClickableStyleDecoratorDemo().start();
    });
  }
}
