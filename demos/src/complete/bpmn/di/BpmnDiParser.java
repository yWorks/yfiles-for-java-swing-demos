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
package complete.bpmn.di;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.OrientedRectangle;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.DefaultFoldingEdgeConverter;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.IColumn;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.IRow;
import com.yworks.yfiles.graph.IStripe;
import com.yworks.yfiles.graph.ITable;
import com.yworks.yfiles.graph.ITagOwner;
import com.yworks.yfiles.graph.labelmodels.EdgePathLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSegmentLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.labelmodels.ExteriorLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeEdgeLabelModel;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.labelmodels.GenericLabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.labelmodels.InsideOutsidePortLabelModel;
import com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.layout.LabelPlacements;
import com.yworks.yfiles.layout.PreferredPlacementDescriptor;
import com.yworks.yfiles.layout.labeling.GenericLabeling;
import com.yworks.yfiles.layout.labeling.LabelingData;
import com.yworks.yfiles.layout.labeling.OptimizationStrategy;
import com.yworks.yfiles.view.TextAlignment;
import com.yworks.yfiles.view.TextWrapping;
import com.yworks.yfiles.view.VerticalAlignment;
import complete.bpmn.view.ActivityNodeStyle;
import complete.bpmn.view.ActivityType;
import complete.bpmn.view.AnnotationNodeStyle;
import complete.bpmn.view.BpmnEdgeStyle;
import complete.bpmn.view.ChoreographyLabelModel;
import complete.bpmn.view.ChoreographyNodeStyle;
import complete.bpmn.view.ConversationNodeStyle;
import complete.bpmn.view.ConversationType;
import complete.bpmn.view.DataObjectNodeStyle;
import complete.bpmn.view.DataObjectType;
import complete.bpmn.view.DataStoreNodeStyle;
import complete.bpmn.view.EdgeType;
import complete.bpmn.view.EventCharacteristic;
import complete.bpmn.view.EventNodeStyle;
import complete.bpmn.view.EventPortStyle;
import complete.bpmn.view.EventType;
import complete.bpmn.view.GatewayNodeStyle;
import complete.bpmn.view.GatewayType;
import complete.bpmn.view.GroupNodeStyle;
import complete.bpmn.view.MessageLabelStyle;
import complete.bpmn.view.Participant;
import complete.bpmn.view.PoolNodeStyle;
import complete.bpmn.view.SubState;
import complete.bpmn.view.TaskType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Parser for the BPMN 2.0 abstract syntax.
 */
public class BpmnDiParser {
  // The current parsed BpmnDocument
  private BpmnDocument document;

  private BpmnDocument getDocument() {
    return this.document;
  }

  private void setDocument( BpmnDocument value ) {
    this.document = value;
  }

  // The currently used diagram
  private BpmnDiagram currentDiagram;

  private BpmnDiagram getCurrentDiagram() {
    return this.currentDiagram;
  }

  private void setCurrentDiagram( BpmnDiagram value ) {
    this.currentDiagram = value;
  }

  // maps a process BpmnElement to the BpmnElement that referenced this process in a 'processRef'
  private Map<BpmnElement, BpmnElement> processRefSource;

  private Map<BpmnElement, BpmnElement> getProcessRefSource() {
    return this.processRefSource;
  }

  private void setProcessRefSource( Map<BpmnElement, BpmnElement> value ) {
    this.processRefSource = value;
  }

  // The master graph
  private IGraph getMasterGraph() {
    return view.getManager().getMasterGraph();
  }

  // The current folding manager and current folding view
  private FoldingManager getManager() {
    return view.getManager();
  }

  private IFoldingView view;

  private GenericLabelModel genericLabelModel;

  /**
   * LabelModel for nodes with exterior label. Provides 32 possible positions.
   * @return The GenericLabelModel.
   */
  private GenericLabelModel getGenericLabelModel() {
    return this.genericLabelModel;
  }

  /**
   * LabelModel for nodes with exterior label. Provides 32 possible positions.
   * @param value The GenericLabelModel to set.
   */
  private void setGenericLabelModel( GenericLabelModel value ) {
    this.genericLabelModel = value;
  }


  /**
   * Flag that sets the rearrangement of labels. Does not work properly with custom label bounds.
   */
  private static final boolean REARRANGE_LABELS = false;

  /**
   * Flag that decides if labels should be parsed, if bpmndi:BPMNLabel XML element is missing.
   */
  private static final boolean PARSE_ALL_LABELS = true;

  /**
   * Flag that decides if the folded Diagrams inside a selected diagram should also be parsed.
   */
  private static final boolean PARSE_FOLDED_DIAGRAMS = true;

  /**
   * Flag that decides if only top level diagrams can be selected, or all possible Diagrams in the file.
   */
  private static final boolean SHOW_ALL_DIAGRAMS = false;

  /**
   * Flag, if false, no edges are parsed (Debug).
   */
  private static final boolean PARSE_EDGES = true;

  /**
   * Flag to determine, if external node labels should be single- or multiline implementation left unfinished, since the
   * right way to do would be overriding the renderer.
   */
  private static final boolean MULTI_LINE_EXTERIOR_NODE_LABELS = false;


  // Can't use BPMN-Constants here, so we have to add the standard size of message envelopes.
  private final SizeD bpmnMessageSize = new SizeD(20, 14);


  /**
   * Constructs a new instance of the parser.
   */
  public BpmnDiParser() {
    // Initialize the GenericLabelModel
    initGenericLabelModel();
    setProcessRefSource(new HashMap<>());
  }

  /**
   * Parses a BPMN File and build a graph.
   * @param graph The graph to build the diagram in.
   * @param resource The resource to read the file from.
   */
  public final void load( IGraph graph, URL resource ) throws IOException {
    try (InputStream stream = resource.openStream()) {
      load(graph, stream);
    }
  }

  /**
   * Parses a BPMN File and build a graph.
   * @param graph The graph to build the diagram in.
   * @param filePath The path to read the file from.
   */
  public final void load( IGraph graph, String filePath ) throws IOException {
    load(graph, filePath, null);
  }

  /**
   * Parses a BPMN File and build a graph.
   * @param graph The graph to build the diagram in.
   * @param filePath The path to read the file from.
   * @param selectDiagramCallback Callback method which chooses one diagram name from a given list. If no method is provided the first diagram is chosen.
   */
  public final void load( IGraph graph, String filePath, Function<Collection<BpmnDiagram>, BpmnDiagram> selectDiagramCallback ) throws IOException {
    try (InputStream stream = new FileInputStream(filePath)) {
      load(graph, stream);
    }
  }

  /**
   * Parses a BPMN File and build a graph.
   * @param graph The graph to build the diagram in.
   * @param stream The stream to load the graph from.
   */
  public final void load( IGraph graph, InputStream stream ) throws IOException {
    load(graph, stream, null);
  }

  /**
   * Parses a BPMN File and build a graph.
   * @param graph The graph Instance build the diagram in.
   * @param stream The stream to load the graph from.
   * @param selectDiagramCallback Callback method which chooses one diagram name from a given list. If no method is provided the first diagram is chosen.
   */
  public final void load( IGraph graph, InputStream stream, Function<Collection<BpmnDiagram>, BpmnDiagram> selectDiagramCallback ) throws IOException {
    // Initialize FoldingManager & View for the Graph
    view = graph.getFoldingView();
    if (view == null) {
      throw new IllegalArgumentException("Folding must be enabled.");
    }
    MultiLabelFolderNodeConverter multiLabelFolderNodeConverter = new MultiLabelFolderNodeConverter();
    multiLabelFolderNodeConverter.setCopyingFirstLabelEnabled(true);
    multiLabelFolderNodeConverter.setCopyLabels(true);
    multiLabelFolderNodeConverter.setNodeStyleCloningEnabled(true);
    multiLabelFolderNodeConverter.setLabelLayoutParameterCloningEnabled(true);
    multiLabelFolderNodeConverter.setLabelStyle(BpmnLabelStyle.newDefaultInstance());
    // Initialize the default Layout for folded Group Nodes
    getManager().setFolderNodeConverter(multiLabelFolderNodeConverter);
    DefaultFoldingEdgeConverter defaultFoldingEdgeConverter = new DefaultFoldingEdgeConverter();
    defaultFoldingEdgeConverter.setEdgeStyleCloningEnabled(true);
    defaultFoldingEdgeConverter.setCopyingFirstLabelEnabled(true);
    defaultFoldingEdgeConverter.setLabelStyleCloningEnabled(true);
    defaultFoldingEdgeConverter.setLabelLayoutParameterCloningEnabled(true);
    defaultFoldingEdgeConverter.setReusingMasterPortsEnabled(true);
    defaultFoldingEdgeConverter.setReusingFolderNodePortsEnabled(true);
    defaultFoldingEdgeConverter.setBendsResettingEnabled(false);
    // Initialize the Layout for Edges alongside folded Group Nodes
    getManager().setFoldingEdgeConverter(defaultFoldingEdgeConverter);

    // Clear previous Graph
    getMasterGraph().clear();

    // Create BpmnDocument from XML Stream
    Document doc = read(stream);
    setDocument(new BpmnDocument(doc));

    List<BpmnDiagram> topLevelDiagrams = SHOW_ALL_DIAGRAMS ? getDocument().getDiagrams() : getDocument().getTopLevelDiagrams();

    // Get the Diagram to load
    BpmnDiagram diaToLoad;
    selectDiagramCallback = selectDiagramCallback != null ? selectDiagramCallback : this::defaultSelectionCallback;
    diaToLoad = selectDiagramCallback.apply(topLevelDiagrams);

    // Loads the selected Diagram into the supplied Graph
    if (diaToLoad != null) {
      loadDiagram(diaToLoad, null);
    }
  }

