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

/**
 * A data class for nodes specifying their row and column index in a PartitionGrid.
 */
public class NodeGridData {

  // region Property RowIndex

  private int rowIndex;

  /**
   * Returns the index of the partition grid row of the node.
   */
  public int getRowIndex() {
    return rowIndex;
  }

  /**
   * Sets the index of the partition grid row of the node.
   */
  public void setRowIndex(int rowIndex) {
    this.rowIndex = rowIndex;
  }

  // endregion

  // region Property ColumnIndex

  private int columnIndex;

  /**
   * Returns the index of the partition grid column of the node.
   */
  public int getColumnIndex() {
    return columnIndex;
  }

  /**
   * Sets the index of the partition grid column of the node.
   */
  public void setColumnIndex(int columnIndex) {
    this.columnIndex = columnIndex;
  }

  // endregion

  // region Constructors

  /**
   * Creates a new instance with {@link #hasValidIndices() invalid} indices.
   */
  public NodeGridData() {
    this.rowIndex = -1;
    this.columnIndex = -1;
  }

  /**
   * Creates a new instance with the specified <code>rowIndex</code> and <code>columnIndex</code>.
   * @param rowIndex the index of the partition grid row of the node.
   * @param columnIndex the index of the partition grid column of the node.
   */
  public NodeGridData(int rowIndex, int columnIndex) {
    this.rowIndex = rowIndex;
    this.columnIndex = columnIndex;
  }

  // endregion

  /**
   * Returns whether the {@link #getRowIndex() row index} and {@link #getColumnIndex() column index} are valid.
   */
  public boolean hasValidIndices() {
    return rowIndex >= 0 && columnIndex >= 0;
  }
}
