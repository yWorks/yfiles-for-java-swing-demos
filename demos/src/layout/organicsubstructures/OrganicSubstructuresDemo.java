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
package layout.organicsubstructures;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.organic.ChainSubstructureStyle;
import com.yworks.yfiles.layout.organic.CycleSubstructureStyle;
import com.yworks.yfiles.layout.organic.OrganicLayout;
import com.yworks.yfiles.layout.organic.OrganicLayoutData;
import com.yworks.yfiles.layout.organic.ParallelSubstructureStyle;
import com.yworks.yfiles.layout.organic.StarSubstructureStyle;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IGraphSelection;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.PopulateItemPopupMenuEventArgs;
import toolkit.AbstractDemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.ListCellRenderer;

/**
 * Shows how {@link OrganicLayout} handles substructures and node types.
 */
public class OrganicSubstructuresDemo extends AbstractDemo {
  private final JComboBox<CycleSubstructureStyle> cycleStyles;
  private final JComboBox<ChainSubstructureStyle> chainStyles;
  private final JComboBox<StarSubstructureStyle> starStyles;
  private final JComboBox<ParallelSubstructureStyle> parallelStyles;
  private final JCheckBox useEdgeGrouping;
  private final JCheckBox considerNodeTypes;
  private final JCheckBox separateParallel;
  private final JCheckBox separateStar;
  private boolean layoutPending;

