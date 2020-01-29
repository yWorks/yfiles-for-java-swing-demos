/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package viewer.largegraphs;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.GraphDecorator;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.labelmodels.DefaultLabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.EdgeSegmentLabelModel;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameterFinder;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.graph.styles.VoidLabelStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.graphml.SerializationProperties;
import com.yworks.yfiles.utils.IEnumerator;
import com.yworks.yfiles.utils.IEventArgs;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.Animator;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.EdgeStyleDecorationInstaller;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.GraphOverviewComponent;
import com.yworks.yfiles.view.GraphSelection;
import com.yworks.yfiles.view.IAnimation;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.LabelStyleDecorationInstaller;
import com.yworks.yfiles.view.NodeStyleDecorationInstaller;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.StyleDecorationZoomPolicy;
import com.yworks.yfiles.view.VisualCachingPolicy;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.ICommand;
import com.yworks.yfiles.view.input.IInputMode;
import com.yworks.yfiles.view.input.WaitInputMode;
import toolkit.AbstractDemo;
import viewer.largegraphs.animations.CircleNodeAnimation;
import viewer.largegraphs.animations.CirclePanAnimation;
import viewer.largegraphs.animations.ZoomInAndBackAnimation;
import viewer.largegraphs.styles.WrapperEdgeStyle;
import viewer.largegraphs.styles.WrapperLabelStyle;
import viewer.largegraphs.styles.WrapperNodeStyle;
import viewer.largegraphs.styles.fast.FastEdgeStyle;
import viewer.largegraphs.styles.fast.FastLabelStyle;
import viewer.largegraphs.styles.levelofdetail.LevelOfDetailLabelStyle;
import viewer.largegraphs.styles.levelofdetail.LevelOfDetailNodeStyle;
import viewer.largegraphs.styles.selection.FastEdgeSelectionStyle;
import viewer.largegraphs.styles.selection.FastLabelSelectionStyle;
import viewer.largegraphs.styles.selection.FastNodeSelectionStyle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

/**
 * This demo illustrates improvements in rendering performance for very large graphs in yFiles for Java (Swing).
 */
public class LargeGraphsDemo extends AbstractDemo {

  // region Wrapper styles so that styles are easier to change.

  // Wrapper style for edge labels.
  private WrapperLabelStyle edgeLabelStyle = new WrapperLabelStyle(null);

  // Wrapper style for edges.
  private WrapperEdgeStyle edgeStyle = new WrapperEdgeStyle(null);

  // Wrapper style for node labels
  private WrapperLabelStyle nodeLabelStyle = new WrapperLabelStyle(null);

  // Wrapper style for nodes
  private WrapperNodeStyle nodeStyle = new WrapperNodeStyle(null);


  // region Fields for GUI items

  private GraphOverviewComponent overview;

  private ShowGraph previousBtn;
  private ShowGraph nextBtn;

  private JCheckBox disableOverviewCB;
  private JCheckBox enableFastStylesCB;
  private JPanel fastStylesPane;
  private DoubleTextField hideEdgesTF;
  private DoubleTextField hideBendsTF;
  private DoubleTextField hideEdgeLabelsTF;
  private DoubleTextField sketchEdgeLabelsTF;
  private DoubleTextField nodeStyleTF;
  private DoubleTextField hideNodeLabelsTF;
  private DoubleTextField sketchNodeLabelsTF;
  private JCheckBox disableSelectionHandlesCB;
  private JCheckBox customSelectionDecorationCB;
  private JCheckBox labelModelBakingCB;
  private JCheckBox visualCachingCB;
  private JLabel zoomLbl;
  private JLabel selectedItemsLbl;
  private JLabel fpsLbl;
  private JLabel frameCountLbl;
  private FPSMeter fpsMeter;

  // endregion

  // region Configure GUI Components

