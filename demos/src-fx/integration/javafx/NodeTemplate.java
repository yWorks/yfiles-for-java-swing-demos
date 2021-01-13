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
package integration.javafx;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.DefaultGraph;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.PixelImageExporter;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * An enum for node templates to populate the palette with and which may be dragged into the GraphComponent.
 */
public enum NodeTemplate {
  RECTANGLE("Rectangle") {
    INode createNode() {
      ShapeNodeStyle style = new ShapeNodeStyle();
      style.setShape(ShapeNodeShape.RECTANGLE);
      style.setPen(Pen.getBlack());
      style.setPaint(Color.ORANGE);
      return createNode(style);
    }
  },
  ROUNDED_RECTANGLE("Rounded Rectangle") {
    INode createNode() {
      ShapeNodeStyle style = new ShapeNodeStyle();
      style.setShape(ShapeNodeShape.ROUND_RECTANGLE);
      style.setPen(Pen.getBlack());
      style.setPaint(Color.ORANGE);
      return createNode(style);
    }
  },
  STAR("Star") {
    INode createNode() {
      ShapeNodeStyle style = new ShapeNodeStyle();
      style.setShape(ShapeNodeShape.STAR5);
      style.setPen(Pen.getBlack());
      style.setPaint(Color.ORANGE);
      return createNode(style);
    }
  },
  SHINY_PLATE("Shiny Plate") {
    INode createNode() {
      ShinyPlateNodeStyle style = new ShinyPlateNodeStyle();
      style.setPaint(Color.ORANGE);
      return createNode(style);
    }
  },
  PANEL("Group") {
    INode createNode() {
      PanelNodeStyle style = new PanelNodeStyle();
      style.setColor(Colors.LIGHT_BLUE);
      style.setInsets(new InsetsD(25, 5, 5, 5));
      INode node = createNode(style, new RectD(0, 0, 70, 70));
      getGraph().addLabel(node, "Group Node", InteriorStretchLabelModel.NORTH, new DefaultLabelStyle());
      return node;
    }
  };

  // name of the template
  private final String description;
  // node that applies the template
  private final INode node;

  // the graph in which the template nodes live
  private static IGraph graph;

  NodeTemplate(String description) {
    this.description = description;
    this.node = createNode();
  }

  /**
   * Returns the graph in which the template nodes live.
   */
  static IGraph getGraph() {
    if (graph == null) {
      graph = new DefaultGraph();
    }
    return graph;
  }

  /**
   * Returns a node that applies the template.
   */
  public INode node() {
    return node;
  }

  /**
   * Returns a description of the template.
   */
  public String description() {
    return description;
  }

  /**
   * Creates a node that applies the template.
   */
  abstract INode createNode();

  /**
   * Creates a node with the given style and a size of 30x30.
   */
  INode createNode(INodeStyle style) {
    return createNode(style, new RectD(0, 0, 30, 30));
  }

  /**
   * Creates a node with the given style and the given bounds.
   */
  INode createNode(INodeStyle style, RectD bounds) {
    return getGraph().createNode(bounds, style);
  }

  /**
   * Creates an image showing a node applying the template.
   */
  public BufferedImage createImage() {
    GraphComponent graphComponent = new GraphComponent();

    // create a copy of the node
    INode newNode = graphComponent.getGraph().createNode(node.getLayout().toRectD(), node.getStyle(), node.getTag());

    // copy labels as well
    for (ILabel label : node.getLabels()) {
      graphComponent.getGraph().addLabel(newNode, label.getText(), label.getLayoutParameter(), label.getStyle(), label.getPreferredSize(), label.getTag());
    }

    graphComponent.updateContentRect();

    // render the graph component in an image
    PixelImageExporter pixelImageExporter = new PixelImageExporter(graphComponent.getContentRect().getEnlarged(2));
    pixelImageExporter.setTransparencyEnabled(true);
    return pixelImageExporter.exportToBitmap(graphComponent);
  }
}
