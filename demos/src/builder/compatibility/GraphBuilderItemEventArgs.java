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
package builder.compatibility;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.utils.ItemEventArgs;

/**
 * Event arguments for item events in {@link GraphBuilder}, {@link AdjacentNodesGraphBuilder}, and {@link TreeBuilder}.
 *
 * @param <TItem>   The type of the item contained in the argument.
 * @param <TObject> The type of object that the item was created from.
 */
public class GraphBuilderItemEventArgs<TItem, TObject> extends ItemEventArgs<TItem> {
  private IGraph graph;

  /**
   * Gets the graph that can be used to modify the {@link ItemEventArgs#getItem() Item}.
   *
   * @return The Graph.
   */
  public final IGraph getGraph() {
    return this.graph;
  }

  /**
   * Sets the graph that can be used to modify the {@link ItemEventArgs#getItem() Item}.
   *
   * @param value The Graph to set.
   * @see #getGraph()
   */
  private final void setGraph(IGraph value) {
    this.graph = value;
  }

  private TObject sourceObject;

  /**
   * Gets the object the {@link ItemEventArgs#getItem() Item} has been created from.
   *
   * @return The SourceObject.
   */
  public final TObject getSourceObject() {
    return this.sourceObject;
  }

  /**
   * Sets the object the {@link ItemEventArgs#getItem() Item} has been created from.
   *
   * @param value The SourceObject to set.
   * @see #getSourceObject()
   */
  private final void setSourceObject(TObject value) {
    this.sourceObject = value;
  }

  /**
   * Creates a new instance of the {@link GraphBuilderItemEventArgs} class with the given graph, item, and source
   * object.
   *
   * @param graph        The graph that can be used to modify {@code item}.
   * @param item         The item created from {@code sourceObject}.
   * @param sourceObject The object {@code item} was created from.
   */
  public GraphBuilderItemEventArgs(IGraph graph, TItem item, TObject sourceObject) {
    super(item);
    setGraph(graph);
    setSourceObject(sourceObject);
  }
}
