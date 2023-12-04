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
package style.textwrapping;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdgeDefaults;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.labelmodels.EdgePathLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.TextWrappingShape;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.TextTrimming;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.VerticalAlignment;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;

import javax.swing.JFrame;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;

/**
 * This demo shows different wrapping, trimming and clipping options for labels using a DefaultLabelStyle.
 */
public class TextWrappingDemo extends AbstractDemo {

  /**
   * Creates a {@link DefaultLabelStyle} with the provided clipping, wrapping and trimming.
   */
  private DefaultLabelStyle createLabelStyle(boolean clip, TextWrapping textWrapping, TextTrimming textTrimming) {
    DefaultLabelStyle style = new DefaultLabelStyle();
    style.setUsingFractionalFontMetricsEnabled(true);
    style.setTextClippingEnabled(clip);
    style.setTextWrapping(textWrapping);
    style.setTextTrimming(textTrimming);
    style.setVerticalTextAlignment(VerticalAlignment.CENTER);
    style.setFont(new Font("Dialog", Font.PLAIN, 15));
    return style;
  }

  /**
   * Creates a {@link DefaultLabelStyle} with the provided trimming, wrapping shape and insets.
   */
  private DefaultLabelStyle createShapeWrappingLabelStyle(TextTrimming textTrimming, TextWrappingShape wrappingShape,
                                                          InsetsD insets) {
    // always enable trimming and wrapping
    DefaultLabelStyle style = createLabelStyle(true, TextWrapping.WRAP, textTrimming);
    style.setTextWrappingShape(wrappingShape);
    style.setTextWrappingPadding(5);
    style.setInsets(insets);
    return style;
  }

