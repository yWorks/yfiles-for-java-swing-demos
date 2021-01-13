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
package layout.hierarchiclayout;

import com.yworks.yfiles.view.IVisual;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IMapper;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Visualizes the layers that have been calculated by the {@link com.yworks.yfiles.layout.hierarchic.HierarchicLayout}.
 * Each layer is visualized by a rectangle.
 */
class LayerVisualCreator implements IVisualCreator {
  private static final int LAYER_INSETS = 10;

  // the dark brush used for drawing the layers
  private static final Paint DARK_PAINT = new Color(150, 200, 255, 128);
  // the light brush used for drawing the layers
  private static final Paint LIGHT_PAINT = new Color(220, 240, 240, 128);

  // the bounds of the complete drawing
  private RectD bounds;
  // the list of the dividers (one less than the number of layers)
  // a divider is the y coordinate between two adjacent layers
  private List<Float> dividers = new ArrayList<>();

  /**
   * Updates the bounds of the layers based on the bounds of their containing nodes.
   */
  public void updateLayerBounds(IGraph graph, IMapper<INode, Integer> layerMapper) {
    // count the layers
    Collection<INode> nodes;
    // collect nodes except for empty group nodes
    nodes = graph.getNodes().stream().filter(node -> graph.getChildren(node).size() == 0).collect(Collectors.toList());

    int layerCount = 0;
    for (INode node : nodes) {
      layerCount = Math.max(layerCount, layerMapper.getValue(node));
    }
    layerCount++;

    // calculate min and max values
    int[] mins = new int[layerCount];
    int[] maxs = new int[layerCount];
    for (int i = 0; i < maxs.length; i++) {
      maxs[i] = Integer.MIN_VALUE;
    }

    for (int i = 0; i < maxs.length; i++) {
      mins[i] = Integer.MAX_VALUE;
    }

    double minX = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY;
    for (INode node : nodes) {
      double minY = node.getLayout().getY();
      double maxY = minY + node.getLayout().getHeight();
      mins[layerMapper.getValue(node)] = Math.min(mins[layerMapper.getValue(node)], (int) minY);
      maxs[layerMapper.getValue(node)] = Math.max(maxs[layerMapper.getValue(node)], (int) maxY);
      minX = Math.min(minX, node.getLayout().getX());
      maxX = Math.max(maxX, node.getLayout().getX() + node.getLayout().getWidth());
    }

    // now determine divider locations
    dividers.clear();
    for (int i = 0; i < maxs.length - 1; i++) {
      dividers.add((maxs[i] + mins[i + 1]) * 0.5f);
    }

    // determine the bounds of all elements
    int margin = 10;
    mins[0] -= margin;
    minX -= margin;
    maxX += margin;
    maxs[maxs.length - 1] += margin;
    if (nodes.iterator().hasNext()) {
      mins[0] -= margin;
      minX -= margin;
      maxX += margin;
      maxs[maxs.length - 1] += margin;
      bounds = new RectD((float)minX, mins[0], (float)(maxX - minX), maxs[maxs.length - 1] - mins[0]);
    } else {
      bounds = new RectD();
    }
  }

  /**
   * Gets the layer to insert a node at the given location.
   * @param location the location
   * @return a positive value if a specific layer is hit, a negative one to indicate that a new layer should be inserted
   *         before layer -(value + 1) - int.MaxValue if no layer has been hit.
   */
  public int getLayer(PointD location) {
    // global bounds
    RectD nbounds = new RectD(
        bounds.getX(),
        bounds.getY() - LAYER_INSETS,
        bounds.getWidth(),
        bounds.getHeight() + LAYER_INSETS * 2);
    if (location.getY() < bounds.getMinY()) {
      // before the first layer
      return -1;
    }
    if (location.getY() > bounds.getMaxY()) {
      // after the last layer
      return -((dividers.size() + 2) + 1);
    }
    // nothing found,
    if (!nbounds.contains(location)) {
      return Integer.MAX_VALUE;
    }

    // now search the layer
    double top = bounds.getMinY();

    int layerCount = 0;
    for (float divider : dividers) {
      RectD layerBounds = new RectD(bounds.getX(), top, bounds.getWidth(), divider - top);
      if (layerBounds.contains(location)) {
        return getLayerIndex(location, layerBounds, layerCount);
      }
      layerCount++;
      top = divider;
    }
    {
      RectD layerBounds = new RectD(bounds.getX(), top, bounds.getWidth(), bounds.getMaxY() - top);
      if (layerBounds.contains(location)) {
        return getLayerIndex(location, layerBounds, layerCount);
      }
    }
    // should not really happen...
    return Integer.MAX_VALUE;
  }

