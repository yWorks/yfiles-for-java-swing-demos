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
package style.arrownodestyle;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.styles.ArrowNodeDirection;
import com.yworks.yfiles.graph.styles.ArrowNodeStyle;
import com.yworks.yfiles.graph.styles.ArrowStyleShape;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.LabelShape;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.VerticalAlignment;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.HandlePositions;
import com.yworks.yfiles.view.input.IReshapeHandler;
import com.yworks.yfiles.view.input.NodeReshapeHandleProvider;
import toolkit.AbstractDemo;
import toolkit.Palette;
import toolkit.Themes;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSlider;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ArrowNodeStyleDemo extends AbstractDemo {
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
   * Initializes the default styles.
   */
  private void initializeStyleDefaults() {
    Palette orange = Themes.PALETTE_ORANGE;

    ArrowNodeStyle nodeStyle = new ArrowNodeStyle();
    nodeStyle.setPaint(orange.getBackgroundPaint());
    nodeStyle.setPen(new Pen(orange.getOutlinePaint(), 1));

    ExteriorLabelModel labelModel = new ExteriorLabelModel();
    labelModel.setInsets(new InsetsD(30));

    INodeDefaults nodeDefaults = graphComponent.getGraph().getNodeDefaults();
    nodeDefaults.setStyle(nodeStyle);
    nodeDefaults.setSize(new SizeD(200, 100));
    nodeDefaults.setStyleInstanceSharingEnabled(false);
    nodeDefaults.getLabelDefaults().setStyle(createLabelStyle(orange));
    nodeDefaults.getLabelDefaults().setLayoutParameter(labelModel.createParameter(ExteriorLabelModel.Position.SOUTH));
  }

  /**
   * Creates a new node label style with colors from the given palette.
   */
  private ILabelStyle createLabelStyle(Palette palette) {
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setShape(LabelShape.ROUND_RECTANGLE);
    labelStyle.setBackgroundPaint(palette.getNodeLabelBackgroundPaint());
    labelStyle.setTextPaint(palette.getTextPaint());
    labelStyle.setTextAlignment(TextAlignment.LEFT);
    labelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    labelStyle.setInsets(new InsetsD(4, 8, 4, 8));
    labelStyle.setFont(new Font(labelStyle.getFont().getName(), labelStyle.getFont().getStyle(), 14));
    labelStyle.setUsingFractionalFontMetricsEnabled(true);
    return labelStyle;
  }

  /**
   * Creates the initial sample graph.
   */
  private void createSampleNodes() {
    // create nodes with different shapes, angles and shaft ratios
    createNodes(0, ArrowStyleShape.ARROW, Themes.PALETTE_ORANGE);
    createNodes(300, ArrowStyleShape.DOUBLE_ARROW, Themes.PALETTE_BLUE);
    createNodes(600, ArrowStyleShape.NOTCHED_ARROW, Themes.PALETTE_RED);
    createNodes(900, ArrowStyleShape.PARALLELOGRAM, Themes.PALETTE_GREEN);
    createNodes(1200, ArrowStyleShape.TRAPEZOID, Themes.PALETTE_PURPLE);
  }

  /**
   * Creates several nodes with the given shape and different angles and shaft ratios.
   * @param xOffset The x-location where to place the nodes.
   * @param shape The shape to use for the arrow.
   * @param palette The colors to use for nodes and labels.
   */
  private void createNodes(int xOffset, ArrowStyleShape shape, Palette palette) {
    double angleFactor =
        shape == ArrowStyleShape.PARALLELOGRAM ||
        shape == ArrowStyleShape.TRAPEZOID
            ? 0.5
            : 1;

    // small angle and shaft ratio pointing left
    ArrowNodeStyle style1 = new ArrowNodeStyle();
    style1.setShape(shape);
    style1.setDirection(ArrowNodeDirection.LEFT);
    style1.setAngle((angleFactor * Math.PI) / 8);
    style1.setShaftRatio(0.25);
    style1.setPaint(palette.getBackgroundPaint());
    style1.setPen(new Pen(palette.getOutlinePaint(), 1));

    // default angle and shaft ratio pointing up
    ArrowNodeStyle style2 = new ArrowNodeStyle();
    style2.setShape(shape);
    style2.setDirection(ArrowNodeDirection.UP);
    style2.setAngle((angleFactor * Math.PI) / 4);
    style2.setShaftRatio(1.0 / 3);
    style2.setPaint(palette.getBackgroundPaint());
    style2.setPen(new Pen(palette.getOutlinePaint(), 1));

    // bigger angle and shaft ratio pointing right
    ArrowNodeStyle style3 = new ArrowNodeStyle();
    style3.setShape(shape);
    style3.setDirection(ArrowNodeDirection.RIGHT);
    style3.setAngle((angleFactor * Math.PI * 3) / 8);
    style3.setShaftRatio(0.75);
    style3.setPaint(palette.getBackgroundPaint());
    style3.setPen(new Pen(palette.getOutlinePaint(), 1));


   // negative angle and max shaft ratio pointing right
    ArrowNodeStyle style4 = new ArrowNodeStyle();
    style4.setShape(shape);
    style4.setDirection(ArrowNodeDirection.RIGHT);
    style4.setAngle(((angleFactor * -Math.PI) / 8));
    style4.setShaftRatio(1);
    style4.setPaint(palette.getBackgroundPaint());
    style4.setPen(new Pen(palette.getOutlinePaint(), 1));

    ArrowNodeStyle[] styles = {style1, style2, style3, style4};

    // create a sample node for each sample style instance
    IGraph graph = graphComponent.getGraph();
    int y = 0;
    for (int i = 0; i < styles.length; ++i) {
      double x = xOffset + (i == 1 ? 50 : 0);
      double width = i == 1 ? 100 : 200;
      double height = i == 1 ? 200 : 100;
      ArrowNodeStyle style = styles[i];

      ExteriorLabelModel labelModel = new ExteriorLabelModel();
      labelModel.setInsets(new InsetsD(30));

      graph.addLabel(
          graph.createNode(new RectD(x, y, width, height), style),
          styleToText(style),
          labelModel.createParameter(ExteriorLabelModel.Position.SOUTH),
          createLabelStyle(palette)
        );
      y += height + 250;
    }
  }

  /**
   * Sets up an input mode for the GraphComponent, and adds reshape handles.
   */
  private void initializeInputMode() {
    // reserve some space for the angle adjustment handle
    graphComponent.setContentMargins(new InsetsD(20));

    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setCreateEdgeAllowed(false);
    inputMode.setAddLabelAllowed(false);
    inputMode.setEditLabelAllowed(false);
    inputMode.setSelectableItems(GraphItemTypes.NODE);
    graphComponent.setInputMode(inputMode);

    // add a label to newly created node that shows the current style settings
    inputMode.addNodeCreatedListener((source, args) -> {
      INode node = args.getItem();
      graphComponent.getGraph().addLabel(node, styleToText((ArrowNodeStyle) node.getStyle()));
    });

    // listen for selection changes to update the option handler for the style properties
    graphComponent.getSelection().addItemSelectionChangedListener((source, args) -> {
      if (args.isItemSelected()) {
        adjustOptionPanel((INode) args.getItem());
      }
    });

    NodeDecorator nodeDecorator = graphComponent.getGraph().getDecorator().getNodeDecorator();

    // add handle that enables the user to change the angle and shaft ratio of a node style
    nodeDecorator.getHandleProviderDecorator().setImplementationWrapper(
        node -> node.getStyle() instanceof ArrowNodeStyle,
        (node, delegateProvider) -> new ArrowNodeStyleHandleProvider(node, () -> {
          adjustOptionPanel(node);
          ArrowNodeStyle style = (ArrowNodeStyle) node.getStyle();
          if (node.getLabels().size() == 0) {
            graphComponent.getGraph().addLabel(node, styleToText(style));
          } else {
            graphComponent.getGraph().setLabelText(node.getLabels().first(), styleToText(style));
          }
        }, delegateProvider)
    );

    // only provide reshape handles for the east, south and south-east sides,
    // so they don't clash with the custom handles
    nodeDecorator.getReshapeHandleProviderDecorator().setFactory(node ->
        new NodeReshapeHandleProvider(node, node.lookup(IReshapeHandler.class),
            HandlePositions.EAST.or(HandlePositions.SOUTH).or(HandlePositions.SOUTH_EAST)));

    nodeDecorator.getSelectionDecorator().hideImplementation();
  }

  /**
   * Returns a text description of the style configuration.
   */
  private static String styleToText(ArrowNodeStyle style) {
    return
        "Shape: " + style.getShape().name() + "\n" +
        "Direction: " + style.getDirection().name() + "\n" +
        "Angle: " + Math.round(toDegrees(style.getAngle())) + "\n" +
        "Shaft Ratio: " + (Math.round(style.getShaftRatio() * 100)) / 100d;
  }

  /**
   * Returns the given angle in degrees.
   */
  private static double toDegrees(double radians) {
    return (radians * 180) / Math.PI;
  }

  /**
   * Returns the given angle in radians.
   */
  private static double toRadians(double degrees) {
    return (degrees / 180) * Math.PI;
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
      new ArrowNodeStyleDemo().start();
    });
  }


  private final JComboBox<ArrowStyleShape> shapeBox = createShapeBox();
  private final JComboBox<ArrowNodeDirection> directionBox = createDirectionBox();
  private final JSlider angleSlider = createAngleSlider();
  private final JSlider shaftRatioSlider = createShaftRatiolider();
  private boolean optionsUpdating = false;

  /**
   * Shows the values of the given node's style in the option panel.
   */
  private void adjustOptionPanel(INode node) {
    if (node.getStyle() instanceof ArrowNodeStyle) {
      optionsUpdating = true;

      ArrowNodeStyle style = (ArrowNodeStyle) node.getStyle();
      shapeBox.setSelectedItem(style.getShape());
      directionBox.setSelectedItem(style.getDirection());
      angleSlider.setValue((int) Math.round(toDegrees(style.getAngle())));
      shaftRatioSlider.setValue((int) Math.round(style.getShaftRatio()*100));

      // update defaultArrowNodeStyle to correspond to the option panel
      if (graphComponent.getGraph().getNodeDefaults().getStyle() instanceof ArrowNodeStyle) {
        ArrowNodeStyle defaultNodeStyle = (ArrowNodeStyle) graphComponent.getGraph().getNodeDefaults().getStyle();
        defaultNodeStyle.setShape(style.getShape());
        defaultNodeStyle.setDirection(style.getDirection());
        defaultNodeStyle.setAngle(style.getAngle());
        defaultNodeStyle.setShaftRatio(style.getShaftRatio());
        graphComponent.getGraph().getNodeDefaults().setSize(node.getLayout().toSizeD());
      }

      optionsUpdating = false;
    }
  }

  /**
   * Applies the values of the option panel to the selected nodes.
   */
  private void adjustSelectedNodes() {
    if (optionsUpdating) {
      return;
    }

    ArrowStyleShape shape = (ArrowStyleShape) shapeBox.getSelectedItem();
    ArrowNodeDirection direction = (ArrowNodeDirection) directionBox.getSelectedItem();
    double angle = toRadians((angleSlider.getValue()));
    double shaftRatio = shaftRatioSlider.getValue() / 100d;

    IGraph graph = graphComponent.getGraph();
    for (INode node : graphComponent.getSelection().getSelectedNodes()) {
      if (node.getStyle() instanceof ArrowNodeStyle) {
        ArrowNodeStyle style = (ArrowNodeStyle) node.getStyle();
        applyStyleSettings(style, shape, direction, angle, shaftRatio);
        if (node.getLabels().size() == 0) {
          graph.addLabel(node, styleToText(style));
        } else {
          graph.setLabelText(node.getLabels().first(), styleToText(style));
        }
      }
    }

    ArrowNodeStyle defaultStyle = (ArrowNodeStyle) graph.getNodeDefaults().getStyle();
    applyStyleSettings(defaultStyle, shape, direction, angle, shaftRatio);

    graphComponent.repaint();
  }

  private void applyStyleSettings(
      ArrowNodeStyle style,
      ArrowStyleShape shape,
      ArrowNodeDirection direction,
      double angle,
      double shaftRatio) {
    style.setShape(shape);
    style.setDirection(direction);
    style.setAngle(angle);
    style.setShaftRatio(shaftRatio);
  }

  private JPanel createOptionPanel() {
    JPanel optionPanel = new JPanel(new GridBagLayout());
    optionPanel.setPreferredSize(new Dimension(300, Integer.MAX_VALUE));
    optionPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(5, 5, 5, 5),
        BorderFactory.createTitledBorder("ArrowNodeStyle properties")));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 0, 5);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0;
    optionPanel.add(new JLabel("Basic Shape:"), gbc);

    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.weightx = 1;
    optionPanel.add(shapeBox, gbc);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.weightx = 0;
    optionPanel.add(new JLabel("Shape Direction:"), gbc);

    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.weightx = 1;
    optionPanel.add(directionBox, gbc);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.weightx = 0;
    optionPanel.add(new JLabel("Angle (°):"), gbc);

    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.weightx = 1;
    optionPanel.add(angleSlider, gbc);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.weightx = 0;
    optionPanel.add(new JLabel("Shaft Ratio (%):"), gbc);

    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.weightx = 1;
    optionPanel.add(shaftRatioSlider, gbc);

    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    gbc.weighty = 1;
    optionPanel.add(new JPanel(), gbc);

    return optionPanel;
  }

  private JComboBox<ArrowStyleShape> createShapeBox() {
    JComboBox<ArrowStyleShape> shapeBox = new JComboBox<>(new ArrowStyleShape[]{
        ArrowStyleShape.ARROW,
        ArrowStyleShape.DOUBLE_ARROW,
        ArrowStyleShape.NOTCHED_ARROW,
        ArrowStyleShape.PARALLELOGRAM,
        ArrowStyleShape.TRAPEZOID
    });
    shapeBox.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setText(enumToText((ArrowStyleShape) value));
        return this;
      }

      private String enumToText(ArrowStyleShape shape) {
        switch (shape) {
          default:
          case ARROW: return "Arrow";
          case DOUBLE_ARROW: return "Double Arrow";
          case NOTCHED_ARROW: return "Notched Arrow";
          case PARALLELOGRAM: return "Parallelogram";
          case TRAPEZOID: return "Trapezoid";
        }
      }
    });
    shapeBox.addActionListener(e -> adjustSelectedNodes());
    return shapeBox;
  }

  private JComboBox<ArrowNodeDirection> createDirectionBox() {
    JComboBox<ArrowNodeDirection> directionBox = new JComboBox<>(new ArrowNodeDirection[]{
        ArrowNodeDirection.RIGHT,
        ArrowNodeDirection.DOWN,
        ArrowNodeDirection.LEFT,
        ArrowNodeDirection.UP
    });
    directionBox.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setText(enumToText((ArrowNodeDirection) value));
        return this;
      }

      private String enumToText(ArrowNodeDirection direction) {
        switch (direction) {
          default:
          case RIGHT: return "Right";
          case DOWN: return "Down";
          case LEFT: return "Left";
          case UP: return "Up";
        }
      }
    });
    directionBox.addActionListener(e -> adjustSelectedNodes());
    return directionBox;
  }

  private JSlider createAngleSlider() {
    JSlider slider = new JSlider(-180, 180, 90);
    slider.setMajorTickSpacing(90);
    slider.setMinorTickSpacing(1);
    slider.setSnapToTicks(true);
    slider.setPaintLabels(true);
    slider.setPaintTicks(true);
    slider.addChangeListener(e -> adjustSelectedNodes());
    return slider;
  }

  private JSlider createShaftRatiolider() {
    JSlider slider = new JSlider(0, 100, 30);
    slider.setMajorTickSpacing(25);
    slider.setMinorTickSpacing(1);
    slider.setSnapToTicks(true);
    slider.setPaintLabels(true);
    slider.setPaintTicks(true);
    slider.addChangeListener(e -> adjustSelectedNodes());
    return slider;
  }

}
