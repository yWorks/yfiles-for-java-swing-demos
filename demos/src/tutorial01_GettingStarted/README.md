# Tutorial 1: Getting Started

This tutorial is a step-by-step introduction to the concepts and main features of the yFiles for Java (Swing) diagramming library. Topics include custom styles, full user interaction, Undo/Redo, clipboard, I/O, grouping and folding.

It is intended for developers who want to get started with the library in an effective way. If you are new to the yFiles for Java (Swing) diagramming library, we recommend to start by going through the projects in this tutorial one by one. To make full use of the tutorial, we also recommend to review and possibly modify the source code of each sample project.

## Running a Tutorial Step
The best way to run a tutorial step is from within your preferred IDE. Set the directory `<yFiles-for-Java>/demos/src/` as source directory of your project and add the yFiles library (`yfiles-for-java-swing.jar`) to your references. Each step can be started by running or debugging its class `SampleApplication`.

##Steps in this Tutorial

|Name|	Description|
|----|-------------|
|Step 01: Creating the View|	The class `GraphComponent` is the central UI element for working with graphs.|
|Step 02: Creating Graph Elements|	The `IGraph` interface defines fundamental graph structure-related functionality including creation and deletion of graph elements.|
|Step 03: Managing the Viewport|	The viewport is the rectangle that defines the part of the graph that is currently visible.|
|Step 04: Modifying the Visualizations|	The visual appearance for each type of graph element (except bends) is specified by means of styles.|
|Step 05: Placing Labels|	Label positions are not specified through explicit coordinates. Instead, so called `ILabelModelParameters` created by `ILabelModels` are used, which encode a specific position.|
|Step 06: Basic Interaction|	The GraphEditorInputMode is an input mode that supports a large number of ways to interact with the graph and its view.|
|Step 07: Undo and Clipboard|	Undo, redo and clipboard support are provided by the graph out-of-the-box.|
|Step 08: Using GraphML for Loading and Saving|	Graphs can be saved and loaded into an application using the GraphML format.|
|Step 09: Customizing Behavior|	The ports that an edge can connect to interactively can be customized by means of the look-up mechanism.|
|Step 10: Grouping|	Grouping adds hierarchical structure to a graph by assigning one or several nodes to a common group node.|
Step 11: Folding|	Folding is the ability to collapse and expand group nodes.|
|Step 12: Binding Data to Graph Elements|	The usual method to bind arbitrary custom data to graph elements is to add an `IMapper` to the graph's `IMapperRegistry`.|
|Step 13: Exporting Custom Data in GraphML|	The I/O support can read and write custom data that is bound to graph elements to and from a GraphML file.|
|Step 14: Automatic Graph Layout|	Using layout algorithms can make your graph easier to read by automatically generating high-quality graph drawings.|
|Step 15: Commands and Command Bindings|	The built-in command framework of yFiles can be used to easily connect event handler callbacks with shortcuts that benefit from automatic management of enabling/disabling.|
|Step 16: Snapping|	The snapping feature simplifies the interactive arrangement of graph elements. It helps to align nodes, ports and bends and to distribute them evenly.|
|Step 17: Grid Snapping|	Beyond snapping to other items yFiles also supports snapping of graph elements to a grid.|
|Step 18: Orthogonal Edges|	The orthogonal edge editing feature helps the user to interactively create and maintain orthogonal edge paths.|