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
package input.sizeconstraintprovider;

import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.view.ShapeVisual;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.NodeDecorator;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.INodeSizeConstraintProvider;
import com.yworks.yfiles.view.input.NodeSizeConstraintProvider;
import com.yworks.yfiles.view.Pen;
import toolkit.AbstractDemo;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

/**
 * Customize the resizing behavior of nodes by implementing a custom
 * {@link com.yworks.yfiles.view.input.INodeSizeConstraintProvider}.
 */
public class SizeConstraintProviderDemo extends AbstractDemo {

  /**
   * Registers a callback function as decorator that provides a custom {@link com.yworks.yfiles.view.input.INodeSizeConstraintProvider}
   * for each node.
   * <p>
   * This callback function is called whenever a node in the graph is queried
   * for its {@link INodeSizeConstraintProvider}. In this case, the 'node' parameter will be set
   * to that node.
   * </p>
   */
  public void registerSizeConstraintProvider(Rectangle2D boundaryRectangle) {
    // One shared instance that will be used by all blue nodes
    INodeSizeConstraintProvider blueSizeConstraintProvider = new BlueSizeConstraintProvider();

    NodeDecorator nodeDecorator = graphComponent.getGraph().getDecorator().getNodeDecorator();
    nodeDecorator.getSizeConstraintProviderDecorator().setFactory(
        node -> {
          // Obtain the tag from the node
          Object nodeTag = node.getTag();

          // Check if it is a known tag and choose the respective implementation.
          // Fallback to the default behavior otherwise.
          if (Colors.ROYAL_BLUE.equals(nodeTag)) {
            return blueSizeConstraintProvider;
          } else if (Colors.FOREST_GREEN.equals(nodeTag)) {
            return new GreenSizeConstraintProvider();
          } else if (Colors.DARK_ORANGE.equals(nodeTag)) {
            return new NodeSizeConstraintProvider(
                new SizeD(50, 50),
                new SizeD(300, 300),
                new RectD(boundaryRectangle.getX(), boundaryRectangle.getY(), boundaryRectangle.getWidth(),
                    boundaryRectangle.getHeight()));
          } else {
            return null;
          }
        });
  }

  /**
   * Initializes this demo by configuring the input mode and the model item lookup and creating an example graph together
   * with a boundary rectangle limiting the movement of some nodes.
   */
  public void initialize() {
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(new Color(153, 153, 153));
    graphComponent.getGraph().getNodeDefaults().setStyle(nodeStyle);

    // Initialize the input mode
    initializeInputMode();

    // Create the rectangle that limits the movement of some nodes
    Rectangle2D boundaryRectangle = new Rectangle2D.Double(210, 350, 30, 30);

    // and add a Visual to the GraphComponent to represent the rectangle using a black border and a transparent fill
    ShapeVisual visual = new ShapeVisual(boundaryRectangle, new Pen(Colors.BLACK, 2), Colors.TRANSPARENT);
    graphComponent.getRootGroup().addChild(visual, ICanvasObjectDescriptor.VISUAL);

    // Initialize the graph
    createSampleGraph(graphComponent.getGraph());

    // Enable Undo/Redo for all edits after the initial graph has been constructed
    graphComponent.getGraph().setUndoEngineEnabled(true);

    // Register custom provider implementations
    registerSizeConstraintProvider(boundaryRectangle);
  }

  private void initializeInputMode() {
    GraphEditorInputMode inputMode = new GraphEditorInputMode();
    // do not allow for moving any graph items
    inputMode.setMovableItems(GraphItemTypes.NONE);
    // do not allow for creating nodes and edges
    inputMode.setCreateNodeAllowed(false);
    inputMode.setCreateEdgeAllowed(false);
    // do not allow copy/paste
    inputMode.setClipboardOperationsAllowed(false);
    // do not allow to delete any element
    inputMode.setDeletableItems(GraphItemTypes.NONE);
    graphComponent.setInputMode(inputMode);
  }