  /**
   * Initializes the genericLabelModel using a model with 32 positions (better than ExteriorLabelModel which only has 8) to
   * enable more options for customization in the user interface.
   */
  private void initGenericLabelModel() {
    ExteriorLabelModel exteriorLabelModel = new ExteriorLabelModel();
    exteriorLabelModel.setInsets(InsetsD.fromLTRB(3, 3, 3, 3));
    setGenericLabelModel(new GenericLabelModel(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.SOUTH)));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.SOUTH_EAST));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.SOUTH_WEST));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.NORTH));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.NORTH_EAST));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.NORTH_WEST));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.WEST));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.EAST));
    // Big Insets
    exteriorLabelModel = new ExteriorLabelModel();
    exteriorLabelModel.setInsets(InsetsD.fromLTRB(18, 18, 18, 18));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.SOUTH));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.SOUTH_EAST));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.SOUTH_WEST));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.NORTH));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.NORTH_EAST));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.NORTH_WEST));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.WEST));
    getGenericLabelModel().addParameter(exteriorLabelModel.createParameter(ExteriorLabelModel.Position.EAST));

    // Label Positions between existing exterior positions
    FreeNodeLabelModel freeNodeLabelModel = new FreeNodeLabelModel();
    // Small Insets
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0.25, 0), new PointD(-1.5, -3), new PointD(0.75, 1)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0.75, 0), new PointD(1.5, -3), new PointD(0.25, 1)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(1, 0.25), new PointD(3, -1.5), new PointD(0, 0.75)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(1, 0.75), new PointD(3, 1.5), new PointD(0, 0.25)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0.75, 1), new PointD(1.5, 3), new PointD(0.25, 0)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0.25, 1), new PointD(-1.5, 3), new PointD(0.75, 0)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0, 0.75), new PointD(-3, 1.5), new PointD(1, 0.25)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0, 0.25), new PointD(-3, -1.5), new PointD(1, 0.75)));
    // Big Insets
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0.25, 0), new PointD(-9, -18), new PointD(0.75, 1)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0.75, 0), new PointD(9, -18), new PointD(0.25, 1)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(1, 0.25), new PointD(18, -9), new PointD(0, 0.75)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(1, 0.75), new PointD(18, 9), new PointD(0, 0.25)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0.75, 1), new PointD(9, 18), new PointD(0.25, 0)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0.25, 1), new PointD(-9, 18), new PointD(0.75, 0)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0, 0.75), new PointD(-18, 9), new PointD(1, 0.25)));
    getGenericLabelModel().addParameter(freeNodeLabelModel.createParameter(new PointD(0, 0.25), new PointD(-18, -9), new PointD(1, 0.75)));
  }



  /**
   * Builds the first diagram via drawing the individual nodes and edges.
   * @param diagram The diagram to draw
   * @param localRoot The local root node
   */
  private void loadDiagram( BpmnDiagram diagram, INode localRoot ) {
    setCurrentDiagram(diagram);

    // iterate the BpmnElements of the BpmnPlane and build up all with a BpmnShape first and with a BpmnEdge afterwards
    List<BpmnEdge> bpmnEdges = new ArrayList<>();
    for (BpmnElement child : diagram.getPlane().getElement().getChildren()) {
      buildElement(child, diagram.getPlane(), localRoot, bpmnEdges);
    }
    for (BpmnEdge bpmnEdge : bpmnEdges) {
      buildEdge(bpmnEdge);
    }

    // If we collapse the shape before we add edges, edge labels disappear -> Folding after edge creation
    // But we have to rearrange Labels first, otherwise they are not in sync with the positions after folding.
    rearrange();
    for (BpmnShape shape : diagram.getPlane().getListOfShapes()) {
      if ("false".equals(shape.getIsExpanded())) {
        view.collapse(shape.getElement().getNode());
      }
    }

    if (PARSE_FOLDED_DIAGRAMS) {
      for (Map.Entry<BpmnDiagram, BpmnElement> child : diagram.getChildren().entrySet()) {
        boolean collapsed = !view.isExpanded(child.getValue().getNode());
        INode lastRoot = view.getLocalRoot();
        if (collapsed) {
          view.setLocalRoot(child.getValue().getNode());
        }
        loadDiagram(child.getKey(), child.getValue().getNode());
        if (collapsed) {
          view.setLocalRoot(lastRoot);
        }
      }
    }

    List<INode> groupNodes = this.getMasterGraph().getNodes().stream()
        .filter(node -> node.getStyle() instanceof GroupNodeStyle)
        .collect(Collectors.toList());
    for (INode groupNode : groupNodes) {
      if (getMasterGraph().getChildren(groupNode).size() == 0) {
        List<INode> newChildren = getMasterGraph().getChildren(getMasterGraph().getParent(groupNode)).stream()
            .filter(child -> child != groupNode && groupNode.getLayout().contains(child.getLayout().getTopLeft()) && groupNode.getLayout().contains(child.getLayout().getBottomRight()))
            .collect(Collectors.toList());
        for (INode newChild : newChildren) {
          getMasterGraph().setParent(newChild, groupNode);
        }
      }
    }
  }

  /**
   * Returns the {@link BpmnShape} for the {@code element} in the context of this {@code plane}.
   * @param element The element to get the shape for.
   * @param plane The plane containing the shape for the element.
   */
  private BpmnShape getShape( BpmnElement element, BpmnPlane plane ) {
    BpmnElement referencedElement = null;
    if ("participantRef".equals(element.getName())) {
      referencedElement = getDocument().getElements().get(element.getValue());
    }

    // check if there is a valid shape for this element or the referenced one
    for (BpmnShape shape : plane.getListOfShapes()) {
      if (isValidShape(shape, element, referencedElement, plane)) {
        return shape;
      }
    }
    return null;
  }

  /**
   * Returns whether the {@code shape} belongs to this {@code element} or {@code referencedElement} in the context of the
   * {@code plane}.
   * @param shape The shape to check for validity.
   * @param element The element to check if the shape is valid.
   * @param referencedElement The element referenced by the given element.
   * @param plane The plane containing the given shape.
   */
  private boolean isValidShape( BpmnShape shape, BpmnElement element, BpmnElement referencedElement, BpmnPlane plane ) {
    if (shape.getElement() != element && shape.getElement() != referencedElement) {
      // shape has to be defined for Element or referenced Element
      return false;
    }
    if (shape.getChoreographyActivityShape() == null) {
      // there is no ChoreographyActivityShape, so no further checks needed
      return true;
    }
    if (element.getParent() != null && ("choreographyTask".equals(element.getParent().getName()) || "subChoreography".equals(element.getParent().getName()))) {
      // if a ChoreographyActivityShape is defined, we need to be inside the defined choreographyTask or subChoreography
      BpmnShape choreoShape = getShape(element.getParent(), plane);
      if (choreoShape != null) {
        return Objects.equals(shape.getChoreographyActivityShape(), choreoShape.getId());
      }
    }
    return false;
  }

  /**
   * Returns the {@link BpmnEdge} for the {@code element} in the context of this {@code plane}.
   * @param element The element to get an BpmnEdge for.
   * @param plane The plane containing the BpmnEdges.
   */
  private BpmnEdge getEdge( BpmnElement element, BpmnPlane plane ) {
    for (BpmnEdge bpmnEdge : plane.getListOfEdges()) {
      if (bpmnEdge.getElement() == element) {
        return bpmnEdge;
      }
    }
    return null;
  }

  /**
   * Recursively builds BPMN items from {@code element} and its descendants.
   * @param element The element to build an BPMN item for.
   * @param plane The plane containing the shapes for the current {@link BpmnDiagram}.
   * @param localRoot The current root node.
   * @param bpmnEdges The collection to add all found {@link BpmnEdge} to process later.
   */
  private void buildElement( BpmnElement element, BpmnPlane plane, INode localRoot, Collection<BpmnEdge> bpmnEdges ) {
    if ("laneSet".equals(element.getName())) {
      // build up the Pool structure defined by the laneSet
      buildPool(element, plane, localRoot);
    } else {
      BpmnShape bpmnShape = getShape(element, plane);
      BpmnEdge bpmnEdge = getEdge(element, plane);
      if (bpmnShape != null) {
        if (element.getParent().getNode() == null) {
          element.getParent().setNode(localRoot);
        }
        buildShape(bpmnShape, element);
      } else if (bpmnEdge != null) {
        bpmnEdges.add(bpmnEdge);
        return;
      }
      if (element.getProcess() != null) {
        // The element references another Process so build it as well
        final BpmnElement process = getElementForId(element.getProcess());
        if (process != null) {
          getProcessRefSource().put(process, element);
          buildElement(process, plane, localRoot, bpmnEdges);
        }
      }
      // check if all children or only data associations shall be processed
      boolean parseOnlyDataAssociations = bpmnShape != null && "subProcess".equals(element.getName()) && "false".equals(bpmnShape.getIsExpanded());
      if (parseOnlyDataAssociations) {
        // this is a collapsed subProcess - check if it is linked to its own Diagram
        if (!getDocument().getElementToDiagram().containsKey(element)) {
          // there is no diagram associated with the subProcess so we parse the children for this diagram
          parseOnlyDataAssociations = false;
        }
      }
      for (BpmnElement child : element.getChildren()) {
        if (!parseOnlyDataAssociations || "dataInputAssociation".equals(child.getName()) || "dataOutputAssociation".equals(child.getName())) {
          buildElement(child, plane, localRoot, bpmnEdges);
        }
      }
    }
  }

  /**
   * Returns the {@link BpmnElement} registered by {@code id}.
   * @param id The id to look up the element for.
   * @return The looked-up element
   */
  private BpmnElement getElementForId(String id) {
    if (getDocument().getElements().containsKey(id)) {
      return getDocument().getElements().get(id);
    }
    int separatorIndex = id.indexOf(':');
    if (separatorIndex > 0) {
      // if no element was found for id but the id was prefixed for a namespace, try to find an element for an id without prefix
      String shortId = id.substring(separatorIndex + 1);
      if (getDocument().getElements().containsKey(shortId)) {
        return getDocument().getElements().get(shortId);
      }
    }
    return null;
  }

  /**
   * Rearranges the labels using a labeling algorithm to reduce overlaps.
   */
  private void rearrange() {
    if (!REARRANGE_LABELS) {
      return;
    }
    LabelingData labelingData = new LabelingData();
    labelingData.getEdgeLabelPreferredPlacement().setFunction(label -> {
      PreferredPlacementDescriptor preferredPlacementDescriptor = new PreferredPlacementDescriptor();
      if ("yes".equalsIgnoreCase(label.getText()) || "no".equalsIgnoreCase(label.getText())) {
        preferredPlacementDescriptor.setPlaceAlongEdge(LabelPlacements.AT_SOURCE);
        return preferredPlacementDescriptor;
      } else {
        preferredPlacementDescriptor.setPlaceAlongEdge(LabelPlacements.AT_CENTER);
      }
      preferredPlacementDescriptor.setDistanceToEdge(5);
      preferredPlacementDescriptor.setSideOfEdge(LabelPlacements.LEFT_OF_EDGE.or(LabelPlacements.RIGHT_OF_EDGE));
      return preferredPlacementDescriptor;
    });
    GenericLabeling genericLabeling = new GenericLabeling();
    genericLabeling.setEdgeLabelPlacementEnabled(true);
    genericLabeling.setNodeLabelPlacementEnabled(false);
    genericLabeling.setInternalNodeLabelMovingEnabled(false);
    genericLabeling.setOptimizationStrategy(OptimizationStrategy.PREFERRED_PLACEMENT);
    genericLabeling.setAmbiguityReductionEnabled(true);
    genericLabeling.setAutoFlippingEnabled(false);
    genericLabeling.setEdgeGroupOverlapAllowed(false);
    genericLabeling.setLabelOverlapsReducingEnabled(true);
    genericLabeling.setNodeOverlapsRemovalEnabled(true);
    genericLabeling.setEdgeOverlapsRemovalEnabled(true);
    getMasterGraph().applyLayout(genericLabeling, labelingData);
  }

  /**
   * Creates an {@link INode} on the graph.
   * @param shape The {@link BpmnShape} to draw.
   * @param originalElement The original element the shape shall be applied for.
   */
  private void buildShape( BpmnShape shape, BpmnElement originalElement ) {
    RectD bounds = new RectD(shape.getX(), shape.getY(), shape.getWidth(), shape.getHeight());

    switch (shape.getElement().getName()) {
        // Gateways
      case BpmnDiConstants.EXCLUSIVE_GATEWAY_ELEMENT:
        if (shape.isMarkerVisible()) {
          buildGatewayNode(shape, bounds, GatewayType.EXCLUSIVE_WITH_MARKER);
        } else {
          buildGatewayNode(shape, bounds, GatewayType.EXCLUSIVE_WITHOUT_MARKER);
        }
        break;
      case BpmnDiConstants.PARALLEL_GATEWAY_ELEMENT:
        buildGatewayNode(shape, bounds, GatewayType.PARALLEL);
        break;
      case BpmnDiConstants.INCLUSIVE_GATEWAY_ELEMENT:
        buildGatewayNode(shape, bounds, GatewayType.INCLUSIVE);
        break;
      case BpmnDiConstants.EVENT_BASED_GATEWAY_ELEMENT:
        if ("Exclusive".equals(shape.getAttribute("eventGatewayType"))) {
          buildGatewayNode(shape, bounds, GatewayType.EXCLUSIVE_EVENT_BASED);
        } else if ("Parallel".equals(shape.getAttribute("eventGatewayType"))) {
          buildGatewayNode(shape, bounds, GatewayType.PARALLEL_EVENT_BASED);
        } else {
          buildGatewayNode(shape, bounds, GatewayType.EVENT_BASED);
        }
        break;
      case BpmnDiConstants.COMPLEX_GATEWAY_ELEMENT:
        buildGatewayNode(shape, bounds, GatewayType.COMPLEX);
        break;

        // Activities - Tasks
      case BpmnDiConstants.TASK_ELEMENT:
        buildTaskNode(shape, bounds, TaskType.ABSTRACT);
        break;
      case BpmnDiConstants.USER_TASK_ELEMENT:
        buildTaskNode(shape, bounds, TaskType.USER);
        break;
      case BpmnDiConstants.MANUAL_TASK_ELEMENT:
        buildTaskNode(shape, bounds, TaskType.MANUAL);
        break;
      case BpmnDiConstants.SERVICE_TASK_ELEMENT:
        buildTaskNode(shape, bounds, TaskType.SERVICE);
        break;
      case BpmnDiConstants.SCRIPT_TASK_ELEMENT:
        buildTaskNode(shape, bounds, TaskType.SCRIPT);
        break;
      case BpmnDiConstants.SEND_TASK_ELEMENT:
        buildTaskNode(shape, bounds, TaskType.SEND);
        break;
      case BpmnDiConstants.RECEIVE_TASK_ELEMENT:
        buildTaskNode(shape, bounds, TaskType.RECEIVE);
        break;
      case BpmnDiConstants.BUSINESS_RULE_TASK_ELEMENT:
        buildTaskNode(shape, bounds, TaskType.BUSINESS_RULE);
        break;

        // Activities - subProcess
      case BpmnDiConstants.SUB_PROCESS_ELEMENT:
        if ("true".equals(shape.getAttribute(BpmnDiConstants.TRIGGERED_BY_EVENT_ATTRIBUTE))) {
          buildSubProcessNode(shape, bounds, ActivityType.EVENT_SUB_PROCESS);
        } else {
          buildSubProcessNode(shape, bounds, ActivityType.SUB_PROCESS);
        }
        break;

        // Activities - Ad-Hoc Sub-Process 
      case BpmnDiConstants.AD_HOC_SUB_PROCESS_ELEMENT:
        if ("true".equals(shape.getAttribute(BpmnDiConstants.TRIGGERED_BY_EVENT_ATTRIBUTE))) {
          buildSubProcessNode(shape, bounds, ActivityType.EVENT_SUB_PROCESS);
        } else {
          buildSubProcessNode(shape, bounds, ActivityType.SUB_PROCESS);
        }
        break;

        // Activities - Transaction
      case BpmnDiConstants.TRANSACTION_ELEMENT:
        buildSubProcessNode(shape, bounds, ActivityType.TRANSACTION);
        break;

        // Activities - callActivity
      case BpmnDiConstants.CALL_ACTIVITY_ELEMENT:
        buildSubProcessNode(shape, bounds, ActivityType.CALL_ACTIVITY);
        break;

        //Events
      case BpmnDiConstants.START_EVENT_ELEMENT:
        if ("true".equals(shape.getAttribute(BpmnDiConstants.IS_INTERRUPTING_ATTRIBUTE))) {
          buildEventNode(shape, bounds, EventCharacteristic.SUB_PROCESS_INTERRUPTING);
        } else if ("false".equals(shape.getAttribute(BpmnDiConstants.IS_INTERRUPTING_ATTRIBUTE))) {
          buildEventNode(shape, bounds, EventCharacteristic.SUB_PROCESS_NON_INTERRUPTING);
        } else {
          buildEventNode(shape, bounds, EventCharacteristic.START);
        }
        break;
      case BpmnDiConstants.END_EVENT_ELEMENT:
        buildEventNode(shape, bounds, EventCharacteristic.END);
        break;
      case BpmnDiConstants.BOUNDARY_EVENT_ELEMENT:
        // Boundary Events are realized as Ports instead of Nodes
        buildBoundaryEvent(shape);
        break;
      case BpmnDiConstants.INTERMEDIATE_THROW_EVENT_ELEMENT:
        buildEventNode(shape, bounds, EventCharacteristic.THROWING);
        break;
      case BpmnDiConstants.INTERMEDIATE_CATCH_EVENT_ELEMENT:
        buildEventNode(shape, bounds, EventCharacteristic.CATCHING);
        break;

        // Conversation
      case BpmnDiConstants.CONVERSATION_ELEMENT:
        buildConversationNode(shape, bounds, ConversationType.CONVERSATION, null);
        break;
      case BpmnDiConstants.CALL_CONVERSATION_ELEMENT:
        final BpmnElement refElement = getElementForId(shape.getAttribute(BpmnDiConstants.CALLED_COLLABORATION_REF_ATTRIBUTE));
        if (refElement != null) {
          switch (refElement.getName()) {
            case BpmnDiConstants.COLLABORATION_ELEMENT:
              buildConversationNode(shape, bounds, ConversationType.CALLING_COLLABORATION, refElement);
              break;
            case BpmnDiConstants.GLOBAL_CONVERSATION_ELEMENT:
              buildConversationNode(shape, bounds, ConversationType.CALLING_GLOBAL_CONVERSATION, refElement);
              break;
            default:
              // This should not happen under strict conformance
              buildConversationNode(shape, bounds, ConversationType.CONVERSATION, refElement);
              break;
          }
        }
        break;
      case BpmnDiConstants.SUB_CONVERSATION_ELEMENT:
        buildConversationNode(shape, bounds, ConversationType.SUB_CONVERSATION, null);
        break;

        // Choreography
      case BpmnDiConstants.CHOREOGRAPHY_TASK_ELEMENT:
      case BpmnDiConstants.SUB_CHOREOGRAPHY_ELEMENT:
      case BpmnDiConstants.CALL_CHOREOGRAPHY_ELEMENT:
        buildChoreographyNode(shape, bounds);
        break;

        // Participants 
      case BpmnDiConstants.PARTICIPANT_ELEMENT:
        BpmnElement parent = originalElement.getParent();
        // If the participant is not part of a choreography node, create a node
        if (!parent.getName().toLowerCase().contains("choreography")) {
          buildParticipantNode(shape, bounds);
        } else if (parent.getNode() != null) {
          // Else add it to the appropriate choreography
          buildParticipantLabel(shape);
        }
        break;
      case "participantRef":

        break;
        // Text Annotations 
      case BpmnDiConstants.TEXT_ANNOTATION_ELEMENT:
        buildTextAnnotationNode(shape, bounds);
        break;

        // Groups
      case BpmnDiConstants.GROUP_ELEMENT:
        buildGroupNode(shape, bounds);
        break;

        // DataObject
      case BpmnDiConstants.DATA_OBJECT_REFERENCE_ELEMENT:
        // Find out, if the data Object is a collection
        boolean collection = false;
        final BpmnElement dataObject = getElementForId(shape.getAttribute(BpmnDiConstants.DATA_OBJECT_REF_ATTRIBUTE));
        if (dataObject != null) {
          final String collectionString = dataObject.getAttributes().get(BpmnDiConstants.IS_COLLECTION_ATTRIBUTE);
          if ("true".equals(collectionString)) {
            collection = true;
          }
        }
        buildDataObjectNode(shape, bounds, DataObjectType.NONE, collection);
        break;
      case BpmnDiConstants.DATA_INPUT_ELEMENT:
        // Find out, if the data Object is a collection
        collection = "true".equals(shape.getAttribute(BpmnDiConstants.IS_COLLECTION_ATTRIBUTE));
        buildDataObjectNode(shape, bounds, DataObjectType.INPUT, collection);
        break;
      case BpmnDiConstants.DATA_OUTPUT_ELEMENT:
        // Find out, if the data Object is a collection
        collection = "true".equals(shape.getAttribute(BpmnDiConstants.IS_COLLECTION_ATTRIBUTE));
        buildDataObjectNode(shape, bounds, DataObjectType.OUTPUT, collection);
        break;

        // DataStore
      case BpmnDiConstants.DATA_STORE_REFERENCE_ELEMENT:
        buildDataStoreReferenceNode(shape, bounds);
        break;
    }
    INode iNode = shape.getElement().getNode();
    if (iNode != null) {
      setNodeTag(shape, iNode);
    }
  }

  /**
   * Creates an {@link IEdge} on the graph.
   * @param edge The {@link BpmnEdge} to draw.
   */
  private void buildEdge( BpmnEdge edge ) {
    BpmnElement element = edge.getElement();
    BpmnElement source = edge.getSource();
    IEdge iEdge = null;
    switch (element.getName()) {
      case BpmnDiConstants.SEQUENCE_FLOW_ELEMENT:
        if (element.getChild(BpmnDiConstants.CONDITION_EXPRESSION_ELEMENT) != null && !(source.getName().endsWith(BpmnDiConstants.GATEWAY_SUFFIX))) {
          iEdge = buildDefaultEdge(edge, EdgeType.CONDITIONAL_FLOW);
        } else  {
          String elementId = element.getId();
          if (source != null &&
              (elementId == null
              ? null == source.getValue("default")
              : elementId.equals(source.getValue("default")))) {
            iEdge = buildDefaultEdge(edge, EdgeType.DEFAULT_FLOW);
          } else {
            iEdge = buildDefaultEdge(edge, EdgeType.SEQUENCE_FLOW);
          }
        }
        break;
      case BpmnDiConstants.ASSOCIATION_ELEMENT:
        String direction = edge.getAttribute(BpmnDiConstants.ASSOCIATION_DIRECTION_ATTRIBUTE);
        if (direction == null) {
          direction = "";
        }
        switch (direction) {
          case "None":
            iEdge = buildDefaultEdge(edge, EdgeType.ASSOCIATION);
            break;
          case "One":
            iEdge = buildDefaultEdge(edge, EdgeType.DIRECTED_ASSOCIATION);
            break;
          case "Both":
            iEdge = buildDefaultEdge(edge, EdgeType.BIDIRECTED_ASSOCIATION);
            break;
          default:
            // This shouldn't happen under strict conformance
            iEdge = buildDefaultEdge(edge, EdgeType.ASSOCIATION);
            break;
        }
        break;
      case BpmnDiConstants.DATA_ASSOCIATION_ELEMENT:
        iEdge = buildDefaultEdge(edge, EdgeType.ASSOCIATION);
        break;
      case BpmnDiConstants.CONVERSATION_LINK_ELEMENT:
        iEdge = buildDefaultEdge(edge, EdgeType.CONVERSATION);
        break;
      case BpmnDiConstants.MESSAGE_FLOW_ELEMENT:
        iEdge = buildMessageFlow(edge);
        break;
      case BpmnDiConstants.DATA_INPUT_ASSOCIATION_ELEMENT:
        iEdge = buildDefaultEdge(edge, EdgeType.DIRECTED_ASSOCIATION);
        break;
      case BpmnDiConstants.DATA_OUTPUT_ASSOCIATION_ELEMENT:
        iEdge = buildDefaultEdge(edge, EdgeType.DIRECTED_ASSOCIATION);
        break;
    }
    if (iEdge != null) {
      // Create label & set style
      addEdgeLabel(edge);

      setEdgeTag(edge, iEdge);
    }
  }

  /**
   * Adds some of the {@link BpmnShape} or {@link BpmnElement} data to the {@link ITagOwner#getTag() Tag}
   * of {@code iNode}.
   * @param shape The bpmn shape used to create the node.
   * @param iNode The node whose tag shall be filled.
   */
  final void setNodeTag( BpmnShape shape, INode iNode ) {
    BpmnElement extensionElements = shape.getElement().getChild("extensionElements");
    if (extensionElements != null) {
      StringBuilder toolTipText = new StringBuilder();
      for (Node foreignChild : extensionElements.getForeignChildren()) {
        toolTipText.append(foreignChild).append("\n");
      }
      iNode.setTag(toolTipText.toString());
    }
  }

  /**
   * Adds some of the {@link BpmnEdge} or {@link BpmnElement} data to the {@link com.yworks.yfiles.graph.ITagOwner#getTag() Tag}
   * of {@code iEdge}.
   * @param edge The bpmn edge used to create the edge.
   * @param iEdge The edge whose tag shall be filled.
   */
  final void setEdgeTag( BpmnEdge edge, IEdge iEdge ) {
    BpmnElement elementExtensions = edge.getElement().getChild("elementExtensions");
    if (elementExtensions != null) {
      StringBuilder toolTipText = new StringBuilder();
      for (Node foreignChild : elementExtensions.getForeignChildren()) {
        toolTipText.append(foreignChild).append("\n");
      }
      iEdge.setTag(toolTipText.toString());
    }
  }



  // Builds a Gateway node
  private void buildGatewayNode( BpmnShape shape, RectD bounds, GatewayType type ) {
    INode node = getMasterGraph().createNode(bounds);
    BpmnElement element = shape.getElement();
    setParent(node, element.getParent().getNode());
    element.setNode(node);

    // dataAssociations point to invisible children of activities, therefore, the INode has to be linked there
    element.setINodeInputOutput(node);

    //Add Style
    GatewayNodeStyle gatewayStyle = new GatewayNodeStyle();
    gatewayStyle.setType(type);
    getMasterGraph().setStyle(node, gatewayStyle);

    //Add Label
    ILabel label = addNodeLabel(node, shape);
    if (shape.hasLabelPosition()) {
      setFixedBoundsLabelStyle(label, shape.getLabelBounds());
    } else {
      setExternalLabelStyle(label);
      if (shape.hasLabelSize()) {
        getMasterGraph().setLabelPreferredSize(label, shape.getLabelBounds().getSize());
      }
    }
  }

  // Builds a Task node
  private void buildTaskNode( BpmnShape shape, RectD bounds, TaskType type ) {
    INode node = getMasterGraph().createNode(bounds);
    BpmnElement element = shape.getElement();
    setParent(node, element.getParent().getNode());
    element.setNode(node);

    // dataAssociations point to invisible children of activities, therefore, the INode has to be linked there
    element.setINodeInputOutput(node);

    //Add Style
    ActivityNodeStyle activityStyle = new ActivityNodeStyle();
    activityStyle.setCompensation("true".equals(shape.getAttribute("isForCompensation")));
    activityStyle.setLoopCharacteristic(element.getLoopCharacteristics());
    activityStyle.setActivityType(ActivityType.TASK);
    activityStyle.setTaskType(type);
    getMasterGraph().setStyle(node, activityStyle);

    //Add Label
    ILabel label = addNodeLabel(node, shape);
    setInternalLabelStyle(label);
  }

  // Builds a SubProcess node
  private void buildSubProcessNode( BpmnShape shape, RectD bounds, ActivityType type ) {
    INode node = getMasterGraph().createNode(bounds);
    BpmnElement element = shape.getElement();

    // All SubProcess have to be GroupNodes, so they can be collapsed/expanded
    getMasterGraph().setIsGroupNode(node, true);

    setParent(node, element.getParent().getNode());
    element.setNode(node);
    final BpmnElement calledElement = element.getCalledElement() != null
        ? getElementForId(element.getCalledElement())
        : null;

    // If this subProcess is a callActivity and calls an existing process, link the Node there aswell
    if (calledElement != null) {
      calledElement.setNode(node);
    }

    // dataAssociations point to invisible children of activities, therefore, the INode has to be linked there
    element.setINodeInputOutput(node);

    ActivityNodeStyle activityStyle = new ActivityNodeStyle();
    activityStyle.setCompensation("true".equals(shape.getAttribute(BpmnDiConstants.IS_FOR_COMPENSATION_ATTRIBUTE)));
    activityStyle.setLoopCharacteristic(element.getLoopCharacteristics());
    // Get, if the subProcess is expanded
    ILabel label = addNodeLabel(node, shape);
    setSubProcessLabelStyle(label);
    activityStyle.setActivityType(type);
    activityStyle.setTriggerEventType(getEventType(shape));

    if ("true".equals(shape.getAttribute(BpmnDiConstants.IS_INTERRUPTING_ATTRIBUTE))) {
      activityStyle.setTriggerEventCharacteristic(EventCharacteristic.SUB_PROCESS_INTERRUPTING);
    } else {
      activityStyle.setTriggerEventCharacteristic(EventCharacteristic.SUB_PROCESS_NON_INTERRUPTING);
    }
    activityStyle.setSubState(SubState.DYNAMIC);

    getMasterGraph().setStyle(node, activityStyle);
  }

  // Builds an Event node
  private void buildEventNode( BpmnShape shape, RectD bounds, EventCharacteristic characteristic ) {
    INode node = getMasterGraph().createNode(bounds);
    BpmnElement element = shape.getElement();
    setParent(node, element.getParent().getNode());
    element.setNode(node);

    // dataAssociations point to invisible children of activities, therefore, the INode has to be linked there
    element.setINodeInputOutput(node);

    // Add Style
    EventNodeStyle eventStyle = new EventNodeStyle();
    eventStyle.setType(getEventType(shape));
    eventStyle.setCharacteristic(characteristic);
    getMasterGraph().setStyle(node, eventStyle);

    // Add Label
    ILabel label = addNodeLabel(node, shape);
    if (shape.hasLabelPosition()) {
      setFixedBoundsLabelStyle(label, shape.getLabelBounds());
    } else {
      setExternalLabelStyle(label);
      if (shape.hasLabelSize()) {
        getMasterGraph().setLabelPreferredSize(label, shape.getLabelBounds().getSize());
      }
    }
  }

  // Builds a Boundary Event, realized as a port instead of a node
  private void buildBoundaryEvent( BpmnShape shape ) {
    final BpmnElement parent = getElementForId(shape.getAttribute(BpmnDiConstants.ATTACHED_TO_REF_ATTRIBUTE));
    EventPortStyle portStyle = new EventPortStyle();
    portStyle.setType(getEventType(shape));
    portStyle.setCharacteristic("false".equals(shape.getAttribute(BpmnDiConstants.CANCEL_ACTIVITY_ATTRIBUTE)) ? EventCharacteristic.BOUNDARY_NON_INTERRUPTING : EventCharacteristic.BOUNDARY_INTERRUPTING);
    if (parent == null) {
      throw new IllegalArgumentException("Parameter 'shape': Shape with no parent");
    }
    if (parent.getNode() == null) {
      getDocument().getMessages().add("The node for boundaryEvent " + shape.getId() + " was not (yet) created!");
      return;
    }

    BpmnElement element = shape.getElement();

    // dataAssociations point to invisible children of Tasks, therefore, the INode has to be linked there 
    element.setINodeInputOutput(parent.getNode());

    IPort port = getMasterGraph().addPort(parent.getNode(), new PointD(shape.getX() + shape.getWidth() / 2, shape.getY() + shape.getHeight() / 2), portStyle);
    element.setPort(port);
    element.setNode(parent.getNode());
    ILabel label = addNodeLabel(port, shape);

    if (shape.hasLabelPosition()) {
      setFixedBoundsLabelStyle(label, shape.getLabelBounds());
    } else {
      getMasterGraph().setStyle(label, new DefaultLabelStyle());
      if (shape.hasLabelSize()) {
        getMasterGraph().setLabelPreferredSize(label, shape.getLabelBounds().getSize());
      } else {
        InsideOutsidePortLabelModel outsideModel = new InsideOutsidePortLabelModel();
        outsideModel.setDistance(10);
        getMasterGraph().setLabelLayoutParameter(label, outsideModel.createOutsideParameter());
      }
    }
  }

  // Builds a Conversation node
  private void buildConversationNode( BpmnShape shape, RectD bounds, ConversationType type, BpmnElement refElement ) {
    INode node = getMasterGraph().createNode(bounds);
    BpmnElement element = shape.getElement();
    setParent(node, element.getParent().getNode());

    element.setNode(node);

    // dataAssociations point to invisible children of Tasks, therefore, the INode has to be linked there 
    element.setINodeInputOutput(node);

    // Add Style
    ConversationNodeStyle conversationStyle = new ConversationNodeStyle();
    conversationStyle.setType(type);
    getMasterGraph().setStyle(node, conversationStyle);

    // Add Label
    ILabel label = addNodeLabel(node, shape);
    if (shape.hasLabelPosition()) {
      setFixedBoundsLabelStyle(label, shape.getLabelBounds());
    } else {
      setExternalLabelStyle(label);
      if (shape.hasLabelSize()) {
        getMasterGraph().setLabelPreferredSize(label, shape.getLabelBounds().getSize());
      }
    }
  }

  // Builds a Choreography node
  private void buildChoreographyNode( BpmnShape shape, RectD bounds ) {
    INode node = getMasterGraph().createGroupNode(view.getLocalRoot(), bounds);
    BpmnElement element = shape.getElement();
    setParent(node, element.getParent().getNode());
    element.setNode(node);

    // dataAssociations point to invisible children of Tasks, therefore, the INode has to be linked there 
    element.setINodeInputOutput(node);

    ChoreographyNodeStyle choreographyStyle = new ChoreographyNodeStyle();
    choreographyStyle.setLoopCharacteristic(element.getLoopCharacteristics());
    //Get Loop Characteristics
    element.setTopParticipants(0);
    element.setBottomParticipants(0);
    ILabel label = addNodeLabel(node, shape);

    // Get SubState
    if ("true".equals(shape.getIsExpanded())) {
      choreographyStyle.setSubState(SubState.NONE);
      getMasterGraph().setStyle(node, choreographyStyle);
    } else if ("false".equals(shape.getIsExpanded())) {
      choreographyStyle.setSubState(SubState.DYNAMIC);
      getMasterGraph().setStyle(node, choreographyStyle);
    } else {
      getMasterGraph().setStyle(node, choreographyStyle);
    }

    setChoreographyLabelStyle(label);
  }

  // Builds a dataObject Node
  private void buildDataObjectNode( BpmnShape shape, RectD bounds, DataObjectType type, boolean isCollection ) {
    INode node = getMasterGraph().createNode(bounds);
    BpmnElement element = shape.getElement();
    setParent(node, element.getParent().getNode());
    element.setNode(node);

    // dataAssociations point to invisible children of Tasks, therefore, the INode has to be linked there 
    element.setINodeInputOutput(node);

    DataObjectNodeStyle objectStyle = new DataObjectNodeStyle();
    objectStyle.setType(type);
    objectStyle.setCollection(isCollection);
    getMasterGraph().setStyle(node, objectStyle);

    ILabel label = addNodeLabel(node, shape);

    if (shape.hasLabelPosition()) {
      setFixedBoundsLabelStyle(label, shape.getLabelBounds());
    } else {
      setExternalLabelStyle(label);
      if (shape.hasLabelSize()) {
        getMasterGraph().setLabelPreferredSize(label, shape.getLabelBounds().getSize());
      }
    }
  }

  // Builds a participant node (actually a pool)
  private void buildParticipantNode( BpmnShape shape, RectD bounds ) {
    BpmnElement element = shape.getElement();
    String processRef = element.getProcess();
    final BpmnElement processElement = processRef != null ? getElementForId(processRef) : null;
    if (processElement == null || processElement.getChild("laneSet") == null) {
      // not connected to a process so we need our own node

      INode node = getMasterGraph().createNode(bounds);
      setParent(node, element.getParent().getNode());
      element.setNode(node);
      if (processElement != null) {
        processElement.setNode(node);
      }

      // dataAssociations point to invisible children of Tasks, therefore, the INode has to be linked there 
      element.setINodeInputOutput(node);

      PoolNodeStyle partStyle = createTable(shape);
      if (element.hasChild(BpmnDiConstants.PARTICIPANT_MULTIPLICITY_ELEMENT)) {
        if (Integer.parseInt(element.getChildAttribute(BpmnDiConstants.PARTICIPANT_MULTIPLICITY_ELEMENT, "maximum")) > 1) {
          partStyle.setMultipleInstance(true);
        }
      }

      getMasterGraph().setStyle(node, partStyle);

      ITable table = partStyle.getTableNodeStyle().getTable();
      if (shape.isHorizontal()) {
        IRow row = table.getRootRow().getChildRows().iterator().next();
        addTableLabel(table, row, shape);
      } else {
        IColumn column = table.getRootColumn().getChildColumns().iterator().next();
        addTableLabel(table, column, shape);
      }
    }
  }

  // Builds a participant label inside a choreography node
  private void buildParticipantLabel( BpmnShape shape ) {
    BpmnElement choreography = getCurrentDiagram().getPlane().getShape(shape.getChoreographyActivityShape()).getElement();
    INode node = choreography.getNode();
    boolean top = false;
    int index = 0;
    ChoreographyNodeStyle choreographyNodeStyle = ((ChoreographyNodeStyle)node.getStyle());
    boolean multipleInstance = false;
    BpmnElement element = shape.getElement();

    if (element.hasChild(BpmnDiConstants.PARTICIPANT_MULTIPLICITY_ELEMENT)) {
      if (Integer.parseInt(element.getChildAttribute(BpmnDiConstants.PARTICIPANT_MULTIPLICITY_ELEMENT, "maximum")) > 1) {
        multipleInstance = true;
      }
    }
    Participant participant = new Participant();
    participant.setMultiInstance(multipleInstance);
    ILabel label = addParticipantLabel(node, shape);
    switch (shape.getPartBandKind()) {
      case TOP_INITIATING:
        if (shape.isMessageVisible()) choreographyNodeStyle.setInitiatingMessage(true);
        choreographyNodeStyle.setInitiatingAtTop(true);
        choreographyNodeStyle.getTopParticipants().add(participant);
        top = true;
        index = choreography.getTopParticipants();
        choreography.setTopParticipants(index + 1);
        break;
      case TOP_NON_INITIATING:
        if (shape.isMessageVisible()) choreographyNodeStyle.setResponseMessage(true);
        choreographyNodeStyle.getTopParticipants().add(participant);
        top = true;
        index = choreography.getTopParticipants();
        choreography.setTopParticipants(index + 1);
        break;
      case BOTTOM_INITIATING:
        if (shape.isMessageVisible()) choreographyNodeStyle.setInitiatingMessage(true);
        choreographyNodeStyle.setInitiatingAtTop(false);
        choreographyNodeStyle.getBottomParticipants().add(participant);
        index = choreography.getBottomParticipants();
        choreography.setBottomParticipants(index + 1);
        break;
      case BOTTOM_NON_INITIATING:
        if (shape.isMessageVisible()) choreographyNodeStyle.setResponseMessage(true);
        choreographyNodeStyle.getBottomParticipants().add(participant);
        index = choreography.getBottomParticipants();
        choreography.setBottomParticipants(index + 1);
        break;
      case MIDDLE_INITIATING:
        // This shouldn't happen under strict conformance
        if (shape.isMessageVisible()) choreographyNodeStyle.setInitiatingMessage(true);
        if (choreography.getTopParticipants() < choreography.getBottomParticipants()) {
          top = true;
          index = choreography.getTopParticipants();
          choreography.setTopParticipants(index + 1);
          choreographyNodeStyle.setInitiatingAtTop(true);
          choreographyNodeStyle.getTopParticipants().add(participant);
        } else {
          index = choreography.getBottomParticipants();
          choreography.setBottomParticipants(index + 1);
          choreographyNodeStyle.setInitiatingAtTop(false);
          choreographyNodeStyle.getBottomParticipants().add(participant);
        }
        break;
      case MIDDLE_NON_INITIATING:
        if (shape.isMessageVisible()) choreographyNodeStyle.setResponseMessage(true);
        if (choreography.getTopParticipants() < choreography.getBottomParticipants()) {
          top = true;
          index = choreography.getTopParticipants();
          choreography.setTopParticipants(index + 1);
          choreographyNodeStyle.getTopParticipants().add(participant);
        } else {
          index = choreography.getBottomParticipants();
          choreography.setBottomParticipants(index + 1);
          choreographyNodeStyle.getBottomParticipants().add(participant);
        }
        break;
    }
    element.setNode(node);

    // Sets the label Style of the new participant
    ILabelModelParameter parameter = ChoreographyLabelModel.INSTANCE.createParticipantParameter(top, index);
    getMasterGraph().setLabelLayoutParameter(label, parameter);
    DefaultLabelStyle defaultLabelStyle = setCustomLabelStyle(label);
    defaultLabelStyle.setInsets(InsetsD.EMPTY);
    getMasterGraph().setStyle(label, defaultLabelStyle);

    // checks, if there is a message, if yes, tries to set text label
    if (shape.isMessageVisible() && choreography.hasChild(BpmnDiConstants.MESSAGE_FLOW_REF_ELEMENT)) {
      Iterable<BpmnElement> children = choreography.getChildren(BpmnDiConstants.MESSAGE_FLOW_REF_ELEMENT);

      String elementId = element.getId();
      for (BpmnElement child : children) {
        final BpmnElement messageFlow = getElementForId(child.getValue());
        if (messageFlow != null) {
          if (elementId == null
              ? null == messageFlow.getSource()
              : elementId.equals(messageFlow.getSource())) {
            String message = messageFlow.getLabel() != null ? messageFlow.getLabel() : "";
            label = getMasterGraph().addLabel(node, message, null, null, null, shape.getLabelStyle());
            if (top) {
              parameter = ChoreographyLabelModel.NORTH_MESSAGE;
            } else {
              parameter = ChoreographyLabelModel.SOUTH_MESSAGE;
            }
            getMasterGraph().setLabelLayoutParameter(label, parameter);
            defaultLabelStyle = setCustomLabelStyle(label);
            defaultLabelStyle.setInsets(InsetsD.EMPTY);
            getMasterGraph().setStyle(label, defaultLabelStyle);
            break;
          }
        }
      }
    }
  }

  // Builds a TextAnnotationNode
  private void buildTextAnnotationNode( BpmnShape shape, RectD bounds ) {
    INode node = getMasterGraph().createNode(bounds);
    BpmnElement element = shape.getElement();
    setParent(node, element.getParent().getNode());
    element.setNode(node);

    // Add Style
    AnnotationNodeStyle annotationStyle = new AnnotationNodeStyle();
    getMasterGraph().setStyle(node, annotationStyle);

    // Add Label
    ILabel label = addNodeLabel(node, shape);
    setInternalLabelStyle(label);
  }

  // Builds a Group Node
  private void buildGroupNode( BpmnShape shape, RectD bounds ) {
    BpmnElement element = shape.getElement();
    INode node = getMasterGraph().createGroupNode(element.getParent().getNode(), bounds);
    element.setNode(node);

    // Before Adding a Label, we need to get the Label Text, which is located in a categoryValue
    // The id of this category value is located in the Label
    String elementLabel = element.getLabel();
    for (Map.Entry<String, BpmnElement> childElement : getDocument().getElements().entrySet()) {
      if ((elementLabel == null
                 ? null == childElement.getValue().getId()
                 : elementLabel.equals(childElement.getValue().getId()))
          && childElement.getValue().getAttributes().containsKey("value")) {
        element.setLabel(childElement.getValue().getAttributes().get("value"));
        break;
      }
    }

    // Add Label
    ILabel label = addNodeLabel(node, shape);
    setGroupLabelStyle(label);
  }

  // Builds a DataStoreReference Node
  private void buildDataStoreReferenceNode( BpmnShape shape, RectD bounds ) {
    INode node = getMasterGraph().createNode(bounds);
    BpmnElement element = shape.getElement();
    setParent(node, element.getParent().getNode());
    element.setNode(node);

    // dataAssociations point to invisible children of Tasks, therefore, the INode has to be linked there 
    element.setINodeInputOutput(node);

    // Add Style
    DataStoreNodeStyle dataStoreStyle = new DataStoreNodeStyle();
    getMasterGraph().setStyle(node, dataStoreStyle);

    // Add Label
    ILabel label = addNodeLabel(node, shape);
    if (shape.hasLabelPosition()) {
      setFixedBoundsLabelStyle(label, shape.getLabelBounds());
    } else {
      setExternalLabelStyle(label);
      if (shape.hasLabelSize()) {
        getMasterGraph().setLabelPreferredSize(label, shape.getLabelBounds().getSize());
      }
    }
  }

  // Retrieves the correct EventType, returns EventNodeStyle with the EventType set accordingly
  private EventType getEventType( BpmnShape shape ) {
    EventType eventType = EventType.PLAIN;
    BpmnElement element = shape.getElement();

    if (element.hasChild(BpmnDiConstants.MESSAGE_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.MESSAGE;
    }
    if (element.hasChild(BpmnDiConstants.TIMER_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.TIMER;
    }
    if (element.hasChild(BpmnDiConstants.TERMINATE_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.TERMINATE;
    }
    if (element.hasChild(BpmnDiConstants.ESCALATION_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.ESCALATION;
    }
    if (element.hasChild(BpmnDiConstants.ERROR_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.ERROR;
    }
    if (element.hasChild(BpmnDiConstants.CONDITIONAL_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.CONDITIONAL;
    }
    if (element.hasChild(BpmnDiConstants.COMPENSATE_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.COMPENSATION;
    }
    if (element.hasChild(BpmnDiConstants.CANCEL_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.CANCEL;
    }
    if (element.hasChild(BpmnDiConstants.LINK_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.LINK;
    }
    if (element.hasChild(BpmnDiConstants.SIGNAL_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.SIGNAL;
    }
    if (element.hasChild(BpmnDiConstants.MULTIPLE_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.MULTIPLE;
    }
    if (element.hasChild(BpmnDiConstants.PARALLEL_EVENT_DEFINITION_ELEMENT)) {
      eventType = EventType.PARALLEL_MULTIPLE;
    }
    return eventType;
  }

  // Sets the parentNode of a Node, if the parentNode is part of the current Graph
  private void setParent( INode node, INode parentNode ) {
    if (getMasterGraph().contains(parentNode)) {
      getMasterGraph().setParent(node, parentNode);
    }
  }



  // Adds a label to a node
  private ILabel addNodeLabel( ILabelOwner owner, BpmnShape shape ) {
    // blank label, in case we added none
    String name = shape.getElement().getLabel();
    // only has label name, if we added one before
    if (!shape.isHasLabel() && !PARSE_ALL_LABELS || name == null) {
      name = "";
    }
    ILabel label = getMasterGraph().addLabel(owner, name, null, null, null, shape.getLabelStyle());

    return label;
  }

  // Adds a label to a participant
  private ILabel addParticipantLabel( ILabelOwner owner, BpmnShape shape ) {
    // blank label, in case we added none
    String name = shape.getElement().getLabel();
    // only has label name, if we added one before
    if (!shape.isHasLabel() && !PARSE_ALL_LABELS || name == null) {
      name = "";
    }
    ILabel label = getMasterGraph().addLabel(owner, name, null, null, null, shape.getLabelStyle());
    return label;
  }

  // Adds a label to a table object (rows/colums)
  private void addTableLabel( ITable table, IStripe owner, BpmnShape shape ) {
    // blank label, in case we added none
    String name = shape.getElement().getLabel();
    // only has label name, if we added one before
    if (!shape.isHasLabel() && !PARSE_ALL_LABELS || name == null) {
      name = "";
    }
    table.addLabel(owner, name, null, null, null, shape.getLabelStyle());
  }

  // Adds a label to an edge
  private void addEdgeLabel( BpmnEdge edge ) {
    // blank label, in case we added none
    String name = edge.getElement().getLabel();
    // only has label name, if we added one before
    if (!edge.isHasLabel() && !PARSE_ALL_LABELS || name == null) {
      name = "";
    }
    if (!name.isEmpty()) {
      ILabel label = getMasterGraph().addLabel(edge.getElement().getEdge(), name, null, null, null, edge.getLabelStyle());

      if (edge.hasLabelPosition()) {
        setFixedBoundsLabelStyle(label, edge.getLabelBounds());
      } else {
        setEdgeLabelStyle(label);
        if (edge.hasLabelSize()) {
          getMasterGraph().setLabelPreferredSize(label, edge.getLabelBounds().getSize());
        }
      }
    }
  }

  // Sets label style, if there are fixed bounds for this label
  private void setFixedBoundsLabelStyle( ILabel label, RectD bounds ) {
    ILabelModelParameter parameter = null;
    if (label.getOwner() instanceof INode) {
      FreeNodeLabelModel model = new FreeNodeLabelModel();
      parameter = model.findBestParameter(label, model, new OrientedRectangle(bounds));
    } else {
      FreeEdgeLabelModel model = new FreeEdgeLabelModel();
      parameter = model.findBestParameter(label, model, new OrientedRectangle(bounds));
    }
    getMasterGraph().setLabelLayoutParameter(label, parameter);
    DefaultLabelStyle defaultLabelStyle = setCustomLabelStyle(label);
    defaultLabelStyle.setInsets(InsetsD.EMPTY);
    getMasterGraph().setStyle(label, defaultLabelStyle);
    getMasterGraph().setLabelPreferredSize(label, new SizeD(bounds.width, bounds.height));
  }

  // Sets label style for tasks (Centered)
  private void setInternalLabelStyle( ILabel label ) {
    InteriorStretchLabelModel model = new InteriorStretchLabelModel();
    model.setInsets(new InsetsD(3));
    getMasterGraph().setLabelLayoutParameter(label, model.createParameter(InteriorStretchLabelModel.Position.CENTER));
    DefaultLabelStyle defaultLabelStyle = setCustomLabelStyle(label);
    defaultLabelStyle.setTextAlignment(TextAlignment.CENTER);
    defaultLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    defaultLabelStyle.setTextWrapping(TextWrapping.WRAP);
    defaultLabelStyle.setInsets(InsetsD.EMPTY);
    getMasterGraph().setStyle(label, defaultLabelStyle);
  }

  // Sets label style nodes that have an external label (South of the node)
  private void setExternalLabelStyle( ILabel label ) {
    getMasterGraph().setLabelLayoutParameter(label, getGenericLabelModel().createDefaultParameter());
    DefaultLabelStyle defaultLabelStyle = setCustomLabelStyle(label);
    defaultLabelStyle.setTextAlignment(TextAlignment.CENTER);
    defaultLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    defaultLabelStyle.setInsets(InsetsD.EMPTY);
    getMasterGraph().setStyle(label, defaultLabelStyle);

    if (MULTI_LINE_EXTERIOR_NODE_LABELS) {
      // Set some bounds to make labels multi - row
      double maxWidth = Math.max(((INode)label.getOwner()).getLayout().getWidth() * 1.5, 100);
      double maxHeight = label.getPreferredSize().height;
      double width = maxWidth;
      double height = maxHeight;
      while (width < label.getPreferredSize().width) {
        maxHeight += height;
        width += maxWidth;
      }
      getMasterGraph().setLabelPreferredSize(label, new SizeD(maxWidth, maxHeight));
    }
  }

  // Sets label style for the TaskNameBand in a Choreography
  private void setChoreographyLabelStyle( ILabel label ) {
    getMasterGraph().setLabelLayoutParameter(label, ChoreographyLabelModel.TASK_NAME_BAND);
    DefaultLabelStyle defaultLabelStyle = setCustomLabelStyle(label);
    defaultLabelStyle.setTextWrapping(TextWrapping.WRAP);
    defaultLabelStyle.setTextAlignment(TextAlignment.CENTER);
    defaultLabelStyle.setVerticalTextAlignment(VerticalAlignment.CENTER);
    defaultLabelStyle.setInsets(InsetsD.EMPTY);
    getMasterGraph().setStyle(label, defaultLabelStyle);
  }

  // Sets label style for SubProcesses (Upper left corner)
  private void setSubProcessLabelStyle( ILabel label ) {
    InteriorStretchLabelModel model = new InteriorStretchLabelModel();
    model.setInsets(new InsetsD(3));
    getMasterGraph().setLabelLayoutParameter(label, model.createParameter(InteriorStretchLabelModel.Position.NORTH));
    DefaultLabelStyle defaultLabelStyle = setCustomLabelStyle(label);
    defaultLabelStyle.setTextAlignment(TextAlignment.LEFT);
    defaultLabelStyle.setVerticalTextAlignment(VerticalAlignment.TOP);
    defaultLabelStyle.setInsets(InsetsD.EMPTY);
    getMasterGraph().setStyle(label, defaultLabelStyle);
  }

  // Sets label style for Groups (Upper boundary)
  private void setGroupLabelStyle( ILabel label ) {
    InteriorStretchLabelModel model = new InteriorStretchLabelModel();
    model.setInsets(new InsetsD(3));
    getMasterGraph().setLabelLayoutParameter(label, model.createParameter(InteriorStretchLabelModel.Position.NORTH));
    DefaultLabelStyle defaultLabelStyle = setCustomLabelStyle(label);
    defaultLabelStyle.setTextAlignment(TextAlignment.CENTER);
    defaultLabelStyle.setInsets(InsetsD.EMPTY);
    getMasterGraph().setStyle(label, defaultLabelStyle);
  }

  // Sets edge label style 
  private void setEdgeLabelStyle( ILabel label ) {
    if (label != null) {
      EdgePathLabelModel model = new EdgePathLabelModel();
      model.setOffset(7);
      model.setSideOfEdge(EdgeSides.ABOVE_EDGE);
      model.setAutoRotationEnabled(false);
      getMasterGraph().setLabelLayoutParameter(label, model.createDefaultParameter());
      DefaultLabelStyle defaultLabelStyle = setCustomLabelStyle(label);
      defaultLabelStyle.setTextAlignment(TextAlignment.CENTER);
      defaultLabelStyle.setInsets(InsetsD.EMPTY);
      getMasterGraph().setStyle(label, defaultLabelStyle);
    }
  }

  // Sets custom style elements
  private DefaultLabelStyle setCustomLabelStyle( ILabel label ) {
    String styleName = (String)label.getTag();
    return getCurrentDiagram().getStyle(styleName);
  }



  // Builds all edges, except for message flows
  private IEdge buildDefaultEdge( BpmnEdge edge, EdgeType type ) {
    BpmnElement sourceVar = edge.getSource();
    BpmnElement targetVar = edge.getTarget();
    List<PointD> waypoints = edge.getWaypoints();
    String id = edge.getId();

    // Check, if source and target were correctly parsed
    if (sourceVar == null) {
      getDocument().getMessages().add("Edge " + (id) + " has no valid Source.");
      return null;
    }
    if (targetVar == null) {
      getDocument().getMessages().add("Edge " + (id) + " has no valid Target.");
      return null;
    }

    // Get source & target node
    INode sourceNode = sourceVar.getNode();
    INode targetNode = targetVar.getNode();

    // Get bends & ports from waypoints
    int count = waypoints.size();
    PointD source = sourceNode.getLayout().getCenter();
    PointD target = targetNode.getLayout().getCenter();
    if (count > 0) {
      // First waypoint is source Port
      source = waypoints.get(0);
      // Last is target port
      target = waypoints.get(count - 1);
      waypoints.remove(source);
      waypoints.remove(target);
    }

    IPort sourcePort = null;
    IPort targetPort = null;

    // Use boundary event port, if source is a boundary event
    if (BpmnDiConstants.BOUNDARY_EVENT_ELEMENT.compareTo(sourceVar.getName()) == 0) {
      sourcePort = sourceVar.getPort();
      if (sourcePort != null) {
        sourceNode = (INode)sourcePort.getOwner();
      } else {
        getDocument().getMessages().add("The source boundary event for edge " + id + " was not (yet) created.");
        return null;
      }
    } else if (sourceNode != null) {
      sourcePort = getMasterGraph().addPort(sourceNode, source);
    }

    // Use boundary event port, if target is a boundary event
    if (BpmnDiConstants.BOUNDARY_EVENT_ELEMENT.compareTo(targetVar.getName()) == 0) {
      targetPort = targetVar.getPort();
      if (targetPort != null) {
        targetNode = (INode)targetPort.getOwner();
      } else {
        getDocument().getMessages().add("The target boundary event for edge " + id + " was not (yet) created.");
        return null;
      }
    } else if (targetNode != null) {
      targetPort = getMasterGraph().addPort(targetNode, target);
    }

    // Test for textAnnotation, workaround fix for textAnnotations
    if (type == EdgeType.ASSOCIATION) {
      if (targetNode == null) {
        targetPort = getMasterGraph().addPort(sourceNode, target);
      }

      if (sourceNode == null) {
        sourcePort = getMasterGraph().addPort(targetNode, source);
      }
    }

    // Test if one of the ports is still null, notify user and carry on.
    if (sourcePort == null) {
      getDocument().getMessages().add("Edge " + (id) + " has no valid Source.");
      return null;
    }
    if (targetPort == null) {
      getDocument().getMessages().add("Edge " + (id) + " has no valid Target.");
      return null;
    }

    // Create edge on graph
    IEdge iEdge = getMasterGraph().createEdge(sourcePort, targetPort);
    for (PointD point : waypoints) {
      getMasterGraph().addBend(iEdge, point, -1);
    }

    edge.getElement().setEdge(iEdge);

    // Set edge style
    BpmnEdgeStyle edgeStyle = new BpmnEdgeStyle();
    edgeStyle.setType(type);
    getMasterGraph().setStyle(iEdge, edgeStyle);

    return iEdge;
  }

  // Builds MessageFlow edges
  private IEdge buildMessageFlow( BpmnEdge edge ) {
    BpmnElement sourceVar = edge.getSource();
    BpmnElement targetVar = edge.getTarget();
    List<PointD> waypoints = edge.getWaypoints();
    String id = edge.getId();

    // Check, if source and target were correctly parsed
    if (sourceVar == null) {
      getDocument().getMessages().add("Edge " + (id) + " has no valid Source.");
      return null;
    }
    if (targetVar == null) {
      getDocument().getMessages().add("Edge " + (id) + " has no valid Target.");
      return null;
    }

    // Get source & target node
    INode sourceNode = sourceVar.getNode();
    INode targetNode = targetVar.getNode();

    // Get bends & ports from waypoints
    int count = waypoints.size();
    PointD source = sourceNode.getLayout().getCenter();
    PointD target = targetNode.getLayout().getCenter();
    if (count > 0) {
      // First waypoint is source Port
      source = waypoints.get(0);
      // Last is target port
      target = waypoints.get(count - 1);
      waypoints.remove(source);
      waypoints.remove(target);
    }

    // Get source & target port
    IPort sourcePort = BpmnDiConstants.BOUNDARY_EVENT_ELEMENT.equals(sourceVar.getName()) ? sourceVar.getPort() : getMasterGraph().addPort(sourceNode, source);
    IPort targetPort = BpmnDiConstants.BOUNDARY_EVENT_ELEMENT.equals(targetVar.getName()) ? targetVar.getPort() : getMasterGraph().addPort(targetNode, target);

    IEdge iEdge = getMasterGraph().createEdge(sourcePort, targetPort);
    for (PointD point : waypoints) {
      getMasterGraph().addBend(iEdge, point, -1);
    }
    edge.getElement().setEdge(iEdge);

    // If there is a message icon, add a corresponding label
    switch (edge.getMessageVisibleK()) {
      case INITIATING:
        ILabel messageLabel = getMasterGraph().addLabel(iEdge, "");
        getMasterGraph().setStyle(messageLabel, MessageLabelStyle.createInitiatingStyle());
        EdgeSegmentLabelModel model = new EdgeSegmentLabelModel();
        model.setSideOfEdge(EdgeSides.ON_EDGE);
        model.setAutoRotationEnabled(false);
        getMasterGraph().setLabelPreferredSize(messageLabel, bpmnMessageSize);
        getMasterGraph().setLabelLayoutParameter(messageLabel, model.createParameterFromCenter(0.5, EdgeSides.ON_EDGE));
        break;
      case NON_INITIATING:
        messageLabel = getMasterGraph().addLabel(iEdge, "");
        getMasterGraph().setStyle(messageLabel, MessageLabelStyle.createResponseStyle());
        model = new EdgeSegmentLabelModel();
        model.setSideOfEdge(EdgeSides.ON_EDGE);
        model.setAutoRotationEnabled(false);
        getMasterGraph().setLabelPreferredSize(messageLabel, bpmnMessageSize);
        getMasterGraph().setLabelLayoutParameter(messageLabel, model.createParameterFromCenter(0.5, EdgeSides.ON_EDGE));
        break;
      case UNSPECIFIED:
        break;
    }

    // Set edge style
    BpmnEdgeStyle edgeStyle = new BpmnEdgeStyle();
    edgeStyle.setType(EdgeType.MESSAGE_FLOW);
    getMasterGraph().setStyle(iEdge, edgeStyle);

    return iEdge;
  }



  private INode buildPool( BpmnElement element, BpmnPlane plane, INode localRoot ) {
    BpmnElement parent = element.getParent();
    while (BpmnDiConstants.PROCESS_ELEMENT.compareTo(parent.getName()) != 0 && BpmnDiConstants.SUB_PROCESS_ELEMENT.compareTo(parent.getName()) != 0) {
      parent = parent.getParent();
    }

    RectD layout = RectD.EMPTY;
    boolean isHorizontal = false;
    boolean multipleInstance = false;

    BpmnShape tableShape = getShape(element, plane);
    if (tableShape == null) {
      tableShape = getShape(parent, plane);
      final BpmnElement processRefSource = getProcessRefSource().get(parent);
      if (tableShape == null && processRefSource != null) {
        tableShape = getShape(processRefSource, plane);
        if (processRefSource.hasChild(BpmnDiConstants.PARTICIPANT_MULTIPLICITY_ELEMENT)) {
          if (Integer.parseInt(processRefSource.getChildAttribute(BpmnDiConstants.PARTICIPANT_MULTIPLICITY_ELEMENT, "maximum")) > 1) {
            multipleInstance = true;
          }
        }
      }
    }

    if (tableShape != null) {
      // table has a shape itself so we use its layout to initialize the table
      layout = new RectD(tableShape.getX(), tableShape.getY(), tableShape.getWidth(), tableShape.getHeight());
      isHorizontal = tableShape.isHorizontal();
    } else {
      // check the child lanes for their shapes
      for (BpmnElement lane : element.getChildren("lane")) {
        BpmnShape laneShape = getShape(lane, plane);
        if (laneShape != null) {
          layout = RectD.add(layout, new RectD(laneShape.getX(), laneShape.getY(), laneShape.getWidth(), laneShape.getHeight()));
          isHorizontal = laneShape.isHorizontal();
        }
      }
    }
    INode node = null;
    if (!layout.isEmpty()) {
      final ITable table;
      if (parent.getTable() != null) {
        table = parent.getTable();
        node = parent.getNode();
      } else {
        // table was already initialized for the Process due to a Participant element
        node = getMasterGraph().createNode(localRoot, layout, null, null);
        PoolNodeStyle poolStyle = createPoolNodeStyle(isHorizontal);
        poolStyle.setMultipleInstance(multipleInstance);
        getMasterGraph().setStyle(node, poolStyle);
        table = poolStyle.getTableNodeStyle().getTable();

        // create dummy stripe for the direction where no lanes are defined
        if (isHorizontal) {
          IColumn col = table.createColumn(layout.width - table.getRowDefaults().getInsets().left);
          col.setTag(new PointD(layout.x, layout.y));
        } else {
          IRow row = table.createRow(layout.height - table.getColumnDefaults().getInsets().top);
          row.setTag(new PointD(layout.x, layout.y));
        }
      }

      IStripe parentStripe = isHorizontal
          ? table.getRootRow().getChildRows().stream().findFirst().orElse(table.getRootRow())
          : table.getRootColumn().getChildColumns().stream().findFirst().orElse(table.getRootColumn());
      if (tableShape != null) {
        parentStripe = addToTable(tableShape, table, node, parentStripe);
      }

      element.setNode(node);
      if (parent.getNode() == null) {
        parent.setNode(node);
      }

      addChildLanes(element, table, parentStripe, plane, node);

      // Resize the root row/column after adding a column/row with insets
      IRectangle nodeLayout = node.getLayout();
      if (isHorizontal) {
        double max = max(table.getRootRow().getLeaves(), s -> s.getLayout().getX() - table.getLayout().getX() + s.getInsets().left);
        IColumn firstCol = table.getRootColumn().getChildColumns().iterator().next();
        table.setSize(firstCol, nodeLayout.getWidth() - max);
      } else {
        double max = max(table.getRootColumn().getLeaves(), s -> s.getLayout().getY() - table.getLayout().getY() + s.getInsets().top);
        IRow firstRow = table.getRootRow().getChildRows().iterator().next();
        table.setSize(firstRow, nodeLayout.getHeight() - max);
      }

      // There can be situations, in which the table Layout does not match the node size. In this case, we
      // resize the node
      if (nodeLayout.getWidth() != table.getLayout().getWidth()) {
        getMasterGraph().setNodeLayout(node, new RectD(nodeLayout.getX(), nodeLayout.getY(), table.getLayout().getWidth(), nodeLayout.getHeight()));
      }
      if (nodeLayout.getHeight() != table.getLayout().getHeight()) {
        getMasterGraph().setNodeLayout(node, new RectD(nodeLayout.getX(), nodeLayout.getY(), nodeLayout.getWidth(), table.getLayout().getHeight()));
      }
    }
    return node;
  }

  private void addChildLanes( BpmnElement element, ITable table, IStripe parentStripe, BpmnPlane plane, INode node ) {
    for (BpmnElement lane : element.getChildren(BpmnDiConstants.LANE_ELEMENT)) {
      BpmnShape laneShape = getShape(lane, plane);
      if (laneShape != null) {
        IStripe addedStripe = addToTable(laneShape, table, node, parentStripe);
        for (BpmnElement refElement : lane.getChildren("flowNodeRef")) {
          final BpmnElement bpmnElement = refElement.getValue() != null ? getElementForId(refElement.getValue()) : null;
          if (bpmnElement != null) {
            bpmnElement.getParent().setNode(node);
          }
        }
        BpmnElement childLaneSet = lane.getChild("childLaneSet");
        if (childLaneSet != null) {
          addChildLanes(childLaneSet, table, addedStripe, plane, node);
        }
      }
    }
  }

  // Adds the given lane to the appropriate table (pool), or creates a new one
  private IStripe addToTable( final BpmnShape shape, ITable table, INode node, IStripe parentStripe ) {
    // lane element
    BpmnElement element = shape.getElement();

    // Link the node to the BpmnElement of the lane

    element.setNode(node);
    if (shape.isHorizontal()) {
      IRow parentRow = (parentStripe instanceof IRow) ? (IRow)parentStripe : null;
      //getIndex
      int index = (int) parentRow.getChildRows().stream().filter( siblingRow -> ((PointD)siblingRow.getTag()).y < shape.getY()).count();

      IRow row = table.createRow(parentRow, shape.getHeight(), -1, null, null, null, index);
      row.setTag(new PointD(shape.getX(), shape.getY()));

      addTableLabel(table, row, shape);
      return row;
    } else {
      IColumn parentCol = (parentStripe instanceof IColumn) ? (IColumn)parentStripe : null;
      //getIndex
      int index = (int) parentCol.getChildColumns().stream().filter(siblingCol -> ((PointD)siblingCol.getTag()).x < shape.getX()).count();

      IColumn col = table.createColumn(parentCol, shape.getWidth(), -1, null, null, null, index);
      col.setTag(new PointD(shape.getX(), shape.getY()));
      addTableLabel(table, col, shape);
      return col;
    }
  }

  // Creates table (participant/pool)
  private PoolNodeStyle createTable( BpmnShape shape ) {
    PoolNodeStyle poolNodeStyle = createPoolNodeStyle(shape.isHorizontal());
    ITable table = poolNodeStyle.getTableNodeStyle().getTable();

    // Create first row & column
    IColumn col = table.createColumn(shape.getWidth() - table.getRowDefaults().getInsets().left, -1, null, null, null, -1);
    IRow row = table.createRow(shape.getHeight() - table.getColumnDefaults().getInsets().top, -1, null, null, null, -1);

    PointD location = new PointD(shape.getX(), shape.getY());
    row.setTag(location);
    col.setTag(location);
    return poolNodeStyle;
  }

  private PoolNodeStyle createPoolNodeStyle( boolean isHorizontal ) {
    PoolNodeStyle partStyle = new PoolNodeStyle(!isHorizontal);
    ITable table = partStyle.getTableNodeStyle().getTable();

    if (isHorizontal) {
      table.getColumnDefaults().setInsets(InsetsD.EMPTY);
    } else {
      table.getRowDefaults().setInsets(InsetsD.EMPTY);
    }

    // Set table insets to 0
    table.setInsets(InsetsD.EMPTY);

    return partStyle;
  }


  private BpmnDiagram defaultSelectionCallback(Collection<BpmnDiagram> topLevelDiagrams) {
    BpmnDiagram diaToLoad = null;
    int noOfDiagrams = topLevelDiagrams.size();
    if (noOfDiagrams == 1) {
      diaToLoad = topLevelDiagrams.iterator().next();
    } else if (noOfDiagrams > 1) {
      final BpmnDiagram[] choice = new BpmnDiagram[1];

      final String msg =
              "Your file contains several BPMN diagrams. \n" +
              "Choose the diagram you want to display and then click 'open'. \n" +
              "If you choose nothing, an empty diagram will be opened.";
      final JTextArea jta = new JTextArea(msg);
      jta.setBorder(BorderFactory.createEmptyBorder());
      jta.setEditable(false);
      jta.setOpaque(false);
      final JPanel textPane = new JPanel(new GridLayout(1, 1));
      textPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
      textPane.add(jta);

      final DefaultListModel<BpmnDiagram> model = new DefaultListModel<>();
      for (BpmnDiagram diagram : topLevelDiagrams) {
        model.addElement(diagram);
      }

      final JButton jb = new JButton("Open");
      final JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.TRAILING));
      buttonPane.add(jb);

      final JList<BpmnDiagram> jl = new JList<>(model);

      final JPanel chooserPane = new JPanel(new BorderLayout());
      chooserPane.add(new JScrollPane(jl), BorderLayout.CENTER);
      chooserPane.add(buttonPane, BorderLayout.SOUTH);

      final JPanel contentPane = new JPanel(new BorderLayout());
      contentPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      contentPane.add(textPane, BorderLayout.NORTH);
      contentPane.add(chooserPane, BorderLayout.CENTER);

      final JDialog jd = new JDialog();
      jb.addActionListener(e -> {
        final int idx = jl.getSelectedIndex();
        if (idx > -1) {
          choice[0] = model.get(idx);
        }

        jd.setVisible(false);
        jd.dispose();
      });
      jd.setModal(true);
      jd.setTitle("Choose Diagram");
      jd.getContentPane().add(contentPane);
      jd.pack();
      jd.setLocationRelativeTo(null);
      jd.setVisible(true);

      diaToLoad = choice[0];
    }

    return diaToLoad;
  }

  private static <T> double max( Iterable<T> items, Function<T, Double> f ) {
    double max = Double.NEGATIVE_INFINITY;
    for (T item : items) {
      double value = f.apply(item);
      if (max < value) {
        max = value;
      }
    }
    return max;
  }

  private static Document read( InputStream stream ) throws IOException {
    try {
      return toolkit.XmlUtils.parse(stream);
    } catch (Exception e) {
      if (e instanceof IOException) {
        throw (IOException) e;
      } else {
        throw new IOException(e);
      }
    }
  }
}
