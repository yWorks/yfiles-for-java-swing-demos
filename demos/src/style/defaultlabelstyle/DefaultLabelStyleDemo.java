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
package style.defaultlabelstyle;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.SmartEdgeLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.LabelShape;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.TextTrimming;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.VerticalAlignment;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;
import toolkit.Palette;
import toolkit.Themes;

import javax.swing.JFrame;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;

/**
 * Shows the most important configuration options for the built-in {@link DefaultLabelStyle} class.
 */
public class DefaultLabelStyleDemo extends AbstractDemo {
  /**
   * Creates and configures a node label style.
   *
   * @param palette  The palette to use for the style's fills and pen.
   * @param shape    The label shape for the background.
   * @param insets   Insets to account for special background shapes.
   * @param wrapping Text wrapping defining how text of the label is wrapped.
   * @param clipText Determines whether overflowing text should be clipped.
   * @param trimming Text trimming defining how text of the label is trimmed.
   */
  private static ILabelStyle createNodeLabelStyle(
      Palette palette,
      LabelShape shape,
      double insets,
      TextWrapping wrapping,
      boolean clipText,
      TextTrimming trimming
  ) {
    DefaultLabelStyle style = new DefaultLabelStyle();
    style.setShape(shape);
    style.setBackgroundPaint(palette.getNodeLabelBackgroundPaint());
    style.setBackgroundPen(null);
    style.setInsets(new InsetsD(insets));
    style.setFont(FONT_14);
    style.setTextPaint(palette.getTextPaint());
    style.setTextWrapping(wrapping);
    style.setVerticalTextAlignment(VerticalAlignment.CENTER);
    style.setTextAlignment(TextAlignment.CENTER);
    style.setTextClippingEnabled(clipText);
    style.setTextTrimming(trimming);
    style.setUsingFractionalFontMetricsEnabled(true);
    return style;
  }

  /**
   * Creates and configures an edge label style.
   *
   * @param palette           The palette to use for the style's fills and pens.
   * @param shape             The label shape for the background.
   * @param insets            Insets to account for special background shapes.
   * @param font              The font for the label text.
   * @param verticalAlignment The vertical text alignment.
   * @param textAlignment     The horizontaly text alignment.
   */
  private static ILabelStyle createEdgeLabelStyle(
      Palette palette,
      LabelShape shape,
      double insets,
      Font font,
      VerticalAlignment verticalAlignment,
      TextAlignment textAlignment) {

    DefaultLabelStyle style = new DefaultLabelStyle();
    style.setShape(shape);
    style.setBackgroundPaint(palette.getEdgeLabelBackgroundPaint());
    style.setBackgroundPen(null);
    style.setInsets(new InsetsD(insets));
    style.setFont(font);
    style.setTextWrapping(TextWrapping.NO_WRAP);
    style.setVerticalTextAlignment(verticalAlignment);
    style.setTextAlignment(textAlignment);
    style.setUsingFractionalFontMetricsEnabled(true);
    return style;
  }


  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    super.initialize();
    initializeInteraction();

