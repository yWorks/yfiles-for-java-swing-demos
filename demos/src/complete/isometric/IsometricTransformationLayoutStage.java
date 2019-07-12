/****************************************************************************
 **
 ** This demo file is part of yFiles for Java (Swing) 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.isometric;

import com.yworks.yfiles.algorithms.AbstractDpKey;
import com.yworks.yfiles.algorithms.Edge;
import com.yworks.yfiles.algorithms.GraphObjectDpKey;
import com.yworks.yfiles.algorithms.IDataProvider;
import com.yworks.yfiles.algorithms.IEdgeMap;
import com.yworks.yfiles.algorithms.INodeMap;
import com.yworks.yfiles.algorithms.Maps;
import com.yworks.yfiles.algorithms.Node;
import com.yworks.yfiles.algorithms.YDimension;
import com.yworks.yfiles.algorithms.YInsets;
import com.yworks.yfiles.algorithms.YOrientedRectangle;
import com.yworks.yfiles.algorithms.YPoint;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.layout.AbstractLayoutStage;
import com.yworks.yfiles.layout.GroupingKeys;
import com.yworks.yfiles.layout.IEdgeLabelLayout;
import com.yworks.yfiles.layout.IEdgeLayout;
import com.yworks.yfiles.layout.ILayoutAlgorithm;
import com.yworks.yfiles.layout.INodeLabelLayout;
import com.yworks.yfiles.layout.INodeLayout;
import com.yworks.yfiles.layout.LabelLayoutData;
import com.yworks.yfiles.layout.LabelLayoutKeys;
import com.yworks.yfiles.layout.LayoutGraph;

/**
 * An {@link com.yworks.yfiles.layout.ILayoutStage} that transforms the graph to layout space before layout calculation is done and
 * transforms the graph back to the view space afterwards. The layout space is base area of the isometric space. The
 * view space contains the projection of the isometric space.
 */
class IsometricTransformationLayoutStage extends AbstractLayoutStage {
  static final GraphObjectDpKey<IsometricTransformationSupport> TRANSFORMATION_DATA_DPKEY =
          new GraphObjectDpKey(IsometricGeometry.class, IsometricTransformationLayoutStage.class, "TransformationDataDpKey");

  static final int GROUP_NODE_INSET = 20;

  private boolean fromSketchMode;

  /**
   * Creates a new instance of IsometricTransformationLayoutStage.
   */
  IsometricTransformationLayoutStage( ILayoutAlgorithm coreLayout, boolean fromSketchMode ) {
    super(coreLayout);
    this.fromSketchMode = fromSketchMode;
  }

  /**
   * Determines whether or not this layout stage transforms the coordinates of the graph elements before calculating
   * layout. This is important for incremental layout.
   */
  boolean isFromSketchMode() {
    return this.fromSketchMode;
  }

  /**
   * Specifies whether or not this layout stage transforms the coordinates of the graph elements before calculating
   * layout. This is important for incremental layout.
   */
  void setFromSketchMode( boolean value ) {
    this.fromSketchMode = value;
  }

  /**
   * Transforms the graph to the layout space, lay it out using the core layout and transforms the result back into
   * the view space.
   */
  @Override
  public void applyLayout( LayoutGraph graph ) {
    // The group node insets will not automatically be passed to the layout graph by the LayoutExecutor.
    // Therefore, we set an appropriate data provider manually.
    IDataProvider oldGroupNodeInsets = graph.getDataProvider(GroupingKeys.GROUP_NODE_INSETS_DPKEY);
    IDataProvider oldMinimumNodeSizes = graph.getDataProvider(GroupingKeys.MINIMUM_NODE_SIZE_DPKEY);
    INodeMap groupNodeInsets = Maps.createHashedNodeMap();
    INodeMap minimumNodeSizes = Maps.createHashedNodeMap();

    // To assure that a group node is always wide enough to contain its label and group state icon a minimum node size
    // is calculated for each group node.
    for (Node node : graph.getNodes()) {
      INodeLabelLayout[] labels = graph.getLabelLayout(node);
      if (labels.length > 0) {
        INodeLabelLayout label = labels[0];
        groupNodeInsets.set(node, new YInsets(
                GROUP_NODE_INSET,
                GROUP_NODE_INSET,
                GROUP_NODE_INSET + label.getBoundingBox().getHeight(),
                GROUP_NODE_INSET));

        minimumNodeSizes.set(node, new YDimension(
                label.getBoundingBox().getWidth() + GroupNodeStyle.ICON_WIDTH + (GroupNodeStyle.ICON_GAP * 2),
                0));
      }
    }

    graph.addDataProvider(GroupingKeys.GROUP_NODE_INSETS_DPKEY, groupNodeInsets);
    graph.addDataProvider(GroupingKeys.MINIMUM_NODE_SIZE_DPKEY, minimumNodeSizes);

    // Transform the graph to the layout space.
    this.transformGraph(graph, false, this.fromSketchMode);

    // Calculate the layout.
    this.applyLayoutCore(graph);

    // Transform the graph back to the view space.
    this.transformGraph(graph, true, this.fromSketchMode);

    // Restore the original group node insets and minimum size provider.
    graph.removeDataProvider(GroupingKeys.GROUP_NODE_INSETS_DPKEY);
    if (oldGroupNodeInsets != null) {
      graph.addDataProvider(GroupingKeys.GROUP_NODE_INSETS_DPKEY, oldGroupNodeInsets);
    }
    graph.removeDataProvider(GroupingKeys.MINIMUM_NODE_SIZE_DPKEY);
    if (oldMinimumNodeSizes != null) {
      graph.addDataProvider(GroupingKeys.MINIMUM_NODE_SIZE_DPKEY, oldMinimumNodeSizes);
    }
  }

