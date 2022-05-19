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
package layout.treemap;

import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YDimension;
import com.yworks.yfiles.algorithms.YInsets;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.layout.GroupingKeys;
import com.yworks.yfiles.layout.tree.TilingPolicy;
import com.yworks.yfiles.layout.tree.TreeMapLayout;
import com.yworks.yfiles.layout.tree.TreeMapLayoutData;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import toolkit.AbstractDemo;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Locale;


/**
 * Shows how to use the {@link TreeMapLayout} algorithm.
 */
public class TreeMapDemo extends AbstractDemo {
  /*
   * Settings for the layout algorithm.
   */
  /** The minimum width the algorithm will assign to nodes */
  private static final int MINIMUM_NODE_WIDTH = 5;
  /** The minimum height the algorithm will assign to nodes */
  private static final int MINIMUM_NODE_HEIGHT = 5;
  /** The preferred aspect ratio */
  private static final int ASPECT_RATIO = 1;
  /** The distance between nodes that belong to the same level in the tree map */
  private static final int SPACING = 1;
  /** The tiling policy for the layout algorithm */
  private static final TilingPolicy TILING_ALGORITHM = TilingPolicy.SQUARIFIED;
  /** If nodes should be sorted by file name or file size */
  private static final boolean SORT_BY_NAME = false;
  /** If nodes should be sorted in ascending or descending order */
  private static final boolean SORT_ASCENDING = false;
  /** The sort criterion for nodes that represent different file types */
  private static final FileTypeOrder FILE_TYPE_ORDER = FileTypeOrder.DirectoriesFirst;
  /**
   * The distance between the top border a the group node and its child nodes
   * (the group node label is placed inside that space)
   */
  private static final int GROUP_NODE_LABEL_HEIGHT = 24;

  // GraphML namespace for the demo's {@link FileView} data type.
  private static final String NS_URI = "http://www.yworks.com/yfiles-for-java/TreeMap/1.0";

  /**
   * Initializes the demo.
   */
  public void initialize() {
    initializeGraph();
    initializeInputMode();
  }

  /**
   * Initializes user interaction.
   */
  private void initializeInputMode() {
    GraphViewerInputMode viewerInputMode = new GraphViewerInputMode();
    viewerInputMode.setSelectableItems(GraphItemTypes.NONE);
    viewerInputMode.setFocusableItems(GraphItemTypes.NONE);
    viewerInputMode.setClickableItems(GraphItemTypes.NONE);

    // add tooltips that show the label text
    viewerInputMode.setToolTipItems(GraphItemTypes.NODE);
    viewerInputMode.addQueryItemToolTipListener(( source, args ) -> {
      if (args.isHandled()) {
        return;
      }
      if (args.getItem() instanceof INode) {
        INode hitNode = (INode) args.getItem();
        if (hitNode.getLabels().size() > 0) {
          args.setToolTip(hitNode.getLabels().first().getText());
        }
      }
      args.setHandled(true);
    });

    graphComponent.setInputMode(viewerInputMode);
  }

