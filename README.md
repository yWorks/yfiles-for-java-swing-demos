
# yFiles for Java (Swing) Programming Samples

This repository contains source code demos and tutorials that use the commercial
[yFiles for Java (Swing)](https://www.yworks.com/yfilesjava) software programming
library for the visualization of graphs, diagrams, and networks.
The library itself is __*not*__ part of this repository.

[![yFiles for HTML](./demo-grid.png)](https://live.yworks.com/yfiles-for-html)

# Running the Demos

For most of these demos equivalent ones based on
[yFiles for HTML](https://www.yworks.com/yfileshtml) are hosted
[online here](https://live.yworks.com/yfiles-for-html) for everyone to play with.
Developers should
[evaluate the library](https://www.yworks.com/products/yfiles-for-java/evaluate),
instead. The evaluation version also contains these demos and the necessary
library to execute the code.
  
## [Complete Demos](demos/src/complete/)

  

 This folder and its subfolders contain demo applications which make use of the different features of yFiles for Java (Swing).   

| Demo | Description |
|------|-------------|
|[SimpleEditor](demos/src/complete/simpleeditor/)| A graph editor which demonstrates the editing features of yFiles for Java (Swing). |
|[OrgChart](demos/src/complete/orgchart/)| View and manipulate an organization chart. |
|[RotatableNodes](demos/src/complete/rotatablenodes/)| Shows how support for rotated node visualizations can be implemented on top of the yFiles library. |
|[BPMNEditor](demos/src/complete/bpmn/)| Business Process Diagram sample application. |
|[HierarchicGrouping](demos/src/complete/hierarchicgrouping/)| Organize subgraphs in groups and folders and interactively expand and collapse them. |
|[IsometricDrawing](demos/src/complete/isometric/)| Displays graphs in an isometric fashion to create an impression of a 3-dimensional view. |
|[LogicGate](demos/src/complete/logicgate/)| An editor for networks of logic gates with dedicated ports for incoming and outgoing connections. |
|[CollapsibleTree](demos/src/complete/collapse/)| Interactively collapse and expand subgraphs. |
|[TableEditor](demos/src/complete/tableeditor/)| Interactive creation and editing of tables. |
|[Uml](demos/src/complete/uml/)| Interactive creation and editing of UML class diagrams. |
  
## [Layout Demos](demos/src/layout/)

  

 This folder and its subfolders contain demo applications which make use of the different layout algorithms of the layout component of yFiles for Java (Swing).   

| Demo | Description |
|------|-------------|
|[LayoutStyles](demos/src/layout/layoutstyles/)| Play with the most used layout algorithms of yFiles. |
|[HierarchicLayout](demos/src/layout/hierarchiclayout/)| Showcase of one of our central layout algorithms, the HierarchicLayout. |
|[InteractiveOrganicLayout](demos/src/layout/interactiveorganic/)| Use InteractiveOrganicLayout for organic layout in interactive environments. |
|[FamilyTree](demos/src/layout/familytree/)| This demo shows how genealogical graphs (family trees) can be visualized. |
|[CriticalPaths](demos/src/layout/criticalpaths/)| This demo shows how to emphazise important paths with hierarchic and tree layout algorithms. |
|[EdgeBundling](demos/src/layout/edgebundling/)| Shows how edge bundling can be applied for reducing visual cluttering in dense graphs. |
|[EdgeGrouping](demos/src/layout/edgegrouping/)| Shows the effects of edge and port grouping when arranging graphs with HierarchicLayout. |
|[PartialLayout](demos/src/layout/partiallayout/)| Shows how to arrange some elements in a graph while keeping other elements fixed. |
|[PartitionGrid](demos/src/layout/partitiongrid/)| Demonstrates the usage of a *PartitionGrid* for hierarchic and organic layout calculations. |
|[Sankey](demos/src/layout/sankey/)| Showcase of how a Sankey diagram can be produced by means of the HierarchicLayout. |
|[SplitEdges](demos/src/layout/splitedges/)| Shows how to align edges at group nodes using RecursiveGroupLayout together with HierarchicLayout. |
|[TreeLayout](demos/src/layout/treelayout/)| Demonstrates the tree layout style and the different ways in which this layout can arrange a node and its children. |
|[TreeMap](demos/src/layout/treemap/)| Shows disk usage of a directory tree with the Tree Map layout. |
  
## [Input Demos](demos/src/input/)

  

 This folder and its subfolders contain demo applications which use and customize the graph editing features provided by yFiles for Java (Swing).   

| Demo | Description |
|------|-------------|
|[CustomLabelModel](demos/src/input/customlabelmodel/)| Customize label placement model. |
|[CustomPortModel](demos/src/input/customportmodel/)| Customize port location model. |
|[CustomSnapping](demos/src/input/customsnapping/)| Enable and customize the snapping behaviour of graph items. |
|[DragAndDrop](demos/src/input/draganddrop/)| Enable and customize the drag-and-drop support for nodes, labels and ports. |
|[EdgeReconnection](demos/src/input/edgereconnection/)| Enable and customize the reconnection behavior of edges. |
|[Hyperlink](demos/src/input/hyperlink/)| Add hyperlink support to HTML formatted labels. |
|[LabelHandleProvider](demos/src/input/labelhandleprovider/)| Enable interactive rotating and resizing labels. |
|[OrthogonalEdges](demos/src/input/orthogonaledges/)| Enable and customize orthogonal edge editing. |
|[PopupMenu](demos/src/input/popupmenu/)| Enable and customize context menus for nodes and for the canvas background. |
|[PortCandidateProvider](demos/src/input/portcandidateprovider/)| Customize the ports at which edges connect to nodes. |
|[PositionHandler](demos/src/input/positionhandler/)| Customize the movement behavior of nodes. |
|[ReparentHandler](demos/src/input/reparenthandler/)| Customize the re-parenting behavior of nodes. |
|[ReshapeHandleProvider](demos/src/input/reshapehandleprovider/)| Customize the reshape handles of nodes. |
|[SingleSelection](demos/src/input/singleselection/)| Configure the `GraphEditorInputMode` for single selection mode. |
|[SizeConstraintProvider](demos/src/input/sizeconstraintprovider/)| Customize the resizing behavior of nodes. |
  
## [Integration Demos](demos/src-neo4j/integration/)

  

 This folder and its subfolders contain demo applications which illustrate the integration of yFiles for Java with different GUI frameworks.   

| Demo | Description |
|------|-------------|
|[JavaFXApplication](demos/src-fx/integration/javafx/)| Integrate yFiles for Java (Swing) in a JavaFX application. |
|[Neo4j](demos/src-neo4j/integration/neo4j/)| Demo application that shows how to integrate Neo4j into yFiles for Java (Swing). |
  
## [Viewer Demos](demos/src/viewer/)

  

 This folder and its subfolders contain demo applications which make use of the different features of the viewer component of yFiles for Java (Swing).   

| Demo | Description |
|------|-------------|
|[PDFImageExport](demos/src-pdf/viewer/pdfimageexport/)| Export a graph as a PDF, EPS or EMF document. |
|[SVGImageExport](demos/src-svg/viewer/svgimageexport/)| Export a graph as a SVG image. |
|[BackgroundImage](demos/src/viewer/backgroundimage/)| Shows how to add background visualizations to a graph component. |
|[ClickableStyleDecorator](demos/src/viewer/clickablestyledecorator/)| Shows how to handle mouse clicks in specific areas of a node's visualization. |
|[EdgeToEdge](demos/src/viewer/edgetoedge/)| Shows edge-to-edge connections. |
|[GraphEvents](demos/src/viewer/events/)| Explore the different kinds of events dispatched by yFiles for Java (Swing). |
|[Filtering](demos/src/viewer/filtering/)| Shows how to temporarily remove nodes or edges from the graph. |
|[FilteringWithFolding](demos/src/viewer/filteringandfolding/)| Shows how to combine yFiles [filtering](https://docs.yworks.com/yfilesjava/doc/api/#/dguide/filtering) and [folding](https://docs.yworks.com/yfilesjava/doc/api/#/dguide/folding) features. |
|[Folding](demos/src/viewer/folding/)| Shows how to use yFiles [folding](https://docs.yworks.com/yfilesjava/doc/api/#/dguide/folding) feature. |
|[GraphCopy](demos/src/viewer/graphcopy/)| Shows how to copy a graph or sub graph. |
|[GraphMLCompatibility](demos/src/viewer/graphmlcompatibility/)| Shows how to enable read compatibility for GraphML files from older versions. |
|[GraphViewer](demos/src/viewer/graphviewer/)| Showcase of different kinds of graphs created with yFiles for Java (Swing). |
|[GridSnapping](demos/src/viewer/gridsnapping/)| Demonstrates how to enable grid snapping functionality for graph elements. |
|[ImageExport](demos/src/viewer/imageexport/)| Export a graph as a bitmap image. |
|[LargeGraphs](demos/src/viewer/largegraphs/)| Improve the rendering performance for very large graphs in yFiles for Java (Swing). |
|[LevelOfDetail](demos/src/viewer/levelofdetail/)| Demonstrates how to change the level of detail when zooming in and out. |
|[Printing](demos/src/viewer/printing/)| Print a graph by using the yFiles CanvasPrintable. |
|[RenderingOrder](demos/src/viewer/renderingorder/)| Shows the effect of different rendering policies to the model items. |
|[SmartClickNavigation](demos/src/viewer/smartclicknavigation/)| Demonstrates how to navigate in a large graph. |
|[Snapping](demos/src/viewer/snapping/)| Demonstrates how to enable snapping functionality for graph elements. |
|[Tooltips](demos/src/viewer/tooltips/)| Demonstrates how to add tooltips to graph items. |
  
## [Style Demos](demos/src/style/)

  

 This folder and its subfolders contain demo applications which make use of the different features of the styles component of yFiles for Java (Swing).   

| Demo | Description |
|------|-------------|
|[SimpleCustomStyle](demos/src/style/simplecustomstyle/)| Implement sophisticated styles for graph objects in yFiles for Java (Swing). |
|[SVGNodeStyle](demos/src-svg/style/svgnodestyle/)| Demonstrates SVG node visualizations. |
|[JComponentStyle](demos/src/style/jcomponentstyle/)| Arbitrary Swing components incorporated as graph elements. |
  
## [Data Binding Demos](demos/src/databinding/)

  

 This folder and its subfolders contain demo applications which demonstrate how to use the `GraphBuilder` classes for binding graph elements to business data in yFiles for Java (Swing).   

| Demo | Description |
|------|-------------|
|[GraphBuilder](demos/src/databinding/graphbuilder/)| Demonstrates data binding using the `GraphBuilder` class. |
|[InteractiveNodesGraphBuilder](demos/src/databinding/interactivenodesgraphbuilder/)| Demonstrates data binding using class `AdjacentNodesGraphBuilder` . |
  
## [Analysis Demos](demos/src/analysis/)

  

 This folder and its subfolders contain demo applications which demonstrate some of the graph analysis algorithms available in yFiles for Java (Swing).   

| Demo | Description |
|------|-------------|
|[GraphAnalysis](demos/src/analysis/graphanalysis/)| Algorithms to analyse the structure of a graph in yFiles for Java (Swing). |
|[ShortestPath](demos/src/analysis/shortestpath/)| Usage and visualization of shortest path algorithms in yFiles for Java (Swing). |
  
## [Deployment Demos](demos/src/deploy/)

  

 This folder and its subfolders contain demo applications which illustrate tasks for deployment of yFiles for Java (Swing) applications, e.g. obfuscation.   

| Demo | Description |
|------|-------------|
|[ObfuscationDemo](demos/src/deploy/obfuscation/)| Obfuscate an yFiles for Java (Swing) application via yGuard. |
  
# Tutorials

The yFiles for Java (Swing) tutorials are extensive source code samples that present
the functionality of the yFiles for Java (Swing) library.

To navigate to a specific tutorial, just follow the corresponding link from the
table below.

## Available Tutorials

| Category | Description |
|----------|-------------|
|[Getting Started](./demos/src/tutorial01_GettingStarted/)| Introduces basic concepts as well as main features like custom styles, full user interaction, Undo/Redo, clipboard, I/O, grouping and folding.|
|[Custom Styles](./demos/src/tutorial02_CustomStyles/)| A step-by-step guide to customizing the visual representation of graph elements. This tutorial is intended for users who want to learn how to create custom styles from scratch.|


# License

Use of the software hosted in this repository is subject to the license terms of the corresponding yFiles for Java (Swing) license.
Owners of a valid software license for a yFiles for Java (Swing) version that these
demos are shipped with are allowed to use the demo source code as basis
for their own yFiles for Java (Swing) powered applications. Use of such programs is
governed by the rights and conditions as set out in the yFiles for Java (Swing)
license agreement. More details [here](./LICENSE). If in doubt, feel free to [contact](https://www.yworks.com/contact) the yFiles for Java (Swing) support team.