    // Create node and edge labels using different label style settings
    createSampleNodeLabels(graphComponent.getGraph());
    createSampleEdgeLabels(graphComponent.getGraph());
  }

  /**
   * Creates some sample node labels with different background styles.
   *
   * @param graph The graph to add node labels to.
   */
  private static void createSampleNodeLabels(IGraph graph) {
    INode n1 = graph.createNode(new RectD(-25, -100, 50, 200), DemoStyles.createDemoNodeStyle(Themes.PALETTE_ORANGE));
    // Add sample node labels to the first node, distributed evenly on the side and with different
    // background shapes
    graph.addLabel(
        n1,
        "Rectangle Node Label",
        createNodeLabelParameter(new PointD(1, 0.2), new PointD(100, 0)),
        createNodeLabelStyle(LabelShape.RECTANGLE, 0)
    );
    graph.addLabel(
        n1,
        "Rounded Node Label",
        createNodeLabelParameter(new PointD(1, 0.4), new PointD(100, 0)),
        createNodeLabelStyle(LabelShape.ROUND_RECTANGLE, 2)
    );
    graph.addLabel(
        n1,
        "Hexagon Node Label",
        createNodeLabelParameter(new PointD(1, 0.6), new PointD(100, 0)),
        //The hexagon background needs slightly larger insets at the sides
        createNodeLabelStyle(LabelShape.HEXAGON, 0)
    );
    graph.addLabel(
        n1,
        "Pill Node Label",
        createNodeLabelParameter(new PointD(1, 0.8), new PointD(100, 0)),
        createNodeLabelStyle(LabelShape.PILL, 0)
    );

    // Create two more nodes, the bottom one and the right one
    INode n2 = graph.createNode(new RectD(275, 600, 50, 50), DemoStyles.createDemoNodeStyle(Themes.PALETTE14));
    INode n3 = graph.createNode(new RectD(525, -100, 50, 200), DemoStyles.createDemoNodeStyle(Themes.PALETTE12));

    // Add three node labels to the right node showing different text clipping and text wrapping options
    graph.addLabel(
        n3,
        "Wrapped and clipped label text",
        createNodeLabelParameter(new PointD(1, 0.2), new PointD(120, 0)),
        createNodeLabelStyle(Themes.PALETTE12, LabelShape.PILL, 0, TextWrapping.WRAP, true, TextTrimming.WORD_ELLIPSIS),
        new SizeD(140, 25)
    );

    graph.addLabel(
        n3,
        "Un-wrapped but clipped label text",
        createNodeLabelParameter(new PointD(1, 0.5), new PointD(120, 0)),
        createNodeLabelStyle(Themes.PALETTE12, LabelShape.PILL, 0, TextWrapping.NO_WRAP, true, TextTrimming.NONE),
        new SizeD(140, 25)
    );

    // For the last label, disable text clipping
    graph.addLabel(
        n3,
        "Un-wrapped and un-clipped label text",
        createNodeLabelParameter(new PointD(1, 0.8), new PointD(120, 0)),
        createNodeLabelStyle(Themes.PALETTE12, LabelShape.PILL, 0, TextWrapping.NO_WRAP, false, TextTrimming.NONE),
        new SizeD(140, 25)
    );
  }

  /**
   * Creates some sample edge labels with different background styles.
   *
   * @param graph The graph to add edge labels to.
   */
  private static void createSampleEdgeLabels(IGraph graph) {
    SmartEdgeLabelModel edgeLabelModel = new SmartEdgeLabelModel();
    edgeLabelModel.setAngle(Math.PI / 2);

    graph.getEdgeDefaults().getLabelDefaults().setLayoutParameter(edgeLabelModel.createDefaultParameter());
    graph.getEdgeDefaults().setStyle(DemoStyles.createDemoEdgeStyle(Themes.PALETTE12, true));

    IEdge edge1 = graph.createEdge(graph.getNodes().getItem(0), graph.getNodes().getItem(1));
    graph.addBend(edge1, new PointD(0, 400));

    // Add sample edge labels on the first edge segment, distributed evenly on the path and with different
    // background shapes
    graph.addLabel(
        edge1,
        "Rectangle Edge Label\n" + "A second line of sample text.",
        edgeLabelModel.createParameterFromSource(0, 0, 0.2),
        createEdgeLabelStyle(LabelShape.RECTANGLE, 0)
    );
    graph.addLabel(
        edge1,
        "Rounded Edge Label\n" + "A second line of sample text.",
        edgeLabelModel.createParameterFromSource(0, 0, 0.4),
        // For the round rectangle, we can manually increase the padding around the text
        // using the insets property. By default, this would be just as tight as for
        // LabelShape.RECTANGLE, but in order to make sure that text is less likely to touch
        // the stroke of the round rectangle, we add 2 extra pixels.
        createEdgeLabelStyle(LabelShape.ROUND_RECTANGLE, 2)
    );
    graph.addLabel(
        edge1,
        "Hexagon Edge Label\n" + "A second line of sample text.",
        edgeLabelModel.createParameterFromSource(0, 0, 0.6),
        createEdgeLabelStyle(LabelShape.HEXAGON, 0)
    );
    graph.addLabel(
        edge1,
        "Pill Edge Label\n" + "A second line of sample text.",
        edgeLabelModel.createParameterFromSource(0, 0, 0.8),
        createEdgeLabelStyle(LabelShape.PILL, 0)
    );

    // Add rotated edge labels on the second edge segment, distributed evenly and with different background shapes
    graph.addLabel(
        edge1,
        "Rotated Rectangle",
        edgeLabelModel.createParameterFromSource(1, 0, 0.2),
        createEdgeLabelStyle(Themes.PALETTE15, LabelShape.RECTANGLE, 0, FONT_16, VerticalAlignment.CENTER, TextAlignment.CENTER
        )
    );
    graph.addLabel(
        edge1,
        "Rotated Rounded Rectangle",
        edgeLabelModel.createParameterFromSource(1, 0, 0.4),
        createEdgeLabelStyle(Themes.PALETTE15, LabelShape.ROUND_RECTANGLE,
            // For the round rectangle, we can manually increase the padding around the text
            // using the insets property. By default, this would be just as tight as for
            // LabelShape.RECTANGLE, but in order to make sure that text is less likely to touch
            // the stroke of the round rectangle, we add 2 extra pixels.
            2, FONT_16, VerticalAlignment.CENTER, TextAlignment.CENTER
        )
    );
    graph.addLabel(
        edge1,
        "Rotated Hexagon",
        edgeLabelModel.createParameterFromSource(1, 0, 0.6),
        createEdgeLabelStyle(Themes.PALETTE15, LabelShape.HEXAGON, 0, FONT_16, VerticalAlignment.CENTER, TextAlignment.CENTER)
    );
    graph.addLabel(
        edge1,
        "Rotated Pill",
        edgeLabelModel.createParameterFromSource(1, 0, 0.8),
        createEdgeLabelStyle(Themes.PALETTE15, LabelShape.PILL, 0, FONT_16, VerticalAlignment.CENTER, TextAlignment.CENTER
        )
    );

    IEdge edge2 = graph.createEdge(graph.getNodes().getItem(2), graph.getNodes().getItem(1));
    graph.addBend(edge2, new PointD(550, 625));

    // Add larger edge labels with different vertical and horizontal text alignment settings to the second edge
    graph.addLabel(
        edge2,
        "Edge Label\nwith vertical text\nalignment at bottom",
        edgeLabelModel.createParameterFromSource(0, -20, 0.4),
        createEdgeLabelStyle(Themes.PALETTE12, LabelShape.ROUND_RECTANGLE, 2, FONT_12_BOLD, VerticalAlignment.BOTTOM, TextAlignment.CENTER),
        // Explicitly specify a preferred size for the label that is much larger than needed for the label's text
        new SizeD(150, 120)
    );
    graph.addLabel(
        edge2,
        "Edge Label\nwith vertical text\nalignment at top",
        edgeLabelModel.createParameterFromSource(0, 20, 0.4),
        createEdgeLabelStyle(Themes.PALETTE12, LabelShape.ROUND_RECTANGLE, 2, FONT_12_BOLD, VerticalAlignment.TOP, TextAlignment.CENTER),
        // Explicitly specify a preferred size for the label that is much larger than needed for the label's text
        new SizeD(150, 120)
    );
    graph.addLabel(
        edge2,
        "Edge Label\nwith vertical center\nand horizontal left\ntext alignment",
        edgeLabelModel.createParameterFromSource(0, 20, 0.7),
        createEdgeLabelStyle(Themes.PALETTE12, LabelShape.ROUND_RECTANGLE, 2, FONT_12_BOLD, VerticalAlignment.CENTER, TextAlignment.LEFT),
        // Explicitly specify a preferred size for the label that is much larger than needed for the label's text
        new SizeD(150, 120)
    );
    graph.addLabel(
        edge2,
        "Edge Label\nwith vertical bottom\nand horizontal right\ntext alignment",
        edgeLabelModel.createParameterFromSource(0, -20, 0.7),
        createEdgeLabelStyle(Themes.PALETTE12, LabelShape.ROUND_RECTANGLE, 2, FONT_12_BOLD, VerticalAlignment.BOTTOM, TextAlignment.RIGHT),
        // Explicitly specify a preferred size for the label that is much larger than needed for the label's text
        new SizeD(150, 120)
    );
  }

  /**
   * Creates and configures a node label style.
   *
   * @param shape  The label shape for the background.
   * @param insets Insets to account for special background shapes.
   */
  private static ILabelStyle createNodeLabelStyle(LabelShape shape, double insets) {
    return createNodeLabelStyle(Themes.PALETTE13, shape, insets, TextWrapping.NO_WRAP, true, TextTrimming.NONE);
  }

  /**
   * Creates and configures an edge label style.
   *
   * @param shape  The label shape for the background.
   * @param insets Insets to account for special background shapes.
   */
  private static ILabelStyle createEdgeLabelStyle(LabelShape shape, double insets) {
    return createEdgeLabelStyle(Themes.PALETTE13, shape, insets, FONT_12, VerticalAlignment.CENTER, TextAlignment.CENTER);
  }

  /**
   * Creates a node label at the specified vertical ratio.
   *
   * @param layoutRatio  The ratio that describes the point on the node's layout relative to its upper-left corner.
   * @param layoutOffset The absolute offset to apply to the point on the node after the ratio has been determined.
   */
  private static ILabelModelParameter createNodeLabelParameter(PointD layoutRatio, PointD layoutOffset) {
    return FreeNodeLabelModel.INSTANCE.createParameter(layoutRatio, layoutOffset, new PointD(0.5, 0.5));
  }

  static final Font FONT_12 = new Font("Dialog", Font.PLAIN, 12);
  static final Font FONT_12_BOLD = new Font("Dialog", Font.BOLD, 12);
  static final Font FONT_14 = new Font("Dialog", Font.PLAIN, 14);
  static final Font FONT_16 = new Font("Dialog", Font.PLAIN, 16);

  /**
   * Restricts user interaction to selecting, panning, and zooming.
   */
  private void initializeInteraction() {
    graphComponent.setInputMode(new GraphViewerInputMode());
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
   * Maximizes the window.
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
      new DefaultLabelStyleDemo().start();
    });
  }
}
