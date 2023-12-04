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
package style.groupnodestyle;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.DefaultFolderNodeConverter;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.GroupNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.GroupNodeStyle;
import com.yworks.yfiles.graph.styles.GroupNodeStyleIconBackgroundShape;
import com.yworks.yfiles.graph.styles.GroupNodeStyleIconPosition;
import com.yworks.yfiles.graph.styles.GroupNodeStyleIconType;
import com.yworks.yfiles.graph.styles.GroupNodeStyleTabPosition;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.TextTrimming;
import com.yworks.yfiles.view.VerticalAlignment;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.NodeAlignmentPolicy;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;
import toolkit.Palette;
import toolkit.Themes;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;

public class GroupNodeStyleDemo extends AbstractDemo {

  private static final Font FONT_11 = new Font("Dialog", Font.PLAIN, 11);

  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    super.initialize();
    initializeFolding();
    initializeInputMode();
    initializeDefaultStyles();
    createSampleGraph();
  }

  /**
   * Enables folding and specifies that the label of the group node should also appear on the folder
   * node.
   */
  private void initializeFolding() {
    DefaultFolderNodeConverter nodeConverter = new DefaultFolderNodeConverter();
    nodeConverter.setCopyingFirstLabelEnabled(true);

    FoldingManager foldingManager = new FoldingManager(graphComponent.getGraph());
    foldingManager.setFolderNodeConverter(nodeConverter);
    graphComponent.setGraph(foldingManager.createFoldingView().getGraph());
  }

  /**
   * Restricts user interaction to selecting, panning, and zooming.
   */
  private void initializeInputMode() {
    graphComponent.setFileIOEnabled(true);

    GraphEditorInputMode geim = new GraphEditorInputMode();
    geim.setCreateNodeAllowed(false);
    geim.setGroupingOperationsAllowed(true);
    geim.setDeletableItems(GraphItemTypes.ALL.and(GraphItemTypes.NODE.inverse()));
    geim.getNavigationInputMode().setAutoGroupNodeAlignmentPolicy(NodeAlignmentPolicy.CENTER);

    // provide a way to collapse group nodes or expand folder nodes even if their style does not
    // show an icon for collapsing or expanding
    geim.addItemLeftDoubleClickedListener((source, args) -> {
      if (args.getItem() instanceof INode) {
        INode node = (INode) args.getItem();
        if (ICommand.TOGGLE_EXPANSION_STATE.canExecute(node, graphComponent)) {
          ICommand.TOGGLE_EXPANSION_STATE.execute(node, graphComponent);
          // we need to make sure that any handles that are present are reevaluated because they
          // may have different constraints after the expand/collapse operation
          geim.requeryHandles();
          args.setHandled(true);
        }
      }
    });

    graphComponent.setInputMode(geim);
  }

  /**
   * Configures the default styles for new nodes, edges, and labels in the given graph.
   */
  private void initializeDefaultStyles() {
    DemoStyles.initDemoStyles(
        graphComponent.getGraph(),
        Themes.PALETTE58,
        Themes.PALETTE58,
        Themes.PALETTE58,
        Themes.PALETTE58,
        Themes.PALETTE58
        );
  }

  /**
   * Creates a sample graph with several group and folder nodes.
   */
  private void createSampleGraph() {
    IGraph graph = graphComponent.getGraph() ;
    graph.clear();

    Palette red = Themes.PALETTE59;
    Palette green = Themes.PALETTE53;
    Palette blue = Themes.PALETTE56;
    Palette orange = Themes.PALETTE51;

    // create a couple of GroupNodeStyle instances that demonstrate various tab configuration options
    // for tabs that are placed at the top of the respective node ...

    // style for red nodes
    GroupNodeStyle redGroupNodeStyle = new GroupNodeStyle();
    redGroupNodeStyle.setFolderIcon(GroupNodeStyleIconType.NONE);
    redGroupNodeStyle.setTabPaint(red.getBackgroundPaint());

    // style for green nodes
    GroupNodeStyle greenGroupNodeStyle = new GroupNodeStyle();
    greenGroupNodeStyle.setCornerRadius(0);
    greenGroupNodeStyle.setGroupIcon(GroupNodeStyleIconType.TRIANGLE_DOWN);
    greenGroupNodeStyle.setFolderIcon(GroupNodeStyleIconType.TRIANGLE_UP);
    greenGroupNodeStyle.setIconPosition(GroupNodeStyleIconPosition.LEADING);
    greenGroupNodeStyle.setIconBackgroundShape(GroupNodeStyleIconBackgroundShape.SQUARE);
    greenGroupNodeStyle.setIconForegroundPaint(Colors.WHITE);
    greenGroupNodeStyle.setTabPaint(green.getBackgroundPaint());
    greenGroupNodeStyle.setTabPosition(GroupNodeStyleTabPosition.TOP_LEADING);
    greenGroupNodeStyle.setTabSlope(0);
    greenGroupNodeStyle.setPen(new Pen(green.getOutlinePaint(), 1));

    // style for blue nodes
    GroupNodeStyle blueGroupNodeStyle= new GroupNodeStyle();
    blueGroupNodeStyle.setShadowDrawingEnabled(true);
    blueGroupNodeStyle.setGroupIcon(GroupNodeStyleIconType.CHEVRON_DOWN);
    blueGroupNodeStyle.setFolderIcon(GroupNodeStyleIconType.CHEVRON_UP);
    blueGroupNodeStyle.setIconForegroundPaint(blue.getOutlinePaint());
    blueGroupNodeStyle.setIconPosition(GroupNodeStyleIconPosition.TRAILING);
    blueGroupNodeStyle.setTabPosition(GroupNodeStyleTabPosition.TOP_LEADING);
    blueGroupNodeStyle.setTabPaint(blue.getBackgroundPaint());
    blueGroupNodeStyle.setTabBackgroundPaint(blue.getOutlinePaint());
    blueGroupNodeStyle.setTabHeight(23);
    blueGroupNodeStyle.setTabSlope(0.5);
    blueGroupNodeStyle.setPen(new Pen(blue.getOutlinePaint(), 1));

    // style for orange nodes
    GroupNodeStyle orangeGroupNodeStyle = new GroupNodeStyle();
    orangeGroupNodeStyle.setCornerRadius(8);
    orangeGroupNodeStyle.setContentAreaPaint(orange.getEdgeLabelBackgroundPaint());
    orangeGroupNodeStyle.setShadowDrawingEnabled(true);
    orangeGroupNodeStyle.setGroupIcon(GroupNodeStyleIconType.MINUS);
    orangeGroupNodeStyle.setIconBackgroundPaint(orange.getNodeLabelBackgroundPaint());
    orangeGroupNodeStyle.setIconForegroundPaint(orange.getOutlinePaint());
    orangeGroupNodeStyle.setIconBackgroundShape(GroupNodeStyleIconBackgroundShape.CIRCLE_SOLID);
    orangeGroupNodeStyle.setTabPaint(orange.getBackgroundPaint());
    orangeGroupNodeStyle.setTabHeight(22);
    orangeGroupNodeStyle.setTabInset(8.0);
    orangeGroupNodeStyle.setPen(new Pen(orange.getOutlinePaint(), 1));

    GroupNodeStyle[] stylesWithTabAtTop = {
        redGroupNodeStyle,
        greenGroupNodeStyle,
        blueGroupNodeStyle,
        orangeGroupNodeStyle
    };

    // ... and for tabs at different sides of the respective nodes
    Palette gold = Themes.PALETTE510;
    Palette gray = Themes.PALETTE58;
    Palette lightGreen = Themes.PALETTE54;
    Palette purple = Themes.PALETTE55;

    // style for gold nodes
    GroupNodeStyle goldGroupNodeStyle = new GroupNodeStyle();
    goldGroupNodeStyle.setGroupIcon(GroupNodeStyleIconType.MINUS);
    goldGroupNodeStyle.setIconForegroundPaint(gold.getBackgroundPaint());
    goldGroupNodeStyle.setTabPaint(gold.getBackgroundPaint());

    // style for gray nodes
    GroupNodeStyle grayGroupNodeStyle = new GroupNodeStyle();
    grayGroupNodeStyle.setCornerRadius(0);
    grayGroupNodeStyle.setGroupIcon(GroupNodeStyleIconType.TRIANGLE_LEFT);
    grayGroupNodeStyle.setFolderIcon(GroupNodeStyleIconType.TRIANGLE_RIGHT);
    grayGroupNodeStyle.setIconPosition(GroupNodeStyleIconPosition.LEADING);
    grayGroupNodeStyle.setIconBackgroundShape(GroupNodeStyleIconBackgroundShape.SQUARE);
    grayGroupNodeStyle.setIconForegroundPaint(Colors.WHITE);
    grayGroupNodeStyle.setTabPaint(gray.getBackgroundPaint());
    grayGroupNodeStyle.setTabSlope(0);
    grayGroupNodeStyle.setTabPosition(GroupNodeStyleTabPosition.RIGHT_LEADING);
    grayGroupNodeStyle.setPen(new Pen(gray.getOutlinePaint(), 1));

    // style for light-green nodes
    GroupNodeStyle lightGreenGroupNodeStyle = new GroupNodeStyle();
    lightGreenGroupNodeStyle.setShadowDrawingEnabled(true);
    lightGreenGroupNodeStyle.setGroupIcon(GroupNodeStyleIconType.CHEVRON_UP);
    lightGreenGroupNodeStyle.setFolderIcon(GroupNodeStyleIconType.CHEVRON_DOWN);
    lightGreenGroupNodeStyle.setIconForegroundPaint(blue.getOutlinePaint());
    lightGreenGroupNodeStyle.setIconPosition(GroupNodeStyleIconPosition.LEADING);
    lightGreenGroupNodeStyle.setTabPosition(GroupNodeStyleTabPosition.BOTTOM_TRAILING);
    lightGreenGroupNodeStyle.setTabPaint(lightGreen.getBackgroundPaint());
    lightGreenGroupNodeStyle.setTabBackgroundPaint(green.getOutlinePaint());
    lightGreenGroupNodeStyle.setTabHeight(23);
    lightGreenGroupNodeStyle.setTabSlope(0.5);
    lightGreenGroupNodeStyle.setPen(new Pen(lightGreen.getOutlinePaint(), 1));

    // style for purple nodes
    GroupNodeStyle purpleGroupNodeStyle = new GroupNodeStyle();
    purpleGroupNodeStyle.setCornerRadius(8);
    purpleGroupNodeStyle.setContentAreaPaint(purple.getNodeLabelBackgroundPaint());
    purpleGroupNodeStyle.setShadowDrawingEnabled(true);
    purpleGroupNodeStyle.setGroupIcon(GroupNodeStyleIconType.MINUS);
    purpleGroupNodeStyle.setIconPosition(GroupNodeStyleIconPosition.LEADING);
    purpleGroupNodeStyle.setIconBackgroundPaint(purple.getNodeLabelBackgroundPaint());
    purpleGroupNodeStyle.setIconForegroundPaint(purple.getOutlinePaint());
    purpleGroupNodeStyle.setIconBackgroundShape(GroupNodeStyleIconBackgroundShape.CIRCLE_SOLID);
    purpleGroupNodeStyle.setTabPosition(GroupNodeStyleTabPosition.LEFT);
    purpleGroupNodeStyle.setTabPaint(purple.getBackgroundPaint());
    purpleGroupNodeStyle.setTabHeight(22);
    purpleGroupNodeStyle.setTabInset(8.0);
    purpleGroupNodeStyle.setPen(new Pen(purple.getOutlinePaint(), 1));

    GroupNodeStyle[] stylesWithTabAtMiscPositions = {
        goldGroupNodeStyle,
        grayGroupNodeStyle,
        lightGreenGroupNodeStyle,
        purpleGroupNodeStyle
    };

    // create label styles that use the same color sets as the GroupNodeStyle instances created above
    // Note that for some label styles the insets are set to EMPTY and the font size is reduced so the text fits
    // into the default tabHeight of their group node styles.

    // style for red nodes
    DefaultLabelStyle redLabelStyle = new DefaultLabelStyle();
    redLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    redLabelStyle.setTextTrimming(TextTrimming.CHARACTER_ELLIPSIS);
    redLabelStyle.setTextClippingEnabled(false);
    redLabelStyle.setTextPaint(red.getNodeLabelBackgroundPaint());
    redLabelStyle.setInsets(InsetsD.EMPTY);
    redLabelStyle.setFont(FONT_11);

    // style for green nodes
    DefaultLabelStyle greenLabelStyle = new DefaultLabelStyle();
    greenLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    greenLabelStyle.setTextTrimming(TextTrimming.CHARACTER_ELLIPSIS);
    greenLabelStyle.setTextClippingEnabled(false);
    greenLabelStyle.setTextPaint(green.getTextPaint());
    greenLabelStyle.setInsets(InsetsD.EMPTY);
    greenLabelStyle.setFont(FONT_11);

    // style for blue nodes
    // this style uses centered horizontal text because of the sloped tab in the blue nodes
    DefaultLabelStyle blueLabelStyle = new DefaultLabelStyle();
    blueLabelStyle.setTextAlignment(TextAlignment.CENTER);
    blueLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    blueLabelStyle.setTextTrimming(TextTrimming.CHARACTER_ELLIPSIS);
    blueLabelStyle.setTextClippingEnabled(false);
    blueLabelStyle.setTextPaint(blue.getNodeLabelBackgroundPaint());

    // style for orange nodes
    DefaultLabelStyle orangeLabelStyle = new DefaultLabelStyle();
    orangeLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    orangeLabelStyle.setTextTrimming(TextTrimming.CHARACTER_ELLIPSIS);
    orangeLabelStyle.setTextClippingEnabled(false);
    orangeLabelStyle.setTextPaint(gold.getNodeLabelBackgroundPaint());
    orangeLabelStyle.setInsets(InsetsD.EMPTY);
    orangeLabelStyle.setFont(FONT_11);

    DefaultLabelStyle[] labelStyles = {
        redLabelStyle,
        greenLabelStyle,
        blueLabelStyle,
        orangeLabelStyle
    };

    // style for gold nodes
    DefaultLabelStyle goldLabelStyle = new DefaultLabelStyle();
    goldLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    goldLabelStyle.setTextTrimming(TextTrimming.CHARACTER_ELLIPSIS);
    goldLabelStyle.setTextClippingEnabled(false);
    goldLabelStyle.setTextPaint(gold.getNodeLabelBackgroundPaint());
    goldLabelStyle.setInsets(InsetsD.EMPTY);
    goldLabelStyle.setFont(FONT_11);

    // style for gray nodes
    DefaultLabelStyle grayLabelStyle = new DefaultLabelStyle();
    grayLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    grayLabelStyle.setTextTrimming(TextTrimming.CHARACTER_ELLIPSIS);
    grayLabelStyle.setTextClippingEnabled(false);
    grayLabelStyle.setInsets(InsetsD.EMPTY);
    grayLabelStyle.setFont(FONT_11);

    // style for lieght-green nodes
    DefaultLabelStyle lightGreenLabelStyle = new DefaultLabelStyle();
    lightGreenLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    lightGreenLabelStyle.setTextTrimming(TextTrimming.CHARACTER_ELLIPSIS);
    lightGreenLabelStyle.setTextClippingEnabled(false);
    lightGreenLabelStyle.setTextPaint(green.getNodeLabelBackgroundPaint());

    // style for purple nodes
    DefaultLabelStyle purpleLabelStyle = new DefaultLabelStyle();
    purpleLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    purpleLabelStyle.setTextTrimming(TextTrimming.CHARACTER_ELLIPSIS);
    purpleLabelStyle.setTextClippingEnabled(false);
    purpleLabelStyle.setTextPaint(purple.getNodeLabelBackgroundPaint());
    purpleLabelStyle.setInsets(InsetsD.EMPTY);
    purpleLabelStyle.setFont(FONT_11);

    DefaultLabelStyle[] labelStylesWithTabAtMiscPositions = {
        goldLabelStyle,
        grayLabelStyle,
        lightGreenLabelStyle,
        purpleLabelStyle
    };

    String[] labelTexts = { "Red", "Green", "Blue", "Orange" };
    String[] labelTextsWithTabAtMiscPositions = { "Gold", "Gray", "Light green", "Purple" };

    // create one group node and one folder node for each of the above GroupNodeStyle instances
    createGroupAndFolderNodes(graph, stylesWithTabAtTop, labelStyles, labelTexts, 0, 0);
    createGroupAndFolderNodes(graph, stylesWithTabAtMiscPositions, labelStylesWithTabAtMiscPositions,
        labelTextsWithTabAtMiscPositions, 0, 425);

    // create a couple of child nodes for group nodes ...
    INode[] nodes = graph.getNodes().toArray(INode.class);
    INode p1c1 = createChildNode(graph, nodes[1], 20, 52);
    INode p1c2 = createChildNode(graph, nodes[1], 80, 32);
    INode p1c3 = createChildNode(graph, nodes[1], 60, 102);
    INode p1c4 = createChildNode(graph, nodes[1], 140, 102);
    INode p2c1 = createChildNode(graph, nodes[2], 43, 42);
    INode p2c2 = createChildNode(graph, nodes[2], 133, 78);
    createChildNode(graph, nodes[8], 33, 33);
    INode p8c2 = createChildNode(graph, nodes[8], 68, 103);
    INode p8c3 = createChildNode(graph, nodes[8], 103, 33);
    INode p9c1 = createChildNode(graph, nodes[9], 10, 10);
    INode p9c2 = createChildNode(graph, nodes[9], 58, 42);
    INode p9c3 = createChildNode(graph, nodes[9], 96, 94);
    createChildNode(graph, nodes[10], 43, 14);
    INode pBc1 = createChildNode(graph, nodes[11], 34, 34);
    INode pBc2 = createChildNode(graph, nodes[11], 128, 74);
    INode pBc3 = createChildNode(graph, nodes[11], 138, 28);
    INode pBc4 = createChildNode(graph, nodes[11], 50, 88);

    graph.createEdge(p1c1, p1c3);
    graph.createEdge(p1c3, p1c2);
    graph.createEdge(p1c3, p1c4);
    graph.createEdge(p2c2, p2c1);
    graph.createEdge(p8c2, p8c3);
    graph.createEdge(p9c1, p9c2);
    graph.createEdge(p9c2, p9c3);
    graph.createEdge(pBc1, pBc2);
    graph.createEdge(pBc3, pBc4);

    // ... and folder nodes
    createChildNode(graph, nodes[4], 68, 46);
    createChildNode(graph, nodes[4], 147, 82);
    createChildNode(graph, nodes[7], 55, 100);
    createChildNode(graph, nodes[12], 8, 26);
    createChildNode(graph, nodes[12], 87, 62);
    createChildNode(graph, nodes[13], 29, 85);
    createChildNode(graph, nodes[13], 59, 55);
    createChildNode(graph, nodes[13], 89, 25);
    createChildNode(graph, nodes[14], 8, 15);
    createChildNode(graph, nodes[14], 58, 15);
    createChildNode(graph, nodes[14], 108, 15);
    createChildNode(graph, nodes[14], 58, 55);
    createChildNode(graph, nodes[14], 108, 55);
    createChildNode(graph, nodes[14], 158, 55);
    createChildNode(graph, nodes[15], 55, 25);
    createChildNode(graph, nodes[15], 133, 25);
    createChildNode(graph, nodes[15], 55, 95);
    createChildNode(graph, nodes[15], 133, 95);
  }

  /**
   * Creates a group node and a folder node for each of the given style instances.
   * Additionally, this method will add one label to each created group or folder node.
   * @param graph The graph in which to create the new group and folder nodes.
   * @param nodeStyles The style instances for which to create new group and folder nodes.
   * @param labelStyles The style instances for the labels of the new group and folder nodes.
   * @param labelTexts The texts for the labels of the new group and folder nodes.
   * @param x0 The top-left x-coordinate of the first node to create.
   * @param y0 The top-left x-coordinate of the first node to create.
   */
  private void createGroupAndFolderNodes(
      IGraph graph,
      GroupNodeStyle[] nodeStyles,
      DefaultLabelStyle[] labelStyles,
      String[] labelTexts,
      int x0,
      int y0) {
    // place the labels of the group and folder nodes into the tab background of their visualizations
    // GroupNodeLabelModel's default parameter can be used to place labels into the tab area instead
    ILabelModelParameter tabBackgroundParameter = new GroupNodeLabelModel().createTabBackgroundParameter();

    int y = y0;
    int width = 200;
    int height = 150;
    for (int j = 0; j < 2; ++j) {
      int x = x0;
      int n = nodeStyles.length;
      for (int i = 0; i < n; ++i) {
        INode node = graph.createGroupNode(null, new RectD(x, y, width, height), nodeStyles[i]);
        graph.addLabel(node, labelTexts[i] + (j + 1), tabBackgroundParameter, labelStyles[i]);
        if (j > 0) {
          collapseLast(graph);
        }
        x += width + 100;
      }
      y += height + 25;
    }
  }

  /**
   * Collapses the last group node in the given graph.
   */
  private void collapseLast(IGraph graph) {
    graph.getFoldingView().collapse(graph.getNodes().last());
  }

  /**
   * Creates a child node for the given parent group node.
   * The created node will be neither a group node nor a folder node.
   * @param graph The graph in which to create the new node.
   * @param parent The parent node for the new node.
   * @param xOffset The distance in x-direction from the new node's top left corner to the parent node's top left corner.
   * @param yOffset The distance in y-direction from the new node's top left corner to the parent node's top left corner.
   */
  private INode createChildNode(IGraph graph, INode parent, int xOffset, int yOffset) {
    IRectangle nl = parent.getLayout();
    INode node = graph.createNode(new RectD(nl.getX() + xOffset, nl.getY() + yOffset, 30, 30));
    graph.setParent(node, parent);
    return node;
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

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    super.configureToolBar(toolBar);
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Open", "open-16.png", ICommand.OPEN, null, graphComponent));
    toolBar.add(createCommandButtonAction("Save", "save-16.png", ICommand.SAVE_AS, null, graphComponent));

  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new GroupNodeStyleDemo().start();
    });
  }
}
