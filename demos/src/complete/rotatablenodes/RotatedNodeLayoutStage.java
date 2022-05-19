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
package complete.rotatablenodes;

import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.IDataMap;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.IEdgeMap;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.NodeDpKey;
import com.yworks.yfiles.algorithms.YDimension;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.algorithms.YRectangle;
import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.geometry.Matrix2D;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.layout.AbstractLayoutStage;
import com.yworks.yfiles.layout.GroupingKeys;
import com.yworks.yfiles.layout.IEdgeLayout;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.INodeLayout;
import com.yworks.yfiles.layout.LayoutGraph;
import com.yworks.yfiles.layout.PortConstraint;
import com.yworks.yfiles.layout.PortConstraintKeys;
import com.yworks.yfiles.layout.PortSide;

import java.util.HashMap;
import java.util.Map;

/**
 * Layout stage which handles {@link complete.rotatablenodes.RotatableNodeStyleDecorator} rotated nodes
 * <p>
 * During the {@link AbstractLayoutStage#getCoreLayout()} the layout is calculated with the rotated node's bounding
 * box, i.e. a rectangular box which is large enough to fully include the rotated node.
 * The edges are connected with the actual rotated shape of the node according to the {@link #edgeRoutingMode}.
 * </p>
 */
public class RotatedNodeLayoutStage extends AbstractLayoutStage {

  /**
   * The {@link IDataProvider} key to register a data provider that provides
   * the outline and oriented layout to this stage.
   */
  static final NodeDpKey<RotatedNodeShape> ROTATED_NODE_LAYOUT_DP_KEY
          = new NodeDpKey<>(RotatedNodeShape.class, RotatedNodeLayoutStage.class, "RotatedNodeLayoutDpKey");

  /**
   * The mode which is used to connect edges from the bounding box to the actual shape.
   */
  private RoutingMode edgeRoutingMode;

  /**
   * Initialize a new instance.
   * @param coreLayout as default value null should be used.
   */
  RotatedNodeLayoutStage(ILayoutAlgorithm coreLayout) {
    super(coreLayout);

    this.edgeRoutingMode = RoutingMode.SHORTEST_STRAIGHT_PATH_TO_BORDER;
  }