  /**
   * Checks if the specified layer has been clicked near the border. If the layer has been clicked near the top border
   * return <code>-(layerIndex + 1)</code>, i.e a new layer should be inserted above the given one. If the layer has
   * been clicked near the bottom border return <code>-(layerIndex + 2)</code>, i.e. a new layer should be inserted
   * below the given one. Otherwise return the specified layer index.
   */
  private int getLayerIndex(PointD location, RectD layerBounds, int layerIndex) {
    // check if close to top or bottom
    if (location.getY() - layerBounds.getMinY() < LAYER_INSETS) {
      // before current layer
      return -(layerIndex + 1);
    }
    if (layerBounds.getMaxY() - location.getY() < LAYER_INSETS) {
      // after current layer
      return -(layerIndex + 2);
    }
    // in current layer
    return layerIndex;
  }

  /**
   * Gets the bounds of a layer by index as specified by {@link #getLayer(com.yworks.yfiles.geometry.PointD)}.
   */
  public RectD getLayerBounds(int layerIndex) {
    if (layerIndex == Integer.MAX_VALUE) {
      return RectD.INFINITE;
    }
    if (layerIndex < 0) {
      // new layer
      int beforeLayer = -(layerIndex + 1);
      if (beforeLayer <= dividers.size()) {
        RectD layerBounds = getLayerBounds(beforeLayer);
        return new RectD(layerBounds.getX(), layerBounds.getMinY() - LAYER_INSETS, layerBounds.getWidth(), LAYER_INSETS * 2);
      } else {
        // after last layer
        RectD layerBounds = getLayerBounds(dividers.size());
        return new RectD(layerBounds.getX(), layerBounds.getMaxY() - LAYER_INSETS, layerBounds.getWidth(), LAYER_INSETS * 2);
      }
    } else {
      double top = layerIndex > 0 ? dividers.get(layerIndex - 1) : bounds.getMinY();
      double bottom = layerIndex < dividers.size() ? dividers.get(layerIndex) : bounds.getMaxY();
      return new RectD(bounds.getX(), top, bounds.getWidth(), bottom - top);
    }
  }

  @Override
  public IVisual createVisual(IRenderContext renderContext) {
    // create a new VisualGroup and update it with the current state
    VisualGroup container = new VisualGroup();
    updateLayerVisuals(container);
    return container;
  }

  @Override
  public IVisual updateVisual(IRenderContext renderContext, IVisual oldVisual) {
    if (oldVisual instanceof VisualGroup) {
      VisualGroup visual = (VisualGroup) oldVisual;
      updateLayerVisuals(visual);
      return visual;
    } else {
      return createVisual(renderContext);
    }
  }

  /**
   * Iterates over the dividers of the layers from top to bottom and updates or creates a rectangle for each layer for
   * its visualization.
   * @param container contains the visuals for the layers
   */
  private void updateLayerVisuals(VisualGroup container) {
    // create a rectangle for each layer for its visualization
    double y = bounds.getMinY();
    int count = 0;
    for (float divider : dividers) {
      createOrUpdateLayerVisual(container, count, y, divider);
      count++;
      y = divider;
    }

    // we have one less dividers than the number of layers,
    // so we have to update or create here one more rectangle
    createOrUpdateLayerVisual(container, count, y, bounds.getMaxY());
    count++;

    // the number of layers has decreased - remove all remaining rectangles
    while (container.getChildren().size() > count) {
      container.getChildren().remove(container.getChildren().size() - 1);
    }
  }

  private void createOrUpdateLayerVisual(VisualGroup container, int index, double y, double maxY) {
    ShapeVisual rectangle;
    Rectangle2D.Double rect;
    if (container.getChildren().size() <= index) {
      // the number of layers has increased - we need to create additional rectangles
      rect = new Rectangle2D.Double();
      container.getChildren().add(rectangle = new ShapeVisual(rect));
    } else {
      // update an existing rectangle
      rectangle = (ShapeVisual) container.getChildren().get(index);
      rect = (Rectangle2D.Double) rectangle.getShape();
    }
    // set the bounds of the rectangle
    rect.setRect(bounds.getMinX(), y, bounds.getWidth(), maxY - y);
    rectangle.setFill(index %2 == 1 ? LIGHT_PAINT : DARK_PAINT);
  }
}