  /**
   * Creates the sample graph of this demo.
   */
  private void createSampleGraph(IGraph graph) {
    createNode(graph, 100, 100, 100, 60, Colors.ROYAL_BLUE, Colors.WHITE_SMOKE, "Never Shrink\n(Max 3x)");
    createNode(graph, 300, 100, 160, 30, Colors.ROYAL_BLUE, Colors.WHITE_SMOKE, "Never Shrink (Max 3x)");
    createNode(graph, 100, 215, 100, 30, Colors.FOREST_GREEN, Colors.WHITE_SMOKE, "Enclose Label");
    createNode(graph, 300, 200, 140, 80, Colors.FOREST_GREEN, Colors.WHITE_SMOKE, "Enclose Label,\nEven Large Ones");
    createNode(graph, 200, 340, 140, 140, Colors.DARK_ORANGE, Colors.BLACK, "Encompass Rectangle,\nMin and Max Size");
  }

  /**
   * Creates a sample node for this demo.
   */
  private static void createNode(IGraph graph, double x, double y, double w, double h, Color fillColor, Color textColor, String labelText) {
    ShinyPlateNodeStyle nodeStyle = new ShinyPlateNodeStyle();
    nodeStyle.setPaint(fillColor);
    INode node = graph.createNode(new RectD(x, y, w, h), nodeStyle, fillColor);
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setFont(new Font("Dialog", Font.BOLD, 12));
    labelStyle.setTextPaint(textColor);
    labelStyle.setUsingFractionalFontMetricsEnabled(true);
    graph.addLabel(node, labelText, InteriorLabelModel.CENTER, labelStyle);
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new SizeConstraintProviderDemo().start();
    });
  }

  /**
   * An {@link INodeSizeConstraintProvider} that prevents shrinking of
   * nodes. Additionally, neither side of the node can become larger than
   * three times its initial size in each resizing operation.
   */
  private static class BlueSizeConstraintProvider implements INodeSizeConstraintProvider {
    /**
     * Returns the current node size to prevent the shrinking of nodes.
     */
    public SizeD getMinimumSize(INode item) {
      return item.getLayout().toSizeD();
    }

    /**
     * Returns three times the current node size.
     */
    public SizeD getMaximumSize(INode item) {
      return SizeD.times(3, item.getLayout().toSizeD());
    }

    /**
     * Returns an empty rectangle since this area is not constraint.
     */
    public RectD getMinimumEnclosedArea(INode item) {
      return RectD.EMPTY;
    }
  }

  /**
   * An {@link com.yworks.yfiles.view.input.INodeSizeConstraintProvider} that returns the size of the
   * biggest label as minimum size. The maximum size is not limited.
   */
  private static class GreenSizeConstraintProvider implements INodeSizeConstraintProvider {
    /**
     * Returns the label size to prevent the shrinking of nodes beyond their
     * label's size.
     */
    @Override
    public SizeD getMinimumSize(INode item) {
      // get the minimum sizes for all labels
      return item.getLabels().stream().map(label -> getLabelSize(item, label))
          // and take the maximum width and height
          .reduce(SizeD::max)
          // or if there is no label, take a minimum size
          .orElse(new SizeD(1, 1));
    }

    private SizeD getLabelSize(INode item, ILabel label) {
      INodeSizeConstraintProvider labelProvider = label.lookup(INodeSizeConstraintProvider.class);
      if (labelProvider != null) {
        return labelProvider.getMinimumSize(item);
      }

      if (label.getLayoutParameter().getModel() instanceof InteriorLabelModel) {
        return label.getPreferredSize();
      }
      return new SizeD(1, 1);
    }

    /**
     * Returns the infinite size since the maximum size is not limited.
     */
    public SizeD getMaximumSize(INode item) {
      return SizeD.INFINITE;
    }

    /**
     * Returns an empty rectangle since this area is not constraint.
     */
    @Override
    public RectD getMinimumEnclosedArea(INode item) {
      return RectD.EMPTY;
    }
  }
}
