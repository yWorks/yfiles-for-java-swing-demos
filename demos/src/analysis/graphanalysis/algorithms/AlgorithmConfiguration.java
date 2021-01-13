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
package analysis.graphanalysis.algorithms;

import analysis.graphanalysis.ModelItemInfo;
import analysis.graphanalysis.styles.SourceTargetNodeStyle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.Mapper;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.Pen;

import javax.swing.JPopupMenu;
import java.awt.Color;

/**
 * Abstract base class for the different analysis algorithm configurations that
 * can be displayed in the demo.
 */
public abstract class AlgorithmConfiguration {
  private boolean directed;
  private boolean useUniformWeights;
  private boolean edgeRemoved;
  private int kCore;
  private Mapper<INode, Boolean> incrementalElements;

  protected AlgorithmConfiguration() {
    directed = false;
    useUniformWeights = false;
    edgeRemoved = false;
    kCore = 1;
  }

  /**
   * Returns whether or not to take edge direction into account.
   * @return true for directed edges, false otherwise
   */
  public boolean isDirected() {
    return directed;
  }

  /**
   * Specifies whether or not to take edge direction into account.
   * @param directed true for directed edge, false otherwise
   */
  public void setDirected(boolean directed) {
    this.directed = directed;
  }

  /**
   * Returns whether or not to use uniform weights for all edges.
   * @return true for uniform weights, false otherwise
   */
  public boolean isUseUniformWeights() {
    return useUniformWeights;
  }

  /**
   * Specifies whether or not to use uniform weights for all edges.
   * @param useUniformWeights true for uniform weights, false otherwise
   */
  public void setUseUniformWeights(boolean useUniformWeights) {
    this.useUniformWeights = useUniformWeights;
  }

  /**
   * Returns the k-Core value to use in the k-Core connectivity analysis
   * @return the k-Core value
   */
  public int getkCore() {
    return kCore;
  }

  /**
   * Sets the k-Core value to use in the k-Core connectivity analysis
   * @param kCore the k-Core value
   */
  public void setkCore(int kCore) {
    this.kCore = kCore;
  }

  /**
   * Returns the elements that are changed from user actions.
   * @return the incremental elements mapper
   */
  public Mapper<INode, Boolean> getIncrementalElements() {
    return incrementalElements;
  }

  /**
   * Specifies the elements that are changed from user actions.
   * @param incrementalElements the incremental elements mapper
   */
  public void setIncrementalElements(Mapper<INode, Boolean> incrementalElements) {
    this.incrementalElements = incrementalElements;
  }

  /**
   * Returns whether or not an edge(s) has been removed.
   * @return true if a value has been removed, false otherwise
   */
  public boolean isEdgeRemoved() {
    return edgeRemoved;
  }

  /**
   * Specifies whether or not an edge(s) has been removed.
   * @param edgeRemoved true if a value has been removed, false otherwise
   */
  public void setEdgeRemoved(boolean edgeRemoved) {
    this.edgeRemoved = edgeRemoved;
  }

  /**
   * Calculates the result of the analysis algorithm and performs
   * post-processing steps.
   * @param graphComponent the given graph component
   */
  public void apply(GraphComponent graphComponent) {
    runAlgorithm(graphComponent.getGraph());
  }

  /**
   * Adds algorithm-specific entries to the given context menu.
   * May be overridden to add algorithm-specific entries and functionality to the popup menu.
   * @param contextMenu the context menu to which the entries are added
   * @param item the item that is affected by this context menu
   * @param graphComponent the given graph component
   */
  public void populateContextMenu(JPopupMenu contextMenu, IModelItem item, GraphComponent graphComponent) {
  }

  /**
   * Calculates the result of the analysis algorithm.
   * Must be overridden to run the selected algorithm.
   * @param graph the graph on which the algorithm is executed
   */
  protected abstract void runAlgorithm(IGraph graph);

  /**
   * Returns a node style for marked nodes.
   * @param color the marked node's color
   * @returns the marked node's style
   * @see #generateColors(GradientInfo)
   */
  protected INodeStyle getMarkedNodeStyle(Color color) {
    ShapeNodeStyle style = new ShapeNodeStyle();
    style.setPaint(color);
    style.setPen(null);
    style.setShape(ShapeNodeShape.ELLIPSE);
    return style;
  }

  /**
   * Returns a node style to mark source and target nodes of paths.
   * @param source true if the node is the source node of a path.
   * @param target true if the node is the target node of a path.
   * @returns the marked node's style
   */
  protected INodeStyle getSourceTargetNodeStyle(boolean source, boolean target) {
    SourceTargetNodeStyle.Type type;
    if (source && target) {
      type = SourceTargetNodeStyle.Type.TYPE_SOURCE_AND_TARGET;
    } else if (source) {
      type = SourceTargetNodeStyle.Type.TYPE_SOURCE;
    } else {
      type = SourceTargetNodeStyle.Type.TYPE_TARGET;
    }
    return new SourceTargetNodeStyle(type);
  }

