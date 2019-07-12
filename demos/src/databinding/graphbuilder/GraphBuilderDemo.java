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
package databinding.graphbuilder;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphBuilder;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdgeDefaults;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelDefaults;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.NodeEventArgs;
import com.yworks.yfiles.graph.TreeBuilder;
import com.yworks.yfiles.graph.labelmodels.EdgeSegmentLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.utils.IEventListener;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.IInputMode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import toolkit.AbstractDemo;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 * Shows how to use the @link GraphBuilder} for quickly populating a graph from
 * a data source. The GraphBuilder constructs the example graphs from XML data. 
 */
public class GraphBuilderDemo extends AbstractDemo {
  public static void main( String[] args ) {
    EventQueue.invokeLater(() -> {
      initLnF();
      new GraphBuilderDemo().start();
    });
  }

  /**
   * Adds a control for choosing sample diagrams to the demo tool bar. 
   */
  @Override
  protected void configureToolBar( JToolBar toolBar ) {
    super.configureToolBar(toolBar);

    JComboBox<String> exampleComboBox = new JComboBox<>(new String[]{"Organization", "Classes"});
    exampleComboBox.addActionListener(e -> {
      switch (((JComboBox<String>) e.getSource()).getSelectedIndex()) {
        case 0:
          createOrganizationGraph();
          break;
        case 1:
          createClassesGraph();
          break;
      }
    });
    exampleComboBox.setMaximumSize(exampleComboBox.getPreferredSize());

    toolBar.addSeparator();
    toolBar.add(new JLabel("Example Data:"));
    toolBar.add(exampleComboBox);
  }

  /**
   * Creates an initial example diagram when the demo application becomes
   * visible for the first time.
   */
  @Override
  public void onVisible() {
    createOrganizationGraph();
  }

  @Override
  public void initialize() {
    // Initialize input mode
    graphComponent.setInputMode(createInputMode());

    initializeGraphDefaults();
  }

  /**
   * Creates a viewer input mode.
   */
  private static IInputMode createInputMode() {
    GraphViewerInputMode gvim = new GraphViewerInputMode();
    gvim.setSelectableItems(GraphItemTypes.NONE);
    gvim.setFocusableItems(GraphItemTypes.NONE);
    return gvim;
  }

  /**
   * Sets default styles for the graph.
   */
  void initializeGraphDefaults() {
    IGraph graph = graphComponent.getGraph();

    // configure default shape and colors for nodes
    ShapeNodeStyle nodeStyle = new ShapeNodeStyle();
    nodeStyle.setShape(ShapeNodeShape.ROUND_RECTANGLE);
    nodeStyle.setPaint(new Color(255, 237, 204));
    nodeStyle.setPen(Pen.getDarkOrange());
    INodeDefaults nodeDefaults = graph.getNodeDefaults();
    nodeDefaults.setStyle(nodeStyle);
    // configure default font for node labels
    DefaultLabelStyle nodeLabelStyle = new DefaultLabelStyle();
    nodeLabelStyle.setFont(new Font("Dialog", Font.PLAIN, 13));
    ILabelDefaults nodeLabelDefaults = nodeDefaults.getLabelDefaults();
    nodeLabelDefaults.setStyle(nodeLabelStyle);
    // configure default placement for node labels
    // i.e. node labels should be placed in the center of their associated nodes
    nodeLabelDefaults.setLayoutParameter(InteriorLabelModel.CENTER);

    // configure default shape and colors for group nodes
    PanelNodeStyle groupStyle = new PanelNodeStyle();
    groupStyle.setColor(new Color(225, 242, 253, 127));
    groupStyle.setLabelInsetsColor(Colors.ANTIQUE_WHITE);
    groupStyle.setInsets(new InsetsD(20, 5, 5, 5));
    INodeDefaults groupNodeDefaults = graph.getGroupNodeDefaults();
    groupNodeDefaults.setStyle(groupStyle);
    // configure default font and text color for group node labels
    DefaultLabelStyle groupLabelStyle = new DefaultLabelStyle();
    groupLabelStyle.setFont(new Font("Dialog", Font.BOLD, 32));
    groupLabelStyle.setTextPaint(Color.GRAY);
    ILabelDefaults groupLabelDefaults = groupNodeDefaults.getLabelDefaults();
    groupLabelDefaults.setStyle(groupLabelStyle);
    // configure default placement for group node labels
    // i.e. group node labels should be placed inside and close to the upper
    // left corner of their associated group node
    groupLabelDefaults.setLayoutParameter(FreeNodeLabelModel.INSTANCE.createParameter(
            PointD.ORIGIN, new PointD(5, 5), PointD.ORIGIN, PointD.ORIGIN, 0));

    // configure default visualization of edges
    // the edge style depends on the sample graph and will be specified in the
    // corresponding factory methods
    IEdgeDefaults edgeDefaults = graph.getEdgeDefaults();
    // configure default background and border color for edge labels
    DefaultLabelStyle edgeLabelStyle = new DefaultLabelStyle();
    edgeLabelStyle.setBackgroundPaint(new Color(225, 242, 253));
    edgeLabelStyle.setBackgroundPen(Pen.getLightSkyBlue());
    ILabelDefaults edgeLabelDefaults = edgeDefaults.getLabelDefaults();
    edgeLabelDefaults.setStyle(edgeLabelStyle);
    // configure default placement for edge labels
    // i.e. edge labels should be placed on the path of their associated edge
    // and their text baseline should be parallel to the x-axis
    EdgeSegmentLabelModel edgeLabelModel = new EdgeSegmentLabelModel();
    edgeLabelModel.setAutoRotationEnabled(false);
    edgeLabelModel.setSideOfEdge(EdgeSides.ON_EDGE);
    edgeLabelDefaults.setLayoutParameter(edgeLabelModel.createDefaultParameter());
  }


