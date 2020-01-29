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
package layout.familytree;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.genealogy.FamilyTreeLayout;
import com.yworks.yfiles.layout.genealogy.FamilyTreeLayoutData;
import com.yworks.yfiles.layout.genealogy.FamilyType;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import toolkit.AbstractDemo;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;
import java.time.Duration;
import java.util.Locale;

/**
 * Shows how to use the {@link FamilyTreeLayout} algorithm.
 */
public class FamilyTreeDemo extends AbstractDemo {
  private ShapeNodeStyle maleStyle;
  private ShapeNodeStyle femaleStyle;
  private ShapeNodeStyle familyKnot;
  private DefaultLabelStyle namesStyle;
  private DefaultLabelStyle dateStyle;

  /**
   * Initializes the demo.
   */
  @Override
  public void initialize() {
    // configure interaction
    graphComponent.setInputMode(new GraphViewerInputMode());

    // initialize the default edge style.
    initializeEdgeStyle();

    // initializes the different styles for the nodes.
    initializeStyles();

    // load a sample graph from the graphml file and applies appropriate the styles
    // depending on node type (male, female, family node).
    createSampleGraph();

    // applies the FamilyTree layout.
    runLayout();
  }

  /**
   * Sets the default edge style.
   */
  private void initializeEdgeStyle() {
    PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
    edgeStyle.setPen(new Pen(new Color(170, 170, 170)));
    edgeStyle.setTargetArrow(IArrow.NONE);
    graphComponent.getGraph().getEdgeDefaults().setStyle(edgeStyle);
  }

  /**
   * Initializes the styles for the family tree nodes.
   * Males will be visualized in blue color, females in pink and family nodes
   * that connect partners with each other and their children will be circular
   * with gray color.
   */
  private void initializeStyles() {
    maleStyle = new ShapeNodeStyle();
    maleStyle.setPaint(new Color(176, 224, 230));
    maleStyle.setPen(new Pen(Colors.WHITE));
    maleStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);

    femaleStyle = new ShapeNodeStyle();
    femaleStyle.setPaint(new Color(255, 182, 193));
    femaleStyle.setPen(new Pen(Colors.WHITE));
    femaleStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);

    familyKnot = new ShapeNodeStyle();
    familyKnot.setPaint(new Color(170, 170, 170));
    familyKnot.setPaint(Colors.WHITE);
    familyKnot.setShape(ShapeNodeShape.ELLIPSE);

    namesStyle = new DefaultLabelStyle();
    namesStyle.setFont(new Font("Dialog", Font.PLAIN, 14));
    namesStyle.setTextPaint(Colors.BLACK);
    namesStyle.setTextAlignment(TextAlignment.CENTER);
    namesStyle.setInsets(new InsetsD(-10, 0, 0, 0));

    dateStyle = new DefaultLabelStyle();
    dateStyle.setFont(new Font("Dialog", Font.PLAIN, 11));
    dateStyle.setTextPaint(new Color(119,136,153));
    dateStyle.setInsets(new InsetsD(5, 5, 5, 5));
  }

  /**
   * Applies the pre-configured demo styles to nodes and labels.
   */
  private void applyStyles() {
    IGraph graph = graphComponent.getGraph();
    for (INode node : graph.getNodes()) {
      String familyType = (String) node.getTag();
      if (familyType == null) {
        continue;
      }

      // set node styles depending on the familytype
      switch (familyType) {
        case "MALE":
          graph.setStyle(node, maleStyle);
          break;
        case "FEMALE":
          graph.setStyle(node, femaleStyle);
          break;
        case "FAMILY":
          graph.setStyle(node, familyKnot);
          break;
      }

      // set appropriate label layout parameter
      IListEnumerable<ILabel> labels = node.getLabels();
      for (ILabel label : labels) {
        String labelText = label.getText();
        if (labelText.contains("*")) {
          graph.setStyle(label, dateStyle);
          graph.setLabelLayoutParameter(label, InteriorLabelModel.SOUTH_WEST);
        } else if (labelText.contains("✝")) {
          graph.setStyle(label, dateStyle);
          graph.setLabelLayoutParameter(label, InteriorLabelModel.SOUTH_EAST);
        } else {
          graph.setStyle(label, namesStyle);
          graph.setLabelLayoutParameter(label, InteriorLabelModel.CENTER);
        }
      }
    }
  }

  /**
   * Loads a sample graph from a GraphML file.
   */
  private void createSampleGraph() {
    try {
      graphComponent.importFromGraphML(getClass().getResource("resources/kennedy-family.graphml"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    applyStyles();
  }

  /**
   * Applies the family tree layout using the nodes' types stored in the tags.
   * Family tree layout considers both, the graph structure and the node type
   * when calculating positions for nodes.
   */
  private void runLayout() {
    FamilyTreeLayoutData familyTreeLayoutData = new FamilyTreeLayoutData();
    familyTreeLayoutData.setFamilyTypes(node -> {
      String tag = (String) node.getTag();
      if ("MALE".equals(tag)) {
        return FamilyType.MALE;
      } else if ("FEMALE".equals(tag)) {
        return FamilyType.FEMALE;
      } else {
        return FamilyType.FAMILY;
      }
    });

    graphComponent.morphLayout(new FamilyTreeLayout(), Duration.ofSeconds(1), familyTreeLayoutData);
  }

  public static void main( final String[] args ) {
    EventQueue.invokeLater(() -> {
      Locale.setDefault(Locale.ENGLISH);
      initLnF();
      new FamilyTreeDemo().start();
    });
  }
}