  /**
   * Initialize the graph with several nodes with labels using different text wrapping settings.
   */
  private void initializeGraph() {
    // label model and style for the description labels north of the nodes
    ExteriorLabelModel northLabelModel = new ExteriorLabelModel();
    northLabelModel.setInsets(new InsetsD(10));
    ILabelModelParameter northParameter = northLabelModel.createParameter(ExteriorLabelModel.Position.NORTH);
    DefaultLabelStyle northLabelStyle = new DefaultLabelStyle();
    northLabelStyle.setTextAlignment(TextAlignment.CENTER);
    IGraph graph = graphComponent.getGraph();
    RectangleNodeStyle defaultNodeStyle = (RectangleNodeStyle) graph.getNodeDefaults().getStyle();

    // create nodes
    INode node1 = graph.createNode(new RectD(0, -450, 190, 200));
    INode node2 = graph.createNode(new RectD(0, -150, 190, 200));
    INode node3 = graph.createNode(new RectD(0, 150, 190, 200));
    INode node4 = graph.createNode(new RectD(250, -150, 190, 200));
    INode node5 = graph.createNode(new RectD(250, 250, 190, 200));
    INode node6 = graph.createNode(new RectD(500, -150, 190, 200));
    INode node7 = graph.createNode(new RectD(500, 150, 190, 200));
    INode node8 = graph.createNode(new RectD(750, -150, 190, 200),
        createShapeNodeStyle(ShapeNodeShape.HEXAGON, defaultNodeStyle));
    INode node9 = graph.createNode(new RectD(750, 150, 190, 200),
        createShapeNodeStyle(ShapeNodeShape.TRIANGLE2, defaultNodeStyle));
    INode node10 = graph.createNode(new RectD(1000, -150, 190, 200),
        createShapeNodeStyle(ShapeNodeShape.OCTAGON, defaultNodeStyle));
    INode node11 = graph.createNode(new RectD(1000, 150, 190, 200),
        createShapeNodeStyle(ShapeNodeShape.ELLIPSE, defaultNodeStyle));

    // use a label model that stretches the label over the full node layout, with small insets
    InteriorStretchLabelModel centerLabelModel = new InteriorStretchLabelModel();
    centerLabelModel.setInsets(new InsetsD(5));
    ILabelModelParameter centerParameter = centerLabelModel.createParameter(InteriorStretchLabelModel.Position.CENTER);

    // A label that does not wrap or clip at all. As the ClipText property is set to false and no wrapping and
    // trimming is used, the label extends the bounds of its owner node.
    DefaultLabelStyle noWrapNoTrimNoClipStyle = createLabelStyle(false, TextWrapping.NO_WRAP, TextTrimming.NONE);
    graph.addLabel(node1, Text, centerParameter, noWrapNoTrimNoClipStyle);
    graph.addLabel(node1, "No Wrapping\nNo Trimming\nNo Clipping", northParameter, northLabelStyle);

    // A label that does not wrap or clip at all. By default, ClipText is true, so it is clipped at the given bounds.
    DefaultLabelStyle noWrapNoTrimStyle = createLabelStyle(true, TextWrapping.NO_WRAP, TextTrimming.NONE);
    graph.addLabel(node2, Text, centerParameter, noWrapNoTrimStyle);
    graph.addLabel(node2, "No Wrapping\nNo Trimming\nClipping", northParameter, northLabelStyle);

    // A label that is not wrapped but trimmed with ellipsis at the given bounds if there is not enough space.
    DefaultLabelStyle noWrapCharTrimStyle = createLabelStyle(true, TextWrapping.NO_WRAP,
        TextTrimming.CHARACTER_ELLIPSIS);
    graph.addLabel(node3, Text, centerParameter, noWrapCharTrimStyle);
    graph.addLabel(node3, "No Wrapping\nCharacter Trimming\nClipping", northParameter, northLabelStyle);

    // A label that is wrapped at word boundaries but not trimmed. If there is not enough space, the wrapped lines
    // are placed according to the chosen vertical alignment. With 'VerticalAlignment.center' the top and bottom part
    // of the label are clipped.
    DefaultLabelStyle wrapNoTrimStyle = createLabelStyle(true, TextWrapping.WRAP, TextTrimming.NONE);
    graph.addLabel(node4, Text, centerParameter, wrapNoTrimStyle);
    graph.addLabel(node4, "Wrapping\nNoTrimming\nClipping", northParameter, northLabelStyle);

    // A label that is wrapped at word boundaries but not trimmed or clipped.
    // Due to the label exceeding the node bounds vertically, we place the description label even further north
    ExteriorLabelModel furtherNorthLabelModel = new ExteriorLabelModel();
    furtherNorthLabelModel.setInsets(new InsetsD(110));
    ILabelModelParameter furtherNorthParameter = furtherNorthLabelModel.createParameter(
        ExteriorLabelModel.Position.NORTH);
    DefaultLabelStyle wrapNoTrimNoClipStyle = createLabelStyle(false, TextWrapping.WRAP, TextTrimming.NONE);
    graph.addLabel(node5, Text, centerParameter, wrapNoTrimNoClipStyle);
    graph.addLabel(node5, "Wrapping\nNoTrimming\nNoClipping", furtherNorthParameter, northLabelStyle);

    // A label that is wrapped and trimmed at characters at the end.
    DefaultLabelStyle wrapCharTrimStyle = createLabelStyle(true, TextWrapping.WRAP, TextTrimming.CHARACTER_ELLIPSIS);
    graph.addLabel(node7, Text, centerParameter, wrapCharTrimStyle);
    graph.addLabel(node7, "Wrapping\nCharacter Trimming\nClipping", northParameter, northLabelStyle);

    // A label that is wrapped and trimmed at word boundaries.
    DefaultLabelStyle wrapWordTrimStyle = createLabelStyle(true, TextWrapping.WRAP, TextTrimming.WORD_ELLIPSIS);
    graph.addLabel(node6, Text, centerParameter, wrapWordTrimStyle);
    graph.addLabel(node6, "Wrapping\nWord Trimming\nClipping", northParameter, northLabelStyle);

    // A label that is wrapped but uses a hexagon shape to fit the text inside.
    // The TextWrappingShape can be combined with the TextWrappingPadding that keeps empty paddings inside this shape.
    DefaultLabelStyle wrapHexagonShapeStyle = createShapeWrappingLabelStyle(TextTrimming.WORD_ELLIPSIS,
        TextWrappingShape.HEXAGON, InsetsD.EMPTY);
    graph.addLabel(node8, Text, centerParameter, wrapHexagonShapeStyle);
    graph.addLabel(node8, "Wrapping\nat Hexagon Shape\nWord Trimming", northParameter, northLabelStyle);

    // A label that is wrapped inside a triangular shape.
    DefaultLabelStyle wrapTriangleShapeStyle = createShapeWrappingLabelStyle(TextTrimming.CHARACTER_ELLIPSIS,
        TextWrappingShape.TRIANGLE2, InsetsD.EMPTY);
    graph.addLabel(node9, Text, centerParameter, wrapTriangleShapeStyle);
    graph.addLabel(node9, "Wrapping\nat Triangle Shape\nCharacterTrimming", northParameter, northLabelStyle);

    // A label that is wrapped inside an elliptic shape.
    // In addition to the TextWrappingPadding some insets are defined for the top and bottom side
    // to keep the upper and lower part of the ellipse empty.
    InsetsD topBottomInsets = new InsetsD(40, 0, 40, 0);
    DefaultLabelStyle wrapEllipseShapeStyle = createShapeWrappingLabelStyle(TextTrimming.CHARACTER_ELLIPSIS,
        TextWrappingShape.ELLIPSE, topBottomInsets);
    graph.addLabel(node11, Text, centerParameter, wrapEllipseShapeStyle);
    graph.addLabel(node11, "Wrapping\nat Ellipse Shape\nwith Top/Bottom Insets\nCharacter Trimming", northParameter,
        northLabelStyle);

    // A label that is wrapped inside an octagon shape.
    // In addition to the TextWrappingPadding some insets are defined for the top and bottom side
    // to keep the upper and lower part of the octagon empty.
    DefaultLabelStyle wrapOctagonShapeStyle = createShapeWrappingLabelStyle(TextTrimming.WORD_ELLIPSIS,
        TextWrappingShape.OCTAGON, topBottomInsets);
    graph.addLabel(node10, Text, centerParameter, wrapOctagonShapeStyle);
    graph.addLabel(node10, "Wrapping\nat Octagon Shape\nwith Top/Bottom Insets\nWord Trimming", northParameter,
        northLabelStyle);
  }

