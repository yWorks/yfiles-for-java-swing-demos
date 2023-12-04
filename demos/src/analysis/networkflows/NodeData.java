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
package analysis.networkflows;

import com.yworks.yfiles.graph.INode;

/**
 * Flow related data for {@link INode}s.
 */
public class NodeData {
  private boolean cut;
  private double flow;
  private double supply;
  private boolean source;
  private boolean sink;

  /**
   * Creates a new instance using the provided supply and flow.
   *
   * @param supply The supply provided by the node.
   * @param flow   The flow through the node.
   */
  public NodeData(double supply, double flow) {
    this.flow = flow;
    this.supply = supply;
    this.cut = false;
    this.source = false;
  }

  /**
   * Gets whether the node is in the source partition of the minimum cut.
   */
  public boolean isCut() {
    return cut;
  }

  /**
   * Sets whether the node is in the source partition of the minimum cut.
   */
  public void setCut(boolean cut) {
    this.cut = cut;
  }

  /**
   * Gets whether the node is a source node and therefore has no incoming edges.
   */
  public boolean isSource() {
    return source;
  }

  /**
   * Sets whether the node is a source node and therefore has no incoming edges.
   */
  public void setSource(boolean source) {
    this.source = source;
  }

  /**
   * Gets whether the node is a sink node and therefore has no outgoing edges.
   */
  public boolean isSink() {
    return sink;
  }

  /**
   * Sets whether the node is a sink node and therefore has no outgoing edges.
   */
  public void setSink(boolean sink) {
    this.sink = sink;
  }

  /**
   * Gets the flow through the node.
   */
  public double getFlow() {
    return flow;
  }

  /**
   * Sets the flow through the node.
   */
  public void setFlow(double flow) {
    this.flow = flow;
  }

  /**
   * Gets the supply provided by the node.
   */
  public double getSupply() {
    return supply;
  }

  /**
   * Sets the supply provided by the node.
   */
  public void setSupply(double supply) {
    this.supply = supply;
  }
}