  /**
   * Executes the layout algorithm.
   *
   * Enlarges the node layout to fully encompass the rotated layout (The rotated layout's bounding box).
   * If the {@link #edgeRoutingMode is set to {@link RoutingMode#FIXED_PORT}}, port constraints are created
   * to keep the ports at their current location. Existing port constraints are adjusted to the rotation.
   *
   * Then, the {@link AbstractLayoutStage#getCoreLayout()} is executed.
   *
   * After the core layout the original node sizes are restored
   * If the {@link #edgeRoutingMode} is set to {@link RoutingMode#SHORTEST_STRAIGHT_PATH_TO_BORDER} the
   * last edge segment is extended from the bounding box to the rotated layout.
   */
  @Override
  public void applyLayout(LayoutGraph graph) {
    if (getCoreLayout() == null) {
      return;
    }

    IDataProvider boundsProvider = graph.getDataProvider(ROTATED_NODE_LAYOUT_DP_KEY);
    if (boundsProvider == null) {
      //no provider: this stage adds nothing to the core layout
      applyLayoutCore(graph);
      return;
    }

    boolean addedSourcePortConstraints = false;
    boolean addedTargetPortConstraints = false;

    IDataMap sourcePortConstraints = (IDataMap) graph.getDataProvider(PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY);
    IDataMap targetPortConstraints = (IDataMap) graph.getDataProvider(PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY);

    if(edgeRoutingMode == RoutingMode.FIXED_PORT) {
      //Fixed port: create port constraints to keep the ports at position
      //in this case: create data providers if they yet don't exist

      if (sourcePortConstraints == null){
        sourcePortConstraints = graph.createEdgeMap();
        graph.addDataProvider(PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY, sourcePortConstraints);
        addedSourcePortConstraints = true;
      }

      if (targetPortConstraints == null) {
        targetPortConstraints = graph.createEdgeMap();
        graph.addDataProvider(PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY, targetPortConstraints);
        addedTargetPortConstraints = true;
      }
    }

    try {
      Map<Node, OldDimensions> originalDimensions = new HashMap<>();

      for (Node node : graph.getNodes()) {
        RotatedNodeShape nodeShape = (RotatedNodeShape) boundsProvider.get(node);
        IOrientedRectangle orientedLayout = nodeShape != null ? nodeShape.getOrientedLayout() : null;
        GeneralPath outline = nodeShape != null ? nodeShape.getOutline() : null;

        if (orientedLayout != null) {
          //if the current node is rotated: apply fixes and remember old layout and size
          INodeLayout oldLayout = graph.getLayout(node);

          RectD layoutBounds = orientedLayout.getBounds();
          YRectangle newLayout = new YRectangle(layoutBounds.getX(), layoutBounds.getY(),
                  layoutBounds.getWidth(), layoutBounds.getHeight());
          YPoint offset = new YPoint(newLayout.getX() - oldLayout.getX(), newLayout.getY() - oldLayout.getY());
          YDimension originalSize = new YDimension(oldLayout.getWidth(), oldLayout.getHeight());

          OldDimensions oldDimensions = new OldDimensions();
          oldDimensions.setOffset(offset);
          oldDimensions.setOutline(outline);
          oldDimensions.setSize(originalSize);

          if (edgeRoutingMode == RoutingMode.FIXED_PORT) {
            //EdgeRoutingMode: FixedPort: keep the ports at their current location

            //the oriented layout's corners to find the best PortSide
            PointD tl = new PointD(orientedLayout.getAnchorX() + orientedLayout.getUpX() * orientedLayout.getHeight(),
                    orientedLayout.getAnchorY() + orientedLayout.getUpY() * orientedLayout.getHeight());
            PointD tr = new PointD(orientedLayout.getAnchorX() + orientedLayout.getUpX() * orientedLayout.getHeight() - orientedLayout.getUpY() * orientedLayout.getWidth(),
                    orientedLayout.getAnchorY() + orientedLayout.getUpY() * orientedLayout.getHeight() + orientedLayout.getUpX() * orientedLayout.getWidth());

            PointD bl = new PointD(orientedLayout.getAnchorX(), orientedLayout.getAnchorY());
            PointD br = new PointD(orientedLayout.getAnchorX() - orientedLayout.getUpY() * orientedLayout.getWidth(),
                    orientedLayout.getAnchorY() + orientedLayout.getUpX() * orientedLayout.getWidth());

            //fore each out edge
            for (Edge edge : node.getOutEdges()) {
              //create a strong port constraint for the side which is closest to the port location (without rotation)
              Object constraint = sourcePortConstraints.get(edge);
              if (constraint == null) {
                PointD point = new PointD(graph.getSourcePointAbs(edge).getX(), graph.getSourcePointAbs(edge).getY());
                PortSide side = findBestSide(point, bl, br, tl, tr);
                sourcePortConstraints.set(edge, PortConstraint.create(side, true));
              }
            }

            //fore each in edge
            for (Edge edge : node.getInEdges()) {
              //create a strong port constraint for the side which is closest to the port location (without rotation)
              Object constraint = targetPortConstraints.get(edge);
              if (constraint == null) {
                PointD point = new PointD(graph.getTargetPointAbs(edge).getX(), graph.getTargetPointAbs(edge).getY());
                PortSide side = findBestSide(point, bl, br, tl, tr);
                targetPortConstraints.set(edge, PortConstraint.create(side, true));
              }
            }
          }

          // For source and target port constraints: fix the PortSide according to the rotation
          double angle = Math.atan2(orientedLayout.getUpY(), orientedLayout.getUpX());
          if (sourcePortConstraints != null) {
            for (Edge edge : node.getOutEdges()) {
              fixPortConstraintSide(sourcePortConstraints, edge, angle);
            }
          }
          if (targetPortConstraints != null) {
            for (Edge edge : node.getInEdges()) {
              fixPortConstraintSide(targetPortConstraints, edge, angle);
            }
          }

          // enlarge the node layout
          YPoint position = new YPoint(newLayout.getX(), newLayout.getY());
          oldDimensions.location = position;
          originalDimensions.put(node, oldDimensions);
          graph.setLocation(node, position);
          graph.setSize(node, newLayout);
        }
      }

      // ===============================================================

      applyLayoutCore(graph);

      // ===============================================================

      IDataProvider groups = graph.getDataProvider(GroupingKeys.GROUP_DPKEY);
      for (Node node : graph.getNodes()) {
        if (groups != null && groups.getBool(node)) {
          // groups don't need to be adjusted to their former size and location because their bounds are entirely
          // calculated by the layout algorithm and they are not rotated
          continue;
        }

        // for each node which has been corrected: undo the correction
        OldDimensions oldDimensions = originalDimensions.get(node);
        YPoint offset = oldDimensions.offset;
        YDimension originalSize = oldDimensions.size;
        INodeLayout newLayout = graph.getLayout(node);

        // create a general path representing the new rotated layout
        GeneralPath path = oldDimensions.outline;
        Matrix2D transform = new Matrix2D();
        transform.translate(new PointD(newLayout.getX() - oldDimensions.location.getX(), newLayout.getY() - oldDimensions.location.getY()));
        path.transform(transform);

        // restore the original size
        graph.setLocation(node, new YPoint(newLayout.getX() - offset.getX(), newLayout.getY() - offset.getY()));
        graph.setSize(node, originalSize);

        if (getEdgeRoutingMode() == RoutingMode.NO_ROUTING) {
          // NoRouting still needs fix for self-loops
          for (Edge edge : node.getEdges()) {
            if (edge.isSelfLoop()) {
              fixPorts(graph, edge, path, false);
              fixPorts(graph, edge, path, true);
            }
          }
          continue;
        }

        if (getEdgeRoutingMode() != RoutingMode.SHORTEST_STRAIGHT_PATH_TO_BORDER) {
          continue;
        }

        // enlarge the adjacent segment to the oriented rectangle (represented by the path)
        // handling in and out edges separately will automatically cause selfloops to be handled correctly
        for (Edge edge : node.getInEdges()) {
          fixPorts(graph, edge, path, false);
        }
        for (Edge edge : node.getOutEdges()) {
          fixPorts(graph, edge, path, true);
        }
      }
    } finally {
      // if data provider for the port constraints have been added
      // remove and dispose them
      if (addedSourcePortConstraints) {
        graph.removeDataProvider(PortConstraintKeys.SOURCE_PORT_CONSTRAINT_DPKEY);
        graph.disposeEdgeMap((IEdgeMap)sourcePortConstraints);
      }
      if (addedTargetPortConstraints) {
        graph.removeDataProvider(PortConstraintKeys.TARGET_PORT_CONSTRAINT_DPKEY);
        graph.disposeEdgeMap((IEdgeMap)targetPortConstraints);
      }
    }
  }

