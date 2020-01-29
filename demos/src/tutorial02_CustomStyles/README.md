# Tutorial 2: Custom Styles

This tutorial is a step-by-step guide to customizing the visual representation of graph elements. It shows the creation of custom styles for nodes, edges, labels, and ports. Moreover, it presents a custom arrowhead rendering, a customized edge selection, and a visual representation of graph elements that depends on the current application state. Finally, several optimization strategies are discussed.

It is intended for users who want to learn how to create custom styles from scratch. If you are new to styles, in particular to their customization, we recommend to start by going through the tutorial steps that show the basics one by one. Of the specialized topics, you can skip the ones that doesn't apply to your use case. To make full use of the tutorial, we recommend to review and possibly modify the source code of the sample projects.

## Running a Tutorial Step
The best way to run a tutorial step is from within your preferred IDE. Set the directory `<yFiles-for-Java>/demos/src/` as source directory of your project and add the yFiles library (`yfiles-for-java-swing.jar`) to your references. Each step can be started by running or debugging its class `SampleApplication`.

## Steps in this Tutorial
|Name|	Description|
|----|-------------|
|Step 01: Custom Node Style|	Create a custom node style.|
|Step 02: Node Color|	Change a style depending on the node's tag.|
|Step 03: UpdateVisual|	Implement high-performance rendering for nodes.|
|Step 04: Improve Rendering Performance|	Improve the rendering performance of nodes using pre-rendered images.|
|Step 05: DropShadow|	This step shows two ways on how to create a drop shadow of nodes.|
|Step 06: IsInside|	Crop edges properly at a node's visual bounds and how to determine whether or not a location, e.g. of a port, is inside said node's visual bounds.|
|Step 07: Hit Test|	Implement a proper hit detection for mouse clicks and marquee selection.|
|Step 08: GetBounds|	Include the shadow of the nodes in the node's bounds calculation, which is e.g. necessary if the view port is adjusted to show the complete graph.|
|Step 09: Edge from Node to Label|	Visually connect a node to its label(s) by means of edges.|
|Step 10: IsVisible|	Make a connector to a label visible as long as it is within the view port, even if the node itself is currently outside of the view port.|
|Step 11: Custom Label Style|	Create a custom label style.|
|Step 12: Label Preferred Size|	Adjust the size of the label depending on the size of its text.|
|Step 13: High Performance Rendering of Labels|	Implement high-performance rendering for labels.|
|Step 14: Label Edit Button|	Implement a button within a label to open the label editor.|
|Step 15: Button Visibility|	Hide the button depending on the zoom level.|
|Step 16: Using Data In Label Tag|	Adjust rendering depending on data from a business object stored in the label's tag.|
|Step 17: Custom Edge Style|	Create a custom edge style which allows to specify the edge thickness by setting a property on the style.|
|Step 18: Edge Hit Test|	Take the thickness of an edge into account when checking if the edge was clicked.|
|Step 19: Edge Cropping|	Crop an edge at the node bounds.|
|Step 20: Animated Edge Selection|	Change the style of an edge if the edge is selected and create an animated linear gradient fill.|
|Step 21: Add Arrows|	Add arrows to the edges.|
|Step 22: Custom Arrow|	Create a custom arrow.|
|Step 23: Arrow Thickness|	Render the arrow depending on a property of the edge it belongs to.|
|Step 24: Custom Ports|	Implement a custom port style. In this case the port is a transparent white circle.|
|Step 25: Style Decorator|	Implement a node style decorator that enhances an existing node style by adding visual decorators.|
|Step 26: Custom Group Style|	Implement a special node style for group nodes.|
|Step 27: Custom Group Bounds|	Customize the way the group insets are calculated.|
|Step 28: Bridge Support|	Enable bridges for a custom edge style.|