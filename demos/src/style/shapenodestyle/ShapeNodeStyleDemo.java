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
package style.shapenodestyle;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.LabelShape;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.VerticalAlignment;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import toolkit.AbstractDemo;
import toolkit.Palette;
import toolkit.Themes;

import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Paint;
import java.util.Arrays;
import java.util.stream.Stream;

public class ShapeNodeStyleDemo extends AbstractDemo {
  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    super.initialize();
    initializeStyleDefaults();
    createSampleNodes();
    initializeInputMode();
  }

  private void initializeStyleDefaults() {
    IGraph graph = graphComponent.getGraph();

    // All node labels share the same style and label model parameter
    Palette labelPalette = Themes.PALETTE58;

    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setShape(LabelShape.ROUND_RECTANGLE);
    labelStyle.setBackgroundPaint(labelPalette.getNodeLabelBackgroundPaint());
    labelStyle.setTextPaint(labelPalette.getTextPaint());
    labelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    labelStyle.setTextAlignment(TextAlignment.CENTER);
    labelStyle.setInsets(new InsetsD(2, 4, 1, 4));
    labelStyle.setFont(new Font("Arial", Font.PLAIN, 18));
    graph.getNodeDefaults().getLabelDefaults().setStyle(labelStyle);

    graph.getNodeDefaults().getLabelDefaults().setLayoutParameter(FreeNodeLabelModel.INSTANCE.createParameter(
        new PointD(0.5, 0),
        new PointD(0, -50),
        new PointD(0.5, 0.5)
    ));

    // Edges share the same style as well, they are not important in this demo
    Palette edgePalette = Themes.PALETTE56;

    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(new Pen(edgePalette.getOutlinePaint(), 1.5));
    edgeStyle.setTargetArrow(new Arrow(ArrowType.TRIANGLE, (Color) edgePalette.getOutlinePaint()));

    graph.getEdgeDefaults().setStyle(edgeStyle);
  }

  /**
   * Creates a sample graph with all the shapes that are available for {@link ShapeNodeStyle}.
   */
  private void createSampleNodes() {
    // Create the various shape samples
    ShapeNodeShape[] rectangularShapes = {
        ShapeNodeShape.RECTANGLE, ShapeNodeShape.ROUND_RECTANGLE, ShapeNodeShape.PILL
    };
    ShapeNodeShape[] ellipticalShapes = { ShapeNodeShape.ELLIPSE };
    ShapeNodeShape[] skewedShapes = {
        ShapeNodeShape.DIAMOND, ShapeNodeShape.SHEARED_RECTANGLE, ShapeNodeShape.SHEARED_RECTANGLE2,
        ShapeNodeShape.TRAPEZ, ShapeNodeShape.TRAPEZ2
    };
    ShapeNodeShape[] arrowShapes = { ShapeNodeShape.FAT_ARROW, ShapeNodeShape.FAT_ARROW2 };
    ShapeNodeShape[] polygonalShapes = {
        ShapeNodeShape.TRIANGLE, ShapeNodeShape.TRIANGLE2, ShapeNodeShape.HEXAGON, ShapeNodeShape.HEXAGON2,
        ShapeNodeShape.OCTAGON
    };
    ShapeNodeShape[] starShapes = {
        ShapeNodeShape.STAR5, ShapeNodeShape.STAR5_UP, ShapeNodeShape.STAR6, ShapeNodeShape.STAR8
    };

    ShapeNodeShape[] ellipticalAndArrowShapes = Stream
        .concat(Arrays.stream(ellipticalShapes), Arrays.stream(arrowShapes))
        .toArray(ShapeNodeShape[]::new);

    createShapeSamples(rectangularShapes, 0);
    createShapeSamples(ellipticalAndArrowShapes, 1);
    createShapeSamples(skewedShapes, 2);
    createShapeSamples(polygonalShapes, 3);
    createShapeSamples(starShapes, 4);
  }

  private void createShapeSamples(ShapeNodeShape[] shapes, int column) {
    IGraph graph = graphComponent.getGraph();

    final int size1 = 45;
    final int size2 = 90;

    // Define colors for distinguishing the three different aspect ratios used below
    Paint fill1 = Themes.PALETTE54.getBackgroundPaint();
    Paint fill2 = Themes.PALETTE56.getBackgroundPaint();
    Paint fill3 = Themes.PALETTE510.getBackgroundPaint();
    Paint penPaint1 = Themes.PALETTE54.getOutlinePaint();
    Paint penPaint2 = Themes.PALETTE56.getOutlinePaint();
    Paint penPaint3 = Themes.PALETTE510.getOutlinePaint();

    for (int i = 0; i < shapes.length; i++) {
      // Create a green node with aspect ratio 1:1
      ShapeNodeStyle style1 = new ShapeNodeStyle();
      style1.setShape(shapes[i]);
      style1.setPaint(fill1);
      style1.setPen(new Pen(penPaint1, 1));
      RectD bounds1 = new RectD(column * 350 - size1 / 2d, i * 200 - size1 / 2d, size1, size1);
      INode n1 = graph.createNode(bounds1, style1);

      // Create a blue node with aspect ratio 2:1
      ShapeNodeStyle style2 = new ShapeNodeStyle();
      style2.setShape(shapes[i]);
      style2.setPaint(fill2);
      style2.setPen(new Pen(penPaint2, 1));
      RectD bounds2 = new RectD(column * 350 + 100 - size2 / 2d, i * 200 - size1 / 2d, size2, size1);
      INode n2 = graph.createNode(bounds2, style2);
      graph.addLabel(n2, shapes[i].name());

      // Create a yellow node with aspect ratio 1:2
      ShapeNodeStyle style3 = new ShapeNodeStyle();
      style3.setShape(shapes[i]);
      style3.setPaint(fill3);
      style3.setPen(new Pen(penPaint3, 1));
      RectD bounds3 = new RectD(column * 350 + 200 - size1 / 2d, i * 200 - size2 / 2d, size1, size2);
      INode n3 = graph.createNode(bounds3, style3);

      graph.createEdge(n1, n2);
      graph.createEdge(n2, n3);
    }
  }

  /**
   * Restricts user interaction.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.setGroupingOperationsAllowed(false);
    geim.setMarqueeSelectableItems(GraphItemTypes.NODE);
    geim.setCreateBendAllowed(false);
    geim.setCreateEdgeAllowed(false);
    geim.setCreateNodeAllowed(false);
    geim.setAddLabelAllowed(false);
    geim.setEditLabelAllowed(false);
    geim.setDeletableItems(GraphItemTypes.NONE);
    geim.setMovableItems(GraphItemTypes.NODE);
    graphComponent.setInputMode(geim);
  }

  /**
   * Center the sample graph in the visible area.
   */
  @Override
  public void onVisible() {
    super.onVisible();
    EventQueue.invokeLater(graphComponent::fitGraphBounds);
  }

  /**
   * Adds a button to toggle the intrinsic aspect ratio property to the toolbar.
   */
  @Override
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(createKeepIntrinsicRatioButton());
  }

  private JToggleButton createKeepIntrinsicRatioButton() {
    JToggleButton button = new JToggleButton();
    button.setText("Keep Intrinsic Aspect Ratio");
    button.setToolTipText("Keep the intrinsic aspect ratio of the shape.");
    button.addActionListener(e -> {
      boolean keepAspectRatio = ((JToggleButton) e.getSource()).isSelected();
      graphComponent.getGraph().getNodes().stream()
          .filter(node -> node.getStyle() instanceof ShapeNodeStyle)
          .forEach(node -> ((ShapeNodeStyle) node.getStyle()).setKeepingIntrinsicAspectRatioEnabled(keepAspectRatio));
      graphComponent.repaint();
    });
    return button;
  }

  /**
   * Enables full screen mode.
   */
  @Override
  protected JFrame createFrame(String title) {
    JFrame frame = super.createFrame(title);
    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    return frame;
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new ShapeNodeStyleDemo().start();
    });
  }
}