  /**
   * Initializes a new {@code OrganicSubstructuresDemo} instance.
   */
  public OrganicSubstructuresDemo() {
    cycleStyles = newStyleChooser(CycleSubstructureStyle.values());
    chainStyles = newStyleChooser(ChainSubstructureStyle.values());
    starStyles = newStyleChooser(StarSubstructureStyle.values());
    parallelStyles = newStyleChooser(ParallelSubstructureStyle.values());
    useEdgeGrouping = newCheckBox();
    considerNodeTypes = newCheckBox();
    separateParallel = newCheckBox();
    separateStar = newCheckBox();
  }

  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new OrganicSubstructuresDemo().start();
    });
  }

  /**
   * Initializes user interaction and loads an initial sample graph.
   */
  @Override
  public void initialize() {
    // enable interactive editing
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    inputMode.setSelectableItems(GraphItemTypes.EDGE.or(GraphItemTypes.NODE));
    inputMode.setAddLabelAllowed(false);
    inputMode.setEditLabelAllowed(false);
    inputMode.addPopulateItemPopupMenuListener(this::onPopulateItemPopupMenu);
    graphComponent.setInputMode(inputMode);

    loadSample("mixed_large");
  }

  /**
   * Calculates a new graph layout and optionally applies the new layout in an
   * animated fashion. This method creates and configures a new organic layout
   * algorithm for this purpose.
   */
  private void runLayout( boolean animate ) {
    if (layoutPending) {
      return;
    }
    layoutPending = true;

    // configure the organic layout algorithm
    OrganicLayout algorithm = new OrganicLayout();

    //configure some basic settings
    algorithm.setDeterministicModeEnabled(true);
    algorithm.setMinimumNodeDistance(20);
    algorithm.setPreferredEdgeLength(60);

    // configure substructure styles (cycles, chains, parallel structures, star)
    algorithm.setCycleSubstructureStyle(getSelectedStyle(cycleStyles));
    algorithm.setChainSubstructureStyle(getSelectedStyle(chainStyles));
    algorithm.setParallelSubstructureStyle(getSelectedStyle(parallelStyles));
    algorithm.setStarSubstructureStyle(getSelectedStyle(starStyles));

    //configure type separation for parallel and star substructures
    algorithm.setParallelSubstructureTypeSeparationEnabled(separateParallel.isSelected());
    algorithm.setStarSubstructureTypeSeparationEnabled(separateStar.isSelected());

    // configure data-driven features for the organic layout algorithm by using OrganicLayoutData
    OrganicLayoutData layoutData = new OrganicLayoutData();

    if (useEdgeGrouping.isSelected()) {
      // if desired, define edge grouping on the organic layout data
      layoutData.setSourceGroupIds("groupAll");
      layoutData.setTargetGroupIds("groupAll");
    }

    if (considerNodeTypes.isSelected()) {
      // if types should be considered define a delegate on the respective layout data property
      // that queries the type from the node's tag
      layoutData.setNodeTypes(this::getNodeType);
    }

    // runs the layout algorithm and applies the result
    Duration duration = animate ? Duration.ofMillis(500) : Duration.ZERO;
    graphComponent.morphLayout(algorithm, duration, layoutData, (source, args) -> layoutPending = false);
  }

  /**
   * Determines the type of the given node.
   * This demo uses integers stored in the node's tag as node types for
   * simplicity's sake. However, {@link OrganicLayout} and
   * {@link OrganicLayoutData} do not impose any
   * restrictions on the type of objects used as node types.
   */
  private Integer getNodeType( INode node ) {
    return NodeTypeSupport.getNodeType(node);
  }

  /**
   * Configures default visualizations for the given graph.
   * @param graph The demo's graph.
   */
  private void configureGraph( IGraph graph ) {
    graph.getNodeDefaults().setSize(new SizeD(40, 40));
    graph.getNodeDefaults().setStyle(NodeTypeSupport.newNodeStyle(0));

    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(new Pen(new Color(102, 43, 0)));
    graph.getEdgeDefaults().setStyle(edgeStyle);
  }

  /**
   * Loads a sample graph for testing the substructure and node types support
   * of the organic layout algorithm.
   */
  private void loadSample( String sample ) {
    try {
      // load sample data
      IGraph newGraph = new DefaultGraph();
      updateGraph(newGraph, "resources/" + sample + ".graphml");

      // update the settings UI to match the sample's default layout settings
      Properties settings =
        loadSampleSettings("resources/" + sample + ".properties");
      updateLayoutSettings(settings);

      // update input mode setting depending on whether we are allowed to
      // change the graph structure
      boolean alterTypesAndStructure =
        booleanValue(settings, "alterTypesAndStructure");
      GraphEditorInputMode inputMode =
        (GraphEditorInputMode) graphComponent.getInputMode();
      inputMode.setCreateEdgeAllowed(alterTypesAndStructure);
      inputMode.setCreateNodeAllowed(alterTypesAndStructure);
      inputMode.setDuplicateAllowed(alterTypesAndStructure);
      inputMode.setDeletableItems(
        alterTypesAndStructure ? GraphItemTypes.ALL : GraphItemTypes.NONE);

      // center new sample graph in current view
      graphComponent.setGraph(newGraph);

      // configures default styles for newly created graph elements
      configureGraph(newGraph);

      runLayout(false);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Loads sample setting from the file identified by the given sample path.
   * @param settingsPath the path to the sample settings file.
   */
  private Properties loadSampleSettings( String settingsPath ) {
    try {
      Properties settings = new Properties();
      try (InputStream is = getClass().getResource(settingsPath).openStream()) {
        settings.load(is);
      }
      return settings;
    } catch (Exception ex) {
      return new Properties();
    }
  }

  /**
   * Rebuilds the demo's graph from the given sample data.
   * @param graph The demo's graph.
   * @param samplePath The path to the sample data representing the desired
   * graph structure.
   */
  private void updateGraph( IGraph graph, String samplePath ) throws IOException {
    new GraphMLIOHandler().read(graph, getClass().getResource(samplePath));
  }

  /**
   * Updates the demo's settings controls for the given sample settings.
   * @param settings The sample settings to be used.
   */
  private void updateLayoutSettings( Properties settings ) {
    boolean oldValue = layoutPending;
    layoutPending = true;
    try {
      updateLayoutSettingsCore(settings);
    } finally {
      layoutPending = oldValue;
    }
  }

  /**
   * Rebuilds the demo's graph from the given sample data.
   * @param graph The demo's graph.
   * @param samplePath The path to the sample data representing the desired graph structure.
   */
  private void updateLayoutSettingsCore( Properties settings ) {
    if (settings.isEmpty()) {
      cycleStyles.setSelectedIndex(0);
      chainStyles.setSelectedIndex(0);
      starStyles.setSelectedIndex(0);
      parallelStyles.setSelectedIndex(0);
      useEdgeGrouping.setSelected(false);
      considerNodeTypes.setSelected(true);
      separateParallel.setSelected(false);
      separateStar.setSelected(false);
    } else {
      cycleStyles.setSelectedItem(CycleSubstructureStyle.valueOf(settings.getProperty("cycleSubstructureStyle")));
      chainStyles.setSelectedItem(ChainSubstructureStyle.valueOf(settings.getProperty("chainSubstructureStyle")));
      starStyles.setSelectedItem(StarSubstructureStyle.valueOf(settings.getProperty("starSubstructureStyle")));
      parallelStyles.setSelectedItem(ParallelSubstructureStyle.valueOf(settings.getProperty("parallelSubstructureStyle")));

      useEdgeGrouping.setSelected(booleanValue(settings, "useEdgeGrouping"));
      considerNodeTypes.setSelected(booleanValue(settings, "considerNodeTypes"));
      separateParallel.setSelected(booleanValue(settings, "parallelSubstructureTypeSeparation"));
      separateStar.setSelected(booleanValue(settings, "starSubstructureTypeSeparation"));
    }
  }

  /**
   * Returns a boolean value corresponding to the value of the given setting.
   */
  private static boolean booleanValue( Properties settings, String setting ) {
    return Boolean.parseBoolean(settings.getProperty(setting));
  }

  /**
   * Adds controls for changing a node's type to the context menu for nodes.
   */
  private void onPopulateItemPopupMenu(
    Object source, PopulateItemPopupMenuEventArgs<IModelItem> args
  ) {
    IModelItem item = args.getItem();
    if (item instanceof INode) {
      GraphComponent graphControl = ((GraphEditorInputMode) source).getGraphComponent();

      // ensure the clicked node is selected
      IGraphSelection selection = graphControl.getSelection();
      if (!selection.isSelected(item)) {
        selection.setSelected(item, true);
      }

      JPopupMenu menu = (JPopupMenu) args.getMenu();
      int typeCount = 8;
      for (int i = 0; i < typeCount; ++i) {
        Integer newType = Integer.valueOf(i);

        JMenuItem menuItem = new JMenuItem();
        menuItem.setOpaque(true);
        menuItem.setBackground((Color) NodeTypeSupport.getFillColor(i));
        menuItem.addActionListener(e -> {
          for (INode node : selection.getSelectedNodes()) {
            Integer oldType = NodeTypeSupport.getNodeType(node);
            if (!oldType.equals(newType)) {
              NodeTypeSupport.setNodeType(node, newType);
              graphControl.getGraph().setStyle(node, NodeTypeSupport.newNodeStyle(newType));
            }
          }

          EventQueue.invokeLater(() -> runLayout(true));
        });
        menu.add(menuItem);
      }
    }
  }

  /**
   * Centers the initial sample graph in the visible area.
   */
  @Override
  public void onVisible() {
    graphComponent.fitGraphBounds();
  }



  /*
   * #####################################################################
   * 
   * Swing UI boilerplate methods
   * The following methods are not related to yFiles functionality, but
   * are required to build the demo's Swing UI.
   * 
   * #####################################################################
   */

  /**
   * Adds controls for layout settings to the demo's UI.
   */
  @Override
  protected void configure( JRootPane rootPane ) {
    super.configure(rootPane);

    Container contentPane = rootPane.getContentPane();
    contentPane.add(newOptionPane(), BorderLayout.WEST);
  }

  /**
   * Creates the demo's layout settings editor.
   */
  private JComponent newOptionPane() {
    JComboBox<NamedSample> samples = new JComboBox<>(new NamedSample[]{
      new NamedSample("Simple Mixed, Large", "mixed_large"),
      new NamedSample("Simple Mixed, Small", "mixed_small"),
      new NamedSample("Simple Parallel", "parallel"),
      new NamedSample("Simple Star", "star"),
      new NamedSample("Computer Network", "computer_network"),
    });
    samples.addActionListener(
      e -> loadSample(((NamedSample) samples.getSelectedItem()).file));

    JButton applyLayout = new JButton("Apply Layout");
    applyLayout.addActionListener(e -> runLayout(true));


    int m = 8;
    JPanel optionPane = new JPanel(new GridBagLayout());
    optionPane.setBorder(BorderFactory.createEmptyBorder(m, m, m, m));

    Insets sectionSep = new Insets(m * 2, 0, 0, 0);
    Insets labelSep = new Insets(0, m, 0, 0);

    GridBagConstraints gbc = new GridBagConstraints();
    Insets empty = gbc.insets;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.weightx = 0;
    gbc.weighty = 0;
    optionPane.add(newTitle("Layout Settings"), gbc);

    ++gbc.gridy;
    gbc.insets = sectionSep;
    optionPane.add(newSection("Sample Graph"), gbc);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    ++gbc.gridy;
    gbc.insets = empty;
    gbc.weightx = 1;
    optionPane.add(samples, gbc);


    gbc.fill = GridBagConstraints.NONE;
    ++gbc.gridy;
    gbc.insets = sectionSep;
    gbc.weightx = 0;
    optionPane.add(newSection("Substructure Layout"), gbc);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.gridwidth = 1;
    gbc.insets = empty;
    gbc.weightx = 0;
    optionPane.add(newLabel("Cycles"), gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    ++gbc.gridx;
    gbc.gridwidth = 2;
    gbc.insets = labelSep;
    gbc.weightx = 1;
    optionPane.add(cycleStyles, gbc);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.gridwidth = 1;
    gbc.insets = empty;
    gbc.weightx = 0;
    optionPane.add(newLabel("Chains"), gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    ++gbc.gridx;
    gbc.gridwidth = 2;
    gbc.insets = labelSep;
    gbc.weightx = 1;
    optionPane.add(chainStyles, gbc);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.gridwidth = 1;
    gbc.insets = empty;
    gbc.weightx = 0;
    optionPane.add(newLabel("Star"), gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx += gbc.gridwidth;
    gbc.gridwidth = 2;
    gbc.insets = labelSep;
    gbc.weightx = 1;
    optionPane.add(starStyles, gbc);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.gridwidth = 1;
    gbc.insets = empty;
    gbc.weightx = 0;
    optionPane.add(newLabel("Parallel"), gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx += gbc.gridwidth;
    gbc.gridwidth = 2;
    gbc.insets = labelSep;
    gbc.weightx = 1;
    optionPane.add(parallelStyles, gbc);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.gridwidth = 2;
    gbc.insets = empty;
    gbc.weightx = 0;
    optionPane.add(newLabel("Use Edge Grouping"), gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx += gbc.gridwidth;
    gbc.gridwidth = 1;
    gbc.insets = labelSep;
    optionPane.add(useEdgeGrouping, gbc);


    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.gridwidth = 3;
    gbc.insets = sectionSep;
    gbc.weightx = 0;
    optionPane.add(newSection("Node Types"), gbc);

    gbc.anchor = GridBagConstraints.WEST;
    ++gbc.gridy;
    gbc.insets = empty;
    gbc.gridwidth = 2;
    optionPane.add(newLabel("Consider Node Types"), gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx += gbc.gridwidth;
    gbc.gridwidth = 1;
    gbc.insets = labelSep;
    optionPane.add(considerNodeTypes, gbc);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.gridwidth = 2;
    gbc.insets = empty;
    optionPane.add(newLabel("Separate Parallel Structures by Type"), gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx += gbc.gridwidth;
    gbc.gridwidth = 1;
    gbc.insets = labelSep;
    optionPane.add(separateParallel, gbc);

    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.gridwidth = 2;
    gbc.insets = empty;
    optionPane.add(newLabel("Separate Star Structures by Type"), gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridx += gbc.gridwidth;
    gbc.gridwidth = 1;
    gbc.insets = labelSep;
    optionPane.add(separateStar, gbc);


    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    ++gbc.gridy;
    gbc.gridwidth = 3;
    gbc.insets = empty;
    gbc.weightx = 1;
    gbc.weighty = 1;
    optionPane.add(new JPanel(), gbc);


    gbc.anchor = GridBagConstraints.SOUTH;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    ++gbc.gridy;
    gbc.insets = empty;
    gbc.weighty = 0;
    optionPane.add(applyLayout, gbc);

    optionPane.setPreferredSize(new Dimension(300, 250));
    return optionPane;
  }

  /**
   * Creates a combo box for choosing one of the given substructure styles.
   */
  private <T> JComboBox<T> newStyleChooser( T[] values ) {
    JComboBox<T> jcb = new JComboBox<>(values);
    jcb.setRenderer(new StyleRenderer<>());
    jcb.addActionListener(e -> runLayout(true));
    return jcb;
  }

  /**
   * Returns the selected item of the given combo box.
   */
  private static <T> T getSelectedStyle( JComboBox<T> jcb ) {
    return (T) jcb.getSelectedItem();
  }

  /**
   * Creates a new check box for the demo's layout settings editor.
   */
  private JCheckBox newCheckBox() {
    JCheckBox jcb = new JCheckBox();
    jcb.addItemListener(e -> runLayout(true));
    return jcb;
  }

  /**
   * Creates the title label for the demo's layout settings editor.
   */
  private static JLabel newTitle( String text ) {
    return newLabel(text, Font.BOLD, 4);
  }

  /**
   * Creates a section name label for the demo's layout settings editor.
   */
  private static JLabel newSection( String text ) {
    return newLabel(text, Font.BOLD, 2);
  }

  /**
   * Creates a setting name label for the demo's layout settings editor.
   */
  private static JLabel newLabel( String text ) {
    return newLabel(text, Font.PLAIN, 0);
  }

  /**
   * Creates a new label with the specified text, font style, and font size
   * <em>increment</em>.
   */
  private static JLabel newLabel( String text, int style, int size ) {
    JLabel jl = new JLabel(text);
    Font oldFont = jl.getFont();
    jl.setFont(oldFont.deriveFont(style, (float) (oldFont.getSize() + size)));
    return jl;
  }


  /**
   * Struct for storing a human-readable name together with a file name
   * for the demo's sample diagrams.
   */
  private static final class NamedSample {
    final String name;
    final String file;

    NamedSample( String name, String file ) {
      this.name = name;
      this.file = file;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  /**
   * Displays human-readable names for substructure style values.
   */
  private static final class StyleRenderer<T> implements ListCellRenderer<T> {
    final DefaultListCellRenderer dlcr;
    final Map<T, String> names;

    /**
     * Initializes a new {@code StyleRenderer} instance.
     */
    StyleRenderer() {
      dlcr = new DefaultListCellRenderer();
      names = new HashMap<>();
    }

    /**
     * Returns the component the renders the given style value in the given
     * combobox list.
     */
    @Override
    public Component getListCellRendererComponent(
      JList<? extends T> list,
      T value,
      int index,
      boolean isSelected,
      boolean cellHasFocus
    ) {
      return dlcr.getListCellRendererComponent(list, getHumanReadableName(value), index, isSelected, cellHasFocus);
    }

    /**
     * Determines the human-readabble name of the given style value.
     */
    private String getHumanReadableName( T value ) {
      String name = names.get(value);
      if (name == null) {
        name = toHumanReadableName(value.toString());
        names.put(value, name);
      }
      return name;
    }

    /**
     * Converts the given style constant name into a human-readable style name.
     */
    private static String toHumanReadableName( String s ) {
      String del = "";
      StringBuilder sb = new StringBuilder();
      for (StringTokenizer st = new StringTokenizer(s, "_"); st.hasMoreTokens();) {
        String token = st.nextToken();
        sb.append(del)
          .append(Character.toUpperCase(token.charAt(0)))
          .append(token.substring(1).toLowerCase(Locale.ENGLISH));
        del = " ";
      }
      return sb.toString();
    }
  }
}