  /**
   * Creates a simple organizational chart.
   */
  void createOrganizationGraph() {
    Document document = parse("resources/organizationmodel.xml");

    if (document != null) {
      PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
      edgeStyle.setSmoothingLength(20);
      IGraph graph = graphComponent.getGraph();
      graph.getEdgeDefaults().setStyle(edgeStyle);


      Element data = document.getDocumentElement();

      HashMap<String, Element> businessUnits = new HashMap<>();
      for (Element unit : XmlUtils.getDescendantsByTagName(data, "businessunit")) {
        businessUnits.put(unit.getAttribute("name"), unit);
      }

      TreeBuilder<Element, Element> builder = new TreeBuilder<>(graph);
      // Nodes, edges, and groups are obtained from XML elements in the model
      builder.setNodesSource(XmlUtils.getDescendantsByTagName(data, "employee"));
      builder.setGroupsSource(XmlUtils.getDescendantsByTagName(data, "businessunit"));
      // Mapping nodes to groups is done via an attribute on the employee.
      builder.setGroupProvider(element -> businessUnits.get(element.getAttribute("businessUnit")));
      // Group nesting is determined by nesting of the XML elements
      builder.setParentGroupProvider(element -> {
        Node node = element.getParentNode();
        return node != null && node.getNodeType() == Node.ELEMENT_NODE ? node : null;
      });
      // As is the hierarchy of employees
      builder.setChildProvider(element -> XmlUtils.getChildrenByTagName(element, "employee"));
      // Add descriptive labels to edges and groups. Nodes get two labels instead, which is handled in an event below.
      builder.setEdgeLabelProvider((source, target) -> target.getAttribute("name"));
      builder.setGroupLabelProvider(element -> element.getAttribute("name"));


      // A label model with some space to the node's border
      InteriorLabelModel nodeLabelModel = new InteriorLabelModel();
      nodeLabelModel.setInsets(new InsetsD(5));

      builder.addNodeCreatedListener((source, args) -> {
        IGraph _graph = args.getGraph();
        INode employeeNode = args.getItem();
        Element employeeElement = args.getSourceObject();

        // Add employee name and position as labels
        ILabel l1 = _graph.addLabel(
                employeeNode,
                employeeElement.getAttribute("name"),
                nodeLabelModel.createParameter(InteriorLabelModel.Position.NORTH_WEST));
        ILabel l2 = _graph.addLabel(
                employeeNode,
                employeeElement.getAttribute("position"),
                nodeLabelModel.createParameter(InteriorLabelModel.Position.SOUTH_WEST));

        // Determine optimal node size
        SizeD size1 = l1.getPreferredSize();
        SizeD size2 = l2.getPreferredSize();
        SizeD bestSize = new SizeD(
                Math.max(size1.getWidth(), size2.getWidth()) + 10,
                size1.getHeight() + size2.getHeight() + 12);
        // Set node to that size. Location is irrelevant here, since we're running a layout anyway
        _graph.setNodeLayout(employeeNode, new RectD(PointD.ORIGIN, bestSize));
      });

      builder.buildGraph();

      HierarchicLayout algorithm = new HierarchicLayout();
      algorithm.setIntegratedEdgeLabelingEnabled(true);
      graphComponent.morphLayout(algorithm, Duration.ofMillis(1000));
    }
  }