  /**
   * Find the best {@link PortSide} according to the position of the port.
   *
   * The orientation is not rotated i.e. bottomLeft is always the anchor of the oriented rectangle.
   *
   * @param point The port position.
   * @param bottomLeft The bottom left corner of the oriented rectangle.
   * @param bottomRight The bottom right corner.
   * @param topLeft The top left corner.
   * @param topRight The top right corner.
   * @return The side to which the given port is closest.
   */
  private static PortSide findBestSide(PointD point, PointD bottomLeft, PointD bottomRight, PointD topLeft, PointD topRight) {
    //determines the distance to the side of the oriented rectangle with a small penalty to the left and right
    double distToBottom = point.distanceToSegment(bottomLeft, bottomRight);
    double distToTop = point.distanceToSegment(topLeft, topRight);
    double distToLeft = point.distanceToSegment(topLeft, bottomLeft) * 1.05;
    double distToRight = point.distanceToSegment(topRight, bottomRight) * 1.05;

    PortSide side;

    if (distToTop <= distToBottom) {
      if (distToTop <= distToLeft) {
        side = distToTop < distToRight ? PortSide.NORTH : PortSide.EAST;
      } else {
        side = distToLeft < distToRight ? PortSide.WEST : PortSide.EAST;
      }
    } else if (distToBottom <= distToLeft) {
      side = distToBottom <= distToRight ? PortSide.SOUTH : PortSide.EAST;
    } else {
      side = distToLeft < distToRight ? PortSide.WEST : PortSide.EAST;
    }
    return side;
  }