  protected void configure(JRootPane rootPane) {
    Container contentPane = rootPane.getContentPane();
    contentPane.add(graphComponent, BorderLayout.CENTER);

    JToolBar toolBar = createToolBar();
    if (toolBar != null) {
      configureToolBar(toolBar);
      contentPane.add(toolBar, BorderLayout.NORTH);
    }

    JPanel rightBox = new JPanel();
    rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.Y_AXIS));
    overview = new GraphOverviewComponent();
    overview.setSize(100, 165);
    overview.setVisible(false);
    overview.setBorder(BorderFactory.createTitledBorder("Overview"));
    rightBox.add(overview);
    JComponent helpPane = createHelpPane();
    if (helpPane != null) {
      rightBox.add(helpPane);
    }
    contentPane.add(rightBox, BorderLayout.EAST);

    JPanel leftBox = new JPanel(new BorderLayout());
    JComponent settingsPane = createSettingsPane();
    JScrollPane settingScrollPane = new JScrollPane(settingsPane);
    leftBox.add(settingScrollPane, BorderLayout.CENTER);
    JComponent testControlsPane = createTestControlsPane();
    leftBox.add(testControlsPane, BorderLayout.SOUTH);
    contentPane.add(leftBox, BorderLayout.WEST);


    // Adds a menu bar to the JRootPane of the application frame in addition to the default graph component, toolbar, and help pane.
    JMenuBar menuBar = new JMenuBar();
    configureMenu(menuBar);
    rootPane.setJMenuBar(menuBar);
  }

  // region Create Performance Settings

  private JComponent createSettingsPane() {
    JPanel settingsPane = new JPanel(new GridBagLayout());
    TitledBorder performanceBorder = BorderFactory.createTitledBorder("Performance optimizations");
    settingsPane.setBorder(performanceBorder);

    GridBagConstraints gbc = new GridBagConstraints();
    int settingsGridY = 0;

    {
      disableOverviewCB = new JCheckBox("Disable overview", true);
      disableOverviewCB.setToolTipText("Disables the overview component, which can make drawing the main graph control slower");
      disableOverviewCB.addActionListener(
          e -> getPerformanceSettings().setOverviewDisabled(disableOverviewCB.isSelected()));
      gbc.anchor = GridBagConstraints.LINE_START;
      settingsPane.add(disableOverviewCB, gbc);
    }
    {
      enableFastStylesCB = new JCheckBox("Enable fast styles", true);
      enableFastStylesCB.setToolTipText("Enables level-of-detail styles and low-fidelity styles for low zoom levels");
      enableFastStylesCB.addActionListener(e -> {
        boolean enabled = enableFastStylesCB.isSelected();
        getPerformanceSettings().setFastStylesEnabled(enabled);
        fastStylesPane.setEnabled(enabled);
        for (Component component : fastStylesPane.getComponents()) {
          component.setEnabled(enabled);
        }
      });
      gbc.gridy = ++settingsGridY;
      settingsPane.add(enableFastStylesCB, gbc);
    }
    {
      fastStylesPane = new JPanel(new GridBagLayout());
      TitledBorder fastStyleBorder = BorderFactory.createTitledBorder("Fast Styles");
      fastStylesPane.setBorder(fastStyleBorder);

      int fastStyleGridY = 0;
      {
        gbc.gridy = fastStyleGridY;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        fastStylesPane.add(new JLabel("Edges"), gbc);

        JPanel spacerCol3 = new JPanel();
        Dimension col3Size = new Dimension(40, 20);
        spacerCol3.setMinimumSize(col3Size);
        spacerCol3.setMaximumSize(col3Size);
        spacerCol3.setPreferredSize(col3Size);
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        fastStylesPane.add(spacerCol3, gbc);

        JPanel spacerCol4 = new JPanel();
        Dimension col4Size = new Dimension(20, 20);
        spacerCol4.setMinimumSize(col4Size);
        spacerCol4.setMaximumSize(col4Size);
        spacerCol4.setPreferredSize(col4Size);
        gbc.gridx = 3;
        fastStylesPane.add(spacerCol4, gbc);
      }
      {
        JPanel spacerCol1 = new JPanel();
        Dimension col1Size = new Dimension(20, 20);
        spacerCol1.setMinimumSize(col1Size);
        spacerCol1.setMaximumSize(col1Size);
        spacerCol1.setPreferredSize(col1Size);
        gbc.gridx = 0;
        gbc.gridy = ++fastStyleGridY;
        fastStylesPane.add(spacerCol1, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel hideEdgesLbl = new JLabel("Hide edges shorter than");
        hideEdgesLbl.setToolTipText("Edges shorter than this many pixels are not drawn; this doesn't affect the visual result much");
        fastStylesPane.add(hideEdgesLbl, gbc);

        hideEdgesTF = new DoubleTextField(0, value -> getPerformanceSettings().setMinimumEdgeLength(value));
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_END;
        fastStylesPane.add(hideEdgesTF, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.NONE;
        fastStylesPane.add(new JLabel("px"), gbc);
      }
      {
        gbc.gridx = 1;
        gbc.gridy = ++fastStyleGridY;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel hideBendsLbl = new JLabel("Don't show bends below");
        hideBendsLbl.setToolTipText("Bends will not be shown below this zoom level");
        fastStylesPane.add(hideBendsLbl, gbc);

        hideBendsTF = new DoubleTextField(0, value -> getPerformanceSettings().setEdgeBendThreshold(value));
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_END;
        fastStylesPane.add(hideBendsTF, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.NONE;
        fastStylesPane.add(new JLabel("%"), gbc);
      }

      gbc.gridx = 0;
      gbc.gridy = ++fastStyleGridY;
      gbc.gridwidth = 2;
      gbc.anchor = GridBagConstraints.LINE_START;
      fastStylesPane.add(new JLabel("Edge labels"), gbc);
      {
        gbc.gridx = 1;
        gbc.gridy = ++fastStyleGridY;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel hideEdgeLabelsBelowLbl = new JLabel("Hide below");
        hideEdgeLabelsBelowLbl.setToolTipText("Hide edge labels below this zoom level");
        fastStylesPane.add(hideEdgeLabelsBelowLbl, gbc);


        hideEdgeLabelsTF = new DoubleTextField(50, value -> getPerformanceSettings().setEdgeLabelVisibilityThreshold(value));
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_END;
        fastStylesPane.add(hideEdgeLabelsTF, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.NONE;
        fastStylesPane.add(new JLabel("%"), gbc);
      }
      {
        gbc.gridx = 1;
        gbc.gridy = ++fastStyleGridY;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel sketchEdgeLabelsLbl = new JLabel("Sketch below");
        sketchEdgeLabelsLbl.setToolTipText("Render edge labels as sketch below this zoom level");
        fastStylesPane.add(sketchEdgeLabelsLbl, gbc);

        sketchEdgeLabelsTF = new DoubleTextField(50,
            value -> getPerformanceSettings().setEdgeLabelTextThreshold(value));
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_END;
        fastStylesPane.add(sketchEdgeLabelsTF, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.NONE;
        fastStylesPane.add(new JLabel("%"), gbc);
      }

      gbc.gridx = 0;
      gbc.gridy = ++fastStyleGridY;
      gbc.gridwidth = 2;
      gbc.anchor = GridBagConstraints.LINE_START;
      fastStylesPane.add(new JLabel("Nodes"), gbc);
      {
        gbc.gridx = 1;
        gbc.gridy = ++fastStyleGridY;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel complexNodeStyleLbl = new JLabel("Prettier node style above");
        complexNodeStyleLbl.setToolTipText("Render nodes in a more complex style above this zoom level");
        fastStylesPane.add(complexNodeStyleLbl, gbc);

        nodeStyleTF = new DoubleTextField(60, value -> getPerformanceSettings().setComplexNodeStyleThreshold(value));
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_END;
        fastStylesPane.add(nodeStyleTF, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.NONE;
        fastStylesPane.add(new JLabel("%"), gbc);
      }

      gbc.gridx = 0;
      gbc.gridy = ++fastStyleGridY;
      gbc.gridwidth = 2;
      gbc.anchor = GridBagConstraints.LINE_START;
      fastStylesPane.add(new JLabel("Node labels"), gbc);
      {
        gbc.gridx = 1;
        gbc.gridy = ++fastStyleGridY;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel hideNodeLabelsLbl = new JLabel("Hide below");
        hideNodeLabelsLbl.setToolTipText("Hide node labels below this zoom level");
        fastStylesPane.add(hideNodeLabelsLbl, gbc);

        hideNodeLabelsTF = new DoubleTextField(20,
            value -> getPerformanceSettings().setNodeLabelVisibilityThreshold(value));
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_END;
        fastStylesPane.add(hideNodeLabelsTF, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.NONE;
        fastStylesPane.add(new JLabel("%"), gbc);
      }
      {
        gbc.gridx = 1;
        gbc.gridy = ++fastStyleGridY;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        JLabel sketchNodeLabelsLbl = new JLabel("Sketch below");
        sketchNodeLabelsLbl.setToolTipText("Render node labels as sketch below this zoom level");
        fastStylesPane.add(sketchNodeLabelsLbl, gbc);

        sketchNodeLabelsTF = new DoubleTextField(40,
            value -> getPerformanceSettings().setNodeLabelTextThreshold(value));
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.LINE_END;
        fastStylesPane.add(sketchNodeLabelsTF, gbc);

        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.NONE;
        fastStylesPane.add(new JLabel("%"), gbc);
      }
      {
        JPanel dummy = new JPanel();
        gbc.gridx = 0;
        gbc.gridy = ++fastStyleGridY;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.BOTH;
        fastStylesPane.add(dummy, gbc);
      }

      gbc.gridx = 0;
      gbc.gridy = ++settingsGridY;
      gbc.gridwidth = 1;
      settingsPane.add(fastStylesPane, gbc);
    }


    {
      disableSelectionHandlesCB = new JCheckBox("Disable selection handles", true);
      disableSelectionHandlesCB.addActionListener(e -> getPerformanceSettings().setSelectionHandlesDisabled(
          disableSelectionHandlesCB.isSelected()));
      disableSelectionHandlesCB.setToolTipText("Disables selection handles, which can slow down things considerably if there are many of them");
      gbc.gridy = ++settingsGridY;
      gbc.anchor = GridBagConstraints.LINE_START;
      settingsPane.add(disableSelectionHandlesCB, gbc);
    }
    {
      customSelectionDecorationCB = new JCheckBox("Enable custom selection decoration", true);
      customSelectionDecorationCB.addActionListener(e -> getPerformanceSettings().setCustomSelectionDecoratorEnabled(
          customSelectionDecorationCB.isSelected()));
      customSelectionDecorationCB.setToolTipText("Uses faster implementations for the selection decoration");
      gbc.gridy = ++settingsGridY;
      gbc.anchor = GridBagConstraints.LINE_START;
      settingsPane.add(customSelectionDecorationCB, gbc);
    }
    {
      labelModelBakingCB = new JCheckBox("Enable label model baking", false);
      labelModelBakingCB.addActionListener(e -> getPerformanceSettings().setLabelModelBakingEnabled(
          labelModelBakingCB.isSelected()));
      labelModelBakingCB.setToolTipText("Fixes the position of labels on the canvas which makes calculating their position much cheaper");
      gbc.gridy = ++settingsGridY;
      gbc.anchor = GridBagConstraints.LINE_START;
      settingsPane.add(labelModelBakingCB, gbc);
    }
    {
      visualCachingCB = new JCheckBox("Enable visual caching", false);
      visualCachingCB.addActionListener(e -> getPerformanceSettings().setVisualCachingEnabled(visualCachingCB.isSelected()));
      visualCachingCB.setToolTipText("Caches some visuals while they are outside the view port so they don't have to be recreated so often.");
      gbc.gridy = ++settingsGridY;
      gbc.anchor = GridBagConstraints.LINE_START;
      settingsPane.add(visualCachingCB, gbc);
    }


    gbc.gridx = 0;
    gbc.gridy = ++settingsGridY;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;

    JPanel bottomPane = new JPanel();
    settingsPane.add(bottomPane, gbc);

    return settingsPane;
  }

  // endregion

  // region Create Test controls

  private JComponent createTestControlsPane() {
    JPanel testControlPane = new JPanel(new GridBagLayout());
    testControlPane.setBorder(BorderFactory.createTitledBorder("Test controls"));

    GridBagConstraints gbc = new GridBagConstraints();
    int testControlGridY = 0;

    // Information
    JPanel informationPane = new JPanel(new GridBagLayout());
    TitledBorder virtualizationPaneBorder = BorderFactory.createTitledBorder("Information");
    informationPane.setBorder(virtualizationPaneBorder);
    int informationGridY = 0;
    {
      gbc.gridx = 0;
      gbc.gridy = informationGridY;
      gbc.anchor = GridBagConstraints.LINE_START;
      JLabel zoomLevelLbl = new JLabel("Zoom level");
      zoomLevelLbl.setToolTipText("The current zoom level of the graph component");
      informationPane.add(zoomLevelLbl, gbc);

      JPanel spacer = new JPanel();
      gbc.gridx = 1;
      gbc.weightx = 1;
      informationPane.add(spacer, gbc);

      gbc.gridx = 2;
      gbc.weightx = 0;
      gbc.anchor = GridBagConstraints.LINE_END;
      zoomLbl = new JLabel();
      informationPane.add(zoomLbl, gbc);
    }
    {
      gbc.gridx = 0;
      gbc.gridy = ++informationGridY;
      gbc.anchor = GridBagConstraints.LINE_START;
      JLabel selectedItemsLbl = new JLabel("Selected items");
      selectedItemsLbl.setToolTipText("The number of currently selected elements");
      informationPane.add(selectedItemsLbl, gbc);

      JPanel spacer = new JPanel();
      gbc.gridx = 1;
      gbc.weightx = 1;
      informationPane.add(spacer, gbc);

      gbc.gridx = 2;
      gbc.weightx = 0;
      gbc.anchor = GridBagConstraints.LINE_END;
      this.selectedItemsLbl = new JLabel("0");
      informationPane.add(this.selectedItemsLbl, gbc);
    }
    {
      gbc.gridx = 0;
      gbc.gridy = ++informationGridY;
      gbc.anchor = GridBagConstraints.LINE_START;
      JLabel animationFPSLbl = new JLabel("Animation FPS");
      animationFPSLbl.setToolTipText("The current number of frames drawn per second");
      informationPane.add(animationFPSLbl, gbc);

      JPanel spacer = new JPanel();
      gbc.gridx = 1;
      gbc.weightx = 1;
      informationPane.add(spacer, gbc);

      gbc.gridx = 2;
      gbc.weightx = 0;
      gbc.anchor = GridBagConstraints.LINE_END;
      fpsLbl = new JLabel("0");
      informationPane.add(fpsLbl, gbc);
    }
    {
      gbc.gridx = 0;
      gbc.gridy = ++informationGridY;
      gbc.anchor = GridBagConstraints.LINE_START;
      JLabel fpaLbl = new JLabel("Frames in Animation");
      fpaLbl.setToolTipText("The total number of frames rendered for the current animation");
      informationPane.add(fpaLbl, gbc);

      JPanel spacer = new JPanel();
      gbc.gridx = 1;
      gbc.weightx = 1;
      informationPane.add(spacer, gbc);

      gbc.gridx = 2;
      gbc.weightx = 0;
      gbc.anchor = GridBagConstraints.LINE_END;
      frameCountLbl = new JLabel("0");
      informationPane.add(frameCountLbl, gbc);
    }


    gbc.gridx = 0;
    gbc.gridy = testControlGridY;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    testControlPane.add(informationPane, gbc);

    // Animations
    JPanel animationPane = new JPanel(new GridBagLayout());
    animationPane.setBorder(BorderFactory.createTitledBorder("Animations"));
    {
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.fill = GridBagConstraints.NONE;
      animationPane.add(new IconActionLabel("zoom.PNG", "Zooms to a random node and back", this::onZoomAnimationClicked), gbc);

      gbc.gridx = 1;
      animationPane.add(new IconActionLabel("panInCircle.PNG","Pans the viewport in a circular motion" , this::onPanAnimationClicked), gbc);

      gbc.gridx = 2;
      animationPane.add(new IconActionLabel("spiralZoom.PNG", "Combines zooming and panning at the same time", this::onSpiralZoomAnimationClicked), gbc);

      gbc.gridx = 3;
      animationPane.add(new IconActionLabel("moveNodes.PNG","Moves selected nodes randomly" , this::onNodeAnimationClicked), gbc);
    }
    gbc.gridx = 0;
    gbc.gridy = ++testControlGridY;
    testControlPane.add(animationPane, gbc);

    // Selections
    JPanel selectionPane = new JPanel(new GridBagLayout());
    selectionPane.setBorder(BorderFactory.createTitledBorder("Selection"));
    {

      gbc.gridx = 1;
      gbc.gridy = 0;
      gbc.anchor = GridBagConstraints.CENTER;
      selectionPane.add(new JLabel("Nodes"), gbc);
      gbc.gridx = 2;
      selectionPane.add(new JLabel("Edges"), gbc);
      gbc.gridx = 3;
      selectionPane.add(new JLabel("Labels"), gbc);

      gbc.gridx = 0;
      gbc.gridy = 1;
      gbc.anchor = GridBagConstraints.LINE_START;
      gbc.fill = GridBagConstraints.BOTH;
      JButton selNothingBtn = new JButton("Nothing");
      selNothingBtn.setToolTipText("Deselect everything");
      selNothingBtn.addActionListener(this::onSelectNothing);
      selectionPane.add(selNothingBtn, gbc);

      JButton sel1000NodesBtn = new JButton("1000");
      sel1000NodesBtn.setToolTipText("Select 1000 random nodes");
      sel1000NodesBtn.addActionListener(this::onSelect1000Nodes);
      gbc.gridx = 1;
      selectionPane.add(sel1000NodesBtn, gbc);

      JButton sel1000EdgesBtn = new JButton("1000");
      sel1000EdgesBtn.setToolTipText("Select 1000 random edges");
      sel1000EdgesBtn.addActionListener(this::onSelect1000Edges);
      gbc.gridx = 2;
      selectionPane.add(sel1000EdgesBtn, gbc);

      JButton sel1000LabelsBtn = new JButton("1000");
      sel1000LabelsBtn.setToolTipText("Select 1000 random labels");
      sel1000LabelsBtn.addActionListener(this::onSelect1000Labels);
      gbc.gridx = 3;
      selectionPane.add(sel1000LabelsBtn, gbc);

      JButton selEveryThingBtn = new JButton("Everything");
      selEveryThingBtn.setToolTipText("Select all nodes, edges and labels in the graph");
      selEveryThingBtn.addActionListener(this::onSelectAll);
      gbc.gridy = 2;
      gbc.gridx = 0;
      selectionPane.add(selEveryThingBtn, gbc);

      JButton selAllNodesBtn = new JButton("All");
      selAllNodesBtn.setToolTipText("Select all nodes");
      selAllNodesBtn.addActionListener(this::onSelectAllNodes);
      gbc.gridx = 1;
      selectionPane.add(selAllNodesBtn, gbc);

      JButton selAllEdgesBtn = new JButton("All");
      selAllEdgesBtn.setToolTipText("Select all edges");
      selAllEdgesBtn.addActionListener(this::onSelectAllEdges);
      gbc.gridx = 2;
      selectionPane.add(selAllEdgesBtn, gbc);

      JButton selAllLabelsBtn = new JButton("All");
      selAllLabelsBtn.setToolTipText("Select all labels");
      selAllLabelsBtn.addActionListener(this::onSelectAllLabels);
      gbc.gridx = 3;
      selectionPane.add(selAllLabelsBtn, gbc);
    }
    gbc.gridx = 0;
    gbc.gridy = ++testControlGridY;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    testControlPane.add(selectionPane, gbc);

    return testControlPane;
  }

  // endregion

  // region Configure Menu

  /**
   * Configures the given {@link javax.swing.JMenuBar}.
   *
   * @param menuBar the {@link javax.swing.JMenuBar} to configure
   */
  private void configureMenu(JMenuBar menuBar) {
    JMenu fileMenu = new JMenu("File");
    fileMenu.add(createExitAction());
    menuBar.add(fileMenu);

    JMenu editMenu = new JMenu("Edit");
    editMenu.add(createCommandMenuItemAction("Cut", ICommand.CUT, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Copy", ICommand.COPY, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Paste", ICommand.PASTE, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Duplicate", ICommand.DUPLICATE, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Delete", ICommand.DELETE, null, graphComponent));
    editMenu.addSeparator();
    editMenu.add(createCommandMenuItemAction("Undo", ICommand.UNDO, null, graphComponent));
    editMenu.add(createCommandMenuItemAction("Redo", ICommand.REDO, null, graphComponent));
    menuBar.add(editMenu);

    JMenu viewMenu = new JMenu("View");
    viewMenu.add(createCommandMenuItemAction("Increase zoom", ICommand.INCREASE_ZOOM, null, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Zoom 1:1", ICommand.ZOOM, 1, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Decrease zoom", ICommand.DECREASE_ZOOM, null, graphComponent));
    viewMenu.add(createCommandMenuItemAction("Fit Graph to Bounds", ICommand.FIT_GRAPH_BOUNDS, null, graphComponent));
    menuBar.add(viewMenu);
  }

  /**
   * Creates an {@link javax.swing.Action} to exit the demo.
   */
  private Action createExitAction() {
    AbstractAction action = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    };
    action.putValue(Action.NAME, "Exit");
    return action;
  }

  // endregion

  // region Configure ToolBar

  private JComboBox<GraphEntry> graphChooserBox;

  @Override
  protected void configureToolBar(JToolBar toolBar) {
    toolBar.add(createCommandButtonAction("Undo", "undo-16.png", ICommand.UNDO, null, graphComponent));
    toolBar.add(createCommandButtonAction("Redo", "redo-16.png", ICommand.REDO, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Cut", "cut-16.png", ICommand.CUT, null, graphComponent));
    toolBar.add(createCommandButtonAction("Copy", "copy-16.png", ICommand.COPY, null, graphComponent));
    toolBar.add(createCommandButtonAction("Paste", "paste-16.png", ICommand.PASTE, null, graphComponent));
    toolBar.add(createCommandButtonAction("Delete", "delete3-16.png", ICommand.DELETE, null, graphComponent));
    toolBar.addSeparator();
    toolBar.add(createCommandButtonAction("Zoom in", "plus2-16.png", ICommand.INCREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom 1:1", "zoom-original2-16.png", ICommand.ZOOM, 1, graphComponent));
    toolBar.add(createCommandButtonAction("Zoom out", "minus2-16.png", ICommand.DECREASE_ZOOM, null, graphComponent));
    toolBar.add(createCommandButtonAction("Fit the graph content", "fit2-16.png", ICommand.FIT_CONTENT, null, graphComponent));
    toolBar.addSeparator();
    Component comboBox = createComboBox();
    previousBtn = new ShowGraph(false);
    toolBar.add(previousBtn);
    toolBar.add(comboBox);
    nextBtn = new ShowGraph(true);
    toolBar.add(nextBtn);
  }

  /**
   * Creates the JComboBox where the various graphs are selectable.
   */
  private Component createComboBox() {
    graphChooserBox = new JComboBox<>(new GraphEntry[]{
        new GraphEntry("hierarchic_2000_2100.graphmlz", "Hierarchic: 2000 nodes, 2100 edges"),
        new GraphEntry("hierarchic_5000_5100.graphmlz", "Hierarchic: 5000 nodes, 5100 edges"),
        new GraphEntry("hierarchic_10000_11000.graphmlz", "Hierarchic: 10000 nodes, 11000 edges"),
        new GraphEntry("hierarchic_15000_16000.graphmlz", "Hierarchic: 15000 nodes, 16000 edges"),
        new GraphEntry("balloon_2000_1999.graphmlz", "Tree: 2000 nodes, 1999 edges"),
        new GraphEntry("balloon_5000_4999.graphmlz", "Tree: 5000 nodes, 4999 edges"),
        new GraphEntry("balloon_10000_9999.graphmlz", "Tree: 10000 nodes, 9999 edges"),
        new GraphEntry("balloon_15000_14999.graphmlz", "Tree: 15000 nodes, 14999 edges")
    });
    graphChooserBox.setMaximumSize(graphChooserBox.getPreferredSize());
    graphChooserBox.addActionListener(e -> readSampleGraph());
    return graphChooserBox;
  }

  /**
   * Reads the currently selected GraphML from the graphChooserBox
   */
  private void readSampleGraph() {
    GraphEntry graphEntry = (GraphEntry) this.graphChooserBox.getSelectedItem();
    loadGraphAsync(graphEntry);
  }

  /**
   * Loads a graph asynchronously and places it in the {@link GraphComponent}.
   *
   * @param graphEntry The graph information
   */
  private void loadGraphAsync(GraphEntry graphEntry) {
    graphChooserBox.setEnabled(false);
    nextBtn.setEnabled(false);
    previousBtn.setEnabled(false);

    graphComponent.lookup(WaitInputMode.class).setWaiting(true);

    updatePerformanceSettings(bestSettings[graphChooserBox.getSelectedIndex()]);
    final DefaultGraph g = new DefaultGraph();
    g.setUndoEngineEnabled(true);
    setDefaultStyles(g);
    updateStyles();
    setSelectionDecorators(g);
    updateSelectionHandlesSetting();
    updateOverviewDisabledSetting();

    // first derive the file name
    URL graphML = getClass().getResource("resources/" + graphEntry.fileName);

    new Thread(() -> {
      // then load the graph asynchronously
      try {
        GraphMLIOHandler handler = graphComponent.getGraphMLIOHandler();
        FileInputStream inputStream = new FileInputStream(new File(graphML.toURI()));
        GZIPInputStream zipInputStream = new GZIPInputStream(inputStream);

        handler.getDeserializationPropertyOverrides().set(SerializationProperties.PARSE_LABEL_SIZE, Boolean.FALSE);
        handler.read(g, zipInputStream);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }

      EventQueue.invokeLater(() ->
        {
          // update and set the graph in the AWT Event thread
          updateLabelModelBakingSetting(g);
          updateVisualCaching();

          graphChooserBox.setEnabled(true);
          updateButtons();
          graphComponent.lookup(WaitInputMode.class).setWaiting(false);
          graphComponent.setGraph(g);
          graphComponent.fitGraphBounds();
          // the commands CanExecute state might have changed - suggest a re-query. mainly to update the enabled status of the previous / next buttons.
          ICommand.invalidateRequerySuggested();
        }
      );
    }).start();

  }

  /**
   * Disables the »Previous/Next graph« buttons in the UI according to whether there is a previous/next graph to switch
   * to.
   */
  private void updateButtons() {
    nextBtn.setEnabled(graphChooserBox.getSelectedIndex() < graphChooserBox.getItemCount() - 1);
    previousBtn.setEnabled(graphChooserBox.getSelectedIndex() > 0);
  }

  private class ShowGraph extends AbstractAction {
    final boolean next;

    ShowGraph(boolean next) {
      super(next ? "Next" : "Previous");
      this.next = next;
      putValue(SHORT_DESCRIPTION, next ? "Show next graph" : "Show previous graph");
      putValue(SMALL_ICON, createIcon(next ? "arrow-right-16.png" : "arrow-left-16.png"));

      graphChooserBox.addItemListener(e -> {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          updateEnabledState();
        }
      });
      updateEnabledState();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      final JComboBox jcb = LargeGraphsDemo.this.graphChooserBox;
      if (next) {
        jcb.setSelectedIndex(jcb.getSelectedIndex() + 1);
      } else {
        jcb.setSelectedIndex(jcb.getSelectedIndex() - 1);
      }
    }

    private void updateEnabledState() {
      final JComboBox jcb = LargeGraphsDemo.this.graphChooserBox;
      if (next) {
        setEnabled(jcb.getSelectedIndex() < jcb.getItemCount() - 1);
      } else {
        setEnabled(jcb.getSelectedIndex() > 0);
      }
    }
  }

  /**
   * Entry of the {@link #graphChooserBox} containing the file name and the display name of a sample graph.
   */
  private static class GraphEntry {
    private String fileName;
    private String displayName;

    public GraphEntry(String fileName, String displayName) {
      this.fileName = fileName;
      this.displayName = displayName;
    }

    @Override
    public String toString() {
      return displayName;
    }
  }

  // endregion

  // endregion

  @Override
  public void initialize() {
    initializeInformationPane();
    initializeInputMode();
    initializePerformanceSettings();
  }

  @Override
  public void onVisible() {
    graphChooserBox.setSelectedIndex(0);
  }

  /**
   * Initializes the listener to update the information on the Information pane.
   */
  private void initializeInformationPane() {
    graphComponent.addZoomChangedListener(
        (source, args) -> zoomLbl.setText(((int) (graphComponent.getZoom() * 10000)) / 100.0 + " %"));
    graphComponent.getSelection().addItemSelectionChangedListener((source, args) -> {
      IGraphSelection s = graphComponent.getSelection();
      int selectedItemCount = s.getSelectedBends().size() + s.getSelectedEdges().size() + s.getSelectedLabels().size() + s.getSelectedNodes().size() + s.getSelectedPorts().size();
      selectedItemsLbl.setText(Integer.toString(selectedItemCount));
    });

    fpsMeter = new FPSMeter(graphComponent, () -> {
      fpsLbl.setText(fpsMeter.getFps());
      frameCountLbl.setText(Integer.toString(fpsMeter.getFrameCount()));
    });
  }

  /**
   * Initializes the input mode for the {@link GraphComponent}.
   */
  private void initializeInputMode() {
    GraphEditorInputMode geim = new GraphEditorInputMode();
    {
      // These two sub-input modes need to look at all elements in the graph to determine whether they are
      // responsible for a beginning drag. This slows down initial UI response to marquee selection or panning the
      // viewport in graphs with a large number of items.
      // Depending on the exact needs it might be better to enable those only when edges or bends actually need to
      // be created.
      // Of course, using a GraphViewerInputMode here sidesteps the problem completely, if no graph editing is
      // needed.
      geim.getCreateEdgeInputMode().setEnabled(false);
      geim.getCreateBendInputMode().setEnabled(false);
    }
    ;
    graphComponent.setInputMode(geim);
  }

  // region Performance setting helpers

  // Optimal settings for the sample graphs.
  private PerformanceSettings[] bestSettings;

  private PerformanceSettings performanceSettings;

  /**
   * Gets the performance settings for the current graph.
   */
  public PerformanceSettings getPerformanceSettings() {
    return performanceSettings;
  }

  /**
   * Sets the performance settings for the current graph.
   * <p>
   * For each sample graph, prepared settings are applied.
   * </p>
   */
  public void setPerformanceSettings(PerformanceSettings performanceSettings) {
    this.performanceSettings = performanceSettings;
    disableOverviewCB.setSelected(performanceSettings.isOverviewDisabled());
    enableFastStylesCB.setSelected(performanceSettings.isFastStylesEnabled());
    try {
      hideEdgesTF.setValue(performanceSettings.getMinimumEdgeLength());
      hideEdgesTF.commitEdit();
      hideBendsTF.setValue(performanceSettings.getEdgeBendThreshold());
      hideBendsTF.commitEdit();
      hideEdgeLabelsTF.setValue(performanceSettings.getEdgeLabelVisibilityThreshold());
      hideEdgeLabelsTF.commitEdit();
      sketchEdgeLabelsTF.setValue(performanceSettings.getEdgeLabelTextThreshold());
      sketchEdgeLabelsTF.commitEdit();
      nodeStyleTF.setValue(performanceSettings.getComplexNodeStyleThreshold());
      nodeStyleTF.commitEdit();
      hideNodeLabelsTF.setValue(performanceSettings.getNodeLabelVisibilityThreshold());
      hideNodeLabelsTF.commitEdit();
      sketchNodeLabelsTF.setValue(performanceSettings.getNodeLabelTextThreshold());
      sketchNodeLabelsTF.commitEdit();
    } catch (ParseException e) {
    }

    disableSelectionHandlesCB.setSelected(performanceSettings.isSelectionHandlesDisabled());
    customSelectionDecorationCB.setSelected(performanceSettings.isCustomSelectionDecoratorEnabled());
    labelModelBakingCB.setSelected(performanceSettings.isLabelModelBakingEnabled());
    visualCachingCB.setSelected(performanceSettings.isVisualCachingEnabled());
  }

  /**
   * Initializes the list of optimal performance settings for the sample graphs.
   */
  private void initializePerformanceSettings() {

    PerformanceSettings ps1 = new PerformanceSettings();
    {
      ps1.setMinimumEdgeLength(0);
      ps1.setEdgeBendThreshold(0);
      ps1.setEdgeLabelVisibilityThreshold(50);
      ps1.setNodeLabelVisibilityThreshold(20);
      ps1.setNodeLabelTextThreshold(40);
      ps1.setEdgeLabelTextThreshold(50);
      ps1.setComplexNodeStyleThreshold(60);
      ps1.setOverviewDisabled(true);
      ps1.setFastStylesEnabled(true);
      ps1.setSelectionHandlesDisabled(true);
      ps1.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps2 = new PerformanceSettings();
    {
      ps2.setMinimumEdgeLength(10);
      ps2.setEdgeBendThreshold(50);
      ps2.setEdgeLabelVisibilityThreshold(80);
      ps2.setNodeLabelVisibilityThreshold(20);
      ps2.setNodeLabelTextThreshold(40);
      ps2.setEdgeLabelTextThreshold(80);
      ps2.setComplexNodeStyleThreshold(100);
      ps2.setOverviewDisabled(true);
      ps2.setFastStylesEnabled(true);
      ps2.setSelectionHandlesDisabled(true);
      ps2.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps3 = new PerformanceSettings();
    {
      ps3.setLabelModelBakingEnabled(true);
      ps3.setVisualCachingEnabled(true);
      ps3.setMinimumEdgeLength(10);
      ps3.setEdgeBendThreshold(50);
      ps3.setEdgeLabelVisibilityThreshold(80);
      ps3.setNodeLabelVisibilityThreshold(20);
      ps3.setNodeLabelTextThreshold(40);
      ps3.setEdgeLabelTextThreshold(80);
      ps3.setComplexNodeStyleThreshold(100);
      ps3.setOverviewDisabled(true);
      ps3.setFastStylesEnabled(true);
      ps3.setSelectionHandlesDisabled(true);
      ps3.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps4 = new PerformanceSettings();
    {
      ps4.setLabelModelBakingEnabled(true);
      ps4.setVisualCachingEnabled(true);
      ps4.setMinimumEdgeLength(10);
      ps4.setEdgeBendThreshold(50);
      ps4.setEdgeLabelVisibilityThreshold(80);
      ps4.setNodeLabelVisibilityThreshold(20);
      ps4.setNodeLabelTextThreshold(40);
      ps4.setEdgeLabelTextThreshold(80);
      ps4.setComplexNodeStyleThreshold(100);
      ps4.setOverviewDisabled(true);
      ps4.setFastStylesEnabled(true);
      ps4.setSelectionHandlesDisabled(true);
      ps4.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps5 = new PerformanceSettings();
    {
      ps5.setMinimumEdgeLength(0);
      ps5.setEdgeBendThreshold(0);
      ps5.setEdgeLabelVisibilityThreshold(50);
      ps5.setNodeLabelVisibilityThreshold(20);
      ps5.setNodeLabelTextThreshold(40);
      ps5.setEdgeLabelTextThreshold(50);
      ps5.setComplexNodeStyleThreshold(60);
      ps5.setOverviewDisabled(true);
      ps5.setFastStylesEnabled(true);
      ps5.setSelectionHandlesDisabled(true);
      ps5.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps6 = new PerformanceSettings();
    {
      ps6.setMinimumEdgeLength(10);
      ps6.setEdgeBendThreshold(0);
      ps6.setEdgeLabelVisibilityThreshold(50);
      ps6.setNodeLabelVisibilityThreshold(20);
      ps6.setNodeLabelTextThreshold(40);
      ps6.setEdgeLabelTextThreshold(50);
      ps6.setComplexNodeStyleThreshold(60);
      ps6.setOverviewDisabled(true);
      ps6.setFastStylesEnabled(true);
      ps6.setSelectionHandlesDisabled(true);
      ps6.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps7 = new PerformanceSettings();
    {
      ps7.setLabelModelBakingEnabled(true);
      ps7.setVisualCachingEnabled(true);
      ps7.setMinimumEdgeLength(10);
      ps7.setEdgeBendThreshold(0);
      ps7.setEdgeLabelVisibilityThreshold(50);
      ps7.setNodeLabelVisibilityThreshold(20);
      ps7.setNodeLabelTextThreshold(40);
      ps7.setEdgeLabelTextThreshold(50);
      ps7.setComplexNodeStyleThreshold(60);
      ps7.setOverviewDisabled(true);
      ps7.setFastStylesEnabled(true);
      ps7.setSelectionHandlesDisabled(true);
      ps7.setCustomSelectionDecoratorEnabled(true);
    }
    PerformanceSettings ps8 = new PerformanceSettings();
    {
      ps8.setLabelModelBakingEnabled(true);
      ps8.setVisualCachingEnabled(true);
      ps8.setMinimumEdgeLength(10);
      ps8.setEdgeBendThreshold(0);
      ps8.setEdgeLabelVisibilityThreshold(80);
      ps8.setNodeLabelVisibilityThreshold(30);
      ps8.setNodeLabelTextThreshold(40);
      ps8.setEdgeLabelTextThreshold(80);
      ps8.setComplexNodeStyleThreshold(100);
      ps8.setOverviewDisabled(true);
      ps8.setFastStylesEnabled(true);
      ps8.setSelectionHandlesDisabled(true);
      ps8.setCustomSelectionDecoratorEnabled(true);
    }
    bestSettings = new PerformanceSettings[]{ps1, ps2, ps3, ps4, ps5, ps6, ps7, ps8};
  }

  /**
   * Sets a new {@link PerformanceSettings} instance for the sample graphs and updates the GUI.
   * <p>
   * Since the instance is mutable, this will assign a copy to {@link #setPerformanceSettings(PerformanceSettings)}.
   * </p>
   *
   * @param newSettings The new settings instance.
   */
  private void updatePerformanceSettings(PerformanceSettings newSettings) {
    if (getPerformanceSettings() != null) {
      getPerformanceSettings().setChangedCallback(null);
    }
    setPerformanceSettings(PerformanceSettings.getCopy(newSettings));
    getPerformanceSettings().setChangedCallback(this::onPerformanceSettingsChanged);
  }

  /**
   * Called when a property in {@link #getPerformanceSettings()} changes.
   * <p>
   * Styles and other settings are updated if needed, depending on the property that changed.
   * </p>
   *
   * @param propertyName The name of the property that changed.
   */
  private void onPerformanceSettingsChanged(String propertyName) {
    // If the property name is the empty string or null, it indicates that every property changed.
    if (propertyName == null || propertyName.isEmpty()) {
      updateStyles();
      updateSelectionHandlesSetting();
      updateLabelModelBakingSetting(graphComponent.getGraph());
      updateVisualCaching();
      updateOverviewDisabledSetting();
      graphComponent.repaint();
      refreshSelection();
      return;
    }
    switch (propertyName) {
      case "FastStylesEnabled":
      case "MinimumEdgeLength":
      case "EdgeBendThreshold":
      case "EdgeLabelVisibilityThreshold":
      case "NodeLabelVisibilityThreshold":
      case "NodeLabelTextThreshold":
      case "EdgeLabelTextThreshold":
      case "ComplexNodeStyleThreshold":
        updateStyles();
        break;
      case "SelectionHandlesDisabled":
        updateSelectionHandlesSetting();
        refreshSelection();
        break;
      case "CustomSelectionDecoratorEnabled":
        refreshSelection();
        break;
      case "LabelModelBakingEnabled":
        updateLabelModelBakingSetting(graphComponent.getGraph());
        break;
      case "VisualCachingEnabled":
        updateVisualCaching();
        break;
      case "OverviewDisabled":
        updateOverviewDisabledSetting();
        break;
    }
  }

  private void updateOverviewDisabledSetting() {
    boolean b = getPerformanceSettings().isOverviewDisabled();
    overview.setGraphComponent(b ? null : graphComponent);
    overview.setVisible(!b);
  }

  private void setDefaultStyles(IGraph graph) {
    graph.getNodeDefaults().setStyle(nodeStyle);
    graph.getEdgeDefaults().setStyle(edgeStyle);
    graph.getNodeDefaults().getLabelDefaults().setStyle(nodeLabelStyle);
    graph.getEdgeDefaults().getLabelDefaults().setStyle(edgeLabelStyle);
  }

  /**
   * Updates the styles according to the values in {@link #getPerformanceSettings()}.
   * <p>
   * See {@link PerformanceSettings#isFastStylesEnabled()} for a detailed description of the optimizations involved.
   * </p>
   */
  private void updateStyles() {
    PerformanceSettings p = getPerformanceSettings();

    // A few colors we need more than once
    Color darkOrange = Colors.DARK_ORANGE;
    Pen black = Pen.getBlack();
    Color white = Colors.WHITE;

    // Default label styles (those are also used at high zoom levels)
    DefaultLabelStyle simpleEdgeLabelStyle = new DefaultLabelStyle();
    simpleEdgeLabelStyle.setBackgroundPaint(white);
    DefaultLabelStyle simpleNodeLabelStyle = new DefaultLabelStyle();

    if (p.isFastStylesEnabled()) {
      // Nodes
      LevelOfDetailNodeStyle lodns = new LevelOfDetailNodeStyle();
      ShapeNodeStyle sns1 = new ShapeNodeStyle();
      sns1.setShape(ShapeNodeShape.RECTANGLE);
      sns1.setPen(null);
      sns1.setPaint(darkOrange);
      lodns.getStyles().add(0, sns1);

      ShapeNodeStyle sns2 = new ShapeNodeStyle();
      sns2.setShape(ShapeNodeShape.ROUND_RECTANGLE);
      sns2.setPen(black);
      sns2.setPaint(darkOrange);
      lodns.getStyles().add(p.getComplexNodeStyleThreshold() / 100 / 2, sns2);

      ShinyPlateNodeStyle spns = new ShinyPlateNodeStyle();
      spns.setPen(black);
      spns.setPaint(darkOrange);
      lodns.getStyles().add(p.getComplexNodeStyleThreshold() / 100, spns);
      nodeStyle.setStyle(lodns);

      // Edges
      edgeStyle.setStyle(new FastEdgeStyle(p.getEdgeBendThreshold() / 100, p.getMinimumEdgeLength()));

      // Node labels
      LevelOfDetailLabelStyle lodls1 = new LevelOfDetailLabelStyle();
      lodls1.getStyles().add(0, VoidLabelStyle.INSTANCE);
      lodls1.getStyles().add(p.getNodeLabelVisibilityThreshold() / 100, new FastLabelStyle(true));
      lodls1.getStyles().add(p.getNodeLabelTextThreshold() / 100, simpleNodeLabelStyle);
      nodeLabelStyle.setStyle(lodls1);

      // Edge labels
      LevelOfDetailLabelStyle lodls2 = new LevelOfDetailLabelStyle();
      lodls2.getStyles().add(0, VoidLabelStyle.INSTANCE);
      lodls2.getStyles().add(p.getEdgeLabelVisibilityThreshold() / 100, new FastLabelStyle(true, white));
      lodls2.getStyles().add(p.getEdgeLabelTextThreshold() / 100, simpleEdgeLabelStyle);
      edgeLabelStyle.setStyle(lodls2);
    } else {
      ShapeNodeStyle sns = new ShapeNodeStyle();
      sns.setShape(ShapeNodeShape.RECTANGLE);
      sns.setPen(black);
      sns.setPaint(darkOrange);
      nodeStyle.setStyle(sns);
      edgeStyle.setStyle(new PolylineEdgeStyle());
      edgeLabelStyle.setStyle(simpleEdgeLabelStyle);
      nodeLabelStyle.setStyle(simpleNodeLabelStyle);
    }

    // Repaint the graph control to update the visuals according to the changed style
    graphComponent.repaint();
  }

  /**
   * Sets the selection decorators on the given {@link IGraph} instance.
   * <p>
   * This actually sets the selection decorator implementation by using a custom predicate which simply queries the
   * current {@link #getPerformanceSettings()}. Thus the decoration is always up-to-date; the only thing that's needed
   * when the setting changes is to re-select all selected items to re-create the respective selection decoration
   * visuals.
   * </p>
   *
   * @param g The graph.
   */
  private void setSelectionDecorators(IGraph g) {
    NodeStyleDecorationInstaller nodeStyleDecorationInstaller = new NodeStyleDecorationInstaller();
    nodeStyleDecorationInstaller.setNodeStyle(new FastNodeSelectionStyle(null, new Pen(Colors.DARK_RED, 4)));
    nodeStyleDecorationInstaller.setZoomPolicy(StyleDecorationZoomPolicy.WORLD_COORDINATES);
    nodeStyleDecorationInstaller.setMargins(InsetsD.EMPTY);

    EdgeStyleDecorationInstaller edgeStyleDecorationInstaller = new EdgeStyleDecorationInstaller();
    edgeStyleDecorationInstaller.setEdgeStyle(new FastEdgeSelectionStyle(new Pen(Colors.DARK_RED, 3)));
    edgeStyleDecorationInstaller.setZoomPolicy(StyleDecorationZoomPolicy.WORLD_COORDINATES);

    LabelStyleDecorationInstaller labelStyleDecorationInstaller = new LabelStyleDecorationInstaller();
    labelStyleDecorationInstaller.setLabelStyle(new FastLabelSelectionStyle(null, new Pen(Colors.LIGHT_GRAY, 4)));
    labelStyleDecorationInstaller.setZoomPolicy(StyleDecorationZoomPolicy.WORLD_COORDINATES);
    labelStyleDecorationInstaller.setMargins(InsetsD.EMPTY);

    GraphDecorator decorator = g.getDecorator();
    decorator.getNodeDecorator().getSelectionDecorator().setImplementation(
        a -> getPerformanceSettings().isCustomSelectionDecoratorEnabled(),
        nodeStyleDecorationInstaller);
    decorator.getEdgeDecorator().getSelectionDecorator().setImplementation(
        a -> getPerformanceSettings().isCustomSelectionDecoratorEnabled(),
        edgeStyleDecorationInstaller);
    decorator.getLabelDecorator().getSelectionDecorator().setImplementation(
        a -> getPerformanceSettings().isCustomSelectionDecoratorEnabled(),
        labelStyleDecorationInstaller);
  }

  /**
   * Updates the input mode to reflect the current value of the {@link PerformanceSettings#isSelectionHandlesDisabled()}
   * setting.
   * <p>
   * See {@link PerformanceSettings#isSelectionHandlesDisabled()} for a rationale for this optimization.
   * </p>
   */
  private void updateSelectionHandlesSetting() {
    PerformanceSettings p = getPerformanceSettings();
    IInputMode inputMode = graphComponent.getInputMode();

    if (inputMode instanceof GraphEditorInputMode) {
      ((GraphEditorInputMode) inputMode).setShowHandleItems(
          p.isSelectionHandlesDisabled() ? GraphItemTypes.NONE : GraphItemTypes.ALL);
    }
  }

  /**
   * Updates all labels in the graph according to the current value of the {@link PerformanceSettings#isLabelModelBakingEnabled()}
   * setting.
   * <p>
   * See the {@link PerformanceSettings#isLabelModelBakingEnabled()} for a rationale for this optimization.
   * </p>
   * <p>
   * When activating this setting, all labels get a {@link FreeLabelModel}. An {@link ILabelModelParameterFinder}
   * instance from the label model helps finding the correct parameter so that the labels don't change their positions.
   * When disabling this setting, the same process is used, just in reverse, that is, the respective label model for
   * node and edge labels is used and its parameter finder asked for a good parameter.
   * </p>
   * <p>
   * Labels using the {@link FreeLabelModel} are positioned absolutely in the canvas. Thus they won't move when their
   * owners move. If there is no need of getting the last bit of performance out of yFiles, {@link FreeNodeLabelModel}
   * and {@link FreeEdgeLabelModel} can be used instead. They are a bit slower than {@link FreeLabelModel}, but have the
   * benefit that they are anchored relative to their owner, creating a less jarring experience than labels that just
   * stay where they were when their owner moves.
   * </p>
   * <p>
   * Another option (not shown in this demo) would be to convert between the label models on affected labels prior to
   * and after an edit operation, such as moving nodes, adding or moving bends to edges, etc. so that the editing
   * experience uses the expensive label models, but all non-affected labels (and after finishing the edit, all labels)
   * use a {@link FreeLabelModel}.
   * </p>
   *
   * @param graph The graph whose labels shall be updated.
   */
  private void updateLabelModelBakingSetting(IGraph graph) {
    ILabelModel bakedNodeLabelModel;
    ILabelModel bakedEdgeLabelModel;
    ILabelModel bakedPortLabelModel;
    if (getPerformanceSettings().isLabelModelBakingEnabled()) {
      bakedNodeLabelModel = FreeLabelModel.INSTANCE;
      bakedEdgeLabelModel = FreeLabelModel.INSTANCE;
      bakedPortLabelModel = FreeLabelModel.INSTANCE;
    } else {
      bakedNodeLabelModel = new InteriorLabelModel();
      bakedEdgeLabelModel = new EdgeSegmentLabelModel();
      bakedPortLabelModel = new ExteriorLabelModel();
    }

    for (ILabel l : graph.getLabels()) {
      ILabelModel bakedLabelModel = null;
      if (l.getOwner() instanceof INode) {
        bakedLabelModel = bakedNodeLabelModel;
      } else if (l.getOwner() instanceof IEdge) {
        bakedLabelModel = bakedEdgeLabelModel;
      } else if (l.getOwner() instanceof IPort) {
        bakedLabelModel = bakedPortLabelModel;
      }
      ILabelModelParameterFinder finder = bakedLabelModel.lookup(ILabelModelParameterFinder.class);
      finder = finder != null ? finder : DefaultLabelModelParameterFinder.INSTANCE;
      ILabelModelParameter param = finder.findBestParameter(l, bakedLabelModel, l.getLayout());
      graph.setLabelLayoutParameter(l, param);
    }
  }

  /**
   * Updates the {@link CanvasComponent#setVisualCaching(VisualCachingPolicy) caching policy}.
   * <p>
   * See the {@link PerformanceSettings#isVisualCachingEnabled()} for a rationale for this optimization.
   * </p>
   */
  private void updateVisualCaching() {
    boolean visualCachingEnabled = getPerformanceSettings().isVisualCachingEnabled();
    graphComponent.setVisualCaching(visualCachingEnabled ? VisualCachingPolicy.STRONG : VisualCachingPolicy.NEVER);
  }

  /**
   * De-selects all elements and re-selects them again.
   * <p>
   * This is needed to update the visuals for the handles or selection decoration.
   * </p>
   */
  private void refreshSelection() {
    IGraphSelection oldSelection = graphComponent.getSelection();
    graphComponent.setSelection(new GraphSelection(graphComponent.getGraph()));
    graphComponent.setSelection(oldSelection);
  }

  // endregion

  // region Selection helpers

  /**
   * Clears the selection.
   */
  void onSelectNothing(ActionEvent e) {
    graphComponent.getSelection().clear();
  }

  /**
   * Selects all nodes, edges and labels.
   */
  void onSelectAll(ActionEvent e) {
    onSelectAllNodes(e);
    onSelectAllEdges(e);
    onSelectAllLabels(e);
  }

  /**
   * Randomly selects 1000 nodes.
   */
  void onSelect1000Nodes(ActionEvent e) {
    select1000((IListEnumerable) graphComponent.getGraph().getNodes());
  }

  /**
   * Randomly selects 1000 edges.
   */
  void onSelect1000Edges(ActionEvent e) {
    select1000((IListEnumerable) graphComponent.getGraph().getEdges());
  }

  /**
   * Randomly selects 1000 labels.
   */
  void onSelect1000Labels(ActionEvent e) {
    select1000((IListEnumerable) graphComponent.getGraph().getLabels());
  }

  private void select1000(IListEnumerable<IModelItem> items) {
    IEnumerator<IModelItem> enumerator = items.enumerator();
    Object[] shuffled = shuffle(enumerator.toArray(enumerator, items.size()));
    for (int i = 0; i < 1000; i++) {
      graphComponent.getSelection().setSelected((IModelItem) shuffled[i], true);
    }
  }

  /**
   * Fisher Yates Shuffle for arrays.
   *
   * @param {yfiles.collections.IList.<T>} array
   * @return {yfiles.collections.IList.<T>} Shuffled Array.
   * @template T
   */
  Object[] shuffle(Object[] array) {
    int m = array.length;
    Object t;
    int i;
    while (m > 0) {
      // pick a remaining element
      i = getRandomInt(m);
      m--;
      // swap with current element
      t = array[m];
      array[m] = array[i];
      array[i] = t;
    }
    return array;
  }

  int getRandomInt(int upper) {
    int result = (int) Math.floor(Math.random() * upper);
    return result;
  }

  /**
   * Selects all nodes.
   */
  void onSelectAllNodes(ActionEvent e) {
    IListEnumerable<INode> nodes = graphComponent.getGraph().getNodes();
    for (INode node : nodes) {
      graphComponent.getSelection().setSelected(node, true);
    }
  }

  /**
   * Selects all edges.
   */
  void onSelectAllEdges(ActionEvent e) {
    IListEnumerable<IEdge> edges = graphComponent.getGraph().getEdges();
    for (IEdge edge : edges) {
      graphComponent.getSelection().setSelected(edge, true);
    }

  }

  /**
   * Selects all labels.
   */
  void onSelectAllLabels(ActionEvent e) {
    IListEnumerable<ILabel> labels = graphComponent.getGraph().getLabels();
    for (ILabel label : labels) {
      graphComponent.getSelection().setSelected(label, true);
    }

  }

  // endregion

  // region Animations

  /**
   * Called when The 'Zoom animation' button was clicked.
   */
  void onZoomAnimationClicked() {
    startAnimation();
    INode node = getRandomNode();
    graphComponent.setCenter(node.getLayout().getCenter());

    IAnimation animation = new ZoomInAndBackAnimation(graphComponent, 10, Duration.ofSeconds(5));
    Animator animator = new Animator(graphComponent);
    animator.animate(animation, this::endAnimation);
  }

  /**
   * Called when the 'Pan animation' button was clicked.
   */
  void onPanAnimationClicked() {
    startAnimation();
    IAnimation animation = new CirclePanAnimation(graphComponent, 5, Duration.ofSeconds(5));
    Animator animator = new Animator(graphComponent);
    animator.animate(animation, this::endAnimation);
  }

  /**
   * Called when the 'Spiral zoom animation' button was clicked.
   */
  void onSpiralZoomAnimationClicked() {
    startAnimation();
    INode node = getRandomNode();
    graphComponent.setCenter(
        PointD.add(node.getLayout().getCenter(), new PointD(graphComponent.getViewport().getWidth() / 4, 0)));

    IAnimation zoom = new ZoomInAndBackAnimation(graphComponent, 10, Duration.ofSeconds(10));
    IAnimation pan = new CirclePanAnimation(graphComponent, 14, Duration.ofSeconds(10));
    IAnimation animation = IAnimation.createParallelAnimation(zoom, pan);
    Animator animator = new Animator(graphComponent);
    animator.animate(animation, this::endAnimation);
  }

  /**
   * Called when 'Move nodes' button was clicked.
   */
  void onNodeAnimationClicked() {
    startAnimation();
    IGraphSelection selection = graphComponent.getSelection();
    // If there is nothing selected, just use a random node
    if (selection.getSelectedNodes().size() == 0) {
      selection.setSelected(getRandomNode(), true);
    }

    ArrayList<INode> selectedNodes = new ArrayList<>(selection.getSelectedNodes().size());
    selection.getSelectedNodes().forEach(selectedNodes::add);

    IAnimation animation = new CircleNodeAnimation(graphComponent.getGraph(), selectedNodes,
        graphComponent.getViewport().getWidth() / 10, 10, Duration.ofSeconds(10));
    Animator animator = new Animator(graphComponent);
    animator.animate(animation, (source, args) -> {
      endAnimation(source, args);
      graphComponent.invalidate();
    });
  }

  private void startAnimation() {
    fpsMeter.setRecording(true);
  }

  private void endAnimation(Object source, IEventArgs args) {
    fpsMeter.setRecording(false);
  }

  private Random rnd = new Random(42);

  /**
   * Returns a random node from the graph.
   *
   * @return A random node from the graph.
   */
  private INode getRandomNode() {
    IListEnumerable<INode> nodes = graphComponent.getGraph().getNodes();
    INode node = nodes.getItem(rnd.nextInt(nodes.size()));
    return node;
  }


  // endregion

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new LargeGraphsDemo().start();
    });
  }

  // region Utility classes

  static class DoubleTextField extends JFormattedTextField {
    public DoubleTextField(double value, Consumer<Double> handler) {
      super(NumberFormat.getNumberInstance());
      setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
      setValue(value);
      addActionListener(e -> handler.accept(getDoubleValue()));
      addFocusListener(new FocusAdapter() {
        public void focusLost(FocusEvent e) {
          handler.accept(getDoubleValue());
        }
      });
    }

    private double getDoubleValue() {
      try {
        commitEdit();
      } catch (ParseException e) {
      }
      Object value = getValue();
      if (value instanceof Integer) {
        return ((Integer) value);
      } else if (value instanceof Double) {
        return ((Double) value).doubleValue();
      } else if (value instanceof Long) {
        return ((Long) value).doubleValue();
      } else {
        return 0;
      }
    }
  }

  static class IconActionLabel extends JLabel {
    private Border hoverBorder = BorderFactory.createLineBorder(Color.BLUE);
    private Border noBorder = BorderFactory.createLineBorder(Colors.TRANSPARENT);

    public IconActionLabel(String imageName, String tooltip, Runnable action) {
      super();
      setIcon(new ImageIcon(getClass().getResource("resources/" + imageName)));
      setToolTipText(tooltip);
      addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          action.run();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
          super.mouseEntered(e);
          setBorder(hoverBorder);
        }

        @Override
        public void mouseExited(MouseEvent e) {
          super.mouseExited(e);
          setBorder(noBorder);
        }
      });
      setBorder(noBorder);
    }
  }

  // endregion
}