  /**
   * Creates a simple class hierarchy diagram.
   */
  void createClassesGraph() {
    Document document = parse("resources/classesmodel.xml");

    if (document != null) {
      IGraph graph = graphComponent.getGraph();
      // use edges with arrowhead
      PolylineEdgeStyle edgeStyle = new PolylineEdgeStyle();
      edgeStyle.setSmoothingLength(20);
      edgeStyle.setTargetArrow(IArrow.SHORT);
      graph.getEdgeDefaults().setStyle(edgeStyle);


      Element data = document.getDocumentElement();

      HashMap<String, Element> classes = new HashMap<>();
      for (Element _class : XmlUtils.getDescendantsByTagName(data, "class")) {
        classes.put(_class.getAttribute("name"), _class);
      }

      GraphBuilder<Element, Element, Element> builder = new GraphBuilder<>(graph);
      // Nodes and edges are obtained from XML elements in the model
      builder.setNodesSource(XmlUtils.getDescendantsByTagName(data, "class"));
      builder.setEdgesSource(XmlUtils.getDescendantsByTagName(data, "class"));
      // Nodes are grouped by their parent
      builder.setGroupProvider(element -> {
        Node node = element.getParentNode();
        return node != null &&
               node.getNodeType() == Node.ELEMENT_NODE &&
               "class".equals(node.getNodeName())
               ? node : null;
      });
      // edges are drawn for classes with an "extends" attribute
      // between the class itself
      builder.setSourceNodeProvider(element -> element);
      // and the class which is provided by the "extends" attribute
      builder.setTargetNodeProvider(element -> classes.get(element.getAttribute("extends")));
      // the node label is either ClassName, interface ClassName, or ClassName extends OtherClass
      builder.setNodeLabelProvider(element -> {
        String name = element.getAttribute("name");
        String superType = element.getAttribute("extends");
        boolean isInterface = "interface".equals(element.getAttribute("type"));
        return (isInterface ? "interface" : "") + name + (superType.isEmpty() ? "" : " extends " + superType);
      });
      // edge label "Source extends Target"
      builder.setEdgeLabelProvider(element -> {
        Element superType = classes.get(element.getAttribute("extends"));
        return element.getAttribute("name") + " extends " + superType.getAttribute("name");
      });

      builder.addNodeCreatedListener((source, args) -> {
        INode node = args.getItem();
        ILabel l1 = node.getLabels().first();
        // Determine optimal node size
        IRectangle nl = node.getLayout();
        SizeD size1 = l1.getPreferredSize();
        SizeD bestSize = new SizeD(
                Math.max(nl.getWidth(), size1.getWidth() + 10),
                Math.max(nl.getHeight(), size1.getHeight() + 12));
        // Set node to that size. Location is irrelevant here, since we're running a layout anyway
        args.getGraph().setNodeLayout(node, new RectD(PointD.ORIGIN, bestSize));
      });


      // use different label models for the groups
      IEventListener<NodeEventArgs> setLabelParameterForGroups = this::setLabelParameterForGroups;
      graph.addIsGroupNodeChangedListener(setLabelParameterForGroups);

      builder.buildGraph();

      graph.removeIsGroupNodeChangedListener(setLabelParameterForGroups);


      HierarchicLayout algorithm = new HierarchicLayout();
      algorithm.setLayoutOrientation(LayoutOrientation.BOTTOM_TO_TOP);
      algorithm.setIntegratedEdgeLabelingEnabled(true);
      algorithm.setMinimumLayerDistance(30);
      algorithm.setNodeLabelConsiderationEnabled(false);
      graphComponent.morphLayout(algorithm, Duration.ofMillis(1000));
    }
  }

  private void setLabelParameterForGroups( Object source, NodeEventArgs args ) {
    ILabel label = args.getItem().getLabels().first();
    ((IGraph)source).setLabelLayoutParameter(label, InteriorLabelModel.NORTH_WEST);
  }


  /**
   * Parses the content of the resource at the given relative resource path as
   * an XML document.
   */
  private Document parse( String path ) {
    URL resource = getClass().getResource(path);
    if (resource == null) {
      return null; 
    } else {
      return XmlUtils.parse(resource);
    }
  }
}
