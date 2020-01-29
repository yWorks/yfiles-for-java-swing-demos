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
package input.orthogonaledges;

import com.yworks.yfiles.geometry.IPoint;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.graph.IBend;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.view.input.CreateBendInputMode;
import com.yworks.yfiles.view.input.DefaultBendCreator;
import com.yworks.yfiles.view.input.IBendCreator;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import com.yworks.yfiles.view.input.SegmentOrientation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates a new bend at the given location. If this bend is on the first or last segment,
 * a second bend is created and placed at a location that ensures that the newly create
 * inner segment is orthogonal.
 */
class BlueBendCreator implements IBendCreator {

  /**
   * Creates a new bend at the given location. If this bend is on the first or last segment,
   * a second bend is created and placed at a location that ensures that the newly create
   * inner segment is orthogonal.
   */
  @Override
  public int createBend(IInputModeContext context, IGraph graph, IEdge edge, PointD location) {

    // check if everything is alright before we can proceed
    if (context == null || !(context.getParentInputMode() instanceof CreateBendInputMode)) {
      return -1;
    }
    // the editingContext should always be non-null in this demo, but theoretically it could be
    OrthogonalEdgeEditingContext editingContext = context.lookup(OrthogonalEdgeEditingContext.class);
    if (editingContext == null) {
      return -1;
    }

    // get a list of source, bends and target locations of the edge
    List<IPoint> edgePoints = getEdgePoints(edge);

    // look for the segment in which the bend has been created
    int closestSegment = getClosestSegmentIndex(location, edgePoints);

    // if no segment can be found for the created bend, exit
    if (closestSegment == -1) {
      return -1;
    }

    int firstSegment = 0;
    int lastSegment = edge.getBends().size();

    if (closestSegment != firstSegment && closestSegment != lastSegment) {
      // the bend wasn't created in first or last segment, call default action
      return (new DefaultBendCreator()).createBend(context, graph, edge, location);
    }

    if (closestSegment == firstSegment) {
      // the bend was created in the first segment, we need to add another
      // bend apart from the created to make the edge stay orthogonal
      IPoint nextPoint = edgePoints.get(1);
      // get orientation of next edge segment to determine second bend location
      SegmentOrientation orientation = editingContext.getSegmentOrientation(edge, 1);
      graph.addBend(edge, location.clone(), 0);
      if (orientation == SegmentOrientation.HORIZONTAL) {
        graph.addBend(edge, new PointD(nextPoint.getX(), location.getY()), 1);
      } else if (orientation == SegmentOrientation.VERTICAL) {
        graph.addBend(edge, new PointD(location.getX(), nextPoint.getY()), 1);
      }
      return 0;
    }

    // the bend was created in the last segment, we need to add another
    // bend apart from the created to make the edge stay orthogonal
    IPoint prevPoint = edgePoints.get(edge.getBends().size());
    // get orientation of next edge segment to determine second bend location
    SegmentOrientation orientation = editingContext.getSegmentOrientation(edge, edge.getBends().size() - 1);
    graph.addBend(edge, location.clone(), edge.getBends().size());
    if (orientation == SegmentOrientation.HORIZONTAL) {
      graph.addBend(edge, new PointD(prevPoint.getX(), location.getY()), edge.getBends().size() - 1);
    } else if (orientation == SegmentOrientation.VERTICAL) {
      graph.addBend(edge, new PointD(location.getX(), prevPoint.getY()), edge.getBends().size() - 1);
    }
    return edge.getBends().size() - 1;
  }

  /**
   * Returns a list containing the source port location, the bend locations,
   * and the target port location of the given edge.
   * @param edge the edge to create the points for
   */
  private List<IPoint> getEdgePoints(IEdge edge) {
    List<IPoint> points = edge.getBends().stream().map(IBend::getLocation).collect(Collectors.toList());
    points.add(0, getLocation(edge.getSourcePort()));
    points.add(getLocation(edge.getTargetPort()));
    return points;
  }

  /**
   * Gets a snapshot of the current location of the port.
   * Unlike {@link com.yworks.yfiles.graph.IPort#getLocation()} this does not return a dynamic point that always refers to the current location.
   * @param port The port to retrieve the location from.
   * @return The current port location.
   */
  private static PointD getLocation(IPort port) {
    IPortLocationModelParameter param = port.getLocationParameter();
    return param.getModel().getLocation(port, param);
  }

  /**
   * Determines the index of the segment in which the bend was created.
   * @param location the location of the created bend
   * @param points   a list of locations of existing bends and ports
   * @return the zero-based index of the index of the bend or -1 if not found
   */
  private int getClosestSegmentIndex(PointD location, List<IPoint> points) {
    int closestSegment = -1;
    double minDist = Double.MAX_VALUE;
    for (int i = 0; i < points.size() - 1; i++) {
      IPoint a = points.get(i);
      IPoint b = points.get(i + 1);
      double dist = location.distanceToSegment(a.toPointD(), b.toPointD());
      if (dist < minDist) {
        closestSegment = i;
        minDist = dist;
      }
    }
    return closestSegment;
  }
}