  /**
   * Fix the {@link PortSide} of the given edge's port constraints
   * for the oriented rectangles rotation.
   *
   * If the oriented rectangle is rotated 180 degrees the port sides will be flipped, e.g.
   * The port constraints will be replaced.
   * @param portConstraints The data provider for source or target constraints.
   * @param edge The edge to fix the port constraints for.
   * @param angle The angle as obtained by applying {@link Math#atan2(double, double)}.
   * to the oriented rectangle's upX and upY vectors.
   */
  private static void fixPortConstraintSide(IDataMap portConstraints, Edge edge, double angle) {
    PortConstraint constraint = (PortConstraint)portConstraints.get(edge);
    if (constraint != null && !constraint.isAtAnySide()) {
      PortSide side = constraint.getSide();
      if (angle < Math.PI / 4 && angle > -Math.PI / 4) {
        // top is rotated 90 deg left
        switch (side) {
          case SOUTH:
            side = PortSide.WEST;
            break;
          case WEST:
            side = PortSide.NORTH;
            break;
          case EAST:
            side = PortSide.SOUTH;
            break;
          case NORTH:
            side = PortSide.EAST;
            break;
        }
      } else if (angle > Math.PI / 4 && angle < Math.PI * 0.75 && angle > 0) {
        // 180 deg
        switch (side) {
          case WEST:
            side = PortSide.EAST;
            break;
          case SOUTH:
            side = PortSide.NORTH;
            break;
          case EAST:
            side = PortSide.WEST;
            break;
          case NORTH:
            side = PortSide.SOUTH;
            break;
        }
      } else if (angle > Math.PI * 0.75 || angle < -Math.PI * 0.75) {
        // top is rotated 90 deg right
        switch (side) {
          case WEST:
            side = PortSide.SOUTH;
            break;
          case SOUTH:
            side = PortSide.EAST;
            break;
          case EAST:
            side = PortSide.NORTH;
            break;
          case NORTH:
            side = PortSide.WEST;
            break;
        }

      } else {
        // no rotation
        return;
      }
      // Side is not writable, so set new constraint
      portConstraints.set(edge, PortConstraint.create(side, constraint.isStrong()));
    }
  }

  /**
   * Fix the ports for {@link RoutingMode#SHORTEST_STRAIGHT_PATH_TO_BORDER}
   * by enlarging the adjacent segment to the rotated layout.
   *
   * @param graph The layout graph to work on.
   * @param edge The edge to fix.
   * @param path A {@link GeneralPath} which represents the rotated layout.
   * @param atSource Whether to fix the source or target port of the edge.
   */
  private static void fixPorts(LayoutGraph graph, Edge edge, GeneralPath path, boolean atSource) {
    IEdgeLayout el = graph.getLayout(edge);
    int pointCount = el.pointCount();
    // find the opposite point of the port at the adjacent segment
    YPoint firstBend = atSource
            ? (pointCount > 0 ? el.getPoint(0) : graph.getTargetPointAbs(edge))
            : (pointCount > 0 ? el.getPoint(pointCount - 1) : graph.getSourcePointAbs(edge));
    // The port itself
    YPoint port = (atSource ? graph.getSourcePointAbs(edge) : graph.getTargetPointAbs(edge));
    // The adjacent segment as vector pointing from the opposite point to the port
    YPoint direction = YPoint.subtract(port, firstBend);
    // find the intersection (there is always one)
    double intersection = path.findRayIntersection(asPointD(firstBend), asPointD(direction));
    YPoint point = port;
    if (intersection < Double.POSITIVE_INFINITY) {
      // found an intersection: extend the adjacent segment
      point = YPoint.add(firstBend, new YPoint(direction.getX() * intersection, direction.getY() * intersection));
    } else {
      // no intersection: connect to the original port's nearest point
      GeneralPath.PathCursor cursor = path.createCursor();
      double minDistance = Double.POSITIVE_INFINITY;
      while (cursor.moveNext()) {
        double distance = port.distanceTo(asYPoint(cursor.getCurrentEndPoint()));
        if (distance < minDistance) {
          minDistance = distance;
          point = asYPoint(cursor.getCurrentEndPoint());
        }
      }
    }
    // set the port position
    if (atSource) {
      graph.setSourcePointAbs(edge, point);
    } else {
      graph.setTargetPointAbs(edge, point);
    }
  }

