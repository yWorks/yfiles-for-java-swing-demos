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
package deploy.obfuscation;

import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.utils.Obfuscation;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.URL;

/**
 * Sample application that demonstrates how to obfuscate a yFiles for Java (Swing) application.
 * <p>
 * For an explanation of the annotation mechanism demonstrated here, have a look at the yGuard manual at
 * </p><p>
 * <a href="https://yworks.github.io/yGuard/task_documentation/#controlling-obfuscation-exclusion-with-annotations">
 * https://yworks.github.io/yGuard/task_documentation/#controlling-obfuscation-exclusion-with-annotations
 * </a>
 * </p><p>
 * Information regarding the build and obfuscation process itself, yGuard and the used mechanisms in this demo
 * can be found in the description in the build.xml file.
 * </p>
 */
public class ObfuscationDemo extends AbstractDemo {
  /**
   * A field exists solely for the purpose of demonstrating how to exclude
   * members from obfuscation using yGuard's <code>Obfuscation</code> annotation.
   */
  @Obfuscation( exclude = true )
  private BackgroundRectangle background;

  /**
   * A field that is only used in code and thus can be obfuscated.
   * This field exists solely for demonstration purposes.
   */
  private GraphEditorInputMode inputMode;
  /**
   * A field that is only used in code and thus can be obfuscated.
   * This field exists solely for demonstration purposes.
   */
  private INodeStyle nodeStyle;


  @Override
  public void initialize() {
    initializeBackground();
    initializeDefaults();
    initializeInputModes();

    loadSampleGraph();
  }

  /**
   * Adds a blue rectangle that visualizes the content rectangle
   * of the graph component in the component's background.
   */
  private void initializeBackground() {
    ICanvasObjectGroup bg = graphComponent.getRootGroup().addGroup();
    bg.toBack();
    background = new BackgroundRectangle();
    bg.addChild(background, ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE);
  }

  /**
   * Initializes the default node style.
   * This is a private method and thus may be obfuscated.
   */
  private void initializeDefaults() {
    DemoStyles.initDemoStyles(graphComponent.getGraph());
    nodeStyle = graphComponent.getGraph().getNodeDefaults().getStyle();
  }

  /**
   * Initializes the {@link com.yworks.yfiles.view.input.GraphEditorInputMode}.
   * This is a private method and thus may be obfuscated.
   */
  private void initializeInputModes() {
    inputMode = new GraphEditorInputMode();
    graphComponent.setInputMode(inputMode);
  }

  /**
   * Loads the sample graph. This is a private method and thus can be obfuscated.
   */
  private void loadSampleGraph() {
    URL resource = getClass().getResource("resources/example.graphml");
    if (resource != null) {
      try {
        graphComponent.importFromGraphML(resource);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Centers the graph in the graph component when it is initially displayed.
   */
  @Override
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      (new ObfuscationDemo()).start();
    });
  }
}
