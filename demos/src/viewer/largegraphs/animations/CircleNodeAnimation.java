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
package viewer.largegraphs.animations;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.IAnimation;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An animation that moves nodes in a circular motion.
 * @see IAnimation
 */
public class CircleNodeAnimation implements IAnimation {

  private final IGraph graph;
  private final List<INode> nodes;
  private final double radius;
  private final double revolutions;
  private final Duration preferredDuration;

  // A list of the nodes' start locations.
  List<RectD> startBounds;

  public CircleNodeAnimation(IGraph graph, List<INode> nodes, double radius, double revolutions, Duration preferredDuration) {
    this.graph = graph;
    this.nodes = nodes;
    this.radius = radius;
    this.revolutions = revolutions;
    this.preferredDuration = preferredDuration;
  }

  @Override
  public void initialize() {
    this.startBounds = this.nodes.stream().map(n -> n.getLayout().toRectD()).collect(Collectors.toList());
  }

  @Override
  public void animate(double time) {
    double totalAngle = 2 * Math.PI * this.revolutions;
    double currentAngle = totalAngle * time;
    PointD offset = new PointD(
        Math.cos(currentAngle) * this.radius,
        Math.sin(currentAngle) * this.radius);

    for (int i = 0; i < this.nodes.size(); i++) {
      INode n = this.nodes.get(i);

      PointD topRight = new PointD(this.radius, 0);
      PointD topLeft = this.startBounds.get(i).getTopLeft();
      PointD p = new PointD(topLeft.x - topRight.x, topLeft.y - topRight.y);
      PointD newPosition = PointD.add(p, offset);
      this.graph.setNodeLayout(n, new RectD(newPosition, this.startBounds.get(i).getSize()));
    }
  }

  @Override
  public void cleanUp() {
  }

  @Override
  public Duration getPreferredDuration() {
    return preferredDuration;
 }
}
