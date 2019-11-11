# JavaFX Application
  

 JavaFX 8 introduces the `SwingNode` class, which enables embedding yFiles for Java (Swing) in a JavaFX application.   
*  A toolbar that provides JavaFX buttons enables the user to change the zoom level of the GraphComponent that is a Swing component as well as JavaFX buttons for undo/redo functionality.   
*  A right click on a node shown in the GraphComponent opens a JavaFX context menu and allows the user to delete the clicked node from the GraphComponent.   
*  On the left side a JavaFX palette offers nodes with different styles that can be dragged into the GraphComponent.       

 NOTE: If you are using JDK 11 or later, you have to add the javafx modules as dependencies as well. For the `build.xml` to work, the property `path.to.fx` has to be set to point to your [JavaFX SDK](https://openjfx.io/openjfx-docs/#install-javafx) .   