  /**
   * Transforms the all edge points, node positions and sizes to the view or layout space.
   * @param graph  the graph to transform
   * @param toView <code>true</code> to transform the given point to the view space, <code>false</code> to the layout space
   * @param fromSketchMode <code>true</code> when the initial layout is taken into account, <code>false</code> otherwise
   */
  private void transformGraph( LayoutGraph graph, boolean toView, boolean fromSketchMode ) {
    // The transformation changes the size of the nodes. To avoid that this changes the position of the source and
    // target points of the edges, they are stored before the transformation and restored afterwards.
    IEdgeMap sourcePoints = Maps.createHashedEdgeMap();
    IEdgeMap targetPoints = Maps.createHashedEdgeMap();
    for (Edge edge : graph.getEdges()) {
      sourcePoints.set(edge, graph.getSourcePointAbs(edge));
      targetPoints.set(edge, graph.getTargetPointAbs(edge));
    }

    // Transform the node sizes and locations.
    IDataProvider transformationData = graph.getDataProvider(IsometricTransformationLayoutStage.TRANSFORMATION_DATA_DPKEY);
    for (Node node : graph.getNodes()) {
      INodeLayout nodeLayout = graph.getLayout(node);
      IsometricGeometry data = (IsometricGeometry) transformationData.get(node);

      if (toView) {
        double oldWidth = nodeLayout.getWidth();
        double oldHeight = nodeLayout.getHeight();
        double oldCenterX = nodeLayout.getX() + (oldWidth * 0.5);
        double oldCenterY = nodeLayout.getY() + (oldHeight * 0.5);
        // Store the width and height calculated by the core layout. This is necessary for group nodes!
        data.setWidth(oldWidth);
        data.setDepth(oldHeight);

        RectD bounds = IsometricTransformationSupport.calculateViewBounds(data);
        double newWidth = bounds.width;
        double newHeight = bounds.height;
        double newCenterX = IsometricTransformationSupport.toViewX(oldCenterX, oldCenterY);
        double newCenterY = IsometricTransformationSupport.toViewY(oldCenterX, oldCenterY) - (data.getHeight() * 0.5);
        nodeLayout.setSize(newWidth, newHeight);
        nodeLayout.setLocation(newCenterX - (newWidth * 0.5), newCenterY - (newHeight * 0.5));
      } else {
        double oldCenterX = nodeLayout.getX() + (nodeLayout.getWidth() * 0.5);
        double oldCenterY = nodeLayout.getY() + (nodeLayout.getHeight() * 0.5) + (data.getHeight() * 0.5);
        double newCenterX = IsometricTransformationSupport.toLayoutX(oldCenterX, oldCenterY);
        double newCenterY = IsometricTransformationSupport.toLayoutY(oldCenterX, oldCenterY);
        double newWidth = data.getWidth();
        double newHeight = data.getDepth();
        nodeLayout.setSize(newWidth, newHeight);
        if (fromSketchMode) {
          nodeLayout.setLocation(newCenterX - (newWidth * 0.5), newCenterY - (newHeight * 0.5));
        }
      }
    }

    // Transform bends and end points for all edges in the graph.
    for (Edge edge : graph.getEdges()) {
      IEdgeLayout edgeLayout = graph.getLayout(edge);
      for (int i = 0, n = edgeLayout.pointCount(); i < n; i++) {
        YPoint point = edgeLayout.getPoint(i);
        YPoint transformedPoint = this.transformPoint(point, toView, fromSketchMode);
        edgeLayout.setPoint(i, transformedPoint.x, transformedPoint.y);
      }

      // Restore the position of the source and target points of the edges.
      graph.setSourcePointAbs(edge, this.transformPoint((YPoint) sourcePoints.get(edge), toView, fromSketchMode));
      graph.setTargetPointAbs(edge, this.transformPoint((YPoint) targetPoints.get(edge), toView, fromSketchMode));
    }

    if (toView) {
      for (Edge edge : graph.getEdges()) {
        IDataProvider edgeLabelMap = graph.getDataProvider(LabelLayoutKeys.EDGE_LABEL_LAYOUT_DPKEY);
        IEdgeLabelLayout[] labels = graph.getLabelLayout(edge);
        LabelLayoutData[] labelLayoutData = (LabelLayoutData[]) edgeLabelMap.get(edge);
        IEdgeLayout edgeLayout = graph.getLayout(edge);
        INodeLayout sourceLayout = graph.getLayout(edge.source());
        if (labelLayoutData != null) {
          INodeLayout targetLayout = graph.getLayout(edge.target());
          for (int i = 0; i < labels.length; ++i) {
            IEdgeLabelLayout label = labels[i];
            LabelLayoutData labelData = labelLayoutData[i];
            double oldWidth = labelData.getWidth();
            double oldHeight = labelData.getHeight();
            double oldCenterX = labelData.getX() + (oldWidth * 0.5);
            double oldCenterY = labelData.getY() + (oldHeight * 0.5);
            // Store the width and height calculated by the core layout. This is necessary for group nodes!
            IsometricGeometry data = (IsometricGeometry) transformationData.get(label);
            YOrientedRectangle labelBounds = labelData.getBounds();
            data.setHorizontal(labelBounds.getUpY() == -1 || labelBounds.getUpY() == 1);
            data.setWidth(oldWidth);
            data.setDepth(oldHeight);

            RectD bounds = IsometricTransformationSupport.calculateViewBounds(data);
            double newWidth = bounds.getWidth();
            double newHeight = bounds.getHeight();
            double newCenterX = IsometricTransformationSupport.toViewX(oldCenterX, oldCenterY);
            double newCenterY = IsometricTransformationSupport.toViewY(oldCenterX, oldCenterY) - (data.getHeight() * 0.5);

            YOrientedRectangle newBounds = new YOrientedRectangle(
                    newCenterX - (newWidth * 0.5),
                    newCenterY + (newHeight * 0.5), newWidth, newHeight);
            Object parameter = label.getLabelModel().createModelParameter(newBounds, edgeLayout, sourceLayout, targetLayout);
            YOrientedRectangle labelPlacement = label.getLabelModel().getLabelPlacement(newBounds.getSize(), edgeLayout, sourceLayout, targetLayout, parameter);
            label.setModelParameter(parameter);
            label.getOrientedBox().adoptValues(labelPlacement);
          }
        }
      }
      graph.removeDataProvider(LabelLayoutKeys.EDGE_LABEL_LAYOUT_DPKEY);
    } else {
      IEdgeMap edgeLabelMap = Maps.createHashedEdgeMap();
      for (Edge edge : graph.getEdges()) {
        IEdgeLabelLayout[] labels = graph.getLabelLayout(edge);
        LabelLayoutData[] labelLayoutData = new LabelLayoutData[labels.length];
        for (int i = 0; i < labels.length; ++i) {
          IEdgeLabelLayout label = labels[i];
          IsometricGeometry data = (IsometricGeometry) transformationData.get(label);
          if (data.isHorizontal()) {
            labelLayoutData[i] = new LabelLayoutData(data.getWidth(), data.getDepth());
          } else {
            labelLayoutData[i] = new LabelLayoutData(data.getDepth(), data.getWidth());
          }
          labelLayoutData[i].setPreferredPlacementDescriptor(label.getPreferredPlacementDescriptor());
        }
        edgeLabelMap.set(edge, labelLayoutData);
      }
      graph.addDataProvider(LabelLayoutKeys.EDGE_LABEL_LAYOUT_DPKEY, edgeLabelMap);
    }
  }

  /**
   * Transforms the given point to the view or layout space.
   * @param point  the point to transform
   * @param toView <code>true</code> to transform the given point to the view space, <code>false</code> to the layout space
   * @param fromSketchMode <code>true</code> when the initial layout is taken into account, <code>false</code> otherwise
   */
  private static YPoint transformPoint( YPoint point, boolean toView, boolean fromSketchMode ) {
    double x = point.x;
    double y = point.y;

    if (toView) {
      return new YPoint(IsometricTransformationSupport.toViewX(x, y), IsometricTransformationSupport.toViewY(x, y));
    } else if (fromSketchMode) {
      return new YPoint(IsometricTransformationSupport.toLayoutX(x, y), IsometricTransformationSupport.toLayoutY(x, y));
    }
    return point;
  }
}