  /**
   * Returns a edge style for marked edges.
   * @param isDirected whether or not the style draws an arrow
   * @param color the color for the edge
   * @returns the marked edge's style
   */
  protected IEdgeStyle getMarkedEdgeStyle(boolean isDirected, Color color) {
    IArrow arrow = isDirected ? new Arrow(ArrowType.DEFAULT, color) : IArrow.NONE;

    PolylineEdgeStyle style = new PolylineEdgeStyle();
    style.setPen(new Pen(color, 5));
    style.setSourceArrow(IArrow.NONE);
    style.setTargetArrow(arrow);
    return style;
  }

  /**
   * Returns the edge weight for a given edge.
   * This implementation retrieves weights from edge labels or alternatively from the edge length.
   * @param edge the given edge
   * @return weight of the edge
   */
  protected double getEdgeWeight(IEdge edge) {
    if (this.useUniformWeights) {
      return 1;
    }

    // if edge has at least one label...
    if (edge.getLabels().size() > 0) {
      // ...try to return its value
      double edgeWeight = Double.parseDouble(edge.getLabels().first().getText());
      return edgeWeight > 0 ? edgeWeight : 0;
    }

    // calculate geometric edge length
    IEdgeStyle style = edge.getStyle();
    return style.getRenderer().getPathGeometry(edge, style).getPath().getLength();
  }

  /**
   * Resets the default style of all nodes and edges in the graph.
   * @param graph the graph to be reset.
   */
  public void resetGraph(IGraph graph) {
    graph.getNodes().forEach(node -> {
      if (graph.contains(node)) {
        // reset size
        SizeD size = graph.getNodeDefaults().getSize();
        // reset style
        graph.setStyle(node, graph.getNodeDefaults().getStyle());
        graph.setNodeLayout(node, new RectD(node.getLayout().getX(), node.getLayout().getY(), size.width, size.height));

        // remove labels
        ILabel[] labels = node.getLabels().toArray(ILabel.class);
        for (ILabel label : labels) {
          if ("centrality".equals(label.getTag())) {
            graph.remove(label);
          }
        }
      }
    });

    Arrow arrow = new Arrow(ArrowType.DEFAULT, Pen.getBlack(), Colors.BLACK);
    IEdgeStyle defaultEdgeStyle = graph.getEdgeDefaults().getStyle();
    // special treatment for the strongly connected components that are always
    // considered directed from the algorithm
    if (defaultEdgeStyle instanceof PolylineEdgeStyle) {
      ((PolylineEdgeStyle) defaultEdgeStyle).setTargetArrow(directed || isAlwaysDirected() ? arrow : IArrow.NONE);
    }

    graph.getEdges().forEach(edge -> {
      if (graph.contains(edge)) {
        graph.setStyle(edge, defaultEdgeStyle);

        // remove labels
        ILabel[] labels = edge.getLabels().toArray(ILabel.class);
        for (ILabel label : labels) {
          if ("centrality".equals(label.getTag())) {
            graph.remove(label);
          }
        }
      }
    });
  }

  /**
   * Returns whether or not the algorithm always assumes directed edges.
   */
  protected boolean isAlwaysDirected() {
    return false;
  }

  /**
   * Returns whether or not the algorithm supports directed edges.
   */
  public abstract boolean supportsDirectedEdges();

  /**
   * Returns whether or not the algorithm supports edges weights.
   */
  public abstract boolean supportsEdgeWeights();


  /**
   * Generates color ranges for nodes and edges.
   * @returns an array of colors
   */
  protected static Color[] newGradient( boolean lightToDark, int gradientSteps) {
    Color[] colors = new Color[gradientSteps];
    int maxIdx = gradientSteps - 1;
    float[] c1 = Colors.LIGHT_BLUE.getRGBComponents(null);
    float[] c2 = Color.BLUE.getRGBComponents(null);

    for (int i = 0; i < gradientSteps; i++) {
      float r = (c1[0] * (maxIdx - i) + c2[0] * i) / maxIdx;
      float g = (c1[1] * (maxIdx - i) + c2[1] * i) / maxIdx;
      float b = (c1[2] * (maxIdx - i) + c2[2] * i) / maxIdx;
      float a = (c1[3] * (maxIdx - i) + c2[3] * i) / maxIdx;
      colors[lightToDark ? i : gradientSteps - 1 - i] = new Color(r, g, b, a);
    }

    return colors;
  }

  /**
   * Determines the color of a node or an edge.
   * @param componentIdx the component index of the element
   * @return the element color
   */
  protected static Color getComponentColor(int componentIdx) {
    return ModelItemInfo.getComponentColor(componentIdx);
  }
}
