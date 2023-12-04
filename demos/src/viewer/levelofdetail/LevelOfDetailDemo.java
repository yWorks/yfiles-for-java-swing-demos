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
package viewer.levelofdetail;

import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import toolkit.AbstractDemo;

import java.awt.EventQueue;

/**
 * Demonstrates how to change the level of detail for node visualizations when
 * zooming in and out.
 */
public class LevelOfDetailDemo extends AbstractDemo {
  /**
   * Initializes the demo application.
   */
  public void initialize() {
    // initialize the input mode
    createViewerInputMode();

    // loads an initial sample graph
    loadGraph();
  }

  /**
   * Loads an initial sample graph.
   */
  private void loadGraph() {
    try {
      GraphMLIOHandler reader = graphComponent.getGraphMLIOHandler();
      reader.addXamlNamespaceMapping("http://www.yworks.com/yfiles-for-java/demos/LevelOfDetail/1.0", Employee.class);
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Creates an input mode that prevents interactive modifications of the demo's
   * sample graph but allows panning, zooming, and scrolling.
   */
  private void createViewerInputMode() {
    graphComponent.setInputMode(new GraphViewerInputMode());
  }

  /**
   * Centers the demo's sample graph inside the visible area.
   */
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main( final String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new LevelOfDetailDemo().start();
    });
  }
}
