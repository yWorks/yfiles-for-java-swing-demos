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
package style.rectanglenodestyle;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.CornerStyle;
import com.yworks.yfiles.graph.styles.Corners;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.RectangleNodeStyle;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.IReshapeHandler;
import com.yworks.yfiles.view.input.NodeReshapeHandleProvider;
import toolkit.AbstractDemo;
import toolkit.DemoStyles;
import toolkit.Palette;
import toolkit.Themes;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

public class RectangleNodeStyleDemo extends AbstractDemo {
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

  /**
   * Initializes defaults for the given graph.
   */
  private void initializeStyleDefaults() {
    // Set defaults for new nodes
    Palette gray = Themes.PALETTE75;

    RectangleNodeStyle nodeStyle = new RectangleNodeStyle();
    nodeStyle.setPaint(gray.getBackgroundPaint());
    nodeStyle.setPen(new Pen(gray.getOutlinePaint(), 1));

    INodeDefaults nodeDefaults = graphComponent.getGraph().getNodeDefaults();
    nodeDefaults.setStyle(nodeStyle);
    nodeDefaults.setSize(new SizeD(300, 100));
    nodeDefaults.setStyleInstanceSharingEnabled(false);
  }

  /**
   * Creates a small sample with different node style settings.
   */
  private void createSampleNodes() {
    Palette yellow = Themes.PALETTE71;
    Palette orange = Themes.PALETTE72;
    Palette green = Themes.PALETTE73;
    Palette blue = Themes.PALETTE74;

    // Create nodes with round corners with different resizing behaviors
    createNode(new PointD(0, 0), yellow, CornerStyle.ROUND, false, 10, Corners.ALL);
    createNode(new PointD(0, 200), orange, CornerStyle.ROUND, true, 0.2, Corners.ALL);
    createNode(new PointD(0, 400), green, CornerStyle.ROUND, true, 0.5, Corners.ALL);
    createNode(new PointD(0, 600), blue, CornerStyle.ROUND, true, 0.8, Corners.BOTTOM);

    // Create nodes with cut-off corners with different resizing behaviors
    createNode(new PointD(400, 0), yellow, CornerStyle.CUT, false, 10, Corners.ALL);
    createNode(new PointD(400, 200), orange, CornerStyle.CUT, true, 0.2, Corners.ALL);
    createNode(new PointD(400, 400), green, CornerStyle.CUT, true, 0.5, Corners.ALL);
    createNode(new PointD(400, 600), blue, CornerStyle.CUT, true, 0.8, Corners.BOTTOM);
  }

  /**
   * Creates a node with a label that describes the configuration of the RectangleNodeStyle.
   * @param location The location of the node.
   * @param color The color set of the node and label.
   * @param cornerStyle Whether corners should be round or a line.
   * @param scaleCornerSize Whether the corner size should be used as absolute value or be scaled with the node size.
   * @param cornerSize The corner size.
   * @param corners Which corners are drawn with the given corner style.
   */
  private void createNode(PointD location, Palette color, CornerStyle cornerStyle, boolean scaleCornerSize, double cornerSize, Corners corners) {
    RectangleNodeStyle style = new RectangleNodeStyle();
    style.setPaint(color.getBackgroundPaint());
    style.setPen(new Pen(color.getOutlinePaint(), 1));
    style.setCornerStyle(cornerStyle);
    style.setCornerSizeScalingEnabled(scaleCornerSize);
    style.setCornerSize(cornerSize);
    style.setCorners(corners);

    INode node = graphComponent.getGraph().createNode(location, style);
    addLabel(node, color);
  }

  /**
   * Adds a label that describes the owner's style configuration.
   * @param node The owner of the label.
   * @param color The color set of the label.
   */
  private void addLabel(INode node, Palette color) {
    DefaultLabelStyle style = DemoStyles.createDemoNodeLabelStyle(color);
    style.setUsingFractionalFontMetricsEnabled(true);

    graphComponent.getGraph().addLabel(
        node,
        styleToText((RectangleNodeStyle) node.getStyle()),
        InteriorLabelModel.CENTER,
        style
    );

  }