  /**
   * Gets YPoint object and returns same object as PointD.
   */
  private static PointD asPointD( YPoint p ) {
    return new PointD(p.getX(), p.getY());
  }

  /**
   * Gets PointD object and returns same object as YPoint.
   */
  private static YPoint asYPoint( PointD p ) {
    return new YPoint(p.getX(), p.getY());
  }

  /**
   * Returns the mode to use to connect edges from the bounding box to the actual shape.
   */
  private RoutingMode getEdgeRoutingMode() {
    return edgeRoutingMode;
  }

  /**
   * Sets the mode to use to connect edges from the bounding box to the actual shape.
   */
  void setEdgeRoutingMode(RoutingMode edgeRoutingMode) {
    this.edgeRoutingMode = edgeRoutingMode;
  }

  /**
   * The mode which determines how to route the edges from the bounding box to the actual layout.
   */
  public enum RoutingMode {
    /**
     * Does nothing. This ideally suited for layout algorithms which connect to the center of the nodes,
     * e.g. {@link com.yworks.yfiles.layout.organic.OrganicLayout}.
     */
    NO_ROUTING,

    /**
     * Prolongs the last edge segment until it connects with the actual node shape.
     */
    SHORTEST_STRAIGHT_PATH_TO_BORDER,

    /**
     * Keeps the ports at the position they had before the layout.
     */
    FIXED_PORT
  }

  /**
   * Remember some aspects of the original layout (before fixing size and before the layout.
   */
  private static class OldDimensions {

    //the offset the position is moved while fixing the size
    private YPoint offset;

    //the original size
    private YDimension size;

    //the original location
    private YPoint location;

    //the original outline of the node
    private GeneralPath outline;

    /**
     * Returns the offset position is moved while fixing the size.
     */
    public YPoint getOffset() {
      return offset;
    }

    /**
     * Sets the offset position is moved while fixing the size.
     */
    public void setOffset(YPoint offset) {
      this.offset = offset;
    }

    /**
     * Returns the original size.
     */
    public YDimension getSize() {
      return size;
    }

    /**
     * Sets the original size.
     */
    public void setSize(YDimension size) {
      this.size = size;
    }

    /**
     * Returns the original location.
     */
    public YPoint getLocation() {
      return location;
    }

    /**
     * Sets the original location.
     */
    public void setLocation(YPoint location) {
      this.location = location;
    }

    /**
     * Returns the original outline of the node.
     */
    public GeneralPath getOutline() {
      return outline;
    }

    /**
     * Sets the original outline of the node.
     */
    public void setOutline(GeneralPath outline) {
      this.outline = outline;
    }
  }

  /**
   * Data holder used by {@link #ROTATED_NODE_LAYOUT_DP_KEY}.
   */
  public static class RotatedNodeShape {

    /**
     * The {@link com.yworks.yfiles.graph.styles.IShapeGeometry} of a node.
     */
    public GeneralPath outline;

    /**
     * The rotated layout of a node.
     */
    IOrientedRectangle orientedLayout;

    /**
     * creates a new instance.
     */
    RotatedNodeShape(GeneralPath outline, IOrientedRectangle orientedLayout) {
      this.outline = outline;
      this.orientedLayout = orientedLayout;
    }

    /**
     * Returns the {@link com.yworks.yfiles.graph.styles.IShapeGeometry} of a node.
     */
    public GeneralPath getOutline() {
      return outline;
    }

    /**
     * Sets the {@link com.yworks.yfiles.graph.styles.IShapeGeometry} of a node.
     */
    private void setOutline(GeneralPath outline) {
      this.outline = outline;
    }

    /**
     * Returns the rotated layout of a node.
     */
    IOrientedRectangle getOrientedLayout() {
      return orientedLayout;
    }

    /**
     * Sets the oriented layout of a node.
     */
    private void setOrientedLayout(IOrientedRectangle orientedLayout) {
      this.orientedLayout = orientedLayout;
    }
  }
}
