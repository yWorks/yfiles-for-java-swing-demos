/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package layout.partitiongrid;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.layout.ColumnDescriptor;
import com.yworks.yfiles.layout.PartitionCellId;
import com.yworks.yfiles.layout.PartitionGrid;
import com.yworks.yfiles.layout.PartitionGridData;
import com.yworks.yfiles.layout.RowDescriptor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * A helper class to create a configured {@link PartitionGridData}.
 */
public class PartitionGridConfigurator {
  
  private IGraph graph;
  private Function<INode, NodeGridData> nodeGridDataProvider;

  /**
   * Creates a new instance using the given graph and a provider function to get {@link NodeGridData}s for {@link INode}s.
   * @param graph The graph to configure the partition grid data for.
   * @param nodeGridDataProvider A provider function to get {@link NodeGridData}s for {@link INode}s.
   */
  public PartitionGridConfigurator(IGraph graph, Function<INode, NodeGridData> nodeGridDataProvider) {
    this.graph = graph;
    this.nodeGridDataProvider = nodeGridDataProvider;
  }

  /**
   * Creates a new {@link PartitionGridData} using the given configuration.
   * @param rowCount The number of rows in the partition grid.
   * @param columnCount The number of columns in the partition grid.
   * @param fixedColumnOrder Whether the columns should have their {@link ColumnDescriptor#isIndexFixed() index fixed}.
   * @param minimumColumnWidth The minimum width of each column.
   * @param stretchGroups Whether group nodes should span the partition cells of their descendants.
   * @return A configured PartitionGridData.
   */
  PartitionGridData createPartitionGridData(int rowCount, int columnCount, boolean fixedColumnOrder,
                                            double minimumColumnWidth, boolean stretchGroups) {
    // create a PartitionGrid and configure the rows and columns
    PartitionGrid grid = new PartitionGrid(rowCount, columnCount);
    for(Object col: grid.getColumns()) {
      ColumnDescriptor columnDescriptor = (ColumnDescriptor) col;
      columnDescriptor.setLeftInset(10);
      columnDescriptor.setRightInset(10);
      columnDescriptor.setMinimumWidth(minimumColumnWidth);
      columnDescriptor.setIndexFixed(fixedColumnOrder);
    }
    for(Object row: grid.getRows()) {
      RowDescriptor rowDescriptor = (RowDescriptor) row;
      rowDescriptor.setTopInset(10);
      rowDescriptor.setBottomInset(10);
      rowDescriptor.setMinimumHeight(10);
    }

    // create the PartitionGridData for the grid
    PartitionGridData partitionGridData = new PartitionGridData();
    partitionGridData.setGrid(grid);
    // set the delegate that specifies the PartitionCellIds for each node
    partitionGridData.setCellIds((node, partitionGrid) -> {
      boolean isGroupNode = graph.isGroupNode(node);
      if (!isGroupNode) {
        // for normal nodes the row and column indices in their NodeGridData is used
        return getNodeCellId(node, partitionGrid);
      } else {
        // we have a group node
        if (!stretchGroups || graph.getChildren(node).size() == 0) {
          // the group nodes shall not be stretched or the group node has no children so we return null
          // this means the group node will be adjusted to contain its children but has no specific assignment to cells
          return null;
        } else {
          // the group nodes has children whose partition cells shall be spanned so a spanning PartitionCellId is created
          // that contains all rows/column of its child nodes.
          return getGroupNodeCellId(node, partitionGrid);
        }
      }
    });
    return partitionGridData;
  }

  /**
   * Returns a PartitionCellId for the given node if its NodeGridData {@link NodeGridData#hasValidIndices() has valid indices}
   * or <code>null</code> otherwise.
   * @param node The node to create the cell id for.
   * @param partitionGrid The grid to create the cell id.
   * @return A PartitionCellId for the given node if its NodeGridData {@link NodeGridData#hasValidIndices() has valid indices}
   * or <code>null</code> otherwise.
   */
  private PartitionCellId getNodeCellId(INode node, PartitionGrid partitionGrid) {
    NodeGridData nodeGridData = nodeGridDataProvider.apply(node);
    if (nodeGridData.hasValidIndices()) {
      // create a cell id with the stored row and column indices
      return partitionGrid.createCellId(nodeGridData.getRowIndex(), nodeGridData.getColumnIndex());
    } else {
      // if there is no valid row and column assignment ('black' nodes) we return null so the layout algorithm may place the
      // node where it wants.
      return null;
    }
  }

  /**
   * Returns a PartitionCellId for the given group node if any of its descendants has a valid PartitionCellId or
   * <code>null</code> otherwise.
   * @param node The group node to create the cell id for.
   * @param partitionGrid The grid to create the cell id.
   * @return A PartitionCellId for the given group node if any of its descendants has a valid PartitionCellId or
   * <code>null</code> otherwise.
   */
  private PartitionCellId getGroupNodeCellId(INode node, PartitionGrid partitionGrid) {
    // collect the RowDescriptor and ColumnDescriptor of the descendents of the node
    Set rowSet = new HashSet();
    Set columnSet = new HashSet();

    for (INode child : graph.getChildren(node)) {
      // get the cell id of the child
      PartitionCellId childCellId;
      if (!graph.isGroupNode(child)) {
        childCellId = getNodeCellId(child, partitionGrid);
      } else {
        childCellId = getGroupNodeCellId(child, partitionGrid);
      }
      // if there is a cell id, add all row and column descriptors to our row/columnSets
      if (childCellId != null) {
        for (Object cellObject : childCellId.getCells()) {
          PartitionCellId.Cell pair = (PartitionCellId.Cell) cellObject;
          rowSet.add(pair.getRow());
          columnSet.add(pair.getColumn());
        }
      }
    }
    if (!rowSet.isEmpty() && !columnSet.isEmpty()) {
      // at least one row and one column is specified by the children and should be spanned by this group node
      return partitionGrid.createCellSpanId(rowSet, columnSet);
    }
    // otherwise the group node doesn't span any partition cells
    return null;
  }
}
