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
package databinding.interactivenodesgraphbuilder;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.AdjacentNodesGraphBuilder;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelDefaults;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayoutData;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.utils.ICloneable;
import com.yworks.yfiles.utils.PropertyChangedEventArgs;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.ShowFocusPolicy;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import toolkit.AbstractDemo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.event.ListSelectionEvent;

/**
 * Shows data binding with class {@link AdjacentNodesGraphBuilder}.
 */
public class InteractiveNodesGraphBuilderDemo extends AbstractDemo {
  private AdjacentNodesGraphBuilder<BusinessData, BusinessData> graphBuilder;

  private BusinessData currentData;

  private JLabel currentDataLabel;
  private JList<BusinessData> nodesView;
  private JList<BusinessData> predecessorsView;
  private JList<BusinessData> successorsView;

  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new InteractiveNodesGraphBuilderDemo().start();
    });
  }

  /**
   * Creates the controls for adding and removing business data items.
   * <p>
   * <b>Note:</b> The controls for modifying business data work on the business
   * data structure created in {@link #createInitialBusinessData()} and do not
   * reflect the induced predecessor/successor relationships displayed in the
   * application's graph view.
   * </p>
   */
  @Override
  protected void configure( JRootPane rootPane ) {
    super.configure(rootPane);

    Consumer<BusinessData> onChanged = data -> {
      if (data == null) {
        update(true);
      } else {
        update(true, data);
      }
    };

    int m = 8;

    JPanel inputPane = new JPanel(new GridBagLayout());
    inputPane.setBorder(BorderFactory.createEmptyBorder(m, m, m, m));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    nodesView = SwingUtils.newList(BusinessData.class, onChanged, false);
    nodesView.addListSelectionListener(this::selectedValueChanged);
    inputPane.add(SwingUtils.newListPane(
            nodesView, "Nodes Source", BusinessData::new), gbc);

    ++gbc.gridy;
    gbc.weighty = 0.5;
    currentDataLabel = SwingUtils.newNonValidatingLabel();
    JPanel currentItemPane = new JPanel(new GridLayout(1, 1));
    currentItemPane.setBorder(BorderFactory.createTitledBorder("Current Item"));
    currentItemPane.add(currentDataLabel);
    inputPane.add(currentItemPane, gbc);

    gbc.insets = new Insets(m, 0, 0, 0);
    ++gbc.gridy;
    gbc.weighty = 1;
    predecessorsView = SwingUtils.newList(BusinessData.class, onChanged, true);
    inputPane.add(SwingUtils.newListPane(
            predecessorsView, "Predecessors", BusinessData::new), gbc);

    ++gbc.gridy;
    successorsView = SwingUtils.newList(BusinessData.class, onChanged, true);
    inputPane.add(SwingUtils.newListPane(
            successorsView, "Successors", BusinessData::new), gbc);

    ++gbc.gridy;
    inputPane.add(SwingUtils.newTrash(BusinessData.class, this::onTrashDropped), gbc);

    inputPane.setPreferredSize(new Dimension(280, 120));
    rootPane.getContentPane().add(inputPane, BorderLayout.WEST);
  }

  /**
   * Configures the yFiles related features demonstrated in this demo
   * application.
   */
  @Override
  public void initialize() {
    initializeGraphComponent();

    initializeGraphDefaults();


    // configure the graph builder and its data sources
    ObservableCollection<BusinessData> nodeSource = createInitialBusinessData();
    nodesView.setModel(nodeSource);
    graphBuilder = new AdjacentNodesGraphBuilder<>(graphComponent.getGraph());
    graphBuilder.setNodesSource(nodeSource);
    graphBuilder.setSuccessorProvider(BusinessData::getSuccessors);
    graphBuilder.setPredecessorProvider(BusinessData::getPredecessors);
    graphBuilder.setNodeLabelProvider(BusinessData::getNodeName);
    graphBuilder.addNodeCreatedListener((source, args) -> {
      INode node = args.getItem();
      // Set optimal node size
      ILabel l1 = node.getLabels().first();
      SizeD size1 = l1.getPreferredSize();
      SizeD bestSize = new SizeD(size1.getWidth() + 10, size1.getHeight() + 12);
      // Set node to that size. Location is irrelevant here, since we're running a layout anyway
      args.getGraph().setNodeLayout(node, new RectD(PointD.ORIGIN, bestSize));
    });

    // Create the graph from the model data
    graphBuilder.buildGraph();
  }

  /**
   * Configures the graph component for limited interaction (only panning
   * and dragging nodes is possible).
   * @see #createInputMode()
   */
  private void initializeGraphComponent() {
    graphComponent.setAutoDragEnabled(false);
    graphComponent.setInputMode(createInputMode());

    graphComponent.getFocusIndicatorManager().setShowFocusPolicy(ShowFocusPolicy.ALWAYS);
    graphComponent.addCurrentItemChangedListener(this::currentItemChanged);
  }

  /**
   * Creates a viewer input mode that allows panning and dragging nodes.
   */
  private static IInputMode createInputMode() {
    // create new input mode
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    gvim.setSelectableItems(GraphItemTypes.NONE);

    // add a custom input mode that allows dragging nodes from the graph to the lists
    NodeDragInputMode ndim = new NodeDragInputMode(BusinessData.class);
    ndim.setPriority(-1);
    gvim.add(ndim);

    return gvim;
  }

  /**
   * Sets default styles for the graph.
   */
  void initializeGraphDefaults() {
    IGraph graph = graphComponent.getGraph();

    ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
    nodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    nodeStyle.setPaint(new Color(255, 237, 204));
    nodeStyle.setPen(Pen.getDarkOrange());
    INodeDefaults nodeDefaults = graph.getNodeDefaults();
    nodeDefaults.setStyle(nodeStyle);
    DefaultLabelStyle nodeLabelStyle = new DefaultLabelStyle();
    nodeLabelStyle.setFont(new Font("Dialog", Font.PLAIN, 13));
    ILabelDefaults nodeLabelDefaults = nodeDefaults.getLabelDefaults();
    nodeLabelDefaults.setStyle(nodeLabelStyle);
    nodeLabelDefaults.setLayoutParameter(InteriorLabelModel.CENTER);

    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setSmoothingLength(20);
    edgeStyle.setTargetArrow(IArrow.DEFAULT);
  }

  @Override
  public void onVisible() {
    applyLayout(false);
  }

  /**
   * Creates the node source.
   * @return A list of {@link BusinessData} items.
   */
  private static ObservableCollection<BusinessData> createInitialBusinessData() {
    BusinessData jenny = new BusinessData("Jenny");
    BusinessData julia = new BusinessData("Julia");
    BusinessData marc = new BusinessData("Marc");
    BusinessData martin = new BusinessData("Martin");
    BusinessData natalie = new BusinessData("Natalie");
    BusinessData nicole = new BusinessData("Nicole");
    BusinessData petra = new BusinessData("Petra");
    BusinessData stephen = new BusinessData("Stephen");
    BusinessData tim = new BusinessData("Tim");
    BusinessData tom = new BusinessData("Tom");
    BusinessData tony = new BusinessData("Tony");

    julia.getPredecessors().add(jenny);
    julia.getSuccessors().add(petra);
    marc.getPredecessors().add(julia);
    marc.getSuccessors().add(tim);
    martin.getPredecessors().add(julia);
    martin.getSuccessors().add(natalie);
    martin.getSuccessors().add(nicole);
    nicole.getSuccessors().add(petra);
    tim.getSuccessors().add(tom);
    tom.getSuccessors().add(tony);
    tony.getSuccessors().add(tim);
    tony.getPredecessors().add(julia);
    stephen.getSuccessors().add(tom);

    ObservableCollection<BusinessData> list = new ObservableCollection<>();
    list.add(marc);
    list.add(martin);
    list.add(stephen);
    return list;
  }

  /**
   * Updates the current item of the graph component when the selected value
   * in the {@code nodesView} list has changed.
   */
  private void selectedValueChanged( ListSelectionEvent e ) {
    JList<BusinessData> listBox = (JList<BusinessData>) e.getSource();
    BusinessData selected = (BusinessData) listBox.getSelectedValue();
    if (currentData != selected) {
      IGraph graph = graphComponent.getGraph();
      for (INode node : graph.getNodes()) {
        if (node.getTag() == selected) {
          graphComponent.setCurrentItem(node);
          break;
        }
      }
      setCurrentData(selected);
    }
  }

  /**
   * Set the current business data and update the successor and predecessor list.
   * @param selected The business data to select.
   */
  private void setCurrentData( BusinessData selected ) {
    if (currentData == selected) {
      return;
    }

    currentData = selected;

    if (selected == null) {
      currentDataLabel.setText("");
      predecessorsView.setModel(ObservableCollection.EMPTY);
      successorsView.setModel(ObservableCollection.EMPTY);
    } else {
      currentDataLabel.setText(selected.getNodeName());
      predecessorsView.setModel(selected.getPredecessors());
      successorsView.setModel(selected.getSuccessors());
    }
  }

  /**
   * Called when the graph component's current item has changed.
   * Updates the current item and the successor and predecessor lists.
   */
  void currentItemChanged( Object source, PropertyChangedEventArgs args ) {
    BusinessData currentData = null;
    IModelItem currentItem = graphComponent.getCurrentItem();
    if (currentItem != null) {
      currentData = (BusinessData) currentItem.getTag();
    }
    if (this.currentData != currentData) {
      if (SwingUtils.contains(nodesView, currentData)) {
        nodesView.setSelectedValue(currentData, true);
      } else {
        nodesView.clearSelection();
      }
      setCurrentData(currentData);
    }
  }

  /**
   * Updates the graph after changes to the business data.
   * @param incremental Whether to keep the unchanged parts of the graph stable.
   * @param incrementalNodes The nodes which have changed.
   */
  public void update( boolean incremental, BusinessData... incrementalNodes ) {
    graphBuilder.updateGraph();
    applyLayout(incremental, incrementalNodes);
  }


  /**
   * Applies the layout. Uses a {@link HierarchicLayout}. If single graph items
   * are created or removed, the incremental mode of this layout algorithm is
   * used to keep most of the current layout of the graph unchanged.
   * @param incremental if set to {@code true} incremental.
   * @param incrementalNodes the incremental nodes.
   */
  private void applyLayout( boolean incremental, BusinessData... incrementalNodes ) {
    HierarchicLayout layout = new HierarchicLayout();
    HierarchicLayoutData layoutData = null;
    if (!incremental) {
      layout.setLayoutMode(LayoutMode.FROM_SCRATCH);
    } else {
      layout.setLayoutMode(LayoutMode.INCREMENTAL);

      if (incrementalNodes != null && incrementalNodes.length > 0) {
        // we need to add hints for incremental nodes
        ArrayList<INode> nodes = new ArrayList<>();
        for (BusinessData data : incrementalNodes) {
          nodes.add(graphBuilder.getNode(data));
        }
        layoutData = new HierarchicLayoutData();
        layoutData.getIncrementalHints().setIncrementalLayeringNodes(nodes);
      }
    }
    graphComponent.morphLayout(layout, Duration.ofMillis(1000), layoutData);
  }

  /**
   * Handles drop over the trashcan.
   * Removes the dropped item from all lists.
   */
  private void onTrashDropped( BusinessData dropped ) {
    if (dropped == currentData) {
      setCurrentData(null);
    }

    boolean changed = false;
    for (INode node : graphComponent.getGraph().getNodes()) {
      BusinessData data = (BusinessData) node.getTag();
      changed |= data.getPredecessors().remove(dropped);
      changed |= data.getSuccessors().remove(dropped);
    }
    changed |= ((ObservableCollection<BusinessData>) graphBuilder.getNodesSource()).remove(dropped);

    if (changed) {
      update(true);
    }
  }


  /**
   * Represents an object of the business data.
   */
  public static final class BusinessData implements ICloneable {
    String nodeName;
    ObservableCollection<BusinessData> successors;
    ObservableCollection<BusinessData> predecessors;

    public BusinessData() {
      this("Unnamed");
    }
    
    public BusinessData( String name ) {
      nodeName = name;
      successors = new ObservableCollection<>();
      predecessors = new ObservableCollection<>();
    }

    public String getNodeName() {
      return nodeName;
    }

    public void setNodeName( String nodeName ) {
      this.nodeName = nodeName;
    }

    public ObservableCollection<BusinessData> getSuccessors() {
      return successors;
    }

    public ObservableCollection<BusinessData> getPredecessors() {
      return predecessors;
    }

    @Override
    public String toString() {
      return getNodeName();
    }

    @Override
    public BusinessData clone() {
      try {
        return (BusinessData) super.clone();
      } catch (CloneNotSupportedException exception) {
        throw new RuntimeException("Class doesn't implement java.lang.Cloneable");
      }
    }
  }
}
