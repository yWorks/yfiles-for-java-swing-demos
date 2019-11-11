
# yFiles for Java (Swing) Demo Sources

This repository contains source code demos that use the commercial [yFiles for Java (Swing)](https://www.yworks.com/yfilesjava) software programming library for the visualization of graphs, diagrams, and networks. The library itself is __*not*__ part of this repository.

[![yFiles for Java (Swing) Demos](./demo-grid.png)](https://live.yworks.com/yfiles-for-html)

# Running the Demos

For most of these demos equivalent ones based on [yFiles for HTML](https://www.yworks.com/yfileshtml)
are hosted [online here](https://live.yworks.com/yfiles-for-html) for everyone to play with. Developers should [evaluate the library](https://www.yworks.com/products/yfiles-for-java/evaluate), instead.
The evaluation version also contains these demos and the necessary library to execute the code.
  
## [Demo Applications](demos/src/complete/README.md)

  

 This folder and its subfolders contain demo applications which make use of the different features of yFiles for Java (Swing).   

| Demo | Description |
|------|:-----------:|
|[SimpleEditor](demos/src/complete/simpleeditor/README.md)| A graph editor which demonstrates the editing features of yFiles for Java (Swing). |
|[OrgChart](demos/src/complete/orgchart/README.md)| View and manipulate an organization chart. |
|[RotatableNodes](demos/src/complete/rotatablenodes/README.md)| Shows how support for rotated node visualizations can be implemented on top of the yFiles library. |
|[BPMNEditor](demos/src/complete/bpmn/README.md)| Business Process Diagram sample application. |
|[HierarchicGrouping](demos/src/complete/hierarchicgrouping/README.md)| Organize subgraphs in groups and folders and interactively expand and collapse them. |
|[IsometricDrawing](demos/src/complete/isometric/README.md)| Displays graphs in an isometric fashion to create an impression of a 3-dimensional view. |
|[LogicGate](demos/src/complete/logicgate/README.md)| An editor for networks of logic gates with dedicated ports for incoming and outgoing connections. |
|[CollapsibleTree](demos/src/complete/collapse/README.md)| Interactively collapse and expand subgraphs. |
|[TableEditor](demos/src/complete/tableeditor/README.md)| Interactive creation and editing of tables. |
|[Uml](demos/src/complete/uml/README.md)| Interactive creation and editing of UML class diagrams. |
  
## [Layout Demos](demos/src/layout/README.md)

  

 This folder and its subfolders contain demo applications which make use of the different layout algorithms of the layout component of yFiles for Java (Swing).   

| Demo | Description |
|------|:-----------:|
|[LayoutStyles](demos/src/layout/layoutstyles/README.md)| Play with the most used layout algorithms of yFiles. |
|[HierarchicLayout](demos/src/layout/hierarchiclayout/README.md)| Showcase of one of our central layout algorithms, the HierarchicLayout. |
|[InteractiveOrganicLayout](demos/src/layout/interactiveorganic/README.md)| Use InteractiveOrganicLayout for organic layout in interactive environments. |
|[EdgeBundling](demos/src/layout/edgebundling/README.md)| Shows how edge bundling can be applied for reducing visual cluttering in dense graphs. |
|[PartitionGrid](demos/src/layout/partitiongrid/README.md)| Demonstrates the usage of a *PartitionGrid* for hierarchic and organic layout calculations. |
|[Sankey](demos/src/layout/sankey/README.md)| Showcase of how a Sankey diagram can be produced by means of the HierarchicLayout. |
  
## [Input Demos](demos/src/input/README.md)

  

 This folder and its subfolders contain demo applications which use and customize the graph editing features provided by yFiles for Java (Swing).   

| Demo | Description |
|------|:-----------:|
|[CustomPortModel](demos/src/input/customportmodel/README.md)| Customize port location model. |
|[CustomSnapping](demos/src/input/customsnapping/README.md)| Enable and customize the snapping behaviour of graph items. |
|[DragAndDrop](demos/src/input/draganddrop/README.md)| Enable and customize the drag-and-drop support for nodes, labels and ports. |
|[EdgeReconnection](demos/src/input/edgereconnection/README.md)| Enable and customize the reconnection behavior of edges. |
|[Hyperlink](demos/src/input/hyperlink/README.md)| Add hyperlink support to HTML formatted labels. |
|[OrthogonalEdges](demos/src/input/orthogonaledges/README.md)| Enable and customize orthogonal edge editing. |
|[PopupMenu](demos/src/input/popupmenu/README.md)| Enable and customize context menus for nodes and for the canvas background. |
|[PortCandidateProvider](demos/src/input/portcandidateprovider/README.md)| Customize the ports at which edges connect to nodes. |
|[PositionHandler](demos/src/input/positionhandler/README.md)| Customize the movement behavior of nodes. |
|[ReparentHandler](demos/src/input/reparenthandler/README.md)| Customize the re-parenting behavior of nodes. |
|[ReshapeHandleProvider](demos/src/input/reshapehandleprovider/README.md)| Customize the reshape handles of nodes. |
|[SingleSelection](demos/src/input/singleselection/README.md)| Configure the `GraphEditorInputMode` for single selection mode. |
|[SizeConstraintProvider](demos/src/input/sizeconstraintprovider/README.md)| Customize the resizing behavior of nodes. |
  
## [Integration Demos](demos/src-neo4j/integration/README.md)

  

 This folder and its subfolders contain demo applications which illustrate the integration of yFiles for Java with different GUI frameworks.   

| Demo | Description |
|------|:-----------:|
|[JavaFXApplication](demos/src-fx/integration/javafx/README.md)| Integrate yFiles for Java (Swing) in a JavaFX application. |
|[Neo4j](demos/src-neo4j/integration/neo4j/README.md)| Demo application that shows how to integrate Neo4j into yFiles for Java (Swing). |
  
## [Viewer Demos](demos/src/viewer/README.md)

  

 This folder and its subfolders contain demo applications which make use of the different features of the viewer component of yFiles for Java (Swing).   

| Demo | Description |
|------|:-----------:|
|[PDFImageExport](demos/src-pdf/viewer/pdfimageexport/README.md)| Export a graph as a PDF, EPS or EMF document. |
|[SVGImageExport](demos/src-svg/viewer/svgimageexport/README.md)| Export a graph as a SVG image. |
|[EdgeToEdge](demos/src/viewer/edgetoedge/README.md)| Shows edge-to-edge connections. |
|[GraphEvents](demos/src/viewer/events/README.md)| Explore the different kinds of events dispatched by yFiles for Java (Swing). |
|[GraphMLCompatibility](demos/src/viewer/graphmlcompatibility/README.md)| Shows how to enable read compatibility for GraphML files from older versions. |
|[GraphViewer](demos/src/viewer/graphviewer/README.md)| Showcase of different kinds of graphs created with yFiles for Java (Swing). |
|[ImageExport](demos/src/viewer/imageexport/README.md)| Export a graph as a bitmap image. |
|[LargeGraphs](demos/src/viewer/largegraphs/README.md)| Improve the rendering performance for very large graphs in yFiles for Java (Swing). |
|[Printing](demos/src/viewer/printing/README.md)| Print a graph by using the yFiles CanvasPrintable. |
|[RenderingOrder](demos/src/viewer/renderingorder/README.md)| Shows the effect of different rendering policies to the model items. |
  
## [Style Demos](demos/src/style/README.md)

  

 This folder and its subfolders contain demo applications which make use of the different features of the styles component of yFiles for Java (Swing).   

| Demo | Description |
|------|:-----------:|
|[SimpleCustomStyle](demos/src/style/simplecustomstyle/README.md)| Implement sophisticated styles for graph objects in yFiles for Java (Swing). |
|[SVGNodeStyle](demos/src-svg/style/svgnodestyle/README.md)| Demonstrates SVG node visualizations. |
|[JComponentStyle](demos/src/style/jcomponentstyle/README.md)| Arbitrary Swing components incorporated as graph elements. |
  
## [Data Binding Demos](demos/src/databinding/README.md)

  

 This folder and its subfolders contain demo applications which demonstrate how to use the `GraphBuilder` classes for binding graph elements to business data in yFiles for Java (Swing).   

| Demo | Description |
|------|:-----------:|
|[GraphBuilder](demos/src/databinding/graphbuilder/README.md)| Demonstrates data binding using the `GraphBuilder` class. |
|[InteractiveNodesGraphBuilder](demos/src/databinding/interactivenodesgraphbuilder/README.md)| Demonstrates data binding using class `AdjacentNodesGraphBuilder` . |
  
## [Analysis Demos](demos/src/analysis/README.md)

  

 This folder and its subfolders contain demo applications which demonstrate some of the graph analysis algorithms available in yFiles for Java (Swing).   

| Demo | Description |
|------|:-----------:|
|[GraphAnalysis](demos/src/analysis/graphanalysis/README.md)| Algorithms to analyse the structure of a graph in yFiles for Java (Swing). |
|[ShortestPath](demos/src/analysis/shortestpath/README.md)| Usage and visualization of shortest path algorithms in yFiles for Java (Swing). |
  
## [Deployment Demos](demos/src/deploy/README.md)

  

 This folder and its subfolders contain demo applications which illustrate tasks for deployment of yFiles for Java (Swing) applications, e.g. obfuscation.   

| Demo | Description |
|------|:-----------:|
|[ObfuscationDemo](demos/src/deploy/obfuscation/README.md)| Obfuscate an yFiles for Java (Swing) application via yGuard. |
  
# Tutorials

The yFiles for Java (Swing) tutorials are extensive source code samples that present the functionality of the yFiles for Java (Swing) library. All tutorials can be found in subdirectories of the current directory.

To navigate to a specific tutorial, just follow the corresponding link from the table below.

## Available Tutorials

| Category | Description |
|------|:-----------:|
|[Getting Started](./tutorials/tutorial01_GettingStarted/README.md)| 	Introduces basic concepts as well as main features like custom styles, full user interaction, Undo/Redo, clipboard, I/O, grouping and folding.|
|[Custom Styles](./tutorials/tutorial02_CustomStyles/README.md)| A step-by-step guide to customizing the visual representation of graph elements. This tutorial is intended for users who want to learn how to create custom styles from scratch.|


# License

Use of the software hosted in this repository is subject to the license terms of the corresponding yFiles for Java (Swing) license.
Owners of a valid software license for a yFiles for Java (Swing) version that these
demos are shipped with are allowed to use the demo source code as basis
for their own yFiles for Java (Swing) powered applications. Use of such programs is
governed by the rights and conditions as set out in the yFiles for Java (Swing)
license agreement. More details [here](./LICENSE). If in doubt, feel free to [contact](https://www.yworks.com/contact) the yFiles for Java (Swing) support team.