  /**
   * Returns a text description of the style configuration.
   */
  private static String styleToText(RectangleNodeStyle style) {
    return "Corner Style: " + style.getCornerStyle().name() + "\n" +
        "Corner Size Scaling: " + (style.isCornerSizeScalingEnabled() ? "RELATIVE":"ABSOLUTE") + "\n" +
        "Affected Corners: " + cornersToText(style.getCorners());
  }

  /**
   * Returns a text description of the given corner configuration.
   */
  private static String cornersToText(Corners corners) {
    Corners[] all = {
        Corners.ALL, Corners.TOP, Corners.BOTTOM, Corners.RIGHT, Corners.LEFT, Corners.TOP_LEFT, Corners.TOP_RIGHT,
        Corners.BOTTOM_LEFT, Corners.BOTTOM_RIGHT
    };

    ArrayList<String> affected = new ArrayList<>();
    for (Corners corner : all) {
      if ((corners.and(corner).equals(corner))) {
        corners = corners.and(corner.inverse());
        affected.add(corner.toString());
      }
    }
    return affected.size() > 0 ? String.join(" & ", affected) : "none";
  }

  /**
   * Sets up an input mode for the GraphControl, and adds a custom handle
   * that allows to change the corner size.
   */
  private void initializeInputMode() {
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setCreateEdgeAllowed(false);
    inputMode.setAddLabelAllowed(false);
    inputMode.setEditLabelAllowed(false);
    inputMode.setSelectableItems(GraphItemTypes.NODE);
    graphComponent.setInputMode(inputMode);

    // add a label to newly created node that shows the current style settings
    inputMode.addNodeCreatedListener((source, args) -> {
      INode node = args.getItem();
      addLabel(node, Themes.PALETTE75);
    });

    // listen for selection changes to update the option handler for the style properties
    graphComponent.getSelection().addItemSelectionChangedListener((source, args) -> {
      if (args.isItemSelected()) {
        adjustOptionPanel((INode) args.getItem());
      }
    });

    NodeDecorator nodeDecorator = graphComponent.getGraph().getDecorator().getNodeDecorator();

    // add handle that enables the user to change the corner size of a node
    nodeDecorator.getHandleProviderDecorator().setImplementationWrapper(
        node -> node.getStyle() instanceof RectangleNodeStyle,
        (node, delegateProvider) -> new CornerSizeHandleProvider(node, () -> adjustOptionPanel(node), delegateProvider)
    );

    // only provide reshape handles for the east, south and south-east sides, so they don't clash with the corner size handle
    nodeDecorator.getReshapeHandleProviderDecorator().setFactory(node ->
        new NodeReshapeHandleProvider(node, node.lookup(IReshapeHandler.class),
            HandlePositions.EAST.or(HandlePositions.SOUTH).or(HandlePositions.SOUTH_EAST)));

    nodeDecorator.getSelectionDecorator().hideImplementation();
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
   * Adds the option panel to the left side.
   * @param rootPane
   */
  @Override
  protected void configure(JRootPane rootPane) {
    super.configure(rootPane);
    rootPane.getContentPane().add(createOptionPanel(), BorderLayout.WEST);
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new RectangleNodeStyleDemo().start();
    });
  }


  private final JComboBox<CornerStyle> cornerStyleBox = createCornerStyleBox();;
  private final JComboBox<Boolean> cornerSizeBox = createCornerSizeBox();
  private final JCheckBox topLeftCheckBox = createCheckBox();
  private final JCheckBox topRightCheckBox = createCheckBox();
  private final JCheckBox bottomLeftCheckBox = createCheckBox();
  private final JCheckBox bottomRightCheckBox = createCheckBox();
  private boolean optionsUpdating = false;

  private void adjustOptionPanel(INode node) {
    if (node.getStyle() instanceof RectangleNodeStyle) {
      optionsUpdating = true;
      RectangleNodeStyle style = (RectangleNodeStyle) node.getStyle();

      cornerStyleBox.setSelectedItem(style.getCornerStyle());
      cornerSizeBox.setSelectedItem(style.isCornerSizeScalingEnabled());

      Corners corners = style.getCorners();
      topLeftCheckBox.setSelected(corners.and(Corners.TOP_LEFT).equals(Corners.TOP_LEFT));
      topRightCheckBox.setSelected(corners.and(Corners.TOP_RIGHT).equals(Corners.TOP_RIGHT));
      bottomRightCheckBox.setSelected(corners.and(Corners.BOTTOM_RIGHT).equals(Corners.BOTTOM_RIGHT));
      bottomLeftCheckBox.setSelected(corners.and(Corners.BOTTOM_LEFT).equals(Corners.BOTTOM_LEFT));
      optionsUpdating = false;
    }
  }

  private void adjustSelectedNodes() {
    if (optionsUpdating) {
      return;
    }

    CornerStyle cornerStyle = (CornerStyle) cornerStyleBox.getSelectedItem();
    boolean scaleCornerSize = (boolean) cornerSizeBox.getSelectedItem();

    Corners affectedCorners = Corners.NONE;
    if (topLeftCheckBox.isSelected()) {
      affectedCorners = affectedCorners.or(Corners.TOP_LEFT);
    }
    if (topRightCheckBox.isSelected()) {
      affectedCorners = affectedCorners.or(Corners.TOP_RIGHT);
    }
    if (bottomRightCheckBox.isSelected()) {
      affectedCorners = affectedCorners.or(Corners.BOTTOM_RIGHT);
    }
    if (bottomLeftCheckBox.isSelected()) {
      affectedCorners = affectedCorners.or(Corners.BOTTOM_LEFT);
    }

    IGraph graph = graphComponent.getGraph();
    for (INode node : graphComponent.getSelection().getSelectedNodes()) {
      if (node.getStyle() instanceof RectangleNodeStyle) {
        RectangleNodeStyle style = (RectangleNodeStyle) node.getStyle();
        applyStyleSettings(style, cornerStyle, scaleCornerSize, affectedCorners);
        if (node.getLabels().size() == 0) {
          graph.addLabel(node, styleToText(style));
        } else {
          graph.setLabelText(node.getLabels().first(), styleToText(style));
        }
      }
    }
    RectangleNodeStyle defaultStyle = (RectangleNodeStyle) graph.getNodeDefaults().getStyle();
    applyStyleSettings(defaultStyle, cornerStyle, scaleCornerSize, affectedCorners);
    graphComponent.repaint();
  }

  private void applyStyleSettings(RectangleNodeStyle style, CornerStyle cornerStyle, boolean scaleCornerSize, Corners corners) {
    style.setCornerStyle(cornerStyle);
    style.setCornerSizeScalingEnabled(scaleCornerSize);
    style.setCorners(corners);
  }

  private JPanel createOptionPanel() {
    JPanel optionPanel = new JPanel(new GridBagLayout());
    optionPanel.setPreferredSize(new Dimension(250, Integer.MAX_VALUE));
    optionPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5),
        BorderFactory.createTitledBorder("RectangleNodeStyle properties")));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 0, 5);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0;
    optionPanel.add(new JLabel("Corner Style: "), gbc);

    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.weightx = 1;
    optionPanel.add(cornerStyleBox, gbc);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.weightx = 0;
    optionPanel.add(new JLabel("Corner Size Scaling: "), gbc);

    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.weightx = 1;
    optionPanel.add(cornerSizeBox, gbc);

    JPanel cornerPanel = new JPanel(new GridBagLayout());
    cornerPanel.setBorder(BorderFactory.createTitledBorder("Corners"));
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.weightx = 0;
    gbc.gridwidth = 2;
    optionPanel.add(cornerPanel, gbc);

    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    gbc.weighty = 1;
    optionPanel.add(new JPanel(), gbc);

    GridBagConstraints gbc2 = new GridBagConstraints();
    gbc2.insets = new Insets(5, 5, 0, 5);

    gbc2.anchor = GridBagConstraints.WEST;
    gbc2.fill = GridBagConstraints.NONE;
    gbc2.gridx = 0;
    gbc2.gridy = 0;
    gbc2.weightx = 0;
    cornerPanel.add(new JLabel("TopLeft: "), gbc2);

    gbc2.anchor = GridBagConstraints.EAST;
    gbc2.fill = GridBagConstraints.HORIZONTAL;
    gbc2.gridx = 1;
    gbc2.weightx = 1;
    cornerPanel.add(topLeftCheckBox, gbc2);

    gbc2.anchor = GridBagConstraints.WEST;
    gbc2.fill = GridBagConstraints.NONE;
    gbc2.gridx = 0;
    gbc2.gridy++;
    gbc2.weightx = 0;
    cornerPanel.add(new JLabel("TopRight: "), gbc2);

    gbc2.anchor = GridBagConstraints.EAST;
    gbc2.fill = GridBagConstraints.HORIZONTAL;
    gbc2.gridx = 1;
    gbc2.weightx = 1;
    cornerPanel.add(topRightCheckBox, gbc2);

    gbc2.anchor = GridBagConstraints.WEST;
    gbc2.fill = GridBagConstraints.NONE;
    gbc2.gridx = 0;
    gbc2.gridy++;
    gbc2.weightx = 0;
    cornerPanel.add(new JLabel("BottomRight: "), gbc2);

    gbc2.anchor = GridBagConstraints.EAST;
    gbc2.fill = GridBagConstraints.HORIZONTAL;
    gbc2.gridx = 1;
    gbc2.weightx = 1;
    cornerPanel.add(bottomRightCheckBox, gbc2);

    gbc2.anchor = GridBagConstraints.WEST;
    gbc2.fill = GridBagConstraints.NONE;
    gbc2.gridx = 0;
    gbc2.gridy++;
    gbc2.weightx = 0;
    cornerPanel.add(new JLabel("BottomLeft: "), gbc2);

    gbc2.anchor = GridBagConstraints.EAST;
    gbc2.fill = GridBagConstraints.HORIZONTAL;
    gbc2.gridx = 1;
    gbc2.weightx = 1;
    cornerPanel.add(bottomLeftCheckBox, gbc2);

    return optionPanel;
  }

  private JComboBox<CornerStyle> createCornerStyleBox() {
    JComboBox<CornerStyle> cornerStyleBox = new JComboBox<>(new CornerStyle[]{CornerStyle.ROUND, CornerStyle.CUT});
    cornerStyleBox.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        CornerStyle cs = (CornerStyle) value;
        setText(cs.equals(CornerStyle.ROUND) ? "Round" : "Cut");
        return this;
      }
    });
    cornerStyleBox.addActionListener(e -> adjustSelectedNodes());
    return cornerStyleBox;
  }

  private JComboBox<Boolean> createCornerSizeBox() {
    JComboBox<Boolean> cornerSizeBox = new JComboBox<>(new Boolean[]{Boolean.FALSE, Boolean.TRUE});
    cornerSizeBox.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        boolean relative = (boolean) value;
        setText(relative ? "Relative" : "Absolute");
        return this;
      }
    });
    cornerSizeBox.addActionListener(e -> adjustSelectedNodes());
    return cornerSizeBox;
  }

  private JCheckBox createCheckBox() {
    JCheckBox checkBox = new JCheckBox();
    checkBox.addActionListener(e -> adjustSelectedNodes());
    return checkBox;
  }

}
