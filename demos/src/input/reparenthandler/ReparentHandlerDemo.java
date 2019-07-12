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
package input.reparenthandler;

import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.ShinyPlateNodeStyle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IInputModeContext;
import com.yworks.yfiles.view.input.ReparentNodeHandler;
import toolkit.AbstractDemo;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.Arrays;

/**
 * Customize the reparent gesture in a {@link com.yworks.yfiles.graph.IGraph} by
 * implementing a custom {@link com.yworks.yfiles.view.input.IReparentNodeHandler}.
 */
public class ReparentHandlerDemo extends AbstractDemo {

  /**
   * Initializes the demo by setting the input mode, creating a sample graph and registering the reparent node handler.
   */
  public void initialize() {
    IGraph graph = graphComponent.getGraph();

    // Enable the undo feature
    graph.setUndoEngineEnabled(true);

    // initialize the input mode
    initializeInputMode();

    // create a sample graph using different colored nodes to show different reparenting behaviors
    createSampleGraph(graph);
  }

  private void initializeInputMode() {
    GraphEditorInputMode mode = new GraphEditorInputMode();

    // disable element creation and deletion
    mode.setCreateEdgeAllowed(false);
    mode.setCreateNodeAllowed(false);
    mode.setDeletableItems(GraphItemTypes.NONE);
    mode.setGroupSelectionAllowed(false);

    // enable grouping operations such as grouping selected nodes moving nodes
    // into group nodes
    mode.setGroupingOperationsAllowed(true);

    // assign the custom reparent handler
    mode.setReparentNodeHandler(new DemoReparentNodeHandler());

    graphComponent.setInputMode(mode);
  }

  /**
   * Creates the sample graph with colored nodes and groups. The color indicates which node can be reparented to which
   * group node.
   */
  private void createSampleGraph(IGraph graph) {
    // Create some group nodes
    INode group1 = createGroupNode(graph, 100, 100, Colors.ROYAL_BLUE, "Only Blue Children");
    INode group2 = createGroupNode(graph, 160, 130, Colors.ROYAL_BLUE, "Only Blue Children");
    INode greenGroup = createGroupNode(graph, 100, 350, Colors.FOREST_GREEN, "Only Green Children");
    createGroupNode(graph, 400, 350, Colors.FOREST_GREEN, "Only Green Children");

    // And some regular nodes
    ShinyPlateNodeStyle blueStyle = new ShinyPlateNodeStyle();
    blueStyle.setPaint(Colors.ROYAL_BLUE);
    INode blueNode = graph.createNode(new RectD(110, 130, 30, 30), blueStyle, Colors.ROYAL_BLUE);
    ShinyPlateNodeStyle greenStyle = new ShinyPlateNodeStyle();
    greenStyle.setPaint(Colors.FOREST_GREEN);
    INode greenNode = graph.createNode(new RectD(130, 380, 30, 30), greenStyle, Colors.FOREST_GREEN);
    ShinyPlateNodeStyle redStyle = new ShinyPlateNodeStyle();
    redStyle.setPaint(Colors.FIREBRICK);
    graph.createNode(new RectD(400, 100, 30, 30), redStyle, Colors.FIREBRICK);
    graph.createNode(new RectD(500, 100, 30, 30), greenStyle, Colors.FOREST_GREEN);
    graph.createNode(new RectD(400, 200, 30, 30), blueStyle, Colors.ROYAL_BLUE);
    graph.createNode(new RectD(500, 200, 30, 30), redStyle, Colors.FIREBRICK);

    // Add some initial children to the groups
    graph.groupNodes(group1, Arrays.asList(blueNode, group2));
    graph.groupNodes(greenGroup, IEnumerable.create(greenNode));

    // Ensure that the outer blue group completely contains its inner group
    graph.setNodeLayout(group1, new RectD(100, 100, 200, 150));

    // Uncomment the following line to adjust the layout of the outer blue group automatically
//     graph.adjustGroupNodeLayout(group1);
  }

  /**
   * Creates a group node for the sample graph with a specific styling.
   */
  private static INode createGroupNode(IGraph graph, double x, double y, Color fillColor, String labelText) {
    PanelNodeStyle groupNodeStyle = new PanelNodeStyle();
    groupNodeStyle.setInsets(new InsetsD(25, 5, 5, 5));
    groupNodeStyle.setColor(fillColor);
    groupNodeStyle.setLabelInsetsColor(fillColor);
    INode groupNode = graph.createGroupNode(null, new RectD(x, y, 130, 100), groupNodeStyle, fillColor);

    // The label style and placement
    DefaultLabelStyle labelStyle = new DefaultLabelStyle();
    labelStyle.setTextPaint(Colors.WHITE);
    InteriorStretchLabelModel labelModel = new InteriorStretchLabelModel();
    labelModel.setInsets(new InsetsD(2, 5, 4, 5));
    ILabelModelParameter modelParameter = labelModel.createParameter(InteriorStretchLabelModel.Position.NORTH);
    graph.addLabel(groupNode, labelText, modelParameter, labelStyle);

    return groupNode;
  }

  /**
   * Builds the user interface and initializes the demo.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new ReparentHandlerDemo().start();
    });
  }

  /**
   * Customized variant of the default {@link com.yworks.yfiles.view.input.ReparentNodeHandler} that
   * determines the possible reparenting operations based on the node's tag.
   */
  private static class DemoReparentNodeHandler extends ReparentNodeHandler {
    /**
     * In general, this method determines whether the current gesture that
     * can be determined through the context is a reparent gesture. In this
     * case, it returns true if the base implementation returns true or if the
     * current node is green.
     * @param context the context that provides information about the user input
     * @param node the node that will possibly be reparented
     * @return whether this is a reparenting gesture
     */
    @Override
    public boolean isReparentGesture(IInputModeContext context, INode node) {
      return super.isReparentGesture(context, node) || Colors.FOREST_GREEN.equals(node.getTag());
    }

    /**
     * In general, this method determines whether the user may detach the
     * given node from its current parent in order to reparent it. In this case,
     * it returns false for red nodes.
     * @param context the context that provides information about the user input
     * @param node the node that is about to be detached from its current parent
     * @return whether the node may be detached and reparented
     */
    @Override
    public boolean shouldReparent(IInputModeContext context, INode node) {
      return !Colors.FIREBRICK.equals(node.getTag());
    }

    /**
     * In general, this method determines whether the provided node
     * may be reparented to the given <code>newParent</code>.
     * @param context the context that provides information about the user input
     * @param node the node that will be reparented
     * @param newParent the potential new parent
     * @return whether <code>newParent</code> is a valid new parent for <code>node</code>
     */
    @Override
    public boolean isValidParent(IInputModeContext context, INode node, INode newParent) {
      // Obtain the tag from the designated child
      Object nodeTag = node.getTag();
      // and from the designated parent.
      Object parentTag = newParent == null ? null : newParent.getTag();
      if (nodeTag == null) {
        // Nodes without a tag can be reparented freely
        return true;
      }
      // Otherwise allow nodes to be moved only if their tags are the same color
      if (nodeTag instanceof Color && parentTag instanceof Color) {
        return nodeTag.equals(parentTag);
      }
      // Finally, if there is no new parent, this is ok, too
      return newParent == null;
    }
  }
}
