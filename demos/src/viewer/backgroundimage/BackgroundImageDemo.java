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
package viewer.backgroundimage;

import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import toolkit.AbstractDemo;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;

/**
 * Shows how to add background visualizations to a graph component.
 */
public class BackgroundImageDemo extends AbstractDemo {
  /**
   * The canvas object that stores the background visualization.
   * This can be used to remove the background image.
   */
  private ICanvasObject background;

  /**
   * The background image to display in the demo's graph component.
   */
  private BufferedImage backgroundImage;

  /**
   * Adds the corresponding Buttons to the toolbar.
   */
  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    super.configureToolBar(toolBar);

    JToggleButton imgBtn = new JToggleButton("Image");
    imgBtn.setToolTipText("Add an image to the background");
    imgBtn.addActionListener(e -> displayImage());
    imgBtn.setSelected(true);

    JToggleButton rectBtn = new JToggleButton("Rectangle");
    rectBtn.setToolTipText("Add an colored rectangle to the background");
    rectBtn.addActionListener(e -> displayRectangle());

    ButtonGroup group = new ButtonGroup();
    group.add(imgBtn);
    group.add(rectBtn);

    toolBar.addSeparator();
    toolBar.add(imgBtn);
    toolBar.add(rectBtn);
  }

  /**
   * Centers the graph in the visible area.
   */
  @Override
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }

  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    //Configure the interaction.
    initializeInputMode();

    //Create the sample graph
    loadGraph();

    //Display the image in the background.
    loadImage();
    displayImage();

    graphComponent.getGraph().setUndoEngineEnabled(true);
  }

  /**
   * Removes the current background and adds the image.
   */
  private void displayImage() {
    if (background != null) {
      background.remove();
    }
    background = graphComponent.getBackgroundGroup().addChild(
            new ImageVisualCreator(backgroundImage),
            ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE
    );
  }

  /**
   * Removes the current background and adds the rectangle.
   */
  private void displayRectangle() {
    if (background != null) {
      background.remove();
    }
    background = graphComponent.getBackgroundGroup().addChild(
            new RectangleVisualCreator(),
            ICanvasObjectDescriptor.ALWAYS_DIRTY_INSTANCE
    );
  }

  /**
   * Loads the initial sample graph.
   */
  private void loadGraph() {
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/sample.graphml"));
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Loads the image to be displayed by this creator's visuals.
   */
  private void loadImage() {
    try {
      backgroundImage = ImageIO.read(getClass().getResource("resources/ylogo.png"));
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Initializes the input mode for the demo.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.setGroupingOperationsAllowed(true);
    graphComponent.setInputMode(geim);
  }

  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new BackgroundImageDemo().start();
    });
  }
}