  /**
   * Loads a sample graph and applies the layout.
   */
  private void initializeGraph() {
    // read sample graph from GraphML file
    try {
      read(graphComponent.getGraph(), getClass().getResource("resources/treemap.graphml"));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

    applyLayout();
  }

  /**
   * Applies a {@link TreeMapLayout} to the current graph.
   */
  private void applyLayout() {
    IGraph graph = graphComponent.getGraph();


    TreeMapLayout layout = new TreeMapLayout();
    layout.setPreferredSize(new YDimension(1000, 1000));
    layout.setAspectRatio(ASPECT_RATIO);
    layout.setTilingPolicy(TILING_ALGORITHM);
    layout.setMinimumNodeSize(new YDimension(MINIMUM_NODE_WIDTH, MINIMUM_NODE_HEIGHT));
    layout.setSpacing(SPACING);
    layout.setNodeComparator(newNodeComparator());

    // determine the current weight range
    long minSize = Integer.MAX_VALUE;
    long maxSize = 0;
    for (INode node : graph.getNodes()) {
      if (!graph.isGroupNode(node)) {
        long weight = ((FileView) node.getTag()).getSize();
        minSize = Math.min(minSize, weight);
        maxSize = Math.max(maxSize, weight);
      }
    }

    final long prefMaxWeight = 500;
    final long prefMinWeight = 10;
    final long prefWeightRange = prefMaxWeight - prefMinWeight;
    final long maxWeight = maxSize;
    final long minWeight = minSize;
    final long weightRange = maxWeight - minWeight;

    // determines if weights should be scaled into a preferred weight range
    // to avoid large differences
    final boolean scaleWeight =
            maxWeight / minWeight > prefMaxWeight / prefMinWeight &&
            weightRange > prefWeightRange;

    TreeMapLayoutData layoutData = new TreeMapLayoutData();
    layoutData.setNodeWeights(node -> {
      if (graph.isGroupNode(node)) {
        return 0d;
      }
      FileView tag = (FileView) node.getTag();
      double weight = tag.getSize();
      if (scaleWeight) {
        // scale weights to avoid large differences
        double scaledWeight = (weight - minWeight) / weightRange;
        return prefMinWeight + scaledWeight * prefWeightRange;
      } else {
        return weight;
      }
    });

    // hide labels during layout
    ICanvasObjectGroup nodeLabelGroup = graphComponent.getGraphModelManager().getNodeLabelGroup();
    nodeLabelGroup.setVisible(false);

    // register a mapper providing group node insets to avoid children overlapping group labels
    graph.getMapperRegistry().createConstantMapper(
            GroupingKeys.GROUP_NODE_INSETS_DPKEY,
            new YInsets(GROUP_NODE_LABEL_HEIGHT, 1, 1, 1));

    if (SORT_BY_NAME) {
      // register a mapper providing file names for sorting
      graph.getMapperRegistry().createFunctionMapper(
              INode.class, String.class, TreeMapNodeComparator.NODE_TO_NAME_DPKEY,
              (node -> {
                IListEnumerable<ILabel> labels = node.getLabels();
                return labels.size() > 0 ? labels.first().getText() : "";
              }));
    }

    graphComponent.morphLayout(layout, Duration.ofMillis(700), layoutData, ( source, layoutEventArgs ) -> {
      // clean previously added mappers
      graph.getMapperRegistry().removeMapper(GroupingKeys.GROUP_NODE_INSETS_DPKEY);
      graph.getMapperRegistry().removeMapper(TreeMapNodeComparator.NODE_TO_NAME_DPKEY);

      // show labels again
      nodeLabelGroup.setVisible(true);
    });
  }

  /**
   * Reads the given graphml file. The tag of each node provides the path and size of the corresponding file.
   */
  private void read( IGraph graph, URL resource ) throws IOException {
    GraphMLIOHandler reader = new GraphMLIOHandler();
    reader.addNamespace(NS_URI, "demo");
    reader.addXamlNamespaceMapping(NS_URI, FileView.class);
    reader.read(graph, resource);
  }

  public static void main( final String[] args ) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new TreeMapDemo().start();
    });
  }

  /**
   * Creates a node comparator according to the sorting settings.
   * @return {@link TreeMapNodeComparator}
   */
  private static TreeMapNodeComparator newNodeComparator() {
    return new TreeMapNodeComparator(
            SORT_ASCENDING, SORT_BY_NAME,
            // if files after or before directories is used, the leaf state has to be considered
            FILE_TYPE_ORDER != FileTypeOrder.Mixed,
            // if files should come after directories, trailing leafs are not considered
            FILE_TYPE_ORDER == FileTypeOrder.DirectoriesFirst);
  }

  /**
   * A flexible comparator which can be used for sorting groups and leaf nodes using different criteria.
   */
  private static class TreeMapNodeComparator extends TreeMapLayout.NodeWeightComparator {
    static final Object NODE_TO_NAME_DPKEY = "TreeMapNodeComparator.NODE_TO_NAME_DPKEY";

    final boolean ascending;
    final boolean sortByName;
    final boolean considerLeafState;
    final boolean leafsTrailing;

    TreeMapNodeComparator( boolean ascending, boolean sortByName, boolean considerLeafState, boolean leafsTrailing ) {
      this.ascending = ascending;
      this.sortByName = sortByName;
      this.considerLeafState = considerLeafState;
      this.leafsTrailing = leafsTrailing;
    }

    @Override
    public int compare( Object o1, Object o2 ) {
      Node node1 = (Node) o1;
      Node node2 = (Node) o2;

      if (considerLeafState) {
        // leafs should either come last (trailing) or first (leading)
        int degree1 = node1.outDegree();
        int degree2 = node2.outDegree();
        if (degree1 == 0 && degree2 > 0) {
          // only first node is a leaf
          return leafsTrailing ? 1 : -1;
        }
        if (degree1 > 0 && degree2 == 0) {
          // only second node is a leaf
          return leafsTrailing ? -1 : 1;
        }
      } // else: leafs are handled the same way as non-leafs

      // both are non-leafs or leafs, or leaf state is ignored
      // 1: compare by name
      if (sortByName) {
        IDataProvider names = node1.getGraph().getDataProvider(NODE_TO_NAME_DPKEY);
        String name1 = (String) names.get(node1);
        String name2 = (String) names.get(node2);
        int result = name1.compareTo(name2);
        return ascending ? result : -result;
      }

      // 2: compare by weight
      int result = super.compare(node1, node2);
      return ascending ? -result : result;
    }
  }

  /**
   * Orders by which the files can be sorted.
   */
  private enum FileTypeOrder {
    DirectoriesFirst,
    Mixed,
    FilesFirst
  }
}
