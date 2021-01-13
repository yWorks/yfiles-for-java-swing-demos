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
package toolkit.aggregation;


/**
 * Determines what kind of edges should be created when replacing original edges with aggregation edges in calls to methods
 * of the {@link AggregateGraphWrapper}.
 */
public enum EdgeReplacementPolicy {
  /**
   * Edges will not be replaced by aggregation edges.
   */
  NONE(0),

  /**
   * During {@link toolkit.aggregation.AggregateGraphWrapper#aggregate(com.yworks.yfiles.utils.IListEnumerable, com.yworks.yfiles.geometry.RectD, com.yworks.yfiles.graph.styles.INodeStyle, java.lang.Object)}
   * all edges between any of the aggregated nodes and other nodes are replaced by a single aggregation edge between the
   * aggregation node and the other node.
   * <p>
   * This means there will be no duplicate edges between any pairs of nodes. The direction of the created edge is not
   * deterministic.
   * </p>
   */
  UNDIRECTED(1),

  /**
   * Edges in both directions will be created, resulting in up to two edges between pairs of nodes.
   */
  DIRECTED(2);

  private final int value;

  private EdgeReplacementPolicy( final int value ) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final EdgeReplacementPolicy fromOrdinal( int ordinal ) {
    for (EdgeReplacementPolicy current : values()) {
      if (ordinal == current.value) return current;
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }

}