  /**
   * Creates a {@link ShapeNodeShape} with the given shape that uses the same paint and pen as the given
   * {@link RectangleNodeStyle}.
   */
  private INodeStyle createShapeNodeStyle(ShapeNodeShape shape, RectangleNodeStyle defaultNodeStyle) {
    ShapeNodeStyle style = new ShapeNodeStyle();
    style.setShape(shape);
    style.setPaint(defaultNodeStyle.getPaint());
    style.setPen(defaultNodeStyle.getPen());
    return style;
  }

  // a long multi-line text to demonstrate the clipping/wrapping/trimming behavior
  private static final String Text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n" + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.\n\n" + "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.\n" + "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    super.initialize();
    // initialize the settings used when creating new graph items
    initializeGraphDefaults();

    // initialize the graph
    initializeGraph();

    // initialize the input mode
    initializeInputMode();
  }

  /**
   * Sets up defaults used when creating new graph items.
   */
  private void initializeGraphDefaults() {
    DemoStyles.initDemoStyles(graphComponent.getGraph());

    INodeDefaults nodeDefaults = graphComponent.getGraph().getNodeDefaults();
    nodeDefaults.setSize(new SizeD(100, 80));

    // Use a label model that stretches the label over the full node layout, with small insets. The label style
    // is responsible for drawing the label in the given space. Depending on its implementation, it can either
    // ignore the given space, clip the label at the width or wrapping the text.
    // See the initializeGraph function where labels are added with different style options.
    InteriorStretchLabelModel centerLabelModel = new InteriorStretchLabelModel();
    centerLabelModel.setInsets(new InsetsD(5));
    nodeDefaults.getLabelDefaults().setLayoutParameter(
        centerLabelModel.createParameter(InteriorStretchLabelModel.Position.CENTER));

    IEdgeDefaults edgeDefaults = graphComponent.getGraph().getEdgeDefaults();
    edgeDefaults.getLabelDefaults().setLayoutParameter(
        new EdgePathLabelModel(5, 0, 0, true, EdgeSides.BELOW_EDGE).createRatioParameter(0.5, EdgeSides.BELOW_EDGE));
  }

  /**
   * Allow all kind of user interactions including node resizing.
   */
  private void initializeInputMode() {
    graphComponent.setInputMode(new GraphEditorInputMode());
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
      new TextWrappingDemo().start();
    });
  }
}
